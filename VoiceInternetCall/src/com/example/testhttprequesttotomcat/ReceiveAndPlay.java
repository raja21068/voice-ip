package com.example.testhttprequesttotomcat;

import java.io.DataInputStream;
import java.net.Socket;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.util.Log;

public class ReceiveAndPlay extends Thread{
	Socket socket;
	byte[] buffer ;
	MainActivity parent;
	AudioTrack at;
	public ReceiveAndPlay(Socket socket , MainActivity parent){
		this.socket = socket;		
		this.parent = parent;
	}
	@Override
	public void run() {
		try{
			final int bufferSizeForOutput = AudioTrack.getMinBufferSize(MainActivity.FREQUENCY, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT);
		
			Log.e("> BufferOutput ", ""+bufferSizeForOutput);
			byte[] buffer = new byte[bufferSizeForOutput];
			//AudioTrack at = new AudioTrack(AudioManager.ROUTE_HEADSET, MainActivity.FREQUENCY,AudioFormat.CHANNEL_CONFIGURATION_MONO,MediaRecorder.AudioEncoder.AMR_NB, bufferSizeForOutput,AudioTrack.MODE_STREAM);
			at = new AudioTrack(AudioManager.STREAM_VOICE_CALL, MainActivity.FREQUENCY,AudioFormat.CHANNEL_CONFIGURATION_MONO,MediaRecorder.AudioEncoder.AMR_NB, bufferSizeForOutput,AudioTrack.MODE_STREAM);
			at.setPlaybackRate(MainActivity.FREQUENCY);
			
			Log.e("MainActivityVoip", "Accepted..");
			DataInputStream in = new DataInputStream(socket.getInputStream());
			Log.e("MainActivityVoip", "Accepted..");
			at.play();
			while(true){
				int bytesReaded = in.read(buffer, 0, bufferSizeForOutput);
				at.write(buffer, 0, bytesReaded);
				//at.flush();
			}
		}catch(Exception ex){
			at.release();
			parent.register();
			ex.printStackTrace();
		}
	}
}