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
 * A semantic event which indicates changes of list.
 * @author Grigor Iliev
 */
public class ListEvent<E> extends java.util.EventObject {
	private E entry;
	
	/**
	 * Constructs a <code>ListEvent</code> object.
	 *
	 * @param source The object that originated the event.
	 * @param entry The entry for which this event occurs.
	 */
	public
	ListEvent(Object source, E entry) {
		super(source);
		this.entry = entry;
	}
	
	/**
	 * Gets the entry for which this event occurs.
	 * @return The entry for which this event occurs.
	 */
	public E
	getEntry() { return entry; }
}
