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

package org.jsampler.view.classic;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.FileOutputStream;

import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import org.jsampler.CC;
import org.jsampler.HF;

import org.jsampler.view.LscpFileFilter;

import static org.jsampler.view.classic.ClassicI18n.i18n;


/**
 *
 * @author Grigor Iliev
 */
public class LscpScriptDlg extends JDialog {
	private final JTextPane textPane = new JTextPane();
	private final JButton btnSaveAs =
		new JButton(i18n.getButtonLabel("LscpScriptDlg.btnSaveAs"));
	private final JButton btnClose = new JButton(i18n.getButtonLabel("close"));
	
	
	/** Creates a new instance of <code>LscpScriptDlg</code>. */
	public
	LscpScriptDlg() { this(CC.getMainFrame()); }
	
	/** Creates a new instance of <code>LscpScriptDlg</code>. */
	public
	LscpScriptDlg(Frame owner) {
		super(owner);
		
		JPanel mainPane = new JPanel();
		
		mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
		mainPane.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		
		JScrollPane sp = new JScrollPane(textPane);
		sp.setAlignmentX(JPanel.RIGHT_ALIGNMENT);
		mainPane.add(sp);
		
		mainPane.add(Box.createRigidArea(new Dimension(0, 17)));
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.add(btnSaveAs);
		p.add(Box.createRigidArea(new Dimension(6, 0)));
		p.add(btnClose);
		p.setAlignmentX(JPanel.RIGHT_ALIGNMENT);
		
		mainPane.add(p);
		add(mainPane);
		
		pack();
		setSize(500, 400);
		setLocationRelativeTo(getOwner());
		
		btnSaveAs.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { saveScript(); }
		});
		
		btnClose.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { setVisible(false); }
		});
	}
	
	public void
	setCommands(String[] commands) {
		StringBuffer sb = new StringBuffer();
		
		for(String s : commands) sb.append(s).append("\n");
		
		textPane.setText(sb.toString());
	}
	
	private void
	saveScript() {
		JFileChooser fc = new JFileChooser(ClassicPrefs.getLastScriptLocation());
		fc.setFileFilter(new LscpFileFilter());
		int result = fc.showSaveDialog(this);
		if(result != JFileChooser.APPROVE_OPTION) return;
		
		String path = fc.getCurrentDirectory().getAbsolutePath();
		ClassicPrefs.setLastScriptLocation(path);
		
		try {
			FileOutputStream fos = new FileOutputStream(fc.getSelectedFile());
			fos.write(textPane.getText().getBytes("US-ASCII"));
			fos.close();
		} catch(Exception e) {
			CC.getLogger().log(Level.FINE, HF.getErrorMessage(e), e);
			HF.showErrorMessage(e);
		}
	}
}
