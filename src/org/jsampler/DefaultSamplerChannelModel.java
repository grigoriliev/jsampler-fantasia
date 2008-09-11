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

import java.util.Vector;

import javax.swing.SwingUtilities;

import net.sf.juife.Task;
import net.sf.juife.event.TaskEvent;
import net.sf.juife.event.TaskListener;

import org.jsampler.event.EffectSendsEvent;
import org.jsampler.event.EffectSendsListener;
import org.jsampler.event.SamplerChannelEvent;
import org.jsampler.event.SamplerChannelListener;

import org.jsampler.task.Channel;
import org.jsampler.task.Channel.LoadEngine;
import org.jsampler.task.Channel.LoadInstrument;
import org.jsampler.task.Channel.SetMidiInputChannel;
import org.jsampler.task.Channel.SetMidiInputDevice;
import org.jsampler.task.Channel.SetMidiInputPort;
import org.jsampler.task.Channel.SetMute;
import org.jsampler.task.Channel.SetSolo;
import org.jsampler.task.Channel.SetVolume;
import org.jsampler.task.Channel.UpdateFxSendInfo;
import org.jsampler.task.DuplicateChannels;

import org.linuxsampler.lscp.FxSend;
import org.linuxsampler.lscp.SamplerChannel;

import org.linuxsampler.lscp.event.MidiDataEvent;
import org.linuxsampler.lscp.event.MidiDataListener;


/**
 * This class provides default implementation of the <code>SamplerChannelModel</code> interface.
 * Note that all methods that begin with <code>setBackend</code> alter the settings
 * on the backend side.
 * @author Grigor Iliev
 */
public class DefaultSamplerChannelModel implements SamplerChannelModel {
	private SamplerChannel channel;
	private int streamCount = 0;
	private int voiceCount = 0;
	
	private final Vector<SamplerChannelListener> listeners =
		new Vector<SamplerChannelListener>();
	
	private final Vector<EffectSendsListener> fxListeners = new Vector<EffectSendsListener>();
	
	private final Vector<FxSend> fxSends = new Vector<FxSend>();
	
	private final Vector<MidiDataListener> midiListeners = new Vector<MidiDataListener>();
	
	/**
	 * Creates a new instance of <code>DefaultSamplerChannelModel</code> using the
	 * specified channel settings.
	 * @param channel A non-null <code>SamplerChannel</code> instance containing the current
	 * settings of the channel which will be represented by this sampler channel model.
	 * @throws IllegalArgumentException If <code>channel</code> is <code>null</code>.
	 */
	public DefaultSamplerChannelModel(SamplerChannel channel) {
		if(channel == null) throw new IllegalArgumentException("channel must be non null");
		this.channel = channel;
	}
	
	/**
	 * Registers the specified listener for receiving event messages.
	 * @param l The <code>SamplerChannelListener</code> to register.
	 */
	public void
	addSamplerChannelListener(SamplerChannelListener l) { listeners.add(l); }
	
	/**
	 * Removes the specified listener.
	 * @param l The <code>SamplerChannelListener</code> to remove.
	 */
	public void
	removeSamplerChannelListener(SamplerChannelListener l) { listeners.remove(l); }
	
	/**
	 * Registers the specified listener for receiving event messages.
	 * @param l The <code>EffectSendsListener</code> to register.
	 */
	public void
	addEffectSendsListener(EffectSendsListener l) { fxListeners.add(l); }
	
	/**
	 * Removes the specified listener.
	 * @param l The <code>EffectSendsListener</code> to remove.
	 */
	public void
	removeEffectSendsListener(EffectSendsListener l) { fxListeners.remove(l); }
	
	/**
	 * Registers the specified listener to be notified when
	 * MIDI events are sent to the channel.
	 * @param l The <code>MidiDataListener</code> to register.
	 */
	public void
	addMidiDataListener(MidiDataListener l) { midiListeners.add(l); }
	
	/**
	 * Removes the specified listener.
	 * @param l The <code>MidiDataListener</code> to remove.
	 */
	public void
	removeMidiDataListener(MidiDataListener l) { midiListeners.remove(l); }
	
	/**
	 * Gets the sampler channel number.
	 * @return The sampler channel number or -1 if the sampler channel number is not set.
	 */
	public int
	getChannelId() { return channel == null ? -1 : channel.getChannelId(); }
	
	/**
	 * Gets the current settings of the sampler channel.
	 * @return <code>SamplerChannel</code> instance containing
	 * the current settings of the sampler channel.
	 */
	public SamplerChannel
	getChannelInfo() { return channel; }
	
	/**
	 * Sets the current settings of the sampler channel.
	 * Note that this method does not changes the channel settings on
	 * the backend. It is invoked just notify for channel settings' changes.
	 * @param channel A <code>SamplerChannel</code> instance containing
	 * the new settings for this sampler channel.
	 * @throws IllegalArgumentException If <code>channel</code> is <code>null</code>.
	 */
	public void
	setChannelInfo(SamplerChannel channel) {
		if(channel == null) throw new IllegalArgumentException("channel must be non null");
		if(this.channel == channel) return;
		
		this.channel = channel;
		fireSamplerChannelChanged();
	}
	
	/**
	 * Gets the number of active disk streams.
	 * @return The number of active disk streams.
	 */
	public int
	getStreamCount() { return streamCount; }
	
	/**
	 * Sets the number of active disk streams.
	 * Note that this method does <b>not</b> alter the number
	 * of active disk streams on the backend side.
	 * @param count The new number of active disk streams.
	 */
	public void
	setStreamCount(int count) {
		if(streamCount == count) return;
		
		streamCount = count;
		fireStreamCountChanged();
	}
	
	/**
	 * Gets the number of active voices.
	 * @return The number of active voices.
	 */
	public int
	getVoiceCount() { return voiceCount; }
	
	/**
	 * Sets the number of active voices.
	 * Note that this method does <b>not</b> alter the number
	 * of active voices on the backend side.
	 * @param count The new number of active voices.
	 */
	public void
	setVoiceCount(int count) {
		if(voiceCount == count) return;
		
		voiceCount = count;
		fireVoiceCountChanged();
	}
	
	/**
	 * Schedules a new task for setting the sampler engine type to be used.
	 * @param engine The name of the engine type to be used.
	 */
	public void
	setBackendEngineType(String engine) {
		final LoadEngine loadEngine = new LoadEngine(engine, getChannelId());
		final SamplerChannelEvent event = new SamplerChannelEvent(this);
		
		loadEngine.addTaskListener(new TaskListener() {
			public void
			taskPerformed(TaskEvent e) {
				/*
				 * Because with the invokation of the method the task is considered
				 * to be done, if the task fails, we must notify for a channel
				 * changes. This should be done to revert the old channel settings.
				 */
				if(loadEngine.doneWithErrors()) fireSamplerChannelChanged(event);
			}
		});
		CC.getTaskQueue().add(loadEngine);
		
		// We leave this event to be notified by the LinuxSampler notification system.
	}
	
	/**
	 * Schedules a new task for setting the mute mode of the channel. 
	 * @param mute Specifies the mute mode. If <code>true</code> the channel is muted, else
	 * the channel is unmuted.
	 */
	public void
	setBackendMute(boolean mute) {
		final SetMute smc = new SetMute(getChannelId(), mute);
		final SamplerChannelEvent event = new SamplerChannelEvent(this);
		
		smc.addTaskListener(new TaskListener() {
			public void
			taskPerformed(TaskEvent e) {
				/*
				 * Because with the invokation of the method the task is considered
				 * to be done, if the task fails, we must notify for a channel
				 * changes. This should be done to revert the old channel settings.
				 */
				if(smc.doneWithErrors()) fireSamplerChannelChanged(event);
			}
		});
		CC.getTaskQueue().add(smc);
		
		// We leave this event to be notified by the LinuxSampler notification system.
	}
	
	/**
	 * Schedules a new task for setting on the backend side the solo mode of the channel. 
	 * @param solo Specifies the solo mode. If <code>true</code> the channel is soloed, else
	 * the channel is unsoloed.
	 */
	public void
	setBackendSolo(boolean solo) {
		final SetSolo ssc = new SetSolo(getChannelId(), solo);
		final SamplerChannelEvent event = new SamplerChannelEvent(this);
		
		ssc.addTaskListener(new TaskListener() {
			public void
			taskPerformed(TaskEvent e) {
				/*
				 * Because with the invokation of the method the task is considered
				 * to be done, if the task fails, we must notify for a channel
				 * changes. This should be done to revert the old channel settings.
				 */
				if(ssc.doneWithErrors()) fireSamplerChannelChanged(event);
			}
		});
		CC.getTaskQueue().add(ssc);
		
		// We leave this event to be notified by the LinuxSampler notification system.
	}
	
	/**
	 * Schedules a new task for setting the channel volume on the backend side.
	 * @param volume Specifies the new volume value.
	 */
	public void
	setBackendVolume(float volume) {
		final SetVolume scv = new SetVolume(getChannelId(), volume);
		final SamplerChannelEvent event = new SamplerChannelEvent(this);
		
		scv.addTaskListener(new TaskListener() {
			public void
			taskPerformed(TaskEvent e) {
				/*
				 * Because with the invokation of the method the task is considered
				 * to be done, if the task fails, we must notify for a channel
				 * changes. This should be done to revert the old channel settings.
				 */
				if(scv.doneWithErrors()) fireSamplerChannelChanged(event);
			}
		});
		CC.getTaskQueue().add(scv);
		
		// We leave this event to be notified by the LinuxSampler notification system.
	}
	
	/**
	 * Schedules a new task for setting on the backend side the MIDI input
	 * device of the channel represented by this model. 
	 * @param deviceId Specifies the numerical ID of the MIDI input device to be set.
	 */
	public void
	setBackendMidiInputDevice(int deviceId) {
		final Task scmid = new SetMidiInputDevice(getChannelId(), deviceId);
		final SamplerChannelEvent event = new SamplerChannelEvent(this);
		
		scmid.addTaskListener(new TaskListener() {
			public void
			taskPerformed(TaskEvent e) {
				/*
				 * Because with the invokation of the method the task is considered
				 * to be done, if the task fails, we must notify for a channel
				 * changes. This should be done to revert the old channel settings.
				 */
				if(scmid.doneWithErrors()) fireSamplerChannelChanged(event);
			}
		});
		CC.getTaskQueue().add(scmid);
		
		// We leave this event to be notified by the LinuxSampler notification system.
	}
	
	/**
	 * Schedules a new task for setting (on the backend side) the
	 * MIDI input port of the channel represented by this model. 
	 * @param port Specifies the number of the MIDI input port.
	 */
	public void
	setBackendMidiInputPort(int port) {
		final Task scmip = new SetMidiInputPort(getChannelId(), port);
		final SamplerChannelEvent event = new SamplerChannelEvent(this);
		
		scmip.addTaskListener(new TaskListener() {
			public void
			taskPerformed(TaskEvent e) {
				/*
				 * Because with the invokation of the method the task is considered
				 * to be done, if the task fails, we must notify for a channel
				 * changes. This should be done to revert the old channel settings.
				 */
				if(scmip.doneWithErrors()) fireSamplerChannelChanged(event);
			}
		});
		CC.getTaskQueue().add(scmip);
		
		// We leave this event to be notified by the LinuxSampler notification system.
	}
	
	/**
	 * Schedules a new task for setting (on the backend side) the MIDI channel
	 * that the channel represented by this model should listen to. 
	 * @param channel Specifies the MIDI channel that the channel
	 * represented by this model should listen to.
	 */
	public void
	setBackendMidiInputChannel(int channel) {
		final Task scmic = new SetMidiInputChannel(getChannelId(), channel);
		final SamplerChannelEvent event = new SamplerChannelEvent(this);
		
		scmic.addTaskListener(new TaskListener() {
			public void
			taskPerformed(TaskEvent e) {
				/*
				 * Because with the invokation of the method the task is considered
				 * to be done, if the task fails, we must notify for a channel
				 * changes. This should be done to revert the old channel settings.
				 */
				if(scmic.doneWithErrors()) fireSamplerChannelChanged(event);
			}
		});
		CC.getTaskQueue().add(scmic);
		
		// We leave this event to be notified by the LinuxSampler notification system.
	}
	
	/**
	 * Schedules a new task for setting (on the backend side) the audio output
	 * device of the channel represented by this model. 
	 * @param deviceId Specifies the numerical ID of the audio output device to be set.
	 */
	public void
	setBackendAudioOutputDevice(int deviceId) {
		final Task scaod = new Channel.SetAudioOutputDevice(getChannelId(), deviceId);
		final SamplerChannelEvent event = new SamplerChannelEvent(this);
		
		scaod.addTaskListener(new TaskListener() {
			public void
			taskPerformed(TaskEvent e) {
				/*
				 * Because with the invokation of the method the task is considered
				 * to be done, if the task fails, we must notify for a channel
				 * changes. This should be done to revert the old channel settings.
				 */
				if(scaod.doneWithErrors()) fireSamplerChannelChanged(event);
			}
		});
		CC.getTaskQueue().add(scaod);
		
		// We leave this event to be notified by the LinuxSampler notification system.
	}
	
	/**
	 * Sets the destination of the destination of the specified audio channel.
	 * @param audioSrc The numerical ID of the sampler channel's audio
	 * output channel, which should be rerouted.
	 * @param audioDst The audio channel of the selected audio output device
	 * where <code>audioSrc</code> should be routed to.
	 */
	public void
	setBackendAudioOutputChannel(int audioSrc, int audioDst) {
		final Task t;
		t = new Channel.SetAudioOutputChannel(getChannelId(), audioSrc, audioDst);
		final SamplerChannelEvent event = new SamplerChannelEvent(this);
		
		t.addTaskListener(new TaskListener() {
			public void
			taskPerformed(TaskEvent e) {
				/*
				 * Because with the invokation of the method the task is considered
				 * to be done, if the task fails, we must notify for a channel
				 * changes. This should be done to revert the old channel settings.
				 */
				if(t.doneWithErrors()) fireSamplerChannelChanged(event);
			}
		});
		CC.getTaskQueue().add(t);
	}
	
	/**
	 * Schedules a new task for assigning (on the backend side) the
	 * specified MIDI instrument map to this sampler channel. 
	 * @param mapId Specify the numerical ID of the MIDI instrument
	 * map that should be assigned to this sampler
	 * channel or <code>-1</code> to remove the current map binding.
	 */
	public void
	setBackendMidiInstrumentMap(int mapId) {
		final Task t = new Channel.SetMidiInstrumentMap(getChannelId(), mapId);
		final SamplerChannelEvent event = new SamplerChannelEvent(this);
		
		t.addTaskListener(new TaskListener() {
			public void
			taskPerformed(TaskEvent e) {
				/*
				 * Because with the invokation of the method the task is considered
				 * to be done, if the task fails, we must notify for a channel
				 * changes. This should be done to revert the old channel settings.
				 */
				if(t.doneWithErrors()) fireSamplerChannelChanged(event);
			}
		});
		CC.getTaskQueue().add(t);
	}
	
	/**
	 * Schedules a new task for loading and assigning the specified instrument
	 * to the sampler channel represented by this model.
	 * @param filename The file name of the instrument to be loaded.
	 * @param InstrIndex The index of the instrument in the instrument file to be loaded.
	 */
	public void
	loadBackendInstrument(String filename, int InstrIndex) {
		final Task li = new LoadInstrument(filename, InstrIndex, getChannelId());
		CC.getTaskQueue().add(li);
		
		// We leave this event to be notified by the LinuxSampler notification system.
	}
	
	/** Schedules a new task for reseting the channel. */
	public void
	resetBackendChannel() {
		CC.getTaskQueue().add(new org.jsampler.task.Channel.Reset(getChannelId()));
		
		// We leave this event to be notified by the LinuxSampler notification system.
	}
	
	/** Schedules a new task for duplicating the channel. */
	public void
	duplicateBackendChannel() {
		CC.getTaskQueue().add(new DuplicateChannels(getChannelInfo()));
	}
	
	/**
	 * Schedules a new task for adding a new effect send on the
	 * backend side. The effect send will be actually added to this model
	 * when the backend notifies for its creation.
	 * @param midiCtrl Defines the MIDI controller, which
	 * will be able alter the effect send level.
	 */
	public void
	addBackendFxSend(int midiCtrl) {
		CC.getTaskQueue().add(new Channel.AddFxSend(getChannelId(), midiCtrl));
		// We leave this event to be notified by the LinuxSampler notification system.
	}
	
	/**
	 * Schedules a new task for adding a new effect send on the
	 * backend side. The effect send will be actually added to this model
	 * when the backend notifies for its creation.
	 * @param midiCtrl Defines the MIDI controller, which
	 * will be able alter the effect send level.
	 * @param name The name of the effect send entity.
	 * The name does not have to be unique.
	 */
	public void
	addBackendFxSend(int midiCtrl, String name) {
		CC.getTaskQueue().add(new Channel.AddFxSend(getChannelId(), midiCtrl, name));
		// We leave this event to be notified by the LinuxSampler notification system.
	}
	
	/**
	 * Adds the specified effect send.
	 * @param fxSend The effect send to be added.
	 */
	public void
	addFxSend(FxSend fxSend) {
		fxSends.add(fxSend);
		fireFxSendAdded(fxSend);
	}
	
	/**
	 * Schedules a new task for removing the specified effect send on the backend side.
	 * @param fxSendId The ID of the effect send to remove.
	 */
	public void
	removeBackendFxSend(int fxSendId) {
		CC.getTaskQueue().add(new Channel.RemoveFxSend(getChannelId(), fxSendId));
	}
	
	/**
	 * Gets the effect send at the specified position.
	 * @param index The index of the effect send to be returned.
	 * @return The effect send at the specified position.
	 */
	public FxSend
	getFxSend(int index) { return fxSends.get(index); }
	
	/**
	 * Gets the effect send with the specified ID.
	 * @param fxSendId The ID of the effect send to return.
	 * @return The effect send with the specified ID or <code>null</code>
	 * if there is no effect send with ID <code>fxSendId</code>.
	 */
	public FxSend
	getFxSendById(int fxSendId) {
		for(FxSend fxs : fxSends) {
			if(fxs.getFxSendId() == fxSendId) return fxs;
		}
		
		return null;
	}
	
	/**
	 * Removes the effect send at the specified position.
	 * @param index The position of the effect send to remove.
	 * @return The removed effect send.
	 */
	public FxSend
	removeFxSend(int index) {
		FxSend fxs = fxSends.remove(index);
		fireFxSendRemoved(fxs);
		return fxs;
	}
	
	/**
	 * Removes the specified effect send.
	 * @param fxSendId The ID of the effect send to remove.
	 * @return <code>true</code> if the effect send is removed successfully, <code>false</code>
	 * if the channel does not contain effect send with ID <code>fxSendId</code>.
	 */
	public boolean
	removeFxSendById(int fxSendId) {
		for(int i = 0; i < fxSends.size(); i++) {
			FxSend fxs = fxSends.get(i);
			if(fxs.getFxSendId() == fxSendId) {
				fxSends.remove(i);
				fireFxSendRemoved(fxs);
				return true;
			}
		}
		
		return false;
	}
	
	/** Removes all effect sends from this channel. */
	public void
	removeAllFxSends() {
		for(int i = fxSends.size() - 1; i >= 0; i--) {
			FxSend fxs = fxSends.get(i);
			fxSends.removeElementAt(i);
			fireFxSendRemoved(fxs);
		}
	}
	
	/**
	 * Updates the specified effect send.
	 * @param fxSend The effect send to update.
	 */
	public void
	updateFxSend(FxSend fxSend) {
		for(int i = 0; i < fxSends.size(); i++) {
			FxSend fxs = fxSends.get(i);
			if(fxs.getFxSendId() == fxSend.getFxSendId()) {
				fxSends.setElementAt(fxSend, i);
				fireFxSendUpdated(fxSend);
				return;
			}
		}
	}
	
	/**
	 * Gets the current number of effect sends.
	 * @return The current number of effect sends.
	 */
	public int
	getFxSendCount() { return fxSends.size(); }
	
	/**
	 * Gets the current list of effect sends.
	 * @return The current list of effect sends.
	 */
	public FxSend[]
	getFxSends() { return fxSends.toArray(new FxSend[fxSends.size()]); }
	
	/**
	 * Sets the name of the specified effect send.
	 * @param fxSend The numerical ID of the effect send.
	 * @param name The new name of the effect send entity.
	 */
	public void
	setBackendFxSendName(final int fxSend, String name) {
		final Task t = new Channel.SetFxSendName(getChannelId(), fxSend, name);
		t.addTaskListener(new TaskListener() {
			public void
			taskPerformed(TaskEvent e) {
				/*
				 * Because with the invokation of the method the task is considered
				 * to be done, if the task fails, we must update the settings.
				 */
				if(t.doneWithErrors()) {
					int id = getChannelId();
					CC.getTaskQueue().add(new UpdateFxSendInfo(id, fxSend));
				}
			}
		});
		CC.getTaskQueue().add(t);
	}
	
	/**
	 * Sets the destination of an effect send's audio channel.
	 * @param fxSend The numerical ID of the effect send entity to be rerouted.
	 * @param audioSrc The numerical ID of the effect send's audio output channel,
	 * which should be rerouted.
	 * @param audioDst The audio channel of the selected audio output device
	 * where <code>audioSrc</code> should be routed to.
	 */
	public void
	setBackendFxSendAudioOutputChannel(int fxSend, int audioSrc, int audioDst) {
		Task t = new Channel.SetFxSendAudioOutputChannel (
			getChannelId(), fxSend, audioSrc, audioDst
		);
		
		CC.getTaskQueue().add(t);
	}
	
	/**
	 * Sets the MIDI controller of the specified effect send.
	 * @param fxSend The numerical ID of the effect send.
	 * @param midiCtrl The MIDI controller which shall be
	 * able to modify the effect send's send level.
	 */
	public void
	setBackendFxSendMidiController(int fxSend, int midiCtrl) {
		Task t = new Channel.SetFxSendMidiController(getChannelId(), fxSend, midiCtrl);
		CC.getTaskQueue().add(t);
	}
	
	/**
	 * Sets the volume of the specified effect send.
	 * @param fxSend The numerical ID of the effect
	 * send, which volume should be changed.
	 * @param level The new volume value.
	 */
	public void
	setBackendFxSendLevel(int fxSend, float level) {
		CC.getTaskQueue().add(new Channel.SetFxSendLevel(getChannelId(), fxSend, level));
	}
	
	/**
	 * Sends a MIDI data message to this sampler channel.
	 */
	public void
	sendBackendMidiData(MidiDataEvent e) {
		sendBackendMidiData(e.getType(), e.getNote(), e.getVelocity());
	}
	
	/**
	 * Sends a MIDI data message to this sampler channel.
	 * @param type The type of MIDI message to send.
	 * @param arg1 Depends on the message type.
	 * @param arg2 Depends on the message type.
	 */
	public void
	sendBackendMidiData(MidiDataEvent.Type type, int arg1, int arg2) {
		CC.getTaskQueue().add(new Channel.SendMidiMsg(getChannelId(), type, arg1, arg2));
	}
	
	/** Notifies listeners that the sampler channel settings has changed. */
	protected void
	fireSamplerChannelChanged() {
		final SamplerChannelEvent e = new SamplerChannelEvent(this);
		
		SwingUtilities.invokeLater(new Runnable() {
			public void
			run() { fireSamplerChannelChanged(e); }
		});
	}
	
	/** 
	 * Notifies listeners that the sampler channel settings has changed.
	 * This method should be invoked from the event-dispatching thread.
	 */
	protected void
	fireSamplerChannelChanged(SamplerChannelEvent e) {
		CC.getSamplerModel().setModified(true);
		for(SamplerChannelListener l : listeners) l.channelChanged(e);
	}
	
	/** Notifies listeners that the number of active disk streams has changed. */
	protected void
	fireStreamCountChanged() {
		final SamplerChannelEvent e = new SamplerChannelEvent(this);
		
		SwingUtilities.invokeLater(new Runnable() {
			public void
			run() { fireStreamCountChanged(e); }
		});
	}
	
	/** 
	 * Notifies listeners that the number of active disk streams has changed.
	 * This method should be invoked from the event-dispatching thread.
	 */
	protected void
	fireStreamCountChanged(SamplerChannelEvent e) {
		for(SamplerChannelListener l : listeners) l.streamCountChanged(e);
	}
	
	/** Notifies listeners that the number of active voices has changed. */
	protected void
	fireVoiceCountChanged() {
		final SamplerChannelEvent e = new SamplerChannelEvent(this);
		
		SwingUtilities.invokeLater(new Runnable() {
			public void
			run() { fireVoiceCountChanged(e); }
		});
	}
	
	/** 
	 * Notifies listeners that the number of active voices has changed.
	 * This method should be invoked from the event-dispatching thread.
	 */
	protected void
	fireVoiceCountChanged(SamplerChannelEvent e) {
		for(SamplerChannelListener l : listeners) l.voiceCountChanged(e);
	}
	
	/**
	 * Notifies listeners that the specified effect send has been added to the channel.
	 */
	protected void
	fireFxSendAdded(FxSend fxSend) {
		final EffectSendsEvent e = new EffectSendsEvent(this, fxSend);
		
		SwingUtilities.invokeLater(new Runnable() {
			public void
			run() { fireFxSendAdded(e); }
		});
	}
	
	/** 
	 * Notifies listeners that the specified effect send has been added to the channel.
	 * This method should be invoked from the event-dispatching thread.
	 */
	protected void
	fireFxSendAdded(EffectSendsEvent e) {
		CC.getSamplerModel().setModified(true);
		for(EffectSendsListener l : fxListeners) l.effectSendAdded(e);
	}
	
	/** Notifies listeners that the specified effect send has been removed. */
	protected void
	fireFxSendRemoved(FxSend fxSend) {
		final EffectSendsEvent e = new EffectSendsEvent(this, fxSend);
		
		SwingUtilities.invokeLater(new Runnable() {
			public void
			run() { fireFxSendRemoved(e); }
		});
	}
	
	/** 
	 * Notifies listeners that the specified effect send has been removed.
	 * This method should be invoked from the event-dispatching thread.
	 */
	protected void
	fireFxSendRemoved(EffectSendsEvent e) {
		CC.getSamplerModel().setModified(true);
		for(EffectSendsListener l : fxListeners) l.effectSendRemoved(e);
	}
	
	/** Notifies listeners that the specified effect send has been updated. */
	protected void
	fireFxSendUpdated(FxSend fxSend) {
		final EffectSendsEvent e = new EffectSendsEvent(this, fxSend);
		
		SwingUtilities.invokeLater(new Runnable() {
			public void
			run() { fireFxSendUpdated(e); }
		});
	}
	
	/** 
	 * Notifies listeners that the specified effect send has been updated.
	 * This method should be invoked from the event-dispatching thread.
	 */
	protected void
	fireFxSendUpdated(EffectSendsEvent e) {
		CC.getSamplerModel().setModified(true);
		for(EffectSendsListener l : fxListeners) l.effectSendChanged(e);
	}
	
	/** 
	 * Notifies listeners that the specified effect send has been updated.
	 * This method should be invoked from the event-dispatching thread.
	 */
	protected void
	fireMidiDataEvent(MidiDataEvent e) {
		for(MidiDataListener l : midiListeners) l.midiDataArrived(e);
	}
}
