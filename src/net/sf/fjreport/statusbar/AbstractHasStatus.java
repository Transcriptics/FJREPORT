package net.sf.fjreport.statusbar;

import java.util.ArrayList;
import java.util.List;

public  abstract class AbstractHasStatus implements HasStatus{
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
}
