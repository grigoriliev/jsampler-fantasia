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

import org.linuxsampler.lscp.SamplerChannel;

import static org.jsampler.JSI18n.i18n;


/**
 * This task updates the settings of a specific sampler channel.
 * @author Grigor Iliev
 */
public class UpdateChannelInfo extends EnhancedTask {
	private int channel;
	
	/**
	 * Creates new instance of <code>UpdateChannelInfo</code>.
	 * @param channel The sampler channel to be updated.
	 */
	public
	UpdateChannelInfo(int channel) {
		setTitle("UpdateChannelInfo_task");
		setDescription(i18n.getMessage("UpdateChannelInfo.description"));
		
		this.channel = channel;
	}
	
	/** The entry point of the task. */
	public void
	run() {
		try {
			SamplerModel sm = CC.getSamplerModel();
			sm.changeChannel(CC.getClient().getSamplerChannelInfo(channel));
		} catch(Exception x) {
			/*
			 * We don't want to bother the user if error occurs when updating
			 * a channel because in most cases this happens due to a race condition
			 * between delete/update events. So we just log this error instead
			 * to indicate the failure of this task.
			 */
			String msg = getDescription() + ": " + HF.getErrorMessage(x);
			CC.getLogger().log(Level.INFO, msg, x);
		}
	}
	
	/**
	 * Gets the ID of the channel for which information should be obtained.
	 * @return The ID of the channel for which information should be obtained.
	 */
	public int
	getChannelID() { return channel; }
}
