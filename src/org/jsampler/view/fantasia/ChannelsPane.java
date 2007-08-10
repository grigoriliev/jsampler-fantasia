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

package org.jsampler.view.fantasia;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.ListSelectionModel;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sf.juife.ComponentList;
import net.sf.juife.ComponentListModel;
import net.sf.juife.DefaultComponentListModel;

import org.jsampler.CC;
import org.jsampler.SamplerChannelModel;

import org.jsampler.view.JSChannel;
import org.jsampler.view.JSChannelsPane;


/**
 *
 * @author Grigor Iliev
 */
public class ChannelsPane extends JSChannelsPane {
	private final ChannelListPane chnList = new ChannelListPane();
	private final DefaultComponentListModel listModel = new DefaultComponentListModel();
		
	
	/**
	 * Creates a new instance of <code>ChannelsPane</code> with
	 * the specified <code>title</code>.
	 * @param title The title of this <code>ChannelsPane</code>
	 */
	public
	ChannelsPane(String title) {
		super(title);
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		chnList.setModel(listModel);
		chnList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		add(chnList);
	}
	
	class ChannelListPane extends ComponentList {
		public java.awt.Dimension
		getMaximumSize() { return getPreferredSize(); }
	}
	
	/**
	 * Adds new channel to this channels pane.
	 * @param channelModel The sampler channel model to be used by the new channel.
	 */
	public void
	addChannel(SamplerChannelModel channelModel) {
		Channel channel = new Channel(channelModel);
		listModel.add(channel);
		if(channel.getChannelInfo().getEngine() == null) channel.expandChannel(false);
		chnList.setSelectedComponent(channel, true);
	}
	
	/**
	 * Adds the specified channels to this channels pane.
	 * @param chns The channels to be added.
	 */
	public void
	addChannels(JSChannel[] chns) {
		if(chns == null || chns.length == 0) return;
		
		for(JSChannel c : chns) listModel.add(c);
		
		chnList.setSelectedIndex(listModel.getSize() - 1);
	}
	
	/**
	 * Removes the specified channel from this channels pane.
	 * This method is invoked when a sampler channel is removed in the back-end.
	 * @param chn The channel to be removed from this channels pane.
	 */
	public void
	removeChannel(JSChannel chn) {
		listModel.remove(chn);
	}
	
	/**
	 * Determines whether there is at least one selected channel.
	 * @return <code>true</code> if there is at least one selected channel,
	 * <code>false</code> otherwise.
	 */
	public boolean
	hasSelectedChannel() { return !chnList.isSelectionEmpty(); }
	
	/**
	 * Gets the first channel in this channels pane.
	 * @return The first channel in this channels pane or <code>null</code> if 
	 * the channels pane is empty.
	 */
	public JSChannel
	getFirstChannel() { return listModel.size() == 0 ? null : (JSChannel)listModel.get(0); }
	
	/**
	 * Gets the last channel in this channels pane.
	 * @return The last channel in this channels pane or <code>null</code> if 
	 * the channels pane is empty.
	 */
	public JSChannel
	getLastChannel() {
		return listModel.size() == 0 ? null : (JSChannel)listModel.get(listModel.size()-1);
	}
	
	/**
	 * Gets the channel at the specified index.
	 * @return The channel at the specified index.
	 * @throws ArrayIndexOutOfBoundsException If the index is out of range.
	 */
	public JSChannel
	getChannel(int idx) { return (JSChannel)listModel.get(idx); }
	
	/**
	 * Gets an array of all channels in this channels pane.
	 * @return An array of all channels in this channels pane.
	 */
	public JSChannel[]
	getChannels() {
		JSChannel[] chns = new JSChannel[listModel.size()];
		for(int i = 0; i < listModel.size(); i++) chns[i] = (JSChannel)listModel.get(i);
		return chns;
	}
	
	/**
	 * Gets the number of channels in this channels pane.
	 * @return The number of channels in this channels pane.
	 */
	public int
	getChannelCount() { return listModel.size(); }
	
	/**
	 * Gets an array of all selected channels.
	 * The channels are sorted in increasing index order.
	 * @return The selected channels or an empty array if nothing is selected.
	 */
	public JSChannel[]
	getSelectedChannels() {
		Component[] cS = chnList.getSelectedComponents();
		JSChannel[] chns = new JSChannel[cS.length];
		for(int i = 0; i < cS.length; i++) chns[i] = (JSChannel)cS[i];
		return chns;
	}
	
	/**
	 * Gets the number of the selected channels.
	 * @return The number of the selected channels.
	 */
	public int
	getSelectedChannelCount() { return chnList.getSelectedIndices().length; }
	
	/**
	 * Removes all selected channels in this channels pane.
	 * Notice that this method does not remove any channels in the back-end.
	 * It is invoked after the channels are already removed in the back-end.
	 * @return The number of removed channels.
	 */
	public int
	removeSelectedChannels() {
		int[] l = chnList.getSelectedIndices();
		ComponentListModel model = chnList.getModel();
		
		for(;;) {
			int i = chnList.getMinSelectionIndex();
			if(i == -1) break;
			model.remove(i);
		}
		
		return l.length;
	}
	
	/**
	 * Registers the specified listener for receiving list selection events.
	 * @param listener The <code>ListSelectionListener</code> to register.
	 */
	public void
	addListSelectionListener(ListSelectionListener listener) {
		listenerList.add(ListSelectionListener.class, listener);
	}
	
	/**
	 * Removes the specified listener.
	 * @param listener The <code>ListSelectionListener</code> to remove.
	 */
	public void
	removeListSelectionListener(ListSelectionListener listener) {
		listenerList.remove(ListSelectionListener.class, listener);
	}
}
