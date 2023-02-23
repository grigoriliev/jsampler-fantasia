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

package org.jsampler.android.view;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

import org.jsampler.SamplerChannelModel;
import org.jsampler.view.JSChannelsPane;
import org.jsampler.view.SessionViewConfig.ChannelConfig;

import android.view.View;

public abstract class AndroidChannelsPane<C extends AndroidChannel> implements JSChannelsPane<C> {
	private String title;
	private View   view = null;
	
	private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	
	protected final ArrayList<C> channels = new ArrayList<C>(); 
	
	/** Creates a new instance of <code>AndroidChannelsPane</code>. */
	public
	AndroidChannelsPane(String title) {
		this.title = title;
	}
	
	public abstract View createView();
	
	public View
	getView() {
		if(view == null) view = createView();
		return view;
	}
	
	/** Add a PropertyChangeListener to the listener list. */
	public void
	addPropertyChangeListener(PropertyChangeListener l) {
		propertyChangeSupport.addPropertyChangeListener(l);
	}
	
	/** Remove a PropertyChangeListener from the listener list. */
	public void
	removePropertyChangeListener(PropertyChangeListener l) {
		propertyChangeSupport.removePropertyChangeListener(l);
	}
	
	/** Add a PropertyChangeListener for a specific property. */
	public void
	addPropertyChangeListener(String propertyName, PropertyChangeListener l) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, l);
	}
	
	/** Remove a PropertyChangeListener for a specific property. */
	public void
	removePropertyChangeListener(String propertyName, PropertyChangeListener l) {
		propertyChangeSupport.removePropertyChangeListener(propertyName, l);
	}
	
	/**
	 * Report a bound property update to any registered listeners.
	 * No event is fired if old and new are equal and non-null.
	 */
	protected void
	firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}
	
	/**
	 * Returns the title of this channels' pane.
	 * @return The title of this channels' pane.
	 */
	@Override
	public String
	getTitle() { return title; }
	
	/**
	 * Sets the title of this channels' pane.
	 * @param title The new title of this channels' pane.
	 */
	@Override
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
	@Override
	public String
	toString() { return getTitle(); }
	
	/**
	 * Adds new channel to this channels pane.
	 * @param channelModel The sampler channel model to be used by the new channel.
	 * @param config The view config of the sampler channel.
	 */
	@Override
	public void
	addChannel(SamplerChannelModel channelModel, ChannelConfig config) {
		addChannel(channelModel);
	}
	
	/**
	 * Adds the specified channels to this channels pane.
	 * @param chns The channels to be added.
	 */
	@Override
	public void
	addChannels(C[] chns) {
		if(chns == null || chns.length == 0) return;
		
		for(C c : chns) channels.add(c);
		firePropertyChange("channelsAdded", null, chns);
	}
	
	/**
	 * Removes the specified channel from this channels pane.
	 * This method is invoked when a sampler channel is removed in the back-end.
	 * @param chn The channel to be removed from this channels pane.
	 */
	@Override
	public void
	removeChannel(C chn) {
		channels.remove(chn);
		firePropertyChange("channelRemoved", null, chn);
	}
	
	/**
	 * Gets the first channel in this channels pane.
	 * @return The first channel in this channels pane or <code>null</code> if 
	 * the channels pane is empty.
	 */
	@Override
	public C
	getFirstChannel() { return channels.size() == 0 ? null : channels.get(0); }
	
	/**
	 * Gets the last channel in this channels pane.
	 * @return The last channel in this channels pane or <code>null</code> if 
	 * the channels pane is empty.
	 */
	@Override
	public C
	getLastChannel() {
		return channels.size() == 0 ? null : channels.get(channels.size() - 1);
	}
	
	/**
	 * Gets the channel at the specified index.
	 * @return The channel at the specified index.
	 * @throws ArrayIndexOutOfBoundsException If the index is out of range.
	 */
	@Override
	public C
	getChannel(int idx) { return channels.get(idx); }
	
	/**
	 * Gets the number of channels in this channels pane.
	 * @return The number of channels in this channels pane.
	 */
	@Override
	public int
	getChannelCount() { return channels.size(); }
}
