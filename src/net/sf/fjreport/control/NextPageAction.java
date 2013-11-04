package net.sf.fjreport.control;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import net.sf.fjreport.FJReport;
import net.sf.fjreport.MultiPage;
import net.sf.fjreport.util.StringResource;

public class NextPageAction extends AbstractAction{
	private MultiPage book;
	public NextPageAction(MultiPage book){
		super("", new ImageIcon(FJReport.class.getResource("resources/nextpage.gif")));
		this.book = book;
		putValue(SHORT_DESCRIPTION, StringResource.getString("nextPageHint"));
	}
	public void actionPerformed(ActionEvent arg0) {
		book.nextPage();
	}
}