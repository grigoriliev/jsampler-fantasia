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

import org.jsampler.event.OrchestraListener;

import org.w3c.dom.Document;
import org.w3c.dom.Node;


/**
 * A data model representing an orchestra.
 * @author Grigor Iliev
 */
public interface OrchestraModel {
	/**
	 * Registers the specified listener for receiving event messages.
	 * @param l The <code>OrchestraListener</code> to register.
	 */
	public void addOrchestraListener(OrchestraListener l);
	
	/**
	 * Removes the specified listener.
	 * @param l The <code>OrchestraListener</code> to remove.
	 */
	public void removeOrchestraListener(OrchestraListener l);
	
	/**
	 * Gets the name of this orchestra.
	 * @return The name of this orchestra.
	 */
	public String getName();
	
	/**
	 * Sets the name of this orchestra.
	 * @param name The new name of this orchestra.
	 */
	public void setName(String name);
	
	/**
	 * Gets a brief description about this orchestra.
	 * @return A brief description about this orchestra.
	 */
	public String getDescription();
	
	/**
	 * Sets a description about this orchestra.
	 * @param desc A brief description about this orchestra.
	 */
	public void setDescription(String desc);
	
	/**
	 * Gets the current number of instruments in this orchestra.
	 * @return The current number of instruments in this orchestra.
	 */
	public int getInstrumentCount();
	
	/**
	 * Gets the instrument at the specified position.
	 * @param idx The index of the instrument to be returned.
	 * @return The instrument at the specified position.
	 */
	public Instrument getInstrument(int idx);
	
	/**
	 * Adds the specified instrument to this orchestra.
	 * @param instr The instrument to be added.
	 */
	public void addInstrument(Instrument instr);
	
	/**
	 * Inserts the specified instrument at the specified position.
	 * @param instr The instrument to be inserted.
	 * @param idx The position of the instrument.
	 * @throws IllegalArgumentException If <code>instr</code> is <code>null</code>.
	 * @throws ArrayIndexOutOfBoundsException If the specified index is invalid.
	 */
	public void insertInstrument(Instrument instr, int idx);
	
	/**
	 * Removes the specified instrument from this orchestra.
	 * @param idx The index of the instrument to remove.
	 */
	public void removeInstrument(int idx);
	
	/**
	 * Removes the specified instrument from this orchestra.
	 * @param instr The instrument to remove.
	 * @return <code>true</code> if the specified instrument was in this orchestra,
	 * <code>false</code> otherwise.
	 */
	public boolean removeInstrument(Instrument instr);
	
	/**
	 * Gets the position of the specified instrument in this orchestra.
	 * @param instr The instrument whose index should be returned.
	 * @return The position of the specified instrument in this orchestra,
	 * and -1 if <code>instr</code> is <code>null</code> or
	 * the orchestra does not contain the specified instrument.
	 */
	public int getInstrumentIndex(Instrument instr);
	
	/**
	 * Moves the specified instrument one the top of the instrument list.
	 * This method does nothing if <code>instr</code> is <code>null</code>,
	 * the orchestra does not contain the specified instrument,
	 * or if the instrument is already on the top.
	 * @param instr The instrument to move on top.
	 */
	public void moveInstrumentOnTop(Instrument instr);
	
	/**
	 * Moves the specified instrument one position up in the instrument list.
	 * This method does nothing if <code>instr</code> is <code>null</code>,
	 * the orchestra does not contain the specified instrument,
	 * or if the instrument is already on the top.
	 * @param instr The instrument to move up.
	 */
	public void moveInstrumentUp(Instrument instr);
	
	/**
	 * Moves the specified instrument one position down in the instrument list.
	 * This method does nothing if <code>instr</code> is <code>null</code>,
	 * the orchestra does not contain the specified instrument,
	 * or if the instrument is already at the bottom.
	 * @param instr The instrument to move down.
	 */
	public void moveInstrumentDown(Instrument instr);
	
	/**
	 * Moves the specified instrument at the bottom of the instrument list.
	 * This method does nothing if <code>instr</code> is <code>null</code>,
	 * the orchestra does not contain the specified instrument,
	 * or if the instrument is already at the bottom.
	 * @param instr The instrument to move at bottom.
	 */
	public void moveInstrumentAtBottom(Instrument instr);
	
	/**
	 * Reads and sets the content of this orchestra provided by <code>node</code>.
	 * @param node The node providing the content of this orchestra.
	 * @throws IllegalArgumentException If an error occurs while
	 * reading the content of this orchestra.
	 */
	public void readObject(Node node);
	
	/**
	 * Writes the content of this orchestra to the
	 * specified node of document <code>doc</code>.
	 * @param doc The document containing <code>node</code>.
	 * @param node Specifies the node where the content of this orchestra
	 * should be written.
	 */
	public void writeObject(Document doc, Node node);
}
