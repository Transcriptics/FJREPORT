package net.sf.fjreport.samples;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.sf.fjreport.FJReport;
import net.sf.fjreport.control.PrintAction;
import net.sf.fjreport.statusbar.JStatusBar;
import net.sf.fjreport.statusbar.StatusChangeListener;

public class PiggyLetter extends JFrame {

	public PiggyLetter(){
		super("FJReport Sample - Piggy Letter");
		FJReport report = new FJReport();
		System.out.println(PiggyLetter.class.getResource(""));
		report.loadReport(PiggyLetter.class.getResourceAsStream("piggy.xml"));
//		report.loadReport(PiggyLetter.class.getResource("") + "piggy.xml");
//		System.out.println(PiggyLetter.class.getResource("") + "piggy.xml");
		// use two different methods to set image cell. image string could be an url
		report.setValue("pigimg1", PiggyLetter.class.getResource("") + "piggy1.jpg");

		Image img = null;
		try {
			img = Toolkit.getDefaultToolkit().getImage(new URL(PiggyLetter.class.getResource("") + "piggy2.jpg"));
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		MediaTracker imgLoadCheck = new MediaTracker(report);
		imgLoadCheck.addImage(img, 0);
		try {
			imgLoadCheck.waitForID(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		report.setValue("pigimg2", img);
		
		report.setValue("content1", 
			  "Dear Professor:\n"
			+ "     This is a test to see if "
			+ "this works fine. Presummibly "
			+ "it does, at ten o'clock?  We are "
			+ "planning a small, costume dance."
			);
		report.setValue("signature1", "Piggy Dancer");
		report.setValue("date1", "2006-4-30");
		
		report.setValue("content2", 
			  "Dear Miss Dancer:\n"
			+ "      Thank you for the good things you provided. "
			+ "I'm even unwilling to leave. The wonderful music, "
			+ "the romantic dance, the beautiful ladies, all these "
			+ "give me lots of enjoyments. I really hope I could "
			+ "have stayed still longer.\n"
			+ "      Do not hesitate to inform me next time. I believe "
			+ "we will have a good time."
				);
		report.setValue("signature2", "Piggy Braver");
		report.setValue("date2", new Date());
		
		report.setState(FJReport.EDIT_STATE);
		//report.setState(FJReport.READONLY_STATE);
		JScrollPane sc = new JScrollPane(report);
		
		final JStatusBar statusBar = new JStatusBar(new int[]{1,1,0});
		statusBar.setContent(2, "Author: Frank Lewis");
		statusBar.setContent(0, "Editing");
		report.addStatusChangeListener(new StatusChangeListener(){
			public void statusChange(int messageType, String message) {
				statusBar.setContent(messageType, message);
			}});

		report.setValue("chksample", true);
		report.setValue("comboboxsample", "Call my piggy");
		report.firstPage();
		
		JPanel p = report.getEditToolBarPane();
		JButton btnPrint = new JButton(new PrintAction(report));
		btnPrint.setPreferredSize(new Dimension(28, 28));
		p.add(btnPrint);
		add(p, "North");
		add(sc, "Center");
		add(statusBar, "South");
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(700, 600);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	
	public static void main(String[] args) {
		
		java.util.Date olderDate= new java.util.Date();
		 
        new PiggyLetter();
		 
		java.util.Date newerDate= new java.util.Date();
		
		long diffInMillis =    newerDate.getTime() - olderDate.getTime();
				
		System.out.print("Total execution time: " + diffInMillis + "ms");
	}

}
