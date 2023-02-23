/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2011 Grigor Iliev <grigor@grigoriliev.com>
 *
 *   This file is part of JSampler.
 *
 *   JSampler is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License version 2
 *   as published by the Free Software Foundation.
 *
 *   JSampler is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with JSampler; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 *   MA  02111-1307  USA
 */

package org.jsampler.android;

import java.util.TreeMap;

import net.sf.juife.PDUtils;
import net.sf.juife.impl.AndroidPDUtilsImpl;

import org.jsampler.CC;
import org.jsampler.HF;
import org.jsampler.JSampler;
import org.jsampler.android.view.std.ChooseBackendActivity;
import org.jsampler.android.view.std.JSamplerHomeChooser;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;


public class JSamplerActivity extends Activity {
	public static final int DLG_CHOOSE_HOME_DIR = 0;
	
	public static final int CHOOSE_BACKEND_REQUEST = 1000;
	
	private boolean started = false;
	
	private final TreeMap<Integer, Requestor> requestorMap = new TreeMap<Integer, Requestor> ();
	
	private GestureDetector gestureDetector;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		gestureDetector = new GestureDetector(this, gestureListener);
		
		//showDialog(DLG_ADD_BACKEND);
		
		AndroidPDUtilsImpl.activity = this;
		AHF.setActivity(this);
		
		if(!HF.canReadWriteFiles(System.getProperty("user.home"))) {
			// FIXME: workaround to make java.util.prefs to work properly
			System.setProperty("user.home", getFilesDir().getAbsolutePath());
		}
		
		if(!started) {
			JSampler.main(null);
			started = true;
		}
		
		AHF.getMainFrame().onCreate();
		setContentView(AHF.getMainFrame().getView());
		
	}
	
	private SimpleOnGestureListener gestureListener = new SimpleOnGestureListener() {
		public boolean
		onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			return AHF.getMainFrame().onFling(e1, e2, velocityX, velocityY);
		}
		
		public boolean
		onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			return AHF.getMainFrame().onFling(e1, e2, distanceX, distanceY);
		}
	};
	
	/*public boolean
	onTouchEvent(MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}*/
	
	public boolean
	dispatchTouchEvent(MotionEvent event) {
		boolean b = gestureDetector.onTouchEvent(event);
		if (!b) return super.dispatchTouchEvent(event);
		return b;
	}
	
	@Override
	protected void
	onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode != Activity.RESULT_OK) return;
		
		switch(requestCode) {
		case CHOOSE_BACKEND_REQUEST:
			Requestor r = requestorMap.get(CHOOSE_BACKEND_REQUEST);
			if(r == null) return;
			Log.w("JSamplerActivity", "onActivityResult: CHOOSE_BACKEND_REQUEST");
			r.setResult(data);
			requestorMap.remove(CHOOSE_BACKEND_REQUEST);
		break;
		}
	}

	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		AHF.getMainFrame().uninstall();
		
		// postpone
		PDUtils.runOnUiThread(new Runnable() {
			public void
			run() { CC.cleanExit(); }
		});
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		
		switch(id) {
		case DLG_CHOOSE_HOME_DIR:
			dialog = new JSamplerHomeChooser(this);
			break;
		default:
			dialog = null;
		}
		
		return dialog;
	}
	
	@Override
	public boolean
	onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.jsampler_options_menu, menu);
		return true;
	}
	
	@Override
	public boolean
	onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.jsampler_options_menu_choose_backend:
			CC.changeBackend();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	public static <R> R
	getLocalResult(Intent i) {
		try {
			if(i == null) return null;
			
			return (R)i.getSerializableExtra("org.jsampler.SomeSerializableObject");
		} catch(ClassCastException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static abstract class Requestor<R> {
		private final int requestId;
		
		/**
		 * Creates a requestor which is used to start a local activity.
		 * @param requestId Specifies the activity to be started.
		 * @see #startLocalActivity
		 */
		public Requestor(int requestId) {
			this.requestId = requestId;
		}
		
		private void
		setResult(Intent data) {
			onResult(JSamplerActivity.<R>getLocalResult(data));
		}
		
		/**
		 * Invoked when the activity is finished with result. 
		 * @param res The result obtained from the activity.
		 */
		public abstract void onResult(R res);
	}
	
	/**
	 * Creates a local intent (for app internal use) if currently
	 * there is no registered requestor for the intended activity. 
	 * @param requestId Used to determine the activity which should be started.
	 * @return The newly created intent or <code>null</code> if the request ID is uknown
	 * or if there is already registered requestor for the intended activity.
	 */
	private Intent
	createLocalIntent(int requestId) {
		if(requestorMap.containsKey(requestId)) {
			Log.w("request ID: " + requestId, "There is a registered requestor for this activity");
			return null;
		}
		switch(requestId) {
		case CHOOSE_BACKEND_REQUEST:
			return new Intent(this, ChooseBackendActivity.class);
		default:
			Log.w("createLocalIntent", "Unknown request ID: " + requestId);
			return null;
		}
	}
	
	/** Starts an app local activity. */
	public void
	startLocalActivity(Requestor requestor) {
		Intent intent = createLocalIntent(requestor.requestId);
		if(intent == null) return;
		requestorMap.put(requestor.requestId, requestor);
		startActivityForResult(intent, CHOOSE_BACKEND_REQUEST);
	}
}