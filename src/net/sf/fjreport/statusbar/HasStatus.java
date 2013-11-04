package net.sf.fjreport.statusbar;


public interface HasStatus {
	public void addStatusChangeListener(StatusChangeListener statusListener);
	public void removeStatusChangeListener(StatusChangeListener statusListener);
}
