package com.example.octomanager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.provider.MediaStore.Images;

public class NetworkActivity extends Activity implements SocketListenerInterface{
	
	private Socket sock;
	private BufferedReader r;
	private BufferedWriter out;
	private Thread thrd;
	private ConnectionHandler conhandler;
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_network);

		// Make sure we're running on Honeycomb or higher to use ActionBar APIs
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// Show the Up button in the action bar.
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		// Get the message from the intent
		Intent intent = getIntent();
		String message = intent.getStringExtra(FullscreenActivity.EXTRA_MESSAGE);
		
		// Create the text view
		TextView textView = new TextView(this);
		textView.setTextSize(40);
		textView.setText(message);

		// Set the text view as the activity layout
		setContentView(textView);   
		

  }
	
	public void onSocketReady(){
		
		conhandler.send( "NetworkActivity::onSocketReady".getBytes()); 
		Log.i("NetworkActivity:onSocketReady","Load");
		Bitmap bMap = BitmapFactory.decodeFile("/sdcard/test.jpg");

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bMap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		byte[] byteArray = stream.toByteArray();
		Log.i("NetworkActivity:onSocketReady","sending bitmap");
		conhandler.send(byteArray); 
		
	}
  @Override
  public void onResume() {
    super.onResume();
    
	// Write to socket
	try{
	    conhandler = new ConnectionHandler();
	    conhandler.execute();
	    conhandler.addListener(this);
		
	}catch(Exception e){
		Log.w("NetworkActivity","onResume:Exception "+e);
	}
    

    }
 
  @Override
  public void onPause() {
    super.onPause();

    conhandler.cancel(true);
    conhandler = null;
    
  }
 

}

