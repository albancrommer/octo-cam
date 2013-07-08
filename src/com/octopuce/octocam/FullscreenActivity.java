package com.octopuce.octocam;


import java.net.InetAddress;

import org.apache.http.conn.util.InetAddressUtils;

import com.octopuce.octocam.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

/**
 * TODO : Doc
 * @author alban
 *
	

 */
public class FullscreenActivity extends OctoActivity {
	
	private static final String TAG 				= "octoCam:FullScreenActivity";
	private static final String PREFERENCES 		= "PREFERENCES";
	private static final String PREFERENCES_IP 		= "IP";
	private static final String PREFERENCES_PORT 	= "PORT";
	private static final String PREFERENCES_FORMAT	= "FORMAT";
	private static final String PREFERENCES_CODEC	= "CODEC";
	public static final String EXTRA_IP 			= "EXTRA_IP";
	public static final String EXTRA_PORT 			= "EXTRA_PORT";
	public static final String EXTRA_FORMAT			= "EXTRA_FORMAT";
	public static final String EXTRA_CODEC			= "EXTRA_CODEC";
	public static final int FORMAT_MPEGTS 			= 8;
	public static final int FORMAT_MP4 				= 2;
	public static final int FORMAT_3GPP 			= 1;
	public static final int FORMAT_MATROSKA 		= 4;
	public static final int FORMAT_WEBM 			= 5;
	public static final int FORMAT_RTP 				= 7;
	public static final int CODEC_H264		 		= 2;
	public static final int CODEC_H263				= 1;
	public static final int CODEC_MP4				= 3;

	private String mIp 								= null;
	private int mPort 								= 0;
	private int mFormat 							= 0;
	private int mCodec 								= 0;
	
	private SharedPreferences mSettings 			= null;
	private SharedPreferences.Editor mEditor;



	/**
	 * 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen);
	
		findViewById(R.id.radioBoxMPEGTS).setId(FORMAT_MPEGTS);
		findViewById(R.id.radioBoxMP4).setId(FORMAT_MP4);
		findViewById(R.id.radioBox3GPP).setId(FORMAT_3GPP);
//		findViewById(R.id.radioBoxMATROSKA).setId(FORMAT_MATROSKA);
//		findViewById(R.id.radioBoxWEBM).setId(FORMAT_WEBM);
		findViewById(R.id.radioBoxRTP).setId(FORMAT_RTP);
		findViewById(R.id.radioCodecH264).setId(CODEC_H264);
		findViewById(R.id.radioCodecH263).setId(CODEC_H263);
		findViewById(R.id.radioCodecMP4).setId(CODEC_MP4);


	}
	/**
	 * 
	 */
	@Override
	protected void onResume() {
		super.onResume();
		Log.w(TAG,"onResume ");

		RadioGroup radioGroup;

		// Reads preferences
		mSettings 				= getSharedPreferences(PREFERENCES, 0);
	    mEditor 				= mSettings.edit();
	    
		// Retrieves values if available
		mIp 					= mSettings.getString(PREFERENCES_IP, null);
		mPort 					= mSettings.getInt(PREFERENCES_PORT, 0);
		mFormat					= mSettings.getInt(PREFERENCES_FORMAT, 0);
		mCodec 					= mSettings.getInt(PREFERENCES_CODEC, 0);
		Log.i(TAG,"onResume Pref Format: "+mFormat+" Codec:"+mCodec+" Target "+mIp+":"+mPort);
	    
	    // Presets IP
		EditText ipAdressText 	= (EditText) findViewById(R.id.ipAdressText);
		if( null != mIp){
			ipAdressText.setText(mIp);
		}

	    // Presets Port
		EditText portText 		= (EditText) findViewById(R.id.portText);
		if( 0 != mPort ){
			portText.setText(Integer.toString(mPort));
		}
		
	    // Presets Format
		if( 0 == mFormat ){
			mFormat				= FORMAT_MPEGTS;
		}
		radioGroup				= (RadioGroup) findViewById(R.id.radioBoxGroup);
		radioGroup.check(mFormat);

	    // Presets Codec
		if( 0 == mCodec ){
			mCodec				= CODEC_H264;
		}
		radioGroup				= (RadioGroup) findViewById(R.id.radioCodecGroup);
		radioGroup.check(mCodec);


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
			showToast(getString(R.string.alert_ip_missing));
//			return;
		}
		
		// Checks Port
	    try{
	    	mPort 				= Integer.parseInt(portString);
	    }catch(NumberFormatException the_input_string_isnt_an_integer){
			Log.w(TAG,"Port failed "+mPort);
			showToast(getString(R.string.alert_port_missing));
	         // failed
			return;
	    }			
	    
	    // Retrieves Format
	    mFormat					= ((RadioGroup)findViewById(R.id.radioBoxGroup)).getCheckedRadioButtonId();
		Log.w(TAG,"RadioGroup Format:"+Integer.toString(mFormat));
	    
	    // Retrieves Codec
	    mCodec					= ((RadioGroup)findViewById(R.id.radioCodecGroup)).getCheckedRadioButtonId();
		Log.w(TAG,"RadioGroup Codec:"+Integer.toString(mCodec));
	    
		// saves values for future use
	    SharedPreferences.Editor mEditor = mSettings.edit();
	    mEditor.putString(PREFERENCES_IP, mIp);
	    mEditor.putInt(PREFERENCES_PORT, mPort);
	    mEditor.putInt(PREFERENCES_FORMAT, mFormat);
	    mEditor.putInt(PREFERENCES_FORMAT, mFormat);
	    mEditor.putInt(PREFERENCES_CODEC, mCodec);
	    mEditor.commit();

		Log.i(TAG,"Recorded in preferences mFormat:"+mFormat+" target "+mIp+":"+mPort);
		
	    // Start Streaming
	    intent.putExtra(EXTRA_IP, mIp); 
	    intent.putExtra(EXTRA_FORMAT, mFormat); 
	    intent.putExtra(EXTRA_PORT, mPort); 
	    intent.putExtra(EXTRA_CODEC, mCodec); 
		startActivity(intent);

	}

}
