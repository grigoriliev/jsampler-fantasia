/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2006 Grigor Iliev <grigor@grigoriliev.com>
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

import javax.swing.JPanel;
import javax.swing.event.ListSelectionListener;

import org.jsampler.SamplerChannelModel;


/**
 * This class defines the skeleton of a pane containg sampler channels.
 * @author Grigor Iliev
 */
public abstract class JSChannelsPane extends JPanel {
	/** The key used for reporting title's property change. */
	public final static String TITLE = "ChannelsPaneTitle";
	
	private String title;
	
	/** Creates a new instance of <code>JSChannelsPane</code>. */
	public
	JSChannelsPane(String title) { this.title = title; }
	
	/**
	 * Returns the title of this <code>JSChannelsPane</code>.
	 * @return The title of this <code>JSChannelsPane</code>.
	 */
	public String
	getTitle() { return title; }
	
	/**
	 * Sets the title of this <code>JSChannelsPane</code>.
	 * @param title The new title of this <code>JSChannelsPane</code>.
	 */
	public void
	setTitle(String title) {
		if(this.title.equals(title)) return;
		
		String oldTitle = this.title;
		this.title = title;
		firePropertyChange(TITLE, oldTitle, title);
	}
	
	/**
	 * Returns the title of this <code>JSChannelsPane</code>.
	 * @return The title of this <code>JSChannelsPane</code>.
	 */
	public String
	toString() { return getTitle(); }
	
	/**
	 * Adds new channel to this channels pane.
	 * @param channelModel The sampler channel model to be used by the new channel.
	 */
	public abstract void addChannel(SamplerChannelModel channelModel);
	
	/**
	 * Adds the specified channels to this channels pane.
	 * @param chns The channels to be added.
	 */
	public abstract void addChannels(JSChannel[] chns);
	
	/**
	 * Removes the specified channel from this channels pane.
	 * This method is invoked when a sampler channel is removed in the back-end.
	 * @param chn The channel to be removed from this channels pane.
	 */
	public abstract void removeChannel(JSChannel chn);
	
	/**
	 * Determines whether there is at least one selected channel.
	 * @return <code>true</code> if there is at least one selected channel,
	 * <code>false</code> otherwise.
	 */
	public abstract boolean hasSelectedChannel();
	
	/**
	 * Gets the first channel in this channels pane.
	 * @return The first channel in this channels pane or <code>null</code> if 
	 * the channels pane is empty.
	 */
	public abstract JSChannel getFirstChannel();
	
	/**
	 * Gets the last channel in this channels pane.
	 * @return The last channel in this channels pane or <code>null</code> if 
	 * the channels pane is empty.
	 */
	public abstract JSChannel getLastChannel();
	
	/**
	 * Gets the channel at the specified index.
	 * @return The channel at the specified index.
	 * @throws ArrayIndexOutOfBoundsException If the index is out of range.
	 */
	public abstract JSChannel getChannel(int idx);
	
	/**
	 * Gets an array of all channels in this channels pane.
	 * @return An array of all channels in this channels pane.
	 */
	public abstract JSChannel[] getChannels();
	
	/**
	 * Gets the number of channels in this channels pane.
	 * @return The number of channels in this channels pane.
	 */
	public abstract int getChannelCount();
	
	/**
	 * Gets an array of all selected channels.
	 * The channels are sorted in increasing index order.
	 * @return The selected channels or an empty array if nothing is selected.
	 */
	public abstract JSChannel[] getSelectedChannels();
	
	/**
	 * Gets the number of the selected channels.
	 * @return The number of the selected channels.
	 */
	public abstract int getSelectedChannelCount();
	
	/**
	 * Removes all selected channels in this channels pane.
	 * Notice that this method does not remove any channels in the back-end.
	 * It is invoked after the channels are already removed in the back-end.
	 * @return The number of removed channels.
	 */
	public abstract int removeSelectedChannels();
	
	/**
	 * Registers the specified listener for receiving list selection events.
	 * @param listener The <code>ListSelectionListener</code> to register.
	 */
	public abstract void addListSelectionListener(ListSelectionListener listener);
	
	/**
	 * Removes the specified listener.
	 * @param listener The <code>ListSelectionListener</code> to remove.
	 */
	public abstract void removeListSelectionListener(ListSelectionListener listener);
	
	/**
	 * Determines whether the channel list UI should be automatically updated
	 * when channel is added/removed. The default value is <code>true</code>.
	 * @see updateChannelListUI
	 */
	public abstract boolean getAutoUpdate();
	
	/**
	 * Determines whether the channel list UI should be automatically updated
	 * when channel is added/removed.
	 * @see updateChannelListUI
	 */
	public abstract void setAutoUpdate(boolean b);
	
	/**
	 * Updates the channel list UI.
	 * @see setAutoUpdate
	 */
	public abstract void updateChannelListUI();
}
