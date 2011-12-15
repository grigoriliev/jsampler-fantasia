/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2011 Grigor Iliev <grigor@grigoriliev.com>
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

package org.jsampler.android.view.classic;

import org.jsampler.JSPrefs;

/**
*
* @author Grigor Iliev
*/
public class ClassicPrefs extends JSPrefs {
	private final static ClassicPrefs prefs = new ClassicPrefs();
	
	/** Forbids instantiation of <code>ClassicPrefs</code>. */
	private
	ClassicPrefs() { super("org.jsampler.android.view.classic"); }
	
	public static ClassicPrefs
	preferences() { return prefs; }
	
	@Override
	public boolean
	getDefaultBoolValue(String name) {
		if(LAUNCH_BACKEND_LOCALLY.equals(name)) return false;
		return super.getDefaultBoolValue(name);
	}
}
