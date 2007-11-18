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

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.sf.juife.EnhancedDialog;
import net.sf.juife.JuifeUtils;

import net.sf.juife.event.TaskEvent;
import net.sf.juife.event.TaskListener;

import org.jsampler.CC;
import org.jsampler.JSPrefs;

import org.jsampler.event.ParameterEvent;
import org.jsampler.event.ParameterListener;

import org.jsampler.task.Audio;
import org.jsampler.view.ParameterTable;

import org.linuxsampler.lscp.AudioOutputDriver;
import org.linuxsampler.lscp.Parameter;

import static org.jsampler.view.std.StdI18n.i18n;
import static org.jsampler.view.std.StdPrefs.*;


/**
 *
 * @author Grigor Iliev
 */
public class JSNewAudioDeviceDlg extends EnhancedDialog {
	private final Pane mainPane = new Pane();
	
	/** Creates a new instance of NewMidiDeviceDlg */
	public JSNewAudioDeviceDlg(Frame owner) {
		super(owner, i18n.getLabel("JSNewAudioDeviceDlg.title"));
		
		initNewAudioDeviceDlg();
	}
	
	/** Creates a new instance of NewMidiDeviceDlg */
	public
	JSNewAudioDeviceDlg(Dialog owner) {
		super(owner, i18n.getLabel("JSNewAudioDeviceDlg.title"));
		
		initNewAudioDeviceDlg();
	}
	
	private void
	initNewAudioDeviceDlg() {
		mainPane.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
		mainPane.setPreferredSize (
			JuifeUtils.getUnionSize(mainPane.getMinimumSize(), new Dimension(250, 230))
		);
		add(mainPane);
		
		pack();
		
		setLocation(JuifeUtils.centerLocation(this, getOwner()));
		
		mainPane.btnCancel.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { onCancel(); }
		});
		
		mainPane.btnCreate.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { onOk(); }
		});
		
		addWindowListener(new WindowAdapter() {
			public void
			windowActivated(WindowEvent e) { mainPane.btnCreate.requestFocusInWindow(); }
		});
	}
	
	protected void
	onOk() {
		AudioOutputDriver driver = mainPane.getSelectedDriver();
		if(driver == null) {
			JOptionPane.showMessageDialog (
				this, i18n.getMessage("JSNewAudioDeviceDlg.selectDriver!"),
				"",
				JOptionPane.INFORMATION_MESSAGE
			);
			
			return;
		}
		
		mainPane.btnCreate.setEnabled(false);
		
		final Audio.CreateDevice cad =
			new Audio.CreateDevice(driver.getName(), mainPane.getParameters());
		
		cad.addTaskListener(new TaskListener() {
			public void
			taskPerformed(TaskEvent e) {
				mainPane.btnCreate.setEnabled(true);
				setVisible(false);
			}
		});
		
		CC.getTaskQueue().add(cad);
	}
	
	protected void
	onCancel() { setVisible(false); }
	
	private static JSPrefs
	preferences() { return CC.getViewConfig().preferences(); }
	
	public static class Pane extends JPanel {
		private final JLabel lDriver = new JLabel(i18n.getLabel("JSNewAudioDeviceDlg.lDriver"));
		private final JComboBox cbDrivers = new JComboBox();
		private final ParameterTable parameterTable = new ParameterTable();
		
		public final JButton btnCreate =
			new JButton(i18n.getButtonLabel("JSNewAudioDeviceDlg.btnCreate"));
		public final JButton btnCancel = new JButton(i18n.getButtonLabel("cancel"));
	
		public
		Pane() {
			setLayout(new BorderLayout());
			JPanel mainPane = new JPanel();
			mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
			
			JPanel p = new JPanel();
			p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
			p.add(lDriver);
			
			parameterTable.getModel().setEditFixedParameters(true);
			
			parameterTable.getModel().addParameterListener(new ParameterListener() {
				public void
				parameterChanged(ParameterEvent e) {
					updateParameters();
				}
			});
			
			cbDrivers.addActionListener(new ActionListener() {
				public void
				actionPerformed(ActionEvent e) {
					AudioOutputDriver d =
						(AudioOutputDriver)cbDrivers.getSelectedItem();
					if(d == null) return;
					cbDrivers.setToolTipText(d.getDescription());
					parameterTable.getModel().setParameters(d.getParameters());
					updateParameters();
				}
			});
			
			for(AudioOutputDriver d : CC.getSamplerModel().getAudioOutputDrivers()) {
				cbDrivers.addItem(d);
			}
			
			String s = preferences().getStringProperty(DEFAULT_AUDIO_DRIVER);
			for(AudioOutputDriver d : CC.getSamplerModel().getAudioOutputDrivers()) {
				if(d.getName().equals(s)) {
					cbDrivers.setSelectedItem(d);
					break;
				}
			}
			
			cbDrivers.setMaximumSize(cbDrivers.getPreferredSize());
			p.add(Box.createRigidArea(new Dimension(5, 0)));
			p.add(cbDrivers);
			
			p.setAlignmentX(LEFT_ALIGNMENT);
			mainPane.add(p);
			
			mainPane.add(Box.createRigidArea(new Dimension(0, 12)));
			
			//parameterTable.setModel(new ParameterTableModel(CC.getSamplerModel().));
			JScrollPane sp = new JScrollPane(parameterTable);
			sp.setAlignmentX(LEFT_ALIGNMENT);
			mainPane.add(sp);
			
			add(mainPane);
			
			JPanel btnPane = new JPanel();
			btnPane.setLayout(new BoxLayout(btnPane, BoxLayout.X_AXIS));
			btnPane.add(Box.createGlue());
			btnPane.add(btnCreate);
			btnPane.add(Box.createRigidArea(new Dimension(5, 0)));
			btnPane.add(btnCancel);
			
			btnPane.setBorder(BorderFactory.createEmptyBorder(17, 0, 0, 0));
			add(btnPane, BorderLayout.SOUTH);
			
		}
		
		/**
		 * Stops any cell editing in progress and returns the parameters
		 */
		public Parameter[]
		getParameters() {
			if(parameterTable.getCellEditor() != null) {
				parameterTable.getCellEditor().stopCellEditing();
			}
			return parameterTable.getModel().getParameters();
		}
		
		public AudioOutputDriver
		getSelectedDriver() {
			return (AudioOutputDriver)cbDrivers.getSelectedItem();
		}
		
		private void
		updateParameters() {
			AudioOutputDriver d = (AudioOutputDriver)cbDrivers.getSelectedItem();
			if(d == null) return;
			
			final Parameter[] parameters = parameterTable.getModel().getParameters();
			
			final Audio.GetDriverParametersInfo task =
				new Audio.GetDriverParametersInfo(d.getName(), parameters);
				
			task.addTaskListener(new TaskListener() {
				public void
				taskPerformed(TaskEvent e) {
					if(task.doneWithErrors()) return;
					for(Parameter p : parameters) {
						for(Parameter p2 : task.getResult()) {
							if(p2.getName().equals(p.getName())) {
								p2.setValue(p.getValue());
								if(p2.getValue() == null) {
									p2.setValue(p2.getDefault());
								}
								break;
							}
						}
					}
					
					parameterTable.getModel().setParameters(task.getResult());
				}
			});
			
			CC.getTaskQueue().add(task);
		}
	}
}
