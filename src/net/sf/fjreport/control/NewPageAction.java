package net.sf.fjreport.control;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import net.sf.fjreport.FJReport;
import net.sf.fjreport.MultiPage;
import net.sf.fjreport.util.StringResource;

public class NewPageAction extends AbstractAction{
	private FJReport report;
	public NewPageAction(FJReport report){
		super("", new ImageIcon(FJReport.class.getResource("resources/new.gif")));
		this.report = report;
		putValue(SHORT_DESCRIPTION, StringResource.getString("newPageHint"));
	}
	public void actionPerformed(ActionEvent arg0) {
		report.insertPage();
	}
}