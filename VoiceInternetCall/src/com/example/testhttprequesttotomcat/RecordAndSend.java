package com.example.testhttprequesttotomcat;

import java.io.DataOutputStream;
import java.net.Socket;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.util.Log;

public class RecordAndSend extends Thread{
	Socket socket;
	int bufferSize;
	AudioRecord ar;
	byte[] buffer ;
	MainActivity parent;
	public RecordAndSend(Socket socket, MainActivity parent){
		this.socket = socket;
		bufferSize = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT);
		
		Log.e("Size: ", ""+bufferSize);
		ar = new AudioRecord(MediaRecorder.AudioSource.MIC,8000,AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
		buffer = new byte[bufferSize];
		this.parent = parent;
	}
	@Override
	public void run() {
		try{
			ar.startRecording();
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			while(true){
				ar.read(buffer, 0, bufferSize);
				out.write(buffer, 0, bufferSize);
				out.flush();
			}
		}catch(Exception ex){ 
			ar.release();
			parent.register();
			ex.printStackTrace();
		}
	}
}
