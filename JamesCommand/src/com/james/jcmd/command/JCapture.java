package com.james.jcmd.command;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.imageio.ImageIO;

public class JCapture{
	private static final int SCREENPORT=1985;
	private Socket socket,screensocket;
	private OutputStream out;
	private PrintWriter bout;
	private double scale = 0.6; 
	
	public JCapture(){
		
	}
	
	public JCapture(Socket socket){
		this.socket=socket;		
		this.exec();
	}
	
	public void exec(){
		ServerSocket screenserver=null;
		try {
			bout=new PrintWriter(socket.getOutputStream());	
			screenserver=new ServerSocket(SCREENPORT);
			bout.println("ready");
			bout.flush();
			screensocket=screenserver.accept();
			out=screensocket.getOutputStream();
						
			Robot robot=new Robot();
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			Rectangle screenRect = new Rectangle(screenSize);
			BufferedImage screen = robot.createScreenCapture(screenRect);
			BufferedImage img = resize(screen, scale);
			ImageIO.write(img, "jpeg", out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				screenserver.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private BufferedImage resize(BufferedImage src, double scale){
		int width = (int)(src.getWidth()*scale);
		int height = (int)(src.getHeight()*scale);
		Image img = src.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		BufferedImage bufimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
		bufimg.getGraphics().drawImage(img, 0, 0, null);
		return bufimg;
	}
}
