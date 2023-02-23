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

package org.jsampler.view.classic;

import java.awt.Frame;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;

import org.jsampler.view.swing.SHF;

import static org.jsampler.view.classic.ClassicI18n.i18n;


/**
 *
 * @author Grigor Iliev
 */
public class LSConsoleDlg extends JDialog {
	/** Creates a new instance of <code>LSConsoleDlg<code>. */
	public
	LSConsoleDlg(Frame owner, LSConsolePane pane) {
		super(owner, i18n.getLabel("LSConsoleDlg.title"), false);
		add(pane);
		pane.setOwner(this);
		
		pack();
		setSize(400, 200);
		setLocationRelativeTo(owner);
		
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void
			windowClosing(WindowEvent e) {
				MainFrame mainFrame = (MainFrame)SHF.getMainFrame();
				mainFrame.setLSConsoleShown(false);
				setVisible(false);
			}
		});
	}
	
}
