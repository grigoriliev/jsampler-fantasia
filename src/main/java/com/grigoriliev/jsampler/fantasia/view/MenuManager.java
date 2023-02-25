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

import java.awt.event.KeyEvent;

import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

import com.grigoriliev.jsampler.CC;

/**
 *
 * @author Grigor Iliev
 */
public class MenuManager implements com.grigoriliev.jsampler.event.ListSelectionListener {
	private static MenuManager menuManager = null;
	private final Vector<ChannelViewGroup> channelViewGroups =
		new Vector<ChannelViewGroup>();
	
	private
	MenuManager() {
		CC.getMainFrame().addChannelsPaneSelectionListener(this);
		
		for(int i = 0; i < CC.getMainFrame().getChannelsPaneCount(); i++) {
			CC.getMainFrame().getChannelsPane(i).addListSelectionListener(this);
		}
	}
	
	public static MenuManager
	getMenuManager() {
		if(menuManager == null) menuManager = new MenuManager();
		return menuManager;
	}
	
	@Override
	public void
	valueChanged(com.grigoriliev.jsampler.event.ListSelectionEvent e) {
		if(e.getValueIsAdjusting()) return;
		updateChannelViewGroups();
	}
	
	public void
	updateChannelViewGroups() {
		boolean sv = false, nv = false;
		
		int i = A4n.SetView.getViewCount(ChannelView.Type.SMALL);
		int j = A4n.SetView.getViewCount(ChannelView.Type.NORMAL);
		
		if( (i != 0 && j != 0) || (i == 0 && j == 0) );
		else if(i != 0) sv = true;
		else nv = true;
		
		boolean enable = i != 0 || j != 0;
		
		for(ChannelViewGroup g : channelViewGroups) {
			g.rbmiSmallView.setSelected(sv);
			g.rbmiNormalView.setSelected(nv);
			if(!sv && !nv) g.clearSelection();
			g.setEnabled(enable);
		}
	}
	
	public void
	registerChannelViewGroup(ChannelViewGroup group) {
		channelViewGroups.add(group);
		if(channelViewGroups.size() < 2) updateChannelViewGroups();
		else {
			boolean sv = channelViewGroups.get(0).rbmiSmallView.isSelected();
			boolean nv = channelViewGroups.get(0).rbmiNormalView.isSelected();
			group.rbmiSmallView.setSelected(sv);
			group.rbmiNormalView.setSelected(nv);
		}
	}
	
	public void
	unregisterChannelViewGroup(ChannelViewGroup group) {
		channelViewGroups.remove(group);
	}
	
	public static class ChannelViewGroup extends ButtonGroup {
		private JRadioButtonMenuItem rbmiSmallView;
		private JRadioButtonMenuItem rbmiNormalView;
		
		private boolean alwaysEnabled;
		
		public
		ChannelViewGroup() { this(false, false); }
		
		public
		ChannelViewGroup(boolean alwaysEnabled, boolean noAccel) {
			this.alwaysEnabled = alwaysEnabled;
			
			rbmiSmallView = new JRadioButtonMenuItem(A4n.a4n.setSmallView);
			KeyStroke k = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.ALT_MASK);
			if(!noAccel) rbmiSmallView.setAccelerator(k);
			
			rbmiNormalView = new JRadioButtonMenuItem(A4n.a4n.setNormalView);
			k = KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.ALT_MASK);
			if(!noAccel) rbmiNormalView.setAccelerator(k);
			
			add(rbmiSmallView);
			add(rbmiNormalView);
		}
		
		public JRadioButtonMenuItem[]
		getMenuItems() {
			JRadioButtonMenuItem[] m = new JRadioButtonMenuItem[2];
			m[0] = rbmiSmallView;
			m[1] = rbmiNormalView;
			
			return m;
		}
		
		public void
		setEnabled(boolean b) {
			if(alwaysEnabled) return;
			
			rbmiSmallView.setEnabled(b);
			rbmiNormalView.setEnabled(b);
		}
	}
}
