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

import java.awt.Dialog;
import java.awt.Frame;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sf.juife.OkCancelDialog;

import static org.jsampler.view.classic.ClassicI18n.i18n;


/**
 *
 * @author Grigor Iliev
 */
public class DbDescriptionDlg extends OkCancelDialog {
	private final JTextField tfDesc = new JTextField();
	
	/** Creates a new instance of <code>DbDescriptionDlg</code> */
	public
	DbDescriptionDlg(Frame owner) {
		super(owner, i18n.getLabel("DbDescriptionDlg.title"));
		initDbDescriptionDlg();
	}
	
	/** Creates a new instance of <code>DbDescriptionDlg</code> */
	public
	DbDescriptionDlg(Dialog owner) {
		super(owner, i18n.getLabel("DbDescriptionDlg.title"));
		initDbDescriptionDlg();
	}
	
	private void
	initDbDescriptionDlg() {
		setMainPane(tfDesc);
		setMinimumSize(getPreferredSize());
		setResizable(true);
	}
	
	protected void
	onOk() {
		if(!btnOk.isEnabled()) return;
		setCancelled(false);
		setVisible(false);
	}
	
	protected void
	onCancel() { setVisible(false); }
	
	public String
	getDescription() { return tfDesc.getText(); }
	
	public void
	setDescription(String s) { tfDesc.setText(s); }
}
