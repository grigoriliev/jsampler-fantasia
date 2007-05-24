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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTree;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;

import javax.swing.tree.TreePath;

import org.jsampler.view.DbDirectoryTreeNode;

/**
 *
 * @author Grigor Iliev
 */
public abstract class AbstractInstrumentsDbTree extends JTree {
	/**
	 * Creates a new instance of <code>AbstractInstrumentsDbTree</code>.
	 */
	public
	AbstractInstrumentsDbTree() { this((ActionListener)null); }
	
	/**
	 * Creates a new instance of <code>AbstractInstrumentsDbTree</code>.
	 * 
	 * @param l A listener that will be notified when the root
	 * directory content is loaded.
	 */
	public
	AbstractInstrumentsDbTree(ActionListener l) { this(new InstrumentsDbTreeModel(l)); }
	
	/**
	 * Creates a new instance of <code>AbstractInstrumentsDbTree</code>
	 * using the specified tree model.
	 * 
	 * @param model The model to be used by this tree.
	 */
	public
	AbstractInstrumentsDbTree(InstrumentsDbTreeModel model) {
		setModel(model);
		addTreeWillExpandListener(getHandler());
		addTreeSelectionListener(getHandler());
	}
	
	public InstrumentsDbTreeModel
	getModel() { return (InstrumentsDbTreeModel) super.getModel(); }
	
	/**
	 * Removes the selected directory.
	 */
	public void
	removeSelectedDirectory() {
		DbDirectoryTreeNode node = getSelectedDirectoryNode();
		if(node == null) return;
		
		String path = getModel().getPathName(getModel().getPathToRoot(node));
		
	}
	
	/**
	 * Returns the currently selected directory, or <code>null</code>
	 * if nothing is selected.
	 */
	public DbDirectoryTreeNode
	getSelectedDirectoryNode() {
		if(getSelectionCount() == 0) return null;
		return (DbDirectoryTreeNode)getSelectionPath().getLastPathComponent();
	}
	
	public void
	setSelectedDirectoryNode(DbDirectoryTreeNode node) {
		Object[] objs = getModel().getPathToRoot(node);
		setSelectionPath(new TreePath(objs));
	}
	
	public String
	getSelectedDirectoryPath() {
		return getModel().getPathByNode(getSelectedDirectoryNode());
	}
	
	/**
	 * Selects the specified directory.
	 * Note that if there is at least one directory in the path,
	 * which is not connected the selection will be changed
	 * after the execution of this method.
	 */
	public void
	setSelectedDirectory(final String dir) {
		getModel().loadPath(dir, new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				DbDirectoryTreeNode node = getModel().getNodeByPath(dir);
				if(node != null) setSelectedDirectoryNode(node);
			}
		});
	}
	
	/**
	 * Schedules a task for refreshing the content of the specified directory.
	 * Note that the specified directory is expected to be connected.
	 * @param dir The absolute path name of the directory to refresh.
	 */
	public void
	refreshDirectoryContent(String dir) {
		getModel().refreshDirectoryContent(dir);
	}
	
	public boolean
	hasBeenExpanded(TreePath p) {
		return super.hasBeenExpanded(p) || !getModel().isLeaf(p.getLastPathComponent());
	}
	
	private final EventHandler eventHandler = new EventHandler();
	
	private EventHandler
	getHandler() { return eventHandler; }
	
	private class EventHandler implements TreeWillExpandListener, TreeSelectionListener {
		public void
		treeWillCollapse(TreeExpansionEvent e) { }
		
		public void
		treeWillExpand(TreeExpansionEvent e) {
			getModel().treeWillExpand(e.getPath());
		}
		
		public void
		valueChanged(TreeSelectionEvent e) {
			TreePath p = e.getPath();
			if(p == null) {
				System.err.println("p is null");
				return;
			}
			getModel().treeWillExpand(p);
		}
	}
}
