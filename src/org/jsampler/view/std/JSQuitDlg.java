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

import java.awt.BorderLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sf.juife.EnhancedDialog;
import net.sf.juife.JuifeUtils;

import org.jsampler.CC;

import static org.jsampler.view.std.StdI18n.i18n;

/**
 *
 * @author Grigor Iliev
 */
public class JSQuitDlg extends EnhancedDialog {
	private final JButton btnExport = new JButton(i18n.getButtonLabel("JSQuitDlg.btnExport"));
	private final JButton btnQuit = new JButton(i18n.getButtonLabel("JSQuitDlg.btnQuit"));
	private final JButton btnDontQuit = new JButton(i18n.getButtonLabel("JSQuitDlg.btnDontQuit"));
	
	/** Creates a new instance of <code>JSQuitDlg</code> */
	public
	JSQuitDlg(Icon questionIcon) {
		super(CC.getMainFrame());
		getContentPane().setLayout(new BorderLayout());
		
		JLabel l = new JLabel(i18n.getMessage("JSQuitDlg.sureToQuit?"));
		l.setIcon(questionIcon);
		l.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 12));
		getContentPane().add(l);
		
		JPanel btnPane = new JPanel();
		btnPane.setLayout(new BoxLayout(btnPane, BoxLayout.X_AXIS));
		btnPane.add(btnExport);
		btnPane.add(Box.createRigidArea(new java.awt.Dimension(17, 0)));
		btnPane.add(Box.createGlue());
		btnPane.add(btnQuit);
		btnPane.add(Box.createRigidArea(new java.awt.Dimension(5, 0)));
		btnPane.add(btnDontQuit);
		
		btnPane.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
		
		getContentPane().add(btnPane, BorderLayout.SOUTH);
		pack();
		
		setLocation(JuifeUtils.centerLocation(this, CC.getMainFrame()));
		
		btnQuit.requestFocus();
		btnQuit.setMnemonic('q');
		
		btnExport.setMnemonic('x');
		btnExport.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				StdA4n.a4n.exportSamplerConfig();
			}
		});
		
		btnQuit.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				onOk();
			}
		});
		
		btnDontQuit.setMnemonic('n');
		btnDontQuit.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				onCancel();
			}
		});
		
		setCancelled(true);
	}
	
	protected void
	onOk() {
		setCancelled(false);
		setVisible(false);
	}
	
	protected void
	onCancel() { setVisible(false); }
}
