package net.sf.fjreport.control;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import net.sf.fjreport.FJReport;
import net.sf.fjreport.util.CommonUtil;
import net.sf.fjreport.util.StringResource;

public class AboutDialogAction extends AbstractAction{

	private static final long serialVersionUID = 1L;

	public AboutDialogAction(){
		super("", new ImageIcon(FJReport.class.getResource("resources/about.gif")));
//		putValue(SHORT_DESCRIPTION, StringResource.getString("newPageHint"));
	}
	
	public void actionPerformed(ActionEvent arg0) {
		String msg = "FJReport version 0.4\n" +
			"author: Frank Lewis\n" +
			StringResource.getString("rights");
		JOptionPane.showMessageDialog(CommonUtil.mainFrame, msg);
	}

}
