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
import org.linuxsampler.lscp.Parameter;

import static org.jsampler.JSI18n.i18n;


/**
 * This task creates a new audio output device.
 * @author Grigor Iliev
 */
public class CreateAudioDevice extends EnhancedTask<Integer> {
	private String driver;
	private Parameter[] parameters;
	
	
	/**
	 * Creates a new instance of <code>CreateAudioDevice</code>.
	 * @param driver The desired audio output system.
	 * @param parameters An optional list of driver specific parameters.
	 */
	public
	CreateAudioDevice(String driver, Parameter... parameters) {
		setTitle("CreateAudioDevice_task");
		setDescription(i18n.getMessage("CreateAudioDevice.description"));
		
		this.driver = driver;
		this.parameters = parameters;
	}
	
	/** The entry point of the task. */
	public void
	run() {
		try {
			Integer deviceID =
				CC.getClient().createAudioOutputDevice(driver, parameters);
			setResult(deviceID);
			
			// TODO: This must be done through the LinuxSampler notification system
			CC.getSamplerModel().addAudioDevice (
				CC.getClient().getAudioOutputDeviceInfo(deviceID)
			);
		} catch(Exception x) {
			setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
			CC.getLogger().log(Level.FINE, getErrorMessage(), x);
		}
	}
}
