/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005 Grigor Kirilov Iliev
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

package org.jsampler.event;

import org.jsampler.MidiDeviceModel;


/**
 * A semantic event which indicates changes of a MIDI device list.
 * @author Grigor Iliev
 */
public class MidiDeviceListEvent extends java.util.EventObject {
	private MidiDeviceModel midiDeviceModel;
	
	/**
	 * Constructs a <code>MidiDeviceListEvent</code> object.
	 *
	 * @param source The object that originated the event.
	 * @param midiDeviceModel The model of the MIDI device for which this event occurs.
	 */
	public
	MidiDeviceListEvent(Object source, MidiDeviceModel midiDeviceModel) {
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
