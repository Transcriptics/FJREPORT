package net.sf.fjreport.control;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import net.sf.fjreport.FJReport;
import net.sf.fjreport.MultiPage;
import net.sf.fjreport.util.StringResource;

public class LastPageAction extends AbstractAction{
	private MultiPage book;
	public LastPageAction(MultiPage book){
		super("", new ImageIcon(FJReport.class.getResource("resources/lastpage.gif")));
		this.book = book;
		putValue(SHORT_DESCRIPTION, StringResource.getString("lastPageHint"));
	}
	public void actionPerformed(ActionEvent arg0) {
		book.lastPage();
	}
}