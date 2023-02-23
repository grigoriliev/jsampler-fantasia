/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2007 Grigor Iliev <grigor@grigoriliev.com>
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

package org.jsampler;

import java.util.Vector;

import net.sf.juife.event.GenericEvent;
import net.sf.juife.event.GenericListener;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 *
 * @author Grigor Iliev
 */
public class ServerList {
	private final Vector<Server> servers = new Vector<Server>();
	private final Vector<GenericListener> listeners = new Vector<GenericListener>();
	
	private final GenericListener l = new GenericListener() {
		public void
		jobDone(GenericEvent e) { fireChangeEvent(); }
	};
	
	/**
	 * Creates a new instance of <code>ServerList</code>
	 */
	public
	ServerList() {
		
	}
	
	/**
	 * Registers the specified listener to be notified when the server list is changed.
	 * @param l The <code>GenericListener</code> to register.
	 */
	public void
	addChangeListener(GenericListener l) { listeners.add(l); }
	
	/**
	 * Removes the specified listener.
	 * @param l The <code>GenericListener</code> to remove.
	 */
	public void
	removeChangeListener(GenericListener l) { listeners.remove(l); }
	
	/**
	 * Adds the specified server to the server list.
	 */
	public void
	addServer(Server server) {
		servers.add(server);
		server.addChangeListener(l);
		fireChangeEvent();
	}
	
	/**
	 * Gets the current number of servers in the list.
	 * @return The current number of servers in the list.
	 */
	public int
	getServerCount() { return servers.size(); }
	
	/**
	 * Gets the server at the specified position.
	 * @param idx The index of the server to be returned.
	 * @return The server at the specified position.
	 */
	public Server
	getServer(int idx) { return servers.get(idx); }
	
	/**
	 * Removes the specified server from the list.
	 * @param idx The index of the server to remove.
	 */
	public void
	removeServer(int idx) {
		Server server = servers.get(idx);
		servers.removeElementAt(idx);
		server.removeChangeListener(l);
		fireChangeEvent();
	}
	
	/**
	 * Removes the specified server from the list.
	 * @param server The server to remove.
	 * @return <code>true</code> if the list contained the specified server.
	 */
	public boolean
	removeServer(Server server) {
		boolean b = servers.remove(server);
		fireChangeEvent();
		return b;
	}
	
	/**
	 * Gets the position of the specified server in this server list.
	 * @param server The server whose index should be returned.
	 * @return The position of the specified server in this server list,
	 * and -1 if <code>server</code> is <code>null</code> or
	 * the server list does not contain the specified server.
	 */
	public int
	getServerIndex(Server server) {
		if(server == null) return -1;
		
		for(int i = 0; i < getServerCount(); i++) {
			if(getServer(i) == server) return i;
		}
		
		return -1;
	}
	
	/**
	 * Moves the specified server one the top of the server list.
	 * This method does nothing if <code>server</code> is <code>null</code>,
	 * the server list does not contain the specified server,
	 * or if the server is already on the top.
	 * @param server The server to move on top.
	 */
	public void
	moveServerOnTop(Server server) {
		if(server == null) return;
		
		int idx = getServerIndex(server);
		if(idx <= 0) return;
		
		removeServer(idx);
		servers.insertElementAt(server, 0);
		fireChangeEvent();
	}
	
	/**
	 * Moves the specified server one position up in the server list.
	 * This method does nothing if <code>server</code> is <code>null</code>,
	 * the server list does not contain the specified server,
	 * or if the server is already on the top.
	 * @param server The server to move up.
	 */
	public void
	moveServerUp(Server server) {
		if(server == null) return;
		
		int idx = getServerIndex(server);
		if(idx <= 0) return;
		
		removeServer(idx);
		servers.insertElementAt(server, idx - 1);
		fireChangeEvent();
	}
	
	/**
	 * Moves the specified server one position down in the server list.
	 * This method does nothing if <code>server</code> is <code>null</code>,
	 * the server list does not contain the specified server,
	 * or if the server is already at the bottom.
	 * @param server The server to move down.
	 */
	public void
	moveServerDown(Server server) {
		if(server == null) return;
		
		int idx = getServerIndex(server);
		if(idx < 0 || idx == getServerCount() - 1) return;
		removeServer(idx);
		servers.insertElementAt(server, idx + 1);
		fireChangeEvent();
	}
	
	/**
	 * Moves the specified server at the bottom of the server list.
	 * This method does nothing if <code>server</code> is <code>null</code>,
	 * the server list does not contain the specified server,
	 * or if the server is already at the bottom.
	 * @param server The server to move at bottom.
	 */
	public void
	moveServerAtBottom(Server server) {
		if(server == null) return;
		
		int idx = getServerIndex(server);
		if(idx < 0 || idx == getServerCount() - 1) return;
		
		removeServer(idx);
		servers.insertElementAt(server, getServerCount());
		fireChangeEvent();
	}
	
	/** Notifies listeners that the server list has changed. */
	protected void
	fireChangeEvent() {
		GenericEvent e = new GenericEvent(this);
		for(GenericListener l : listeners) l.jobDone(e);
	}
	
	/**
	 * Reads and loads the content provided by <code>node</code> to this server list.
	 * @param node The node providing the content of this server list.
	 * @throws IllegalArgumentException If an error occurs while
	 * reading the content of this server list.
	 */
	public void
	readObject(Node node) {
		if(
			node.getNodeType() != Node.ELEMENT_NODE ||
			!(node.getNodeName().equals("servers"))
		) {
			throw new IllegalArgumentException("Not a server list node!");
		}
		
		NodeList nl = node.getChildNodes();
		
		for(int i = 0; i < nl.getLength(); i++) {
			node = nl.item(i);
			if(node.getNodeType() != Node.ELEMENT_NODE) continue;
			
			Server s = new Server();
			s.readObject(node);
			addServer(s);
		}
	}
	
	/**
	 * Writes the content of this server list to the
	 * specified node of document <code>doc</code>.
	 * @param doc The document containing <code>node</code>.
	 * @param node Specifies the node where the content of this server
	 * list should be written.
	 */
	public void
	writeObject(Document doc, Node node) {
		Element el = doc.createElement("servers");
		node.appendChild(el);
				
		node = el;
		
		for(int i = 0; i < getServerCount(); i++) {
			getServer(i).writeObject(doc, node);
		}
	}
}
