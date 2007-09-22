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

import org.jsampler.event.AudioDeviceListener;

import org.linuxsampler.lscp.AudioOutputDevice;
import org.linuxsampler.lscp.Parameter;


/**
 * A data model for an audio output device.
 * @author Grigor Iliev
 */
public interface AudioDeviceModel {
	/**
	 * Registers the specified listener to be notified when
	 * the settings of the audio device are changed.
	 * @param l The <code>AudioDeviceListener</code> to register.
	 */
	public void
	addAudioDeviceListener(AudioDeviceListener l);
	
	/**
	 * Removes the specified listener.
	 * @param l The <code>AudioDeviceListener</code> to remove.
	 */
	public void removeAudioDeviceListener(AudioDeviceListener l);
	
	/**
	 * Gets the numerical ID of this audio device.
	 * @return The numerical ID of this audio device or
	 * -1 if the device number is not set.
	 */
	public int getDeviceId();
	
	/**
	 * Gets the current settings of the audio device represented by this model.
	 * @return <code>AudioOutputDevice</code> instance providing
	 * the current settings of the audio device represented by this model.
	 */
	public AudioOutputDevice getDeviceInfo();
	
	/**
	 * Updates the settings of the audio device represented by this model.
	 * @param device The new audio device settings.
	 */
	public void setDeviceInfo(AudioOutputDevice device);
	
	/**
	 * Sets whether the audio device is enabled or disabled.
	 * @param active If <code>true</code> the audio device is enabled,
	 * else the device is disabled.
	 */
	public void setActive(boolean active);
	
	/**
	 * Determines whether the audio device is active.
	 * @return <code>true</code> if the device is enabled and <code>false</code> otherwise.
	 */
	public boolean isActive();
	
	/**
	 * Schedules a new task for enabling/disabling the audio device.
	 * @param active If <code>true</code> the audio device is enabled,
	 * else the device is disabled.
	 */
	public void setBackendActive(boolean active);
	
	/**
	 * Schedules a new task for altering
	 * a specific setting of the audio output device.
	 * @param prm The parameter to be set.
	 */
	public void setBackendDeviceParameter(Parameter prm);
	
	/**
	 * Schedules a new task for changing the channel number of the audio device.
	 * @param channels The new number of audio channels.
	 */
	public void setBackendChannelCount(int channels);
	
	/**
	 * Schedules a new task for altering a specific
	 * setting of the specified audio output channel.
	 * @param channel The channel number.
	 * @param prm The parameter to be set.
	 */
	public void setBackendChannelParameter(int channel, Parameter prm);
}
