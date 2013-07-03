package com.example.octomanager;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.annotation.SuppressLint;
import android.hardware.Camera;

public class NetworkActivity extends Activity implements SocketListenerInterface{
	
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
	private ConnectionHandler conhandler;
    private Camera mCamera;
    private CameraPreview mPreview;
    private MediaRecorder mMediaRecorder;
    private boolean isRecording = false;
    private static String TAG = "NetworkActivity";
   
    private Socket s;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_network);
        // Create an instance of Camera
        mCamera = getCameraInstance();
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
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
//		                     mCamera.lock();         // take camera access back from MediaRecorder
		                     isRecording = false;
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
	public void onResume() {
		super.onResume();
		Log.i(TAG,"onResume:init");
		try{
			// Connects to socket
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
		Log.i(TAG,"onPause:init");
		try{
		    conhandler.cancel(true);
		    conhandler = null;
		    releaseMediaRecorder();       // if you are using MediaRecorder, release it first
		    releaseCamera();              // release the camera immediately on pause event
		}catch(Exception e){
			e.printStackTrace();
		}
  }

	private boolean prepareVideoRecorder(){
		Log.i( TAG, "prepareVideoRecorder:init");
	    mCamera = getCameraInstance();
	    if( null == mCamera){
	    	Log.e(TAG,"prepareVideoRecorder:: can't get camera");
	    	return false;
	    }
	    mMediaRecorder = new MediaRecorder();

	    // Step 1: Unlock and set camera to MediaRecorder
	    mCamera.unlock();
	    mMediaRecorder.setCamera(mCamera);

	    // Step 2: Set sources
	    mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
	    mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

	    // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
	    mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

	    // Step 4: Set output file
	    mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());

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

	
	/** A safe way to get an instance of the Camera object. */
	public Camera getCameraInstance(){
		
		if( mCamera != null ){
			Log.i(TAG,"getCameraInstance:Camera instanced already");
			return mCamera;
		}
		Log.i(TAG,"getCameraInstance:Camera not instanced already");

	    Camera c = null;
	    try {
	        c = Camera.open(); // attempt to get a Camera instance
	    }
	    catch (Exception e){
	    	Log.e(TAG,"getCameraInstance::Camera is not available");
	    	e.printStackTrace();
	        // Camera is not available (in use or does not exist)
	    }
	    
	    return c; // returns null if camera is unavailable
	}
	
	public void onSocketReady(){
		
		conhandler.send( "NetworkActivity::onSocketReady".getBytes()); 
		Log.i("NetworkActivity:onSocketReady","Load");
		s = conhandler.getSocket();
//		Bitmap bMap = BitmapFactory.decodeFile("/sdcard/test.jpg");
//
//		ByteArrayOutputStream stream = new ByteArrayOutputStream();
//		bMap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//		byte[] byteArray = stream.toByteArray();
//		Log.i("NetworkActivity:onSocketReady","sending bitmap");
//		conhandler.send(byteArray); 
	
		
	}

  private void releaseMediaRecorder(){
      if (mMediaRecorder != null) {
          mMediaRecorder.reset();   // clear recorder configuration
          mMediaRecorder.release(); // release the recorder object
          mMediaRecorder = null;
          mCamera.lock();           // lock camera for later use
      }
  }

  private void releaseCamera(){
      if (mCamera != null){
          mCamera.release();        // release the camera for other applications
          mCamera = null;
      }
  }

	/** Create a file Uri for saving an image or video */
	private static Uri getOutputMediaFileUri(int type){
	      return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.
	
	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "MyCameraApp");
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.
	
	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("MyCameraApp", "failed to create directory");
	            return null;
	        }
	    }
	
	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File mediaFile;
	    if (type == MEDIA_TYPE_IMAGE){
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "IMG_"+ timeStamp + ".jpg");
	    } else if(type == MEDIA_TYPE_VIDEO) {
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "VID_"+ timeStamp + ".mp4");
	    } else {
	        return null;
	    }
	
	    return mediaFile;
	}
}

