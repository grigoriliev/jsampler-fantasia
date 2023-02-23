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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.table.AbstractTableModel;
import org.jsampler.AudioDeviceModel;
import org.jsampler.event.ListEvent;
import org.jsampler.event.ListListener;

import static org.jsampler.view.swing.SamplerTreeModel.*;

/**
 *
 * @author Grigor Iliev
 */
public class SamplerTableModel extends AbstractTableModel {
	private TreeNodeBase node;
	
	
	/** Creates a new instance of <code>SamplerTableModel</code>. */
	public
	SamplerTableModel() {
		this(null);
	}
	
	/** Creates a new instance of <code>SamplerTableModel</code>. */
	public
	SamplerTableModel(TreeNodeBase node) {
		this.node = node;
		
	}
	
	public TreeNodeBase
	getNode() { return node; }
	
	public void
	setNode(TreeNodeBase node) {
		if(this.node != null) removeListeners(this.node);
		this.node = node;
		if(node != null) addListeners(node);
		fireTableStructureChanged();
		fireTableDataChanged();
	}
	
	private void
	addListeners(TreeNodeBase node) {
		node.addPropertyChangeListener(getHandler());
	}
	
	private void
	removeListeners(TreeNodeBase node) {
		node.addPropertyChangeListener(getHandler());
	}
	
	/**
	 * Gets the number of columns in the model.
	 * @return The number of columns in the model.
	 */
	@Override
	public int
	getColumnCount() { return node == null ? 1 : node.getColumnCount(); }
	
	/**
	 * Gets the name of the column at <code>columnIndex</code>.
	 * @return The name of the column at <code>columnIndex</code>.
	 */
	@Override
	public String
	getColumnName(int col) { return node == null ? " " : node.getColumnName(col); }
	
	/**
	 * Gets the number of rows in the model.
	 * @return The number of rows in the model.
	 */
	@Override
	public int
	getRowCount() { return node == null ? 0 : node.getRowCount(); }
	
	/**
	 * Gets the value for the cell at <code>columnIndex</code> and
	 * <code>rowIndex</code>.
	 * @param row The row whose value is to be queried.
	 * @param col The column whose value is to be queried.
	 * @return The value for the cell at <code>columnIndex</code> and
	 * <code>rowIndex</code>.
	 */
	@Override
	public Object
	getValueAt(int row, int col) {
		return node == null ? null : node.getValueAt(row, col);
	}
	
	public TreeNodeBase
	getNodeAt(int idx) {
		if(node == null) return null;
		if(idx >= node.getChildCount()) return null;
		return node.getChildAt(idx);
	}
	
	
	private final EventHandler eventHandler = new EventHandler();
	
	private EventHandler
	getHandler() { return eventHandler; }
	
	private class EventHandler implements PropertyChangeListener {
		@Override
		public void
		propertyChange(PropertyChangeEvent e) {
			if(e.getPropertyName() == "SamplerTreeModel.update") {
				fireTableDataChanged();
			}
		}
	}
	
	AudioDeviceListListener audioDeviceListListener = new AudioDeviceListListener();
	
	private class AudioDeviceListListener implements ListListener<AudioDeviceModel> {
		/** Invoked when a new entry is added to a list. */
		public void
		entryAdded(ListEvent<AudioDeviceModel> e) { fireTableDataChanged(); }
	
		/** Invoked when an entry is removed from a list. */
		public void
		entryRemoved(ListEvent<AudioDeviceModel> e) { fireTableDataChanged(); }
	}
}
