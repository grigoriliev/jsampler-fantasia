/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2011 Grigor Iliev <grigor@grigoriliev.com>
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

package org.jsampler;

import java.beans.PropertyChangeSupport;

import java.io.BufferedReader;
import java.io.StringReader;

import java.util.Vector;
import java.util.prefs.Preferences;

/**
 *
 * @author Grigor Iliev
 */
public class JSPrefs extends PropertyChangeSupport {
	/** Property representing the global sampler-wide limit of maximum voices. */
	public final static String GLOBAL_VOICE_LIMIT = "globalVoiceLimit";
	
	/** Property representing the global sampler-wide limit of maximum disk streams. */
	public final static String GLOBAL_STREAM_LIMIT = "globalStreamLimit";
	
	/**
	 * Property which specifies whether to apply default
	 * actions to newly created sampler channels.
	 */
	public final static String USE_CHANNEL_DEFAULTS = "samplerChannel.useDefaultActions";
	
	/**
	 * Property representing the default engine to be used when
	 * new sampler channel is created. The action is taken only if
	 * <code>USE_CHANNEL_DEFAULTS</code> is <code>true</code>.
	 */
	public final static String DEFAULT_ENGINE = "defaultEngine";
	
	/**
	 * Property representing the default MIDI input to be used when
	 * new sampler channel is created. The action is taken only if
	 * <code>USE_CHANNEL_DEFAULTS</code> is <code>true</code>.
	 */
	public final static String DEFAULT_MIDI_INPUT = "defaultMidiInput";
	
	/**
	 * Property representing the default audio output to be used when
	 * new sampler channel is created. The action is taken only if
	 * <code>USE_CHANNEL_DEFAULTS</code> is <code>true</code>.
	 */
	public final static String DEFAULT_AUDIO_OUTPUT = "defaultAudioOutput";
	
	/**
	 * Property representing the default MIDI instrument map to be used when
	 * new sampler channel is created. The action is taken only if
	 * <code>USE_CHANNEL_DEFAULTS</code> is <code>true</code>.
	 */
	public final static String DEFAULT_MIDI_INSTRUMENT_MAP = "defaultMidiInstrumentMap";
	
	/**
	 * Property representing the default channel volume when
	 * new sampler channel is created. The action is taken only if
	 * <code>USE_CHANNEL_DEFAULTS</code> is <code>true</code>.
	 */
	public final static String DEFAULT_CHANNEL_VOLUME = "defaultChannelVolume";
	
	/**
	 * Property representing the default MIDI input driver to be used
	 * when creating new MIDI input device.
	 */
	public final static String DEFAULT_MIDI_DRIVER = "defaultMidiDriver";
	
	/**
	 * Property representing the default audio output driver to be used
	 * when creating new audio output device.
	 */
	public final static String DEFAULT_AUDIO_DRIVER = "defaultAudioDriver";
	
	/** Property which specifies whether the volume values should be shown in decibels. */
	public final static String VOL_MEASUREMENT_UNIT_DECIBEL = "volMeasurementUnitDecibel";
	
	/**
	 * Property which specifies whether the MIDI instrument maps
	 * should be included in the session script.
	 */
	public final static String EXPORT_MIDI_MAPS_TO_SESSION_SCRIPT = "exportMidiMapsToSessionScript";
	
	/**
	 * Property which specifies whether to set the MIDI instrument loading
	 * in background when exporting MIDI instrument maps to LSCP script.
	 */
	public final static String LOAD_MIDI_INSTRUMENTS_IN_BACKGROUND = "loadMidiInstrumentsInBackground";
	
	/**
	 * Property which specifies whether the user should manually select a server to connect on startup.
	 */
	public final static String MANUAL_SERVER_SELECT_ON_STARTUP = "manualServerSelectOnStartup";
	
	/**
	 * Property which specifies whether the backend should be launched by jsampler
	 * if it is not yet started, when trying to connect locally - 127.0.0.1 or localhost.
	 */
	public final static String LAUNCH_BACKEND_LOCALLY = "launchBackednLocally";
	
	/**
	 * Property which specifies the command to be used to start the backend.
	 */
	public final static String BACKEND_LAUNCH_COMMAND = "backendLaunchCommand";
	
	/**
	 * Property which specifies whether the backend should be launched by jsampler
	 * if it is not yet started, when trying to connect locally - 127.0.0.1 or localhost.
	 */
	public final static String BACKEND_LAUNCH_DELAY = "backendLaunchDelay";
	
	/**
	 * Integer property which provides the index of the server to connect on startup.
	 */
	public final static String SERVER_INDEX = "serverIndex";
	
	/**
	 * Property specifying the MIDI bank numbering, whether
	 * the index of the first MIDI bank is 0 or 1 (zero-based or one-based).
	 */
	public final static String FIRST_MIDI_BANK_NUMBER = "firstMidiBankNumber";
	
	/**
	 * Property specifying the MIDI program numbering, whether
	 * the index of the first MIDI program is 0 or 1 (zero-based or one-based).
	 */
	public final static String FIRST_MIDI_PROGRAM_NUMBER = "firstMidiProgramNumber";
	
	/** Property representing the socket read timeout (in seconds). */
	public final static String SOCKET_READ_TIMEOUT = "socketReadTimeout";
	
	
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
	
	
	private final String pathName;
	private final Preferences userPrefs;
	
	/**
	 * Creates a new instance of <code>JSPrefs</code>.
	 * @param pathName The path name of the preferences node.
	 */
	public
	JSPrefs(String pathName) {
		super(new Object());
		
		this.pathName = pathName;
		userPrefs = Preferences.userRoot().node(pathName);
	}
	
	private Preferences
	user() { return userPrefs; }
	
	/**
	 * Gets a string property.
	 * @param name The name of the property.
	 * @return The value of the specified property.
	 * If the property is not set, the return value is <code>null</code>.
	 * @see #getDefaultStringValue
	 */
	public String
	getStringProperty(String name) {
		return getStringProperty(name, getDefaultStringValue(name));
	}
	
	/**
	 * Gets a string property.
	 * @param name The name of the property.
	 * @param defaultValue The value to return if the property is not set.
	 * @return The value of the specified property.
	 */
	public String
	getStringProperty(String name, String defaultValue) {
		return user().get(name, defaultValue);
	}
	
	/**
	 * Sets a string property.
	 * @param name The name of the property.
	 * @param s The new value for the specified property.
	 */
	public void
	setStringProperty(String name, String s) {
		String oldValue = getStringProperty(name);
		
		if(s == null) user().remove(name);
		else user().put(name, s);
		
		firePropertyChange(name, oldValue, s);
	}
	
	/**
	 * Gets the default value for the specified property.
	 * The default value is used when the property is not set.
	 * Override this method to provide custom default values for specific properties.
	 * @param name The name of the property whose default value should be obtained.
	 * @return <code>null</code>
	 * @see #getStringProperty(String name)
	 */
	public String
	getDefaultStringValue(String name) {
		if(BACKEND_LAUNCH_COMMAND.equals(name)) return "linuxsampler";
		if(RECENT_LSCP_SCRIPTS.equals(name)) return "";
		if(DEFAULT_ENGINE.equals(name)) return "GIG";
		if(DEFAULT_MIDI_INPUT.equals(name)) return "firstDeviceNextChannel";
		if(DEFAULT_AUDIO_OUTPUT.equals(name)) return "firstDevice";
		if(DEFAULT_MIDI_DRIVER.equals(name)) return "ALSA";
		if(DEFAULT_AUDIO_DRIVER.equals(name)) return "ALSA";
		if(DEFAULT_MIDI_INSTRUMENT_MAP.equals(name)) return "midiInstrumentMap.none";
		
		return null;
	}
	
	/**
	 * Gets a string list property.
	 * @param name The name of the property.
	 * @return The value of the specified property.
	 * If the property is not set, the return value is an empty array.
	 * @see #getDefaultStringListValue
	 */
	public String[]
	getStringListProperty(String name) {
		return getStringListProperty(name, getDefaultStringListValue(name));
	}
	
	/**
	 * Gets a string list property.
	 * @param name The name of the property.
	 * @param defaultValue The value to return if the property is not set.
	 * @return The value of the specified property.
	 */
	public String[]
	getStringListProperty(String name, String[] defaultValue) {
		String s = user().get(name, null);
		if(s == null) return defaultValue;
		if(s.length() == 0) return new String[0];
		
		BufferedReader br = new BufferedReader(new StringReader(s));
		Vector<String> v = new Vector();
		
		try {
			s = br.readLine();
			while(s != null) {
				v.add(s);
				s = br.readLine();
			}
		} catch(Exception x) {
			x.printStackTrace();
		}
		
		return v.toArray(new String[v.size()]);
	}
	
	/**
	 * Sets a string list property.
	 * Note that the string elements may not contain new lines.
	 * @param name The name of the property.
	 * @param list The new value for the specified property.
	 */
	public void
	setStringListProperty(String name, String[] list) {
		String[] oldValue = getStringListProperty(name);
		
		if(list == null) user().remove(name);
		else {
			StringBuffer sb = new StringBuffer();
			for(String s : list) sb.append(s).append("\n");
			user().put(name, sb.toString());
		}
		
		firePropertyChange(name, oldValue, list);
	}
	
	/**
	 * Gets the default value for the specified property.
	 * The default value is used when the property is not set.
	 * Override this method to provide custom default values for specific properties.
	 * @param name The name of the property whose default value should be obtained.
	 * @return An empty array.
	 * @see #getStringListProperty(String name)
	 */
	public String[]
	getDefaultStringListValue(String name) { return new String[0]; }
	
	/**
	 * Gets an integer property.
	 * @param name The name of the property.
	 * @return The value of the specified property.
	 * @see #getDefaultIntValue
	 */
	public int
	getIntProperty(String name) {
		return getIntProperty(name, getDefaultIntValue(name));
	}
	
	/**
	 * Gets an integer property.
	 * @param name The name of the property.
	 * @param defaultValue The value to return if the property is not set.
	 * @return The value of the specified property.
	 */
	public int
	getIntProperty(String name, int defaultValue) {
		return user().getInt(name, defaultValue);
	}
	
	/**
	 * Gets the default value for the specified property.
	 * The default value is used when the property is not set.
	 * Override this method to provide custom default values for specific properties.
	 * @param name The name of the property whose default value should be obtained.
	 * @return <code>0</code>
	 * @see #getIntProperty(String name)
	 */
	public int
	getDefaultIntValue(String name) {
		if(GLOBAL_VOICE_LIMIT.equals(name)) return 64;
		if(GLOBAL_STREAM_LIMIT.equals(name)) return 90;
		if(SOCKET_READ_TIMEOUT.equals(name)) return 90;
		if(FIRST_MIDI_BANK_NUMBER.equals(name)) return 0;
		if(FIRST_MIDI_PROGRAM_NUMBER.equals(name)) return 1;
		if(BACKEND_LAUNCH_DELAY.equals(name)) return 3;
		if(DEFAULT_CHANNEL_VOLUME.equals(name)) return 100;
		if(LS_CONSOLE_HISTSIZE.equals(name)) return 1000;
		if(RECENT_LSCP_SCRIPTS_SIZE.equals(name)) return 7;
		if(MAXIMUM_MASTER_VOLUME.equals(name)) return 100;
		if(MAXIMUM_CHANNEL_VOLUME.equals(name)) return 100;
		if(INSTRUMENTS_DB_FRAME_SORT_ORDER.equals(name)) return 1;
		if(DEFAULT_CHANNEL_VIEW.equals(name)) return 1;
		if(CHANNEL_VIEW_ON_MOUSE_OVER.equals(name)) return 1;
		
		return 0;
	}
	
	/**
	 * Sets an integer property.
	 * @param name The name of the property.
	 * @param i The new value for the specified property.
	 */
	public void
	setIntProperty(String name, int i) {
		int oldValue = getIntProperty(name);
		user().putInt(name, i);
		firePropertyChange(name, oldValue, i);
	}
	
	
	/**
	 * Gets a boolean property.
	 * @param name The name of the property.
	 * @return The value of the specified property.
	 * @see #getDefaultBoolValue
	 */
	public boolean
	getBoolProperty(String name) {
		return getBoolProperty(name, getDefaultBoolValue(name));
	}
	/**
	 * Gets a boolean property.
	 * @param name The name of the property.
	 * @param defaultValue The value to return if the property is not set.
	 * @return The value of the specified property.
	 */
	public boolean
	getBoolProperty(String name, boolean defaultValue) {
		return user().getBoolean(name, defaultValue);
	}
	
	/**
	 * Sets a boolean property.
	 * @param name The name of the property.
	 * @param b The new value for the specified property.
	 */
	public void
	setBoolProperty(String name, boolean b) {
		boolean oldValue = getBoolProperty(name);
		user().putBoolean(name, b);
		firePropertyChange(name, oldValue, b);
	}
	
	/**
	 * Gets the default value for the specified property.
	 * The default value is used when the property is not set.
	 * Override this method to provide custom default values for specific properties.
	 * @param name The name of the property whose default value should be obtained.
	 * @see #getBoolProperty(String name)
	 */
	public boolean
	getDefaultBoolValue(String name) {
		if(VOL_MEASUREMENT_UNIT_DECIBEL.equals(name)) return true;
		if(EXPORT_MIDI_MAPS_TO_SESSION_SCRIPT.equals(name)) return true;
		if(LOAD_MIDI_INSTRUMENTS_IN_BACKGROUND.equals(name)) return true;
		if(LAUNCH_BACKEND_LOCALLY.equals(name)) return true;
		if(DIFFERENT_CHANNEL_VIEW_ON_MOUSE_OVER.equals(name)) return true;
		if(CONFIRM_CHANNEL_REMOVAL.equals(name)) return true;
		if(CONFIRM_DEVICE_REMOVAL.equals(name)) return true;
		if(CONFIRM_APP_QUIT.equals(name)) return true;
		if(SAVE_LS_CONSOLE_HISTORY.equals(name)) return true;
		if(USE_CHANNEL_DEFAULTS.equals(name)) return true;
		if("nativeFileChoosers".equals(name) && CC.isMacOS()) return true;
		
		return false;
	}
}
