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

import static org.jsampler.JSI18n.i18n;


/**
 * This task loads a sampler engine in a specific sampler channel.
 * @author Grigor Iliev
 */
public class LoadEngine extends EnhancedTask {
	private String engine;
	private int channel;
	
	/**
	 * Creates new instance of <code>LoadEngine</code>.
	 * @param engine The name of the engine to load.
	 * @param channel The number of the sampler channel
	 * the deployed engine should be assigned to.
	 */
	public
	LoadEngine(String engine, int channel) {
		this.engine = engine;
		this.channel = channel;
		
		setTitle("LoadEngine_task");
		
		Object[] objs = { engine, new Integer(channel) };
		setDescription(i18n.getMessage("LoadEngine.description", objs));
	}
	
	/** The entry point of the task. */
	public void
	run() {
		try { CC.getClient().loadSamplerEngine(engine, channel); }
		catch(Exception x) {
			setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
			CC.getLogger().log(Level.FINE, getErrorMessage(), x);
		}
	}
}
