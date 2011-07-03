/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2011 Grigor Iliev <grigor@grigoriliev.com>
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

import java.util.ArrayList;
import java.util.Vector;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

/**
 *
 * @author Grigor Iliev
 */
public abstract class AbstractTreeModel implements TreeModel {
	private ArrayList<TreeModelListener> listeners = new ArrayList<TreeModelListener>();
	
	
	
	// Tree model methods
	@Override
	public void
	addTreeModelListener(TreeModelListener l) {
		listeners.add(l);
	}
	
	@Override
	public void
	removeTreeModelListener(TreeModelListener l) {
		listeners.remove(l);
	}
	
	@Override
	public boolean
	isLeaf(Object node) { return ((TreeNode)node).isLeaf(); }
	
	@Override
	public Object
	getChild(Object parent, int index) {
		return ((TreeNode)parent).getChildAt(index);
	}
	
	@Override
	public int
	getChildCount(Object parent) {
		return ((TreeNode)parent).getChildCount();
	}
	
	@Override
	public int
	getIndexOfChild(Object parent, Object child) {
		if(parent == null || child == null) return -1;
		return ((TreeNode)parent).getIndex((TreeNode)child);
	}
	//////////
	
	protected Object[]
	getPathToRoot(TreeNode node) {
		Vector v = new Vector();
		
		while(node != null) {
			v.insertElementAt(node, 0);
			if(node == getRoot()) break;
			node = node.getParent();
		}
		
		return v.toArray(new Object[v.size()]);
	}
	
	protected void
	fireNodeInserted(TreeNode node, int index) {
		Object[] path = getPathToRoot(node.getParent());
		
		int[] idxs = { index };
		Object[] objs = { node };
		TreeModelEvent e = new TreeModelEvent(this, path, idxs, objs);
		for(TreeModelListener l : listeners) {
			l.treeNodesInserted(e);
		}
	}
	
	protected void
	fireNodeChanged(TreeNode node, int index) {
		Object[] path = getPathToRoot(node.getParent());
		int[] idxs = { index };
		Object[] objs = { node };
		TreeModelEvent e = new TreeModelEvent(this, path, idxs, objs);
		for(TreeModelListener l : listeners) {
			l.treeNodesChanged(e);
		}
	}
	
	protected void
	fireNodeRemoved(TreeNode parent, TreeNode node, int index) {
		Object[] path = getPathToRoot(parent);
		int[] idxs = { index };
		Object[] objs = { node };
		TreeModelEvent e = new TreeModelEvent(this, path, idxs, objs);
		for(int i = listeners.size() - 1; i >=0; i--) {
			listeners.get(i).treeNodesRemoved(e);
		}
	}
	
	protected void
	fireNodeStructureChanged(TreeNode node) {
		Object[] path = getPathToRoot(node);
		Object[] objs = { node };
		TreeModelEvent e = new TreeModelEvent(this, path);
		for(TreeModelListener l : listeners) {
			l.treeStructureChanged(e);
		}
	}
}
