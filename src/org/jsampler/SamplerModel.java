/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2007 Grigor Iliev <grigor@grigoriliev.com>
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

import org.jsampler.event.ListListener;
import org.jsampler.event.MidiDeviceListListener;
import org.jsampler.event.SamplerChannelListListener;
import org.jsampler.event.SamplerListener;

import org.linuxsampler.lscp.*;


/**
 * A data model representing a sampler.
 * Note that the setter methods does <b>not</b> alter any settings
 * on the backend side unless otherwise specified.
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
	 * @param listener The <code>ListListener</code> to register.
	 */
	public void addAudioDeviceListListener(ListListener<AudioDeviceModel> listener);
	
	/**
	 * Removes the specified listener.
	 * @param listener The <code>ListListener</code> to remove.
	 */
	public void removeAudioDeviceListListener(ListListener<AudioDeviceModel> listener);
	
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
	 * @param listener The <code>ListListener</code> to register.
	 */
	public void addMidiInstrumentMapListListener(ListListener<MidiInstrumentMap> listener);
	
	/**
	 * Removes the specified listener.
	 * @param listener The <code>ListListener</code> to remove.
	 */
	public void removeMidiInstrumentMapListListener(ListListener<MidiInstrumentMap> listener);
	
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
	 * Gets the model of the audio device at the specified position.
	 * @param index The position of the audio device to return.
	 * @return The model of the audio device at the specified position.
	 * @see #getAudioDeviceCount
	 */
	public AudioDeviceModel getAudioDevice(int index);
	
	/**
	 * Gets the model of the audio device with ID <code>deviceId</code>.
	 * @param deviceId The ID of the audio device whose model should be obtained.
	 * @return The model of the specified audio device or <code>null</code> 
	 * if there is no audio device with ID <code>deviceId</code>.
	 */
	public AudioDeviceModel getAudioDeviceById(int deviceId);
	
	/**
	 * Gets the current number of audio devices.
	 * @return The current number of audio devices.
	 */
	public int getAudioDeviceCount();
	
	/**
	 * Gets the current list of audio device models.
	 * @return The current list of audio device models.
	 */
	public AudioDeviceModel[] getAudioDevices();
	
	/**
	 * Adds the specified audio device.
	 * @param device The audio device to be added.
	 */
	public void addAudioDevice(AudioOutputDevice device);
	
	/**
	 * Removes the specified audio device.
	 * @param deviceId The ID of the audio device to be removed.
	 * @return <code>true</code> if the audio device is removed successfully, <code>false</code>
	 * if the device list does not contain audio device with ID <code>deviceId</code>.
	 */
	public boolean removeAudioDeviceById(int deviceId);
	
	/**
	 * Removes (on the backend side) the specified audio device.
	 * @param deviceId The ID of the audio device to be removed.
	 */
	public void removeBackendAudioDevice(int deviceId);
	
	/**
	 * Gets all MIDI input drivers currently available for the LinuxSampler instance.
	 * 
	 * @return <code>MidiInputDriver</code> array containing all MIDI input drivers currently 
	 * available for the LinuxSampler instance.
	 */
	public MidiInputDriver[] getMidiInputDrivers();
	
	/**
	 * Gets the model of the MIDI device at the specified position.
	 * @param index The position of the MIDI device to return.
	 * @return The model of the MIDI device at the specified position.
	 */
	public MidiDeviceModel getMidiDevice(int index);
	
	/**
	 * Gets the model of the MIDI device with ID <code>deviceId</code>.
	 * @param deviceId The ID of the MIDI device whose model should be obtained.
	 * @return The model of the specified MIDI device or <code>null</code> 
	 * if there is no MIDI device with ID <code>deviceId</code>.
	 */
	public MidiDeviceModel getMidiDeviceById(int deviceId);
	
	/**
	 * Gets the current number of MIDI input devices.
	 * @return The current number of MIDI input devices.
	 */
	public int getMidiDeviceCount();
	
	/**
	 * Gets the current list of MIDI device models.
	 * @return The current list of MIDI device models.
	 */
	public MidiDeviceModel[] getMidiDevices();
	
	/**
	 * Adds the specified MIDI device.
	 * @param device The MIDI device to be added.
	 */
	public void addMidiDevice(MidiInputDevice device);
	
	/**
	 * Schedules a new task for adding new MIDI device.
	 * @param driver The desired MIDI input system.
	 * @param parameters An optional list of driver specific parameters.
	 */
	public void addBackendMidiDevice(String driver, Parameter... parameters);
	
	/**
	 * Removes the specified MIDI device.
	 * @param deviceId The ID of the MIDI device to be removed.
	 * @return <code>true</code> if the MIDI device is removed successfully, <code>false</code>
	 * if the device list does not contain MIDI device with ID <code>deviceId</code>.
	 */
	public boolean removeMidiDeviceById(int deviceId);
	
	/**
	 * Schedules a new task for removing the specified MIDI device.
	 * @param deviceId The ID of the MIDI input device to be destroyed.
	 */
	public void removeBackendMidiDevice(int deviceId);
	
	/**
	 * Gets the MIDI instrument map with ID <code>mapId</code>.
	 * @param mapId The ID of the MIDI instrument map to obtain.
	 * @return The MIDI instrument map with the specified ID or <code>null</code> 
	 * if there is no MIDI instrument map with ID <code>mapId</code>.
	 */
	public MidiInstrumentMap getMidiInstrumentMapById(int mapId);
	
	/**
	 * Gets the MIDI instrument map at the specified position.
	 * @param index The position of the MIDI instrument map to return.
	 * @return The MIDI instrument map at the specified position.
	 */
	public MidiInstrumentMap getMidiInstrumentMap(int index);
	
	/**
	 * Gets the current number of MIDI instrument maps.
	 * @return The current number of MIDI instrument maps.
	 */
	public int getMidiInstrumentMapCount();
	
	/**
	 * Gets the current list of MIDI instrument maps.
	 * @return The current list of MIDI instrument maps.
	 */
	public MidiInstrumentMap[] getMidiInstrumentMaps();
	
	/**
	 * Gets the position of the specified MIDI instrument map in the list.
	 * @param map The map whose index should be returned.
	 * @return The position of the specified map in the list,
	 * or -1 if <code>map</code> is <code>null</code> or
	 * the map list does not contain the specified map.
	 */
	public int getMidiInstrumentMapIndex(MidiInstrumentMap map);
	
	/**
	 * Adds the specified MIDI instrument map.
	 * @param map The MIDI instrument map to be added.
	 */
	public void addMidiInstrumentMap(MidiInstrumentMap map);
	
	/**
	 * Schedules a new task for creating a new MIDI instrument map on the backend side.
	 * @param name The name of the MIDI instrument map.
	 * @throws IllegalArgumentException If <code>name</code> is <code>null</code>.
	 */
	public void addBackendMidiInstrumentMap(String name);
	
	/**
	 * Removes the specified MIDI instrument map.
	 * @param mapId The ID of the MIDI instrument map to be removed.
	 * @return <code>true</code> if the MIDI instrument map is removed successfully,
	 * <code>false</code> if the MIDI instrument map's list does not contain
	 * MIDI instrument map with ID <code>mapId</code>.
	 */
	public boolean removeMidiInstrumentMapById(int mapId);
	
	/** Removes all MIDI instrument maps. */
	public void removeAllMidiInstrumentMaps();
	
	/**
	 * Schedules a new task for removing the
	 * specified MIDI instrument map on the backend side.
	 * @param mapId The numerical ID of the MIDI instrument map to remove.
	 * @throws IllegalArgumentException If <code>mapId</code> is negative.
	 */
	public void removeBackendMidiInstrumentMap(int mapId);
	
	/**
	 * Schedules a new task for changing the name of
	 * the specified MIDI instrument map on the backend side.
	 * @param mapId The numerical ID of the MIDI instrument map.
	 * @param name The new name for the specified MIDI instrument map.
	 */
	public void setBackendMidiInstrumentMapName(int mapId, String name);
	
	/**
	 * Gets the default MIDI instrument map.
	 * @return The default MIDI instrument map or <code>null</code>
	 * if there are no maps created.
	 */
	public MidiInstrumentMap getDefaultMidiInstrumentMap();
	
	/**
	 * Schedules a new task for mapping a MIDI instrument on the backend side.
	 * @param mapId The id of the MIDI instrument map.
	 * @param bank The index of the MIDI bank, which shall contain the instrument.
	 * @param program The MIDI program number of the new instrument.
	 * @param instrInfo Provides the MIDI instrument settings.
	 */
	public void
	mapBackendMidiInstrument(int mapId, int bank, int program, MidiInstrumentInfo instrInfo);
	
	/**
	 * Schedules a new task for removing a MIDI instrument on the backend side.
	 * @param mapId The id of the MIDI instrument map containing the instrument to be removed.
	 * @param bank The index of the MIDI bank containing the instrument to be removed.
	 * @param program The MIDI program number of the instrument to be removed.
	 */
	public void unmapBackendMidiInstrument(int mapId, int bank, int program);
	
	/**
	 * Gets a list of all available engines.
	 * @return A list of all available engines.
	 */
	public SamplerEngine[] getEngines();
	
	/**
	 * Gets the current list of sampler channel models.
	 * @return The current list of sampler channel models.
	 */
	public SamplerChannelModel[] getChannels();
	
	/**
	 * Gets the model of the sampler channel in the specified position.
	 * @param index The position of the channel to return.
	 * @return The model of the specified sampler channel.
	 * @see #getchannelCount
	 */
	public SamplerChannelModel getChannel(int index);
	
	/**
	 * Gets the model of the sampler channel with ID <code>channelId</code>.
	 * @param channelId The ID of the sampler channel whose model should be obtained.
	 * @return The model of the specified sampler channel or <code>null</code> 
	 * if there is no channel with ID <code>channelId</code>.
	 */
	public SamplerChannelModel getChannelById(int channelId);
	
	/**
	 * Gets the position of the specified channel.
	 * @param channel The model of the channel.
	 * @return The position of the specified channel in the channel list or -1
	 * if the channel is not in the list.
	 */
	public int getChannelIndex(SamplerChannelModel channel);
	
	/**
	 * Gets the current number of sampler channels.
	 * @return The current number of sampler channels.
	 */
	public int getChannelCount();
	
	/**
	 * Adds a new sampler channel on the backend side. The channel will
	 * be actually added to this model when the backend notifies for its creation.
	 * @see #addChannel
	 */
	public void addBackendChannel();
	
	/**
	 * Adds the specified sampler channel.
	 * @param channel The channel to be added.
	 */
	public void addChannel(SamplerChannel channel);
	
	/**
	 * Removes the specified sampler channel.
	 * Note that this method doesn't remove the channel in the backend,
	 * it is used to remove the channel from the model when those channel
	 * is removed in the backend.
	 * @param channelId The ID of the channel to be removed.
	 * @return <code>true</code> if the channel is removed successfully, <code>false</code>
	 * if the channel's list does not contain channel with ID <code>channelId</code>.
	 */
	public boolean removeChannelById(int channelId);
	
	/**
	 * Schedules a new task for removing the specified sampler channel on the backend side.
	 * @param channelId The ID of the channel to be removed.
	 */
	public void removeBackendChannel(int channelId);
	
	/**
	 * Updates the settings of the specified channel.
	 * @param channel A <code>SamplerChannel</code> instance containing the new settings
	 * for the channel.
	 */
	public void updateChannel(SamplerChannel channel);
	
	/**
	 * Determines whether there are known upcoming changes to the 
	 * channel list, which should be considered as part of a single action.
	 */
	public boolean getChannelListIsAdjusting();
	
	/**
	 * Sets whether the upcoming changes to the 
	 * channel list should be considered part of a single action.
	 */
	public void setChannelListIsAdjusting(boolean b);
	
	/**
	 * Schedules a new task for starting an instrument editor for editing
	 * the loaded instrument on the specified sampler channel.
	 * @param channelId The sampler channel number.
	 */
	public void editBackendInstrument(int channelId);
	
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
	 * Gets the total number of active streams.
	 * @return The total number of active streams.
	 */
	public int getTotalStreamCount();
	
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
	 * Gets the golobal volume of the sampler.
	 * @return The golobal volume of the sampler.
	 */
	public float getVolume();
	
	/**
	 * Sets the global volume.
	 * @param volume The new volume value.
	 */
	public void setVolume(float volume);
	
	/**
	 * Sets the global volume on the backend side.
	 * @param volume The new volume value.
	 */
	public void setBackendVolume(float volume);
	
	/**
	 * Schedules a new task for resetting the sampler.
	 */
	public void resetBackend();
	
	/**
	 * Updates the current number of active disk streams in the sampler.
	 * @param count The new number of active streams.
	 */
	public void updateActiveStreamsInfo(int count);
	
	/**
	 * Updates the current and the maximum number of active voices in the sampler.
	 * @param count The new number of active voices.
	 * @param countMax The maximum number of active voices.
	 */
	public void updateActiveVoiceInfo(int count, int countMax);
	
	/**
	 * Determines whether the sampler configuration is modified.
	 */
	public boolean isModified();
	
	/**
	 * Sets whether the sampler configuration is modified.
	 */
	public void setModified(boolean b);
	
	/** Resets the model. */
	public void reset();
}
