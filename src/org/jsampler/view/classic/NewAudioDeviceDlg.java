/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005 Grigor Kirilov Iliev
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

package org.jsampler.view.classic;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
import org.jsampler.task.CreateAudioDevice;

import org.linuxsampler.lscp.AudioOutputDriver;

import static org.jsampler.view.classic.ClassicI18n.i18n;
import org.jsampler.view.ParameterTable;


/**
 *
 * @author Grigor Iliev
 */
public class NewAudioDeviceDlg extends EnhancedDialog {
	private final JLabel lDriver = new JLabel(i18n.getLabel("NewAudioDeviceDlg.lDriver"));
	private final JComboBox cbDrivers = new JComboBox();
	private final ParameterTable parameterTable = new ParameterTable();
	
	private final JButton btnCreate =
		new JButton(i18n.getButtonLabel("NewAudioDeviceDlg.btnCreate"));
	private final JButton btnCancel = new JButton(i18n.getButtonLabel("cancel"));
	
	/** Creates a new instance of NewMidiDeviceDlg */
	public NewAudioDeviceDlg(Frame owner) {
		super(owner, i18n.getLabel("NewAudioDeviceDlg.title"));
		
		JPanel mainPane = new JPanel();
		mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.add(lDriver);
		
		parameterTable.getModel().setEditFixedParameters(true);
		
		cbDrivers.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				AudioOutputDriver d =
					(AudioOutputDriver)cbDrivers.getSelectedItem();
				if(d == null) return;
				cbDrivers.setToolTipText(d.getDescription());
				parameterTable.getModel().setParameters(d.getParameters());
			}
		});
		
		for(AudioOutputDriver d : CC.getSamplerModel().getAudioOutputDrivers()) {
			cbDrivers.addItem(d);
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
		
		mainPane.setBorder(BorderFactory.createEmptyBorder(12, 12, 17, 12));
		mainPane.setPreferredSize (
			JuifeUtils.getUnionSize(mainPane.getMinimumSize(), new Dimension(250, 200))
		);
		add(mainPane);
		
		JPanel btnPane = new JPanel();
		btnPane.setLayout(new BoxLayout(btnPane, BoxLayout.X_AXIS));
		btnPane.add(Box.createGlue());
		btnPane.add(btnCreate);
		btnPane.add(Box.createRigidArea(new Dimension(5, 0)));
		btnPane.add(btnCancel);
		
		btnPane.setBorder(BorderFactory.createEmptyBorder(0, 12, 12, 12));
		add(btnPane, BorderLayout.SOUTH);
		
		pack();
		
		setLocation(JuifeUtils.centerLocation(this, getOwner()));
		
		btnCancel.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { onCancel(); }
		});
		
		btnCreate.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { onOk(); }
		});
	}
	
	protected void
	onOk() {
		Object obj = cbDrivers.getSelectedItem();
		if(obj == null) {
			JOptionPane.showMessageDialog (
				this, i18n.getMessage("NewAudioDeviceDlg.selectDriver!"),
				"",
				JOptionPane.INFORMATION_MESSAGE
			);
			
			return;
		}
		
		btnCreate.setEnabled(false);
		
		AudioOutputDriver driver = (AudioOutputDriver)obj;
		
		final CreateAudioDevice cad =
			new CreateAudioDevice(driver.getName(), driver.getParameters());
		
		cad.addTaskListener(new TaskListener() {
			public void
			taskPerformed(TaskEvent e) {
				btnCreate.setEnabled(true);
				setVisible(false);
			}
		});
		
		CC.getTaskQueue().add(cad);
	}
	
	protected void
	onCancel() { setVisible(false); }
}
