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

import static org.jsampler.JSI18n.i18n;


/**
 *
 * @author Grigor Iliev
 */
public class EnableAudioDevice extends AbstractTask {
	private int dev;
	private boolean enable;
	
	/**
	 * Creates new instance of <code>EnableAudioDevice</code>.
	 * @param dev The id of the device whose parameter should be enabled/disabled.
	 * @param enable Specify <code>true</code> to enable the audio device;
	 * code>false</code> to disable it.
	 */
	public
	EnableAudioDevice(int dev, boolean enable) {
		setTitle("EnableAudioDevice_task");
		setDescription(i18n.getMessage("EnableAudioDevice.description", dev));
		
		this.dev = dev;
		this.enable = enable;
	}
	
	public void
	stop() { CC.cleanExit(); }
	
	public void
	run() {
		try { 
			CC.getClient().enableAudioOutputDevice(dev, enable);
			
			// TODO: This should be done through LinuxSampler notification system.
			CC.getSamplerModel().getAudioDeviceModel(dev).setActive(enable);
		} catch(Exception x) {
			setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
			CC.getLogger().log(Level.FINE, getErrorMessage(), x);
		}
	}
}
