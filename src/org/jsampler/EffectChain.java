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
import org.linuxsampler.lscp.EffectChainInfo;
import org.linuxsampler.lscp.EffectInstanceInfo;


/**
 *
 * @author Grigor Iliev
 */
public class EffectChain {
	private int chainId = -1;
	private final ArrayList<EffectInstance> effectInstances = new ArrayList<EffectInstance>();
	
	private final ArrayList<EffectChainListener> listeners = new ArrayList<EffectChainListener>();
	
	public
	EffectChain(EffectChainInfo chain) {
		setChainId(chain.getChainId());
		
		setEffectInstances(chain);
	}
	
	/**
	 * Registers the specified listener to be notified when
	 * the settings of the effect chain are changed.
	 * @param l The <code>EffectChainListener</code> to register.
	 */
	public void
	addEffectChainListener(EffectChainListener l) { listeners.add(l); }
	
	/**
	 * Removes the specified listener.
	 * @param l The <code>EffectChainListener</code> to remove.
	 */
	public void
	removeEffectChainListener(EffectChainListener l) { listeners.remove(l); }
	
	/**
	 * Gets the numerical ID of the chain.
	 * @return The numerical ID of the chain or -1 if the ID is not set.
	 */
	public int
	getChainId() { return chainId; }
	
	/** Sets the numerical ID of the chain. */
	public void
	setChainId(int id) { chainId = id; }
	
	public EffectInstance[]
	getEffectInstances() {
		return effectInstances.toArray(new EffectInstance[0]);
	}
	
	public void
	setEffectInstances(EffectChainInfo chain) {
		effectInstances.clear();
		for(int i = 0; i < chain.getEffectInstanceCount(); i++) {
			effectInstances.add(new EffectInstance(chain.getEffectInstance(i)));
		}
		fireEffectInstanceListChanged();
		
	}
	
	public int
	getEffectInstanceCount() { return effectInstances.size(); }
	
	public EffectInstance
	getEffectInstance(int idx) { return effectInstances.get(idx); }
	
	public int
	getIndex(int instanceId) {
		for(int i = 0; i < effectInstances.size(); i++) {
			if(effectInstances.get(i).getInstanceId() == instanceId) {
				return i;
			}
		}
		return -1; 
	}
	
	public EffectInstance
	getEffectInstanceById(int instanceId) {
		int idx = getIndex(instanceId);
		if(idx == -1) return null;
		
		return effectInstances.get(idx);
	}
	
	private void
	fireEffectInstanceListChanged() {
		final EffectChainEvent e = new EffectChainEvent(this, this);
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
