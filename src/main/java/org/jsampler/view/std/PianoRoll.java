/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2009 Grigor Iliev <grigor@grigoriliev.com>
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

import java.awt.Graphics;
import java.awt.Graphics2D;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

import java.util.HashMap;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.jsampler.CC;

import org.linuxsampler.lscp.event.MidiDataEvent;
import org.linuxsampler.lscp.event.MidiDataListener;

import static org.jsampler.view.std.StdI18n.i18n;

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
	
	private Integer[] enabledKeys = null;
	private Integer[] disabledKeys = null;
	private Integer[] keyswitches = null;
	private Integer[] notKeyswitches = null;
	
	private int currentOctave = 3;
	private int constantVelocity = 80;
	private HashMap<Integer, Integer> keyMap = new HashMap<Integer, Integer>();
	
	private final Vector<MidiDataListener> listeners = new Vector<MidiDataListener>();
	
	private boolean shouldRepaint = true;
	
	private PianoRollPainter painter;
	
	public final Action actionScrollLeft = new ActionScrollLeft();
	public final Action actionScrollRight = new ActionScrollRight();
	public final Action actionIncreaseKeyNumber = new ActionIncreaseKeyNumber();
	public final Action actionDecreaseKeyNumber = new ActionDecreaseKeyNumber();
	
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
		
		setPainter(new BasicPianoRollPainter(this));
		
		setKeyRange(0, 127);
		
		installListeneres();
	}
	
	private void
	installListeneres() {
		addMouseListener(getHandler());
		addMouseMotionListener(getHandler());
		addKeyListener(getHandler());
		
		registerKeys(this);
		
		
	}
	
	public KeyListener
	getKeyListener() { return getHandler(); }
	
	public void
	registerKeys(JComponent c) {
		int modKey = CC.getViewConfig().getDefaultModKey();

		KeyStroke k = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, modKey);
		c.getInputMap(JComponent.WHEN_FOCUSED).put(k, "scrollLeft");
		c.getActionMap().put("scrollLeft", actionScrollLeft);
		
		k = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, modKey);
		c.getInputMap(JComponent.WHEN_FOCUSED).put(k, "scrollRight");
		c.getActionMap().put("scrollRight", actionScrollRight);
		
		k = KeyStroke.getKeyStroke(KeyEvent.VK_UP, modKey);
		c.getInputMap(JComponent.WHEN_FOCUSED).put(k, "increaseKeyRange");
		c.getActionMap().put("increaseKeyRange", actionIncreaseKeyNumber);
		
		k = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, modKey);
		c.getInputMap(JComponent.WHEN_FOCUSED).put(k, "decreaseKeyRange");
		c.getActionMap().put("decreaseKeyRange", actionDecreaseKeyNumber);
		
		k = KeyStroke.getKeyStroke(KeyEvent.VK_0, 0);
		c.getInputMap(JComponent.WHEN_FOCUSED).put(k, "changeCurrentOctave0");
		c.getActionMap().put("changeCurrentOctave0", new ActionChangeCurrentOctave(0));
		
		k = KeyStroke.getKeyStroke(KeyEvent.VK_1, 0);
		c.getInputMap(JComponent.WHEN_FOCUSED).put(k, "changeCurrentOctave1");
		c.getActionMap().put("changeCurrentOctave1", new ActionChangeCurrentOctave(1));
		
		k = KeyStroke.getKeyStroke(KeyEvent.VK_2, 0);
		c.getInputMap(JComponent.WHEN_FOCUSED).put(k, "changeCurrentOctave2");
		c.getActionMap().put("changeCurrentOctave2", new ActionChangeCurrentOctave(2));
		
		k = KeyStroke.getKeyStroke(KeyEvent.VK_3, 0);
		c.getInputMap(JComponent.WHEN_FOCUSED).put(k, "changeCurrentOctave3");
		c.getActionMap().put("changeCurrentOctave3", new ActionChangeCurrentOctave(3));
		
		k = KeyStroke.getKeyStroke(KeyEvent.VK_4, 0);
		c.getInputMap(JComponent.WHEN_FOCUSED).put(k, "changeCurrentOctave4");
		c.getActionMap().put("changeCurrentOctave4", new ActionChangeCurrentOctave(4));
		
		k = KeyStroke.getKeyStroke(KeyEvent.VK_5, 0);
		c.getInputMap(JComponent.WHEN_FOCUSED).put(k, "changeCurrentOctave5");
		c.getActionMap().put("changeCurrentOctave5", new ActionChangeCurrentOctave(5));
		
		k = KeyStroke.getKeyStroke(KeyEvent.VK_6, 0);
		c.getInputMap(JComponent.WHEN_FOCUSED).put(k, "changeCurrentOctave6");
		c.getActionMap().put("changeCurrentOctave6", new ActionChangeCurrentOctave(6));
		
		k = KeyStroke.getKeyStroke(KeyEvent.VK_7, 0);
		c.getInputMap(JComponent.WHEN_FOCUSED).put(k, "changeCurrentOctave7");
		c.getActionMap().put("changeCurrentOctave7", new ActionChangeCurrentOctave(7));
	}
	
	public PianoRollPainter
	getPainter() { return painter; }
	
	public void
	setPainter(PianoRollPainter painter) {
		if(painter == null) painter = new BasicPianoRollPainter(this);
		this.painter = painter;
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
	
	private void
	fireNoteOn(int key, int velocity) {
		MidiDataEvent evt = new MidiDataEvent (
			PianoRoll.this, MidiDataEvent.Type.NOTE_ON, key, velocity
		);
		
		fireMidiDataEvent(evt);
	}
	
	private void
	fireNoteOff(int key, int velocity) {
		MidiDataEvent evt = new MidiDataEvent (
			PianoRoll.this, MidiDataEvent.Type.NOTE_OFF, key, velocity
		);
		
		fireMidiDataEvent(evt);
	}
	
	/**
	 * Used to determine which note to play when using
	 * the computer keyboard's key bindings.
	 * @return
	 */
	public int
	getCurrentOctave() { return currentOctave; }
	
	/**
	 * @see #getCurrentOctave
	 * @param octave Specifies the octave to be used as current.
	 */
	public void
	setCurrentOctave(int octave) { currentOctave = octave; }
	
	public int
	getConstantVelocity() { return constantVelocity; }
	
	public void
	setConstantVelocity(int velocity) { constantVelocity = velocity; }
	
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
		if(getShouldRepaint()) repaint(painter.getKeyRectangle(key));
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
		if(getShouldRepaint()) repaint(painter.getKeyRectangle(key));
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
	 * Keys outside the keyboard range are remembered and
	 * applied if needed when the key range is changed.
	 * @param keys List of keys
	 */
	public void
	setKeyswitches(Integer[] keys, boolean b) {
		if(b) keyswitches = keys;
		else notKeyswitches = keys;
		if(keys == null) return;
		
		boolean changed = false;
		for(int k : keys) {
			if(!hasKey(k)) continue;
			if(getKey(k).isKeyswitch() != b) {
				getKey(k).setKeyswitch(b);
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
	 * Disabled keys outside the keyboard range are remembered and
	 * applied if needed when the key range is changed.
	 * @param keys List of keys
	 */
	public void
	setDisabled(Integer[] keys, boolean disable) {
		if(disable) disabledKeys = keys;
		else enabledKeys = keys;
		
		if(keys == null) return;
		
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
		
		boolean b = getShouldRepaint();
		setShouldRepaint(false);
		
		reset(true, false);
		setDisabled(enabledKeys, false);
		setDisabled(disabledKeys, true);
		setKeyswitches(keyswitches, true);
		setKeyswitches(notKeyswitches, false);
		
		setShouldRepaint(b);
		if(getShouldRepaint()) repaint();
	}
	
	public int
	getFirstKey() { return firstKey; }
	
	public int
	getLastKey() { return lastKey; }
	
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
	public static int
	getWhiteKeyCount(int firstKey, int lastKey) {
		int count = 0;
		for(int j = firstKey; j <= lastKey; j++) { // FIXME: Stupid but works
			if(isWhiteKey(j)) count++;
		}
		
		return count;
	}
	
	/**
	 * Gets the MIDI note number of the specified white key position.
	 * @param whiteKey The white key position on the keyboard (zero-based)
	 * @return The MIDI number of the note
	 */
	public int
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
	
	/** Gets the number of white keys in the piano roll. */
	public int
	getWhiteKeyCount() { return whiteKeyCount; }
	
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
		reset(dissableAllKeys, true);
	}
	
	private void
	reset(boolean dissableAllKeys, boolean clear) {
		if(clear) {
			enabledKeys = null;
			disabledKeys = null;
			keyswitches = null;
			notKeyswitches = null;
		}
		
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
	
	@Override public void
	paint(Graphics g) {
		super.paint(g);
		painter.paint(this, (Graphics2D)g);
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
	
	
	private class ActionScrollLeft extends AbstractAction {
		ActionScrollLeft() {
			super(i18n.getLabel("PianoRoll.scrollLeft"));
			
			String s = i18n.getLabel("PianoRoll.scrollLeft");
			putValue(SHORT_DESCRIPTION, s);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			if(getFirstKey() == 0) return;
			int k = getFirstKey() - 1;
			if(!isWhiteKey(k)) k--;
			int k2 = getLastKey() - 1;
			if(!isWhiteKey(k2)) k2--;
			
			setKeyRange(k, k2);
			CC.preferences().setIntProperty("midiKeyboard.firstKey", k);
			CC.preferences().setIntProperty("midiKeyboard.lastKey", k2);
			
		}
	}
	
	
	private class ActionScrollRight extends AbstractAction {
		ActionScrollRight() {
			super(i18n.getLabel("PianoRoll.scrollRight"));
			
			String s = i18n.getLabel("PianoRoll.scrollRight");
			putValue(SHORT_DESCRIPTION, s);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			if(getLastKey() == 127) return;
			int k = getFirstKey() + 1;
			if(!isWhiteKey(k)) k++;
			int k2 = getLastKey() + 1;
			if(!isWhiteKey(k2)) k2++;
			
			setKeyRange(k, k2);
			CC.preferences().setIntProperty("midiKeyboard.firstKey", k);
			CC.preferences().setIntProperty("midiKeyboard.lastKey", k2);
			
		}
	}
	
	private class ActionIncreaseKeyNumber extends AbstractAction {
		ActionIncreaseKeyNumber() {
			super(i18n.getLabel("PianoRoll.increaseKeyNumber"));
			
			String s = i18n.getLabel("PianoRoll.increaseKeyNumber");
			putValue(SHORT_DESCRIPTION, s);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			if(getFirstKey() == 0 && getLastKey() == 127) return;
			int k = getFirstKey() == 0 ? 0 : getFirstKey() - 1;
			if(!isWhiteKey(k)) k--;
			int k2 = getLastKey() == 127 ? 127 : getLastKey() + 1;
			if(!isWhiteKey(k2)) k2++;
			
			setKeyRange(k, k2);
			CC.preferences().setIntProperty("midiKeyboard.firstKey", k);
			CC.preferences().setIntProperty("midiKeyboard.lastKey", k2);
			
		}
	}
	
	private class ActionDecreaseKeyNumber extends AbstractAction {
		ActionDecreaseKeyNumber() {
			super(i18n.getLabel("PianoRoll.decreaseKeyNumber"));
			
			String s = i18n.getLabel("PianoRoll.decreaseKeyNumber");
			putValue(SHORT_DESCRIPTION, s);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			if(getLastKey() - getFirstKey() < 31) return;
			int k = getFirstKey() + 1;
			if(!isWhiteKey(k)) k++;
			int k2 = getLastKey() - 1;
			if(!isWhiteKey(k2)) k2--;
			
			setKeyRange(k, k2);
			CC.preferences().setIntProperty("midiKeyboard.firstKey", k);
			CC.preferences().setIntProperty("midiKeyboard.lastKey", k2);
			
		}
	}
	
	private class ActionChangeCurrentOctave extends AbstractAction {
		private int octave;
		
		ActionChangeCurrentOctave(int octave) {
			super("");
			this.octave = octave;
		}
		
		public void
		actionPerformed(ActionEvent e) {
			setCurrentOctave(octave);
		}
	}
	
	private void
	keyPressed(int keyCode, int note) {
		fireNoteOn(note, getConstantVelocity());
		keyMap.put(keyCode, note);
	}
	
	private void
	keyPressed(int keyCode) {
		if(!isPlayingEnabled()) return;
		
		int offset = (getCurrentOctave() + 2) * 12;
		
		switch(keyCode) {
			case KeyEvent.VK_A:
				keyPressed(keyCode, offset); break;
			case KeyEvent.VK_W:
				keyPressed(keyCode, offset + 1); break;
			case KeyEvent.VK_S:
				keyPressed(keyCode, offset + 2); break;
			case KeyEvent.VK_E:
				keyPressed(keyCode, offset + 3); break;
			case KeyEvent.VK_D:
				keyPressed(keyCode, offset + 4); break;
			case KeyEvent.VK_F:
				keyPressed(keyCode, offset + 5); break;
			case KeyEvent.VK_T:
				keyPressed(keyCode, offset + 6); break;
			case KeyEvent.VK_G:
				keyPressed(keyCode, offset + 7); break;
			case KeyEvent.VK_Y:
				keyPressed(keyCode, offset + 8); break;
			case KeyEvent.VK_H:
				keyPressed(keyCode, offset + 9); break;
			case KeyEvent.VK_U:
				keyPressed(keyCode, offset + 10); break;
			case KeyEvent.VK_J:
				keyPressed(keyCode, offset + 11); break;
			case KeyEvent.VK_K:
				keyPressed(keyCode, offset + 12); break;
			case KeyEvent.VK_O:
				keyPressed(keyCode, offset + 13); break;
			case KeyEvent.VK_L:
				keyPressed(keyCode, offset + 14); break;
			case KeyEvent.VK_P:
				keyPressed(keyCode, offset + 15); break;
			case KeyEvent.VK_SEMICOLON:
				keyPressed(keyCode, offset + 16); break;
			case KeyEvent.VK_QUOTE:
				keyPressed(keyCode, offset + 17); break;
		}
	}
	
	public void
	keyReleased(int keyCode) {
		if(!isPlayingEnabled()) return;
		
		switch(keyCode) {
			case KeyEvent.VK_A:
			case KeyEvent.VK_W:
			case KeyEvent.VK_S:
			case KeyEvent.VK_E:
			case KeyEvent.VK_D:
			case KeyEvent.VK_F:
			case KeyEvent.VK_T:
			case KeyEvent.VK_G:
			case KeyEvent.VK_Y:
			case KeyEvent.VK_H:
			case KeyEvent.VK_U:
			case KeyEvent.VK_J:
			case KeyEvent.VK_K:
			case KeyEvent.VK_O:
			case KeyEvent.VK_L:
			case KeyEvent.VK_P:
			case KeyEvent.VK_SEMICOLON:
			case KeyEvent.VK_QUOTE:
				if(keyMap.get(keyCode) == null) return;
				fireNoteOff(keyMap.get(keyCode), getConstantVelocity());
				keyMap.remove(keyCode);
		}
	}
	
	private final Handler handler = new Handler();
	
	private Handler
	getHandler() { return handler; }
	
	private class Handler extends MouseAdapter implements KeyListener {
		@Override
		public void
		mouseClicked(MouseEvent e) { requestFocusInWindow(); }
		
		private int pressedKey = -1;
		
		@Override
		public void
		mousePressed(MouseEvent e) {
			if(!isPlayingEnabled()) return;
			if(e.getButton() != MouseEvent.BUTTON1) return;
			
			int key = painter.getKeyByPoint(e.getPoint());
			if(key == -1) return;
			pressedKey = key;
			setKeyPressed(key, true);
			int velocity = painter.getVelocity(e.getPoint(), key);
			
			fireNoteOn(key, velocity);
		}
		
		@Override
		public void
		mouseReleased(MouseEvent e) {
			if(!isPlayingEnabled()) return;
			if(e.getButton() != MouseEvent.BUTTON1) return;
			
			if(pressedKey == -1) return;
			setKeyPressed(pressedKey, false);
			
			int velocity = painter.getVelocity(e.getPoint(), pressedKey);
			MidiDataEvent evt = new MidiDataEvent (
				PianoRoll.this, MidiDataEvent.Type.NOTE_OFF, pressedKey, velocity
			);
			
			pressedKey = -1;
			
			fireMidiDataEvent(evt);
		}
		
		@Override
		public void
		mouseDragged(MouseEvent e) {
			if(!isPlayingEnabled()) return;
			//if(e.getButton() != MouseEvent.BUTTON1) return;
			
			if(pressedKey == -1) return;
			int key = painter.getKeyByPoint(e.getPoint());
			if(key == pressedKey) return;
			setKeyPressed(pressedKey, false);
			
			int velocity = painter.getVelocity(e.getPoint(), pressedKey);
			fireNoteOff(pressedKey, velocity);
			
			pressedKey = key;
			if(pressedKey == -1) return;
			
			setKeyPressed(key, true);
			velocity = painter.getVelocity(e.getPoint(), key);
			
			fireNoteOn(key, velocity);
		}
		
		private HashMap<Integer, Long> pressedKeysMap = new HashMap<Integer, Long>();
		
		public void
		keyPressed(KeyEvent e) {
			// Ugly fix for bug 4153069
			if(pressedKeysMap.get(e.getKeyCode()) == null) {
				keyPressedNoAutoRepeat(e);
			}
			
			pressedKeysMap.put(e.getKeyCode(), e.getWhen());
		}
		
		public void
		keyPressedNoAutoRepeat(KeyEvent e) {
			//System.out.println("Pressed: " + e.getKeyCode() + " " + e.getWhen());
			if(e.isControlDown() || e.isAltDown() || e.isShiftDown() || e.isMetaDown());
			else PianoRoll.this.keyPressed(e.getKeyCode());
		}
		
		public void
		keyReleased(final KeyEvent e) {
			// Ugly fix for bug 4153069
			SwingUtilities.invokeLater(new Fix4153069(e));
		}
		
		public void
		keyReleasedNoAutoRepeat(KeyEvent e) {
			//System.out.println("Released: " + e.getKeyCode() + " " + e.getWhen());
			PianoRoll.this.keyReleased(e.getKeyCode());
		}
		
		public void
		keyTyped(KeyEvent e) { }
	}
	
	class Fix4153069 implements Runnable, ActionListener {
		KeyEvent e;
		int count = 0;
		
		Fix4153069(KeyEvent e) {
			this.e = e;
		}
		
		public void
		actionPerformed(ActionEvent e) { run(); }
		
		public void
		run() {
			Long l = getHandler().pressedKeysMap.get(e.getKeyCode());
			if(l == null || l.longValue() < e.getWhen()) {
				if(l != null) {
					if(delay()) return;
				}
				getHandler().pressedKeysMap.remove(e.getKeyCode());
				getHandler().keyReleasedNoAutoRepeat(e);
			} 
			
		}
		
		private boolean
		delay() {
			if(count < 1) {
				//System.out.println("Delaying...");
				count++;
				
				Timer t = new Timer(4, this);
				t.setRepeats(false);
				t.start();
				return true;
			}
			
			return false;
		}
	}
}
