package net.sf.fjreport.io;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class ReportFileFilter extends FileFilter {

	public boolean accept(File arg0) {
		return (arg0.isDirectory() || 
				arg0.getName().toLowerCase().endsWith(".xml"));
	}

	public String getDescription() {
		return "FJReport files (*.xml)";
	}

}
