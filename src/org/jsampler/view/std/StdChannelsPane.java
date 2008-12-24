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

package org.jsampler.view.std;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Rectangle;

import java.awt.event.KeyEvent;

import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
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

import static javax.swing.KeyStroke.*;


/**
 *
 * @author Grigor Iliev
 */
public abstract class StdChannelsPane extends JSChannelsPane implements ListSelectionListener {
	protected final ChannelList chnList = createChannelList();
	protected final ChannelListModel listModel = createChannelListModel();
	
	protected final JScrollPane scrollPane;
	
	protected static class ChannelList extends ComponentList { }
	
	protected static class ChannelListModel extends DefaultComponentListModel<JSChannel> { }
	
	private final Vector<ListSelectionListener> selListeners = new Vector<ListSelectionListener>();
		
	/**
	 * Creates a new instance of <code>StdChannelsPane</code> with
	 * the specified <code>title</code>.
	 * @param title The title of this <code>StdChannelsPane</code>
	 */
	public
	StdChannelsPane(String title) {
		super(title);
		
		setLayout(new BorderLayout());
		
		chnList.setOpaque(false);
		//chnList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		chnList.setModel(listModel);
		chnList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		scrollPane = new JScrollPane(chnList);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		add(scrollPane);
		
		setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		installListeners();
	}
	
	protected void
	installListeners() {
		chnList.addListSelectionListener(this);
		
		KeyStroke k = getKeyStroke(KeyEvent.VK_UP, KeyEvent.ALT_MASK | KeyEvent.SHIFT_MASK);
		getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(k, "a4n.moveChannelsOnTop");
		getActionMap().put("a4n.moveChannelsOnTop", getA4n().moveChannelsOnTop);
		
		k = getKeyStroke(KeyEvent.VK_UP, KeyEvent.ALT_MASK);
		getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(k, "a4n.moveChannelsUp");
		getActionMap().put("a4n.moveChannelsUp", getA4n().moveChannelsUp);
		
		k = getKeyStroke(KeyEvent.VK_DOWN, KeyEvent.ALT_MASK);
		getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(k, "a4n.moveChannelsDown");
		getActionMap().put("a4n.moveChannelsDown", getA4n().moveChannelsDown);
		
		k = getKeyStroke(KeyEvent.VK_DOWN, KeyEvent.ALT_MASK | KeyEvent.SHIFT_MASK);
		getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(k, "a4n.moveChannelsAtBottom");
		getActionMap().put("a4n.moveChannelsAtBottom", getA4n().moveChannelsAtBottom);
		
	}
	
	public StdA4n
	getA4n() { return ((StdMainFrame)CC.getMainFrame()).getA4n(); }
	
	/**
	 * Registers the specified listener for receiving list selection events.
	 * @param listener The <code>ListSelectionListener</code> to register.
	 */
	@Override
	public void
	addListSelectionListener(ListSelectionListener listener) {
		selListeners.add(listener);
	}
	
	/**
	 * Removes the specified listener.
	 * @param listener The <code>ListSelectionListener</code> to remove.
	 */
	@Override
	public void
	removeListSelectionListener(ListSelectionListener listener) {
		selListeners.remove(listener);
	}
	
	/**
	 * Invoked when the selection has changed.
	 * This method implements <code>valueChanged</code>
	 * method of the <code>ListSelectionListener</code> interface.
	 * @param e A <code>ListSelectionEvent</code>
	 * instance providing the event information.
	 */
	@Override
	public void
	valueChanged(ListSelectionEvent e) {
		ListSelectionEvent e2 = null;
		for(ListSelectionListener l : selListeners) {
			if(e2 == null) e2 = new ListSelectionEvent (
				this,
				e.getFirstIndex(),
				e.getLastIndex(),
				e.getValueIsAdjusting()
			);
			l.valueChanged(e2);
		}
	}
	
	protected ChannelList
	createChannelList() { return new ChannelList(); }
	
	protected ChannelListModel
	createChannelListModel() { return new ChannelListModel(); }
	
	@Override
	public void
	setSelectedChannel(JSChannel channel) {
		chnList.setSelectedComponent(channel, true);
	}
	
	/**
	 * Adds new channel to this channels pane.
	 * @param channelModel The sampler channel model to be used by the new channel.
	 */
	@Override
	public void
	addChannel(SamplerChannelModel channelModel) {
		JSChannel channel = createChannel(channelModel);
		listModel.add(channel);
		chnList.setSelectedComponent(channel, true);
		scrollToBottom();
	}
	
	protected abstract JSChannel createChannel(SamplerChannelModel channelModel);
	
	/**
	 * Adds the specified channels to this channels pane.
	 * @param chns The channels to be added.
	 */
	@Override
	public void
	addChannels(JSChannel[] chns) {
		if(chns == null || chns.length == 0) return;
		
		for(JSChannel c : chns) listModel.add(c);
		chnList.setSelectionInterval (
			listModel.getSize() - chns.length, listModel.getSize() - 1
		);
		
		chnList.ensureIndexIsVisible(listModel.getSize() - 1);
	}
		
	/**
	 * Removes the specified channel from this channels pane.
	 * This method is invoked when a sampler channel is removed in the back-end.
	 * @param chn The channel to be removed from this channels pane.
	 */
	@Override
	public void
	removeChannel(JSChannel chn) { listModel.remove(chn); }
	
	/**
	 * Gets the first channel in this channels pane.
	 * @return The first channel in this channels pane or <code>null</code> if 
	 * the channels pane is empty.
	 */
	@Override
	public JSChannel
	getFirstChannel() { return listModel.size() == 0 ? null : listModel.get(0); }
	
	/**
	 * Gets the last channel in this channels pane.
	 * @return The last channel in this channels pane or <code>null</code> if 
	 * the channels pane is empty.
	 */
	@Override
	public JSChannel
	getLastChannel() {
		return listModel.size() == 0 ? null : listModel.get(listModel.size() - 1);
	}
	
	/**
	 * Gets the channel at the specified index.
	 * @return The channel at the specified index.
	 * @throws ArrayIndexOutOfBoundsException If the index is out of range.
	 */
	@Override
	public JSChannel
	getChannel(int idx) { return listModel.get(idx); }
	
	/**
	 * Gets an array with all channels in this channels pane.
	 * @return An array with all channels in this channels pane.
	 */
	@Override
	public JSChannel[]
	getChannels() {
		JSChannel[] chns = new JSChannel[listModel.size()];
		for(int i = 0; i < listModel.size(); i++) chns[i] = listModel.get(i);
		return chns;
	}
	
	/**
	 * Gets the number of channels in this channels pane.
	 * @return The number of channels in this channels pane.
	 */
	@Override
	public int
	getChannelCount() { return listModel.size(); }
	
	/**
	 * Determines whether there is at least one selected channel.
	 * @return <code>true</code> if there is at least one selected channel,
	 * <code>false</code> otherwise.
	 */
	@Override
	public boolean
	hasSelectedChannel() { return !chnList.isSelectionEmpty(); }
	
	/**
	 * Gets the number of the selected channels.
	 * @return The number of the selected channels.
	 */
	@Override
	public int
	getSelectedChannelCount() { return chnList.getSelectedIndices().length; }
	
	/**
	 * Gets an array with all selected channels.
	 * The channels are sorted in increasing index order.
	 * @return The selected channels or an empty array if nothing is selected.
	 */
	@Override
	public JSChannel[]
	getSelectedChannels() {
		Component[] cS = chnList.getSelectedComponents();
		JSChannel[] chns = new JSChannel[cS.length];
		for(int i = 0; i < cS.length; i++) chns[i] = (JSChannel)cS[i];
		return chns;
	}
	
	/**
	 * Removes all selected channels in this channels pane.
	 * Notice that this method does not remove any channels in the back-end.
	 * It is invoked after the channels are already removed in the back-end.
	 * @return The number of removed channels.
	 */
	@Override
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
	
	/** Selects all channels. */
	@Override
	public void
	selectAll() { chnList.selectAll(); }
	
	/** Deselects all selected channels. */
	@Override
	public void
	clearSelection() { chnList.clearSelection(); }
	
	/**
	 * Determines whether the channel list UI should be automatically updated
	 * when channel is added/removed. The default value is <code>true</code>.
	 * @see updateChannelListUI
	 */
	@Override
	public boolean
	getAutoUpdate() { return chnList.getAutoUpdate(); }
	
	/**
	 * Determines whether the channel list UI should be automatically updated
	 * when channel is added/removed.
	 * @see updateChannelListUI
	 */
	@Override
	public void
	setAutoUpdate(boolean b) { chnList.setAutoUpdate(b); }
	
	/**
	 * Updates the channel list UI.
	 * @see setAutoUpdate
	 */
	@Override
	public void
	updateChannelListUI() { chnList.updateList(); }
	
		
	@Override
	public void
	moveSelectedChannelsOnTop() {
		JSChannel[] chns = getSelectedChannels();
			
		if(chns.length == 0) {
			CC.getLogger().info("Can't move channel(s) at the beginning");
			return;
		}
		
		for(int i = chns.length - 1; i >= 0; i--) {
			listModel.remove(chns[i]);
			listModel.insert(chns[i], 0);
		}
		
		chnList.setSelectionInterval(0, chns.length - 1);
		chnList.ensureIndexIsVisible(0);
	}
	
	@Override
	public void
	moveSelectedChannelsUp() {
		JSChannel[] chns = getSelectedChannels();
			
		if(chns.length == 0 || chns[0] == getFirstChannel()) {
			CC.getLogger().info("Can't move channel(s) up");
			return;
		}
		
		for(int i = 0; i < chns.length; i++) listModel.moveUp(chns[i]);
		
		int[] si = chnList.getSelectedIndices();
		
		for(int i = 0; i < si.length; i++) si[i] -= 1;
		
		chnList.setSelectedIndices(si);
		chnList.ensureIndexIsVisible(si[0]);
	}
	
	@Override
	public void
	moveSelectedChannelsDown() {
		JSChannel[] chns = getSelectedChannels();
			
		if(chns.length == 0 || chns[chns.length - 1] == getLastChannel()) {
			CC.getLogger().info("Can't move channel(s) down");
			return;
		}
		
		for(int i = chns.length - 1; i >= 0; i--) listModel.moveDown(chns[i]);
		
		int[] si = chnList.getSelectedIndices();
		for(int i = 0; i < si.length; i++) si[i] += 1;
		chnList.setSelectedIndices(si);
		chnList.ensureIndexIsVisible(si[si.length - 1]);
	}
	
	@Override
	public void
	moveSelectedChannelsAtBottom() {
		JSChannel[] chns = getSelectedChannels();
			
		if(chns.length == 0) {
			CC.getLogger().info("Can't move channel(s) at the end");
			return;
		}
		
		for(int i =  0; i < chns.length; i++) {
			listModel.remove(chns[i]);
			listModel.add(chns[i]);
		}
		
		chnList.setSelectionInterval (
			listModel.getSize() - chns.length, listModel.getSize() - 1
		);
		chnList.ensureIndexIsVisible(listModel.getSize() - 1);
	}
	
	@Override
	public void
	processChannelSelection(JSChannel c, boolean controlDown, boolean shiftDown) {
		chnList.getUI().processSelectionEvent(c, controlDown, shiftDown);
	}
	
	private void
	scrollToBottom() {
		int h = scrollPane.getViewport().getView().getHeight();
		scrollPane.getViewport().scrollRectToVisible(new Rectangle(0, h - 2, 1, 1));
	}
}
