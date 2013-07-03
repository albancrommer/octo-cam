package com.example.octocam;


import com.octopuce.octocam.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * TODO : Doc
 * @author alban
 *
 */
public class FullscreenActivity extends Activity {
	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

	  
	/**
	 * 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen);

	}
	/** 
	 * when the user clicks the Send to Network button 
	 */
	public void sendNet(View view) {
		Intent intent = new Intent(this, NetworkActivity.class);
		startActivity(intent);

	}
	
}
