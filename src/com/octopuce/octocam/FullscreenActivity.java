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
import android.widget.RadioGroup;

/**
 * TODO : Doc
 * @author alban
 *
public static final int 	AAC_ADIF 	5
public static final int 	AAC_ADTS 	6
public static final int 	AMR_NB 	3
public static final int 	AMR_WB 	4
public static final int 	DEFAULT 	0
public static final int 	MPEG_4 	2
public static final int 	OUTPUT_FORMAT_MPEG2TS 	8
public static final int 	OUTPUT_FORMAT_RTP_AVP 	7
public static final int 	RAW_AMR 	3
public static final int 	THREE_GPP 	1

    <string name="radio_mpegts">MPEGTS</string>
    <string name="radio_mp4">MP4</string>
    <string name="radio_3gpp">3GPP</string>
    <string name="radio_matroska">Matroska</string>
    <string name="radio_webm">WebM</string>
    <string name="radio_rtp">RTP AVP</string>
 */
public class FullscreenActivity extends Activity {
	
	private static final String TAG 				= "octoCam:FullScreenActivity";
	private static final String PREFERENCES 		= "PREFERENCES";
	private static final String PREFERENCES_IP 		= "IP";
	private static final String PREFERENCES_PORT 	= "PORT";
	private static final String PREFERENCES_FORMAT	= "FORMAT";
	public static final String EXTRA_IP 			= "EXTRA_IP";
	public static final String EXTRA_PORT 			= "EXTRA_PORT";
	public static final String EXTRA_FORMAT			= "EXTRA_FORMAT";
	public static final int FORMAT_MPEGTS 			= 8;
	public static final int FORMAT_MP4 				= 2;
	public static final int FORMAT_3GPP 			= 1;
	public static final int FORMAT_MATROSKA 		= 4;
	public static final int FORMAT_WEBM 			= 5;
	public static final int FORMAT_RTP 				= 7;

	private String mIp = null;
	private int mPort = 0;
	private int mFormat = 0;
	private SharedPreferences mSettings = null;
	private SharedPreferences.Editor mEditor;



	/**
	 * 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen);
	
		findViewById(R.id.radioMPEGTS).setId(FORMAT_MPEGTS);
		findViewById(R.id.radioMP4).setId(FORMAT_MP4);
		findViewById(R.id.radio3GPP).setId(FORMAT_3GPP);
//		findViewById(R.id.radioMATROSKA).setId(FORMAT_MATROSKA);
//		findViewById(R.id.radioWEBM).setId(FORMAT_WEBM);
		findViewById(R.id.radioRTP).setId(FORMAT_RTP);

	}
	/**
	 * 
	 */
	@Override
	protected void onResume() {
		super.onResume();
		Log.w(TAG,"onResume ");

		// Reads preferences
		mSettings 				= getSharedPreferences(PREFERENCES, 0);
	    mEditor 				= mSettings.edit();
	    
		// retrieve values if available
		mIp 					= mSettings.getString(PREFERENCES_IP, null);
		mPort 					= mSettings.getInt(PREFERENCES_PORT, 0);
		mFormat					= mSettings.getInt(PREFERENCES_FORMAT, 0);
		Log.i(TAG,"onResume Pref Format: "+mFormat+" Target "+mIp+":"+mPort);
	    
		EditText ipAdressText 	= (EditText) findViewById(R.id.ipAdressText);
		EditText portText 		= (EditText) findViewById(R.id.portText);

		if( null != mIp){
			ipAdressText.setText(mIp);
		}
		if( 0 != mPort ){
			portText.setText(Integer.toString(mPort));
		}
		if( 0 == mFormat ){
			mFormat				= FORMAT_MPEGTS;
		}
		RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
		radioGroup.check(mFormat);

	}
	
	
	/** 
	 * when the user clicks the Send to Network button 
	 */
	public void sendNet(View view) {
		
		Intent intent 			= new Intent(this, NetworkActivity.class);
		mSettings 				= getSharedPreferences(PREFERENCES, 0);
		
		// Retrieves IP & PORT values from form
		EditText ipAdressText 	= (EditText) findViewById(R.id.ipAdressText);
		mIp 					= ipAdressText.getText().toString();
		EditText portText 		= (EditText) findViewById(R.id.portText);
		String portString		= portText.getText().toString();

	    // Checks IP
		if( ! InetAddressUtils.isIPv4Address(ipAdressText.toString())
		 && ! InetAddressUtils.isIPv6Address(ipAdressText.toString())
		){
			Log.w(TAG,"ipAdress failed "+mIp);
//			return;
		}
		
		// Checks Port
	    try{
	    	mPort 				= Integer.parseInt(portString);
	    }catch(NumberFormatException the_input_string_isnt_an_integer){
			Log.w(TAG,"Port failed "+mPort);
	         // failed
			return;
	    }			
	    
	    // Retrieves Format
	    mFormat					= ((RadioGroup)findViewById(R.id.radioGroup)).getCheckedRadioButtonId();
		Log.w(TAG,"RadioGroup "+Integer.toString(mFormat));
	    
		// saves values for future use
	    SharedPreferences.Editor mEditor = mSettings.edit();
	    mEditor.putString(PREFERENCES_IP, mIp);
	    mEditor.putInt(PREFERENCES_PORT, mPort);
	    mEditor.putInt(PREFERENCES_FORMAT, mFormat);
	    mEditor.commit();

		Log.i(TAG,"Recorded in preferences mFormat:"+mFormat+" target "+mIp+":"+mPort);
		
	    // Start Streaming
	    intent.putExtra(EXTRA_IP, mIp); 
	    intent.putExtra(EXTRA_FORMAT, mFormat); 
	    intent.putExtra(EXTRA_PORT, mPort); 
		startActivity(intent);

	}
	
}
