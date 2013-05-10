package com.james.jcmdController.command;

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
import java.net.Socket;

import com.james.jcmdController.JCommand;

public class Jtransfer extends JCommand{
	private static final int FILEPORT=1985;
	private Socket socket,filesocket;
	private InputStream in;
	private OutputStream out;
	private BufferedReader bin,sysin;
	private PrintWriter bout;

	public Jtransfer(Socket socket) {
		this.socket=socket;
		this.exec();
	}

	public void exec() {
		try {
			sysin=new BufferedReader(new InputStreamReader(System.in));
			bin=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			bout=new PrintWriter(socket.getOutputStream());
			
			String ip = socket.getInetAddress().getHostAddress();
			System.out.print(ip + "$Jtransfer>");
			String cmd,remotefile,localfile;
			while (!(cmd = sysin.readLine()).equals("exit")) {
				bout.println("$Jtransfer");
				bout.flush();
				String res;
				if(!(res=bin.readLine()).equals("ready")){
					System.out.println(res);
					return;
				}
				filesocket=new Socket(ip,FILEPORT);
				in=filesocket.getInputStream();
				out=filesocket.getOutputStream();
				String[] cmds = cmd.split(" ");
				if(cmds.length!=3){
					this.dispalyUsage();
					continue;
				}
				if ("get".equals(cmds[0])) {
					remotefile=cmds[1];
					localfile=cmds[2];
					this.getFile(remotefile, localfile);
				}
				else if("send".equals(cmds[0])){
					localfile=cmds[1];
					remotefile=cmds[2];
					this.sendFile(localfile, remotefile);
				}
				else{
					this.dispalyUsage();
				}
				filesocket.close();
				System.out.print(ip + "$Jtransfer>");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void getFile(String remotefile,String localfile) {
		try {
			File desfile=new File(localfile);
			if(desfile.exists()){
				System.out.println("file aleady exist,will you over write it? Y or N");
				if(sysin.readLine().equals("Y")){
					if(!desfile.delete()){
						System.out.println("this file can not over write!");
						return;
					}
				}
				else{
					return;
				}
			}
			if(!desfile.createNewFile()){
				System.out.println("unable to create this file!");
				return;
			}
			bout.println("get "+remotefile);
			bout.flush();
			String status=bin.readLine();
			if(!status.equals("ready")){
				System.out.println(status);
			}
			else{
				FileOutputStream fos=new FileOutputStream(localfile);
				System.out.print("receiving");
				byte[] bytes=new byte[1024*100];
				int c;
				while((c=in.read(bytes))!=-1){
					fos.write(bytes,0,c);
					System.out.print(".");
				}
				fos.close();			
				System.out.println();
				System.out.println(bin.readLine());
			}
		} catch (FileNotFoundException e) {
			System.out.println(localfile+" not found!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void sendFile(String localfile,String remotefile) {		
		try {
			bout.println("send "+remotefile);
			bout.flush();
			String status=bin.readLine();
			if(!status.equals("ready")){
				System.out.println(status);
				String answer=sysin.readLine();
				bout.println(answer);
				bout.flush();
				status=bin.readLine();
			}
			if(status.equals("ready")){
				FileInputStream fis=new FileInputStream(localfile);
				System.out.print("sending");
				byte[] bytes=new byte[1024*100];
				int c;
				while((c=fis.read(bytes))!=-1){
					out.write(bytes,0,c);
					System.out.print(".");
				}
				fis.close();
				out.close();				
				System.out.println();
				System.out.println(bin.readLine());
			}
		} catch (FileNotFoundException e) {
			System.out.println(localfile+" not found!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void dispalyUsage(){
		System.out.println("usage:get remotefile localfile");
		System.out.println("or send localfile remotefile");
	}
}
