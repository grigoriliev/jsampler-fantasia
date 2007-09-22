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

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.jsampler.CC;
import org.jsampler.JSPrefs;

import static org.jsampler.view.std.StdI18n.i18n;
import static org.jsampler.view.std.StdPrefs.*;

/**
 *
 * @author Grigor Iliev
 */
public class JSViewProps {
	
	/** Forbids the instantiation of this class. */
	private JSViewProps() { }
	
	private static JSPrefs
	preferences() { return CC.getViewConfig().preferences(); }
	
	
	public static class MidiDevicesPane extends JPanel {
		private final JCheckBox checkAdditionalParams =
			new JCheckBox(i18n.getLabel("JSViewProps.checkAdditionalParams"));
		public
		MidiDevicesPane() {
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			checkAdditionalParams.setAlignmentX(JPanel.LEFT_ALIGNMENT);
			add(checkAdditionalParams);
			String s = "MidiDevice.showAdditionalParameters";
			boolean b = preferences().getBoolProperty(s);
			checkAdditionalParams.setSelected(b);
			s = i18n.getLabel("JSViewProps.MidiDevicesPane");
			setBorder(BorderFactory.createTitledBorder(s));
			setMaximumSize(new Dimension(Short.MAX_VALUE, getPreferredSize().height));
			setAlignmentX(JPanel.LEFT_ALIGNMENT);
		}
		
		public void
		apply() {
			String s = "MidiDevice.showAdditionalParameters";
			preferences().setBoolProperty(s, checkAdditionalParams.isSelected());
		}
	}
	
	public static class AudioDevicesPane extends JPanel {
		private final JCheckBox checkAdditionalParams =
			new JCheckBox(i18n.getLabel("JSViewProps.checkAdditionalParams"));
		public
		AudioDevicesPane() {
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			checkAdditionalParams.setAlignmentX(JPanel.LEFT_ALIGNMENT);
			add(checkAdditionalParams);
			String s = "AudioDevice.showAdditionalParameters";
			boolean b = preferences().getBoolProperty(s);
			checkAdditionalParams.setSelected(b);
			s = i18n.getLabel("JSViewProps.AudioDevicesPane");
			setBorder(BorderFactory.createTitledBorder(s));
			setMaximumSize(new Dimension(Short.MAX_VALUE, getPreferredSize().height));
			setAlignmentX(JPanel.LEFT_ALIGNMENT);
		}
		
		public void
		apply() {
			String s = "AudioDevice.showAdditionalParameters";
			preferences().setBoolProperty(s, checkAdditionalParams.isSelected());
		}
	}
}
