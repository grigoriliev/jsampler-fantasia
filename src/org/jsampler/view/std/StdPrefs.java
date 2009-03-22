/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2009 Grigor Iliev <grigor@grigoriliev.com>
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

import org.jsampler.CC;
import org.jsampler.JSPrefs;

/**
 *
 * @author Grigor Iliev
 */
public class StdPrefs extends JSPrefs {
	/** Property representing the maximum number of lines to be kept in the command history. */
	public final static String LS_CONSOLE_HISTSIZE = "LSConsole.historySize";
	
	/** Property which specifies whether the command history should be saved on exit. */
	public final static String SAVE_LS_CONSOLE_HISTORY = "LSConsole.saveCommandHistory";
	
	/** Property representing the background color of the LS Console. */
	public final static String LS_CONSOLE_BACKGROUND_COLOR = "LSConsole.backgroundColor";
	
	/** Property representing the text color of the LS Console. */
	public final static String LS_CONSOLE_TEXT_COLOR = "LSConsole.textColor";
	
	/** Property representing the notification messages' color of the LS Console. */
	public final static String LS_CONSOLE_NOTIFY_COLOR = "LSConsole.notifyColor";
	
	/** Property representing the warning messages' color of the LS Console. */
	public final static String LS_CONSOLE_WARNING_COLOR = "LSConsole.warningColor";
	
	/** Property representing the error messages' color of the LS Console. */
	public final static String LS_CONSOLE_ERROR_COLOR = "LSConsole.errorColor";
	
	/** Property representing the list of recent LSCP scripts. */
	public final static String RECENT_LSCP_SCRIPTS = "recentLscpScripts";
	
	/** Property representing the maximum number of recent LSCP scripts to be stored. */
	public final static String RECENT_LSCP_SCRIPTS_SIZE = "recentLscpScripts.maxNumber";
	
	/** Property which specifies whether the LS Console should be shown when script is run. */
	public final static String SHOW_LS_CONSOLE_WHEN_RUN_SCRIPT = "showLSConsoleWhenRunScript";
	
	/** Property representing the maximum master volume (in percents). */
	public final static String MAXIMUM_MASTER_VOLUME = "maximumMasterVolume";
	
	/** Property representing the maximum channel volume (in percents). */
	public final static String MAXIMUM_CHANNEL_VOLUME = "maximumChannelVolume";
	
	/** Property which specifies whether the user should confirm channel removals. */
	public final static String CONFIRM_CHANNEL_REMOVAL = "confirmChannelRemoval";
	
	/** Property which specifies whether the user should confirm audio/MIDI device removals. */
	public final static String CONFIRM_DEVICE_REMOVAL = "confirmDeviceRemoval";
	
	/** Property which specifies whether the user should confirm quiting. */
	public final static String CONFIRM_APP_QUIT = "confirmAppQuit";
	
	/** Property which specifies the sort order in the instruments database frame. */
	public final static String INSTRUMENTS_DB_FRAME_SORT_ORDER = "instrumentsDbFrameSortOrder";
	
	/** Property representing the channel view to be used when creating a sampler channel. */
	public final static String DEFAULT_CHANNEL_VIEW = "defaultChannelView";
	
	/**
	 * Property which specifies whether a different sampler channel view should be shown
	 * when the mouse cursor is over a sampler channel.
	 */
	public final static String DIFFERENT_CHANNEL_VIEW_ON_MOUSE_OVER = "differentChannelViewOnMO";
	
	/**
	 * Property representing the channel view to be used when
	 * the mouse cursor is over a sampler channel.
	 */
	public final static String CHANNEL_VIEW_ON_MOUSE_OVER = "channelViewOnMouseOver";
	
	
	/**
	 * Creates a new instance of <code>StdPrefs</code>.
	 * @param pathName The path name of the preferences node.
	 */
	public
	StdPrefs(String pathName) {
		super(pathName);
	}
	
	@Override
	public String
	getDefaultStringValue(String name) {
		if(name == RECENT_LSCP_SCRIPTS) return "";
		if(name == DEFAULT_ENGINE) return "GIG";
		if(name == DEFAULT_MIDI_INPUT) return "firstDeviceNextChannel";
		if(name == DEFAULT_AUDIO_OUTPUT) return "firstDevice";
		if(name == DEFAULT_MIDI_DRIVER) return "ALSA";
		if(name == DEFAULT_AUDIO_DRIVER) return "ALSA";
		if(name == DEFAULT_MIDI_INSTRUMENT_MAP) return "midiInstrumentMap.none";
		
		return super.getDefaultStringValue(name);
	}
	
	@Override
	public int
	getDefaultIntValue(String name) {
		if(name == DEFAULT_CHANNEL_VOLUME) return 100;
		if(name == LS_CONSOLE_HISTSIZE) return 1000;
		if(name == RECENT_LSCP_SCRIPTS_SIZE) return 7;
		if(name == MAXIMUM_MASTER_VOLUME) return 100;
		if(name == MAXIMUM_CHANNEL_VOLUME) return 100;
		if(name == INSTRUMENTS_DB_FRAME_SORT_ORDER) return 1;
		if(name == DEFAULT_CHANNEL_VIEW) return 1;
		if(name == CHANNEL_VIEW_ON_MOUSE_OVER) return 1;
		
		return super.getDefaultIntValue(name);
	}
	
	@Override
	public boolean
	getDefaultBoolValue(String name) {
		if(name == DIFFERENT_CHANNEL_VIEW_ON_MOUSE_OVER) return true;
		if(name == CONFIRM_CHANNEL_REMOVAL) return true;
		if(name == CONFIRM_DEVICE_REMOVAL) return true;
		if(name == CONFIRM_APP_QUIT) return true;
		if(name == SAVE_LS_CONSOLE_HISTORY) return true;
		if(name == USE_CHANNEL_DEFAULTS) return true;
		if("nativeFileChoosers".equals(name) && CC.isMacOS()) return true;
		
		return super.getDefaultBoolValue(name);
	}
}
