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

import org.jsampler.MidiDeviceModel;


/**
 * A semantic event which indicates changes of a MIDI device settings.
 * @author Grigor Iliev
 */
public class MidiDeviceEvent extends java.util.EventObject {
	private MidiDeviceModel midiDeviceModel;
	
	/**
	 * Constructs a <code>MidiDeviceEvent</code> object.
	 *
	 * @param source The object that originated the event.
	 * @param midiDeviceModel The model of the MIDI device for which this event occurs.
	 */
	public MidiDeviceEvent(Object source, MidiDeviceModel midiDeviceModel) {
		super(source);
		this.midiDeviceModel = midiDeviceModel;
	}
	
	/**
	 * Gets the MIDI device model for which this event occurs.
	 * @return The MIDI device model for which this event occurs.
	 */
	public MidiDeviceModel
	getMidiDeviceModel() { return midiDeviceModel; }
}
