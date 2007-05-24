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
package org.jsampler.task;

import java.util.Vector;
import java.util.logging.Level;

import net.sf.juife.Task;

import org.linuxsampler.lscp.BoolParameter;
import org.linuxsampler.lscp.MidiInputDevice;
import org.linuxsampler.lscp.MidiInputDriver;
import org.linuxsampler.lscp.MidiInstrumentEntry;
import org.linuxsampler.lscp.MidiInstrumentInfo;
import org.linuxsampler.lscp.MidiInstrumentMapInfo;
import org.linuxsampler.lscp.Parameter;

import org.jsampler.CC;
import org.jsampler.HF;
import org.jsampler.MidiDeviceModel;
import org.jsampler.MidiInstrument;
import org.jsampler.MidiInstrumentMap;
import org.jsampler.SamplerModel;

import static org.jsampler.JSI18n.i18n;


/**
 * Provides the MIDI specific tasks.
 * @author Grigor Iliev
 */
public class Midi {
	/** Forbits the instantiation of this class. */
	private Midi() { }
	
	
	/**
	 * This task retrieves all MIDI input drivers currently
	 * available for the LinuxSampler instance.
	 */
	public static class GetDrivers extends EnhancedTask<MidiInputDriver[]> {
		/** Creates a new instance of <code>GetDrivers</code>. */
		public
		GetDrivers() {
			setTitle("Midi.GetDrivers_task");
			setDescription(i18n.getMessage("Midi.GetDrivers.desc"));
		}
	
		/** The entry point of the task. */
		public void
		run() {
			try { setResult(CC.getClient().getMidiInputDrivers()); }
			catch(Exception x) {
				setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
				CC.getLogger().log(Level.FINE, getErrorMessage(), x);
			}
		}
	}

	/**
	 * This task creates a new MIDI input device.
	 */
	public static class CreateDevice extends EnhancedTask<Integer> {
		private String driver;
		private Parameter[] parameters;
	
		/**
		 * Creates a new instance of <code>CreateDevice</code>.
		 * @param driver The desired MIDI input system.
		 * @param parameters An optional list of driver specific parameters.
		 */
		public
		CreateDevice(String driver, Parameter... parameters) {
			setTitle("Midi.CreateDevice_task");
			setDescription(i18n.getMessage("Midi.CreateDevice.desc"));
		
			this.driver = driver;
			this.parameters = parameters;
		}
	
		/** The entry point of the task. */
		public void
		run() {
			try {
				run0();
			} catch(Exception x) {
				setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
				CC.getLogger().log(Level.FINE, getErrorMessage(), x);
			}
		}
		
		private void
		run0() throws Exception {
			Integer deviceId = CC.getClient().createMidiInputDevice(driver, parameters);
			setResult(deviceId);
		}
	}
	
	/**
	 * This task destroys the specified MIDI input device.
	 */
	public static class DestroyDevice extends EnhancedTask {
		private int deviceId;
	
		/**
		 * Creates a new instance of <code>DestroyDevice</code>.
		 * @param deviceId The ID of the MIDI input device to be destroyed.
		 */
		public
		DestroyDevice(int deviceId) {
			setTitle("Midi.DestroyDevice_task");
			setDescription(i18n.getMessage("Midi.DestroyDevice.desc", deviceId));
			
			this.deviceId = deviceId;
		}
	
		/** The entry point of the task. */
		public void
		run() {
			try { CC.getClient().destroyMidiInputDevice(deviceId); }
			catch(Exception x) {
				setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
				CC.getLogger().log(Level.FINE, getErrorMessage(), x);
			}
		}
	}

	/**
	 * This task enables/disables a specific MIDI input device.
	 */
	public static class EnableDevice extends EnhancedTask {
		private int dev;
		private boolean enable;
	
		/**
		 * Creates new instance of <code>EnableDevice</code>.
		 * @param dev The id of the device to be enabled/disabled.
		 * @param enable Specify <code>true</code> to enable the MIDI device;
		 * code>false</code> to disable it.
		 */
		public
		EnableDevice(int dev, boolean enable) {
			setTitle("Midi.EnableDevice_task");
			setDescription(i18n.getMessage("Midi.EnableDevice.desc", dev));
		
			this.dev = dev;
			this.enable = enable;
		}
	
		/** The entry point of the task. */
		public void
		run() {
			try { 
				CC.getClient().enableMidiInputDevice(dev, enable);
			
				// Not needed, but eventually speeds up the change.
				CC.getSamplerModel().getMidiDeviceById(dev).setActive(enable);
			} catch(Exception x) {
				setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
				CC.getLogger().log(Level.FINE, getErrorMessage(), x);
			}
		}
	}

	/**
	 * This task alters a specific setting of a created MIDI input device.
	 */
	public static class SetDeviceParameter extends EnhancedTask {
		private int dev;
		private Parameter prm;
	
		/**
		 * Creates new instance of <code>SetDeviceParameter</code>.
		 * @param dev The id of the device whose parameter should be set.
		 * @param prmName The parameter name.
		 * @param value The new value for the specified parameter.
		 */
		public
		SetDeviceParameter(int dev, String prmName, boolean value) {
			this(dev, new BoolParameter(prmName, value));
		}
	
		/**
		 * Creates new instance of <code>SetDeviceParameter</code>.
		 * @param dev The id of the device whose parameter should be set.
		 * @param prm The parameter to be set.
		 */
		public
		SetDeviceParameter(int dev, Parameter prm) {
			setTitle("Midi.SetDeviceParameter_task");
			setDescription(i18n.getMessage("Midi.SetDeviceParameter.desc"));
		
			this.dev = dev;
			this.prm = prm;
		}
	
		/** The entry point of the task. */
		public void
		run() {
			try { 
				CC.getClient().setMidiInputDeviceParameter(dev, prm);
				
				CC.getSamplerModel().getMidiDeviceById(dev);
			} catch(Exception x) {
				setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
				CC.getLogger().log(Level.FINE, getErrorMessage(), x);
			}
		}
	}

	/**
	 * This task changes the port number of a speicific MIDI input device.
	 */
	public static class SetPortCount extends EnhancedTask {
		private int deviceId;
		private int ports;
	
		/**
		 * Creates new instance of <code>SetPortCount</code>.
		 * @param deviceId The id of the device whose ports number will be changed.
		 * @param ports The new number of ports.
		 */
		public
		SetPortCount(int deviceId, int ports) {
			setTitle("SetMidiInputPortCount_task");
			setDescription(i18n.getMessage("Midi.SetPortCount.desc", deviceId));
		
			this.deviceId = deviceId;
			this.ports = ports;
		}
	
		/** The entry point of the task. */
		public void
		run() {
			try { CC.getClient().setMidiInputPortCount(deviceId, ports); }
			catch(Exception x) {
				setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
				CC.getLogger().log(Level.FINE, getErrorMessage(), x);
			}
		}
	}
	
	/**
	 * This task alters a specific setting of a MIDI input port.
	 */
	public static class SetPortParameter extends EnhancedTask {
		private int dev;
		private int port;
		private Parameter prm;
	
		/**
		 * Creates new instance of <code>SetPortParameter</code>.
		 * @param dev The id of the device whose port parameter should be set.
		 * @param port The number of the port.
		 * @param prm The parameter to be set.
		 */
		public
		SetPortParameter(int dev, int port, Parameter prm) {
			setTitle("Midi.SetPortParameter_task");
			setDescription(i18n.getMessage("Midi.SetPortParameter.desc"));
		
			this.dev = dev;
			this.port = port;
			this.prm = prm;
		}
	
		/** The entry point of the task. */
		public void
		run() {
			try { CC.getClient().setMidiInputPortParameter(dev, port, prm); }
			catch(Exception x) {
				setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
				CC.getLogger().log(Level.FINE, getErrorMessage(), x);
			}
		}
	}

	/**
	 * This task updates the settings of a MIDI input device.
	 */
	public static class UpdateDeviceInfo extends EnhancedTask {
		private int dev;
		
		/**
		 * Creates new instance of <code>UpdateDeviceInfo</code>.
		 * @param dev The id of the device, whose settings should be updated.
		 */
		public
		UpdateDeviceInfo(int dev) {
			setTitle("Midi.UpdateDeviceInfo_task");
			setDescription(i18n.getMessage("Midi.UpdateDeviceInfo.desc", dev));
		
			this.dev = dev;
		}
	
		/** The entry point of the task. */
		public void
		run() {
			try { 
				MidiInputDevice mid = CC.getClient().getMidiInputDeviceInfo(dev);
				CC.getSamplerModel().getMidiDeviceById(dev).setDeviceInfo(mid);
			} catch(Exception x) {
				setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
				CC.getLogger().log(Level.FINE, getErrorMessage(), x);
			}
		}
	}

	/**
	 * This task updates the MIDI device list and all MIDI devices' settings.
	 */
	public static class UpdateDevices extends EnhancedTask {
		/** Creates a new instance of <code>UpdateDevices</code>. */
		public
		UpdateDevices() {
			setTitle("Midi.UpdateDevices_task");
			setDescription(i18n.getMessage("Midi.UpdateDevices.desc"));
		}
	
		/** The entry point of the task. */
		public void
		run() {
			try { 
				SamplerModel sm = CC.getSamplerModel();
				Integer[] deviceIDs = CC.getClient().getMidiInputDeviceIDs();
			
				boolean found = false;
				
				for(MidiDeviceModel m : sm.getMidiDevices()) {
					for(int i = 0; i < deviceIDs.length; i++) {
						if(m.getDeviceId() == deviceIDs[i]) {
							deviceIDs[i] = -1;
							found = true;
						}
					}
				
					if(!found) sm.removeMidiDeviceById(m.getDeviceId());
					found = false;
				}
			
				MidiInputDevice dev;
				
				for(int id : deviceIDs) {
					if(id >= 0) {
						dev = CC.getClient().getMidiInputDeviceInfo(id);
						sm.addMidiDevice(dev);
					}
				}
			} catch(Exception x) {
				setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
				CC.getLogger().log(Level.FINE, getErrorMessage(), x);
			}
		}
	}
	
	/**
	 * This task creates a new MIDI instrument map.
	 */
	public static class AddInstrumentMap extends EnhancedTask<Integer> {
		private String name;
		
		/**
		 * Creates a new instance of <code>AddInstrumentMap</code>.
		 * 
		 * @param name The chosen name for the new MIDI instrument map.
		 */
		public
		AddInstrumentMap(String name) {
			setTitle("Midi.AddMidiInstrumentMap_task");
			setDescription(i18n.getMessage("Midi.AddMidiInstrumentMap.desc"));
		
			this.name = name;
		}
	
		/** The entry point of the task. */
		public void
		run() {
			try {
				Integer mapId = CC.getClient().addMidiInstrumentMap(name);
				setResult(mapId);
			} catch(Exception x) {
				setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
				CC.getLogger().log(Level.FINE, getErrorMessage(), x);
			}
		}
	}
	
	/**
	 * This task removes the specified MIDI instrument map.
	 */
	public static class RemoveInstrumentMap extends EnhancedTask {
		private int mapId;
		
		/**
		 * Creates a new instance of <code>RemoveInstrumentMap</code>.
		 * 
		 * @param mapId The numerical ID of the MIDI instrument map to remove.
		 */
		public
		RemoveInstrumentMap(int mapId) {
			setTitle("Midi.RemoveMidiInstrumentMap_task");
			setDescription(i18n.getMessage("Midi.RemoveMidiInstrumentMap.desc", mapId));
		
			this.mapId = mapId;
		}
	
		/** The entry point of the task. */
		public void
		run() {
			try {
				CC.getClient().removeMidiInstrumentMap(mapId);
			} catch(Exception x) {
				setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
				CC.getLogger().log(Level.FINE, getErrorMessage(), x);
			}
		}
	}
	
	/**
	 * This task changes the MIDI instrument map settings.
	 */
	public static class SetInstrumentMapInfo extends EnhancedTask {
		private int mapId;
		private String name;
		
		/**
		 * Creates a new instance of <code>SetInstrumentMapInfo</code>.
		 * @param mapId The numerical ID of the MIDI instrument map.
		 * @param name The new name for the specified MIDI instrument map.
		 */
		public
		SetInstrumentMapInfo(int mapId, String name) {
			setTitle("Midi.SetInstrumentMapInfo_task");
			setDescription(i18n.getMessage("Midi.SetInstrumentMapInfo.desc"));
		
			this.mapId = mapId;
			this.name = name;
		}
	
		/** The entry point of the task. */
		public void
		run() {
			try {
				CC.getClient().setMidiInstrumentMapName(mapId, name);
			} catch(Exception x) {
				setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
				CC.getLogger().log(Level.FINE, getErrorMessage(), x);
			}
		}
	}
	
	/**
	 * This task updates the settings of a MIDI instrument map.
	 */
	public static class UpdateInstrumentMapInfo extends EnhancedTask {
		private int mapId;
		
		/**
		 * Creates new instance of <code>UpdateInstrumentMapInfo</code>.
		 * @param mapId The id of the MIDI instrument map, whose settings should be updated.
		 */
		public
		UpdateInstrumentMapInfo(int mapId) {
			setTitle("Midi.UpdateInstrumentMapInfo_task");
			setDescription(i18n.getMessage("Midi.UpdateInstrumentMapInfo.desc", mapId));
		
			this.mapId = mapId;
		}
	
		/** The entry point of the task. */
		public void
		run() {
			try { 
				MidiInstrumentMapInfo info =
					CC.getClient().getMidiInstrumentMapInfo(mapId);
				
				CC.getSamplerModel().getMidiInstrumentMapById(mapId).setInfo(info);
			} catch(Exception x) {
				setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
				CC.getLogger().log(Level.FINE, getErrorMessage(), x);
			}
		}
	}

	/**
	 * This task gets the MIDI instrument map list and all MIDI instruments' settings.
	 */
	public static class GetInstrumentMaps extends EnhancedTask<MidiInstrumentMap[]> {
		/** Creates a new instance of <code>GetInstrumentMaps</code>. */
		public
		GetInstrumentMaps() {
			setTitle("Midi.GetInstrumentMaps_task");
			setDescription(i18n.getMessage("Midi.GetInstrumentMaps.desc"));
		}
	
		/** The entry point of the task. */
		public void
		run() {
			try { 
				MidiInstrumentMapInfo[] mims;
				mims = CC.getClient().getMidiInstrumentMaps();
				MidiInstrumentMap[] maps = new MidiInstrumentMap[mims.length];
				
				for(int i = 0; i < mims.length; i++) {
					maps[i] = createMap(mims[i]);
				}
				
				setResult(maps);
			} catch(Exception x) {
				setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
				CC.getLogger().log(Level.FINE, getErrorMessage(), x);
			}
		}
		
		private MidiInstrumentMap
		createMap(MidiInstrumentMapInfo m) throws Exception {
			MidiInstrumentMap map = new MidiInstrumentMap(m);
			MidiInstrumentInfo[] miis = CC.getClient().getMidiInstruments(m.getMapId());
			
			for(MidiInstrumentInfo instrInfo : miis) {
				MidiInstrument instr = new MidiInstrument(instrInfo);
				map.mapMidiInstrument(instrInfo.getEntry(),  instr);
			}
			
			return map;
		}
	}
	
	/**
	 * This task updates the MIDI instrument map list.
	 */
	public static class UpdateInstrumentMaps extends EnhancedTask {
		/** Creates a new instance of <code>UpdateInstrumentMaps</code>. */
		public
		UpdateInstrumentMaps() {
			setTitle("Midi.UpdateInstrumentMaps_task");
			setDescription(i18n.getMessage("Midi.UpdateInstrumentMaps.desc"));
		}
	
		/** The entry point of the task. */
		public void
		run() {
			try { 
				SamplerModel sm = CC.getSamplerModel();
				Integer[] mapIDs = CC.getClient().getMidiInstrumentMapIDs();
			
				boolean found = false;
				
				for(MidiInstrumentMap m : sm.getMidiInstrumentMaps()) {
					for(int i = 0; i < mapIDs.length; i++) {
						if(mapIDs[i] == m.getMapId()) {
							mapIDs[i] = -1;
							found = true;
						}
					}
					
					if(!found) sm.removeMidiInstrumentMapById(m.getMapId());
					found = false;
				}
				
				MidiInstrumentMapInfo map;
				
				for(int id : mapIDs) {
					if(id >= 0) {
						map = CC.getClient().getMidiInstrumentMapInfo(id);
						sm.addMidiInstrumentMap(new MidiInstrumentMap(map));
					}
				}
			} catch(Exception x) {
				setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
				CC.getLogger().log(Level.FINE, getErrorMessage(), x);
				x.printStackTrace();
			}
		}
	}

	/**
	 * This task maps a new MIDI instrument or replaces an existing one.
	 */
	public static class MapInstrument extends EnhancedTask {
		private int mapId;
		private int bank;
		private int program;
		private MidiInstrumentInfo instrInfo;
		
		/**
		 * Creates new instance of <code>MapInstrument</code>.
		 * @param mapId The id of the MIDI instrument map.
		 * @param bank The index of the MIDI bank, which will contain the instrument.
		 * @param program The MIDI program number of the new instrument.
		 * @param instrInfo Provides the MIDI instrument settings.
		 */
		public
		MapInstrument(int mapId, int bank, int program, MidiInstrumentInfo instrInfo) {
			setTitle("Midi.MapInstrument_task");
			setDescription(i18n.getMessage("Midi.MapInstrument.desc"));
		
			this.mapId = mapId;
			this.bank = bank;
			this.program = program;
			this.instrInfo = instrInfo;
		}
	
		/** The entry point of the task. */
		public void
		run() {
			try { 
				MidiInstrumentEntry entry = new MidiInstrumentEntry(bank, program);
				CC.getClient().mapMidiInstrument(mapId, entry, instrInfo, true);
			} catch(Exception x) {
				setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
				CC.getLogger().log(Level.FINE, getErrorMessage(), x);
			}
		}
	}

	/**
	 * This task removes a MIDI instrument.
	 */
	public static class UnmapInstrument extends EnhancedTask {
		private int mapId;
		private int bank;
		private int program;
		
		/**
		 * Creates new instance of <code>UnmapInstrument</code>.
		 * @param mapId The id of the MIDI instrument
		 * map containing the instrument to be removed.
		 * @param bank The index of the MIDI bank containing the instrument to be removed.
		 * @param program The MIDI program number of the instrument to be removed.
		 */
		public
		UnmapInstrument(int mapId, int bank, int program) {
			setTitle("Midi.UnmapInstrument_task");
			setDescription(i18n.getMessage("Midi.UnmapInstrument.desc"));
		
			this.mapId = mapId;
			this.bank = bank;
			this.program = program;
		}
	
		/** The entry point of the task. */
		public void
		run() {
			try { 
				MidiInstrumentEntry entry = new MidiInstrumentEntry(bank, program);
				CC.getClient().unmapMidiInstrument(mapId, entry);
			} catch(Exception x) {
				setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
				CC.getLogger().log(Level.FINE, getErrorMessage(), x);
			}
		}
	}

	/**
	 * This task updates the settings of a MIDI instrument.
	 */
	public static class UpdateInstrumentInfo extends EnhancedTask {
		private int mapId;
		private int bank;
		private int program;
		
		/**
		 * Creates new instance of <code>UpdateInstrumentInfo</code>.
		 * @param mapId The id of the MIDI instrument map containg the instrument.
		 * @param bank The index of the MIDI bank, containing the instrument.
		 * @param program The MIDI program number of the instrument.
		 */
		public
		UpdateInstrumentInfo(int mapId, int bank, int program) {
			setTitle("Midi.UpdateInstrumentInfo_task");
			setDescription(i18n.getMessage("Midi.UpdateInstrumentInfo.desc"));
		
			this.mapId = mapId;
			this.bank = bank;
			this.program = program;
		}
	
		/** The entry point of the task. */
		public void
		run() {
			try { 
				MidiInstrumentInfo info =
					CC.getClient().getMidiInstrumentInfo(mapId, bank, program);
				
				MidiInstrumentMap map;
				map = CC.getSamplerModel().getMidiInstrumentMapById(mapId);
				map.getMidiInstrument(bank, program).setInfo(info);
			} catch(Exception x) {
				setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
				CC.getLogger().log(Level.FINE, getErrorMessage(), x);
			}
		}
	}

	/**
	 * This task updates the MIDI instrument list on a specific MIDI instrument map.
	 */
	public static class UpdateInstruments extends EnhancedTask {
		private int mapId;
		
		/** Creates a new instance of <code>UpdateInstruments</code>. */
		public
		UpdateInstruments(int mapId) {
			setTitle("Midi.UpdateInstruments_task");
			setDescription(i18n.getMessage("Midi.UpdateInstruments.desc"));
			
			this.mapId = mapId;
		}
	
		/** The entry point of the task. */
		public void
		run() {
			try { 
				SamplerModel sm = CC.getSamplerModel();
				MidiInstrumentInfo[] iS = CC.getClient().getMidiInstruments(mapId);
				MidiInstrumentMap map = sm.getMidiInstrumentMapById(mapId);
				
				boolean found = false;
				
				for(MidiInstrument instr : map.getAllMidiInstruments()) {
					for(int i = 0; i < iS.length; i++) {
						if(instr.getInfo().equals(iS[i])) {
							iS[i] = null;
							found = true;
						}
					}
				
					if(!found) {
						map.unmapMidiInstrument(instr.getInfo().getEntry());
					}
					found = false;
				}
			
				for(MidiInstrumentInfo mii : iS) {
					if(mii != null) {
						MidiInstrument instr = new MidiInstrument(mii);
						map.mapMidiInstrument(mii.getEntry(), instr);
					}
				}
			} catch(Exception x) {
				setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
				CC.getLogger().log(Level.FINE, getErrorMessage(), x);
			}
		}
	}

}
