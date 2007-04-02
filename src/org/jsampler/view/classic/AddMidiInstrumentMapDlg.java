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

package org.jsampler.view.classic;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.sf.juife.OkCancelDialog;

import org.jsampler.CC;

import static org.jsampler.view.classic.ClassicI18n.i18n;


/**
 *
 * @author Grigor Iliev
 */
public class AddMidiInstrumentMapDlg extends OkCancelDialog {
	private final JLabel lName = new JLabel(i18n.getLabel("AddMidiInstrumentMapDlg.lName"));
	private final JTextField tfName = new JTextField();
	
	/** Creates a new instance of <code>AddMidiInstrumentMapDlg</code> */
	public
	AddMidiInstrumentMapDlg() {
		super(CC.getMainFrame(), i18n.getLabel("AddMidiInstrumentMapDlg.title"));
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.add(lName);
		p.add(Box.createRigidArea(new Dimension(6, 0)));
		p.add(tfName);
		setMainPane(p);
		updateState();
		tfName.getDocument().addDocumentListener(getHandler());
	}
	
	/**
	 * Gets the chosen name for the new MIDI instrument map.
	 * @return The chosen name for the new MIDI instrument map.
	 */
	public String
	getMapName() { return tfName.getText(); }
	
	/**
	 * Sets the text name text field.
	 */
	public void
	setMapName(String name) { tfName.setText(name); }
	
	protected void
	onOk() {
		if(!btnOk.isEnabled()) return;
		
		setVisible(false);
		setCancelled(false);
	}
	
	protected void
	onCancel() { setVisible(false); }
	
	private void
	updateState() { btnOk.setEnabled(tfName.getText().length() != 0); }
	
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
