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

import org.jsampler.AudioDeviceModel;
import org.jsampler.CC;
import org.jsampler.EffectChain;
import org.jsampler.SamplerModel;

import org.linuxsampler.lscp.Effect;
import org.linuxsampler.lscp.Instrument;
import org.linuxsampler.lscp.SamplerEngine;
import org.linuxsampler.lscp.ServerInfo;


import static org.jsampler.JSI18n.i18n;


/**
 * Provides tasks for managing the global settings of the sampler.
 * @author Grigor Iliev
 */
public class Global {
	
	/** Forbids the instantiation of this class. */
	private Global() { }

	/**
	 * Establishes connection to LinuxSampler.
	 */
	public static class Connect extends EnhancedTask {
		/** Creates a new instance of <code>Connect</code>. */
		public
		Connect() {
			setTitle("Global.Connect_task");
			setDescription(i18n.getMessage("Global.Connect.desc"));
		}

		/** The entry point of the task. */
		@Override
		public void
		exec() throws Exception { CC.getClient().connect(); }
	}

	public static class Disconnect extends EnhancedTask {
		/** Creates a new instance of <code>Disconnect</code>. */
		public
		Disconnect() {
			setSilent(true);
			setTitle("Global.Disconnect_task");
			setDescription("Disconnecting...");
		}

		/** The entry point of the task. */
		@Override
		public void
		exec() throws Exception {
			CC.getClient().disconnect();
			if(CC.getMainFrame().getLSConsoleModel() != null) {
				CC.getMainFrame().getLSConsoleModel().quit();
			}
		}
	}
	
	/**
	 * This task retrieves information about the LinuxSampler instance.
	 */
	public static class GetServerInfo extends EnhancedTask<ServerInfo> {
		/** Creates a new instance of <code>GetServerInfo</code>. */
		public
		GetServerInfo() {
			setTitle("Global.GetServerInfo_task");
			setDescription(i18n.getMessage("Global.GetServerInfo.desc"));
		}
		
		/** The entry point of the task. */
		@Override
		public void
		exec() throws Exception { setResult(CC.getClient().getServerInfo()); }
	}
	
	/**
	 * This task resets the whole sampler.
	 */
	public static class ResetSampler extends EnhancedTask {
		/** Creates a new instance of <code>ResetSampler</code>. */
		public
		ResetSampler() {
			setTitle("Global.ResetSampler_task");
			setDescription(i18n.getMessage("Global.ResetSampler.desc"));
		}
		
		/** The entry point of the task. */
		@Override
		public void
		exec() throws Exception { CC.getClient().resetSampler(); }
	}

	/**
	 * This task retrieves the list of all available engines.
	 */
	public static class GetEngines extends EnhancedTask<SamplerEngine[]> {
		/** Creates a new instance of <code>GetEngines</code>. */
		public
		GetEngines() {
			setTitle("Global.GetEngines_task");
			setDescription(i18n.getMessage("Global.GetEngines.desc"));
		}

		/** The entry point of the task. */
		@Override
		public void
		exec() throws Exception { setResult(CC.getClient().getEngines()); }
	}
	
	/**
	 * This task gets the global volume of the sampler.
	 */
	public static class GetVolume extends EnhancedTask<Float> {
		/** Creates a new instance of <code>GetVolume</code>. */
		public
		GetVolume() {
			setTitle("Global.GetVolume_task");
			setDescription(i18n.getMessage("Global.GetVolume.desc"));
		}
	
		/** The entry point of the task. */
		@Override
		public void
		exec() throws Exception { setResult(CC.getClient().getVolume()); }
	}

	
	/**
	 * This task sets the global volume of the sampler.
	 */
	public static class SetVolume extends EnhancedTask {
		private float volume;
	
		/**
		 * Creates new instance of <code>SetVolume</code>.
		 * @param volume The new volume value.
		 */
		public
		SetVolume(float volume) {
			setTitle("Global.SetVolume_task");
			setDescription(i18n.getMessage("Global.SetVolume.desc"));
			this.volume = volume;
		}
	
		/** The entry point of the task. */
		@Override
		public void
		exec() throws Exception { CC.getClient().setVolume(volume); }
	}

	
	/**
	 * This task sets the global sampler-wide limits of maximum voices and streams.
	 */
	public static class SetPolyphony extends EnhancedTask {
		private int maxVoices;
		private int maxStreams;
		
		/**
		 * Creates new instance of <code>SetPolyphony</code>.
		 * @param maxVoices The new global limit of maximum voices or
		 * <code>-1</code> to ignore it.
		 * @param maxStreams The new global limit of maximum disk streams or
		 * <code>-1</code> to ignore it.
		 */
		public
		SetPolyphony(int maxVoices, int maxStreams) {
			setTitle("Global.SetPolyphony_task");
			setDescription(i18n.getMessage("Global.SetPolyphony.desc"));
			this.maxVoices = maxVoices;
			this.maxStreams = maxStreams;
		}
	
		/** The entry point of the task. */
		@Override
		public void
		exec() throws Exception {
			if(maxVoices != -1) CC.getClient().setGlobalVoiceLimit(maxVoices);
			if(maxStreams != -1) CC.getClient().setGlobalStreamLimit(maxStreams);
		}
	}

	/**
	 * This task updates the current number of all active voices
	 * and the maximum number of active voices allowed.
	 */
	public static class UpdateTotalVoiceCount extends EnhancedTask {
		/** Creates a new instance of <code>UpdateTotalVoiceCount</code>. */
		public
		UpdateTotalVoiceCount() {
			setSilent(true);
			setTitle("Global.UpdateTotalVoiceCount_task");
			setDescription(i18n.getMessage("Global.UpdateTotalVoiceCount.desc"));
		}

		/** The entry point of the task. */
		@Override
		public void
		exec() throws Exception {
			SamplerModel sm = CC.getSamplerModel();
			int voices = CC.getClient().getTotalVoiceCount();
			int voicesMax = CC.getClient().getTotalVoiceCountMax();
			sm.updateActiveVoiceInfo(voices, voicesMax);
		}

		/**
		 * Used to decrease the traffic. All task in the queue
		 * equal to this are removed if added using {@link org.jsampler.CC#scheduleTask}.
		 * @see org.jsampler.CC#addTask
		 */
		@Override
		public boolean
		equals(Object obj) {
			if(obj == null) return false;
			if(!(obj instanceof UpdateTotalVoiceCount)) return false;

			return true;
		}
	}

	
	/**
	 * This task sets the LSCP client's read timeout.
	 */
	public static class SetClientReadTimeout extends EnhancedTask {
		private int timeout;
	
		/**
		 * Creates new instance of <code>SetClientReadTimeout</code>.
		 * @param timeout The new timeout value (in seconds).
		 */
		public
		SetClientReadTimeout(int timeout) {
			setTitle("Global.SetClientReadTimeout_task");
			setDescription(i18n.getMessage("Global.SetClientReadTimeout.desc"));
			this.timeout = timeout;
		}
	
		/** The entry point of the task. */
		@Override
		public void
		exec() throws Exception { CC.getClient().setSoTimeout(timeout * 1000); }
	}
	
	/**
	 * This task gets the list of instruments in the specified instrument file.
	 */
	public static class GetFileInstruments extends EnhancedTask<Instrument[]> {
		private final String filename;
		
		/** Creates a new instance of <code>GetFileInstruments</code>. */
		public
		GetFileInstruments(String filename) {
			setSilent(true);
			this.filename = filename;
			setTitle("Global.GetFileInstruments_task");
			setDescription(i18n.getMessage("Global.GetFileInstruments.desc"));
		}
	
		/** The entry point of the task. */
		@Override
		public void
		exec() throws Exception {
			setResult(CC.getClient().getFileInstruments(filename));
		}
	}
	
	/**
	 * This task gets information about the specified instrument.
	 */
	public static class GetFileInstrument extends EnhancedTask<Instrument> {
		private final String filename;
		private final int instrIdx;
		
		/** Creates a new instance of <code>GetFileInstrument</code>. */
		public
		GetFileInstrument(String filename, int instrIdx) {
			setSilent(true);
			this.filename = filename;
			this.instrIdx = instrIdx;
			setTitle("Global.GetFileInstrument_task");
			setDescription(i18n.getMessage("Global.GetFileInstrument.desc"));
		}
	
		/** The entry point of the task. */
		@Override
		public void
		exec() throws Exception {
			setResult(CC.getClient().getFileInstrumentInfo(filename, instrIdx));
		}
	}

	/**
	 * This task retrieves the list of internal effects, available to the sampler.
	 */
	public static class GetEffects extends EnhancedTask<Effect[]> {
		/** Creates a new instance of <code>GetEffects</code>. */
		public
		GetEffects() {
			setTitle("Global.GetEffects_task");
			setDescription(i18n.getMessage("Global.GetEffects.desc"));
		}
	
		/** The entry point of the task. */
		@Override
		public void
		exec() throws Exception { setResult(CC.getClient().getEffects()); }
	}

	/**
	 * This task updates the send effect chains and the effect instances in those chains.
	 */
	public static class UpdateSendEffectChains extends EnhancedTask {
		private final int audioDeviceId;
		
		/** Creates a new instance of <code>UpdateSendEffectChains</code>. */
		public
		UpdateSendEffectChains() { this(-1); }
		
		/** Creates a new instance of <code>UpdateSendEffectChains</code>. */
		public
		UpdateSendEffectChains(int audioDeviceId) {
			this.audioDeviceId = audioDeviceId;
			setTitle("Global.UpdateSendEffectChains_task");
			setDescription(i18n.getMessage("Global.UpdateSendEffectChains.desc"));
		}
	
		/** The entry point of the task. */
		@Override
		public void
		exec() throws Exception {
			// TODO: synchornization
			
			if(audioDeviceId < 0) {
				Integer[] aodIDs = CC.getClient().getAudioOutputDeviceIDs();
				for(int id : aodIDs) { updateSendEffectChains(id); }
			} else {
				updateSendEffectChains(audioDeviceId);
			}
		}
		
		private void
		updateSendEffectChains(int devId) throws Exception {
			org.linuxsampler.lscp.EffectChain[] chains =
				CC.getClient().getSendEffectChains(devId);
			
			AudioDeviceModel adm = CC.getSamplerModel().getAudioDeviceById(devId);
			adm.removeAllSendEffectChains();
			
			for(org.linuxsampler.lscp.EffectChain c : chains) {
				adm.addSendEffectChain(new EffectChain(c));
			}
		}
	}
	
	public static class DummyTask extends EnhancedTask {
		@Override
		public void
		run() { }
	}
}
