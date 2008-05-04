/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2007 Grigor Iliev <grigor@grigoriliev.com>
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

import java.awt.Dialog;
import java.awt.Dimension;
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
import org.jsampler.Prefs;
import org.jsampler.Server;

import org.jsampler.event.SamplerChannelListEvent;
import org.jsampler.event.SamplerChannelListListener;


/**
 * Defines the skeleton of a JSampler's main frame.
 * @author Grigor Iliev
 */
public abstract class JSMainFrame extends JFrame {
	private final Vector<JSChannelsPane> chnPaneList = new Vector<JSChannelsPane>();
	private boolean autoUpdateChannelListUI = true;
	
	/** Creates a new instance of <code>JSMainFrame</code>. */
	public
	JSMainFrame() {
		super(JSampler.NAME + ' ' + JSampler.VERSION);
		
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void
			windowClosing(WindowEvent we) { onWindowClose(); }
		});
		
		CC.getSamplerModel().addSamplerChannelListListener(new EventHandler());
		
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
	protected void
	onWindowClose() {
		CC.cleanExit();
	}
	
	/**
	 * Invoked on startup when no JSampler home directory is specified
	 * or the specified JSampler home directory doesn't exist.
	 * This method should ask the user to specify a JSampler
	 * home directory and then set the specified JSampler home directory using
	 * {@link org.jsampler.CC#setJSamplerHome} method.
	 * @see org.jsampler.CC#getJSamplerHome
	 * @see org.jsampler.CC#setJSamplerHome
	 */
	public abstract void installJSamplerHome();
	
	/**
	 * Shows a detailed error information about the specified exception.
	 */
	public abstract void showDetailedErrorMessage(Frame owner, String err, String details);
	
	/**
	 * Shows a detailed error information about the specified exception.
	 */
	public abstract void showDetailedErrorMessage(Dialog owner, String err, String details);
	
	/**
	 * Returns a list containing all <code>JSChannelsPane</code>s added to the view.
	 * @return A list containing all <code>JSChannelsPane</code>s added to the view.
	 * @see #addChannelsPane
	 * @see #removeChannelsPane
	 */
	public Vector<JSChannelsPane>
	getChannelsPaneList() { return chnPaneList; }
	
	/**
	 * Return the <code>JSChannelsPane</code> at the specified position.
	 * @param idx The position of the <code>JSChannelsPane</code> to be returned.
	 * @return The <code>JSChannelsPane</code> at the specified position.
	 */
	public JSChannelsPane
	getChannelsPane(int idx) { return chnPaneList.get(idx); }
	
	/**
	 * Adds the specified <code>JSChannelsPane</code> to the view.
	 * @param chnPane The <code>JSChannelsPane</code> to be added.
	 */
	public void
	addChannelsPane(JSChannelsPane chnPane) { chnPaneList.add(chnPane); }
	
	/**
	 * Removes the specified <code>JSChannelsPane</code> from the view.
	 * Override this method to remove <code>chnPane</code> from the view,
	 * and don't forget to call <code>super.removeChannelsPane(chnPane);</code>.
	 * @param chnPane The <code>JSChannelsPane</code> to be removed.
	 * @return <code>true</code> if the specified code>JSChannelsPane</code>
	 * is actually removed from the view, <code>false</code> otherwise.
	 */
	public boolean
	removeChannelsPane(JSChannelsPane chnPane) { return chnPaneList.remove(chnPane); }
	
	/**
	 * Gets the current number of <code>JSChannelsPane</code>s added to the view.
	 * @return The current number of <code>JSChannelsPane</code>s added to the view.
	 */
	public int
	getChannelsPaneCount() { return chnPaneList.size(); }
	
	/**
	 * Inserts the specified <code>JSChannelsPane</code> at the specified position
	 * in the view and in the code>JSChannelsPane</code> list.
	 * Where and how this pane will be shown depends on the view/GUI implementation.
	 * Note that some GUI implementation may have only one pane containing sampler channels.
	 * @param pane The <code>JSChannelsPane</code> to be inserted.
	 * @param idx Specifies the position of the <code>JSChannelsPane</code>.
	 * @see #getChannelsPaneList
	 */
	public abstract void insertChannelsPane(JSChannelsPane pane, int idx);
	
	/**
	 * Gets the <code>JSChannelsPane</code> that is currently shown,
	 * or has the focus if more than one channels' panes are shown.
	 * If the GUI implementation has only one pane containing sampler channels,
	 * than this method should always return that pane (the <code>JSChannelsPane</code>
	 * with index 0).
	 * @return The selected <code>JSChannelsPane</code>.
	 */
	public abstract JSChannelsPane getSelectedChannelsPane();
	
	/**
	 * Gets the server address to which to connect. If the server should be
	 * manually selected, a dialog asking the user to choose a server is displayed.
	 */
	public abstract Server getServer();
	
	/**
	 * Gets the server address to which to connect. If the server should be
	 * manually selected, a dialog asking the user to choose a server is displayed.
	 * @param manualSelect Determines whether the server should be manually selected.
	 */
	public abstract Server getServer(boolean manualSelect);
	
	/**
	 * Sets the <code>JSChannelsPane</code> to be selected.
	 * @param pane The <code>JSChannelsPane</code> to be shown.
	 */
	public abstract void setSelectedChannelsPane(JSChannelsPane pane);
	
	private class EventHandler implements SamplerChannelListListener {
		/**
		 * Invoked when a new sampler channel is created.
		 * @param e A <code>SamplerChannelListEvent</code>
		 * instance providing the event information.
		 */
		public void
		channelAdded(SamplerChannelListEvent e) {
			Integer id = e.getChannelModel().getChannelId();
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
			removeChannel(e.getChannelModel().getChannelId());
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
			for(JSChannel c : cp.getChannels()) if(c.getChannelId() == id) return c;
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
				if(c.getChannelId() == id) {
					cp.removeChannel(c);
					return c;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Determines whether the channel list UI should be automatically updated
	 * when channel is added/removed. The default value is <code>true</code>.
	 */
	public boolean
	getAutoUpdateChannelListUI() { return autoUpdateChannelListUI; }
	
	/**
	 * Determines whether the channel list UI should be automatically updated
	 * when channel is added/removed.
	 */
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
	public void
	updateChannelListUI() {
		for(JSChannelsPane cp : getChannelsPaneList()) {
			cp.updateChannelListUI();
		}
	}
}
