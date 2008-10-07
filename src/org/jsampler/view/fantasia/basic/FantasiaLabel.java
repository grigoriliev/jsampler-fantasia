/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2008 Grigor Iliev <grigor@grigoriliev.com>
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

package org.jsampler.view.fantasia.basic;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import javax.swing.plaf.basic.BasicLabelUI;

import org.jsampler.view.fantasia.Res;

/**
 *
 * @author Grigor Iliev
 */
public class FantasiaLabel extends JLabel {
	private static java.awt.Color textColor = new java.awt.Color(0xFFA300);
	private static Insets pixmapInsets = new Insets(5, 5, 4, 5);
	
	private boolean antialiased;
	
	/** Creates a new instance of <code>FantasiaLabel</code> */
	public
	FantasiaLabel(String text) { this(text, false); }
	
	/** Creates a new instance of <code>FantasiaLabel</code> */
	public
	FantasiaLabel(String text, boolean antialiased) {
		super(text);
		this.antialiased = antialiased;
		setOpaque(false);
		setBorder(BorderFactory.createEmptyBorder(4, 3, 6, 5));
		setForeground(new java.awt.Color(0xFFA300));
		setFont(Res.fontScreen);
	}
	
	@Override
	protected void
	paintComponent(Graphics g) {
		if(antialiased) {
			Graphics2D g2d = (Graphics2D)g;
			
			g2d.setRenderingHint (
				java.awt.RenderingHints.KEY_TEXT_ANTIALIASING,
				java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON
			);
		}
		
		PixmapPane.paintComponent(this, g, Res.gfxTextField, pixmapInsets);
		super.paintComponent(g);
	}
		
	@Override
	public void
	updateUI() { setUI(new BasicLabelUI()); }
	
	@Override
	public java.awt.Color
	getForeground() { return textColor; }
	
	@Override
	public java.awt.Font
	getFont() { return Res.fontScreen; }
}
