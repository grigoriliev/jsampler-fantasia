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

package org.jsampler.view.classic;

import java.awt.Color;

import java.util.prefs.Preferences;


/**
 * This class represents the preferences of the JS Classic package.
 * @author Grigor Iliev
 */
public class ClassicPrefs {
	private final static String prefNode = "org.jsampler.view.classic";
	
	public final static String SHOW_TOOLBAR = "Toolbar.visible";
	public final static boolean DEF_SHOW_TOOLBAR = true;
	
	public final static String SHOW_STATUSBAR = "Statusbar.visible";
	public final static boolean DEF_SHOW_STATUSBAR = true;
	
	public final static String SHOW_LEFT_PANE = "LeftPane.visible";
	public final static boolean DEF_SHOW_LEFT_PANE = true;
	
	public final static String CHANNEL_BORDER_COLOR = "Channel.borderColor";
	public final static int DEF_CHANNEL_BORDER_COLOR = 0xb8cfe5;
	
	public final static String CUSTOM_CHANNEL_BORDER_COLOR = "Channel.customBorderColor";
	public final static boolean DEF_CUSTOM_CHANNEL_BORDER_COLOR = false;
	
	private static Preferences userPrefs = Preferences.userRoot().node(prefNode);
	
	public static Preferences
	user() { return userPrefs; }
	
	/**
	 * Determines whether the toolbar should be visible.
	 * @return <code>true</code> if the toolbar should be visible,
	 * <code>false</code> otherwise.
	 */
	public static boolean
	shouldShowToolbar() { return user().getBoolean(SHOW_TOOLBAR, DEF_SHOW_TOOLBAR); }
	
	/**
	 * Sets whether the toolbar should be visible.
	 * @param b If <code>true</code> the toolbar will be visible at startup.
	 */
	public static void
	setShowToolbar(boolean b) {
		if(b == shouldShowToolbar()) return;
		user().putBoolean(SHOW_TOOLBAR, b);
	}
	
	/**
	 * Determines whether the statusbar should be visible.
	 * @return <code>true</code> if the statusbar should be visible,
	 * <code>false</code> otherwise.
	 */
	public static boolean
	shouldShowStatusbar() { return user().getBoolean(SHOW_STATUSBAR, DEF_SHOW_STATUSBAR); }
	
	/**
	 * Sets whether the statusbar should be visible.
	 * @param b If <code>true</code> the statusbar will be visible at startup.
	 */
	public static void
	setShowStatusbar(boolean b) {
		if(b == shouldShowStatusbar()) return;
		user().putBoolean(SHOW_STATUSBAR, b);
	}
	
	/**
	 * Determines whether the left pane should be visible.
	 * @return <code>true</code> if the left pane should be visible,
	 * <code>false</code> otherwise.
	 */
	public static boolean
	shouldShowLeftPane() { return user().getBoolean(SHOW_LEFT_PANE, DEF_SHOW_LEFT_PANE); }
	
	/**
	 * Sets whether the left pane should be visible.
	 * @param b If <code>true</code> the left pane will be visible at startup.
	 */
	public static void
	setShowLeftPane(boolean b) {
		if(b == shouldShowLeftPane()) return;
		user().putBoolean(SHOW_LEFT_PANE, b);
	}
	
	/**
	 * Gets the default border color that is used for the selected channels.
	 * @return The default border color that is used for the selected channels.
	 */
	public static Color
	getDefaultChannelBorderColor() { return new Color(DEF_CHANNEL_BORDER_COLOR); }
	
	/**
	 * Gets the custom border color to be used for the selected channels.
	 * @return The custom border color to be used for the selected channels.
	 */
	public static Color
	getChannelBorderColor() {
		int c = user().getInt(CHANNEL_BORDER_COLOR, DEF_CHANNEL_BORDER_COLOR);
		return new Color(c);
	}
	
	/**
	 * Sets the border color to be used for the selected channels.
	 * Use <code>null</code> to remove the current value.
	 * @param color The border color to be used for the selected channels.
	 */
	public static void
	setChannelBorderColor(Color c) {
		if(c == null) {
			user().remove(CHANNEL_BORDER_COLOR);
			return;
		}
		
		if(c.getRGB() == getChannelBorderColor().getRGB()) return;
		
		user().putInt(CHANNEL_BORDER_COLOR, c.getRGB());
	}
	
	/**
	 * Determines whether to use a custom border color for the selected channels.
	 * @return <code>true</code> if custom border color must be used
	 * for the selected channels, <code>false</code> otherwise.
	 */
	public static boolean
	getCustomChannelBorderColor() {
		return user().getBoolean (
			CUSTOM_CHANNEL_BORDER_COLOR, DEF_CUSTOM_CHANNEL_BORDER_COLOR
		);
	}
	
	/**
	 * Sets whether to use a custom border color for the selected channels.
	 * @param b specify <code>true</code> to use a custom border color
	 * for the selected channels, <code>false</code> otherwise.
	 */
	public static void
	setCustomChannelBorderColor(boolean b) {
		if(b == getCustomChannelBorderColor()) return;
		user().putBoolean(CUSTOM_CHANNEL_BORDER_COLOR, b);
	}
}
