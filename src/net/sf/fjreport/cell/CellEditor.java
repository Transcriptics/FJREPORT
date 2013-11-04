package net.sf.fjreport.cell;

import java.awt.Color;
import java.awt.Insets;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;


import net.sf.fjreport.FJReport;
import net.sf.fjreport.util.ModalDialog;
import net.sf.fjreport.util.StringResource;


public class CellEditor extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static CellEditor cellEditor;
	private JTextField nameField = new JTextField(10);
	private JTextArea contentField = new JTextArea(10, 3);
	private JComboBox cbType = new JComboBox();

	private JTextArea typeOptionArea = new JTextArea(10, 5);
	private JTextField upField = new JTextField(2);
	private JTextField leftField = new JTextField(2);
	private JTextField downField = new JTextField(2);
	private JTextField rightField = new JTextField(2);
	
	private CellEditor(Cell cell, FJReport report){
		super(null);
		createUI();
	}
	
	private void createUI() {
		JLabel lblName = new JLabel(StringResource.getString("cellEditorName"));
		JLabel lblContent = new JLabel(StringResource.getString("cellEditorContent"));
		JLabel lblType = new JLabel(StringResource.getString("cellEditorType"));
		JLabel lblComboOptions = new JLabel(StringResource.getString("cellEditorOption"));
		JLabel lblMargin = new JLabel(StringResource.getString("cellEditorMargin"));
		JLabel lblTop = new JLabel(StringResource.getString("cellTopMargin"));
		JLabel lblLeft = new JLabel(StringResource.getString("cellLeftMargin"));
		JLabel lblBottom = new JLabel(StringResource.getString("cellBottomMargin"));
		JLabel lblRight = new JLabel(StringResource.getString("cellRightMargin"));
		contentField.setBorder(new LineBorder(Color.GRAY, 1));
		contentField.setAutoscrolls(true);
		
		addItem(lblName, 		10, 10, 84, 24);
		addItem(lblContent, 	10, 40, 84, 24);
		addItem(lblType, 		10, 94, 84, 24);
		addItem(lblComboOptions, 10, 124, 84, 24);
		addItem(nameField, 		100, 10, 120, 24);
		addItem(contentField, 	100, 40, 120, 48);
		addItem(cbType, 		100, 94, 120, 24);
		addItem(new JScrollPane(typeOptionArea), 100, 124, 120, 80);
		addItem(lblMargin,		10, 220, 84, 24);
		addItem(lblTop,			156, 220, 60, 24);
		addItem(upField,		131, 220, 20, 24);
		addItem(lblLeft,		120, 263, 84, 24);
		addItem(leftField,		90, 263, 20, 24);
		addItem(lblBottom,		156, 300, 84, 24);
		addItem(downField,		131, 300, 20, 24);
		addItem(lblRight,		196, 263, 84, 24);
		addItem(rightField,		170, 263, 20, 24);
		for (int i = 0; i < 8; i++) {
			cbType.addItem(Cell.getTypeDescription(i));
		}
		setSize(240, 384);
	}

	private void addItem(JComponent item, int x, int y, int w, int h) {
		add(item);
		item.setBounds(x, y, w, h);
	}

	public static void edit(Cell cell, FJReport report) {
		cellEditor = new CellEditor(cell, report);
		cellEditor.nameField.setText(cell.getName());
		if (cell.getType() == cell.TYPE_LABEL)
			cellEditor.contentField.setText(cell.getStrValue());
		cellEditor.cbType.setSelectedIndex(cell.getType());
		cellEditor.upField.setText(String.valueOf(cell.getMargin().top));
		cellEditor.leftField.setText(String.valueOf(cell.getMargin().left));
		cellEditor.downField.setText(String.valueOf(cell.getMargin().bottom));
		cellEditor.rightField.setText(String.valueOf(cell.getMargin().right));
		if (cell.getType() == Cell.TYPE_COMBOBOX) {
			cellEditor.typeOptionArea.setText(cell.getComboxOptionsStr());
		}
		if (ModalDialog.doModal(report,cellEditor, StringResource.getString("cellEditorTitle"))) {
			cell.setName(cellEditor.nameField.getText());
			cell.setType(cellEditor.cbType.getSelectedIndex());
			cell.createEditor();
			if (cell.getType() == cell.TYPE_LABEL)
				cell.setValue(cellEditor.contentField.getText());
			if (cell.getType() == Cell.TYPE_COMBOBOX) {
				cell.setComboxOptionsStr(cellEditor.typeOptionArea.getText());
			}
			if (   Integer.parseInt(cellEditor.upField.getText()) != cell.getMargin().top
			 	|| Integer.parseInt(cellEditor.leftField.getText()) != cell.getMargin().left
			 	|| Integer.parseInt(cellEditor.downField.getText()) != cell.getMargin().bottom
			 	|| Integer.parseInt(cellEditor.rightField.getText()) != cell.getMargin().top) {
				cell.setMargin(new Insets(Integer.parseInt(cellEditor.upField.getText()), Integer.parseInt(cellEditor.leftField.getText()),
						Integer.parseInt(cellEditor.downField.getText()), Integer.parseInt(cellEditor.rightField.getText())));
				cell.setEditorBounds();
			}
			report.updateUI();
		}
	}

}
