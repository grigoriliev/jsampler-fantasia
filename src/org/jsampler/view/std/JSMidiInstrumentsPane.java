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

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import net.sf.juife.Wizard;

import org.jsampler.CC;
import org.jsampler.MidiInstrument;
import org.jsampler.MidiInstrumentMap;

import org.linuxsampler.lscp.MidiInstrumentInfo;

import static org.jsampler.view.std.StdI18n.i18n;

/**
 *
 * @author Grigor Iliev
 */
public class JSMidiInstrumentsPane extends JPanel implements TreeSelectionListener {
	protected final JSMidiInstrumentTree midiInstrumentTree = new JSMidiInstrumentTree();
	
	protected final Action actionAddInstrument = new AddInstrumentAction();
	protected final Action actionEditInstrument = new EditInstrumentAction();
	protected final Action actionRemove = new RemoveAction();
	
	/** Creates a new instance of <code>JSMidiInstrumentsPane</code> */
	public
	JSMidiInstrumentsPane() {
		this(null);
	}
	
	/** Creates a new instance of <code>JSMidiInstrumentsPane</code> */
	public
	JSMidiInstrumentsPane(MidiInstrumentMap map) {
		setMidiInstrumentMap(map);
		
		setLayout(new BorderLayout());
		JScrollPane sp = new JScrollPane(midiInstrumentTree);
		add(sp);
		
		midiInstrumentTree.addTreeSelectionListener(this);
	}
	
	public void
	setMidiInstrumentMap(MidiInstrumentMap map) {
		midiInstrumentTree.setMidiInstrumentMap(map);
		actionAddInstrument.setEnabled(map != null);
	}
	
	public void
	valueChanged(TreeSelectionEvent e) {
		actionRemove.setEnabled(midiInstrumentTree.getSelectionCount() > 0);
		boolean b = midiInstrumentTree.getSelectedInstrument() != null;
		actionEditInstrument.setEnabled(b);
	}
	
	public void
	addInstrument() { }
	
	public void
	editInstrument(MidiInstrument instr) {
		JSEditMidiInstrumentDlg dlg = new JSEditMidiInstrumentDlg(instr.getInfo());
		dlg.setVisible(true);
		
		if(dlg.isCancelled()) return;
		
		MidiInstrumentInfo info = dlg.getInstrument();
		CC.getSamplerModel().mapBackendMidiInstrument (
			info.getMapId(), info.getMidiBank(), info.getMidiProgram(), info
		);
	}
	
	private class AddInstrumentAction extends AbstractAction {
		AddInstrumentAction() {
			super(i18n.getLabel("JSMidiInstrumentsPane.addInstrument"));
			
			String s = "JSMidiInstrumentsPane.addInstrument.tt";
			putValue(SHORT_DESCRIPTION, i18n.getLabel(s));
		}
		
		public void
		actionPerformed(ActionEvent e) {
			addInstrument();
		}
	}
	
	private class EditInstrumentAction extends AbstractAction {
		EditInstrumentAction() {
			super(i18n.getLabel("JSMidiInstrumentsPane.editInstrument"));
			
			String s = i18n.getLabel("JSMidiInstrumentsPane.editInstrument.tt");
			putValue(SHORT_DESCRIPTION, s);
			
			setEnabled(false);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			editInstrument(midiInstrumentTree.getSelectedInstrument());
			
		}
	}
	
	private class RemoveAction extends AbstractAction {
		RemoveAction() {
			String s = i18n.getLabel("JSMidiInstrumentsPane.remove.tt");
			putValue(SHORT_DESCRIPTION, s);
			
			setEnabled(false);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			midiInstrumentTree.removeSelectedInstrumentOrBank();
		}
	}
}
