package com.james.jcmdController;

import java.net.Socket;


public class Help extends JCommand{
	private String[] commands={"Jtransfer","Jtalk","Help"};
	
	public Help(Socket socket){
		System.out.println("available command:");
		for(String command:commands)
			System.out.println("\t"+command);
	}
}
