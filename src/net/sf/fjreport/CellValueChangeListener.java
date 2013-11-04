package net.sf.fjreport;

import net.sf.fjreport.cell.Cell;

/**
 * cell value change event, occurs after a cell's
 * editor value changed
 * Copyright (C) since <2006>  <Frank Lewis>
 */
public interface CellValueChangeListener {
	/**
	 * @param cell the cell whose value changed
	 * @param newValue new value of cell's editor
	 * @param oldValue old value of cell
	 */
	public void valueChangeAction(Cell cell, Object newValue, Object oldValue);
}
