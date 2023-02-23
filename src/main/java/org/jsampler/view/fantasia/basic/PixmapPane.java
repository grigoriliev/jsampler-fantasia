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
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.plaf.basic.BasicPanelUI;

import sun.swing.plaf.synth.Paint9Painter;


/**
 *
 * @author Grigor Iliev
 */
public class PixmapPane extends javax.swing.JPanel {
	private ImageIcon pixmap;
	private Insets pixmapInsets = null;
	
	private static Paint9Painter paint9Painter = new Paint9Painter(1);
	
	/**
	 * Creates a new double buffered <code>PixmapPane</code> with
	 * flow layout and with the specified pixmap to be used as background.
	 * @param pixmap The pixmap to be used for background.
	 */
	public
	PixmapPane(ImageIcon pixmap) { this(pixmap, new java.awt.FlowLayout()); }
	
	/**
	 * Creates a new double buffered <code>PixmapPane</code> with
	 * the specified layout manager and pixmap to be used as background.
	 * @param pixmap The pixmap to be used for background.
	 * @param layout the <code>LayoutManager</code> to use.
	 */
	public
	PixmapPane(ImageIcon pixmap, java.awt.LayoutManager layout) {
		super(layout);
		
		setOpaque(false);
		this.pixmap = pixmap;
	}
	
	@Override
	protected void
	paintComponent(Graphics g) {
		super.paintComponent(g);
		paintComponent(this, g, pixmap, pixmapInsets);
	}
	
	protected static void
	paintComponent(java.awt.Component c, Graphics g, ImageIcon pixmap, Insets pixmapInsets) {
		paint9Painter.paint (
			c, g, 0, 0, c.getWidth(), c.getHeight(), pixmap.getImage(),
			pixmapInsets, pixmapInsets,
			Paint9Painter.PaintType.PAINT9_STRETCH,
			Paint9Painter.PAINT_ALL
		);
	}
	
	/**
	 * Gets the pixmap that is used for background.
	 * @return The pixmap that is used for background.
	 */
	public ImageIcon
	getPixmap() { return pixmap; }
	
	/**
	 * Sets the pixmap to be used for background.
	 * @param pixmap Specifies the pixmap to be used for background.
	 */
	public void
	setPixmap(ImageIcon pixmap) {
		this.pixmap = pixmap;
		revalidate();
		repaint();
	}
	
	public void
	setPixmapInsets(Insets insets) {
		pixmapInsets = insets;
	}
	
	@Override
	public void
	updateUI() { setUI(new BasicPanelUI()); }
}
