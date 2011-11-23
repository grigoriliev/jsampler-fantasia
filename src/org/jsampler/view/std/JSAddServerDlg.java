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

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.sf.juife.swing.OkCancelDialog;

import org.jsampler.CC;
import org.jsampler.Server;
import org.jsampler.view.swing.SHF;

import static org.jsampler.view.std.StdI18n.i18n;

/**
 *
 * @author Grigor Iliev
 */
public class JSAddServerDlg extends OkCancelDialog {
	private final JLabel lName = new JLabel(i18n.getLabel("JSAddServerDlg.lName"));
	private final JLabel lDesc = new JLabel(i18n.getLabel("JSAddServerDlg.lDesc"));
	private final JLabel lAddress = new JLabel(i18n.getLabel("JSAddServerDlg.lAddress"));
	private final JLabel lPort = new JLabel(i18n.getLabel("JSAddServerDlg.lPort"));
	
	private final JTextField tfName = new JTextField();
	private final JTextField tfDesc = new JTextField();
	private final JTextField tfAddress = new JTextField();
	private final JTextField tfPort = new JTextField();
	
	
	/**
	 * Creates a new instance of <code>JSAddServerDlg</code>
	 * @param owner Specifies the <code>Frame</code> from which this dialog is displayed.
	 */
	public
	JSAddServerDlg(Frame owner) {
		super(owner, i18n.getLabel("JSAddServerDlg.title"));
		
		initJSAddServerDlg();
	}
	
	/**
	 * Creates a new instance of <code>JSAddServerDlg</code>
	 * @param owner Specifies the <code>Dialog</code> from which this dialog is displayed.
	 */
	public
	JSAddServerDlg(Dialog owner) {
		super(owner, i18n.getLabel("JSAddServerDlg.title"));
		
		initJSAddServerDlg();
	}
	
	private void
	initJSAddServerDlg() {
		btnOk.setEnabled(false);
		
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
		c.gridy = 1;
		gridbag.setConstraints(lDesc, c);
		mainPane.add(lDesc);
		
		c.gridx = 0;
		c.gridy = 2;
		gridbag.setConstraints(lAddress, c);
		mainPane.add(lAddress);
		
		c.gridx = 0;
		c.gridy = 3;
		gridbag.setConstraints(lPort, c);
		mainPane.add(lPort);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(tfName, c);
		mainPane.add(tfName);
		
		c.gridx = 1;
		c.gridy = 1;
		gridbag.setConstraints(tfDesc, c);
		mainPane.add(tfDesc);
		
		c.gridx = 1;
		c.gridy = 2;
		gridbag.setConstraints(tfAddress, c);
		mainPane.add(tfAddress);
		
		c.gridx = 1;
		c.gridy = 3;
		gridbag.setConstraints(tfPort, c);
		mainPane.add(tfPort);
		
		setMainPane(mainPane);
		setResizable(true);
		
		int w = getPreferredSize().width;
		Dimension d = new Dimension(w > 300 ? w : 300, getPreferredSize().height);
		setPreferredSize(d);
		setMinimumSize(d);
		pack();
		
		setLocationRelativeTo(getOwner());
		
		tfName.getDocument().addDocumentListener(getHandler());
		tfAddress.getDocument().addDocumentListener(getHandler());
		tfPort.getDocument().addDocumentListener(getHandler());
	}
	
	protected void
	onOk() {
		if(!btnOk.isEnabled()) return;
		
		Server server = new Server();
		server.setName(tfName.getText());
		server.setDescription(tfDesc.getText());
		server.setAddress(tfAddress.getText());
		
		int p;
		String s = tfPort.getText();
		try { p = Integer.parseInt(s); }
		catch(Exception x) {
			SHF.showErrorMessage(i18n.getError("JSAddServerDlg.invalidPort", s), this);
			return;
		}
		
		if(p < 0 || p > 0xffff) {
			SHF.showErrorMessage(i18n.getError("JSAddServerDlg.invalidPort", s), this);
			return;
		}
		
		server.setPort(p);
		CC.getServerList().addServer(server);
		
		setVisible(false);
		setCancelled(false);
	}
	
	private void
	updateState() {
		boolean b = true;
		if(tfName.getText().length() == 0) b = false;
		if(tfAddress.getText().length() == 0) b = false;
		if(tfPort.getText().length() == 0) b = false;
		
		btnOk.setEnabled(b);
	}
	
	protected void
	onCancel() { setVisible(false); }
	
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
