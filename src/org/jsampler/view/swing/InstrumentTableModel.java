/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2006 Grigor Iliev <grigor@grigoriliev.com>
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

import javax.swing.table.AbstractTableModel;

import org.jsampler.DefaultOrchestraModel;
import org.jsampler.OrchestraModel;

import org.jsampler.event.OrchestraEvent;
import org.jsampler.event.OrchestraListener;


/**
 * A tabular data model for representing instruments.
 * @author Grigor Iliev
 */
public class InstrumentTableModel extends AbstractTableModel {
	private OrchestraModel orchestraModel;
	
	/**
	 * Creates a new instance of <code>InstrumentTableModel</code>.
	 */
	public
	InstrumentTableModel() { this(new DefaultOrchestraModel()); }
	
	/**
	 * Creates a new instance of <code>InstrumentTableModel</code>.
	 * @param orchestraModel The <code>OrchestraModel</code>,
	 * which this table model should represent.
	 * @throws IllegalArgumentException If <code>orchestraModel</code> is <code>null</code>.
	 */
	public
	InstrumentTableModel(OrchestraModel orchestraModel) {
		setOrchestraModel(orchestraModel);
	}
	
	/**
	 * Gets the number of columns in the model.
	 * @return The number of columns in the model.
	 */
	public int
	getColumnCount() { return 1; }
	
	/**
	 * Gets the number of rows in the model.
	 * @return The number of rows in the model.
	 */
	public int
	getRowCount() { return orchestraModel.getInstrumentCount(); }
	
	/**
	 * Gets the name of the column at <code>columnIndex</code>.
	 * @return The name of the column at <code>columnIndex</code>.
	 */
	public String
	getColumnName(int col) {
		String s = orchestraModel.getName();
		if(s == null || s.length() == 0) return " ";
		return s;
	}
	
	/**
	 * Gets the value for the cell at <code>columnIndex</code> and
	 * <code>rowIndex</code>.
	 * @param row The row whose value is to be queried.
	 * @param col The column whose value is to be queried.
	 * @return The value for the cell at <code>columnIndex</code> and
	 * <code>rowIndex</code>.
	 */
	public Object
	getValueAt(int row, int col) {
		return orchestraModel.getInstrument(row);
	}
	
	/**
	 * Sets the value in the cell at <code>col</code>
	 * and <code>row</code> to <code>value</code>.
	 */
	public void
	setValueAt(Object value, int row, int col) {
		orchestraModel.getInstrument(row).setName(value.toString());
		fireTableCellUpdated(row,  col);
	}
	
	/**
	 * Returns <code>true</code> if the cell at
	 * <code>row</code> and <code>col</code> is editable.
	 */
	public boolean
	isCellEditable(int row, int col) { return false; }
	
	/**
	 * Gets the <code>OrchestraModel</code>, represented by this table model.
	 */
	public OrchestraModel
	getOrchestraModel() { return orchestraModel; }
	
	/**
	 * Sets the <code>OrchestraModel</code>, represented by this table model.
	 * @param orchestraModel The new <code>OrchestraModel</code>,
	 * represented by this table model.
	 * @throws IllegalArgumentException If <code>orchestraModel</code> is <code>null</code>.
	 */
	public void
	setOrchestraModel(OrchestraModel orchestraModel) {
		if(orchestraModel == null)
			throw new IllegalArgumentException("orchestraModel should be non-null!");
		
		if(getOrchestraModel() != null)
			getOrchestraModel().removeOrchestraListener(getHandler());
		
		this.orchestraModel = orchestraModel;
		orchestraModel.addOrchestraListener(getHandler());
		
		fireTableStructureChanged();
		fireTableDataChanged();
	}
	
	private final Handler eventHandler = new Handler();
	
	private Handler
	getHandler() { return eventHandler; }
	
	private class Handler implements OrchestraListener {
		/** Invoked when the name of orchestra is changed. */
		public void
		nameChanged(OrchestraEvent e) { fireTableStructureChanged(); }
	
		/** Invoked when the description of orchestra is changed. */
		public void
		descriptionChanged(OrchestraEvent e) { fireTableDataChanged(); }
	
		/** Invoked when an instrument is added to the orchestra. */
		public void
		instrumentAdded(OrchestraEvent e) { fireTableDataChanged(); }
	
		/** Invoked when an instrument is removed from the orchestra. */
		public void
		instrumentRemoved(OrchestraEvent e) { fireTableDataChanged(); }
		
		/** Invoked when the settings of an instrument are changed. */
		public void
		instrumentChanged(OrchestraEvent e) { fireTableDataChanged(); }
	}
}
