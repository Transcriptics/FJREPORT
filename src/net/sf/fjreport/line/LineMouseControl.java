package net.sf.fjreport.line;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Collections;

import net.sf.fjreport.FJReport;
import net.sf.fjreport.control.BaseControl;

public class LineMouseControl extends BaseControl{

	private FJReport report;
	private Line line;
	private static final int selectionSize = 6;
	
	private int state;
	private static final int NONE_STATE = 1;
	private static final int DRAWING_STATE = 1;
	private static final int SELECT_STATE = 4;
	private static final int SELECT_STARTPOINT_STATE = 5;
	private static final int SELECT_ENDPOINT_STATE = 6;
	private static final int SELECT_OTHER_STATE = 7;
	
	private static LineMouseControl instance;
	private int mousePickX;
	private int mousePickY;
	private int pickX;
	private int pickY;
	
	private static int defaultLineType = 0;
	private static int defaultLineWidth = 1;
	private static boolean defaultInVisible = false;
	
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		if (arg0.getButton() == MouseEvent.BUTTON1) {
			if (arg0.getClickCount() == 2) {
				if (line != null) {
					if (LineEditor.edit(report, line)) {
						defaultLineType = line.getLineType();
						defaultLineWidth = line.getLineWidth();
						defaultInVisible = line.isInVisible();
						report.updateUI();
					}
				} else {
					line = new Line(report);
					line.setStartPoint(new Point(report.getImageableBorderX(), arg0.getY()));
					line.setOrientation(Line.HORIZONTAL_ORIENTATION);
					line.setLineType(defaultLineType);
					line.setLineWidth(defaultLineWidth);
					line.setInVisible(defaultInVisible);
					line.setLength(report.getImageableBorderWidth());
					report.currentPage.hLines.add(line);
					Collections.sort(report.currentPage.hLines);
					report.updateUI();
					state = SELECT_STATE;
				}
			} else if (state <= SELECT_STATE) {
				line = pickLine(arg0.getPoint());
				if (line != null) state = SELECT_STATE;
				else state = NONE_STATE;
				report.updateUI();
			}
		} else if (arg0.getButton() == MouseEvent.BUTTON3) {
			if (arg0.getClickCount() == 2 && line == null) {
				line = new Line(report);
				line.setStartPoint(new Point(arg0.getX(), report.getImageableBorderY()));
				line.setOrientation(Line.VERTICAL_ORIENTATION);
				line.setLineType(defaultLineType);
				line.setLineWidth(defaultLineWidth);
				line.setInVisible(defaultInVisible);
				line.setLength(report.getImageableBorderHeight());
				report.currentPage.vLines.add(line);
				Collections.sort(report.currentPage.vLines);
				report.updateUI();
				state = SELECT_STATE;
				return;
			}
			if (state >= SELECT_STATE) {
				if (line.isBorder()) return;
				if (line.getOrientation() == Line.HORIZONTAL_ORIENTATION)
					report.getCurrentPage().hLines.remove(line);
				else
					report.getCurrentPage().vLines.remove(line);
				line = null;
				state = NONE_STATE;
				report.updateUI();
			}
		}
	}

	private Line pickLine(Point point) {
		// TODO Auto-generated method stub
		// pick the shortest line, border can be picked but cant be moved 
		Line line;
		int foundInd, foundLength;
		foundInd = -1;
		foundLength = 10000;
		for(int i=0; i<report.currentPage.vLines.size(); i++) {
			line = (Line) report.currentPage.vLines.get(i);
			if (line.getX() > point.x + 7) break;
			if (line.isInside(point)) {
				if (line.getLength() < foundLength) {
					foundLength = line.getLength();
					foundInd = i;
				}
			}
		}
		for(int i=0; i<report.currentPage.hLines.size(); i++) {
			line = (Line) report.currentPage.hLines.get(i);
			if (line.getY() > point.y + 7) break;
			if (line.isInside(point)) {
				if (line.getLength() < foundLength) {
					foundLength = line.getLength();
					foundInd = i + report.currentPage.vLines.size();
				}
			}
		}
		if (foundInd >= report.currentPage.vLines.size()) 
			return (Line) report.currentPage.hLines.get(foundInd - report.currentPage.vLines.size());
		else if (foundInd >= 0) return (Line) report.currentPage.vLines.get(foundInd);
		else return null;
	}
	
	public void mousePressed(MouseEvent arg0) {
		if (arg0.getButton() == MouseEvent.BUTTON1 && state == SELECT_STATE && line != null) {
			if (Math.abs(arg0.getX() - line.getEndX()) + Math.abs(arg0.getY() - line.getEndY()) <= selectionSize)
				state = SELECT_ENDPOINT_STATE;
			else if (Math.abs(arg0.getX() - line.getX()) + Math.abs(arg0.getY() - line.getY()) <= selectionSize)
				state = SELECT_STARTPOINT_STATE;
			else if (line.isInside(arg0.getPoint())) {
				state = SELECT_OTHER_STATE;
				mousePickX = arg0.getX();
				mousePickY = arg0.getY();
				pickX = line.getX();
				pickY = line.getY();
			}
			return;
		} else if (arg0.getButton()==MouseEvent.BUTTON3 && state <= DRAWING_STATE) {
			state = DRAWING_STATE;
			line = new Line(report);
			line.setStartPoint(new Point(arg0.getX(), arg0.getY()));
			line.setOrientation(Line.HORIZONTAL_ORIENTATION);
			line.setLineType(defaultLineType);
			line.setLineWidth(defaultLineWidth);
			line.setInVisible(defaultInVisible);
			line.setLength(3);
			report.updateUI();
		}
	}
	
	public void mouseReleased(MouseEvent arg0) {
		if (state > SELECT_STATE) {
			state = SELECT_STATE;
			return;
		}			
		if (arg0.getButton() != MouseEvent.BUTTON3 || line == null || state != DRAWING_STATE) return;
		if (line.getLength()<8) {
			line = null;
			report.updateUI();
			state = NONE_STATE;
			return;
		}
		if (line.getOrientation() == Line.HORIZONTAL_ORIENTATION) {
			report.getCurrentPage().hLines.add(line);
			Collections.sort(report.getCurrentPage().hLines);
		} else {
			report.getCurrentPage().vLines.add(line);
			Collections.sort(report.getCurrentPage().vLines);
		}
		line = null;
		report.updateUI();
	}
	
	public void mouseDragged(MouseEvent arg0) {
		if (line == null || line.isBorder()) return;
		if (state == DRAWING_STATE || state == SELECT_ENDPOINT_STATE) {
			Point startPoint = line.getStartPoint();
			if (arg0.getX() - startPoint.x > arg0.getY() - startPoint.y) {
				line.setOrientation(Line.HORIZONTAL_ORIENTATION);
				line.setLength(Math.max(arg0.getX() - startPoint.x, 1));
			} else {
				line.setOrientation(Line.VERTICAL_ORIENTATION);
				line.setLength(Math.max(arg0.getY() - startPoint.y, 1));
			}
			report.updateUI();
		} else if (state == SELECT_STARTPOINT_STATE) {
			line.setStartPoint(new Point(arg0.getPoint().x, arg0.getPoint().y));
			report.updateUI();
		} else if (state == SELECT_OTHER_STATE) {
			line.setStartPoint(new Point(pickX + arg0.getX() - mousePickX, pickY + arg0.getY() - mousePickY));
			report.updateUI();
		}
	}

	public LineMouseControl(FJReport report) {
		super();
		this.report = report;
	}

	public void render(int w, int h, Graphics2D g2d) {
		// TODO Auto-generated method stub
		if (line == null) return;
		Color c = g2d.getColor();
		g2d.setColor(Color.RED);
		if (state == DRAWING_STATE) line.render(w, h, g2d);
		Point startPoint = line.getStartPoint();
		g2d.drawLine(startPoint.x, startPoint.y, line.getEndX(), line.getEndY());
		if (line.isHorizontal()) {
			g2d.drawLine(startPoint.x, startPoint.y - selectionSize, startPoint.x, startPoint.y + selectionSize);
			g2d.drawLine(line.getEndX(), startPoint.y - selectionSize, line.getEndX(), startPoint.y + selectionSize);
		} else {
			g2d.drawLine(startPoint.x - selectionSize, startPoint.y, startPoint.x + selectionSize, startPoint.y);
			g2d.drawLine(startPoint.x - selectionSize, line.getEndY(), startPoint.x + selectionSize, line.getEndY());
		}
		g2d.setColor(c);
	}

}
