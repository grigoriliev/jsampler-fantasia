/*
 *   JSampler - a front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2023 Grigor Iliev <grigor@grigoriliev.com>
 *
 *   This file is part of JSampler.
 *
 *   JSampler is free software: you can redistribute it and/or modify it under
 *   the terms of the GNU General Public License as published by the Free
 *   Software Foundation, either version 3 of the License, or (at your option)
 *   any later version.
 *
 *   JSampler is distributed in the hope that it will be useful, but WITHOUT
 *   ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *   FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 *   more details.
 *
 *   You should have received a copy of the GNU General Public License along
 *   with JSampler. If not, see <https://www.gnu.org/licenses/>. 
 */

package org.jsampler.event;

/**
 * Adapter class for receiving events.
 * This class exists as convenience for creating listener objects.
 * The methods in this class are empty.
 * @author Grigor Iliev
 */
public abstract class SamplerChannelAdapter implements SamplerChannelListener {
	/**
	 * Invoked when changes are made to a sampler channel.
	 * @param e A <code>SamplerChannelEvent</code> instance containing event information.
	 */
	public void channelChanged(SamplerChannelEvent e) { }
	
	/**
	 * Invoked when the number of active disk streams has changed.
	 * @param e A <code>SamplerChannelEvent</code> instance containing event information.
	 */
	public void streamCountChanged(SamplerChannelEvent e) { }
	
	/**
	 * Invoked when the number of active voices has changed.
	 * @param e A <code>SamplerChannelEvent</code> instance containing event information.
	 */
	public void voiceCountChanged(SamplerChannelEvent e) { }
}
