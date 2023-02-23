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

package org.jsampler;

import java.util.prefs.Preferences;


/**
 * This class represents the preferences of the JSampler package.
 * @author Grigor Iliev
 */
public class Prefs {
	private final static String prefNode = "org.jsampler";
	
	private final static String VIEW = "VIEW";
	private final static String DEF_VIEW = "classic";
	
	private final static String JSAMPLER_HOME = "JSampler.home";
	private final static String DEF_JSAMPLER_HOME = null;
	
	private final static String INTERFACE_LANGUAGE = "iface.language";
	private final static String DEF_INTERFACE_LANGUAGE = "en";
	
	private final static String INTERFACE_COUNTRY = "iface.country";
	private final static String DEF_INTERFACE_COUNTRY = "US";
	
	private final static String INTERFACE_FONT = "iface.font";
	private final static String DEF_INTERFACE_FONT = null;
	
		
	private static Preferences userPrefs = Preferences.userRoot().node(prefNode);
	
	/**
	 * Gets the user preferences node of the JSampler package.
	 * @return The user preferences node of the JSampler package.
	 */
	private static Preferences
	user() { return userPrefs; }
	
	
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
	 * Gets the absolute path of JSampler home directory.
	 * @return The absolute path of JSampler home directory or
	 * <code>null</code> if the JSampler home directory is not set.
	 */
	public static String
	getJSamplerHome() { return user().get(JSAMPLER_HOME, DEF_JSAMPLER_HOME); }
	
	/**
	 * Sets the JSampler home directory.
	 * @param home The absolute path of JSampler home directory.
	 * @return <code>true</code> if the JSampler home directory has
	 * been changed and <code>false</code> otherwise.
	 */
	public static boolean
	setJSamplerHome(String home) {
		if(home == null) {
			if(getJSamplerHome() == null) return false;
			user().remove(JSAMPLER_HOME);
			return true;
		} else if(!home.equals(getJSamplerHome())) {
			user().put(JSAMPLER_HOME, home);
			return true;
		}
		return false;
	}
}
