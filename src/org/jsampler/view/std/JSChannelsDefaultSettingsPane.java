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

import java.awt.Dialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sf.juife.InformationDialog;

import org.linuxsampler.lscp.SamplerEngine;

import org.jsampler.CC;
import org.jsampler.JSPrefs;

import static org.jsampler.view.std.StdI18n.i18n;
import static org.jsampler.view.std.StdPrefs.*;


/**
 *
 * @author Grigor Iliev
 */
public class JSChannelsDefaultSettingsPane extends JPanel {
	private final JLabel lDefaultEngine =
		new JLabel(i18n.getLabel("JSChannelsDefaultSettingsPane.lDefaultEngine"));
	
	private final JLabel lMidiInput =
		new JLabel(i18n.getLabel("JSChannelsDefaultSettingsPane.lMidiInput"));
	
	private final JLabel lAudioOutput =
		new JLabel(i18n.getLabel("JSChannelsDefaultSettingsPane.lAudioOutput"));
	
	private final JComboBox cbDefaultEngine = new JComboBox();
	private final JComboBox cbMidiInput = new JComboBox();
	private final JComboBox cbAudioOutput = new JComboBox();
	
	private final static String strFirstDevice =
		i18n.getLabel("JSChannelsDefaultSettingsPane.strFirstDevice");
	
	private final static String strFirstDeviceNextChannel =
		i18n.getLabel("JSChannelsDefaultSettingsPane.strFirstDeviceNextChannel");
	
	/** Creates a new instance of <code>JSChannelsDefaultSettingsPane</code> */
	public
	JSChannelsDefaultSettingsPane() {
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
	
		setLayout(gridbag);
		
		c.fill = GridBagConstraints.NONE;
	
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(3, 3, 3, 3);
		gridbag.setConstraints(lDefaultEngine, c);
		add(lDefaultEngine); 

		c.gridx = 0;
		c.gridy = 1;
		gridbag.setConstraints(lMidiInput, c);
		add(lMidiInput); 

		c.gridx = 0;
		c.gridy = 2;
		gridbag.setConstraints(lAudioOutput, c);
		add(lAudioOutput);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(cbDefaultEngine, c);
		add(cbDefaultEngine);
		
		c.gridx = 1;
		c.gridy = 1;
		gridbag.setConstraints(cbMidiInput, c);
		add(cbMidiInput);
		
		c.gridx = 1;
		c.gridy = 2;
		gridbag.setConstraints(cbAudioOutput, c);
		add(cbAudioOutput);
		
		for(SamplerEngine e : CC.getSamplerModel().getEngines()) {
			cbDefaultEngine.addItem(e);
		}
		
		String defaultEngine = preferences().getStringProperty(DEFAULT_ENGINE);
		for(SamplerEngine e : CC.getSamplerModel().getEngines()) {
			if(e.getName().equals(defaultEngine)) cbDefaultEngine.setSelectedItem(e);
		}
		
		cbDefaultEngine.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { changeDefaultEngine(); }
		});
		
		cbMidiInput.addItem(strFirstDevice);
		cbMidiInput.addItem(strFirstDeviceNextChannel);
		
		String s = preferences().getStringProperty(DEFAULT_MIDI_INPUT);
		
		if(s.equals("firstDevice")) {
			cbMidiInput.setSelectedItem(strFirstDevice);
		} else if(s.equals("firstDeviceNextChannel")) {
			cbMidiInput.setSelectedItem(strFirstDeviceNextChannel);
		}
		
		cbMidiInput.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { changeDefaultMidiInput(); }
		});
		
		cbAudioOutput.addItem(strFirstDevice);
		
		cbAudioOutput.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { changeDefaultAudioOutput(); }
		});
	}
	
	public JDialog
	createDialog(Dialog owner) {
		String s = i18n.getLabel("JSChannelsDefaultSettingsPane.title");
		InformationDialog dlg = new InformationDialog(owner, s, this);
		return dlg;
	}
	
	private void
	changeDefaultEngine() {
		Object o = cbDefaultEngine.getSelectedItem();
		if(o == null) return;
		String s = ((SamplerEngine) o).getName();
		preferences().setStringProperty(DEFAULT_ENGINE, s);
	}
	
	private void
	changeDefaultMidiInput() {
		Object o = cbMidiInput.getSelectedItem();
		if(o == null) return;
		
		if(o == strFirstDevice) {
			preferences().setStringProperty(DEFAULT_MIDI_INPUT, "firstDevice");
		} else if(o == strFirstDeviceNextChannel) {
			preferences().setStringProperty(DEFAULT_MIDI_INPUT, "firstDeviceNextChannel");
		}
	}
	
	private void
	changeDefaultAudioOutput() {
		Object o = cbAudioOutput.getSelectedItem();
		if(o == null) return;
		
		if(o == strFirstDevice) {
			preferences().setStringProperty(DEFAULT_AUDIO_OUTPUT, "firstDevice");
		}
	}
	
	private static JSPrefs
	preferences() { return CC.getViewConfig().preferences(); }
}
