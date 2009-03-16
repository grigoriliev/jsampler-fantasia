/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2009 Grigor Iliev <grigor@grigoriliev.com>
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

import org.jsampler.AudioDeviceModel;
import org.jsampler.CC;
import org.jsampler.SamplerModel;

import org.linuxsampler.lscp.AudioOutputDevice;
import org.linuxsampler.lscp.AudioOutputDriver;
import org.linuxsampler.lscp.Parameter;

import static org.jsampler.JSI18n.i18n;


/**
 * Provides the audio specific tasks.
 * @author Grigor Iliev
 */
public class Audio {
	/** Forbits the instantiation of this class. */
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
