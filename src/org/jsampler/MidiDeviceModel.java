/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2006 Grigor Iliev <grigor@grigoriliev.com>
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

import org.linuxsampler.lscp.MidiInputDevice;
import org.linuxsampler.lscp.Parameter;

import org.jsampler.event.MidiDeviceListener;


/**
 * A data model for a MIDI input device.
 * @author Grigor Iliev
 */
public interface MidiDeviceModel {
	/**
	 * Registers the specified listener to be notified when
	 * the settings of the MIDI device are changed.
	 * @param l The <code>MidiDeviceListListener</code> to register.
	 */
	public void addMidiDeviceListener(MidiDeviceListener l);
	
	/**
	 * Removes the specified listener.
	 * @param l The <code>MidiDeviceListener</code> to remove.
	 */
	public void removeMidiDeviceListener(MidiDeviceListener l);
	
	/**
	 * Gets the numerical ID of this MIDI device.
	 * @return The numerical ID of this MIDI device or
	 * -1 if the device number is not set.
	 */
	public int getDeviceId();
	
	/**
	 * Gets the current settings of the MIDI device represented by this model.
	 * @return <code>MidiInputDevice</code> instance providing
	 * the current settings of the MIDI device represented by this model.
	 */
	public MidiInputDevice getDeviceInfo();
	
	/**
	 * Updates the settings of the MIDI device represented by this model.
	 * @param device The new MIDI device settings.
	 */
	public void setDeviceInfo(MidiInputDevice device);
	
	/**
	 * Determines whether the MIDI device is active.
	 * @return <code>true</code> if the device is enabled and <code>false</code> otherwise.
	 */
	public boolean isActive();
	
	/**
	 * Sets whether the MIDI device is enabled or disabled.
	 * @param active If <code>true</code> the MIDI device is enabled,
	 * else the device is disabled.
	 */
	public void setActive(boolean active);
	
	/**
	 * Schedules a new task for enabling/disabling the MIDI device.
	 * @param active If <code>true</code> the MIDI device is enabled,
	 * else the device is disabled.
	 */
	public void setBackendActive(boolean active);
	
	/**
	 * Schedules a new task for altering
	 * a specific setting of the MIDI input device.
	 * @param prm The parameter to be set.
	 */
	public void setBackendDeviceParameter(Parameter prm);
	
	/**
	 * Schedules a new task for changing the port number of the MIDI device.
	 * @param ports The new number of ports.
	 */
	public void setBackendPortCount(int ports);
	
	/**
	 * Schedules a new task for altering a specific
	 * setting of the specified MIDI input port.
	 * @param port The port number.
	 * @param prm The parameter to be set.
	 */
	public void setBackendPortParameter(int port, Parameter prm);
}
