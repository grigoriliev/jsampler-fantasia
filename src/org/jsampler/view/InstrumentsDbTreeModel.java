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

import java.util.Vector;

import javax.swing.SwingUtilities;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import net.sf.juife.event.TaskEvent;
import net.sf.juife.event.TaskListener;

import org.jsampler.CC;

import org.jsampler.task.InstrumentsDb;

import org.linuxsampler.lscp.DbDirectoryInfo;
import org.linuxsampler.lscp.DbInstrumentInfo;

import org.linuxsampler.lscp.event.InstrumentsDbEvent;
import org.linuxsampler.lscp.event.InstrumentsDbListener;

/**
 *
 * @author Grigor Iliev
 */
public class InstrumentsDbTreeModel implements TreeModel {
	private DbDirectoryTreeNode root = null;
	private Vector<TreeModelListener> listeners = new Vector<TreeModelListener>();
	
	/**
	 * Creates a new instance of <code>InstrumentsDbTreeModel</code>.
	 */
	public
	InstrumentsDbTreeModel() { this(null); }
	
	/**
	 * Creates a new instance of <code>InstrumentsDbTreeModel</code>.
	 * @param l A listener that will be notified when the root
	 * directory content is loaded.
	 */
	public
	InstrumentsDbTreeModel(final ActionListener l) {
		final InstrumentsDb.GetDrectory gdi = new InstrumentsDb.GetDrectory("/");
		final InstrumentsDb.GetDrectories gd = new InstrumentsDb.GetDrectories("/");
		final InstrumentsDb.GetInstruments gi = new InstrumentsDb.GetInstruments("/");
		
		gdi.addTaskListener(new TaskListener() {
			public void
			taskPerformed(TaskEvent e) {
				if(gdi.doneWithErrors()) return;
				root = new DbDirectoryTreeNode(gdi.getResult());
				fireNodeStructureChanged(root);
				
				// TODO: This shouldn't be done in the event-dispatcing thread
				CC.getClient().addInstrumentsDbListener(getHandler());
				///////
				CC.getTaskQueue().add(gd);
				CC.getTaskQueue().add(gi);
			}
		});
		
		gd.addTaskListener(new TaskListener() {
			public void
			taskPerformed(TaskEvent e) {
				root.setConnected(true);
				if(gd.doneWithErrors()) return;
				updateDirectoryContent(root, gd.getResult());
				
				for(int i = 0; i < root.getChildCount(); i++) {
					DbDirectoryTreeNode node = root.getChildAt(i);
					node.setConnected(true);
					updateDirectoryContent(node, "/" + node.toString());
				}
				
				if(l != null) l.actionPerformed(null);
			}
		});
		
		gi.addTaskListener(new TaskListener() {
			public void
			taskPerformed(TaskEvent e) {
				if(gi.doneWithErrors()) return;
				updateDirectoryContent(root, gi.getResult());
			}
		});
		
		CC.getTaskQueue().add(gdi);
	}
	
	public void
	treeWillExpand(TreePath path) {
		DbDirectoryTreeNode node = (DbDirectoryTreeNode)path.getLastPathComponent();
		
		if(!node.isConnected()) {
			node.setConnected(true);
			updateDirectoryContent(node, node.getInfo().getDirectoryPath());
		}
		
		for(int i = 0; i < node.getChildCount(); i++) {
			DbDirectoryTreeNode child = node.getChildAt(i);
			if(child.isConnected()) continue;
			child.setConnected(true);
			String pathName = getPathName(path.getPath());
			if(pathName.length() > 1) pathName += "/";
			updateDirectoryContent(child, pathName + child.toString());
		}
	}
	
	// Tree model methods
	public void
	addTreeModelListener(TreeModelListener l) {
		listeners.add(l);
	}
	
	public void
	removeTreeModelListener(TreeModelListener l) {
		listeners.remove(l);
	}
	
	public Object
	getChild(Object parent, int index) {
		return ((DbDirectoryTreeNode)parent).getChildAt(index);
	}
	
	public int
	getChildCount(Object parent) {
		return ((DbDirectoryTreeNode)parent).getChildCount();
	}
	
	public Object
	getRoot() { return root; }
	
	public int
	getIndexOfChild(Object parent, Object child) {
		if(parent == null || child == null) return -1;
		return ((DbDirectoryTreeNode)parent).getIndex((DbDirectoryTreeNode)child);
	}
	
	public boolean
	isLeaf(Object node) { return ((DbDirectoryTreeNode)node).isLeaf(); }
	
	public void
	valueForPathChanged(TreePath path, Object newValue) {
		
	}
	///////
	
	/**
	 * Schedules an update of the directory content for the specified directory node.
	 */
	private void
	updateDirectoryContent(final DbDirectoryTreeNode dirNode, String dirPath) {
		updateDirectoryContent(dirNode, dirPath, null);
	}
	
	/**
	 * Schedules an update of the directory content for the specified directory node.
	 * @param l A listener that will be notified when the subdirectory list is updated.
	 */
	private void
	updateDirectoryContent (
		final DbDirectoryTreeNode dirNode, String dirPath, final ActionListener l
	) {
		final InstrumentsDb.GetDrectories gd = new InstrumentsDb.GetDrectories(dirPath);
		
		gd.addTaskListener(new TaskListener() {
			public void
			taskPerformed(TaskEvent e) {
				if(gd.doneWithErrors()) {
					if(l != null) l.actionPerformed(null);
					return;
				}
				updateDirectoryContent(dirNode, gd.getResult());
				if(l != null) l.actionPerformed(null);
			}
		});
		CC.scheduleTask(gd);
		
		final InstrumentsDb.GetInstruments gi = new InstrumentsDb.GetInstruments(dirPath);
		
		gi.addTaskListener(new TaskListener() {
			public void
			taskPerformed(TaskEvent e) {
				if(gi.doneWithErrors()) return;
				updateDirectoryContent(dirNode, gi.getResult());
			}
		});
		CC.scheduleTask(gi);
	}
	
	private void
	updateDirectoryContent(DbDirectoryTreeNode parent, DbDirectoryInfo[] children) {
		boolean found = false;
		Vector<DbDirectoryTreeNode> removedNodes = new Vector<DbDirectoryTreeNode>();
		for(int i = 0; i < parent.getChildCount(); i++) {
			for(int j = 0; j < children.length; j++) {
				if(children[j] == null) continue;
				if(children[j].getName().equals(parent.getChildAt(i).toString())) {
					children[j] = null;
					found = true;
					break;
				}
			}
			if(!found) removedNodes.add(parent.getChildAt(i));
			found = false;
		}
		
		for(DbDirectoryTreeNode node : removedNodes) {
			int i = parent.getIndex(node);
			parent.removeDirectory(i);
			fireNodeRemoved(parent, node, i);
		}
		
		for(DbDirectoryInfo info : children) {
			if(info == null) continue;
			DbDirectoryTreeNode node = new DbDirectoryTreeNode(info);
			parent.addDirectory(node);
			fireNodeInserted(node, parent.getIndex(node));
			if(parent.getParent() == null) {
				node.setConnected(true);
				updateDirectoryContent(node, info.getDirectoryPath());
			} else if(parent.isConnected()) {
				updateDirectoryContent(node, info.getDirectoryPath());
			}
		}
	}
	
	private void
	updateDirectoryContent(DbDirectoryTreeNode parent, DbInstrumentInfo[] children) {
		boolean found = false;
		Vector<DbInstrumentInfo> removedNodes = new Vector<DbInstrumentInfo>();
		for(int i = 0; i < parent.getInstrumentCount(); i++) {
			String name = parent.getInstrumentAt(i).getName();
			
			for(int j = 0; j < children.length; j++) {
				if(children[j] == null) continue;
				if(children[j].getName().equals(name)) {
					children[j] = null;
					found = true;
					break;
				}
			}
			if(!found) removedNodes.add(parent.getInstrumentAt(i));
			found = false;
		}
		
		for(DbInstrumentInfo info : removedNodes) {
			int i = parent.getInstrumentIndex(info);
			parent.removeInstrument(i);
		}
		
		for(DbInstrumentInfo info : children) {
			if(info == null) continue;
			parent.addInstrument(info);
		}
	}
	
	/**
	 * Schedules a task for refreshing the directory content of the specified directory.
	 * Note that the specified directory is expected to be connected.
	 * @param dir The absolute path name of the directory to refresh.
	 */
	public void
	refreshDirectoryContent(String dir) {
		final DbDirectoryTreeNode node = getNodeByPath(dir);
		if(node == null) return;
		
		node.removeAllDirectories();
		fireNodeStructureChanged(node);
		node.removeAllInstruments();
		
		final InstrumentsDb.GetDrectories gd = new InstrumentsDb.GetDrectories(dir);
		
		gd.addTaskListener(new TaskListener() {
			public void
			taskPerformed(TaskEvent e) {
				if(gd.doneWithErrors()) return;
				
				for(DbDirectoryInfo info : gd.getResult()) {
					DbDirectoryTreeNode n = new DbDirectoryTreeNode(info);
					node.addDirectory(n);
					fireNodeInserted(n, node.getIndex(n));
					n.setConnected(true);
					updateDirectoryContent(n, n.getInfo().getDirectoryPath());
				}
			}
		});
		CC.scheduleTask(gd);
		
		final InstrumentsDb.GetInstruments gi = new InstrumentsDb.GetInstruments(dir);
		
		gi.addTaskListener(new TaskListener() {
			public void
			taskPerformed(TaskEvent e) {
				if(gi.doneWithErrors()) return;
				
				for(DbInstrumentInfo info : gi.getResult()) {
					node.addInstrument(info);
				}
			}
		});
		CC.scheduleTask(gi);
	}
	
	protected Object[]
	getPathToRoot(DbDirectoryTreeNode node) {
		Vector v = new Vector();
		
		while(node != null) {
			v.insertElementAt(node, 0);
			if(node == getRoot()) break;
			node = node.getParent();
		}
		
		return v.toArray(new Object[v.size()]);
	}
	
	protected String
	getPathName(Object[] objs) {
		if(objs.length == 1) return "/";
		
		StringBuffer sb = new StringBuffer();
		for(int i = 1; i < objs.length; i++) {
			sb.append('/').append(objs[i].toString());
		}
		
		return sb.toString();
	}
	
	private void
	fireNodeInserted(DbDirectoryTreeNode node, int index) {
		Object[] path = getPathToRoot(node.getParent());
		int[] idxs = { index };
		Object[] objs = { node };
		TreeModelEvent e = new TreeModelEvent(this, path, idxs, objs);
		for(TreeModelListener l : listeners) {
			l.treeNodesInserted(e);
		}
	}
	
	private void
	fireNodeChanged(DbDirectoryTreeNode node, int index) {
		Object[] path = getPathToRoot(node.getParent());
		int[] idxs = { index };
		Object[] objs = { node };
		TreeModelEvent e = new TreeModelEvent(this, path, idxs, objs);
		for(TreeModelListener l : listeners) {
			l.treeNodesChanged(e);
		}
	}
	
	private void
	fireNodeRemoved(DbDirectoryTreeNode parent, DbDirectoryTreeNode node, int index) {
		Object[] path = getPathToRoot(parent);
		int[] idxs = { index };
		Object[] objs = { node };
		TreeModelEvent e = new TreeModelEvent(this, path, idxs, objs);
		for(int i = listeners.size() - 1; i >=0; i--) {
			listeners.get(i).treeNodesRemoved(e);
		}
	}
	
	private void
	fireNodeStructureChanged(DbDirectoryTreeNode node) {
		Object[] path = getPathToRoot(node);
		Object[] objs = { node };
		TreeModelEvent e = new TreeModelEvent(this, path);
		for(TreeModelListener l : listeners) {
			l.treeStructureChanged(e);
		}
	}
	
	public DbDirectoryTreeNode
	getNodeByPath(String path) {
		String[] dirs = getDirectoryPath(path);
		if(dirs == null) return null;
		if(dirs.length == 1) return root;
		
		DbDirectoryTreeNode node = root;
		boolean found = false;
		for(int i = 1; i < dirs.length; i++) {
			for(int k = 0; k < node.getChildCount(); k++) {
				if(dirs[i].equals(node.getChildAt(k).toString())) {
					node = node.getChildAt(k);
					found = true;
					break;
				}
			}
			
			if(!found) return null;
			found = false;
		}
		
		return node;
	}
	
	public String
	getPathByNode(DbDirectoryTreeNode node) {
		if(node == null) return null;
		return getPathName(getPathToRoot(node));
	}
	
	private String[]
	getDirectoryPath(String path) {
		if(path == null || path.length() == 0) return null;
		if(path.charAt(0) != '/') return null;
		Vector<String> v = new Vector<String>();
		v.add("/");
		if(path.length() == 1) return v.toArray(new String[v.size()]);
		
		if(path.charAt(path.length() - 1) != '/') path += "/";
		int i = 1;
		int j = path.indexOf('/', i);
		
		while(j != -1) {
			v.add(path.substring(i, j));
			
			i = j + 1;
			if(i >= path.length()) return v.toArray(new String[v.size()]);
			j = path.indexOf('/', i);
		}
		
		return null;
	}
	
	public static String
	getParentDirectory(String path) {
		if(path == null || path.length() == 0) return null;
		if(path.charAt(0) != '/') return null;
		if(path.length() == 1) return null;
		
		if(path.charAt(path.length() - 1) == '/') {
			path = path.substring(0, path.length() - 1);
		}
		
		int i = path.lastIndexOf('/');
		if(i == 0) return "/";
		return path.substring(0, i);
	}
	
	/**
	 * @param l A listener which will be notified when the operation is completed.
	 */
	public void
	loadPath(String path, final ActionListener l) {
		// TODO: This method is lazily implemented. Should be optimized.
		final String[] dirs = getDirectoryPath(path);
		if(dirs == null) {
			l.actionPerformed(null);
			return;
		}
		
		final ActionListener listener = new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				String s = "";
				DbDirectoryTreeNode node = null;
				for(int i = 0; i < dirs.length; i++) {
					if(i > 1) s += "/" + dirs[i];
					else s += dirs[i];
					node = getNodeByPath(s);
					if(node == null) {
						if(l != null) l.actionPerformed(null);
						return;
					}
					
					if(!node.isConnected()) {
						node.setConnected(true);
						updateDirectoryContent(node, s, this);
						return;
					}
				}
				
				if(l != null) l.actionPerformed(null);
			}
		};
		
		listener.actionPerformed(null);
	}
	
	private final EventHandler eventHandler = new EventHandler();
	
	private EventHandler
	getHandler() { return eventHandler; }
	
	private class EventHandler implements InstrumentsDbListener {
		/**
		 * Invoked when the number of instrument
		 * directories in a specific directory has changed.
		 */
		public void
		directoryCountChanged(final InstrumentsDbEvent e) {
			SwingUtilities.invokeLater(new Runnable() {
				public void
				run() {
					DbDirectoryTreeNode node = getNodeByPath(e.getPathName());
					if(node == null) {
						return;
					}
					if(!node.isConnected()) {
						return;
					}
					
					updateDirectoryContent(node, e.getPathName());
				}
			});
		}
		
		/**
		 * Invoked when the settings of an instrument directory are changed.
		 */
		public void
		directoryInfoChanged(final InstrumentsDbEvent e) {
			SwingUtilities.invokeLater(new Runnable() {
				public void
				run() { updateDirectoryInfo(e); }
			});
		}
		
		private void
		updateDirectoryInfo(InstrumentsDbEvent e) {
			final DbDirectoryTreeNode node = getNodeByPath(e.getPathName());
			if(node == null) return;
			
			final InstrumentsDb.GetDrectory t =
				new InstrumentsDb.GetDrectory(e.getPathName());
			
			t.addTaskListener(new TaskListener() {
				public void
				taskPerformed(TaskEvent e) {
					if(t.doneWithErrors()) return;
					if(node.getParent() != null) {
						node.getParent().updateDirectory(t.getResult());
						fireNodeChanged(node, node.getParent().getIndex(node));
					}
				}
			});
			
			CC.getTaskQueue().add(t);
		}
		
		/**
		 * Invoked when an instrument directory is renamed.
		 */
		public void
		directoryNameChanged(final InstrumentsDbEvent e) {
			SwingUtilities.invokeLater(new Runnable() {
				public void
				run() { directoryRenamed(e); }
			});
		}
		
		private void
		directoryRenamed(InstrumentsDbEvent e) {
			DbDirectoryTreeNode node = getNodeByPath(e.getPathName());
			if(node == null) {
				// If the directory is renamed by this frontend the
				// directory should already be with the new name
				String s = getParentDirectory(e.getPathName());
				if(s.length() == 1) s += e.getNewName();
				else s += "/" + e.getNewName();
				node = getNodeByPath(s);
			}
			if(node == null || node.getParent() == null) {
				CC.getLogger().warning("Invalid path: " + e.getPathName());
				return;
			}
			
			node.getInfo().setName(e.getNewName());
			DbDirectoryTreeNode parent = node.getParent();
			
			int i = parent.getIndex(node);
			parent.removeDirectory(i);
			fireNodeRemoved(parent, node, i);
			
			parent.addDirectory(node);
			fireNodeInserted(node, parent.getIndex(node));
		}
		
		/**
		 * Invoked when the number of instruments
		 * in a specific directory has changed.
		 */
		public void
		instrumentCountChanged(final InstrumentsDbEvent e) {
			SwingUtilities.invokeLater(new Runnable() {
				public void
				run() {
					DbDirectoryTreeNode node = getNodeByPath(e.getPathName());
					if(node == null) {
						return;
					}
					if(!node.isConnected()) {
						return;
					}
					
					updateDirectoryContent(node, e.getPathName());
				}
			});
		}
		
		/**
		 * Invoked when the settings of an instrument are changed.
		 */
		public void
		instrumentInfoChanged(final InstrumentsDbEvent e) {
			SwingUtilities.invokeLater(new Runnable() {
				public void
				run() { updateInstrumentInfo(e); }
			});
		}
		
		/**
		 * Invoked when an instrument is renamed.
		 */
		public void
		instrumentNameChanged(final InstrumentsDbEvent e) {
			SwingUtilities.invokeLater(new Runnable() {
				public void
				run() { instrumentRenamed(e); }
			});
		}
		
		private void
		updateInstrumentInfo(InstrumentsDbEvent e) {
			String dir = getParentDirectory(e.getPathName());
			final DbDirectoryTreeNode node = getNodeByPath(dir);
			if(node == null) return;
			if(!node.isConnected()) return;
			
			final InstrumentsDb.GetInstrument t =
				new InstrumentsDb.GetInstrument(e.getPathName());
			
			t.addTaskListener(new TaskListener() {
				public void
				taskPerformed(TaskEvent e) {
					if(t.doneWithErrors()) return;
					node.updateInstrument(t.getResult());
				}
			});
			
			CC.getTaskQueue().add(t);
		}
		
		private void
		instrumentRenamed(InstrumentsDbEvent e) {
			String dir = getParentDirectory(e.getPathName());
			DbDirectoryTreeNode node = getNodeByPath(dir);
			if(node == null) return;
			
			String instr = e.getPathName();
			int i = instr.lastIndexOf('/');
			if(i != -1 && i < instr.length() - 1) instr = instr.substring(i + 1);
			else return;
			
			DbInstrumentInfo info = node.getInstrument(instr);
			
			if(info == null) {
				// If the instrument is renamed by this frontend the
				// instrument should already be with the new name
				info = node.getInstrument(e.getNewName());
			}
			if(info == null) {
				CC.getLogger().warning("Invalid path: " + e.getPathName());
				return;
			}
			
			info.setName(e.getNewName());
			node.removeInstrument(node.getInstrumentIndex(info));
			node.addInstrument(info);
		}
		
		/** Invoked when the status of particular job has changed. */
		public void
		jobStatusChanged(InstrumentsDbEvent e) { }
	}
}
