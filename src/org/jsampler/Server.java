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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 *
 * @author Grigor Iliev
 */
public class Server extends Resource {
	private String address = "127.0.0.1";
	private int port = 8888;
	
	/**
	 * Creates a new instance of <code>Server</code>
	 */
	public
	Server() {
		
	}
	
	/**
	 * Gets the address of the server.
	 * @return The address of the server.
	 */
	public String
	getAddress() { return address; }
	
	/**
	 * Sets the address of the server.
	 * @param name The new address of the server.
	 */
	public void
	setAddress(String address) {
		this.address = address;
		fireChangeEvent();
	}
	
	/**
	 * Gets the port to which to connect.
	 * @return The port to which to connect.
	 */
	public int
	getPort() { return port; }
	
	/**
	 * Sets the port to which to connect.
	 * @param name The new port to which to connect.
	 */
	public void
	setPort(int port) {
		this.port = port;
		fireChangeEvent();
	}
	
	/** Determines whether this server is on the local host. */
	public boolean
	isLocal() {
		if(getAddress() == null) return false;
		if("127.0.0.1".equals(getAddress())) return true;
		if("localhost".equalsIgnoreCase(getAddress())) return true;
		return false;
	}
	
	/**
	 * Reads and sets the the server information provided by <code>node</code>.
	 * @param node The node providing the server information.
	 * @throws IllegalArgumentException If an error occurs while
	 * reading the server information.
	 */
	public void
	readObject(Node node) {
		if(
			node.getNodeType() != Node.ELEMENT_NODE ||
			!(node.getNodeName().equals("server"))
		) {
			throw new IllegalArgumentException("Not a server node!");
		}
		
		NamedNodeMap nnm = node.getAttributes();
		Node n = nnm.getNamedItem("name");
		if(n == null) {
			throw new IllegalArgumentException("The server name is undefined!");
		}
		DOMUtils.validateTextContent(n);
		setName(n.getFirstChild().getNodeValue());
		
		
		String s = null;
		NodeList nl = node.getChildNodes();
		
		for(int i = 0; i < nl.getLength(); i++) {
			node = nl.item(i);
			if(node.getNodeType() != Node.ELEMENT_NODE) continue;
			
			s = node.getNodeName();
			if(s.equals("description")) {
				if(node.hasChildNodes()) {
					DOMUtils.validateTextContent(node);
					setDescription(node.getFirstChild().getNodeValue());
				}
			} else if(s.equals("address")) {
				if(node.hasChildNodes()) {
					DOMUtils.validateTextContent(node);
					setAddress(node.getFirstChild().getNodeValue());
				}
			} else if(s.equals("port")) {
				if(node.hasChildNodes()) {
					DOMUtils.validateTextContent(node);
					String port = node.getFirstChild().getNodeValue();
					int p;
					try { p = Integer.parseInt(port); }
					catch(Exception e) {
						throw new IllegalArgumentException("Invalid port!");
					}
					
					setPort(p);
				}
			} else {	// Unknown content
				CC.getLogger().info ("Unknown field: " + s);
			}
		}
	}
	
	/**
	 * Writes the content of this server info to the
	 * specified node of document <code>doc</code>.
	 * @param doc The document containing <code>node</code>.
	 * @param node Specifies the node where the content of this server info
	 * should be written.
	 */
	public void
	writeObject(Document doc, Node node) {
		Element el = doc.createElement("server");
		el.setAttribute("name", getName());
		node.appendChild(el);
				
		node = el;
		
		el = doc.createElement("description");
		el.appendChild(doc.createTextNode(getDescription()));
		node.appendChild(el);
		
		el = doc.createElement("address");
		el.appendChild(doc.createTextNode(getAddress()));
		node.appendChild(el);
		
		el = doc.createElement("port");
		el.appendChild(doc.createTextNode(String.valueOf(getPort())));
		node.appendChild(el);
	}
	
	@Override
	public String
	toString() { return getName(); }
}
