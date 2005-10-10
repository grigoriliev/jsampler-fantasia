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
 *
 * @author Grigor Iliev
 */
public class Prefs {
	private final static String prefNode = "org.jsampler";
	
	private final static String VIEW = "VIEW";
	private final static String DEF_VIEW = "classic";
	
	public final static String INTERFACE_LANGUAGE = "iface.language";
	public final static String DEF_INTERFACE_LANGUAGE = "en";
	
	public final static String INTERFACE_COUNTRY = "iface.country";
	public final static String DEF_INTERFACE_COUNTRY = "US";
	
	public final static String INTERFACE_FONT = "iface.font";
	public final static String DEF_INTERFACE_FONT = null;
	
	public final static String LS_ADDRESS = "LinuxSampler.address";
	public final static String DEF_LS_ADDRESS = "127.0.0.1";
	
	public final static String LS_PORT = "LinuxSampler.port";
	public final static int DEF_LS_PORT = 8888;
	
	
	
	private static Preferences sysPrefs = Preferences.systemRoot().node(prefNode);
	private static Preferences userPrefs = Preferences.userRoot().node(prefNode);
	
	public static Preferences
	sys() { return sysPrefs; }
	
	public static Preferences
	user() { return userPrefs; }
	
// VIEW
	public static String
	getView() { return user().get(VIEW, DEF_VIEW); }
	
	public static void
	setView(String view) {
		if(view == null) user().remove(VIEW);
		else if(!view.equals(getView())) user().put(VIEW, view);
	}
	
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
	public static String
	getLSAddress() { return user().get(LS_ADDRESS, DEF_LS_ADDRESS); }
	
	public static void
	setLSAddress(String address) {
		if(address.length() == 0) user().remove(LS_ADDRESS);
		else if(!address.equals(getLSAddress()))
			user().put(LS_ADDRESS, address);
	}

	public static int
	getLSPort() { return user().getInt(LS_PORT, DEF_LS_PORT); }
	
	/**
	 * Sets the LinuxSampler port number.
	 * This method das not check the validity of the port number.
	 * @param port the port number. Use -1 to reset to default value.
	 */
	public static void
	setAuthSrvPort(int port) {
		if(port == -1) user().remove(LS_PORT);
		else if(port != getLSPort()) user().putInt(LS_PORT, port);
	}
}
