package net.sf.fjreport;

import java.awt.Point;

public class Zoom {
	private float scale;
	private Point translatePoint;
	
	public Zoom(float scale, int x, int y){
		this.scale = scale;
		translatePoint = new Point(x, y);
	}
	
	public float getScale() {
		return scale;
	}
	public void setScale(float scale) {
		this.scale = scale;
	}
	public Point getTranslatePoint() {
		return translatePoint;
	}
	public void setTranslatePoint(Point translatePoint) {
		this.translatePoint = translatePoint;
	}
	public void getZoomPoint(Point p) {
		p.x = (int)(p.x * scale) + translatePoint.x;
		p.y = (int)(p.y * scale) + translatePoint.y;
	}
	public void getOriginPoint(Point p) {
		p.x = (int) ((p.x - translatePoint.x) / scale);
		p.y = (int) ((p.y - translatePoint.y) / scale);
	}
	public int getZoomX(int x) {
		return (int)(x * scale) + translatePoint.x;
	}
	public int getZoomY(int y) {
		return (int)(y * scale) + translatePoint.y;
	}
	public int getOriginX(int x) {
		return (int) ((x - translatePoint.x) /scale);
	}
	public int getOriginY(int y) {
		return (int) ((y - translatePoint.y) /scale);
	}
}
