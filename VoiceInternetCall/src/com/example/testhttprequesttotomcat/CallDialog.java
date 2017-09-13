package com.example.testhttprequesttotomcat;

import java.io.DataOutputStream;

import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CallDialog extends Dialog{

	Button buttonAccept , buttonDecline;
	TextView textViewNumber;
	
	GetInputThread callingThread;
	String number = "";
	Context context;
	MediaPlayer player;
	public CallDialog(Context context) {
		super(context);
		setContentView(R.layout.dialog_call);
		this.context = context;
	}

	public void setConfiguration(String number ,GetInputThread thread){
		this.callingThread = thread;
		this.number = number;
	}
	
	@Override
	public void show(){
		try{
			textViewNumber = (TextView)findViewById(R.id.textViewNumber);
			textViewNumber.setText(number);
			buttonAccept = (Button)findViewById(R.id.buttonAccept);
			buttonDecline = (Button)findViewById(R.id.buttonDecline);
			player = MediaPlayer.create(context, R.raw.ring);
			player.start();
			buttonAccept.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					try{
						DataOutputStream out = new DataOutputStream(callingThread.socketThread.getOutputStream());
						out.writeBytes("ACCEPTED\n");
						callingThread.startConverstion();
						dismiss();
						player.stop();
					}catch(Exception ex){
						ex.printStackTrace();
					}
				}
			});
			
			buttonDecline.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					try{
						DataOutputStream out = new DataOutputStream(callingThread.socketThread.getOutputStream());
						out.writeBytes("REJECTED\n");
						callingThread.parent.register();
						dismiss();
						player.stop();
					}catch(Exception ex){
						ex.printStackTrace();
					}
				}
			});
			super.show();
			
			
		}catch(Exception ex){ex.printStackTrace();}
	}
	
}
