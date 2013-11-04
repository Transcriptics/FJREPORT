package net.sf.fjreport.control;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import net.sf.fjreport.FJReport;
import net.sf.fjreport.Printer;
import net.sf.fjreport.util.StringResource;

public class PrintSetupAction extends AbstractAction{
	private Printer printer;
	public PrintSetupAction(Printer printer){
		super("", new ImageIcon(FJReport.class.getResource("resources/pagesetup.gif")));
		this.printer = printer;
		putValue(SHORT_DESCRIPTION, StringResource.getString("btnPageHint"));
	}
	public void actionPerformed(ActionEvent arg0) {
		printer.printSetup();
	}
}