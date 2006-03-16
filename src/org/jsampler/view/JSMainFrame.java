/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005 Grigor Kirilov Iliev
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

package org.jsampler.view;

import java.awt.Dimension;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.Vector;
import java.util.logging.Level;

import javax.swing.JFrame;

import org.jsampler.CC;
import org.jsampler.JSampler;
import org.jsampler.Prefs;

import org.jsampler.event.SamplerChannelListEvent;
import org.jsampler.event.SamplerChannelListListener;

/**
 *
 * @author Grigor Iliev
 */
public abstract class JSMainFrame extends JFrame {
	private final Vector<JSChannelsPane> chnPaneList = new Vector<JSChannelsPane>();
	
	public
	JSMainFrame() {
		super(JSampler.NAME + ' ' + JSampler.VERSION);
		
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void
			windowClosing(WindowEvent we) { onWindowClose(); }
		});
		
		CC.getSamplerModel().addSamplerChannelListListener(new EventHandler());
	}
	
	private void
	onWindowClose() {
		if(Prefs.getSaveWindowProperties()) {
			Prefs.setWindowMaximized (
				(getExtendedState() & MAXIMIZED_BOTH) == MAXIMIZED_BOTH
			);
			
			setVisible(false);
			if(Prefs.getWindowMaximized()) {
				//setExtendedState(getExtendedState() & ~MAXIMIZED_BOTH);
				CC.cleanExit();
				return;
			}
			
			java.awt.Point p = getLocation();
			Dimension d = getSize();
			StringBuffer sb = new StringBuffer();
			sb.append(p.x).append(',').append(p.y).append(',');
			sb.append(d.width).append(',').append(d.height);
			Prefs.setWindowSizeAndLocation(sb.toString());
		}
		
		CC.cleanExit();
	}
	
	public Vector<JSChannelsPane>
	getChannelsPaneList() { return chnPaneList; }
	
	public JSChannelsPane
	getChannelsPane(int idx) { return chnPaneList.get(idx); }
	
	public void
	addChannelsPane(JSChannelsPane chnPane) { chnPaneList.add(chnPane); }
	
	public boolean
	removeChannelsPane(JSChannelsPane chnPane) { return chnPaneList.remove(chnPane); }
	
	public int
	getChannelsPaneCount() { return chnPaneList.size(); }
	
	public abstract void insertChannelsPane(JSChannelsPane pane, int idx);
	public abstract JSChannelsPane getSelectedChannelsPane();
	public abstract void setSelectedChannelsPane(JSChannelsPane pane);
	
	private class EventHandler implements SamplerChannelListListener {
		/**
		 * Invoked when a new sampler channel is created.
		 * @param e A <code>SamplerChannelListEvent</code>
		 * instance providing the event information.
		 */
		public void
		channelAdded(SamplerChannelListEvent e) {
			Integer id = e.getChannelModel().getChannelID();
			if(findChannel(id) != null) {
				CC.getLogger().log(Level.WARNING, "JSMainFrame.channelExist!", id);
				return;
			}
			
			getSelectedChannelsPane().addChannel(e.getChannelModel());
		}
	
		/**
		 * Invoked when a sampler channel is removed.
		 * @param e A <code>SamplerChannelListEvent</code>
		 * instance providing the event information.
		 */
		public void
		channelRemoved(SamplerChannelListEvent e) {
			removeChannel(e.getChannelModel().getChannelID());
		}
	}
	
	/**
	 * Searches for the first occurence of a channel with numerical ID <code>id</code>.
	 * @return The first occurence of a channel with numerical ID <code>id</code> or
	 * <code>null</code> if there is no channel with numerical ID <code>id</code>.
	 */
	public JSChannel
	findChannel(int id) {
		if(id < 0) return null;
		
		for(JSChannelsPane cp : getChannelsPaneList()) {
			for(JSChannel c : cp.getChannels()) if(c.getChannelID() == id) return c;
		}
		
		return null;
	}
	
	/**
	 * Removes the first occurence of a channel with numerical ID <code>id</code>.
	 * This method is invoked when a sampler channel is removed in the back-end.
	 * @return The removed channel or <code>null</code>
	 * if there is no channel with numerical ID <code>id</code>.
	 */
	public JSChannel
	removeChannel(int id) {
		if(id < 0) return null;
		
		for(JSChannelsPane cp : getChannelsPaneList()) {
			for(JSChannel c : cp.getChannels()) {
				if(c.getChannelID() == id) {
					cp.removeChannel(c);
					return c;
				}
			}
		}
		
		return null;
	}
}
