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

import org.linuxsampler.lscp.SamplerChannel;


/**
 * A semantic event which indicates sampler changes.
 * @author Grigor Iliev
 */
public class SamplerEvent extends java.util.EventObject {
	/**
	 * Constructs a <code>SamplerEvent</code> object.
	 *
	 * @param source The object that originated the event.
	 */
	public SamplerEvent(Object source) {
		super(source);
	}
}
