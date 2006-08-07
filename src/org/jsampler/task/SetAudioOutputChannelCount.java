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

package org.jsampler.task;

import java.util.logging.Level;

import org.jsampler.CC;
import org.jsampler.HF;

import org.linuxsampler.lscp.AudioOutputDevice;

import static org.jsampler.JSI18n.i18n;


/**
 * This task changes the channel number of the speicifed audio output device.
 * @author Grigor Iliev
 */
public class SetAudioOutputChannelCount extends EnhancedTask {
	private int deviceID;
	private int channels;
	
	/**
	 * Creates new instance of <code>SetAudioOutputChannelCount</code>.
	 * @param deviceID The id of the device whose channels number will be changed.
	 * @param channels The new number of audio channels.
	 */
	public
	SetAudioOutputChannelCount(int deviceID, int channels) {
		setTitle("SetAudioOutputChannelCount_task");
		setDescription(i18n.getMessage("SetAudioOutputChannelCount.description", deviceID));
		
		this.deviceID = deviceID;
		this.channels = channels;
	}
	
	/** The entry point of the task. */
	public void
	run() {
		try { 
			CC.getClient().setAudioOutputChannelCount(deviceID, channels);
			
			// TODO: This must be done through the LinuxSampler notification system.
			AudioOutputDevice d = CC.getClient().getAudioOutputDeviceInfo(deviceID);
			CC.getSamplerModel().getAudioDeviceModel(deviceID).setDeviceInfo(d);
		} catch(Exception x) {
			setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
			CC.getLogger().log(Level.FINE, getErrorMessage(), x);
		}
	}
}
