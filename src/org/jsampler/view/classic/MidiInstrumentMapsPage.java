/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2006 Grigor Iliev <grigor@grigoriliev.com>
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

package org.jsampler.view.classic;

import java.awt.Dimension;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import net.sf.juife.LinkButton;
import net.sf.juife.NavigationPage;

import org.jsampler.CC;
import org.jsampler.MidiInstrument;
import org.jsampler.MidiInstrumentMap;

import org.jsampler.event.ListEvent;
import org.jsampler.event.ListListener;
import org.jsampler.event.MidiInstrumentMapEvent;
import org.jsampler.event.MidiInstrumentMapListener;

import org.linuxsampler.lscp.MidiInstrumentInfo;

import static org.jsampler.view.classic.ClassicI18n.i18n;


/**
 *
 * @author Grigor Iliev
 */
public class MidiInstrumentMapsPage extends NavigationPage {
	private final JToolBar tbMaps = new JToolBar();
	private final JToolBar tbInstruments = new JToolBar();
	
	private final EditMap actionEditMap = new EditMap();
	private final RemoveMap actionRemoveMap = new RemoveMap();
	private final EditInstrument actionEditInstrument = new EditInstrument();
	private final RemoveInstrument removeInstrument = new RemoveInstrument();
	
	private final ToolbarButton btnAddMap = new ToolbarButton(A4n.addMidiInstrumentMap);
	private final ToolbarButton btnEditMap = new ToolbarButton(actionEditMap);
	private final ToolbarButton btnRemoveMap = new ToolbarButton(actionRemoveMap);
	private final ToolbarButton btnExportMaps = new ToolbarButton(A4n.exportMidiInstrumentMaps);
	//private final ToolbarButton btnCloseMapBar = new ToolbarButton();
	
	private final ToolbarButton btnAddInstrument =
		new ToolbarButton(A4n.addMidiInstrumentWizard);
	
	private final ToolbarButton btnEditInstrument = new ToolbarButton(actionEditInstrument);
	private final ToolbarButton btnRemoveInstrument = new ToolbarButton(removeInstrument);
	//private final ToolbarButton btnCloseInstrumentBar = new ToolbarButton();
	
	private final JComboBox cbMaps = new JComboBox();
	private final MidiInstrumentTree midiInstrumentTree = new MidiInstrumentTree();
	
	/** Creates a new instance of MidiInstrumentMapsPage */
	public
	MidiInstrumentMapsPage() {
		setTitle(i18n.getLabel("MidiInstrumentMapsPage.title"));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		/*btnCloseMapBar.setIcon(Res.iconClose8);
		btnCloseMapBar.setMargin(new java.awt.Insets(0, 0, 0, 0));
		btnCloseMapBar.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				tbMaps.setVisible(false);
				validate();
				repaint();
			}
		});
		
		
		btnCloseInstrumentBar.setIcon(Res.iconClose8);
		btnCloseInstrumentBar.setMargin(new java.awt.Insets(0, 0, 0, 0));
		btnCloseInstrumentBar.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				tbInstruments.setVisible(false);
				validate();
				repaint();
			}
		});*/
		
		
		tbMaps.add(btnAddMap);
		tbMaps.add(btnEditMap);
		tbMaps.add(btnRemoveMap);
		//tbMaps.addSeparator();
		//tbMaps.add(btnExportMaps);
		
		/*tbMaps.add(Box.createHorizontalGlue());
		tbMaps.add(btnCloseMapBar);*/
		
		Dimension d = new Dimension(Short.MAX_VALUE, tbMaps.getPreferredSize().height);
		tbMaps.setMaximumSize(d);
		tbMaps.setFloatable(false);
		tbMaps.setAlignmentX(LEFT_ALIGNMENT);
		
		add(tbMaps);
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setAlignmentX(LEFT_ALIGNMENT);
		p.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
				
		cbMaps.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { mapChanged(); }
		});
		
		for(MidiInstrumentMap m : CC.getSamplerModel().getMidiInstrumentMaps()) {
			cbMaps.addItem(m);
			m.addMidiInstrumentMapListener(getHandler());
		}
		
		cbMaps.setEnabled(cbMaps.getItemCount() > 0);
		
		d = new Dimension(Short.MAX_VALUE, cbMaps.getPreferredSize().height);
		cbMaps.setMaximumSize(d);
		
		CC.getSamplerModel().addMidiInstrumentMapListListener(getHandler());
		
		p.add(cbMaps);
		p.add(Box.createRigidArea(new Dimension(0, 12)));
		add(p);
		
		tbInstruments.add(btnAddInstrument);
		tbInstruments.add(btnEditInstrument);
		tbInstruments.add(btnRemoveInstrument);
		
		/*tbInstruments.add(Box.createHorizontalGlue());
		tbInstruments.add(btnCloseInstrumentBar); */
				
		d = new Dimension(Short.MAX_VALUE, tbInstruments.getPreferredSize().height);
		tbInstruments.setMaximumSize(d);
		tbInstruments.setFloatable(false);
		tbInstruments.setAlignmentX(LEFT_ALIGNMENT);
		
		add(tbInstruments);
		
		p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setAlignmentX(LEFT_ALIGNMENT);
		p.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
				
		p.add(new JScrollPane(midiInstrumentTree));
		
		add(p);
		
		boolean b = cbMaps.getItemCount() != 0;
		actionEditMap.setEnabled(b);
		actionRemoveMap.setEnabled(b);
		A4n.removeMidiInstrumentMap.setEnabled(b);
		
		midiInstrumentTree.addTreeSelectionListener(getHandler());
		removeInstrument.setEnabled(midiInstrumentTree.getSelectionCount() > 0);
		actionEditInstrument.setEnabled(midiInstrumentTree.getSelectedInstrument() != null);
	}
	
	private void
	mapChanged() {
		MidiInstrumentMap map = (MidiInstrumentMap)cbMaps.getSelectedItem();
		midiInstrumentTree.setMidiInstrumentMap(map);
		
		boolean b = cbMaps.getItemCount() != 0;
		actionEditMap.setEnabled(b);
		actionRemoveMap.setEnabled(b);
		A4n.removeMidiInstrumentMap.setEnabled(b);
	}
	
	
	private class EditMap extends AbstractAction {
		EditMap() {
			super(i18n.getLabel("MidiInstrumentMapsPage.editMap"));
			
			String s = i18n.getLabel("MidiInstrumentMapsPage.editMap.tt");
			putValue(SHORT_DESCRIPTION, s);
			putValue(Action.SMALL_ICON, Res.iconEdit16);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			MidiInstrumentMap map = (MidiInstrumentMap)cbMaps.getSelectedItem();
			int id = map.getMapId();
			AddMidiInstrumentMapDlg dlg = new AddMidiInstrumentMapDlg();
			dlg.setTitle(i18n.getLabel("MidiInstrumentMapsPage.editMap"));
			dlg.setMapName(map.getName());
			dlg.setVisible(true);
			if(dlg.isCancelled()) return;
			
			map.setName(dlg.getMapName());
			CC.getSamplerModel().setBackendMidiInstrumentMapName(id, dlg.getMapName());
		}
	}
	
	private class RemoveMap extends AbstractAction {
		RemoveMap() {
			super(i18n.getMenuLabel("actions.midiInstruments.removeMap"));
			
			String s = i18n.getMenuLabel("actions.midiInstruments.removeMap.tt");
			putValue(SHORT_DESCRIPTION, s);
			putValue(Action.SMALL_ICON, Res.iconDelete16);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			MidiInstrumentMap map = (MidiInstrumentMap)cbMaps.getSelectedItem();
			RemoveMidiInstrumentMapDlg dlg = new RemoveMidiInstrumentMapDlg(map);
			dlg.setVisible(true);
			if(dlg.isCancelled()) return;
			int id = dlg.getSelectedMap().getMapId();
			CC.getSamplerModel().removeBackendMidiInstrumentMap(id);
		}
	}
	
	private class EditInstrument extends AbstractAction {
		EditInstrument() {
			super(i18n.getLabel("MidiInstrumentMapsPage.EditInstrument"));
			
			String s = i18n.getLabel("MidiInstrumentMapsPage.EditInstrument.tt");
			putValue(SHORT_DESCRIPTION, s);
			putValue(Action.SMALL_ICON, Res.iconEdit16);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			MidiInstrument i = midiInstrumentTree.getSelectedInstrument();
			EditMidiInstrumentDlg dlg = new EditMidiInstrumentDlg(i.getInfo());
			dlg.setVisible(true);
			
			if(dlg.isCancelled()) return;
			
			MidiInstrumentInfo info = dlg.getInstrument();
			CC.getSamplerModel().mapBackendMidiInstrument (
				info.getMapId(), info.getMidiBank(), info.getMidiProgram(), info
			);
		}
	}
	
	private class RemoveInstrument extends AbstractAction {
		RemoveInstrument() {
			String s = i18n.getLabel("MidiInstrumentMapsPage.RemoveInstrument.tt");
			putValue(SHORT_DESCRIPTION, s);
			putValue(Action.SMALL_ICON, Res.iconDelete16);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			midiInstrumentTree.removeSelectedInstrumentOrBank();
		}
	}
	
	private final Handler handler = new Handler();
	
	private Handler
	getHandler() { return handler; }
	
	private class Handler implements ListListener<MidiInstrumentMap>,
				MidiInstrumentMapListener, TreeSelectionListener {
		
		/** Invoked when a new map is added. */
		public void
		entryAdded(ListEvent<MidiInstrumentMap> e) {
			MidiInstrumentMap map = e.getEntry();
			if(cbMaps.getItemCount() == 0) cbMaps.setEnabled(true);
			cbMaps.addItem(map);
			cbMaps.setSelectedItem(map);
			map.addMidiInstrumentMapListener(getHandler());
		}
	
		/** Invoked when a map is removed. */
		public void
		entryRemoved(ListEvent<MidiInstrumentMap> e) {
			cbMaps.removeItem(e.getEntry());
			if(cbMaps.getItemCount() == 0) cbMaps.setEnabled(false);
			e.getEntry().removeMidiInstrumentMapListener(getHandler());
		}
		
		/** Invoked when the name of MIDI instrument map is changed. */
		public void
		nameChanged(MidiInstrumentMapEvent e) {
			cbMaps.repaint();
		}
		
		/** Invoked when an instrument is added to a MIDI instrument map. */
		public void instrumentAdded(MidiInstrumentMapEvent e) { }
		
		/** Invoked when an instrument is removed from a MIDI instrument map. */
		public void instrumentRemoved(MidiInstrumentMapEvent e) { }
		
		public void
		valueChanged(TreeSelectionEvent e) {
			removeInstrument.setEnabled(midiInstrumentTree.getSelectionCount() > 0);
			boolean b = midiInstrumentTree.getSelectedInstrument() != null;
			actionEditInstrument.setEnabled(b);
		}
	}
}
