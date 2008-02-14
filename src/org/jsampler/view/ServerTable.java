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

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;

import org.jsampler.ServerList;
import org.jsampler.Server;

import static javax.swing.KeyStroke.*;

/**
 *
 * @author Grigor Iliev
 */
public class ServerTable extends JTable {
	
	/** Creates a new instance of <code>ServerTable</code> */
	public
	ServerTable() {
		this(new ServerTableModel(new ServerList()));
	}
	
	/**
	 * Creates a new instance of <code>ServerTable</code> using the specified data model.
	 * @param dataModel The data model to be represented by this table.
	 */
	public
	ServerTable(ServerTableModel dataModel) {
		super(dataModel);
		
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		installKeyboardListeners();
	}
	
	private void
	installKeyboardListeners() {
		KeyStroke k = getKeyStroke(KeyEvent.VK_UP, KeyEvent.ALT_MASK | KeyEvent.SHIFT_MASK);
		getInputMap(JComponent.WHEN_FOCUSED).put(k, Actions.MOVE_ON_TOP);
		getActionMap().put(Actions.MOVE_ON_TOP, new Actions(Actions.MOVE_ON_TOP));
		
		k = getKeyStroke(KeyEvent.VK_UP, KeyEvent.ALT_MASK);
		getInputMap(JComponent.WHEN_FOCUSED).put(k, Actions.MOVE_UP);
		getActionMap().put(Actions.MOVE_UP, new Actions(Actions.MOVE_UP));
		
		k = getKeyStroke(KeyEvent.VK_DOWN, KeyEvent.ALT_MASK);
		getInputMap(JComponent.WHEN_FOCUSED).put(k, Actions.MOVE_DOWN);
		getActionMap().put(Actions.MOVE_DOWN, new Actions(Actions.MOVE_DOWN));
		
		k = getKeyStroke(KeyEvent.VK_DOWN, KeyEvent.ALT_MASK | KeyEvent.SHIFT_MASK);
		getInputMap(JComponent.WHEN_FOCUSED).put(k, Actions.MOVE_AT_BOTTOM);
		getActionMap().put(Actions.MOVE_AT_BOTTOM, new Actions(Actions.MOVE_AT_BOTTOM));
	}
	
	/**
	 * Returns the <code>ServerTableModel</code> that
	 * provides the data displayed by this <code>ServerTable</code>.
	 */
	public ServerTableModel
	getModel() { return (ServerTableModel) super.getModel(); }
	
	/**
	 * Returns the selected server, or <code>null</code> if nothing is selected.
	 */
	public Server
	getSelectedServer() {
		int i = getSelectedRow();
		if(i == -1) return null;
		return getModel().getServerAt(i);
	}
	
	/** Selects the specified server. */
	public void
	setSelectedServer(Server server) {
		int i = getModel().getServerList().getServerIndex(server);
		if(i == -1) return;
		getSelectionModel().setSelectionInterval(i, i);
	}
	
	/**
	 * Moves the currently selected server one position up.
	 * This method does nothing if the there is no server selected or
	 * if the selected server is already on the top.
	 */
	public void
	moveSelectedServerUp() {
		int i = getSelectedRow();
		if(i < 1) return;
		
		Server s = getSelectedServer();
		getModel().getServerList().moveServerUp(s);
		setSelectedServer(s);
	}
	
	/**
	 * Moves the currently selected server one position down.
	 * This method does nothing if the there is no server selected or
	 * if the selected server is already at the bottom.
	 */
	public void
	moveSelectedServerDown() {
		int i = getSelectedRow();
		if(i == -1 || i == getModel().getRowCount() - 1) return;
		
		Server s = getSelectedServer();
		getModel().getServerList().moveServerDown(s);
		setSelectedServer(s);
	}
	
	/**
	 * Removes the currently selected server.
	 */
	public void
	removeSelectedServer() {
		Server s = getSelectedServer();
		if(s == null) return;
		
		int i = getSelectedRow();
		getModel().getServerList().removeServer(s);
		
		if(getRowCount() > i) {
			getSelectionModel().setSelectionInterval(i, i);
		} else {
			i = getRowCount() - 1;
			if(i >= 0) getSelectionModel().setSelectionInterval(i, i);
		}
	}
	
	private class Actions extends AbstractAction {
		private static final String MOVE_ON_TOP = "moveServerOnTop";
		private static final String MOVE_UP = "moveServerUp";
		private static final String MOVE_DOWN = "moveServerDown";
		private static final String MOVE_AT_BOTTOM = "moveServerAtBottom";
		
		Actions(String name) { super(name); }
		
		public void
		actionPerformed(ActionEvent e) {
			String key = getValue(Action.NAME).toString();
			
			if(key == MOVE_ON_TOP) {
				Server server = getSelectedServer();
				getModel().getServerList().moveServerOnTop(server);
				setSelectedServer(server);
			} else if(key == MOVE_UP) {
				moveSelectedServerUp();
			} else if(key == MOVE_DOWN) {
				moveSelectedServerDown();
			} else if(key == MOVE_AT_BOTTOM) {
				Server server = getSelectedServer();
				getModel().getServerList().moveServerAtBottom(server);
				setSelectedServer(server);
			}
		}
	}
}
