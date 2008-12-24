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

import static org.jsampler.JSI18n.i18n;


/**
 * This task updates the current number of all active voices
 * and the maximum number of active voices allowed.
 * @author Grigor Iliev
 */
public class UpdateTotalVoiceCount extends EnhancedTask {
	/** Creates a new instance of <code>UpdateTotalVoiceCount</code>. */
	public
	UpdateTotalVoiceCount() {
		setTitle("UpdateTotalVoiceCount_task");
		setDescription(i18n.getMessage("UpdateTotalVoiceCount.description"));
	}
	
	/** The entry point of the task. */
	@Override
	public void
	run() {
		try {
			SamplerModel sm = CC.getSamplerModel();
			int voices = CC.getClient().getTotalVoiceCount();
			int voicesMax = CC.getClient().getTotalVoiceCountMax();
			sm.updateActiveVoiceInfo(voices, voicesMax);
		} catch(Exception x) {
			String msg = getDescription() + ": " + HF.getErrorMessage(x);
			CC.getLogger().log(Level.INFO, msg, x);
		}
			
	}
		
	/**
	 * Used to decrease the traffic. All task in the queue
	 * equal to this are removed if added using {@link org.jsampler.CC#scheduleTask}.
	 * @see org.jsampler.CC#addTask
	 */
	@Override
	public boolean
	equals(Object obj) {
		if(obj == null) return false;
		if(!(obj instanceof UpdateTotalVoiceCount)) return false;
		
		return true;
	}
}
