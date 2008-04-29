/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2008 Grigor Iliev <grigor@grigoriliev.com>
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

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.JTextField;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import javax.swing.table.AbstractTableModel;

import net.sf.juife.event.TaskEvent;
import net.sf.juife.event.TaskListener;

import org.jsampler.CC;
import org.jsampler.task.InstrumentsDb.SetInstrumentFilePath;

import static org.jsampler.JSI18n.i18n;

/**
 * A table for representing the list of lost instrument files in the instruments database.
 * @author Grigor Iliev
 */
public class LostFilesTable extends JTable {
	
	/** Creates a new instance of <code>LostFilesTable</code> */
	public
	LostFilesTable() {
		super(new LostFilesTableModel());
	}
	
	public void
	editSelectedFile() {
		int i = getSelectedRow();
		if(i == -1) return;
		editCellAt(i, 0);
		
		if(getEditorComponent() == null) return;
		
		Component c = getEditorComponent();
		c.requestFocus();
		//if(c instanceof JTextField) ((JTextField)c).selectAll();
	}
}

/**
 * A tabular data model for representing the list
 * of lost instrument files in the instruments database.
 * @author Grigor Iliev
 */
class LostFilesTableModel extends AbstractTableModel {
	
	/**
	 * Creates a new instance of <code>MidiMapTableModel</code>.
	 */
	public
	LostFilesTableModel() {
		CC.getLostFilesModel().addChangeListener(new ChangeListener() {
			public void
			stateChanged(ChangeEvent e) { fireTableDataChanged(); }
		});
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
	getRowCount() { return CC.getLostFilesModel().getLostFileCount(); }
	
	/**
	 * Gets the name of the column at <code>columnIndex</code>.
	 * @return The name of the column at <code>columnIndex</code>.
	 */
	public String
	getColumnName(int col) { return i18n.getLabel("LostFilesTableModel.title"); }
	
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
		return CC.getLostFilesModel().getLostFile(row);
	}
	
	/**
	 * Sets the value in the cell at <code>col</code>
	 * and <code>row</code> to <code>value</code>.
	 */
	public void
	setValueAt(Object value, int row, int col) {
		String oldPath = CC.getLostFilesModel().getLostFile(row);
		String newPath = value.toString();
		if(newPath.equals(oldPath)) return;
		
		final SetInstrumentFilePath t = new SetInstrumentFilePath(oldPath, newPath);
		
		t.addTaskListener(new TaskListener() {
			public void
			taskPerformed(TaskEvent e) {
				CC.getLostFilesModel().update();
			}
		});
		
		CC.getTaskQueue().add(t);
		//fireTableCellUpdated(row,  col);
	}
	
	/**
	 * Returns <code>true</code> if the cell at
	 * <code>row</code> and <code>col</code> is editable.
	 */
	public boolean
	isCellEditable(int row, int col) { return true; }
}
