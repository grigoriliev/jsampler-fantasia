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

package org.jsampler;

import java.util.prefs.Preferences;


/**
 * This class represents the preferences of the JSampler package.
 * @author Grigor Iliev
 */
public class Prefs {
	private final static String prefNode = "org.jsampler";
	
	private final static String WINDOW_SIZE_AND_LOCATION = "Mainframe.sizeAndLocation";
	private final static String DEF_WINDOW_SIZE_AND_LOCATION = null;
	
	private final static String WINDOW_MAXIMIZED = "Mainframe.maximized";
	private final static boolean DEF_WINDOW_MAXIMIZED = false;
	
	private final static String SAVE_WINDOW_PROPERTIES = "Mainframe.saveProperties";
	private final static boolean DEF_SAVE_WINDOW_PROPERTIES = true;
	
	private final static String VIEW = "VIEW";
	private final static String DEF_VIEW = "classic";
	
	private final static String INTERFACE_LANGUAGE = "iface.language";
	private final static String DEF_INTERFACE_LANGUAGE = "en";
	
	private final static String INTERFACE_COUNTRY = "iface.country";
	private final static String DEF_INTERFACE_COUNTRY = "US";
	
	private final static String INTERFACE_FONT = "iface.font";
	private final static String DEF_INTERFACE_FONT = null;
	
	private final static String LS_ADDRESS = "LinuxSampler.address";
	private final static String DEF_LS_ADDRESS = "127.0.0.1";
	
	private final static String LS_PORT = "LinuxSampler.port";
	private final static int DEF_LS_PORT = 8888;
	
	
	
	private static Preferences userPrefs = Preferences.userRoot().node(prefNode);
	
	/**
	 * Gets the user preferences node of the JSampler package.
	 * @return The user preferences node of the JSampler package.
	 */
	private static Preferences
	user() { return userPrefs; }
	
	/**
	 * Gets a string representation of the main window's size and location.
	 * The string representation is a comma-separated list
	 * of x and y coordinates, and width and height of the window.
	 * @return A string representation of the main window's size and location,
	 * or <code>null</code> if the value is not set.
	 */
	public static String
	getWindowSizeAndLocation() {
		return user().get(WINDOW_SIZE_AND_LOCATION, DEF_WINDOW_SIZE_AND_LOCATION);
	}
	
	/**
	 * Sets the main window's size and location.
	 * Use <code>null</code> to remove the current value.
	 * @param s A string representation of the main window's size and location.
	 */
	public static void
	setWindowSizeAndLocation(String s) {
		if(s == null) {
			user().remove(WINDOW_SIZE_AND_LOCATION);
			return;
		}
		
		user().put(WINDOW_SIZE_AND_LOCATION, s);
	}
	
	/**
	 * Determines whether the main window should be maximized.
	 * @return <code>true</code> if the main window should be maximized,
	 * <code>false</code> otherwise.
	 */
	public static boolean
	getWindowMaximized() {
		return user().getBoolean(WINDOW_MAXIMIZED, DEF_WINDOW_MAXIMIZED);
	}
	
	/**
	 * Sets whether the main window should be maximized.
	 * @param b If <code>true</code> the main window should be maximized.
	 */
	public static void
	setWindowMaximized(boolean b) {
		if(b == getWindowMaximized()) return;
		user().putBoolean(WINDOW_MAXIMIZED, b);
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
	
// VIEW
	/**
	 * Gets the name of the current View.
	 * @return the name of the current View.
	 */
	public static String
	getView() { return user().get(VIEW, DEF_VIEW); }
	
	/**
	 * Sets the current View of JSampler.
	 * @param view the name of the new View.
	 */
	public static void
	setView(String view) {
		if(view == null) user().remove(VIEW);
		else if(!view.equals(getView())) user().put(VIEW, view);
	}
	
	/**
	 * Gets the interface language.
	 * @return The interface language.
	 */
	public static String
	getInterfaceLanguage() { return user().get(INTERFACE_LANGUAGE, DEF_INTERFACE_LANGUAGE); }
	
	/**
	 * Sets the interface language.
	 * @return <code>true</code> if the interface language has changed and <code>false</code>
	 * otherwise.
	 */
	public static boolean
	setInterfaceLanguage(String language) {
		if(language == null) {
			user().remove(INTERFACE_LANGUAGE);
			return true;
		} else if(!language.equals(getInterfaceLanguage())) {
			user().put(INTERFACE_LANGUAGE, language);
			return true;
		}
		return false;
	}
	
	/**
	 * Gets the interface country.
	 * @return The interface country.
	 */
	public static String
	getInterfaceCountry() { return user().get(INTERFACE_COUNTRY, DEF_INTERFACE_COUNTRY); }
	
	/**
	 * Sets the interface country.
	 * @return <code>true</code> if the interface country has changed and <code>false</code>
	 * otherwise.
	 */
	public static boolean
	setInterfaceCountry(String country) {
		if(country == null) {
			user().remove(INTERFACE_COUNTRY);
			return true;
		} else if(!country.equals(getInterfaceCountry())) {
			user().put(INTERFACE_COUNTRY, country);
			return true;
		}
		return false;
	}
	
	/**
	 * Gets the interface font.
	 * @return The interface font.
	 */
	public static String
	getInterfaceFont() { return user().get(INTERFACE_FONT, DEF_INTERFACE_FONT); }
	
	/**
	 * Sets the interface font.
	 * @return <code>true</code> if the interface font has changed and <code>false</code>
	 * otherwise.
	 */
	public static boolean
	setInterfaceFont(String font) {
		if(font == null) {
			if(getInterfaceFont() == null) return false;
			user().remove(INTERFACE_FONT);
			return true;
		} else if(!font.equals(getInterfaceFont())) {
			user().put(INTERFACE_FONT, font);
			return true;
		}
		return false;
	}

// PREFERENCES
	/**
	 * Gets the LinuxSampler address.
	 * @return The LinuxSampler address.
	 */
	public static String
	getLSAddress() { return user().get(LS_ADDRESS, DEF_LS_ADDRESS); }
	
	/**
	 * Sets the LinuxSampler address.
	 * @param address The LinuxSampler address.
	 */
	public static void
	setLSAddress(String address) {
		if(address.length() == 0) user().remove(LS_ADDRESS);
		else if(!address.equals(getLSAddress()))
			user().put(LS_ADDRESS, address);
	}

	/**
	 * Gets the LinuxSampler port.
	 * @return The LinuxSampler port number.
	 */
	public static int
	getLSPort() { return user().getInt(LS_PORT, DEF_LS_PORT); }
	
	/**
	 * Sets the LinuxSampler port number.
	 * This method das not check the validity of the port number.
	 * @param port the port number. Use -1 to reset to default value.
	 */
	public static void
	setLSPort(int port) {
		if(port == -1) user().remove(LS_PORT);
		else if(port != getLSPort()) user().putInt(LS_PORT, port);
	}
}
