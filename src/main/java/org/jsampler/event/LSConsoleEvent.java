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

package org.jsampler.event;

/**
 * A semantic event which indicates LS Console changes.
 * @author Grigor Iliev
 */
public class LSConsoleEvent extends java.util.EventObject {
	private String response;
	private String oldCmdLine;
	
	/**
	 * Constructs an <code>LSConsoleEvent</code> object.
	 * @param source The object that originated the event.
	 */
	public
	LSConsoleEvent(Object source) { this(source, null); }
	
	/**
	 * Constructs an <code>LSConsoleEvent</code> object.
	 * @param source The object that originated the event.
	 */
	public
	LSConsoleEvent(Object source, String response) {
		this(source, response, null);
	}
	
	/**
	 * Constructs an <code>LSConsoleEvent</code> object.
	 * @param source The object that originated the event.
	 * @param response Provides the LinuxSampler's response
	 * when <code>responseReceived</code> event occurs.
	 * @param oldCmdLine Provides the previous text of the command line
	 * when <code>commandLineTextChanged</code> event occurs.
	 */
	public
	LSConsoleEvent(Object source, String response, String oldCmdLine) {
		super(source);
		this.response = response;
		setPreviousCommandLineText(oldCmdLine);
	}
	
	/**
	 * Returns the LinuxSampler's response when <code>responseReceived</code> event occurs.
	 * @return The LinuxSampler's response when <code>responseReceived</code> event occurs
	 * and <code>null</code> for all other events.
	 */
	public String
	getResponse() { return response; }
	
	/**
	 * Returns the previous command line text when
	 * <code>commandLineTextChanged</code> event occurs.
	 * @return The previous command line text when <code>commandLineTextChanged</code>
	 * event occurs and <code>null</code> for all other events.
	 */
	public String
	getPreviousCommandLineText() { return oldCmdLine; }
	
	/**
	 * Sets the previous text of the command line.
	 * @param oldCmdLine Provides the previous text of the command line.
	 */
	private void
	setPreviousCommandLineText(String oldCmdLine) { this.oldCmdLine = oldCmdLine; }
}
