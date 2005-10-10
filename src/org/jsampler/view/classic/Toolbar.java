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

package org.jsampler.view.classic;

import javax.swing.JButton;
import javax.swing.JToolBar;

import org.jsampler.view.classic.A4n;

import static org.jsampler.view.classic.ClassicI18n.i18n;


/**
 *
 * @author Grigor Iliev
 */
public class Toolbar extends JToolBar {
	private final JButton btnNew = new ToolbarButton(A4n.newChannel);
	private final JButton btnDuplicate = new ToolbarButton(A4n.duplicateChannels);
	private final JButton btnUp = new ToolbarButton(A4n.moveChannelsUp);
	private final JButton btnDown = new ToolbarButton(A4n.moveChannelsDown);
	private final JButton btnRemove = new ToolbarButton(A4n.removeChannels);
	
	private final JButton btnSamplerInfo = new ToolbarButton(A4n.samplerInfo);
	private final JButton btnRefresh = new ToolbarButton(A4n.refresh);
	
	private final JButton btnPreferences = new ToolbarButton(A4n.preferences);
	
	
	/** Creates a new instance of Toolbar */
	public Toolbar() {
		super(i18n.getLabel("Toolbar.name"));
		
		add(btnSamplerInfo);
		//add(btnRefresh);
		
		addSeparator();
		
		add(btnNew);
		add(btnDuplicate);
		add(btnRemove);
		
		addSeparator();
		
		add(btnUp);
		add(btnDown);
		
		addSeparator();
		
		add(btnPreferences);
	}
}
