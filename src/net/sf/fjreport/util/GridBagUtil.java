package net.sf.fjreport.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;

public class GridBagUtil {
	
	public static final int VERTICAL = GridBagConstraints.VERTICAL;
	public static final int BOTH = GridBagConstraints.BOTH;
	public static final int HORIZONTAL = GridBagConstraints.HORIZONTAL;


	public static void add(Container parent, Component c, int gridx, int gridy) {
		GridBagConstraints cc = new GridBagConstraints();
		cc.gridx = gridx;
		cc.gridy = gridy;
		parent.add(c, cc);
	}
	
	public static void addwh(Container parent, Component c, int gridx, int gridy, int w, int h){
		GridBagConstraints cc = new GridBagConstraints();
		cc.gridx = gridx;
		cc.gridy = gridy;
		cc.gridwidth = w;
		cc.gridheight = h;
		parent.add(c, cc);
	}
	
	public static void addf(Container parent, Component c, int gridx, int gridy, int fill) {
		GridBagConstraints cc = new GridBagConstraints();
		cc.gridx = gridx;
		cc.gridy = gridy;
		cc.fill = fill;
		parent.add(c, cc);
	}

	public static void addwhf(Container parent, Component c, int gridx, int gridy, int w, int h, int fill){
		GridBagConstraints cc = new GridBagConstraints();
		cc.gridx = gridx;
		cc.gridy = gridy;
		cc.gridwidth = w;
		cc.gridheight = h;
		cc.fill = fill;
		parent.add(c, cc);
	}
	
	public static void add2(Container parent, Component c, int gridx, int gridy, int weightx, int weighty) {
		GridBagConstraints cc = new GridBagConstraints();
		cc.gridx = gridx;
		cc.gridy = gridy;
		cc.weightx = weightx;
		cc.weighty = weighty;
		parent.add(c, cc);
	}
	
	public static void addwh2(Container parent, Component c, int gridx, int gridy, int w, int h, int weightx, int weighty){
		GridBagConstraints cc = new GridBagConstraints();
		cc.gridx = gridx;
		cc.gridy = gridy;
		cc.gridwidth = w;
		cc.gridheight = h;
		cc.weightx = weightx;
		cc.weighty = weighty;
		parent.add(c, cc);
	}
	
	public static void addf2(Container parent, Component c, int gridx, int gridy, int weightx, int weighty, int fill) {
		GridBagConstraints cc = new GridBagConstraints();
		cc.gridx = gridx;
		cc.gridy = gridy;
		cc.weightx = weightx;
		cc.weighty = weighty;
		cc.fill = fill;
		parent.add(c, cc);
	}
	
	public static void addwhf2(Container parent, Component c, int gridx, int gridy, int w, int h, int weightx, int weighty, int fill){
		GridBagConstraints cc = new GridBagConstraints();
		cc.gridx = gridx;
		cc.gridy = gridy;
		cc.gridwidth = w;
		cc.gridheight = h;
		cc.weightx = weightx;
		cc.weighty = weighty;
		cc.fill = fill;
		parent.add(c, cc);
	}
	
	
}
