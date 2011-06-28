/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2011 Grigor Iliev <grigor@grigoriliev.com>
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

package org.jsampler.view;

import javax.swing.JPopupMenu;
import java.awt.event.KeyEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.jsampler.CC;
import org.jsampler.JSPrefs;
import org.jsampler.MidiInstrument;

import static org.jsampler.JSPrefs.*;

/**
 * Provides the view configuration.
 * @author Grigor Iliev
 */
public abstract class JSViewConfig {
	private boolean measurementUnitDecibel;
	
	private int firstMidiBankNumber;
	private int firstMidiProgramNumber;
	
	/** Creates a new instance of <code>JSViewConfig</code> */
	public
	JSViewConfig() {
		measurementUnitDecibel = preferences().getBoolProperty(VOL_MEASUREMENT_UNIT_DECIBEL);
		
		String s = VOL_MEASUREMENT_UNIT_DECIBEL;
		preferences().addPropertyChangeListener(s, new PropertyChangeListener() {
			public void
			propertyChange(PropertyChangeEvent e) {
				boolean b;
				b = preferences().getBoolProperty(VOL_MEASUREMENT_UNIT_DECIBEL);
				measurementUnitDecibel = b;
			}
		});
		
		firstMidiBankNumber = preferences().getIntProperty(FIRST_MIDI_BANK_NUMBER);
		firstMidiProgramNumber = preferences().getIntProperty(FIRST_MIDI_PROGRAM_NUMBER);
		
		MidiInstrument.setFirstProgramNumber(firstMidiProgramNumber);
		
		s = FIRST_MIDI_BANK_NUMBER;
		preferences().addPropertyChangeListener(s, new PropertyChangeListener() {
			public void
			propertyChange(PropertyChangeEvent e) {
				firstMidiBankNumber = preferences().getIntProperty(FIRST_MIDI_BANK_NUMBER);
			}
		});
		
		s = FIRST_MIDI_PROGRAM_NUMBER;
		preferences().addPropertyChangeListener(s, new PropertyChangeListener() {
			public void
			propertyChange(PropertyChangeEvent e) {
				firstMidiProgramNumber = preferences().getIntProperty(FIRST_MIDI_PROGRAM_NUMBER);
				MidiInstrument.setFirstProgramNumber(firstMidiProgramNumber);
			}
		});
	}
	
	/**
	 * Provides UI information for instruments database trees.
	 */
	public abstract InstrumentsDbTreeView getInstrumentsDbTreeView();
	
	/**
	 * Provides UI information for instruments database tables.
	 */
	public abstract InstrumentsDbTableView getInstrumentsDbTableView();
	
	public abstract SamplerBrowserView getSamplerBrowserView();
	
	public abstract BasicIconSet getBasicIconSet();
	
	public abstract JSPrefs preferences();
	
	/**
	 * Determines whether this view provides instruments database support.
	 * @return <code>false</code>
	 */
	public boolean
	getInstrumentsDbSupport() { return false; }
	
	/**
	 * Determines whether the volume values should be shown in decibels.
	 */
	public boolean
	isMeasurementUnitDecibel() { return measurementUnitDecibel; }
	
	/** Exports the view configuration of the current session. */
	public String
	exportSessionViewConfig() { return ""; }
	
	private SessionViewConfig sessionViewConfig = null;
	
	public SessionViewConfig
	getSessionViewConfig() { return sessionViewConfig; }
	
	public void
	setSessionViewConfig(SessionViewConfig config) { sessionViewConfig = config; }
	
	public int
	getFirstMidiBankNumber() { return firstMidiBankNumber; }
	
	public int
	getFirstMidiProgramNumber() { return firstMidiProgramNumber; }

	public int
	getDefaultModKey() {
		return CC.isMacOS() ? KeyEvent.META_MASK : KeyEvent.CTRL_MASK;
	}

	/**
	 * Determines whether main menu is moved to
	 * the screen menu bar when running on Mac OS
	 */
	public boolean
	isUsingScreenMenuBar() {
		if(!CC.isMacOS()) return false;
		String s = System.getProperty("apple.laf.useScreenMenuBar");
		return (s != null && "true".equalsIgnoreCase(s)) ? true : false;
	}

	/**  Constructs a new multicolumn menu with the supplied string as its text. */
	public javax.swing.JMenu
	createMultiColumnMenu(String s) { return new net.sf.juife.MultiColumnMenu(s); }

	/**  Constructs a new multicolumn popup menu. */
	public JPopupMenu
	createMultiColumnPopupMenu()
	{ return new net.sf.juife.MultiColumnMenu.PopupMenu(); }
}
