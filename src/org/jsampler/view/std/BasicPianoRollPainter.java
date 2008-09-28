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

package org.jsampler.view.std;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import java.awt.geom.RoundRectangle2D;

/**
 *
 * @author Grigor Iliev
 */
public class BasicPianoRollPainter implements PianoRollPainter {
	private PianoRoll pianoRoll;
	
	private Color borderColor = Color.BLACK;
	private Color keyColor = Color.WHITE;
	private Color disabledKeyColor = new Color(0xaaaaaa);
	private Color pressedKeyColor = Color.GREEN;
	private Color keySwitchColor = Color.PINK;
	private Color blackKeyColor = Color.BLACK;
	
	public
	BasicPianoRollPainter(PianoRoll pianoRoll) {
		if(pianoRoll == null) {
			throw new IllegalArgumentException("piano roll must be non-null");
		}
		this.pianoRoll = pianoRoll;
	}
	
	@Override
	public void
	paint(PianoRoll pianoRoll, Graphics2D g) {
		double whiteKeyWidth = getWhiteKeyWidth();
		double whiteKeyHeight = getWhiteKeyHeight();
		double arcw = whiteKeyWidth/4.0d;
		double arch = whiteKeyHeight/14.0d;
		
		g.setPaint(keyColor);
		
		RoundRectangle2D.Double rect;
		
		g.setRenderingHint (
			RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF
		);
		
		int i = 0;
		for(int j = pianoRoll.getFirstKey(); j <= pianoRoll.getLastKey(); j++) {
			if(!PianoRoll.isWhiteKey(j)) continue;
			
			Color c = getKeyColor(j);
			if(g.getPaint() != c) g.setPaint(c);
			
			rect = new RoundRectangle2D.Double (
				// If you change this you should also change getKeyRectangle()
				whiteKeyWidth * i + i, 0,
				whiteKeyWidth, whiteKeyHeight,
				arcw, arch
			);
			
			g.fill(rect);
			i++;
		}
		
		g.setRenderingHint (
			RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON
		);
		
		g.setStroke(new java.awt.BasicStroke(1.5f));
		g.setPaint(borderColor);
		
		i = 0;
		for(int j = pianoRoll.getFirstKey(); j <= pianoRoll.getLastKey(); j++) {
			if(!PianoRoll.isWhiteKey(j)) continue;
			
			rect = new RoundRectangle2D.Double (
				whiteKeyWidth * i + i, 0,
				whiteKeyWidth, whiteKeyHeight,
				arcw, arch
			);
		
			g.draw(rect);
			i++;
		}
		
		
		g.setStroke(new java.awt.BasicStroke(2.5f));
		double blackKeyWidth = getBlackKeyWidth();
		double blackKeyHeight = getBlackKeyHeight();
		
		i = 0;
		for(int j = pianoRoll.getFirstKey(); j <= pianoRoll.getLastKey(); j++) {
			if(pianoRoll.getOctaveLabelsVisible() && j % 12 == 0) {
				int octave = j / 12 - 2;
				paintOctaveLabel(g, octave, i);
			}
			if(!PianoRoll.isWhiteKey(j)) continue;
			
			int k = (i + PianoRoll.getKeyOctaveIndex(pianoRoll.getFirstKey())) % 7;
			if(k == 2 || k == 6) { i++; continue; }
			
			Color c = (j == pianoRoll.getLastKey()) ? blackKeyColor : getKeyColor(j + 1);
			if(g.getPaint() != c) g.setPaint(c);
			
			// If you change this you should also change getKeyRectangle()
			double x = blackKeyWidth * (2*(i + 1)) - blackKeyWidth * 0.5d + i;
			rect = new RoundRectangle2D.Double (
				// If you change this you should also change getKeyRectangle()
				x, 0,
				blackKeyWidth, blackKeyHeight,
				arcw, arch
			);
			///////
			
			boolean pressed = (j == pianoRoll.getLastKey()) ? false : pianoRoll.getKey(j + 1).isPressed();
			if(!pressed) g.fill(rect);
			
			RoundRectangle2D.Double rect2;
			rect2 = new RoundRectangle2D.Double (
				x, 0,
				blackKeyWidth, arch,
				arcw, arch / 1.8d
			);
			
			g.fill(rect2);
			g.setPaint(borderColor);
			g.draw(rect);
			
			if(pressed) {
				GradientPaint gr = new GradientPaint (
					(float)(x + blackKeyWidth/2), (float)(blackKeyHeight/4), Color.BLACK,
					(float)(x + blackKeyWidth/2), (float)blackKeyHeight, new Color(0x058a02)
				);
				g.setPaint(gr);
				g.fill(rect);
			}
			i++;
		}
	}
	
	protected void
	paintOctaveLabel(Graphics2D g, int octave, int whiteKeyIndex) {
		double h = pianoRoll.getSize().getHeight();
		double whiteKeyWidth = getWhiteKeyWidth();
		g.setPaint(Color.BLACK);
		int fsize = (int) (whiteKeyWidth / (1.5 + whiteKeyWidth / 50));
		if(fsize < 8) fsize = 8;
		g.setFont(g.getFont().deriveFont(Font.BOLD, fsize));
		
		float x = (float) (whiteKeyWidth * whiteKeyIndex + whiteKeyIndex);
		float y = (float) (h - 1);
		
		String s = String.valueOf(octave);
		FontMetrics fm = g.getFontMetrics();
		
		// center text
		int i = fm.stringWidth(s);
		if(i < whiteKeyWidth) {
			x += (whiteKeyWidth - i) / 2;
		} else {
			x += 2;
		}
		
		y -= (h / 12);
		
		g.drawString(s, x, y);
	}
	
	private Color
	getKeyColor(int key) {
		PianoRoll.Key k = pianoRoll.getKey(key);
		if(PianoRoll.isWhiteKey(key)) {
			if(k.isPressed()) return pressedKeyColor;
			if(k.isKeyswitch()) return keySwitchColor;
			if(k.isDisabled()) return disabledKeyColor;
			return keyColor;
		} else {
			if(k.isPressed()) return Color.GREEN;
			return blackKeyColor;
		}
	}
	
	
	private double
	getWhiteKeyWidth() {
		double w = pianoRoll.getSize().getWidth();
		return (w - pianoRoll.getWhiteKeyCount()) / pianoRoll.getWhiteKeyCount();
	}
	
	private double
	getWhiteKeyHeight() {
		return pianoRoll.getSize().getHeight() - 3.0d;
	}
	
	private double
	getBlackKeyWidth() {
		return getWhiteKeyWidth() / 2.0d;
	}
	
	private double
	getBlackKeyHeight() {
		return getWhiteKeyHeight() / 1.5d;
	}
	
	/**
	 * Gets the index of the key containing the specified point.
	 * @return Number between 0 and 127 (inclusive) as specified in the MIDI standard.
	 */
	@Override
	public int
	getKeyByPoint(Point p) {
		double w = getWhiteKeyWidth() + /* space between keys */ 1.0d;
		if(w == 0) return -1;
		int whiteKeyNumber = (int) (p.getX() / w);
		double leftBorder = whiteKeyNumber * w;
		int key = pianoRoll.getWhiteKeyByNumber(whiteKeyNumber);
		if(key == -1) return -1;
		
		double bh = getBlackKeyHeight();
		double blackKeyOffset = w / 4.0d;
		if(p.getY() > bh) return key;
		if(key != pianoRoll.getFirstKey() && !PianoRoll.isWhiteKey(key - 1)) {
			if(p.getX() <= leftBorder + blackKeyOffset) return key - 1;
		}
		if(key != pianoRoll.getLastKey() && !PianoRoll.isWhiteKey(key + 1)) {
			if(p.getX() >= leftBorder + 3 * blackKeyOffset - 3) return key + 1;
		}
		
		return key;
	}
	
	@Override
	public Rectangle
	getKeyRectangle(int key) {
		Rectangle r = new Rectangle();
		if(!pianoRoll.hasKey(key)) return r;
		
		int whiteKeyIndex = PianoRoll.getWhiteKeyCount(pianoRoll.getFirstKey(), key) - 1;
		
		if(PianoRoll.isWhiteKey(key)) {
			double whiteKeyWidth = getWhiteKeyWidth();
			double whiteKeyHeight = getWhiteKeyHeight();
			double x = whiteKeyWidth * whiteKeyIndex + whiteKeyIndex;
			r.setRect(x, 0, whiteKeyWidth, whiteKeyHeight);
		} else {
			double blackKeyWidth = getBlackKeyWidth();
			double blackKeyHeight = getBlackKeyHeight();
			int i = whiteKeyIndex;
			double x = blackKeyWidth * (2*(i + 1)) - blackKeyWidth * 0.5d + i;
			r.setRect(x, 0, blackKeyWidth, blackKeyHeight);
		}
		
		return r;
	}
	
	@Override
	public int
	getVelocity(Point p, int key) {
		boolean whiteKey = PianoRoll.isWhiteKey(key);
		double h = whiteKey ? getWhiteKeyHeight() : getBlackKeyHeight();
		int velocity = (int) ((p.getY() / h) * 127.0d + 1);
		if(velocity < 0) velocity = 0;
		if(velocity > 127) velocity = 127;
		return velocity;
	}
}
