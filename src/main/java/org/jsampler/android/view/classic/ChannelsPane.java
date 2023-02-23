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

import org.jsampler.SamplerChannelModel;
import org.jsampler.android.AHF;
import org.jsampler.android.R;
import org.jsampler.android.view.AndroidChannelsPane;
import org.jsampler.android.view.SamplerChannelListAdapter;
import org.jsampler.event.ListSelectionListener;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class ChannelsPane extends AndroidChannelsPane<Channel> {
	public
	ChannelsPane() { this("Untitled"); }
	
	public
	ChannelsPane(String title) { super(title); }
	
	public View
	createView() {
		LayoutInflater inflater =
			(LayoutInflater)AHF.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.sampler_channel_lane, null);
		
		TextView tv = (TextView)v.findViewById(R.id.sampler_channel_lane_title);
		tv.setText(getTitle());
		
		ListView lv = (ListView)v.findViewById(R.id.sampler_channel_lane_list);
		SamplerChannelListAdapter model = new SamplerChannelListAdapter(this);
		lv.setAdapter(model);
		
		lv.setOnItemClickListener(new  ListView.OnItemClickListener() {
			@Override
			public void
			onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent i = new Intent(AHF.getActivity(), ChannelPagerActivity.class);
				i.putExtra("org.jsampler.android.SelectedChannelID", position);
				AHF.getActivity().startActivity(i);
			}
		});
		
		return v;
	}
	
	/**
	 * Adds new channel to this channels pane.
	 * @param channelModel The sampler channel model to be used by the new channel.
	 */
	@Override
	public void
	addChannel(SamplerChannelModel channelModel) {
		Channel chn = new Channel(channelModel);
		channels.add(chn);
		firePropertyChange("channelAdded", null, channelModel);
	}
	
	/**
	 * Determines whether there is at least one selected channel.
	 * @return <code>true</code> if there is at least one selected channel,
	 * <code>false</code> otherwise.
	 */
	@Override
	public boolean
	hasSelectedChannel() {
		return false;
	}
	
	/**
	 * Gets an array of all channels in this channels pane.
	 * @return An array of all channels in this channels pane.
	 */
	@Override
	public Channel[] getChannels() {
		Channel[] chns = new Channel[channels.size()];
		for(int i = 0; i < channels.size(); i++) chns[i] = channels.get(i);
		return chns;
	}
	
	/**
	 * Gets an array of all selected channels.
	 * The channels are sorted in increasing index order.
	 * @return The selected channels or an empty array if nothing is selected.
	 */
	@Override
	public Channel[]
	getSelectedChannels() { return new Channel[0]; }
	
	/**
	 * Gets the number of the selected channels.
	 * @return The number of the selected channels.
	 */
	@Override
	public int
	getSelectedChannelCount() { return 0; }
	
	/**
	 * Selects the specified channel.
	 */
	@Override
	public void
	setSelectedChannel(Channel channel) { }
	
	/** Selects all channels. */
	@Override
	public void selectAll() { }
	
	/** Deselects all selected channels. */
	@Override
	public void clearSelection() { }
	
	/**
	 * Removes all selected channels in this channels pane.
	 * Notice that this method does not remove any channels in the back-end.
	 * It is invoked after the channels are already removed in the back-end.
	 * @return The number of removed channels.
	 */
	@Override
	public int removeSelectedChannels() { return 0; }
	
	@Override
	public void moveSelectedChannelsOnTop() { }
	
	@Override
	public void moveSelectedChannelsUp() { }
	
	@Override
	public void moveSelectedChannelsDown() { }
	
	@Override
	public void moveSelectedChannelsAtBottom() { }
		
	/**
	 * Registers the specified listener for receiving list selection events.
	 * @param listener The <code>ListSelectionListener</code> to register.
	 */
	@Override
	public void addListSelectionListener(ListSelectionListener listener) { }
	
	/**
	 * Removes the specified listener.
	 * @param listener The <code>ListSelectionListener</code> to remove.
	 */
	@Override
	public void removeListSelectionListener(ListSelectionListener listener) { }
	
	/**
	 * Process a selection event.
	 * @param c The newly selected channel.
	 * @param controlDown Specifies whether the control key is held down during selection.
	 * @param shiftDown Specifies whether the shift key is held down during selection.
	 */
	@Override
	public void processChannelSelection(Channel c, boolean controlDown, boolean shiftDown) { }
	
	/**
	 * Determines whether the channel list UI should be automatically updated
	 * when channel is added/removed. The default value is <code>true</code>.
	 * @see updateChannelListUI
	 */
	@Override
	public boolean getAutoUpdate() { return true; }
	
	/**
	 * Determines whether the channel list UI should be automatically updated
	 * when channel is added/removed.
	 * @see updateChannelListUI
	 */
	@Override
	public void setAutoUpdate(boolean b) { }
	
	/**
	 * Updates the channel list UI.
	 * @see setAutoUpdate
	 */
	@Override
	public void updateChannelListUI() { }
}
