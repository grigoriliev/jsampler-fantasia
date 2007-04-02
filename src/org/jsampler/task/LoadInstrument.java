/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2006 Grigor Iliev <grigor@grigoriliev.com>
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

import org.linuxsampler.lscp.SamplerChannel;

import static org.jsampler.JSI18n.i18n;


/**
 * This task loads and assigns an instrument to a sampler channel.
 * @author Grigor Iliev
 */
public class LoadInstrument extends EnhancedTask {
	private String filename;
	private int instrIndex;
	private int channel;
	
	/**
	 * Creates new instance of <code>LoadInstrument</code>.
	 * @param filename The name of the instrument file
	 * on the LinuxSampler instance's host system.
	 * @param instrIndex The index of the instrument in the instrument file.
	 * @param channel The number of the sampler channel the instrument should be assigned to.
	 */
	public
	LoadInstrument(String filename, int instrIndex, int channel) {
		this.filename = filename;
		this.instrIndex = instrIndex;
		this.channel = channel;
		
		setTitle("LoadInstrument_task");
		setDescription(i18n.getMessage("LoadInstrument.description"));
	}
	
	/** The entry point of the task. */
	public void
	run() {
		try { CC.getClient().loadInstrument(filename, instrIndex, channel, true); }
		catch(Exception x) {
			setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
			CC.getLogger().log(Level.FINE, getErrorMessage(), x);
		}
	}
}
