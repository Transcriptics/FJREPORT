package net.sf.fjreport.control;

import java.awt.Event;
import java.awt.event.MouseEvent;
import java.util.Iterator;

import net.sf.fjreport.FJReport;
import net.sf.fjreport.cell.Cell;

public class EditMouseControl extends BaseControl {

	private FJReport report;
	
	public EditMouseControl(FJReport report) {
		this.report = report;
	}

	public void mouseClicked(MouseEvent arg0) {
		if (arg0.getButton() == MouseEvent.BUTTON1) {
			Iterator it = report.getCurrentPage().cells.iterator();
			Cell c;
			while(it.hasNext()) {
				c = (Cell) it.next();
				if (c.contains(arg0.getX(), arg0.getY())) {
					report.setCurrentCell(c);
					report.updateUI();
					return;
				}
			}
			report.setCurrentCell(null);
			report.updateUI();
		}	
	}
	
}
