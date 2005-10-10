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

import org.linuxsampler.lscp.Parameter;


/**
 * A semantic event which indicates that the value of a particular parameter is changed.
 * @author Grigor Iliev
 */
public class ParameterEvent extends java.util.EventObject {
	private Parameter parameter;
	
	/**
	 * Constructs a <code>ParameterEvent</code> object.
	 *
	 * @param source The object that originated the event.
	 * @param prm The parameter for which this event occurs.
	 */
	public
	ParameterEvent(Object source, Parameter prm) {
		super(source);
		parameter = prm;
	}
	
	/**
	 * Gets the parameter for which this event occurs.
	 * @return The parameter for which this event occurs.
	 */
	public Parameter
	getParameter() { return parameter; }
}
