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

package org.jsampler;

/**
 * Provides the LSCP grammar represented in a tree structure.
 * @author Grigor Iliev
 */
public class LscpTree {
	private static LscpNode rootNode;

	static {
		LscpNode add = new LscpNode("ADD");
		LscpNode clear = new LscpNode("CLEAR", new LscpNode("MIDI_INSTRUMENTS", true, false));
		LscpNode create = new LscpNode("CREATE");
		LscpNode destroy = new LscpNode("DESTROY");
		LscpNode get = new LscpNode("GET");
		LscpNode list = new LscpNode("LIST");
		LscpNode load = new LscpNode("LOAD");
		LscpNode map = new LscpNode("MAP", new LscpNode("MIDI_INSTRUMENT", true));
		LscpNode quit = new LscpNode("QUIT", true, false);
		LscpNode remove = new LscpNode("REMOVE");
		LscpNode reset = new LscpNode("RESET", new LscpNode("CHANNEL"), true, false);
		LscpNode set = new LscpNode("SET");
		LscpNode subscribe = new LscpNode("SUBSCRIBE");
		LscpNode unmap = new LscpNode("UNMAP", new LscpNode("MIDI_INSTRUMENT"), true);
		LscpNode unsubscribe = new LscpNode("UNSUBSCRIBE");
		
		LscpNode[] cmds = {
			add, clear, create, destroy, get, list, load, map, quit,
			remove, reset, set, subscribe, unmap, unsubscribe
		};
		
		rootNode = new LscpNode("", cmds);
		
		// ADD command
		LscpNode[] nodes = new LscpNode[2];
		nodes[0] = new LscpNode("CHANNEL", true, false);
		nodes[1] = new LscpNode("MIDI_INSTRUMENT_MAP");
		add.setChildren(nodes);
		
		// CREATE command
		nodes = new LscpNode[3];
		nodes[0] = new LscpNode("AUDIO_OUTPUT_DEVICE");
		nodes[1] = new LscpNode("FX_SEND");
		nodes[2] = new LscpNode("MIDI_INPUT_DEVICE");
		create.setChildren(nodes);
		
		// DESTROY command
		nodes = new LscpNode[3];
		nodes[0] = new LscpNode("AUDIO_OUTPUT_DEVICE");
		nodes[1] = new LscpNode("FX_SEND");
		nodes[2] = new LscpNode("MIDI_INPUT_DEVICE");
		destroy.setChildren(nodes);
		
		// GET command
		nodes = new LscpNode[26];
		nodes[0] = new LscpNode("AUDIO_OUTPUT_CHANNEL", new LscpNode("INFO"));
		nodes[1] = new LscpNode("AUDIO_OUTPUT_CHANNEL_PARAMETER", new LscpNode("INFO"));
		nodes[2] = new LscpNode("AUDIO_OUTPUT_DEVICE", new LscpNode("INFO"));
		nodes[3] = new LscpNode("AUDIO_OUTPUT_DEVICES", true, false);
		nodes[4] = new LscpNode("AUDIO_OUTPUT_DRIVER", new LscpNode("INFO"));
		nodes[5] = new LscpNode("AUDIO_OUTPUT_DRIVER_PARAMETER", new LscpNode("INFO"));
		nodes[6] = new LscpNode("AVAILABLE_AUDIO_OUTPUT_DRIVERS", true, false);
		nodes[7] = new LscpNode("AVAILABLE_ENGINES", true, false);
		nodes[8] = new LscpNode("AVAILABLE_MIDI_INPUT_DRIVERS", true, false);
		LscpNode getChn = new LscpNode("CHANNEL");
		nodes[9] = getChn;
		nodes[10] = new LscpNode("CHANNELS", true, false);
		nodes[11] = new LscpNode("ENGINE", new LscpNode("INFO"));
		nodes[12] = new LscpNode("FX_SEND", new LscpNode("INFO"));
		nodes[13] = new LscpNode("FX_SENDS");
		nodes[14] = new LscpNode("MIDI_INPUT_DEVICE", new LscpNode("INFO"));
		nodes[15] = new LscpNode("MIDI_INPUT_DEVICES", true, false);
		nodes[16] = new LscpNode("MIDI_INPUT_DRIVER", new LscpNode("INFO"));
		nodes[17] = new LscpNode("MIDI_INPUT_DRIVER_PARAMETER", new LscpNode("INFO"));
		nodes[18] = new LscpNode("MIDI_INPUT_PORT", new LscpNode("INFO"));
		nodes[19] = new LscpNode("MIDI_INPUT_PORT_PARAMETER", new LscpNode("INFO"));
		nodes[20] = new LscpNode("MIDI_INSTRUMENT", new LscpNode("INFO"));
		nodes[21] = new LscpNode("MIDI_INSTRUMENT_MAP", new LscpNode("INFO"));
		nodes[22] = new LscpNode("MIDI_INSTRUMENT_MAPS", true, false);
		nodes[23] = new LscpNode("MIDI_INSTRUMENTS", new LscpNode("ALL", true, false), true);
		nodes[24] = new LscpNode("SERVER", new LscpNode("INFO", true, false));
		nodes[25] = new LscpNode("VOLUME", true, false);
		get.setChildren(nodes);
		
		// GET CHANNEL command
		nodes = new LscpNode[4];
		nodes[0] = new LscpNode("BUFFER_FILL");
		nodes[1] = new LscpNode("INFO");
		nodes[2] = new LscpNode("STREAM_COUNT");
		nodes[3] = new LscpNode("VOICE_COUNT");
		getChn.setChildren(nodes);
		
		// LIST command
		nodes = new LscpNode[9];
		nodes[0] = new LscpNode("AUDIO_OUTPUT_DEVICES", true, false);
		nodes[1] = new LscpNode("AVAILABLE_AUDIO_OUTPUT_DRIVERS", true, false);
		nodes[2] = new LscpNode("AVAILABLE_ENGINES", true, false);
		nodes[3] = new LscpNode("AVAILABLE_MIDI_INPUT_DRIVERS", true, false);
		nodes[4] = new LscpNode("CHANNELS", true, false);
		nodes[5] = new LscpNode("FX_SENDS");
		nodes[6] = new LscpNode("MIDI_INPUT_DEVICES", true, false);
		nodes[7] = new LscpNode("MIDI_INSTRUMENT_MAPS", true, false);
		nodes[8] = new LscpNode("MIDI_INSTRUMENTS", true, false);
		list.setChildren(nodes);
		
		// LOAD command
		nodes = new LscpNode[2];
		nodes[0] = new LscpNode("ENGINE");
		nodes[1] = new LscpNode("INSTRUMENT", new LscpNode("NON_MODAL"), true);
		load.setChildren(nodes);
		
		// REMOVE command
		nodes = new LscpNode[2];
		nodes[0] = new LscpNode("CHANNEL");
		nodes[1] = new LscpNode("MIDI_INSTRUMENT_MAP", new LscpNode("ALL"), true);
		remove.setChildren(nodes);
		
		// SET command
		nodes = new LscpNode[9];
		nodes[0] = new LscpNode("AUDIO_OUTPUT_CHANNEL_PARAMETER");
		nodes[1] = new LscpNode("AUDIO_OUTPUT_DEVICE_PARAMETER");
		LscpNode setChn = new LscpNode("CHANNEL");
		nodes[2] = setChn;
		nodes[3] = new LscpNode("ECHO");
		LscpNode fxSend = new LscpNode("FX_SEND");
		nodes[4] = fxSend;
		nodes[5] = new LscpNode("MIDI_INPUT_DEVICE_PARAMETER");
		nodes[6] = new LscpNode("MIDI_INPUT_PORT_PARAMETER");
		nodes[7] = new LscpNode("MIDI_INSTRUMENT_MAP", new LscpNode("NAME"));
		nodes[8] = new LscpNode("VOLUME");
		set.setChildren(nodes);
		
		// SET CHANNEL command
		nodes = new LscpNode[12];
		nodes[0] = new LscpNode("AUDIO_OUTPUT_CHANNEL");
		nodes[1] = new LscpNode("AUDIO_OUTPUT_DEVICE");
		nodes[2] = new LscpNode("AUDIO_OUTPUT_TYPE");
		nodes[3] = new LscpNode("MIDI_INPUT");
		nodes[4] = new LscpNode("MIDI_INPUT_CHANNEL");
		nodes[5] = new LscpNode("MIDI_INPUT_DEVICE");
		nodes[6] = new LscpNode("MIDI_INPUT_PORT");
		nodes[7] = new LscpNode("MIDI_INPUT_TYPE");
		nodes[8] = new LscpNode("MIDI_INSTRUMENT_MAP");
		nodes[9] = new LscpNode("MUTE");
		nodes[10] = new LscpNode("SOLO");
		nodes[11] = new LscpNode("VOLUME");
		setChn.setChildren(nodes);
		
		// SET FX_SEND command
		nodes = new LscpNode[4];
		nodes[0] = new LscpNode("AUDIO_OUTPUT_CHANNEL");
		nodes[1] = new LscpNode("LEVEL");
		nodes[2] = new LscpNode("MIDI_CONTROLLER");
		nodes[3] = new LscpNode("NAME");
		fxSend.setChildren(nodes);
		
		// SUBSCRIBE command
		nodes = new LscpNode[18];
		nodes[0] = new LscpNode("AUDIO_OUTPUT_DEVICE_COUNT", true, false);
		nodes[1] = new LscpNode("AUDIO_OUTPUT_DEVICE_INFO", true, false);
		nodes[2] = new LscpNode("BUFFER_FILL", true, false);
		nodes[3] = new LscpNode("CHANNEL_COUNT", true, false);
		nodes[4] = new LscpNode("CHANNEL_INFO", true, false);
		nodes[5] = new LscpNode("FX_SEND_COUNT", true, false);
		nodes[6] = new LscpNode("FX_SEND_INFO", true, false);
		nodes[7] = new LscpNode("GLOBAL_INFO", true, false);
		nodes[8] = new LscpNode("MIDI_INPUT_DEVICE_COUNT", true, false);
		nodes[9] = new LscpNode("MIDI_INPUT_DEVICE_INFO", true, false);
		nodes[10] = new LscpNode("MIDI_INSTRUMENT_COUNT", true, false);
		nodes[11] = new LscpNode("MIDI_INSTRUMENT_INFO", true, false);
		nodes[12] = new LscpNode("MIDI_INSTRUMENT_MAP_COUNT", true, false);
		nodes[13] = new LscpNode("MIDI_INSTRUMENT_MAP_INFO", true, false);
		nodes[14] = new LscpNode("MISCELLANEOUS", true, false);
		nodes[15] = new LscpNode("STREAM_COUNT", true, false);
		nodes[16] = new LscpNode("TOTAL_VOICE_COUNT", true, false);
		nodes[17] = new LscpNode("VOICE_COUNT", true, false);
		subscribe.setChildren(nodes);
		
		// UNSUBSCRIBE command
		nodes = new LscpNode[18];
		nodes[0] = new LscpNode("AUDIO_OUTPUT_DEVICE_COUNT", true, false);
		nodes[1] = new LscpNode("AUDIO_OUTPUT_DEVICE_INFO", true, false);
		nodes[2] = new LscpNode("BUFFER_FILL", true, false);
		nodes[3] = new LscpNode("CHANNEL_COUNT", true, false);
		nodes[4] = new LscpNode("CHANNEL_INFO", true, false);
		nodes[5] = new LscpNode("FX_SEND_COUNT", true, false);
		nodes[6] = new LscpNode("FX_SEND_INFO", true, false);
		nodes[7] = new LscpNode("GLOBAL_INFO", true, false);
		nodes[8] = new LscpNode("MIDI_INPUT_DEVICE_COUNT", true, false);
		nodes[9] = new LscpNode("MIDI_INPUT_DEVICE_INFO", true, false);
		nodes[10] = new LscpNode("MIDI_INSTRUMENT_COUNT", true, false);
		nodes[11] = new LscpNode("MIDI_INSTRUMENT_INFO", true, false);
		nodes[12] = new LscpNode("MIDI_INSTRUMENT_MAP_COUNT", true, false);
		nodes[13] = new LscpNode("MIDI_INSTRUMENT_MAP_INFO", true, false);
		nodes[14] = new LscpNode("MISCELLANEOUS", true, false);
		nodes[15] = new LscpNode("STREAM_COUNT", true, false);
		nodes[16] = new LscpNode("TOTAL_VOICE_COUNT", true, false);
		nodes[17] = new LscpNode("VOICE_COUNT", true, false);
		unsubscribe.setChildren(nodes);
	}
	
	/** Forbits the instantiation of this class. */
	private
	LscpTree() { }
	
	/**
	 * Gets the root node of the tree.
	 */
	public static LscpNode
	getRoot() { return rootNode; }
}
