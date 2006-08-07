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

package org.jsampler;

import org.jsampler.event.SamplerChannelListener;

import org.linuxsampler.lscp.SamplerChannel;


/**
 * A data model representing a sampler channel.
 * @author Grigor Iliev
 */
public interface SamplerChannelModel {
	/**
	 * Registers the specified listener for receiving event messages.
	 * @param l The <code>SamplerChannelListener</code> to register.
	 */
	public void addSamplerChannelListener(SamplerChannelListener l);
	
	/**
	 * Removes the specified listener.
	 * @param l The <code>SamplerChannelListener</code> to remove.
	 */
	public void removeSamplerChannelListener(SamplerChannelListener l);
	
	/**
	 * Gets the sampler channel number.
	 * @return The sampler channel number or -1 if the sampler channel number is not set.
	 */
	public int getChannelID();
	
	/**
	 * Gets the current settings of the sampler channel.
	 * @return <code>SamplerChannel</code> instance containing
	 * the current settings of the sampler channel.
	 */
	public SamplerChannel getChannelInfo();
	
	/**
	 * Sets the current settings of the sampler channel.
	 * @param channel A <code>SamplerChannel</code> instance containing
	 * the new settings for this sampler channel.
	 */
	public void setChannelInfo(SamplerChannel channel);
	
	/**
	 * Gets the number of active disk streams.
	 * @return The number of active disk streams.
	 */
	public int getStreamCount();
	
	/**
	 * Sets the number of active disk streams.
	 * @param count The new number of active disk streams.
	 */
	public void setStreamCount(int count);
	
	/**
	 * Gets the number of active voices.
	 * @return The number of active voices.
	 */
	public int getVoiceCount();
	
	/**
	 * Sets the number of active voices.
	 * @param count The new number of active voices.
	 */
	public void setVoiceCount(int count);
	
	/**
	 * Sets the sampler engine type to be used.
	 * @param engine The name of the engine type to be used.
	 */
	public void setEngineType(String engine);
	
	/**
	 * Sets the mute mode of the channel. 
	 * @param mute Specifies the mute mode. If <code>true</code> the channel is muted, else
	 * the channel is unmuted.
	 */
	public void setMute(boolean mute);
	
	/**
	 * Sets the solo mode of the channel. 
	 * @param solo Specifies the solo mode. If <code>true</code> the channel is soloed, else
	 * the channel is unsoloed.
	 */
	public void setSolo(boolean solo);
	
	/**
	 * Sets the channel volume. 
	 * @param volume Specifies the new volume value.
	 */
	public void setVolume(float volume);
	
	/**
	 * Sets the MIDI input device of the channel represented by this model. 
	 * @param deviceID Specifies the numerical ID of the MIDI input device to be set.
	 */
	public void setMidiInputDevice(int deviceID);
	
	/**
	 * Sets the MIDI input port of the channel represented by this model. 
	 * @param port Specifies the number of the MIDI input port.
	 */
	public void setMidiInputPort(int port);
	
	/**
	 * Sets the MIDI channel that the channel represented by this model should listen to. 
	 * @param channel Specifies the MIDI channel that the channel
	 * represented by this model should listen to.
	 */
	public void setMidiInputChannel(int channel);
	
	/**
	 * Sets the audio output device of the channel represented by this model. 
	 * @param deviceID Specifies the numerical ID of the audio output device to be set.
	 */
	public void setAudioOutputDevice(int deviceID);
	
	/**
	 * Loads and assigns the specified instrument
	 * to the sampler channel represented by this model.
	 * @param filename The file name of the instrument to be loaded.
	 * @param InstrIndex The index of the instrument in the instrument file to be loaded.
	 */
	public void loadInstrument(String filename, int InstrIndex);
	
	/** Resets the channel. */
	public void resetChannel();
	
	/** Duplicates the channel. */
	public void duplicateChannel();
}
