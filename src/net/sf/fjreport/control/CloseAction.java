package net.sf.fjreport.control;

import java.awt.event.ActionEvent;
import java.io.Closeable;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import net.sf.fjreport.FJReport;
import net.sf.fjreport.util.StringResource;


public class CloseAction extends AbstractAction{

	private Closeable c;
	public CloseAction(Closeable c){
		super("", new ImageIcon(FJReport.class.getResource("resources/close.gif")));
		this.c = c;
		putValue(SHORT_DESCRIPTION, StringResource.getString("btnCloseHint"));
	}
	public void actionPerformed(ActionEvent arg0) {
		try {
			c.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
