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

package org.jsampler.view.std;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.sf.juife.OkCancelDialog;

import net.sf.juife.event.TaskEvent;
import net.sf.juife.event.TaskListener;

import org.jsampler.CC;
import org.jsampler.JSPrefs;
import org.jsampler.MidiInstrumentMap;

import org.jsampler.task.Midi;

import org.linuxsampler.lscp.Instrument;
import org.linuxsampler.lscp.MidiInstrumentEntry;
import org.linuxsampler.lscp.MidiInstrumentInfo;

import static org.jsampler.view.std.StdI18n.i18n;


/**
 *
 * @author Grigor Iliev
 */
public class JSAddMidiInstrumentDlg extends OkCancelDialog {
	private final JLabel lName = new JLabel(i18n.getLabel("JSAddMidiInstrumentDlg.lName"));
	private final JLabel lBank = new JLabel(i18n.getLabel("JSAddMidiInstrumentDlg.lBank"));
	private final JLabel lProgram = new JLabel(i18n.getLabel("JSAddMidiInstrumentDlg.lProgram"));
	private final JLabel lVolume = new JLabel(i18n.getLabel("JSAddMidiInstrumentDlg.lVolume"));
	private final JLabel lLoadMode =
		new JLabel(i18n.getLabel("JSAddMidiInstrumentDlg.lLoadMode"));
	
	private final JTextField tfName = new JTextField();
	private JSpinner spinnerBank;
	private final JComboBox cbProgram = new JComboBox();
	private final JSlider slVolume = new JSlider(0, 100, 100);
	private final JComboBox cbLoadMode = new JComboBox();
	
	private MidiInstrumentMap map;
	private Instrument instr;
	
	private int mbBase;
	private int mpBase;
	
	/**
	 * Creates a new instance of <code>JSAddMidiInstrumentDlg</code>
	 */
	public
	JSAddMidiInstrumentDlg(Frame owner, MidiInstrumentMap map, Instrument instr) {
		super(owner, i18n.getLabel("JSAddMidiInstrumentDlg.title"));
		initAddMidiInstrumentDlg(map, instr);
	}
	
	/**
	 * Creates a new instance of <code>JSAddMidiInstrumentDlg</code>
	 */
	public
	JSAddMidiInstrumentDlg(Dialog owner, MidiInstrumentMap map, Instrument instr) {
		super(owner, i18n.getLabel("JSAddMidiInstrumentDlg.title"));
		initAddMidiInstrumentDlg(map, instr);
	}
	
	private void
	initAddMidiInstrumentDlg(final MidiInstrumentMap map, Instrument instr) {
		mbBase = CC.getViewConfig().getFirstMidiBankNumber();
		mpBase = CC.getViewConfig().getFirstMidiProgramNumber();
		
		spinnerBank = new JSpinner(new SpinnerNumberModel(mbBase, mbBase, 16383 + mbBase, 1));
		
		this.map = map;
		this.instr = instr;
		
		JPanel mainPane = new JPanel();
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		
		mainPane.setLayout(gridbag);
		
		c.fill = GridBagConstraints.NONE;
		
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(3, 3, 3, 3);
		gridbag.setConstraints(lName, c);
		mainPane.add(lName); 
		
		c.gridx = 0;
		c.gridy = 2;
		gridbag.setConstraints(lBank, c);
		mainPane.add(lBank);
		
		c.gridx = 0;
		c.gridy = 3;
		gridbag.setConstraints(lProgram, c);
		mainPane.add(lProgram);
		
		c.gridx = 0;
		c.gridy = 4;
		gridbag.setConstraints(lLoadMode, c);
		mainPane.add(lLoadMode);
		
		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(3, 3, 24, 3);
		gridbag.setConstraints(lVolume, c);
		mainPane.add(lVolume);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1.0;
		c.insets = new Insets(3, 3, 3, 3);
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(tfName, c);
		mainPane.add(tfName);
			
		c.gridx = 1;
		c.gridy = 2;
		gridbag.setConstraints(spinnerBank, c);
		mainPane.add(spinnerBank);
		
		c.gridx = 1;
		c.gridy = 3;
		gridbag.setConstraints(cbProgram, c);
		mainPane.add(cbProgram);
		
		c.gridx = 1;
		c.gridy = 4;
		gridbag.setConstraints(cbLoadMode, c);
		mainPane.add(cbLoadMode);
		
		c.gridx = 1;
		c.gridy = 1;
		c.insets = new Insets(3, 3, 24, 3);
		gridbag.setConstraints(slVolume, c);
		mainPane.add(slVolume);
		
		setMainPane(mainPane);
		
		setResizable(true);
		setMinimumSize(getPreferredSize());
		
		for(int i = 0; i < 128; i++) cbProgram.addItem(new Integer(i + mpBase));
		
		cbLoadMode.addItem(MidiInstrumentInfo.LoadMode.DEFAULT);
		cbLoadMode.addItem(MidiInstrumentInfo.LoadMode.ON_DEMAND);
		cbLoadMode.addItem(MidiInstrumentInfo.LoadMode.ON_DEMAND_HOLD);
		cbLoadMode.addItem(MidiInstrumentInfo.LoadMode.PERSISTENT);
		
		int i = preferences().getIntProperty("std.midiInstrument.loadMode", 0);
		if(cbLoadMode.getItemCount() > i) cbLoadMode.setSelectedIndex(i);
		
		cbLoadMode.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				int j = cbLoadMode.getSelectedIndex();
				if(j < 0) return;
				preferences().setIntProperty("std.midiInstrument.loadMode", j);
			}
		});
		
		tfName.getDocument().addDocumentListener(getHandler());
		
		setInstrumentName(instr.getName());
		
		CC.scheduleInTaskQueue(new Runnable() {
			public void
			run() { setMidiInstrumentEntry(map.getAvailableEntry()); }
		});
		
	}
	
	protected JSPrefs
	preferences() { return CC.getViewConfig().preferences(); }
	
	/**
	 * Sets the MIDI bank and MIDI program of the dialog.
	 */
	public void
	setMidiInstrumentEntry(MidiInstrumentEntry entry) {
		if(entry == null) return;
		setMidiBank(entry.getMidiBank());
		setMidiProgram(entry.getMidiProgram());
	}
	
	/**
	 * Gets the selected MIDI bank (always zero-based).
	 */
	public int
	getMidiBank() { return Integer.parseInt(spinnerBank.getValue().toString()) - mbBase; }
	
	/**
	 * Sets the selected MIDI bank (always zero-based).
	 */
	public void
	setMidiBank(int bank) { spinnerBank.setValue(mbBase + bank); }
	
	/**
	 * Gets the selected MIDI program (always zero-based).
	 */
	public int
	getMidiProgram() { return cbProgram.getSelectedIndex(); }
	
	/**
	 * Sets the selected MIDI program (always zero-based).
	 */
	public void
	setMidiProgram(int program) { cbProgram.setSelectedIndex(program); }
	
	/**
	 * Gets the chosen name for the new MIDI instrument.
	 * @return The chosen name for the new MIDI instrument.
	 */
	public String
	getInstrumentName() { return tfName.getText(); }
	
	/**
	 * Sets the name for the new MIDI instrument.
	 * @param name The name for the new MIDI instrument.
	 */
	public void
	setInstrumentName(String name) { tfName.setText(name); }
	
	/**
	 * Returns the volume level of the new MIDI instrument.
	 * @return The volume level of the new MIDI instrument.
	 */
	public float
	getVolume() {
		float f = slVolume.getValue();
		f /= 100;
		return f;
	}
	
	/** Gets the selected load mode. */
	public MidiInstrumentInfo.LoadMode
	getLoadMode() { return (MidiInstrumentInfo.LoadMode) cbLoadMode.getSelectedItem(); }
	
	protected void
	onOk() {
		if(!btnOk.isEnabled()) return;
		
		btnOk.setEnabled(false);
		preferences().setIntProperty("lastUsedMidiBank", getMidiBank());
		preferences().setIntProperty("lastUsedMidiProgram", getMidiProgram());
		
		MidiInstrumentInfo instrInfo = new MidiInstrumentInfo();
		instrInfo.setName(getInstrumentName());
		instrInfo.setFilePath(instr.getFilePath());
		instrInfo.setInstrumentIndex(instr.getInstrumentIndex());
		instrInfo.setEngine(instr.getEngine());
		instrInfo.setVolume(getVolume());
		instrInfo.setLoadMode(getLoadMode());
		
		int id = map.getMapId();
		int b = getMidiBank();
		int p = getMidiProgram();
		final Midi.MapInstrument t = new Midi.MapInstrument(id, b, p, instrInfo);
		
		t.addTaskListener(new TaskListener() {
			public void
			taskPerformed(TaskEvent e) {
				btnOk.setEnabled(true);
				if(t.doneWithErrors()) return;
				setCancelled(false);
				setVisible(false);
			}
		});
		
		CC.getTaskQueue().add(t);
	}
	
	protected void
	onCancel() { setVisible(false); }
	
	private void
	updateState() {
		boolean b = tfName.getText().length() > 0;
		b = b && cbProgram.getSelectedItem() != null;
		btnOk.setEnabled(b);
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
