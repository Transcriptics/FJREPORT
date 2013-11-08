package net.sf.fjreport.cell;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Locale;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.sf.fjreport.FJReport;
import net.sf.fjreport.datepicker.FJDateField;
import net.sf.fjreport.io.ReportImageFileFilter;
import net.sf.fjreport.util.StringResource;


public class Cell extends Rectangle{

	private JComponent editor;
	private boolean readOnly;
	private Cell instance = this;
	private static Font defaultFont = new Font("Dialog", 0, 24);
	
	//indices of 4 lines composing of the cell. top, left, bottom, right 
	
	// id = topLine.id * 512^3 + leftLine.id * 512^2 + bottomLine.id * 512 + 
	private long id;
	private FJReport report;
	
	private String name = "";
	private Object value = "";
	private Image imgValue;

	private int type;
	public static final int TYPE_NULL = 0;
	public static final int TYPE_LABEL = 1;
	public static final int TYPE_TEXTFIELD = 2;
	public static final int TYPE_TEXTAREA = 3;
	public static final int TYPE_COMBOBOX = 4;
	public static final int TYPE_CHECKBOX = 5;
	public static final int TYPE_DATEFIELD = 6;
	public static final int TYPE_IMAGE = 7;
	public static final int TYPE_PANEL = 8;
	public static final int TYPE_CUSTOM = 9;
	
	private int alignment;
	public static final int ALIGN_LEFT = 0;
	public static final int ALIGN_CENTER = 1;
	public static final int ALIGN_RIGHT = 2;
	private Font font = defaultFont;

	private boolean selected;
	
	private String[] comboxOptions;
	private static final Insets DEFAULT_MARGIN = new Insets(0, 0, 0, 0);
	private Insets margin = DEFAULT_MARGIN;
	
	private static final int innerMargin = 3;
	private static final Insets insets = new Insets(innerMargin, innerMargin, innerMargin, innerMargin);
	
	public boolean isSelected() {
		return selected;
	}

	public void render(int w, int h, Graphics2D g2d){
		if (width <= 0 || height <= 0) return;
		if (isSelected()) {
			Color color = g2d.getColor();
			g2d.setColor(new Color(224, 224, 224));
			g2d.fillRect(x + margin.left, y + margin.top, 
					width - margin.left - margin.right, 
					height - margin.top - margin.bottom);
			g2d.setColor(color);
		}
		drawContent(g2d);
	}
	
	public void drawContent(Graphics2D g2d) {
		int x, y, height, width;
		x = this.x + margin.left;
		y = this.y + margin.top;
		width = this.width - margin.left - margin.right;
		height = this.height - margin.top - margin.bottom;
		if (height == 0 || width == 0) return;
		if (type == TYPE_IMAGE  && (report != null && report.getState() != FJReport.CELL_STATE)) {
			if (imgValue != null)
			g2d.drawImage(imgValue, x, y, null);
			return;
		}
		if (isEmpty()) return;
		if (type == this.TYPE_CHECKBOX) {
			if (editor == null) {
				registerEditor();
			}
			g2d.translate(x, y);
			editor.paint(g2d);
			g2d.translate(-x, -y);
			return;
		}
		String displayStr = null;
		if (type == this.TYPE_LABEL || report == null || report.getState() != FJReport.CELL_STATE)
			displayStr = getStrValue();
		else {
			displayStr = getName();
			g2d.setColor(Color.RED);
		}
		if (displayStr == null) return;
		g2d.setFont(font);
		FontMetrics fm = g2d.getFontMetrics(font);
		Rectangle2D rect = fm.getStringBounds(displayStr, g2d);
		String[] strs = null;
		strs = displayStr.split("\n");
		if (strs == null || strs.length == 0) return;
		if (strs.length == 1) {	// single line
			if (alignment == ALIGN_CENTER)
				g2d.drawString(displayStr,
						(int)(x + (width - rect.getWidth())/2),
						(int)(y + (height - rect.getHeight())/2 - rect.getMinY()));
			else if (alignment == ALIGN_LEFT) 
				g2d.drawString(displayStr,
						x + innerMargin,
						(int)(y + (height - rect.getHeight())/2 - rect.getMinY()));
			else if (alignment == ALIGN_RIGHT)
				g2d.drawString(displayStr,
						(int)(x + width - rect.getWidth() - innerMargin),
						(int)(y + (height - rect.getHeight())/2 - rect.getMinY()));
		} else {		// multi line
//			int hIncrement = (height - innerMargin - innerMargin - (int)rect.getHeight()) / (strs.length - 1);
			int hIncrement = (int)rect.getHeight();
			int baseY = (int) (y + innerMargin - rect.getMinY());
			if (alignment == ALIGN_LEFT)
				for (int i = 0; i < strs.length; i++) {
					g2d.drawString(strs[i],	x + innerMargin, baseY);
					baseY += hIncrement;
				}
			else if (alignment == ALIGN_CENTER) {
				for (int i = 0; i < strs.length; i++) {
					rect = fm.getStringBounds(strs[i], g2d);
					g2d.drawString(strs[i],	(int)(x + (width - rect.getWidth())/2), baseY);
					baseY += hIncrement;
				}
			}
			else if (alignment == ALIGN_RIGHT) {
				for (int i = 0; i < strs.length; i++) {
					rect = fm.getStringBounds(strs[i], g2d);
					g2d.drawString(strs[i],	(int)(x + width - rect.getWidth() - innerMargin), baseY);
					baseY += hIncrement;
				}
			}
		}
		g2d.setColor(Color.BLACK);
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
//		if (report == null) return;
//		if (report.getState() == FJReport.EDIT_STATE) {
//			if (selected) {
//				report.removeAll();
//				if (editor == null) createEditor();
//				if (editor != null) {
//					setEditorValue(value);
//					report.add(editor);
//					editor.setBounds(this);
//					editor.grabFocus();
//				}
//				report.updateUI();
//			} else {
//				if (editor != null) {
//					value = getEditorValue();
//					report.remove(editor);
//				}
//			}
//		}
	}

	public Cell(FJReport report, int x1, int y1, int x2, int y2, int top, int left, int bottom, int right){
		super(x1, y1, x2-x1, y2-y1);
		this.report = report;
		id = top * 256 * 256 * 256 + left * 256 * 256 + bottom * 256 + right; 
		font = report.getFont();
	}
	
	public Cell(int x, int y, int w, int h) {
		super(x, y, w, h);
	}
	public Cell(int type) {
		super();
		this.type = type;
	}

	public JComponent getEditor() {
		return editor;
	}

	public void setEditor(JComponent editor) {
		this.editor = editor;
		setSelected(isSelected());
	}

	/**
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return if two cells are same
	 */
	public boolean equil(int x1, int y1, int x2, int y2) {  // if intersect >= 1/2 then equil
		// TODO Auto-generated method stub
		int intersectW = Math.min(x+width, x2) - Math.max(x, x1);
		if (intersectW <= 0) return false;
		int intersectH = Math.min(y+height, y2) - Math.max(y, y1);
		if (intersectH <= 0) return false;
		return intersectW * intersectH * 2 >= width * height;
//		return (i == topLineIndex && j == leftLineIndex && i2 == bottomLineIndex && j2 == rightLineIndex);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		if (type == this.type) return;
		if ( isEmpty() ) {
			setEditor(null);
			return;
		} else if (editor != null ){
			editor = null;
		}
		this.type = type;
	}
	
	public void createEditor(){
		if (type == TYPE_LABEL) {
//			editor = new JLabel();
		}
		else if (type == TYPE_TEXTFIELD) {
			editor = new JTextField();
			((JTextField)editor).setBorder(null);
			((JTextField)editor).setMargin(insets);
		}
		else if (type == TYPE_TEXTAREA) {
			editor = new JTextArea();
			((JTextArea)editor).setBorder(null);
			((JTextArea)editor).setMargin(insets);
			((JTextArea)editor).setWrapStyleWord(true);
			((JTextArea)editor).setLineWrap(true);
		}
		else if (type == TYPE_COMBOBOX) {
			editor = new JComboBox();
			if (comboxOptions != null && comboxOptions.length > 0) {
				for (int i = 0; i < comboxOptions.length; i++) {
					((JComboBox)editor).addItem(comboxOptions[i]);
				}
			}
//			((JComboBox)editor).setEditable(true);
		}
		else if (type == TYPE_CHECKBOX) {
			editor = new JCheckBox();
			((JCheckBox)editor).setMargin(insets);
		}
		else if (type == TYPE_DATEFIELD) {
			editor = new FJDateField();
			((FJDateField)editor).setMargin(insets);
		} 
		else if (type == TYPE_IMAGE) {
			editor = new JPanel() {
				public void paint(Graphics g) {
					super.paint(g);
					if (imgValue != null) g.drawImage(imgValue, 0, 0, null);
				}
			};
			((JPanel)editor).setFocusable(true);
			((JPanel)editor).setBackground(Color.WHITE);
			editor.addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent arg0) {
					if (isReadOnly()) return;
					JFileChooser chooser = new JFileChooser();
				    chooser.setFileFilter(new ReportImageFileFilter());
				    if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				    	loadImage(chooser.getSelectedFile().getAbsolutePath());
				    	editor.updateUI();
				    };
				}});
		}
		if (editor != null) {
			editor.setBorder(null);
			editor.setFont(font);
			editor.setBackground(Color.WHITE);
			editor.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent arg0) {
					if (report != null && report.hasCellEnterListener())
						report.fireCellEnter(instance);
					report.currentPage.currentCell = instance;
				}

				public void focusLost(FocusEvent arg0) {
					if (!getValue().equals(instance.getEditorValue())) {
						setValue(instance.getEditorValue());
					}
					if (report != null && report.hasCellLeaveListener())
						report.fireCellLeave(instance);
				}
			});
			setAlignment(alignment);
			setEditorValue(value);
		}
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		if (value == null) value = "";
		if (!value.equals(this.value)) {
			if (report != null && report.hasValueChangedListener())
				report.fireValueChanged(this, value, this.value);
		}
		Object editorValue = getEditorValue();
		if (editorValue == null || !editorValue.equals(value)) setEditorValue(value);
		this.value = value;
	}
	
	public boolean isEmpty(){
		if ((getName()==null || getName().equals(""))
				&& (getValue()==null || getValue().equals(""))) return true;
		else return false;
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
		if (editor != null) {
			editor.setFont(font);
			editor.updateUI();
		} 
		if (editor != null) {
			editor.setBorder(null);
		}
	}

	public int getAlignment() {
		return alignment;
	}

	public void setAlignment(int alignment) {
		this.alignment = alignment;
		if (editor != null) {
			if (editor instanceof JLabel) {
				if (alignment == ALIGN_LEFT)
					((JLabel)editor).setHorizontalAlignment(JLabel.LEFT);
				else if (alignment == ALIGN_CENTER)
					((JLabel)editor).setHorizontalAlignment(JLabel.CENTER);
				else if (alignment == ALIGN_RIGHT)
					((JLabel)editor).setHorizontalAlignment(JLabel.RIGHT);
			} else if (editor instanceof JTextField) {
				if (alignment == ALIGN_LEFT)
					((JTextField)editor).setHorizontalAlignment(JTextField.LEFT);
				else if (alignment == ALIGN_CENTER)
					((JTextField)editor).setHorizontalAlignment(JTextField.CENTER);
				else if (alignment == ALIGN_RIGHT)
					((JTextField)editor).setHorizontalAlignment(JTextField.RIGHT);
			} else if (editor instanceof JTextArea) {
			}
		}
	}

	public static String getTypeDescription(int type) {
		return StringResource.getString("cellType" + String.valueOf(type));
	}

	public String[] getComboxOptions() {
		return comboxOptions;
	}
	public void setComboxOptions(String[] comboxOptions) {
		this.comboxOptions = comboxOptions;
	}
	public String getComboxOptionsStr() {
		String result = "";
		if (comboxOptions != null) {
			for (int i = 0; i < comboxOptions.length; i++) {
				if (i == 0) result = comboxOptions[i];
				else result = result + "\n" + comboxOptions[i];
			}
		}
		return result;
	}
	public void setComboxOptionsStr(String comboxOptionsStr) {
		if (comboxOptionsStr == null || comboxOptionsStr.equals("")) return;
		setComboxOptions(comboxOptionsStr.split("\n"));
	}

	public void registerEditor() {
		if (editor == null) {
			createEditor();
		}
		if (editor != null) {
			try {
				report.add(editor);
				setEditorBounds();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void printRender(Graphics2D arg0) {
		if (type == TYPE_TEXTAREA) {
			arg0.translate(x, y);
			editor.paint(arg0);
			arg0.translate(-x, -y);
			return;
		}
		drawContent(arg0);
	}
	
	public int getContentHeight(Graphics g){
		String value = getStrValue();
		if (value == null || value.equals("")) return 0;
		String[] strs = value.split("\n");
		if (strs == null || strs.length == 0) return 0;
		return ((int) g.getFontMetrics(font).getStringBounds(" ", g).getHeight()
				* (strs.length) + innerMargin * 2);
	}


	public String getStrValue() {
//		try {
//			System.out.println((String)value);
//		} catch(Exception e) {
//			System.out.println(this.getTypeDescription(type));
//			System.out.println(value.getClass());
//		}
		if (value instanceof Date) {
			if (value != null)
				return FJDateField.defaultDf.format(value);
			else return "";
		}
		return (String) value;
	}

	public FJReport getReport() {
		return report;
	}

	public void setReport(FJReport report) {
		this.report = report;
	}
	public Object getEditorValue(){
		if (editor == null) return value;
		if (type == TYPE_LABEL) {
			return ((JLabel)editor).getText();
		} else if (type == this.TYPE_TEXTFIELD) {
			return ((JTextField)editor).getText();
		} else if (type == this.TYPE_TEXTAREA) {
			return ((JTextArea)editor).getText();
		} else if (type == this.TYPE_COMBOBOX) {
			return (String) ((JComboBox)editor).getSelectedItem();
		} else if (type == this.TYPE_CHECKBOX) {
			return String.valueOf(((JCheckBox)editor).isSelected());			
		} else if (type == this.TYPE_DATEFIELD) {
			return ((FJDateField)editor).getValue();
		}
		return value;
	}
	
	public void setEditorValue(Object value) {
		if (editor == null) return;
		if (type == TYPE_LABEL) {
			((JLabel)editor).setText((String) value);
		} else if (type == TYPE_TEXTFIELD) {
			((JTextField)editor).setText((String) value);
		} else if (type == TYPE_TEXTAREA) {
			((JTextArea)editor).setText((String) value);
		} else if (type == TYPE_COMBOBOX) {
			((JComboBox)editor).setSelectedItem(value);
		} else if (type == TYPE_CHECKBOX) {
			if (value == null || "".equals(value)) ((JCheckBox)editor).setSelected(false);
			else ((JCheckBox)editor).setSelected((Boolean) value);
		} else if (type == TYPE_DATEFIELD) {
			if (value instanceof Date)
				((FJDateField)editor).setValue((Date) value);
			else if (value instanceof String)
				((FJDateField)editor).setDateStr((String) value);
		} else if (type == TYPE_IMAGE) {
			if (value instanceof String) {
				if (value.equals("")) imgValue = null;
				else loadImage((String) value);
			} else if (value instanceof Image) {
				createBufferredImage((Image) value);
			} else imgValue = null;
			editor.updateUI();
		}
	}

	private void loadImage(String imgFileName) {
		Image img = null;
		URL url = null;
		try {
			url = new URL(imgFileName);
		} catch (MalformedURLException e) {
			url = null;
		}
		if (url != null)
			img = Toolkit.getDefaultToolkit().getImage(url);
		else img = Toolkit.getDefaultToolkit().getImage(imgFileName);
		MediaTracker imgLoadCheck = new MediaTracker(report);
		imgLoadCheck.addImage(img, 0);
		try {
			imgLoadCheck.waitForID(0);
			createBufferredImage(img);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createBufferredImage(Image img) {
		int srcWidth, srcHeight, dstWidth, dstHeight;
		srcWidth = img.getWidth(null);
		srcHeight = img.getHeight(null);
		double zoom = Math.min(((double)width-margin.left-margin.right)/srcWidth, (double)(height-margin.top-margin.bottom)/srcHeight);
		dstWidth = (int)((double)srcWidth * zoom);
		dstHeight = (int)((double)srcHeight * zoom);
		imgValue = new BufferedImage(dstWidth, dstHeight, BufferedImage.TYPE_INT_RGB);
		imgValue.getGraphics().drawImage(img, 0, 0, dstWidth, dstHeight, 0, 0, srcWidth, srcHeight, null);
	}


	public boolean isReadOnly() {
		return readOnly || 
			(report != null && report.getState() != report.EDIT_STATE);
	}


	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
		if (editor != null) {
			if (editor instanceof JTextField) {
				((JTextField)editor).setEditable(!readOnly);;
			} else if (editor instanceof JTextArea) {
				((JTextArea)editor).setEditable(!readOnly);
			} else  {
				editor.setEnabled(!readOnly);
			} 
		}
	}


	public Insets getMargin() {
		return margin;
	}


	public void setMargin(Insets margin) {
		this.margin = margin;
	}

	public void setEditorBounds() {
		if (editor != null) 
			editor.setBounds(x + margin.left, y + margin.top, width - margin.left - margin.right, height - margin.top - margin.bottom);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
}
