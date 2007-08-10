/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2006 Grigor Iliev <grigor@grigoriliev.com>
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

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.sf.juife.OkCancelDialog;

import org.jsampler.CC;
import org.jsampler.DefaultOrchestraModel;
import org.jsampler.OrchestraModel;

import static org.jsampler.view.std.StdI18n.i18n;


/**
 *
 * @author Grigor Iliev
 */
public class JSAddOrEditOrchestraDlg extends OkCancelDialog {
	private final JLabel lName = new JLabel(i18n.getLabel("JSAddOrEditOrchestraDlg.lName"));
	private final JLabel lDesc = new JLabel(i18n.getLabel("JSAddOrEditOrchestraDlg.lDesc"));
	
	private final JTextField tfName = new JTextField();
	private final JTextField tfDesc = new JTextField();
	
	private OrchestraModel orchestraModel;
	
	
	/**
	 * Creates a new instance of <code>JSAddOrEditOrchestraDlg</code>.
	 */
	public JSAddOrEditOrchestraDlg() { this(new DefaultOrchestraModel()); }
	
	/**
	 * Creates a new instance of <code>JSAddOrEditOrchestraDlg</code>.
	 * 
	 * @param orchestra The orchestra model to modify.
	 */
	public JSAddOrEditOrchestraDlg(OrchestraModel orchestra) {
		super(CC.getMainFrame(), i18n.getLabel("JSAddOrEditOrchestraDlg.title"));
		
		orchestraModel = orchestra;
		
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
	
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(tfName, c);
		p.add(tfName);
		
		c.gridx = 1;
		c.gridy = 1;
		gridbag.setConstraints(tfDesc, c);
		p.add(tfDesc);
		
		setMainPane(p);
		
		int w = getPreferredSize().width;
		Dimension d = new Dimension(w > 400 ? w : 400, getPreferredSize().height);
		setPreferredSize(d);
		pack();
		
		setLocationRelativeTo(getOwner());
		
		btnOk.setEnabled(false);
		tfName.getDocument().addDocumentListener(getHandler());
		
		updateInfo();
		updateState();
	}
	
	private void
	updateInfo() {
		tfName.setText(getOrchestra().getName());
		tfDesc.setText(getOrchestra().getDescription());
	}
	
	private void
	updateState() {
		boolean b = true;
		if(tfName.getText().length() == 0) b = false;
		
		btnOk.setEnabled(b);
	}
	
	protected void
	onOk() {
		if(!btnOk.isEnabled()) return;
		
		orchestraModel.setName(tfName.getText());
		orchestraModel.setDescription(tfDesc.getText());
		setVisible(false);
		
		setCancelled(false);
	}
	
	protected void
	onCancel() {
		setVisible(false);
	}
	
	/**
	 * Gets the newly created orchestra.
	 * @return The newly created orchestra or <code>null</code> if
	 * the Cancel button is pressed.
	 */
	public OrchestraModel
	getOrchestra() { return orchestraModel; }
	
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
