package net.sf.fjreport.util;

import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.print.PageFormat;
import java.awt.print.Paper;

public class CommonUtil {

	public static Frame mainFrame;
	public static String fontNames[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
	public static String fontSizes[] = { "6", "7", "8", "9", "10", "11", "12", "14", "16",
		"18", "20", "22", "24", "26", "28", "36", "48", "72" };
	
	public static boolean isPageEqual(final PageFormat pf1, final PageFormat pf2) {
		// isPageEqual function is copied from JFreeReport, many thanks to the guys.
		if (pf1 == pf2) {
			return true;
		}
		if (pf1 == null || pf2 == null) {
			return false;
		}

		if (pf1.getOrientation() != pf2.getOrientation()) {
			return false;
		}
		final Paper p1 = pf1.getPaper();
		final Paper p2 = pf2.getPaper();
		
		return (   (int)(p1.getWidth()*10000) == (int)(p2.getWidth()*10000)
				&& (int)(p1.getHeight()*10000) == (int)(p2.getHeight()*10000)
				&& (int)(p1.getImageableHeight()*10000) == (int)(p2.getImageableHeight()*10000)
				&& (int)(p1.getImageableWidth()*10000) == (int)(p2.getImageableWidth()*10000)
				&& (int)(p1.getImageableX()*10000) == (int)(p2.getImageableX()*10000)
				&& (int)(p1.getImageableY()*10000) == (int)(p2.getImageableY()*10000)
		);


//   imageablewidth is double


//		if (p1.getWidth() != p2.getWidth()) {
//			return false;
//		}
//		if (p1.getHeight() != p2.getHeight()) {
//			return false;
//		}
//		if (p1.getImageableX() != p2.getImageableX()) {
//			return false;
//		}
//		if (p1.getImageableY() != p2.getImageableY()) {
//			return false;
//		}
//		if (p1.getImageableWidth() != p2.getImageableWidth()) {
//			return false;
//		}
//		if (p1.getImageableHeight() != p2.getImageableHeight()) {
//			return false;
//		}
//		return true;
	}
	

}
