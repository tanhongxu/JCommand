package com.james.jcmd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class JcmdThread extends Thread{
	private Socket socket=null;
	private Charset dcs=Charset.defaultCharset();
	
	public JcmdThread(Socket socket){
		this.socket=socket;
	}
	
	public void run(){
		PrintWriter ctrlout;
		BufferedReader ctrlin,resbr;
		String cmd,res;
		Process exec;
		CharsetDecoder cd=dcs.newDecoder();
		try {
				ctrlin = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				ctrlout = new PrintWriter(socket.getOutputStream());
				while (!socket.isClosed()) {
					cmd = ctrlin.readLine();
					if(cmd==null)
						break;
					System.out.println(cmd);
					if(cmd.charAt(0)=='$'){
						cmd=cmd.substring(1);
						try {
							Class.forName("com.james.jcmd.command."+cmd).getConstructor(Socket.class).newInstance(socket);
						} catch (ClassNotFoundException e) {
							ctrlout.println("command "+cmd+" not found!");
							ctrlout.flush();
							ctrlout.println("complete!");
							ctrlout.flush();
							continue;
						}
					}
					else{
						try {
							exec = Runtime.getRuntime().exec(cmd);
						} catch (Exception e) {
//						e.printStackTrace(ctrlout);
							ctrlout.println(e.getMessage());
							ctrlout.flush();
							ctrlout.println("complete!");
							ctrlout.flush();
							continue;
						}
						resbr=new BufferedReader(new InputStreamReader(exec.getInputStream()));					
						while((res=resbr.readLine())!=null){
							ctrlout.println(cd.decode(ByteBuffer.wrap(res.getBytes())).toString());
							ctrlout.flush();
						}
						ctrlout.println("complete!");
						ctrlout.flush();
					}
				}				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
