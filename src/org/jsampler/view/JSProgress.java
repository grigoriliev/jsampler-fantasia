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

package org.jsampler.view;

/**
 * Defines the requirements for an object responsible for the indication of work in progress.
 * @author Grigor Iliev
 */
public interface JSProgress {
	/**
	 * Sets the progress string.
	 * @param s The value of the progress string.
	 */
	public void setString(String s);
	
	/** Initiates the indication that an operation is ongoing. */
	public void start();
	
	/** Stops the indication that an operation is ongoing. */
	public void stop();
}
