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

import java.awt.BorderLayout;
import java.awt.Dimension;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;

import net.sf.juife.EnhancedDialog;

import org.jsampler.CC;
import org.jsampler.JSPrefs;
import org.jsampler.Server;

import static org.jsampler.view.std.StdI18n.i18n;


/**
 *
 * @author Grigor Iliev
 */
public class JSConnectDlg extends EnhancedDialog {
	private final JSConnectionPropsPane.ServerListPane serverListPane =
		new JSConnectionPropsPane.ServerListPane();
	
	private final JButton btnConnect =
		new JButton(i18n.getButtonLabel("JSConnectDlg.btnConnect"));
	
	/** Creates a new instance of <code>JSConnectDlg</code> */
	public
	JSConnectDlg() {
		super(CC.getMainFrame(), i18n.getLabel("JSConnectDlg.title"));
		
		JPanel mainPane = new JPanel();
		mainPane.setLayout(new BorderLayout());
		
		mainPane.add(serverListPane);
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.add(Box.createGlue());
		p.add(btnConnect);
		p.add(Box.createGlue());
		p.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
		
		mainPane.add(p, BorderLayout.SOUTH);
		mainPane.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		mainPane.setPreferredSize(new java.awt.Dimension(300, 300));
		
		btnConnect.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { onOk(); }
		});
		
		getContentPane().add(mainPane);
		
		pack();
		setMinimumSize(getPreferredSize());
		
		btnConnect.requestFocus();
		
		setLocationRelativeTo(getOwner());
	}
	
	private static JSPrefs
	preferences() { return CC.getViewConfig().preferences(); }
	
	protected void
	onOk() {
		setVisible(false);
		setCancelled(false);
	}
	
	protected void
	onCancel() {
		setVisible(false);
	}
	
	/** Returns the server that is selected by the user to connect. */
	public Server
	getSelectedServer() {
		if(isCancelled()) return null;
		return serverListPane.serverTable.getSelectedServer();
	}
}
