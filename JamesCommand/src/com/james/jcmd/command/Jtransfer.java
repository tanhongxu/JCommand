package com.james.jcmd.command;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Jtransfer {
	private static final int FILEPORT=1985;
	private Socket socket,filesocket;
	private InputStream in;
	private OutputStream out;
	private BufferedReader bin;
	private PrintWriter bout;
	
	public Jtransfer(Socket socket){
		this.socket=socket;		
		this.exec();
	}
	public void exec(){
		ServerSocket fileserver=null;
		try {
			bin=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			bout=new PrintWriter(socket.getOutputStream());			
			fileserver=new ServerSocket(FILEPORT);		
			bout.println("ready");
			bout.flush();
			filesocket=fileserver.accept();
			in=filesocket.getInputStream();
			out=filesocket.getOutputStream();
			
			String cmds=bin.readLine();
			String[] cmd=cmds.split(" ");
			if(cmd[0].equals("send")){
				this.receiveFile(cmd[1]);
			}
			else if(cmd[0].equals("get")){
				this.fetchFile(cmd[1]);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	finally{
			try {
				fileserver.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void fetchFile(String file){
		try {
			File desfile=new File(file);
			if(!desfile.exists()){
				bout.println("file not found!");
				bout.flush();
				return;
			}
			FileInputStream fis=new FileInputStream(desfile);
			bout.println("ready");
			bout.flush();
			byte[] bytes=new byte[1024*100];
			int c;
			while((c=fis.read(bytes))!=-1){
				out.write(bytes,0,c);
			}
			fis.close();
			out.close();
			bout.println("transfer complete!");
			bout.flush();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	public void receiveFile(String file){
		try {
			File desfile=new File(file);
			if(desfile.exists()){
				bout.println("file aleady exist,will you over write it? Y or N");
				bout.flush();
				if(bin.readLine().equals("Y")){
					if(!desfile.delete()){
						bout.println("this file can not over write!");
						bout.flush();
						return;
					}
				}
				else{
					bout.println("end");
					bout.flush();
					return;
				}
			}
			if(!desfile.createNewFile()){
				bout.println("unable to create this file!");
				bout.flush();
				return;
			}				
			FileOutputStream fos=new FileOutputStream(desfile);
			bout.println("ready");
			bout.flush();
			byte[] bytes=new byte[1024*100];
			int c;
			while((c=in.read(bytes))!=-1){
				fos.write(bytes,0,c);
			}
			fos.close();
			bout.println("transfer complete!");
			bout.flush();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}
