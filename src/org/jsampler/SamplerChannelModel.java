/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2006 Grigor Iliev <grigor@grigoriliev.com>
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

import org.jsampler.event.EffectSendsListener;
import org.jsampler.event.SamplerChannelListener;

import org.linuxsampler.lscp.FxSend;
import org.linuxsampler.lscp.SamplerChannel;


/**
 * A data model representing a sampler channel.
 * Note that all methods that begin with <code>setBackend</code> alter the settings
 * on the backend side.
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
	 * Registers the specified listener for receiving event messages.
	 * @param l The <code>EffectSendsListener</code> to register.
	 */
	public void addEffectSendsListener(EffectSendsListener l);
	
	/**
	 * Removes the specified listener.
	 * @param l The <code>EffectSendsListener</code> to remove.
	 */
	public void removeEffectSendsListener(EffectSendsListener l);
	
	/**
	 * Gets the sampler channel number.
	 * @return The sampler channel number or -1 if the sampler channel number is not set.
	 */
	public int getChannelId();
	
	/**
	 * Gets the current settings of the sampler channel.
	 * @return <code>SamplerChannel</code> instance containing
	 * the current settings of the sampler channel.
	 */
	public SamplerChannel getChannelInfo();
	
	/**
	 * Sets the current settings of the sampler channel.
	 * Note that this method does not changes the channel settings on
	 * the backend. It is invoked just notify for channel settings' changes.
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
	 * Note that this method does <b>not</b> alter the number
	 * of active disk streams on the backend side.
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
	 * Note that this method does <b>not</b> alter the number
	 * of active voices on the backend side.
	 * @param count The new number of active voices.
	 */
	public void setVoiceCount(int count);
	
	/**
	 * Schedules a new task for setting the sampler engine type to be used.
	 * @param engine The name of the engine type to be used.
	 */
	public void setBackendEngineType(String engine);
	
	/**
	 * Schedules a new task for setting the mute mode of the channel. 
	 * @param mute Specifies the mute mode. If <code>true</code> the channel is muted, else
	 * the channel is unmuted.
	 */
	public void setBackendMute(boolean mute);
	
	/**
	 * Schedules a new task for setting on the backend side the solo mode of the channel. 
	 * @param solo Specifies the solo mode. If <code>true</code> the channel is soloed, else
	 * the channel is unsoloed.
	 */
	public void setBackendSolo(boolean solo);
		
	/**
	 * Schedules a new task for setting the channel volume on the backend side.
	 * @param volume Specifies the new volume value.
	 */
	public void setBackendVolume(float volume);
	
	/**
	 * Schedules a new task for setting on the backend side the MIDI input
	 * device of the channel represented by this model. 
	 * @param deviceId Specifies the numerical ID of the MIDI input device to be set.
	 */
	public void setBackendMidiInputDevice(int deviceId);
	
	/**
	 * Schedules a new task for setting (on the backend side) the
	 * MIDI input port of the channel represented by this model. 
	 * @param port Specifies the number of the MIDI input port.
	 */
	public void setBackendMidiInputPort(int port);
	
	/**
	 * Schedules a new task for setting (on the backend side) the MIDI channel
	 * that the channel represented by this model should listen to. 
	 * @param channel Specifies the MIDI channel that the channel
	 * represented by this model should listen to.
	 */
	public void setBackendMidiInputChannel(int channel);
	
	/**
	 * Schedules a new task for setting (on the backend side) the audio output
	 * device of the channel represented by this model. 
	 * @param deviceId Specifies the numerical ID of the audio output device to be set.
	 */
	public void setBackendAudioOutputDevice(int deviceId);
	
	/**
	 * Sets the destination of the destination of the specified audio channel.
	 * @param audioSrc The numerical ID of the sampler channel's audio
	 * output channel, which should be rerouted.
	 * @param audioDst The audio channel of the selected audio output device
	 * where <code>audioSrc</code> should be routed to.
	 */
	public void setBackendAudioOutputChannel(int audioSrc, int audioDst);
	
	/**
	 * Schedules a new task for assigning (on the backend side) the
	 * specified MIDI instrument map to this sampler channel. 
	 * @param mapId Specify the numerical ID of the MIDI instrument
	 * map that should be assigned to this sampler
	 * channel or <code>-1</code> to remove the current map binding.
	 */
	public void setBackendMidiInstrumentMap(int mapId);
	
	/**
	 * Schedules a new task for loading and assigning the specified instrument
	 * to the sampler channel represented by this model.
	 * @param filename The file name of the instrument to be loaded.
	 * @param InstrIndex The index of the instrument in the instrument file to be loaded.
	 */
	public void loadBackendInstrument(String filename, int InstrIndex);
	
	/** Schedules a new task for reseting the channel. */
	public void resetBackendChannel();
	
	/** Schedules a new task for duplicating the channel. */
	public void duplicateBackendChannel();
	
	/**
	 * Schedules a new task for adding a new effect send on the
	 * backend side. The effect send will be actually added to this model
	 * when the backend notifies for its creation.
	 * @param midiCtrl Defines the MIDI controller, which
	 * will be able alter the effect send level.
	 */
	public void addBackendFxSend(int midiCtrl);
	
	/**
	 * Schedules a new task for adding a new effect send on the
	 * backend side. The effect send will be actually added to this model
	 * when the backend notifies for its creation.
	 * @param midiCtrl Defines the MIDI controller, which
	 * will be able alter the effect send level.
	 * @param name The name of the effect send entity.
	 * The name does not have to be unique.
	 */
	public void addBackendFxSend(int midiCtrl, String name);
	
	/**
	 * Adds the specified effect send.
	 * @param fxSend The effect send to be added.
	 */
	public void addFxSend(FxSend fxSend);
	
	/**
	 * Schedules a new task for removing the specified effect send on the backend side.
	 * @param fxSendId The ID of the effect send to remove.
	 */
	public void removeBackendFxSend(int fxSendId);
	
	/**
	 * Removes the effect send at the specified position.
	 * @param index The position of the effect send to remove.
	 * @return The removed effect send.
	 */
	public FxSend removeFxSend(int index);
	
	/**
	 * Removes the specified effect send.
	 * @param fxSendId The ID of the effect send to remove.
	 * @return <code>true</code> if the effect send is removed successfully, <code>false</code>
	 * if the channel does not contain effect send with ID <code>fxSendId</code>.
	 */
	public boolean removeFxSendById(int fxSendId);
	
	/** Removes all effect sends from this channel. */
	public void removeAllFxSends();
	
	/**
	 * Updates the specified effect send.
	 * @param fxSend The effect send to update.
	 */
	public void updateFxSend(FxSend fxSend);
	
	/**
	 * Gets the current number of effect sends.
	 * @return The current number of effect sends.
	 */
	public int getFxSendCount();
	
	/**
	 * Gets the effect send at the specified position.
	 * @param index The index of the effect send to be returned.
	 * @return The effect send at the specified position.
	 */
	public FxSend getFxSend(int index);
	
	/**
	 * Gets the effect send with the specified ID.
	 * @param fxSendId The ID of the effect send to return.
	 * @return The effect send with the specified ID or <code>null</code>
	 * if there is no effect send with ID <code>fxSendId</code>.
	 */
	public FxSend getFxSendById(int fxSendId);
	
	/**
	 * Gets the current list of effect sends.
	 * @return The current list of effect sends.
	 */
	public FxSend[] getFxSends();
	
	/**
	 * Sets the name of the specified effect send.
	 * @param fxSend The numerical ID of the effect send.
	 * @param name The new name of the effect send entity.
	 */
	public void setBackendFxSendName(int fxSend, String name);
	
	/**
	 * Sets the destination of an effect send's audio channel.
	 * @param fxSend The numerical ID of the effect send entity to be rerouted.
	 * @param audioSrc The numerical ID of the effect send's audio output channel,
	 * which should be rerouted.
	 * @param audioDst The audio channel of the selected audio output device
	 * where <code>audioSrc</code> should be routed to.
	 */
	public void setBackendFxSendAudioOutputChannel(int fxSend, int audioSrc, int audioDst);
	
	/**
	 * Sets the MIDI controller of the specified effect send.
	 * @param fxSend The numerical ID of the effect send.
	 * @param midiCtrl The MIDI controller which shall be
	 * able to modify the effect send's send level.
	 */
	public void setBackendFxSendMidiController(int fxSend, int midiCtrl);
	
	/**
	 * Sets the volume of the specified effect send.
	 * @param fxSend The numerical ID of the effect
	 * send, which volume should be changed.
	 * @param level The new volume value.
	 */
	public void setBackendFxSendLevel(int fxSend, float level);
}
