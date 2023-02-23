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

import static org.jsampler.android.view.AndroidI18n.i18n;

import org.jsampler.AudioDeviceModel;
import org.jsampler.CC;
import org.jsampler.event.ListEvent;
import org.jsampler.event.ListListener;

public class AudioDeviceSpinnerAdapter extends AbstractSpinnerAdapter<AudioDeviceModel> 
				implements ListListener<AudioDeviceModel> {
	public AudioDeviceSpinnerAdapter() {
		CC.getSamplerModel().addAudioDeviceListListener(this);
	}
	
	public void
	uninstall() { CC.getSamplerModel().removeAudioDeviceListListener(this); }
	
	@Override
	public int
	size() { return CC.getSamplerModel().getAudioDeviceCount(); }
	
	@Override
	public Object
	get(int position) { return CC.getSamplerModel().getAudioDevice(position); }
	
	public String
	getEmptyItemText() { return i18n.getLabel("AudioDeviceSpinnerAdapter.selectDevice"); }
	
	public String
	getItemText(int position) {
		AudioDeviceModel dev = (AudioDeviceModel)getItem(position);
		return i18n.getLabel("AudioDeviceSpinnerAdapter.device", dev.getDeviceId());
	}
	
	@Override
	public boolean
	compare(AudioDeviceModel d1, AudioDeviceModel d2) {
		if(d1 == null || d2 == null) return false;
		return d1.getDeviceId() == d2.getDeviceId();
	}
	
	/** Invoked when a new audio device is created. */
	@Override
	public void
	entryAdded(ListEvent<AudioDeviceModel> e) { notifyDataSetChanged(); }

	/** Invoked when an audio device is removed. */
	@Override
	public void
	entryRemoved(ListEvent<AudioDeviceModel> e) { notifyDataSetChanged(); }
}
