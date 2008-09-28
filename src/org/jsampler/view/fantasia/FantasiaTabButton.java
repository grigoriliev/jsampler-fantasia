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

package org.jsampler.view.fantasia;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;
import javax.swing.JToggleButton;

import javax.swing.plaf.basic.BasicButtonUI;


/**
 *
 * @author Grigor Iliev
 */
public class FantasiaTabButton extends JToggleButton {
	private FantasiaTabbedPane tabbedPane;
	int index = 0;
	
	public
	FantasiaTabButton(FantasiaTabbedPane owner) {
		this(owner, "");
	}
	
	public
	FantasiaTabButton(FantasiaTabbedPane owner, String s) {
		super(s);
		
		tabbedPane = owner;
		setFocusable(false);
		setContentAreaFilled(false);
		setFocusPainted(false);
		setBorder(BorderFactory.createEmptyBorder(5, 3, 5, 3));
		setRolloverEnabled(true);
		setForeground(new Color(0xcccccc));
		//setHorizontalAlignment(LEFT);
	}
	
	public int
	getIndex() { return index; }
	
	public void
	setIndex(int idx) { index = idx; }
	
	public FantasiaTabbedPane
	getTabbedPane() { return tabbedPane; }
	
	private Color color3 = new Color(0x5e5e5e);
	private Color color4 = new Color(0x6e6e6e);
	private Color color5 = new Color(0x7a7a7a);
	private Color color6 = new Color(0x8a8a8a);
	
	@Override
	protected void
	paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		
		Paint oldPaint = g2.getPaint();
		Composite oldComposite = g2.getComposite();
		
		g2.setRenderingHint (
			RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF
		);
		
		if(isSelected()) paintSelectedButton(g2);
		else paintUnselectedButton(g2);
		
		g2.setPaint(oldPaint);
		g2.setComposite(oldComposite);
		
		g2.setRenderingHint (
			RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON
		);
		
		super.paintComponent(g);
	}
	
	private void
	paintSelectedButton(Graphics2D g2) {
		double h = getSize().getHeight();
		double w = getSize().getWidth();
		
		double x1 = 0.0;
		double y2 = getModel().isSelected() ? h : h - 3;
		double x2 = isLastButton() ? w : w - 1;
		
		Rectangle2D.Double rect = new Rectangle2D.Double(x1, 0.0, x2, y2);
		
		GradientPaint gr = new GradientPaint (
			(float)x1, 0.0f, color6,
			(float)x1, (float)y2, color5
		);
		
		g2.setPaint(gr);
		g2.fill(rect);
		
		paintSelectedBorder(g2, x1, 0, x2 - 1, h - 1);
	}
	
	private void
	paintUnselectedButton(Graphics2D g2) {
		double h = getSize().getHeight();
		double w = getSize().getWidth();
		
		double x1 = 0.0;
		double y1 = 1.0;
		double y2 = h - 2;
		double x2 = isLastButton() ? w : w - 1;
		
		Rectangle2D.Double rect = new Rectangle2D.Double(x1, y1, x2, y2);
		
		Color c1 = getModel().isRollover() ? color4 : color3;
		Color c2 = getModel().isRollover() ? color6 : color5;
		
		GradientPaint gr = new GradientPaint (
			(float)x1, (float)y1, c1,
			(float)x1, (float)y2, c2
		);
		
		g2.setPaint(gr);
		g2.fill(rect);
		
		// draw the bottom component
		rect = new Rectangle2D.Double(0.0, h - 2, w, y2);
		g2.setPaint(color5);
		g2.fill(rect);
		
		paintUnselectedBorder(g2, x1, y1, x2 - 1, h);
	}
	
	private void
	paintSelectedBorder(Graphics2D g2, double x1, double y1, double x2, double y2) {
		FantasiaPainter.paintTopBoldOuterBorder(g2, x1 + 2, y1, x2 - 2, y1);
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.40f);
		
		FantasiaPainter.paintTopBoldRoundCorners(g2, x1, y1, x2, y2);
		
		g2.setComposite(ac.derive(0.255f));
		
		y2 -= 1;
		
		double y3 = getIndex() == 0 ? y2 + 1 : y2;
		Line2D.Double l = new Line2D.Double(x1, y1 + 2, x1, y3);
		g2.draw(l);
		
		if(getIndex() == 0) {
			g2.setComposite(ac.derive(0.07f));
			l = new Line2D.Double(x1 + 1, y1 + 3, x1 + 1, y3);
			g2.draw(l);
		}
		
		g2.setComposite(ac.derive(0.40f));
		g2.setPaint(Color.BLACK);
		
		g2.setComposite(ac.derive(0.20f));
		
		y3 = getIndex() == getTabbedPane().getTabCount() - 1 ? y2 + 1 : y2;
		l = new Line2D.Double(x2, y1 + 2, x2, y3);
		g2.draw(l);
		
		if(getIndex() == getTabbedPane().getTabCount() - 1) {
			g2.setComposite(ac.derive(0.07f));
			l = new Line2D.Double(x2 - 1, y1 + 3, x2 - 1, y3);
			g2.draw(l);
		}
		
		// draw bottom component right border
		if(getIndex() != getTabbedPane().getTabCount() - 1) {
			g2.setPaint(color5);
			g2.setComposite(ac.derive(1.0f));
			l = new Line2D.Double(x2 + 1, y2, x2 + 1, y2 + 1);
			g2.draw(l);
			
			g2.setPaint(Color.WHITE);
			
			g2.setComposite(ac.derive(0.10f));
			l = new Line2D.Double(x2, y2, x2, y2);
			g2.draw(l);
			
			g2.setComposite(ac.derive(0.20f));
			l = new Line2D.Double(x2 + 1, y2, x2 + 1, y2);
			g2.draw(l);
			
			// draw bottom component border
			g2.setComposite(ac.derive(0.10f));
			l = new Line2D.Double(x2 + 1, y2 + 1, x2 + 1, y2 + 1);
			g2.draw(l);
		}
	}
	
	public boolean
	isLastButton() { return getIndex() == getTabbedPane().getTabCount() - 1; }
	
	private void
	paintUnselectedBorder(Graphics2D g2, double x1, double y1, double x2, double y2) {
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.40f);
		g2.setComposite(ac);
		
		g2.setPaint(Color.WHITE);
		Line2D.Double l = new Line2D.Double(x1 + 2, y1, x2 - 3, y1);
		g2.draw(l);
		
		double h = getSize().getHeight();
		
		// draw bottom component border
		double x0 = getIndex() == 0 ? 2 : 0;
		double x3 = isLastButton() ? x2 - 3 : x2 + 1;
		l = new Line2D.Double(x0, h - 2, x3, h - 2);
		g2.draw(l);
		
		g2.setComposite(ac.derive(0.20f));
		l = new Line2D.Double(x1, y1 + 1, x2 - 2, y1 + 1);
		g2.draw(l);
		
		// draw bottom component border
		l = new Line2D.Double(x0, h - 1, x3, h - 1);
		g2.draw(l);
		
		paintRoundCorners(g2, x1, y1, x2, y2);
		
		if(getIndex() == 0) {
			FantasiaPainter.paintTopLeftBoldRoundCorner(g2, 0.0, h - 2);
		} else if(isLastButton()) {
			FantasiaPainter.paintTopRightBoldRoundCorner(g2, y2 - 2, x2);
		}
		g2.setComposite(ac.derive(0.255f));
		
		y2 -= 3;
		
		l = new Line2D.Double(x1, y1 + 2, x1, y2);
		g2.draw(l);
		
		g2.setComposite(ac.derive(0.30f));
		g2.setPaint(Color.BLACK);
		
		l = new Line2D.Double(x1, y2, x2, y2);
		g2.draw(l);
		
		g2.setComposite(ac.derive(0.20f));
		
		l = new Line2D.Double(x2, y1 + 2, x2, y2);
		g2.draw(l);
	}
	
	private void
	paintRoundCorners(Graphics2D g2, double x1, double y1, double x2, double y2) {
		Paint oldPaint = g2.getPaint();
		
		g2.setPaint(Color.WHITE);
		
		// Round corner - left
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.10f);
		g2.setComposite(ac);
		Line2D.Double l = new Line2D.Double(x1, y1, x1, y1);
		g2.draw(l);
		
		g2.setPaint(Color.WHITE);
		g2.setComposite(ac.derive(0.37f));
		l = new Line2D.Double(x1 + 1, y1, x1 + 1, y1);
		g2.draw(l);
		
		g2.setComposite(ac.derive(0.15f));
		l = new Line2D.Double(x1, y1 + 1, x1, y1 + 1);
		g2.draw(l);
		
		g2.setComposite(ac.derive(0.20f));
		l = new Line2D.Double(x1 + 1, y1 + 1, x1 + 1, y1 + 1);
		g2.draw(l);
		
		g2.setComposite(ac.derive(0.10f));
		l = new Line2D.Double(x1, y1 + 2, x1 + 1, y1 + 2);
		g2.draw(l);
		// Round corner - right
		g2.setPaint(Color.WHITE);
		g2.setComposite(ac.derive(0.30f));
		l = new Line2D.Double(x2 - 2, y1, x2 - 2, y1);
		g2.draw(l);
		
		g2.setComposite(ac.derive(0.20f));
		l = new Line2D.Double(x2 - 1, y1, x2 - 1, y1);
		g2.draw(l);
		
		g2.setPaint(Color.BLACK);
		g2.setComposite(ac.derive(0.10f));
		l = new Line2D.Double(x2, y1, x2, y1);
		g2.draw(l);
		
		g2.setPaint(Color.WHITE);
		l = new Line2D.Double(x2 - 1, y1 + 1, x2 - 1, y1 + 1);
		g2.draw(l);
		
		g2.setPaint(Color.BLACK);
		g2.setComposite(ac.derive(0.05f));
		l = new Line2D.Double(x2, y1 + 1, x2, y1 + 1);
		g2.draw(l);
		
		g2.setPaint(oldPaint);
	}
	
	public void
	updateUI() { setUI(new BasicButtonUI()); }
}
