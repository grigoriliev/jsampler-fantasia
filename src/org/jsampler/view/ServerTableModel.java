/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2008 Grigor Iliev <grigor@grigoriliev.com>
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

package org.jsampler.view;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import javax.swing.table.AbstractTableModel;

import org.jsampler.Server;
import org.jsampler.ServerList;

/**
 *
 * @author Grigor Iliev
 */
public class ServerTableModel extends AbstractTableModel {
	private ServerList serverList;
	
	/**
	 * Creates a new instance of <code>ServerTableModel</code>.
	 * @param serverList The server list,
	 * which this table model should represent.
	 * @throws IllegalArgumentException If <code>serverList</code> is <code>null</code>.
	 */
	public
	ServerTableModel(ServerList serverList) {
		setServerList(serverList);
	}
	
	/**
	 * Gets the server list, represented by this table model.
	 */
	public ServerList
	getServerList() { return serverList; }
	
	/**
	 * Sets the server list, represented by this table model.
	 * @param serverList The new server list,
	 * represented by this table model.
	 * @throws IllegalArgumentException If <code>serverList</code> is <code>null</code>.
	 */
	public void
	setServerList(ServerList serverList) {
		if(serverList == null)
			throw new IllegalArgumentException("serverList should be non-null!");
		
		if(getServerList() != null)
			getServerList().removeChangeListener(getHandler());
		
		this.serverList = serverList;
		serverList.addChangeListener(getHandler());
		
		fireTableStructureChanged();
		fireTableDataChanged();
	}
	
	/**
	 * Gets the number of columns in the model.
	 * @return The number of columns in the model.
	 */
	public int
	getColumnCount() { return 1; }
	
	/**
	 * Gets the name of the column at <code>columnIndex</code>.
	 * @return The name of the column at <code>columnIndex</code>.
	 */
	public String
	getColumnName(int col) { return " "; }
	
	/**
	 * Gets the number of rows in the model.
	 * @return The number of rows in the model.
	 */
	public int
	getRowCount() { return serverList.getServerCount(); }
	
	/**
	 * Gets the value for the cell at <code>columnIndex</code> and
	 * <code>rowIndex</code>.
	 * @param row The row whose value is to be queried.
	 * @param col The column whose value is to be queried.
	 * @return The value for the cell at <code>columnIndex</code> and
	 * <code>rowIndex</code>.
	 */
	public Object
	getValueAt(int row, int col) {
		return serverList.getServer(row);
	}
	
	public Server
	getServerAt(int index) { return serverList.getServer(index); }
	
	private final Handler eventHandler = new Handler();
	
	private Handler
	getHandler() { return eventHandler; }
	
	private class Handler implements ChangeListener {
		public void
		stateChanged(ChangeEvent e) {
			fireTableDataChanged();
		}
	}
}
