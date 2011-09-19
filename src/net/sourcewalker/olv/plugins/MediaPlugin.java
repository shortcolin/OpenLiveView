package net.sourcewalker.olv.plugins;

import net.sourcewalker.olv.messages.calls.EncodeDisplayPanel;
import net.sourcewalker.olv.service.LiveViewService;
import android.util.Log;
import android.view.KeyEvent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;


public class MediaPlugin extends Plugin {
	private static final String TAG = "liveviewmedia";

	// http://blog.dexetra.com/2010/09/get-current-playing-track-info-from.html
	
	public static final String SERVICECMD = "com.android.music.musicservicecommand";
	public static final String CMDNAME = "command";
	public static final String CMDTOGGLEPAUSE = "togglepause";
	public static final String CMDSTOP = "stop";
	public static final String CMDPAUSE = "pause";
	public static final String CMDPREVIOUS = "previous";
	public static final String CMDNEXT = "next";

	private LiveViewService service;
	private byte[] image;
	private BroadcastReceiver trackChangedReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			String cmd = intent.getStringExtra("command");
			Log.d(TAG, action + " / " + cmd);
			String artist = intent.getStringExtra("artist");
			String album = intent.getStringExtra("album");
			String track = intent.getStringExtra("track");
			Boolean playing = intent.getBooleanExtra("playing", false);
			Log.d(TAG, artist + ":" + album + ":" + track);
			
			EncodeDisplayPanel ep;
			
			if (playing) { 
				ep = new EncodeDisplayPanel(album, track, false, image);
			}
			else {
				ep = new EncodeDisplayPanel(album, "<paused>", false, image);
			}
			MediaPlugin.this.service.sendCall(ep);
		}
	};
	
	public MediaPlugin(LiveViewService service) {
		this.service = service;
		this.image = service.getIcon("menu_blank.png");
	}
	
	@Override
	public void start() {
		IntentFilter iF = new IntentFilter();
		iF.addAction("com.android.music.metachanged");
		iF.addAction("com.android.music.playstatechanged");
		iF.addAction("com.android.music.playbackcomplete");
		iF.addAction("com.android.music.queuechanged");

		this.service.registerReceiver(trackChangedReceiver, iF);
	}

	public void stop() {
		this.service.unregisterReceiver(trackChangedReceiver);
	}
	
	private void sendButtonIntent(int code) {
		// ...key down
		Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
		KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, code);
		intent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
		service.sendOrderedBroadcast(intent, null);
		
		// ...key up
		intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
		keyEvent = new KeyEvent(KeyEvent.ACTION_UP, code);
		intent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
		service.sendOrderedBroadcast(intent, null);		
	}
	
	@Override
	public boolean handleButtonLeft(Boolean doublePress, Boolean longPress) {
		sendButtonIntent(KeyEvent.KEYCODE_MEDIA_PREVIOUS);
		return false;
	}

	@Override
	public boolean handleButtonRight(Boolean doublePress, Boolean longPress) {
		sendButtonIntent(KeyEvent.KEYCODE_MEDIA_NEXT);
		return false;
	}

	@Override
	public boolean handleButtonUp(Boolean doublePress, Boolean longPress) {
		sendButtonIntent(KeyEvent.KEYCODE_VOLUME_UP);
		return false;
	}

	@Override
	public boolean handleButtonDown(Boolean doublePress, Boolean longPress) {
		sendButtonIntent(KeyEvent.KEYCODE_VOLUME_DOWN);
		return false;
	}

	@Override
	public boolean handleButtonSelect(Boolean doublePress, Boolean longPress) {
		sendButtonIntent(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
		return false;
	}

}
