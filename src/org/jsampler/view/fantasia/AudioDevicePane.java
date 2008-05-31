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

import org.jsampler.AudioDeviceModel;
import org.jsampler.CC;

import org.jsampler.event.AudioDeviceEvent;
import org.jsampler.event.AudioDeviceListener;
import org.jsampler.event.ParameterEvent;
import org.jsampler.event.ParameterListener;

import org.jsampler.task.Audio;
import org.jsampler.view.ParameterTable;

import org.linuxsampler.lscp.AudioOutputChannel;
import org.linuxsampler.lscp.AudioOutputDevice;
import org.linuxsampler.lscp.Parameter;
import org.linuxsampler.lscp.ParameterFactory;

import static org.jsampler.view.fantasia.FantasiaI18n.i18n;
import static org.jsampler.view.fantasia.FantasiaPrefs.preferences;

/**
 *
 * @author Grigor Iliev
 */
public class AudioDevicePane extends DevicePane {
	private final OptionsPane optionsPane;
	private final ParameterTable channelParamTable = new ParameterTable();
	
	private final AudioDeviceModel audioDeviceModel;
	
	
	/** Creates a new instance of <code>AudioDevicePane</code> */
	public
	AudioDevicePane(AudioDeviceModel model) {
		audioDeviceModel = model;
		
		channelParamTable.setFillsViewportHeight(true);
		
		optionsPane = new OptionsPane();
		setOptionsPane(optionsPane);
		
		int id = model.getDeviceId();
		String s = model.getDeviceInfo().getDriverName();
		setDeviceName(i18n.getLabel("AudioDevicePane.lDevName", id, s));
	}
	
	protected void
	destroyDevice() {
		final Task t = new Audio.DestroyDevice(getDeviceId());
		t.addTaskListener(new TaskListener() {
			public void
			taskPerformed(TaskEvent e) {
				if(t.doneWithErrors()) restoreDevice();
			}
		});
		
		CC.getTaskQueue().add(t);
	}
	
	public int
	getDeviceId() { return audioDeviceModel.getDeviceId(); }
	
	class OptionsPane extends PixmapPane implements ActionListener, ItemListener,
				ChangeListener, AudioDeviceListener, ParameterListener {
		
		private final JCheckBox checkActive =
			new JCheckBox(i18n.getLabel("AudioDevicePane.checkActive"));
		
		private final JLabel lChannels
			= new JLabel(i18n.getLabel("AudioDevicePane.lChannels"));
		
		private final JSpinner spinnerChannels;
		
		private final JLabel lChannel =
			new JLabel(i18n.getLabel("AudioDevicePane.lChannel"));
		
		private final JComboBox cbChannel = new JComboBox();
		
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
			p.add(lChannels);
			p.add(Box.createRigidArea(new Dimension(5, 0)));
			
			Parameter<Integer> prm =
				audioDeviceModel.getDeviceInfo().getChannelsParameter();
			int min = 1;
			if(prm.getRangeMin() != null) min = prm.getRangeMin().intValue();
			int max = 10000;
			if(prm.getRangeMax() != null) max = prm.getRangeMax().intValue();
			
			spinnerChannels = new JSpinner(new SpinnerNumberModel(1, min, max, 1));
			if(prm.isFixed()) spinnerChannels.setEnabled(false);
			int h = spinnerChannels.getPreferredSize().height;
			spinnerChannels.setPreferredSize(new Dimension(30, h));
			p.add(spinnerChannels);
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
			p2.add(lChannel);
			p2.add(Box.createRigidArea(new Dimension(5, 0)));
			p2.add(cbChannel);
			p2.setOpaque(false);
			p.add(p2);
			
			p.add(Box.createRigidArea(new Dimension(0, 5)));
			
			sp = new JScrollPane(channelParamTable);
			sp.setPreferredSize(new Dimension(77, 90));
			p.add(sp);
			
			mainPane.add(p);
			add(mainPane);
			
			checkActive.setSelected(audioDeviceModel.isActive());
			spinnerChannels.setValue(audioDeviceModel.getDeviceInfo().getChannelCount());
			
			cbChannel.addActionListener(this);
			checkActive.addItemListener(this);
			spinnerChannels.addChangeListener(this);
			audioDeviceModel.addAudioDeviceListener(this);
			channelParamTable.getModel().addParameterListener(this);
			
			AudioDeviceModel m = audioDeviceModel;
			for(AudioOutputChannel chn : m.getDeviceInfo().getAudioChannels()) {
				cbChannel.addItem(chn);
			}
			
			updateParams(audioDeviceModel.getDeviceInfo());
			additionalParamsTable.getModel().addParameterListener(new ParameterListener() {
				public void
				parameterChanged(ParameterEvent e) {
					audioDeviceModel.setBackendDeviceParameter(e.getParameter());
				}
			});
			
			updateAdditionalParamsViewState();
			String s = "AudioDevice.showAdditionalParameters";
			preferences().addPropertyChangeListener(s, new PropertyChangeListener() {
				public void
				propertyChange(PropertyChangeEvent e) {
					updateAdditionalParamsViewState();
				}
			});
		}
		
		private void
		updateAdditionalParamsViewState() {
			String s = "AudioDevice.showAdditionalParameters";
			additionalParamsPane.setVisible(preferences().getBoolProperty(s));
			validate();
		}
		
		public void
		actionPerformed(ActionEvent e) {
			Object obj = cbChannel.getSelectedItem();
			if(obj == null) {
				channelParamTable.getModel().setParameters(new Parameter[0]);
				return;
			}
			
			AudioOutputChannel chn = (AudioOutputChannel)obj;
			
			channelParamTable.getModel().setParameters(chn.getAllParameters());
		}
		
		public void
		itemStateChanged(ItemEvent e) {
			boolean a = checkActive.isSelected();
			if(a != audioDeviceModel.isActive()) audioDeviceModel.setBackendActive(a);
		}
		
		public void
		stateChanged(ChangeEvent e) {
			int c = (Integer)spinnerChannels.getValue();
			if(c != audioDeviceModel.getDeviceInfo().getAudioChannelCount()) {
				audioDeviceModel.setBackendChannelCount(c);
			}
		}
		
		public void
		settingsChanged(AudioDeviceEvent e) {
			int c = (Integer)spinnerChannels.getValue();
			int nc = audioDeviceModel.getDeviceInfo().getAudioChannelCount();
			if(c != nc) spinnerChannels.setValue(nc);
			
			boolean a = checkActive.isSelected();
			boolean na = audioDeviceModel.isActive();
			if(a != na) checkActive.setSelected(na);
			
			AudioOutputDevice d = e.getAudioDeviceModel().getDeviceInfo();
			updateParams(d);
			
			int idx = cbChannel.getSelectedIndex();
			cbChannel.removeAllItems();
			for(AudioOutputChannel chn : d.getAudioChannels()) cbChannel.addItem(chn);
			
			if(idx >= cbChannel.getModel().getSize()) idx = 0;
			
			if(cbChannel.getModel().getSize() > 0) cbChannel.setSelectedIndex(idx);
		}
		
		public void
		parameterChanged(ParameterEvent e) {
			int c = cbChannel.getSelectedIndex();
			if(c == -1) {
				CC.getLogger().warning("There is no audio channel selected!");
				return;
			}
			
			audioDeviceModel.setBackendChannelParameter(c, e.getParameter());
		}
		
		private void
		updateParams(AudioOutputDevice d) {
			Parameter p = d.getSampleRateParameter();
			boolean b = p == null || p.getName() == null || p.getValue() == null;
			Parameter[] params = d.getAdditionalParameters();
			Parameter[] p2s;
			if(b) p2s = new Parameter[params.length];
			else p2s = new Parameter[params.length + 1];
			
			for(int i = 0; i < params.length; i++) p2s[i] = params[i];
			
			if(!b) p2s[params.length] = p;
			
			additionalParamsTable.getModel().setParameters(p2s);
		}
	}
	
}
