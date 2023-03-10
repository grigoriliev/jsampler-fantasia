/*
 *   JSampler - a front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2023 Grigor Iliev <grigor@grigoriliev.com>
 *
 *   This file is part of JSampler.
 *
 *   JSampler is free software: you can redistribute it and/or modify it under
 *   the terms of the GNU General Public License as published by the Free
 *   Software Foundation, either version 3 of the License, or (at your option)
 *   any later version.
 *
 *   JSampler is distributed in the hope that it will be useful, but WITHOUT
 *   ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *   FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 *   more details.
 *
 *   You should have received a copy of the GNU General Public License along
 *   with JSampler. If not, see <https://www.gnu.org/licenses/>. 
 */

package com.grigoriliev.jsampler.fantasia.view;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import com.grigoriliev.jsampler.CC;
import com.grigoriliev.jsampler.view.JSChannel;
import com.grigoriliev.jsampler.swing.view.SHF;
import com.grigoriliev.jsampler.swing.view.std.JSNewAudioDeviceDlg;
import com.grigoriliev.jsampler.swing.view.std.JSNewMidiDeviceDlg;
import com.grigoriliev.jsampler.swing.view.std.StdA4n;

import static com.grigoriliev.jsampler.JSPrefs.*;

/**
 *
 * @author Grigor Iliev
 */
public class A4n extends StdA4n {
	protected static A4n a4n = new A4n();
	
	/** Forbids the instantiation of <code>A4n</code> */
	private A4n() {
		refresh.putValue(Action.SMALL_ICON, Res.iconReload32);
		resetSampler.putValue(Action.SMALL_ICON, Res.iconReset32);
		exportSamplerConfig.putValue(Action.SMALL_ICON, Res.iconSave32);
		//exportMidiInstrumentMaps.putValue(Action.SMALL_ICON, Res.icon);
	}
	
	@Override
	protected FantasiaPrefs
	preferences() { return FantasiaPrefs.preferences(); }
	
	public final Action samplerInfo = new SamplerInfo();
	
	private static class SamplerInfo extends AbstractAction {
		SamplerInfo() {
			super(FantasiaI18n.i18n.getMenuLabel("actions.samplerInfo"));
			
			putValue(SHORT_DESCRIPTION, FantasiaI18n.i18n.getMenuLabel("actions.samplerInfo.tt"));
			putValue(Action.SMALL_ICON, Res.iconSamplerInfo32);
		}
		
		@Override
		public void
		actionPerformed(ActionEvent e) {
			new SamplerInfoDlg(SHF.getMainFrame()).setVisible(true);
		}
	}
	
	public final Action loadScript = new LoadLscpScript();
	
	private class LoadLscpScript extends AbstractAction {
		LoadLscpScript() {
			super(FantasiaI18n.i18n.getMenuLabel("actions.runScript"));
			
			putValue(SHORT_DESCRIPTION, FantasiaI18n.i18n.getMenuLabel("actions.runScript.tt"));
			putValue(Action.SMALL_ICON, Res.iconOpen32);
		}
		
		@Override
		public void
		actionPerformed(ActionEvent e) {
			if(!((MainFrame)SHF.getMainFrame()).runScript()) return;
			
			if(preferences().getBoolProperty(SHOW_LS_CONSOLE_WHEN_RUN_SCRIPT)) {
				windowLSConsole.actionPerformed(null);
			}
		}
	}
	
	public final Action createMidiDevice = new CreateMidiDevice();
	
	private class CreateMidiDevice extends AbstractAction {
		CreateMidiDevice() {
			super(FantasiaI18n.i18n.getMenuLabel("edit.createMidiDevice"));
			
			String s = FantasiaI18n.i18n.getMenuLabel("edit.createMidiDevice.tt");
			putValue(SHORT_DESCRIPTION, s);
			//putValue(Action.SMALL_ICON, Res.iconNew16);
		}
		
		@Override
		public void
		actionPerformed(ActionEvent e) {
			if(!CC.verifyConnection()) return;
			new JSNewMidiDeviceDlg(SHF.getMainFrame()).setVisible(true);
		}
	}
	
	public final Action createAudioDevice = new CreateAudioDevice();
	
	private class CreateAudioDevice extends AbstractAction {
		CreateAudioDevice() {
			super(FantasiaI18n.i18n.getMenuLabel("edit.createAudioDevice"));
			
			String s = FantasiaI18n.i18n.getMenuLabel("edit.createAudioDevice.tt");
			putValue(SHORT_DESCRIPTION, s);
			//putValue(Action.SMALL_ICON, Res.iconNew16);
		}
		
		@Override
		public void
		actionPerformed(ActionEvent e) {
			if(!CC.verifyConnection()) return;
			new JSNewAudioDeviceDlg(SHF.getMainFrame()).setVisible(true);
		}
	}
	
	// EDIT
	
	public final Action editPreferences = new EditPreferences();
	
	private class EditPreferences extends AbstractAction {
		EditPreferences() {
			super(FantasiaI18n.i18n.getMenuLabel("edit.preferences"));
			
			putValue(SHORT_DESCRIPTION, FantasiaI18n.i18n.getMenuLabel("edit.preferences.tt"));
			putValue(Action.SMALL_ICON, Res.iconPreferences32);
		}
		
		@Override
		public void
		actionPerformed(ActionEvent e) { new PrefsDlg(SHF.getMainFrame()).setVisible(true); }
	}
	
	
	// CHANNELS
	
	public final Action setSmallView = new SetView(ChannelView.Type.SMALL);
	public final Action setNormalView = new SetView(ChannelView.Type.NORMAL);
	
	public static class SetView extends AbstractAction {
		private ChannelView.Type type;
		
		SetView(ChannelView.Type type) {
			this.type = type;
			
			switch(type) {
			case SMALL:
				putValue(Action.NAME, FantasiaI18n.i18n.getMenuLabel("channels.smallView"));
				break;
			case NORMAL:
				putValue(Action.NAME, FantasiaI18n.i18n.getMenuLabel("channels.normalView"));
				break;
			}
		}
		
		@Override
		public void
		actionPerformed(ActionEvent e) {
			JSChannel[] channels =
				CC.getMainFrame().getSelectedChannelsPane().getSelectedChannels();
			
			for(JSChannel c : channels) {
				Channel c2 = (Channel)c;
				if(c2.getViewTracker().getOriginalView().getType() == type) {
					continue;
				}
				
				c2.getViewTracker().setView(createView(type, c2));
			}
			
			MenuManager.getMenuManager().updateChannelViewGroups();
		}
		
		public static int
		getViewCount(ChannelView.Type type) {
			int count = 0;
			
			JSChannel[] channels =
				CC.getMainFrame().getSelectedChannelsPane().getSelectedChannels();
			
			for(JSChannel c : channels) {
				Channel c2 = (Channel)c;
				if(c2.getViewTracker().getOriginalView().getType() == type) {
					count++;
				}
			}
			
			return count;
		}
		
		public ChannelView
		createView(ChannelView.Type type, Channel channel) {
			switch(type) {
				case SMALL: return new SmallChannelView(channel);
				case NORMAL: return new NormalChannelView(channel);
			}
			
			throw new IllegalArgumentException("Unknown channel type");
		}
	}
	
	// WINDOW
	public final Action windowLSConsole  = new WindowLSConsole();
	
	private class WindowLSConsole extends AbstractAction {
		WindowLSConsole() {
			super(FantasiaI18n.i18n.getMenuLabel("window.lsConsole"));
			putValue(SHORT_DESCRIPTION, FantasiaI18n.i18n.getMenuLabel("window.lsConsole.tt"));
			putValue(Action.SMALL_ICON, Res.iconLSConsole32);
		}
		
		@Override
		public void
		actionPerformed(ActionEvent e) {
			LSConsoleFrame console = ((MainFrame)SHF.getMainFrame()).getLSConsoleFrame();
			
			if(console.isVisible()) console.setVisible(false);
			
			console.setVisible(true);
		}
	}
	
	public final Action windowInstrumentsDb = new WindowInstrumentsDb();
	
	private class WindowInstrumentsDb extends AbstractAction {
		InstrumentsDbFrame instrumentsDbFrame = null;
		
		WindowInstrumentsDb() {
			super(FantasiaI18n.i18n.getMenuLabel("window.instrumentsDb"));
			putValue(SHORT_DESCRIPTION, FantasiaI18n.i18n.getMenuLabel("window.instrumentsDb.tt"));
			putValue(Action.SMALL_ICON, Res.iconDb32);
		}
		
		@Override
		public void
		actionPerformed(ActionEvent e) {
			if(!CC.verifyConnection()) return;
			
			if(SHF.getInstrumentsDbTreeModel() == null) {
				String s = FantasiaI18n.i18n.getMessage("A4n.noInstrumentsDbSupport!");
				SHF.showErrorMessage(s, SHF.getMainFrame());
				return;
			}
			
			if(instrumentsDbFrame != null) {
				instrumentsDbFrame.setVisible(false);
				instrumentsDbFrame.setVisible(true);
				return;
			}
			
			if(CC.getViewConfig().isUsingScreenMenuBar()) {
				// fix for moving the menu bar on top of the screen
				// when running on Mac OS and third party plugin is used
				((ViewConfig)CC.getViewConfig()).setNativeMenuProperties();
				instrumentsDbFrame = new InstrumentsDbFrame();
				((ViewConfig)CC.getViewConfig()).restoreMenuProperties();

			} else {
				instrumentsDbFrame = new InstrumentsDbFrame();
			}

			instrumentsDbFrame.setVisible(true);
		}
	}
	
	public final Action windowSamplerBrowser  = new WindowSamplerBrowser();

	private class WindowSamplerBrowser extends AbstractAction {
		WindowSamplerBrowser() {
			super(FantasiaI18n.i18n.getMenuLabel("window.SamplerBrowser"));
			putValue(SHORT_DESCRIPTION, FantasiaI18n.i18n.getMenuLabel("window.SamplerBrowser.tt"));
			//putValue(Action.SMALL_ICON, Res.iconLSConsole32);
		}

		@Override
		public void
		actionPerformed(ActionEvent e) {
			SamplerBrowserFrame browser =
				((MainFrame)SHF.getMainFrame()).getSamplerBrowserFrame();

			if(browser.isVisible()) browser.setVisible(false);

			browser.setVisible(true);
		}
	}
	
// HELP
	public final Action helpAbout = new HelpAbout();
	
	private class HelpAbout extends AbstractAction {
		HelpAbout() {
			super(FantasiaI18n.i18n.getMenuLabel("help.about", "Fantasia"));
		}
		
		@Override
		public void
		actionPerformed(ActionEvent e) {
			new HelpAboutDlg(SHF.getMainFrame()).setVisible(true);
		}
	}
}
