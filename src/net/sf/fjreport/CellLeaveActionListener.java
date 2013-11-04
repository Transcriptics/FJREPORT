package net.sf.fjreport;

import net.sf.fjreport.cell.Cell;

/**
 * 
 * Cell leave event, occurs when a cell loose
 * focus under edit or readonly state
 * 
 * Copyright (C) since <2006>  <Frank Lewis>
 */


public interface CellLeaveActionListener {
	/**
	 * response to cell leave event.
	 * @param lostFocusCell the cell loose focus
	 */
public void cellLeaveActionListener(Cell lostFocusCell);
}
