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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.jsampler.AudioDeviceModel;
import org.jsampler.CC;
import org.jsampler.EffectChain;
import org.jsampler.EffectInstance;
import org.jsampler.SamplerChannelModel;
import org.jsampler.event.AudioDeviceEvent;
import org.jsampler.event.AudioDeviceListener;
import org.jsampler.event.EffectChainEvent;
import org.jsampler.event.EffectChainListener;
import org.jsampler.event.EffectInstanceEvent;
import org.jsampler.event.EffectInstanceListener;
import org.jsampler.event.EffectSendsEvent;
import org.jsampler.event.EffectSendsListener;
import org.jsampler.event.ListEvent;
import org.jsampler.event.ListListener;

import org.linuxsampler.lscp.Effect;
import org.linuxsampler.lscp.EffectParameter;
import org.linuxsampler.lscp.FxSend;

import static org.jsampler.JSI18n.i18n;

/**
 *
 * @author Grigor Iliev
 */
public class SamplerTreeModel extends AbstractTreeModel {
	private final SamplerTreeNode root = new SamplerTreeNode(this);
	
	private final SamplerChannelDirTreeNode samplerChannels;
	private final AudioDevicesTreeNode audioDevices;
	private final InternalEffectsTreeNode internalEffects;
	
	public
	SamplerTreeModel() {
		audioDevices = new AudioDevicesTreeNode(this, root); // should be created before samplerChannels
		
		for(AudioDeviceModel a : CC.getSamplerModel().getAudioDevices()) {
			audioDevices.addChild(new AudioDeviceTreeNode(this, audioDevices, a));
			a.addAudioDeviceListener(getHandler());
		}
		
		CC.getSamplerModel().addAudioDeviceListListener(getHandler());
		
		root.addChild(audioDevices);
		
		samplerChannels = new SamplerChannelDirTreeNode(this, root);
		root.insertChild(samplerChannels, root.getIndex(audioDevices));
		
		internalEffects = new InternalEffectsTreeNode(this, root);
		root.addChild(internalEffects);
		for(int i = 0; i < CC.getSamplerModel().getEffects().getEffectCount(); i++) {
			Effect fx = CC.getSamplerModel().getEffects().getEffect(i);
			internalEffects.addChild(new InternalEffectTreeNode(internalEffects, fx));
		}
	}
	
	@Override
	public Object
	getRoot() { return root; }
	
	@Override
	public void
	valueForPathChanged(TreePath path, Object newValue) {
		
	}
	///////
	
	
	private final EventHandler eventHandler = new EventHandler();
	
	private EventHandler
	getHandler() { return eventHandler; }
	
	private class EventHandler implements ListListener<AudioDeviceModel>, AudioDeviceListener {
		
	
		/** Invoked when a new entry is added to a list. */
		@Override
		public void
		entryAdded(final ListEvent<AudioDeviceModel> e) {
			e.getEntry().addAudioDeviceListener(getHandler());
			AudioDeviceTreeNode node =
				new AudioDeviceTreeNode(SamplerTreeModel.this, audioDevices, e.getEntry());
			audioDevices.addChild(node);
			fireNodeInserted(node, audioDevices.getIndex(node));
			
			root.firePropertyChange("SamplerTreeModel.update", null, null);
			audioDevices.firePropertyChange("SamplerTreeModel.update", null, null);
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
			
			root.firePropertyChange("SamplerTreeModel.update", null, null);
			audioDevices.firePropertyChange("SamplerTreeModel.update", null, null);
		}
		
		@Override
		public void
		settingsChanged(AudioDeviceEvent e) {
			audioDevices.firePropertyChange("SamplerTreeModel.update", null, null);
			
			TreeNodeBase node =
				audioDevices.getChildById(e.getAudioDeviceModel().getDeviceId());
			
			if(node != null) {
				node.firePropertyChange("SamplerTreeModel.update", null, null);
			}
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
			
			SendEffectChainsTreeNode chainsNode = node.getSendEffectChainsNode();
			
			SendEffectChainTreeNode child = new SendEffectChainTreeNode (
				SamplerTreeModel.this, chainsNode, e.getEffectChain()
			);
			
			chainsNode.addChild(child);
			fireNodeInserted(child, chainsNode.getIndex(child));
			
			node.firePropertyChange("SamplerTreeModel.update", null, null);
			chainsNode.firePropertyChange("SamplerTreeModel.update", null, null);
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
			
			SendEffectChainsTreeNode chainsNode = node.getSendEffectChainsNode();
			
			SendEffectChainTreeNode child = chainsNode.getChildById(e.getEffectChain().getChainId());
			if(child == null)  {
				CC.getLogger().warning("Missing send effect chain node. This is a bug!");
				return;
			}
			
			child.uninstall();
			
			int idx = chainsNode.getIndex(child);
			chainsNode.removeChildAt(idx);
			fireNodeRemoved(chainsNode, child, idx);
			
			node.firePropertyChange("SamplerTreeModel.update", null, null);
			chainsNode.firePropertyChange("SamplerTreeModel.update", null, null);
		}
	}
	
	public static abstract class TreeNodeBase<T extends TreeNodeBase>
					extends PropertyChangeSupport implements TreeNode {
		
		public TreeNodeBase() { super(new Object()); }
		
		public abstract T getChildAt(int index);
		public abstract boolean isLeaf();
		public abstract void uninstall();
		
		public boolean
		isLink() { return false; }
		
		public TreeNodeBase
		getLink() { return null; }
		
		public void edit() { }
		
		public int
		getId() { return -1; }
		
		/** Gets the number of columns for the corresponding table model. */
		public int
		getColumnCount() { return 1; }
		
		/** Gets the number of rows for the corresponding table model. */
		public int
		getRowCount() { return 1; }
		
		/**
		 * Gets the value for the cell at <code>row</code> and
		 * <code>col</code> for the corresponding table model.
		 */
		public Object
		getValueAt(int row, int col) { return "Not implemented yet"; }
		
		/** Gets the name of the column for the corresponding table model. */
		public String
		getColumnName(int col) { return " "; }
		
		/** Gets the number of items this node contains. */
		public int
		getItemCount() { return getRowCount(); }
		
		public String
		getItemCountString() { return String.valueOf(getItemCount()); }
		
		/** Determines the alignment for the cells in the specified column. */
		public int
		getHorizontalAlignment(int column) {
			return column == 0 ? SwingConstants.LEFT : SwingConstants.CENTER;
		}
	}

	public static class AbstractTreeNode<T extends TreeNodeBase> extends TreeNodeBase<T> {
		protected  AbstractTreeModel treeModel;
		private  TreeNodeBase parent;
		private Vector<T> children = new Vector<T>();
	
		public
		AbstractTreeNode(AbstractTreeModel treeModel) { this(treeModel, null); }
	
		public
		AbstractTreeNode(AbstractTreeModel treeModel, TreeNodeBase parent) {
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
		public TreeNodeBase
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
		///////
	
		public void
		insertChild(T child, int index) { children.insertElementAt(child, index); }
	
		public T
		removeChildAt(int index) { return children.remove(index); }
	
		public void
		removeAllChildren() {
			children.removeAllElements();
		}
	
		public T
		getChildById(int id) {
			if(id == -1) return null;
			
			for(int i = 0; i < getChildCount(); i++) {
				if(getChildAt(i).getId() == id) return getChildAt(i);
			}
		
			return null;
		}
	
		public void
		removeAndUninstallAllChildren() {
			for(int i = getChildCount() - 1; i >= 0; i--) {
				T child = getChildAt(i);
				removeChildAt(i);
				child.uninstall();
			}
		}
		
		public void
		uninstall() {
			removeAndUninstallAllChildren();
			treeModel = null;
			parent = null;
		}
	}

	public static class AbstractTreeLeaf<T extends TreeNodeBase> extends TreeNodeBase<T> {
		private T parent;
	
		public
		AbstractTreeLeaf() { this(null); }
	
		public
		AbstractTreeLeaf(T parent) {this.parent = parent; }
	
		// Tree node model methods
		@Override
		public T
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
		
		public void
		uninstall() { parent = null; }
	}

	public static class StandardTreeNode<T extends TreeNodeBase> extends AbstractTreeNode<T> {
		public
		StandardTreeNode(AbstractTreeModel treeModel) { this(treeModel, null); }
	
		public
		StandardTreeNode(AbstractTreeModel treeModel, TreeNodeBase parent) {
			super(treeModel, parent);
		}
		
		/** Gets the number of columns for the corresponding table model. */
		@Override
		public int
		getColumnCount() { return 3; }
		
		/** Gets the number of rows for the corresponding table model. */
		@Override
		public int
		getRowCount() { return getChildCount(); }
		
		/**
		 * Gets the value for the cell at <code>row</code> and
		 * <code>col</code> for the corresponding table model.
		 */
		@Override
		public Object
		getValueAt(int row, int col) {
			if(col == 0) return getChildAt(row);
			if(col == 1) return getChildAt(row).getItemCountString();
			return "";
		}
		
		/** Gets the name of the column for the corresponding table model. */
		@Override
		public String
		getColumnName(int col) {
			if(col == 0) return i18n.getLabel("SamplerTreeModel.tableColumn.name");
			if(col == 1) return i18n.getLabel("SamplerTreeModel.tableColumn.itemCount");
			return " ";
		}
	}

	public static class SamplerTreeNode extends StandardTreeNode {
		public
		SamplerTreeNode(SamplerTreeModel treeModel) {
			super(treeModel);
		}
	
		@Override
		public String
		toString() { return i18n.getLabel("SamplerTreeNode.toString"); }
	}

	public static class SamplerChannelDirTreeNode
				extends StandardTreeNode<ChannelLaneTreeNode>
				implements PropertyChangeListener {
		public
		SamplerChannelDirTreeNode(SamplerTreeModel treeModel, TreeNodeBase parent) {
			super(treeModel, parent);
			
			updateChildren();
			
			CC.getMainFrame().addPropertyChangeListener(this);
		}
		
		@Override
		public void
		propertyChange(PropertyChangeEvent e) {
			if (
				e.getPropertyName() == "channelLaneAdded" ||
				e.getPropertyName() == "channelLaneInserted" ||
				e.getPropertyName() == "channelLaneRemoved"
			) {
				updateChildren();
				firePropertyChange("SamplerTreeModel.update", null, null);
				getParent().firePropertyChange("SamplerTreeModel.update", null, null);
			}
		}
		
		private void
		updateChildren() {
			removeAndUninstallAllChildren();
			
			for(int i = 0; i < CC.getMainFrame().getChannelsPaneCount(); i++) {
				JSChannelsPane lane = CC.getMainFrame().getChannelsPane(i);
				addChild(new ChannelLaneTreeNode(treeModel, this, lane));
			}
		}
		
		@Override
		public void
		uninstall() {
			CC.getMainFrame().removePropertyChangeListener(this);
			
			super.uninstall();
		}
	
		@Override
		public String
		toString() { return i18n.getLabel("SamplerChannelDirTreeNode.toString"); }
	}

	public static class ChannelLaneTreeNode extends StandardTreeNode<SamplerChannelTreeNode>
						implements PropertyChangeListener {
		
		private JSChannelsPane lane;
		
		public
		ChannelLaneTreeNode (
			AbstractTreeModel treeModel, TreeNodeBase parent, JSChannelsPane lane
		) {
			super(treeModel, parent);
			this.lane = lane;
			
			updateChildren();
			
			lane.addPropertyChangeListener(this);
		}
		
		public JSChannelsPane
		getLane() { return lane; }
		
		/** Gets the number of columns for the corresponding table model. */
		@Override
		public int
		getColumnCount() { return 3; }
		
		/** Gets the number of rows for the corresponding table model. */
		@Override
		public int
		getRowCount() { return getChildCount(); }
		
		/**
		 * Gets the value for the cell at <code>row</code> and
		 * <code>col</code> for the corresponding table model.
		 */
		@Override
		public Object
		getValueAt(int row, int col) {
			if(col == 0) return getChildAt(row);
			if(col == 1) {
				Object o = getChildAt(row).getChannel().getChannelInfo().getEngine();
				if(o == null) o = i18n.getLabel("ChannelLaneTreeNode.noEngine");
				return o;
			}
			return "";
		}
		
		/** Gets the name of the column for the corresponding table model. */
		@Override
		public String
		getColumnName(int col) {
			if(col == 0) return i18n.getLabel("SamplerTreeModel.tableColumn.chn");
			if(col == 1) return i18n.getLabel("SamplerTreeModel.tableColumn.engine");
			return " ";
		}
		
		@Override
		public void
		propertyChange(PropertyChangeEvent e) {
			if (
				e.getPropertyName() == "channelAdded" ||
				e.getPropertyName() == "channelsAdded" ||
				e.getPropertyName() == "channelRemoved" ||
				e.getPropertyName() == "channelsRemoved"
			) {
				updateChildren();
				firePropertyChange("SamplerTreeModel.update", null, null);
				getParent().firePropertyChange("SamplerTreeModel.update", null, null);
			}
			
			if(e.getPropertyName() == "channelsPositionChanged") {
				updateChildren();
				firePropertyChange("SamplerTreeModel.update", null, null);
			}
		}
		
		private void
		updateChildren() {
			removeAndUninstallAllChildren();
			
			for(int i = 0; i < lane.getChannelCount(); i++) {
				SamplerChannelModel chn = lane.getChannel(i).getModel();
				addChild(new SamplerChannelTreeNode(treeModel, this, chn));
			}
			
			treeModel.fireNodeStructureChanged(this);
		}
		
		@Override
		public void
		uninstall() {
			lane.removePropertyChangeListener(this);
			lane = null;
			
			super.uninstall();
		}
	
		@Override
		public String
		toString() { return lane.getTitle(); }
	}

	public static class SamplerChannelTreeNode extends StandardTreeNode {
		private SamplerChannelModel channel;
		
		public
		SamplerChannelTreeNode (
			AbstractTreeModel treeModel, TreeNodeBase parent, SamplerChannelModel chn
		) {
			super(treeModel, parent);
			channel = chn;
			addChild(new FxSendDirTreeNode(treeModel, this, chn));
		}
		
		public SamplerChannelModel
		getChannel() { return channel; }
		
		public int
		getId() { return getChannel().getChannelId(); }
		
		@Override
		public void
		uninstall() {
			super.uninstall();
			channel = null; // might be needed by the children to uninstall properly
		}
	
		@Override
		public String
		toString() {
			String s = CC.getMainFrame().getChannelPath(channel);
			return i18n.getLabel("SamplerChannelTreeNode.toString", s);
		}
	}

	public static class FxSendDirTreeNode
				extends StandardTreeNode<FxSendTreeNode>
				implements EffectSendsListener {
		
		private SamplerChannelModel channel;
		
		public
		FxSendDirTreeNode (
			AbstractTreeModel treeModel, TreeNodeBase parent, SamplerChannelModel chn
		) {
			super(treeModel, parent);
			channel = chn;
			
			for(FxSend fx : chn.getFxSends()) {
				addChild(new FxSendTreeNode(treeModel, this, fx));
			}
			
			chn.addEffectSendsListener(this);
		}
		
		public SamplerChannelModel
		getChannel() { return channel; }
		
		@Override
		public void
		effectSendAdded(EffectSendsEvent e) {
			FxSendTreeNode node = new FxSendTreeNode(treeModel, this, e.getFxSend());
			addChild(node);
			treeModel.fireNodeInserted(node, getIndex(node));
			firePropertyChange("SamplerTreeModel.update", null, null);
			getParent().firePropertyChange("SamplerTreeModel.update", null, null);
		}
	
		@Override
		public void
		effectSendRemoved(EffectSendsEvent e) {
			final FxSendTreeNode child = getChildById(e.getFxSend().getFxSendId());
			int i = getIndex(child);
			if(i == -1) return;
			
			removeChildAt(i);
			treeModel.fireNodeRemoved(this, child, i);
			firePropertyChange("SamplerTreeModel.update", null, null);
			getParent().firePropertyChange("SamplerTreeModel.update", null, null);
			
			/* To avoid ConcurrentModificationException. */
			SwingUtilities.invokeLater(new Runnable() {
				public void
				run() { child.uninstall(); }
			});
			///////
		}
	
		@Override
		public void
		effectSendChanged(EffectSendsEvent e) {
			FxSendTreeNode node = getChildById(e.getFxSend().getFxSendId());
			node.setFxSend(e.getFxSend());
			
			treeModel.fireNodeChanged(node, getIndex(node));
			
			firePropertyChange("SamplerTreeModel.update", null, null);
		}
		
		@Override
		public void
		uninstall() {
			channel.removeEffectSendsListener(this);
			super.uninstall();
			channel = null; // might be needed by the children to uninstall properly
		}
	
		@Override
		public String
		toString() { return i18n.getLabel("FxSendDirTreeNode.toString"); }
	}

	public static class FxSendTreeNode extends StandardTreeNode<DestEffectDirTreeNode> {
		private FxSend fxSend;
		private DestEffectDirTreeNode destEffectDir;
		
		public
		FxSendTreeNode(AbstractTreeModel treeModel, TreeNodeBase parent, FxSend fxSend) {
			super(treeModel, parent);
			this.fxSend = fxSend;
			
			destEffectDir = new DestEffectDirTreeNode(treeModel, this, fxSend);
			addChild(destEffectDir);
		}
		
		public SamplerChannelModel
		getChannel() { return ((FxSendDirTreeNode)getParent()).getChannel(); }
		
		public FxSend
		getFxSend() { return fxSend; }
		
		public void
		setFxSend(FxSend fxSend) {
			this.fxSend = fxSend;
			destEffectDir.setFxSend(fxSend);
		}
		
		public int
		getId() { return fxSend.getFxSendId(); }
		
		@Override
		public void
		uninstall() {
			fxSend = null;
			super.uninstall();
		}
	
		@Override
		public String
		toString() { return fxSend != null ? fxSend.getName(): super.toString(); }
	}

	public static class DestEffectDirTreeNode extends AbstractTreeNode<DestEffectTreeNode> {
		public
		DestEffectDirTreeNode(AbstractTreeModel treeModel, TreeNodeBase parent, FxSend fxSend) {
			super(treeModel, parent);
			
			addChild(new DestEffectTreeNode(this, fxSend));
		}
		
		public void
		setFxSend(FxSend fxSend) { getChildAt(0).setFxSend(fxSend); }
		
		/** Gets the number of columns for the corresponding table model. */
		@Override
		public int
		getColumnCount() { return 3; }
		
		/** Gets the number of rows for the corresponding table model. */
		@Override
		public int
		getRowCount() { return getChildCount(); }
		
		/**
		 * Gets the value for the cell at <code>row</code> and
		 * <code>col</code> for the corresponding table model.
		 */
		@Override
		public Object
		getValueAt(int row, int col) {
			if(col == 0) return getChildAt(row);
			if(col == 1) return getChildAt(row).getEffectInstanceString();
			return "";
		}
		
		/** Gets the name of the column for the corresponding table model. */
		@Override
		public String
		getColumnName(int col) {
			if(col == 0) return i18n.getLabel("SamplerTreeModel.tableColumn.destChain");
			if(col == 1) return i18n.getLabel("SamplerTreeModel.tableColumn.destFx");
			return " ";
		}
	
		@Override
		public String
		toString() { return i18n.getLabel("DestEffectDirTreeNode.toString"); }
	}

	public static class DestEffectTreeNode  extends AbstractTreeLeaf<DestEffectDirTreeNode>
						implements EffectSendsListener {
		
		private SendEffectChainTreeNode chain;
		private FxSend fxSend;
				
		public
		DestEffectTreeNode(DestEffectDirTreeNode parent, FxSend fxSend) {
			super(parent);
			
			setFxSend(fxSend);
			
			/* To avoid ConcurrentModificationException because
			 * this method is created due to effect sends event. */
			SwingUtilities.invokeLater(new Runnable() {
				public void
				run() { getChannel().addEffectSendsListener(DestEffectTreeNode.this); }
			});
			///////
		}
		
		public SamplerChannelModel
		getChannel() { return ((FxSendTreeNode)getParent().getParent()).getChannel(); }
		
		public FxSend
		getFxSend() { return fxSend; }
		
		public void
		setFxSend(FxSend fxSend) {
			this.fxSend = fxSend;
			
			int d = getChannel().getChannelInfo().getAudioOutputDevice();
			int c = fxSend.getDestChainId();
			SamplerTreeModel m = (SamplerTreeModel)getParent().treeModel;
			chain = m.audioDevices.getSendEffectChainNodeById(d, c);
		}
		
		public SendEffectChainTreeNode
		getEffectChain() { return chain; }
		
		public EffectInstance
		getEffectInstance() {
			if(chain == null) return null;
			return chain.getEffectChain().getEffectInstance(fxSend.getDestChainPos());
		}
		
		public String
		getEffectInstanceString() {
			if(chain == null) return "";
			int i = fxSend.getDestChainPos();
			String s = chain.getEffectChain().getEffectInstance(i).getInfo().getDescription();
			return String.valueOf(i) + " (" + s + ")";
		}
		
		public AudioDeviceModel
		getAudioDevice() {
			int dev = getChannel().getChannelInfo().getAudioOutputDevice();
			if(dev == -1) return null;
			return CC.getSamplerModel().getAudioDeviceById(dev);
		}
		
		@Override
		public boolean
		isLink() { return true; }
		
		@Override
		public TreeNodeBase
		getLink() {
			if(chain == null) return null;
			return chain.getChildAt(fxSend.getDestChainPos());
		}
		
		@Override
		public void
		effectSendAdded(EffectSendsEvent e) { }
	
		@Override
		public void
		effectSendRemoved(EffectSendsEvent e) { }
	
		@Override
		public void
		effectSendChanged(EffectSendsEvent e) {
			if(e.getFxSend().getFxSendId() != getFxSend().getFxSendId()) return;
			firePropertyChange("SamplerTreeModel.update", null, null);
		}
		
		@Override
		public void
		uninstall() {
			getChannel().removeEffectSendsListener(this);
			fxSend = null;
			chain = null;
			
			super.uninstall();
		}
	
		@Override
		public String
		toString() {
			Object o = getEffectChain();
			return o == null ? i18n.getLabel("DestEffectTreeNode.noFx") : o.toString();
		}
	}

	public static class AudioDevicesTreeNode extends AbstractTreeNode<AudioDeviceTreeNode> {
		public
		AudioDevicesTreeNode(SamplerTreeModel treeModel, TreeNodeBase parent) {
			super(treeModel, parent);
		}
		
		public SendEffectChainTreeNode
		getSendEffectChainNodeById(int devId, int chainId) {
			AudioDeviceTreeNode node = getChildById(devId);
			if(node == null) return null;
			return node.getSendEffectChainNodeById(chainId);
		}
		
		/** Gets the number of columns for the corresponding table model. */
		@Override
		public int
		getColumnCount() { return 5; }
		
		/** Gets the number of rows for the corresponding table model. */
		@Override
		public int
		getRowCount() { return getChildCount(); }
		
		/**
		 * Gets the value for the cell at <code>row</code> and
		 * <code>col</code> for the corresponding table model.
		 */
		@Override
		public Object
		getValueAt(int row, int col) {
			AudioDeviceModel m = getChildAt(row).getAudioDevice();
			if(col == 0) return getChildAt(row);
			if(col == 1) return m.getDeviceInfo().getDriverName();
			if(col == 2) return m.getDeviceInfo().getSampleRate();
			if(col == 3) return m.getDeviceInfo().getChannelCount();
			return "";
		}
		
		/** Gets the name of the column for the corresponding table model. */
		@Override
		public String
		getColumnName(int col) {
			if(col == 0) return i18n.getLabel("SamplerTreeModel.tableColumn.dev");
			if(col == 1) return i18n.getLabel("SamplerTreeModel.tableColumn.drv");
			if(col == 2) return i18n.getLabel("SamplerTreeModel.tableColumn.smplrate");
			if(col == 3) return i18n.getLabel("SamplerTreeModel.tableColumn.chns");
			return " ";
		}
	
		@Override
		public String
		toString() { return i18n.getLabel("AudioDevicesTreeNode.toString"); }
	}

	public static class AudioDeviceTreeNode extends StandardTreeNode {
		private AudioDeviceModel audioDevice;
		private SendEffectChainsTreeNode effectChains;
	
		public
		AudioDeviceTreeNode (
			AbstractTreeModel treeModel, TreeNodeBase parent, AudioDeviceModel audioDevice
		) {
			super(treeModel, parent);
			this.audioDevice = audioDevice;
			
			effectChains = new SendEffectChainsTreeNode(treeModel, this, audioDevice);
			addChild(effectChains);
		}
	
		public AudioDeviceModel
		getAudioDevice() { return audioDevice; }
	
		@Override
		public int
		getId() { return audioDevice.getDeviceId(); }
		
		public SendEffectChainsTreeNode
		getSendEffectChainsNode() { return effectChains; }
		
		public SendEffectChainTreeNode
		getSendEffectChainNodeById(int id) {
			return getSendEffectChainsNode().getChildById(id);
		}
		
		@Override
		public void
		uninstall() {
			audioDevice = null;
			effectChains = null;
			super.uninstall();
		}
	
		@Override
		public String
		toString() { return i18n.getLabel("AudioDeviceTreeNode.toString", getId()); }
	}

	public static class SendEffectChainsTreeNode extends StandardTreeNode<SendEffectChainTreeNode> {
		private AudioDeviceModel audioDevice;
	
		public
		SendEffectChainsTreeNode (
			AbstractTreeModel treeModel, TreeNodeBase parent, AudioDeviceModel audioDevice
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
		
		@Override
		public void
		uninstall() {
			audioDevice = null;
			super.uninstall();
		}
	
		@Override
		public String
		toString() { return i18n.getLabel("SendEffectChainsTreeNode.toString"); }
	}

	public static class SendEffectChainTreeNode extends AbstractTreeNode<EffectInstanceTreeNode>
							implements EffectChainListener {
		private EffectChain chain;
	
		public
		SendEffectChainTreeNode (
			AbstractTreeModel treeModel, SendEffectChainsTreeNode parent, EffectChain chain
		) {
			super(treeModel, parent);
			this.chain = chain;
			chain.addEffectChainListener(this);
			updateEffectInstanceList();
		}
		///////
		
		public EffectChain
		getEffectChain() { return chain; }
		
		@Override
		public int
		getId() { return chain.getChainId(); }
	
		/**
		 * Gets the audio device to which the
		 * send effect chain represented by this tree node belongs.
		 */
		public AudioDeviceModel
		getAudioDevice() { return ((SendEffectChainsTreeNode)getParent()).getAudioDevice(); }
		
		/** Gets the number of columns for the corresponding table model. */
		@Override
		public int
		getColumnCount() { return 5; }
		
		/** Gets the number of rows for the corresponding table model. */
		@Override
		public int
		getRowCount() { return getChildCount(); }
		
		/**
		 * Gets the value for the cell at <code>row</code> and
		 * <code>col</code> for the corresponding table model.
		 */
		@Override
		public Object
		getValueAt(int row, int col) {
			EffectInstance e = getChildAt(row).effectInstance;
			if(col == 0) return getChildAt(row);
			if(col == 1) return e.getInfo().getSystem();
			if(col == 2) return e.getInfo().getModule();
			if(col == 3) return e.getInfo().getName();
			return "";
		}
		
		/** Gets the name of the column for the corresponding table model. */
		@Override
		public String
		getColumnName(int col) {
			if(col == 0) return i18n.getLabel("SamplerTreeModel.tableColumn.effect");
			if(col == 1) return i18n.getLabel("SamplerTreeModel.tableColumn.type");
			if(col == 2) return i18n.getLabel("SamplerTreeModel.tableColumn.file");
			if(col == 3) return i18n.getLabel("SamplerTreeModel.tableColumn.id");
			return " ";
		}
		
		@Override
		public int
		getHorizontalAlignment(int column) {
			return column == 1 ? SwingConstants.CENTER : SwingConstants.LEFT;
		}
		
		@Override
		public void
		uninstall() {
			chain.removeEffectChainListener(this);
			chain = null;
			super.uninstall();
		}
	
		@Override
		public String
		toString() { return i18n.getLabel("SendEffectChainTreeNode.toString", chain.getChainId()); }
		
		@Override
		public void
		effectInstanceListChanged(EffectChainEvent e) {
			updateEffectInstanceList();
			treeModel.fireNodeStructureChanged(this);
			
			firePropertyChange("SamplerTreeModel.update", null, null);
			getParent().firePropertyChange("SamplerTreeModel.update", null, null);
		}
		
		private void
		updateEffectInstanceList() {
			removeAndUninstallAllChildren();
			
			for(int i = 0; i < chain.getEffectInstanceCount(); i++) {
				EffectInstance ei = chain.getEffectInstance(i);
				addChild(new EffectInstanceTreeNode(treeModel, this, ei));
			}
		}
	}

	public static class EffectInstanceTreeNode extends AbstractTreeNode implements EffectInstanceListener {
		private EffectInstance effectInstance;
		private EffectParameter[] params;
		
		public
		EffectInstanceTreeNode (
			AbstractTreeModel treeModel, SendEffectChainTreeNode parent, EffectInstance ei
		) {
			super(treeModel, parent);
			effectInstance = ei;
			params = ei.getInfo().getParameters();
			
			effectInstance.addEffectInstanceListener(this);
		}
		
		@Override
		public void
		uninstall() {
			effectInstance.removeEffectInstanceListener(this);
			effectInstance = null;
			params = null;
			super.uninstall();
		}
	
		@Override
		public SendEffectChainTreeNode
		getParent() { return (SendEffectChainTreeNode)super.getParent(); }
		
		public int
		getInstanceId() { return effectInstance.getInstanceId(); }
		
		/** Gets the number of columns for the corresponding table model. */
		@Override
		public int
		getColumnCount() { return 3; }
		
		/** Gets the number of rows for the corresponding table model. */
		@Override
		public int
		getRowCount() { return params.length; }
		
		/**
		 * Gets the value for the cell at <code>row</code> and
		 * <code>col</code> for the corresponding table model.
		 */
		@Override
		public Object
		getValueAt(int row, int col) {
			if(col == 0) return effectInstance.getInfo().getParameter(row);
			if(col == 1) return effectInstance.getInfo().getParameter(row).getValue();
			return "";
		}
		
		/** Gets the name of the column for the corresponding table model. */
		@Override
		public String
		getColumnName(int col) {
			if(col == 0) return i18n.getLabel("SamplerTreeModel.tableColumn.name");
			if(col == 1) return i18n.getLabel("SamplerTreeModel.tableColumn.value");
			return " ";
		}
		
		@Override
		public int
		getHorizontalAlignment(int column) {
			return column == 1 ? SwingConstants.RIGHT : SwingConstants.LEFT;
		}
		
		@Override
		public void
		effectInstanceChanged(EffectInstanceEvent e) {
			firePropertyChange("SamplerTreeModel.update", null, null);
		}
	
		@Override
		public String
		toString() { return effectInstance.getInfo().getDescription(); }
	}

	public static class InternalEffectsTreeNode extends AbstractTreeNode<InternalEffectTreeNode> {
		public
		InternalEffectsTreeNode(SamplerTreeModel treeModel, TreeNodeBase parent) {
			super(treeModel, parent);
		}
		
		/** Gets the number of columns for the corresponding table model. */
		@Override
		public int
		getColumnCount() { return 5; }
		
		/** Gets the number of rows for the corresponding table model. */
		@Override
		public int
		getRowCount() { return getChildCount(); }
		
		/**
		 * Gets the value for the cell at <code>row</code> and
		 * <code>col</code> for the corresponding table model.
		 */
		@Override
		public Object
		getValueAt(int row, int col) {
			Effect e = getChildAt(row).effect;
			if(col == 0) return getChildAt(row);
			if(col == 1) return e.getSystem();
			if(col == 2) return e.getModule();
			if(col == 3) return e.getName();
			return "";
		}
		
		/** Gets the name of the column for the corresponding table model. */
		@Override
		public String
		getColumnName(int col) {
			if(col == 0) return i18n.getLabel("SamplerTreeModel.tableColumn.effect");
			if(col == 1) return i18n.getLabel("SamplerTreeModel.tableColumn.type");
			if(col == 2) return i18n.getLabel("SamplerTreeModel.tableColumn.file");
			if(col == 3) return i18n.getLabel("SamplerTreeModel.tableColumn.id");
			return " ";
		}
		
		@Override
		public int
		getHorizontalAlignment(int column) {
			return column == 1 ? SwingConstants.CENTER : SwingConstants.LEFT;
		}
	
		@Override
		public String
		toString() { return i18n.getLabel("InternalEffectsTreeNode.toString"); }
	}

	public static class InternalEffectTreeNode extends AbstractTreeLeaf {
		private final Effect effect;
		
		public
		InternalEffectTreeNode(TreeNodeBase parent, Effect effect) {
			super(parent);
			this.effect = effect;
		}
	
		@Override
		public String
		toString() { return effect.getDescription(); }
	}

}
