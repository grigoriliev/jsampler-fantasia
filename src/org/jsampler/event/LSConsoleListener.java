/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005, 2006 Grigor Kirilov Iliev
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
 * The listener interface for receiving events about LS Console changes.
 * @author Grigor Iliev
 */
public interface LSConsoleListener {
	/** Invoked when the text in the command line is changed. */
	public void commandLineTextChanged(LSConsoleEvent e);
	
	/** Invoked when the command in the command line has been executed. */
	public void commandExecuted(LSConsoleEvent e);
	
	/** Invoked when response is received from LinuxSampler. */
	public void responseReceived(LSConsoleEvent e);
}
