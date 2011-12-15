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

package org.jsampler.android.view.classic;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.jsampler.CC;
import org.jsampler.HF;
import org.jsampler.JSPrefs;
import org.jsampler.Server;
import org.jsampler.android.AHF;
import org.jsampler.android.JSamplerActivity;
import org.jsampler.android.R;
import org.jsampler.android.view.AndroidChannelsPane;
import org.jsampler.android.view.AndroidMainFrame;
import org.jsampler.event.SamplerAdapter;
import org.jsampler.event.SamplerEvent;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import static org.jsampler.JSPrefs.MAXIMUM_MASTER_VOLUME;
import static org.jsampler.JSPrefs.VOL_MEASUREMENT_UNIT_DECIBEL;
import static org.jsampler.android.view.AndroidI18n.i18n;
import static org.jsampler.android.view.classic.ClassicPrefs.preferences;
import static org.jsampler.android.JSamplerActivity.*;

public class MainFrame extends AndroidMainFrame<AndroidChannelsPane> {
	private static final int FLING_MIN_DISTANCE = 100;
	
	private View view = null;
	private ViewFlipper panesFlipper;
	private ViewPager lanesPager;
	
	public MainFrame() {
		
	}
	
	@Override
	public void
	onCreate() {
		LayoutInflater inflater =
			(LayoutInflater)AHF.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.classic_main, null);
		
		ChannelLanesPagerAdapter lanesPagerAdapter = new ChannelLanesPagerAdapter();
		lanesPager = (ViewPager)view.findViewById(R.id.main_channel_lane_pager);
		lanesPager.setAdapter(lanesPagerAdapter);

		panesFlipper = (ViewFlipper)view.findViewById(R.id.main_flipper);
		panesFlipper.setDisplayedChild(1);
		updateMainTitle();
		
		installListeners();
		updateVolume();
		
		ChannelsPane cp = new ChannelsPane();
		cp.setTitle("Channel Lane 1");
		addChannelsPane(cp);
		
		addChannelsPane(new ChannelsPane("Channel Lane 2"));
		setSelectedChannelsPane(cp);
	}
	
	public void
	uninstall() {
		uninstallListeners();
	}
	
	@Override
	public View
	getView() { return view; }
	
	@Override
	public boolean
	onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		/*ViewFlipper flipper = getFlipper();
		
		if(Math.abs(e1.getX() - e2.getX()) > Math.abs(e1.getY() - e2.getY())) {
			if(Math.abs(e1.getX() - e2.getX()) < FLING_MIN_DISTANCE) return false;
			if(e1.getX() - e2.getX() > 0) { // right to left
				flipper.setInAnimation(AHF.inFromRightAnimation());
				flipper.setOutAnimation(AHF.outToLeftAnimation());
				flipper.showNext();
			} else { // left to right
				flipper.setInAnimation(AHF.inFromLeftAnimation());
				flipper.setOutAnimation(AHF.outToRightAnimation());
				flipper.showPrevious();
			}
			return true;
		} else {
			if(e1.getY() - e2.getY() > 0) { // up
				
			} else { // down
				
			}
		}*/
		return false;
	}
	
	public boolean
	onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		
		return false;
	}
	
	@Override
	public void setVisible(boolean b) {
		
	}
	
	private void
	installListeners() {
		SeekBar sliderVolume = (SeekBar)view.findViewById(R.id.main_slider_volume);
		int i = CC.preferences().getIntProperty(MAXIMUM_MASTER_VOLUME);
		sliderVolume.setMax(i);
		CC.preferences().addPropertyChangeListener(MAXIMUM_MASTER_VOLUME, getHandler());
		
		sliderVolume.setOnSeekBarChangeListener(getHandler());
		
		CC.getSamplerModel().addSamplerListener(getHandler());
		
		CC.preferences().addPropertyChangeListener(VOL_MEASUREMENT_UNIT_DECIBEL, getHandler());
		
		ImageButton btn = (ImageButton)view.findViewById(R.id.classic_main_flip_back);
		btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				panesFlipper.setInAnimation(AHF.inFromLeftAnimation());
				panesFlipper.setOutAnimation(AHF.outToRightAnimation());
				panesFlipper.showPrevious();
				updateMainTitle();
			}
		});
		
		btn = (ImageButton)view.findViewById(R.id.classic_main_flip_forward);
		btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				panesFlipper.setInAnimation(AHF.inFromRightAnimation());
				panesFlipper.setOutAnimation(AHF.outToLeftAnimation());
				panesFlipper.showNext();
				updateMainTitle();
			}
		});
	}
	
	private void
	uninstallListeners() {
		CC.preferences().removePropertyChangeListener(MAXIMUM_MASTER_VOLUME, getHandler());
		CC.getSamplerModel().removeSamplerListener(getHandler());
		CC.preferences().removePropertyChangeListener(VOL_MEASUREMENT_UNIT_DECIBEL, getHandler());
		
		ImageButton btn = (ImageButton)view.findViewById(R.id.classic_main_flip_back);
		btn.setOnClickListener(null);
		
		btn = (ImageButton)view.findViewById(R.id.classic_main_flip_forward);
		btn.setOnClickListener(null);
		
		SeekBar sliderVolume = (SeekBar)view.findViewById(R.id.main_slider_volume);
		sliderVolume.setOnSeekBarChangeListener(null);
	}
	
	private void
	updateMainTitle() {
		TextView text = (TextView)view.findViewById(R.id.main_title);
		switch(panesFlipper.getDisplayedChild()) {
		case 0:
			text.setText(i18n.getLabel("MainFrame.titleMidiDevices"));
			break;
		case 1:
			text.setText(i18n.getLabel("MainFrame.titleSamplerChannels"));
			break;
		case 2:
			text.setText(i18n.getLabel("MainFrame.titleAudioDevices"));
			break;
		default:
			text.setText("");
		}
	}
	
	private void
	updateVolume() {
		SeekBar sliderVolume = (SeekBar)view.findViewById(R.id.main_slider_volume);
		sliderVolume.setProgress((int)(CC.getSamplerModel().getVolume() * 100));
	}
	
	private void
	setVolume(boolean isAdjusting) {
		SeekBar sliderVolume = (SeekBar)view.findViewById(R.id.main_slider_volume);
		TextView tvVolume = (TextView)view.findViewById(R.id.main_text_volume);
		int volume = sliderVolume.getProgress();
		
		tvVolume.setText(HF.getVolumeString(volume));
		
		if(isAdjusting) return;
		
		int vol = (int)(CC.getSamplerModel().getVolume() * 100);
		
		if(vol == sliderVolume.getProgress()) return;
		
		/*
		 * If the model's volume is not equal to the slider
		 * value we assume that the change is due to user input.
		 * So we must update the volume at the backend too.
		 */
		float v = sliderVolume.getProgress();
		v /= 100;
		CC.getSamplerModel().setBackendVolume(v);
	}
	
	EventHandler handler = new EventHandler();
	
	private EventHandler
	getHandler() { return handler; }
	
	private class EventHandler extends SamplerAdapter
				implements SeekBar.OnSeekBarChangeListener, PropertyChangeListener {
		
		@Override
		public void
		volumeChanged(SamplerEvent e) { updateVolume(); }
		
		@Override
		public void
		onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			setVolume(true);
		}
		
		@Override
		public void
		onStopTrackingTouch(SeekBar seekBar) { setVolume(false); }
		
		@Override
		public void
		onStartTrackingTouch(SeekBar seekBar) { }
		
		@Override
		public void
		propertyChange(PropertyChangeEvent e) {
			if (VOL_MEASUREMENT_UNIT_DECIBEL.equals(e.getPropertyName())) {
				setVolume(true);
			} else if (MAXIMUM_MASTER_VOLUME.equals(e.getPropertyName())) {
				int j = CC.preferences().getIntProperty(MAXIMUM_MASTER_VOLUME);
				SeekBar sliderVolume = (SeekBar)view.findViewById(R.id.main_slider_volume);
				sliderVolume.setMax(j);
			}
			
		}
	}
	
	/**
	 * Gets the channels' pane that is currently shown,
	 * or has the focus if more than one channels' panes are shown.
	 * If the GUI implementation has only one pane containing sampler channels,
	 * than this method should always return that pane (the channels' pane
	 * with index 0).
	 * @return The selected channels' pane.
	 */
	@Override
	public AndroidChannelsPane
	getSelectedChannelsPane() {
		return getChannelsPane(lanesPager.getCurrentItem());
	}
	
	/**
	 * Sets the channels' pane to be selected.
	 * Note that all registered listeners should be notified
	 * when the selection is changed.
	 * @param pane The channels' pane to be shown.
	 * @see #fireChannelsPaneSelectionChanged
	 */
	@Override
	public void
	setSelectedChannelsPane(AndroidChannelsPane pane) {
		int idx = getChannelsPaneIndex(pane);
		if(idx == -1) {
			Log.w("setSelectedChannelsPane", "Channel lane not found!");
			return;
		}
		lanesPager.setCurrentItem(idx, true);
	}
	
	/**
	 * Inserts the specified channels' pane at the specified position
	 * in the view and in the channels' pane list.
	 * Where and how this pane will be shown depends on the view/GUI implementation.
	 * Note that some GUI implementation may have only one pane containing sampler channels.
	 * @param pane The channels' pane to be inserted.
	 * @param idx Specifies the position of the channels' pane.
	 * @see #getChannelsPaneList
	 */
	@Override
	public void insertChannelsPane(AndroidChannelsPane pane, int idx) {
		chnPaneList.insertElementAt(pane, idx);
		lanesPager.getAdapter().notifyDataSetChanged();
		lanesPager.setCurrentItem(idx, true);
		
		/*ViewFlipper flipper = getFlipper();
		
		if(idx < 0 || flipper.getChildCount() < idx) {
			Log.w("classic.MainFrame.insertChannelsPane", "Incorrect lane position: " + idx);
			flipper.addView(pane.getView());
		} else {
			flipper.addView(pane.getView(), idx);
		}
		
		flipper.setDisplayedChild(idx);*/
		firePropertyChange("channelLaneAdded", null, pane);
	}
	
	/**
	 * Invoked on startup when no JSampler home directory is specified
	 * or the specified JSampler home directory doesn't exist.
	 * This method should ask the user to specify a JSampler
	 * home directory and then set the specified JSampler home directory using
	 * {@link org.jsampler.CC#setJSamplerHome} method.
	 * @see org.jsampler.CC#getJSamplerHome
	 * @see org.jsampler.CC#setJSamplerHome
	 */
	@Override
	public void installJSamplerHome() {
		AHF.getActivity().showDialog(JSamplerActivity.DLG_CHOOSE_HOME_DIR);
	}
	
	/** Shows a detailed error information about the specified exception. */
	@Override
	public void showDetailedErrorMessage(String err, String details) {
		
	}
	
	@Override
	public void handleConnectionFailure() {
		
	}
	
	/**
	 * Gets the server address to which to connect. If the server should be
	 * manually selected, a dialog asking the user to choose a server is displayed.
	 * @param manualSelect Determines whether the server should be manually selected.
	 */
	@Override
	public void getServer(final CC.Run<Server> r, boolean manualSelect) {
		if(!manualSelect) {
			int i = preferences().getIntProperty(JSPrefs.SERVER_INDEX);
			int size = CC.getServerList().getServerCount();
			if(size == 0) r.run(null);
			else if(i >= size) r.run(CC.getServerList().getServer(0));
			else r.run(CC.getServerList().getServer(i));
			return;
		}
		
		AHF.getActivity().startLocalActivity(new Requestor<Server>(CHOOSE_BACKEND_REQUEST) {
			public void
			onResult(Server res) { r.run(res); }
		});
	}
	
	/**
	 * Sends the specified script to the backend.
	 * @param script The file name of the script to run.
	 */
	@Override
	public void runScript(String script) {
		
	}
	
	private class ChannelLanesPagerAdapter extends PagerAdapter {
		public int
		getCount() { return getChannelsPaneCount(); }
		
		public void startUpdate(View container) { }
		public void finishUpdate(View container) { }
		
		public Object
		instantiateItem(View container, int position) {
			View view = getChannelsPane(position).getView();
			((ViewPager)container).addView(view, 0);
			return view;
		}
		
		public void
		destroyItem(View container, int position, Object object) {
			((ViewPager)container).removeView((View)object);
		}
		
		public void
		setPrimaryItem(View container, int position, Object object) {
			setSelectedChannelsPane(getChannelsPane(position));
		}
		
		public boolean
		isViewFromObject(View view, Object object) { return view == object; }
		
		public Parcelable
		saveState() { return null; }
		
		public void
		restoreState(Parcelable state, ClassLoader loader) { }
	}
}
