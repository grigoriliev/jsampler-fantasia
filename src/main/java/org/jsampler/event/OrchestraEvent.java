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

import org.jsampler.OrchestraInstrument;


/**
 * A semantic event which indicates orchestra changes.
 * @author Grigor Iliev
 */
public class OrchestraEvent extends java.util.EventObject {
	private OrchestraInstrument instrument;
	
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
	OrchestraEvent(Object source, OrchestraInstrument instrument) {
		super(source);
		this.instrument = instrument;
	}
	
	/**
	 * Returns the instrument that has been added, removed or changed.
	 * @return The instrument that has been added, removed or changed;
	 * <code>null</code> otherwise.
	 */
	public OrchestraInstrument
	getInstrument() { return instrument; }
}
