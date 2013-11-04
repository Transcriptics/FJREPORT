package net.sf.fjreport.line;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sf.fjreport.util.ModalDialog;
import net.sf.fjreport.util.StringResource;




public class LineEditor extends JPanel{
	
	private static Line line = new Line(null);
	static {
		line.setStartPoint(new Point(60, 8));
		line.setOrientation(Line.HORIZONTAL_ORIENTATION);
		line.setLength(215);
	}
	private static JLabel labelLine = new JLabel(StringResource.getString("lineEditorLableLine")){
		public void paint(Graphics g) {
			super.paint(g);
			line.render(200, 200, (Graphics2D)g);
		}
	};
	
	private static Line[] typeLines = new Line[5];
	private static Line[] widthLines = new Line[5];
	private static List typeCheckBoxes = new ArrayList();
	private static List widthCheckBoxes = new ArrayList();
	
	private JCheckBox cbHide;
	
	private static final LineEditor lineEditor = new LineEditor();
	
	protected LineEditor(){
		super(new BorderLayout());
		createLines();
		createUI();
	}
	
	private void createLines() {
		Line line;
		for (int i = 0; i < 5; i++) {
			line = new Line(null);
			line.setStartPoint(new Point(20, 18));
			line.setLength(100);
			line.setOrientation(Line.HORIZONTAL_ORIENTATION);
			line.setLineWidth(i+1);
			line.setLineType(Line.LINE_NORMAL);
			widthLines[i] = line;
		}
		for (int i = 0; i < 5; i++) {
			line = new Line(null);
			line.setStartPoint(new Point(20, 18));
			line.setLength(100);
			line.setOrientation(Line.HORIZONTAL_ORIENTATION);
			line.setLineType(i);
			typeLines[i] = line;
		}
	}

	private void createUI() {
		setLayout(new BorderLayout());
		JPanel centerPanel = new JPanel(new GridLayout(1, 2, 5, 5));
		JPanel upLeftPane = new JPanel();
		upLeftPane.setLayout(new BoxLayout(upLeftPane, BoxLayout.Y_AXIS));
		JPanel upRightPane = new JPanel();
		upRightPane.setLayout(new BoxLayout(upRightPane, BoxLayout.Y_AXIS));
		
		upLeftPane.setBorder(new TitledBorder(StringResource.getString("lineEditorTypeTitle")));
		upRightPane.setBorder(new TitledBorder(StringResource.getString("lineEditorWidthTitle")));

		LineTypeBox b;
		CheckboxGroup cg = new CheckboxGroup();
		for(int i=0; i<5; i++) {
			b = new LineTypeBox(typeLines[i], cg);
			if (i==0) b.setState(true); 
			typeCheckBoxes.add(b);
			upLeftPane.add(b, cg);
		}
		cg = new CheckboxGroup();
		LineWidthBox bb;
		for(int i=0; i<5; i++) {
			bb = new LineWidthBox(widthLines[i], cg);
			if (i+1==line.getLineWidth()) bb.setState(true);
			widthCheckBoxes.add(bb);
			upRightPane.add(bb, cg);
		}
		centerPanel.add(upLeftPane);
		centerPanel.add(upRightPane);
		
		cbHide = new JCheckBox(StringResource.getString("lineEditorInvisibleCaption"));
		cbHide.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent arg0) {
				line.setInVisible(((JCheckBox)arg0.getSource()).isSelected());
				labelLine.updateUI();
			}});
		
		add(labelLine, "North");
		add(centerPanel, "Center");
		add(cbHide, "South");
			
		setPreferredSize(new Dimension(300, 300));
	}



	public static boolean edit(Component parent, Line line) {
		// TODO Auto-generated method stub
		lineEditor.line.setLineType(line.getLineType());
		lineEditor.line.setLineWidth(line.getLineWidth());
		lineEditor.line.setInVisible(line.isInVisible());
		lineEditor.cbHide.setSelected(line.isInVisible());
		((Checkbox)lineEditor.typeCheckBoxes.get(line.getLineType())).setState(true);
		((Checkbox)lineEditor.widthCheckBoxes.get(line.getLineWidth()-1)).setState(true);
		
		if (ModalDialog.doModal(parent, lineEditor, StringResource.getString("lineEditorTitle"))) {
			line.setLineType(lineEditor.line.getLineType());
			line.setLineWidth(lineEditor.line.getLineWidth());
			line.setInVisible(lineEditor.line.isInVisible());
			return true;
		} return false;
	}

	class LineTypeBox extends Checkbox {
		private Line line;
		LineTypeBox(Line line, CheckboxGroup cg) {
			super("                                   ", cg, false);
			this.line = line;
		}
		public void paint(Graphics arg0) {
			// TODO Auto-generated method stub
			super.paint(arg0);
			Graphics2D g2d = (Graphics2D)arg0;
			line.render(200, 100, g2d);
		}
		public void setState(boolean arg0) {
			// TODO Auto-generated method stub
			super.setState(arg0);
			if (arg0){ 
				LineEditor.line.setLineType(line.getLineType());
				labelLine.updateUI();
			}
		}
	}
	class LineWidthBox extends Checkbox {
		private Line line;
		LineWidthBox(Line line, CheckboxGroup cg) {
			super("                                   ", cg, false);
			this.line = line;
		}
		public void paint(Graphics arg0) {
			// TODO Auto-generated method stub
			super.paint(arg0);
			Graphics2D g2d = (Graphics2D)arg0;
			line.render(0, 0, g2d);
		}
		public void setState(boolean arg0) {
			// TODO Auto-generated method stub
			super.setState(arg0);
			if (arg0) {
				LineEditor.line.setLineWidth(line.getLineWidth());
				labelLine.updateUI();
			}
		}
	}
}
