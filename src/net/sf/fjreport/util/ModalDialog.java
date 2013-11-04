package net.sf.fjreport.util;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.DefaultKeyboardFocusManager;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;




public class ModalDialog extends JDialog{

	private JPanel contentPane;
	private JButton btnOK = new JButton(StringResource.getString("btnOKCaption"));
	private JButton btnCancel = new JButton(StringResource.getString("btnCancelCaption"));
	
	private static boolean modalResult;

	private DefaultKeyboardFocusManager keyListener = new DefaultKeyboardFocusManager() {
		public boolean dispatchKeyEvent(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
				doCancel();
				return true;
			}
			return super.dispatchKeyEvent(e);
		}
	};

	private ModalDialog(Frame parentFrame, JPanel content){
		super(parentFrame, true);
		this.contentPane = content;
		add(content, "Center");

		btnOK.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				if (contentPane instanceof CheckInput){
					if (!((CheckInput)contentPane).check()) return;
				}
				doOK();
			}});
		btnCancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				doCancel();
			}});
		int h = btnOK.getPreferredSize().height;
		int w = Math.max(btnOK.getPreferredSize().width, btnCancel.getPreferredSize().width); 
		btnOK.setPreferredSize(new Dimension(w + 10, h));
		btnCancel.setPreferredSize(new Dimension(w + 10, h));
		btnOK.setMargin(new Insets(0, 0, 0, 0));
		btnCancel.setMargin(new Insets(0, 0, 0, 0));
		JPanel bottomButtonPane = new JPanel(new FlowLayout());
		bottomButtonPane.add(btnOK);
		bottomButtonPane.add(btnCancel);
		if (contentPane instanceof CustomDlgUI) {
			JPanel bottomPane = new JPanel(new BorderLayout());
			JPanel additionalPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
			((CustomDlgUI)contentPane).buildBottomPane(additionalPane);
			bottomPane.add(bottomButtonPane, "East");
			bottomPane.add(additionalPane, "Center");
			add(bottomPane, "South");
		} else {
			add(bottomButtonPane, "South");
		}
		KeyboardFocusManager kbfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		kbfm.addKeyEventDispatcher(keyListener);
	}
	
	private void doOK(){
		modalResult = true;
		setVisible(false);
	}
	private void doCancel(){
		modalResult = false;
		setVisible(false);
	}
	
	public static boolean doModal(Component parent, JPanel contentPane, String title){
		ModalDialog dlg;
		if (parent == null) dlg = new ModalDialog(null, contentPane);
		else dlg = new ModalDialog(JOptionPane.getFrameForComponent(parent), contentPane);
		dlg.setTitle(title);
		Dimension d = contentPane.getPreferredSize();
		if (d == null || d.width < 60 || d.height < 60) 
			dlg.setSize(300, 300);
		else {
			dlg.setSize(Math.min(Toolkit.getDefaultToolkit().getScreenSize().width,	d.width),
					Math.min(Toolkit.getDefaultToolkit().getScreenSize().height-50, d.height+30));
		}
		dlg.setLocationRelativeTo(null);
		dlg.setResizable(false);
		modalResult = false;
		dlg.setVisible(true);
		KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(dlg.keyListener);
		return modalResult;
	}
	
	public static boolean doModalSticky(Component parent, JPanel contentPane, String title, Rectangle bounds){
		ModalDialog dlg;
		if (parent == null) dlg = new ModalDialog(null, contentPane);
		else dlg = new ModalDialog(JOptionPane.getFrameForComponent(parent), contentPane);
		dlg.setTitle(title);
		Dimension d = contentPane.getPreferredSize();
		if (d == null || d.width < 60 || d.height < 60) 
			dlg.setSize(300, 300);
		else {
			dlg.setSize(Math.min(Toolkit.getDefaultToolkit().getScreenSize().width,	d.width),
					Math.min(Toolkit.getDefaultToolkit().getScreenSize().height-50, d.height+30));
		}
		dlg.setBounds(bounds);
		dlg.setUndecorated(true);
		dlg.setResizable(false);
		modalResult = false;
		dlg.setVisible(true);
		KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(dlg.keyListener);
		return modalResult;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JPanel p = new JPanel();
		p.setPreferredSize(new Dimension(400, 60));
		if (doModal(null, p, "Test Modal Dialog")) System.out.println("Choose ok");
		else System.out.println("Choose cancel");
		System.exit(0);
	}

}
