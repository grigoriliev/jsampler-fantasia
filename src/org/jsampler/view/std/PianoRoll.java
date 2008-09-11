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

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

import java.awt.geom.RoundRectangle2D;

import java.util.Vector;

import javax.swing.JPanel;

import org.linuxsampler.lscp.event.MidiDataEvent;
import org.linuxsampler.lscp.event.MidiDataListener;

/**
 *
 * @author Grigor Iliev
 */
public class PianoRoll extends JPanel implements MidiDataListener {
	public class Key {
		private boolean disabled = false;
		private boolean pressed = false;
		private boolean keyswitch = false;
		
		public
		Key() {
			
		}
		
		public boolean
		isDisabled() { return disabled; }
		
		public void
		setDisabled(boolean b) { disabled = b; }
		
		public boolean
		isPressed() { return pressed; }
		
		public void
		setPressed(boolean b) { pressed = b; }
		
		public boolean
		isKeyswitch() { return keyswitch; }
		
		public void
		setKeyswitch(boolean b) { keyswitch = b; }
		
	}
	
	
	private final Vector<Key> keys = new Vector<Key>();
	
	private boolean vertical;
	private boolean mirror;
	private boolean octaveLabelsVisible = true;
	private boolean playingEnabled = true;
	
	private int firstKey = -1;
	private int lastKey = -1;
	private int whiteKeyCount = 68;
	
	private Color borderColor = Color.BLACK;
	private Color keyColor = Color.WHITE;
	private Color disabledKeyColor = Color.GRAY;
	private Color pressedKeyColor = Color.GREEN;
	private Color keySwitchColor = Color.PINK;
	private Color blackKeyColor = Color.BLACK;
	
	private final Vector<MidiDataListener> listeners = new Vector<MidiDataListener>();
	
	private boolean shouldRepaint = false;
	
	/**
	 * Creates a new horizontal, not mirrored <code>PianoRoll</code>.
	 */
	public
	PianoRoll() { this(false); }
	
	/**
	 * Creates a new not mirrored <code>PianoRoll</code>.
	 * @param vertical Specifies whether the piano roll
	 * should be vertical or horizontal
	 */
	public
	PianoRoll(boolean vertical) { this(vertical, false); }
	
	/**
	 * Creates a new instance of <code>PianoRoll</code>.
	 * @param vertical Specifies whether the piano roll
	 * should be vertical or horizontal.
	 * @param mirror Specifies whether to mirror the piano roll.
	 */
	public
	PianoRoll(boolean vertical, boolean mirror) {
		this.vertical = vertical;
		this.mirror = mirror;
		
		this.addMouseListener(getHandler());
		setKeyRange(0, 127);
	}
	
	/**
	 * Registers the specified listener to be notified when
	 * MIDI event occurs due to user input.
	 * @param l The <code>MidiDataListener</code> to register.
	 */
	public void
	addMidiDataListener(MidiDataListener l) { listeners.add(l); }
	
	/**
	 * Removes the specified listener.
	 * @param l The <code>MidiDataListener</code> to remove.
	 */
	public void
	removeMidiDataListener(MidiDataListener l) { listeners.remove(l); }
	
	private void
	fireMidiDataEvent(MidiDataEvent e) {
		for(MidiDataListener l : listeners) l.midiDataArrived(e);
	}
	
	/**
	 * Determines whether the user input is processed
	 * and respective MIDI data events are sent.
	 */
	public boolean
	isPlayingEnabled() { return playingEnabled; }
	
	/**
	 * Sets whether the user input should be processed
	 * and respective MIDI data events should be sent.
	 * @see #isPlayingEnabled
	 */
	public void
	setPlayingEnabled(boolean b) { playingEnabled = b; }
	
	public boolean
	hasKey(int key) {
		if(key >= firstKey && key <= lastKey) return true;
		return false;
	}
	
	public Key
	getKey(int key) { return keys.get(key - firstKey); }
	
	public boolean
	isKeyDisabled(int key) {
		return getKey(key).isDisabled();
	}
	
	public void
	setKeyDisabled(int key, boolean disable) {
		Key k = getKey(key);
		if(k.isDisabled() == disable) return;
		
		getKey(key).setDisabled(disable);
		if(getShouldRepaint()) repaint();
	}
	
	public boolean
	isKeyPressed(int key) {
		return getKey(key).isPressed();
	}
	
	public void
	setKeyPressed(int key, boolean press) {
		Key k = getKey(key);
		if(k.isPressed() == press) return;
		
		getKey(key).setPressed(press);
		if(getShouldRepaint()) repaint(getKeyRectangle(key));
	}
	
	public boolean
	isKeyswitch(int key) {
		return getKey(key).isKeyswitch();
	}
	
	public void
	setKeyswitch(int key, boolean keyswitch) {
		Key k = getKey(key);
		if(k.isKeyswitch() == keyswitch) return;
		
		getKey(key).setKeyswitch(keyswitch);
		if(getShouldRepaint()) repaint(getKeyRectangle(key));
	}
	
	public void
	setAllKeysPressed(boolean b) {
		boolean changed = false;
		for(Key key : keys) {
			if(key.isPressed() != b) {
				key.setPressed(b);
				changed = true;
			}
		}
		
		if(changed && getShouldRepaint()) repaint();
	}
	
	public void
	setAllKeysDisabled(boolean b) {
		boolean changed = false;
		for(Key key : keys) {
			if(key.isDisabled() != b) {
				key.setDisabled(b);
				changed = true;
			}
		}
		
		if(changed && getShouldRepaint()) repaint();
	}
	
	public Integer[]
	getKeyswitches() {
		Vector<Integer> v = new Vector<Integer>();
		for(int i = 0; i < keys.size(); i++) {
			if(keys.get(i).isKeyswitch()) v.add(firstKey + i);
		}
		
		return v.toArray(new Integer[v.size()]);
	}
	
	/**
	 * Keys outside the keyboard range are ignored.
	 * @param keys List of keys
	 */
	public void
	setKeyswitches(Integer[] keys, boolean keyswitches) {
		boolean changed = false;
		for(int k : keys) {
			if(!hasKey(k)) continue;
			if(getKey(k).isKeyswitch() != keyswitches) {
				getKey(k).setKeyswitch(keyswitches);
				changed = true;
			}
		}
		if(changed && getShouldRepaint()) repaint();
	}
	
	public Integer[]
	getEnabledKeys() {
		Vector<Integer> v = new Vector<Integer>();
		for(int i = 0; i < keys.size(); i++) {
			if(!keys.get(i).isDisabled()) v.add(firstKey + i);
		}
		
		return v.toArray(new Integer[v.size()]);
	}
	
	/**
	 * Enables or disables the specified list of keys.
	 * Keys outside the keyboard range are ignored.
	 * @param keys List of keys
	 */
	public void
	setDisabled(Integer[] keys, boolean disable) {
		boolean changed = false;
		for(int k : keys) {
			if(!hasKey(k)) continue;
			if(getKey(k).isDisabled() != disable) {
				getKey(k).setDisabled(disable);
				changed = true;
			}
		}
		if(changed && getShouldRepaint()) repaint();
	}
	
	public void
	removeAllKeyswitches() {
		boolean changed = false;
		for(Key key : keys) {
			if(key.isKeyswitch()) {
				key.setKeyswitch(false);
				changed = true;
			}
		}
		if(changed && getShouldRepaint()) repaint();
	}
	
	/**
	 * Sets the piano key range which this piano roll will provide.
	 * Note that the specified last key is also included in the piano roll.
	 * Also, the first and the last key should be white keys. If the first key 
	 * and/or the last key are not white keys then the range is extended automatically.
	 * @param firstKey Number between 0 and 127 (inclusive) as specified in the MIDI standard.
	 * @param lastKey Number between 0 and 127 (inclusive) as specified in the MIDI standard.
	 * @throws IllegalArgumentException if the specified range is invalid.
	 */
	public void
	setKeyRange(int firstKey, int lastKey) {
		if(this.firstKey == firstKey && this.lastKey == lastKey) return;
		
		if(firstKey < 0 || firstKey > 127 || lastKey < 0 || lastKey > 127 || firstKey >= lastKey) {
			throw new IllegalArgumentException("Invalid range: " + firstKey + "-" + lastKey);
		}
		
		/*Integer[] enabledKeys = getEnabledKeys();
		Integer[] keyswitches = getKeyswitches();*/
		
		if(!isWhiteKey(firstKey)) firstKey--;
		if(!isWhiteKey(lastKey)) lastKey++;
		this.firstKey = firstKey;
		this.lastKey = lastKey;
		
		keys.removeAllElements();
		for(int i = 0; i <= lastKey - firstKey; i++) keys.add(new Key());
		
		whiteKeyCount = getWhiteKeyCount(firstKey, lastKey);
		
		/*setAllKeysDisabled(true);
		setDisabled(enabledKeys, false);
		setKeyswitches(keyswitches, true);*/
		
		if(getShouldRepaint()) repaint();
	}
	
	public boolean
	getOctaveLabelsVisible() {
		return octaveLabelsVisible;
	}
	
	public void
	setOctaveLabelsVisible(boolean b) {
		octaveLabelsVisible = b;
	}
	
	/**
	 * Gets the number of white keys int the specified range (inclusive).
	 * @see #setKeyRange
	 */
	private static int
	getWhiteKeyCount(int firstKey, int lastKey) {
		int count = 0;
		for(int j = firstKey; j <= lastKey; j++) { // FIXME: Stupid but works
			if(isWhiteKey(j)) count++;
		}
		
		return count;
	}
	
	private int
	getWhiteKeyByNumber(int whiteKey) {
		int count = 0;
		for(int j = firstKey; j <= lastKey; j++) { // FIXME: Stupid but works
			if(isWhiteKey(j)) {
				if(whiteKey == count) return j;
				count++;
			}
		}
		
		return -1;
	}
	
	private int
	getWhiteKeyCount() {
		return whiteKeyCount;
	}
	
	/**
	 * Determines whether the specified key is a white key.
	 * @param key Number between 0 and 127 (inclusive) as specified in the MIDI standard.
	 */
	public static boolean
	isWhiteKey(int key) {
		if(key < 0 || key > 127) return false;
		
		int k = key % 12;
		if(k == 1 || k == 3 || k == 6 || k == 8 || k == 10) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Returns the position (zero-based) of the specified white key in the octave.
	 * @param whiteKey Number between 0 and 127 (inclusive) as specified in the MIDI standard.
	 * @return Number between 0 and 6 (inclusive) and -1 if the
	 * specified key is not a white key.
	 */
	public static int
	getKeyOctaveIndex(int whiteKey) {
		if(whiteKey < 0 || whiteKey > 127) return -1;
		
		int k = whiteKey % 12;
		if(k == 1 || k == 3 || k == 6 || k == 8 || k == 10) {
			return -1;
		}
		
		return getWhiteKeyCount(0, k) - 1;
	}
	
	private Color
	getKeyColor(int key) {
		Key k = getKey(key);
		if(isWhiteKey(key)) {
			if(k.isPressed()) return pressedKeyColor;
			if(k.isKeyswitch()) return keySwitchColor;
			if(k.isDisabled()) return disabledKeyColor;
			return keyColor;
		} else {
			if(k.isPressed()) return Color.GREEN;
			return blackKeyColor;
		}
	}
	
	/**
	 * Releases all pressed keys, enables all keys and removes all keyswitches.
	 */
	public void
	reset() { reset(false); }
	
	/**
	 * Releases all pressed keys, enables/disables all keys and removes all keyswitches.
	 * @param dissable Specifies whether all keys should be enabled or disabled
	 */
	public void
	reset(boolean dissableAllKeys) {
		boolean b = getShouldRepaint();
		setShouldRepaint(false);
		setAllKeysPressed(false);
		removeAllKeyswitches();
		setAllKeysDisabled(dissableAllKeys);
		setShouldRepaint(b);
		if(getShouldRepaint()) repaint();
	}
	
	public boolean
	getShouldRepaint() { return shouldRepaint; }
	
	public void
	setShouldRepaint(boolean b) { shouldRepaint = b; }
	
	private int
	getKey(Point p) {
		double w = getWhiteKeyWidth() + /* space between keys */ 1.0d;
		if(w == 0) return -1;
		int whiteKeyNumber = (int) (p.getX() / w);
		double leftBorder = whiteKeyNumber * w;
		int key = getWhiteKeyByNumber(whiteKeyNumber);
		if(key == -1) return -1;
		
		double bh = getBlackKeyHeight();
		double blackKeyOffset = w / 4.0d;
		if(p.getY() > bh) return key;
		if(key != firstKey && !isWhiteKey(key - 1)) {
			if(p.getX() <= leftBorder + blackKeyOffset) return key - 1;
		}
		if(key != lastKey && !isWhiteKey(key + 1)) {
			if(p.getX() >= leftBorder + 3 * blackKeyOffset - 3) return key + 1;
		}
		
		return key;
	}
	
	private int
	getVelocity(Point p, boolean whiteKey) {
		double h = whiteKey ? getWhiteKeyHeight() : getBlackKeyHeight();
		int velocity = (int) ((p.getY() / h) * 127.0d + 1);
		if(velocity < 0) velocity = 0;
		if(velocity > 127) velocity = 127;
		return velocity;
	}
	
	private double
	getWhiteKeyWidth() {
		double w = getSize().getWidth();
		return (w - getWhiteKeyCount()) / getWhiteKeyCount();
	}
	
	private double
	getWhiteKeyHeight() {
		return getSize().getHeight() - 3.0d;
	}
	
	private double
	getBlackKeyWidth() {
		return getWhiteKeyWidth() / 2.0d;
	}
	
	private double
	getBlackKeyHeight() {
		return getWhiteKeyHeight() / 1.5d;
	}
	
	protected void
	paintOctaveLabel(Graphics2D g, int octave, int whiteKeyIndex) {
		double h = getSize().getHeight();
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
	
	/**
	 * Gets the rectangle in which the key is drawn and empty rectangle
	 * if the specified key is not shown on the piano roll or is invalid.
	 */
	public Rectangle
	getKeyRectangle(int key) {
		Rectangle r = new Rectangle();
		if(!hasKey(key)) return r;
		
		int whiteKeyIndex = getWhiteKeyCount(firstKey, key) - 1;
		
		if(isWhiteKey(key)) {
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
	
	@Override public void
	paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;
		
		double whiteKeyWidth = getWhiteKeyWidth();
		double whiteKeyHeight = getWhiteKeyHeight();
		double arcw = whiteKeyWidth/4.0d;
		double arch = whiteKeyHeight/14.0d;
		
		g2.setPaint(keyColor);
		
		RoundRectangle2D.Double rect;
		
		g2.setRenderingHint (
			RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF
		);
		
		int i = 0;
		for(int j = firstKey; j <= lastKey; j++) {
			if(!isWhiteKey(j)) continue;
			
			Color c = getKeyColor(j);
			if(g2.getPaint() != c) g2.setPaint(c);
			
			rect = new RoundRectangle2D.Double (
				// If you change this you should also change getKeyRectangle()
				whiteKeyWidth * i + i, 0,
				whiteKeyWidth, whiteKeyHeight,
				arcw, arch
			);
			
			g2.fill(rect);
			i++;
		}
		
		g2.setRenderingHint (
			RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON
		);
		
		g2.setStroke(new java.awt.BasicStroke(1.5f));
		g2.setPaint(borderColor);
		
		i = 0;
		for(int j = firstKey; j <= lastKey; j++) {
			if(!isWhiteKey(j)) continue;
			
			rect = new RoundRectangle2D.Double (
				whiteKeyWidth * i + i, 0,
				whiteKeyWidth, whiteKeyHeight,
				arcw, arch
			);
		
			g2.draw(rect);
			i++;
		}
		
		
		g2.setStroke(new java.awt.BasicStroke(2.5f));
		double blackKeyWidth = getBlackKeyWidth();
		double blackKeyHeight = getBlackKeyHeight();
		
		i = 0;
		for(int j = firstKey; j <= lastKey; j++) {
			if(getOctaveLabelsVisible() && j % 12 == 0) {
				int octave = j / 12 - 2;
				paintOctaveLabel(g2, octave, i);
			}
			if(!isWhiteKey(j)) continue;
			
			int k = (i + getKeyOctaveIndex(firstKey)) % 7;
			if(k == 2 || k == 6) { i++; continue; }
			
			Color c = (j == lastKey) ? blackKeyColor : getKeyColor(j + 1);
			if(g2.getPaint() != c) g2.setPaint(c);
			
			// If you change this you should also change getKeyRectangle()
			double x = blackKeyWidth * (2*(i + 1)) - blackKeyWidth * 0.5d + i;
			rect = new RoundRectangle2D.Double (
				// If you change this you should also change getKeyRectangle()
				x, 0,
				blackKeyWidth, blackKeyHeight,
				arcw, arch
			);
			///////
			
			boolean pressed = (j == lastKey) ? false : getKey(j + 1).isPressed();
			if(!pressed) g2.fill(rect);
			
			RoundRectangle2D.Double rect2;
			rect2 = new RoundRectangle2D.Double (
				x, 0,
				blackKeyWidth, arch,
				arcw, arch / 1.8d
			);
			
			g2.fill(rect2);
			g2.setPaint(borderColor);
			g2.draw(rect);
			
			if(pressed) {
			GradientPaint gr = new GradientPaint (
				(float)(x + blackKeyWidth/2), (float)(blackKeyHeight/4), Color.BLACK,
				(float)(x + blackKeyWidth/2), (float)blackKeyHeight, new Color(0x058a02)
			);
			g2.setPaint(gr);
			g2.fill(rect);
			}
			i++;
		}
	}
	
	@Override public void
	midiDataArrived(MidiDataEvent e) {
		switch(e.getType()) {
			case NOTE_ON:
				setKeyPressed(e.getNote(), true);
				break;
			case NOTE_OFF:
				setKeyPressed(e.getNote(), false);
		}
	}
	
	private final Handler handler = new Handler();
	
	private Handler
	getHandler() { return handler; }
	
	private class Handler extends MouseAdapter {
		private int pressedKey = -1;
		
		@Override public void
		mousePressed(MouseEvent e) {
			if(!isPlayingEnabled()) return;
			if(e.getButton() != MouseEvent.BUTTON1) return;
			
			int key = getKey(e.getPoint());
			if(key == -1) return;
			pressedKey = key;
			setKeyPressed(key, true);
			int velocity = getVelocity(e.getPoint(), isWhiteKey(key));
			
			MidiDataEvent evt = new MidiDataEvent (
				PianoRoll.this, MidiDataEvent.Type.NOTE_ON, key, velocity
			);
			
			fireMidiDataEvent(evt);
		}
		
		@Override public void
		mouseReleased(MouseEvent e) {
			if(!isPlayingEnabled()) return;
			if(e.getButton() != MouseEvent.BUTTON1) return;
			
			if(pressedKey == -1) return;
			setKeyPressed(pressedKey, false);
			
			int velocity = getVelocity(e.getPoint(), isWhiteKey(pressedKey));
			MidiDataEvent evt = new MidiDataEvent (
				PianoRoll.this, MidiDataEvent.Type.NOTE_OFF, pressedKey, velocity
			);
			
			pressedKey = -1;
			
			fireMidiDataEvent(evt);
		}
	}
}
