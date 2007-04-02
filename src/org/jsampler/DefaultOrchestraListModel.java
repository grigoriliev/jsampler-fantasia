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

package org.jsampler;

import java.util.Vector;

import org.jsampler.event.ListEvent;
import org.jsampler.event.ListListener;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * This class provides default implementation of the <code>OrchestraListModel</code> interface.
 * @author Grigor Iliev
 */
public class DefaultOrchestraListModel implements OrchestraListModel {
	private final Vector<OrchestraModel> orchestras = new Vector<OrchestraModel>();
	private final Vector<ListListener<OrchestraModel>> listeners =
		new Vector<ListListener<OrchestraModel>>();
	
	
	/** Creates a new instance of <code>DefaultOrchestraListModel</code>. */
	public
	DefaultOrchestraListModel() {
		
	}
	
	/**
	 * Registers the specified listener for receiving event messages.
	 * @param l The <code>OrchestraListListener</code> to register.
	 */
	public void
	addOrchestraListListener(ListListener<OrchestraModel> l) { listeners.add(l); }
	
	/**
	 * Removes the specified listener.
	 * @param l The <code>OrchestraListListener</code> to remove.
	 */
	public void
	removeOrchestraListListener(ListListener<OrchestraModel> l) { listeners.remove(l); }
	
	/**
	 * Gets the current number of orchestras in the list.
	 * @return The current number of orchestras in the list.
	 */
	public int
	getOrchestraCount() { return orchestras.size(); }
	
	/**
	 * Gets the orchestra at the specified position.
	 * @param idx The index of the orchestra to be returned.
	 * @return The orchestra at the specified position.
	 */
	public OrchestraModel
	getOrchestra(int idx) { return orchestras.get(idx); }
	
	/**
	 * Adds the specified orchestra to the list.
	 * @param orchestra The model of the orchestra to be added.
	 * @throws IllegalArgumentException If <code>orchestra</code> is <code>null</code>.
	 */
	public void
	addOrchestra(OrchestraModel orchestra) {
		insertOrchestra(orchestra, getOrchestraCount());
	}
	
	/**
	 * Inserts the specified orchestra at the specified position.
	 * @param orchestra The orchestra to be inserted.
	 * @param idx The position of the orchestra.
	 * @throws IllegalArgumentException If <code>orchestra</code> is <code>null</code>.
	 * @throws ArrayIndexOutOfBoundsException If the specified index is invalid.
	 */
	public void
	insertOrchestra(OrchestraModel orchestra, int idx) {
		if(orchestra == null)
			throw new IllegalArgumentException("orchestra should be non-null!");
		
		orchestras.insertElementAt(orchestra, idx);
		fireOrchestraAdded(orchestra);
	}
	
	/**
	 * Removes the specified orchestra from the list.
	 * @param idx The index of the orchestra to remove.
	 */
	public void
	removeOrchestra(int idx) {
		OrchestraModel orchestraModel = orchestras.get(idx);
		orchestras.removeElementAt(idx);
		fireOrchestraRemoved(orchestraModel);
	}
	
	/**
	 * Removes the specified orchestra from the list.
	 * @param orchestraModel The model of the orchestra to remove.
	 * @return <code>true</code> if the specified orchestra was in the list,
	 * <code>false</code> otherwise.
	 */
	public boolean
	removeOrchestra(OrchestraModel orchestraModel) {
		boolean b = orchestras.removeElement(orchestraModel);
		if(b) fireOrchestraRemoved(orchestraModel);
		return b;
	}
	
	/** Removes all orchestras from the list. */
	public void
	removeAllOrchestras() {
		for(int i = 0; i < getOrchestraCount(); i++) removeOrchestra(i);
	}
	
	/**
	 * Gets the position of the specified orchestra in this orchestra list.
	 * @param orchestra The orchestra whose index should be returned.
	 * @return The position of the specified orchestra in this orchestra list,
	 * and -1 if <code>orchestra</code> is <code>null</code> or
	 * the orchestra list does not contain the specified orchestra.
	 */
	public int
	getOrchestraIndex(OrchestraModel orchestra) {
		if(orchestra == null) return -1;
		
		for(int i = 0; i < getOrchestraCount(); i++) {
			if(getOrchestra(i) == orchestra) return i;
		}
		
		return -1;
	}
	
	/**
	 * Moves the specified orchestra one the top of the orchestra list.
	 * This method does nothing if <code>orchestra</code> is <code>null</code>,
	 * the orchestra list does not contain the specified orchestra,
	 * or if the orchestra is already on the top.
	 * @param orchestra The orchestra to move on top.
	 */
	public void
	moveOrchestraOnTop(OrchestraModel orchestra) {
		if(orchestra == null) return;
		
		int idx = getOrchestraIndex(orchestra);
		if(idx <= 0) return;
		
		removeOrchestra(idx);
		insertOrchestra(orchestra, 0);
	}
	
	/**
	 * Moves the specified orchestra one position up in the orchestra list.
	 * This method does nothing if <code>orchestra</code> is <code>null</code>,
	 * the orchestra list does not contain the specified orchestra,
	 * or if the orchestra is already on the top.
	 * @param orchestra The orchestra to move up.
	 */
	public void
	moveOrchestraUp(OrchestraModel orchestra) {
		if(orchestra == null) return;
		
		int idx = getOrchestraIndex(orchestra);
		if(idx <= 0) return;
		
		removeOrchestra(idx);
		insertOrchestra(orchestra, idx - 1);
	}
	
	/**
	 * Moves the specified orchestra one position down in the orchestra list.
	 * This method does nothing if <code>orchestra</code> is <code>null</code>,
	 * the orchestra list does not contain the specified orchestra,
	 * or if the orchestra is already at the bottom.
	 * @param orchestra The orchestra to move down.
	 */
	public void
	moveOrchestraDown(OrchestraModel orchestra) {
		if(orchestra == null) return;
		
		int idx = getOrchestraIndex(orchestra);
		if(idx < 0 || idx == getOrchestraCount() - 1) return;
		removeOrchestra(idx);
		insertOrchestra(orchestra, idx + 1);
	}
	
	/**
	 * Moves the specified orchestra at the bottom of the orchestra list.
	 * This method does nothing if <code>orchestra</code> is <code>null</code>,
	 * the orchestra list does not contain the specified orchestra,
	 * or if the orchestra is already at the bottom.
	 * @param orchestra The orchestra to move at bottom.
	 */
	public void
	moveOrchestraAtBottom(OrchestraModel orchestra) {
		if(orchestra == null) return;
		
		int idx = getOrchestraIndex(orchestra);
		if(idx < 0 || idx == getOrchestraCount() - 1) return;
		
		removeOrchestra(idx);
		insertOrchestra(orchestra, getOrchestraCount());
	}
	
	/**
	 * Reads and loads the content provided by <code>node</code> to this orchestra list.
	 * @param node The node providing the content of this orchestra list.
	 * @throws IllegalArgumentException If an error occurs while
	 * reading the content of this orchestra list.
	 */
	public void
	readObject(Node node) {
		if(
			node.getNodeType() != Node.ELEMENT_NODE ||
			!(node.getNodeName().equals("orchestras"))
		) {
			throw new IllegalArgumentException("Not an orchestra list node!");
		}
		
		NodeList nl = node.getChildNodes();
		
		for(int i = 0; i < nl.getLength(); i++) {
			node = nl.item(i);
			if(node.getNodeType() != Node.ELEMENT_NODE) continue;
			
			OrchestraModel om = new DefaultOrchestraModel();
			om.readObject(node);
			addOrchestra(om);
		}
	}
	
	/**
	 * Writes the content of this orchestra list to the
	 * specified node of document <code>doc</code>.
	 * @param doc The document containing <code>node</code>.
	 * @param node Specifies the node where the content of this orchestra
	 * list should be written.
	 */
	public void
	writeObject(Document doc, Node node) {
		Element el = doc.createElement("orchestras");
		node.appendChild(el);
				
		node = el;
		
		for(int i = 0; i < getOrchestraCount(); i++) {
			getOrchestra(i).writeObject(doc, node);
		}
	}
	
	/** Notifies listeners that an orchestra has been added to the list. */
	private void
	fireOrchestraAdded(OrchestraModel orchestraModel) {
		ListEvent<OrchestraModel> e = new ListEvent<OrchestraModel>(this, orchestraModel);
		for(ListListener<OrchestraModel> l : listeners) l.entryAdded(e);
	}
	
	/** Notifies listeners that an orchestra has been removed from the list. */
	private void
	fireOrchestraRemoved(OrchestraModel orchestraModel) {
		ListEvent<OrchestraModel> e = new ListEvent<OrchestraModel>(this, orchestraModel);
		for(ListListener<OrchestraModel> l : listeners) l.entryRemoved(e);
	}
}
