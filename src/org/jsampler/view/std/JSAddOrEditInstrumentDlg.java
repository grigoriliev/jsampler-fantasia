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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.sf.juife.OkCancelDialog;

import org.jsampler.CC;
import org.jsampler.Instrument;
import org.jsampler.JSPrefs;

import static org.jsampler.view.std.StdI18n.i18n;
import static org.linuxsampler.lscp.Parser.*;


/**
 *
 * @author Grigor Iliev
 */
public class JSAddOrEditInstrumentDlg extends OkCancelDialog {
	private final JLabel lName = new JLabel(i18n.getLabel("JSAddOrEditInstrumentDlg.lName"));
	private final JLabel lDesc = new JLabel(i18n.getLabel("JSAddOrEditInstrumentDlg.lDesc"));
	private final JLabel lPath = new JLabel(i18n.getLabel("JSAddOrEditInstrumentDlg.lPath"));
	private final JLabel lIndex = new JLabel(i18n.getLabel("JSAddOrEditInstrumentDlg.lIndex"));
	
	private final JButton btnBrowse =
		new JButton(i18n.getButtonLabel("browse"));
	
	private final JTextField tfName = new JTextField();
	private final JTextField tfDesc = new JTextField();
	private final JComboBox cbPath = new JComboBox();
	private final JSpinner spinnerIndex = new JSpinner(new SpinnerNumberModel(0, 0, 500, 1));
	
	private Instrument instrument;
	
	
	/**
	 * Creates a new instance of <code>JSAddOrEditInstrumentDlg</code>.
	 */
	public JSAddOrEditInstrumentDlg() { this(new Instrument()); }
	
	/**
	 * Creates a new instance of <code>JSAddOrEditInstrumentDlg</code>.
	 * 
	 * @param instr The instrument to modify.
	 */
	public JSAddOrEditInstrumentDlg(Instrument instr) {
		super(CC.getMainFrame(), i18n.getLabel("JSAddOrEditInstrumentDlg.title"));
		
		instrument = instr;
		
		cbPath.setEditable(true);
		String[] files = preferences().getStringListProperty("recentInstrumentFiles");
		for(String s : files) cbPath.addItem(s);
		cbPath.setSelectedItem(null);
		
		cbPath.setPreferredSize (
			new Dimension(200, cbPath.getPreferredSize().height)
		);
		
		JPanel p = new JPanel();
		
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
	
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
		gridbag.setConstraints(lDesc, c);
		p.add(lDesc);
	
		c.gridx = 0;
		c.gridy = 2;
		gridbag.setConstraints(lPath, c);
		p.add(lPath);
	
		c.gridx = 2;
		c.gridy = 2;
		gridbag.setConstraints(btnBrowse, c);
		p.add(btnBrowse);
	
		c.gridx = 0;
		c.gridy = 3;
		gridbag.setConstraints(lIndex, c);
		p.add(lIndex);
	
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1.0;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(tfName, c);
		p.add(tfName);
		
		c.gridx = 1;
		c.gridy = 1;
		gridbag.setConstraints(tfDesc, c);
		p.add(tfDesc);
		
		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 1;
		gridbag.setConstraints(cbPath, c);
		p.add(cbPath);
		
		c.gridx = 1;
		c.gridy = 3;
		c.gridwidth = 2;
		gridbag.setConstraints(spinnerIndex, c);
		p.add(spinnerIndex);
		
		setMainPane(p);
		
		int w = getPreferredSize().width;
		Dimension d = new Dimension(w > 500 ? w : 500, getPreferredSize().height);
		setPreferredSize(d);
		pack();
		
		setLocationRelativeTo(getOwner());
		
		tfName.getDocument().addDocumentListener(getHandler());
		btnBrowse.addActionListener(getHandler());
		cbPath.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { updateState(); }
		});
		
		updateInfo();
		updateState();
	}
	
	protected JSPrefs
	preferences() { return CC.getViewConfig().preferences(); }
	
	private void
	updateInfo() {
		tfName.setText(getInstrument().getName());
		tfDesc.setText(getInstrument().getDescription());
		cbPath.setSelectedItem(getInstrument().getPath());
		spinnerIndex.setValue(getInstrument().getInstrumentIndex());
	}
	
	private void
	updateState() {
		boolean b = true;
		if(tfName.getText().length() == 0) b = false;
		Object o = cbPath.getSelectedItem();
		if(o == null || o.toString().length() == 0) b = false;
		
		btnOk.setEnabled(b);
	}
	
	protected void
	onOk() {
		if(!btnOk.isEnabled()) return;
		
		instrument.setName(tfName.getText());
		instrument.setDescription(tfDesc.getText());
		instrument.setPath(cbPath.getSelectedItem().toString());
		int idx = Integer.parseInt(spinnerIndex.getValue().toString());
		instrument.setInstrumentIndex(idx);
		
		StdUtils.updateRecentElements("recentInstrumentFiles", instrument.getPath());
		
		setVisible(false);
		setCancelled(false);
	}
	
	protected void
	onCancel() {
		setVisible(false);
	}
	
	/**
	 * Gets the created/modified orchestra.
	 * @return The created/modified orchestra.
	 */
	public Instrument
	getInstrument() { return instrument; }
	
	
	private final Handler eventHandler = new Handler();
	
	private Handler
	getHandler() { return eventHandler; }
	
	private class Handler implements DocumentListener, ActionListener {
		// DocumentListener
		public void
		insertUpdate(DocumentEvent e) { updateState(); }
		
		public void
		removeUpdate(DocumentEvent e) { updateState(); }
		
		public void
		changedUpdate(DocumentEvent e) { updateState(); }
		
		// ActionListener
		public void
		actionPerformed(ActionEvent e) {
			String path = preferences().getStringProperty("lastInstrumentLocation");
			JFileChooser fc = new JFileChooser(path);
			int result = fc.showOpenDialog(JSAddOrEditInstrumentDlg.this);
			if(result != JFileChooser.APPROVE_OPTION) return;
			
			String s = toEscapedString(fc.getSelectedFile().getAbsolutePath());
			cbPath.setSelectedItem(s);
			path = fc.getCurrentDirectory().getAbsolutePath();
			preferences().setStringProperty("lastInstrumentLocation", path);
		}
	}
}
