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

import net.sf.juife.Task;

import org.jsampler.CC;
import org.jsampler.HF;

import static org.jsampler.JSI18n.i18n;


/**
 * This taks sets the volume of a specific sampler channel.
 * @author Grigor Iliev
 */
public class SetChannelVolume extends EnhancedTask {
	private int channel;
	private float volume;
	
	/**
	 * Creates new instance of <code>SetChannelVolume</code>.
	 * @param channel The sampler channel number.
	 * @param volume The new volume value.
	 */
	public
	SetChannelVolume(int channel, float volume) {
		setTitle("SetChannelVolume_task");
		setDescription(i18n.getMessage("SetChannelVolume.description", channel));
		
		this.channel = channel;
		this.volume = volume;
	}
	
	/** The entry point of the task. */
	public void
	run() {
		/*
		 * Because of the rapid flow of volume change tasks in some cases
		 * we need to do some optimization to decrease the traffic.
		 */
		boolean b = true;
		Task[] tS = CC.getTaskQueue().getPendingTasks();
		
		for(int i = tS.length - 1; i >= 0; i--) {
			Task t = tS[i];
			
			if(t instanceof SetChannelVolume) {
				SetChannelVolume scv = (SetChannelVolume)t;
				if(scv.getChannelID() == channel) {
					CC.getTaskQueue().removeTask(scv);
				}
			}
		}
		
		try { CC.getClient().setChannelVolume(channel, volume); }
		catch(Exception x) {
			setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
			CC.getLogger().log(Level.FINE, getErrorMessage(), x);
		}
	}
	
	/**
	 * Gets the ID of the channel whose volume should be changed.
	 * @return The ID of the channel whose volume should be changed.
	 */
	public int
	getChannelID() { return channel; }
}
