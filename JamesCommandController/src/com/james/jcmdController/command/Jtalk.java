package com.james.jcmdController.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import com.james.jcmdController.JCommand;

public class Jtalk extends JCommand{
	private Socket socket,voicesocket;
	private static final int PORT=1985;
	private OutputStream out;
	private TargetDataLine vline;
	private AudioInputStream vin;
	private BufferedReader bin,sysin;
	private PrintWriter bout;
	
	public Jtalk(Socket socket){
		this.socket=socket;
		this.exec();
	}
	public void exec(){
		String ip=socket.getInetAddress().getHostAddress();
		try {
			sysin=new BufferedReader(new InputStreamReader(System.in));
			bin=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			bout=new PrintWriter(socket.getOutputStream());
			System.out.print(ip+"$Jtalk>");
			bout.println("$Jtalk");
			bout.flush();
			if(!bin.readLine().equals("ready"))
				return;
			voicesocket=new Socket(ip,PORT);
			System.out.println("port 1985 ok");
			out=voicesocket.getOutputStream();
			System.out.println("connected!");
			vline=AudioSystem.getTargetDataLine(new AudioFormat(64,8,1,true,false));
			vline.open();
			vline.start();
			System.out.println("begin to record!");
			vin=new AudioInputStream(vline);
			System.out.println("getint sound...");
			while(!sysin.equals("exit")){
				AudioSystem.write(vin, AudioFileFormat.Type.AU, out);
				System.out.println("sending...");
			}
			bout.println("exit");
			bout.flush();
			vline.stop();
			vline.close();
			out.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}
