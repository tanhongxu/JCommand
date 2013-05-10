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

public class JCaptureM extends JCommand {
	private static final int SCREENPORT = 1985;
	private Socket socket, screensocket;
	private InputStream in;
	private BufferedReader bin;
	private PrintWriter bout;
	private int fps = 8;

	public JCaptureM(Socket socket) {
		this.socket = socket;
		this.exec();
	}

	@Override
	public void exec() {
		try {
			bin = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			bout = new PrintWriter(socket.getOutputStream());
			String ip = socket.getInetAddress().getHostAddress();
			ScreenCaptureGUI gui = new ScreenCaptureGUI();
			gui.open();

			while (true) {
				bout.println("$JCapture");
				bout.flush();
				String res;
				if (!(res = bin.readLine()).equals("ready")) {
					System.out.println(res);
					return;
				}
				screensocket = new Socket(ip, SCREENPORT);
				in = screensocket.getInputStream();

				JPEGImageDecoderImpl decoder = new JPEGImageDecoderImpl(in);
				BufferedImage img = decoder.decodeAsBufferedImage();

				gui.updateImage(img);
				
				Thread.sleep((long)(1000 / fps));
				
			}			
		} catch (IOException e) {
			System.out.println(e.getCause());
		} catch (InterruptedException e) {
			System.out.println(e.getCause());
		} catch (RuntimeException e){
			e.printStackTrace();
		}finally{
			try {
				in.close();
			} catch (IOException e) {
				System.out.println(e.getCause());
			}
		}

	}

}
