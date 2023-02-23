/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2009 Grigor Iliev <grigor@grigoriliev.com>
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

package org.jsampler.view.fantasia;

import com.apple.eawt.ApplicationEvent;

import org.jsampler.CC;
import org.jsampler.JSampler;

import static org.jsampler.view.fantasia.A4n.a4n;

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
		a4n.helpAbout.actionPerformed(null);
		event.setHandled(true);
	}

	@Override
	public void
	handlePreferences(ApplicationEvent event) {
		a4n.editPreferences.actionPerformed(null);
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
