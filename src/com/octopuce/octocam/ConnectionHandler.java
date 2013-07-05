package com.octopuce.octocam;

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
	
	private static final String TAG = "OctoCam:ConnectionHandler";
	//TODO: If you could put that in a DB, that'd be great ;)
	private static String mIp 	= "";
	private static int mPort 	= 0;
	private Socket mSocket;
//	public InputStream mInputStream;
//	public OutputStream mOutputStream;
	protected ArrayList<SocketListenerInterface> listenersList = new ArrayList<SocketListenerInterface>();
	
	
	public ConnectionHandler(String ip, int port) {
		mIp  				= ip;
		mPort				= port;
		Log.w(TAG, "constructor: ip"+mIp+" port"+mPort);
	}
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
	        mSocket = new Socket(mIp, mPort);
	    } catch (Exception e) {
	        Log.e(TAG, "doInBackground: Cannot create Socket",e);
	        return null;
	    }
	    if (mSocket.isConnected()) {
	    	Log.i(TAG, "doInBackground: Socket created");
//	        try {
//	            Log.i(TAG, "doInBackground: do InputStream");
//	        	mInputStream = mSocket.getInputStream();
//	            Log.i(TAG, "doInBackground: doOutputStream");
//	            mOutputStream = mSocket.getOutputStream();
//	            Log.i(TAG, "doInBackground: Socket created, Streams assigned");
//	
//	        } catch (Exception e) {
//	            Log.i(TAG, "doInBackground: Cannot assign Streams, Socket not connected");
//	            e.printStackTrace();
//	    	    return null;
//	        }
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
	protected void closeSocket() {

		try {
			mSocket.close();
			mSocket = null;
		} catch (IOException e) {
			Log.e(TAG, "Failed to close socket");
			e.printStackTrace();
			return;
		}
		Log.i(TAG, "Closed socket successfully");
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