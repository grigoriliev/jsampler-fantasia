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

package org.jsampler.view;

import java.util.Vector;

import org.jsampler.CC;
import org.jsampler.LSConsoleModel;
import org.jsampler.SamplerChannelModel;
import org.jsampler.Server;
import org.jsampler.event.ListSelectionListener;


/**
 * Defines the skeleton of a JSampler's main frame.
 * @author Grigor Iliev
 */
public interface JSMainFrame<CP extends JSChannelsPane> {
	/**
	 * Invoked when this window is about to close.
	 * Don't forget to call <code>super.onWindowClose()</code> at the end,
	 * when override this method.
	 */
	public void onWindowClose();
	
	/**
	 * Invoked on startup when no JSampler home directory is specified
	 * or the specified JSampler home directory doesn't exist.
	 * This method should ask the user to specify a JSampler
	 * home directory and then set the specified JSampler home directory using
	 * {@link org.jsampler.CC#setJSamplerHome} method.
	 * @see org.jsampler.CC#getJSamplerHome
	 * @see org.jsampler.CC#setJSamplerHome
	 */
	public void installJSamplerHome();
	
	/** Shows a detailed error information about the specified exception. */
	public void showDetailedErrorMessage(String err, String details);
	
	public void handleConnectionFailure();
	
	/**
	 * Registers the specified listener for receiving event messages.
	 * @param l The <code>ListSelectionListener</code> to register.
	 */
	public void
	addChannelsPaneSelectionListener(ListSelectionListener l);
	
	/**
	 * Removes the specified listener.
	 * @param l The <code>ListSelectionListener</code> to remove.
	 */
	public void
	removeChannelsPaneSelectionListener(ListSelectionListener l);
	
	/**
	 * Returns a list containing all channels' panes added to the view.
	 * @return A list containing all channels' panes added to the view.
	 * @see #addChannelsPane
	 * @see #removeChannelsPane
	 */
	public Vector<CP> getChannelsPaneList();
	
	/**
	 * Return the channels' pane at the specified position.
	 * @param idx The position of the channels' pane to be returned.
	 * @return The channels' pane at the specified position.
	 */
	public CP getChannelsPane(int idx);
	
	/**
	 * Adds the specified channels' pane to the view.
	 * @param chnPane The channels' pane to be added.
	 */
	public void addChannelsPane(CP chnPane);
	
	/**
	 * Removes the specified channels' pane from the view.
	 * Override this method to remove <code>chnPane</code> from the view,
	 * and don't forget to call <code>super.removeChannelsPane(chnPane);</code>.
	 * @param chnPane The channels' pane to be removed.
	 * @return <code>true</code> if the specified code>JSChannelsPane</code>
	 * is actually removed from the view, <code>false</code> otherwise.
	 */
	public boolean removeChannelsPane(CP chnPane);
	
	/**
	 * Gets the current number of channels' panes added to the view.
	 * @return The current number of channels' panes added to the view.
	 */
	public int getChannelsPaneCount();
	
	/**
	 * Returns the index of the specified channels pane, or -1 if
	 * the specified channels pane is not found.
	 */
	public int getChannelsPaneIndex(CP chnPane);
	
	public void setVisible(boolean b);
	
	/**
	 * Inserts the specified channels' pane at the specified position
	 * in the view and in the channels' pane list.
	 * Where and how this pane will be shown depends on the view/GUI implementation.
	 * Note that some GUI implementation may have only one pane containing sampler channels.
	 * @param pane The channels' pane to be inserted.
	 * @param idx Specifies the position of the channels' pane.
	 * @see #getChannelsPaneList
	 */
	public void insertChannelsPane(CP pane, int idx);
	
	/**
	 * Gets the channels' pane that is currently shown,
	 * or has the focus if more than one channels' panes are shown.
	 * If the GUI implementation has only one pane containing sampler channels,
	 * than this method should always return that pane (the channels' pane
	 * with index 0).
	 * @return The selected channels' pane.
	 */
	public CP getSelectedChannelsPane();
	
	/**
	 * Get the server address to which to connect (asynchronously if needed)
	 * and pass it to <code>r.run()</code>.
	 * If the server should be manually selected, a dialog asking
	 * the user to choose a server is displayed.
	 */
	public void getServer(CC.Run<Server> r);
	
	/**
	 * Get the server address to which to connect (asynchronously if needed)
	 * and pass it to <code>r.run()</code>.
	 * If the server should be manually selected, a dialog asking
	 * the user to choose a server is displayed.
	 * @param manualSelect Determines whether the server should be manually selected.
	 */
	public void getServer(CC.Run<Server> r, boolean manualSelect);
	
	/**
	 * Gets the server address to which to connect. If the server should be
	 * manually selected, a dialog asking the user to choose a server is displayed.
	 */
	public Server getServer();
	
	/**
	 * Gets the server address to which to connect. If the server should be
	 * manually selected, a dialog asking the user to choose a server is displayed.
	 * @param manualSelect Determines whether the server should be manually selected.
	 */
	public Server getServer(boolean manualSelect);

	/**
	 * Gets the LS Console model.
	 * @return The LS Console model or <code>null</code>.
	 */
	public LSConsoleModel getLSConsoleModel();
	
	/**
	 * Sets the channels' pane to be selected.
	 * Note that all registered listeners should be notified
	 * when the selection is changed.
	 * @param pane The channels' pane to be shown.
	 * @see #fireChannelsPaneSelectionChanged
	 */
	public void setSelectedChannelsPane(CP pane);
	
	/**
	 * Searches for the first occurence of a channel with numerical ID <code>id</code>.
	 * @return The first occurence of a channel with numerical ID <code>id</code> or
	 * <code>null</code> if there is no channel with numerical ID <code>id</code>.
	 */
	public JSChannel findChannel(int id);
	
	/**
	 * Removes the first occurrence of a channel with numerical ID <code>id</code>.
	 * This method is invoked when a sampler channel is removed in the back-end.
	 * @return The removed channel or <code>null</code>
	 * if there is no channel with numerical ID <code>id</code>.
	 */
	public JSChannel removeChannel(int id);
	
	/**
	 * Gets the zero-based position of the specified sampler channel
	 * in the channels pane, to which the channel is added.
	 * Note that the position may change when adding/removing sampler channels.
	 * @return The zero-based position of the specified sampler channel
	 * in the channels pane, or -1 if the specified channels is not found.
	 */
	public int getChannelNumber(SamplerChannelModel channel);
	
	/**
	 * Returns a string in the format <code>channelPaneNumber.channelNumber</code>,
	 * where <code>channelPaneNumber</code> is the one-based number of the channels
	 * pane containing the specified channel and <code>channelNumber</code> is the
	 * one-based number of the channel's position in the channels pane.
	 * Note that this path may change when adding/removing channels/channels panes.
	 * @return The channels path, or <code>null</code> if the specified channels is not found.
	 */
	public String getChannelPath(SamplerChannelModel channel);
	
	/**
	 * Gets the zero-based number of the channels pane,
	 * to which the specified sampler channel is added.
	 * Note that the can be moved from one channels pane to another.
	 * @return The zero-based index of the channels pane,
	 * to which the specified sampler channel is added, or
	 * -1 if the specified channels is not found.
	 */
	public int getChannelsPaneNumber(SamplerChannelModel channel);
	
	/**
	 * Sends the specified script to the backend.
	 * @param script The file name of the script to run.
	 */
	public void runScript(String script);
	
	/**
	 * Determines whether the channel list UI should be automatically updated
	 * when channel is added/removed. The default value is <code>true</code>.
	 */
	public boolean getAutoUpdateChannelListUI();
	
	/**
	 * Determines whether the channel list UI should be automatically updated
	 * when channel is added/removed.
	 */
	public void setAutoUpdateChannelListUI(boolean b);
	
	/**
	 * Updates the channel list UI.
	 */
	public void updateChannelListUI();
}
