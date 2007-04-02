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

import org.jsampler.Instrument;


/**
 * A semantic event which indicates orchestra changes.
 * @author Grigor Iliev
 */
public class OrchestraEvent extends java.util.EventObject {
	private Instrument instrument;
	
	/**
	 * Constructs an <code>OrchestraEvent</code> object.
	 * @param source The object that originated the event.
	 */
	public
	OrchestraEvent(Object source) { this(source, null); }
	
	/**
	 * Constructs an <code>OrchestraEvent</code> object.
	 * @param source The object that originated the event.
	 * @param instrument The instrument that has been added, removed or changed.
	 */
	public
	OrchestraEvent(Object source, Instrument instrument) {
		super(source);
		this.instrument = instrument;
	}
	
	/**
	 * Returns the instrument that has been added, removed or changed.
	 * @return The instrument that has been added, removed or changed;
	 * <code>null</code> otherwise.
	 */
	public Instrument
	getInstrument() { return instrument; }
}
