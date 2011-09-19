package net.sourcewalker.olv.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import net.sourcewalker.olv.messages.LiveViewCall;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * This service hosts and controls the thread communicating with the LiveView
 * device.
 * 
 * @author Robert &lt;xperimental@solidproject.de&gt;
 */
public class LiveViewService extends Service {

    public static final String ACTION_START = "start";
    public static final String ACTION_STOP = "stop";
    public static final String TAG = "LiveView";


    private LiveViewThread workerThread = null;

    /*
     * (non-Javadoc)
     * @see android.app.Service#onBind(android.content.Intent)
     */
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (ACTION_START.equals(action)) {
            startThread();
        } else if (ACTION_STOP.equals(action)) {
            stopThread();
        }
        return START_NOT_STICKY;
    }

    /**
     * Starts the worker thread if the thread is not already running. If there
     * is a thread running that has already been stopped then a new thread is
     * started.
     */
    private void startThread() {
        if (workerThread == null || !workerThread.isLooping()) {

            workerThread = new LiveViewThread(this);
            workerThread.start();
        }
    }

    /**
     * Signals the current worker thread to stop itself. If no worker thread is
     * available then nothing is done.
     */
    private void stopThread() {
        if (workerThread != null && workerThread.isAlive()) {
            workerThread.stopLoop();
        }
        stopSelf();
    }
    
    /**
     * Send an item to the LiveView
     * @param call
     */
    public void sendCall(LiveViewCall call) {
    	try {
			workerThread.sendCall(call);
		} catch (IOException e) {
			Log.e(TAG,"Error sending call");
			e.printStackTrace();
		}
    }
    
    public byte[] getIcon(String name) {
    	
        try {
            InputStream stream = this.getAssets().open(name);
            ByteArrayOutputStream arrayStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            while (stream.available() > 0) {
                int read = stream.read(buffer);
                arrayStream.write(buffer, 0, read);
            }
            stream.close();
            return arrayStream.toByteArray();
            
            //menuImage = arrayStream.toByteArray();
            //Log.d(TAG, "Menu icon size: " + menuImage.length);
        } catch (IOException e) {
            Log.e(TAG, "Error reading menu icon: " + e.getMessage());
            throw new RuntimeException("Error reading menu icon: "
                    + e.getMessage(), e);
        }
 

    }

}
