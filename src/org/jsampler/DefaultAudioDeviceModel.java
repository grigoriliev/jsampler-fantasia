/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2011 Grigor Iliev <grigor@grigoriliev.com>
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

import java.util.ArrayList;
import java.util.Vector;

import javax.swing.SwingUtilities;

import org.jsampler.event.AudioDeviceEvent;
import org.jsampler.event.AudioDeviceListener;

import org.jsampler.task.Audio;

import org.linuxsampler.lscp.AudioOutputDevice;
import org.linuxsampler.lscp.Effect;
import org.linuxsampler.lscp.Parameter;


/**
 * This class provides default implementation of the <code>AudioDeviceModel</code> interface.
 * @author Grigor Iliev
 */
public class DefaultAudioDeviceModel implements AudioDeviceModel {
	private AudioOutputDevice audioDevice;
	
	private final ArrayList<AudioDeviceListener> listeners = new ArrayList<AudioDeviceListener>();
	private final ArrayList<EffectChain> effectChains = new ArrayList<EffectChain>();
	
	/**
	 * Creates a new instance of <code>DefaultAudioDeviceModel</code> using the
	 * specified non-null audio device.
	 * @param audioDevice An <code>AudioOutputDevice</code> instance providing the current
	 * settings of the audio device which will be represented by this model.
	 * @throws IllegalArgumentException If <code>audioDevice</code> is <code>null</code>.
	 */
	public
	DefaultAudioDeviceModel(AudioOutputDevice audioDevice) {
		if(audioDevice == null)
			throw new IllegalArgumentException("audioDevice must be non null");
		
		this.audioDevice = audioDevice;
	}
	
	/**
	 * Registers the specified listener to be notified when
	 * the settings of the audio device are changed.
	 * @param l The <code>AudioDeviceListener</code> to register.
	 */
	@Override
	public void
	addAudioDeviceListener(AudioDeviceListener l) { listeners.add(l); }
	
	/**
	 * Removes the specified listener.
	 * @param l The <code>AudioDeviceListener</code> to remove.
	 */
	@Override
	public void
	removeAudioDeviceListener(AudioDeviceListener l) { listeners.remove(l); }
	
	/**
	 * Gets the numerical ID of this audio device.
	 * @return The numerical ID of this audio device or
	 * -1 if the device number is not set.
	 */
	@Override
	public int
	getDeviceId() { return audioDevice.getDeviceId(); }
	
	/**
	 * Gets the current settings of the audio device represented by this model.
	 * @return <code>AudioOutputDevice</code> instance providing
	 * the current settings of the audio device represented by this model.
	 */
	@Override
	public AudioOutputDevice
	getDeviceInfo() { return audioDevice; }
	
	/**
	 * Updates the settings of the audio device represented by this model.
	 * @param device The new audio device settings.
	 */
	@Override
	public void
	setDeviceInfo(AudioOutputDevice device) {
		audioDevice = device;
		fireSettingsChanged();
	}
	
	/**
	 * Sets whether the audio device is enabled or disabled.
	 * @param active If <code>true</code> the audio device is enabled,
	 * else the device is disabled.
	 */
	@Override
	public void
	setActive(boolean active) {
		if(active == getDeviceInfo().isActive()) return;
		
		audioDevice.setActive(active);
		fireSettingsChanged();
	}
	
	/**
	 * Determines whether the audio device is active.
	 * @return <code>true</code> if the device is enabled and <code>false</code> otherwise.
	 */
	@Override
	public boolean
	isActive() { return audioDevice.isActive(); }
	
	/**
	 * Schedules a new task for enabling/disabling the audio device.
	 * @param active If <code>true</code> the audio device is enabled,
	 * else the device is disabled.
	 */
	@Override
	public void
	setBackendActive(boolean active) {
		CC.getTaskQueue().add(new Audio.EnableDevice(getDeviceId(), active));
	}
	
	/**
	 * Schedules a new task for altering
	 * a specific setting of the audio output device.
	 * @param prm The parameter to be set.
	 */
	@Override
	public void
	setBackendDeviceParameter(Parameter prm) {
		CC.getTaskQueue().add(new Audio.SetDeviceParameter(getDeviceId(), prm));
	}
	
	/**
	 * Schedules a new task for changing the channel number of the audio device.
	 * @param channels The new number of audio channels.
	 */
	@Override
	public void
	setBackendChannelCount(int channels) {
		CC.getTaskQueue().add(new Audio.SetChannelCount(getDeviceId(), channels));
	}
	
	/**
	 * Schedules a new task for altering a specific
	 * setting of the specified audio output channel.
	 * @param channel The channel number.
	 * @param prm The parameter to be set.
	 */
	@Override
	public void
	setBackendChannelParameter(int channel, Parameter prm) {
		CC.getTaskQueue().add(new Audio.SetChannelParameter(getDeviceId(), channel, prm));
	}
	
	/** Gets the current number of send effect chains. */
	@Override
	public int
	getSendEffectChainCount() { return effectChains.size(); }
	
	/** Gets the effect chain at the specified position. */
	@Override
	public EffectChain
	getSendEffectChain(int chainIdx) { return effectChains.get(chainIdx); }
	
	@Override
	public EffectChain
	getSendEffectChainById(int chainId) {
		for(int i = 0; i < getSendEffectChainCount(); i++) {
			EffectChain chain = getSendEffectChain(i);
			if(chain.getChainId() == chainId) return chain;
		}
		
		return null;
	}
	
	/**
	 * Gets the index of the send effect chain with ID <code>chainId</code>.
	 * @param chainId The ID of the send effect chain.
	 * @return The zero-based position of the specified send effect chain
	 * in the send effect chain list or <code>-1</code> 
	 * if there is no send effect chain with ID <code>chainId</code>.
	 */
	public int getSendEffectChainIndex(int chainId) {
		for(int i = 0; i < getSendEffectChainCount(); i++) {
			if(getSendEffectChain(i).getChainId() == chainId) return i;
		}
		
		return -1;
	}
	
	/**
	 * Adds the specified send effect chain to the specified audio output device.
	 */
	@Override
	public void
	addSendEffectChain(EffectChain chain) {
		effectChains.add(chain);
		fireSendEffectChainAdded(chain);
	}
	
	/**
	 * Removes the specified send effect chain from the audio output device.
	 */
	@Override
	public void
	removeSendEffectChain(int chainId) {
		for(int i = 0; i < effectChains.size(); i++) {
			if(effectChains.get(i).getChainId() == chainId) {
				fireSendEffectChainRemoved(effectChains.remove(i));
				return;
			}
		}
	}
	
	public void
	removeAllSendEffectChains() {
		for(int i = effectChains.size() - 1; i >= 0; i--) {
			fireSendEffectChainRemoved(effectChains.remove(i));
		}
	}
	
	/**
	 * Schedules a new task for adding a new send effect chain and
	 * assigning it to the specified audio output device.
	 */
	@Override
	public void
	addBackendSendEffectChain() {
		CC.getTaskQueue().add(new Audio.AddSendEffectChain(getDeviceId()));
	}
	
	/** Schedules a new task for removing the specified send effect chain. */
	@Override
	public void
	removeBackendSendEffectChain(int chainId) {
		CC.getTaskQueue().add(new Audio.RemoveSendEffectChain(getDeviceId(), chainId));
	}
	
	/**
	 * Schedules a new task for creating new effect instances and inserting them
	 * in the specified send effect chain at the specified position.
	 */
	@Override
	public void
	addBackendEffectInstances(Effect[] effects, int chainId, int index) {
		CC.getTaskQueue().add (
			new Audio.AddNewEffectInstances(effects, getDeviceId(), chainId, index)
		);
	}
	
	/**
	 * Schedules a new task for removing the specified
	 * effect instance from the specified send effect chain.
	 */
	@Override
	public void
	removeBackendEffectInstance(int chainId, int instanceId) {
		CC.getTaskQueue().add(new Audio.RemoveEffectInstance(getDeviceId(), chainId, instanceId));
	}
	
	/**
	 * Notifies listeners that the settings of the audio device are changed.
	 */
	private void
	fireSettingsChanged() {
		SwingUtilities.invokeLater(new Runnable() {
			public void
			run() {
				AudioDeviceModel model = DefaultAudioDeviceModel.this;
				fireSettingsChanged(new AudioDeviceEvent(model, model));
			}
		});
	}
	
	/**
	 * Notifies listeners that the settings of the audio device are changed.
	 * This method should be invoked from the event-dispatching thread.
	 */
	private void
	fireSettingsChanged(final AudioDeviceEvent e) {
		CC.getSamplerModel().setModified(true);
		for(AudioDeviceListener l : listeners) l.settingsChanged(e);
	}
	
	private void
	fireSendEffectChainAdded(final EffectChain chain) {
		SwingUtilities.invokeLater(new Runnable() {
			public void
			run() {
				AudioDeviceModel m = DefaultAudioDeviceModel.this;
				fireSendEffectChainAdded(new AudioDeviceEvent(m, m, chain));
			}
		});
	}
	
	/** This method should be invoked from the event-dispatching thread. */
	private void
	fireSendEffectChainAdded(final AudioDeviceEvent e) {
		CC.getSamplerModel().setModified(true);
		for(AudioDeviceListener l : listeners) l.sendEffectChainAdded(e);
	}
	
	private void
	fireSendEffectChainRemoved(final EffectChain chain) {
		SwingUtilities.invokeLater(new Runnable() {
			public void
			run() {
				AudioDeviceModel m = DefaultAudioDeviceModel.this;
				fireSendEffectChainRemoved(new AudioDeviceEvent(m, m, chain));
			}
		});
	}
	
	/** This method should be invoked from the event-dispatching thread. */
	private void
	fireSendEffectChainRemoved(final AudioDeviceEvent e) {
		CC.getSamplerModel().setModified(true);
		for(AudioDeviceListener l : listeners) l.sendEffectChainRemoved(e);
	}
}
