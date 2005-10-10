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

import org.jsampler.event.AudioDeviceListListener;
import org.jsampler.event.MidiDeviceListListener;
import org.jsampler.event.SamplerChannelListListener;
import org.jsampler.event.SamplerListener;

import org.linuxsampler.lscp.*;


/**
 *
 * @author Grigor Iliev
 */
public interface SamplerModel {
	/**
	 * Registers the specified listener for receiving event messages.
	 * @param l The <code>SamplerListener</code> to register.
	 */
	public void addSamplerListener(SamplerListener l);
	
	/**
	 * Removes the specified listener.
	 * @param l The <code>SamplerListener</code> to remove.
	 */
	public void removeSamplerListener(SamplerListener l);
	
	/**
	 * Registers the specified listener for receiving event messages.
	 * @param listener The <code>AudioDeviceListListener</code> to register.
	 */
	public void addAudioDeviceListListener(AudioDeviceListListener listener);
	
	/**
	 * Removes the specified listener.
	 * @param listener The <code>AudioDeviceListListener</code> to remove.
	 */
	public void removeAudioDeviceListListener(AudioDeviceListListener listener);
	
	/**
	 * Registers the specified listener for receiving event messages.
	 * @param listener The <code>MidiDeviceListListener</code> to register.
	 */
	public void addMidiDeviceListListener(MidiDeviceListListener listener);
	
	/**
	 * Removes the specified listener.
	 * @param listener The <code>MidiDeviceListListener</code> to remove.
	 */
	public void removeMidiDeviceListListener(MidiDeviceListListener listener);
	
	/**
	 * Registers the specified listener for receiving event messages.
	 * @param listener The <code>SamplerChannelListListener</code> to register.
	 */
	public void addSamplerChannelListListener(SamplerChannelListListener listener);
	
	/**
	 * Removes the specified listener.
	 * @param listener The <code>SamplerChannelListListener</code> to remove.
	 */
	public void removeSamplerChannelListListener(SamplerChannelListListener listener);
	
	/**
	 * Gets information about the LinuxSampler instance the front-end is connected to.
	 * 
	 * @return <code>ServerInfo</code> instance containing
	 * information about the LinuxSampler instance the front-end is connected to.
	 */
	public ServerInfo getServerInfo();
	
	/**
	 * Gets all audio output drivers currently available for the LinuxSampler instance.
	 * 
	 * @return <code>AudioOutputDriver</code> array containing all audio output drivers
	 * currently available for the LinuxSampler instance.
	 */
	public AudioOutputDriver[] getAudioOutputDrivers();
	
	/**
	 * Gets the model of the audio device with ID <code>deviceID</code>.
	 * @param deviceID The ID of the audio device whose model should be obtained.
	 * @return The model of the specified audio device or <code>null</code> 
	 * if there is no audio device with ID <code>deviceID</code>.
	 */
	public AudioDeviceModel getAudioDeviceModel(int deviceID);
	
	/**
	 * Gets the current number of audio devices.
	 * @return The current number of audio devices.
	 */
	public int getAudioDeviceCount();
	
	/**
	 * Gets the current list of audio device models.
	 * @return The current list of audio device models.
	 */
	public AudioDeviceModel[] getAudioDeviceModels();
	
	/**
	 * Adds the specified audio device.
	 * @param device The audio device to be added.
	 */
	public void addAudioDevice(AudioOutputDevice device);
	
	/**
	 * Removes the specified audio device.
	 * @param deviceID The ID of the audio device to be removed.
	 * @return <code>true</code> if the audio device is removed successfully, <code>false</code>
	 * if the device list does not contain audio device with ID <code>deviceID</code>.
	 */
	public boolean removeAudioDevice(int deviceID);
	
	/**
	 * Gets all MIDI input drivers currently available for the LinuxSampler instance.
	 * 
	 * @return <code>MidiInputDriver</code> array containing all MIDI input drivers currently 
	 * available for the LinuxSampler instance.
	 */
	public MidiInputDriver[] getMidiInputDrivers();
	
	/**
	 * Gets the model of the MIDI device with ID <code>deviceID</code>.
	 * @param deviceID The ID of the MIDI device whose model should be obtained.
	 * @return The model of the specified MIDI device or <code>null</code> 
	 * if there is no MIDI device with ID <code>deviceID</code>.
	 */
	public MidiDeviceModel getMidiDeviceModel(int deviceID);
	
	/**
	 * Gets the current number of MIDI input devices.
	 * @return The current number of MIDI input devices.
	 */
	public int getMidiDeviceCount();
	
	/**
	 * Gets the current list of MIDI device models.
	 * @return The current list of MIDI device models.
	 */
	public MidiDeviceModel[] getMidiDeviceModels();
	
	/**
	 * Adds the specified MIDI device.
	 * @param device The MIDI device to be added.
	 */
	public void addMidiDevice(MidiInputDevice device);
	
	/**
	 * Removes the specified MIDI device.
	 * @param deviceID The ID of the MIDI device to be removed.
	 * @return <code>true</code> if the MIDI device is removed successfully, <code>false</code>
	 * if the device list does not contain MIDI device with ID <code>deviceID</code>.
	 */
	public boolean removeMidiDevice(int deviceID);
	
	/**
	 * Gets a list of all available engines.
	 * @return A list of all available engines.
	 */
	public SamplerEngine[] getEngines();
	
	/**
	 * Gets the current list of sampler channel models.
	 * @return The current list of sampler channel models.
	 */
	public SamplerChannelModel[] getChannelModels();
	
	/**
	 * Gets the model of the sampler channel with ID <code>channelID</code>.
	 * @param channelID The ID of the sampler channel whose model should be obtained.
	 * @return The model of the specified sampler channel or <code>null</code> 
	 * if there is no channel with ID <code>channelID</code>.
	 */
	public SamplerChannelModel getChannelModel(int channelID);
	
	/**
	 * Gets the current number of sampler channels.
	 * @return The current number of sampler channels.
	 */
	public int getChannelCount();
	
	/**
	 * Creates a new sampler channel. The channel will be actually added to this model
	 * when the back-end notifies for its creation.
	 * @see #addChannel
	 */
	public void createChannel();
	
	/**
	 * Adds the specified sampler channel.
	 * @param channel The channel to be added.
	 */
	public void addChannel(SamplerChannel channel);
	
	/**
	 * Removes the specified sampler channel.
	 * @param channelID The ID of the channel to be removed.
	 * @return <code>true</code> if the channel is removed successfully, <code>false</code>
	 * if the channel's list does not contain channel with ID <code>channelID</code>.
	 */
	public boolean removeChannel(int channelID);
	
	/**
	 * Updates the settings of the specified channel.
	 * @param channel A <code>SamplerChannel</code> instance containing the new settings
	 * for the channel.
	 */
	public void changeChannel(SamplerChannel channel);
	
	/**
	 * Determines whether there is at least one solo channel in the current list
	 * of sampler channels.
	 * @return <code>true</code> if there is at least one solo channel in the current list of 
	 * sampler channels, <code>false</code> otherwise.
	 */
	public boolean hasSoloChannel();
	
	/**
	 * Gets the number of solo channels in the current list of sampler channels.
	 * @return The number of solo channels in the current list of sampler channels.
	 */
	public int getSoloChannelCount();
	
	/**
	 * Gets the number of muted channels in the current list of sampler channels.
	 * This number includes the channels muted because of the presence of a solo channel.
	 * @return The number of muted channels in the current list of sampler channels.
	 */
	public int getMutedChannelCount();
	
	/**
	 * Gets the number of channels muted because of the presence of a solo channel.
	 * @return The number of channels muted because of the presence of a solo channel.
	 */
	public int getMutedBySoloChannelCount();
	
	/**
	 * Gets the total number of active voices.
	 * @return The total number of active voices.
	 */
	public int getTotalVoiceCount();
	
	/**
	 * Gets the maximum number of active voices.
	 * @return The maximum number of active voices.
	 */
	public int getTotalVoiceCountMax();
	
	/**
	 * Updates the current and the maximum number of active voices in the sampler.
	 * @param count The new number of active voices.
	 * @param countMax The maximum number of active voices.
	 */
	public void updateActiveVoiceInfo(int count, int countMax);
}
