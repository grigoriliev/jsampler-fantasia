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

import java.util.Vector;

import java.util.logging.Level;

import javax.swing.SwingUtilities;

import javax.swing.event.EventListenerList;

import net.sf.juife.Task;
import net.sf.juife.event.TaskEvent;
import net.sf.juife.event.TaskListener;

import org.jsampler.event.ListEvent;
import org.jsampler.event.ListListener;
import org.jsampler.event.MidiDeviceListEvent;
import org.jsampler.event.MidiDeviceListListener;
import org.jsampler.event.SamplerChannelListEvent;
import org.jsampler.event.SamplerChannelListListener;
import org.jsampler.event.SamplerEvent;
import org.jsampler.event.SamplerListener;

import org.jsampler.task.Audio;
import org.jsampler.task.Channel;
import org.jsampler.task.Global;
import org.jsampler.task.Midi;

import org.linuxsampler.lscp.*;


/**
 * This class provides default implementation of the <code>SamplerModel</code> interface.
 * Note that the setter methods of this class does <b>not</b> alter any settings
 * on the backend side unless otherwise specified.
 * @author Grigor Iliev
 */
public class DefaultSamplerModel implements SamplerModel {
	private ServerInfo serverInfo = null;
	private AudioOutputDriver[] aoDrvS = null;
	private MidiInputDriver[] miDrvS = null;
	private SamplerEngine[] engines = null;
	
	private int totalStreamCount = 0;
	private int totalVoiceCount = 0;
	private int totalVoiceCountMax = 0;
	
	private float volume = 1;
	private MidiInstrumentMap defaultMidiInstrumentMap;
	
	private final Vector<SamplerChannelModel> channelModels = new Vector<SamplerChannelModel>();
	private final Vector<AudioDeviceModel> audioDeviceModels = new Vector<AudioDeviceModel>();
	private final Vector<MidiDeviceModel> midiDeviceModels = new Vector<MidiDeviceModel>();
	private final Vector<MidiInstrumentMap> midiInstrMaps = new Vector<MidiInstrumentMap>();
	
	private final Vector<SamplerListener> listeners = new Vector<SamplerListener>();
	private final Vector<ListListener<MidiInstrumentMap>> mapsListeners =
		new Vector<ListListener<MidiInstrumentMap>>();
	
	private final EventListenerList listenerList = new EventListenerList();
	
	private boolean channelListIsAdjusting = false;
	
	private boolean modified = false;
	
	
	/** Creates a new instance of DefaultSamplerModel */
	public
	DefaultSamplerModel() {
		addMidiInstrumentMapListListener(getHandler());
	}
	
	/**
	 * Registers the specified listener for receiving event messages.
	 * @param l The <code>SamplerListener</code> to register.
	 */
	@Override
	public void
	addSamplerListener(SamplerListener l) { listeners.add(l); }
	
	/**
	 * Removes the specified listener.
	 * @param l The <code>SamplerListener</code> to remove.
	 */
	@Override
	public void
	removeSamplerListener(SamplerListener l) { listeners.remove(l); }
	
	/**
	 * Registers the specified listener for receiving event messages.
	 * @param listener The <code>AudioDeviceListListener</code> to register.
	 */
	@Override
	public void
	addAudioDeviceListListener(ListListener<AudioDeviceModel> listener) {
		listenerList.add(ListListener.class, listener);
	}
	
	/**
	 * Removes the specified listener.
	 * @param listener The <code>AudioDeviceListListener</code> to remove.
	 */
	@Override
	public void
	removeAudioDeviceListListener(ListListener<AudioDeviceModel> listener) {
		listenerList.remove(ListListener.class, listener);
	}
	
	/**
	 * Registers the specified listener for receiving event messages.
	 * @param listener The <code>MidiDeviceListListener</code> to register.
	 */
	@Override
	public void
	addMidiDeviceListListener(MidiDeviceListListener listener) {
		listenerList.add(MidiDeviceListListener.class, listener);
	}
	
	/**
	 * Removes the specified listener.
	 * @param listener The <code>MidiDeviceListListener</code> to remove.
	 */
	@Override
	public void
	removeMidiDeviceListListener(MidiDeviceListListener listener) {
		listenerList.remove(MidiDeviceListListener.class, listener);
	}
	
	/**
	 * Registers the specified listener for receiving event messages.
	 * @param listener The <code>ListListener</code> to register.
	 */
	@Override
	public void
	addMidiInstrumentMapListListener(ListListener<MidiInstrumentMap> listener) {
		mapsListeners.add(listener);
	}
	
	/**
	 * Removes the specified listener.
	 * @param listener The <code>ListListener</code> to remove.
	 */
	@Override
	public void
	removeMidiInstrumentMapListListener(ListListener<MidiInstrumentMap> listener) {
		mapsListeners.remove(listener);
	}
	
	/**
	 * Registers the specified listener for receiving event messages.
	 * @param listener The <code>SamplerChannelListListener</code> to register.
	 */
	@Override
	public void
	addSamplerChannelListListener(SamplerChannelListListener listener) {
		listenerList.add(SamplerChannelListListener.class, listener);
	}
	
	/**
	 * Removes the specified listener.
	 * @param listener The <code>SamplerChannelListListener</code> to remove.
	 */
	@Override
	public void
	removeSamplerChannelListListener(SamplerChannelListListener listener) {
		listenerList.remove(SamplerChannelListListener.class, listener);
	}
	
	/**
	 * Gets information about the LinuxSampler instance the front-end is connected to.
	 * 
	 * @return <code>ServerInfo</code> instance containing
	 * information about the LinuxSampler instance the front-end is connected to.
	 */
	@Override
	public ServerInfo
	getServerInfo() { return serverInfo; }
	
	/**
	 * Sets information about the LinuxSampler instance the front-end is connected to.
	 * 
	 * @param serverInfo <code>ServerInfo</code> instance containing
	 * information about the LinuxSampler instance the front-end is connected to.
	 */
	public void
	setServerInfo(ServerInfo serverInfo) { this.serverInfo = serverInfo; }
	
	/**
	 * Gets all audio output drivers currently available for the LinuxSampler instance.
	 * 
	 * @return <code>AudioOutputDriver</code> array containing all audio output drivers
	 * currently available for the LinuxSampler instance.
	 */
	@Override
	public AudioOutputDriver[]
	getAudioOutputDrivers() { return aoDrvS; }
	
	/**
	 * Sets the currently available audio output drivers for the LinuxSampler instance.
	 * 
	 * @param drivers <code>AudioOutputDriver</code> array containing all audio output drivers
	 * currently available for the LinuxSampler instance.
	 */
	public void
	setAudioOutputDrivers(AudioOutputDriver[] drivers) { aoDrvS = drivers; }
	
	/**
	 * Gets the model of the audio device at the specified position.
	 * @param index The position of the audio device to return.
	 * @return The model of the audio device at the specified position.
	 * @see #getAudioDeviceCount
	 */
	@Override
	public AudioDeviceModel
	getAudioDevice(int index) {
		return audioDeviceModels.get(index);
	}
	
	/**
	 * Gets the model of the audio device with ID <code>deviceId</code>.
	 * @param deviceId The ID of the audio device whose model should be obtained.
	 * @return The model of the specified audio device or <code>null</code> 
	 * if there is no audio device with ID <code>deviceId</code>.
	 */
	@Override
	public AudioDeviceModel
	getAudioDeviceById(int deviceId) {
		for(AudioDeviceModel m : audioDeviceModels)
			if(m.getDeviceId() == deviceId) return m;
		
		return null;
	}
	
	/**
	 * Gets the current number of audio devices.
	 * @return The current number of audio devices.
	 */
	@Override
	public int
	getAudioDeviceCount() { return audioDeviceModels.size(); }
	
	/**
	 * Gets the current list of audio device models.
	 * @return The current list of audio device models.
	 */
	@Override
	public AudioDeviceModel[]
	getAudioDevices() {
		return audioDeviceModels.toArray(new AudioDeviceModel[audioDeviceModels.size()]);
	}
	
	/**
	 * Adds the specified audio device.
	 * @param device The audio device to be added.
	 */
	@Override
	public void
	addAudioDevice(AudioOutputDevice device) {
		DefaultAudioDeviceModel model = new DefaultAudioDeviceModel(device);
		audioDeviceModels.add(model);
		fireAudioDeviceAdded(model);
	}
	
	/**
	 * Removes the specified audio device.
	 * @param deviceId The ID of the audio device to be removed.
	 * @return <code>true</code> if the audio device is removed successfully, <code>false</code>
	 * if the device list does not contain audio device with ID <code>deviceId</code>.
	 */
	@Override
	public boolean
	removeAudioDeviceById(int deviceId) {
		for(int i = 0; i < audioDeviceModels.size(); i++) {
			AudioDeviceModel m = audioDeviceModels.get(i);
			if(m.getDeviceId() == deviceId) {
				audioDeviceModels.remove(i);
				fireAudioDeviceRemoved(m);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Schedules a new task for removing the specified audio device on the backend side.
	 * @param deviceId The ID of the audio device to be removed.
	 */
	@Override
	public void
	removeBackendAudioDevice(int deviceId) {
		CC.getTaskQueue().add(new Audio.DestroyDevice(deviceId));
	}
	
	/**
	 * Gets all MIDI input drivers currently available for the LinuxSampler instance.
	 * 
	 * @return <code>MidiInputDriver</code> array containing all MIDI input drivers currently 
	 * available for the LinuxSampler instance.
	 */
	@Override
	public MidiInputDriver[]
	getMidiInputDrivers() { return miDrvS; }
	
	/**
	 * Sets the currently available MIDI input drivers for the LinuxSampler instance.
	 * 
	 * @param drivers <code>MidiInputDriver</code> array containing all MIDI input drivers
	 * currently available for the LinuxSampler instance.
	 */
	public void
	setMidiInputDrivers(MidiInputDriver[] drivers) { miDrvS = drivers; }
	
	/**
	 * Gets the model of the MIDI device at the specified position.
	 * @param index The position of the MIDI device to return.
	 * @return The model of the MIDI device at the specified position.
	 */
	@Override
	public MidiDeviceModel
	getMidiDevice(int index) {
		return midiDeviceModels.get(index);
	}
	
	/**
	 * Gets the model of the MIDI device with ID <code>deviceId</code>.
	 * @param deviceId The ID of the MIDI device whose model should be obtained.
	 * @return The model of the specified MIDI device or <code>null</code> 
	 * if there is no MIDI device with ID <code>deviceId</code>.
	 */
	@Override
	public MidiDeviceModel
	getMidiDeviceById(int deviceId) {
		for(MidiDeviceModel m : midiDeviceModels)
			if(m.getDeviceId() == deviceId) return m;
		
		return null;
	}
	
	/**
	 * Gets the current number of MIDI input devices.
	 * @return The current number of MIDI input devices.
	 */
	@Override
	public int
	getMidiDeviceCount() { return midiDeviceModels.size(); }
	
	/**
	 * Gets the current list of MIDI device models.
	 * @return The current list of MIDI device models.
	 */
	@Override
	public MidiDeviceModel[]
	getMidiDevices() {
		return midiDeviceModels.toArray(new MidiDeviceModel[midiDeviceModels.size()]);
	}
	
	/**
	 * Adds the specified MIDI device.
	 * @param device The MIDI device to be added.
	 */
	@Override
	public void
	addMidiDevice(MidiInputDevice device) {
		DefaultMidiDeviceModel model = new DefaultMidiDeviceModel(device);
		midiDeviceModels.add(model);
		fireMidiDeviceAdded(model);
	}
	
	/**
	 * Schedules a new task for adding new MIDI device.
	 * @param driver The desired MIDI input system.
	 * @param parameters An optional list of driver specific parameters.
	 */
	@Override
	public void
	addBackendMidiDevice(String driver, Parameter... parameters) {
		CC.getTaskQueue().add(new Midi.CreateDevice(driver, parameters));
	}
	
	/**
	 * Removes the specified MIDI device.
	 * @param deviceId The ID of the MIDI device to be removed.
	 * @return <code>true</code> if the MIDI device is removed successfully, <code>false</code>
	 * if the device list does not contain MIDI device with ID <code>deviceId</code>.
	 */
	@Override
	public boolean
	removeMidiDeviceById(int deviceId) {
		for(int i = 0; i < midiDeviceModels.size(); i++) {
			MidiDeviceModel m = midiDeviceModels.get(i);
			if(m.getDeviceId() == deviceId) {
				midiDeviceModels.remove(i);
				fireMidiDeviceRemoved(m);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Schedules a new task for removing the specified MIDI device.
	 * @param deviceId The ID of the MIDI input device to be destroyed.
	 */
	@Override
	public void
	removeBackendMidiDevice(int deviceId) {
		CC.getTaskQueue().add(new Midi.DestroyDevice(deviceId));
	}
	
	/**
	 * Gets the MIDI instrument map with ID <code>mapId</code>.
	 * @param mapId The ID of the MIDI instrument map to obtain.
	 * @return The MIDI instrument map with the specified ID or <code>null</code> 
	 * if there is no MIDI instrument map with ID <code>mapId</code>.
	 */
	@Override
	public MidiInstrumentMap
	getMidiInstrumentMapById(int mapId) {
		for(MidiInstrumentMap m : midiInstrMaps)
			if(m.getMapId() == mapId) return m;
		
		return null;
	}
	
	/**
	 * Gets the MIDI instrument map at the specified position.
	 * @param index The position of the MIDI instrument map to return.
	 * @return The MIDI instrument map at the specified position.
	 */
	@Override
	public MidiInstrumentMap
	getMidiInstrumentMap(int index) {
		return midiInstrMaps.get(index);
	}
	
	/**
	 * Gets the current number of MIDI instrument maps.
	 * @return The current number of MIDI instrument maps.
	 */
	@Override
	public int
	getMidiInstrumentMapCount() { return midiInstrMaps.size(); }
	
	/**
	 * Gets the current list of MIDI instrument maps.
	 * @return The current list of MIDI instrument maps.
	 */
	@Override
	public MidiInstrumentMap[]
	getMidiInstrumentMaps() {
		return midiInstrMaps.toArray(new MidiInstrumentMap[midiInstrMaps.size()]);
	}
	
	/**
	 * Gets the position of the specified MIDI instrument map in the list.
	 * @param map The map whose index should be returned.
	 * @return The position of the specified map in the list,
	 * or -1 if <code>map</code> is <code>null</code> or
	 * the map list does not contain the specified map.
	 */
	@Override
	public int
	getMidiInstrumentMapIndex(MidiInstrumentMap map) {
		if(map == null) return -1;
		
		for(int i = 0; i < getMidiInstrumentMapCount(); i++) {
			if(getMidiInstrumentMap(i) == map) return i;
		}
		
		return -1;
	}
	
	/**
	 * Adds the specified MIDI instrument map.
	 * @param map The MIDI instrument map to be added.
	 * @throws IllegalArgumentException If <code>map</code> is <code>null</code>.
	 */
	@Override
	public void
	addMidiInstrumentMap(MidiInstrumentMap map) {
		if(map == null) throw new IllegalArgumentException("map should be non-null!");
		
		midiInstrMaps.add(map);
		fireMidiInstrumentMapAdded(map);
	}
	
	/**
	 * Schedules a new task for creating a new MIDI instrument map on the backend side.
	 * @param name The name of the MIDI instrument map.
	 * @throws IllegalArgumentException If <code>name</code> is <code>null</code>.
	 */
	@Override
	public void
	addBackendMidiInstrumentMap(String name) {
		if(name == null) throw new IllegalArgumentException("name should be non-null!");
		
		CC.getTaskQueue().add(new Midi.AddInstrumentMap(name));
	}
	
	/**
	 * Removes the specified MIDI instrument map.
	 * @param mapId The ID of the MIDI instrument map to be removed.
	 * @return <code>true</code> if the MIDI instrument map is removed successfully,
	 * <code>false</code> if the MIDI instrument map's list does not contain
	 * MIDI instrument map with ID <code>mapId</code>.
	 */
	@Override
	public boolean
	removeMidiInstrumentMapById(int mapId) {
		for(int i = 0; i < midiInstrMaps.size(); i++) {
			MidiInstrumentMap m = getMidiInstrumentMap(i);
			if(m.getMapId() == mapId) {
				midiInstrMaps.remove(i);
				fireMidiInstrumentMapRemoved(m);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Removes the specified MIDI instrument map.
	 * @param map The MIDI instrument map to remove.
	 * @return <code>true</code> if the specified MIDI instrument map was in the list,
	 * <code>false</code> otherwise.
	 */
	public boolean
	removeMidiInstrumentMap(MidiInstrumentMap map) {
		boolean b = midiInstrMaps.removeElement(map);
		if(b) fireMidiInstrumentMapRemoved(map);
		return b;
	}
	
	/** Removes all MIDI instrument maps. */
	@Override
	public void
	removeAllMidiInstrumentMaps() {
		for(int i = midiInstrMaps.size() - 1; i >= 0; i--) {
			MidiInstrumentMap map = midiInstrMaps.get(i);
			midiInstrMaps.removeElementAt(i);
			fireMidiInstrumentMapRemoved(map);
		}
	}
	
	/**
	 * Schedules a new task for removing the
	 * specified MIDI instrument map on the backend side.
	 * @param mapId The numerical ID of the MIDI instrument map to remove.
	 */
	@Override
	public void
	removeBackendMidiInstrumentMap(int mapId) {
		CC.getTaskQueue().add(new Midi.RemoveInstrumentMap(mapId));
	}
	
	/**
	 * Schedules a new task for changing the name of
	 * the specified MIDI instrument map on the backend side.
	 * @param mapId The numerical ID of the MIDI instrument map.
	 * @param name The new name for the specified MIDI instrument map.
	 */
	@Override
	public void
	setBackendMidiInstrumentMapName(final int mapId, String name) {
		final Task t = new Midi.SetInstrumentMapInfo(mapId, name);
		
		t.addTaskListener(new TaskListener() {
			public void
			taskPerformed(TaskEvent e) {
				/*
				 * Because with the invokation of the method the task is considered
				 * to be done, if the task fails, we must update the settings.
				 */
				if(t.doneWithErrors()) {
					Task t2 = new Midi.UpdateInstrumentMapInfo(mapId);
					CC.getTaskQueue().add(t2);
				}
			}
		});
		CC.getTaskQueue().add(t);
	}
	
	/**
	 * Gets the default MIDI instrument map.
	 * @return The default MIDI instrument map or <code>null</code>
	 * if there are no maps created.
	 */
	@Override
	public MidiInstrumentMap
	getDefaultMidiInstrumentMap() {
		return defaultMidiInstrumentMap;
	}
	
	/**
	 * Gets the default MIDI instrument map.
	 * @return The default MIDI instrument map or <code>null</code>
	 * if there are no maps created.
	 */
	private MidiInstrumentMap
	findDefaultMidiInstrumentMap() {
		for(int i = 0; i < getMidiInstrumentMapCount(); i++) {
			MidiInstrumentMap m = getMidiInstrumentMap(i);
			if(m.getInfo().isDefault()) return m;
		}
		
		return null;
	}
	
	/**
	 * Schedules a new task for mapping a MIDI instrument on the backend side.
	 * @param mapId The id of the MIDI instrument map.
	 * @param bank The index of the MIDI bank, which shall contain the instrument.
	 * @param program The MIDI program number of the new instrument.
	 * @param instrInfo Provides the MIDI instrument settings.
	 */
	@Override
	public void
	mapBackendMidiInstrument(int mapId, int bank, int program, MidiInstrumentInfo instrInfo) {
		CC.getTaskQueue().add(new Midi.MapInstrument(mapId, bank, program, instrInfo));
	}
	
	/**
	 * Schedules a new task for removing a MIDI instrument on the backend side.
	 * @param mapId The id of the MIDI instrument map containing the instrument to be removed.
	 * @param bank The index of the MIDI bank containing the instrument to be removed.
	 * @param program The MIDI program number of the instrument to be removed.
	 */
	@Override
	public void
	unmapBackendMidiInstrument(int mapId, int bank, int program) {
		CC.getTaskQueue().add(new Midi.UnmapInstrument(mapId, bank, program));
	}
	
	/**
	 * Gets a list of all available engines.
	 * @return A list of all available engines.
	 */
	@Override
	public SamplerEngine[]
	getEngines() { return engines; }
	
	/**
	 * Sets the list of all available engines.
	 * @param engines The new list of all available engines.
	 */
	public void
	setEngines(SamplerEngine[] engines) { this.engines = engines; }
	
	/**
	 * Gets the model of the sampler channel in the specified position.
	 * @param index The position of the channel to return.
	 * @return The model of the specified sampler channel.
	 * @see #getchannelCount
	 */
	@Override
	public SamplerChannelModel
	getChannel(int index) {
		return channelModels.get(index);
	}
	
	/**
	 * Gets the model of the sampler channel with ID <code>channelId</code>.
	 * @param channelId The ID of the sampler channel whose model should be obtained.
	 * @return The model of the specified sampler channel or <code>null</code> 
	 * if there is no channel with ID <code>channelId</code>.
	 */
	@Override
	public SamplerChannelModel
	getChannelById(int channelId) {
		for(SamplerChannelModel m : channelModels)
			if(m.getChannelId() == channelId) return m;
		
		return null;
	}
	
	/**
	 * Gets the position of the specified channel.
	 * @param channel The model of the channel.
	 * @return The position of the specified channel in the channel list or -1
	 * if the channel is not in the list.
	 */
	@Override
	public int
	getChannelIndex(SamplerChannelModel channel) {
		if(channel == null) return -1;
		for(int i = 0; i < channelModels.size(); i++) {
			if(channelModels.get(i).getChannelId() == channel.getChannelId()) return i;
		}
		
		return -1;
	}
	
	/**
	 * Gets the current number of sampler channels.
	 * @return The current number of sampler channels.
	 */
	@Override
	public int
	getChannelCount() { return channelModels.size(); }
	
	/**
	 * Gets the current list of sampler channel models.
	 * @return The current list of sampler channel models.
	 */
	@Override
	public SamplerChannelModel[]
	getChannels() {
		return channelModels.toArray(new SamplerChannelModel[channelModels.size()]);
	}
	
	/**
	 * Schedules a new task for adding a new sampler channel on the
	 * backend side. The channel will be actually added to this model
	 * when the backend notifies for its creation.
	 * @see #addChannel
	 */
	@Override
	public void
	addBackendChannel() {
		CC.getTaskQueue().add(new Channel.Add());
		// We leave this event to be notified by the LinuxSampler notification system.
	}
	
	/**
	 * Adds the specified sampler channel.
	 * @param channel The channel to be added.
	 */
	@Override
	public void
	addChannel(SamplerChannel channel) {
		DefaultSamplerChannelModel model = new DefaultSamplerChannelModel(channel);
		channelModels.add(model);
		fireSamplerChannelAdded(model);
	}
	
	/**
	 * Updates the settings of the specified channel.
	 * @param channel A <code>SamplerChannel</code> instance containing the new settings
	 * for the channel.
	 */
	@Override
	public void
	updateChannel(SamplerChannel channel) {
		for(SamplerChannelModel m : channelModels) {
			if(m.getChannelId() == channel.getChannelId()) {
				m.setChannelInfo(channel);
				return;
			}
		}
		
		CC.getLogger().log (
			Level.WARNING, "DefaultSamplerModel.unknownChannel!", channel.getChannelId()
		);
	}
	
	/**
	 * Determines whether there are known upcoming changes to the 
	 * channel list, which should be considered as part of a single action.
	 */
	@Override
	public synchronized boolean
	getChannelListIsAdjusting() { return channelListIsAdjusting; }
	
	/**
	 * Sets whether the upcoming changes to the 
	 * channel list should be considered part of a single action.
	 */
	@Override
	public synchronized void
	setChannelListIsAdjusting(boolean b) {
		channelListIsAdjusting = b;
	}
	
	/**
	 * Removes the specified sampler channel.
	 * Note that this method doesn't remove the channel in the backend,
	 * it is used to remove the channel from the model when those channel
	 * is removed in the backend.
	 * @param channelId The ID of the channel to be removed.
	 * @return <code>true</code> if the channel is removed successfully, <code>false</code>
	 * if the channel's list does not contain channel with ID <code>channelId</code>.
	 */
	@Override
	public boolean
	removeChannelById(int channelId) {
		for(int i = 0; i < channelModels.size(); i++) {
			SamplerChannelModel m = channelModels.get(i);
			if(m.getChannelId() == channelId) {
				channelModels.remove(i);
				fireSamplerChannelRemoved(m);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Removes all sampler channels.
	 * Note that this method doesn't remove the channel in the backend.
	 */
	@Override
	public void
	removeAllChannels() {
		if(channelModels.size() == 0) return;
		
		setChannelListIsAdjusting(true);
		for(int i = channelModels.size() - 1; i > 0; i--) {
			SamplerChannelModel m = channelModels.get(i);
			channelModels.remove(i);
			fireSamplerChannelRemoved(m);
		}
		setChannelListIsAdjusting(false);
		
		SamplerChannelModel m = channelModels.get(0);
		channelModels.remove(0);
		fireSamplerChannelRemoved(m);
	}
	
	/**
	 * Schedules a new task for removing the specified sampler channel on the backend side.
	 * @param channelId The ID of the channel to be removed.
	 */
	@Override
	public void
	removeBackendChannel(int channelId) {
		CC.getTaskQueue().add(new Channel.Remove(channelId));
	}
	
	/**
	 * Schedules a new task for starting an instrument editor for editing
	 * the loaded instrument on the specified sampler channel.
	 * @param channelId The sampler channel number.
	 */
	@Override
	public void
	editBackendInstrument(int channelId) {
		CC.getTaskQueue().add(new Channel.EditInstrument(channelId));
	}
	
	/**
	 * Determines whether there is at least one solo channel in the current list
	 * of sampler channels.
	 * @return <code>true</code> if there is at least one solo channel in the current list of 
	 * sampler channels, <code>false</code> otherwise.
	 */
	@Override
	public boolean
	hasSoloChannel() {
		for(SamplerChannelModel m : channelModels)
			if(m.getChannelInfo().isSoloChannel()) return true;
		
		return false;
	}
	
	/**
	 * Gets the number of solo channels in the current list of sampler channels.
	 * @return The number of solo channels in the current list of sampler channels.
	 */
	@Override
	public int
	getSoloChannelCount() {
		int count = 0;
		for(SamplerChannelModel m : channelModels)
			if(m.getChannelInfo().isSoloChannel()) count++;
		
		return count;
	}
	
	/**
	 * Gets the number of muted channels in the current list of sampler channels.
	 * This number includes the channels muted because of the presence of a solo channel.
	 * @return The number of muted channels in the current list of sampler channels.
	 */
	@Override
	public int
	getMutedChannelCount() {
		int count = 0;
		for(SamplerChannelModel m : channelModels)
			if(m.getChannelInfo().isMuted()) count++;
		
		return count;
	}
	
	/**
	 * Gets the number of channels muted because of the presence of a solo channel.
	 * @return The number of channels muted because of the presence of a solo channel.
	 */
	@Override
	public int
	getMutedBySoloChannelCount() {
		int count = 0;
		for(SamplerChannelModel m : channelModels)
			if(m.getChannelInfo().isMutedBySolo()) count++;
		
		return count;
	}
	
	/**
	 * Gets the total number of active streams.
	 * @return The total number of active streams.
	 */
	@Override
	public int
	getTotalStreamCount() { return totalStreamCount; }
	
	/**
	 * Gets the total number of active voices.
	 * @return The total number of active voices.
	 */
	@Override
	public int
	getTotalVoiceCount() { return totalVoiceCount; }
	
	/**
	 * Gets the maximum number of active voices.
	 * @return The maximum number of active voices.
	 */
	@Override
	public int
	getTotalVoiceCountMax() { return totalVoiceCountMax; }
	
	/**
	 * Gets the golobal volume of the sampler.
	 * @return The golobal volume of the sampler.
	 */
	@Override
	public float
	getVolume() { return volume; }
	
	/**
	 * Sets the global volume.
	 * @param volume The new volume value.
	 */
	@Override
	public void
	setVolume(float volume) {
		if(this.volume == volume) return;
		
		this.volume = volume;
		fireVolumeChanged();
	}
	
	/**
	 * Schedules a new task for setting the global volume on the backend side.
	 * @param volume The new volume value.
	 */
	@Override
	public void
	setBackendVolume(float volume) {
		CC.getTaskQueue().add(new Global.SetVolume(volume));
	}
	
	/**
	 * Schedules a new task for resetting the sampler.
	 */
	@Override
	public void
	resetBackend() { CC.getTaskQueue().add(new org.jsampler.task.Global.ResetSampler()); }
	
	/**
	 * Updates the current number of active disk streams in the sampler.
	 * @param count The new number of active streams.
	 */
	@Override
	public void
	updateActiveStreamsInfo(int count) {
		if(totalStreamCount == count) return;
		
		totalStreamCount = count;
		fireTotalStreamCountChanged();
	}
	
	/**
	 * Updates the current and the maximum number of active voices in the sampler.
	 * @param count The new number of active voices.
	 * @param countMax The maximum number of active voices.
	 */
	@Override
	public void
	updateActiveVoiceInfo(int count, int countMax) {
		if(totalVoiceCount == count && totalVoiceCountMax == countMax) return;
		
		totalVoiceCount = count;
		totalVoiceCountMax = countMax;
		fireTotalVoiceCountChanged();
	}
	
	/**
	 * Determines whether the sampler configuration is modified.
	 */
	@Override
	public boolean
	isModified() { return modified; }
	
	/**
	 * Sets whether the sampler configuration is modified.
	 */
	@Override
	public void
	setModified(boolean b) { modified = b; }
	
	/** Resets the model. */
	@Override
	public void
	reset() {
		removeAllMidiInstrumentMaps();
		
		for(int i = channelModels.size() - 1; i >= 0; i--) {
			SamplerChannelModel m = channelModels.get(i);
			channelModels.remove(i);
			fireSamplerChannelRemoved(m);
		}
		
		for(int i = midiDeviceModels.size() - 1; i >= 0; i--) {
			MidiDeviceModel m = midiDeviceModels.get(i);
			midiDeviceModels.remove(i);
			fireMidiDeviceRemoved(m);
		}
		
		for(int i = audioDeviceModels.size() - 1; i >= 0; i--) {
			AudioDeviceModel m = audioDeviceModels.get(i);
			audioDeviceModels.remove(i);
			fireAudioDeviceRemoved(m);
		}
		
		setServerInfo(null);
		setAudioOutputDrivers(null);
		setMidiInputDrivers(null);
		setEngines(null);
		
		setVolume(0);
		setModified(false);
		
		totalStreamCount = 0;
		totalVoiceCount = 0;
		totalVoiceCountMax = 0;
		
		fireTotalStreamCountChanged();
		fireTotalVoiceCountChanged();
		fireDefaultMapChanged();
	}
	
	/**
	 * Notifies listeners that a sampler channel has been added.
	 * This method can be invoked outside the event-dispatching thread.
	 * @param channelModel A <code>SamplerChannelModel</code> instance.
	 */
	private void
	fireSamplerChannelAdded(SamplerChannelModel channelModel) {
		final SamplerChannelListEvent e = new SamplerChannelListEvent(this, channelModel);
			
		SwingUtilities.invokeLater(new Runnable() {
			public void
			run() { fireSamplerChannelAdded(e); }
		});
	}
	/**
	 * Notifies listeners that a sampler channel has been added.
	 */
	private void
	fireSamplerChannelAdded(SamplerChannelListEvent e) {
		setModified(true);
		Object[] listeners = listenerList.getListenerList();
		
		for(int i = listeners.length - 2; i >= 0; i -= 2) {
			if(listeners[i] == SamplerChannelListListener.class) {
				((SamplerChannelListListener)listeners[i + 1]).channelAdded(e);
			}
		}
	}
	
	/**
	 * Notifies listeners that a sampler channel has been removed.
	 * This method can be invoked outside the event-dispatching thread.
	 * @param channelModel A <code>SamplerChannelModel</code> instance.
	 */
	private void
	fireSamplerChannelRemoved(SamplerChannelModel channelModel) {
		final SamplerChannelListEvent e = new SamplerChannelListEvent(this, channelModel);
			
		SwingUtilities.invokeLater(new Runnable() {
			public void
			run() { fireSamplerChannelRemoved(e); }
		});
	}
	
	/**
	 * Notifies listeners that a sampler channel has been removed.
	 */
	private void
	fireSamplerChannelRemoved(SamplerChannelListEvent e) {
		setModified(true);
		Object[] listeners = listenerList.getListenerList();
		
		for(int i = listeners.length - 2; i >= 0; i -= 2) {
			if(listeners[i] == SamplerChannelListListener.class) {
				((SamplerChannelListListener)listeners[i + 1]).channelRemoved(e);
			}
		}
	}
	
	/**
	 * Notifies listeners that a MIDI device has been added.
	 * This method can be invoked outside the event-dispatching thread.
	 * @param model A <code>MidiDeviceModel</code> instance.
	 */
	private void
	fireMidiDeviceAdded(MidiDeviceModel model) {
		final MidiDeviceListEvent e = new MidiDeviceListEvent(this, model);
			
		SwingUtilities.invokeLater(new Runnable() {
			public void
			run() { fireMidiDeviceAdded(e); }
		});
	}
	/**
	 * Notifies listeners that a MIDI device has been added.
	 */
	private void
	fireMidiDeviceAdded(MidiDeviceListEvent e) {
		setModified(true);
		Object[] listeners = listenerList.getListenerList();
		
		for(int i = listeners.length - 2; i >= 0; i -= 2) {
			if(listeners[i] == MidiDeviceListListener.class) {
				((MidiDeviceListListener)listeners[i + 1]).deviceAdded(e);
			}
		}
	}
	
	/**
	 * Notifies listeners that a MIDI device has been removed.
	 * This method can be invoked outside the event-dispatching thread.
	 * @param model A <code>MidiDeviceModel</code> instance.
	 */
	private void
	fireMidiDeviceRemoved(MidiDeviceModel model) {
		final MidiDeviceListEvent e = new MidiDeviceListEvent(this, model);
			
		SwingUtilities.invokeLater(new Runnable() {
			public void
			run() { fireMidiDeviceRemoved(e); }
		});
	}
	
	/**
	 * Notifies listeners that a MIDI device has been removed.
	 */
	private void
	fireMidiDeviceRemoved(MidiDeviceListEvent e) {
		setModified(true);
		Object[] listeners = listenerList.getListenerList();
		
		for(int i = listeners.length - 2; i >= 0; i -= 2) {
			if(listeners[i] == MidiDeviceListListener.class) {
				((MidiDeviceListListener)listeners[i + 1]).deviceRemoved(e);
			}
		}
	}
	
	/**
	 * Notifies listeners that an audio device has been added.
	 * This method can be invoked outside the event-dispatching thread.
	 * @param model A <code>AudioDeviceModel</code> instance.
	 */
	private void
	fireAudioDeviceAdded(AudioDeviceModel model) {
		final ListEvent<AudioDeviceModel> e = new ListEvent<AudioDeviceModel>(this, model);
			
		SwingUtilities.invokeLater(new Runnable() {
			public void
			run() { fireAudioDeviceAdded(e); }
		});
	}
	
	/**
	 * Notifies listeners that an audio device has been added.
	 */
	private void
	fireAudioDeviceAdded(ListEvent<AudioDeviceModel> e) {
		setModified(true);
		Object[] listeners = listenerList.getListenerList();
		
		for(int i = listeners.length - 2; i >= 0; i -= 2) {
			if(listeners[i] == ListListener.class) {
				((ListListener<AudioDeviceModel>)listeners[i + 1]).entryAdded(e);
			}
		}
	}
	
	/**
	 * Notifies listeners that an audio device has been removed.
	 * This method can be invoked outside the event-dispatching thread.
	 * @param model A <code>AudioDeviceModel</code> instance.
	 */
	private void
	fireAudioDeviceRemoved(AudioDeviceModel model) {
		final ListEvent<AudioDeviceModel> e = new ListEvent<AudioDeviceModel>(this, model);
			
		SwingUtilities.invokeLater(new Runnable() {
			public void
			run() { fireAudioDeviceRemoved(e); }
		});
	}
	
	/**
	 * Notifies listeners that an audio device has been removed.
	 * This method should be invoked from the event-dispatching thread.
	 */
	private void
	fireAudioDeviceRemoved(ListEvent<AudioDeviceModel> e) {
		setModified(true);
		Object[] listeners = listenerList.getListenerList();
		
		for(int i = listeners.length - 2; i >= 0; i -= 2) {
			if(listeners[i] == ListListener.class) {
				((ListListener<AudioDeviceModel>)listeners[i + 1]).entryRemoved(e);
			}
		}
	}
	
	/**
	 * Notifies listeners that a MIDI instrument map has been added to the list.
	 * This method can be invoked outside the event-dispatching thread.
	 */
	private void
	fireMidiInstrumentMapAdded(MidiInstrumentMap map) {
		final ListEvent<MidiInstrumentMap> e = new ListEvent<MidiInstrumentMap>(this, map);
		
		SwingUtilities.invokeLater(new Runnable() {
			public void
			run() { fireMidiInstrumentMapAdded(e); }
		});
	}
	
	/** Notifies listeners that a MIDI instrument map has been added to the list. */
	private void
	fireMidiInstrumentMapAdded(ListEvent<MidiInstrumentMap> e) {
		setModified(true);
		for(ListListener<MidiInstrumentMap> l : mapsListeners) l.entryAdded(e);
	}
	
	/**
	 * Notifies listeners that a MIDI instrument map has been removed from the list.
	 * This method can be invoked outside the event-dispatching thread.
	 */
	private void
	fireMidiInstrumentMapRemoved(MidiInstrumentMap map) {
		final ListEvent<MidiInstrumentMap> e = new ListEvent<MidiInstrumentMap>(this, map);
		
		SwingUtilities.invokeLater(new Runnable() {
			public void
			run() { fireMidiInstrumentMapRemoved(e); }
		});
	}
	/** Notifies listeners that a MIDI instrument map has been removed from the list. */
	private void
	fireMidiInstrumentMapRemoved(ListEvent<MidiInstrumentMap> e) {
		setModified(true);
		for(ListListener<MidiInstrumentMap> l : mapsListeners) l.entryRemoved(e);
	}
	
	/**
	 * Notifies listeners that the global volume has changed.
	 * This method can be invoked outside the event-dispatching thread.
	 */
	private void
	fireVolumeChanged() {
		final SamplerEvent e = new SamplerEvent(this);
			
		SwingUtilities.invokeLater(new Runnable() {
			public void
			run() { fireVolumeChanged(e); }
		});
	}
	
	/**
	 * Notifies listeners that the global volume has changed.
	 */
	private void
	fireVolumeChanged(SamplerEvent e) {
		setModified(true);
		for(SamplerListener l : listeners) l.volumeChanged(e);
	}
	
	/*
	 * Notifies listeners that the total number of active streams has changed.
	 * This method can be invoked outside the event-dispatching thread.
	 */
	private void
	fireTotalStreamCountChanged() {
		final SamplerEvent e = new SamplerEvent(this);
			
		SwingUtilities.invokeLater(new Runnable() {
			public void
			run() { fireTotalStreamCountChanged(e); }
		});
	}
	
	/**
	 * Notifies listeners that the total number of active streams has changed.
	 */
	private void
	fireTotalStreamCountChanged(SamplerEvent e) {
		for(SamplerListener l : listeners) l.totalStreamCountChanged(e);
	}
	
	/*
	 * Notifies listeners that the total number of active voices has changed.
	 * This method can be invoked outside the event-dispatching thread.
	 */
	private void
	fireTotalVoiceCountChanged() {
		final SamplerEvent e = new SamplerEvent(this);
			
		SwingUtilities.invokeLater(new Runnable() {
			public void
			run() { fireTotalVoiceCountChanged(e); }
		});
	}
	
	/**
	 * Notifies listeners that the total number of active voices has changed.
	 */
	private void
	fireTotalVoiceCountChanged(SamplerEvent e) {
		for(SamplerListener l : listeners) l.totalVoiceCountChanged(e);
	}
	
	/**
	 * Notifies listeners that the default MIDI instrument map is changed.
	 */
	private void
	fireDefaultMapChanged() {
		SamplerEvent e = new SamplerEvent(this);
		for(SamplerListener l : listeners) l.defaultMapChanged(e);
	}
	
	private final Handler handler = new Handler();
	
	private Handler
	getHandler() { return handler; }
	
	private class Handler implements ListListener<MidiInstrumentMap> {
		/** Invoked when a new map is added to a list. */
		public void
		entryAdded(ListEvent<MidiInstrumentMap> e) { updateDefaultMap(); }
		
		/** Invoked when a map is removed from a list. */
		public void
		entryRemoved(ListEvent<MidiInstrumentMap> e) { updateDefaultMap(); }
		
		private void
		updateDefaultMap() {
			if(getDefaultMidiInstrumentMap() != findDefaultMidiInstrumentMap()) {
				defaultMidiInstrumentMap = findDefaultMidiInstrumentMap();
				fireDefaultMapChanged();
			}
		}
	}
}
