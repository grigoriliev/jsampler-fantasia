/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2007 Grigor Iliev <grigor@grigoriliev.com>
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

import java.awt.Point;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SpinnerNumberModel;


import org.jsampler.CC;
import org.jsampler.HF;
import org.jsampler.JSPrefs;

import static org.jsampler.view.std.StdPrefs.*;


/**
 *
 * @author Grigor Iliev
 */
public class JSVolumeEditorPopup {
	private final JComponent owner;
	private Popup popup = null;
	private final JSpinner spinner = new JSpinner();
	private boolean decibels = false;
	
	/* Used to prevent double committing on focus lost and to prevent commiting on cancel. */
	private boolean shouldCommit = true;
	
	private final Vector<ActionListener> listeners = new Vector<ActionListener>();
	
	public static enum VolumeType {
		MASTER, CHANNEL
	}
	
	private VolumeType volumeType;
	
	/**
	 * Creates a new instance of <code>JSVolumeEditorPopup</code>
	 */
	public
	JSVolumeEditorPopup(final JComponent owner, VolumeType volumeType) {
		if(owner == null) throw new IllegalArgumentException("owner should be non-null");
		this.owner = owner;
		this.volumeType = volumeType;
		java.awt.Dimension d = spinner.getPreferredSize();
		d.width = 55;
		spinner.setPreferredSize(d);
		
		setNumberModel();
		
		String s;
		switch(volumeType) {
			case MASTER: s = MAXIMUM_MASTER_VOLUME; break;
			case CHANNEL: s = MAXIMUM_CHANNEL_VOLUME; break;
			default: s = MAXIMUM_CHANNEL_VOLUME;
		}
		preferences().addPropertyChangeListener(s, new PropertyChangeListener() {
			public void
			propertyChange(PropertyChangeEvent e) {
				setNumberModel();
			}
		});
		
		s = VOL_MEASUREMENT_UNIT_DECIBEL;
		preferences().addPropertyChangeListener(s, new PropertyChangeListener() {
			public void
			propertyChange(PropertyChangeEvent e) {
				setNumberModel();
			}
		});
	}
	
	public void
	show() {
		if(popup != null) {
			popup.hide();
		}
		
		shouldCommit = true;
		
		Point p = owner.getLocationOnScreen();
		int h = owner.getHeight();
		popup = PopupFactory.getSharedInstance().getPopup(owner, spinner, p.x, p.y + h);
		popup.show();
		JComponent c = spinner.getEditor();
		((JSpinner.DefaultEditor)c).getTextField().requestFocus();
	}
	
	public void
	hide() {
		if(popup == null) return;
		shouldCommit = false;
		popup.hide();
		popup = null;
	}
	
	public boolean
	isVisible() { return popup != null; }
	
	public void
	commit() {
		try { spinner.commitEdit(); }
		catch(Exception x) { }
		if(shouldCommit) fireActionEvent();
	}
	
	public void
	setCurrentVolume(float vol) {
		int volPercentage = (int)(vol * 100);
		if(decibels) {
			double d = HF.percentsToDecibels(volPercentage);
			if(d == Double.NEGATIVE_INFINITY) d = -100;
			spinner.setValue(d);
		} else {
			spinner.setValue(volPercentage);
		}
	}
	
	public float
	getVolumeFactor() {
		if(decibels) {
			double d = (Double)spinner.getValue();
			return HF.decibelsToFactor(d);
		} else {
			int i = (Integer)spinner.getValue();
			return HF.percentsToFactor(i);
		}
	}
	
	public void
	addActionListener(ActionListener l) { listeners.add(l); }
	
	public void
	removeActionListener(ActionListener l) { listeners.remove(l); }
	
	private void
	setNumberModel() {
		int volPercentage;
		if(decibels) {
			volPercentage = HF.decibelsToPercents((Double)spinner.getValue());
		} else {
			volPercentage = (Integer)spinner.getValue();
		}
		
		String s;
		switch(volumeType) {
			case MASTER: s = MAXIMUM_MASTER_VOLUME; break;
			case CHANNEL: s = MAXIMUM_CHANNEL_VOLUME; break;
			default: s = MAXIMUM_CHANNEL_VOLUME;
		}
		
		int max = preferences().getIntProperty(s);
		decibels = preferences().getBoolProperty(VOL_MEASUREMENT_UNIT_DECIBEL);
		SpinnerNumberModel model;
		
		if(decibels) {
			double vol = HF.percentsToDecibels(volPercentage);
			if(vol == Double.NEGATIVE_INFINITY) vol = -100;
			model = new SpinnerNumberModel(vol, -100, HF.percentsToDecibels(max), 1);
			spinner.setModel(model);
		} else {
			model = new SpinnerNumberModel(volPercentage, 0, max, 1);
			spinner.setModel(model);
		}
		
		reinstallEditorListeners();
	}
	
	private FocusAdapter focusListener = new FocusAdapter() {
		public void
		focusLost(FocusEvent e) {
			if(e.getOppositeComponent() == owner) return;
			
			commit();
			hide();
		}
	};
	
	private ActionListener actionListener = new ActionListener() {
		public void
		actionPerformed(ActionEvent e) {
			commit();
			hide();
		}
	};
	
	/** Invoked when the number model of the spinner is changed to reinstall listeners */
	private void
	reinstallEditorListeners() {
		JComponent c = spinner.getEditor();
		JTextField tf = ((JSpinner.DefaultEditor)c).getTextField();
		
		tf.removeFocusListener(focusListener); // just in case the text field stays the same
		tf.addFocusListener(focusListener);
		
		tf.removeActionListener(actionListener);
		tf.addActionListener(actionListener);
		
		tf.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).remove (
			KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0)
		);
		
		tf.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put (
			KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
			"cancelOp"
		);
		
		tf.getActionMap().remove("cancelOp");
		tf.getActionMap().put ("cancelOp", new AbstractAction() {
			public void
			actionPerformed(ActionEvent e) { hide(); }
		});
		
		tf.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).remove (
			KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0)
		);
		
		tf.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put (
			KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
			"commitVolume"
		);
		
		tf.getActionMap().remove("commitVolume");
		tf.getActionMap().put ("commitVolume", new AbstractAction() {
			public void
			actionPerformed(ActionEvent e) {
				commit();
				hide();
			}
		});
	}
	
	private void
	fireActionEvent() {
		for(ActionListener l : listeners) l.actionPerformed(null);
	}
	
	private static JSPrefs
	preferences() { return CC.getViewConfig().preferences(); }
}
