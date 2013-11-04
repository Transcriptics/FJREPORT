package net.sf.fjreport.cell;

import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.JComponent;

public class Cell2 {
	private JComponent editor;
	private boolean readOnly;
	private Cell2 instance = this;
	private static Font defaultFont = new Font("Dialog", 0, 24);
	
	// id = topLine.id * 512^3 + leftLine.id * 512^2 + bottomLine.id * 512 + 
	private long id;
	
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
	public static final int TYPE_CUSTOM = 8;
	
	private int hAlignment;
	private int vAlignment;
	public static final int ALIGN_LEFT = 0;
	public static final int ALIGN_CENTER = 1;
	public static final int ALIGN_RIGHT = 2;
	public static final int ALIGN_TOP = 0;
	public static final int ALIGN_BOTTOM = 2;
	private Font font = defaultFont;

	private boolean selected;
	
	private String comboxOptions;
	private static final Insets DEFAULT_MARGIN = new Insets(0, 0, 0, 0);
	private Insets margin = DEFAULT_MARGIN;
	
	private static final int innerMargin = 3;
	private static final Insets insets = new Insets(innerMargin, innerMargin, innerMargin, innerMargin);
	public String getComboxOptions() {
		return comboxOptions;
	}
	public void setComboxOptions(String comboxOptions) {
		this.comboxOptions = comboxOptions;
	}
	public JComponent getEditor() {
		return editor;
	}
	public void setEditor(JComponent editor) {
		this.editor = editor;
	}
	public Font getFont() {
		return font;
	}
	public void setFont(Font font) {
		this.font = font;
	}
	public int getHAlignment() {
		return hAlignment;
	}
	public void setHAlignment(int alignment) {
		hAlignment = alignment;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Image getImgValue() {
		return imgValue;
	}
	public void setImgValue(Image imgValue) {
		this.imgValue = imgValue;
	}
	public Cell2 getInstance() {
		return instance;
	}
	public void setInstance(Cell2 instance) {
		this.instance = instance;
	}
	public Insets getMargin() {
		return margin;
	}
	public void setMargin(Insets margin) {
		this.margin = margin;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isReadOnly() {
		return readOnly;
	}
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getVAlignment() {
		return vAlignment;
	}
	public void setVAlignment(int alignment) {
		vAlignment = alignment;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}

}
