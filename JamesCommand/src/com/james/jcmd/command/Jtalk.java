package com.james.jcmd.command;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Jtalk {
	private static final int VOICEPORT=1985;
	private Socket socket,voicesocket;
	private InputStream in;
	private BufferedReader bin;
	private PrintWriter bout;
	
	public Jtalk(Socket socket){
		this.socket=socket;
		this.exec();
	}
	public void exec(){
		ServerSocket voiceserver=null;
		try {
			bin=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			bout=new PrintWriter(socket.getOutputStream());	
			voiceserver=new ServerSocket(VOICEPORT);			
			bout.println("ready");
			bout.flush();
			voicesocket=voiceserver.accept();
			in=voicesocket.getInputStream();
//			AudioPlayer voice=AudioPlayer.player;
//			voice.start(in);
			
			//a try: to save the voice in a file;
			File file=new File("f:\\voice.au");
			System.out.println("state1");
			AudioInputStream vin=AudioSystem.getAudioInputStream(in);
			System.out.println("state2");
			if(!file.createNewFile())
				System.out.println("create file failed!");
			while(!bin.readLine().equals("exit")){
				AudioSystem.write(vin, AudioFileFormat.Type.AU, file);
				System.out.println("recording...");
			}
			System.out.println("recorded!");
			vin.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				voiceserver.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
