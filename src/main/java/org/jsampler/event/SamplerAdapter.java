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
public abstract class SamplerAdapter implements SamplerListener {
	/** Invoked when the global volume of the sampler is changed. */
	public void
	volumeChanged(SamplerEvent e) { }
	
	/** Invoked when the total number of active streams is changed. */
	public void
	totalStreamCountChanged(SamplerEvent e) { }
	
	/** Invoked when the total number of active voices is changed. */
	public void
	totalVoiceCountChanged(SamplerEvent e) { }
	
	/** Invoked when the default MIDI instrument map is changed. */
	public void defaultMapChanged(SamplerEvent e) { }
}
