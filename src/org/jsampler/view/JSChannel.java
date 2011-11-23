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
import org.linuxsampler.lscp.SamplerChannel;


/**
 * This class defines the skeleton of a sampler channel.
 * @author Grigor Iliev
 */
public interface JSChannel {
	/**
	 * Gets the model that is currently used by this channel.
	 * @return model The <code>SamplerChannelModel</code> instance
	 * which provides information about this channel.
	 */
	public SamplerChannelModel getModel();
	
	/**
	 * Gets the numerical ID of this sampler channel.
	 * @return The numerical ID of this sampler channel or -1 if the channel's ID is not set.
	 */
	public int getChannelId();
	
	/**
	 * Gets the current settings of this sampler channel.
	 * @return <code>SamplerChannel</code> instance containing
	 * the current settings of this sampler channel.
	 */
	public SamplerChannel getChannelInfo();
	
	/**
	 * Determines whether the channel is selected.
	 * @return <code>true</code> if the channel is selected, <code>false</code> otherwise.
	 */
	public boolean isSelected();
	
	/**
	 * Sets the selection state of this channel.
	 * This method is invoked when the selection state of the channel has changed.
	 * @param select Specifies the new selection state of this channel;
	 * <code>true</code> to select the channel, <code>false</code> otherwise.
	 */
	public void setSelected(boolean select);
}
