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

package org.jsampler.view;

import java.util.LinkedList;

import org.jsampler.CC;

/**
 *
 * @author Grigor Iliev
 */
public class SessionViewConfig {
	public static class ChannelConfig {
		public enum Type {
			SMALL, NORMAL
		}
		
		public int channelsPanel = 0;
		public Type type = Type.NORMAL;
		public boolean expanded = false;
	}
	
	public static class DeviceConfig {
		public boolean expanded = false;
	}
	
	private final LinkedList<ChannelConfig> channelConfigs = new LinkedList<ChannelConfig>();
	private final LinkedList<DeviceConfig> midiDeviceConfigs = new LinkedList<DeviceConfig>();
	private final LinkedList<DeviceConfig> audioDeviceConfigs = new LinkedList<DeviceConfig>();
	
	public
	SessionViewConfig(String[] conf) {
		if(conf == null) return;
		
		for(int i = 0; i < conf.length; i++) {
			String s = conf[i];
			if("[channel]".equals(s)) {
				channelConfigs.add(parseChannel(conf, i + 1));
			} else if("[MIDI device]".equals(s)) {
				midiDeviceConfigs.add(parseDevice(conf, i + 1));
			} else if("[audio device]".equals(s)) {
				audioDeviceConfigs.add(parseDevice(conf, i + 1));
			}
		}
	}
	
	public ChannelConfig
	pollChannelConfig() { return channelConfigs.poll(); }
	
	public DeviceConfig
	pollMidiDeviceConfig() { return midiDeviceConfigs.poll(); }
	
	public DeviceConfig
	pollAudioDeviceConfig() { return audioDeviceConfigs.poll(); }
	
	private ChannelConfig
	parseChannel(String[] conf, int index) {
		ChannelConfig channelConfig = new ChannelConfig();
		for(int i = index; i < conf.length; i++) {
			if(conf[i].startsWith("[")) return channelConfig;
			if(conf[i].startsWith("channelLane = ")) {
				String s = conf[i].substring("channelLane = ".length());
				if(s.isEmpty()) continue;
				
				try {
					channelConfig.channelsPanel = Integer.parseInt(s) - 1;
				} catch(Exception x) {
					CC.getLogger().info("Uknown channel lane: " + s);
				}
			} else if(conf[i].startsWith("channelsPanel = ")) {
				String s = conf[i].substring("channelsPanel = ".length());
				if(s.isEmpty()) continue;

				try {
					channelConfig.channelsPanel = Integer.parseInt(s) - 1;
				} catch(Exception x) {
					CC.getLogger().info("Uknown channel lane: " + s);
				}
			} else if(conf[i].startsWith("viewType = ")) {
				String s = conf[i].substring("viewType = ".length());
				
				if("SMALL".equals(s)) {
					channelConfig.type = ChannelConfig.Type.SMALL;
				} else if("NORMAL".equals(s)) {
					channelConfig.type = ChannelConfig.Type.NORMAL;
				} else {
					CC.getLogger().info("Uknown channel view: " + s);
				}
			} else if(conf[i].startsWith("expanded = ")) {
				String s = conf[i].substring("expanded = ".length());
				channelConfig.expanded = Boolean.parseBoolean(s);
			}
		}
		return channelConfig;
	}
	
	private DeviceConfig
	parseDevice(String[] conf, int index) {
		DeviceConfig deviceConfig = new DeviceConfig();
		for(int i = index; i < conf.length; i++) {
			if(conf[i].startsWith("[")) return deviceConfig;
			if(conf[i].startsWith("expanded = ")) {
				String s = conf[i].substring("expanded = ".length());
				deviceConfig.expanded = Boolean.parseBoolean(s);
			}
		}
		return deviceConfig;
	}
}
