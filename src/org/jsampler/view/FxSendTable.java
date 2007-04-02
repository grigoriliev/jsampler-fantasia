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

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import javax.swing.table.TableCellEditor;

import org.jsampler.SamplerChannelModel;

import org.jsampler.event.EffectSendsEvent;
import org.jsampler.event.EffectSendsListener;

import org.linuxsampler.lscp.FxSend;


/**
 * A table for representing effect sends.
 * @author Grigor Iliev
 */
public class FxSendTable extends JTable {
	private final DefaultCellEditor defaultEditor;
	private final JTextField tfEditor = new JTextField();
	
	/**
	 * Creates a new instance of <code>FxSendTable</code>.
	 * @param channelModel The <code>SamplerChannelModel</code>, which
	 * effect sends should be represented by this table.
	 */
	public
	FxSendTable(SamplerChannelModel channelModel) {
		setModel(new FxSendTableModel(channelModel));
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		channelModel.addEffectSendsListener(getHandler());
		
		tfEditor.setBorder(BorderFactory.createEmptyBorder());
		defaultEditor = new DefaultCellEditor(tfEditor);
	}
	
	/**
	 * Gets the selected effect send.
	 * @return The selected effect send, or 
	 * <code>null</code> if no effect send is selected.
	 */
	public FxSend
	getSelectedFxSend() {
		int i = getSelectedRow();
		if(i == -1) return null;
		
		return ((FxSendTableModel)getModel()).getFxSend(i);
	}
	
	/**
	 * Sets the selected effect send.
	 * @param fxSend The effect send to select.
	 */
	public void
	setSelectedFxSend(FxSend fxSend) {
		int i = ((FxSendTableModel)getModel()).getFxSendPosition(fxSend);
		if(i != -1) getSelectionModel().setSelectionInterval(i, i);
	}
	
	public void
	editSelectedFxSend() {
		FxSend fxSend = getSelectedFxSend();
		if(fxSend == null) return;
		editCellAt(((FxSendTableModel)getModel()).getFxSendPosition(fxSend), 0);
		
		Component c = defaultEditor.getComponent();
		c.requestFocus();
		if(c instanceof JTextField) ((JTextField)c).selectAll();
	}
	
	public TableCellEditor
	getCellEditor(int row, int column) { return defaultEditor; }
	
	private final Handler eventHandler = new Handler();
	
	private Handler
	getHandler() { return eventHandler; }
	
	private class Handler implements EffectSendsListener {
		/** Invoked when a new effect send is added to a sampler channel. */
		public void
		effectSendAdded(EffectSendsEvent e) {
			setSelectedFxSend(e.getFxSend());
		}
		
		/** Invoked when an effect send is removed from a sampler channel. */
		public void 
		effectSendRemoved(EffectSendsEvent e) { }
		
		/** Invoked when an effect send's setting are changed. */
		public void
		effectSendChanged(EffectSendsEvent e) { }
	}
}
