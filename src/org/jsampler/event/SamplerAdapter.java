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

package org.jsampler.event;

/**
 * Adapter class for receiving events.
 * This class exists as convenience for creating listener objects.
 * The methods in this class are empty.
 * @author Grigor Iliev
 */
public abstract class SamplerAdapter implements SamplerListener {
	/**
	 * Invoked when a new sampler channel is created.
	 * @param e A <code>SamplerEvent</code> instance containing the event information.
	 */
	public void
	channelAdded(SamplerEvent e) { }
	
	/**
	 * Invoked when a sampler channel is removed.
	 * @param e A <code>SamplerEvent</code> instance containing the event information.
	 */
	public void
	channelRemoved(SamplerEvent e) { }
	
	/**
	 * Invoked when changes are made to a sampler channel.
	 * @param e A <code>SamplerEvent</code> instance containing the event information.
	 */
	public void
	channelChanged(SamplerEvent e) { }
}
