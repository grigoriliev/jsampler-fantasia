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
package org.jsampler.view.swing;

import org.jsampler.view.SamplerBrowserView;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import org.jsampler.CC;
import org.jsampler.view.swing.SamplerTreeModel.TreeNodeBase;

/**
 *
 * @author Grigor Iliev
 */
public class AbstractSamplerTree extends JTree {
	private SamplerBrowserView view = null;
	
	
	/**
	 * Creates a new instance of <code>AbstractSamplerTree</code>
	 * using the specified tree model.
	 * 
	 * @param model The model to be used by this tree.
	 */
	public
	AbstractSamplerTree(SamplerTreeModel model) {
		setModel(model);
		setView(CC.getViewConfig().getSamplerBrowserView());
		//setRootVisible(false);
		
		setSelectedNode((TreeNodeBase)getModel().getRoot());
	}
	
	@Override
	public SamplerTreeModel
	getModel() { return (SamplerTreeModel) super.getModel(); }
	
	/** Sets the view to be used for retrieving UI information. */
	public void
	setView(SamplerBrowserView view) {
		this.view = view;
	}
	
	/** Gets the view used to retrieve UI information. */
	public SamplerBrowserView<Icon>
	getView() { return view; }
	
	public void
	setSelectedNode(TreeNodeBase node) {
		if(node == null || node.isLeaf()) return;
		Object[] objs = getModel().getPathToRoot(node);
		setSelectionPath(new TreePath(objs));
	}
}
