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

import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import org.jsampler.AudioDeviceModel;
import org.jsampler.CC;
import org.jsampler.EffectChain;
import org.jsampler.event.AudioDeviceAdapter;
import org.jsampler.event.AudioDeviceEvent;
import org.jsampler.event.EffectChainEvent;
import org.jsampler.event.EffectChainListener;
import org.jsampler.event.ListEvent;
import org.jsampler.event.ListListener;

import org.linuxsampler.lscp.Effect;
import org.linuxsampler.lscp.EffectInstance;

import static org.jsampler.JSI18n.i18n;

/**
 *
 * @author Grigor Iliev
 */
public class SamplerTreeModel implements TreeModel {
	private final SamplerTreeNode root = new SamplerTreeNode(this);
	private ArrayList<TreeModelListener> listeners = new ArrayList<TreeModelListener>();
	
	private final AudioDevicesTreeNode audioDevices;
	private final InternalEffectsTreeNode internalEffects;
	
	public
	SamplerTreeModel() {
		audioDevices = new AudioDevicesTreeNode(this, root);
		root.addChild(audioDevices);
		
		for(AudioDeviceModel a : CC.getSamplerModel().getAudioDevices()) {
			audioDevices.addChild(new AudioDeviceTreeNode(this, audioDevices, a));
			a.addAudioDeviceListener(getHandler());
		}
		
		CC.getSamplerModel().addAudioDeviceListListener(getHandler());
		
		internalEffects = new InternalEffectsTreeNode(this, root);
		root.addChild(internalEffects);
		for(int i = 0; i < CC.getSamplerModel().getEffects().getEffectCount(); i++) {
			Effect fx = CC.getSamplerModel().getEffects().getEffect(i);
			internalEffects.addChild(new InternalEffectTreeNode(internalEffects, fx));
		}
	}
	
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
	public Object
	getRoot() { return root; }
	
	@Override
	public int
	getIndexOfChild(Object parent, Object child) {
		if(parent == null || child == null) return -1;
		return ((TreeNode)parent).getIndex((TreeNode)child);
	}
	
	@Override
	public boolean
	isLeaf(Object node) { return ((TreeNode)node).isLeaf(); }
	
	@Override
	public void
	valueForPathChanged(TreePath path, Object newValue) {
		
	}
	///////
	
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
	
	private void
	fireNodeInserted(TreeNode node, int index) {
		Object[] path = getPathToRoot(node.getParent());
		
		int[] idxs = { index };
		Object[] objs = { node };
		TreeModelEvent e = new TreeModelEvent(this, path, idxs, objs);
		for(TreeModelListener l : listeners) {
			l.treeNodesInserted(e);
		}
	}
	
	private void
	fireNodeChanged(TreeNode node, int index) {
		Object[] path = getPathToRoot(node.getParent());
		int[] idxs = { index };
		Object[] objs = { node };
		TreeModelEvent e = new TreeModelEvent(this, path, idxs, objs);
		for(TreeModelListener l : listeners) {
			l.treeNodesChanged(e);
		}
	}
	
	private void
	fireNodeRemoved(TreeNode parent, TreeNode node, int index) {
		Object[] path = getPathToRoot(parent);
		int[] idxs = { index };
		Object[] objs = { node };
		TreeModelEvent e = new TreeModelEvent(this, path, idxs, objs);
		for(int i = listeners.size() - 1; i >=0; i--) {
			listeners.get(i).treeNodesRemoved(e);
		}
	}
	
	private void
	fireNodeStructureChanged(TreeNode node) {
		Object[] path = getPathToRoot(node);
		Object[] objs = { node };
		TreeModelEvent e = new TreeModelEvent(this, path);
		for(TreeModelListener l : listeners) {
			l.treeStructureChanged(e);
		}
	}
	
	
	private final EventHandler eventHandler = new EventHandler();
	
	private EventHandler
	getHandler() { return eventHandler; }
	
	private class EventHandler extends AudioDeviceAdapter implements ListListener<AudioDeviceModel> {
		
	
		/** Invoked when a new entry is added to a list. */
		@Override
		public void
		entryAdded(final ListEvent<AudioDeviceModel> e) {
			e.getEntry().addAudioDeviceListener(getHandler());
			AudioDeviceTreeNode node =
				new AudioDeviceTreeNode(SamplerTreeModel.this, audioDevices, e.getEntry());
			audioDevices.addChild(node);
			fireNodeInserted(node, audioDevices.getIndex(node));
		}
	
		/** Invoked when an entry is removed from a list. */
		@Override
		public void
		entryRemoved(ListEvent<AudioDeviceModel> e) {
			TreeNode node = audioDevices.getChildById(e.getEntry().getDeviceId());
			int i = audioDevices.getIndex(node);
			if(i == -1) return;
			
			audioDevices.removeChildAt(i);
			fireNodeRemoved(audioDevices, node, i);
			
			e.getEntry().removeAudioDeviceListener(getHandler());
		}
		
		/** Invoked when a new send effect chain is added to the audio device. */
		@Override
		public void
		sendEffectChainAdded(AudioDeviceEvent e) {
			AudioDeviceTreeNode node = 
				audioDevices.getChildById(e.getAudioDeviceModel().getDeviceId());
			
			if(node == null) {
				CC.getLogger().warning("Missing audio device node. This is a bug!");
				return;
			}
			
			SendEffectChainTreeNode child =
				new SendEffectChainTreeNode(SamplerTreeModel.this, node, e.getEffectChain());
			node.addChild(child);
			fireNodeInserted(child, node.getIndex(child));
		}
	
		/** Invoked when when a send effect chain is removed from the audio device. */
		@Override
		public void
		sendEffectChainRemoved(AudioDeviceEvent e) {
			AudioDeviceTreeNode node = 
				audioDevices.getChildById(e.getAudioDeviceModel().getDeviceId());
			
			if(node == null) {
				CC.getLogger().warning("Missing audio device node. This is a bug!");
				return;
			}
			
			SendEffectChainTreeNode child = node.getChildById(e.getEffectChain().getChainId());
			if(child == null)  {
				CC.getLogger().warning("Missing send effect chain node. This is a bug!");
				return;
			}
			
			int idx = node.getIndex(child);
			node.removeChildAt(idx);
			fireNodeRemoved(node, child, idx);
		}
	}

	public static class AbstractTreeNode<T extends TreeNode> implements TreeNode {
		protected final SamplerTreeModel treeModel;
		private final TreeNode parent;
		private final Vector<T> children = new Vector<T>();
	
		public
		AbstractTreeNode(SamplerTreeModel treeModel) { this(treeModel, null); }
	
		public
		AbstractTreeNode(SamplerTreeModel treeModel, TreeNode parent) {
			this.parent = parent;
			this.treeModel = treeModel;
		}
	
		// Tree node model methods
		@Override
		public T
		getChildAt(int index) { return children.get(index); }
	
		@Override
		public int
		getChildCount() { return children.size(); }
	
		@Override
		public TreeNode
		getParent() { return parent; }
	
		@Override
		public int
		getIndex(TreeNode node) { return children.indexOf(node); }
	
		@Override
		public boolean
		getAllowsChildren() { return true; }
	
		@Override
		public boolean
		isLeaf() { return false; }
	
		@Override
		public Enumeration
		children() { return children.elements(); }
		///////
	
		public void
		addChild(T child) { children.add(child); }
	
		public T
		removeChildAt(int index) { return children.remove(index); }
	
		public void
		removeAllChildren() { children.removeAllElements(); }
	}

	public static class AbstractTreeLeaf<T extends TreeNode> implements TreeNode {
		private final T parent;
	
		public
		AbstractTreeLeaf() { this(null); }
	
		public
		AbstractTreeLeaf(T parent) {this.parent = parent; }
	
		// Tree node model methods
		@Override
		public TreeNode
		getChildAt(int index) { return null; }
	
		@Override
		public int
		getChildCount() { return 0; }
	
		@Override
		public T
		getParent() { return parent; }
	
		@Override
		public int
		getIndex(TreeNode node) { return -1; }
	
		@Override
		public boolean
		getAllowsChildren() { return false; }
	
		@Override
		public boolean
		isLeaf() { return true; }
	
		@Override
		public Enumeration
		children() { return null; }
		///////
	}

	public static class SamplerTreeNode extends AbstractTreeNode {
		public
		SamplerTreeNode(SamplerTreeModel treeModel) {
			super(treeModel);
		}
	
		@Override
		public String
		toString() { return i18n.getLabel("SamplerTreeNode.toString"); }
	}

	public static class AudioDevicesTreeNode extends AbstractTreeNode<AudioDeviceTreeNode> {
		public
		AudioDevicesTreeNode(SamplerTreeModel treeModel, TreeNode parent) {
			super(treeModel, parent);
		}
	
		public AudioDeviceTreeNode
		getChildById(int audioDeviceId) {
			for(int i = 0; i < getChildCount(); i++) {
				if(getChildAt(i).getAudioDeviceId() == audioDeviceId) return getChildAt(i);
			}
		
			return null;
		}
	
		@Override
		public String
		toString() { return i18n.getLabel("AudioDevicesTreeNode.toString"); }
	}

	public static class AudioDeviceTreeNode extends AbstractTreeNode<SendEffectChainTreeNode> {
		private final AudioDeviceModel audioDevice;
	
		public
		AudioDeviceTreeNode (
			SamplerTreeModel treeModel, TreeNode parent, AudioDeviceModel audioDevice
		) {
			super(treeModel, parent);
			this.audioDevice = audioDevice;
			
			for(int i = 0; i < audioDevice.getSendEffectChainCount(); i++) {
				EffectChain chain = audioDevice.getSendEffectChain(i);
				addChild(new SendEffectChainTreeNode(treeModel, this, chain));
			}
		}
	
		public AudioDeviceModel
		getAudioDevice() { return audioDevice; }
	
		public int
		getAudioDeviceId() { return audioDevice.getDeviceId(); }
		
		public SendEffectChainTreeNode
		getChildById(int chainId) {
			for(int i = 0; i < getChildCount(); i++) {
				if(getChildAt(i).getChainId() == chainId) return getChildAt(i);
			}
			
			return null;
		}
	
		@Override
		public String
		toString() { return i18n.getLabel("AudioDeviceTreeNode.toString", getAudioDeviceId()); }
	}

	public static class SendEffectChainTreeNode extends AbstractTreeNode<EffectInstanceTreeNode>
							implements EffectChainListener {
		private final EffectChain chain;
	
		public
		SendEffectChainTreeNode (
			SamplerTreeModel treeModel, AudioDeviceTreeNode parent, EffectChain chain
		) {
			super(treeModel, parent);
			this.chain = chain;
			chain.addAudioDeviceListener(this);
			updateEffectInstanceList();
		}
		///////
		
		public EffectChain
		getEffectChain() { return chain; }
		
		public int
		getChainId() { return chain.getChainId(); }
	
		/**
		 * Gets the audio device to which the
		 * send effect chain represented by this tree node belongs.
		 */
		public AudioDeviceModel
		getAudioDevice() { return ((AudioDeviceTreeNode)getParent()).getAudioDevice(); }
	
		@Override
		public String
		toString() { return i18n.getLabel("SendEffectChainTreeNode.toString", chain.getChainId()); }
		
		@Override
		public void
		effectInstanceListChanged(EffectChainEvent e) {
			updateEffectInstanceList();
			treeModel.fireNodeStructureChanged(this);
		}
		
		private void
		updateEffectInstanceList() {
			removeAllChildren();
			for(int i = 0; i < chain.getEffectInstanceCount(); i++) {
				EffectInstance ei = chain.getEffectInstance(i);
				addChild(new EffectInstanceTreeNode(this, ei));
			}
			
			
		}
	}

	public static class EffectInstanceTreeNode extends AbstractTreeLeaf<SendEffectChainTreeNode> {
		private final EffectInstance effectInstance;
		
		public
		EffectInstanceTreeNode(SendEffectChainTreeNode parent, EffectInstance ei) {
			super(parent);
			effectInstance = ei;
		}
		
		public int
		getInstanceId() { return effectInstance.getInstanceId(); }
	
		@Override
		public String
		toString() { return effectInstance.getDescription(); }
	}

	public static class InternalEffectsTreeNode extends AbstractTreeNode<InternalEffectTreeNode> {
		public
		InternalEffectsTreeNode(SamplerTreeModel treeModel, TreeNode parent) {
			super(treeModel, parent);
		}
	
		@Override
		public String
		toString() { return i18n.getLabel("InternalEffectsTreeNode.toString"); }
	}

	public static class InternalEffectTreeNode extends AbstractTreeLeaf {
		private final Effect effect;
		
		public
		InternalEffectTreeNode(TreeNode parent, Effect effect) {
			super(parent);
			this.effect = effect;
		}
	
		@Override
		public String
		toString() { return effect.getDescription(); }
	}

}
