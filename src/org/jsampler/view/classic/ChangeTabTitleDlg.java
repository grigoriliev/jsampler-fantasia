/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005 Grigor Kirilov Iliev
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
import java.awt.Frame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sf.juife.EnhancedDialog;
import net.sf.juife.JuifeUtils;

import org.jsampler.CC;
import org.jsampler.HF;
import org.jsampler.view.JSChannelsPane;

import static org.jsampler.view.classic.ClassicI18n.i18n;


/**
 *
 * @author Grigor Iliev
 */
public class ChangeTabTitleDlg extends EnhancedDialog {
	private final JLabel lTitle = new JLabel(i18n.getLabel("ChangeTabTitleDlg.lTitle"));
	private final JTextField tfTitle = new JTextField();
	
	private final JButton btnOk = new JButton(i18n.getButtonLabel("ok"));
	private final JButton btnCancel = new JButton(i18n.getButtonLabel("cancel"));
	
	/** Creates a new instance of ChangeTabTitleDlg */
	public ChangeTabTitleDlg(Frame frm) {
		super(frm, i18n.getLabel("ChangeTabTitleDlg"), true);
		
		initChangeTabTitleDlg();
		setLocation(JuifeUtils.centerLocation(this, frm));
		
		handleEvents();
	}
	
	private void
	initChangeTabTitleDlg() {
		tfTitle.setText(CC.getMainFrame().getSelectedChannelsPane().getTitle());
		tfTitle.selectAll();
		
		// Set preferred size for Ok & Cancel buttons
		Dimension d = JuifeUtils.getUnionSize(btnOk, btnCancel);
		btnOk.setPreferredSize(d);
		btnOk.setMaximumSize(d);
		btnCancel.setPreferredSize(d);
		btnCancel.setMaximumSize(d);
		
		JPanel btnPane = new JPanel();
		btnPane.setLayout(new BoxLayout(btnPane, BoxLayout.X_AXIS));
		btnPane.add(btnOk);
		btnPane.add(Box.createRigidArea(new Dimension(5, 0)));
		btnPane.add(btnCancel);
		btnPane.setAlignmentX(RIGHT_ALIGNMENT);
		
		JPanel pane = new JPanel();
		pane.setLayout(new BoxLayout(pane, BoxLayout.X_AXIS));
		pane.add(lTitle);
		pane.add(Box.createRigidArea(new Dimension(5, 0)));
		pane.add(tfTitle);
		
		JPanel mainPane = new JPanel();
		mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
		
		pane.setAlignmentX(RIGHT_ALIGNMENT);
		mainPane.add(pane);
		mainPane.add(Box.createRigidArea(new Dimension(0, 12)));
		mainPane.add(btnPane);
		
		mainPane.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
		add(mainPane);
		
		pack();
		d = getPreferredSize();
		d.width = d.width > 300 ? d.width : 300;
		setSize(d);
		setResizable(false);
	}
	
	private void
	handleEvents() {
		btnCancel.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { onCancel(); }
		});
		
		btnOk.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { onOk(); }
		});
	}
	
	protected void
	onOk() {
		JSChannelsPane pane = CC.getMainFrame().getSelectedChannelsPane();
		
		String title = tfTitle.getText().trim();
		
		if(title.length() == 0) {
			HF.showErrorMessage(i18n.getError("ChangeTabTitleDlg.emptyTitle!"), this);
			return;
		}
		
		for(JSChannelsPane p : CC.getMainFrame().getChannelsPaneList()) {
			if(p != pane && title.equals(p.getTitle())) {
				String s = i18n.getError("ChangeTabTitleDlg.tabExist!", title);
				HF.showErrorMessage(s, this);
				return;
			}
		}
		
		pane.setTitle(title);
		((MainFrame)CC.getMainFrame()).updateTabTitle(pane);
		
		setVisible(false);
	}
	
	protected void
	onCancel() { setVisible(false); }
}
