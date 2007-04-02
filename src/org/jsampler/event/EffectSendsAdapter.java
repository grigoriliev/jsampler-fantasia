/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2007 Grigor Iliev <grigor@grigoriliev.com>
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
public class EffectSendsAdapter implements EffectSendsListener {
	/** Invoked when a new effect send is added to a sampler channel. */
	public void
	effectSendAdded(EffectSendsEvent e) { }
	
	/** Invoked when an effect send is removed from a sampler channel. */
	public void
	effectSendRemoved(EffectSendsEvent e) { }
	
	/** Invoked when an effect send's setting are changed. */
	public void
	effectSendChanged(EffectSendsEvent e) { }
}
