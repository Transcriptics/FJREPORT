package net.sf.fjreport.samples;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import net.sf.fjreport.tableprint.PageTablePrint;
import net.sf.fjreport.util.GridBagUtil;
import net.sf.fjreport.util.CommonUtil;

public class TablePrintSample extends JFrame{

	private JTable tb;
	private PageTablePrint pageTable;
	public static String alignments[] = { "Left", "Center", "Right" };
	private TablePrintSample instance;
	private JCheckBox ckPrintFirstHeader = new JCheckBox("Print header on first page");

	JTextArea txtTitle = new JTextArea(3, 8);
	JComboBox cbTitleFont = new JComboBox(CommonUtil.fontNames);
	JComboBox cbTitleSize = new JComboBox(CommonUtil.fontSizes);
	JComboBox cbTitleAlign = new JComboBox(alignments);

	JTextArea txtHeader = new JTextArea(3, 8);
	JComboBox cbHeaderFont = new JComboBox(CommonUtil.fontNames);
	JComboBox cbHeaderSize = new JComboBox(CommonUtil.fontSizes);
	JComboBox cbHeaderAlign = new JComboBox(alignments);

	JTextArea txtFooter = new JTextArea(3, 8);
	JComboBox cbFooterFont = new JComboBox(CommonUtil.fontNames);
	JComboBox cbFooterSize = new JComboBox(CommonUtil.fontSizes);
	JComboBox cbFooterAlign = new JComboBox(alignments);
	
	public TablePrintSample() {
		super("Paged Table Print Sample");
		instance = this;
		createUI();
		setSize(660, 560);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	private void createUI() {
		tb = new JTable(getTableModel());
		JScrollPane sc = new JScrollPane(tb);
	
		pageTable = new PageTablePrint();
		pageTable.packTable(this, "Paged Table Print Sample", tb.getModel(), null);

		JPanel bottomPane = new JPanel(new GridLayout(1, 3));
		int preferredW = 0;
		bottomPane.add(getTitleChooserPane());
		bottomPane.add(getHeaderChooserPane());
		bottomPane.add(getFooterChooserPane());
		
		
		JToolBar toolbar = new JToolBar();
		JButton btnPrint = new JButton("Print");
		btnPrint.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				configReports();
				try {
					tb.print();
				} catch (PrinterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//				pageTable.print();
			}});
		JButton btnPreview = new JButton("Preview");
		btnPreview.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				configReports();
				pageTable.preview();
			}});
		JButton btnSetup = new JButton("Page Setup");
		btnSetup.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				pageTable.printSetup();
			}});
		toolbar.add(btnPrint);;
		toolbar.add(btnPreview);
		toolbar.add(ckPrintFirstHeader);
		add(toolbar, "North");
		add(sc, "Center");
		add(bottomPane, "South");
		setSize((int) (bottomPane.getPreferredSize().getWidth() + 10), 500);
	}

	protected void configReports() {
		pageTable.setTitle(txtTitle.getText());
		pageTable.setTitleFont(new Font((String) cbTitleFont
				.getSelectedItem(), 0, Integer
				.parseInt((String) cbTitleSize.getSelectedItem())));
		pageTable.setTitleAlignment(cbTitleAlign.getSelectedIndex());
		
		pageTable.setHeader(txtHeader.getText());
		pageTable.setHeaderFont(new Font((String) cbHeaderFont
				.getSelectedItem(), 0, Integer
				.parseInt((String) cbHeaderSize.getSelectedItem())));
		pageTable.setHeaderAlignment(cbHeaderAlign.getSelectedIndex());
		

		pageTable.setFooter(txtFooter.getText());
		pageTable.setFooterFont(new Font((String) cbFooterFont
				.getSelectedItem(), 0, Integer
				.parseInt((String) cbFooterSize.getSelectedItem())));
		pageTable.setFooterAlignment(cbFooterAlign.getSelectedIndex());
		
		pageTable.setPrintHeaderOnFirstPage(ckPrintFirstHeader.isSelected());
	}

	private JPanel getTitleChooserPane() {
		JPanel pane = new JPanel(new GridBagLayout());
		txtTitle.setText("Project Participants");
		JPanel p = new JPanel(new BorderLayout());
		p.add(cbTitleFont, "North");
		p.add(cbTitleSize,"South");
		cbTitleSize.setSelectedIndex(5);
		cbTitleAlign.setSelectedIndex(1);
		JPanel p2 = new JPanel(new BorderLayout());
		p2.add(new JLabel("Align: "), "West");
		p2.add(cbTitleAlign, "Center");
		GridBagUtil.addf2(pane, new JScrollPane(txtTitle), 0, 0, 1, 1, GridBagUtil.BOTH);
		GridBagUtil.addf2(pane, p, 0, 1, 1, 0, GridBagUtil.HORIZONTAL);
		GridBagUtil.addf2(pane, p2, 0, 2, 1, 0, GridBagUtil.HORIZONTAL);
		pane.setBorder(new TitledBorder("Choose titile style"));
		return pane;
	}
	private JPanel getHeaderChooserPane() {
		JPanel pane = new JPanel(new GridBagLayout());
		txtHeader.setText("page %p of %allpage");
		JPanel p = new JPanel(new BorderLayout());
		p.add(cbHeaderFont, "North");
		p.add(cbHeaderSize,"South");
		cbHeaderSize.setSelectedIndex(4);
		JPanel p2 = new JPanel(new BorderLayout());
		p2.add(new JLabel("Align: "), "West");
		p2.add(cbHeaderAlign, "Center");
		cbHeaderAlign.setSelectedIndex(2);
		GridBagUtil.addf2(pane, new JScrollPane(txtHeader), 0, 0, 1, 1, GridBagUtil.BOTH);
		GridBagUtil.addf2(pane, p, 0, 1, 1, 0, GridBagUtil.HORIZONTAL);
		GridBagUtil.addf2(pane, p2, 0, 2, 1, 0, GridBagUtil.HORIZONTAL);
		pane.setBorder(new TitledBorder("Choose Header style"));
		return pane;
	}
	private JPanel getFooterChooserPane() {
		JPanel pane = new JPanel(new GridBagLayout());
		txtFooter.setText("report maker: Frank Lewis \n%date");
		JPanel p = new JPanel(new BorderLayout());
		p.add(cbFooterFont, "North");
		p.add(cbFooterSize,"South");
		cbFooterSize.setSelectedIndex(4);
		cbFooterAlign.setSelectedIndex(0);
		JPanel p2 = new JPanel(new BorderLayout());
		p2.add(new JLabel("Align: "), "West");
		p2.add(cbFooterAlign, "Center");
		GridBagUtil.addf2(pane, new JScrollPane(txtFooter), 0, 0, 1, 1, GridBagUtil.BOTH);
		GridBagUtil.addf2(pane, p, 0, 1, 1, 0, GridBagUtil.HORIZONTAL);
		GridBagUtil.addf2(pane, p2, 0, 2, 1, 0, GridBagUtil.HORIZONTAL);
		pane.setBorder(new TitledBorder("Choose footer style"));
		return pane;
	}

	private DefaultTableModel rs;
	
	private DefaultTableModel getTableModel() {
	    final Object[] columnNames = new String[]{"Name", "Addr.", "Mobile", "Duty"};
	    DefaultTableModel rs = new DefaultTableModel(columnNames, 200);
	    rs.setValueAt("Tom", 0, 0);
	    rs.setValueAt("Alabama ", 0, 1);
	    rs.setValueAt("12345678", 0, 2);
	    rs.setValueAt("Software Programmer", 0, 3);
	    rs.setValueAt("Jason", 1, 0);
	    rs.setValueAt("Maryland", 1, 1);
	    rs.setValueAt("87654321", 1, 2);
	    rs.setValueAt("Hardware Engineer", 1, 3);
	    rs.setValueAt("Martin", 2, 0);
	    rs.setValueAt("Nevada", 2, 1);
	    rs.setValueAt("12324678", 2, 2);
	    rs.setValueAt("Software Programmer", 2, 3);
	    rs.setValueAt("Jacky", 3, 0);
	    rs.setValueAt("Pennsylvania", 3, 1);
	    rs.setValueAt("12345678", 3, 2);
	    rs.setValueAt("Software Testing", 3, 3);
	    rs.setValueAt("Frank Lewis", 4, 0);
	    rs.setValueAt("Virginia", 4, 1);
	    rs.setValueAt("12345678", 4, 2);
	    rs.setValueAt("System Architecture", 4, 3);
	    for(int i=5; i<200; i++) { 
	    	rs.setValueAt("Coder"+String.valueOf(i), i, 0);
	    	rs.setValueAt("Address"+String.valueOf(i), i, 1);
	    	rs.setValueAt(Math.random(), i, 2);
	    	rs.setValueAt("Ba la ba la ba la ba la", i, 3);
	    }
	    return rs;
	}
	
	
	
	public static void main(String[] args) {
		new TablePrintSample();
	}

}
