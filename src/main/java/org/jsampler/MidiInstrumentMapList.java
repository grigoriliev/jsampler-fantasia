/*
 *   JSampler - a front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2023 Grigor Iliev <grigor@grigoriliev.com>
 *
 *   This file is part of JSampler.
 *
 *   JSampler is free software: you can redistribute it and/or modify it under
 *   the terms of the GNU General Public License as published by the Free
 *   Software Foundation, either version 3 of the License, or (at your option)
 *   any later version.
 *
 *   JSampler is distributed in the hope that it will be useful, but WITHOUT
 *   ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *   FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 *   more details.
 *
 *   You should have received a copy of the GNU General Public License along
 *   with JSampler. If not, see <https://www.gnu.org/licenses/>. 
 */

package org.jsampler;

import java.util.Vector;

import org.jsampler.event.ListEvent;
import org.jsampler.event.ListListener;


/**
 *
 * @author Grigor Iliev
 */
public class MidiInstrumentMapList {
	private final Vector<MidiInstrumentMap> maps = new Vector<MidiInstrumentMap>();
	private final Vector<ListListener<MidiInstrumentMap>> listeners =
		new Vector<ListListener<MidiInstrumentMap>>();
	
	/** Creates a new instance of MidiInstrumentMapList */
	public MidiInstrumentMapList() {
	}
	
	/**
	 * Registers the specified listener for receiving event messages.
	 * @param l The <code>ListListener</code> to register.
	 */
	public void
	addMidiInstrumentMapListListener(ListListener<MidiInstrumentMap> l) {
		listeners.add(l);
	}
	
	/**
	 * Removes the specified listener.
	 * @param l The <code>ListListener</code> to remove.
	 */
	public void
	removeMidiInstrumentMapListListener(ListListener<MidiInstrumentMap> l) {
		listeners.remove(l);
	}
	
	/**
	 * Gets the current number of MIDI instrument maps in the list.
	 * @return The current number of MIDI instrument maps in the list.
	 */
	public int
	getMidiInstrumentMapCount() { return maps.size(); }
	
	/**
	 * Gets the MIDI instrument map at the specified position.
	 * @param idx The index of the MIDI instrument map to be returned.
	 * @return The MIDI instrument map at the specified position.
	 */
	public MidiInstrumentMap
	getMidiInstrumentMap(int idx) { return maps.get(idx); }
	
	/**
	 * Adds the specified MIDI instrument map to the list.
	 * @param map The MIDI instrument map to be added.
	 * @throws IllegalArgumentException If <code>map</code> is <code>null</code>.
	 */
	public void
	addMidiInstrumentMap(MidiInstrumentMap map) {
		
	}
	
	/**
	 * Removes the specified MIDI instrument map from the list.
	 * @param idx The index of the MIDI instrument map to remove.
	 */
	public void
	removeMidiInstrumentMap(int idx) {
		
	}
	
	
	
	
	
}
