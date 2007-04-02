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
import org.jsampler.SamplerChannelModel;
import org.jsampler.SamplerModel;

import static org.jsampler.JSI18n.i18n;


/**
 * This task updates the sampler channel list.
 * @author Grigor Iliev
 */
public class UpdateChannels extends EnhancedTask {
	/** Creates a new instance of <code>UpdateChannels</code>. */
	public
	UpdateChannels() {
		setTitle("UpdateChannels_task");
		setDescription(i18n.getMessage("UpdateChannels.description"));
	}
	
	/** The entry point of the task. */
	public void
	run() {
		try { 
			SamplerModel sm = CC.getSamplerModel();
			Integer[] chnIDs = CC.getClient().getSamplerChannelIDs();
			
			boolean found = false;
				
			for(SamplerChannelModel m : sm.getChannelModels()) {
				for(int i = 0; i < chnIDs.length; i++) {
					if(m.getChannelId() == chnIDs[i]) {
						chnIDs[i] = -1;
						found = true;
					}
				}
				
				if(!found) sm.removeChannel(m.getChannelId());
				found = false;
			}
			
			for(int id : chnIDs) {
				if(id >= 0) sm.addChannel(CC.getClient().getSamplerChannelInfo(id));
			}
		} catch(Exception x) {
			setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
			CC.getLogger().log(Level.FINE, getErrorMessage(), x);
		}
	}
}
