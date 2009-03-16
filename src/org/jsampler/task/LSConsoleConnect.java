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

import java.net.InetSocketAddress;
import java.net.Socket;

import org.jsampler.CC;

import static org.jsampler.JSI18n.i18n;


/**
 * Establishes the connection to LinuxSampler used by the LS Console.
 * @author Grigor Iliev
 */
public class LSConsoleConnect extends EnhancedTask<Socket> {
	private Socket oldSocket;
	
	/** Creates a new instance of <code>LSConsoleConnect</code>. */
	public
	LSConsoleConnect() { this(null); }
	
	/**
	 * Creates a new instance of <code>LSConsoleConnect</code>.
	 * @param oldSocket The socket to close.
	 */
	public
	LSConsoleConnect(Socket oldSocket) {
		setTitle("LSConsoleConnect_task");
		setDescription(i18n.getMessage("LSConsoleConnect.description"));
		setSilent(true);
		this.oldSocket = oldSocket;
	}
	
	/** The entry point of the task. */
	@Override
	public void
	exec() throws Exception {
		String address = CC.getCurrentServer().getAddress();
		int port = CC.getCurrentServer().getPort();
		
		if(oldSocket != null) oldSocket.close();
		InetSocketAddress sockAddr = new InetSocketAddress(address, port);
		
		int soTimeout = 10000;
		Socket sock = new Socket();
		sock.bind(null);
		sock.connect(sockAddr, soTimeout);
		sock.setSoTimeout(soTimeout);
		sock.setTcpNoDelay(true);
		
		setResult(sock);
	}
}
