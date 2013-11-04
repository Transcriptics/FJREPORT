package net.sf.fjreport.io;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class ReportImageFileFilter extends FileFilter {

	public boolean accept(File arg0) {
		String name = arg0.getName().toLowerCase();
		return (arg0.isDirectory() || 
				name.endsWith(".jpg") ||
				name.endsWith(".gif") ||
				name.endsWith(".png"));
	}

	public String getDescription() {
		return "Image files (*.jpg, *.gif, *.png)";
	}

}
