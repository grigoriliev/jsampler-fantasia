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

package org.jsampler.view.fantasia;

import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JSlider;

/**
 *
 * @author Grigor Iliev
 */
public class ChannelsBar extends PixmapPane {
	private final JSlider slVolume = new JSlider();
	
	/** Creates a new instance of <code>ChannelsBar</code> */
	public
	ChannelsBar() {
		super(Res.gfxCreateChannel);
		setPixmapInsets(new Insets(1, 1, 1, 1));
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		add(Box.createRigidArea(new Dimension(5, 0)));
		JLabel l = new JLabel(Res.iconVolume22);
		add(l);
		
		slVolume.setOpaque(false);
		slVolume.setFocusable(false);
		Dimension d = new Dimension(150, 22);
		slVolume.setPreferredSize(d);
		slVolume.setMaximumSize(d);
		
		add(slVolume);
		add(Box.createGlue());
		
		d = new Dimension(420, 29);
		setMinimumSize(d);
		setPreferredSize(d);
		setMaximumSize(d);
		
	}
	
}
