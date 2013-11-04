package net.sf.fjreport.line;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import net.sf.fjreport.FJReport;

public class Line implements Comparable {

	private int lineType;
	public static final int LINE_NORMAL = 0;
	public static final int LINE_DOT = 1;
	public static final int LINE_DASH = 2;
	public static final int LINE_DASH_DOT = 3;
	public static final int LINE_DOUBLE = 4;
	
	private int borderType = 0;
	public static final int BORDER_NONE = 0;
	public static final int BORDER_UP = 1;
	public static final int BORDER_LEFT = 2;
	public static final int BORDER_DOWN = 3;
	public static final int BORDER_RIGHT = 4;
	
	public static final int lineInterval = 4;
	public int id;
	
	public static final float[][] dashes = new float[][]{
		{1},
		{2},
		{8},
		{8, 2},
		{8, 2, 2},
		{},
		{}
	};
	
	
	public List cells = new ArrayList();
	
	private int lineWidth;
	private boolean inVisible;
	
	private int orientation;
	public static final int HORIZONTAL_ORIENTATION = 0;
	public static final int VERTICAL_ORIENTATION = 1;
	
	private Point startPoint;
	private int length;
	
	private static final int selectionSize = 5;

	FJReport report;
	
	public boolean isHorizontal(){
		return (orientation == HORIZONTAL_ORIENTATION);
	}
	
	public Line(FJReport report){
		super();
		this.report = report;
		if (report != null) {
			id = report.getLineID();
		}
	}
	
	public void render(int w, int h, Graphics2D g2){
		if (isInVisible() && report != null && (report.getState() == report.EDIT_STATE || report.getState() == report.READONLY_STATE)) return;
		Stroke stroke = g2.getStroke();
		if (isInVisible())
			g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, dashes[0], 0));
		else if (lineType != LINE_NORMAL && lineType != LINE_DOUBLE)
			g2.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, dashes[lineType], 0));
		else
			g2.setStroke(new BasicStroke(lineWidth));
		int endx = startPoint.x + (1 - orientation) * length;
		int endy = startPoint.y + orientation*length;
		g2.drawLine(startPoint.x, startPoint.y, endx, endy);
		if (lineType == LINE_DOUBLE && !isInVisible()) {
			g2.setStroke(new BasicStroke(1));
			if (orientation == Line.HORIZONTAL_ORIENTATION)
				g2.drawLine(startPoint.x, startPoint.y + lineWidth/2 + 2, startPoint.x+length, startPoint.y + lineWidth/2 + 2);
			else
				g2.drawLine(startPoint.x + lineWidth/2 + 2, startPoint.y, startPoint.x + lineWidth/2 + 2, startPoint.y + length);
		}
		g2.setStroke(stroke);
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		if (length % lineInterval != 0)
		length = length + lineInterval - length % lineInterval;
		this.length = length;
	}

	public int getLineType() {
		return lineType;
	}

	public void setLineType(int lineType) {
		this.lineType = lineType;
	}

	public int getLineWidth() {
		return lineWidth;
	}

	public void setLineWidth(int lineWidth) {
		this.lineWidth = lineWidth;
	}

	public int getOrientation() {
		return orientation;
	}

	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}

	public Point getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(Point startPoint) {
		if (startPoint.x % lineInterval != 0)
			startPoint.x = startPoint.x + lineInterval - startPoint.x % lineInterval;
		if (startPoint.y % lineInterval != 0)
			startPoint.y = startPoint.y + lineInterval - startPoint.y % lineInterval;
		this.startPoint = startPoint;
	}

	public Point getEndPoint() {
		// TODO Auto-generated method stub
		return new Point(getEndX(), getEndY());
	}

	public int getX() {
		return startPoint.x;
	}
	
	public int getY() {
		return startPoint.y;
	}
	
	public int getEndX() {
		// TODO Auto-generated method stub
		return startPoint.x + (1 - orientation) * length;
	}

	public int getEndY() {
		return startPoint.y + orientation * length;
	}

	public boolean isInside(Point p) {
		// TODO Auto-generated method stub
		if (orientation == HORIZONTAL_ORIENTATION)
			return (Math.abs(p.y - startPoint.y) < 7 && p.x > startPoint.x && p.x < startPoint.x + length);
		else
			return (Math.abs(p.x - startPoint.x) < 7 && p.y > startPoint.y && p.y < startPoint.y + length);
	}

	public int compareTo(Object arg0) {
		Point p = ((Line)arg0).startPoint;
		if (orientation == HORIZONTAL_ORIENTATION) return startPoint.y - p.y;
		else return startPoint.x - p.x;
	}

	public boolean isInVisible() {
		return inVisible;
	}

	public void setInVisible(boolean inVisible) {
		this.inVisible = inVisible;
	}

	public int getBorderType() {
		return borderType;
	}

	public void setBorderType(int borderType) {
		this.borderType = borderType;
	}
	public boolean isBorder(){
		return (borderType > 0);
	}

}
