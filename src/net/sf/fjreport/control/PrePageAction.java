package net.sf.fjreport.control;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import net.sf.fjreport.FJReport;
import net.sf.fjreport.MultiPage;
import net.sf.fjreport.util.StringResource;

public class PrePageAction extends AbstractAction{
	private MultiPage book;
	public PrePageAction(MultiPage book){
		super("", new ImageIcon(FJReport.class.getResource("resources/prepage.gif")));
		this.book = book;
		putValue(SHORT_DESCRIPTION, StringResource.getString("prePageHint"));
	}
	public void actionPerformed(ActionEvent arg0) {
		book.prePage();
	}
}
