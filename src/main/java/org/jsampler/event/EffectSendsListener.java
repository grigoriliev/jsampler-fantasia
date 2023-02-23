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
 * The listener interface that is notified about effect sends changes.
 * @author Grigor Iliev
 */
public interface EffectSendsListener extends java.util.EventListener {
	/** Invoked when a new effect send is added to a sampler channel. */
	public void effectSendAdded(EffectSendsEvent e);
	
	/** Invoked when an effect send is removed from a sampler channel. */
	public void effectSendRemoved(EffectSendsEvent e);
	
	/** Invoked when an effect send's setting are changed. */
	public void effectSendChanged(EffectSendsEvent e);
}
