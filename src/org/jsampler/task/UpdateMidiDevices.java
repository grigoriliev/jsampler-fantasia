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

import org.jsampler.MidiDeviceModel;

import static org.jsampler.JSI18n.i18n;


/**
 *
 * @author Grigor Iliev
 */
public class UpdateMidiDevices extends EnhancedTask {
	public
	UpdateMidiDevices() {
		setTitle("UpdateMidiDevices_task");
		setDescription(i18n.getMessage("UpdateMidiDevices.description"));
	}
	
	public void
	run() {
		try { 
			SamplerModel sm = CC.getSamplerModel();
			Integer[] deviceIDs = CC.getClient().getMidiInputDeviceIDs();
			
			boolean found = false;
				
			for(MidiDeviceModel m : sm.getMidiDeviceModels()) {
				for(int i = 0; i < deviceIDs.length; i++) {
					if(m.getDeviceID() == deviceIDs[i]) {
						deviceIDs[i] = -1;
						found = true;
					}
				}
				
				if(!found) sm.removeMidiDevice(m.getDeviceID());
				found = false;
			}
			
			for(int id : deviceIDs) {
				if(id >= 0) 
					sm.addMidiDevice(CC.getClient().getMidiInputDeviceInfo(id));
			}
		} catch(Exception x) {
			setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
			CC.getLogger().log(Level.FINE, getErrorMessage(), x);
		}
	}
}
