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

import org.jsampler.CC;


/**
 * This task sets the Linux Sampler address.
 * This needs to be done out of the event-dispatching thread due to
 * the synchronization of <code>org.linuxsampler.lscp.Client</code> methods
 * (setting the server address and port can be time consuming).
 * @author Grigor Iliev
 */
public class SetServerAddress extends EnhancedTask {
	private String address;
	private int port;
	
	/**
	 * Creates new instance of <code>SetServerAddress</code>.
	 * @param address The Linux Sampler address.
	 * If <code>address</code> is <code>null</code>, sets to default address - 127.0.0.1.
	 * @param port The Linux Sampler port number.
	 */
	public
	SetServerAddress(String address, int port) {
		setSilent(true);
		setTitle("");
		setDescription("");
		this.address = address;
		this.port = port;
	}
	
	/** The entry point of the task. */
	@Override
	public void
	exec() throws Exception {
		CC.getClient().setServerAddress(address);
		CC.getClient().setServerPort(port);
	}
}
