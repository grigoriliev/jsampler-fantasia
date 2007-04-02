/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2007 Grigor Iliev <grigor@grigoriliev.com>
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

package org.jsampler;

import java.util.TreeMap;
import java.util.Vector;

import javax.swing.SwingUtilities;

import org.jsampler.event.MidiInstrumentMapEvent;
import org.jsampler.event.MidiInstrumentMapListener;

import org.linuxsampler.lscp.MidiInstrumentEntry;
import org.linuxsampler.lscp.MidiInstrumentMapInfo;


/**
 * Represents a MIDI instrument map used for mapping instruments
 * to corresponding MIDI bank select and MIDI program change messages.
 * @author Grigor Iliev
 */
public class MidiInstrumentMap {
	MidiInstrumentMapInfo info;
	
	private final TreeMap<MidiInstrumentEntry, MidiInstrument> instrMap =
		new TreeMap<MidiInstrumentEntry, MidiInstrument>();
	
	private final Vector<MidiInstrumentMapListener> listeners =
		new Vector<MidiInstrumentMapListener>();
	
	
	/**
	 * Creates a new instance of <code>MidiInstrumentMap</code>.
	 * @param info Provides the map's properties.
	 */
	public
	MidiInstrumentMap(MidiInstrumentMapInfo info) { this.info = info; }
	
	/**
	 * Registers the specified listener for receiving event messages.
	 * @param l The <code>MidiInstrumentMapListener</code> to register.
	 */
	public void
	addMidiInstrumentMapListener(MidiInstrumentMapListener l) { listeners.add(l); }
	
	/**
	 * Removes the specified listener.
	 * @param l The <code>MidiInstrumentMapListener</code> to remove.
	 */
	public void
	removeMidiInstrumentMapListener(MidiInstrumentMapListener l) { listeners.remove(l); }
	
	/*
	 * Creates a new instance of MidiInstrumentMap.
	 * @param mapId The ID of this MIDI instrument map.
	 * @param name The name of this MIDI instrument map.
	 *
	public
	MidiInstrumentMap(int mapId, String name) {
		this.mapId = mapId;
		setName(name);
	}*/
	
	/** Gets the ID of this MIDI instrument map. */
	public int
	getMapId() { return info.getMapId(); }
	
	/**
	 * Gets the name of this MIDI instrument map.
	 * @return The name of this MIDI instrument map.
	 */
	public String
	getName() { return info.getName(); }
	
	/**
	 * Sets the name of this MIDI instrument map.
	 * @param name The new name of this MIDI instrument map.
	 */
	public void
	setName(String name) {
		if(info.getName().equals(name)) return;
		info.setName(name);
		fireNameChanged();
	}
	
	/**
	 * Gets the information about this MIDI instrument map.
	 * @return The information about this MIDI instrument map.
	 */
	public MidiInstrumentMapInfo
	getInfo() { return info; }
	
	/**
	 * Sets the information about this MIDI instrument map.
	 * @param info The new information about this MIDI instrument map.
	 */
	public void
	setInfo(MidiInstrumentMapInfo info) {
		this.info = info;
		fireNameChanged();
	}
	
	/**
	 * Gets the indices of all MIDI banks that contain at least one instrument.
	 * @return The indices of all MIDI banks that contain at least one instrument.
	 */
	public Integer[]
	getMidiBanks() {
		Vector<Integer> v = new Vector<Integer>();
		
		for(MidiInstrumentEntry e : instrMap.keySet()) {
			if(v.isEmpty()) v.add(e.getMidiBank());
			else {
				if(e.getMidiBank() < v.lastElement())
					throw new RuntimeException("Unsorted map!");
				
				if(e.getMidiBank() > v.lastElement()) v.add(e.getMidiBank());
			}
		}
		
		return v.toArray(new Integer[v.size()]);
	}
	
	/**
	 * Gets the instrument in the specified MIDI bank with the specified program number.
	 * @param bank The index of the MIDI bank, containing the requested instrument.
	 * @param program The program number of the requested instrument.
	 * @return The instrument in MIDI bank <code>bank</code> with
	 * program number <code>program</code>, or <code>null</code> if
	 * there is no such instrument in the map.
	 */
	public MidiInstrument
	getMidiInstrument(int bank, int program) {
		return instrMap.get(new MidiInstrumentEntry(bank, program));
	}
	
	/**
	 * Gets all instruments contained in the specified MIDI bank.
	 * @param bankIndex The index of the MIDI bank, whose instruments should be obtained.
	 * @return All instruments contained in the specified MIDI bank.
	 */
	public MidiInstrument[]
	getMidiInstruments(int bankIndex) {
		Vector<MidiInstrument> v = new Vector<MidiInstrument>();
		
		for(MidiInstrumentEntry e : instrMap.keySet()) {
			if(e.getMidiBank() == bankIndex) v.add(instrMap.get(e));
		}
		
		return v.toArray(new MidiInstrument[v.size()]);
	}
	
	/**
	 * Gets all instruments contained in this MIDI instrument map.
	 * @return All instruments contained in this MIDI instrument map.
	 */
	public MidiInstrument[]
	getAllMidiInstruments() {
		Vector<MidiInstrument> v = new Vector<MidiInstrument>();
		
		for(MidiInstrument i : instrMap.values()) v.add(i);
		
		return v.toArray(new MidiInstrument[v.size()]);
	}
	
	/**
	 * Creates a new or replaces an existing entry in this MIDI instrument map.
	 */
	public void
	mapMidiInstrument(MidiInstrumentEntry entry, MidiInstrument instrument) {
		MidiInstrument mi = instrMap.remove(entry);
		if(mi != null) fireInstrumentRemoved(entry, mi);
		instrMap.put(entry, instrument);
		fireInstrumentAdded(entry, instrument);
	}
	
	/**
	 * Removes an entry from this MIDI instrument map.
	 * @param entry The entry to remove.
	 * @return The MIDI instrument associated with the specified entry or
	 * <code>null</code> if there was no mapping for that entry. 
	 */
	public MidiInstrument
	unmapMidiInstrument(MidiInstrumentEntry entry) {
		MidiInstrument mi = instrMap.remove(entry);
		if(mi != null) fireInstrumentRemoved(entry, mi);
		return mi;
	}
	
	/**
	 * Returns the name of this map.
	 * @return The name of this map.
	 */
	public String
	toString() { return getName(); }
	
	/**
	 * Notifies listeners that the name of the MIDI instrument map has changed.
	 * Note that this method can be invoked outside the event-dispatching thread.
	 */
	private void
	fireNameChanged() {
		final MidiInstrumentMapEvent e = new MidiInstrumentMapEvent(this);
		
		SwingUtilities.invokeLater(new Runnable() {
			public void
			run() { fireNameChanged(e); }
		});
	}
	
	/** Notifies listeners that the name of the MIDI instrument map has changed. */
	private void
	fireNameChanged(MidiInstrumentMapEvent e) {
		for(MidiInstrumentMapListener l : listeners) l.nameChanged(e);
	}
	
	/**
	 * Notifies listeners that a MIDI instrument has been
	 * added to this MIDI instrument map.
	 * Note that this method can be invoked outside the event-dispatching thread
	 */
	private void
	fireInstrumentAdded(MidiInstrumentEntry entry, MidiInstrument instrument) {
		final MidiInstrumentMapEvent e =
			new MidiInstrumentMapEvent(this, entry, instrument);
		
		SwingUtilities.invokeLater(new Runnable() {
			public void
			run() { fireInstrumentAdded(e); }
		});
	}
	
	/**
	 * Notifies listeners that a MIDI instrument has been
	 * added to this MIDI instrument map.
	 */
	private void
	fireInstrumentAdded(MidiInstrumentMapEvent e) {
		for(MidiInstrumentMapListener l : listeners) l.instrumentAdded(e);
	}
	
	/**
	 * Notifies listeners that a MIDI instrument has been
	 * removed from this MIDI instrument map.
	 */
	private void
	fireInstrumentRemoved(MidiInstrumentEntry entry, MidiInstrument instrument) {
		final MidiInstrumentMapEvent e =
			new MidiInstrumentMapEvent(this, entry, instrument);
		
		SwingUtilities.invokeLater(new Runnable() {
			public void
			run() { fireInstrumentRemoved(e); }
		});
	}
	
	/**
	 * Notifies listeners that a MIDI instrument has been
	 * removed from this MIDI instrument map.
	 */
	private void
	fireInstrumentRemoved(MidiInstrumentMapEvent e) {
		for(MidiInstrumentMapListener l : listeners) l.instrumentRemoved(e);
	}
}
