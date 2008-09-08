/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2008 Grigor Iliev <grigor@grigoriliev.com>
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

import org.jsampler.view.std.StdPrefs;


/**
 *
 * @author Grigor Iliev
 */
public class FantasiaPrefs extends StdPrefs {
	/** Property which specifies whether to use animation effects. */
	public final static String ANIMATED = "animated";
	
	private final static String WINDOW_LOCATION = "Mainframe.sizeAndLocation";
	private final static String DEF_WINDOW_LOCATION = null;
	
	private final static FantasiaPrefs prefs = new FantasiaPrefs();
	
		
	/** Forbits instantiation of <code>FantasiaPrefs</code>. */
	private
	FantasiaPrefs() { super("org.jsampler.view.fantasia"); }
	
	public static FantasiaPrefs
	preferences() { return prefs; }
	
	/**
	 * Gets a string representation of the main window's location.
	 * The string representation is a comma-separated list
	 * of x and y coordinates.
	 * @return A string representation of the main window's location,
	 * or <code>null</code> if the value is not set.
	 */
	public String
	getWindowLocation() {
		return getStringProperty(WINDOW_LOCATION, DEF_WINDOW_LOCATION);
	}
	
	/**
	 * Sets the main window's location.
	 * Use <code>null</code> to remove the current value.
	 * @param s A string representation of the main window'socation.
	 * @see #getWindowLocation
	 */
	public void
	setWindowLocation(String s) {
		setStringProperty(WINDOW_LOCATION, s);
	}
	
	public int
	getDefaultIntValue(String name) {
		if(name == LS_CONSOLE_BACKGROUND_COLOR) return 0x626262;
		if(name == LS_CONSOLE_TEXT_COLOR) return 0xb4b4b4;
		if(name == LS_CONSOLE_NOTIFY_COLOR) return 0x848484;
		if(name == LS_CONSOLE_WARNING_COLOR) return 0xf19e0e;
		if(name == LS_CONSOLE_ERROR_COLOR) return 0xfa4a1f;
		
		return super.getDefaultIntValue(name);
	}
	
	public String
	getDefaultStringValue(String name) {
		if(name == "Theme") return "Graphite";
		
		return super.getDefaultStringValue(name);
	}
	
	public boolean
	getDefaultBoolValue(String name) {
		if(name == ANIMATED) return true;
		if("toolBar.visible".equals(name)) return true;
		if("leftSidePane.visible".equals(name)) return true;
		if("rightSidePane.visible".equals(name)) return true;
		if("rightSidePane.showInstrumentsDb".equals(name)) return true;
		if("channel.smallView.showChannelNumbering".equals(name)) return true;
		if("channel.smallView.showStreamVoiceCount".equals(name)) return true;
		
		return super.getDefaultBoolValue(name);
	}
}
