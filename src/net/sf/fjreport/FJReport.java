package net.sf.fjreport;

import java.awt.Color;
import java.awt.DefaultKeyboardFocusManager;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;


import net.sf.fjreport.cell.Cell;
import net.sf.fjreport.cell.CellMouseControl;
import net.sf.fjreport.control.BaseControl;
import net.sf.fjreport.control.NewPageAction;
import net.sf.fjreport.control.NextPageAction;
import net.sf.fjreport.control.PrePageAction;
import net.sf.fjreport.control.RemovePageAction;
import net.sf.fjreport.io.FJReportIO;
import net.sf.fjreport.io.ReportFileFilter;
import net.sf.fjreport.line.Line;
import net.sf.fjreport.line.LineMouseControl;
import net.sf.fjreport.statusbar.HasStatus;
import net.sf.fjreport.statusbar.StatusChangeListener;
import net.sf.fjreport.util.CommonUtil;
import net.sf.fjreport.util.StringResource;


/**
 * Create a report inherited from JPanel
 * @author Frank Lewis
 * 
 */

public class FJReport extends JPanel implements Printable, Scrollable, HasStatus, MultiPage, Printer{


	private Zoom zoom = new Zoom(1, 0, 0);
	private PageFormat pageFormat;
	private boolean editting;
	private BaseControl operation;
	private int state;

	/**
	 * DRAWLINE_STATE, for designing report.
	 * <p>
	 * in line state, you can draw vertical or horizontal lines. Right press
	 * mouse and drag, when right button is released, a new line is drawn. Left
	 * click on a line to pick it. left double click on a line to set its style.
	 * <p/>
	 * When a line is picked (hight light with red color), left press mouse
	 * on it then drag can move the line to the desired position. Left press
	 * mouse on its end point then drag (to vertical line, buttom point is end;
	 * to horizontal line, right point is end), the line length will change.
	 * <p/>
	 * For quickly use, left double click on blank will create a horizontal
	 * line stretching from left border to right border with line.startPoint.y =
	 * mouseEvent.y, while right click on blank will create a vertical line
	 * stretching from top to bottom. 
	 */
	public static final int DRAWLINE_STATE = 1;

	/**
	 * CELL_STATE, can set cell's properties.
	 * <p/>
	 * Left double click on a cell to activate the cell edit dialog. there are
	 * currently 9 cell types. they are empty, label, textfield(sigle line
	 * edit), textarea(multi line edit), combobox, checkbox, datefiled, image,
	 * custom.
	 * <p/>
	 * When state is switched from line to cell, fjreport will update
	 * cells' position and size according to the lines change. the algorithm
	 * performance is about n*n, fast enough.
	 */
	public static final int CELL_STATE = 2;
	/**
	 * edit state, for end user of your application. 
	 * <p/> 
	 * users can finish form-style report in edit state. they can input in the 
	 * disigned grids report. What they see on the screen is what they get on 
	 * a paper from printer. This is the main purpose of this package. To 
	 * provide form-style report editor to user, something like MS Word.
	 */
	public static final int EDIT_STATE = 3;
	/**
	 * READONLY_STATE, the initial state when a report is first craeted.
	 * It looks like edit_state, but can't accept user input.
	 */
	public static final int READONLY_STATE = 0;

	private List<FJReportPage> pages = new ArrayList();

	
	/**
	 * Current editing page of a multi-page report
	 */
	public FJReportPage currentPage;
	private int currentPageIndex;
	
	private JComboBox comboFont;
	private JComboBox comboSize;
	
	private FJReport instance;
	private HashMap cellMap;
	
	/**
	 * get current cell that gain the editing focus.
	 * @return the focused cell.
	 */
	public Cell getCurrentCell() {
		return currentPage.currentCell;
	}

	/**
	 * set focused cell.
	 * @param currentCell the cell you want to set editing focus
	 */
	public void setCurrentCell(Cell currentCell) {
		if (currentPage.currentCell != null) currentPage.currentCell.setSelected(false);
		currentPage.currentCell = currentCell;
		if (currentCell != null) {
			currentCell.setSelected(true);
			if (comboFont != null) comboFont.setSelectedItem(currentCell.getFont().getName());
			if (comboSize != null) comboSize.setSelectedItem(String.valueOf(currentCell.getFont().getSize()));
		}
	}


	/**
	 * clear all lines (except imageable borders) and cells in current page.
	 */
	public void clear(){
		if (currentPage == null) return;
		currentPage.hLines.clear();
		currentPage.vLines.clear();
		addBorders();
		currentPage.cells.clear();
		setPageFormat(pageFormat);
		removeAll();
		updateUI();
	}
	
	/**
	 * not implemented now
	 * @return editing
	 */
	public boolean isEditting() {
		return editting;
	}

	/**
	 * not implemented now
	 * @param editting
	 */
	public void setEditting(boolean editting) {
		this.editting = editting;
	}

	/**
	 * create a report with single page.
	 * <p/>
	 * pageformat is set to default. doublebuffered is true. Background color is
	 * set to white.
	 */
	public FJReport(){
		super(null);
		PrinterJob job = PrinterJob.getPrinterJob();
		setPageFormat(job.validatePage(job.defaultPage()));
		setDoubleBuffered(true);
		insertPage();
		setBackground(Color.WHITE);
		instance = this;
//		KeyboardFocusManager kbfm = KeyboardFocusManager.
//		getCurrentKeyboardFocusManager();
//		kbfm.addKeyEventDispatcher(new MyKeyboardManager());
	}

	/* (non-Javadoc)
	 * @see java.awt.print.Printable#print(java.awt.Graphics, java.awt.print.PageFormat, int)
	 */
	public int print(Graphics arg0, PageFormat arg1, int arg2) throws PrinterException {
		// TODO Auto-generated method stub
		if (pages == null || arg2 >= pages.size() || arg2 < 0) 
			return Printable.NO_SUCH_PAGE;
		setCurrentPageIndex(arg2);
		printPaint(arg0);
		return 0;
	}
	
	private void printPaint(Graphics arg0) {
		Iterator it;
		Line line;
		Graphics2D g2d = (Graphics2D)arg0;
//		paintComponents(arg0);
		if (currentPage.cells != null) {
			it = currentPage.cells.iterator();
			while (it.hasNext()) {
				((Cell)it.next()).printRender(g2d);
			}
		}
		it = currentPage.vLines.iterator();
		while (it.hasNext()) {
			line = (Line) it.next();
			if (!line.isInVisible()) {
				line.render(0, 0, g2d);
			}
		}
		it = currentPage.hLines.iterator();
		while (it.hasNext()) {
			line = (Line) it.next();
			if (!line.isInVisible()) line.render(0, 0, g2d);
		}
	}

	/**
	 * @return page format of the report.
	 */
	public PageFormat getPageFormat() {
		return pageFormat;
	}


	/**
	 * set page format of report.
	 * <p/>
	 * Position and length of all imageable borders will be adjusted
	 * to the new page format. The preferred size of report will also
	 * set to the size of page format.
	 * Note that if current page format equals the new page format,
	 * this function has no effect.
	 * 
	 * @param pageFormat the new page format.
	 */
	public void setPageFormat(PageFormat pageFormat) {

		if (CommonUtil.isPageEqual(this.pageFormat, pageFormat)) return;
		
		int x = (int) pageFormat.getImageableX();
		int y = (int) pageFormat.getImageableY();
		int w = (int) pageFormat.getImageableWidth();
		int h = (int) pageFormat.getImageableHeight();
		
		x = x + Line.lineInterval - x % Line.lineInterval;
		y = y + Line.lineInterval - y % Line.lineInterval;
		w = w - Line.lineInterval - w % Line.lineInterval;
		h = h - Line.lineInterval - h % Line.lineInterval;
		
		Line line;
		for (int i = 0; i < pages.size(); i++){
			FJReportPage page = (FJReportPage)pages.get(i);
			for (int j = page.hLines.size() - 1; j >= 0 ; j--){
				line = (Line) page.hLines.get(j);
				if (line.getBorderType() == Line.BORDER_UP) {
					line.setStartPoint(new Point(x, y));
					line.setLength(w);
				} else if (line.getBorderType() == Line.BORDER_DOWN) {
					line.setStartPoint(new Point(x, y + h));
					line.setLength(w);
				}
			}
			for (int j = page.vLines.size() - 1; j >= 0 ; j--){
				line = (Line) page.vLines.get(j);
				if (line.getBorderType() == Line.BORDER_LEFT) {
					line.setStartPoint(new Point(x, y));
					line.setLength(h);
				} else if (line.getBorderType() == Line.BORDER_RIGHT) {
					line.setStartPoint(new Point(x + w, y));
					line.setLength(h);
				}
			}
		}
		
		if (this.pageFormat != null 
				&& !CommonUtil.isPageEqual(this.pageFormat, pageFormat)) {
			int newx = x;
			int newy = y;
			int neww = w;
			int newh = h;
			
			x = (int) this.pageFormat.getImageableX();
			y = (int) this.pageFormat.getImageableY();
			w = (int) this.pageFormat.getImageableWidth();
			h = (int) this.pageFormat.getImageableHeight();
			
			x = x + Line.lineInterval - x % Line.lineInterval;
			y = y + Line.lineInterval - y % Line.lineInterval;
			w = w - Line.lineInterval - w % Line.lineInterval;
			h = h - Line.lineInterval - h % Line.lineInterval;
			
			FJReportPage page, cPage;
			cPage = currentPage;
			List lines;
			for(int i = 0; i < pages.size(); i++) {
				page = pages.get(i);
				lines = page.vLines;
				for(int j = 0; j < lines.size(); j++) {
					line = (Line) lines.get(j);
					if (line.getX() == x) {
						line.setStartPoint(new Point(newx, line.getY()));
					} else if (line.getX() == x + w) {
						line.setStartPoint(new Point(newx + neww, line.getY()));
					}
					if (line.getY() == y) {
						line.setStartPoint(new Point(line.getX(), newy));
						line.setLength(line.getLength() - newy + y);
					}
					if (line.getEndY() == y + h) {
						line.setLength(line.getLength() + (newy + newh) - (y + h));
					}
				}

				lines = page.hLines;
				for(int j = 0; j < lines.size(); j++) {
					line = (Line) lines.get(j);
					if (line.getY() == y) {
						line.setStartPoint(new Point(line.getX(), newy));
					} else if (line.getY() == y + h) {
						line.setStartPoint(new Point(line.getX(), newy + newh));
					}
					if (line.getX() == x) {
						line.setStartPoint(new Point(newx, line.getY()));
						line.setLength(line.getLength() - newx + x);
					}
					if (line.getEndX() == x + w) {
						line.setLength(line.getLength() + (newx + neww) - (x + w));
					}
				}
				currentPage = page;
				reCalcCells();
			}
			currentPage = cPage;			
		}
		
		this.pageFormat = pageFormat;		
		setPreferredSize(new Dimension((int)pageFormat.getWidth(), (int)pageFormat.getHeight()));
		updateUI();
	}

	private void addBorders(){
		if (currentPage == null) return;
		if (pageFormat == null) return;

		Line border0, border1, border2, border3;
		border0 = new Line(this);
		border1 = new Line(this);
		border2 = new Line(this);
		border3 = new Line(this);
		border0.setLineWidth(1);
		border1.setLineWidth(1);
		border2.setLineWidth(1);
		border3.setLineWidth(1);
		border0.setBorderType(Line.BORDER_UP);
		border1.setBorderType(Line.BORDER_LEFT);
		border2.setBorderType(Line.BORDER_DOWN);
		border3.setBorderType(Line.BORDER_RIGHT);
		border0.setOrientation(Line.HORIZONTAL_ORIENTATION);
		border1.setOrientation(Line.VERTICAL_ORIENTATION);
		border2.setOrientation(Line.HORIZONTAL_ORIENTATION);
		border3.setOrientation(Line.VERTICAL_ORIENTATION);
		
		int x = (int) pageFormat.getImageableX();
		int y = (int) pageFormat.getImageableY();
		int w = (int) pageFormat.getImageableWidth();
		int h = (int) pageFormat.getImageableHeight();
		
		x = x + Line.lineInterval - x % Line.lineInterval;
		y = y + Line.lineInterval - y % Line.lineInterval;
		w = w - Line.lineInterval - w % Line.lineInterval;
		h = h - Line.lineInterval - h % Line.lineInterval;

		border0.setInVisible(true);
		border1.setInVisible(true);
		border2.setInVisible(true);
		border3.setInVisible(true);
		border0.setStartPoint(new Point(x, y));
		border0.setLength(w);
		border1.setStartPoint(new Point(x, y));
		border1.setLength(h);
		border2.setStartPoint(new Point(x, y+h));
		border2.setLength(w);
		border3.setStartPoint(new Point(x+w, y));
		border3.setLength(h);

		currentPage.hLines.add(border0);
		currentPage.vLines.add(border1);
		currentPage.hLines.add(border2);
		currentPage.vLines.add(border3);
	}

	/** 
	 * Get x coordinate of the imageable border.
	 * this corrdinate is slightly different from 
	 * pageformat.getPaper().getImageableX(). Because for
	 * easily snap to grid, the line's position and length is 
	 * automatically set to the round interger that can be 
	 * divided exactly by Line.lineInterval. 
	 * @see net.sf.fjreport.line.Line#lineInterval lineInterval
	 * @return x
	 */
	public int getImageableBorderX(){
		int x = (int) pageFormat.getImageableX();
		return x + Line.lineInterval - x % Line.lineInterval;
	}
	/**
	 * @see FJReport#getImageableBorderX() getImageableBorderX
	 * @return  y
	 */
	public int getImageableBorderY(){
		int y = (int) pageFormat.getImageableY();
		return y + Line.lineInterval - y % Line.lineInterval;
	}
	/**
	 * @see FJReport#getImageableBorderX() getImageableBorderX
	 * @return width
	 */
	public int getImageableBorderWidth(){
		int w = (int) pageFormat.getImageableWidth();
		return w - Line.lineInterval - w % Line.lineInterval;
	}
	/**
	 * @see FJReport#getImageableBorderX() getImageableBorderX
	 * @return height
	 */
	public int getImageableBorderHeight(){
		int h = (int) pageFormat.getImageableHeight();
		return h - Line.lineInterval - h % Line.lineInterval;
	}
	
	/**
	 *  
	 */
	public Zoom getZoom() {
		return zoom;
	}

	/**
	 * zoom
	 * @param zoom
	 */
	public void setZoom(Zoom zoom) {
		this.zoom = zoom;
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	public void paint(Graphics arg0) {
		// TODO Auto-generated method stub
		super.paint(arg0);
		arg0.drawRect(1, 1, (int)pageFormat.getWidth()-2, (int)pageFormat.getHeight()-2);
		Iterator it;
		Line line;
		Graphics2D g2d = (Graphics2D) arg0;
		
		if (state == CELL_STATE) {
			if (currentPage.cells != null) {
				it = currentPage.cells.iterator();
				while (it.hasNext()) {
					((Cell)it.next()).render(0, 0, g2d);
				}
			}
		} else if (state == EDIT_STATE || state == READONLY_STATE) {
			if (currentPage.cells != null) {
				it = currentPage.cells.iterator();
				Cell cell;
				while (it.hasNext()) {
					cell = (Cell)it.next();
					if (cell.getType() == Cell.TYPE_LABEL)
						cell.render(0, 0, g2d);
				}
			}
		}
		
		it = currentPage.vLines.iterator();
		while (it.hasNext()) {
			((Line)it.next()).render(0, 0, g2d);
		}
		it = currentPage.hLines.iterator();
		while (it.hasNext()) {
			((Line)it.next()).render(0, 0, g2d);
		}
		if (operation != null) operation.render(0, 0, g2d);
		
		g2d.dispose();
	}

	/**
	 * @see FJReport#DRAWLINE_STATE DRAWLINE_STATE
	 * @see FJReport#CELL_STATE CELL_STATE
	 * @see FJReport#EDIT_STATE EDIT_STATE
	 * @see FJReport#READONLY_STATE READONLY_STATE
	 * 
	 * @return state of report
	 */
	public int getState() {
		return state;
	}

	/**
	 * @see FJReport#DRAWLINE_STATE DRAWLINE_STATE
	 * @see FJReport#CELL_STATE CELL_STATE
	 * @see FJReport#EDIT_STATE EDIT_STATE
	 * @see FJReport#READONLY_STATE READONLY_STATE
	 * 
	 * @param state
	 */
	public void setState(int state) {
//		if (this.state == state) return;
		removeAll();
		if (this.state == DRAWLINE_STATE && state != DRAWLINE_STATE) {
			reCalcCells();
			buildMap();
		}
		this.state = state;
		if (state == DRAWLINE_STATE) {
			setOperation(new LineMouseControl(this));
		} else if (state == CELL_STATE) {
			setOperation(new CellMouseControl(this));
		} else if (state == EDIT_STATE) {
			addAllEditor();
//			setOperation(new EditMouseControl(this));
			setOperation(null);
			for (int i = 0; i < pages.size(); i++) {
				List cells = ((FJReportPage)pages.get(i)).cells;
				for (int j = 0; j < cells.size(); j++) {
					((Cell)cells.get(j)).setReadOnly(false);
				}
			}
		} else if (state == READONLY_STATE) {
			addAllEditor();
			for (int i = 0; i < pages.size(); i++) {
				List cells = ((FJReportPage)pages.get(i)).cells;
				for (int j = 0; j < cells.size(); j++) {
					((Cell)cells.get(j)).setReadOnly(true);
				}
			}
		}
		updateUI();
	}
	
	private void addAllEditor() {
		if (currentPage == null || currentPage.cells == null) return;
		removeAll();
		Iterator it = currentPage.cells.iterator();
		while (it.hasNext()) {
			((Cell) it.next()).registerEditor();
		}
	}

	private void setOperation(BaseControl control) {
		if (this.operation != null) {
			if (this.operation instanceof MouseMotionListener) 
				removeMouseMotionListener(this.operation);
			if (this.operation instanceof MouseListener) 
				removeMouseListener(this.operation);
		}
		if (control != null) {
			if (control instanceof MouseMotionListener) 
				addMouseMotionListener(control);
			if (control instanceof MouseListener) 
				addMouseListener(control);
		}
		this.operation = control;
	}

	private void calcCells(){   

	// Performance of this algorithm is about O(vLine.size * hLine.size)
	// In order to keep the previous designed cells, when new cells
	// are calculated out, all new cells will be compared to the previous
	// cells, if the intersect area above 1/2, the two cells are
	// considered to be the same one, then the previous cell properties
	// will be set to the new one.
	// Considering the comparing operations between previous cells and new cells,
	// the performance of this function is about O(vLine.size * hLine.size)^2

		List vLines = currentPage.vLines;
		List hLines = currentPage.hLines;
		Collections.sort(vLines);
		Collections.sort(hLines);
		Line hLine, vLine;
		int x, y, l, x2, y2, l2;
		int i2, j2;
		int k;
		int hSize = hLines.size();
		int vSize = vLines.size();		
		boolean[][][] joins = new boolean[hSize][vSize][4]; // 4 orientation,  east, south, west, north
		boolean[] join;
		for (int i = 0; i < hSize; i++) {
			hLine = (Line) hLines.get(i);
			x = hLine.getX();
			y = hLine.getY();
			l = hLine.getLength();
			for (int j = 0; j < vSize; j++){
				vLine = (Line) vLines.get(j);
				x2 = vLine.getX();
				y2 = vLine.getY();
				l2 = vLine.getLength();
				join = joins[i][j];
				if (y < y2 - 5 || y > y2 + l2 + 5 || x2 < x - 5 || x2 > x + l + 5) { //no join
					join[0] = false;  //then 4 orientation no join
					join[1] = false;
					join[2] = false;
					join[3] = false;
					continue;
				}
				joins[i][j][0] = (x + l > x2 + 5);  // east
				joins[i][j][1] = (y2 + l2 > y + 5);  //south 
				joins[i][j][2] = (x < x2 - 5); //west
				joins[i][j][3] = (y2 < y - 5); //north
			}
		}
		for (int i = 0; i < hSize - 1; i++) {
			for (int j = 0; j < vSize - 1; j++){
				if (joins[i][j][0] && joins[i][j][1]) {  //east join && north join
					for (j2 = j + 1; j2 < vSize; j2++) //then look for next sounth join along the hLine
						if (joins[i][j2][1]) break;
					if (j2 < vSize) {  // found next south join
						for (i2 = i + 1; i2 < hSize; i2++)//then look for next west join along the hLine
							if (joins[i2][j2][2]) break;
						if (i2 < hSize) {  // found next west join
							if (joins[i2][j][3] && joins[i2][j][0]) {
								for (k = j2 - 1; k > j; k--) 
									if (joins[i2][k][3]) break;
								if (k == j) {
									for (k = i2 - 1; k > i; k--)
										if (joins[k][j][0]) break;
									if (k == i) {  //found
//										if (i != 0 || j != 0 || i2 != hSize - 1 || j2 != vSize - 1)  //abandon the border cell
											addCell(i, j, i2, j2);
									}
								}
							}								
						}
					}
					j = j2-1;		//skip the useless joins
				}
			}
		}
		updateUI();
	}

	private void addCell(int i, int j, int i2, int j2) {
		int x1 = ((Line)currentPage.vLines.get(j)).getX() + ((Line)currentPage.vLines.get(j)).getLineWidth()/2+1;
		int y1 = ((Line)currentPage.hLines.get(i)).getY() + ((Line)currentPage.hLines.get(i)).getLineWidth()/2+1;
		int x2 = ((Line)currentPage.vLines.get(j2)).getX() - ((Line)currentPage.vLines.get(j2)).getLineWidth()/2;
		int y2 = ((Line)currentPage.hLines.get(i2)).getY() - ((Line)currentPage.hLines.get(i2)).getLineWidth()/2;
		if (x2 - x1 < 4 && y2 - y1 < 4) return; // abandon tiny cell;
		Iterator it = currentPage.cells.iterator();
		Cell c;
		while (it.hasNext()) {
			c = (Cell)it.next();
			if ( c.contains(x1, y1, x2-x1, y2-y1) ) {
				currentPage.cells.remove(c);
				break;
			}
		}
		if (currentPage.oldCells != null) {
			it = currentPage.oldCells.iterator();
			while (it.hasNext()) {
				c = (Cell) it.next();
				if ( c.equil(x1, y1, x2, y2)) {
//				if ( c.equil(i, j, i2, j2) ) {	// if found the same cell in oldcells, then add the oldcell, to keep the modified properties.
					currentPage.cells.add(c);
					c.x = x1;
					c.y = y1;
					c.width = x2 - x1;
					c.height = y2 - y1;
					return;
				}
			}
			currentPage.cells.add(new Cell(this, x1, y1, x2, y2, i, j, i2, j2));
		} else currentPage.cells.add(new Cell(this, x1, y1, x2, y2, i, j, i2, j2));
	}

	/**
	 * Print the report.
	 */
	public void print() {
		PrinterJob job = PrinterJob.getPrinterJob();
		try {
			job.setPrintable(this, job.validatePage(pageFormat));
			if (job.printDialog()) {
				job.setJobName("Flying Frank Report Printing");
				job.print();
			}
		} catch (PrinterException exception) {
			JOptionPane.showMessageDialog(this, exception);
		}
	}

	private void reCalcCells() {
		Iterator it = currentPage.hLines.iterator();
		while (it.hasNext()) {
			((Line)it.next()).cells.clear();
		}
		it = currentPage.vLines.iterator();
		while (it.hasNext()) {
			((Line)it.next()).cells.clear();
		}
		currentPage.oldCells = currentPage.cells;
		currentPage.cells = new ArrayList();
		calcCells();
		currentPage.oldCells = null;
	}


	public int getScrollableUnitIncrement(Rectangle arg0, int arg1, int arg2) {
		return 10;
	}
	public int getScrollableBlockIncrement(Rectangle arg0, int arg1, int arg2) {
		return 200;
	}
	public Dimension getPreferredScrollableViewportSize() {return null;}
	public boolean getScrollableTracksViewportWidth() {return false;}
	public boolean getScrollableTracksViewportHeight() {return false;}

	class LeftAlignAction extends AbstractAction{
		public LeftAlignAction(){
			super("", new ImageIcon(instance.getClass().getResource("resources/left.gif")));
		}
		public void actionPerformed(ActionEvent arg0) {
			if (currentPage == null || currentPage.currentCell == null) return;
			currentPage.currentCell.setAlignment(Cell.ALIGN_LEFT);
			updateUI();
		}
	}
	class RightAlignAction extends AbstractAction{
		public RightAlignAction(){
			super("", new ImageIcon(instance.getClass().getResource("resources/right.gif")));
		}
		public void actionPerformed(ActionEvent arg0) {
			if (currentPage == null || currentPage.currentCell == null) return;
			currentPage.currentCell.setAlignment(Cell.ALIGN_RIGHT);
			updateUI();
		}
	}
	class CenterAlignAction extends AbstractAction{
		public CenterAlignAction(){
			super("", new ImageIcon(instance.getClass().getResource("resources/center.gif")));
		}
		public void actionPerformed(ActionEvent arg0) {
			if (currentPage == null || currentPage.currentCell == null) return;
			currentPage.currentCell.setAlignment(Cell.ALIGN_CENTER);
			updateUI();
		}
	}
	class BoldFontAction extends AbstractAction{
		public BoldFontAction(){
			super("", new ImageIcon(instance.getClass().getResource("resources/bold.gif")));
		}
		public void actionPerformed(ActionEvent arg0) {
			if (currentPage == null || currentPage.currentCell == null) return;
			Font f = currentPage.currentCell.getFont();
			currentPage.currentCell.setFont(new Font(f.getName(), f.getStyle() ^ Font.BOLD, f.getSize()));
			updateUI();
		}
	}
	class ItalicFontAction extends AbstractAction{
		public ItalicFontAction(){
			super("", new ImageIcon(instance.getClass().getResource("resources/italic.gif")));
		}
		public void actionPerformed(ActionEvent arg0) {
			if (currentPage == null || currentPage.currentCell == null) return;
			Font f = currentPage.currentCell.getFont();
			currentPage.currentCell.setFont(new Font(f.getName(), f.getStyle() ^ Font.ITALIC, f.getSize()));
			updateUI();
		}
	}
	/**
	 * get toolbar pane of report.
	 * @return the panel containing newPageButton, removePageButton, 
	 * prePageButton, nextPageButton, chooseCellFontNameComboBox,
	 * chooseCellFontSizeComboBox and 3 alignment buttons.
	 * All components are already combined with corresponding actionlistener.
	 */
	public JPanel getEditToolBarPane(){
		FlowLayout fl = new FlowLayout(FlowLayout.LEFT);
		fl.setVgap(0);
		fl.setHgap(4);
		JPanel editPane = new JPanel(fl);
		comboFont = new JComboBox(CommonUtil.fontNames);
		comboSize = new JComboBox(CommonUtil.fontSizes);
//		comboSize.setEditable(true);
		comboFont.setToolTipText(StringResource.getString("comboFontHint"));
		comboSize.setToolTipText(StringResource.getString("comboSizeHint"));
		comboFont.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				if (currentPage == null || currentPage.currentCell == null) return;
				Font f = currentPage.currentCell.getFont();
				currentPage.currentCell.setFont(new Font((String) comboFont.getSelectedItem(), f.getStyle(), f.getSize()));
				updateUI();
			}});
		comboSize.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				if (currentPage == null || currentPage.currentCell == null) return;
				Font f = currentPage.currentCell.getFont();
				currentPage.currentCell.setFont(new Font(f.getFontName(), f.getStyle(), Integer.parseInt((String)comboSize.getSelectedItem())));
				updateUI();
			}});
		JButton btnBold = new JButton(new BoldFontAction());
		JButton btnItalic = new JButton(new ItalicFontAction());
		JButton btnLeft = new JButton(new LeftAlignAction());
		JButton btnCenter = new JButton(new CenterAlignAction());
		JButton btnRight = new JButton(new RightAlignAction());
		JButton btnNewPage = new JButton(new NewPageAction(this));
		JButton btnRemovePage = new JButton(new RemovePageAction(this));
		JButton btnPrePage = new JButton(new PrePageAction(this));
		JButton btnNextPage = new JButton(new NextPageAction(this));
		
		btnBold.setToolTipText(StringResource.getString("btnBoldHint"));
		btnItalic.setToolTipText(StringResource.getString("btnItalicHint"));
		btnLeft.setToolTipText(StringResource.getString("btnLeftHint"));
		btnCenter.setToolTipText(StringResource.getString("btnCenterHint"));
		btnRight.setToolTipText(StringResource.getString("btnRightHint"));
		
		btnBold.setPreferredSize(new Dimension(26, 28));
		btnItalic.setPreferredSize(new Dimension(28, 28));
		btnLeft.setPreferredSize(new Dimension(28, 28));
		btnCenter.setPreferredSize(new Dimension(28, 28));
		btnRight.setPreferredSize(new Dimension(28, 28));
		btnNewPage.setPreferredSize(new Dimension(28, 28));
		btnRemovePage.setPreferredSize(new Dimension(28, 28));
		btnPrePage.setPreferredSize(new Dimension(28, 28));
		btnNextPage.setPreferredSize(new Dimension(28, 28));
		
		ButtonGroup bg = new ButtonGroup();
		bg.add(btnLeft);
		bg.add(btnCenter);
		bg.add(btnRight);
		
		editPane.add(btnNewPage);
		editPane.add(btnRemovePage);
		editPane.add(btnPrePage);
		editPane.add(btnNextPage);
		editPane.add(comboFont);
		editPane.add(comboSize);
		editPane.add(btnBold);
		editPane.add(btnItalic);
		editPane.add(btnLeft);
		editPane.add(btnCenter);
		editPane.add(btnRight);
		return editPane;
	}

	/**
	 * get pages of report.
	 */
	public List getPages() {
		return pages;
	}

	/**
	 * set pages of report.
	 * @param pages the new pages
	 */
	public void setPages(List pages) {
		this.pages = pages;
	}

	/**
	 * @return current page
	 */
	public FJReportPage getCurrentPage() {
		return currentPage;
	}

	/**
	 * set current page index, page 0 is first page.
	 * @param currentPageIndex
	 */
	public void setCurrentPageIndex(int currentPageIndex) {
		if (pages == null || pages.size() == 0) return;
		if (currentPageIndex >= pages.size()) currentPageIndex = pages.size() - 1;
		else if (currentPageIndex < 0) currentPageIndex = 0;
		this.currentPageIndex = currentPageIndex;
		this.currentPage = (FJReportPage) pages.get(currentPageIndex);
		if (state == EDIT_STATE) addAllEditor();
		String pageMsg = StringResource.getString("pageMessage");
		pageMsg = pageMsg.replace("%p", String.valueOf(currentPageIndex + 1));
		pageMsg = pageMsg.replace("%totalpage", String.valueOf(pages.size()));
		notifyStatusChange(1, pageMsg);
		updateUI();
	}
	public void nextPage(){
		setCurrentPageIndex(currentPageIndex + 1);
	}
	public void prePage(){
		setCurrentPageIndex(currentPageIndex - 1);
	}
	public void lastPage() {
		setCurrentPageIndex(pages.size() - 1);
	}
	public void firstPage() {
		setCurrentPageIndex(0);
	}
	public void insertPage(){
		if (pages == null) pages = new ArrayList();
//		pages.add(currentPageIndex, new FJReportPage());
//		setCurrentPageIndex(currentPageIndex);
		pages.add(new FJReportPage());
		lastPage();
		addBorders();
		reCalcCells();
		updateUI();
	}
	public void removePage(FJReportPage page){
		if (pages == null || pages.size() < 2 || page == null) return;
		pages.remove(page);
		setCurrentPageIndex(currentPageIndex);
	}

	private List statusListeners = new ArrayList();
	/* 
	 * when page change, a "page n of m" message will be fired.
	 * @see net.sf.fjreport.statusbar.HasStatus#addStatusChangeListener(net.sf.fjreport.statusbar.StatusChangeListener)
	 */
	public void addStatusChangeListener(StatusChangeListener statusListener) {
		statusListeners.add(statusListener);
	}
	public void removeStatusChangeListener(StatusChangeListener statusListener) {
		statusListeners.remove(statusListener);
	}
	private void notifyStatusChange(int messageType, String message){
		for (int i = 0; i < statusListeners.size(); i++) {
			((StatusChangeListener)statusListeners.get(i)).statusChange(messageType, message);
		}
	}
	
	/**
	 * printer page setup
	 */
	public void printSetup() {
		PrinterJob job = PrinterJob.getPrinterJob();
		PageFormat pf = job.validatePage(job.pageDialog(pageFormat));
		if (pf != null && !CommonUtil.isPageEqual(pf, pageFormat))
			setPageFormat(pf);
	}
	
	/**
	 * save report to a file
	 * @param fileName the file name the report be saved to
	 */
	public void saveReport(String fileName){
		try {
			FJReportIO.saveReport(fileName, this);
		} catch (TransformerException e) {
				e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
	/**
	 * load a report from file.
	 * @param fileName file name of the report be loaded from 
	 */
	public void loadReport(String fileName) {
		try {
			FJReportIO.loadReport(fileName, this);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadReport(InputStream is) {
		try {
			FJReportIO.loadReport(is, this);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * a save file chooser will be poped out before calling saveReport() function. 
	 */
	public File saveDialog() {
		JFileChooser chooser = new JFileChooser();
	    chooser.setFileFilter(new ReportFileFilter());
	    if(chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
	    	saveReport(chooser.getSelectedFile().getAbsolutePath());
	    	return chooser.getSelectedFile();
	    } return null;
	}

	/**
	 * an open file chooser will be poped out before calling loadReport() function. 
	 */
	public File loadDialog() {
		JFileChooser chooser = new JFileChooser();
	    chooser.setFileFilter(new ReportFileFilter());
	    if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
	    	loadReport(chooser.getSelectedFile().getAbsolutePath());
	    	return chooser.getSelectedFile();
	    } return null;
	}
	
	/**
	 * Each cell except type_null cell is identified by a string which is the
	 * cell's name. Each null cell has value (mostly is instanceof String) which
	 * is shown to end user. The cell value can be access via
	 * FJReport.getValue(String name) and FJReport.setValue(String name, Object
	 * value). Use FJReport.getCellByName(String name) to access a specific
	 * cell.
	 * 
	 * @param name
	 * @param value
	 */
	public void setValue(String name, Object value) {
		Cell cell = getCellByName(name);
		if (cell != null) cell.setValue(value);
	}
	/**
	 * @see FJReport#setValue(String, Object) setValue
	 * @param name
	 * @return value
	 */
	public Object getValue(String name) {
		Cell cell = getCellByName(name);
		if (cell != null) return cell.getValue();
		else return null;
	}
	/**
	 * @see FJReport#setValue(String, Object) setValue
	 * @param name
	 * @return cell
	 */
	public Cell getCellByName(String name){
		return (Cell) cellMap.get(name);
	}
	public void buildMap(){
		cellMap = new HashMap();
		for(int i = 0; i < pages.size(); i ++) {
			List cells = ((FJReportPage)pages.get(i)).cells;
			for(int j = cells.size() - 1; j >= 0 ; j--) {
				Cell cell = (Cell) cells.get(j);
				cellMap.put(cell.getName(), cell);
			}
		}
	}
	
	private List<CellValueChangeListener> valueChangedActions;
	/**
	 * when user change a cell's value, a ValueChanged event will occur.
	 * @param listener
	 */
	public void addValueChangedActionListener(CellValueChangeListener listener){
		if (valueChangedActions == null) valueChangedActions = new ArrayList();
		valueChangedActions.add(listener);
	}
	public void removeValueChangeActionListener(CellValueChangeListener listener) {
		valueChangedActions.remove(listener);
	}
	public boolean hasValueChangedListener(){
		return (valueChangedActions != null && valueChangedActions.size() > 0);
	}
	public void fireValueChanged(Cell cell, Object newValue, Object oldValue){
		for(int i = valueChangedActions.size() - 1; i >= 0; i--) {
			((CellValueChangeListener)valueChangedActions.get(i)).valueChangeAction(cell, newValue, oldValue);
		}
	}
	private List<CellEnterActionListener> cellEnterActions;
	/**
	 * 
	 * cellEnter, occurs when cell grabs focus.
	 * Note that only cells with editors can activate this event
	 * @param listener
	 */
	public void addCellEnterActionListener(CellEnterActionListener listener){
		if (cellEnterActions == null) cellEnterActions = new ArrayList();
		cellEnterActions.add(listener);
	}
	public void removeCellEnterActionListener(CellEnterActionListener listener) {
		cellEnterActions.remove(listener);
	}
	public boolean hasCellEnterListener(){
		return (cellEnterActions != null && cellEnterActions.size() > 0);
	}
	public void fireCellEnter(Cell cell){
		for(int i = cellEnterActions.size() - 1; i >= 0; i--) {
			((CellEnterActionListener)cellEnterActions.get(i)).cellEnterAction(cell);
		}
	}
	private List<CellLeaveActionListener> cellLeaveActions;
	/**
	 * cellLeave, occurs when cell loose focus.
	 * Note that only cells with editors can activate this event
	 * @param listener
	 */
	public void addCellLeaveActionListener(CellLeaveActionListener listener){
		if (cellLeaveActions == null) cellLeaveActions = new ArrayList();
		cellLeaveActions.add(listener);
	}
	public void removeCellLeaveActionListener(CellLeaveActionListener listener) {
		cellLeaveActions.remove(listener);
	}
	public boolean hasCellLeaveListener(){
		return (cellLeaveActions != null && cellLeaveActions.size() > 0);
	}
	public void fireCellLeave(Cell cell){
		for(int i = cellLeaveActions.size() - 1; i >= 0; i--) {
			((CellLeaveActionListener)cellLeaveActions.get(i)).cellLeaveActionListener(cell);
		}
	}

	public void preview() {}

	public int getCurrentPageIndex() {
		return currentPageIndex;
	}
//	class MyKeyboardManager extends DefaultKeyboardFocusManager {
//
//		public boolean dispatchKeyEvent(KeyEvent e) {
//			System.out.println(e.getID());
//			if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
//				System.out.println("Key Pressed");
//				return true;
//			}
//			return super.dispatchKeyEvent(e);
//		}
//	}
	public int getLineID() {
		return currentPage.getLineID();
	}
}
