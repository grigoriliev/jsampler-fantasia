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

package org.jsampler.task;

import java.util.logging.Level;

import org.jsampler.CC;
import org.jsampler.HF;

import static org.jsampler.JSI18n.i18n;


/**
 * This task sets an audio output channel of a specific sampler channel.
 * @author Grigor Iliev
 */
public class SetChannelAudioOutputChannel extends EnhancedTask {
	private int channel;
	private int audioOut;
	private int audioIn;
	
	/**
	 * Creates new instance of <code>SetChannelAudioOutputChannel</code>.
	 * @param channel The sampler channel number.
	 * @param audioOut The sampler channel's audio output channel which should be rerouted.
	 * @param audioIn The audio channel of the selected audio output device where
	 * <code>audioOut</code> should be routed to.
	 */
	public
	SetChannelAudioOutputChannel(int channel, int audioOut, int audioIn) {
		setTitle("SetChannelAudioOutputChannel_task");
		setDescription (
			i18n.getMessage("SetChannelAudioOutputChannel.description", channel)
		);
		
		this.audioOut = audioOut;
		this.audioIn = audioIn;
	}
	
	/** The entry point of the task. */
	public void
	run() {
		try { CC.getClient().setChannelAudioOutputChannel(channel, audioOut, audioIn); }
		catch(Exception x) {
			setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
			CC.getLogger().log(Level.FINE, getErrorMessage(), x);
		}
	}
}
