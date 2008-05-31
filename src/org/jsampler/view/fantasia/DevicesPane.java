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

package org.jsampler.view.fantasia;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.api.SubstanceConstants.FocusKind;
import org.jvnet.substance.shaper.ClassicButtonShaper;

import static org.jsampler.view.fantasia.FantasiaI18n.i18n;
import static org.jsampler.view.fantasia.FantasiaPrefs.*;

/**
 *
 * @author Grigor Iliev
 */
public class DevicesPane extends JPanel {
	private final TaskPaneContainer taskPaneContainer = new TaskPaneContainer();
	private final TaskPane midiDevicesTaskPane = new TaskPane();
	private final TaskPane audioDevicesTaskPane = new TaskPane();
	
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
		
		midiDevicesTaskPane.add(new MidiDevicesPane());
		audioDevicesTaskPane.add(new AudioDevicesPane());
		
		taskPaneContainer.setOpaque(false);
	}
}
