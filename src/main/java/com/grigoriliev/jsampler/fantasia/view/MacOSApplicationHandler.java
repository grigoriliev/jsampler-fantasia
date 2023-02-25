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

package com.grigoriliev.jsampler.fantasia.view;

import com.apple.eawt.ApplicationEvent;

import com.grigoriliev.jsampler.CC;
import com.grigoriliev.jsampler.JSampler;

/**
 *
 * @author Grigor Iliev
 */
public class MacOSApplicationHandler extends com.apple.eawt.ApplicationAdapter {
	public
	MacOSApplicationHandler() {
		com.apple.eawt.Application app = com.apple.eawt.Application.getApplication();
		app.addApplicationListener(this);
		app.setEnabledPreferencesMenu(true);
	}

	@Override
	public void
	handleAbout(ApplicationEvent event) {
		A4n.a4n.helpAbout.actionPerformed(null);
		event.setHandled(true);
	}

	@Override
	public void
	handlePreferences(ApplicationEvent event) {
		A4n.a4n.editPreferences.actionPerformed(null);
		event.setHandled(true);
	}

	@Override
	public void
	handleQuit(ApplicationEvent event) {
		CC.getMainFrame().onWindowClose();
	}

	@Override
	public void
	handleOpenFile(ApplicationEvent event) {
		JSampler.open(event.getFilename());
	}
}
