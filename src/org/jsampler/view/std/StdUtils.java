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

import java.awt.Desktop;

import java.net.URI;

import java.util.Vector;

import org.jsampler.CC;
import org.jsampler.HF;
import org.jsampler.JSPrefs;

import static org.jsampler.view.std.StdI18n.i18n;


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
	
	public static boolean
	checkDesktopSupported() {
		if(Desktop.isDesktopSupported()) return true;
		
		String s = i18n.getError("StdUtils.DesktopApiNotSupported");
		HF.showErrorMessage(s, CC.getMainFrame());
		
		return false;
	}
	
	public static void
	browse(String uri) {
		if(!checkDesktopSupported()) return;
		
		try { Desktop.getDesktop().browse(new URI(uri)); }
		catch(Exception x) { x.printStackTrace(); }
	}
	
	public static void
	mail(String uri) {
		if(!StdUtils.checkDesktopSupported()) return;
		
		Desktop desktop = Desktop.getDesktop();
		
		try { Desktop.getDesktop().mail(new URI(uri)); }
		catch(Exception x) { x.printStackTrace(); }
	}
}
