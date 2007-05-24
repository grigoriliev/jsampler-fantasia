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

package org.jsampler.view;

import java.util.Vector;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jsampler.CC;

import org.linuxsampler.lscp.DbDirectoryInfo;
import org.linuxsampler.lscp.DbInstrumentInfo;


/**
 *
 * @author Grigor Iliev
 */
public class DbClipboard {
	public static enum Operation {
		CUT, COPY, NONE
	}
	
	private Operation operation = Operation.NONE;
	private DbDirectoryInfo[] directories = new DbDirectoryInfo[0];
	private DbInstrumentInfo[] instruments = new DbInstrumentInfo[0];
	
	private final Vector<ChangeListener> listeners = new Vector<ChangeListener>();
	
	/** Creates a new instance of <code>DbClipboard</code> */
	public
	DbClipboard() {
		
	}
	
	/**
	 * Registers the specified listener to be notified
	 * when the content of the clipboard is changed.
	 */
	public void
	addChangeListener(ChangeListener l) {
		listeners.add(l);
	}
	
	/**
	 * Removes the specified listener.
	 */
	public void
	removeChangeListener(ChangeListener l) {
		listeners.remove(l);
	}
	
	public Operation
	getOperation() { return operation; }
	
	public void
	setOperation(Operation operation) { this.operation = operation; }
	
	/**
	 * Gets the directories placed in the clipboard.
	 */
	public DbDirectoryInfo[]
	getDirectories() {
		boolean b = false;
		InstrumentsDbTreeModel m = CC.getInstrumentsDbTreeModel();
		for(int i = 0; i < directories.length; i++) {
			if(m.getNodeByPath(directories[i].getDirectoryPath()) == null) {
				b = true;
				directories[i] = null;
			}
		}
		
		if(!b) return directories;
		
		Vector<DbDirectoryInfo> v = new Vector<DbDirectoryInfo>();
		
		for(DbDirectoryInfo dir : directories) {
			if(dir != null) v.add(dir);
		}
		
		instruments = v.toArray(new DbInstrumentInfo[v.size()]);
		return directories;
	}
	
	/**
	 * Sets the directories placed in the clipboard.
	 */
	public void
	setDirectories(DbDirectoryInfo[] directories) {
		this.directories = directories;
		fireChangeEvent();
	}
	
	/**
	 * Gets the instruments in the clipboard.
	 */
	public DbInstrumentInfo[]
	getInstruments() {
		boolean b = false;
		InstrumentsDbTreeModel m = CC.getInstrumentsDbTreeModel();
		for(int i = 0; i < instruments.length; i++) {
			DbDirectoryTreeNode n = m.getNodeByPath(instruments[i].getDirectoryPath());
			if(n == null) {
				b = true;
				instruments[i] = null;
			} else if(n.getInstrument(instruments[i].getName()) == null) {
				b = true;
				instruments[i] = null;
			}
		}
		
		if(!b) return instruments;
		
		Vector<DbInstrumentInfo> v = new Vector<DbInstrumentInfo>();
		
		for(DbInstrumentInfo instr : instruments) {
			if(instr != null) v.add(instr);
		}
		
		instruments = v.toArray(new DbInstrumentInfo[v.size()]);
		return instruments;
	}
	
	/**
	 * Sets the instruments in the clipboard.
	 */
	public void
	setInstruments(DbInstrumentInfo[] instruments) {
		this.instruments = instruments;
		fireChangeEvent();
	}
	
	private void
	fireChangeEvent() {
		ChangeEvent e = new ChangeEvent(this);
		for(ChangeListener l : listeners) l.stateChanged(e);
	}
}
