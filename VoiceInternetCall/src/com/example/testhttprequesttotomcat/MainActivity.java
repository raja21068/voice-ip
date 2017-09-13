package com.example.testhttprequesttotomcat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import dbhandler.*;

import android.R.color;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends Activity {

	public static DatabaseHandler db;
	public Button regBtn , buttonCall, buttonEndCall;
	public EditText regField , callNoField;
	public Spinner spinnerContacts;
	
	public static final int FREQUENCY = 8000;
//	public static final int OTHERS_CLIENT_PORT = 9999;
	public static final int SERVER_PORT = 8000;
	
	public static final String CALL_STATUS = "CALL";
    public static final String REGISTER = "REG";
    public static final String CALLING = "1";
    public static final String INVALID = "0";
    
    public static  String SERVER_IP = "192.168.1.3";
	
    Socket socket;    
	Thread serverThread;
	
	public Handler handlerEnabler , buttonCallHandler, buttonEndHandler;
	public Handler handlerShowDialog , handlerShowSimDialog;
	GetInputThread inputThread ;
	RegisterThread register;
	MainActivity thisClass;
	
	public CallDialog dialog;
	public Builder dialogSimCall;
	Intent callIntent;
	
	boolean callEnable = false, endEnable = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		thisClass = this;
		dialog = new CallDialog(MainActivity.this);
		regBtn = (Button)findViewById(R.id.buttonRegister);
		buttonCall = (Button)findViewById(R.id.buttonCall);
		buttonEndCall = (Button)findViewById(R.id.buttonEndCall);
		regField = (EditText)findViewById(R.id.regNumberField);
		callNoField = (EditText)findViewById(R.id.editTextCallNumber);
		spinnerContacts = (Spinner)findViewById(R.id.spinnerContacts);
		db=new DatabaseHandler(this);
		dialogSimCall = new AlertDialog.Builder(this)
	    	.setIcon(android.R.drawable.ic_dialog_alert)
	    	.setTitle("GSM Call")
	    	.setMessage("Are you sure you want to call from sim?")
	    	.setPositiveButton("Yes", new DialogInterface.OnClickListener()
	    	{
	    		@Override
	    		public void onClick(DialogInterface dialog, int which) {
	    			startActivity(callIntent);    
	    		}
	
	    	})
    		.setNegativeButton("No", null);
		try{
			ContactBean bean = db.getContactBean();
			if(bean != null){
				regField.setText(bean.getPhoneNumber());
				register();
			}
		}catch(Exception ex){ex.printStackTrace();}
		new ContactsTask().execute();
		spinnerContacts.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				try{
					String str = (String)spinnerContacts.getSelectedItem();
					String number = str.substring(str.lastIndexOf("(") +1, str.length()-1);
					callNoField.setText(number.replaceAll("-", ""));
					callNoField.setText(number.replaceAll(" ", ""));
				}catch(Exception ex){ex.printStackTrace();}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
		
		handlerEnabler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
//				buttonCall.setEnabled(true);
				callNoField.setEnabled(true);
				regField.setEnabled(false);
				regBtn.setEnabled(false);
//				buttonEndCall.setEnabled(true);
			}	
		};
		
		buttonCallHandler = new Handler(){
			@Override
			public void handleMessage(Message msg){
				buttonCall.setEnabled(callEnable);
				if(callEnable){
					buttonCall.setBackgroundColor(getResources().getColor(R.color.greenColor));
				}else{
					buttonCall.setBackgroundColor(color.background_light);
				}
				buttonEndCall.setEnabled(!callEnable);
				if(!callEnable){
					buttonEndCall.setBackgroundColor(getResources().getColor(R.color.redColor));
				}else{
					buttonEndCall.setBackgroundColor(color.background_light);
				}
			}
		};
		buttonEndHandler = new Handler(){
			@Override
			public void handleMessage(Message msg){
				buttonEndCall.setEnabled(endEnable);
				if(endEnable){
					buttonEndCall.setBackgroundColor(getResources().getColor(R.color.redColor));
				}else{
					buttonEndCall.setBackgroundColor(color.background_light);
				}
				buttonCall.setEnabled(!endEnable);
				if(!endEnable){
					buttonCall.setBackgroundColor(getResources().getColor(R.color.greenColor));
				}else{
					buttonCall.setBackgroundColor(color.background_light);
				}
			}
		};
		
		handlerShowDialog = new Handler(){
			@Override
			public void handleMessage(Message msg){
				dialog.show();
			}
		};
		
		handlerShowSimDialog = new Handler(){
			@Override
			public void handleMessage(Message msg){
				dialogSimCall.show();
			}
		};
		Log.e("Main", "reached");
		
		regBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				register();
			}
		});
		
		buttonCall.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new Thread(){
					public void run(){
						String number = callNoField.getText().toString();
						if(number.trim().equals(""))return;
						try{
							if(number.trim().equals(""))return;
							socket = new Socket(SERVER_IP,SERVER_PORT);
							DataOutputStream out = new DataOutputStream(socket.getOutputStream());
							DataInputStream in = new DataInputStream(socket.getInputStream());
							out.writeBytes(CALL_STATUS+"\n");
							out.writeBytes(number+"\n");
							out.writeBytes(regField.getText().toString()+"\n");
							// RESPONSE -> INVALID : CALLING
							// INVALID => NUMBER NOT FOUND IN HASHTABLE
							// CALLING => FOUND NUMBER AND GOING CALL
							String response = in.readLine();
							buttonEndCallEnabled(true);
							if(response.equals(CALLING)){
								// accept: ACCEPTED : DECLINED
								String accept = in.readLine();
								if(accept.equalsIgnoreCase("ACCEPTED")){
									Log.e("MainActivity", "Accepted Call");
									new ReceiveAndPlay(socket, thisClass).start();
									new RecordAndSend(socket, thisClass).start();
									//buttonEndCallEnabled(true);
								}else{
									Log.e("MainActivity", "Declined Call");
									register();
								}
							}else{
								// other client is not online
								Log.e("MainActivity", "Not Online / Invalid");
								makeCall(number);
							}
							
						}catch(Exception ex){
							makeCall(number);
							ex.printStackTrace();
						}
					}
				}.start();
				
			}
		});
		
		buttonEndCall.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				try{
					register();
				}catch(Exception ex){ex.printStackTrace();}
			}
		});

		Toast.makeText(getApplicationContext(),"On cReate", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void register(){
		try{
			db.addContact("",regField.getText().toString() ,"");
			if(socket !=null)socket.close();
			if(register.socket !=null)register.socket.close();
		}catch(Exception ex){Log.e("MainActivity", "Socket already closed");}
		register = new RegisterThread(regField.getText().toString(),socket,handlerEnabler,inputThread, thisClass);
		register.start();

	}
	
	public void buttonCallEnabled(boolean b){
		callEnable = b;
		buttonCallHandler.sendEmptyMessage(0);
	}
	
	public void buttonEndCallEnabled(boolean b){
		endEnable = b;
		buttonEndHandler.sendEmptyMessage(0);
	}
	
	
	private void makeCall(String number){
		try{
			Log.e("Main","From Sim Call");
			callIntent = new Intent(Intent.ACTION_CALL);
			callIntent.setData(Uri.parse("tel:"+number));
			handlerShowSimDialog.sendEmptyMessage(0);
//			startActivity(callIntent);
		}catch(Exception e){e.printStackTrace();}
	}
	
	
	private class ContactsTask extends AsyncTask<Void,Void,ArrayList<String>>{
		@Override
		protected ArrayList<String> doInBackground(Void... params) {
			ArrayList<String> adapter = new ArrayList();
			
			String phoneNumber = null;

			Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
			String _ID = ContactsContract.Contacts._ID;
			String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
			String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

			Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
			String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
			String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

			ContentResolver contentResolver = getContentResolver();

			Cursor cursor = contentResolver.query(CONTENT_URI, null,null, null, null);	

			// Loop for every contact in the phone
			if (cursor.getCount() > 0) {

				while (cursor.moveToNext()) {
					StringBuffer output = new StringBuffer();
					String contact_id = cursor.getString(cursor.getColumnIndex( _ID ));
					String name = cursor.getString(cursor.getColumnIndex( DISPLAY_NAME ));
					int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex( HAS_PHONE_NUMBER )));

					if (hasPhoneNumber > 0) {

						output.append(name);

						// Query and loop for every phone number of the contact
						Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[] { contact_id }, null);

						while (phoneCursor.moveToNext()) {
							phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
							output.append("("+phoneNumber+")");
						}
						phoneCursor.close();
					}
					adapter.add(output.toString());
				}//end while
			}
			return adapter;
		}
		
		@Override
		protected void onPostExecute(ArrayList<String> result) {
			// SETTING CONTACTS TO SPINNER
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(thisClass,
					android.R.layout.simple_spinner_item, result);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinnerContacts.setAdapter(dataAdapter);
			
			super.onPostExecute(result);
		}
		
	} 
	
	
	/* SERVER CODING OUT OF WORKING 
//	 **/
//	
//	private class ServerThread implements Runnable{
//		@Override
//		public void run() {
//			try{
//				ServerSocket server= new ServerSocket(OTHERS_CLIENT_PORT);
//				Log.e("MainActivityVoip", "waiting.."+OTHERS_CLIENT_PORT);
//				Socket client = server.accept();
////				dialogCall = new CallDialog(getApplicationContext(), "");
//				new ReceiveAndPlay(client,thisClass).start();
//				new RecordAndSend(client, thisClass).start();
//			}catch(Exception ex){ex.printStackTrace();}
//		}		
//	}
//	
}
