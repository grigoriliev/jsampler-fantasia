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

package org.jsampler.view.classic;

import java.awt.event.ActionEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.jsampler.CC;

import org.jsampler.view.JSChannel;
import org.jsampler.view.JSChannelsPane;
import org.jsampler.view.std.JSAddMidiInstrumentMapDlg;

import org.jsampler.view.std.JSNewMidiDeviceDlg;
import org.jsampler.view.std.JSNewAudioDeviceDlg;
import org.jsampler.view.std.JSNewMidiInstrumentWizard;
import org.jsampler.view.std.StdA4n;
import org.jsampler.view.swing.SHF;
import org.jsampler.view.swing.SwingChannelsPane;
import org.jsampler.view.swing.SwingMainFrame;

import static org.jsampler.view.classic.ClassicI18n.i18n;


/**
 * This class provides an <code>Action</code> instances performing all needed tasks.
 * @author Grigor Iliev
 */
public class A4n extends StdA4n {
	protected static A4n a4n = new A4n();
	
	/** Forbids the instantiation of <code>A4n</code> */
	private A4n() {
		refresh.putValue(Action.SMALL_ICON, Res.iconReload32);
		resetSampler.putValue(Action.SMALL_ICON, Res.iconReset32);
		exportSamplerConfig.putValue(Action.SMALL_ICON, Res.iconExportSession32);
		exportMidiInstrumentMaps.putValue(Action.SMALL_ICON, Res.iconExport16);
		
		moveChannelsUp.putValue(Action.SMALL_ICON, Res.iconUp24);
		moveChannelsDown.putValue(Action.SMALL_ICON, Res.iconDown24);
		
		duplicateChannels.putValue(Action.SMALL_ICON, Res.iconCopy24);
		removeChannels.putValue(Action.SMALL_ICON, Res.iconDelete24);
	}
	
	@Override
	protected ClassicPrefs
	preferences() {
		return ClassicPrefs.preferences();
	}
	
	private static boolean
	verifyConnection() {
		if(!CC.getClient().isConnected()) {
			SHF.showErrorMessage(i18n.getError("notConnected"));
			return false;
		}
		
		return true;
	}
	
	public final static Action samplerInfo = new SamplerInfo();
	
	private static class SamplerInfo extends AbstractAction {
		SamplerInfo() {
			super(i18n.getMenuLabel("actions.samplerInfo"));
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("ttSamplerInfo"));
			putValue(Action.SMALL_ICON, Res.iconInfo32);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			new SamplerInfoDlg(SHF.getMainFrame()).setVisible(true);
		}
	}
	
	public final static Action addMidiInstrumentMap = new AddMidiInstrumentMap();
	
	private static class AddMidiInstrumentMap extends AbstractAction {
		AddMidiInstrumentMap() {
			super(i18n.getMenuLabel("actions.midiInstruments.addMap"));
			
			String s = i18n.getMenuLabel("actions.midiInstruments.addMap.tt");
			putValue(SHORT_DESCRIPTION, s);
			putValue(Action.SMALL_ICON, Res.iconNew16);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			JSAddMidiInstrumentMapDlg dlg = new JSAddMidiInstrumentMapDlg();
			dlg.setVisible(true);
			if(dlg.isCancelled()) return;
			
			CC.getSamplerModel().addBackendMidiInstrumentMap(dlg.getMapName());
			LeftPane.getLeftPane().showMidiInstrumentMapsPage();
		}
	}
	
	public final static Action removeMidiInstrumentMap = new RemoveMidiInstrumentMap();
	
	private static class RemoveMidiInstrumentMap extends AbstractAction {
		RemoveMidiInstrumentMap() {
			super(i18n.getMenuLabel("actions.midiInstruments.removeMap"));
			
			String s = i18n.getMenuLabel("actions.midiInstruments.removeMap.tt");
			putValue(SHORT_DESCRIPTION, s);
			putValue(Action.SMALL_ICON, Res.iconDelete16);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			RemoveMidiInstrumentMapDlg dlg = new RemoveMidiInstrumentMapDlg();
			dlg.setVisible(true);
			if(dlg.isCancelled()) return;
			int id = dlg.getSelectedMap().getMapId();
			CC.getSamplerModel().removeBackendMidiInstrumentMap(id);
		}
	}
	
	public final static Action addMidiInstrumentWizard = new AddMidiInstrumentWizard();
	
	private static class AddMidiInstrumentWizard extends AbstractAction {
		AddMidiInstrumentWizard() {
			super(i18n.getMenuLabel("actions.midiInstruments.newMidiInstrumentWizard"));
			
			String s = "actions.midiInstruments.newMidiInstrumentWizard.tt";
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel(s));
			putValue(Action.SMALL_ICON, Res.iconNew16);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			ClassicPrefs prefs = ClassicPrefs.preferences();
			JSNewMidiInstrumentWizard wizard =
				new JSNewMidiInstrumentWizard(Res.iconFolderOpen16);
			
			wizard.setMinimumSize(wizard.getPreferredSize());
			javax.swing.JDialog wizardDlg = wizard.getWizardDialog();
			wizardDlg.setMinimumSize(wizardDlg.getPreferredSize());
			
			if(prefs.getBoolProperty("NewMidiInstrumentWizard.skip1")) {
				if(wizard.getModel().getCurrentPage() == null) {
					wizard.getModel().next();
				}
				wizard.getModel().next();
			}
			
			wizard.showWizard();
			
		}
	}
	
	public final static Action loadScript = new LoadLscpScript();
	
	private static class LoadLscpScript extends AbstractAction {
		LoadLscpScript() {
			super(i18n.getMenuLabel("actions.runScript"));
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("ttRunScript"));
			putValue(Action.SMALL_ICON, Res.iconLoadScript32);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			((MainFrame)SHF.getMainFrame()).runScript();
		}
	}
	
	public final static Action addMidiDevice = new AddMidiDevice();
	
	private static class AddMidiDevice extends AbstractAction {
		AddMidiDevice() {
			super("");
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("ttAddMidiDevice"));
			putValue(Action.SMALL_ICON, Res.iconNew16);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			if(!verifyConnection()) return;
			new JSNewMidiDeviceDlg(SHF.getMainFrame()).setVisible(true);
		}
	}
	
	public final static Action addAudioDevice = new AddAudioDevice();
	
	private static class AddAudioDevice extends AbstractAction {
		AddAudioDevice() {
			super("");
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("ttAddAudioDevice"));
			putValue(Action.SMALL_ICON, Res.iconNew16);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			if(!verifyConnection()) return;
			new JSNewAudioDeviceDlg(SHF.getMainFrame()).setVisible(true);
		}
	}

// EDIT
	public final static Action preferences = new Preferences();
	
	private static class Preferences extends AbstractAction {
		Preferences() {
			super(i18n.getMenuLabel("edit.preferences"));
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("ttPrefs"));
			putValue(Action.SMALL_ICON, Res.iconPreferences32);
		}
		
		public void
		actionPerformed(ActionEvent e) { new PrefsDlg(SHF.getMainFrame()).setVisible(true); }
	}
	
// VIEW
	
	

// CHANNELS
	public final static Action newChannel = new NewChannel();
	public final static Action newChannelWizard = new NewChannelWizard();
	
	
	private static class NewChannel extends AbstractAction {
		NewChannel() {
			super(i18n.getMenuLabel("channels.new"));
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("ttNewChannel"));
			putValue(Action.SMALL_ICON, Res.iconNew24);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			if(!verifyConnection()) return;
			CC.getSamplerModel().addBackendChannel();
		}
	}
	
	private static class NewChannelWizard extends AbstractAction {
		NewChannelWizard() {
			super(i18n.getMenuLabel("channels.wizard"));
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("ttNewChannelWizard"));
		}
		
		public void
		actionPerformed(ActionEvent e) {
			new org.jsampler.view.classic.NewChannelWizard().showWizard();
		}
	}
	
	public static class
	MoveChannelsTo extends AbstractAction implements PropertyChangeListener {
		private final SwingChannelsPane pane;
		
		MoveChannelsTo(SwingChannelsPane pane) {
			super(pane.getTitle());
			
			this.pane = pane;
			
			String desc = i18n.getMenuLabel("ttMoveChannelsTo", pane.getTitle());
			putValue(SHORT_DESCRIPTION, desc);
			pane.addPropertyChangeListener(JSChannelsPane.TITLE, this);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			JSChannelsPane acp = CC.getMainFrame().getSelectedChannelsPane();
			JSChannel[] chns = acp.getSelectedChannels();
			
			for(JSChannel c : chns) acp.removeChannel(c);
			
			pane.addChannels(chns);
			
			CC.getMainFrame().setSelectedChannelsPane(pane);
			
		}
		
		public void
		propertyChange(PropertyChangeEvent e) {
			putValue(NAME, pane.getTitle());
			
			String desc = i18n.getMenuLabel("ttMoveChannelsTo", pane.getTitle());
			putValue(SHORT_DESCRIPTION, desc);
		}
	
		public JSChannelsPane
		getChannelsPane() { return pane; }
	}
	
// TABS
	public final static Action newChannelsTab = new NewChannelsTab();
	
	public final static Action editTabTitle = new EditTabTitle();
	
	public final static Action moveTab2Beginning = new MoveTab2Beginning();
	public final static Action moveTab2Left = new MoveTab2Left();
	public final static Action moveTab2Right = new MoveTab2Right();
	public final static Action moveTab2End = new MoveTab2End();
	
	public final static Action closeChannelsTab = new CloseChannelsTab();
	
	
	private static class NewChannelsTab extends AbstractAction {
		NewChannelsTab() {
			super(i18n.getMenuLabel("tabs.new"));
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("ttNewTab"));
			putValue(Action.SMALL_ICON, Res.iconTabNew22);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			new NewChannelsTabDlg(SHF.getMainFrame()).setVisible(true);
		}
	}
	
	private static class EditTabTitle extends AbstractAction {
		EditTabTitle() {
			super(i18n.getMenuLabel("tabs.changeTitle"));
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("ttEditTabTitle"));
			setEnabled(false);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			new ChangeTabTitleDlg(SHF.getMainFrame()).setVisible(true);
		}
	}
	
	private static class MoveTab2Beginning extends AbstractAction {
		MoveTab2Beginning() {
			super(i18n.getMenuLabel("tabs.move2Beginning"));
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("ttMoveTab2Beginning"));
			setEnabled(false);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			((MainFrame)SHF.getMainFrame()).moveTab2Beginning();
		}
	}
	
	private static class MoveTab2Left extends AbstractAction {
		MoveTab2Left() {
			super(i18n.getMenuLabel("tabs.move2Left"));
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("ttMoveTab2Left"));
			putValue(Action.SMALL_ICON, Res.iconTabMoveLeft22);
			setEnabled(false);
		}
		
		public void
		actionPerformed(ActionEvent e) { ((MainFrame)SHF.getMainFrame()).moveTab2Left(); }
	}
	
	private static class MoveTab2Right extends AbstractAction {
		MoveTab2Right() {
			super(i18n.getMenuLabel("tabs.move2Right"));
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("ttMoveTab2Right"));
			putValue(Action.SMALL_ICON, Res.iconTabMoveRight22);
			setEnabled(false);
		}
		
		public void
		actionPerformed(ActionEvent e) { ((MainFrame)SHF.getMainFrame()).moveTab2Right(); }
	}
	
	private static class MoveTab2End extends AbstractAction {
		MoveTab2End() {
			super(i18n.getMenuLabel("tabs.move2End"));
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("ttMoveTab2End"));
			setEnabled(false);
		}
		
		public void
		actionPerformed(ActionEvent e) { ((MainFrame)SHF.getMainFrame()).moveTab2End(); }
	}
	
	private static class CloseChannelsTab extends AbstractAction {
		CloseChannelsTab() {
			super(i18n.getMenuLabel("tabs.close"));
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("ttCloseTab"));
			putValue(Action.SMALL_ICON, Res.iconTabRemove22);
			setEnabled(false);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			SwingMainFrame frm = SHF.getMainFrame();
			JSChannelsPane chnPane = frm.getSelectedChannelsPane();
			if(chnPane.getChannelCount() > 0) {
				CloseTabDlg dlg = new CloseTabDlg(frm);
				dlg.setVisible(true);
				if(dlg.isCancelled()) return;
				
				JSChannel[] chns = chnPane.getChannels();
				
				if(dlg.remove()) {
					
					
				} else {
					JSChannelsPane p = dlg.getSelectedChannelsPane();
					for(JSChannel c : chns) {
						chnPane.removeChannel(c);
					}
					p.addChannels(chns);
					frm.setSelectedChannelsPane(p);
				}
			}
			
			frm.removeChannelsPane(chnPane);
		}
	}
	
// WINDOW
	public final static WindowInstrumentsDb windowInstrumentsDb  = new WindowInstrumentsDb();
	
	private static class WindowInstrumentsDb extends AbstractAction {
		InstrumentsDbFrame instrumentsDbFrame = null;
		
		WindowInstrumentsDb() {
			super(i18n.getMenuLabel("window.instrumentsDb"));
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("window.instrumentsDb.tt"));
			putValue(Action.SMALL_ICON, Res.iconDb32);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			if(!CC.verifyConnection()) return;
			
			if(SHF.getInstrumentsDbTreeModel() == null) {
				String s = i18n.getMessage("A4n.noInstrumentsDbSupport!");
				SHF.showErrorMessage(s, SHF.getMainFrame());
				return;
			}
			
			if(instrumentsDbFrame != null && instrumentsDbFrame.isVisible()) {
				instrumentsDbFrame.setVisible(false);
				instrumentsDbFrame.setVisible(true);
				return;
			}
			
			instrumentsDbFrame = new InstrumentsDbFrame();
			instrumentsDbFrame.setVisible(true);
		}
	}
	
// HELP
	public final static HelpAbout helpAbout = new HelpAbout();
	
	private static class HelpAbout extends AbstractAction {
		HelpAbout() {
			super(i18n.getMenuLabel("help.about", "JS Classic"));
		}
		
		public void
		actionPerformed(ActionEvent e) {
			new HelpAboutDlg(SHF.getMainFrame()).setVisible(true);
		}
	}
}
