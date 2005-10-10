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

import net.sf.juife.AbstractTask;

import org.jsampler.CC;
import org.jsampler.HF;

import org.linuxsampler.lscp.AudioOutputDevice;
import org.linuxsampler.lscp.Parameter;

import static org.jsampler.JSI18n.i18n;


/**
 *
 * @author Grigor Iliev
 */
public class SetAudioChannelParameter extends AbstractTask {
	private int dev;
	private int channel;
	private Parameter prm;
	
	/**
	 * Creates new instance of <code>SetAudioChannelParameter</code>.
	 * @param dev The id of the device whose port parameter should be set.
	 * @param channel The channel number.
	 * @param prm The parameter to be set.
	 */
	public
	SetAudioChannelParameter(int dev, int channel, Parameter prm) {
		setTitle("SetAudioChannelParameter_task");
		setDescription(i18n.getMessage("SetAudioChannelParameter.description"));
		
		this.dev = dev;
		this.channel = channel;
		this.prm = prm;
	}
	
	public void
	stop() { CC.cleanExit(); }
	
	public void
	run() {
		try { 
			CC.getClient().setAudioOutputChannelParameter(dev, channel, prm);
			
			// TODO: This must be done through the LinuxSampler notification system.
			AudioOutputDevice aod = CC.getClient().getAudioOutputDeviceInfo(dev);
			CC.getSamplerModel().getAudioDeviceModel(dev).setDeviceInfo(aod);
		} catch(Exception x) {
			setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
			CC.getLogger().log(Level.FINE, getErrorMessage(), x);
		}
	}
}
