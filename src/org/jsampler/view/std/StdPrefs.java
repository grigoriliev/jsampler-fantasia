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
	
	
	/**
	 * Creates a new instance of <code>StdPrefs</code>.
	 * @param pathName The path name of the preferences node.
	 */
	public
	StdPrefs(String pathName) {
		super(pathName);
	}
	
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
	
	public int
	getDefaultIntValue(String name) {
		if(name == DEFAULT_CHANNEL_VOLUME) return 100;
		if(name == LS_CONSOLE_HISTSIZE) return 1000;
		if(name == RECENT_LSCP_SCRIPTS_SIZE) return 7;
		
		return super.getDefaultIntValue(name);
	}
	
	public boolean
	getDefaultBoolValue(String name) {
		if(name == SAVE_LS_CONSOLE_HISTORY) return true;
		if(name == SHOW_LS_CONSOLE_WHEN_RUN_SCRIPT) return true;
		if(name == USE_CHANNEL_DEFAULTS) return true;
		
		return super.getDefaultBoolValue(name);
	}
}
