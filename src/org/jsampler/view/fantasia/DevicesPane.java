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

import java.awt.BorderLayout;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;

import org.jdesktop.swingx.JXTaskPane;

import org.jsampler.view.fantasia.basic.*;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.api.SubstanceConstants.FocusKind;
import org.jvnet.substance.shaper.ClassicButtonShaper;

import static org.jsampler.view.fantasia.FantasiaI18n.i18n;
import static org.jsampler.view.fantasia.FantasiaPrefs.*;

/**
 *
 * @author Grigor Iliev
 */
public class DevicesPane extends FantasiaPanel {
	private final TaskPaneContainer taskPaneContainer = new TaskPaneContainer();
	private final JXTaskPane midiDevicesTaskPane = new FantasiaTaskPane();
	private final JXTaskPane audioDevicesTaskPane = new FantasiaTaskPane();
	
	private final MidiDevicesPane midiDevicesPane = new MidiDevicesPane();
	private final AudioDevicesPane audioDevicesPane = new AudioDevicesPane();
	
	/** Creates a new instance of <code>DevicesPane</code> */
	public
	DevicesPane() {
		setOpaque(false);
		setLayout(new BorderLayout());
		midiDevicesTaskPane.setTitle(i18n.getLabel("DevicesPane.midiDevicesTaskPane"));
		midiDevicesTaskPane.setAnimated(preferences().getBoolProperty(ANIMATED));
		
		preferences().addPropertyChangeListener(ANIMATED, new PropertyChangeListener() {
			public void
			propertyChange(PropertyChangeEvent e) {
				boolean b = preferences().getBoolProperty(ANIMATED);
				midiDevicesTaskPane.setAnimated(b);
			}
		});
		
		audioDevicesTaskPane.setTitle(i18n.getLabel("DevicesPane.audioDevicesTaskPane"));
		audioDevicesTaskPane.setAnimated(preferences().getBoolProperty(ANIMATED));
		
		preferences().addPropertyChangeListener(ANIMATED, new PropertyChangeListener() {
			public void
			propertyChange(PropertyChangeEvent e) {
				boolean b = preferences().getBoolProperty(ANIMATED);
				audioDevicesTaskPane.setAnimated(b);
			}
		});
		
		midiDevicesTaskPane.putClientProperty (
			SubstanceLookAndFeel.FOCUS_KIND, FocusKind.NONE
		);
		
		taskPaneContainer.putClientProperty (
			SubstanceLookAndFeel.BUTTON_SHAPER_PROPERTY, new ClassicButtonShaper()
		);
		
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
		
		taskPaneContainer.setOpaque(false);
	}
	
	public MidiDevicesPane
	getMidiDevicesPane() { return midiDevicesPane; }
	
	public AudioDevicesPane
	getAudioDevicesPane() { return audioDevicesPane; }
}
