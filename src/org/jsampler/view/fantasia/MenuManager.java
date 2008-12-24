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

package org.jsampler.view.fantasia;

import java.awt.event.KeyEvent;

import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jsampler.CC;

import static org.jsampler.view.fantasia.A4n.a4n;

/**
 *
 * @author Grigor Iliev
 */
public class MenuManager implements ListSelectionListener {
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
	valueChanged(ListSelectionEvent e) {
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
			
			rbmiSmallView = new JRadioButtonMenuItem(a4n.setSmallView);
			KeyStroke k = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.ALT_MASK);
			if(!noAccel) rbmiSmallView.setAccelerator(k);
			
			rbmiNormalView = new JRadioButtonMenuItem(a4n.setNormalView);
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
