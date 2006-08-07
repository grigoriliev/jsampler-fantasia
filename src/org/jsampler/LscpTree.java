/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005, 2006 Grigor Kirilov Iliev
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
 *
 * @author Grigor Iliev
 */
public class LscpTree {
	private static LscpNode rootNode;

	static {
		LscpNode add = new LscpNode("ADD");
		LscpNode create = new LscpNode("CREATE");
		LscpNode destroy = new LscpNode("DESTROY");
		LscpNode get = new LscpNode("GET");
		LscpNode list = new LscpNode("LIST");
		LscpNode load = new LscpNode("LOAD");
		LscpNode quit = new LscpNode("QUIT", true, false);
		LscpNode remove = new LscpNode("REMOVE", new LscpNode("CHANNEL"));
		LscpNode reset = new LscpNode("RESET", new LscpNode("CHANNEL"), true, false);
		LscpNode set = new LscpNode("SET");
		LscpNode subscribe = new LscpNode("SUBSCRIBE");
		LscpNode unsubscribe = new LscpNode("UNSUBSCRIBE");
		
		LscpNode[] cmds = {
			add, create, destroy, get, list, load, quit,
			remove, reset, set, subscribe, unsubscribe
		};
		
		rootNode = new LscpNode("", cmds);
		
		// ADD command
		LscpNode[] nodes = { new LscpNode("CHANNEL", true, false) };
		add.setChildren(nodes);
		
		// CREATE command
		nodes = new LscpNode[2];
		nodes[0] = new LscpNode("AUDIO_OUTPUT_DEVICE");
		nodes[1] = new LscpNode("MIDI_INPUT_DEVICE");
		create.setChildren(nodes);
		
		// DESTROY command
		nodes = new LscpNode[2];
		nodes[0] = new LscpNode("AUDIO_OUTPUT_DEVICE");
		nodes[1] = new LscpNode("MIDI_INPUT_DEVICE");
		destroy.setChildren(nodes);
		
		// GET command
		nodes = new LscpNode[19];
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
		nodes[12] = new LscpNode("MIDI_INPUT_DEVICE", new LscpNode("INFO"));
		nodes[13] = new LscpNode("MIDI_INPUT_DEVICES", true, false);
		nodes[14] = new LscpNode("MIDI_INPUT_DRIVER", new LscpNode("INFO"));
		nodes[15] = new LscpNode("MIDI_INPUT_DRIVER_PARAMETER", new LscpNode("INFO"));
		nodes[16] = new LscpNode("MIDI_INPUT_PORT", new LscpNode("INFO"));
		nodes[17] = new LscpNode("MIDI_INPUT_PORT_PARAMETER", new LscpNode("INFO"));
		nodes[18] = new LscpNode("SERVER", new LscpNode("INFO", true, false));
		get.setChildren(nodes);
		
		// GET CHANNEL command
		nodes = new LscpNode[4];
		nodes[0] = new LscpNode("BUFFER_FILL");
		nodes[1] = new LscpNode("INFO");
		nodes[2] = new LscpNode("STREAM_COUNT");
		nodes[3] = new LscpNode("VOICE_COUNT");
		getChn.setChildren(nodes);
		
		// LIST command
		nodes = new LscpNode[6];
		nodes[0] = new LscpNode("AUDIO_OUTPUT_DEVICES", true, false);
		nodes[1] = new LscpNode("AVAILABLE_AUDIO_OUTPUT_DRIVERS", true, false);
		nodes[2] = new LscpNode("AVAILABLE_ENGINES", true, false);
		nodes[3] = new LscpNode("AVAILABLE_MIDI_INPUT_DRIVERS", true, false);
		nodes[4] = new LscpNode("CHANNELS", true, false);
		nodes[5] = new LscpNode("MIDI_INPUT_DEVICES", true, false);
		list.setChildren(nodes);
		
		// LOAD command
		nodes = new LscpNode[2];
		nodes[0] = new LscpNode("ENGINE");
		nodes[1] = new LscpNode("INSTRUMENT", new LscpNode("NON_MODAL"), true);
		load.setChildren(nodes);
		
		// SET command
		nodes = new LscpNode[6];
		nodes[0] = new LscpNode("AUDIO_OUTPUT_CHANNEL_PARAMETER");
		nodes[1] = new LscpNode("AUDIO_OUTPUT_DEVICE_PARAMETER");
		LscpNode setChn = new LscpNode("CHANNEL");
		nodes[2] = setChn;
		nodes[3] = new LscpNode("ECHO");
		nodes[4] = new LscpNode("MIDI_INPUT_DEVICE_PARAMETER");
		nodes[5] = new LscpNode("MIDI_INPUT_PORT_PARAMETER");
		set.setChildren(nodes);
		
		// SET CHANNEL command
		nodes = new LscpNode[11];
		nodes[0] = new LscpNode("AUDIO_OUTPUT_CHANNEL");
		nodes[1] = new LscpNode("AUDIO_OUTPUT_DEVICE");
		nodes[2] = new LscpNode("AUDIO_OUTPUT_TYPE");
		nodes[3] = new LscpNode("MIDI_INPUT");
		nodes[4] = new LscpNode("MIDI_INPUT_CHANNEL");
		nodes[5] = new LscpNode("MIDI_INPUT_DEVICE");
		nodes[6] = new LscpNode("MIDI_INPUT_PORT");
		nodes[7] = new LscpNode("MIDI_INPUT_TYPE");
		nodes[8] = new LscpNode("MUTE");
		nodes[9] = new LscpNode("SOLO");
		nodes[10] = new LscpNode("VOLUME");
		setChn.setChildren(nodes);
		
		// SUBSCRIBE command
		nodes = new LscpNode[6];
		nodes[0] = new LscpNode("BUFFER_FILL", true, false);
		nodes[1] = new LscpNode("CHANNEL_COUNT", true, false);
		nodes[2] = new LscpNode("CHANNEL_INFO", true, false);
		nodes[3] = new LscpNode("MISCELLANEOUS", true, false);
		nodes[4] = new LscpNode("STREAM_COUNT", true, false);
		nodes[5] = new LscpNode("VOICE_COUNT", true, false);
		subscribe.setChildren(nodes);
		
		// UNSUBSCRIBE command
		nodes = new LscpNode[6];
		nodes[0] = new LscpNode("BUFFER_FILL", true, false);
		nodes[1] = new LscpNode("CHANNEL_COUNT", true, false);
		nodes[2] = new LscpNode("CHANNEL_INFO", true, false);
		nodes[3] = new LscpNode("MISCELLANEOUS", true, false);
		nodes[4] = new LscpNode("STREAM_COUNT", true, false);
		nodes[5] = new LscpNode("VOICE_COUNT", true, false);
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
