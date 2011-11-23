/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2011 Grigor Iliev <grigor@grigoriliev.com>
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

import net.sf.juife.PDUtils;

import org.jsampler.event.MidiDeviceEvent;
import org.jsampler.event.MidiDeviceListener;

import org.jsampler.task.Midi;

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
	@Override
	public void
	addMidiDeviceListener(MidiDeviceListener l) { listeners.add(l); }
	
	/**
	 * Removes the specified listener.
	 * @param l The <code>MidiDeviceListener</code> to remove.
	 */
	@Override
	public void
	removeMidiDeviceListener(MidiDeviceListener l) { listeners.remove(l); }
	
	/**
	 * Gets the numerical ID of this MIDI device.
	 * @return The numerical ID of this MIDI device or
	 * -1 if the device number is not set.
	 */
	@Override
	public int
	getDeviceId() { return midiDevice.getDeviceId(); }
	
	/**
	 * Gets the current settings of the MIDI device represented by this model.
	 * @return <code>MidiInputDevice</code> instance providing
	 * the current settings of the MIDI device represented by this model.
	 */
	@Override
	public MidiInputDevice
	getDeviceInfo() { return midiDevice; }
	
	/**
	 * Updates the settings of the MIDI device represented by this model.
	 * @param device The new MIDI device settings.
	 */
	@Override
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
	@Override
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
	@Override
	public boolean
	isActive() { return midiDevice.isActive(); }
	
	/**
	 * Schedules a new task for enabling/disabling the MIDI device.
	 * @param active If <code>true</code> the MIDI device is enabled,
	 * else the device is disabled.
	 */
	@Override
	public void
	setBackendActive(boolean active) {
		CC.getTaskQueue().add(new Midi.EnableDevice(getDeviceId(), active));
	}
	
	/**
	 * Schedules a new task for altering
	 * a specific setting of the MIDI input device.
	 * @param prm The parameter to be set.
	 */
	@Override
	public void
	setBackendDeviceParameter(Parameter prm) {
		CC.getTaskQueue().add(new Midi.SetDeviceParameter(getDeviceId(), prm));
	}
	
	/**
	 * Schedules a new task for changing the port number of the MIDI device.
	 * @param ports The new number of ports.
	 */
	@Override
	public void
	setBackendPortCount(int ports) {
		CC.getTaskQueue().add(new Midi.SetPortCount(getDeviceId(), ports));
	}
	
	/**
	 * Schedules a new task for altering a specific
	 * setting of the specified MIDI input port.
	 * @param port The port number.
	 * @param prm The parameter to be set.
	 */
	@Override
	public void
	setBackendPortParameter(int port, Parameter prm) {
		CC.getTaskQueue().add(new Midi.SetPortParameter(getDeviceId(), port, prm));
	}
	
	/**
	 * Notifies listeners that the settings of the MIDI device are changed.
	 */
	private void
	fireSettingsChanged() {
		PDUtils.runOnUiThread(new Runnable() {
			public void
			run() {
				MidiDeviceModel model = DefaultMidiDeviceModel.this;
				fireSettingsChanged(new MidiDeviceEvent(model, model));
			}
		});
	}
	
	/**
	 * Notifies listeners that the settings of the MIDI device are changed.
	 * This method should be invoked from the event-dispatching thread.
	 */
	private void
	fireSettingsChanged(final MidiDeviceEvent e) {
		CC.getSamplerModel().setModified(true);
		for(MidiDeviceListener l : listeners) l.settingsChanged(e);
	}
}
