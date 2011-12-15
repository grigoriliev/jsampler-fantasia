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

import java.util.ArrayDeque;

import org.jsampler.CC;
import org.jsampler.android.AHF;
import org.jsampler.event.SamplerChannelListEvent;
import org.jsampler.event.SamplerChannelListListener;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class ChannelPagerActivity extends Activity {
	private ViewPager channelPager;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		channelPager = new ViewPager(this);
		ChannelPagerAdapter adapter = new ChannelPagerAdapter();
		channelPager.setAdapter(adapter);
		CC.getSamplerModel().addSamplerChannelListListener(adapter);
		int idx = getIntent().getExtras().getInt("org.jsampler.android.SelectedChannelID", 0);
		channelPager.setCurrentItem(idx);
		setContentView(channelPager);
	}
	
	@Override
	protected void
	onDestroy() {
		ChannelPagerAdapter adapter = (ChannelPagerAdapter)channelPager.getAdapter();
		CC.getSamplerModel().removeSamplerChannelListListener(adapter);
		super.onDestroy();
		
	}
	
	private static class ChannelViewPair {
		public Channel channel;
		public View    view;
		
		ChannelViewPair(Channel channel, View view) {
			this.channel = channel;
			this.view = view;
		}
	}
	
	private class ChannelPagerAdapter extends PagerAdapter implements SamplerChannelListListener {
		
		
		private ArrayDeque<View> recycledViews = new ArrayDeque<View>();
		private ChannelViewPair primaryItem = null;
		
		private ChannelsPane
		getLane() { return (ChannelsPane)AHF.getMainFrame().getSelectedChannelsPane(); }
		
		@Override
		public int
		getCount() { return getLane().getChannelCount(); }
		
		@Override
		public void startUpdate(View container) { }
		
		@Override
		public void finishUpdate(View container) {
			if(channelPager.getCurrentItem() >= getCount()) {
				// workaround for bug in ViewPager when the
				// current item is the last one and a previous item is removed
				channelPager.setCurrentItem(getCount() - 1, true);
			}
		}
		
		@Override
		public int
		getItemPosition(Object object) { return POSITION_NONE; }
		
		@Override
		public Object
		instantiateItem(View container, int position) {
			Channel chn = getLane().getChannel(position);
			View view = recycledViews.pollLast();
			chn.installView(view);
			view = chn.getView();
			
			((ViewPager)container).addView(view, 0);
			return new ChannelViewPair(chn, view);
		}
		
		@Override
		public void
		destroyItem(View container, int position, Object object) {
			ChannelViewPair pair = (ChannelViewPair)object;
			((ViewPager)container).removeView(pair.view);
			recycledViews.add(pair.view);
			pair.channel.uninstallView();
		}
		
		@Override
		public void
		setPrimaryItem(View container, int position, Object object) {
			primaryItem = (ChannelViewPair)object;
		}
		
		@Override
		public boolean
		isViewFromObject(View view, Object object) {
			return view == ((ChannelViewPair)object).view;
		}
		
		@Override
		public Parcelable
		saveState() { return null; }
		
		@Override
		public void
		restoreState(Parcelable state, ClassLoader loader) { }
		
		@Override
		public void
		channelAdded(SamplerChannelListEvent e) { notifyDataSetChanged(); }
		
		@Override
		public void
		channelRemoved(SamplerChannelListEvent e) { notifyDataSetChanged(); }
	}
}
