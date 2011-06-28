/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2011 Grigor Iliev <grigor@grigoriliev.com>
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
package org.jsampler;

import java.util.ArrayList;
import javax.swing.SwingUtilities;
import org.jsampler.event.EffectInstanceEvent;
import org.jsampler.event.EffectInstanceListener;
import org.jsampler.task.Audio;
import org.linuxsampler.lscp.EffectInstanceInfo;

/**
 *
 * @author Grigor Iliev
 */
public class EffectInstance {
	private EffectInstanceInfo instance;
	
	private final ArrayList<EffectInstanceListener> listeners = new ArrayList<EffectInstanceListener>();
	
	public
	EffectInstance(EffectInstanceInfo instance) {
		this.instance = instance;
	}
	
	/**
	 * Registers the specified listener to be notified when
	 * the settings of the effect instance are changed.
	 * @param l The <code>EffectInstanceListener</code> to register.
	 */
	public void
	addEffectInstanceListener(EffectInstanceListener l) { listeners.add(l); }
	
	/**
	 * Removes the specified listener.
	 * @param l The <code>EffectInstanceListener</code> to remove.
	 */
	public void
	removeEffectInstanceListener(EffectInstanceListener l) { listeners.remove(l); }
	
	public int
	getInstanceId() { return instance.getInstanceId(); }
	
	public EffectInstanceInfo
	getInfo() { return instance; }
	
	public void
	setInfo(EffectInstanceInfo instance) {
		this.instance = instance;
		fireInstanceInfoChanged();
	}
	
	public void
	setBackendParameter(int prmIndex, float newValue) {
		CC.getTaskQueue().add (
			new Audio.SetEffectInstanceParameter(getInstanceId(), prmIndex, newValue)
		);
	}
	
	public void
	fireInstanceInfoChanged() {
		final EffectInstanceEvent e = new EffectInstanceEvent(this, this);
		SwingUtilities.invokeLater(new Runnable() {
			public void
			run() { fireInstanceInfoChanged(e); }
		});
	}
	
	/**
	 * This method should be invoked from the event-dispatching thread.
	 */
	private void
	fireInstanceInfoChanged(EffectInstanceEvent e) {
		CC.getSamplerModel().setModified(true);
		for(EffectInstanceListener l : listeners) l.effectInstanceChanged(e);
	}
}
