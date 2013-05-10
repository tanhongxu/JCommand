package com.james.jcmdController.gui;

import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

public class ScreenCaptureGUI implements Runnable{
	
	private ImageIcon screen;
	private JLabel label;
	
	public ScreenCaptureGUI() {
		super();
		this.screen = new ImageIcon("f:\\processing.gif");
	}

	public ScreenCaptureGUI(Image screen) {
		super();
		this.screen = new ImageIcon(screen);
	}

	public void updateImage(Image screen){
		this.screen = new ImageIcon(screen);
		label = new JLabel(this.screen);
		label.updateUI();
	}
	
	public void open(){
		JFrame frame=new JFrame();
		frame.setTitle("James Screen Capture");
		frame.setVisible(true);
		frame.setSize(screen.getIconWidth(),screen.getIconHeight());
		
//		JToolBar toolbar = new JToolBar();
		
		label = new JLabel(screen);
		
		JScrollPane panel = new JScrollPane(label);
		panel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);	
		frame.add(panel);
		

	}

	public void run() {
		this.open();
	}
	
}
