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

package org.jsampler;

import java.util.Vector;

import javax.swing.SwingUtilities;

import org.jsampler.event.MidiDeviceEvent;
import org.jsampler.event.MidiDeviceListener;

import org.linuxsampler.lscp.BoolParameter;
import org.linuxsampler.lscp.MidiInputDevice;
import org.linuxsampler.lscp.Parameter;


/**
 * The Default implementation of the <code>MidiDeviceModel</code> interface.
 * @author Grigor Iliev
 */
public class DefaultMidiDeviceModel implements MidiDeviceModel {
	private MidiInputDevice midiDevice;
	
	private final Vector<MidiDeviceListener> listeners = new Vector<MidiDeviceListener>();
	
	/**
	 * Creates a new instance of <code>DefaultMidiDeviceModel</code> using the
	 * specified non-null MIDI device.
	 * @param midiDevice A <code>MidiInputDevice</code> instance providing the current
	 * settings of the MIDI device which will be represented by this model.
	 * @throws IllegalArgumentException If <code>midiDevice</code> is <code>null</code>.
	 */
	public
	DefaultMidiDeviceModel(MidiInputDevice midiDevice) {
		if(midiDevice == null)
			throw new IllegalArgumentException("midiDevice must be non null");
		
		this.midiDevice = midiDevice;
	}
	
	/**
	 * Registers the specified listener to be notified when
	 * the settings of the MIDI device are changed.
	 * @param l The <code>MidiDeviceListener</code> to register.
	 */
	public void
	addMidiDeviceListener(MidiDeviceListener l) { listeners.add(l); }
	
	/**
	 * Removes the specified listener.
	 * @param l The <code>MidiDeviceListener</code> to remove.
	 */
	public void
	removeMidiDeviceListener(MidiDeviceListener l) { listeners.remove(l); }
	
	/**
	 * Gets the numerical ID of this MIDI device.
	 * @return The numerical ID of this MIDI device or
	 * -1 if the device number is not set.
	 */
	public int
	getDeviceID() { return midiDevice.getDeviceID(); }
	
	/**
	 * Gets the current settings of the MIDI device represented by this model.
	 * @return <code>MidiInputDevice</code> instance providing
	 * the current settings of the MIDI device represented by this model.
	 */
	public MidiInputDevice
	getDeviceInfo() { return midiDevice; }
	
	/**
	 * Updates the settings of the MIDI device represented by this model.
	 * @param device The new MIDI device settings.
	 */
	public void
	setDeviceInfo(MidiInputDevice device) {
		midiDevice = device;
		fireSettingsChanged();
	}
	
	/**
	 * Sets whether the MIDI device is enabled or disabled.
	 * @param active If <code>true</code> the MIDI device is enabled,
	 * else the device is disabled.
	 */
	public void
	setActive(boolean active) {
		if(active == getDeviceInfo().isActive()) return;
		
		midiDevice.setActive(active);
		fireSettingsChanged();
	}
	
	/**
	 * Determines whether the MIDI device is active.
	 * @return <code>true</code> if the device is enabled and <code>false</code> otherwise.
	 */
	public boolean
	isActive() { return midiDevice.isActive(); }
	
	/**
	 * Notifies listeners that the settings of the MIDI device are changed.
	 */
	private void
	fireSettingsChanged() {
		fireSettingsChanged(new MidiDeviceEvent(this, this));
	}
	
	/**
	 * Notifies listeners that the settings of the MIDI device are changed.
	 * This method should be invoked from the event-dispatching thread.
	 */
	private void
	fireSettingsChanged(final MidiDeviceEvent e) {
		SwingUtilities.invokeLater(new Runnable() {
			public void
			run() { for(MidiDeviceListener l : listeners) l.settingsChanged(e); }
		});
	}
}
