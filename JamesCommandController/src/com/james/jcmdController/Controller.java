package com.james.jcmdController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.SocketFactory;

public class Controller {
	private static final int PORT=1984;
	private Socket socket;
	private PrintWriter os;
	private BufferedReader bfr;
	
	public static void main(String[] args) {
		BufferedReader br=null;
		Controller ctrl=new Controller();
		try {
			System.out.println("welcome to use James Command!");
			System.out.print("ip:");
			br=new BufferedReader(new InputStreamReader(System.in));
			String ip=br.readLine();
//			System.out.print("port:");
//			br=new BufferedReader(new InputStreamReader(System.in));
//			int port=Integer.parseInt(br.readLine());			
			ctrl.connect(ip, PORT);
			System.out.print(ip+">");
			br=new BufferedReader(new InputStreamReader(System.in));
			String cmd;
			while(!(cmd=br.readLine()).equals("exit")){
				if("".equals(cmd)){
					System.out.print(ip+">");
					continue;
				}
				if(cmd.charAt(0)=='$'){
					cmd=cmd.substring(1);
					try {
						new JCommand(ctrl.socket,cmd);
					} catch (ClassNotFoundException e) {
						System.out.println("command "+cmd+" not found!");
					}
				}
				else{
					ctrl.control(cmd);
					ctrl.displayResult();
				}
				System.out.print(ip+">");
			}	
			br.close();
			ctrl.bfr.close();
			ctrl.os.close();	
			ctrl.socket.close();
		}catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}finally{
			try {
				ctrl.socket.close();
				br.close();
				ctrl.bfr.close();
				ctrl.os.close();				
			} catch (Exception e) {
			}			
		}
	}
	
	public void connect(String ip, int port) throws Exception{
		try {
			socket=SocketFactory.getDefault().createSocket(ip, port);			
			os=new PrintWriter(socket.getOutputStream());
			bfr=new BufferedReader(new InputStreamReader(socket.getInputStream(),"GB2312"));
		} catch (UnknownHostException e) {
			throw new Exception("UnknownHost!");
		} catch (IOException e) {
			throw new Exception("connection failed!");
		}
	}
	
	public void control(String cmd){
		try {			
			os.println(cmd);
			os.flush();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Error in writing command!");
		}
	}
//	private CharsetEncoder ce=Charset.forName("GB2312").newEncoder();
	public void displayResult(){
		try {			
			String msg;
			while(!(msg=bfr.readLine()).equals("complete!")){
//				CharBuffer cb=CharBuffer.wrap(msg.toCharArray());
//				System.out.println(cb.toString());
//				ByteBuffer bb=ce.encode(cb);
//				msg=new String(bb.array(),"GB2312");
				System.out.println(msg);
//				System.out.println(new String(ce.encode(CharBuffer.wrap(msg.toCharArray())).array()));
			}
		} catch (IOException e) {
			System.out.println("Error in receive message!");
			e.printStackTrace();
		}
	}
}