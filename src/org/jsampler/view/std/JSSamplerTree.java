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
package org.jsampler.view.std;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.TreeSelectionModel;

import org.jsampler.CC;
import org.jsampler.view.AbstractSamplerTree;
import org.jsampler.view.SamplerTreeModel;

import static org.jsampler.view.SamplerTreeModel.*;
import static org.jsampler.view.std.StdI18n.i18n;

/**
 *
 * @author Grigor Iliev
 */
public class JSSamplerTree extends AbstractSamplerTree {
	/**
	 * Creates a new instance of <code>JSSamplerTree</code>.
	 */
	public
	JSSamplerTree(SamplerTreeModel model) {
		super(model);
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		
		addMouseListener(new MouseAdapter() {
			public void
			mousePressed(MouseEvent e) {
				if(e.getButton() != e.BUTTON3) return;
				setSelectionPath(getClosestPathForLocation(e.getX(), e.getY()));
			}
		});
		
		ContextMenu contextMenu = new ContextMenu();
		addMouseListener(contextMenu);
	}
	
	class ContextMenu extends MouseAdapter {
		private final JPopupMenu aodMenu = new JPopupMenu();
		private final JPopupMenu secMenu = new JPopupMenu();
		private final JPopupMenu eiMenu = new JPopupMenu();
		
		
		ContextMenu() {
			String s = i18n.getMenuLabel("JSSamplerTree.cm.aodMenu.addChain");
			JMenuItem mi = new JMenuItem(s);
			aodMenu.add(mi);
			mi.addActionListener(new ActionListener() {
				public void
				actionPerformed(ActionEvent e) {
					AudioDeviceTreeNode node = getAudioDeviceTreeNode();
					if(node == null) return;
					node.getAudioDevice().addBackendSendEffectChain();
				}
			});
			
			// Send Effect Chain menu
			
			s = i18n.getMenuLabel("JSSamplerTree.cm.secMenu.remove");
			mi = new JMenuItem(s);
			secMenu.add(mi);
			mi.addActionListener(new ActionListener() {
				public void
				actionPerformed(ActionEvent e) {
					SendEffectChainTreeNode node = getSendEffectChainTreeNode();
					if(node == null) return;
					node.getAudioDevice().removeBackendSendEffectChain(node.getChainId());
				}
			});
			
			s = i18n.getMenuLabel("JSSamplerTree.cm.secMenu.appendEffectInstance");
			mi = new JMenuItem(s);
			secMenu.add(mi);
			mi.addActionListener(new ActionListener() {
				public void
				actionPerformed(ActionEvent e) {
					SendEffectChainTreeNode node = getSendEffectChainTreeNode();
					if(node == null) return;
					
					JSAddEffectInstancesDlg dlg = new JSAddEffectInstancesDlg();
					dlg.setVisible(true);
					if(dlg.isCancelled()) return;
					
					node.getAudioDevice().addBackendEffectInstances (
						dlg.getSelectedEffects(), node.getChainId(), -1
					);
				}
			});
			
			// Effect Instance menu
			s = i18n.getMenuLabel("JSSamplerTree.cm.eiMenu.remove");
			mi = new JMenuItem(s);
			eiMenu.add(mi);
			mi.addActionListener(new ActionListener() {
				public void
				actionPerformed(ActionEvent e) {
					EffectInstanceTreeNode node = getEffectInstanceTreeNode();
					if(node == null) return;
					
					node.getParent().getAudioDevice().removeBackendEffectInstance (
						node.getParent().getChainId(), node.getInstanceId()
					);
				}
			});
			
			s = i18n.getMenuLabel("JSSamplerTree.cm.eiMenu.insertInstances");
			mi = new JMenuItem(s);
			eiMenu.add(mi);
			mi.addActionListener(new ActionListener() {
				public void
				actionPerformed(ActionEvent e) {
					EffectInstanceTreeNode node = getEffectInstanceTreeNode();
					if(node == null) return;
					
					JSAddEffectInstancesDlg dlg = new JSAddEffectInstancesDlg();
					dlg.setVisible(true);
					if(dlg.isCancelled()) return;
					
					SendEffectChainTreeNode parent = node.getParent();
					int idx = parent.getEffectChain().getIndex(node.getInstanceId());
					
					parent.getAudioDevice().addBackendEffectInstances (
						dlg.getSelectedEffects(), parent.getChainId(), idx
					);
				}
			});
		}
		
		public void
		mousePressed(MouseEvent e) {
			if(e.isPopupTrigger()) show(e);
		}
	
		public void
		mouseReleased(MouseEvent e) {
			if(e.isPopupTrigger()) show(e);
		}
	
		void
		show(MouseEvent e) {
			Object o = getSelectionModel().getSelectionPath().getLastPathComponent();
			if(o == null) return;
			if(o instanceof AudioDeviceTreeNode) {
				aodMenu.show(e.getComponent(), e.getX(), e.getY());
			} else if(o instanceof SendEffectChainTreeNode) {
				secMenu.show(e.getComponent(), e.getX(), e.getY());
			} else if(o instanceof EffectInstanceTreeNode) {
				eiMenu.show(e.getComponent(), e.getX(), e.getY());
			}
			
		}
		
		private AudioDeviceTreeNode
		getAudioDeviceTreeNode() {
			Object o = getSelectionModel().getSelectionPath().getLastPathComponent();
			if(o == null || !(o instanceof AudioDeviceTreeNode)) return null;
			return (AudioDeviceTreeNode)o;
		}
		
		private SendEffectChainTreeNode
		getSendEffectChainTreeNode() {
			Object o = getSelectionModel().getSelectionPath().getLastPathComponent();
			if(o == null || !(o instanceof SendEffectChainTreeNode)) return null;
			return (SendEffectChainTreeNode)o;
		}
		
		private EffectInstanceTreeNode
		getEffectInstanceTreeNode() {
			Object o = getSelectionModel().getSelectionPath().getLastPathComponent();
			if(o == null || !(o instanceof EffectInstanceTreeNode)) return null;
			return (EffectInstanceTreeNode)o;
		}
	}
}
