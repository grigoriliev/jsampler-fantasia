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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;

import javax.swing.table.TableCellRenderer;

import org.jsampler.CC;
import org.jsampler.DefaultOrchestraModel;
import org.jsampler.HF;
import org.jsampler.OrchestraInstrument;

import static javax.swing.KeyStroke.*;


/**
 * A table for representing instruments.
 * @author Grigor Iliev
 */
public class InstrumentTable extends JTable {
	private boolean performingDnD = false;
	
	/** Creates a new instance of <code>InstrumentTable</code>. */
	public
	InstrumentTable() {
		this(new InstrumentTableModel(new DefaultOrchestraModel()));
	}
	
	/**
	 * Creates a new instance of <code>InstrumentTable</code> using the specified data model.
	 * @param dataModel The data model to be represented by this table.
	 */
	public
	InstrumentTable(InstrumentTableModel dataModel) {
		super(dataModel);
		
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//setColumnSelectionAllowed(false);
		//setCellSelectionEnabled(false);
		//setRowSelectionAllowed(false);
		
		addMouseListener(new MouseAdapter() {
			public void
			mouseExited(MouseEvent e) {
				int b1 = e.BUTTON1_DOWN_MASK;
				if((e.getModifiersEx() & b1) != b1) return;
				
				JComponent c = (JComponent)e.getSource();
				TransferHandler handler = c.getTransferHandler();
				handler.exportAsDrag(c, e, TransferHandler.COPY);
				performingDnD = true;
			}
		});
		
		setTransferHandler(new TransferHandler("instrument") {
			public boolean
			canImport(JComponent comp, DataFlavor[] transferFlavors) {
				if(isPerformingDnD()) return false;
				return super.canImport(comp, transferFlavors);
			}
			
			protected void
			exportDone(JComponent source, Transferable data, int action) {
				performingDnD = false;
			}
		});
		
		installKeyboardListeners();
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
	 * Gets the <code>InstrumentTableModel</code> that
	 * provides the data displayed by this <code>InstrumentTable</code>.
	 * @return The <code>InstrumentTableModel</code> that
	 * provides the data displayed by this <code>InstrumentTable</code>.
	 */
	public InstrumentTableModel
	getModel() { return (InstrumentTableModel) super.getModel(); }
	
	/**
	 * Sets the data model for this table to <code>dataModel</code>.
	 * @param dataModel The new data source for this table.
	 */
	public void
	setModel(InstrumentTableModel dataModel) { super.setModel(dataModel); }
	
	/**
	 * Gets the selected instrument.
	 * @return The selected instrument, or <code>null</code> if no instrument is selected.
	 */
	public OrchestraInstrument
	getSelectedInstrument() {
		int i = getSelectedRow();
		if(i == -1) return null;
		return getModel().getOrchestraModel().getInstrument(i);
	}
	
	/**
	 * Selects the specified instrument. If <code>instr</code> is
	 * <code>null</code> or is not in the table the current selection is cleared.
	 * @param instr The instrument to select.
	 */
	public void
	setSelectedInstrument(OrchestraInstrument instr) {
		int i = getModel().getOrchestraModel().getInstrumentIndex(instr);
		if(i < 0) {
			clearSelection();
			return;
		}
		
		setRowSelectionInterval(i, i);
	}
	
	/**
	 * Gets the selected instrument.
	 * @return The selected instrument, or <code>null</code> if no instrument is selected.
	 */
	public String
	getInstrument() {
		int i = getSelectedRow();
		if(i == -1) return null;
		return getModel().getOrchestraModel().getInstrument(i).getDnDString();
	}
	
	/**
	 * Creates new instrument using the specified
	 * drag & drop string representation of the instrument.
	 * 
	 * @param instr The drag & drop string representation of the instrument.
	 * @see OrchestraInstrument#getDnDString
	 */
	public void
	setInstrument(String instr) {
		if(!OrchestraInstrument.isDnDString(instr)) return;
		
		OrchestraInstrument instrument = new OrchestraInstrument();
		try { instrument.setDnDString(instr); }
		catch(Exception x) {
			CC.getLogger().log(Level.INFO, HF.getErrorMessage(x), x);
			return;
		}
		
		int idx;
		idx = getModel().getOrchestraModel().getInstrumentIndex(getSelectedInstrument());
		if(idx < 0) getModel().getOrchestraModel().addInstrument(instrument);
		else getModel().getOrchestraModel().insertInstrument(instrument, idx);
		
		setSelectedInstrument(instrument);
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
			s = getModel().getOrchestraModel().getInstrument(row).getDescription();
			if(s != null && s.length() == 0) s = null;
			((JComponent)r).setToolTipText(s);
		}
		
		return r;
	}
	
	/**
	 * Determines whether drag and drop is initiated from this table and hasn't finished yet.
	 * @return <code>true</code> if drag and drop is performing
	 * at the moment, <code>false</code> otherwise.
	 */
	public boolean
	isPerformingDnD() { return performingDnD; }
	
	private class Actions extends AbstractAction {
		private static final String CLEAR_SELECTION = "clearSelection";
		private static final String MOVE_ON_TOP = "moveInstrumentOnTop";
		private static final String MOVE_UP = "moveInstrumentUp";
		private static final String MOVE_DOWN = "moveInstrumentDown";
		private static final String MOVE_AT_BOTTOM = "moveInstrumentAtBottom";
		
		Actions(String name) { super(name); }
		
		public void
		actionPerformed(ActionEvent e) {
			String key = getValue(Action.NAME).toString();
			
			if(key == CLEAR_SELECTION) {
				clearSelection();
			} else if(key == MOVE_ON_TOP) {
				OrchestraInstrument instr = getSelectedInstrument();
				getModel().getOrchestraModel().moveInstrumentOnTop(instr);
				setSelectedInstrument(instr);
			} else if(key == MOVE_UP) {
				OrchestraInstrument instr = getSelectedInstrument();
				getModel().getOrchestraModel().moveInstrumentUp(instr);
				setSelectedInstrument(instr);
			} else if(key == MOVE_DOWN) {
				OrchestraInstrument instr = getSelectedInstrument();
				getModel().getOrchestraModel().moveInstrumentDown(instr);
				setSelectedInstrument(instr);
			} else if(key == MOVE_AT_BOTTOM) {
				OrchestraInstrument instr = getSelectedInstrument();
				getModel().getOrchestraModel().moveInstrumentAtBottom(instr);
				setSelectedInstrument(instr);
			}
		}
	}
}
