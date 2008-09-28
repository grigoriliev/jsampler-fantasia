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

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;


/**
 *
 * @author Grigor Iliev
 */
public interface PianoRollPainter {
	public void paint(PianoRoll pianoRoll, Graphics2D g);
	
	/**
	 * Gets the index of the key containing the specified point.
	 * @return Number between 0 and 127 (inclusive) as specified in the MIDI standard.
	 */
	public int getKeyByPoint(Point p);
	
	/**
	 * Gets the rectangle in which the key is drawn and empty rectangle
	 * if the specified key is not shown on the piano roll or is invalid.
	 * Used to optimize the repainting process.
	 */
	public Rectangle getKeyRectangle(int key);
	
	/**
	 * Gets the velocity of the specified key based on the specified point.
	 */
	public int getVelocity(Point p, int key);
}
