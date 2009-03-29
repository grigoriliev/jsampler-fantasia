/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2009 Grigor Iliev <grigor@grigoriliev.com>
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

import java.util.Vector;

import org.jsampler.CC;
import org.jsampler.SamplerChannelModel;
import org.jsampler.SamplerModel;

import org.linuxsampler.lscp.SamplerChannel;

import net.sf.juife.Task;

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
	@Override
	public void
	exec() throws Exception {
		SamplerModel sm = CC.getSamplerModel();
		Integer[] chnIDs = CC.getClient().getSamplerChannelIDs();
		
		boolean found = false, changed = false;
		
		boolean isAdjustingOld = CC.getSamplerModel().getChannelListIsAdjusting();
		
		javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
			public void
			run() {
				CC.getSamplerModel().setChannelListIsAdjusting(true);
				CC.getMainFrame().setAutoUpdateChannelListUI(false);
			}
		});
		
		for(SamplerChannelModel m : sm.getChannels()) {
			for(int i = 0; i < chnIDs.length; i++) {
				if(m.getChannelId() == chnIDs[i]) {
					chnIDs[i] = -1;
					found = true;
				}
			}
			
			if(!found) {
				sm.removeChannelById(m.getChannelId());
				changed = true;
			}
			found = false;
		}
		
		Vector<SamplerChannel> v = new Vector<SamplerChannel>();
		for(int id : chnIDs) {
			if(id >= 0) v.add(CC.getClient().getSamplerChannelInfo(id));
		}
		
		for(int i = 0; i < v.size() - 1; i++) sm.addChannel(v.elementAt(i));
		
		manageAutoUpdate(false);
		
		if(v.size() > 0) sm.addChannel(v.elementAt(v.size() - 1));
		else if(!CC.getSamplerModel().getChannelListIsAdjusting()) {
			if(!isAdjustingOld && !changed); // do nothing if nothing is changed
			else sm.addChannel(null); // fire dummy event to end an adjusting sequence
		}
	}

	@Override
	public void
	onError(Exception e) {
		manageAutoUpdate(true);
	}
	
	private void
	manageAutoUpdate(boolean force) {
		if(!force) {
			Task[] tasks = CC.getTaskQueue().getPendingTasks();
			for(Task t : tasks) if(t.equals(this)) return;
		}
		
		try {
			javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
				public void
				run() {
					CC.getSamplerModel().setChannelListIsAdjusting(false);
					CC.getMainFrame().setAutoUpdateChannelListUI(true);
					CC.getMainFrame().updateChannelListUI();
				}
			});
		} catch(Exception x) {
			x.printStackTrace();
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
		if(!(obj instanceof UpdateChannels)) return false;
		
		return true;
	}
}
