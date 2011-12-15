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

package org.jsampler.android.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.jsampler.HF;
import org.jsampler.android.AHF;
import org.jsampler.android.R;
import org.linuxsampler.lscp.SamplerChannel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static org.jsampler.android.view.AndroidI18n.i18n;

public class SamplerChannelListAdapter extends AbstractListAdapter {
	private AndroidChannelsPane channelLane;
	
	public SamplerChannelListAdapter(AndroidChannelsPane channelLane) {
		this.channelLane = channelLane;
		installListeners();
	}
	
	private void
	installListeners() {
		channelLane.addPropertyChangeListener(getHandler());
	}
	
	private void
	uninstallListeners() {
		channelLane.removePropertyChangeListener(getHandler());
	}
	
	public void
	uninstall() { uninstallListeners(); }
	
	@Override
	public int
	getCount() { return channelLane.getChannelCount(); }
	
	@Override
	public Object
	getItem(int position) { return channelLane.getChannel(position); }
	
	@Override
	public View
	getView(int position, View convertView, ViewGroup parent) {
		View view;
		if(convertView != null) {
			view = convertView;
		} else {
			LayoutInflater inflater =
				(LayoutInflater)AHF.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.sampler_channel_list_item, null);
		}
		
		TextView text = (TextView)view.findViewById(R.id.sampler_channel_list_item_text_num);
		text.setText(String.valueOf(position + 1) + ":");
		
		SamplerChannel sc = channelLane.getChannel(position).getChannelInfo();
		text = (TextView)view.findViewById(R.id.sampler_channel_list_item_text_instr);
		text.setText(getInstrumentText(sc));
		
		text = (TextView)view.findViewById(R.id.sampler_channel_list_item_text_volume);
		text.setText( HF.getVolumeString((int)(sc.getVolume() * 100)) );
		
		return view;
	}
	
	private String
	getInstrumentText(SamplerChannel sc) {
		StringBuffer sb = new StringBuffer();
		int status = sc.getInstrumentStatus();
		if(status >= 0 && status < 100) {
			sb.append(i18n.getLabel("SamplerChannelListAdapter.loadingInstrument", status));
		} else if(status == -1) {
			sb.append(i18n.getLabel("SamplerChannelListAdapter.noInstrument"));
		} else if(status < -1) {
			sb.append(i18n.getLabel("SamplerChannelListAdapter.errorLoadingInstrument"));
		} else {
			if(sc.getInstrumentName() != null) sb.append(sc.getInstrumentName());
			else sb.append(i18n.getButtonLabel("SamplerChannelListAdapter.noInstrument"));
		}
		
		return sb.toString();
	}
	
	EventHandler handler = new EventHandler();
	
	private EventHandler
	getHandler() { return handler; }
	
	private class EventHandler implements PropertyChangeListener {
		@Override
		public void
		propertyChange(PropertyChangeEvent e) {
			if ( "channelRemoved".equals(e.getPropertyName()) ||
			     "channelAdded"  .equals(e.getPropertyName()) ||
			     "channelsAdded" .equals(e.getPropertyName())
			) {
				notifyDataSetChanged();
			}
			
		}
	}
}
