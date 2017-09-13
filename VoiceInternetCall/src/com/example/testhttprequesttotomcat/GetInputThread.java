package com.example.testhttprequesttotomcat;

import java.io.DataInputStream;
import java.net.Socket;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class GetInputThread extends Thread{
	Socket socketThread;
	MainActivity parent;
	
	GetInputThread(Socket so,final MainActivity parent){
		socketThread = so;
		this.parent = parent;
	}
	public void run(){
		try{
			DataInputStream in = new DataInputStream(socketThread.getInputStream());
			String response = in.readLine();
			String callFrom = in.readLine();
			Log.e("> Response GetInput :" , response);
			Log.e("> Call From :" , callFrom);
			if(response.equals(MainActivity.CALLING)){
				parent.dialog.setConfiguration(callFrom, this);
				parent.handlerShowDialog.sendEmptyMessage(0);
				//startConverstion();
			}
		}catch(Exception ex){ex.printStackTrace();}
	}
	
	public void startConverstion(){
		parent.buttonEndCallEnabled(true);
		new ReceiveAndPlay(socketThread, parent).start();
		new RecordAndSend(socketThread, parent).start();
	}
	
}