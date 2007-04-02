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

import org.jsampler.SamplerChannelModel;

import org.jsampler.event.EffectSendsEvent;
import org.jsampler.event.EffectSendsListener;

import org.linuxsampler.lscp.FxSend;


/**
 * A tabular data model for representing effect sends.
 * @author Grigor Iliev
 */
public class FxSendTableModel extends AbstractTableModel {
	private SamplerChannelModel channelModel;
	
	/**
	 * Creates a new instance of <code>FxSendTableModel</code>.
	 * @param channelModel The <code>SamplerChannelModel</code>, which
	 * effect sends should be represented by this table model.
	 * @throws IllegalArgumentException If <code>channelModel</code> is <code>null</code>.
	 */
	public
	FxSendTableModel(SamplerChannelModel channelModel) {
		if(channelModel == null)
			throw new IllegalArgumentException("channelModel should be non-null!");
		
		this.channelModel = channelModel;
		channelModel.addEffectSendsListener(getHandler());
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
	getRowCount() { return channelModel.getFxSendCount(); }
	
	/**
	 * Gets the name of the column at <code>columnIndex</code>.
	 * @return The name of the column at <code>columnIndex</code>.
	 */
	public String
	getColumnName(int col) { return " "; }
	
	/**
	 * Gets the value for the cell at <code>columnIndex</code> and
	 * <code>rowIndex</code>.
	 * @param row The row whose value is to be queried.
	 * @param col The column whose value is to be queried.
	 * @return The value for the cell at <code>columnIndex</code> and
	 * <code>rowIndex</code>.
	 */
	public Object
	getValueAt(int row, int col) { return channelModel.getFxSend(row); }
	
	/**
	 * Sets the value in the cell at <code>col</code>
	 * and <code>row</code> to <code>value</code>.
	 */
	public void
	setValueAt(Object value, int row, int col) {
		if(value.toString().isEmpty()) return;
		
		FxSend fxs = channelModel.getFxSend(row);
		channelModel.setBackendFxSendName(fxs.getFxSendId(), value.toString());
		fxs.setName(value.toString());
		fireTableCellUpdated(row,  col);
	}
	
	/**
	 * Returns <code>true</code> if the cell at
	 * <code>row</code> and <code>col</code> is editable.
	 */
	public boolean
	isCellEditable(int row, int col) {
		if(col == 0) return true;
		return false;
	}
	
	/**
	 * Gets the effect send at the specified position.
	 * @param index The index of the effect send to be returned.
	 * @return The effect send at the specified position.
	 */
	public FxSend
	getFxSend(int index) { return channelModel.getFxSend(index); }
	
	/**
	 * Gets the position of the specified effect send.
	 * @param fxSend The effect send which position should be obtained.
	 * @return The position of the specified effect send, or
	 * <code>-1</code> if the specified effect send is not found in the table.
	 */
	public int
	getFxSendPosition(FxSend fxSend) {
		if(fxSend == null) return -1;
		
		for(int i = 0; i < channelModel.getFxSendCount(); i++) {
			if(channelModel.getFxSend(i).getFxSendId() == fxSend.getFxSendId()) {
				return i;
			}
		}
		
		return -1;
	}
	
	private final Handler eventHandler = new Handler();
	
	private Handler
	getHandler() { return eventHandler; }
	
	private class Handler implements EffectSendsListener {
		/** Invoked when a new effect send is added to a sampler channel. */
		public void
		effectSendAdded(EffectSendsEvent e) { fireTableDataChanged(); }
		
		/** Invoked when an effect send is removed from a sampler channel. */
		public void 
		effectSendRemoved(EffectSendsEvent e) { fireTableDataChanged(); }
		
		/** Invoked when an effect send's setting are changed. */
		public void
		effectSendChanged(EffectSendsEvent e) {
			for(int i = 0; i < getRowCount(); i ++) {
				if(e.getFxSend().equals(getValueAt(i, 0))) {
					fireTableRowsUpdated(i, i);
					return;
				}
			}
		}
	}
}
