package com.example.octomanager;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class FullscreenActivity extends Activity {
	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

	  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen);

	}
	/** when the user clicks the Send to Network button */
	public void sendNet(View view) {
		Intent intent = new Intent(this, NetworkActivity.class);
		EditText editText = (EditText) findViewById(R.id.edit_message);
		String message =  editText.getText().toString();
		intent.putExtra(EXTRA_MESSAGE, message);
		startActivity(intent);

	}
	
	/** Called when the user clicks the Send button */
	public void sendMessage(View view) {
		Intent intent = new Intent(this, DisplayMessageActivity.class);
		EditText editText = (EditText) findViewById(R.id.edit_message);
		String message = editText.getText().toString();
		intent.putExtra(EXTRA_MESSAGE, message);
		startActivity(intent);
	}
}
