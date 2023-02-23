/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2011 Grigor Iliev <grigor@grigoriliev.com>
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

import org.jsampler.event.OrchestraAdapter;
import org.jsampler.event.OrchestraEvent;
import org.jsampler.event.OrchestraListener;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * This class provides default implementation of the <code>OrchestraModel</code> interface.
 * @author Grigor Iliev
 */
public class DefaultOrchestraModel implements OrchestraModel {
	private String name = "";
	private String description = "";
	
	private final Vector<OrchestraInstrument> instruments = new Vector<OrchestraInstrument>();
	
	private final Vector<OrchestraListener> listeners = new Vector<OrchestraListener>();
	
	/** Creates a new instance of <code>DefaultOrchestraModel</code>. */
	public
	DefaultOrchestraModel() {
		addOrchestraListener(getHandler());
	}
	
	/**
	 * Registers the specified listener for receiving event messages.
	 * @param l The <code>OrchestraListener</code> to register.
	 */
	@Override
	public void
	addOrchestraListener(OrchestraListener l) { listeners.add(l); }
	
	/**
	 * Removes the specified listener.
	 * @param l The <code>OrchestraListener</code> to remove.
	 */
	@Override
	public void
	removeOrchestraListener(OrchestraListener l) { listeners.remove(l); }
	
	/**
	 * Gets the name of this orchestra.
	 * @return The name of this orchestra.
	 */
	@Override
	public String
	getName() { return name; }
	
	/**
	 * Sets the name of this orchestra.
	 * @param name The new name of this orchestra.
	 */
	@Override
	public void
	setName(String name) {
		this.name = name;
		fireNameChanged();
	}
	
	/**
	 * Returns the name of this orchestra.
	 * @return The name of this orchestra.
	 */
	@Override
	public String
	toString() { return getName(); }
	
	/**
	 * Gets a brief description about this orchestra.
	 * @return A brief description about this orchestra.
	 */
	@Override
	public String
	getDescription() { return description; }
	
	/**
	 * Sets a description about this orchestra.
	 * @param desc A brief description about this orchestra.
	 */
	@Override
	public void
	setDescription(String desc) {
		description = desc;
		fireDescriptionChanged();
	}
	
	/**
	 * Gets the current number of instruments in this orchestra.
	 * @return The current number of instruments in this orchestra.
	 */
	@Override
	public int
	getInstrumentCount() { return instruments.size(); }
	
	/**
	 * Gets the instrument at the specified position.
	 * @param idx The index of the instrument to be returned.
	 * @return The instrument at the specified position.
	 */
	@Override
	public OrchestraInstrument
	getInstrument(int idx) { return instruments.get(idx); }
	
	/**
	 * Adds the specified instrument to this orchestra.
	 * @param instr The instrument to be added.
	 * @throws IllegalArgumentException If <code>instr</code> is <code>null</code>.
	 */
	@Override
	public void
	addInstrument(OrchestraInstrument instr) {
		insertInstrument(instr, getInstrumentCount());
	}
	
	/**
	 * Inserts the specified instrument at the specified position.
	 * @param instr The instrument to be inserted.
	 * @param idx The position of the instrument.
	 * @throws IllegalArgumentException If <code>instr</code> is <code>null</code>.
	 * @throws ArrayIndexOutOfBoundsException If the specified index is invalid.
	 */
	@Override
	public void
	insertInstrument(OrchestraInstrument instr, int idx) {
		if(instr == null) throw new IllegalArgumentException("instr should be non-null!");
		instruments.insertElementAt(instr, idx);
		fireInstrumentAdded(instr);
	}
	
	/**
	 * Removes the specified instrument from this orchestra.
	 * @param idx The index of the instrument to remove.
	 */
	@Override
	public void
	removeInstrument(int idx) {
		OrchestraInstrument instr = instruments.get(idx);
		instruments.removeElementAt(idx);
		fireInstrumentRemoved(instr);
	}
	
	/**
	 * Removes the specified instrument from this orchestra.
	 * @param instr The instrument to remove.
	 * @return <code>true</code> if the specified instrument was in this orchestra,
	 * <code>false</code> otherwise.
	 */
	@Override
	public boolean
	removeInstrument(OrchestraInstrument instr) {
		boolean b = instruments.removeElement(instr);
		if(b) fireInstrumentRemoved(instr);
		return b;
	}
	
	/**
	 * Gets the position of the specified instrument in this orchestra.
	 * @param instr The instrument whose index should be returned.
	 * @return The position of the specified instrument in this orchestra,
	 * and -1 if <code>instr</code> is <code>null</code> or
	 * the orchestra does not contain the specified instrument.
	 */
	@Override
	public int
	getInstrumentIndex(OrchestraInstrument instr) {
		if(instr == null) return -1;
		
		for(int i = 0; i < getInstrumentCount(); i++) {
			if(getInstrument(i) == instr) return i;
		}
		
		return -1;
	}
	
	/**
	 * Moves the specified instrument one the top of the instrument list.
	 * This method does nothing if <code>instr</code> is <code>null</code>,
	 * the orchestra does not contain the specified instrument,
	 * or if the instrument is already on the top.
	 * @param instr The instrument to move on top.
	 */
	@Override
	public void
	moveInstrumentOnTop(OrchestraInstrument instr) {
		if(instr == null) return;
		
		int idx = getInstrumentIndex(instr);
		if(idx <= 0) return;
		
		removeInstrument(idx);
		insertInstrument(instr, 0);
	}
	
	/**
	 * Moves the specified instrument one position up in the instrument list.
	 * This method does nothing if <code>instr</code> is <code>null</code>,
	 * the orchestra does not contain the specified instrument,
	 * or if the instrument is already on the top.
	 * @param instr The instrument to move up.
	 */
	@Override
	public void
	moveInstrumentUp(OrchestraInstrument instr) {
		if(instr == null) return;
		
		int idx = getInstrumentIndex(instr);
		if(idx <= 0) return;
		
		removeInstrument(idx);
		insertInstrument(instr, idx - 1);
		
	}
	
	/**
	 * Moves the specified instrument one position down in the instrument list.
	 * This method does nothing if <code>instr</code> is <code>null</code>,
	 * the orchestra does not contain the specified instrument,
	 * or if the instrument is already at the bottom.
	 * @param instr The instrument to move down.
	 */
	@Override
	public void
	moveInstrumentDown(OrchestraInstrument instr) {
		if(instr == null) return;
		
		int idx = getInstrumentIndex(instr);
		if(idx < 0 || idx == getInstrumentCount() - 1) return;
		removeInstrument(idx);
		insertInstrument(instr, idx + 1);
	}
	
	/**
	 * Moves the specified instrument at the bottom of the instrument list.
	 * This method does nothing if <code>instr</code> is <code>null</code>,
	 * the orchestra does not contain the specified instrument,
	 * or if the instrument is already at the bottom.
	 * @param instr The instrument to move at bottom.
	 */
	@Override
	public void
	moveInstrumentAtBottom(OrchestraInstrument instr) {
		if(instr == null) return;
		
		int idx = getInstrumentIndex(instr);
		if(idx < 0 || idx == getInstrumentCount() - 1) return;
		
		removeInstrument(idx);
		insertInstrument(instr, getInstrumentCount());
	}
	
	/**
	 * Reads and sets the content of this orchestra provided by <code>node</code>.
	 * @param node The node providing the content of this orchestra.
	 * @throws IllegalArgumentException If an error occurs while
	 * reading the content of this orchestra.
	 */
	@Override
	public void
	readObject(Node node) {
		if(
			node.getNodeType() != Node.ELEMENT_NODE ||
			!(node.getNodeName().equals("orchestra"))
		) {
			throw new IllegalArgumentException("Not an orchestra node!");
		}
		
		NamedNodeMap nnm = node.getAttributes();
		Node n = nnm.getNamedItem("name");
		if(n == null) {
			throw new IllegalArgumentException("The orchestra name is undefined!");
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
			} else if(s.equals("instrument")) {
				OrchestraInstrument instr = new OrchestraInstrument();
				instr.readObject(node);
				addInstrument(instr);
			} else {	// Unknown content
				CC.getLogger().info ("Unknown field: " + s);
			}
		}
	}
	
	/**
	 * Writes the content of this orchestra to the
	 * specified node of document <code>doc</code>.
	 * @param doc The document containing <code>node</code>.
	 * @param node Specifies the node where the content of this orchestra
	 * should be written.
	 */
	@Override
	public void
	writeObject(Document doc, Node node) {
		Element el = doc.createElement("orchestra");
		el.setAttribute("name", getName());
		node.appendChild(el);
				
		node = el;
		
		el = doc.createElement("description");
		el.appendChild(doc.createTextNode(getDescription()));
		node.appendChild(el);
		
		for(int i = 0; i < getInstrumentCount(); i++) {
			getInstrument(i).writeObject(doc, node);
		}
	}
	
	/** Notifies listeners that the name of the orchestra has changed. */
	private void
	fireNameChanged() {
		OrchestraEvent e = new OrchestraEvent(this);
		for(OrchestraListener l : listeners) l.nameChanged(e);
	}
	
	/** Notifies listeners that the orchestra's description has changed. */
	private void
	fireDescriptionChanged() {
		OrchestraEvent e = new OrchestraEvent(this);
		for(OrchestraListener l : listeners) l.descriptionChanged(e);
	}
	
	/** Notifies listeners that an instrument has been added to this orchestra. */
	private void
	fireInstrumentAdded(OrchestraInstrument instr) {
		OrchestraEvent e = new OrchestraEvent(this, instr);
		for(OrchestraListener l : listeners) l.instrumentAdded(e);
	}
	
	/** Notifies listeners that an instrument has been removed from this orchestra. */
	private void
	fireInstrumentRemoved(OrchestraInstrument instr) {
		OrchestraEvent e = new OrchestraEvent(this, instr);
		for(OrchestraListener l : listeners) l.instrumentRemoved(e);
	}
	
	/**
	 * Notifies listeners that the settings of the specified instrument has changed.
	 * @param instr The instrument whose settings has been changed.
	 */
	private void
	fireInstrumentChanged(OrchestraInstrument instr) {
		OrchestraEvent e = new OrchestraEvent(this, instr);
		for(OrchestraListener l : listeners) l.instrumentChanged(e);
	}
	
	private final Handler eventHandler = new Handler();
	
	private Handler
	getHandler() { return eventHandler; }
	
	private class Handler extends OrchestraAdapter implements GenericListener {
		/** Invoked when the settings of an instrument are changed. */
		public void
		jobDone(GenericEvent e) {
			fireInstrumentChanged((OrchestraInstrument)e.getSource());
		}
		
		/** Invoked when an instrument is added to the orchestra. */
		public void
		instrumentAdded(OrchestraEvent e) {
			e.getInstrument().addChangeListener(getHandler());
		
		}
	
		/** Invoked when an instrument is removed from the orchestra. */
		public void
		instrumentRemoved(OrchestraEvent e) {
			e.getInstrument().removeChangeListener(getHandler());
		}
	}
}
