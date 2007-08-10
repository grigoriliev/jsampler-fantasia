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

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jsampler.CC;
import org.jsampler.Prefs;

import org.jsampler.task.SetServerAddress;

import static org.jsampler.view.std.StdI18n.i18n;


/**
 *
 * @author Grigor Iliev
 */
public class JSConnectionPropsPane extends JPanel {
	private final JLabel lAddress = new JLabel(i18n.getLabel("JSConnectionPropsPane.address"));
	private final JLabel lPort = new JLabel(i18n.getLabel("JSConnectionPropsPane.port"));
	private final JTextField tfAddress = new JTextField();
	private final JTextField tfPort = new JTextField();
	
	
	/** Creates a new instance of <code>JSConnectionPropsPane</code> */
	public
	JSConnectionPropsPane() {
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
	
		setLayout(gridbag);
		
		// Set preferred size for username & password fields
		int w1 = (int) tfAddress.getMinimumSize().getWidth();
		int h1 = (int) tfAddress.getMinimumSize().getHeight();
		Dimension d = new Dimension(w1 > 150 ? w1 : 150, h1);
		tfAddress.setMinimumSize(d);
		tfAddress.setPreferredSize(d);
	
		w1 = (int) tfPort.getMinimumSize().getWidth();
		h1 = (int) tfPort.getMinimumSize().getHeight();
		d = new Dimension(w1 > 150 ? w1 : 150, h1);
		tfPort.setMinimumSize(d);
		tfPort.setPreferredSize(d);
	
		c.fill = GridBagConstraints.NONE;
	
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(3, 3, 3, 3);
		gridbag.setConstraints(lAddress, c);
		add(lAddress); 

		c.gridx = 0;
		c.gridy = 1;
		gridbag.setConstraints(lPort, c);
		add(lPort);
	
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(tfAddress, c);
		add(tfAddress);
		
		c.gridx = 1;
		c.gridy = 1;
		gridbag.setConstraints(tfPort, c);
		add(tfPort);
		
		String s = i18n.getLabel("JSConnectionPropsPane.title");
		setBorder(BorderFactory.createTitledBorder(s));
		setMaximumSize(new Dimension(Short.MAX_VALUE, getPreferredSize().height));
	}
	
	public String
	getLSAddress() { return tfAddress.getText(); }
	
	public void
	setLSAddress(String address) { tfAddress.setText(address); }
	
	public String
	getLSPort() { return tfPort.getText(); }
	
	public void
	setLSPort(String port) { tfPort.setText(port); }
	
	public void
	apply() {
		Prefs.setLSAddress(getLSAddress());
		
		boolean b = true;
		String s = getLSPort();
		try {
			if(s.length() > 0) {
				int port = Integer.parseInt(s);
				if(port > 0 && port < 0xffff)
					Prefs.setLSPort(port);
				else b = false;
			} else Prefs.setLSPort(-1);	// -1 resets to default value
		} catch(NumberFormatException x) {
			b = false;
		}
		
		if(!b) {
			JOptionPane.showMessageDialog (
				this, 
				i18n.getError("JSConnectionPropsPane.invalidPort", s),
				i18n.getError("error"),
				JOptionPane.ERROR_MESSAGE
			);
			
			return;
		}
		
		CC.getTaskQueue().add (
			new SetServerAddress(Prefs.getLSAddress(), Prefs.getLSPort())
		);
	}
}
