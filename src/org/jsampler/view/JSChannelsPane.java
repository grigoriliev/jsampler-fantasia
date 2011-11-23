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


import org.jsampler.SamplerChannelModel;
import org.jsampler.event.ListSelectionListener;
import org.jsampler.view.SessionViewConfig.ChannelConfig;


/**
 * This class defines the skeleton of a pane containg sampler channels.
 * @author Grigor Iliev
 */
public interface JSChannelsPane<C extends JSChannel> {
	/** The key used for reporting title's property change. */
	public final static String TITLE = "ChannelsPaneTitle";
	
	/**
	 * Returns the title of this channels' pane.
	 * @return The title of this  channels' pane.
	 */
	public String getTitle();
	
	/**
	 * Sets the title of this  channels' pane.
	 * @param title The new title of this channels' pane.
	 */
	public void setTitle(String title);
	
	//public void firePropertyChange(String propertyName, Object oldValue, Object newValue);
	
	/**
	 * Adds new channel to this channels pane.
	 * @param channelModel The sampler channel model to be used by the new channel.
	 */
	public void addChannel(SamplerChannelModel channelModel);
	
	/**
	 * Adds new channel to this channels pane.
	 * @param channelModel The sampler channel model to be used by the new channel.
	 * @param config The view config of the sampler channel.
	 */
	public void addChannel(SamplerChannelModel channelModel, ChannelConfig config);
	
	/**
	 * Adds the specified channels to this channels pane.
	 * @param chns The channels to be added.
	 */
	public void addChannels(C[] chns);
	
	/**
	 * Removes the specified channel from this channels pane.
	 * This method is invoked when a sampler channel is removed in the back-end.
	 * @param chn The channel to be removed from this channels pane.
	 */
	public void removeChannel(C chn);
	
	/**
	 * Determines whether there is at least one selected channel.
	 * @return <code>true</code> if there is at least one selected channel,
	 * <code>false</code> otherwise.
	 */
	public boolean hasSelectedChannel();
	
	/**
	 * Gets the first channel in this channels pane.
	 * @return The first channel in this channels pane or <code>null</code> if 
	 * the channels pane is empty.
	 */
	public C getFirstChannel();
	
	/**
	 * Gets the last channel in this channels pane.
	 * @return The last channel in this channels pane or <code>null</code> if 
	 * the channels pane is empty.
	 */
	public C getLastChannel();
	
	/**
	 * Gets the channel at the specified index.
	 * @return The channel at the specified index.
	 * @throws ArrayIndexOutOfBoundsException If the index is out of range.
	 */
	public C getChannel(int idx);
	
	/**
	 * Gets an array of all channels in this channels pane.
	 * @return An array of all channels in this channels pane.
	 */
	public C[] getChannels();
	
	/**
	 * Gets the number of channels in this channels pane.
	 * @return The number of channels in this channels pane.
	 */
	public int getChannelCount();
	
	/**
	 * Gets an array of all selected channels.
	 * The channels are sorted in increasing index order.
	 * @return The selected channels or an empty array if nothing is selected.
	 */
	public C[] getSelectedChannels();
	
	/**
	 * Gets the number of the selected channels.
	 * @return The number of the selected channels.
	 */
	public int getSelectedChannelCount();
	
	/**
	 * Selects the specified channel.
	 */
	public void setSelectedChannel(C channel);
	
	/** Selects all channels. */
	public void selectAll();
	
	/** Deselects all selected channels. */
	public void clearSelection();
	
	/**
	 * Removes all selected channels in this channels pane.
	 * Notice that this method does not remove any channels in the back-end.
	 * It is invoked after the channels are already removed in the back-end.
	 * @return The number of removed channels.
	 */
	public int removeSelectedChannels();
	
	public void moveSelectedChannelsOnTop();
	
	public void moveSelectedChannelsUp();
	
	public void moveSelectedChannelsDown();
	
	public void moveSelectedChannelsAtBottom();
		
	/**
	 * Registers the specified listener for receiving list selection events.
	 * @param listener The <code>ListSelectionListener</code> to register.
	 */
	public void addListSelectionListener(ListSelectionListener listener);
	
	/**
	 * Removes the specified listener.
	 * @param listener The <code>ListSelectionListener</code> to remove.
	 */
	public void removeListSelectionListener(ListSelectionListener listener);
	
	/**
	 * Process a selection event.
	 * @param c The newly selected channel.
	 * @param controlDown Specifies whether the control key is held down during selection.
	 * @param shiftDown Specifies whether the shift key is held down during selection.
	 */
	public void processChannelSelection(C c, boolean controlDown, boolean shiftDown);
	
	/**
	 * Determines whether the channel list UI should be automatically updated
	 * when channel is added/removed. The default value is <code>true</code>.
	 * @see updateChannelListUI
	 */
	public boolean getAutoUpdate();
	
	/**
	 * Determines whether the channel list UI should be automatically updated
	 * when channel is added/removed.
	 * @see updateChannelListUI
	 */
	public void setAutoUpdate(boolean b);
	
	/**
	 * Updates the channel list UI.
	 * @see setAutoUpdate
	 */
	public void updateChannelListUI();
}
