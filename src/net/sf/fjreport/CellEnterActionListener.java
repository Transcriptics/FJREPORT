package net.sf.fjreport;

import net.sf.fjreport.cell.Cell;

/**
 * 
 * Cell enter event, occurs when a cell grab
 * focus under edit or readonly state
 * 
 * Copyright (C) since <2006>  <Frank Lewis>
 */
public interface CellEnterActionListener {
	/**
	 * response to cell enter event.
	 * @param grabFocusCell the cell newly grab focus
	 */
	public void cellEnterAction(Cell grabFocusCell);
}
