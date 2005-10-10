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

import javax.swing.SwingUtilities;

import net.sf.juife.Task;
import net.sf.juife.event.TaskEvent;
import net.sf.juife.event.TaskListener;

import org.jsampler.event.SamplerChannelEvent;
import org.jsampler.event.SamplerChannelListener;

import org.jsampler.task.LoadEngine;
import org.jsampler.task.LoadInstrument;
import org.jsampler.task.SetChannelAudioOutputDevice;
import org.jsampler.task.SetChannelMidiInputChannel;
import org.jsampler.task.SetChannelMidiInputDevice;
import org.jsampler.task.SetChannelMidiInputPort;
import org.jsampler.task.SetChannelVolume;
import org.jsampler.task.SetMuteChannel;
import org.jsampler.task.SetSoloChannel;

import org.linuxsampler.lscp.SamplerChannel;


/**
 *
 * @author Grigor Iliev
 */
public class DefaultSamplerChannelModel implements SamplerChannelModel {
	private SamplerChannel channel;
	private int streamCount = 0;
	private int voiceCount = 0;
	
	private final Vector<SamplerChannelListener> listeners =
		new Vector<SamplerChannelListener>();
	
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
	 * Gets the sampler channel number.
	 * @return The sampler channel number or -1 if the sampler channel number is not set.
	 */
	public int
	getChannelID() { return channel == null ? -1 : channel.getChannelID(); }
	
	/**
	 * Gets the current settings of the sampler channel.
	 * @return <code>SamplerChannel</code> instance containing
	 * the current settings of the sampler channel.
	 */
	public SamplerChannel
	getChannelInfo() { return channel; }
	
	/**
	 * Sets the current settings of the sampler channel.
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
	 * @param count The new number of active voices.
	 */
	public void
	setVoiceCount(int count) {
		if(voiceCount == count) return;
		
		voiceCount = count;
		fireVoiceCountChanged();
	}
	
	/**
	 * Sets the sampler engine type to be used.
	 * @param engine The name of the engine type to be used.
	 */
	public void
	setEngineType(String engine) {
		final LoadEngine loadEngine = new LoadEngine(engine, getChannelID());
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
	 * Sets the mute mode of the channel. 
	 * @param mute Specifies the mute mode. If <code>true</code> the channel is muted, else
	 * the channel is unmuted.
	 */
	public void
	setMute(boolean mute) {
		final SetMuteChannel smc = new SetMuteChannel(getChannelID(), mute);
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
	 * Sets the solo mode of the channel. 
	 * @param solo Specifies the solo mode. If <code>true</code> the channel is soloed, else
	 * the channel is unsoloed.
	 */
	public void
	setSolo(boolean solo) {
		final SetSoloChannel ssc = new SetSoloChannel(getChannelID(), solo);
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
	 * Sets the channel volume. 
	 * @param volume Specifies the new volume value.
	 */
	public void
	setVolume(float volume) {
		final SetChannelVolume scv = new SetChannelVolume(getChannelID(), volume);
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
	 * Sets the MIDI input device of the channel represented by this model. 
	 * @param deviceID Specifies the numerical ID of the MIDI input device to be set.
	 */
	public void
	setMidiInputDevice(int deviceID) {
		final Task scmid = new SetChannelMidiInputDevice(getChannelID(), deviceID);
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
	 * Sets the MIDI input port of the channel represented by this model. 
	 * @param port Specifies the number of the MIDI input port.
	 */
	public void
	setMidiInputPort(int port) {
		final Task scmip = new SetChannelMidiInputPort(getChannelID(), port);
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
	 * Sets the MIDI channel that the channel represented by this model should listen to. 
	 * @param channel Specifies the MIDI channel that the channel
	 * represented by this model should listen to.
	 */
	public void
	setMidiInputChannel(int channel) {
		final Task scmic = new SetChannelMidiInputChannel(getChannelID(), channel);
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
	 * Sets the audio output device of the channel represented by this model. 
	 * @param deviceID Specifies the numerical ID of the audio output device to be set.
	 */
	public void
	setAudioOutputDevice(int deviceID) {
		final Task scaod = new SetChannelAudioOutputDevice(getChannelID(), deviceID);
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
	 * Loads and assigns the specified instrument
	 * to the sampler channel represented by this model.
	 * @param filename The file name of the instrument to be loaded.
	 * @param InstrIndex The index of the instrument in the instrument file to be loaded.
	 */
	public void
	loadInstrument(String filename, int InstrIndex) {
		final Task li = new LoadInstrument(filename, InstrIndex, getChannelID());
		CC.getTaskQueue().add(li);
		
		// We leave this event to be notified by the LinuxSampler notification system.
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
}
