/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2011 Grigor Iliev <grigor@grigoriliev.com>
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
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sf.juife.swing.JuifeUtils;

import org.jsampler.CC;
import org.jsampler.DefaultOrchestraModel;
import org.jsampler.OrchestraInstrument;
import org.jsampler.MidiInstrumentMap;
import org.jsampler.OrchestraModel;
import org.jsampler.SamplerChannelModel;

import org.jsampler.event.ListEvent;
import org.jsampler.event.ListListener;
import org.jsampler.event.SamplerChannelListEvent;
import org.jsampler.event.SamplerChannelListListener;

import org.jsampler.view.swing.InstrumentTable;

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
		instrumentTable.setFillsViewportHeight(true);
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
			mousePressed(MouseEvent e) {
				if(e.getButton() != e.BUTTON3) return;
				
				int i = instrumentTable.rowAtPoint(e.getPoint());
				if(i == -1) return;
				
				instrumentTable.getSelectionModel().setSelectionInterval(i, i);
				
			}
			
			public void
			mouseClicked(MouseEvent e) {
				if(e.getClickCount() < 2) return;
				
				if(instrumentTable.getSelectedInstrument() == null) return;
				editInstrument(instrumentTable.getSelectedInstrument());
			}
		});
		
		ContextMenu contextMenu = new ContextMenu();
		instrumentTable.addMouseListener(contextMenu);
		
		KeyStroke k = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
		instrumentTable.getInputMap(JComponent.WHEN_FOCUSED).put(k, "deleteInstrument");
		instrumentTable.getActionMap().put("deleteInstrument", actionDeleteInstrument);
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
	
	public OrchestraInstrument
	getSelectedInstrument() { return instrumentTable.getSelectedInstrument(); }
	
	/**
	 * Invoked when the user initiates the creation of new instrument.
	 * @return The instrument to add
	 * or <code>null</code> if the user cancelled the task.
	 */
	public OrchestraInstrument
	createInstrument() {
		JSAddOrEditInstrumentDlg dlg = new JSAddOrEditInstrumentDlg();
		dlg.setVisible(true);
		
		if(dlg.isCancelled()) return null;
		return dlg.getInstrument();
	}
	
	public void
	editInstrument(OrchestraInstrument instr) {
		JSAddOrEditInstrumentDlg dlg = new JSAddOrEditInstrumentDlg(instr);
		dlg.setVisible(true);
	}
	
	private class InstrumentSelectionHandler implements ListSelectionListener {
		@Override
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
		
		@Override
		public void
		actionPerformed(ActionEvent e) {
			OrchestraInstrument instr = createInstrument();
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
		
		@Override
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
		
		@Override
		public void
		actionPerformed(ActionEvent e) {
			OrchestraInstrument instr = instrumentTable.getSelectedInstrument();
			if(instr == null) return;
			int i = instrumentTable.getSelectedRow();
			orchestra.removeInstrument(instr);
			
			if(instrumentTable.getRowCount() > i) {
				instrumentTable.getSelectionModel().setSelectionInterval(i, i);
			}
		}
	}
	
	private class InstrumentUpAction extends AbstractAction {
		InstrumentUpAction() {
			super("");
			
			String s = i18n.getLabel("JSOrchestraPane.ttInstrumentUp");
			putValue(SHORT_DESCRIPTION, s);
			
			setEnabled(false);
		}
		
		@Override
		public void
		actionPerformed(ActionEvent e) {
			OrchestraInstrument instr = instrumentTable.getSelectedInstrument();
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
		
		@Override
		public void
		actionPerformed(ActionEvent e) {
			OrchestraInstrument instr = instrumentTable.getSelectedInstrument();
			instrumentTable.getModel().getOrchestraModel().moveInstrumentDown(instr);
			instrumentTable.setSelectedInstrument(instr);
		}
	}
	
	
	
	private class LoadInstrumentAction extends StdA4n.LoadInstrumentAction {
		LoadInstrumentAction(SamplerChannelModel model, boolean onPanel) {
			super(model, onPanel);
		}
		
		@Override
		public void
		actionPerformed(ActionEvent e) {
			OrchestraInstrument instr = instrumentTable.getSelectedInstrument();
			if(instr == null) return;
			
			int idx = instr.getInstrumentIndex();
			channelModel.setBackendEngineType(instr.getEngine());
			channelModel.loadBackendInstrument(instr.getFilePath(), idx);
		}
	}
	
	private LoadInstrumentActionFactory loadInstrActionFactory = new LoadInstrumentActionFactory();
	
	class LoadInstrumentActionFactory implements StdA4n.LoadInstrumentActionFactory {
		public StdA4n.LoadInstrumentAction
		createLoadInstrumentAction(SamplerChannelModel model, boolean onPanel) {
			return new LoadInstrumentAction(model, onPanel);
		}
	}
	
	class AddToMidiMapAction extends AbstractAction {
		private final MidiInstrumentMap midiMap;
		
		AddToMidiMapAction(MidiInstrumentMap map) {
			super(map.getName());
			midiMap = map;
		}
		
		@Override
		public void
		actionPerformed(ActionEvent e) {
			OrchestraInstrument instr = instrumentTable.getSelectedInstrument();
			if(instr == null) return;
			
			JSAddMidiInstrumentDlg dlg;
			Window w = JuifeUtils.getWindow(JSOrchestraPane.this);
			if(w instanceof Dialog) {
				dlg = new JSAddMidiInstrumentDlg((Dialog)w, midiMap, instr);
			} else if(w instanceof Frame) {
				dlg = new JSAddMidiInstrumentDlg((Frame)w, midiMap, instr);
			} else {
				dlg = new JSAddMidiInstrumentDlg((Frame)null, midiMap, instr);
			}
			
			dlg.setVisible(true);
		}
	}
	
	
	class ContextMenu extends MouseAdapter
			  implements SamplerChannelListListener, ListSelectionListener {
		
		private final JPopupMenu cmenu = new JPopupMenu();
		JMenuItem miEdit = new JMenuItem(i18n.getMenuLabel("ContextMenu.edit"));
		
		JMenu mLoadInstrument =
			new JMenu(i18n.getMenuLabel("JSOrchestraPane.loadInstrument"));
		
		JMenu mMapInstrument =
			new JMenu(i18n.getMenuLabel("JSOrchestraPane.mapInstrument"));
		
		ContextMenu() {
			cmenu.add(miEdit);
			miEdit.addActionListener(new ActionListener() {
				public void
				actionPerformed(ActionEvent e) {
					actionEditInstrument.actionPerformed(null);
				}
			});
			
			JMenuItem mi = new JMenuItem(i18n.getMenuLabel("ContextMenu.delete"));
			cmenu.add(mi);
			mi.addActionListener(new ActionListener() {
				public void
				actionPerformed(ActionEvent e) {
					actionDeleteInstrument.actionPerformed(null);
				}
			});
			
			cmenu.addSeparator();
			
			cmenu.add(mLoadInstrument);
			cmenu.add(mMapInstrument);
			
			CC.getSamplerModel().addSamplerChannelListListener(this);
			instrumentTable.getSelectionModel().addListSelectionListener(this);
			
			ListListener<MidiInstrumentMap> l = new ListListener<MidiInstrumentMap>() {
				public void
				entryAdded(ListEvent<MidiInstrumentMap> e) {
					updateAddToMidiMapMenu(mMapInstrument);
				}
				
				public void
				entryRemoved(ListEvent<MidiInstrumentMap> e) {
					updateAddToMidiMapMenu(mMapInstrument);
				}
			};
			
			CC.getSamplerModel().addMidiInstrumentMapListListener(l);
			
			CC.getMainFrame().addChannelsPaneSelectionListener(new org.jsampler.event.ListSelectionListener() {
				public void
				valueChanged(org.jsampler.event.ListSelectionEvent e) {
					updateLoadInstrumentMenu(mLoadInstrument);
				}
			});
		}
		
		private void
		updateLoadInstrumentMenu(JMenu menu) {
			menu.removeAll();
			
			StdA4n.updateLoadInstrumentMenu(menu, loadInstrActionFactory);
			updateLoadInstrumentMenuState(menu);
		}
		
		private void
		updateLoadInstrumentMenuState(JMenu menu) {
			OrchestraInstrument instr = instrumentTable.getSelectedInstrument();
			boolean b = instr == null;
			b = b || CC.getSamplerModel().getChannelCount() == 0;
			menu.setEnabled(!b);
		}
		
		private void
		updateAddToMidiMapMenu(JMenu menu) {
			menu.removeAll();
			for(int i = 0; i < CC.getSamplerModel().getMidiInstrumentMapCount(); i++) {
				MidiInstrumentMap m = CC.getSamplerModel().getMidiInstrumentMap(i);
				menu.add(new JMenuItem(new AddToMidiMapAction(m)));
			}
			
			updateAddToMidiMapMenuState(menu);
		}
		
		private void
		updateAddToMidiMapMenuState(JMenu menu) {
			OrchestraInstrument instr = instrumentTable.getSelectedInstrument();
			boolean b = instr == null;
			b = b || CC.getSamplerModel().getMidiInstrumentMapCount() == 0;
			menu.setEnabled(!b);
		}
		
		@Override
		public void
		valueChanged(ListSelectionEvent e) {
			updateLoadInstrumentMenuState(mLoadInstrument);
			updateAddToMidiMapMenuState(mMapInstrument);
		}
		
		@Override
		public void
		channelAdded(SamplerChannelListEvent e) {
			if(CC.getSamplerModel().getChannelListIsAdjusting()) return;
			updateLoadInstrumentMenu(mLoadInstrument);
		}
		
		@Override
		public void
		channelRemoved(SamplerChannelListEvent e) {
			updateLoadInstrumentMenu(mLoadInstrument);
		}
		
		@Override
		public void
		mousePressed(MouseEvent e) {
			if(e.isPopupTrigger()) show(e);
		}
	
		@Override
		public void
		mouseReleased(MouseEvent e) {
			if(e.isPopupTrigger()) show(e);
		}
	
		void
		show(MouseEvent e) {
			cmenu.show(e.getComponent(), e.getX(), e.getY());
		}
	}
}
