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

package org.jsampler.view.swing;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.KeyStroke;

import org.jsampler.DefaultOrchestraListModel;
import org.jsampler.OrchestraModel;

import javax.swing.table.TableCellRenderer;

import static javax.swing.KeyStroke.*;


/**
 * A table for representing orchestras.
 * @author Grigor Iliev
 */
public class OrchestraTable extends JTable {
	
	/** Creates a new instance of <code>OrchestraTable</code>. */
	public
	OrchestraTable() {
		this(new OrchestraTableModel(new DefaultOrchestraListModel()));
	}
	
	/**
	 * Creates a new instance of <code>OrchestraTable</code> using the specified data model.
	 * @param dataModel The data model to be represented by this table.
	 */
	public
	OrchestraTable(OrchestraTableModel dataModel) {
		super(dataModel);
		
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setFillsViewportHeight(true);
		installKeyboardListeners();
		
		addMouseListener(new MouseAdapter() {
			public void
			mouseClicked(MouseEvent e) {
				if(e.getButton() != e.BUTTON1) return;
				int r = rowAtPoint(e.getPoint());
				if(r == -1) {
					clearSelection();
					return;
				}
			}
		});
	}
	
	private void
	installKeyboardListeners() {
		KeyStroke k = getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		getInputMap(JComponent.WHEN_FOCUSED).put(k, Actions.CLEAR_SELECTION);
		getActionMap().put(Actions.CLEAR_SELECTION, new Actions(Actions.CLEAR_SELECTION));
		
		k = getKeyStroke(KeyEvent.VK_UP, KeyEvent.ALT_MASK | KeyEvent.SHIFT_MASK);
		getInputMap(JComponent.WHEN_FOCUSED).put(k, Actions.MOVE_ON_TOP);
		getActionMap().put(Actions.MOVE_ON_TOP, new Actions(Actions.MOVE_ON_TOP));
		
		k = getKeyStroke(KeyEvent.VK_UP, KeyEvent.ALT_MASK);
		getInputMap(JComponent.WHEN_FOCUSED).put(k, Actions.MOVE_UP);
		getActionMap().put(Actions.MOVE_UP, new Actions(Actions.MOVE_UP));
		
		k = getKeyStroke(KeyEvent.VK_DOWN, KeyEvent.ALT_MASK);
		getInputMap(JComponent.WHEN_FOCUSED).put(k, Actions.MOVE_DOWN);
		getActionMap().put(Actions.MOVE_DOWN, new Actions(Actions.MOVE_DOWN));
		
		k = getKeyStroke(KeyEvent.VK_DOWN, KeyEvent.ALT_MASK | KeyEvent.SHIFT_MASK);
		getInputMap(JComponent.WHEN_FOCUSED).put(k, Actions.MOVE_AT_BOTTOM);
		getActionMap().put(Actions.MOVE_AT_BOTTOM, new Actions(Actions.MOVE_AT_BOTTOM));
	}
	
	/**
	 * Gets the <code>OrchestraTableModel</code> that
	 * provides the data displayed by this <code>OrchestraTable</code>.
	 * @return The <code>OrchestraTableModel</code> that
	 * provides the data displayed by this <code>OrchestraTable</code>.
	 */
	public OrchestraTableModel
	getModel() { return (OrchestraTableModel) super.getModel(); }
	
	/**
	 * Sets the data model for this table to <code>dataModel</code>.
	 * @param dataModel The new data source for this table.
	 */
	public void
	setModel(OrchestraTableModel dataModel) {
		super.setModel(dataModel);
	}
	
	/**
	 * Gets the selected orchestra.
	 * @return The selected orchestra, or <code>null</code> if no orchestra is selected.
	 */
	public OrchestraModel
	getSelectedOrchestra() {
		int i = getSelectedRow();
		if(i == -1) return null;
		return getModel().getOrchestraListModel().getOrchestra(i);
	}
	
	/**
	 * Selects the specified orchestra. If <code>orchestra</code> is
	 * <code>null</code> or is not in the table the current selection is cleared.
	 * @param orchestra The orchestra to select.
	 */
	public void
	setSelectedOrchestra(OrchestraModel orchestra) {
		int i = getModel().getOrchestraListModel().getOrchestraIndex(orchestra);
		if(i < 0) {
			clearSelection();
			return;
		}
		
		setRowSelectionInterval(i, i);
	}
	
	/**
	 * Returns an appropriate renderer for the cell specified by
	 * <code>row</code> and <code>column</code>.
	 * @param row The row of the cell to render, where 0 is the first row.
	 * @param column The column of the cell to render, where 0 is the first column.
	 */
	public TableCellRenderer
	getCellRenderer(int row, int column) {
		TableCellRenderer r = super.getCellRenderer(row, column);
		if(r instanceof JComponent) {
			String s;
			s = getModel().getOrchestraListModel().getOrchestra(row).getDescription();
			if(s != null && s.length() == 0) s = null;
			((JComponent)r).setToolTipText(s);
		}
		
		return r;
	}
	
	private class Actions extends AbstractAction {
		private static final String CLEAR_SELECTION = "clearSelection";
		private static final String MOVE_ON_TOP = "moveOrchestraOnTop";
		private static final String MOVE_UP = "moveOrchestraUp";
		private static final String MOVE_DOWN = "moveOrchestraDown";
		private static final String MOVE_AT_BOTTOM = "moveOrchestraAtBottom";
		
		Actions(String name) { super(name); }
		
		public void
		actionPerformed(ActionEvent e) {
			String key = getValue(Action.NAME).toString();
			
			if(key == CLEAR_SELECTION) {
				clearSelection();
			} else if(key == MOVE_ON_TOP) {
				OrchestraModel om = getSelectedOrchestra();
				getModel().getOrchestraListModel().moveOrchestraOnTop(om);
				setSelectedOrchestra(om);
			} else if(key == MOVE_UP) {
				OrchestraModel om = getSelectedOrchestra();
				getModel().getOrchestraListModel().moveOrchestraUp(om);
				setSelectedOrchestra(om);
			} else if(key == MOVE_DOWN) {
				OrchestraModel om = getSelectedOrchestra();
				getModel().getOrchestraListModel().moveOrchestraDown(om);
				setSelectedOrchestra(om);
			} else if(key == MOVE_AT_BOTTOM) {
				OrchestraModel om = getSelectedOrchestra();
				getModel().getOrchestraListModel().moveOrchestraAtBottom(om);
				setSelectedOrchestra(om);
			}
		}
	}
}
