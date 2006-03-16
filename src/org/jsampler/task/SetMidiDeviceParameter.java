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

import org.linuxsampler.lscp.BoolParameter;
import org.linuxsampler.lscp.Parameter;

import static org.jsampler.JSI18n.i18n;


/**
 *
 * @author Grigor Iliev
 */
public class SetMidiDeviceParameter extends EnhancedTask {
	private int dev;
	private Parameter prm;
	
	/**
	 * Creates new instance of <code>SetMidiDeviceParameter</code>.
	 * @param dev The id of the device whose parameter should be set.
	 * @param prmName The parameter name.
	 * @param value The new value for the specified parameter.
	 */
	public
	SetMidiDeviceParameter(int dev, String prmName, boolean value) {
		this(dev, new BoolParameter(prmName, value));
	}
	
	/**
	 * Creates new instance of <code>SetMidiDeviceParameter</code>.
	 * @param dev The id of the device whose parameter should be set.
	 * @param prm The parameter to be set.
	 */
	public
	SetMidiDeviceParameter(int dev, Parameter prm) {
		setTitle("SetMidiDeviceParameter_task");
		setDescription(i18n.getMessage("SetMidiDeviceParameter.description"));
		
		this.dev = dev;
		this.prm = prm;
	}
	
	public void
	run() {
		try { 
			CC.getClient().setMidiInputDeviceParameter(dev, prm);
			
			CC.getSamplerModel().getMidiDeviceModel(dev);
		} catch(Exception x) {
			setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
			CC.getLogger().log(Level.FINE, getErrorMessage(), x);
		}
	}
}
