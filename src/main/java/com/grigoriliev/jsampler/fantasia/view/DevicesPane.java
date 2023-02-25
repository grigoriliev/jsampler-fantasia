/*
 *   JSampler - a front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2023 Grigor Iliev <grigor@grigoriliev.com>
 *
 *   This file is part of JSampler.
 *
 *   JSampler is free software: you can redistribute it and/or modify it under
 *   the terms of the GNU General Public License as published by the Free
 *   Software Foundation, either version 3 of the License, or (at your option)
 *   any later version.
 *
 *   JSampler is distributed in the hope that it will be useful, but WITHOUT
 *   ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *   FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 *   more details.
 *
 *   You should have received a copy of the GNU General Public License along
 *   with JSampler. If not, see <https://www.gnu.org/licenses/>. 
 */

package com.grigoriliev.jsampler.fantasia.view;

import java.awt.BorderLayout;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;

import com.grigoriliev.jsampler.fantasia.view.basic.FantasiaPanel;
import com.grigoriliev.jsampler.fantasia.view.basic.FantasiaSubPanel;
import com.grigoriliev.jsampler.fantasia.view.basic.FantasiaTaskPane;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;

import com.grigoriliev.jsampler.fantasia.view.basic.*;

/**
 *
 * @author Grigor Iliev
 */
public class DevicesPane extends FantasiaPanel {
	private final JXTaskPaneContainer taskPaneContainer = new JXTaskPaneContainer();
	private final JXTaskPane midiDevicesTaskPane = new FantasiaTaskPane();
	private final JXTaskPane audioDevicesTaskPane = new FantasiaTaskPane();
	
	private final MidiDevicesPane midiDevicesPane = new MidiDevicesPane();
	private final AudioDevicesPane audioDevicesPane = new AudioDevicesPane();
	
	/** Creates a new instance of <code>DevicesPane</code> */
	public
	DevicesPane() {
		setOpaque(false);
		setLayout(new BorderLayout());
		midiDevicesTaskPane.setTitle(FantasiaI18n.i18n.getLabel("DevicesPane.midiDevicesTaskPane"));
		midiDevicesTaskPane.setAnimated(FantasiaPrefs.preferences().getBoolProperty(FantasiaPrefs.ANIMATED));
		
		FantasiaPrefs.preferences().addPropertyChangeListener(FantasiaPrefs.ANIMATED, new PropertyChangeListener() {
			public void
			propertyChange(PropertyChangeEvent e) {
				boolean b = FantasiaPrefs.preferences().getBoolProperty(FantasiaPrefs.ANIMATED);
				midiDevicesTaskPane.setAnimated(b);
			}
		});
		
		audioDevicesTaskPane.setTitle(FantasiaI18n.i18n.getLabel("DevicesPane.audioDevicesTaskPane"));
		audioDevicesTaskPane.setAnimated(FantasiaPrefs.preferences().getBoolProperty(FantasiaPrefs.ANIMATED));
		
		FantasiaPrefs.preferences().addPropertyChangeListener(FantasiaPrefs.ANIMATED, new PropertyChangeListener() {
			public void
			propertyChange(PropertyChangeEvent e) {
				boolean b = FantasiaPrefs.preferences().getBoolProperty(FantasiaPrefs.ANIMATED);
				audioDevicesTaskPane.setAnimated(b);
			}
		});

		taskPaneContainer.setBackgroundPainter(null);
		taskPaneContainer.setOpaque(false);
		
		taskPaneContainer.add(midiDevicesTaskPane);
		taskPaneContainer.add(audioDevicesTaskPane);
		taskPaneContainer.setBorder(BorderFactory.createEmptyBorder());
		add(taskPaneContainer);
		
		FantasiaSubPanel fsp = new FantasiaSubPanel(false, true, false);
		fsp.add(midiDevicesPane);
		midiDevicesTaskPane.add(fsp);
		
		fsp = new FantasiaSubPanel(false, true, false);
		fsp.add(audioDevicesPane);
		audioDevicesTaskPane.add(fsp);
	}
	
	public MidiDevicesPane
	getMidiDevicesPane() { return midiDevicesPane; }
	
	public AudioDevicesPane
	getAudioDevicesPane() { return audioDevicesPane; }
}
