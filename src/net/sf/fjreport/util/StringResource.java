package net.sf.fjreport.util;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

public class StringResource {
	private static ResourceBundle resource;
	static {
		try {
			resource = ResourceBundle.getBundle("Strings");
		} catch (Exception e) {
			resource = null;
		}
		if (resource == null) {
			try {
				resource = ResourceBundle.getBundle("Strings", Locale.US);
			} catch (Exception e) {
				resource = null;
			}
		}
		if (resource == null) {
			JOptionPane.showMessageDialog(null, "Can not load language resources file: " + "Strings_" + Locale.getDefault().toString() + ".properties");
			System.exit(0);
		}
	}
	public static String getString(String key){
		try {
			return resource.getString(key);
		} catch(Exception e) {
			return "";
		}
	}
}
