/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2011 Grigor Iliev <grigor@grigoriliev.com>
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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.sf.juife.swing.OkCancelDialog;

import net.sf.juife.event.TaskEvent;
import net.sf.juife.event.TaskListener;

import org.jsampler.CC;
import org.jsampler.JSPrefs;

import org.jsampler.task.Global;
import org.jsampler.view.swing.SHF;

import org.linuxsampler.lscp.Instrument;
import org.linuxsampler.lscp.MidiInstrumentInfo;
import org.linuxsampler.lscp.SamplerEngine;

import static org.jsampler.view.std.StdI18n.i18n;


/**
 *
 * @author Grigor Iliev
 */
public class JSEditMidiInstrumentDlg extends OkCancelDialog {
	private final JLabel lName = new JLabel(i18n.getLabel("JSEditMidiInstrumentDlg.lName"));
	
	private final JLabel lFilename =
		new JLabel(i18n.getLabel("JSEditMidiInstrumentDlg.lFilename"));
	
	private final JLabel lIndex = new JLabel(i18n.getLabel("JSEditMidiInstrumentDlg.lIndex"));
	
	private final JLabel lEngine = new JLabel(i18n.getLabel("JSEditMidiInstrumentDlg.lEngine"));
	
	private final JLabel lLoadMode =
		new JLabel(i18n.getLabel("JSEditMidiInstrumentDlg.lLoadMode"));
	
	private final JLabel lVolume =
		new JLabel(i18n.getLabel("JSEditMidiInstrumentDlg.lVolume"));
	
	private final JTextField tfName = new JTextField();
	private final JComboBox cbFilename = new JComboBox();
	private final JComboBox cbIndex = new JComboBox();
	private final JComboBox cbEngine = new JComboBox();
	private final JComboBox cbLoadMode = new JComboBox();
	private final JSlider slVolume = StdUtils.createVolumeSlider();
	
	private final MidiInstrumentInfo instrument;
	
	/**
	 * Creates a new instance of <code>JSEditMidiInstrumentDlg</code>.
	 * 
	 * @param instr The instrument whose settings should be edited.
	 */
	public JSEditMidiInstrumentDlg(MidiInstrumentInfo instr) {
		super(SHF.getMainFrame(), i18n.getLabel("JSEditMidiInstrumentDlg.title"));
		
		this.instrument = instr;
		
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		
		JPanel p = new JPanel();
		p.setLayout(gridbag);
		
		c.fill = GridBagConstraints.NONE;
		
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(3, 3, 3, 3);
		gridbag.setConstraints(lName, c);
		p.add(lName); 
		
		c.gridx = 0;
		c.gridy = 1;
		gridbag.setConstraints(lFilename, c);
		p.add(lFilename); 
		
		c.gridx = 0;
		c.gridy = 2;
		gridbag.setConstraints(lIndex, c);
		p.add(lIndex);
		
		c.gridx = 0;
		c.gridy = 3;
		c.insets = new Insets(12, 3, 3, 3);
		gridbag.setConstraints(lEngine, c);
		p.add(lEngine);
		
		c.gridx = 0;
		c.gridy = 4;
		c.insets = new Insets(3, 3, 3, 3);
		gridbag.setConstraints(lLoadMode, c);
		p.add(lLoadMode);
		
		c.gridx = 0;
		c.gridy = 5;
		gridbag.setConstraints(lVolume, c);
		p.add(lVolume);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1.0;
		c.insets = new Insets(3, 12, 3, 3);
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(tfName, c);
		p.add(tfName);
		
		cbFilename.setEditable(true);
		String[] files = preferences().getStringListProperty("recentInstrumentFiles");
		for(String s : files) cbFilename.addItem(s);
		cbFilename.setSelectedItem(null);
		Dimension d = cbFilename.getPreferredSize();
		d.width = 250;
		cbFilename.setPreferredSize(d);
		
		c.gridx = 1;
		c.gridy = 1;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(cbFilename, c);
		p.add(cbFilename);
		
		for(int i = 0; i < 101; i++) cbIndex.addItem(i);
		
		c.gridx = 1;
		c.gridy = 2;
		gridbag.setConstraints(cbIndex, c);
		p.add(cbIndex);
		
		c.gridx = 1;
		c.gridy = 3;
		c.insets = new Insets(12, 12, 3, 64);
		gridbag.setConstraints(cbEngine, c);
		p.add(cbEngine);
		
		c.gridx = 1;
		c.gridy = 4;
		c.insets = new Insets(3, 12, 3, 64);
		gridbag.setConstraints(cbLoadMode, c);
		p.add(cbLoadMode);
			
		c.gridx = 1;
		c.gridy = 5;
		gridbag.setConstraints(slVolume, c);
		p.add(slVolume);
		
		setMainPane(p);
		
		cbFilename.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				updateState();
				updateFileInstruments();
			}
		});
		
		tfName.setText(instr.getName());
		cbFilename.setSelectedItem(instr.getFilePath());
		cbIndex.setSelectedIndex(instr.getInstrumentIndex());
		slVolume.setValue((int)(instr.getVolume() * 100));
		
		for(SamplerEngine e : CC.getSamplerModel().getEngines()) {
			cbEngine.addItem(e);
			if(e.getName().equals(instr.getEngine())) cbEngine.setSelectedItem(e);
		}
		
		cbLoadMode.addItem(MidiInstrumentInfo.LoadMode.DEFAULT);
		cbLoadMode.addItem(MidiInstrumentInfo.LoadMode.ON_DEMAND);
		cbLoadMode.addItem(MidiInstrumentInfo.LoadMode.ON_DEMAND_HOLD);
		cbLoadMode.addItem(MidiInstrumentInfo.LoadMode.PERSISTENT);
		cbLoadMode.setSelectedItem(instr.getLoadMode());
		
		tfName.getDocument().addDocumentListener(getHandler());
	}
	
	protected JSPrefs
	preferences() { return CC.getViewConfig().preferences(); }
	
	public MidiInstrumentInfo
	getInstrument() { return instrument; }
	
	public void
	onOk() {
		instrument.setName(tfName.getText());
		instrument.setFilePath(cbFilename.getSelectedItem().toString());
		instrument.setInstrumentIndex(cbIndex.getSelectedIndex());
		String s = ((SamplerEngine)cbEngine.getSelectedItem()).getName();
		instrument.setEngine(s);
		instrument.setLoadMode((MidiInstrumentInfo.LoadMode)cbLoadMode.getSelectedItem());
		float f = slVolume.getValue();
		f /= 100;
		instrument.setVolume(f);
		
		setCancelled(false);
		setVisible(false);
	}
	
	public void
	onCancel() { setVisible(false); }
	
	private void
	updateState() {
		boolean b = tfName.getText().length() != 0;
		if(cbFilename.getSelectedItem() == null) b = false;
		Object o = cbIndex.getSelectedItem();
		if(o == null || o.toString().length() == 0) b = false;
		btnOk.setEnabled(b);
	}
	
	private boolean init = true;
	
	private void
	updateFileInstruments() {
		Object o = cbFilename.getSelectedItem();
		if(o == null) return;
		String s = o.toString();
		final Global.GetFileInstruments t = new Global.GetFileInstruments(s);
		
		t.addTaskListener(new TaskListener() {
			public void
			taskPerformed(TaskEvent e) {
				Instrument[] instrs = t.getResult();
				if(instrs == null) {
					cbIndex.removeAllItems();
					for(int i = 0; i < 101; i++) cbIndex.addItem(i);
					return;
				} else {
				
					cbIndex.removeAllItems();
					for(int i = 0; i < instrs.length; i++) {
						cbIndex.addItem(i + " - " + instrs[i].getName());
					}
				}
				
				if(init) {
					int i = getInstrument().getInstrumentIndex();
					cbIndex.setSelectedIndex(i);
					init = false;
				}
			}
		});
		
		CC.getTaskQueue().add(t);
	}
	
	private final Handler eventHandler = new Handler();
	
	private Handler
	getHandler() { return eventHandler; }
	
	private class Handler implements DocumentListener {
		// DocumentListener
		public void
		insertUpdate(DocumentEvent e) { updateState(); }
		
		public void
		removeUpdate(DocumentEvent e) { updateState(); }
		
		public void
		changedUpdate(DocumentEvent e) { updateState(); }
	}
}
