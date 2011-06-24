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
import java.util.Arrays;
import javax.swing.SwingUtilities;
import org.jsampler.event.EffectChainEvent;
import org.jsampler.event.EffectChainListener;
import org.linuxsampler.lscp.EffectInstance;

/**
 *
 * @author Grigor Iliev
 */
public class EffectChain extends org.linuxsampler.lscp.EffectChain {
	private final ArrayList<EffectChainListener> listeners = new ArrayList<EffectChainListener>();
	
	public
	EffectChain(org.linuxsampler.lscp.EffectChain chain) {
		setChainId(chain.getChainId());
		
		effectInstances = new EffectInstance[chain.getEffectInstanceCount()];
		for(int i = 0; i < chain.getEffectInstanceCount(); i++) {
			effectInstances[i] = chain.getEffectInstance(i);
		}
	}
	
	/**
	 * Registers the specified listener to be notified when
	 * the settings of the effect chain are changed.
	 * @param l The <code>EffectChainListener</code> to register.
	 */
	public void
	addAudioDeviceListener(EffectChainListener l) { listeners.add(l); }
	
	/**
	 * Removes the specified listener.
	 * @param l The <code>EffectChainListener</code> to remove.
	 */
	public void
	removeAudioDeviceListener(EffectChainListener l) { listeners.remove(l); }
	
	public EffectInstance[]
	getEffectInstances() {
		return Arrays.copyOf(effectInstances, effectInstances.length);
	}
	
	public void
	setEffectInstances(EffectInstance[] instances) {
		effectInstances = instances;
		fireEffectInstanceListChanged(instances);
		
	}
	
	public int
	getIndex(int instanceId) {
		for(int i = 0; i < effectInstances.length; i++) {
			if(effectInstances[i].getInstanceId() == instanceId) {
				return i;
			}
		}
		return -1; 
	}
	
	private void
	fireEffectInstanceListChanged(EffectInstance[] instances) {
		final EffectChainEvent e = new EffectChainEvent(this, this, instances);
		SwingUtilities.invokeLater(new Runnable() {
			public void
			run() { fireEffectInstanceListChanged(e); }
		});
	}
	
	/**
	 * This method should be invoked from the event-dispatching thread.
	 */
	private void
	fireEffectInstanceListChanged(EffectChainEvent e) {
		CC.getSamplerModel().setModified(true);
		for(EffectChainListener l : listeners) l.effectInstanceListChanged(e);
	}
}
