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

import org.linuxsampler.lscp.MidiInputDevice;
import org.linuxsampler.lscp.Parameter;

import static org.jsampler.JSI18n.i18n;


/**
 * This task alters a specific setting of a MIDI input port.
 * @author Grigor Iliev
 */
public class SetMidiPortParameter extends EnhancedTask {
	private int dev;
	private int port;
	private Parameter prm;
	
	/**
	 * Creates new instance of <code>SetMidiPortParameter</code>.
	 * @param dev The id of the device whose port parameter should be set.
	 * @param port The number of the port.
	 * @param prm The parameter to be set.
	 */
	public
	SetMidiPortParameter(int dev, int port, Parameter prm) {
		setTitle("SetMidiPortParameter_task");
		setDescription(i18n.getMessage("SetMidiPortParameter.description"));
		
		this.dev = dev;
		this.port = port;
		this.prm = prm;
	}
	
	/** The entry point of the task. */
	public void
	run() {
		try { 
			CC.getClient().setMidiInputPortParameter(dev, port, prm);
			
			// TODO: This must be done through the LinuxSampler notification system.
			MidiInputDevice mid = CC.getClient().getMidiInputDeviceInfo(dev);
			CC.getSamplerModel().getMidiDeviceModel(dev).setDeviceInfo(mid);
		} catch(Exception x) {
			setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
			CC.getLogger().log(Level.FINE, getErrorMessage(), x);
		}
	}
}
