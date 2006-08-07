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
 * Adapter class for receiving events.
 * This class exists as convenience for creating listener objects.
 * The methods in this class are empty.
 * @author Grigor Iliev
 */
public class OrchestraAdapter implements OrchestraListener {
	/** Invoked when the name of orchestra is changed. */
	public void nameChanged(OrchestraEvent e) { }
	
	/** Invoked when the description of orchestra is changed. */
	public void descriptionChanged(OrchestraEvent e) { }
	
	/** Invoked when an instrument is added to the orchestra. */
	public void instrumentAdded(OrchestraEvent e) { }
	
	/** Invoked when an instrument is removed from the orchestra. */
	public void instrumentRemoved(OrchestraEvent e) { }
	
	/** Invoked when the settings of an instrument are changed. */
	public void instrumentChanged(OrchestraEvent e) { }
}
