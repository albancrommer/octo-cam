package com.octopuce.octocam;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.hardware.Camera;
import com.octopuce.octocam.R;
/**
 * TODO: DOC
 * @author alban
 *
 */
public class NetworkActivity extends Activity implements SocketListenerInterface{
	
	private static final String TAG = "octoCam:NetworkActivity";
	private ConnectionHandler conhandler;
    private Camera mCamera;
    private CameraPreview mPreview;
    private MediaRecorder mMediaRecorder;
    private boolean isRecording = false;
   
    private Socket mSocket;
	private String mIp;
	private int mPort;

    
	@SuppressLint("NewApi")
	@Override
	/**
	 * 
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_network);
		
	    Bundle bundle 		= getIntent().getExtras();
	    mIp 				= getIntent().getStringExtra(FullscreenActivity.EXTRA_IP);
	    mPort				= getIntent().getIntExtra(FullscreenActivity.EXTRA_PORT,0);

		Log.i(TAG,"onCreate Extra Target "+mIp+":"+mPort);
		
        // Create an instance of Camera
        mCamera 			= getCameraInstance();
        // Create our Preview view and set it as the content of our activity.
        mPreview 			= new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
	     // Add a listener to the Capture button
	     Button captureButton = (Button) findViewById(R.id.button_capture);
	     captureButton.setOnClickListener(
	         new View.OnClickListener() {
	        	 //TODO : Check if socket is ready
	             @Override
	             public void onClick(View v) {
	            	 Log.i(TAG,"onCreate!onClick");
	            	 try{
		                 if (isRecording) {
		                     // stop recording and release camera
		                     mMediaRecorder.stop();  // stop the recording
		                     releaseMediaRecorder(); // release the MediaRecorder object
		                     isRecording 		= false;
		                 } else {
		                     // initialize video camera
		                     if (prepareVideoRecorder()) {
		                         // Camera is available and unlocked, MediaRecorder is prepared,
		                         // now you can start recording
		                         mMediaRecorder.start();
		                         isRecording = true;
		                     } else {
		                         // prepare didn't work, release the camera
		                         releaseMediaRecorder();
		                     }
		                 }
	            	 }catch(Exception e){
	            		 e.printStackTrace();
	            	 }
	            	 Log.i(TAG,"onCreate!onClick isRecording:"+(isRecording?"yes":"no"));
	             }
	         }
	     ); // End button callback
	}
	
	@Override
	/**
	 * 
	 */
	public void onResume() {
		super.onResume();
		Log.i(TAG,"onResume:init");
		try{
			// Connects to socket
			conhandler = new ConnectionHandler(mIp,mPort);
		    conhandler.execute();
		    conhandler.addListener(this);
		}catch(Exception e){
			Log.w("NetworkActivity","onResume:Exception "+e);
		}
    }
 
	@Override
	/**
	 * 
	 */
	public void onPause() {
		super.onPause();
		Log.i(TAG,"onPause:init");
		try{
			conhandler.closeSocket();
//		    conhandler.cancel(true);
//		    conhandler = null;
		    releaseMediaRecorder();       // if you are using MediaRecorder, release it first
		    releaseCamera();              // release the camera immediately on pause event
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @return
	 */
	private boolean prepareVideoRecorder(){
		Log.i( TAG, "prepareVideoRecorder:init");
	    mCamera = getCameraInstance();
	    if( null == mCamera){
	    	Log.e(TAG,"prepareVideoRecorder:: can't get camera");
	    	return false;
	    }
	    
	    if( null == mSocket){
	    	Log.e(TAG,"prepareVideoRecorder:: can't get socket");
	    	
	    	// TODO : Attempt to open a new socket
	    	return false;
	    }
	    mMediaRecorder = new MediaRecorder();

	    // Step 1: Unlock and set camera to MediaRecorder
	    mCamera.unlock();
	    mMediaRecorder.setCamera(mCamera);

	    // Step 2: Set sources
	    mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
	    mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

	    // Step 3: Set Recorder settings (old API style)
	    mMediaRecorder.setOutputFormat(8); // MPEG-TS
	    mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
	    mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
		mMediaRecorder.setVideoSize(1280, 720);
		mMediaRecorder.setVideoEncodingBitRate(2000000);
		mMediaRecorder.setVideoFrameRate(30);
		mMediaRecorder.setAudioEncodingBitRate(8000);
		mMediaRecorder.setAudioChannels(1);
		mMediaRecorder.setAudioSamplingRate(8000);

	    // Step 4: Set output file
	    ParcelFileDescriptor pfd = ParcelFileDescriptor.fromSocket(mSocket);
	    mMediaRecorder.setOutputFile(pfd.getFileDescriptor());

	    // Step 5: Set the preview output
	    mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());

	    // Step 6: Prepare configured MediaRecorder
	    try {
	        mMediaRecorder.prepare();
	    } catch (IllegalStateException e) {
	        Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
	        releaseMediaRecorder();
	        return false;
	    } catch (IOException e) {
	        Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
	        releaseMediaRecorder();
	        return false;
	    }
	    return true;
	}	

	
	/** 
	* A safe way to get an instance of the Camera object. 
	*/
	public Camera getCameraInstance(){
		
		if( mCamera != null ){
			Log.i(TAG,"getCameraInstance:Camera instanced already");
			return mCamera;
		}
		Log.i(TAG,"getCameraInstance:Camera not instanced already");

	    Camera mCamera = null;
	    try {
	        mCamera = Camera.open(); // attempt to get a Camera instance
		     // get Camera parameters
		     Camera.Parameters params = mCamera.getParameters();
		     // Attempts to set focusing area
		     if (params.getMaxNumMeteringAreas() > 0){ // check that metering areas are supported
		    	 Log.i(TAG, "Setting metering areas");
		    	 List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>();
		    	 Rect areaRect1 = new Rect(-	500, -500, 500, 500);    // specify an area in center of image
		    	 meteringAreas.add(new Camera.Area(areaRect1, 1000)); // set weight to 100%
		    	 params.setMeteringAreas(meteringAreas);
		     }
		     // set Camera parameters
			mCamera.setParameters(params);	        
	    }
	    catch (Exception e){
	    	Log.e(TAG,"getCameraInstance::Camera is not available");
	    	e.printStackTrace();
	        // Camera is not available (in use or does not exist)
	    }
	    
	    return mCamera; // returns null if camera is unavailable
	}
	
	/**
	 * 
	 */
	public void onSocketReady(){
		mSocket = conhandler.getSocket();
	}
	
	/**
	 * 
	 */
	private void releaseMediaRecorder(){
		if (mMediaRecorder != null) {
			mMediaRecorder.reset();   // clear recorder configuration
			mMediaRecorder.release(); // release the recorder object
			mMediaRecorder = null;
			mCamera.lock();           // lock camera for later use
		}
	}
	
	/**
	 * 
	 */
	private void releaseCamera(){
		if (mCamera != null){
			mCamera.release();        // release the camera for other applications
			mCamera = null;
		}
	}

}

