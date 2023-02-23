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
import org.jsampler.event.MidiDeviceListEvent;
import org.jsampler.event.MidiDeviceListListener;

public class MidiDeviceSpinnerAdapter extends AbstractSpinnerAdapter<MidiDeviceModel>
				   implements MidiDeviceListListener {
	
	public MidiDeviceSpinnerAdapter() {
		CC.getSamplerModel().addMidiDeviceListListener(this);
	}
	
	public void
	uninstall() { CC.getSamplerModel().removeMidiDeviceListListener(this); }
	
	@Override
	public int
	size() { return CC.getSamplerModel().getMidiDeviceCount(); }
	
	@Override
	public Object
	get(int position) { return CC.getSamplerModel().getMidiDevice(position); }
	
	public String
	getEmptyItemText() { return i18n.getLabel("MidiDeviceSpinnerAdapter.selectDevice"); }
	
	public String
	getItemText(int position) {
		MidiDeviceModel dev = (MidiDeviceModel)getItem(position);
		return i18n.getLabel("MidiDeviceSpinnerAdapter.device", dev.getDeviceId());
	}
	
	@Override
	public boolean
	compare(MidiDeviceModel d1, MidiDeviceModel d2) {
		if(d1 == null || d2 == null) return false;
		return d1.getDeviceId() == d2.getDeviceId();
	}
	
	@Override
	public void
	deviceAdded(MidiDeviceListEvent e) { notifyDataSetChanged(); }
	
	@Override
	public void
	deviceRemoved(MidiDeviceListEvent e) { notifyDataSetChanged(); }
}
