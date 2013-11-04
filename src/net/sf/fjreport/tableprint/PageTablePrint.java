package net.sf.fjreport.tableprint;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.Closeable;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.table.TableModel;

import net.sf.fjreport.Printer;
import net.sf.fjreport.cell.Cell;
import net.sf.fjreport.control.CloseAction;
import net.sf.fjreport.control.FirstPageAction;
import net.sf.fjreport.control.LastPageAction;
import net.sf.fjreport.control.NextPageAction;
import net.sf.fjreport.control.PrintSetupAction;
import net.sf.fjreport.control.PrePageAction;
import net.sf.fjreport.control.PrintAction;
import net.sf.fjreport.statusbar.JStatusBar;
import net.sf.fjreport.statusbar.StatusChangeListener;
import net.sf.fjreport.util.CommonUtil;

public class PageTablePrint extends JPanel implements Printable, 
	StatusChangeListener, Printer, Closeable{
	
	private PageTableModel tm;
	private static PageFormat pageFormat;
	// static pageFormat, to make sure that all instances share the same page format. 
	
	private Cell titleCell = new Cell(Cell.TYPE_TEXTAREA);
	private Cell headerCell = new Cell(Cell.TYPE_TEXTAREA);
	private Cell footerCell = new Cell(Cell.TYPE_TEXTAREA);
	private String footerStr;
	private String headerStr;
	
	private JTable tb;
	private JScrollPane tbPane;
	private boolean printHeaderOnFirstPage;
	private static DateFormat defaultDf = new SimpleDateFormat("yyyy-MM-dd");
	
	private JDialog frame;
	
	private JStatusBar statusBar;
	private PageTablePrint instance;
	private MouseListener tbMouseListener = new MouseAdapter(){
		public void mouseClicked(MouseEvent arg0) {
			if (arg0.getClickCount() == 2) {
				if (ColumnVisible.setVisible((Component) arg0.getSource(), tm, PageTablePrint.pageFormat.getWidth())) {
					refreshVisibleColumns();
				}
			}
		}
	};
	
	public PageTableModel getModel(){
		return tm;
	}
	
	public PageTablePrint(){
		super(null);
		setBackground(Color.WHITE);
		this.setDoubleBuffered(true);
		instance = this;
	}
	
	public void packTable(Component parent, String title, JTable srcTable, PageFormat pageFormat) {
		if (srcTable == null) return;
		packTable(parent, title, srcTable.getModel(), pageFormat);
	}
	public void packTable(Component parent, String title, TableModel srcTableModel, PageFormat pageFormat) {
		tm = new PageTableModel(srcTableModel, this);
		tb = new JTable(tm);
		tbPane = new JScrollPane(tb, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(tbPane);
		tm.addStatusChangeListener(this);
		PrinterJob job = PrinterJob.getPrinterJob();
		if (pageFormat != null) {
			setPageFormat(job.validatePage(pageFormat));
		} else {
			setPageFormat(job.validatePage(PrinterJob.getPrinterJob().defaultPage()));			
		}
		if (parent == null) frame = new JDialog();
		else frame = new JDialog(JOptionPane.getFrameForComponent(parent));
		createFrame(title);
		tb.addMouseListener(tbMouseListener);
	}
	
	public void refreshVisibleColumns(){
		tm.buildVisibleIndices();
		tb = new JTable(tm);
		Rectangle rect = tbPane.getBounds();
		instance.remove(tbPane);
		tbPane = new JScrollPane(tb, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		tbPane.setBounds(rect);
		instance.add(tbPane);
		tb.addMouseListener(tbMouseListener);
	}
	
	private void createFrame(String title) {
		statusBar = new JStatusBar(new int[]{1});
		frame.setTitle(title);
		JToolBar toolbar = new JToolBar();
		JButton btnFirst = new JButton(new FirstPageAction(tm));
		JButton btnPre = new JButton(new PrePageAction(tm));
		JButton btnNext = new JButton(new NextPageAction(tm));
		JButton btnLast = new JButton(new LastPageAction(tm));
		JButton btnPrint = new JButton(new PrintAction(this));
		JButton btnPageSetup = new JButton(new PrintSetupAction(this));
		JButton btnClose = new JButton(new CloseAction(this));
		toolbar.add(btnFirst);
		toolbar.add(btnPre);
		toolbar.add(btnNext);
		toolbar.add(btnLast);
		toolbar.addSeparator();
		toolbar.add(btnPageSetup);
		toolbar.add(btnPrint);
		toolbar.addSeparator();
		toolbar.add(btnClose);
		frame.add(toolbar, "North");
		frame.add(new JScrollPane(this), "Center");
		frame.add(statusBar, "South");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
	public void setPageFormat(PageFormat pageFormat) {
		PageTablePrint.pageFormat = pageFormat;
		setPreferredSize(new Dimension((int)pageFormat.getWidth(), (int)pageFormat.getHeight()));
		// todo: adjust column width
	}

	public int print(Graphics arg0, PageFormat arg1, int arg2) throws PrinterException {
		if (tm == null || arg2 >= tm.getTotalPageCount() || arg2 < 0) 
			return Printable.NO_SUCH_PAGE;
		tm.setPage(arg2);
		frame.pack();
		paint(arg0, true);
		return 0;
	}

	public void setTitle(String title){
		titleCell.setValue(title);
	}
	public void setFooter(String footer) {
		footerCell.setValue(footer);
		footerStr = footer;
	}
	public void setHeader(String header) {
		headerCell.setValue(header);
		headerStr = header;
	}
	public String getTitle(){
		return (String) titleCell.getValue();
	}
	public String getFooter() {
		return (String) footerCell.getValue();
	}
	public String getHeader() {
		return (String) headerCell.getValue();
	}
	
	public int getFooterAlignment() {
		return footerCell.getAlignment();
	}

	public void setFooterAlignment(int footerAlignment) {
		footerCell.setAlignment(footerAlignment);
	}

	public Font getFooterFont() {
		return footerCell.getFont();
	}

	public void setFooterFont(Font footerFont) {
		footerCell.setFont(footerFont);
	}

	public int getTitleAlignment() {
		return titleCell.getAlignment();
	}

	public void setTitleAlignment(int titleAlignment) {
		titleCell.setAlignment(titleAlignment);
	}

	public Font getTitleFont() {
		return titleCell.getFont();
	}

	public void setTitleFont(Font titleFont) {
		titleCell.setFont(titleFont);
	}

	public static PageFormat getPageFormat() {
		return PageTablePrint.pageFormat;
	}

	public int getHeaderAlignment() {
		return headerCell.getAlignment();
	}

	public void setHeaderAlignment(int headerAlignment) {
		headerCell.setAlignment(headerAlignment);
	}

	public Font getHeaderFont() {
		return headerCell.getFont();
	}

	public void setHeaderFont(Font headerFont) {
		headerCell.setFont(headerFont);
	}

	public Cell getFooterCell() {
		return footerCell;
	}

	public void setFooterCell(Cell footerCell) {
		this.footerCell = footerCell;
	}

	public Cell getHeaderCell() {
		return headerCell;
	}

	public void setHeaderCell(Cell headerCell) {
		this.headerCell = headerCell;
	}

	public Cell getTitleCell() {
		return titleCell;
	}

	public void setTitleCell(Cell titleCell) {
		this.titleCell = titleCell;
	}

	private void pack() {
		int x = (int) pageFormat.getImageableX() + 1;
		int y = (int) pageFormat.getImageableY() + 1;
		int w = (int) pageFormat.getImageableWidth() - 2;
		int h = (int) pageFormat.getImageableHeight() - 2;
		
		Graphics g = getGraphics();
		
		if (tm.getPage() == 0) {  // if is first page
			titleCell.height = titleCell.getContentHeight(g);
			titleCell.width = w;
			titleCell.x = x;
			titleCell.y = y;
		} else {
			titleCell.height = 0;
			titleCell.y = y;
		}
		
		if (tm.getPage() > 0 || isPrintHeaderOnFirstPage()) {
			headerCell.height = headerCell.getContentHeight(g);
			headerCell.width = (int) pageFormat.getImageableWidth() - 2;
			headerCell.x = (int) pageFormat.getImageableX() + 1;
			headerCell.y = titleCell.y + titleCell.height;
		} else {
			headerCell.height = 0;
			headerCell.y = titleCell.y + titleCell.height;
		}
		
		tbPane.setBounds(x, headerCell.y + headerCell.height, w, (int) (tbPane
				.getColumnHeader().getPreferredSize().getHeight()
				+ tm.getRowCount() * tb.getRowHeight() + 2));
			

		footerCell.height = footerCell.getContentHeight(g);
		footerCell.width = w;
		footerCell.x = x;
		footerCell.y = tbPane.getY() + tbPane.getHeight();
		//y + h - footerCell.height;
		
		
		g.dispose();
	}
	
	public void packPage(){
		int x = (int) pageFormat.getImageableX() + 1;
		int y = (int) pageFormat.getImageableY() + 1;
		int w = (int) pageFormat.getImageableWidth() - 2;
		int h = (int) pageFormat.getImageableHeight() - 2;
		
		Graphics g = getGraphics();
		
		int titleHeight = titleCell.getContentHeight(g);
		int headerHeight = headerCell.getContentHeight(g);
		int footerHeight = footerCell.getContentHeight(g);
		
		int tableTitleHeight = tbPane.getColumnHeader().getHeight();
		int rowHeight = tb.getRowHeight();
		
		int firstPageRow;
		int pageRow;
		if (isPrintHeaderOnFirstPage()) {
			firstPageRow = (h - tableTitleHeight - titleHeight - headerHeight - footerHeight) / rowHeight;
		} else firstPageRow = (h - tableTitleHeight - titleHeight - footerHeight) / rowHeight;
		pageRow = (h - tableTitleHeight - headerHeight - footerHeight) / rowHeight;
		tm.packPages(pageRow, firstPageRow);
	}
	
	public void preview() {
		frame.pack();
		if (tm != null) {
			packPage();
			tm.firstPage();
		}
		frame.setSize(new Dimension(Math.min(Toolkit.getDefaultToolkit().getScreenSize().width-100, getPreferredSize().width + 100),
				Math.min(Toolkit.getDefaultToolkit().getScreenSize().height-100, getPreferredSize().height + 50)));
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	public void printSetup() {
		PrinterJob job = PrinterJob.getPrinterJob();
		PageFormat pf = job.validatePage(job.pageDialog(pageFormat));
		if (pf != null && !CommonUtil.isPageEqual(pf, pageFormat)) {
			setPageFormat(pf);
			packPage();
			tm.firstPage();
			updateUI();
		}
	}
	public void print() {
		frame.pack();
		if (tm != null) {
			packPage();
			tm.firstPage();
		}
		PrinterJob job = PrinterJob.getPrinterJob();
		try {
			job.setPrintable(this, job.validatePage(pageFormat));
			if (job.printDialog()) {
				job.setJobName(frame.getTitle() == null ? "Flying Frank Report Printing" : frame.getTitle());
				job.print();
			}
		} catch (PrinterException exception) {
			JOptionPane.showMessageDialog(tb, exception.getMessage());
		} finally {
			job.cancel();
		}
	}

	public boolean isPrintHeaderOnFirstPage() {
		return printHeaderOnFirstPage;
	}

	public void setPrintHeaderOnFirstPage(boolean printHeaderOnFirstPage) {
		this.printHeaderOnFirstPage = printHeaderOnFirstPage;
	}

	public void paint(Graphics arg0) {
		paint(arg0, false);
	}

	private void paint(Graphics arg0, boolean isPrinting) {
		tm.fireTableDataChanged();
		pack();

		Graphics2D g2d = (Graphics2D)arg0;
		if (!isPrinting) super.paint(arg0);
		else {
//			g2d.translate(tbPane.getX(), tbPane.getY());
//			tbPane.paintAll(g2d);
//			g2d.translate(-tbPane.getX(), -tbPane.getY());
			if (frame.isVisible())
				paintComponents(g2d);
			else
				super.paint(arg0);
		}
		
		titleCell.drawContent(g2d);
		headerCell.drawContent(g2d);
		footerCell.drawContent(g2d);
		if (!isPrinting) {
			arg0.drawLine((int)pageFormat.getWidth()+1, 0, (int)pageFormat.getWidth() + 1, (int)pageFormat.getHeight() + 1);
			arg0.drawLine(0, (int)pageFormat.getHeight() + 1, (int)pageFormat.getWidth() + 1, (int)pageFormat.getHeight() + 1);
			arg0.setColor(new Color(200, 200, 200));
			arg0.fillRect((int)pageFormat.getWidth()+2, 0, getWidth(), (int)pageFormat.getHeight());
			arg0.fillRect(0, (int)pageFormat.getHeight() + 2, getWidth(),getHeight());
			arg0.setColor(Color.BLACK);
		}
	}

	public void statusChange(int messageType, String message) {
		statusBar.setContent(messageType, message);
		headerCell.setValue(getStr(headerStr));
		footerCell.setValue(getStr(footerStr));
	}

	private String getStr(String str) {
		if (str == null) return "";
		String result = new String(str);
		result = result.replace("%date", defaultDf.format(new Date()));
		result = result.replace("%p", String.valueOf(tm.getPage()+1));
		result = result.replace("%allpage", String.valueOf(tm.getTotalPageCount()));
		return result;
	}

	public void close() throws IOException {
		if (frame != null && frame.isVisible()) frame.setVisible(false);
	}

	
}
