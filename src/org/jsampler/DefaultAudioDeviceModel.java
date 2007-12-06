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

import java.util.Vector;

import javax.swing.SwingUtilities;

import org.jsampler.event.AudioDeviceEvent;
import org.jsampler.event.AudioDeviceListener;

import org.jsampler.task.Audio;

import org.linuxsampler.lscp.AudioOutputDevice;
import org.linuxsampler.lscp.Parameter;


/**
 * This class provides default implementation of the <code>AudioDeviceModel</code> interface.
 * @author Grigor Iliev
 */
public class DefaultAudioDeviceModel implements AudioDeviceModel {
	private AudioOutputDevice audioDevice;
	
	private final Vector<AudioDeviceListener> listeners = new Vector<AudioDeviceListener>();
	
	/**
	 * Creates a new instance of <code>DefaultAudioDeviceModel</code> using the
	 * specified non-null audio device.
	 * @param audioDevice An <code>AudioOutputDevice</code> instance providing the current
	 * settings of the audio device which will be represented by this model.
	 * @throws IllegalArgumentException If <code>audioDevice</code> is <code>null</code>.
	 */
	public
	DefaultAudioDeviceModel(AudioOutputDevice audioDevice) {
		if(audioDevice == null)
			throw new IllegalArgumentException("audioDevice must be non null");
		
		this.audioDevice = audioDevice;
	}
	
	/**
	 * Registers the specified listener to be notified when
	 * the settings of the audio device are changed.
	 * @param l The <code>AudioDeviceListener</code> to register.
	 */
	public void
	addAudioDeviceListener(AudioDeviceListener l) { listeners.add(l); }
	
	/**
	 * Removes the specified listener.
	 * @param l The <code>AudioDeviceListener</code> to remove.
	 */
	public void
	removeAudioDeviceListener(AudioDeviceListener l) { listeners.remove(l); }
	
	/**
	 * Gets the numerical ID of this audio device.
	 * @return The numerical ID of this audio device or
	 * -1 if the device number is not set.
	 */
	public int
	getDeviceId() { return audioDevice.getDeviceId(); }
	
	/**
	 * Gets the current settings of the audio device represented by this model.
	 * @return <code>AudioOutputDevice</code> instance providing
	 * the current settings of the audio device represented by this model.
	 */
	public AudioOutputDevice
	getDeviceInfo() { return audioDevice; }
	
	/**
	 * Updates the settings of the audio device represented by this model.
	 * @param device The new audio device settings.
	 */
	public void
	setDeviceInfo(AudioOutputDevice device) {
		audioDevice = device;
		fireSettingsChanged();
	}
	
	/**
	 * Sets whether the audio device is enabled or disabled.
	 * @param active If <code>true</code> the audio device is enabled,
	 * else the device is disabled.
	 */
	public void
	setActive(boolean active) {
		if(active == getDeviceInfo().isActive()) return;
		
		audioDevice.setActive(active);
		fireSettingsChanged();
	}
	
	/**
	 * Determines whether the audio device is active.
	 * @return <code>true</code> if the device is enabled and <code>false</code> otherwise.
	 */
	public boolean
	isActive() { return audioDevice.isActive(); }
	
	/**
	 * Schedules a new task for enabling/disabling the audio device.
	 * @param active If <code>true</code> the audio device is enabled,
	 * else the device is disabled.
	 */
	public void
	setBackendActive(boolean active) {
		CC.getTaskQueue().add(new Audio.EnableDevice(getDeviceId(), active));
	}
	
	/**
	 * Schedules a new task for altering
	 * a specific setting of the audio output device.
	 * @param prm The parameter to be set.
	 */
	public void
	setBackendDeviceParameter(Parameter prm) {
		CC.getTaskQueue().add(new Audio.SetDeviceParameter(getDeviceId(), prm));
	}
	
	/**
	 * Schedules a new task for changing the channel number of the audio device.
	 * @param channels The new number of audio channels.
	 */
	public void
	setBackendChannelCount(int channels) {
		CC.getTaskQueue().add(new Audio.SetChannelCount(getDeviceId(), channels));
	}
	
	/**
	 * Schedules a new task for altering a specific
	 * setting of the specified audio output channel.
	 * @param channel The channel number.
	 * @param prm The parameter to be set.
	 */
	public void
	setBackendChannelParameter(int channel, Parameter prm) {
		CC.getTaskQueue().add(new Audio.SetChannelParameter(getDeviceId(), channel, prm));
	}
	
	/**
	 * Notifies listeners that the settings of the audio device are changed.
	 */
	private void
	fireSettingsChanged() {
		SwingUtilities.invokeLater(new Runnable() {
			public void
			run() {
				AudioDeviceModel model = DefaultAudioDeviceModel.this;
				fireSettingsChanged(new AudioDeviceEvent(model, model));
			}
		});
	}
	
	/**
	 * Notifies listeners that the settings of the audio device are changed.
	 * This method should be invoked from the event-dispatching thread.
	 */
	private void
	fireSettingsChanged(final AudioDeviceEvent e) {
		CC.getSamplerModel().setModified(true);
		for(AudioDeviceListener l : listeners) l.settingsChanged(e);
	}
}
