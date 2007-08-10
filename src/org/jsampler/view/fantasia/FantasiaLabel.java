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

import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;


/**
 *
 * @author Grigor Iliev
 */
public class FantasiaLabel extends JLabel {
	private static java.awt.Color textColor = new java.awt.Color(0xFFA300);
	private static Insets pixmapInsets = new Insets(5, 5, 4, 5);
	
	/** Creates a new instance of <code>FantasiaLabel</code> */
	public
	FantasiaLabel(String text) {
		super(text);
		setOpaque(false);
		setBorder(BorderFactory.createEmptyBorder(4, 3, 6, 5));
		setForeground(new java.awt.Color(0xFFA300));
		setFont(Res.fontScreen);
	}
	
	protected void
	paintComponent(Graphics g) {
		PixmapPane.paintComponent(this, g, Res.gfxTextField, pixmapInsets);
		super.paintComponent(g);
	}
	
	public java.awt.Color
	getForeground() { return textColor; }
	
	public java.awt.Font
	getFont() { return Res.fontScreen; }
}
