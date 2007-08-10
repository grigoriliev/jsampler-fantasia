/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2007 Grigor Iliev <grigor@grigoriliev.com>
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

import org.jsampler.CC;
import org.jsampler.MidiInstrumentMap;
import org.jsampler.MidiInstrumentMapList;
import org.jsampler.SamplerModel;

import org.jsampler.event.ListEvent;
import org.jsampler.event.ListListener;
import org.jsampler.event.MidiInstrumentMapEvent;
import org.jsampler.event.MidiInstrumentMapListener;

import static org.jsampler.JSI18n.i18n;

/**
 * A tabular data model for representing MIDI instrument maps.
 * @author Grigor Iliev
 */
public class MidiMapTableModel extends AbstractTableModel {
	
	/**
	 * Creates a new instance of <code>MidiMapTableModel</code>.
	 */
	public
	MidiMapTableModel() {
		SamplerModel sm = CC.getSamplerModel();
		
		for(int i = 0; i < sm.getMidiInstrumentMapCount(); i++) {
			sm.getMidiInstrumentMap(i).addMidiInstrumentMapListener(getHandler());
		}
		
		sm.addMidiInstrumentMapListListener(getHandler());
		
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
	getRowCount() { return CC.getSamplerModel().getMidiInstrumentMapCount(); }
	
	/**
	 * Gets the name of the column at <code>columnIndex</code>.
	 * @return The name of the column at <code>columnIndex</code>.
	 */
	public String
	getColumnName(int col) { return i18n.getLabel("MidiMapTableModel.title"); }
	
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
		return CC.getSamplerModel().getMidiInstrumentMap(row);
	}
	
	/**
	 * Sets the value in the cell at <code>col</code>
	 * and <code>row</code> to <code>value</code>.
	 */
	public void
	setValueAt(Object value, int row, int col) {
		
		fireTableCellUpdated(row,  col);
	}
	
	/**
	 * Returns <code>true</code> if the cell at
	 * <code>row</code> and <code>col</code> is editable.
	 */
	public boolean
	isCellEditable(int row, int col) { return false; }
	
	
	private final Handler eventHandler = new Handler();
	
	private Handler
	getHandler() { return eventHandler; }
	
	private class Handler implements ListListener<MidiInstrumentMap>, MidiInstrumentMapListener {
		/** Invoked when an orchestra is added to the orchestra list. */
		public void
		entryAdded(ListEvent<MidiInstrumentMap> e) {
			e.getEntry().addMidiInstrumentMapListener(getHandler());
			fireTableDataChanged();
		}
	
		/** Invoked when an orchestra is removed from the orchestra list. */
		public void
		entryRemoved(ListEvent<MidiInstrumentMap> e) {
			e.getEntry().removeMidiInstrumentMapListener(getHandler());
			fireTableDataChanged();
		}
		
		public void
		nameChanged(MidiInstrumentMapEvent e) {
			MidiInstrumentMap m = (MidiInstrumentMap)e.getSource();
			int idx = CC.getSamplerModel().getMidiInstrumentMapIndex(m);
			fireTableRowsUpdated(idx, idx);
		}
		
		public void
		instrumentAdded(MidiInstrumentMapEvent e) { }
		
		public void
		instrumentRemoved(MidiInstrumentMapEvent e) { }
	}
}
