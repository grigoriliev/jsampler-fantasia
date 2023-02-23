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

import org.jsampler.event.ListListener;

import org.w3c.dom.Document;
import org.w3c.dom.Node;


/**
 * A data model representing list of orchestras.
 * @author Grigor Iliev
 */
public interface OrchestraListModel {
	/**
	 * Registers the specified listener for receiving event messages.
	 * @param l The <code>OrchestraListListener</code> to register.
	 */
	public void addOrchestraListListener(ListListener<OrchestraModel> l);
	
	/**
	 * Removes the specified listener.
	 * @param l The <code>OrchestraListListener</code> to remove.
	 */
	public void removeOrchestraListListener(ListListener<OrchestraModel> l);
	
	/**
	 * Gets the current number of orchestras in the list.
	 * @return The current number of orchestras in the list.
	 */
	public int getOrchestraCount();
	
	/**
	 * Gets the orchestra at the specified position.
	 * @param idx The index of the orchestra to be returned.
	 * @return The orchestra at the specified position.
	 */
	public OrchestraModel getOrchestra(int idx);
	
	/**
	 * Adds the specified orchestra to the list.
	 * @param orchestra The model of the orchestra to be added.
	 * @throws IllegalArgumentException If <code>orchestra</code> is <code>null</code>.
	 */
	public void addOrchestra(OrchestraModel orchestra);
	
	/**
	 * Inserts the specified orchestra at the specified position.
	 * @param orchestra The orchestra to be inserted.
	 * @param idx The position of the orchestra.
	 * @throws IllegalArgumentException If <code>orchestra</code> is <code>null</code>.
	 * @throws ArrayIndexOutOfBoundsException If the specified index is invalid.
	 */
	public void insertOrchestra(OrchestraModel orchestra, int idx);
	
	/**
	 * Removes the specified orchestra from the list.
	 * @param idx The index of the orchestra to remove.
	 */
	public void removeOrchestra(int idx);
	
	/**
	 * Removes the specified orchestra from the list.
	 * @param orchestraModel The model of the orchestra to remove.
	 * @return <code>true</code> if the specified orchestra was in the list,
	 * <code>false</code> otherwise.
	 */
	public boolean removeOrchestra(OrchestraModel orchestraModel);
	
	/** Removes all orchestras from the list. */
	public void removeAllOrchestras();
	
	/**
	 * Gets the position of the specified orchestra in this orchestra list.
	 * @param orchestra The orchestra whose index should be returned.
	 * @return The position of the specified orchestra in this orchestra list,
	 * and -1 if <code>orchestra</code> is <code>null</code> or
	 * the orchestra list does not contain the specified orchestra.
	 */
	public int getOrchestraIndex(OrchestraModel orchestra);
	
	/**
	 * Moves the specified orchestra one the top of the orchestra list.
	 * This method does nothing if <code>orchestra</code> is <code>null</code>,
	 * the orchestra list does not contain the specified orchestra,
	 * or if the orchestra is already on the top.
	 * @param orchestra The orchestra to move on top.
	 */
	public void moveOrchestraOnTop(OrchestraModel orchestra);
	
	/**
	 * Moves the specified orchestra one position up in the orchestra list.
	 * This method does nothing if <code>orchestra</code> is <code>null</code>,
	 * the orchestra list does not contain the specified orchestra,
	 * or if the orchestra is already on the top.
	 * @param orchestra The orchestra to move up.
	 */
	public void moveOrchestraUp(OrchestraModel orchestra);
	
	/**
	 * Moves the specified orchestra one position down in the orchestra list.
	 * This method does nothing if <code>orchestra</code> is <code>null</code>,
	 * the orchestra list does not contain the specified orchestra,
	 * or if the orchestra is already at the bottom.
	 * @param orchestra The orchestra to move down.
	 */
	public void moveOrchestraDown(OrchestraModel orchestra);
	
	/**
	 * Moves the specified orchestra at the bottom of the orchestra list.
	 * This method does nothing if <code>orchestra</code> is <code>null</code>,
	 * the orchestra list does not contain the specified orchestra,
	 * or if the orchestra is already at the bottom.
	 * @param orchestra The orchestra to move at bottom.
	 */
	public void moveOrchestraAtBottom(OrchestraModel orchestra);
	
	/**
	 * Reads and loads the content provided by <code>node</code> to this orchestra list.
	 * @param node The node providing the content of this orchestra list.
	 * @throws IllegalArgumentException If an error occurs while
	 * reading the content of this orchestra list.
	 */
	public void readObject(Node node);
	
	/**
	 * Writes the content of this orchestra list to the
	 * specified node of document <code>doc</code>.
	 * @param doc The document containing <code>node</code>.
	 * @param node Specifies the node where the content of this orchestra
	 * list should be written.
	 */
	public void writeObject(Document doc, Node node);
}
