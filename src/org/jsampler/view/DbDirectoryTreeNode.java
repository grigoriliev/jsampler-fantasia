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

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.tree.TreeNode;

import org.linuxsampler.lscp.DbDirectoryInfo;
import org.linuxsampler.lscp.DbInstrumentInfo;

import org.linuxsampler.lscp.event.InstrumentsDbEvent;
import org.linuxsampler.lscp.event.InstrumentsDbListener;

/**
 *
 * @author Grigor Iliev
 */
public class DbDirectoryTreeNode implements TreeNode {
	private DbDirectoryTreeNode parent = null;
	private DbDirectoryInfo info;
	private Vector<DbDirectoryTreeNode> dirs = new Vector<DbDirectoryTreeNode>();
	private Vector<DbInstrumentInfo> instrs = new Vector<DbInstrumentInfo>();
	private boolean connected = false;
	private boolean detachedNode = false;
	
	private final Vector<InstrumentsDbListener> listeners = new Vector<InstrumentsDbListener>();
	
	
	/** Creates a new instance of <code>DbDirectoryTreeNode</code>. */
	public
	DbDirectoryTreeNode(DbDirectoryInfo info) {
		this.info = info;
	}
	
	/**
	 * Registers the specified listener for receiving event messages.
	 * @param l The <code>InstrumentsDbListener</code> to register.
	 */
	public void
	addInstrumentsDbListener(InstrumentsDbListener l) {
		listeners.add(l);
	}
	
	/**
	 * Removes the specified listener.
	 * @param l The <code>InstrumentsDbListener</code> to remove.
	 */
	public void
	removeInstrumentsDbListener(InstrumentsDbListener l) {
		listeners.remove(l);
	}
	
	public DbDirectoryInfo
	getInfo() { return info; }
	
	public void
	setInfo(DbDirectoryInfo info) { this.info = info; }
	
	// Tree node model methods
	public DbDirectoryTreeNode
	getChildAt(int index) { return dirs.get(index); }
	
	public int
	getChildCount() { return dirs.size(); }
	
	public DbDirectoryTreeNode
	getParent() { return parent; }
	
	public int
	getIndex(TreeNode node) { return dirs.indexOf(node); }
	
	public boolean
	getAllowsChildren() { return true; }
	
	public boolean
	isLeaf() { return false; }
	
	public Enumeration
	children() { return dirs.elements(); }
	///////
	
	public void
	setParent(DbDirectoryTreeNode parent) { this.parent = parent; }
	
	public void
	addDirectory(DbDirectoryTreeNode child) {
		addDirectory(child, true);
	}
	
	private void
	addDirectory(DbDirectoryTreeNode child, boolean doNotify) {
		for(int i = 0; i < dirs.size(); i++) {
			String s = dirs.get(i).getInfo().toString();
			if(s.compareToIgnoreCase(child.getInfo().toString()) < 0) continue;
			
			dirs.insertElementAt(child, i);
			child.setParent(this);
			fireDirectoryCountChanged();
			return;
		}
		
		dirs.add(child);
		child.setParent(this);
		if(doNotify) fireDirectoryCountChanged();
	}
	
	public void
	addDirectories(DbDirectoryTreeNode[] children) {
		if(children == null || children.length == 0) return;
		for(DbDirectoryTreeNode n : children) addDirectory(n, false);
		fireDirectoryCountChanged();
	}
	
	public void
	removeDirectory(int index) {
		DbDirectoryTreeNode node = dirs.remove(index);
		node.setParent(null);
		fireDirectoryCountChanged();
	}
	
	public void
	removeDirectoryByPathName(String path) {
		if(path == null) return;
		
		for(DbDirectoryTreeNode n : dirs) {
			if(path.equals(n.getInfo().getDirectoryPath())) {
				dirs.removeElement(n);
				return;
			}
		}
	}
	
	public void
	removeInstrumentByPathName(String path) {
		if(path == null) return;
		
		for(DbInstrumentInfo info : instrs) {
			if(path.equals(info.getInstrumentPath())) {
				instrs.removeElement(info);
				return;
			}
		}
	}
	
	public void
	removeAllDirectories() {
		dirs.removeAllElements();
		fireDirectoryCountChanged();
	}
	
	public void
	updateDirectory(DbDirectoryInfo info) {
		for(int i = 0; i < dirs.size(); i++) {
			if(dirs.get(i).getInfo().getName().equals(info.getName())) {
				dirs.get(i).setInfo(info);
				fireDirectoryInfoChanged(info.getDirectoryPath());
				return;
			}
		}
	}
	
	public void
	addInstrument(DbInstrumentInfo info) {
		instrs.add(info);
		fireInstrumentCountChanged();
	}
	
	public void
	addInstruments(DbInstrumentInfo[] infos) {
		if(infos == null || infos.length == 0) return;
		for(DbInstrumentInfo i : infos) instrs.add(i);
		fireInstrumentCountChanged();
	}
	
	public void
	removeAllInstruments() {
		instrs.removeAllElements();
		fireInstrumentCountChanged();
	}
	
	public void
	updateInstrument(DbInstrumentInfo info) {
		for(int i = 0; i < instrs.size(); i++) {
			if(instrs.get(i).getName().equals(info.getName())) {
				instrs.setElementAt(info, i);
				fireInstrumentInfoChanged(info.getInstrumentPath());
				return;
			}
		}
	}
	
	public DbInstrumentInfo
	getInstrumentAt(int index) { return instrs.get(index); }
	
	public int
	getInstrumentCount() { return instrs.size(); }
	
	public int
	getInstrumentIndex(DbInstrumentInfo instr) { return instrs.indexOf(instr); }
	
	public void
	removeInstrument(int index) {
		instrs.remove(index);
		fireInstrumentCountChanged();
	}
	
	/**
	 * Gets the instrument with the specified name.
	 * @param instrName The name of the instrument to return.
	 * @return The instrument with the specified name or <code>null</code>
	 * if there is no instrument with the specified name in this directory.
	 */
	public DbInstrumentInfo
	getInstrument(String instrName) {
		for(int i = 0; i < getInstrumentCount(); i++) {
			if(instrName.equals(getInstrumentAt(i).getName())) {
				return getInstrumentAt(i);
			}
		}
		
		return null;
	}
	
	public boolean
	isConnected() { return connected; }
	
	public void
	setConnected(boolean b) { connected = b; }
	
	/**
	 * Determines whether the node is part of a tree or not.
	 * The default value is <code>false</code>.
	 */
	public boolean
	isDetached() { return detachedNode; }
	
	/**
	 * Sets whether the node is part of a tree or not.
	 */
	public void
	setDetached(boolean b) { detachedNode = b; }
	
	public String
	toString() { return info.getName(); }
	
	private void
	fireDirectoryCountChanged() {
		String s = getInfo() == null ? null : getInfo().getDirectoryPath();
		InstrumentsDbEvent e = new InstrumentsDbEvent(this, s);
		for(InstrumentsDbListener l : listeners) l.directoryCountChanged(e);
	}
	
	private void
	fireDirectoryInfoChanged(String dir) {
		InstrumentsDbEvent e = new InstrumentsDbEvent(this, dir);
		for(InstrumentsDbListener l : listeners) l.directoryInfoChanged(e);
	}
	
	private void
	fireInstrumentCountChanged() {
		String s = getInfo() == null ? null : getInfo().getDirectoryPath();
		InstrumentsDbEvent e = new InstrumentsDbEvent(this, s);
		for(InstrumentsDbListener l : listeners) l.instrumentCountChanged(e);
	}
	
	private void
	fireInstrumentInfoChanged(String instr) {
		InstrumentsDbEvent e = new InstrumentsDbEvent(this, instr);
		for(InstrumentsDbListener l : listeners) l.instrumentInfoChanged(e);
	}
}
