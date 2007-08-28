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
package org.jsampler.view.std;

import java.util.Vector;

import org.jsampler.CC;
import org.jsampler.JSPrefs;


/**
 *
 * @author Grigor Iliev
 */
public class StdUtils {
	
	/** Forbids the instantiation of this class */
	private
	StdUtils() { }
	
	private static JSPrefs
	preferences() { return CC.getViewConfig().preferences(); }
	
	/**
	 * Updates the specified string list property by adding the specified
	 * element on the top. Also restricts the maximum number of elements to 12.
	 */
	public static void
	updateRecentElements(String property, String newElement) {
		String[] elements = preferences().getStringListProperty(property);
		Vector<String> v = new Vector<String>();
		v.add(newElement);
		for(String s : elements) {
			if(!newElement.equals(s)) v.add(s);
		}
		if(v.size() > 12) v.setSize(12);
		
		elements = v.toArray(new String[v.size()]);
		preferences().setStringListProperty(property, elements);
	}
}
