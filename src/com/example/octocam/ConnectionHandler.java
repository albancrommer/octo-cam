package com.example.octocam;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

import android.os.AsyncTask;
import android.util.Log;

/**
 * TODO: DOC
 * @author alban
 *
 */
public class ConnectionHandler extends AsyncTask<Void, Void, Void>{
	
	private static final String TAG = "OctoCam:ConHandler";
	//TODO: If you could put that in a DB, that'd be great ;)
	public static String serverip = "91.194.61.201";
	public static int serverport = 12345;
	Socket mSocket;
	public InputStream mInputStream;
	public OutputStream mOutputStream;
	protected ArrayList<SocketListenerInterface> listenersList = new ArrayList<SocketListenerInterface>();
	
	/**
	 * 
	 */
	@Override
	protected Void doInBackground(Void... params) {
		
		if( null != mSocket && mSocket.isConnected()){
			Log.w(TAG, "doInBackground: already connected");
			return null;
		}else{
			Log.w(TAG, "doInBackground: connecting");
		}
	    try {
	        Log.i(TAG, "doInBackground: Creating Socket");
	        mSocket = new Socket(serverip, serverport);
	    } catch (Exception e) {
	        Log.e(TAG, "doInBackground: Cannot create Socket",e);
	        return null;
	    }
	    if (mSocket.isConnected()) {
	        try {
	            Log.i(TAG, "doInBackground: do InputStream");
	        	mInputStream = mSocket.getInputStream();
	            Log.i(TAG, "doInBackground: doOutputStream");
	            mOutputStream = mSocket.getOutputStream();
	            Log.i(TAG, "doInBackground: Socket created, Streams assigned");
	
	        } catch (Exception e) {
	            Log.i(TAG, "doInBackground: Cannot assign Streams, Socket not connected");
	            e.printStackTrace();
	    	    return null;
	        }
	    } else {
	        Log.i(TAG, "doInBackground: Cannot assign Streams, Socket is closed");
		    return null;
	    }
        Log.i(TAG, "Socket ready");
        onReady();
	    return null;
	}
	
	/**
	 * 
	 */
	@Override
	protected void onCancelled() {

		try {
			mSocket.close();
			mSocket = null;
		} catch (IOException e) {
			Log.e(TAG, "onCancelled Failed to close socket");
			e.printStackTrace();
			return;
		}
		Log.i(TAG, "onCancelled Closed socket successfully");
	}
			

	/**
	 * 
	 * @param listener
	 */
	public void addListener (SocketListenerInterface listener){
		listenersList.add(listener);
	}	
	
	/**
	 * 
	 */
	private void onReady() {
		// signal availability to listeners
		 for( Iterator<SocketListenerInterface> itr = listenersList.iterator(); itr.hasNext(); ){
			 SocketListenerInterface theListener = (SocketListenerInterface) itr.next();
			 theListener.onSocketReady(); 
		 }
	}
	/**
	 * 
	 * @return
	 */
	public Socket getSocket(){
		return mSocket;
	}
	
}