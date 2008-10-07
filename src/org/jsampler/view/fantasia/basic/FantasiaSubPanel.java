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

import java.awt.Color;
import java.awt.Composite;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;

import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 *
 * @author Grigor Iliev
 */
public class FantasiaSubPanel extends JPanel {
	private boolean dark;
	private boolean darkParent;
	private boolean fill;
	
	public
	FantasiaSubPanel() { this(true); }
	
	public
	FantasiaSubPanel(boolean dark) { this(dark, false); }
	
	public
	FantasiaSubPanel(boolean dark, boolean darkParent) {
		this(dark, darkParent, true);
	}
	
	public
	FantasiaSubPanel(boolean dark, boolean darkParent, boolean fill) {
		this.dark = dark;
		this.darkParent = darkParent;
		this.fill = fill;
		
		setLayout(new java.awt.BorderLayout());
		
		if(fill) setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		else setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		
		setOpaque(false);
		//setBackground(Color.WHITE);
	}
	
	@Override
	protected void
	paintComponent(Graphics g) {
		if(isOpaque()) super.paintComponent(g);
		
		double h = getSize().getHeight();
		double w = getSize().getWidth();
		
		paintComponent((Graphics2D)g, 0, 0, w, h);
	}
	
	protected void
	paintComponent(Graphics2D g2, double x1, double y1, double width, double height) {
		Color c1 = dark ? FantasiaPainter.color2 : FantasiaPainter.color5;
		Color c2 = dark ? FantasiaPainter.color1 : FantasiaPainter.color4;
		
		paintComponent(g2, x1, y1, width, height, c1, c2);
	}
	
	protected void
	paintComponent (
		Graphics2D g2, double x1, double y1, double width, double height, Color c1, Color c2
	) {
		Paint oldPaint = g2.getPaint();
		Composite oldComposite = g2.getComposite();
		
		Rectangle2D.Double rect =
			new Rectangle2D.Double(x1 + 1, y1 + 1, width - 3, height - 3);
		
		Color bgColor = dark ? new Color(0x2b2b2b) : new Color(0x323232);
		g2.setPaint(bgColor);
		g2.draw(rect);
		
		if(fill) {
			rect = new Rectangle2D.Double(x1 + 2, y1 + 2, width - 4, height - 4);
			
			GradientPaint gr = new GradientPaint (
				(float)x1, (float)(y1 + 2.0f), c1,
				(float)x1, (float)(y1 + height - 1), c2
			);
			
			g2.setPaint(gr);
			g2.fill(rect);
			
			float alpha = dark ? 0.5f : 1.0f;
			
			FantasiaPainter.paintOuterBorder (
				g2, x1 + 2, y1 + 2, x1 + width - 3, y1 + height - 3, true, alpha, 1.0f
			);
		}
		
		float alpha = darkParent ? 0.4f : 1.0f;
		
		FantasiaPainter.paintInnerBorder (
			g2, x1, y1, x1 + width - 1, y1 + height - 1, true, alpha, 1.0f
		);
		
		g2.setPaint(oldPaint);
		g2.setComposite(oldComposite);
	}
}
