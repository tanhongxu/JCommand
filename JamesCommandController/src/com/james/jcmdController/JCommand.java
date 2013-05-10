package com.james.jcmdController;

import java.net.Socket;

public class JCommand {
	
	public JCommand(){
		
	}
	
	public JCommand(Socket socket,String cmd) throws Exception{
		Class.forName("com.james.jcmdController.command."+cmd).getConstructor(Socket.class).newInstance(socket);
		
	}

	public void exec(){
		
	}
}
