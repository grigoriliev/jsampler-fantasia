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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.tree.TreeSelectionModel;

import org.jsampler.view.swing.AbstractSamplerTree;
import org.jsampler.view.swing.SamplerTreeModel;

import static org.jsampler.view.swing.SamplerTreeModel.*;

/**
 *
 * @author Grigor Iliev
 */
public class JSSamplerTree extends AbstractSamplerTree implements SamplerBrowser.ContextMenuOwner {
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
		
		addMouseListener(new SamplerBrowser.ContextMenu(this));
	}
	
	@Override
	public Object
	getSelectedItem() {
		return getSelectionModel().getSelectionPath().getLastPathComponent();
	}
	
	@Override
	public Object
	getSelectedParent() { return null; }
}
