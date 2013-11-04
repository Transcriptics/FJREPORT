
package net.sf.fjreport.datepicker;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class FJDateField extends JTextField{

	public static DateFormat defaultDf = new SimpleDateFormat("yyyy-MM-dd");
	private DateFormat dateFormat = defaultDf;
	private Date value;
	
	
	public FJDateField(){
		creatUI();
	}
	
	private void creatUI() {
        setLayout(null);

        setText("");
        setBackground(new Color(255, 255, 255));

        addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent arg0) {
				if (isEnabled() && isEditable()) onClick();
			}
        });
        
        addFocusListener(new FocusAdapter(){
			public void focusLost(FocusEvent arg0) {
				// TODO Auto-generated method stub
				super.focusLost(arg0);
				if (getText() == null || getText().equals("")) value = null;
				else {
					try {
						value = dateFormat.parse(getText());
					} catch (ParseException e) {
						// TODO Auto-generated catch block
//						JOptionPane.showMessageDialog((Component) arg0.getSource(), StringResource.getString("invalidDateFormatMsg"));
//						e.printStackTrace();
					}
				}
			}
        });

        addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_SPACE) {
					onClick();
				} else super.keyPressed(arg0);
			}
        });
	}

	protected void onClick() {
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		Point point = getLocationOnScreen();
		int ww = FJDatePicker.getDlgWidth();
		int hh = FJDatePicker.getDlgHeight();
		int positionX, positionY;
		if (ww + point.x > dimension.width * 3 / 4) {
			positionX = point.x + getWidth() - ww;
		} else positionX = point.x;
		if (hh + point.y > dimension.height - 40) {
			positionY = point.y - hh;
		} else positionY = point.y + getHeight();
		FJDatePicker.pickDate(getValue(), positionX, positionY, this);
	}

	public Date getValue() {
		return value;
	}

	public void setValue(Date value) {
		this.value = value;
		if (value != null)
			setText(dateFormat.format(value));
		else
			setText("");
	}
	public String getDateStr() {
		if (value == null || value.equals("")) return "";
		else return dateFormat.format(getValue());
	}
	public void setDateStr(String dateStr) {
		try {
			if (dateStr != null && dateStr != "")
				setValue(dateFormat.parse(dateStr));
			else
				setValue(null);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public DateFormat getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JFrame frame = new JFrame("dateField example");
		frame.setSize(200, 100);
		FJDateField dt = new FJDateField();
		frame.setLayout(null);
		frame.add(dt);
		dt.setBounds(10, 10, 120, 24);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

}
