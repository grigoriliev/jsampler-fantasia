/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2011 Grigor Iliev <grigor@grigoriliev.com>
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

/**
 * The listener interface that is notified when the audio device settings have changed.
 * @author Grigor Iliev
 */
public interface AudioDeviceListener extends java.util.EventListener {
	/** Invoked when when the settings of a particular audio device have changed. */
	public void settingsChanged(AudioDeviceEvent e);
	
	/** Invoked when when a new send effect chain is added to the audio device. */
	public void sendEffectChainAdded(AudioDeviceEvent e);
	
	/** Invoked when when a send effect chain is removed from the audio device. */
	public void sendEffectChainRemoved(AudioDeviceEvent e);
}
