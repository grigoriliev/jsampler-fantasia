/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2010 Grigor Iliev <grigor@grigoriliev.com>
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
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;

import org.jsampler.view.fantasia.Res;
import org.jsampler.view.std.StdUtils;

import org.pushingpixels.substance.api.renderers.SubstanceDefaultListCellRenderer;

/**
 *
 * @author Grigor Iliev
 */
public class FantasiaListCellRenderer extends SubstanceDefaultListCellRenderer {
	private FantasiaRenderer renderer = new FantasiaRenderer();
	
	/** Creates a new instance of <code>FantasiaListCellRenderer</code> */
	public
	FantasiaListCellRenderer() {
		
	}
	
	@Override
	public java.awt.Component
	getListCellRendererComponent (
		JList list, Object value, int index, boolean isSelected, boolean cellHasFocus
	) {
		if(index == -1) {
			renderer.setText(value == null ? "" : value.toString());
			return renderer;
		}
		return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
	}
	
	public static class FantasiaRenderer extends JLabel {
		private static java.awt.Color textColor = new java.awt.Color(0xFFA300);
		private static Insets pixmapInsets = new Insets(3, 5, 6, 5);
		private static ImageIcon bgImage = null;
		
		FantasiaRenderer() {
			setOpaque(false);
			setBorder(BorderFactory.createEmptyBorder(4, 5, 6, 5));
			setForeground(new java.awt.Color(0xFFA300));
			setBackground(new java.awt.Color(0x818181));
			setFont(Res.fontScreen);

			if(bgImage == null) bgImage = StdUtils.createImageIcon (
				Res.gfxCbLabelBg.getImage(), new java.awt.Color(0x818181)
			);
		}
		
		@Override
		protected void
		paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D)g;
			
			g2d.setRenderingHint (
				java.awt.RenderingHints.KEY_TEXT_ANTIALIASING,
				java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON
			);
			
			PixmapPane.paintComponent(this, g, bgImage, pixmapInsets);
			super.paintComponent(g);
		}
		
		@Override
		public java.awt.Color
		getForeground() { return textColor; }
		
		@Override
		public java.awt.Font
		getFont() { return Res.fontScreen; }
	}
}
