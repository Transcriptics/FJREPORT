package net.sf.fjreport.statusbar;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;

public class JStatusBar extends JPanel {
	private static final long serialVersionUID = 1L;
	private int widths[];
	private JPanel panels[];
	private int totalWidth;
	
	public static final int CLOCK = -1;
	
	public JStatusBar(int widths[]){
		super(new GridBagLayout());
		if (widths==null) widths = new int[]{100};
		this.widths = widths;
		createPanels();
	}

	private void createPanels() {
		panels = new JPanel[widths.length];
		GridBagConstraints c;
		totalWidth = 0;
		for(int i = 0; i < widths.length; i++) {
			panels[i] = new JPanel();
			panels[i].setBorder(new BevelBorder(BevelBorder.LOWERED));
			c = new GridBagConstraints();
			c.gridx = i;
			c.gridy = 0;
			c.ipadx = 0;
			c.weightx = widths[i];
			c.ipady = 1;
			c.anchor = GridBagConstraints.WEST;
			c.fill = GridBagConstraints.BOTH;
			c.anchor = GridBagConstraints.WEST;
			add(panels[i], c);
			totalWidth += widths[i];
		}
/*		addComponentListener(new ComponentAdapter(){
			public void componentResized(ComponentEvent arg0) {
				super.componentResized(arg0);
				int w = getWidth();
				System.out.println(w);
				for (int i = 0; i < widths.length; i++){
					int ww = (int) (w * widths[i] / (double) totalWidth);
					System.out.println(ww);
					panels[i].setPreferredSize(new Dimension(ww, 18));
				}
			}
		});*/
	}	
	
	synchronized public void setContent(final int i, Object content){
		if (i > widths.length || i < 0) return;		
		if (content instanceof String) {
			JLabel lbl = null;
			if ((panels[i].getComponentCount() > 0) && (panels[i].getComponent(0) instanceof JLabel)){
				lbl = (JLabel)panels[i].getComponent(0);
			}else{
				panels[i].removeAll();
				lbl = new JLabel();
				lbl.setFont(new Font("Courier", 0, 12));
				FlowLayout fl = new FlowLayout();
				fl.setVgap(0);
				fl.setHgap(5);
				panels[i].setLayout(fl);
				panels[i].add(lbl);
			}			
			lbl.setText(content.toString());
		} else if (content instanceof Integer){
			if (content.equals(CLOCK)) {
				setContent(i, DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date()));
				final Timer timer = new Timer(60000, new ActionListener(){
					DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT);
					public void actionPerformed(ActionEvent arg0) {						
						setContent(i, df.format(new Date()));
					}});
				timer.setRepeats(true);
				timer.start();
			} else if ((Integer)content>=0 && (Integer)content<=100) {
				JProgressBar pb;
				if ((panels[i].getComponentCount() > 0) && (panels[i].getComponent(0) instanceof JProgressBar)){
					pb = (JProgressBar)panels[i].getComponent(0);
				}else{
					panels[i].setLayout(new BorderLayout());
					panels[i].removeAll();
					pb = new JProgressBar(0, 100);
//					pb.setFont(new Font("Courier", 0, 12));
					panels[i].add(pb, "Center");
				}
				pb.setValue((Integer) content);
			}
		}
	}
	
	public Object getContent(int i){
		Object result = null;
		if (i < widths.length && i >=0) {
			if (panels[i].getComponentCount() > 0)
				result = panels[i].getComponent(0);
			if (result instanceof JLabel) return ((JLabel)result).getText();
			else if (result instanceof JProgressBar) return ((JProgressBar)result).getValue();
		}
		return result;
	}
	
	public static void main(String[] args){
		JFrame m = new JFrame("statusbar exmaple");
		m.setLayout(new BorderLayout());
		final JStatusBar bar = new JStatusBar(new int[]{10, 20, 10, 0});
		bar.setContent(0, -1);
		bar.setContent(1, 0);
		final Timer timer = new Timer(100, new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				int ii;
				ii = ((Integer) (bar.getContent(1)) + 1) % 100;
				bar.setContent(1, ii);
			}});
		timer.setRepeats(true);
		timer.start();
		bar.setContent(2, "fj_lewis@users.sf.net");
		bar.setContent(3, "statusbar example");
		m.add(bar, BorderLayout.SOUTH);
		m.setSize(600, 300);
		m.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		m.setVisible(true);
	}
	
}
