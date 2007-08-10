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

package org.jsampler.view.classic;

import java.awt.Color;

import java.util.prefs.Preferences;

import org.jsampler.CC;

import org.jsampler.view.std.StdPrefs;


/**
 * This class represents the preferences of the JS Classic package.
 * @author Grigor Iliev
 */
public class ClassicPrefs extends StdPrefs {
	private final static String prefNode = "org.jsampler.view.classic";
	
	private final static String WINDOW_SIZE_AND_LOCATION = ".sizeAndLocation";
	private final static String DEF_WINDOW_SIZE_AND_LOCATION = null;
	
	private final static String WINDOW_MAXIMIZED = ".maximized";
	private final static boolean DEF_WINDOW_MAXIMIZED = false;
	
	private final static String SAVE_WINDOW_PROPERTIES = "Mainframe.saveProperties";
	private final static boolean DEF_SAVE_WINDOW_PROPERTIES = true;
	
	private final static String HSPLIT_DIVIDER_LOCATION = "HSplit.dividerLocation";
	private final static int DEF_HSPLIT_DIVIDER_LOCATION = 180;
	
	private final static String SAVE_LEFT_PANE_STATE = "LeftPane.saveState";
	private final static boolean DEF_SAVE_LEFT_PANE_STATE = true;
	
	private final static String LEFT_PANE_PAGE_IDX = "LeftPane.pageIndex";
	private final static int DEF_LEFT_PANE_PAGE_IDX = 0;
	
	private final static String SHOW_CHANNELS_BAR = "ChannelsBar.visible";
	private final static boolean DEF_SHOW_CHANNELS_BAR = true;
	
	private final static String SHOW_STATUSBAR = "Statusbar.visible";
	private final static boolean DEF_SHOW_STATUSBAR = true;
	
	private final static String SHOW_LEFT_PANE = "LeftPane.visible";
	private final static boolean DEF_SHOW_LEFT_PANE = true;
	
	private final static String SHOW_STANDARD_BAR = "StandardBar.visible";
	private final static boolean DEF_SHOW_STANDARD_BAR = true;
	
	private final static String SHOW_LS_CONSOLE = "LSConsole.visible";
	private final static boolean DEF_SHOW_LS_CONSOLE = false;
	
	private final static String LS_CONSOLE_POPOUT = "LSConsole.popout";
	private final static boolean DEF_LS_CONSOLE_POPOUT = false;
	
	private final static String CHANNEL_BORDER_COLOR = "Channel.borderColor";
	private final static int DEF_CHANNEL_BORDER_COLOR = 0xb8cfe5;
	
	private final static String CUSTOM_CHANNEL_BORDER_COLOR = "Channel.customBorderColor";
	private final static boolean DEF_CUSTOM_CHANNEL_BORDER_COLOR = false;
	
	private final static String CHANNEL_BORDER_HL_COLOR = "Channel.borderMouseOverColor";
	private final static int DEF_CHANNEL_BORDER_HL_COLOR = 0xb8cfe5;
	
	private final static String CUSTOM_CHANNEL_BORDER_HL_COLOR = "Channel.customHlColor";
	private final static boolean DEF_CUSTOM_CHANNEL_BORDER_HL_COLOR = false;
	
	private final static String SEL_CHANNEL_BG_COLOR = "Channel.sel.BgColor";
	private final static int DEF_SEL_CHANNEL_BG_COLOR = 0xe0e6eb;
	
	private final static String CUSTOM_SEL_CHANNEL_BG_COLOR = "Channel.sel.customBgColor";
	private final static boolean DEF_CUSTOM_SEL_CHANNEL_BG_COLOR = false;
	
	private final static String HL_CHANNEL_BG_COLOR = "Channel.hl.BgColor";
	private final static int DEF_HL_CHANNEL_BG_COLOR = -1;
	
	private final static String CUSTOM_HL_CHANNEL_BG_COLOR = "Channel.hl.customBgColor";
	private final static boolean DEF_CUSTOM_HL_CHANNEL_BG_COLOR = false;
	
	private final static String VSPLIT_DIVIDER_LOCATION = "VSplit.dividerLocation";
	private final static int DEF_VSPLIT_DIVIDER_LOCATION = 200;
	
	private final static String CURRENT_ORCHESTRA_IDX = "OrchestrasPage.currentOrchestraIndex";
	private final static int DEF_CURRENT_ORCHESTRA_IDX = 0;
	
	private static Preferences userPrefs = Preferences.userRoot().node(prefNode);
	
	private final static ClassicPrefs prefs = new ClassicPrefs();
	
	private
	ClassicPrefs() { super(prefNode); }
	
	public static ClassicPrefs
	preferences() { return prefs; }
	
	public static Preferences
	user() { return userPrefs; }
	
	/**
	 * Gets a string representation of a window's size and location.
	 * The string representation is a comma-separated list
	 * of x and y coordinates, and width and height of the window.
	 * @param window The name of the window whose size and location should be obtained.
	 * @return A string representation of the window's size and location,
	 * or <code>null</code> if the value is not set.
	 */
	public static String
	getWindowSizeAndLocation(String window) {
		return user().get(window + WINDOW_SIZE_AND_LOCATION, DEF_WINDOW_SIZE_AND_LOCATION);
	}
	
	/**
	 * Sets the window's size and location.
	 * Use <code>null</code> to remove the current value.
	 * @param window The name of the window whose size and location should be set.
	 * @param s A string representation of the window's size and location.
	 */
	public static void
	setWindowSizeAndLocation(String window, String s) {
		if(s == null) {
			user().remove(window + WINDOW_SIZE_AND_LOCATION);
			return;
		}
		
		user().put(window + WINDOW_SIZE_AND_LOCATION, s);
	}
	
	/**
	 * Determines whether the specified window should be maximized.
	 * @param window The name of the window.
	 * @return <code>true</code> if the specified window should be maximized,
	 * <code>false</code> otherwise.
	 */
	public static boolean
	getWindowMaximized(String window) {
		return user().getBoolean(window + WINDOW_MAXIMIZED, DEF_WINDOW_MAXIMIZED);
	}
	
	/**
	 * Sets whether the specified window should be maximized.
	 * @param window The name of the window.
	 * @param b If <code>true</code> the specified window should be maximized.
	 */
	public static void
	setWindowMaximized(String window, boolean b) {
		if(b == getWindowMaximized(window)) return;
		user().putBoolean(window + WINDOW_MAXIMIZED, b);
	}
	
	/**
	 * Gets the divider location of the horizontal split pane.
	 * @return The divider location of the horizontal split pane.
	 */
	public static int
	getHSplitDividerLocation() {
		return user().getInt(HSPLIT_DIVIDER_LOCATION, DEF_HSPLIT_DIVIDER_LOCATION);
	}
	
	/**
	 * Sets the divider location of the horizontal split pane.
	 * @param i The new divider location of the horizontal split pane.
	 */
	public static void
	setHSplitDividerLocation(int i) {
		if(i == getHSplitDividerLocation()) return;
		user().putInt(HSPLIT_DIVIDER_LOCATION, i);
	}
	
	/**
	 * Determines whether the window properties (like size and location) should be saved.
	 * @return <code>true</code> if the window properties should be saved,
	 * <code>false</code> otherwise.
	 */
	public static boolean
	getSaveWindowProperties() {
		return user().getBoolean(SAVE_WINDOW_PROPERTIES, DEF_SAVE_WINDOW_PROPERTIES);
	}
	
	/**
	 * Sets whether the window properties (like size and location) should be saved.
	 * @param b If <code>true</code> the window properties will be saved.
	 */
	public static void
	setSaveWindowProperties(boolean b) {
		if(b == getSaveWindowProperties()) return;
		user().putBoolean(SAVE_WINDOW_PROPERTIES, b);
	}
	
	/**
	 * Determines whether the Left Pane state should be saved.
	 * @return <code>true</code> if the Left Pane state should be saved,
	 * <code>false</code> otherwise.
	 */
	public static boolean
	getSaveLeftPaneState() {
		return user().getBoolean(SAVE_LEFT_PANE_STATE, DEF_SAVE_LEFT_PANE_STATE);
	}
	
	/**
	 * Sets whether the Left Pane state should be saved.
	 * @param b If <code>true</code> the Left Pane state will be saved.
	 */
	public static void
	setSaveLeftPaneState(boolean b) {
		if(b == getSaveLeftPaneState()) return;
		user().putBoolean(SAVE_LEFT_PANE_STATE, b);
	}
	
	/**
	 * Gets the index of the page to be shown in the Left Pane.
	 * @return The index of the page to be shown in the Left Pane.
	 */
	public static int
	getLeftPanePageIndex() {
		return user().getInt(LEFT_PANE_PAGE_IDX, DEF_LEFT_PANE_PAGE_IDX);
	}
	
	/**
	 * Sets the maximum number of recent scripts to be kept.
	 * @param i Determines the maximum number of recent scripts to be kept.
	 */
	public static void
	setLeftPanePageIndex(int i) {
		if(i == getLeftPanePageIndex()) return;
		user().putInt(LEFT_PANE_PAGE_IDX, i);
	}
	
	/**
	 * Determines whether the <b>Channels</b> toolbar should be visible.
	 * @return <code>true</code> if the <b>Channels</b> toolbar should be visible,
	 * <code>false</code> otherwise.
	 */
	public static boolean
	shouldShowChannelsBar() {
		return user().getBoolean(SHOW_CHANNELS_BAR, DEF_SHOW_CHANNELS_BAR);
	}
	
	/**
	 * Sets whether the <b>Channels</b> toolbar should be visible.
	 * @param b If <code>true</code> the <b>Channels</b> toolbar will be visible at startup.
	 */
	public static void
	setShowChannelsBar(boolean b) {
		if(b == shouldShowChannelsBar()) return;
		user().putBoolean(SHOW_CHANNELS_BAR, b);
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
	 * Determines whether the LS Console should be visible.
	 * @return <code>true</code> if the LS Console should be visible,
	 * <code>false</code> otherwise.
	 */
	public static boolean
	shouldShowLSConsole() { return user().getBoolean(SHOW_LS_CONSOLE, DEF_SHOW_LS_CONSOLE); }
	
	/**
	 * Sets whether the LS Console should be visible.
	 * @param b If <code>true</code> the LS Console will be visible at startup.
	 */
	public static void
	setShowLSConsole(boolean b) {
		if(b == shouldShowLSConsole()) return;
		user().putBoolean(SHOW_LS_CONSOLE, b);
	}
	
	/**
	 * Determines whether the LS Console should be shown in a new window or
	 * docked in the main frame.
	 * @return <code>true</code> if the LS Console should be shown in a new window,
	 * <code>false</code> if the LS Console should be docked in the main frame.
	 */
	public static boolean
	isLSConsolePopOut() { return user().getBoolean(LS_CONSOLE_POPOUT, DEF_LS_CONSOLE_POPOUT); }
	
	/**
	 * Sets whether the LS Console should be shown in a new window or
	 * docked in the main frame.
	 * @param b code>true</code> means that the LS Console will be shown in a new window;
	 * <code>false</code> means that the LS Console will be docked in the main frame.
	 */
	public static void
	setLSConsolePopOut(boolean b) {
		if(b == isLSConsolePopOut()) return;
		user().putBoolean(LS_CONSOLE_POPOUT, b);
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
	 * Determines whether the <b>Standard</b> toolbar should be visible.
	 * @return <code>true</code> if the <b>Standard</b> toolbar should be visible,
	 * <code>false</code> otherwise.
	 */
	public static boolean
	shouldShowStandardBar() {
		return user().getBoolean(SHOW_STANDARD_BAR, DEF_SHOW_STANDARD_BAR);
	}
	
	/**
	 * Sets whether the <b>Standard</b> toolbar should be visible.
	 * @param b If <code>true</code> the <b>Standard</b> toolbar will be visible at startup.
	 */
	public static void
	setShowStandardBar(boolean b) {
		if(b == shouldShowStandardBar()) return;
		user().putBoolean(SHOW_STANDARD_BAR, b);
	}
	
	/**
	 * Gets the default border color that is used for the selected channels.
	 * @return The default border color that is used for the selected channels.
	 */
	public static Color
	getDefaultChannelBorderColor() { return new Color(DEF_CHANNEL_BORDER_COLOR); }
	
	/**
	 * Gets the custom border color to be used for the selected channels.
	 * @return The custom border color to be used for the selected
	 * channels or <code>null</code> if the color is not specified.
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
	
	/**
	 * Gets the default highlighted border color that
	 * is used when the mouse pointer is over a channel.
	 * @return The default highlighted border color.
	 */
	public static Color
	getDefaultChannelBorderHlColor() { return new Color(DEF_CHANNEL_BORDER_HL_COLOR); }
	
	/**
	 * Gets the custom highlighted border color that
	 * is used when the mouse pointer is over a channel.
	 * @return The custom highlighted border color.
	 */
	public static Color
	getChannelBorderHlColor() {
		int c = user().getInt(CHANNEL_BORDER_HL_COLOR, DEF_CHANNEL_BORDER_HL_COLOR);
		return new Color(c);
	}
	
	/**
	 * Sets the highlighted border color that
	 * is used when the mouse pointer is over a channel.
	 * Use <code>null</code> to remove the current value.
	 * @param color The new highlighted border color.
	 */
	public static void
	setChannelBorderHlColor(Color c) {
		if(c == null) {
			user().remove(CHANNEL_BORDER_HL_COLOR);
			return;
		}
		
		if(c.getRGB() == getChannelBorderHlColor().getRGB()) return;
		
		user().putInt(CHANNEL_BORDER_HL_COLOR, c.getRGB());
	}
	
	/**
	 * Determines whether to use a custom highlighted border color.
	 * @return <code>true</code> if custom highlighted border color
	 * must be used, <code>false</code> otherwise.
	 */
	public static boolean
	getCustomChannelBorderHlColor() {
		return user().getBoolean (
			CUSTOM_CHANNEL_BORDER_HL_COLOR, DEF_CUSTOM_CHANNEL_BORDER_HL_COLOR
		);
	}
	
	/**
	 * Sets whether to use a custom highlighted border color.
	 * @param b specify <code>true</code> to use a custom highlighted
	 * border color, <code>false</code> otherwise.
	 */
	public static void
	setCustomChannelBorderHlColor(boolean b) {
		if(b == getCustomChannelBorderHlColor()) return;
		user().putBoolean(CUSTOM_CHANNEL_BORDER_HL_COLOR, b);
	}
	
	/**
	 * Gets the custom background color that
	 * is used when the channel is selected.
	 * @return The custom background color that
	 * is used when the channel is selected.
	 */
	public static Color
	getSelectedChannelBgColor() {
		int c = user().getInt(SEL_CHANNEL_BG_COLOR, DEF_SEL_CHANNEL_BG_COLOR);
		return c == -1 ? null : new Color(c);
	}
	
	/**
	 * Sets the custom background color to
	 * be used when the channel is selected.
	 * Use <code>null</code> to remove the current value.
	 * @param color The new background color to
	 * be used when the channel is selected.
	 */
	public static void
	setSelectedChannelBgColor(Color c) {
		if(c == null) {
			user().remove(SEL_CHANNEL_BG_COLOR);
			return;
		}
		
		if(getSelectedChannelBgColor() != null) {
			if(c.getRGB() == getSelectedChannelBgColor().getRGB()) return;
		}
		
		user().putInt(SEL_CHANNEL_BG_COLOR, c.getRGB());
	}
	
	/**
	 * Determines whether to use a custom background color when a channel is selected.
	 * @return <code>true</code> if custom background color
	 * should be used, <code>false</code> otherwise.
	 */
	public static boolean
	getCustomSelectedChannelBgColor() {
		return user().getBoolean (
			CUSTOM_SEL_CHANNEL_BG_COLOR, DEF_CUSTOM_SEL_CHANNEL_BG_COLOR
		);
	}
	
	/**
	 * Sets whether to use a custom background color when a channel is selected.
	 * @param b specify <code>true</code> to use a custom
	 * background color, <code>false</code> otherwise.
	 */
	public static void
	setCustomSelectedChannelBgColor(boolean b) {
		if(b == getCustomSelectedChannelBgColor()) return;
		user().putBoolean(CUSTOM_SEL_CHANNEL_BG_COLOR, b);
	}
	
	/**
	 * Gets the custom background color that
	 * is used when the mouse pointer is over a channel.
	 * @return The custom background color that
	 * is used when the mouse pointer is over a channel.
	 */
	public static Color
	getHighlightedChannelBgColor() {
		int c = user().getInt(HL_CHANNEL_BG_COLOR, DEF_HL_CHANNEL_BG_COLOR);
		return c == -1 ? null : new Color(c);
	}
	
	/**
	 * Sets the custom background color to
	 * be used when the mouse pointer is over a channel.
	 * Use <code>null</code> to remove the current value.
	 * @param color The new background color to
	 * be used when the mouse pointer is over a channel.
	 */
	public static void
	setHighlightedChannelBgColor(Color c) {
		if(c == null) {
			user().remove(HL_CHANNEL_BG_COLOR);
			return;
		}
		
		if(getHighlightedChannelBgColor() != null) {
			if(c.getRGB() == getHighlightedChannelBgColor().getRGB()) return;
		}
		
		user().putInt(HL_CHANNEL_BG_COLOR, c.getRGB());
	}
	
	/**
	 * Determines whether to use a custom background
	 * color when the mouse pointer is over a channel.
	 * @return <code>true</code> if custom background color
	 * should be used, <code>false</code> otherwise.
	 */
	public static boolean
	getCustomHighlightedChannelBgColor() {
		return user().getBoolean (
			CUSTOM_HL_CHANNEL_BG_COLOR, DEF_CUSTOM_HL_CHANNEL_BG_COLOR
		);
	}
	
	/**
	 * Sets whether to use a custom background
	 * color when the mouse pointer is over a channel.
	 * @param b specify <code>true</code> to use a custom
	 * background color, <code>false</code> otherwise.
	 */
	public static void
	setCustomHighlightedChannelBgColor(boolean b) {
		if(b == getCustomHighlightedChannelBgColor()) return;
		user().putBoolean(CUSTOM_HL_CHANNEL_BG_COLOR, b);
	}
	
	/**
	 * Gets the divider location of the vertical split pane.
	 * @return The divider location of the vertical split pane.
	 */
	public static int
	getVSplitDividerLocation() {
		return user().getInt(VSPLIT_DIVIDER_LOCATION, DEF_VSPLIT_DIVIDER_LOCATION);
	}
	
	/**
	 * Sets the divider location of the vertical split pane.
	 * @param i The new divider location of the vertical split pane.
	 */
	public static void
	setVSplitDividerLocation(int i) {
		if(i == getVSplitDividerLocation()) return;
		user().putInt(VSPLIT_DIVIDER_LOCATION, i);
	}
	
	/**
	 * Gets the current orchestra index of the last session.
	 * @return The current orchestra index of the last session.
	 */
	public static int
	getCurrentOrchestraIndex() {
		return user().getInt(CURRENT_ORCHESTRA_IDX, DEF_CURRENT_ORCHESTRA_IDX);
	}
	
	/**
	 * Sets the current orchestra index.
	 * @param i The orchestra index to be set as current.
	 */
	public static void
	setCurrentOrchestraIndex(int i) {
		if(i == getCurrentOrchestraIndex()) return;
		user().putInt(CURRENT_ORCHESTRA_IDX, i);
	}
	
	public int
	getDefaultIntValue(String name) {
		if(name == LS_CONSOLE_BACKGROUND_COLOR) return 0xffffff;
		if(name == LS_CONSOLE_TEXT_COLOR) return 0x000000;
		if(name == LS_CONSOLE_NOTIFY_COLOR) return 0xcccccc;
		if(name == LS_CONSOLE_WARNING_COLOR) return 0x6699ff;
		if(name == LS_CONSOLE_ERROR_COLOR) return 0xff0000;
		
		return super.getDefaultIntValue(name);
	}
}
