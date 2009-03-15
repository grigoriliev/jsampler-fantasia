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

package org.jsampler.view.std;

import java.awt.BorderLayout;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jsampler.CC;
import org.jsampler.HF;
import org.jsampler.MidiInstrumentMap;

import org.jsampler.view.MidiMapTable;

import static org.jsampler.view.std.StdI18n.i18n;

/**
 *
 * @author Grigor Iliev
 */
public class JSManageMidiMapsPane extends JPanel implements ListSelectionListener {
	protected final MidiMapTable midiMapTable = new MidiMapTable();
	
	protected final Action actionAddMap = new AddMapAction();
	protected final Action actionEditMap = new EditMapAction();
	protected final Action actionRemoveMap = new RemoveMapAction();
	
	/** Creates a new instance of <code>JSManageMidiMapsPane</code> */
	public
	JSManageMidiMapsPane() {
		setLayout(new BorderLayout());
		JScrollPane sp = new JScrollPane(midiMapTable);
		add(sp);
		
		installListeneres();
	}
	
	private void
	installListeneres() {
		midiMapTable.getSelectionModel().addListSelectionListener(this);
		
		midiMapTable.addMouseListener(new MouseAdapter() {
			public void
			mouseClicked(MouseEvent e) {
				if(e.getClickCount() < 2) return;
				
				if(midiMapTable.getSelectedMidiInstrumentMap() == null) return;
				editMidiInstrumentMap(midiMapTable.getSelectedMidiInstrumentMap());
			}
		});
		
		KeyStroke k = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
		midiMapTable.getInputMap(JComponent.WHEN_FOCUSED).put(k, "removeMidiMap");
		midiMapTable.getActionMap().put("removeMidiMap", actionRemoveMap);
	}
	
	@Override
	public void
	valueChanged(ListSelectionEvent e) {
		if(e.getValueIsAdjusting()) return;
		
		boolean b = midiMapTable.getSelectedMidiInstrumentMap() != null;
		actionEditMap.setEnabled(b);
		actionRemoveMap.setEnabled(b);
	}
	
	public void
	addMidiInstrumentMap() {
		JSAddMidiInstrumentMapDlg dlg = new JSAddMidiInstrumentMapDlg();
		dlg.setVisible(true);
		if(dlg.isCancelled()) return;
		
		CC.getSamplerModel().addBackendMidiInstrumentMap(dlg.getMapName());
	}
	
	public void
	editMidiInstrumentMap(MidiInstrumentMap map) {
		int id = map.getMapId();
		JSAddMidiInstrumentMapDlg dlg = new JSAddMidiInstrumentMapDlg();
		dlg.setTitle(i18n.getLabel("JSManageMidiMapsPane.editMap"));
		dlg.setMapName(map.getName());
		dlg.setVisible(true);
		if(dlg.isCancelled()) return;
		
		map.setName(dlg.getMapName());
		CC.getSamplerModel().setBackendMidiInstrumentMapName(id, dlg.getMapName());
	}
	
	private class AddMapAction extends AbstractAction {
		AddMapAction() {
			super(i18n.getLabel("JSManageMidiMapsPane.addMap"));
			
			String s = i18n.getLabel("JSManageMidiMapsPane.addMap.tt");
			putValue(SHORT_DESCRIPTION, s);
		}
		
		@Override
		public void
		actionPerformed(ActionEvent e) {
			addMidiInstrumentMap();
		}
	}
	
	private class EditMapAction extends AbstractAction {
		EditMapAction() {
			super(i18n.getLabel("JSManageMidiMapsPane.editMap"));
			
			String s = i18n.getLabel("JSManageMidiMapsPane.editMap.tt");
			putValue(SHORT_DESCRIPTION, s);
			
			setEnabled(false);
		}
		
		@Override
		public void
		actionPerformed(ActionEvent e) {
			editMidiInstrumentMap(midiMapTable.getSelectedMidiInstrumentMap());
		}
	}
	
	private class RemoveMapAction extends AbstractAction {
		RemoveMapAction() {
			super(i18n.getLabel("JSManageMidiMapsPane.removeMap"));
			
			String s = i18n.getLabel("JSManageMidiMapsPane.removeMap.tt");
			putValue(SHORT_DESCRIPTION, s);
			
			setEnabled(false);
		}
		
		@Override
		public void
		actionPerformed(ActionEvent e) {
			MidiInstrumentMap map = midiMapTable.getSelectedMidiInstrumentMap();
			if(map.getAllMidiInstruments().length > 0) {
				String s = i18n.getMessage("JSManageMidiMapsPane.removeMap", map.getName());
				String s2 = i18n.getMessage("JSManageMidiMapsPane.removeMap?");
				if(!HF.showYesNoDialog(JSManageMidiMapsPane.this, s2, s)) return;
			}
			CC.getSamplerModel().removeBackendMidiInstrumentMap(map.getMapId());
		}
	}
}
