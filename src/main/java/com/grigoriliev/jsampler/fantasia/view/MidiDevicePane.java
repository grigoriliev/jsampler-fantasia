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

import java.awt.Dimension;
import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.grigoriliev.jsampler.fantasia.view.basic.PixmapPane;
import com.grigoriliev.jsampler.juife.Task;
import com.grigoriliev.jsampler.juife.event.TaskEvent;
import com.grigoriliev.jsampler.juife.event.TaskListener;

import com.grigoriliev.jsampler.CC;
import com.grigoriliev.jsampler.MidiDeviceModel;

import com.grigoriliev.jsampler.event.MidiDeviceEvent;
import com.grigoriliev.jsampler.event.MidiDeviceListener;
import com.grigoriliev.jsampler.event.ParameterEvent;
import com.grigoriliev.jsampler.event.ParameterListener;

import com.grigoriliev.jsampler.task.Midi;

import com.grigoriliev.jsampler.swing.view.ParameterTable;

import com.grigoriliev.jsampler.jlscp.MidiInputDevice;
import com.grigoriliev.jsampler.jlscp.MidiPort;
import com.grigoriliev.jsampler.jlscp.Parameter;

import static com.grigoriliev.jsampler.fantasia.view.FantasiaPrefs.preferences;


/**
 *
 * @author Grigor Iliev
 */
public class MidiDevicePane extends DevicePane {
	private final OptionsPane optionsPane;
	private final ParameterTable portParamTable = new ParameterTable();
	
	private MidiDeviceModel midiDeviceModel;
	
	/** Creates a new instance of <code>MidiDevicePane</code> */
	public
	MidiDevicePane(MidiDeviceModel model) {
		midiDeviceModel = model;
		
		portParamTable.setFillsViewportHeight(true);
		
		optionsPane = new OptionsPane();
		setOptionsPane(optionsPane);
		
		int id = model.getDeviceId();
		String s = model.getDeviceInfo().getDriverName();
		setDeviceName(FantasiaI18n.i18n.getLabel("MidiDevicePane.lDevName", id, s));
	}
	
	@Override
	protected void
	destroyDevice() {
		final Task t = new Midi.DestroyDevice(midiDeviceModel.getDeviceId());
		t.addTaskListener(new TaskListener() {
			public void
			taskPerformed(TaskEvent e) {
				if(t.doneWithErrors()) restoreDevice();
			}
		});
		
		CC.getTaskQueue().add(t);
	}
	
	public int
	getDeviceId() { return midiDeviceModel.getDeviceId(); }
	
	class OptionsPane extends PixmapPane implements ActionListener, ItemListener,
				ChangeListener, MidiDeviceListener, ParameterListener {
		
		private final JCheckBox checkActive =
			new JCheckBox(FantasiaI18n.i18n.getLabel("MidiDevicePane.checkActive"));
		
		private final JLabel lPorts = new JLabel(FantasiaI18n.i18n.getLabel("MidiDevicePane.lPorts"));
		private final JSpinner spinnerPorts;
		private final JLabel lPort = new JLabel(FantasiaI18n.i18n.getLabel("MidiDevicePane.lPort"));
		private final JComboBox cbPort = new JComboBox();
		
		private final ParameterTable additionalParamsTable = new ParameterTable();
		private final JPanel additionalParamsPane = new JPanel();
		
		OptionsPane() {
			super(Res.gfxChannelOptions);
			
			setAlignmentX(LEFT_ALIGNMENT);
			
			setPixmapInsets(new Insets(1, 1, 1, 1));
			setLayout(new java.awt.BorderLayout());
			setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			setOpaque(false);
			
			PixmapPane mainPane = new PixmapPane(Res.gfxRoundBg7);
			mainPane.setPixmapInsets(new Insets(3, 3, 3, 3));
			mainPane.setOpaque(false);
			
			mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
			mainPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			mainPane.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
			
			JPanel p = new JPanel();
			p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
			
			p.add(checkActive);
			p.add(Box.createRigidArea(new Dimension(12, 0)));
			p.add(lPorts);
			p.add(Box.createRigidArea(new Dimension(5, 0)));
			
			Parameter<Integer> prm = midiDeviceModel.getDeviceInfo().getPortsParameter();
			int min = 1;
			if(prm.getRangeMin() != null) min = prm.getRangeMin().intValue();
			int max = 50;
			if(prm.getRangeMax() != null) max = prm.getRangeMax().intValue();
			spinnerPorts = new JSpinner(new SpinnerNumberModel(1, min, max, 1));
			if(prm.isFixed()) spinnerPorts.setEnabled(false);
			
			p.add(spinnerPorts);
			p.setOpaque(false);
			
			mainPane.add(p);
			mainPane.add(Box.createRigidArea(new Dimension(0, 5)));
			
			JPanel p2 = additionalParamsPane;
			p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));
			p2.setOpaque(false);
			
			p2.add(createHSeparator());
			p2.add(Box.createRigidArea(new Dimension(0, 5)));
			
			JScrollPane sp = new JScrollPane(additionalParamsTable);
			
			sp.setPreferredSize(new Dimension(77, 90));
			p2.add(sp);
			mainPane.add(p2);
			
			mainPane.add(Box.createRigidArea(new Dimension(0, 5)));
			
			mainPane.add(createHSeparator());
			mainPane.add(Box.createRigidArea(new Dimension(0, 5)));
			
			p = new JPanel();
			p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
			p.setOpaque(false);
			
			p2 = new JPanel();
			p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
			p2.add(lPort);
			p2.add(Box.createRigidArea(new Dimension(5, 0)));
			p2.add(cbPort);
			p2.setOpaque(false);
			p.add(p2);
			
			p.add(Box.createRigidArea(new Dimension(0, 5)));
			
			sp = new JScrollPane(portParamTable);
			sp.setPreferredSize(new Dimension(77, 90));
			p.add(sp);
			
			mainPane.add(p);
			add(mainPane);
			
			checkActive.setSelected(midiDeviceModel.isActive());
			spinnerPorts.setValue(midiDeviceModel.getDeviceInfo().getMidiPortCount());
			
			cbPort.addActionListener(this);
			checkActive.addItemListener(this);
			spinnerPorts.addChangeListener(this);
			midiDeviceModel.addMidiDeviceListener(this);
			portParamTable.getModel().addParameterListener(this);
			
			for(MidiPort port : midiDeviceModel.getDeviceInfo().getMidiPorts()) {
				cbPort.addItem(port);
			}
			
			Parameter[] pS = midiDeviceModel.getDeviceInfo().getAdditionalParameters();
			additionalParamsTable.getModel().setParameters(pS);
			additionalParamsTable.getModel().addParameterListener(new ParameterListener() {
				public void
				parameterChanged(ParameterEvent e) {
					midiDeviceModel.setBackendDeviceParameter(e.getParameter());
				}
			});
			
			updateAdditionalParamsViewState();
			String s = "MidiDevice.showAdditionalParameters";
			preferences().addPropertyChangeListener(s, new PropertyChangeListener() {
				public void
				propertyChange(PropertyChangeEvent e) {
					updateAdditionalParamsViewState();
				}
			});
		}
		
		private void
		updateAdditionalParamsViewState() {
			String s = "MidiDevice.showAdditionalParameters";
			additionalParamsPane.setVisible(preferences().getBoolProperty(s));
			validate();
		}
		
		@Override
		public void
		actionPerformed(ActionEvent e) {
			Object obj = cbPort.getSelectedItem();
			if(obj == null) {
				portParamTable.getModel().setParameters(new Parameter[0]);
				return;
			}
			
			MidiPort port = (MidiPort)obj;
			
			portParamTable.getModel().setParameters(port.getAllParameters());
		}
		
		@Override
		public void
		itemStateChanged(ItemEvent e) {
			boolean a = checkActive.isSelected();
			if(a != midiDeviceModel.isActive()) midiDeviceModel.setBackendActive(a);
		}
		
		@Override
		public void
		stateChanged(ChangeEvent e) {
			int p = (Integer)spinnerPorts.getValue();
			if(p != midiDeviceModel.getDeviceInfo().getMidiPortCount()) {
				midiDeviceModel.setBackendPortCount(p);
			}
		}
		
		@Override
		public void
		settingsChanged(MidiDeviceEvent e) {
			int p = (Integer)spinnerPorts.getValue();
			int np = midiDeviceModel.getDeviceInfo().getMidiPortCount();
			if(p != np) spinnerPorts.setValue(np);
			
			boolean a = checkActive.isSelected();
			boolean na = midiDeviceModel.isActive();
			if(a != na) checkActive.setSelected(na);
			
			MidiInputDevice d = e.getMidiDeviceModel().getDeviceInfo();
			
			Parameter[] params = d.getAdditionalParameters();
			additionalParamsTable.getModel().setParameters(params);
			
			int idx = cbPort.getSelectedIndex();
			cbPort.removeAllItems();
			for(MidiPort port : d.getMidiPorts()) cbPort.addItem(port);
			
			if(idx >= cbPort.getModel().getSize()) idx = 0;
			
			if(cbPort.getModel().getSize() > 0) cbPort.setSelectedIndex(idx);
		}
		
		/** Invoked when when the value of a particular parameter is changed. */
		@Override
		public void
		parameterChanged(ParameterEvent e) {
			int port = cbPort.getSelectedIndex();
			if(port == -1) {
				CC.getLogger().warning("There is no MIDI port selected!");
				return;
			}
			
			midiDeviceModel.setBackendPortParameter(port, e.getParameter());
		}
	}
}
