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

import org.jsampler.CC;
import org.jsampler.view.SamplerTreeModel.TreeNodeBase;

/**
 *
 * @author Grigor Iliev
 */
public class AbstractSamplerTable extends JSTable<SamplerTableModel> {
	//private final DefaultCellEditor nameEditor;
	
	public
	AbstractSamplerTable() {
		super(new SamplerTableModel());
		setFillsViewportHeight(true);
		getTableHeader().setReorderingAllowed(false);
		
		
	}
	
	public TreeNodeBase
	getNode() { return getModel().getNode(); }
	
	public void
	setNode(TreeNodeBase node) {
		saveColumnWidths();
		getModel().setNode(node);
		setTablePrefix(node != null ? node.getClass().getName() : null);
		loadColumnWidths();
	}
	
	public TreeNodeBase
	getSelectedNode() {
		int idx = getSelectedRow();
		if(idx == -1) return null;
		idx = convertRowIndexToModel(idx);
		return getModel().getNodeAt(idx);
	}
	
	/** Gets the view used to retrieve UI information. */
	public SamplerBrowserView
	getView() { return CC.getViewConfig().getSamplerBrowserView(); }
}
