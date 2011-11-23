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

import java.awt.BorderLayout;
import java.awt.Rectangle;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import net.sf.juife.swing.OkCancelDialog;
import org.jsampler.AudioDeviceModel;
import org.jsampler.view.swing.AbstractTreeModel;
import org.jsampler.view.swing.SHF;
import org.jsampler.view.swing.SamplerTreeModel.*;

import static org.jsampler.view.std.StdI18n.i18n;

/**
 *
 * @author Grigor Iliev
 */
public class JSDestEffectChooser  extends OkCancelDialog implements TreeSelectionListener {
	protected AudioDeviceModel audioDev;
	protected JTree tree;
	
	private EffectInstanceTreeNode selectedNode = null;
	
	public
	JSDestEffectChooser(AudioDeviceModel audioDev) {
		super(SHF.getMainFrame(), i18n.getLabel("JSDestEffectChooser.title"));
		setName("JSDestEffectChooser");
		
		 
		
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		
		tree = new JTree(new DestEffectTreeModel(audioDev));
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		p.add(new JScrollPane(tree));
		p.setPreferredSize(new java.awt.Dimension(500, 300));
		setMainPane(p);
		
		setSavedSize();
		
		setResizable(true);
		
		tree.getSelectionModel().addTreeSelectionListener(this);
		btnOk.setEnabled(false);
	}
	
	public EffectInstanceTreeNode
	getSelectedNode() { return selectedNode; }
	
	@Override
	protected void
	onOk() {
		if(!btnOk.isEnabled()) return;
		
		StdUtils.saveWindowBounds(getName(), getBounds());
		
		setVisible(false);
		setCancelled(false);
	}
	
	@Override
	protected void
	onCancel() { setVisible(false); }
	
	private boolean
	setSavedSize() {
		Rectangle r = StdUtils.getWindowBounds(getName());
		if(r == null) return false;
		
		setBounds(r);
		return true;
	}
		
	public void
	valueChanged(TreeSelectionEvent e) {
		if(e.getNewLeadSelectionPath() == null) {
			btnOk.setEnabled(false);
			return;
		}
			
		TreeNodeBase node = (TreeNodeBase)e.getNewLeadSelectionPath().getLastPathComponent();
		
		boolean b = false;
		if(node instanceof SendEffectChainTreeNode) {
			if(((SendEffectChainTreeNode)node).getChildCount() > 0) {
				selectedNode = ((SendEffectChainTreeNode)node).getChildAt(0);
				b = true;
			}
		} else if(node instanceof EffectInstanceTreeNode) {
			selectedNode = (EffectInstanceTreeNode)node;
			b = true;
		}
		
		btnOk.setEnabled(b);
	}
	
	public static class DestEffectTreeModel extends AbstractTreeModel {
		private AudioDeviceTreeNode root;
		
		public
		DestEffectTreeModel(AudioDeviceModel audioDev) {
			root = new AudioDeviceTreeNode(this, null, audioDev);
		}
	
		@Override
		public Object
		getRoot() { return root; }
	
		@Override
		public void
		valueForPathChanged(TreePath path, Object newValue) { }
	}
}
