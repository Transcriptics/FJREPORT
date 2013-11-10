package net.sf.fjreport.line;


public class LineProduct {
	private int borderType = 0;

	public int getBorderType() {
		return borderType;
	}

	public void setBorderType(int borderType) {
		this.borderType = borderType;
	}

	public boolean isBorder() {
		return (borderType > 0);
	}
}