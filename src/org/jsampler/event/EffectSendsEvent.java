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

import org.linuxsampler.lscp.FxSend;

/**
 * A semantic event which indicates effect sends changes.
 * @author Grigor Iliev
 */
public class EffectSendsEvent extends java.util.EventObject {
	private FxSend fxSend;
	
	/**
	 * Constructs a <code>EffectSendsEvent</code> object.
	 * @param source The object that originated the event.
	 */
	public EffectSendsEvent(Object source, FxSend fxSend) {
		super(source);
		this.fxSend = fxSend;
	}
	
	/**
	 * Gets the effect send, for which the event is fired.
	 */
	public FxSend
	getFxSend() { return fxSend; }
}
