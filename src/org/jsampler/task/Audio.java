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

package org.jsampler.task;

import java.util.ArrayList;

import org.jsampler.AudioDeviceModel;
import org.jsampler.CC;
import org.jsampler.EffectChain;
import org.jsampler.SamplerModel;

import org.linuxsampler.lscp.AudioOutputDevice;
import org.linuxsampler.lscp.AudioOutputDriver;
import org.linuxsampler.lscp.EffectChainInfo;
import org.linuxsampler.lscp.EffectInstanceInfo;
import org.linuxsampler.lscp.Effect;
import org.linuxsampler.lscp.Parameter;

import static org.jsampler.JSI18n.i18n;


/**
 * Provides the audio specific tasks.
 * @author Grigor Iliev
 */
public class Audio {
	/** Forbids the instantiation of this class. */
	private Audio() { }

	/**
	 * This task retrieves all audio output drivers currently
	 * available for the LinuxSampler instance.
	 */
	public static class GetDrivers extends EnhancedTask<AudioOutputDriver[]> {
		/** Creates a new instance of <code>GetDrivers</code>. */
		public
		GetDrivers() {
			setTitle("Audio.GetDrivers_task");
			setDescription(i18n.getMessage("Audio.GetDrivers.desc"));
		}
	
		/** The entry point of the task. */
		@Override
		public void
		exec() throws Exception { setResult(CC.getClient().getAudioOutputDrivers()); }
	}
	
	/**
	 * This task retrieves detailed information about all parameters
	 * of the specified audio output driver.
	 */
	public static class GetDriverParametersInfo extends EnhancedTask<Parameter[]> {
		private String driver;
		Parameter[] depList;
		
		/**
		 * Creates a new instance of <code>GetDriverParametersInfo</code>.
		 * @param depList - A dependences list.
		 */
		public
		GetDriverParametersInfo(String driver, Parameter... depList) {
			setTitle("Audio.GetDriverParametersInfo_task");
			setDescription(i18n.getMessage("Audio.GetDriverParametersInfo.desc"));
			
			this.driver = driver;
			this.depList = depList;
		}
	
		/** The entry point of the task. */
		@Override
		public void
		exec() throws Exception {
			AudioOutputDriver d;
			d = CC.getClient().getAudioOutputDriverInfo(driver, depList);
			setResult(d.getParameters());
		}
	}

	/**
	 * This task creates a new audio output device.
	 */
	public static class CreateDevice extends EnhancedTask<Integer> {
		private String driver;
		private Parameter[] parameters;
	
	
		/**
		 * Creates a new instance of <code>CreateDevice</code>.
		 * @param driver The desired audio output system.
		 * @param parameters An optional list of driver specific parameters.
		 */
		public
		CreateDevice(String driver, Parameter... parameters) {
			setTitle("Audio.CreateDevice_task");
			setDescription(i18n.getMessage("Audio.CreateDevice.desc"));
		
			this.driver = driver;
			this.parameters = parameters;
		}
	
		/** The entry point of the task. */
		@Override
		public void
		exec() throws Exception {
			Integer deviceId = CC.getClient().createAudioOutputDevice(driver, parameters);
			setResult(deviceId);
		}
	}
	
	/**
	 * This task destroys the specified audio output device.
	 */
	public static class DestroyDevice extends EnhancedTask {
		private int deviceId;
	
	
		/**
		 * Creates a new instance of <code>DestroyDevice</code>.
		 * @param deviceId The ID of the audio output device to be destroyed.
		 */
		public
		DestroyDevice(int deviceId) {
			setTitle("Audio.DestroyDevice_task");
			setDescription(i18n.getMessage("Audio.DestroyDevice.desc", deviceId));
		
			this.deviceId = deviceId;
		}
	
		/** The entry point of the task. */
		@Override
		public void
		exec() throws Exception { CC.getClient().destroyAudioOutputDevice(deviceId); }
	}
	
	/**
	 * This task enables/disables a specific audio output device.
	 */
	public static class EnableDevice extends EnhancedTask {
		private int dev;
		private boolean enable;
	
		/**
		 * Creates new instance of <code>EnableDevice</code>.
		 * @param dev The id of the device to be enabled/disabled.
		 * @param enable Specify <code>true</code> to enable the audio device;
		 * code>false</code> to disable it.
		 */
		public
		EnableDevice(int dev, boolean enable) {
			setTitle("Audio.EnableDevice_task");
			setDescription(i18n.getMessage("Audio.EnableDevice.desc", dev));
		
			this.dev = dev;
			this.enable = enable;
		}
	
		/** The entry point of the task. */
		@Override
		public void
		exec() throws Exception {
			CC.getClient().enableAudioOutputDevice(dev, enable);
			
			// Not needed, but eventually speeds up the change.
			CC.getSamplerModel().getAudioDeviceById(dev).setActive(enable);
		}
	}

	/**
	 * This task alters a specific setting of an audio output device.
	 */
	public static class SetDeviceParameter extends EnhancedTask {
		private int dev;
		private Parameter prm;
	
		/**
		 * Creates new instance of <code>SetDeviceParameter</code>.
		 * @param dev The id of the device whose parameter should be set.
		 * @param prm The parameter to be set.
		 */
		public
		SetDeviceParameter(int dev, Parameter prm) {
			setTitle("Audio.SetDeviceParameter_task");
			setDescription(i18n.getMessage("Audio.SetDeviceParameter.desc", dev));
		
			this.dev = dev;
			this.prm = prm;
		}
	
		/** The entry point of the task. */
		@Override
		public void
		exec() throws Exception {
			CC.getClient().setAudioOutputDeviceParameter(dev, prm);
		}
	}

	/**
	 * This task alters a specific setting of an audio output channel.
	 */
	public static class SetChannelParameter extends EnhancedTask {
		private int dev;
		private int channel;
		private Parameter prm;
	
		/**
		 * Creates new instance of <code>SetChannelParameter</code>.
		 * @param dev The id of the device whose channel parameter should be set.
		 * @param channel The channel number.
		 * @param prm The parameter to be set.
		 */
		public
		SetChannelParameter(int dev, int channel, Parameter prm) {
			setTitle("Audio.SetChannelParameter_task");
			setDescription(i18n.getMessage("Audio.SetChannelParameter.desc"));
		
			this.dev = dev;
			this.channel = channel;
			this.prm = prm;
		}
	
		/** The entry point of the task. */
		@Override
		public void
		exec() throws Exception {
			CC.getClient().setAudioOutputChannelParameter(dev, channel, prm);
		}
	}
	
	/**
	 * This task changes the channel number of the speicifed audio output device.
	 */
	public static class SetChannelCount extends EnhancedTask {
		private int deviceId;
		private int channels;
	
		/**
		 * Creates new instance of <code>SetChannelCount</code>.
		 * @param deviceId The id of the device whose channels number will be changed.
		 * @param channels The new number of audio channels.
		 */
		public
		SetChannelCount(int deviceId, int channels) {
			setTitle("SetAudioOutputChannelCount_task");
			setDescription(i18n.getMessage("Audio.SetChannelCount.desc", deviceId));
		
			this.deviceId = deviceId;
			this.channels = channels;
		}
	
		/** The entry point of the task. */
		@Override
		public void
		exec() throws Exception {
			CC.getClient().setAudioOutputChannelCount(deviceId, channels);
		}
	}

	/**
	 * This task adds a send effect chain to the specified audio output device.
	 */
	public static class AddSendEffectChain extends EnhancedTask<Integer> {
		private int audioDeviceId;
	
		/**
		 * Creates a new instance of <code>AddSendEffectChain</code>.
		 * @param audioDeviceId The numerical ID of the audio output device.
		 */
		public
		AddSendEffectChain(int audioDeviceId) {
			setTitle("Audio.AddSendEffectChain_task");
			setDescription(i18n.getMessage("Audio.AddSendEffectChain.desc"));
		
			this.audioDeviceId = audioDeviceId;
		}
	
		/** The entry point of the task. */
		@Override
		public void
		exec() throws Exception {
			Integer chainId = CC.getClient().addSendEffectChain(audioDeviceId);
			setResult(chainId);
		}
	}

	/**
	 * This task removes the specified send effect chain of the specified audio output device.
	 */
	public static class RemoveSendEffectChain extends EnhancedTask {
		private int audioDeviceId;
		private int chainId;
	
		/**
		 * Creates a new instance of <code>RemoveSendEffectChain</code>.
		 * @param audioDeviceId The numerical ID of the audio output device.
		 * @param chainId The numerical ID of the send effect chain to remove.
		 */
		public
		RemoveSendEffectChain(int audioDeviceId, int chainId) {
			setTitle("Audio.RemoveSendEffectChain_task");
			setDescription(i18n.getMessage("Audio.RemoveSendEffectChain.desc"));
		
			this.audioDeviceId = audioDeviceId;
			this.chainId = chainId;
		}
	
		/** The entry point of the task. */
		@Override
		public void
		exec() throws Exception {
			AudioDeviceModel adm = CC.getSamplerModel().getAudioDevice(audioDeviceId);
			EffectChain chain = adm.getSendEffectChainById(chainId);
			
			for(int i = chain.getEffectInstanceCount() - 1; i >= 0; i--) {
				CC.getClient().removeEffectInstanceFromChain (
					audioDeviceId, chainId, i
				);
				
				int iid = chain.getEffectInstance(i).getInstanceId();
				CC.getClient().destroyEffectInstance(iid);
			}
			CC.getClient().removeSendEffectChain(audioDeviceId, chainId);
		}
	}

	/**
	 * This task creates new effect instances and inserts them
	 * in the specified send effect chain at the specified position.
	 */
	public static class AddNewEffectInstances extends EnhancedTask {
		private Effect[] effects;
		private int audioDeviceId;
		private int chainId;
		private int index;
	
		/**
		 * Creates a new instance of <code>AddNewEffectInstances</code>.
		 * @param audioDeviceId The numerical ID of the audio output device.
		 * @param chainId The numerical ID of the send effect chain.
		 * @param index The position in the chain where the newly created
		 * effect instances should be inserted to. Use -1 to append.
		 */
		public
		AddNewEffectInstances(Effect[] effects, int audioDeviceId, int chainId, int index) {
			setTitle("Audio.AddNewEffectInstances_task");
			setDescription(i18n.getMessage("Audio.AddNewEffectInstances.desc"));
		
			this.effects = effects;
			this.audioDeviceId = audioDeviceId;
			this.chainId = chainId;
			this.index = index;
		}
	
		/** The entry point of the task. */
		@Override
		public void
		exec() throws Exception {
			for(Effect e : effects) {
				int ei = CC.getClient().createEffectInstance(e);
				if(index != -1) {
					CC.getClient().insertEffectInstance(audioDeviceId, chainId, index, ei);
				} else {
					CC.getClient().appendEffectInstance(audioDeviceId, chainId, ei);
				}
			}
		}
	}

	/**
	 * This task removes the specified effect instance from the specified send effect chain.
	 */
	public static class RemoveEffectInstance extends EnhancedTask {
		private int audioDeviceId;
		private int chainId;
		private int instanceId;
	
		/**
		 * Creates a new instance of <code>RemoveEffectInstance</code>.
		 * @param audioDeviceId The numerical ID of the audio output device.
		 * @param chainId The numerical ID of the send effect chain.
		 * @param instanceId The numerical ID of the effect instance to remove.
		 */
		public
		RemoveEffectInstance(int audioDeviceId, int chainId, int instanceId) {
			setTitle("Audio.RemoveEffectInstance_task");
			setDescription(i18n.getMessage("Audio.RemoveEffectInstance.desc"));
		
			this.audioDeviceId = audioDeviceId;
			this.chainId = chainId;
			this.instanceId = instanceId;
		}
	
		/** The entry point of the task. */
		@Override
		public void
		exec() throws Exception {
			AudioDeviceModel adm = CC.getSamplerModel().getAudioDevice(audioDeviceId);
			EffectChain chain = adm.getSendEffectChainById(chainId);
			
			CC.getClient().removeEffectInstanceFromChain (
				audioDeviceId, chainId, chain.getIndex(instanceId)
			);
				
			CC.getClient().destroyEffectInstance(instanceId);
			
		}
	}


	/**
	 * This task updates the send effect chain list of an audio output device.
	 */
	public static class UpdateSendEffectChains extends EnhancedTask {
		private int devId;
		
		/**
		 * Creates new instance of <code>UpdateSendEffectChains</code>.
		 * @param devId The id of the device.
		 */
		public
		UpdateSendEffectChains(int devId) {
			setTitle("Audio.UpdateSendEffectChains_task");
			setDescription(i18n.getMessage("Audio.UpdateSendEffectChains.desc", devId));
		
			this.devId = devId;
		}
	
		/** The entry point of the task. */
		@Override
		public void
		exec() throws Exception {
			AudioDeviceModel m = CC.getSamplerModel().getAudioDeviceById(devId);
			
			Integer[] idS = CC.getClient().getSendEffectChainIDs(devId);
			
			ArrayList<Integer> removedChains = new ArrayList<Integer>();
			
			for(int i = 0; i < m.getSendEffectChainCount(); i++) {
				boolean found = false;
				for(int j = 0; j < idS.length; j++) {
					if(idS[j] != null && m.getSendEffectChain(i).getChainId() == idS[j]) {
						found = true;
						idS[j] = null;
					}
				}
				if(!found) removedChains.add(m.getSendEffectChain(i).getChainId());
			}
			
			for(int i : removedChains) m.removeSendEffectChain(i);
			
			for(int i = 0; i < idS.length; i++) {
				if(idS[i] != null)  {
					m.addSendEffectChain (
					new EffectChain(CC.getClient().getSendEffectChainInfo(devId, idS[i]))
					);
				}
			}
		}
	}

	/**
	 * This task updates the list of effect instances.
	 */
	public static class UpdateEffectInstances extends EnhancedTask {
		private int audioDeviceId;
		private int chainId;
	
		/**
		 * Creates a new instance of <code>UpdateEffectInstances</code>.
		 * @param audioDeviceId The numerical ID of the audio output device.
		 * @param chainId The numerical ID of the send effect chain.
		 */
		public
		UpdateEffectInstances(int audioDeviceId, int chainId) {
			setTitle("Audio.UpdateEffectInstances_task");
			setDescription(i18n.getMessage("Audio.UpdateEffectInstances.desc"));
		
			this.audioDeviceId = audioDeviceId;
			this.chainId = chainId;
		}
	
		/** The entry point of the task. */
		@Override
		public void
		exec() throws Exception {
			setSilent(true);
			
			EffectChainInfo c =
				CC.getClient().getSendEffectChainInfo(audioDeviceId, chainId);
			
			AudioDeviceModel m = CC.getSamplerModel().getAudioDeviceById(audioDeviceId);
			m.getSendEffectChainById(chainId).setEffectInstances(c);
		}
	}


	/**
	 * This task updates the setting of an effect instance.
	 */
	public static class UpdateEffectInstanceInfo extends EnhancedTask {
		private int instanceId;
		
		/**
		 * Creates new instance of <code>UpdateEffectInstanceInfo</code>.
		 * @param instanceId The id of the effect instance, which settings should be updated.
		 */
		public
		UpdateEffectInstanceInfo(int instanceId) {
			setTitle("Audio.UpdateEffectInstanceInfo_task");
			setDescription(i18n.getMessage("Audio.UpdateEffectInstanceInfo.desc", instanceId));
		
			this.instanceId = instanceId;
		}
	
		/** The entry point of the task. */
		@Override
		public void
		exec() throws Exception {
			EffectInstanceInfo ei = CC.getClient().getEffectInstanceInfo(instanceId);
			CC.getSamplerModel().updateEffectInstance(ei);
		}
	}


	/**
	 * This task changes the value of an effect instance parameter.
	 */
	public static class SetEffectInstanceParameter extends EnhancedTask {
		private int instanceId;
		private int prmIndex;
		private float newValue;
		
		/**
		 * Creates new instance of <code>SetEffectInstanceParameter</code>.
		 */
		public
		SetEffectInstanceParameter(int instanceId, int prmIndex, float newValue) {
			setTitle("Audio.SetEffectInstanceParameter_task");
			setDescription(i18n.getMessage("Audio.SetEffectInstanceParameter.desc"));
		
			this.instanceId = instanceId;
			this.prmIndex = prmIndex;
			this.newValue = newValue;
		}
	
		/** The entry point of the task. */
		@Override
		public void
		exec() throws Exception {
			CC.getClient().setEffectInstanceParameter(instanceId, prmIndex, newValue);
		}
	}


	/**
	 * This task updates the setting of an audio output device.
	 */
	public static class UpdateDeviceInfo extends EnhancedTask {
		private int dev;
		
		/**
		 * Creates new instance of <code>UpdateDeviceInfo</code>.
		 * @param dev The id of the device, which settings should be updated.
		 */
		public
		UpdateDeviceInfo(int dev) {
			setTitle("Audio.UpdateDeviceInfo_task");
			setDescription(i18n.getMessage("Audio.UpdateDeviceInfo.desc", dev));
		
			this.dev = dev;
		}
	
		/** The entry point of the task. */
		@Override
		public void
		exec() throws Exception {
			AudioOutputDevice d = CC.getClient().getAudioOutputDeviceInfo(dev);
			CC.getSamplerModel().getAudioDeviceById(dev).setDeviceInfo(d);
		}
	}

	/**
	 * This task updates the audio output device list.
	 */
	public static class UpdateDevices extends EnhancedTask {
		/** Creates a new instance of <code>UpdateDevices</code>. */
		public
		UpdateDevices() {
			setTitle("Audio.UpdateDevices_task");
			setDescription(i18n.getMessage("Audio.UpdateDevices.desc"));
		}
	
		/** The entry point of the task. */
		@Override
		public void
		exec() throws Exception {
			SamplerModel sm = CC.getSamplerModel();
			Integer[] devIDs = CC.getClient().getAudioOutputDeviceIDs();
		
			boolean found = false;
			
			for(AudioDeviceModel m : sm.getAudioDevices()) {
				for(int i = 0; i < devIDs.length; i++) {
					if(m.getDeviceId() == devIDs[i]) {
						devIDs[i] = -1;
						found = true;
					}
				}
			
				if(!found) sm.removeAudioDeviceById(m.getDeviceId());
				found = false;
			}
		
			AudioOutputDevice d;
			
			for(int id : devIDs) {
				if(id >= 0) {
					d = CC.getClient().getAudioOutputDeviceInfo(id);
					sm.addAudioDevice(d);
				}
			}
		}
	}

}
