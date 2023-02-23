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

package org.jsampler.view.swing;

import java.awt.Dialog;
import java.awt.Frame;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.Vector;
import java.util.logging.Level;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

import org.jsampler.CC;
import org.jsampler.JSampler;
import org.jsampler.LSConsoleModel;
import org.jsampler.SamplerChannelModel;
import org.jsampler.Server;
import org.jsampler.event.ListSelectionEvent;
import org.jsampler.event.ListSelectionListener;
import org.jsampler.event.SamplerChannelListEvent;
import org.jsampler.event.SamplerChannelListListener;
import org.jsampler.view.JSChannel;
import org.jsampler.view.JSChannelsPane;
import org.jsampler.view.JSMainFrame;
import org.jsampler.view.JSViewConfig;
import org.jsampler.view.SessionViewConfig.ChannelConfig;

import static org.jsampler.JSPrefs.MANUAL_SERVER_SELECT_ON_STARTUP;

/**
 * Defines the skeleton of a Swing main frame.
 * @author Grigor Iliev
 */
public abstract class SwingMainFrame<CP extends SwingChannelsPane> extends JFrame implements JSMainFrame<CP> {
	private final Vector<CP> chnPaneList = new Vector<CP>();
	private boolean autoUpdateChannelListUI = true;
	
	/** Creates a new instance of <code>SwingMainFrame</code>. */
	public
	SwingMainFrame() {
		super(JSampler.NAME + ' ' + JSampler.VERSION);
		
		CC.getSamplerModel().addSamplerChannelListListener(new EventHandler());
		
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void
			windowClosing(WindowEvent we) { CC.getMainFrame().onWindowClose(); }
		});
		
		getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put (
			KeyStroke.getKeyStroke(KeyEvent.VK_G, KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK),
			"RunGarbageCollector"
		);
		
		getRootPane().getActionMap().put ("RunGarbageCollector", new AbstractAction() {
			public void
			actionPerformed(ActionEvent e) {
				System.gc();
			}
		});
	}
	
	/**
	 * Invoked when this window is about to close.
	 * Don't forget to call <code>super.onWindowClose()</code> at the end,
	 * when override this method.
	 */
	@Override
	public void
	onWindowClose() { CC.cleanExit(); }
	
	protected Vector<ListSelectionListener> channelsPaneListeners = 
		new Vector<ListSelectionListener>();
	
	/**
	 * Registers the specified listener for receiving event messages.
	 * @param l The <code>ListSelectionListener</code> to register.
	 */
	@Override
	public void
	addChannelsPaneSelectionListener(ListSelectionListener l) {
		channelsPaneListeners.add(l);
	}
	
	/**
	 * Removes the specified listener.
	 * @param l The <code>ListSelectionListener</code> to remove.
	 */
	@Override
	public void
	removeChannelsPaneSelectionListener(ListSelectionListener l) {
		channelsPaneListeners.remove(l);
	}
	
	protected void
	fireChannelsPaneSelectionChanged() {
		int i = getChannelsPaneIndex(getSelectedChannelsPane());
		ListSelectionEvent e = new ListSelectionEvent(this, i, i, false);
		for(ListSelectionListener l : channelsPaneListeners) l.valueChanged(e);
	}
	
	/**
	 * Returns a list containing all channels' panes added to the view.
	 * @return A list containing all channels' panes added to the view.
	 * @see #addChannelsPane
	 * @see #removeChannelsPane
	 */
	@Override
	public Vector<CP>
	getChannelsPaneList() { return chnPaneList; }
	
	/**
	 * Return the channels' pane at the specified position.
	 * @param idx The position of the channels' pane to be returned.
	 * @return The channels' pane at the specified position.
	 */
	@Override
	public CP
	getChannelsPane(int idx) { return chnPaneList.get(idx); }
	
	/**
	 * Adds the specified channels' pane to the view.
	 * @param chnPane The channels' pane to be added.
	 */
	@Override
	public void
	addChannelsPane(CP chnPane) {
		chnPaneList.add(chnPane);
		firePropertyChange("channelLaneAdded", null, chnPane);
	}
	
	/**
	 * Removes the specified channels' pane from the view.
	 * Override this method to remove <code>chnPane</code> from the view,
	 * and don't forget to call <code>super.removeChannelsPane(chnPane);</code>.
	 * @param chnPane The channels' pane to be removed.
	 * @return <code>true</code> if the specified channels' pane
	 * is actually removed from the view, <code>false</code> otherwise.
	 */
	@Override
	public boolean
	removeChannelsPane(CP chnPane) {
		boolean b = chnPaneList.remove(chnPane);
		firePropertyChange("channelLaneRemoved", null, chnPane);
		return b;
	}
	
	/**
	 * Gets the current number of channels' panes added to the view.
	 * @return The current number of channels' panes added to the view.
	 */
	@Override
	public int
	getChannelsPaneCount() { return chnPaneList.size(); }
	
	/**
	 * Returns the index of the specified channels pane, or -1 if
	 * the specified channels pane is not found.
	 */
	@Override
	public int
	getChannelsPaneIndex(CP chnPane) {
		return chnPaneList.indexOf(chnPane);
	}
	
	/**
	 * Gets the LS Console model.
	 * @return The LS Console model or <code>null</code>.
	 */
	@Override
	public LSConsoleModel
	getLSConsoleModel() { return null; }
	
	private class EventHandler implements SamplerChannelListListener {
		/**
		 * Invoked when a new sampler channel is created.
		 * @param e A <code>SamplerChannelListEvent</code>
		 * instance providing the event information.
		 */
		public void
		channelAdded(SamplerChannelListEvent e) {
			if(e.getChannelModel() == null) return;
			Integer id = e.getChannelModel().getChannelId();
			if(findChannel(id) != null) {
				CC.getLogger().log(Level.WARNING, "JSMainFrame.channelExist!", id);
				return;
			}
			
			ChannelConfig config = null;
			JSViewConfig viewConfig = CC.getViewConfig();
			if(viewConfig != null && viewConfig.getSessionViewConfig() != null) {
				config = viewConfig.getSessionViewConfig().pollChannelConfig();
			}
			
			if(config == null) {
				getSelectedChannelsPane().addChannel(e.getChannelModel());
			} else {
				int i = config.channelsPanel;
				if(i >= 0 && i < getChannelsPaneCount()) {
					getChannelsPane(i).addChannel(e.getChannelModel(), config);
				} else {
					getSelectedChannelsPane().addChannel(e.getChannelModel(), config);
				}
			}
		}
	
		/**
		 * Invoked when a sampler channel is removed.
		 * @param e A <code>SamplerChannelListEvent</code>
		 * instance providing the event information.
		 */
		public void
		channelRemoved(SamplerChannelListEvent e) {
			removeChannel(e.getChannelModel().getChannelId());
		}
	}
	
	
	/**
	 * Searches for the first occurrence of a channel with numerical ID <code>id</code>.
	 * @return The first occurrence of a channel with numerical ID <code>id</code> or
	 * <code>null</code> if there is no channel with numerical ID <code>id</code>.
	 */
	@Override
	public JSChannel
	findChannel(int id) {
		if(id < 0) return null;
		
		for(JSChannelsPane cp : getChannelsPaneList()) {
			for(JSChannel c : cp.getChannels()) if(c.getChannelId() == id) return c;
		}
		
		return null;
	}
	
	/**
	 * Removes the first occurrence of a channel with numerical ID <code>id</code>.
	 * This method is invoked when a sampler channel is removed in the back-end.
	 * @return The removed channel or <code>null</code>
	 * if there is no channel with numerical ID <code>id</code>.
	 */
	@Override
	public JSChannel
	removeChannel(int id) {
		if(id < 0) return null;
		
		for(JSChannelsPane cp : getChannelsPaneList()) {
			for(JSChannel c : cp.getChannels()) {
				if(c.getChannelId() == id) {
					cp.removeChannel(c);
					firePropertyChange("channelRemoved", null, c);
					return c;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Gets the zero-based position of the specified sampler channel
	 * in the channels pane, to which the channel is added.
	 * Note that the position may change when adding/removing sampler channels.
	 * @return The zero-based position of the specified sampler channel
	 * in the channels pane, or -1 if the specified channels is not found.
	 */
	@Override
	public int
	getChannelNumber(SamplerChannelModel channel) {
		if(channel == null) return -1;
		
		for(int i = 0; i < getChannelsPaneCount(); i++) {
			JSChannelsPane chnPane = getChannelsPane(i);
			for(int j = 0; j < chnPane.getChannelCount(); j++) {
				if(chnPane.getChannel(j).getChannelId() == channel.getChannelId()) {
					return j;
				}
			}
		}
		
		return -1;
	}
	
	/**
	 * Returns a string in the format <code>channelPaneNumber.channelNumber</code>,
	 * where <code>channelPaneNumber</code> is the one-based number of the channels
	 * pane containing the specified channel and <code>channelNumber</code> is the
	 * one-based number of the channel's position in the channels pane.
	 * Note that this path may change when adding/removing channels/channels panes.
	 * @return The channels path, or <code>null</code> if the specified channels is not found.
	 */
	@Override
	public String
	getChannelPath(SamplerChannelModel channel) {
		if(channel == null) return null;
		
		for(int i = 0; i < getChannelsPaneCount(); i++) {
			JSChannelsPane chnPane = getChannelsPane(i);
			for(int j = 0; j < chnPane.getChannelCount(); j++) {
				if(chnPane.getChannel(j).getChannelId() == channel.getChannelId()) {
					return (i + 1) + "." + (j + 1);
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Gets the zero-based number of the channels pane,
	 * to which the specified sampler channel is added.
	 * Note that the can be moved from one channels pane to another.
	 * @return The zero-based index of the channels pane,
	 * to which the specified sampler channel is added, or
	 * -1 if the specified channels is not found.
	 */
	@Override
	public int
	getChannelsPaneNumber(SamplerChannelModel channel) {
		if(channel == null) return -1;
		
		for(int i = 0; i < getChannelsPaneCount(); i++) {
			JSChannelsPane chnPane = getChannelsPane(i);
			for(int j = 0; j < chnPane.getChannelCount(); j++) {
				if(chnPane.getChannel(j).getChannelId() == channel.getChannelId()) {
					return i;
				}
			}
		}
		
		return -1;
	}
	
	/**
	 * Determines whether the channel list UI should be automatically updated
	 * when channel is added/removed. The default value is <code>true</code>.
	 */
	@Override
	public boolean
	getAutoUpdateChannelListUI() { return autoUpdateChannelListUI; }
	
	/**
	 * Determines whether the channel list UI should be automatically updated
	 * when channel is added/removed.
	 */
	@Override
	public void
	setAutoUpdateChannelListUI(boolean b) {
		if(b == autoUpdateChannelListUI) return;
		
		autoUpdateChannelListUI = b;
		for(JSChannelsPane cp : getChannelsPaneList()) {
			cp.setAutoUpdate(b);
		}
	}
	
	/**
	 * Updates the channel list UI.
	 */
	@Override
	public void
	updateChannelListUI() {
		for(JSChannelsPane cp : getChannelsPaneList()) {
			cp.updateChannelListUI();
		}
	}
	
	/** Shows a detailed error information about the specified exception. */
	@Override
	public void
	showDetailedErrorMessage(String err, String details) {
		showDetailedErrorMessage(SHF.getMainFrame(), err, details);
	}
	
	/**
	 * Shows a detailed error information about the specified exception.
	 */
	public abstract void showDetailedErrorMessage(Frame owner, String err, String details);
	
	/**
	 * Shows a detailed error information about the specified exception.
	 */
	public abstract void showDetailedErrorMessage(Dialog owner, String err, String details);
	
	/**
	 * Get the server address to which to connect (asynchronously if needed)
	 * and pass it to <code>r.run()</code>.
	 * If the server should be manually selected, a dialog asking
	 * the user to choose a server is displayed.
	 */
	@Override
	public void
	getServer(CC.Run<Server> r) {
		getServer(r, CC.preferences().getBoolProperty(MANUAL_SERVER_SELECT_ON_STARTUP));
		
	}
	
	/**
	 * Get the server address to which to connect (asynchronously if needed)
	 * and pass it to <code>r.run()</code>.
	 * If the server should be manually selected, a dialog asking
	 * the user to choose a server is displayed.
	 * @param manualSelect Determines whether the server should be manually selected.
	 */
	@Override
	public void
	getServer(CC.Run<Server> r, boolean manualSelect) {
		r.run(getServer(manualSelect));
	}
}
