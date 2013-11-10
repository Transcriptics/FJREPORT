package net.sf.fjreport;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;


import net.sf.fjreport.control.AboutDialogAction;
import net.sf.fjreport.statusbar.JStatusBar;
import net.sf.fjreport.statusbar.StatusChangeListener;
import net.sf.fjreport.util.CommonUtil;
import net.sf.fjreport.util.StringResource;


/**
 * The report template designer.
 * 
 * Copyright (C) since <2006>  <Frank Lewis>
 */
public class FJReportEditor extends JPanel{

	public static JFrame mainFrame;

	private FJReport report = new FJReport();
	private JStatusBar statusBar = new JStatusBar(new int[]{1, 1});
	public FJReportEditor(){
		super(new BorderLayout());
		report.createUI(statusBar, this);
	}
	
	public static void main(String[] args) {
		mainFrame = new JFrame(StringResource.getString("editor title"));
		CommonUtil.mainFrame = mainFrame;
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setExtendedState(JFrame.MAXIMIZED_VERT);
		mainFrame.setSize(720, 700);
		mainFrame.setLocationRelativeTo(null);
		mainFrame.add(new FJReportEditor());
		mainFrame.setVisible(true);
	}

}
