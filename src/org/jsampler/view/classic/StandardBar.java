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

package org.jsampler.view.classic;

import javax.swing.JToolBar;

import static org.jsampler.view.classic.ClassicI18n.i18n;


/**
 *
 * @author Grigor Iliev
 */
public class StandardBar extends JToolBar {
	private final ToolbarButton btnLoadScript = new ToolbarButton(A4n.loadScript);
	private final ToolbarButton btnExportSession = new ToolbarButton(A4n.exportSamplerConfig);
	private final ToolbarButton btnSamplerInfo = new ToolbarButton(A4n.samplerInfo);
	private final ToolbarButton btnRefresh = new ToolbarButton(A4n.refresh);
	private final ToolbarButton btnResetSampler = new ToolbarButton(A4n.resetSampler);
	private final ToolbarButton btnPreferences = new ToolbarButton(A4n.preferences);
	
	
	/**
	 * Creates a new instance of StandardBar
	 */
	public
	StandardBar() {
		super(i18n.getLabel("StandardBar.name"));
		
		setFloatable(false);
		add(btnSamplerInfo);
		addSeparator();
		add(btnLoadScript);
		add(btnExportSession);
		add(btnRefresh);
		add(btnResetSampler);
		addSeparator();
		add(btnPreferences);
	}
	
}
