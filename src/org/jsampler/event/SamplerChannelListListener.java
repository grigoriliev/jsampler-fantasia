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

package org.jsampler.event;

/**
 * The listener interface for receiving events about adding and removing
 * sampler channels from a sampler channel list.
 * @author Grigor Iliev
 */
public interface SamplerChannelListListener extends java.util.EventListener {
	/**
	 * Invoked when a new sampler channel is created.
	 * @param e A <code>SamplerChannelListEvent</code>
	 * instance providing the event information.
	 */
	public void channelAdded(SamplerChannelListEvent e);
	
	/**
	 * Invoked when a sampler channel is removed.
	 * @param e A <code>SamplerChannelListEvent</code>
	 * instance providing the event information.
	 */
	public void channelRemoved(SamplerChannelListEvent e);
}
