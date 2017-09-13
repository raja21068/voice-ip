package com.example.testhttprequesttotomcat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import android.os.Handler;
import android.util.Log;

public class RegisterThread extends Thread{
	String myNumber;
	public Socket socket;
	Handler handlerEnabler;
	Thread inputThread;
	MainActivity parent;
	public RegisterThread(String num , Socket socket , Handler handlerEnabler , Thread inputThread,MainActivity parent){
		myNumber = num;
		this.socket = socket;
		this.handlerEnabler = handlerEnabler;
		this.inputThread = inputThread;
		this.parent = parent;
	}	
	public void run(){
		try{	
			if(!myNumber.trim().equals("")){
				socket = new Socket(MainActivity.SERVER_IP,MainActivity.SERVER_PORT);
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());
				out.write((MainActivity.REGISTER+"\n").getBytes());
				out.write((myNumber+"\n").getBytes());
				
				DataInputStream in = new DataInputStream(socket.getInputStream());
				String response = in.readLine();
				Log.e(">Response", response);
				if(response.contains("OK")){
					handlerEnabler.sendEmptyMessage(0);
					parent.buttonCallEnabled(true);

					if(inputThread !=null){inputThread = null;}
					inputThread = new GetInputThread(socket, parent);
					inputThread.start();
				}
			}
		}catch(Exception ex){ex.printStackTrace();}
	}
}