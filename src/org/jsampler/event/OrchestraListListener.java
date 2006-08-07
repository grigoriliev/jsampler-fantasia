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
 * The listener interface for receiving events about adding and removing orchestras from the list.
 * @author Grigor Iliev
 */
public interface OrchestraListListener extends java.util.EventListener {
	/** Invoked when an orchestra is added (or inserted) to the orchestra list. */
	public void orchestraAdded(OrchestraListEvent e);
	
	/** Invoked when an orchestra is removed from the orchestra list. */
	public void orchestraRemoved(OrchestraListEvent e);
}
