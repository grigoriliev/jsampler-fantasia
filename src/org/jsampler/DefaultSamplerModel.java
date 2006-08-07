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

import java.util.Vector;

import java.util.logging.Level;

import javax.swing.SwingUtilities;

import javax.swing.event.EventListenerList;

import org.jsampler.event.AudioDeviceListEvent;
import org.jsampler.event.AudioDeviceListListener;
import org.jsampler.event.MidiDeviceListEvent;
import org.jsampler.event.MidiDeviceListListener;
import org.jsampler.event.SamplerChannelListEvent;
import org.jsampler.event.SamplerChannelListListener;
import org.jsampler.event.SamplerEvent;
import org.jsampler.event.SamplerListener;

import org.jsampler.task.AddChannel;

import org.linuxsampler.lscp.*;


/**
 * This class provides default implementation of the <code>SamplerModel</code> interface.
 * @author Grigor Iliev
 */
public class DefaultSamplerModel implements SamplerModel {
	private ServerInfo serverInfo = null;
	private AudioOutputDriver[] aoDrvS = null;
	private MidiInputDriver[] miDrvS = null;
	private SamplerEngine[] engines = null;
	
	private int totalVoiceCount = 0;
	private int totalVoiceCountMax = 0;
	
	private final Vector<SamplerChannelModel> channelModels = new Vector<SamplerChannelModel>();
	private final Vector<AudioDeviceModel> audioDeviceModels = new Vector<AudioDeviceModel>();
	private final Vector<MidiDeviceModel> midiDeviceModels = new Vector<MidiDeviceModel>();
	
	private final Vector<SamplerListener> listeners = new Vector<SamplerListener>();
	private final EventListenerList listenerList = new EventListenerList();
	
	
	/** Creates a new instance of DefaultSamplerModel */
	public
	DefaultSamplerModel() {
	}
	
	/**
	 * Registers the specified listener for receiving event messages.
	 * @param l The <code>SamplerListener</code> to register.
	 */
	public void
	addSamplerListener(SamplerListener l) { listeners.add(l); }
	
	/**
	 * Removes the specified listener.
	 * @param l The <code>SamplerListener</code> to remove.
	 */
	public void
	removeSamplerListener(SamplerListener l) { listeners.remove(l); }
	
	/**
	 * Registers the specified listener for receiving event messages.
	 * @param listener The <code>AudioDeviceListListener</code> to register.
	 */
	public void
	addAudioDeviceListListener(AudioDeviceListListener listener) {
		listenerList.add(AudioDeviceListListener.class, listener);
	}
	
	/**
	 * Removes the specified listener.
	 * @param listener The <code>AudioDeviceListListener</code> to remove.
	 */
	public void
	removeAudioDeviceListListener(AudioDeviceListListener listener) {
		listenerList.remove(AudioDeviceListListener.class, listener);
	}
	
	/**
	 * Registers the specified listener for receiving event messages.
	 * @param listener The <code>MidiDeviceListListener</code> to register.
	 */
	public void
	addMidiDeviceListListener(MidiDeviceListListener listener) {
		listenerList.add(MidiDeviceListListener.class, listener);
	}
	
	/**
	 * Removes the specified listener.
	 * @param listener The <code>MidiDeviceListListener</code> to remove.
	 */
	public void
	removeMidiDeviceListListener(MidiDeviceListListener listener) {
		listenerList.remove(MidiDeviceListListener.class, listener);
	}
	
	/**
	 * Registers the specified listener for receiving event messages.
	 * @param listener The <code>SamplerChannelListListener</code> to register.
	 */
	public void
	addSamplerChannelListListener(SamplerChannelListListener listener) {
		listenerList.add(SamplerChannelListListener.class, listener);
	}
	
	/**
	 * Removes the specified listener.
	 * @param listener The <code>SamplerChannelListListener</code> to remove.
	 */
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
	 * Gets the model of the audio device with ID <code>deviceID</code>.
	 * @param deviceID The ID of the audio device whose model should be obtained.
	 * @return The model of the specified audio device or <code>null</code> 
	 * if there is no audio device with ID <code>deviceID</code>.
	 */
	public AudioDeviceModel
	getAudioDeviceModel(int deviceID) {
		for(AudioDeviceModel m : audioDeviceModels)
			if(m.getDeviceID() == deviceID) return m;
		
		return null;
	}
	
	/**
	 * Gets the current number of audio devices.
	 * @return The current number of audio devices.
	 */
	public int
	getAudioDeviceCount() { return audioDeviceModels.size(); }
	
	/**
	 * Gets the current list of audio device models.
	 * @return The current list of audio device models.
	 */
	public AudioDeviceModel[]
	getAudioDeviceModels() {
		return audioDeviceModels.toArray(new AudioDeviceModel[audioDeviceModels.size()]);
	}
	
	/**
	 * Adds the specified audio device.
	 * @param device The audio device to be added.
	 */
	public void
	addAudioDevice(AudioOutputDevice device) {
		DefaultAudioDeviceModel model = new DefaultAudioDeviceModel(device);
		audioDeviceModels.add(model);
		fireAudioDeviceAdded(model);
	}
	
	/**
	 * Removes the specified audio device.
	 * @param deviceID The ID of the audio device to be removed.
	 * @return <code>true</code> if the audio device is removed successfully, <code>false</code>
	 * if the device list does not contain audio device with ID <code>deviceID</code>.
	 */
	public boolean
	removeAudioDevice(int deviceID) {
		for(int i = 0; i < audioDeviceModels.size(); i++) {
			AudioDeviceModel m = audioDeviceModels.get(i);
			if(m.getDeviceID() == deviceID) {
				audioDeviceModels.remove(i);
				fireAudioDeviceRemoved(m);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Gets all MIDI input drivers currently available for the LinuxSampler instance.
	 * 
	 * @return <code>MidiInputDriver</code> array containing all MIDI input drivers currently 
	 * available for the LinuxSampler instance.
	 */
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
	 * Gets the model of the MIDI device with ID <code>deviceID</code>.
	 * @param deviceID The ID of the MIDI device whose model should be obtained.
	 * @return The model of the specified MIDI device or <code>null</code> 
	 * if there is no MIDI device with ID <code>deviceID</code>.
	 */
	public MidiDeviceModel
	getMidiDeviceModel(int deviceID) {
		for(MidiDeviceModel m : midiDeviceModels)
			if(m.getDeviceID() == deviceID) return m;
		
		return null;
	}
	
	/**
	 * Gets the current number of MIDI input devices.
	 * @return The current number of MIDI input devices.
	 */
	public int
	getMidiDeviceCount() { return midiDeviceModels.size(); }
	
	/**
	 * Gets the current list of MIDI device models.
	 * @return The current list of MIDI device models.
	 */
	public MidiDeviceModel[]
	getMidiDeviceModels() {
		return midiDeviceModels.toArray(new MidiDeviceModel[midiDeviceModels.size()]);
	}
	
	/**
	 * Adds the specified MIDI device.
	 * @param device The MIDI device to be added.
	 */
	public void
	addMidiDevice(MidiInputDevice device) {
		DefaultMidiDeviceModel model = new DefaultMidiDeviceModel(device);
		midiDeviceModels.add(model);
		fireMidiDeviceAdded(model);
	}
	
	/**
	 * Removes the specified MIDI device.
	 * @param deviceID The ID of the MIDI device to be removed.
	 * @return <code>true</code> if the MIDI device is removed successfully, <code>false</code>
	 * if the device list does not contain MIDI device with ID <code>deviceID</code>.
	 */
	public boolean
	removeMidiDevice(int deviceID) {
		for(int i = 0; i < midiDeviceModels.size(); i++) {
			MidiDeviceModel m = midiDeviceModels.get(i);
			if(m.getDeviceID() == deviceID) {
				midiDeviceModels.remove(i);
				fireMidiDeviceRemoved(m);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Gets a list of all available engines.
	 * @return A list of all available engines.
	 */
	public SamplerEngine[]
	getEngines() { return engines; }
	
	/**
	 * Sets the list of all available engines.
	 * @param engines The new list of all available engines.
	 */
	public void
	setEngines(SamplerEngine[] engines) { this.engines = engines; }
	
	/**
	 * Gets the model of the sampler channel with ID <code>channelID</code>.
	 * @param channelID The ID of the sampler channel whose model should be obtained.
	 * @return The model of the specified sampler channel or <code>null</code> 
	 * if there is no channel with ID <code>channelID</code>.
	 */
	public SamplerChannelModel
	getChannelModel(int channelID) {
		for(SamplerChannelModel m : channelModels)
			if(m.getChannelID() == channelID) return m;
		
		return null;
	}
	
	/**
	 * Gets the current number of sampler channels.
	 * @return The current number of sampler channels.
	 */
	public int
	getChannelCount() { return channelModels.size(); }
	
	/**
	 * Gets the current list of sampler channel models.
	 * @return The current list of sampler channel models.
	 */
	public SamplerChannelModel[]
	getChannelModels() {
		return channelModels.toArray(new SamplerChannelModel[channelModels.size()]);
	}
	
	/**
	 * Creates a new sampler channel. The channel will be actually added to this model
	 * when the back-end notifies for its creation.
	 * @see #addChannel
	 */
	public void
	createChannel() {
		CC.getTaskQueue().add(new AddChannel());
		// We leave this event to be notified by the LinuxSampler notification system.
	}
	
	/**
	 * Adds the specified sampler channel.
	 * @param channel The channel to be added.
	 */
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
	public void
	changeChannel(SamplerChannel channel) {
		for(SamplerChannelModel m : channelModels) {
			if(m.getChannelID() == channel.getChannelID()) {
				m.setChannelInfo(channel);
				return;
			}
		}
		
		CC.getLogger().log (
			Level.WARNING, "DefaultSamplerModel.unknownChannel!", channel.getChannelID()
		);
	}
	
	/**
	 * Removes the specified sampler channel.
	 * @param channelID The ID of the channel to be removed.
	 * @return <code>true</code> if the channel is removed successfully, <code>false</code>
	 * if the channel's list does not contain channel with ID <code>channelID</code>.
	 */
	public boolean
	removeChannel(int channelID) {
		for(int i = 0; i < channelModels.size(); i++) {
			SamplerChannelModel m = channelModels.get(i);
			if(m.getChannelID() == channelID) {
				channelModels.remove(i);
				fireSamplerChannelRemoved(m);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Determines whether there is at least one solo channel in the current list
	 * of sampler channels.
	 * @return <code>true</code> if there is at least one solo channel in the current list of 
	 * sampler channels, <code>false</code> otherwise.
	 */
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
	public int
	getMutedBySoloChannelCount() {
		int count = 0;
		for(SamplerChannelModel m : channelModels)
			if(m.getChannelInfo().isMutedBySolo()) count++;
		
		return count;
	}
	
	/**
	 * Gets the total number of active voices.
	 * @return The total number of active voices.
	 */
	public int
	getTotalVoiceCount() { return totalVoiceCount; }
	
	/**
	 * Gets the maximum number of active voices.
	 * @return The maximum number of active voices.
	 */
	public int
	getTotalVoiceCountMax() { return totalVoiceCountMax; }
	
	/**
	 * Updates the current and the maximum number of active voices in the sampler.
	 * @param count The new number of active voices.
	 * @param countMax The maximum number of active voices.
	 */
	public void
	updateActiveVoiceInfo(int count, int countMax) {
		if(totalVoiceCount == count && totalVoiceCountMax == countMax) return;
		
		totalVoiceCount = count;
		totalVoiceCountMax = countMax;
		fireTotalVoiceCountChanged();
	}
	
	/**
	 * Notifies listeners that a sampler channel has been added.
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
	 * This method should be invoked from the event-dispatching thread.
	 */
	private void
	fireSamplerChannelAdded(SamplerChannelListEvent e) {
		Object[] listeners = listenerList.getListenerList();
		
		for(int i = listeners.length - 2; i >= 0; i -= 2) {
			if(listeners[i] == SamplerChannelListListener.class) {
				((SamplerChannelListListener)listeners[i + 1]).channelAdded(e);
			}
		}
	}
	
	/**
	 * Notifies listeners that a sampler channel has been removed.
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
	 * This method should be invoked from the event-dispatching thread.
	 */
	private void
	fireSamplerChannelRemoved(SamplerChannelListEvent e) {
		Object[] listeners = listenerList.getListenerList();
		
		for(int i = listeners.length - 2; i >= 0; i -= 2) {
			if(listeners[i] == SamplerChannelListListener.class) {
				((SamplerChannelListListener)listeners[i + 1]).channelRemoved(e);
			}
		}
	}
	
	/**
	 * Notifies listeners that a MIDI device has been added.
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
	 * This method should be invoked from the event-dispatching thread.
	 */
	private void
	fireMidiDeviceAdded(MidiDeviceListEvent e) {
		Object[] listeners = listenerList.getListenerList();
		
		for(int i = listeners.length - 2; i >= 0; i -= 2) {
			if(listeners[i] == MidiDeviceListListener.class) {
				((MidiDeviceListListener)listeners[i + 1]).deviceAdded(e);
			}
		}
	}
	
	/**
	 * Notifies listeners that a MIDI device has been removed.
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
	 * This method should be invoked from the event-dispatching thread.
	 */
	private void
	fireMidiDeviceRemoved(MidiDeviceListEvent e) {
		Object[] listeners = listenerList.getListenerList();
		
		for(int i = listeners.length - 2; i >= 0; i -= 2) {
			if(listeners[i] == MidiDeviceListListener.class) {
				((MidiDeviceListListener)listeners[i + 1]).deviceRemoved(e);
			}
		}
	}
	
	/**
	 * Notifies listeners that an audio device has been added.
	 * @param model A <code>AudioDeviceModel</code> instance.
	 */
	private void
	fireAudioDeviceAdded(AudioDeviceModel model) {
		final AudioDeviceListEvent e = new AudioDeviceListEvent(this, model);
			
		SwingUtilities.invokeLater(new Runnable() {
			public void
			run() { fireAudioDeviceAdded(e); }
		});
	}
	
	/**
	 * Notifies listeners that an audio device has been added.
	 * This method should be invoked from the event-dispatching thread.
	 */
	private void
	fireAudioDeviceAdded(AudioDeviceListEvent e) {
		Object[] listeners = listenerList.getListenerList();
		
		for(int i = listeners.length - 2; i >= 0; i -= 2) {
			if(listeners[i] == AudioDeviceListListener.class) {
				((AudioDeviceListListener)listeners[i + 1]).deviceAdded(e);
			}
		}
	}
	
	/**
	 * Notifies listeners that an audio device has been removed.
	 * @param model A <code>AudioDeviceModel</code> instance.
	 */
	private void
	fireAudioDeviceRemoved(AudioDeviceModel model) {
		final AudioDeviceListEvent e = new AudioDeviceListEvent(this, model);
			
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
	fireAudioDeviceRemoved(AudioDeviceListEvent e) {
		Object[] listeners = listenerList.getListenerList();
		
		for(int i = listeners.length - 2; i >= 0; i -= 2) {
			if(listeners[i] == AudioDeviceListListener.class) {
				((AudioDeviceListListener)listeners[i + 1]).deviceRemoved(e);
			}
		}
	}
	
	/** Notifies listeners that the total number of active voices has changed. */
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
	 * This method should be invoked from the event-dispatching thread.
	 */
	private void
	fireTotalVoiceCountChanged(SamplerEvent e) {
		for(SamplerListener l : listeners) l.totalVoiceCountChanged(e);
	}
}
