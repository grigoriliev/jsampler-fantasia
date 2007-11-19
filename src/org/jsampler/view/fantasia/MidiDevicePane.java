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

import net.sf.juife.Task;
import net.sf.juife.event.TaskEvent;
import net.sf.juife.event.TaskListener;

import org.jsampler.CC;
import org.jsampler.MidiDeviceModel;

import org.jsampler.event.MidiDeviceEvent;
import org.jsampler.event.MidiDeviceListener;
import org.jsampler.event.ParameterEvent;
import org.jsampler.event.ParameterListener;

import org.jsampler.task.Midi;

import org.jsampler.view.ParameterTable;

import org.linuxsampler.lscp.MidiInputDevice;
import org.linuxsampler.lscp.MidiPort;
import org.linuxsampler.lscp.Parameter;
import org.linuxsampler.lscp.ParameterFactory;

import static org.jsampler.view.fantasia.FantasiaI18n.i18n;
import static org.jsampler.view.fantasia.FantasiaPrefs.preferences;


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
		optionsPane = new OptionsPane();
		setOptionsPane(optionsPane);
		
		int id = model.getDeviceId();
		String s = model.getDeviceInfo().getDriverName();
		setDeviceName(i18n.getLabel("MidiDevicePane.lDevName", id, s));
	}
	
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
	
	class OptionsPane extends PixmapPane  implements ActionListener, ItemListener,
				ChangeListener, MidiDeviceListener, ParameterListener {
		
		private final JCheckBox checkActive =
			new JCheckBox(i18n.getLabel("MidiDevicePane.checkActive"));
		
		private final JLabel lPorts = new JLabel(i18n.getLabel("MidiDevicePane.lPorts"));
		private final JSpinner spinnerPorts;
		private final JLabel lPort = new JLabel(i18n.getLabel("MidiDevicePane.lPort"));
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
		
		public void
		itemStateChanged(ItemEvent e) {
			boolean a = checkActive.isSelected();
			if(a != midiDeviceModel.isActive()) midiDeviceModel.setBackendActive(a);
		}
		
		public void
		stateChanged(ChangeEvent e) {
			int p = (Integer)spinnerPorts.getValue();
			if(p != midiDeviceModel.getDeviceInfo().getMidiPortCount()) {
				midiDeviceModel.setBackendPortCount(p);
			}
		}
		
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
