package com.james.jcmdController.command;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.ImageIcon;

import sun.awt.image.codec.JPEGImageDecoderImpl;

import com.james.jcmdController.JCommand;
import com.james.jcmdController.gui.ScreenCaptureGUI;

public class JCapture extends JCommand {
	private static final int SCREENPORT=1985;
	private Socket socket,screensocket;
	private InputStream in;
	private BufferedReader bin;
	private PrintWriter bout;
	
	public JCapture(Socket socket){
		this.socket=socket;
		this.exec();
	}
	@Override
	public void exec() {
		try {
			bin=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			bout=new PrintWriter(socket.getOutputStream());
			String ip = socket.getInetAddress().getHostAddress();
			
			bout.println("$JCapture");
			bout.flush();
			String res;
			if(!(res=bin.readLine()).equals("ready")){
				System.out.println(res);
				return;
			}
			screensocket = new Socket(ip, SCREENPORT);
			in = screensocket.getInputStream();
			
			JPEGImageDecoderImpl decoder = new JPEGImageDecoderImpl(in);
			BufferedImage img = decoder.decodeAsBufferedImage();
			
			Thread gui = new Thread(new ScreenCaptureGUI(img));
			gui.run();
			
			in.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}

}
