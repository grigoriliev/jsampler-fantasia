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

package org.jsampler.view;

/**
 * Provides the basic icon set.
 * @author Grigor Iliev
 */
public interface BasicIconSet<I> {
	public I getApplicationIcon();
	
	/** Gets the navigation icon Back. */
	public I getBack16Icon();
	
	/** Gets the navigation icon Up. */
	public I getUp16Icon();
	
	/** Gets the navigation icon Forward. */
	public I getForward16Icon();
	
	public I getReload16Icon();
	
	public I getPreferences16Icon();
	
	public I getWarning32Icon();
	
	public I getQuestion32Icon();
}
