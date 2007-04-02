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

package org.jsampler.view.fantasia;

import java.util.prefs.Preferences;


/**
 *
 * @author Grigor Iliev
 */
public class FantasiaPrefs {
	private final static String prefNode = "org.jsampler.view.fantasia";
	private final static Preferences userPrefs = Preferences.userRoot().node(prefNode);
	
	private final static String WINDOW_LOCATION = "Mainframe.sizeAndLocation";
	private final static String DEF_WINDOW_LOCATION = null;
	
	private final static String ALWAYS_ON_TOP = "AlwaysOnTop";
	private final static boolean DEF_ALWAYS_ON_TOP = false;
	
	
	
	/** Forbits instantiation of <code>FantasiaPrefs</code>. */
	private FantasiaPrefs() {
	}
	
	public static Preferences
	user() { return userPrefs; }
	
	/**
	 * Gets a string representation of the main window's location.
	 * The string representation is a comma-separated list
	 * of x and y coordinates.
	 * @return A string representation of the main window's location,
	 * or <code>null</code> if the value is not set.
	 */
	public static String
	getWindowLocation() {
		return user().get(WINDOW_LOCATION, DEF_WINDOW_LOCATION);
	}
	
	/**
	 * Sets the main window's ocation.
	 * Use <code>null</code> to remove the current value.
	 * @param s A string representation of the main window'socation.
	 * @see #getWindowLocation
	 */
	public static void
	setWindowLocation(String s) {
		if(s == null) {
			user().remove(WINDOW_LOCATION);
			return;
		}
		
		user().put(WINDOW_LOCATION, s);
	}
	
	/**
	 * Determines whether the main window should be always-on-top window.
	 * @return <code>true</code> if the main window should be always-on-top window,
	 * <code>false</code> otherwise.
	 */
	public static boolean
	isAlwaysOnTop() {
		return user().getBoolean(ALWAYS_ON_TOP, DEF_ALWAYS_ON_TOP);
	}
	
	/**
	 * Sets whether the main window should be always-on-top window.
	 * @param b If <code>true</code> the main window should be always-on-top window.
	 */
	public static void
	setAlwaysOnTop(boolean b) {
		if(b == isAlwaysOnTop()) return;
		user().putBoolean(ALWAYS_ON_TOP, b);
	}
	
	
}
