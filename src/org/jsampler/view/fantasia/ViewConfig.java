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

package org.jsampler.view.fantasia;

import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.UIManager;

import org.jsampler.CC;
import org.jsampler.JSPrefs;

import org.jsampler.view.InstrumentsDbTableView;
import org.jsampler.view.InstrumentsDbTreeView;
import org.jsampler.view.BasicIconSet;
import org.jsampler.view.JSMainFrame;
import org.jsampler.view.JSViewConfig;

import org.jvnet.substance.api.SubstanceConstants;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.skin.SubstanceRavenGraphiteLookAndFeel;

/**
 *
 * @author Grigor Iliev
 */
public class ViewConfig extends JSViewConfig {
	private InstrumentsDbTreeView instrumentsDbTreeView = new TreeView();
	private InstrumentsDbTableView instrumentsDbTableView = new TableView();
	private BasicIconSet basicIconSet = new IconSet();

	private Map nativeMenuPropsMap = null;
	private Map menuPropsMap = null;
	
	/** Creates a new instance of <code>ViewConfig</code> */
	public
	ViewConfig() {
		try {
			if(isUsingScreenMenuBar()) {
				// fix for setting the menu bar on top of the screen
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				nativeMenuPropsMap = getMenuProperties();
			}
			UIManager.setLookAndFeel(new SubstanceRavenGraphiteLookAndFeel());
			UIManager.put(SubstanceLookAndFeel.WATERMARK_VISIBLE, Boolean.FALSE);
			
			UIManager.put (
				SubstanceLookAndFeel.TABBED_PANE_CONTENT_BORDER_KIND,
				SubstanceConstants.TabContentPaneBorderKind.SINGLE_FULL
			);

			if(isUsingScreenMenuBar()) {
				// fix for setting the menu bar on top of the screen
				menuPropsMap = getMenuProperties();
				setNativeMenuProperties();
			}
			
			if(!preferences().getBoolProperty("TurnOffCustomWindowDecoration")) {
				javax.swing.JFrame.setDefaultLookAndFeelDecorated(true);
				javax.swing.JDialog.setDefaultLookAndFeelDecorated(true);
			}
			
			Res.loadTheme(preferences().getStringProperty("Theme"));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * If running on Mac OS and third party LaF is used
	 * certain properties from the system LaF should be set
	 * to be able to move the menu bar on top of the screen.
	 */
	public void
	setNativeMenuProperties() {
		setMenuProperties(nativeMenuPropsMap);
	}

	/**
	 * If running on Mac OS and third party LaF is used
	 * certain properties from the system LaF should be set
	 * to be able to move the menu bar on top of the screen.
	 * This method is used to reverse them back to the LaF to be used.
	 */
	public void
	restoreMenuProperties() {
		setMenuProperties(menuPropsMap);
	}

	private void
	setMenuProperties(Map props) {
		if(props == null) return;
		for(Object o : props.keySet()) UIManager.put(o, props.get(o));
	}

	public Map
	getMenuProperties() {
		Map props = new HashMap();
		props.put("MenuBarUI", UIManager.get("MenuBarUI"));
		props.put("MenuUI", UIManager.get("MenuUI"));
		props.put("MenuItemUI", UIManager.get("MenuItemUI"));
		props.put("CheckBoxMenuItemUI", UIManager.get("CheckBoxMenuItemUI"));
		props.put("RadioButtonMenuItemUI", UIManager.get("RadioButtonMenuItemUI"));
		props.put("PopupMenuUI", UIManager.get("PopupMenuUI"));
		return props;
	}
	
	@Override
	public JSPrefs
	preferences() { return FantasiaPrefs.preferences(); }
	
	/** Exports the view configuration of the current session. */
	@Override
	public String
	exportSessionViewConfig() {
		StringBuffer sb = new StringBuffer();
		MainFrame frame = (MainFrame)CC.getMainFrame();
		
		for(int i = 0; i < frame.getChannelsPaneCount(); i++) {
			exportSamplerChannels(sb, i);
		}
		
		MidiDevicesPane midi = frame.getRightSidePane().getDevicesPane().getMidiDevicesPane();
		
		for(int i = 0; i < midi.getDevicePaneCount(); i++) {
			sb.append("#jsampler.fantasia: [MIDI device]\r\n");
			
			boolean b = midi.getDevicePaneAt(i).isOptionsPaneExpanded();
			sb.append("#jsampler.fantasia: expanded = ").append(b).append("\r\n");
			
			sb.append("#\r\n");
		}
		
		AudioDevicesPane au = frame.getRightSidePane().getDevicesPane().getAudioDevicesPane();
		
		for(int i = 0; i < au.getDevicePaneCount(); i++) {
			sb.append("#jsampler.fantasia: [audio device]\r\n");
			
			boolean b = au.getDevicePaneAt(i).isOptionsPaneExpanded();
			sb.append("#jsampler.fantasia: expanded = ").append(b).append("\r\n");
			
			sb.append("#\r\n");
		}
		
		return sb.toString();
	}
	
	private void
	exportSamplerChannels(StringBuffer sb, int channelsPane) {
		JSMainFrame frame = CC.getMainFrame();
		
		for(int i = 0; i < frame.getChannelsPane(channelsPane).getChannelCount(); i++) {
			Channel c = (Channel)frame.getChannelsPane(channelsPane).getChannel(i);
			
			sb.append("#jsampler.fantasia: [channel]\r\n");
			
			sb.append("#jsampler.fantasia: channelLane = ");
			sb.append(channelsPane + 1).append("\r\n");
			
			switch(c.getViewTracker().getOriginalView().getType()) {
				case SMALL:
					sb.append("#jsampler.fantasia: viewType = SMALL\r\n");
					break;
					
				case NORMAL:
					sb.append("#jsampler.fantasia: viewType = NORMAL\r\n");
					break;
			}
			
			boolean b = c.getViewTracker().getOriginalView().isOptionsButtonSelected();
			sb.append("#jsampler.fantasia: expanded = ").append(b).append("\r\n");
			
			sb.append("#\r\n");
		}
	}
	
	@Override
	public InstrumentsDbTreeView
	getInstrumentsDbTreeView() { return instrumentsDbTreeView; }
	
	@Override
	public InstrumentsDbTableView
	getInstrumentsDbTableView() { return instrumentsDbTableView; }
	
	@Override
	public BasicIconSet
	getBasicIconSet() { return basicIconSet; }
	
	private class TreeView implements InstrumentsDbTreeView {
		@Override
		public Icon
		getRootIcon() { return Res.iconDb16; }
		
		@Override
		public Icon
		getClosedIcon() { return Res.iconFolder16; }
	
		@Override
		public Icon
		getOpenIcon() { return Res.iconFolderOpen16; }
	
		@Override
		public Icon
		getInstrumentIcon() { return Res.iconInstrument16; }
	
		@Override
		public Icon
		getGigInstrumentIcon() { return Res.iconInstrument16; }
	}
	
	private static class TableView implements InstrumentsDbTableView {
		@Override
		public Icon
		getFolderIcon() { return Res.iconFolder16; }
	
		@Override
		public Icon
		getInstrumentIcon() { return Res.iconInstrument16; }
	
		@Override
		public Icon
		getGigInstrumentIcon() { return Res.iconInstrument16; }
	}
	
	private class IconSet implements BasicIconSet {
		@Override
		public ImageIcon
		getApplicationIcon() { return Res.iconAppIcon; }
		
		@Override
		public Icon
		getBack16Icon() { return Res.iconBack16; }
	
		@Override
		public Icon
		getUp16Icon() { return Res.iconUp16; }
	
		@Override
		public Icon
		getForward16Icon() { return Res.iconNext16; }
		
		@Override
		public Icon
		getReload16Icon() { return Res.iconReload16; }
		
		@Override
		public Icon
		getPreferences16Icon() { return Res.iconPreferences16; }
		
		@Override
		public Icon
		getWarning32Icon() { return Res.iconWarning32; }
		
		@Override
		public Icon
		getQuestion32Icon() { return Res.iconQuestion32; }
	}
	
	@Override
	public boolean
	getInstrumentsDbSupport() { return true; }
}
