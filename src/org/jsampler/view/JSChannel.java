/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005 Grigor Kirilov Iliev
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

import java.awt.BorderLayout;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;

import org.jsampler.SamplerChannelModel;
import org.linuxsampler.lscp.SamplerChannel;


/**
 * This class defines common behaviors for sampler channel.
 * @author Grigor Iliev
 */
public abstract class JSChannel extends JPanel {
	private SamplerChannelModel model;
	
	/**
	 * Creates a new instance of <code>JSChannel</code> using the specified non-null
	 * channel model.
	 * @param model The model to be used by this channel.
	 * @throws IllegalArgumentException If the model is <code>null</code>.
	 */
	public
	JSChannel(SamplerChannelModel model) {
		super(new BorderLayout());
		
		if(model == null) throw new IllegalArgumentException("model must be non null");
		this.model = model;
		
		addPropertyChangeListener("selectionProbablyChanged", new PropertyChangeListener() {
			public void
			propertyChange(PropertyChangeEvent e) {
				boolean b = Boolean.parseBoolean(e.getNewValue().toString());
				if(isSelected() == b) return;
				
				setSelected(b);
			}
		});
	}
	
	/**
	 * Gets the model that is currently used by this channel.
	 * @return model The <code>SamplerChannelModel</code> instance
	 * which provides information about this channel.
	 */
	public SamplerChannelModel
	getModel() { return model; }
	
	/**
	 * Gets the numerical ID of this sampler channel.
	 * @return The numerical ID of this sampler channel or -1 if the channel's ID is not set.
	 */
	public int
	getChannelID() {
		return getChannelInfo() == null ? -1 : getChannelInfo().getChannelID();
	}
	
	/**
	 * Gets the current settings of this sampler channel.
	 * @return <code>SamplerChannel</code> instance containing
	 * the current settings of this sampler channel.
	 */
	public SamplerChannel
	getChannelInfo() { return getModel().getChannelInfo(); }
	
	/**
	 * Sets the current settings of this sampler channel.
	 * @param chn A <code>SamplerChannel</code> instance containing
	 * the new settings for this sampler channel.
	 *
	public void
	setChannelInfo(SamplerChannel chn) {
		SamplerChannel oldChn = this.chn;
		this.chn = chn;
		
		firePropertyChange("ChannelInfo", oldChn, this.chn);
	}*/
	
	/**
	 * Determines whether the channel is selected.
	 * @return <code>true</code> if the channel is selected, <code>false</code> otherwise.
	 */
	public abstract boolean isSelected();
	
	/**
	 * Sets the selection state of this channel.
	 * This method is invoked when the selection state of the channel has changed.
	 * @param select Specifies the new selection state of this channel;
	 * <code>true</code> to select the channel, <code>false</code> otherwise.
	 */
	public abstract void setSelected(boolean select);
}
