/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2008 Grigor Iliev <grigor@grigoriliev.com>
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

import javax.swing.Icon;

/**
 * Provides the basic icon set.
 * @author Grigor Iliev
 */
public interface BasicIconSet {
	/** Gets the navigation icon Back. */
	public Icon getBack16Icon();
	
	/** Gets the navigation icon Up. */
	public Icon getUp16Icon();
	
	/** Gets the navigation icon Forward. */
	public Icon getForward16Icon();
	
	public Icon getReload16Icon();
	
	public Icon getPreferences16Icon();
	
	public Icon getWarning32Icon();
	
	public Icon getQuestion32Icon();
}
