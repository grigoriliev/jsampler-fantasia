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

package org.jsampler.task;

import javax.swing.SwingUtilities;

import org.jsampler.CC;

import static org.jsampler.JSI18n.i18n;

/**
 *
 * @author Grigor Iliev
 */
public class LaunchBackend extends EnhancedTask {
	private final int delay;
	private final Object monitor;
	
	/**
	 * Creates new instance of <code>LaunchBackend</code>.
	 * @param delay Specifies the delay in seconds.
	 */
	public
	LaunchBackend(int delay, Object monitor) {
		setSilent(true);
		setTitle("LaunchBackend_task");
		setDescription(i18n.getMessage("LaunchBackend.desc"));
		this.delay = delay;
		this.monitor = monitor;
	}
	
	/** The entry point of the task. */
	@Override
	public void
	exec() throws Exception {
		synchronized(monitor) { monitor.wait(delay * 1000); }
		
		SwingUtilities.invokeLater(new Runnable() {
			public void
			run() { CC.reconnect(); }
		});
	}
}
