/*
 *   JSampler - a front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2023 Grigor Iliev <grigor@grigoriliev.com>
 *
 *   This file is part of JSampler.
 *
 *   JSampler is free software: you can redistribute it and/or modify it under
 *   the terms of the GNU General Public License as published by the Free
 *   Software Foundation, either version 3 of the License, or (at your option)
 *   any later version.
 *
 *   JSampler is distributed in the hope that it will be useful, but WITHOUT
 *   ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *   FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 *   more details.
 *
 *   You should have received a copy of the GNU General Public License along
 *   with JSampler. If not, see <https://www.gnu.org/licenses/>.
 */

package org.jsampler.view.swing;

import java.awt.Component;

import java.awt.event.MouseEvent;

import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;

import javax.swing.table.TableCellEditor;


/**
 * This method implements a cell editor for numbers, using a spinner component.
 * @author Grigor Iliev
 */
public class NumberCellEditor extends AbstractCellEditor implements TableCellEditor {
	private final JSpinner editor = new JSpinner();
	private SpinnerNumberModel spinnerModel;
	
	/** Creates a new instance of <code>NumberCellEditor</code>. */
	public
	NumberCellEditor() {
		editor.setBorder(BorderFactory.createEmptyBorder());
		spinnerModel = (SpinnerNumberModel)editor.getModel();
	}
	
	public boolean
	isCellEditable(EventObject anEvent) {
		if(anEvent instanceof MouseEvent) {
			return ((MouseEvent)anEvent).getClickCount() > 1;
		}
		
		return false;
	}
	
	/**
	 * Gets the value contained in the editor.
	 * @return Te value contained in the editor.
	 */
	public Object
	getCellEditorValue() { return editor.getValue(); }
	
	public Component
	getTableCellEditorComponent (
		JTable table,
		Object value,
		boolean isSelected,
		int row,
		int column
	) {
		editor.setValue(value != null ? value : getModel().getMinimum());
		return editor;
	}
	
	/**
	 * Gets the <code>SpinnerNumberModel</code> used by this editor.
	 * @return The <code>SpinnerNumberModel</code> used by this editor.
	 */
	public SpinnerNumberModel
	getModel() { return spinnerModel; }
	
	/**
	 * Sets the <code>SpinnerNumberModel</code> to be used by this editor.
	 * @param model The <code>SpinnerNumberModel</code> to be used by this editor.
	 */
	public void
	setModel(SpinnerNumberModel model) {
		spinnerModel = model;
		editor.setModel(spinnerModel);
	}
	
	/**
	 * Sets the minimum value allowed.
	 * @param minimum The new minimum value.
	 */
	public void
	setMinimum(Comparable minimum) { getModel().setMinimum(minimum); }
	
	/**
	 * Sets the maximum value allowed.
	 * @param maximum The new maximum value.
	 */
	public void
	setMaximum(Comparable maximum) { getModel().setMaximum(maximum); }
}
