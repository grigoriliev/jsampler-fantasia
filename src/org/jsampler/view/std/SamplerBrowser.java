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

import org.jsampler.CC;
import org.jsampler.EffectInstance;
import org.jsampler.view.SamplerTreeModel.*;

import org.linuxsampler.lscp.EffectParameter;

import static org.jsampler.view.std.StdA4n.a4n;
import static org.jsampler.view.std.StdI18n.i18n;

/**
 *
 * @author Grigor Iliev
 */
public class SamplerBrowser {
	
	
	private static class SamplerMenu extends JPopupMenu {
		public
		SamplerMenu() {
			JMenuItem mi = new JMenuItem(a4n.refresh);
			add(mi);
			
			mi = new JMenuItem(a4n.resetSampler);
			add(mi);
		}
	}
	
	private static class AudioDeviceMenu extends JPopupMenu {
		public
		AudioDeviceMenu() {
			String s = i18n.getMenuLabel("SamplerBrowser.action.removeAudioDev");
			JMenuItem mi = new JMenuItem(s);
			add(mi);
			mi.addActionListener(new ActionListener() {
				public void
				actionPerformed(ActionEvent e) {
					AudioDeviceTreeNode node = getAudioDeviceTreeNode();
					if(node == null) return;
					
					int id = node.getAudioDevice().getDeviceId();
					CC.getSamplerModel().removeBackendAudioDevice(id);
				}
			});
		}
		
		private AudioDeviceTreeNode
		getAudioDeviceTreeNode() {
			if(ContextMenu.getCurrentOwner() == null) return null;
			Object o = ContextMenu.getCurrentOwner().getSelectedItem();
			if(o == null || !(o instanceof AudioDeviceTreeNode)) return null;
			return (AudioDeviceTreeNode)o;
		}
	}
	
	private static class SendEffectChainDirMenu extends JPopupMenu {
		public
		SendEffectChainDirMenu() {
			String s = i18n.getMenuLabel("SamplerBrowser.action.addChain");
			JMenuItem mi = new JMenuItem(s);
			add(mi);
			mi.addActionListener(new ActionListener() {
				public void
				actionPerformed(ActionEvent e) {
					SendEffectChainsTreeNode node = getSendEffectChainsTreeNode();
					if(node == null) return;
					node.getAudioDevice().addBackendSendEffectChain();
				}
			});
		}
		
		private SendEffectChainsTreeNode
		getSendEffectChainsTreeNode() {
			if(ContextMenu.getCurrentOwner() == null) return null;
			Object o = ContextMenu.getCurrentOwner().getSelectedItem();
			if(o == null || !(o instanceof SendEffectChainsTreeNode)) return null;
			return (SendEffectChainsTreeNode)o;
		}
	}
	
	private static class SendEffectChainMenu extends JPopupMenu {
		public
		SendEffectChainMenu() {
			String s = i18n.getMenuLabel("SamplerBrowser.action.removeChain");
			JMenuItem mi = new JMenuItem(s);
			add(mi);
			mi.addActionListener(new ActionListener() {
				public void
				actionPerformed(ActionEvent e) {
					SendEffectChainTreeNode node = getSendEffectChainTreeNode();
					if(node == null) return;
					
					node.getAudioDevice().removeBackendSendEffectChain(node.getChainId());
				}
			});
			
			s = i18n.getMenuLabel("SamplerBrowser.action.appendEffectInstance");
			mi = new JMenuItem(s);
			add(mi);
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
		}
		
		private SendEffectChainTreeNode
		getSendEffectChainTreeNode() {
			if(ContextMenu.getCurrentOwner() == null) return null;
			Object o = ContextMenu.getCurrentOwner().getSelectedItem();
			if(o == null || !(o instanceof SendEffectChainTreeNode)) return null;
			return (SendEffectChainTreeNode)o;
		}
	}
	
	private static class EffectInstanceMenu extends JPopupMenu {
		public
		EffectInstanceMenu() {
			String s = i18n.getMenuLabel("SamplerBrowser.action.removeEffectInstance");
			JMenuItem mi = new JMenuItem(s);
			add(mi);
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
			
			s = i18n.getMenuLabel("SamplerBrowser.action.insertEffectInstance");
			mi = new JMenuItem(s);
			add(mi);
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
		
		private EffectInstanceTreeNode
		getEffectInstanceTreeNode() {
			if(ContextMenu.getCurrentOwner() == null) return null;
			Object o = ContextMenu.getCurrentOwner().getSelectedItem();
			if(o == null || !(o instanceof EffectInstanceTreeNode)) return null;
			return (EffectInstanceTreeNode)o;
		}
	}
	
	private static class EffectParameterMenu extends JPopupMenu {
		public
		EffectParameterMenu() {
			String s = i18n.getMenuLabel("SamplerBrowser.action.editEffectPrm");
			JMenuItem mi = new JMenuItem(s);
			add(mi);
			mi.addActionListener(new ActionListener() {
				public void
				actionPerformed(ActionEvent e) { editParameter(); }
			});
		}
		
		private void
		editParameter() {
			EffectParameter prm = getEffectParameter();
			if(prm == null) return;
			
			JSSetParameterDlg dlg = new JSSetParameterDlg(prm);
			
			dlg.setVisible(true);
			if(dlg.isCancelled()) return;
			
			EffectInstance ei;
			ei = CC.getSamplerModel().getEffectInstanceById(prm.getEffectInstanceId());
			if(ei == null) return;
			ei.setBackendParameter(prm.getIndex(), dlg.getNewValue());
		}
		
		private EffectParameter
		getEffectParameter() {
			if(ContextMenu.getCurrentOwner() == null) return null;
			Object o = ContextMenu.getCurrentOwner().getSelectedItem();
			if(o == null || !(o instanceof EffectParameter)) return null;
			return (EffectParameter)o;
		}
	}
	
	public static class ContextMenu extends MouseAdapter {
		private static ContextMenuOwner currentOwner;
		
		private static SamplerMenu samplerMenu = null;
		private static AudioDeviceMenu auDevMenu = null;
		private static SendEffectChainDirMenu chainDirMenu = null;
		private static SendEffectChainMenu chainMenu = null;
		private static EffectInstanceMenu instanceMenu = null;
		private static EffectParameterMenu effectPrmMenu = null;
		
		private final ContextMenuOwner owner;
	
		public
		ContextMenu(ContextMenuOwner owner) {
			this.owner = owner;
		}
		
		public static ContextMenuOwner
		getCurrentOwner() { return currentOwner; }
		
		@Override
		public void
		mousePressed(MouseEvent e) {
			if(e.isPopupTrigger()) show(e);
		}
	
		@Override
		public void
		mouseReleased(MouseEvent e) {
			if(e.isPopupTrigger()) show(e);
		}
	
		void
		show(MouseEvent e) {
			Object o = owner.getSelectedItem();
			if(o == null) return;
			
			currentOwner = owner;
			
			if(o instanceof SamplerTreeNode) {
				if(samplerMenu == null) samplerMenu = new SamplerMenu();
				samplerMenu.show(e.getComponent(), e.getX(), e.getY());
			} else if(o instanceof AudioDeviceTreeNode) {
				if(auDevMenu == null) auDevMenu = new AudioDeviceMenu();
				auDevMenu.show(e.getComponent(), e.getX(), e.getY());
			} else if(o instanceof SendEffectChainsTreeNode) {
				if(chainDirMenu == null) chainDirMenu = new SendEffectChainDirMenu();
				chainDirMenu.show(e.getComponent(), e.getX(), e.getY());
			} else if(o instanceof SendEffectChainTreeNode) {
				if(chainMenu == null) chainMenu = new SendEffectChainMenu();
				chainMenu.show(e.getComponent(), e.getX(), e.getY());
			} else if(o instanceof EffectInstanceTreeNode) {
				if(instanceMenu == null) instanceMenu = new EffectInstanceMenu();
				instanceMenu.show(e.getComponent(), e.getX(), e.getY());
			} else if(o instanceof EffectParameter) {
				if(effectPrmMenu == null) effectPrmMenu = new EffectParameterMenu();
				effectPrmMenu.show(e.getComponent(), e.getX(), e.getY());
			}
			
		}
	}
	
	public static interface ContextMenuOwner {
		public Object getSelectedItem();
	}
}
