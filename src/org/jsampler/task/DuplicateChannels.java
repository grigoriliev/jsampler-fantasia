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

import org.jsampler.view.JSChannel;

import org.linuxsampler.lscp.Client;
import org.linuxsampler.lscp.SamplerChannel;

import static org.jsampler.JSI18n.i18n;


/**
 *
 * @author Grigor Iliev
 */
public class DuplicateChannels extends EnhancedTask {
	JSChannel[] channels;
	
	public
	DuplicateChannels(JSChannel[] channels) {
		this.channels = channels;
		
		setTitle("DuplicateChannels_task");
		setDescription(i18n.getMessage("DuplicateChannels.description"));
	}
	
	public void
	run() {
		try {
			for(JSChannel c : channels) {
				int i = CC.getClient().addSamplerChannel();
				duplicateSettings(c.getChannelInfo(), i);
			}
		}
		catch(Exception x) {
			setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
			CC.getLogger().log(Level.FINE, getErrorMessage(), x);
		}
	}
	
	private void
	duplicateSettings(SamplerChannel sc, int c) throws Exception {
		Client client = CC.getClient();
		
		if(sc.getMidiInputDevice() >= 0) {
			client.setChannelMidiInputDevice(c, sc.getMidiInputDevice());
			client.setChannelMidiInputPort(c, sc.getMidiInputPort());
			client.setChannelMidiInputChannel(c, sc.getMidiInputChannel());
		}
		
		if(sc.getAudioOutputDevice() >= 0) {
			client.setChannelAudioOutputDevice(c, sc.getAudioOutputDevice());
		}
		
		if(sc.getEngine() != null) {
			client.loadSamplerEngine(sc.getEngine().getName(), c);
			
			client.setChannelVolume(c, sc.getVolume());
			
			if(sc.isSoloChannel()) client.setChannelSolo(c, true);
			
			if(sc.isMuted() && !sc.isMutedBySolo())
				client.setChannelMute(c, true);
		
			if(sc.getInstrumentFile() != null) client.loadInstrument (
				sc.getInstrumentFile(), sc.getInstrumentIndex(), c, true
			);
		}
	}
}
