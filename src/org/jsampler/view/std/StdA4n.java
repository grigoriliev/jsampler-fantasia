/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2009 Grigor Iliev <grigor@grigoriliev.com>
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

import java.awt.event.ActionEvent;

import java.io.File;
import java.io.FileOutputStream;

import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.Action;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jsampler.CC;
import org.jsampler.HF;
import org.jsampler.JSPrefs;

import org.jsampler.SamplerChannelModel;

import org.jsampler.view.JSChannel;
import org.jsampler.view.JSChannelsPane;

import static org.jsampler.view.std.StdI18n.i18n;


/**
 * This class provides an <code>Action</code> instances performing some of the common tasks.
 * @author Grigor Iliev
 */
public class StdA4n {
	protected static StdA4n a4n = new StdA4n();
	
	protected StdA4n() { }
	
	protected JSPrefs preferences() { return CC.getViewConfig().preferences(); }
	
	protected void
	exportSamplerConfig() {
		File f = StdUtils.showSaveLscpFileChooser();
		if(f == null) return;
		if(f.exists()) {
			String msg = i18n.getMessage("StdA4n.overwriteFile?");
			if(!HF.showYesNoDialog(CC.getMainFrame(), msg)) return;
		}
	
		try {
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(CC.exportSessionToLscpScript().getBytes("US-ASCII"));
			fos.close();
		} catch(Exception x) {
			CC.getLogger().log(Level.FINE, HF.getErrorMessage(x), x);
			HF.showErrorMessage(x);
		}
	}
	
	protected void
	exportMidiInstrumentMaps() {
		File f = StdUtils.showSaveLscpFileChooser();
		if(f == null) return;

		if(f.exists()) {
			String msg = i18n.getMessage("StdA4n.overwriteFile?");
			if(!HF.showYesNoDialog(CC.getMainFrame(), msg)) return;
		}
		
		try {
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(CC.exportInstrMapsToLscpScript().getBytes("US-ASCII"));
			fos.close();
		} catch(Exception x) {
			CC.getLogger().log(Level.FINE, HF.getErrorMessage(x), x);
			HF.showErrorMessage(x);
		}
	}
	
	public final Action connect = new Connect();
		
	private class Connect extends AbstractAction {
		Connect() {
			super(i18n.getMenuLabel("actions.connect"));
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("actions.connect.tt"));
		}
		
		@Override
		public void
		actionPerformed(ActionEvent e) { CC.reconnect(); }
	}
	
	public final Action refresh = new Refresh();
	
	private class Refresh extends AbstractAction {
		Refresh() {
			super(i18n.getMenuLabel("actions.refresh"));
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("actions.refresh.tt"));
		}
		
		@Override
		public void
		actionPerformed(ActionEvent e) {
			if(!CC.verifyConnection()) {
				CC.changeBackend();
				return;
			}
			CC.reconnect();
		}
	}
	
	public final Action resetSampler = new Reset();
		
	private class Reset extends AbstractAction {
		Reset() {
			super(i18n.getMenuLabel("actions.resetSampler"));
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("actions.resetSampler.tt"));
		}
		
		@Override
		public void
		actionPerformed(ActionEvent e) {
			if(!CC.verifyConnection()) return;
			
			String s = i18n.getMessage("StdA4n.resetSampler?");
			if(!HF.showYesNoDialog(CC.getMainFrame(), s)) return;
			CC.getSamplerModel().resetBackend();
		}
	}
	
	public final Action exportSamplerConfig = new ExportSamplerConfig();
	
	private class ExportSamplerConfig extends AbstractAction {
		ExportSamplerConfig() {
			super(i18n.getMenuLabel("actions.export.samplerConfiguration"));
			
			String s = i18n.getMenuLabel("actions.export.samplerConfiguration.tt");
			putValue(SHORT_DESCRIPTION, s);
			
		}
		
		@Override
		public void
		actionPerformed(ActionEvent e) {
			if(!CC.verifyConnection()) return;
			exportSamplerConfig();
		}
	}
	
	public final Action exportMidiInstrumentMaps = new ExportMidiInstrumentMaps();
	
	private class ExportMidiInstrumentMaps extends AbstractAction {
		ExportMidiInstrumentMaps() {
			super(i18n.getMenuLabel("actions.export.MidiInstrumentMaps"));
			
			String s = i18n.getMenuLabel("actions.export.MidiInstrumentMaps.tt");
			putValue(SHORT_DESCRIPTION, s);
		}
		
		@Override
		public void
		actionPerformed(ActionEvent e) {
			if(!CC.verifyConnection()) return;
			exportMidiInstrumentMaps();
		}
	}
	
	public final Action changeBackend = new ChangeBackend();
	
	private class ChangeBackend extends AbstractAction {
		ChangeBackend() {
			super(i18n.getMenuLabel("actions.changeBackend"));
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("actions.changeBackend.tt"));
		}
		
		@Override
		public void
		actionPerformed(ActionEvent e) { CC.changeBackend(); }
	}
	
	
	public final Action moveChannelsOnTop = new MoveChannelsOnTop();
	
	private class MoveChannelsOnTop extends AbstractAction {
		MoveChannelsOnTop() {
			super(i18n.getMenuLabel("channels.moveOnTop"));
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("channels.moveOnTop.tt"));
			setEnabled(false);
		}
		
		@Override
		public void
		actionPerformed(ActionEvent e) {
			JSChannelsPane p = CC.getMainFrame().getSelectedChannelsPane();
			p.moveSelectedChannelsOnTop();
		}
	}
	
	public final Action moveChannelsUp = new MoveChannelsUp();
	
	private class MoveChannelsUp extends AbstractAction {
		MoveChannelsUp() {
			super(i18n.getMenuLabel("channels.moveUp"));
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("channels.moveUp.tt"));
			setEnabled(false);
		}
		
		@Override
		public void
		actionPerformed(ActionEvent e) {
			JSChannelsPane p = CC.getMainFrame().getSelectedChannelsPane();
			p.moveSelectedChannelsUp();
		}
	}
	
	public final Action moveChannelsDown = new MoveChannelsDown();
	
	private class MoveChannelsDown extends AbstractAction {
		MoveChannelsDown() {
			super(i18n.getMenuLabel("channels.moveDown"));
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("channels.moveDown.tt"));
			setEnabled(false);
		}
		
		@Override
		public void
		actionPerformed(ActionEvent e) {
			JSChannelsPane p = CC.getMainFrame().getSelectedChannelsPane();
			p.moveSelectedChannelsDown();
		}
	}
	
	public final Action moveChannelsAtBottom = new MoveChannelsAtBottom();
	
	private class MoveChannelsAtBottom extends AbstractAction {
		MoveChannelsAtBottom() {
			super(i18n.getMenuLabel("channels.moveAtBottom"));
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("channels.moveAtBottom.tt"));
			setEnabled(false);
		}
		
		@Override
		public void
		actionPerformed(ActionEvent e) {
			JSChannelsPane p = CC.getMainFrame().getSelectedChannelsPane();
			p.moveSelectedChannelsAtBottom();
		}
	}
	
	public final Action duplicateChannels = new DuplicateChannels();

	private static class DuplicateChannels extends AbstractAction {
		DuplicateChannels() {
			super(i18n.getMenuLabel("channels.duplicate"));
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("channels.duplicateChannels.tt"));
			
			setEnabled(false);
		}
		
		@Override
		public void
		actionPerformed(ActionEvent e) {
			JSChannel[] channels =
				CC.getMainFrame().getSelectedChannelsPane().getSelectedChannels();
			
			if(channels.length > 2) {
				if(!HF.showYesNoDialog (
					CC.getMainFrame(),
					i18n.getMessage("StdA4n.duplicateChannels?")
				)) return;
			}
			
			CC.getTaskQueue().add (
				new org.jsampler.task.DuplicateChannels(channels)
			);
		}
	}
	
	public final Action removeChannels = new RemoveChannels();
	
	private static class RemoveChannels extends AbstractAction {
		RemoveChannels() {
			super(i18n.getMenuLabel("channels.removeChannel"));
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("channels.removeChannels.tt"));
			
			setEnabled(false);
		}
		
		@Override
		public void
		actionPerformed(ActionEvent e) {
			JSChannelsPane p = CC.getMainFrame().getSelectedChannelsPane();
			if(p.getSelectedChannelCount() > 1) 
				if(!HF.showYesNoDialog (
					CC.getMainFrame(), i18n.getMessage("StdA4n.removeChannels?")
				)) return;
			
			JSChannel[] chnS = p.getSelectedChannels();
			
			for(JSChannel c : chnS) removeChannel(c);
		}
		
		private void
		removeChannel(final JSChannel c) {
			final JSChannelsPane p = CC.getMainFrame().getSelectedChannelsPane();
			int id = c.getChannelInfo().getChannelId();
			
			CC.getSamplerModel().removeBackendChannel(id);
		}
	}
	
	public static class
	MoveChannelsToPanel extends AbstractAction implements ListSelectionListener {
		private final JSChannelsPane pane;
		
		public
		MoveChannelsToPanel(JSChannelsPane pane) {
			super(pane.getTitle());
			this.pane = pane;
			CC.getMainFrame().addChannelsPaneSelectionListener(this);
			valueChanged(null);
		}
		
		@Override
		public void
		actionPerformed(ActionEvent e) {
			JSChannelsPane acp = CC.getMainFrame().getSelectedChannelsPane();
			JSChannel[] chns = acp.getSelectedChannels();
			
			for(JSChannel c : chns) acp.removeChannel(c);
			
			pane.addChannels(chns);
			
			//CC.getMainFrame().setSelectedChannelsPane(pane);
			
		}
		
		@Override
		public void
		valueChanged(ListSelectionEvent e) {
			setEnabled(CC.getMainFrame().getSelectedChannelsPane() != pane);
		}
	
		public JSChannelsPane
		getChannelsPane() { return pane; }
	}
	
	public final Action selectAllChannels = new SelectAllChannels();
	
	private static class SelectAllChannels extends AbstractAction {
		SelectAllChannels() {
			super(i18n.getMenuLabel("channels.selectAll"));
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("channels.selectAll.tt"));
		}
		
		@Override
		public void
		actionPerformed(ActionEvent e) {
			CC.getMainFrame().getSelectedChannelsPane().selectAll();
		}
	}
	
	public final Action deselectChannels = new DeselectChannels();
	
	private static class DeselectChannels extends AbstractAction {
		DeselectChannels() {
			super(i18n.getMenuLabel("channels.selectNone"));
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("channels.selectNone.tt"));
		}
		
		@Override
		public void
		actionPerformed(ActionEvent e) {
			CC.getMainFrame().getSelectedChannelsPane().clearSelection();
		}
	}
	
	public final Action browseOnlineTutorial = new BrowseOnlineTutorial();
		
	private class BrowseOnlineTutorial extends AbstractAction {
		BrowseOnlineTutorial() {
			super(i18n.getMenuLabel("help.onlineTutorial"));
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("help.onlineTutorial.tt"));
		}
		
		@Override
		public void
		actionPerformed(ActionEvent e) {
			StdUtils.browse("http://jsampler.sourceforge.net/");
		}
	}
	
	public static abstract class LoadInstrumentAction extends AbstractAction {
		protected final SamplerChannelModel channelModel;
		
		LoadInstrumentAction(SamplerChannelModel model) { this(model, false); }
		
		LoadInstrumentAction(SamplerChannelModel model, boolean onPanel) {
			String s = onPanel ? "instrumentsdb.actions.loadInstrument.onPanel.channel"
					   : "instrumentsdb.actions.loadInstrument.onChannel";
			int i = CC.getMainFrame().getChannelNumber(model) + 1;
			putValue(Action.NAME, i18n.getMenuLabel(s, i));
			channelModel = model;
		}
	}
	
	public static interface LoadInstrumentActionFactory {
		public LoadInstrumentAction
		createLoadInstrumentAction(SamplerChannelModel model, boolean onPanel);
	}
	
	
	
	public static void
	updateLoadInstrumentMenu(final JMenu menu, final LoadInstrumentActionFactory factory) {
		SwingUtilities.invokeLater(new Runnable() {
			public void
			run() { updateLoadInstrumentMenu0(menu, factory); }
		});
	}
	
	private static void
	updateLoadInstrumentMenu0(JMenu menu, LoadInstrumentActionFactory factory) {
		if(CC.getMainFrame() == null) return;
		menu.removeAll();
		int count = 0;
		JSChannelsPane chnPane = null;
		for(int i = 0; i < CC.getMainFrame().getChannelsPaneCount(); i++) {
			if(CC.getMainFrame().getChannelsPane(i).getChannelCount() == 0) continue;
			
			chnPane = CC.getMainFrame().getChannelsPane(i);
			count++;
			String s = "instrumentsdb.actions.loadInstrument.onPanel";
			JMenu m = new JMenu(i18n.getMenuLabel(s, i + 1));
			for(int j = 0; j < chnPane.getChannelCount(); j++) {
				SamplerChannelModel chn = chnPane.getChannel(j).getModel();
				m.add(new JMenuItem(factory.createLoadInstrumentAction(chn, true)));
			}
			menu.add(m);
		}
		
		if(count == 1 && CC.getMainFrame().getSelectedChannelsPane() == chnPane) {
			menu.removeAll();
			
			for(int j = 0; j < chnPane.getChannelCount(); j++) {
				SamplerChannelModel chn = chnPane.getChannel(j).getModel();
				menu.add(new JMenuItem(factory.createLoadInstrumentAction(chn, false)));
			}
		}
	}
}
