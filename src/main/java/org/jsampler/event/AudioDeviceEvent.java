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

import org.jsampler.AudioDeviceModel;
import org.jsampler.EffectChain;


/**
 * A semantic event which indicates changes of an audio device settings.
 * @author Grigor Iliev
 */
public class AudioDeviceEvent extends java.util.EventObject {
	private AudioDeviceModel audioDeviceModel;
	
	private EffectChain chain;
	
	/**
	 * Constructs an <code>AudioDeviceEvent</code> object.
	 *
	 * @param source The object that originated the event.
	 * @param audioDeviceModel The model of the audio device for which this event occurs.
	 */
	public
	AudioDeviceEvent(Object source, AudioDeviceModel audioDeviceModel) {
		this(source, audioDeviceModel, null);
	}
	
	/**
	 * Constructs an <code>AudioDeviceEvent</code> object.
	 *
	 * @param source The object that originated the event.
	 * @param audioDeviceModel The model of the audio device for which this event occurs.
	 */
	public
	AudioDeviceEvent(Object source, AudioDeviceModel audioDeviceModel, EffectChain chain) {
		super(source);
		this.audioDeviceModel = audioDeviceModel;
		this.chain = chain;
	}
	
	/**
	 * Gets the audio device model for which this event occurs.
	 * @return The audio device model for which this event occurs.
	 */
	public AudioDeviceModel
	getAudioDeviceModel() { return audioDeviceModel; }
	
	/**
	 * Depending on the event provides the newly added effect chain when
	 * a new chain is added or the removed effect chain when a chain is removed.
	 */
	public EffectChain
	getEffectChain() { return chain; }
}
