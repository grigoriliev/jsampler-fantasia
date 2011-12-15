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

import org.jsampler.SamplerChannelModel;
import org.jsampler.view.JSChannel;
import org.linuxsampler.lscp.SamplerChannel;

public class AndroidChannel implements JSChannel {
	private SamplerChannelModel model;
	
	/**
	 * Creates a new instance of <code>AndroidChannel</code> using the specified
	 * non-<code>null</code> channel model.
	 * @param model The model to be used by this channel.
	 * @throws IllegalArgumentException If the model is <code>null</code>.
	 */
	public AndroidChannel(SamplerChannelModel model) {
		if(model == null) throw new IllegalArgumentException("model must be non null");
		this.model = model;
	}
	
	/**
	 * Gets the model that is currently used by this channel.
	 * @return model The <code>SamplerChannelModel</code> instance
	 * which provides information about this channel.
	 */
	@Override
	public SamplerChannelModel
	getModel() { return model; }
	
	/**
	 * Gets the numerical ID of this sampler channel.
	 * @return The numerical ID of this sampler channel or -1 if the channel's ID is not set.
	 */
	@Override
	public int
	getChannelId() {
		return getChannelInfo() == null ? -1 : getChannelInfo().getChannelId();
	}
	
	/**
	 * Gets the current settings of this sampler channel.
	 * @return <code>SamplerChannel</code> instance containing
	 * the current settings of this sampler channel.
	 */
	@Override
	public SamplerChannel
	getChannelInfo() { return getModel().getChannelInfo(); }
	
	@Override
	public boolean
	isSelected() { return false; }
	
	@Override
	public void
	setSelected(boolean select) { }
}
