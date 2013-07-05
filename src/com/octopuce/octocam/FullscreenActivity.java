package com.octopuce.octocam;


import java.net.InetAddress;

import org.apache.http.conn.util.InetAddressUtils;

import com.octopuce.octocam.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

/**
 * TODO : Doc
 * @author alban
 *
 */
public class FullscreenActivity extends Activity {
	
	private static final String TAG 				= "octoCam:FullScreenActivity";
	private static final String PREFERENCES 		= "PREFERENCES";
	private static final String PREFERENCES_IP 		= "IP";
	private static final String PREFERENCES_PORT 	= "PORT";
	public static final String EXTRA_IP 			= "EXTRA_IP";
	public static final String EXTRA_PORT 			= "EXTRA_PORT";
	private String mIp = null;
	private int mPort = 0;
	private SharedPreferences mSettings = null;
	private SharedPreferences.Editor mEditor;

	  
	/**
	 * 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen);


	}
	/**
	 * 
	 */
	@Override
	protected void onResume() {
		super.onResume();
		Log.w(TAG,"onResume ");

		mSettings 				= getSharedPreferences(PREFERENCES, 0);
	    mEditor 				= mSettings.edit();
//	    mEditor.clear();
//	    mEditor.commit();
	    
		// retrieve values if available
		mIp 					= mSettings.getString(PREFERENCES_IP, null);
		mPort 					= mSettings.getInt(PREFERENCES_PORT, 0);

		Log.i(TAG,"onResume Pref Target "+mIp+":"+mPort);
	    
		EditText ipAdressText 	= (EditText) findViewById(R.id.ipAdressText);
		EditText portText 		= (EditText) findViewById(R.id.portText);

		if( null != mIp){
			Log.i(TAG,"onResume set IP text "+mIp);
			ipAdressText.setText(mIp);
		}
		if( 0 != mPort ){
			Log.i(TAG,"onResume set Port text "+mPort);
			
			portText.setText(Integer.toString(mPort));
			
		}

	}
	/** 
	 * when the user clicks the Send to Network button 
	 */
	public void sendNet(View view) {
		
		Intent intent 			= new Intent(this, NetworkActivity.class);
		mSettings 				= getSharedPreferences(PREFERENCES, 0);
		
		// Retrieves values from form
		EditText ipAdressText 	= (EditText) findViewById(R.id.ipAdressText);
		mIp 					= ipAdressText.getText().toString();
		Log.w(TAG,"ipAdress "+mIp);
		EditText portText 		= (EditText) findViewById(R.id.portText);
		String portString		= portText.getText().toString();
		Log.w(TAG,"port "+portString);
		
		// Checks IP
		if( ! InetAddressUtils.isIPv4Address(ipAdressText.toString())
		 && ! InetAddressUtils.isIPv6Address(ipAdressText.toString())
		){
			Log.w(TAG,"ipAdress failed "+mIp);
//			return;
		}
		
		// Checks Port
	    try{
	    	mPort = Integer.parseInt(portString);
	    }catch(NumberFormatException the_input_string_isnt_an_integer){
			Log.w(TAG,"Port failed "+mPort);
	         // failed
			return;
	    }			
	    
		// saves values for future use
	    SharedPreferences.Editor mEditor = mSettings.edit();
	    mEditor.putString(PREFERENCES_IP, mIp);
	    mEditor.putInt(PREFERENCES_PORT, mPort);
	    mEditor.commit();

		Log.i(TAG,"Recorded in preferences target "+mIp+":"+mPort);
		
	    // Start Streaming
	    intent.putExtra(EXTRA_IP, mIp); 
	    intent.putExtra(EXTRA_PORT, mPort); 
		startActivity(intent);

	}
	
}
