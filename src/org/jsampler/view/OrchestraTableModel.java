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

package org.jsampler.view;

import javax.swing.table.AbstractTableModel;

import org.jsampler.OrchestraListModel;
import org.jsampler.OrchestraModel;

import org.jsampler.event.ListEvent;
import org.jsampler.event.ListListener;
import org.jsampler.event.OrchestraAdapter;
import org.jsampler.event.OrchestraEvent;

import static org.jsampler.JSI18n.i18n;


/**
 * A tabular data model for representing orchestras.
 * @author Grigor Iliev
 */
public class OrchestraTableModel extends AbstractTableModel {
	private final OrchestraListModel orchestraListModel;
	
	/**
	 * Creates a new instance of <code>OrchestraTableModel</code>.
	 * @param orchestraListModel The <code>OrchestraListModel</code>,
	 * which this table model should represent.
	 * @throws IllegalArgumentException If <code>orchestraListModel</code>
	 * is <code>null</code>.
	 */
	public
	OrchestraTableModel(OrchestraListModel orchestraListModel) {
		if(orchestraListModel == null) throw new IllegalArgumentException (
			"orchestraListModel should be non-null!"
		);
		
		this.orchestraListModel = orchestraListModel;
		orchestraListModel.addOrchestraListListener(getHandler());
		
		for(int i = 0; i < orchestraListModel.getOrchestraCount(); i++) {
			orchestraListModel.getOrchestra(i).addOrchestraListener(getHandler());
		}
		orchestraListModel.addOrchestraListListener(getHandler());
		
		
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
	getRowCount() { return orchestraListModel.getOrchestraCount(); }
	
	/**
	 * Gets the name of the column at <code>columnIndex</code>.
	 * @return The name of the column at <code>columnIndex</code>.
	 */
	public String
	getColumnName(int col) { return i18n.getLabel("OrchestraTableModel.orchestras"); }
	
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
		return orchestraListModel.getOrchestra(row);
	}
	
	/**
	 * Sets the value in the cell at <code>col</code>
	 * and <code>row</code> to <code>value</code>.
	 */
	public void
	setValueAt(Object value, int row, int col) {
		orchestraListModel.getOrchestra(row).setName(value.toString());
		fireTableCellUpdated(row,  col);
	}
	
	/**
	 * Returns <code>true</code> if the cell at
	 * <code>row</code> and <code>col</code> is editable.
	 */
	public boolean
	isCellEditable(int row, int col) { return false; }
	
	/**
	 * Gets the <code>OrchestraListModel</code>, represented by this table model.
	 */
	public OrchestraListModel
	getOrchestraListModel() { return orchestraListModel; }
	
	private final Handler eventHandler = new Handler();
	
	private Handler
	getHandler() { return eventHandler; }
	
	private class Handler extends OrchestraAdapter implements ListListener<OrchestraModel> {
		/** Invoked when an orchestra is added to the orchestra list. */
		public void
		entryAdded(ListEvent<OrchestraModel> e) {
			e.getEntry().addOrchestraListener(getHandler());
			fireTableDataChanged();
		}
	
		/** Invoked when an orchestra is removed from the orchestra list. */
		public void
		entryRemoved(ListEvent<OrchestraModel> e) {
			e.getEntry().removeOrchestraListener(getHandler());
			fireTableDataChanged();
		}
		
		/** Invoked when the name of orchestra is changed. */
		public void
		nameChanged(OrchestraEvent e) {
			OrchestraModel m = (OrchestraModel)e.getSource();
			int idx = orchestraListModel.getOrchestraIndex(m);
			fireTableRowsUpdated(idx, idx);
		}
	
		/** Invoked when the description of orchestra is changed. */
		public void
		descriptionChanged(OrchestraEvent e) { }
	
	
	}
}
