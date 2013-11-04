package net.sf.fjreport;

import java.util.ArrayList;
import java.util.List;

import net.sf.fjreport.cell.Cell;
import net.sf.fjreport.line.Line;

/**
 * A report template is composed by multi-page
 * Each page is a structure of cells, hLines, vLines
 * and currentCell.
 * 
 * Copyright (C) since <2006>  <Frank Lewis>
 */
public class FJReportPage {
	/**
	 * all cells of the page 
	 */
	public List<Cell> cells = new ArrayList();
	List<Cell> oldCells = new ArrayList();
	/**
	 * current editing cell
	 */
	public Cell currentCell;
	/**
	 * horizontal lines
	 */
	public List<Line> hLines = new ArrayList();
	/**
	 * vertical lines
	 */
	public List<Line> vLines = new ArrayList();
	
	public int lineIDSeed = 0;
	
	public int getLineID() { 
		return lineIDSeed++;
	}
}
