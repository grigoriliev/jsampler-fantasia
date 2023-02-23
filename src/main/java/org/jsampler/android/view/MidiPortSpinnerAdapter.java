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

import org.jsampler.CC;
import org.jsampler.MidiDeviceModel;
import org.jsampler.event.MidiDeviceEvent;
import org.jsampler.event.MidiDeviceListener;
import org.linuxsampler.lscp.MidiPort;

import android.util.Log;

public class MidiPortSpinnerAdapter extends AbstractSpinnerAdapter<MidiPort> implements MidiDeviceListener {
	private MidiDeviceModel midiDev = null;
	
	@Override
	public int
	size() { return midiDev == null ? 0 : midiDev.getDeviceInfo().getMidiPortCount(); }
	
	@Override
	public Object
	get(int position) { return midiDev.getDeviceInfo().getMidiPort(position); }
	
	public String
	getEmptyItemText() { return i18n.getLabel("MidiPortSpinnerAdapter.selectPort"); }
	
	public String
	getItemText(int position) {
		return ((MidiPort)getItem(position)).getName();
	}
	
	public void
	setMidiDevice(int devId) {
		if(midiDev == null) {
			if(devId == -1) return;
			changeMidiDevice(CC.getSamplerModel().getMidiDeviceById(devId));
		} else {
			MidiDeviceModel newDev;
			if(devId == -1) {
				newDev = null;
			} else {
				newDev = CC.getSamplerModel().getMidiDeviceById(devId);
				if(newDev == null) Log.w("MidiPortSpinnerAdapter", "Unknown MIDI device - this is a bug!");
			}
			if(newDev != null && midiDev.getDeviceId() == newDev.getDeviceId()) return;
			
			changeMidiDevice(newDev);
		}
	}
	
	private void
	changeMidiDevice(MidiDeviceModel newDev) {
		if(midiDev != null) midiDev.removeMidiDeviceListener(this);
		midiDev = newDev; 
		if(midiDev != null) midiDev.addMidiDeviceListener(this);
		notifyDataSetChanged();
	}
	
	@Override
	public void
	settingsChanged(MidiDeviceEvent e) {
		notifyDataSetChanged();
	}
}
