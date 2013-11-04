package net.sf.fjreport.datepicker;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import net.sf.fjreport.util.StringResource;



public class FJDatePicker extends JPanel{

	private Calendar calendar = Calendar.getInstance();
	private static Date originalDate;
	private int firstMonthDay;
	
	private static final int dayCellSize = 24;
	private static final int width = dayCellSize * 7 + 14;
	private static final int height = dayCellSize * 7 + 42;
	private static final int upperPaneHeight = 24;
	private static final int x = 9;
	private static final int y = 36;
	
	private static final String[] dayOfWeekName = StringResource.getString("dayOfWeekName").split(" ");
	private static DateFormat df = new SimpleDateFormat("yyyy-MM");
    private static final Insets insets = new Insets(2, 2, 2, 2);

	private JLabel dateStr = new JLabel();
	private JButton lButton = new JButton("<");
	private JButton rButton = new JButton(">");
	private JButton llButton = new JButton("<<");
	private JButton rrButton = new JButton(">>");
	private JPanel upperPane;
	private JDialog dlg;
	private boolean modalResult;
	private static int currentMouseSelected;
	
	private FJDateField editor;
	
	public static int getDlgWidth(){
		return width + 4;
	}
	
	public static int getDlgHeight(){
		return height + 6;
	}
	
	public Date getValue() {
		return calendar.getTime();
	}

	public void setValue(Date value) {
		currentMouseSelected = 0;
		calendar.setTime(value);
		dateStr.setText(df.format(value));
		int w = calendar.get(Calendar.DAY_OF_WEEK);
		int d = calendar.get(Calendar.DAY_OF_MONTH);
		firstMonthDay = (w + 35 - d) % 7;
		updateUI();
	}

	public FJDatePicker(){
		super();
		originalDate = new Date();
		setValue(new Date());
		crateUI();
	}
	
	public FJDatePicker(Date dt) {
		super();
		crateUI();
		if (dt != null)	{
			originalDate = dt;
			setValue(dt);
		}
		else {
			originalDate = new Date();
			setValue(new Date());
		}
	}
	
	private void crateUI() {
		setPreferredSize(new Dimension(width, height));
		setMinimumSize(new Dimension(width, height));
		setMaximumSize(new Dimension(width, height));
		FlowLayout f = new FlowLayout();
		f.setVgap(0);
		upperPane = new JPanel(f);
		lButton.setMargin(insets);
		rButton.setMargin(insets);
		llButton.setMargin(insets);
		rrButton.setMargin(insets);
		JPanel lPane = new JPanel(new BorderLayout());
		JPanel rPane = new JPanel(new BorderLayout());
		lPane.add(lButton, "East");
		lPane.add(llButton, "West");
		rPane.add(rButton, "West");
		rPane.add(rrButton, "East");
		lPane.setSize(new Dimension(upperPaneHeight+upperPaneHeight, upperPaneHeight));
		rPane.setSize(new Dimension(upperPaneHeight+upperPaneHeight, upperPaneHeight));
		upperPane.add(lPane, "West");
		upperPane.add(dateStr, "Center");
		upperPane.add(rPane, "East");
		lButton.setPreferredSize(new Dimension(upperPaneHeight, upperPaneHeight));
		rButton.setPreferredSize(new Dimension(upperPaneHeight, upperPaneHeight));
		llButton.setPreferredSize(new Dimension(upperPaneHeight, upperPaneHeight));
		rrButton.setPreferredSize(new Dimension(upperPaneHeight, upperPaneHeight));
		dateStr.setPreferredSize(new Dimension(width - upperPaneHeight * 4 - 23, upperPaneHeight));
		upperPane.setPreferredSize(new Dimension(width-3, upperPaneHeight));
		lButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				addMonth(-1);
			}});
		rButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				addMonth(1);
			}});
		llButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				addYear(-1);
			}});
		rrButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				addYear(1);
			}});
		lButton.setFocusable(false);
		rButton.setFocusable(false);
		llButton.setFocusable(false);
		rrButton.setFocusable(false);
		add(upperPane, "North");
		dateStr.setHorizontalAlignment(JLabel.CENTER);
		setBorder(new LineBorder(new Color(192, 192, 192), 1));
		setFocusable(true);
		addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent arg0) {
				super.keyPressed(arg0);
				if (arg0.getKeyCode() == KeyEvent.VK_LEFT) addDay(-1);
				else if (arg0.getKeyCode() == KeyEvent.VK_RIGHT) addDay(1);
				else if (arg0.getKeyCode() == KeyEvent.VK_UP) addMonth(1);
				else if (arg0.getKeyCode() == KeyEvent.VK_DOWN) addMonth(-1);
				else if (arg0.getKeyCode() == KeyEvent.VK_PAGE_UP) addYear(1);
				else if (arg0.getKeyCode() == KeyEvent.VK_PAGE_DOWN) addYear(-1);
				else if (arg0.getKeyCode() == KeyEvent.VK_ENTER) doOK();
				else if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE) doCancel();
			}
		});
		addFocusListener(new FocusAdapter(){
			public void focusLost(FocusEvent arg0) {
				doCancel();
			}});
		addMouseMotionListener(new MouseMotionAdapter(){
			public void mouseMoved(MouseEvent arg0) {
				int mouseDate = getMouseDate(arg0.getX(), arg0.getY());
				if (mouseDate > 0 && currentMouseSelected != mouseDate) {
					currentMouseSelected = mouseDate;
					updateUI();
				}
			}});
		addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent arg0) {
				if (getMouseDate(arg0.getX(), arg0.getY()) > 0) {
					calendar.add(Calendar.DATE, currentMouseSelected - calendar.get(Calendar.DAY_OF_MONTH)); 
					doOK();
				}
			}
		});
	}

	private int getMouseDate(int xx, int yy){
		if (xx >= x + dayCellSize*7) return 0;
		int dd = (xx - x) / dayCellSize + ((yy - y) / dayCellSize) * 7 - firstMonthDay - 6;
		if (dd <0 || dd > calculateDaysInMonth(calendar)) return 0;
		else return dd;
	}
	
	private void doCancel(){
		if (dlg != null) dlg.dispose();
	}
	private void doOK(){
		modalResult = true;
		if (editor != null) editor.setValue(getValue());
		if (dlg != null) dlg.dispose();
	}

	private void addDay(int increment){
		calendar.add(Calendar.DATE, increment);
		setValue(calendar.getTime());
	}
	private void addMonth(int increment){
		calendar.add(Calendar.MONTH, increment);
		setValue(calendar.getTime());
	}
	private void addYear(int increment){
		calendar.add(Calendar.YEAR, increment);
		setValue(calendar.getTime());
	}
	
	@Override
	public void paint(Graphics arg0) {
		// TODO Auto-generated method stub
		super.paint(arg0);
		int stringW, stringW2, stringH, stringBase;
		Rectangle2D r;
		arg0.setColor(new Color(210, 210, 210));
		arg0.fillRect(x, y, dayCellSize*7, dayCellSize*7);
		int daysInMonth = calculateDaysInMonth(calendar);
		arg0.setColor(new Color(250, 250, 250));
		arg0.fillRect(x + dayCellSize*firstMonthDay, y + dayCellSize, (7-firstMonthDay)*dayCellSize, dayCellSize);
		arg0.fillRect(x, y + dayCellSize + dayCellSize, 7*dayCellSize, dayCellSize * ((daysInMonth-7+firstMonthDay)/7) );
		arg0.fillRect(x, y + dayCellSize*((daysInMonth+firstMonthDay)/7+1), ((daysInMonth+firstMonthDay)%7)*dayCellSize, dayCellSize);

		arg0.setColor(Color.YELLOW);
		arg0.fillRect(x+((calendar.get(Calendar.DAY_OF_WEEK)-1)%7)*dayCellSize, y+((calendar.get(Calendar.DAY_OF_MONTH)+firstMonthDay-1)/7+1)*dayCellSize, dayCellSize, dayCellSize);
		
		arg0.setColor(Color.GREEN);
		if (currentMouseSelected>0)
			arg0.fillRect(x+((currentMouseSelected+firstMonthDay-1)%7)*dayCellSize, 
					y+((currentMouseSelected+firstMonthDay-1)/7+1)*dayCellSize, dayCellSize, 
					dayCellSize);
		
		arg0.setColor(new Color(160, 160, 192));
		for(int i=0; i<=7; i++) {
			arg0.drawLine(x + dayCellSize*i, y, x+dayCellSize*i, y + dayCellSize*7);
			arg0.drawLine(x, y + dayCellSize*i, x+dayCellSize*7, y + dayCellSize*i);
		}
		arg0.setColor(Color.BLACK);
		FontMetrics fm = arg0.getFontMetrics();
		for(int i=0; i<7; i++){
			r = fm.getStringBounds(dayOfWeekName[i], arg0);
			stringW = (int) r.getWidth();
			stringH = (int) r.getHeight();
			stringBase = (int) r.getMinY();
			arg0.drawString(dayOfWeekName[i], x+dayCellSize*i+(dayCellSize-stringW)/2, 
					y + (dayCellSize-stringH)/2 - stringBase);
		}
		r = fm.getStringBounds("2", arg0);
		stringW = (int) r.getWidth();
		stringH = (int) r.getHeight();
		stringBase = (int) r.getMinY();
		r = fm.getStringBounds("22", arg0);
		stringW2 = (int) r.getWidth();
		for(int i=0; i<9; i++){
			arg0.drawString(String.valueOf(i+1), x + ((i+firstMonthDay)%7)*dayCellSize+(dayCellSize-stringW)/2,
					y + ((i+firstMonthDay)/7+1)*dayCellSize + (dayCellSize-stringH)/2 - stringBase);
		}
		for(int i=9; i<daysInMonth; i++){
			arg0.drawString(String.valueOf(i+1), x + ((i+firstMonthDay)%7)*dayCellSize+(dayCellSize-stringW2)/2,
					y + ((i+firstMonthDay)/7+1)*dayCellSize + (dayCellSize-stringH)/2 - stringBase);
		}
	}

	private static int calculateDaysInMonth(final Calendar c) {
        int daysInMonth = 0;
        switch (c.get(Calendar.MONTH)) {
            case 0:
            case 2:
            case 4:
            case 6:
            case 7:
            case 9:
            case 11:
                daysInMonth = 31;
                break;
            case 3:
            case 5:
            case 8:
            case 10:
                daysInMonth = 30;
                break;
            case 1:
                final int year = c.get(Calendar.YEAR);
                daysInMonth =
                        (0 == year % 1000) ? 29 :
                        (0 == year % 100) ? 28 :
                        (0 == year % 4) ? 29 : 28;
                break;
        }
        return daysInMonth;
    }
	
	public static Date pickDate(Date originDate, Frame parent, String title){
		final FJDatePicker dp = new FJDatePicker(originDate);
		dp.dlg = new JDialog(parent, title, true);
		dp.dlg.setSize(new Dimension(width+10, height+32));
		dp.dlg.add(dp, "Center");
		dp.dlg.setResizable(false);
		dp.dlg.setLocationRelativeTo(parent);
		dp.dlg.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		dp.dlg.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent arg0) {
				if (dp.modalResult != true) dp.setValue(originalDate);
				super.windowClosing(arg0);
			}
		});
		dp.dlg.setVisible(true);
		return dp.getValue();
	}
	
	public static void pickDate(Date originDate, int x, int y, FJDateField editor){
		final FJDatePicker dp = new FJDatePicker(originDate);
		dp.editor = editor;
		dp.dlg = new JDialog((Frame)null, true);
		dp.dlg.setSize(new Dimension(width+4, height+6));		
		dp.dlg.add(dp, "Center");
		dp.dlg.setResizable(false);
		dp.dlg.setLocation(x, y);
		dp.dlg.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		dp.dlg.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent arg0) {
				if (dp.modalResult != true) dp.setValue(originalDate);
				super.windowClosing(arg0);
			}
		});
		dp.dlg.setUndecorated(true);
		dp.dlg.setVisible(true);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(new SimpleDateFormat("yyyy-MM-dd").format(pickDate(null,
				null, "pick date")));
	}
}
