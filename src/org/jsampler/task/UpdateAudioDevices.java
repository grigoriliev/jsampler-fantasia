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
import org.jsampler.SamplerModel;

import org.jsampler.AudioDeviceModel;

import static org.jsampler.JSI18n.i18n;


/**
 * This task updates the audio device list and all audio devices' settings.
 * @author Grigor Iliev
 */
public class UpdateAudioDevices extends EnhancedTask {
	/** Creates a new instance of <code>UpdateAudioDevices</code>. */
	public
	UpdateAudioDevices() {
		setTitle("UpdateAudioDevices_task");
		setDescription(i18n.getMessage("UpdateAudioDevices.description"));
	}
	
	/** The entry point of the task. */
	public void
	run() {
		try { 
			SamplerModel sm = CC.getSamplerModel();
			Integer[] deviceIDs = CC.getClient().getAudioOutputDeviceIDs();
			
			boolean found = false;
				
			for(AudioDeviceModel m : sm.getAudioDeviceModels()) {
				for(int i = 0; i < deviceIDs.length; i++) {
					if(m.getDeviceID() == deviceIDs[i]) {
						deviceIDs[i] = -1;
						found = true;
					}
				}
				
				if(!found) sm.removeAudioDevice(m.getDeviceID());
				found = false;
			}
			
			for(int id : deviceIDs) {
				if(id >= 0) sm.addAudioDevice (
					CC.getClient().getAudioOutputDeviceInfo(id)
				);
			}
		} catch(Exception x) {
			setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
			CC.getLogger().log(Level.FINE, getErrorMessage(), x);
		}
	}
}
