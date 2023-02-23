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
import org.jsampler.MidiInstrumentMap;
import org.jsampler.event.ListEvent;
import org.jsampler.event.ListListener;

public class MidiMapSpinnerAdapter extends AbstractSpinnerAdapter<Object>
				implements ListListener<MidiInstrumentMap> {
	
	public static final String noMap = new String(i18n.getLabel("MidiMapSpinnerAdapter.noMap"));
	public static final String defaultMap = new String(i18n.getLabel("MidiMapSpinnerAdapter.defaultMap"));
	
	public MidiMapSpinnerAdapter() {
		CC.getSamplerModel().addMidiInstrumentMapListListener(this);
	}
	
	public void
	uninstall() { CC.getSamplerModel().removeMidiInstrumentMapListListener(this); }
	
	@Override
	public int
	size() { return CC.getSamplerModel().getMidiInstrumentMapCount() + 2; }
	
	@Override
	public Object
	get(int position) {
		if(position == 0) return noMap;
		if(position == 1) return defaultMap;
		return CC.getSamplerModel().getMidiInstrumentMap(position - 2);
	}
	
	public String
	getEmptyItemText() { return i18n.getLabel("MidiMapSpinnerAdapter.selectMap"); }
	
	public String
	getItemText(int position) {
		return getItem(position).toString();
	}
	
	@Override
	public boolean
	compare(Object o1, Object o2) {
		if(o1 == null || o2 == null) return false;
		if(o1 instanceof MidiInstrumentMap) {
			if(o2 instanceof MidiInstrumentMap) {
				return ((MidiInstrumentMap)o1).getMapId() == ((MidiInstrumentMap)o2).getMapId();
			} else {
				return false;
			}
		}
		
		return o1 == o2; // default map, no map
	}
	
	/** Invoked when a new MIDI instrument map is added to a list. */
	@Override
	public void
	entryAdded(ListEvent<MidiInstrumentMap> e) { notifyDataSetChanged(); }

	/** Invoked when a new MIDI instrument map is removed from a list. */
	@Override
	public void
	entryRemoved(ListEvent<MidiInstrumentMap> e) { notifyDataSetChanged(); }
}
