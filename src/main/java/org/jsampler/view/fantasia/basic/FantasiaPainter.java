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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.RenderingHints;

import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

/**
 *
 * @author Grigor Iliev
 */
public class FantasiaPainter {
	public static Color color1 = new Color(0x434343);
	public static Color color2 = new Color(0x535353);
	public static Color color4 = new Color(0x6e6e6e);
	public static Color color5 = new Color(0x7a7a7a);
	public static Color color6 = new Color(0x8a8a8a);
	public static Color color7 = new Color(0x9a9a9a);
	
	public static class RoundCorners {
		public boolean topLeft, bottomLeft, bottomRight, topRight;
		
		public
		RoundCorners(boolean round) {
			this(round, round, round, round);
		}
		
		public
		RoundCorners(boolean topLeft, boolean bottomLeft, boolean bottomRight, boolean topRight) {
			this.topLeft = topLeft;
			this.topRight = topRight;
			this.bottomLeft = bottomLeft;
			this.bottomRight = bottomRight;
		}
	}
	
	public static class Border {
		public boolean paintTop, paintLeft, paintBottom, paintRight;
		
		public
		Border(boolean paintBorder) {
			this(paintBorder, paintBorder, paintBorder, paintBorder);
		}
		
		public
		Border(boolean paintTop, boolean paintLeft, boolean paintBottom, boolean paintRight) {
			this.paintTop = paintTop;
			this.paintLeft = paintLeft;
			this.paintBottom = paintBottom;
			this.paintRight = paintRight;
		}
	}
	
	private
	FantasiaPainter() { }
	
	public static void
	paintGradient(Graphics2D g2, double x1, double y1, double x2, double y2) {
		paintGradient(g2, x1, y1, x2, y2, color5, color4);
	}
	
	public static void
	paintDarkGradient(Graphics2D g2, double x1, double y1, double x2, double y2) {
		paintGradient(g2, x1, y1, x2, y2, color2, color1);
	}
	
	public static void
	paintGradient(Graphics2D g2, double x1, double y1, double x2, double y2, Color c1, Color c2) {
		Paint oldPaint = g2.getPaint();
		
		Rectangle2D.Double rect = new Rectangle2D.Double(x1, y1, x2 - x1 + 1, y2 -y1 + 1);
		
		GradientPaint gr = new GradientPaint (
			(float)x1, (float)y1, c1,
			(float)x1, (float)y2, c2
		);
		
		g2.setPaint(gr);
		g2.fill(rect);
		
		g2.setPaint(oldPaint);
	}
	
	public static void
	paintOuterBorder(Graphics2D g2, double x1, double y1, double x2, double y2) {
		paintOuterBorder(g2, x1, y1, x2, y2, false);
	}
	
	public static void
	paintOuterBorder(Graphics2D g2, double x1, double y1, double x2, double y2, boolean round) {
		paintOuterBorder(g2, x1, y1, x2, y2, round, 1.0f, 1.0f);
	}
	
	public static void
	paintOuterBorder(Graphics2D g2, double x1, double y1, double x2, double y2, RoundCorners rc) {
		paintOuterBorder(g2, x1, y1, x2, y2, rc, 1.0f, 1.0f);
	}
	
	public static void
	paintOuterBorder (
		Graphics2D g2,
		double x1, double y1, double x2, double y2,
		boolean round, float alphaWhite, float alphaBlack
	) {
		paintOuterBorder(g2, x1, y1, x2, y2, new RoundCorners(round), alphaWhite, alphaBlack);
	}
	
	public static void
	paintOuterBorder (
		Graphics2D g2,
		double x1, double y1, double x2, double y2,
		boolean round, float alphaTop, float alphaLeft, float alphaBottom, float alphaRight
	) {
		paintOuterBorder (
			g2, x1, y1, x2, y2, new RoundCorners(round), 1.0f, 1.0f,
			alphaTop, alphaLeft, alphaBottom, alphaRight
		);
	}
	
	public static void
	paintOuterBorder (
		Graphics2D g2,
		double x1, double y1, double x2, double y2,
		RoundCorners rc, float alphaWhite, float alphaBlack
	) {
		paintOuterBorder (
			g2, x1, y1, x2, y2, rc, alphaWhite, alphaBlack,
			alphaWhite * 0.40f, alphaWhite * 0.255f, alphaBlack * 0.40f, alphaBlack * 0.20f
		);
	}
	public static void
	paintOuterBorder (
		Graphics2D g2,
		double x1, double y1, double x2, double y2, RoundCorners rc,
		float alphaWhite, float alphaBlack,
		float alphaTop, float alphaLeft, float alphaBottom, float alphaRight
	) {
		
		Paint oldPaint = g2.getPaint();
		Composite oldComposite = g2.getComposite();
		
		AlphaComposite ac;
		ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaTop);
		g2.setComposite(ac);
		
		double x1t = rc.topLeft ? x1 + 2 : x1;
		double x1b = rc.bottomLeft ? x1 + 2 : x1;
		double x2t = rc.topRight ? x2 - 2 : x2;
		double x2b = rc.bottomRight ? x2 - 2 : x2;
		double y1l = rc.topLeft ? y1 + 2 : y1;
		double y1r = rc.topRight ? y1 + 2 : y1;
		double y2l = rc.bottomLeft ? y2 - 2 : y2;
		double y2r = rc.bottomRight ? y2 - 2 : y2;
		
		g2.setPaint(Color.WHITE);
		Line2D.Double l = new Line2D.Double(x1t, y1, x2t, y1);
		g2.draw(l);
		
		if(rc.topLeft) {
			// top-left corner
			g2.setComposite(ac.derive(0.30f * alphaWhite));
			l = new Line2D.Double(x1 + 1, y1, x1 + 1, y1);
			g2.draw(l);
			
			g2.setComposite(ac.derive(0.20f * alphaWhite));
			l = new Line2D.Double(x1 + 1, y1 + 1, x1 + 1, y1 + 1);
			g2.draw(l);
			
			g2.setComposite(ac.derive(0.15f * alphaWhite));
			l = new Line2D.Double(x1, y1 + 1, x1, y1 + 1);
			g2.draw(l);
			
			g2.setPaint(Color.BLACK);
			g2.setComposite(ac.derive(0.15f * alphaBlack));
			l = new Line2D.Double(x1, y1, x1, y1);
			g2.draw(l);
			
			g2.setPaint(Color.WHITE);
		}
		
		if(rc.topRight) {
			// top-right corner
			g2.setPaint(Color.WHITE);
			g2.setComposite(ac.derive(0.20f * alphaWhite));
			l = new Line2D.Double(x2 - 1, y1, x2 - 1, y1);
			g2.draw(l);
			
			g2.setComposite(ac.derive(0.10f * alphaWhite));
			l = new Line2D.Double(x2 - 1, y1 + 1, x2 - 1, y1 + 1);
			g2.draw(l);
		}
		
		g2.setComposite(ac.derive(alphaLeft));
		
		l = new Line2D.Double(x1, y1l, x1, y2l);
		g2.draw(l);
		
		g2.setComposite(ac.derive(alphaBottom));
		g2.setPaint(Color.BLACK);
		
		l = new Line2D.Double(x1b, y2, x2b, y2);
		g2.draw(l);
		
		if(rc.bottomLeft) {
			// bottom-left corner
			l = new Line2D.Double(x1, y2, x1, y2);
			g2.draw(l);
			
			g2.setComposite(ac.derive(0.30f * alphaBlack));
			l = new Line2D.Double(x1 + 1, y2, x1 + 1, y2);
			g2.draw(l);
			
			g2.setComposite(ac.derive(0.10f * alphaBlack));
			l = new Line2D.Double(x1 + 1, y2 - 1, x1 + 1, y2 - 1);
			g2.draw(l);
			
			g2.setPaint(Color.WHITE);
			g2.setComposite(ac.derive(0.05f * alphaWhite));
			l = new Line2D.Double(x1, y2 - 1, x1, y2 - 1);
			g2.draw(l);
			g2.setPaint(Color.BLACK);
		}
		
		g2.setComposite(ac.derive(alphaRight));
		
		l = new Line2D.Double(x2, y1r, x2, y2r);
		g2.draw(l);
		
		if(rc.topRight) {
			//top-right corner
			g2.setComposite(ac.derive(0.15f * alphaBlack));
			l = new Line2D.Double(x2, y1 + 1, x2, y1 + 1);
			g2.draw(l);
			
			g2.setComposite(ac.derive(0.25f * alphaBlack));
			l = new Line2D.Double(x2, y1, x2, y1);
			g2.draw(l);
		}
		
		if(rc.bottomRight) {
			//bottom-right corner
			g2.setComposite(ac.derive(0.30f * alphaBlack));
			l = new Line2D.Double(x2, y2 - 1, x2, y2 - 1);
			g2.draw(l);
			
			g2.setComposite(ac.derive(0.10f * alphaBlack));
			l = new Line2D.Double(x2 - 1, y2 - 1, x2 - 1, y2 - 1);
			g2.draw(l);
			
			g2.setComposite(ac.derive(0.43f * alphaBlack));
			l = new Line2D.Double(x2, y2, x2, y2);
			g2.draw(l);
			
			g2.setComposite(ac.derive(0.38f * alphaBlack));
			l = new Line2D.Double(x2 - 1, y2, x2 - 1, y2);
			g2.draw(l);
		}
		
		g2.setComposite(oldComposite);
		g2.setPaint(oldPaint);
	}
	
	public static void
	paintBoldOuterBorder(Graphics2D g2, double x1, double y1, double x2, double y2) {
		paintBoldOuterBorder(g2, x1, y1, x2, y2, new Border(true));
	}
	
	public static void
	paintBoldOuterBorder(Graphics2D g2, double x1, double y1, double x2, double y2, Border border) {
		Paint oldPaint = g2.getPaint();
		Composite oldComposite = g2.getComposite();
		
		if(border.paintTop) {
			paintTopBoldOuterBorder(g2, x1 + 2, y1, x2 - 2, y1);
			paintTopBoldRoundCorners(g2, x1, y1, x2, y2);
		}
		
		if(border.paintLeft) {
			paintLeftBoldOuterBorder(g2, x1, y1, x2, y2);
		}
		
		if(border.paintBottom) {
			paintBottomBoldOuterBorder(g2, x1, y1, x2, y2);
		}
		
		paintBottomLeftBoldRoundCorner(g2, x1, y2);
		
		if(border.paintRight) {
			paintRightBoldOuterBorder(g2, x1, y1, x2, y2);
		}
		
		paintBottomRightBoldRoundCorner(g2, x2, y2);
		
		g2.setPaint(oldPaint);
		g2.setComposite(oldComposite);
	}
	
	public static void
	paintRightBoldOuterBorder(Graphics2D g2, double x1, double y1, double x2, double y2) {
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.20f);
		g2.setComposite(ac);
		
		g2.setPaint(Color.BLACK);
		Line2D.Double l = new Line2D.Double(x2, y1, x2, y2 - 3);
		g2.draw(l);
		
		g2.setComposite(ac.derive(0.10f));
		l = new Line2D.Double(x2 - 1, y1, x2 - 1, y2 - 1);
		g2.draw(l);
	}
	
	public static void
	paintBottomBoldOuterBorder(Graphics2D g2, double x1, double y1, double x2, double y2) {
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.40f);
		g2.setComposite(ac);
		
		g2.setPaint(Color.BLACK);
		Line2D.Double l = new Line2D.Double(x1 + 3, y2, x2 - 2, y2);
		g2.draw(l);
		
		g2.setComposite(ac.derive(0.20f));
		l = new Line2D.Double(x1 + 1, y2 - 1, x2 - 1, y2 - 1);
		g2.draw(l);
	}
	
	public static void
	paintLeftBoldOuterBorder(Graphics2D g2, double x1, double y1, double x2, double y2) {
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.255f);
		g2.setComposite(ac);
		
		g2.setPaint(Color.WHITE);
		Line2D.Double l = new Line2D.Double(x1, y1, x1, y2 - 3);
		g2.draw(l);
		
		g2.setComposite(ac.derive(0.10f));
		l = new Line2D.Double(x1 + 1, y1, x1 + 1, y2 - 1);
		g2.draw(l);
	}
	
	public static void
	paintBottomRightBoldRoundCorner(Graphics2D g2, double x2, double y2) {
		g2.setPaint(Color.BLACK);
		// round right-down corner
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.37f);
		g2.setComposite(ac);
		Line2D.Double l = new Line2D.Double(x2 - 1, y2, x2 - 1, y2);
		g2.draw(l);
		
		g2.setComposite(ac.derive(0.34f));
		l = new Line2D.Double(x2, y2, x2, y2);
		g2.draw(l);
		///////
		
		// round right corner
		g2.setComposite(ac.derive(0.25f));
		l = new Line2D.Double(x2, y2 - 2, x2, y2 - 2);
		g2.draw(l);
		
		g2.setComposite(ac.derive(0.30f));
		l = new Line2D.Double(x2, y2 - 1, x2, y2 - 1);
		g2.draw(l);
		///////
		
	}
	
	public static void
	paintBottomLeftBoldRoundCorner(Graphics2D g2, double x1, double y2) {
		g2.setPaint(Color.WHITE);
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f);
		g2.setComposite(ac);
		Line2D.Double l = new Line2D.Double(x1, y2 - 2, x1, y2 - 2);
		g2.draw(l);
		
		g2.setPaint(Color.BLACK);
		g2.setComposite(ac.derive(0.27f));
		l = new Line2D.Double(x1, y2, x1, y2);
		g2.draw(l);
		
		g2.setComposite(ac.derive(0.32f));
		l = new Line2D.Double(x1 + 1, y2, x1 + 1, y2);
		g2.draw(l);
		
		g2.setComposite(ac.derive(0.37f));
		l = new Line2D.Double(x1 + 2, y2, x1 + 2, y2);
		g2.draw(l);
	}
	
	public static void
	paintTopBoldOuterBorder(Graphics2D g2, double x1, double y1, double x2, double y2) {
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.40f);
		g2.setComposite(ac);
		
		g2.setPaint(Color.WHITE);
		Line2D.Double l = new Line2D.Double(x1, y1, x2 - 1, y2);
		g2.draw(l);
		
		g2.setComposite(ac.derive(0.20f));
		l = new Line2D.Double(x1, y1 + 1, x2, y2 + 1);
		g2.draw(l);
	}
	
	public static void
	paintTopBoldRoundCorners(Graphics2D g2, double x1, double y1, double x2, double y2) {
		paintTopLeftBoldRoundCorner(g2, x1, y1);
		paintTopRightBoldRoundCorner(g2, y1, x2);
	}
	
	public static void
	paintTopLeftBoldRoundCorner(Graphics2D g2, double x1, double y1) {
		Paint oldPaint = g2.getPaint();
		Composite oldComposite = g2.getComposite();
		
		g2.setPaint(Color.BLACK);
		
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.20f);
		g2.setComposite(ac);
		Line2D.Double l = new Line2D.Double(x1, y1, x1, y1);
		g2.draw(l);
		
		g2.setPaint(Color.WHITE);
		g2.setComposite(ac.derive(0.20f));
		l = new Line2D.Double(x1 + 1, y1, x1 + 1, y1);
		g2.draw(l);
		
		g2.setComposite(ac.derive(0.15f));
		l = new Line2D.Double(x1, y1 + 1, x1, y1 + 1);
		g2.draw(l);
		
		g2.setComposite(ac.derive(0.30f));
		l = new Line2D.Double(x1 + 1, y1 + 1, x1 + 1, y1 + 1);
		g2.draw(l);
		
		g2.setComposite(ac.derive(0.10f));
		l = new Line2D.Double(x1, y1 + 2, x1 + 1, y1 + 2);
		g2.draw(l);
		
		g2.setPaint(oldPaint);
		g2.setComposite(oldComposite);
	}
	
	public static void
	paintTopRightBoldRoundCorner(Graphics2D g2, double y1, double x2) {
		Paint oldPaint = g2.getPaint();
		Composite oldComposite = g2.getComposite();
		
		g2.setPaint(Color.WHITE);
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.30f);
		g2.setComposite(ac);
		Line2D.Double l = new Line2D.Double(x2 - 2, y1, x2 - 2, y1);
		g2.draw(l);
		
		g2.setComposite(ac.derive(0.20f));
		l = new Line2D.Double(x2 - 1, y1, x2 - 1, y1);
		g2.draw(l);
		
		g2.setPaint(Color.BLACK);
		g2.setComposite(ac.derive(0.20f));
		l = new Line2D.Double(x2, y1, x2, y1);
		g2.draw(l);
		
		g2.setPaint(Color.WHITE);
		g2.setComposite(ac.derive(0.10f));
		l = new Line2D.Double(x2 - 1, y1 + 1, x2 - 1, y1 + 1);
		g2.draw(l);
		
		g2.setPaint(Color.BLACK);
		g2.setComposite(ac.derive(0.05f));
		l = new Line2D.Double(x2, y1 + 1, x2, y1 + 1);
		g2.draw(l);
		
		g2.setPaint(oldPaint);
		g2.setComposite(oldComposite);
	}
	
	public static void
	paintInnerBorder(Graphics2D g2, double x1, double y1, double x2, double y2) {
		paintInnerBorder(g2, x1, y1, x2, y2, false);
	}
	
	public static void
	paintInnerBorder(Graphics2D g2, double x1, double y1, double x2, double y2, boolean round) {
		paintInnerBorder(g2, x1, y1, x2, y2, round, 1.0f, 1.0f);
	}
	
	public static void
	paintInnerBorder (
		Graphics2D g2,
		double x1, double y1, double x2, double y2,
		boolean round, float alphaWhite, float alphaBlack
	) {
		Paint oldPaint = g2.getPaint();
		Composite oldComposite = g2.getComposite();
		
		AlphaComposite ac;
		ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.30f * alphaWhite);
		g2.setComposite(ac);
		
		g2.setPaint(Color.WHITE);
		double x1b = round ? x1 + 3 : x1;
		double x2b = round ? x2 - 3 : x2;
		double y1b = round ? y1 + 3 : y1;
		double y2b = round ? y2 - 3 : y2;
		
		Line2D.Double l = new Line2D.Double(x1b, y2, x2b, y2);
		g2.draw(l);
		
		// bottom-left round border
		if(round) {
			g2.setComposite(ac.derive(0.20f * alphaWhite));
			l = new Line2D.Double(x1 + 2, y2, x1 + 2, y2);
			g2.draw(l);
			
			g2.setComposite(ac.derive(0.10f * alphaWhite));
			l = new Line2D.Double(x1 + 1, y2, x1 + 1, y2);
			g2.draw(l);
			
			g2.setComposite(ac.derive(0.05f * alphaWhite));
			l = new Line2D.Double(x1 + 1, y2 - 1, x1 + 1, y2 - 1);
			g2.draw(l);
		}
		///////
		
		g2.setComposite(ac.derive(0.20f * alphaWhite));
		l = new Line2D.Double(x2, y1b, x2, y2b);
		g2.draw(l);
		
		// bottom-right round border
		if(round) {
			g2.setComposite(ac.derive(0.15f * alphaWhite));
			l = new Line2D.Double(x2, y2 - 2, x2, y2 - 2);
			g2.draw(l);
			
			g2.setComposite(ac.derive(0.10f * alphaWhite));
			l = new Line2D.Double(x2, y2 - 1, x2, y2 - 1);
			g2.draw(l);
			
			g2.setComposite(ac.derive(0.10f * alphaWhite));
			l = new Line2D.Double(x2 - 1, y2 - 1, x2 - 1, y2 - 1);
			g2.draw(l);
			
			/***/
			
			g2.setComposite(ac.derive(0.15f * alphaWhite));
			l = new Line2D.Double(x2 - 2, y2, x2 - 2, y2);
			g2.draw(l);
			
			g2.setComposite(ac.derive(0.10f * alphaWhite));
			l = new Line2D.Double(x2 - 1, y2, x2 - 1, y2);
			g2.draw(l);
		}
		///////
		
		g2.setComposite(ac.derive(0.20f * alphaBlack));
		g2.setPaint(Color.BLACK);
		
		l = new Line2D.Double(x1b, y1, x2b, y1);
		g2.draw(l);
		
		if(round) {
			// top-left round border
			g2.setComposite(ac.derive(0.05f * alphaBlack));
			l = new Line2D.Double(x1, y1, x1, y1);
			g2.draw(l);
			
			g2.setComposite(ac.derive(0.10f * alphaBlack));
			l = new Line2D.Double(x1 + 1, y1, x1 + 1, y1);
			g2.draw(l);
			
			g2.setComposite(ac.derive(0.15f * alphaBlack));
			l = new Line2D.Double(x1 + 2, y1, x1 + 2, y1);
			g2.draw(l);
			
			g2.setPaint(Color.WHITE);
			g2.setComposite(ac.derive(0.15f * alphaWhite));
			l = new Line2D.Double(x1 + 1, y1 + 1, x1 + 1, y1 + 1);
			g2.draw(l);
			
			// top-right round border
			g2.setComposite(ac.derive(0.05f * alphaWhite));
			l = new Line2D.Double(x2, y1 + 1, x2, y1 + 1);
			g2.draw(l);
			
			g2.setComposite(ac.derive(0.10f * alphaWhite));
			l = new Line2D.Double(x2, y1 + 2, x2, y1 + 2);
			g2.draw(l);
			
			g2.setComposite(ac.derive(0.15f * alphaWhite));
			l = new Line2D.Double(x2 - 1, y1 + 1, x2 - 1, y1 + 1);
			g2.draw(l);
			
			g2.setPaint(Color.BLACK);
			g2.setComposite(ac.derive(0.10f * alphaBlack));
			l = new Line2D.Double(x2 - 2, y1, x2 - 2, y1);
			g2.draw(l);
			
			g2.setComposite(ac.derive(0.05f * alphaBlack));
			l = new Line2D.Double(x2 - 1, y1, x2 - 1, y1);
			g2.draw(l);
		}
		
		g2.setComposite(ac.derive(0.20f * alphaBlack));
		l = new Line2D.Double(x1, y1b, x1, y2b);
		g2.draw(l);
		
		if(round) {
			// top-left round border
			g2.setComposite(ac.derive(0.10f * alphaBlack));
			l = new Line2D.Double(x1, y1 + 1, x1, y1 + 1);
			g2.draw(l);
			
			g2.setComposite(ac.derive(0.15f * alphaBlack));
			l = new Line2D.Double(x1, y1 + 2, x1, y1 + 2);
			g2.draw(l);
			
			// bottom-left round border
			g2.setComposite(ac.derive(0.10f * alphaBlack));
			l = new Line2D.Double(x1, y2 - 2, x1, y2 - 2);
			g2.draw(l);
			
			g2.setComposite(ac.derive(0.05f * alphaBlack));
			l = new Line2D.Double(x1, y2 - 1, x1, y2 - 1);
			g2.draw(l);
		}
		///////
		
		g2.setComposite(oldComposite);
		g2.setPaint(oldPaint);
	}
	
	public static void
	paintBoldInnerBorder(Graphics2D g2, double x1, double y1, double x2, double y2) {
		Paint oldPaint = g2.getPaint();
		Composite oldComposite = g2.getComposite();
		
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.40f);
		g2.setComposite(ac);
		
		g2.setPaint(Color.WHITE);
		Line2D.Double l = new Line2D.Double(x1, y2, x2, y2);
		g2.draw(l);
		
		g2.setComposite(ac.derive(0.15f));
		l = new Line2D.Double(x1 - 1, y2 + 1, x2 + 1, y2 + 1);
		g2.draw(l);
		
		g2.setComposite(ac.derive(0.255f));
		l = new Line2D.Double(x2, y1, x2, y2);
		g2.draw(l);
		
		g2.setComposite(ac.derive(0.13f));
		l = new Line2D.Double(x2 + 1, y1 - 1, x2 + 1, y2 + 1);
		g2.draw(l);
		
		g2.setComposite(ac.derive(0.20f));
		g2.setPaint(Color.BLACK);
		
		l = new Line2D.Double(x1 - 1, y1 - 1, x2 + 1, y1 - 1);
		g2.draw(l);
		
		g2.setComposite(ac.derive(0.40f));
		g2.setPaint(Color.BLACK);
		
		l = new Line2D.Double(x1, y1, x2, y1);
		g2.draw(l);
		
		g2.setComposite(ac.derive(0.20f));
		
		l = new Line2D.Double(x1 - 1, y1 - 1, x1 - 1, y2 + 1);
		g2.draw(l);
		
		g2.setComposite(ac.derive(0.40f));
		
		l = new Line2D.Double(x1, y1, x1, y2);
		g2.draw(l);
		
		g2.setComposite(oldComposite);
		g2.setPaint(oldPaint);
	}
	
	public static void
	paintBorder(Graphics2D g2, double x1, double y1, double x2, double y2, double size) {
		paintBorder(g2, x1, y1, x2, y2, size, true);
	}
	
	public static void
	paintBorder (
		Graphics2D g2,
		double x1, double y1, double x2, double y2,
		double size,
		boolean paintInnerBorder
	) {
		Paint oldPaint = g2.getPaint();
		Composite oldComposite = g2.getComposite();
		
		Rectangle2D.Double rect = new Rectangle2D.Double(0, 0, x2 - x1 + 1, y2 - y1 + 1);
		Area area = new Area(rect);
		
		rect = new Rectangle2D.Double (
			x1 + size, y1 + size, x2 - x1 + 1 - (2 * size), y2 - y1 + 1 - (2 * size)
		);
		area.subtract(new Area(rect));
		
		GradientPaint gr = new GradientPaint (
			0.0f, (float)y1, FantasiaPainter.color5,
			0.0f, (float)y2, FantasiaPainter.color4
		);
		
		g2.setPaint(gr);
		g2.fill(area);
		
		if(paintInnerBorder) {
			paintInnerBorder (
				g2, x1 + size - 1, y1 + size - 1, x2 - size + 1, y2 - size + 1, true
			);
		}
		
		paintBoldOuterBorder(g2, x1, y1, x2, y2);
		
		g2.setPaint(oldPaint);
		g2.setComposite(oldComposite);
	}

	private static Color surface1Color1 = new Color(0x7a7a7a);
	private static Color surface1Color2 = new Color(0x5e5e5e);
	private static Color surface1Color3 = new Color(0x2e2e2e);

	/**
	 * Used to paint the MIDI keyboard
	 */
	public static void
	paintSurface1(JComponent c, Graphics g) {
		Graphics2D g2 = (Graphics2D)g;

		Paint oldPaint = g2.getPaint();
		Composite oldComposite = g2.getComposite();
		Object aa = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);

		Insets insets = c.getInsets();
		double x1 = insets.left;
		double y1 = insets.top;

		double w = c.getSize().getWidth();
		double x2 = w - insets.right - 1;
		double h = c.getSize().getHeight();
		double y2 = h - insets.bottom - 1;

		g2.setRenderingHint (
			RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF
		);

		FantasiaPainter.paintGradient(g2, x1, y1, x2, y2 - 10, surface1Color1, surface1Color2);

		double y3 = y2 - 10;
		if(y3 < 0) y3 = 0;

		Rectangle2D.Double rect = new Rectangle2D.Double(x1, y3, x2 - x1 + 1, 11);

		GradientPaint gr = new GradientPaint (
			0.0f, (float)y3, surface1Color2,
			0.0f, (float)h, surface1Color3
		);

		g2.setPaint(gr);
		g2.fill(rect);

		drawSurface1OutBorder(g2, x1, y1, x2, y2);

		g2.setPaint(oldPaint);
		g2.setComposite(oldComposite);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, aa);
	}

	private static void
	drawSurface1OutBorder(Graphics2D g2, double x1, double y1, double x2, double y2) {
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.40f);
		g2.setComposite(ac);

		g2.setPaint(Color.WHITE);
		Line2D.Double l = new Line2D.Double(x1, y1, x2, y1);
		g2.draw(l);

		g2.setComposite(ac.derive(0.20f));
		l = new Line2D.Double(x1, y1 + 1, x2, y1 + 1);
		g2.draw(l);

		g2.setComposite(ac.derive(0.255f));

		l = new Line2D.Double(x1, y1, x1, y2);
		g2.draw(l);

		g2.setComposite(ac.derive(0.40f));
		g2.setPaint(Color.BLACK);

		//l = new Line2D.Double(x1, y2, x2, y2);
		//g2.draw(l);

		g2.setComposite(ac.derive(0.20f));

		l = new Line2D.Double(x2, y1, x2, y2);
		g2.draw(l);
	}
}
