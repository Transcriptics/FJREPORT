package net.sf.fjreport.control;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import net.sf.fjreport.FJReport;
import net.sf.fjreport.util.StringResource;

public class RemovePageAction extends AbstractAction{
	FJReport report;
	public RemovePageAction(FJReport report){
		super("", new ImageIcon(FJReport.class.getResource("resources/removepage.gif")));
		putValue(SHORT_DESCRIPTION, StringResource.getString("removePageHint"));
		this.report = report;
	}
	public void actionPerformed(ActionEvent arg0) {
		if (JOptionPane.showConfirmDialog(JOptionPane.getFrameForComponent(report),
				StringResource.getString("confirmDeletePageMsg"), 
				StringResource.getString("confirmDeletePageDlgTitle"),
				JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
			report.removePage(report.currentPage);
	}
}