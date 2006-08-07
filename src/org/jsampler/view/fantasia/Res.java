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

package org.jsampler.view.fantasia;

import javax.swing.ImageIcon;


/**
 * This class contains all pixmap resources needed by <b>Fantasia</b> view.
 * @author Grigor Iliev
 */
public class Res {
	
	/** Forbits the instantiation of this class. */
	private Res() { }
	
	protected final static ImageIcon iconPowerOn
		= new ImageIcon(Res.class.getResource("res/power_on.png"));
	
	protected final static ImageIcon iconPowerOff
		= new ImageIcon(Res.class.getResource("res/power_off.png"));

	protected final static ImageIcon iconMuteOn
		= new ImageIcon(Res.class.getResource("res/btn_mute_on.png"));
	
	protected final static ImageIcon iconMuteOff
		= new ImageIcon(Res.class.getResource("res/btn_mute_off.png"));
	
	protected final static ImageIcon iconMutedBySolo
		= new ImageIcon(Res.class.getResource("res/btn_mute_off.png"));
	
	protected final static ImageIcon iconSoloOn
		= new ImageIcon(Res.class.getResource("res/btn_mute_on.png"));
	
	protected final static ImageIcon iconSoloOff
		= new ImageIcon(Res.class.getResource("res/btn_mute_off.png"));
	
	protected final static ImageIcon iconMuteTitle
		= new ImageIcon(Res.class.getResource("res/title_mute.png"));
	
	protected final static ImageIcon iconSoloTitle
		= new ImageIcon(Res.class.getResource("res/title_solo.png"));
	
	protected final static ImageIcon iconVolumeTitle
		= new ImageIcon(Res.class.getResource("res/title_volume.png"));
	
	protected final static ImageIcon iconVolumeDial
		= new ImageIcon(Res.class.getResource("res/knob_volume.png"));
	
	protected final static ImageIcon iconOptionsTitle
		= new ImageIcon(Res.class.getResource("res/title_options.png"));
	
	protected final static ImageIcon iconOptionsOn
		= new ImageIcon(Res.class.getResource("res/btn_hide_channel_options.png"));
	
	protected final static ImageIcon iconOptionsOff
		= new ImageIcon(Res.class.getResource("res/btn_show_channel_options.png"));
	
	protected final static ImageIcon iconMidiInputTitle
		= new ImageIcon(Res.class.getResource("res/title_midi_input.png"));
	
	protected final static ImageIcon iconEngineTitle
		= new ImageIcon(Res.class.getResource("res/title_engine.png"));
	
	protected final static ImageIcon iconAudioOutputTitle
		= new ImageIcon(Res.class.getResource("res/title_audio_output.png"));
	
}
