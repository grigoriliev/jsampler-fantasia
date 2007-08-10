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

package org.jsampler.view.std;

import java.awt.BorderLayout;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jsampler.DefaultOrchestraModel;
import org.jsampler.Instrument;
import org.jsampler.OrchestraModel;

import org.jsampler.view.InstrumentTable;
import org.jsampler.view.InstrumentTableModel;

import static org.jsampler.view.std.StdI18n.i18n;

/**
 *
 * @author Grigor Iliev
 */
public class JSOrchestraPane extends JPanel {
	protected final InstrumentTable instrumentTable;
	
	protected final Action actionAddInstrument = new AddInstrumentAction();
	protected final Action actionEditInstrument = new EditInstrumentAction();
	protected final Action actionDeleteInstrument = new DeleteInstrumentAction();
	protected final Action actionInstrumentUp = new InstrumentUpAction();
	protected final Action actionInstrumentDown = new InstrumentDownAction();
	
	private OrchestraModel orchestra;
	
	/** Creates a new instance of <code>JSOrchestraPane</code> */
	public
	JSOrchestraPane() {
		this(null);
	}
	
	/** Creates a new instance of <code>JSOrchestraPane</code> */
	public
	JSOrchestraPane(OrchestraModel orchestra) {
		instrumentTable = new InstrumentTable();
		setOrchestra(orchestra);
		
		setLayout(new BorderLayout());
		JScrollPane sp = new JScrollPane(instrumentTable);
		add(sp);
		
		installListeneres();
	}
	
	private void
	installListeneres() {
		InstrumentSelectionHandler l = new InstrumentSelectionHandler();
		instrumentTable.getSelectionModel().addListSelectionListener(l);
		
		instrumentTable.addMouseListener(new MouseAdapter() {
			public void
			mouseClicked(MouseEvent e) {
				if(e.getClickCount() < 2) return;
				
				if(instrumentTable.getSelectedInstrument() == null) return;
				editInstrument(instrumentTable.getSelectedInstrument());
			}
		});
	}
	
	public void
	setOrchestra(OrchestraModel orchestra) {
		this.orchestra = orchestra;
		if(orchestra == null) {
			orchestra = new DefaultOrchestraModel();
			actionAddInstrument.setEnabled(false);
		} else {
			actionAddInstrument.setEnabled(true);
		}
		instrumentTable.getModel().setOrchestraModel(orchestra);
	}
	
	public Instrument
	getSelectedInstrument() { return instrumentTable.getSelectedInstrument(); }
	
	/**
	 * Invoked when the user initiates the creation of new instrument.
	 * @return The instrument to add
	 * or <code>null</code> if the user cancelled the task.
	 */
	public Instrument
	createInstrument() {
		JSAddOrEditInstrumentDlg dlg = new JSAddOrEditInstrumentDlg();
		dlg.setVisible(true);
		
		if(dlg.isCancelled()) return null;
		return dlg.getInstrument();
	}
	
	public void
	editInstrument(Instrument instr) {
		JSAddOrEditInstrumentDlg dlg = new JSAddOrEditInstrumentDlg(instr);
		dlg.setVisible(true);
	}
	
	private class InstrumentSelectionHandler implements ListSelectionListener {
		public void
		valueChanged(ListSelectionEvent e) {
			if(e.getValueIsAdjusting()) return;
			
			if(instrumentTable.getSelectedInstrument() == null) {
				actionEditInstrument.setEnabled(false);
				actionDeleteInstrument.setEnabled(false);
				actionInstrumentUp.setEnabled(false);
				actionInstrumentDown.setEnabled(false);
				return;
			}
			
			actionEditInstrument.setEnabled(true);
			actionDeleteInstrument.setEnabled(true);
			
			int idx = instrumentTable.getSelectedRow();
			actionInstrumentUp.setEnabled(idx != 0);
			actionInstrumentDown.setEnabled(idx != instrumentTable.getRowCount() - 1);
		}
	}
	
	private class AddInstrumentAction extends AbstractAction {
		AddInstrumentAction() {
			super("");
			
			String s = i18n.getLabel("JSOrchestraPane.ttAddInstrument");
			putValue(SHORT_DESCRIPTION, s);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			Instrument instr = createInstrument();
			if(instr == null) return;
			orchestra.addInstrument(instr);
		}
	}
	
	private class EditInstrumentAction extends AbstractAction {
		EditInstrumentAction() {
			super("");
			
			String s = i18n.getLabel("JSOrchestraPane.ttEditInstrument");
			putValue(SHORT_DESCRIPTION, s);
			
			setEnabled(false);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			editInstrument(instrumentTable.getSelectedInstrument());
		}
	}
	
	private class DeleteInstrumentAction extends AbstractAction {
		DeleteInstrumentAction() {
			super("");
			
			String s = i18n.getLabel("JSOrchestraPane.ttDeleteInstrument");
			putValue(SHORT_DESCRIPTION, s);
			
			setEnabled(false);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			Instrument instr = instrumentTable.getSelectedInstrument();
			if(instr == null) return;
			orchestra.removeInstrument(instr);
		}
	}
	
	private class InstrumentUpAction extends AbstractAction {
		InstrumentUpAction() {
			super("");
			
			String s = i18n.getLabel("JSOrchestraPane.ttInstrumentUp");
			putValue(SHORT_DESCRIPTION, s);
			
			setEnabled(false);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			Instrument instr = instrumentTable.getSelectedInstrument();
			instrumentTable.getModel().getOrchestraModel().moveInstrumentUp(instr);
			instrumentTable.setSelectedInstrument(instr);
		}
	}
	
	private class InstrumentDownAction extends AbstractAction {
		InstrumentDownAction() {
			super("");
			
			String s = i18n.getLabel("JSOrchestraPane.ttInstrumentDown");
			putValue(SHORT_DESCRIPTION, s);
			
			setEnabled(false);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			Instrument instr = instrumentTable.getSelectedInstrument();
			instrumentTable.getModel().getOrchestraModel().moveInstrumentDown(instr);
			instrumentTable.setSelectedInstrument(instr);
		}
	}
}
