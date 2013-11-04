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

	private static JFrame mainFrame;

	private FJReport report = new FJReport();
	private JStatusBar statusBar = new JStatusBar(new int[]{1, 1});
	public FJReportEditor(){
		super(new BorderLayout());
		createUI();
	}
	
	private void createUI(){
		JScrollPane sc = new JScrollPane(report);
		report.addStatusChangeListener(new StatusChangeListener(){
			public void statusChange(int messageType, String message) {
				statusBar.setContent(messageType, message);
			}});
		JToolBar toolbar = new JToolBar(JToolBar.VERTICAL);
		JButton btnLine = new JButton(StringResource.getString("btnLineCaption"));
		JButton btnClear = new JButton(StringResource.getString("btnClearCaption"));
		JButton btnCell = new JButton(StringResource.getString("btnCellCaption"));
		JButton btnEdit = new JButton(StringResource.getString("btnEditCaption"));
		JButton btnPrintSetup = new JButton(StringResource.getString("btnPageCaption"));
		final JButton btnPrint = new JButton(StringResource.getString("btnPrintCaption"));
		JButton btnSave = new JButton(StringResource.getString("btnSaveCaption"));
		JButton btnLoad = new JButton(StringResource.getString("btnLoadCaption"));
		
		btnLine.setToolTipText(StringResource.getString("btnLineHint"));
		btnClear.setToolTipText(StringResource.getString("btnClearHint"));
		btnCell.setToolTipText(StringResource.getString("btnCellHint"));
		btnEdit.setToolTipText(StringResource.getString("btnEditHint"));
		btnPrintSetup.setToolTipText(StringResource.getString("btnPageHint"));
		btnPrint.setToolTipText(StringResource.getString("btnPrintHint"));
		btnSave.setToolTipText(StringResource.getString("btnSaveHint"));
		btnLoad.setToolTipText(StringResource.getString("btnLoadHint"));
		
		btnLine.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				report.setState(FJReport.DRAWLINE_STATE);
				btnPrint.setEnabled(false);
				statusBar.setContent(0, StringResource.getString("drawLineStatus"));
			}});
		btnCell.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				report.setState(FJReport.CELL_STATE);
				btnPrint.setEnabled(false);
				statusBar.setContent(0, StringResource.getString("desginCellStatus"));
			}});
		btnEdit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				report.setState(FJReport.EDIT_STATE);
				btnPrint.setEnabled(true);
				statusBar.setContent(0, StringResource.getString("editStatus"));
			}});
		btnClear.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				if (JOptionPane.showConfirmDialog(JOptionPane.getFrameForComponent(report),
						StringResource.getString("confirmClearPageMsg"), 
						StringResource.getString("confirmClearPageDlgTitle"),
						JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
					return;
				report.clear();
				statusBar.setContent(0, StringResource.getString("clearStatus"));
			}});
		btnPrintSetup.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				report.printSetup();
			}});
		btnPrint.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				report.print();
			}});
		btnSave.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				report.saveDialog();
			}});
		btnLoad.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				File file = report.loadDialog();
				if (file != null)
				mainFrame.setTitle(StringResource.getString("editor title") + " - " + file.getName());
			}});
		FlowLayout g = new FlowLayout();
		g.setVgap(1);
		toolbar.setLayout(g);
		toolbar.setPreferredSize(new Dimension(82, 50));
		
		toolbar.add(btnLine);
		toolbar.add(btnCell);
		toolbar.add(btnEdit);
		toolbar.add(btnClear);
		toolbar.add(btnPrintSetup);
		toolbar.add(btnPrint);
		toolbar.add(btnSave);
		toolbar.add(btnLoad);
		
		Dimension btnSize = new Dimension(80, 34);
		btnLine.setPreferredSize(btnSize);
		btnClear.setPreferredSize(btnSize);
		btnCell.setPreferredSize(btnSize);
		btnEdit.setPreferredSize(btnSize);
		btnPrintSetup.setPreferredSize(btnSize);
		btnPrint.setPreferredSize(btnSize);		
		btnSave.setPreferredSize(btnSize);		
		btnLoad.setPreferredSize(btnSize);		

		Insets insets = new Insets(0, 0, 0, 0);
		btnLine.setMargin(insets);
		btnClear.setMargin(insets);
		btnEdit.setMargin(insets);
		btnCell.setMargin(insets);
		btnPrintSetup.setMargin(insets);
		btnPrint.setMargin(insets);		
		btnSave.setMargin(insets);		
		btnLoad.setMargin(insets);		

		JToolBar upperToolbar = new JToolBar();
		JPanel toolbarPane = report.getEditToolBarPane();
		JButton btnAbout = new JButton(new AboutDialogAction());
		btnAbout.setPreferredSize(new Dimension(28, 28));
		toolbarPane.add(btnAbout);
		upperToolbar.add(toolbarPane);
		add(upperToolbar, "North");

		sc.setFocusable(true);
		sc.grabFocus();
		report.setFocusable(true);
		report.grabFocus();
		
		report.addKeyListener(new KeyListener(){

			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub
				System.out.println(arg0.getKeyChar());
			}

			public void keyPressed(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}});
		sc.addKeyListener(new KeyListener(){

			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub
				System.out.println(arg0.getKeyChar());
			}

			public void keyPressed(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}});
		
		addKeyListener(new KeyListener(){

			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub
				System.out.println(arg0.getKeyChar());
			}

			public void keyPressed(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}});

		
		add(toolbar, "East");
		add(sc, "Center");		
		add(statusBar, "South");
		report.firstPage();
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
