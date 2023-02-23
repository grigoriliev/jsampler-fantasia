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

package org.jsampler.view.fantasia;

import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;

import org.jsampler.CC;
import org.jsampler.SamplerChannelModel;

import org.jsampler.view.SessionViewConfig.ChannelConfig;

import org.jsampler.view.std.StdChannelsPane;
import org.jsampler.view.swing.SwingChannel;


/**
 *
 * @author Grigor Iliev
 */
public class ChannelsPane extends StdChannelsPane {
	private ActionListener listener;
	
	/**
	 * Creates a new instance of <code>ChannelsPane</code> with
	 * the specified <code>title</code>.
	 * @param title The title of this <code>ChannelsPane</code>
	 */
	public
	ChannelsPane(String title) {
		this(title, null);
	}
	
	/**
	 * Creates a new instance of <code>ChannelsPane</code> with
	 * the specified <code>title</code>.
	 * @param title The title of this <code>ChannelsPane</code>
	 * @param l A listener which is notified when a newly created
	 * channel is fully expanded on the screen.
	 */
	public
	ChannelsPane(String title, ActionListener l) {
		super(title);
		
		listener = l;
		
		removeAll();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		add(chnList);
		setBorder(BorderFactory.createEmptyBorder());
	}
	
	@Override
	protected ChannelList
	createChannelList() { return new FantasiaChannelList(); }
	
	@Override
	protected ChannelListModel
	createChannelListModel() { return new FantasiaChannelListModel(); }
	
	class FantasiaChannelList extends ChannelList {
		@Override
		public java.awt.Dimension
		getMaximumSize() { return getPreferredSize(); }
	}
	
	class FantasiaChannelListModel extends ChannelListModel {
		@Override
		public boolean
		getComponentListIsAdjusting() {
			boolean b = CC.getSamplerModel().getChannelListIsAdjusting();
			return super.getComponentListIsAdjusting() || b;
		}
	}
	
	@Override
	protected SwingChannel
	createChannel(SamplerChannelModel channelModel) {
		return new Channel(channelModel, listener);
	}
	
	/**
	 * Adds new channel to this channels pane.
	 * @param channelModel The sampler channel model to be used by the new channel.
	 */
	@Override
	public void
	addChannel(SamplerChannelModel channelModel) {
		addChannel(channelModel, null);
	}
	
	@Override
	public void
	addChannel(SamplerChannelModel channelModel, ChannelConfig config) {
		Channel channel = null;
		
		if(config != null) {
			switch(config.type) {
			case SMALL:
				channel = new Channel(channelModel, listener, ChannelView.Type.SMALL);
				break;
			case NORMAL:
				channel = new Channel(channelModel, listener, ChannelView.Type.NORMAL);
				break;
			}
		}
		if(channel == null) channel = new Channel(channelModel, listener);
		if(config != null) {
			if(config.expanded) channel.expandChannel(false);
		} else {
			if(channel.getChannelInfo().getEngine() == null) {
				channel.expandChannel(false);
			}
		}
		listModel.add(channel);
		chnList.setSelectedComponent(channel, true);
		
		firePropertyChange("channelAdded", null, channelModel);
	}
}
