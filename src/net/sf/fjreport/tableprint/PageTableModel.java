package net.sf.fjreport.tableprint;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import net.sf.fjreport.MultiPage;
import net.sf.fjreport.statusbar.HasStatus;
import net.sf.fjreport.statusbar.StatusChangeListener;
import net.sf.fjreport.util.StringResource;

public class PageTableModel extends AbstractTableModel implements MultiPage, HasStatus {

	TableModel tm;
	boolean visibilities[];
	int visibleCount;
	int visibleIndices[];
	
	private int pageRowCount = 1;
	private int firstPageRowCount = 1;
	private int rowOffset;
	private int page;
	private int pageCount = 0;
	private PageTablePrint pageTable;
	
	public PageTableModel(TableModel tm, PageTablePrint pageTable){
		this.tm = tm;
		this.pageTable = pageTable;
		visibilities = new boolean[tm.getColumnCount()];
		visibleIndices = new int[tm.getColumnCount()];
		for(int i = 0; i < tm.getColumnCount(); i++) visibilities[i] = true;
		buildVisibleIndices();
	}
	
	public boolean isVisible(int i){
		return visibilities[i];
	}
	public void setVisible(int i, boolean visible) {
		visibilities[i] = visible;
	}
	public void buildVisibleIndices(){
		visibleCount = 0;
		for(int i = 0; i < visibilities.length; i++) {
			if (visibilities[i]) visibleIndices[visibleCount++] = i;
		}
	}
	
	public void packPages(int pageRowCount, int firstPageRowCount){
		if (tm == null) return;
		this.pageRowCount = pageRowCount;
		this.firstPageRowCount = firstPageRowCount;
		int totalCount = tm.getRowCount();
		if (totalCount <= firstPageRowCount) pageCount = 1;
		else pageCount = (int) (1 + Math.ceil((totalCount - firstPageRowCount) / (double)pageRowCount));
	}
	
	public void setPage(int page) {
		if (page <0 || page >= pageCount) return;
		this.page = page;
		if (page == 0) rowOffset = 0;
		else if (page > 0) rowOffset = firstPageRowCount + pageRowCount * (page - 1); 
		fireTableDataChanged();
		if (pageTable != null) pageTable.updateUI();
		String pageMsg = StringResource.getString("pageMessage");
		pageMsg = pageMsg.replace("%p", String.valueOf(page + 1));
		pageMsg = pageMsg.replace("%totalpage", String.valueOf(pageCount));
		notifyStatusChange(0, pageMsg);
	}
	
	public int getRowCount() {
		// TODO Auto-generated method stub
		if (page == 0)
			return Math.min(tm.getRowCount(), firstPageRowCount);
		else
			return Math.min(tm.getRowCount() - firstPageRowCount - (page - 1) * pageRowCount, pageRowCount);
	}

	public int getColumnCount() {
		// TODO Auto-generated method stub
		//return tm.getColumnCount();
		return visibleCount;
	}

	public String getColumnName(int arg0) {
		// TODO Auto-generated method stub
		//return tm.getColumnName(arg0);
		if (arg0 < 0 || arg0 >= visibleCount) return null;
		return tm.getColumnName(visibleIndices[arg0]);
	}

	public Object getValueAt(int arg0, int arg1) {
		// TODO Auto-generated method stub
		if (arg1 < 0 || arg1 >= visibleCount) return null;
		return tm.getValueAt(arg0 + rowOffset, visibleIndices[arg1]);
	}

	@Override
	public Class<?> getColumnClass(int arg0) {
		// TODO Auto-generated method stub
		return super.getColumnClass(visibleIndices[arg0]);
	}

	public void prePage() {
		setPage(page-1);
	}

	public void nextPage() {
		setPage(page+1);
	}

	public void firstPage() {
		setPage(0);
	}

	public void lastPage() {
		setPage(pageCount-1);
	}

	public int getPage() {
		return page;
	}

	private List statusListeners = new ArrayList();
	public void addStatusChangeListener(StatusChangeListener statusListener) {
		statusListeners.add(statusListener);
	}
	public void removeStatusChangeListener(StatusChangeListener statusListener) {
		statusListeners.remove(statusListener);
	}
	private void notifyStatusChange(int messageType, String message){
		for (int i = 0; i < statusListeners.size(); i++) {
			((StatusChangeListener)statusListeners.get(i)).statusChange(messageType, message);
		}
	}

	public int getTotalPageCount() {
		return pageCount;
	}
}
