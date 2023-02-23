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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.ButtonModel;
import javax.swing.DefaultButtonModel;
import javax.swing.JSlider;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import javax.swing.plaf.basic.BasicSliderUI;

/**
 *
 * @author Grigor Iliev
 */
public class FantasiaFaderUI extends BasicSliderUI implements Trackable {
	private ButtonModel knobModel = new DefaultButtonModel();
	private RolloverControlListener rolloverListener =
		new RolloverControlListener(this, knobModel);
	
	public
	FantasiaFaderUI(JSlider slider) {
		super(slider);
		slider.setOpaque(false);
	}
	
	@Override
	protected void
	installListeners(JSlider slider) {
		super.installListeners(slider);
		
		rolloverListener = new RolloverControlListener(this, knobModel);
		slider.addMouseListener(rolloverListener);
		slider.addMouseMotionListener(rolloverListener);
		
		knobModel.addChangeListener(getHandler());
	}
	
	@Override
	protected void
	uninstallListeners(JSlider slider) {
		super.uninstallListeners(slider);
		slider.removeMouseListener(rolloverListener);
		slider.removeMouseMotionListener(rolloverListener);
		rolloverListener = null;
		
		knobModel.removeChangeListener(getHandler());
	}
	
	@Override
	public void
	paintTrack(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		
		if(slider.getOrientation() == JSlider.HORIZONTAL) {
			int cy = (trackRect.height / 2) - 3;
			int cw = trackRect.width;
			
			g.translate(trackRect.x, trackRect.y + cy);
			
			Rectangle2D.Double rect = new Rectangle2D.Double(0, 0, cw - 1, 3);
			g2.setPaint(new Color(0x4b4b4b));
			g2.fill(rect);
			
			FantasiaPainter.paintBoldInnerBorder(g2, 0, 0, cw - 1, 3);
			
			g.translate(-trackRect.x, -(trackRect.y + cy));
			
			
		} else {
			int cx = (trackRect.width / 2) - 2;
			int ch = trackRect.height;
			
			g.translate(trackRect.x + cx, trackRect.y);
			
			Rectangle2D.Double rect = new Rectangle2D.Double(0, 0, 3, ch - 1);
			g2.setPaint(new Color(0x4b4b4b));
			g2.fill(rect);
			
			FantasiaPainter.paintBoldInnerBorder(g2, 0, 0, 3, ch - 1);
			
			g.translate(-(trackRect.x + cx), -trackRect.y);
			
			
		}
		
	}
	
	Color c1 = new Color(0x888888);
	Color c2 = new Color(0x555555);
	Color c3 = new Color(0xf5f5f5);
	Color c4 = new Color(0.0f, 0.0f, 0.0f, 0.10f);
	Color c6 = new Color(0.0f, 0.0f, 0.0f, 0.50f);
	Color c8 = new Color(0.0f, 0.0f, 0.0f, 0.78f);
	
	Color c12 = new Color(1.0f, 1.0f, 1.0f, 0.02f);
	Color c14 = new Color(1.0f, 1.0f, 1.0f, 0.22f);
	Color c16 = new Color(1.0f, 1.0f, 1.0f, 0.50f);
	Color c18 = new Color(1.0f, 1.0f, 1.0f, 0.78f);
	
	public void
	paintHorizontalLine(Graphics2D g2, double cy, double x1, double x2, Color c) {
		float r = c.getRed();
		r /= 255;
		float g = c.getGreen();
		g /= 255;
		float b = c.getBlue();
		b /= 255;
		
		GradientPaint gr = new GradientPaint (
			(float)x1, (float)cy, new Color(r, g, b, 0.40f),
			(float)x1 + 3, (float)cy, c
		);
		
		Line2D.Double l;
		l = new Line2D.Double(x1, cy, x1 + 3, cy);
		
		g2.setPaint(gr);
		g2.draw(l);
		
		g2.setPaint(c);
		l = new Line2D.Double(x1 + 4, cy, x2 - 5, cy);
		g2.draw(l);
		
		gr = new GradientPaint (
			(float)x2 - 4, (float)cy, c,
			(float)x2, (float)cy, new Color(r, g, b, 0.10f)
		);
		
		l = new Line2D.Double(x2 - 4, cy, x2, cy);
		g2.setPaint(gr);
		g2.draw(l);
	}
	
	public void
	paintVerticalLine(Graphics2D g2, double cx, double y1, double y2, Color c) {
		float r = c.getRed();
		r /= 255;
		float g = c.getGreen();
		g /= 255;
		float b = c.getBlue();
		b /= 255;
		
		GradientPaint gr = new GradientPaint (
			(float)cx, (float)y1, new Color(r, g, b, 0.40f),
			(float)cx, (float)y1 + 3, c
		);
		
		Line2D.Double l = new Line2D.Double(cx, y1, cx, y1 + 3);
		g2.setPaint(gr);
		g2.draw(l);
		
		g2.setPaint(c);
		l = new Line2D.Double(cx, y1 + 4, cx, y2 - 7);
		g2.draw(l);
		
		gr = new GradientPaint (
			(float)cx, (float)y2 - 6, c,
			(float)cx, (float)y2, new Color(r, g, b, 0.00f)
		);
		
		l = new Line2D.Double(cx, y2 - 6, cx, y2);
		g2.setPaint(gr);
		g2.draw(l);
	}
	
	@Override
	public void
	paintThumb(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		
		g2.setRenderingHint (
			RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON
		);
		
		double h = thumbRect.getHeight();
		double w = thumbRect.getWidth();
		
		double x1 = thumbRect.x + 2;
		double y1 = thumbRect.y + 1;
		double x2 = thumbRect.x + w - 3;
		double y2 = thumbRect.y + h - 5;
		
		// body
		
		RoundRectangle2D.Double rect = new RoundRectangle2D.Double (
			x1, y1, x2 - x1 + 1, y2 - y1 + 1, 8, 8
		);
		
		Color color = knobModel.isRollover() ? new Color(0x999999) : c1;
		 if(knobModel.isPressed()) color = new Color(0x777777);
		GradientPaint gr = new GradientPaint (
			(float)x1, (float)y1, color,
			(float)x1, (float)y2, c2
		);
		
		g2.setPaint(gr);
		g2.fill(rect);
		
		//border
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.50f);
		g2.setComposite(ac);
		g2.setPaint(Color.BLACK);
		
		rect = new RoundRectangle2D.Double (
			x1 - 1, y1 - 1, x2 - x1 + 2, y2 - y1 + 1, 6, 6
		);
		g2.draw(rect);
		
		g2.setComposite(ac.derive(1.0f));
		gr = new GradientPaint (
			(float)x1, (float)y1 + 1, c14, (float)x1, (float)y1 + 3, c12
		);
		
		g2.setPaint(gr);
		
		Arc2D.Double arc = new Arc2D.Double(x1, y1, x2 - x1, 4, 0, 180, Arc2D.OPEN);
		//g2.setPaint(Color.WHITE);
		g2.draw(arc);
		///////
		
		
		// Shadow down
		gr = new GradientPaint (
			(float)x1, (float)y2 - 4, c6, (float)x1, (float)y2 + 4, c4
		);
		
		g2.setPaint(gr);
		
		g2.setComposite(ac.derive(0.70f));
		arc = new Arc2D.Double(x1 - 1, y2 - 4, x2 - x1 + 3, 7, 180, 180, Arc2D.PIE);
		g2.fill(arc);
		
		g2.setPaint(Color.BLACK);
		g2.setComposite(ac.derive(0.07f));
		arc = new Arc2D.Double(x1 - 1, y2 - 3, x2 - x1 + 2, 5, 180, 180, Arc2D.OPEN);
		g2.draw(arc);
		
		g2.setPaint(Color.BLACK);
		g2.setComposite(ac.derive(0.20f));
		Line2D.Double l = new Line2D.Double(x1 + 3, y2 + 1, x2 - 3, y2 + 1);
		g2.draw(l); // right
		///////
		
		
		
		g2.setPaint(c3);
		g2.setComposite(ac.derive(0.06f));
		l = new Line2D.Double(x1 + 6, y1 - 1, x1 + 8, y1 - 1);
		g2.draw(l);
		
		
		
		if(slider.getOrientation() == JSlider.HORIZONTAL) {
			double cx = (int)thumbRect.x + w / 2;
			
			// center line
			g2.setComposite(ac.derive(1.0f));
			paintVerticalLine(g2, cx, y1, y2, c3);
			
			// center down line
			g2.setComposite(ac.derive(0.30f));
			paintVerticalLine(g2, cx - 1, y1, y2, Color.BLACK);
			///
			
			// center up line
			g2.setPaint(Color.WHITE);
			g2.setComposite(ac.derive(0.10f));
			paintVerticalLine(g2, cx + 1, y1, y2, Color.WHITE);
		} else {
			double cy = (int) thumbRect.y + h / 2 - 2;
			// center line
			g2.setComposite(ac.derive(1.0f));
			paintHorizontalLine(g2, cy, x1, x2, c3);
			
			// center down line
			g2.setComposite(ac.derive(0.30f));
			paintHorizontalLine(g2, cy - 1, x1, x2, Color.BLACK);
			///
			
			// center up line
			g2.setPaint(Color.WHITE);
			g2.setComposite(ac.derive(0.10f));
			paintHorizontalLine(g2, cy + 1, x1, x2, Color.WHITE);
			///
		}
		
		// border shadow
		g2.setPaint(Color.BLACK);
		g2.setComposite(ac.derive(0.10f));
		l = new Line2D.Double(x2, y1 + 1, x2, y2 - 2);
		g2.draw(l); // right
		
		
		g2.setComposite(ac.derive(0.06f));
		l = new Line2D.Double(x1 - 2, y1 + 2, x1 - 2, y2 - 2);
		g2.draw(l);// left
		
		l = new Line2D.Double(x2 + 2, y1 + 2, x2 + 2, y2 - 2);
		g2.draw(l); // right
		///
	}
	
	@Override
	public boolean
	isInside(MouseEvent e) {
		if(thumbRect == null) return false;
		return thumbRect.contains(e.getX(), e.getY());
	}
	
	private final Handler handler = new Handler();
	
	private Handler
	getHandler() { return handler; }
	
	private class Handler implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent e) {
			slider.repaint(thumbRect);
		}
		
	}	
	
	
	@Override
	protected Dimension
	getThumbSize() {
		Dimension d = (Dimension)slider.getClientProperty("Fader.knobSize");
		if(d != null) return d;
		return slider.getOrientation() == JSlider.VERTICAL ?
			new Dimension(27, 20) : new Dimension(17, 27);
	}
}



/*
 * Copyright (c) 2005-2009 Substance Kirill Grouchnikov. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of Substance Kirill Grouchnikov nor the names of
 *    its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/** @author Kirill Grouchnikov */
interface Trackable { public boolean isInside(MouseEvent me); }

/**
 * Control listener for rollover effects. Tracks the mouse motion interaction
 * for the associated {@link org.jvnet.substance.utils.Trackable} control. This
 * class is <b>for internal use only</b>.
 *
 * @author Kirill Grouchnikov
 */
class RolloverControlListener implements MouseListener,
		MouseMotionListener {
	/**
	 * If the mouse pointer is currently inside the designated area (fetched
	 * from the associated {@link #trackableUI}), <code>this</code> flag is
	 * <code>true</code>.
	 */
	private boolean isMouseInside;

	/**
	 * Surrogate model for tracking control status.
	 */
	private ButtonModel model;

	/**
	 * Object that is queried for mouse events. This object is responsible for
	 * handling the designated (hot-spot) area of the associated control.
	 */
	private Trackable trackableUI;

	/**
	 * Simple constructor.
	 *
	 * @param trackableUI
	 *            Object that is queried for mouse events.
	 * @param model
	 *            Surrogate model for tracking control status.
	 */
	public RolloverControlListener(Trackable trackableUI, ButtonModel model) {
		this.trackableUI = trackableUI;
		this.model = model;
		this.isMouseInside = false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent e) {
		Component component = (Component) e.getSource();
		if (!component.isEnabled())
			return;
		boolean isInside = this.trackableUI.isInside(e);
		// boolean isInsideChanged = (this.isMouseInside != isInside);
		this.isMouseInside = isInside;
		this.model.setRollover(isInside);
		this.model.setEnabled(component.isEnabled());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent e) {
		Component component = (Component) e.getSource();
		if (!component.isEnabled())
			return;
		// boolean isInsideChanged = (this.isMouseInside != false);
		this.isMouseInside = false;
		this.model.setRollover(false);
		this.model.setEnabled(component.isEnabled());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent e) {
		// System.out.println("mouse released [" + e.getX() + ":" + e.getY() +
		// "]");
		Component component = (Component) e.getSource();
		if (!component.isEnabled())
			return;
		boolean isInside = this.trackableUI.isInside(e);
		// boolean isInsideChanged = (this.isMouseInside != isInside);
		this.isMouseInside = isInside;
		this.model.setRollover(this.isMouseInside);
		this.model.setPressed(false);
		this.model.setArmed(false);
		this.model.setSelected(false);
		this.model.setEnabled(component.isEnabled());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent e) {
		// System.out.println("mouse pressed [" + e.getX() + ":" + e.getY() +
		// "]");
		Component component = (Component) e.getSource();
		if (!component.isEnabled())
			return;
		boolean isInside = this.trackableUI.isInside(e);
		// boolean isInsideChanged = (this.isMouseInside != isInside);
		this.isMouseInside = isInside;
		this.model.setRollover(this.isMouseInside);
		if (this.isMouseInside) {
			this.model.setPressed(true);
			this.model.setArmed(true);
			this.model.setSelected(true);
		}
		this.model.setEnabled(component.isEnabled());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent
	 * )
	 */
	public void mouseDragged(MouseEvent e) {
		// System.out.println("mouse dragged [" + e.getX() + ":" + e.getY() +
		// "]");
		Component component = (Component) e.getSource();
		if (!component.isEnabled())
			return;
		boolean isInside = this.trackableUI.isInside(e);
		// boolean isInsideChanged = (this.isMouseInside != isInside);
		this.isMouseInside = isInside;
		this.model.setEnabled(component.isEnabled());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved(MouseEvent e) {
		// System.out.println("mouse moved [" + e.getX() + ":" + e.getY() +
		// "]");
		Component component = (Component) e.getSource();
		if (!component.isEnabled())
			return;
		boolean isInside = this.trackableUI.isInside(e);
		// System.out.println("inside");
		// boolean isInsideChanged = (this.isMouseInside != isInside);
		this.isMouseInside = isInside;
		this.model.setRollover(isInside);
		this.model.setEnabled(component.isEnabled());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent e) {
	}
}
