package net.sf.fjreport.tableprint;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import net.sf.fjreport.util.CheckInput;
import net.sf.fjreport.util.ModalDialog;
import net.sf.fjreport.util.StringResource;

public class ColumnVisible extends JPanel implements CheckInput{
	
	private PageTableModel pagedTm;
	private TableModel tm;
	
	public ColumnVisible(PageTableModel tm){
		super(new BorderLayout());
		this.pagedTm = tm;
		createTableModel();
		add(new JScrollPane(new JTable(this.tm)), "Center");
		setPreferredSize(new Dimension(300, 300));
	}
	
	private void createTableModel() {
		String[] columnTitles = new String[]{
				StringResource.getString("columnVisibleTbTitle1"),
				StringResource.getString("columnVisibleTbTitle2")
		};
		tm = new DefaultTableModel(columnTitles, pagedTm.tm.getColumnCount()) {
			public Class<?> getColumnClass(int arg0) {
				if (arg0==0) return Boolean.class;
				else return String.class;
			}
			public boolean isCellEditable(int arg0, int arg1) {
				return (arg1==0);
			}
		};
		for(int i = 0; i < pagedTm.tm.getColumnCount(); i++) {
			tm.setValueAt(pagedTm.isVisible(i), i, 0);
			tm.setValueAt(pagedTm.tm.getColumnName(i), i, 1);
		}
	}

	public static boolean setVisible(Component parent, PageTableModel ptm, double width) {
		ColumnVisible pane = new ColumnVisible(ptm);
		if (ModalDialog.doModal(parent, pane, StringResource.getString("columnVisibleDlgTitle"))) {
			for(int i = 0; i < pane.tm.getRowCount(); i++) {
				ptm.setVisible(i, (Boolean) pane.tm.getValueAt(i, 0));
			}
			ptm.buildVisibleIndices();
			ptm.fireTableChanged(new TableModelEvent(ptm));
			ptm.setPage(ptm.getPage());
			return true;
		}
		return false;
	}

	public boolean check() {
		for(int i = 0; i < tm.getRowCount(); i++) {
			if ((Boolean) tm.getValueAt(i, 0)) return true;
		}
		return false;
	}

}
