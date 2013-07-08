package com.octopuce.octocam;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * 
 * @author alban
 *
 */
public class OctoActivity extends Activity {
	/**
	 * 
	 * @param sMessage
	 */
	public void showToast( String sMessage ){
	
		Context context 	= getApplicationContext();
		CharSequence text 	= sMessage;
		int duration 		= Toast.LENGTH_LONG;
		Toast toast 		= Toast.makeText(context, text, duration);
		toast.setGravity(Gravity.TOP|Gravity.LEFT, 10, 11);		
		toast.show();

	}
}
