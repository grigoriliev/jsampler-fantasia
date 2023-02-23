/*
 *   JSampler - a front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2023 Grigor Iliev <grigor@grigoriliev.com>
 *
 *   This file is part of JSampler.
 *
 *   JSampler is free software: you can redistribute it and/or modify it under
 *   the terms of the GNU General Public License as published by the Free
 *   Software Foundation, either version 3 of the License, or (at your option)
 *   any later version.
 *
 *   JSampler is distributed in the hope that it will be useful, but WITHOUT
 *   ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *   FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 *   more details.
 *
 *   You should have received a copy of the GNU General Public License along
 *   with JSampler. If not, see <https://www.gnu.org/licenses/>.
 */

package org.jsampler.view.classic;

import javax.swing.JToolBar;

import static org.jsampler.view.classic.A4n.a4n;
import static org.jsampler.view.classic.ClassicI18n.i18n;


/**
 *
 * @author Grigor Iliev
 */
public class StandardBar extends JToolBar {
	private final ToolbarButton btnLoadScript = new ToolbarButton(A4n.loadScript);
	private final ToolbarButton btnExportSession = new ToolbarButton(a4n.exportSamplerConfig);
	private final ToolbarButton btnSamplerInfo = new ToolbarButton(A4n.samplerInfo);
	private final ToolbarButton btnRefresh = new ToolbarButton(a4n.refresh);
	private final ToolbarButton btnResetSampler = new ToolbarButton(a4n.resetSampler);
	private final ToolbarButton btnInstrumentsDb = new ToolbarButton(A4n.windowInstrumentsDb);
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
		add(btnInstrumentsDb);
		addSeparator();
		add(btnPreferences);
	}
	
}
