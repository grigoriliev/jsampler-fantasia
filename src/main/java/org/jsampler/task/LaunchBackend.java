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

package org.jsampler.task;

import net.sf.juife.PDUtils;

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
		
		PDUtils.runOnUiThread(new Runnable() {
			public void
			run() { CC.reconnect(); }
		});
	}
}
