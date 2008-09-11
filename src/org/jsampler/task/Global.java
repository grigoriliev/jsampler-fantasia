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

package org.jsampler.task;

import java.util.logging.Level;

import org.jsampler.CC;
import org.jsampler.HF;

import org.linuxsampler.lscp.ServerInfo;
import org.linuxsampler.lscp.Instrument;

import static org.jsampler.JSI18n.i18n;


/**
 * Provides tasks for managing the global settings of the sampler.
 * @author Grigor Iliev
 */
public class Global {
	
	/** Forbits the instantiation of this class. */
	private Global() { }
	
	/**
	 * This task retrieves information about the LinuxSampler instance.
	 * @author Grigor Iliev
	 */
	public static class GetServerInfo extends EnhancedTask<ServerInfo> {
		/** Creates a new instance of <code>GetServerInfo</code>. */
		public
		GetServerInfo() {
			setTitle("Global.GetServerInfo_task");
			setDescription(i18n.getMessage("Global.GetServerInfo.desc"));
		}
		
		/** The entry point of the task. */
		public void
		run() {
			try { setResult(CC.getClient().getServerInfo()); }
			catch(Exception x) {
				setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
				CC.getLogger().log(Level.FINE, getErrorMessage(), x);
			}
		}
	}
	
	/**
	 * This task resets the whole sampler.
	 * @author Grigor Iliev
	 */
	public static class ResetSampler extends EnhancedTask {
		/** Creates a new instance of <code>ResetSampler</code>. */
		public
		ResetSampler() {
			setTitle("Global.ResetSampler_task");
			setDescription(i18n.getMessage("Global.ResetSampler.desc"));
		}
		
		/** The entry point of the task. */
		public void
		run() {
			try { CC.getClient().resetSampler(); }
			catch(Exception x) {
				setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
				CC.getLogger().log(Level.FINE, getErrorMessage(), x);
			}
		}
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
		public void
		run() {
			try { setResult(CC.getClient().getVolume()); }
			catch(Exception x) {
				setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
				CC.getLogger().log(Level.FINE, getErrorMessage(), x);
			}
		}
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
		public void
		run() {
			try {
				CC.getClient().setVolume(volume);
			} catch(Exception x) {
				setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
				CC.getLogger().log(Level.FINE, getErrorMessage(), x);
			}
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
		public void
		run() {
			try {
				CC.getClient().setSoTimeout(timeout * 1000);
			} catch(Exception x) {
				setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
				CC.getLogger().log(Level.FINE, getErrorMessage(), x);
			}
		}
	}
	
	/**
	 * This task gets the list of instruments in the specified instrument file.
	 */
	public static class GetFileInstruments extends EnhancedTask<Instrument[]> {
		private final String filename;
		
		/** Creates a new instance of <code>GetFileInstruments</code>. */
		public
		GetFileInstruments(String filename) {
			this.filename = filename;
			setTitle("Global.GetFileInstruments_task");
			setDescription(i18n.getMessage("Global.GetFileInstruments.desc"));
		}
	
		/** The entry point of the task. */
		public void
		run() {
			try { setResult(CC.getClient().getFileInstruments(filename)); }
			catch(Exception x) {
				String s = getDescription() + ": " + HF.getErrorMessage(x);
				CC.getLogger().log(Level.FINE, s, x);
			}
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
			this.filename = filename;
			this.instrIdx = instrIdx;
			setTitle("Global.GetFileInstrument_task");
			setDescription(i18n.getMessage("Global.GetFileInstrument.desc"));
		}
	
		/** The entry point of the task. */
		public void
		run() {
			try { setResult(CC.getClient().getFileInstrumentInfo(filename, instrIdx)); }
			catch(Exception x) {
				String s = getDescription() + ": " + HF.getErrorMessage(x);
				CC.getLogger().log(Level.FINE, s, x);
			}
		}
	}
	
	public static class DummyTask extends EnhancedTask {
		public void
		run() { }
	}
}
