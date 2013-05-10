package com.james.jcmd;

import java.io.IOException;
import java.net.ServerSocket;

import javax.net.ServerSocketFactory;

public class Jcmd{
	private static final int PORT = 1984;

	public static void main(String[] args) {
		ServerSocket server=null;
		try {
			server = ServerSocketFactory.getDefault().createServerSocket(PORT);
			while(true){
				new JcmdThread(server.accept()).start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				server.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}