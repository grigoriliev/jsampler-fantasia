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

package org.jsampler.event;

import org.jsampler.MidiInstrument;

import org.linuxsampler.lscp.MidiInstrumentEntry;


/**
 * A semantic event which indicates MIDI instrument map changes.
 * @author Grigor Iliev
 */
public class MidiInstrumentMapEvent extends java.util.EventObject {
	private MidiInstrument instrument;
	private MidiInstrumentEntry entry;
	
	/**
	 * Constructs a <code>MidiInstrumentMapEvent</code> object.
	 * @param source The object that originated the event.
	 */
	public
	MidiInstrumentMapEvent(Object source) { this(source, null, null); }
	
	/**
	 * Constructs a <code>MidiInstrumentMapEvent</code> object.
	 * @param source The object that originated the event.
	 * @param entry Specifies the position of a MIDI instrument in a MIDI instrument map.
	 * @param instrument The MIDI instrument that has been added or removed.
	 */
	public
	MidiInstrumentMapEvent(Object source, MidiInstrumentEntry entry, MidiInstrument instrument) {
		super(source);
		this.instrument = instrument;
		this.entry = entry;
	}
	
	/**
	 * Returns the entry associated with the added or removed MIDI instrument.
	 * @return entry associated with the added or removed MIDI instrument;
	 * <code>null</code> otherwise.
	 */
	public MidiInstrumentEntry
	getEntry() { return entry; }
	
	/**
	 * Returns the MIDI instrument that has been added or removed.
	 * @return The MIDI instrument that has been added or removed;
	 * <code>null</code> otherwise.
	 */
	public MidiInstrument
	getInstrument() { return instrument; }
}
