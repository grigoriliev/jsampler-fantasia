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
package org.jsampler.event;

import org.linuxsampler.lscp.EffectChain;
import org.linuxsampler.lscp.EffectInstance;

/**
 *
 * @author Grigor Iliev
 */
public class EffectChainEvent extends java.util.EventObject {
	private EffectChain chain;
	private EffectInstance[] instances;
	
	/**
	 * Constructs an <code>EffectChainEvent</code> object.
	 *
	 * @param source The object that originated the event.
	 * @param audioDeviceModel The model of the audio device to which
	 * the specified effect chain belongs
	 * @param chain The effect chain.
	 * @param instances The new list of effect stances.
	 */
	public
	EffectChainEvent( Object source, EffectChain chain, EffectInstance[] instances ) {
		super(source);
		this.chain = chain;
		this.instances = instances;
	}
	
	/**
	 * Depending on the event provides the newly added effect chain when
	 * a new chain is added, the removed effect chain when a chain is removed
	 * and the chain which is changed when an effect chain change event occurs.
	 */
	public EffectChain
	getEffectChain() { return chain; }
	
	/** Provides the new list of effect instances. */
	public EffectInstance[]
	getEffectInstances() { return instances; }
}
