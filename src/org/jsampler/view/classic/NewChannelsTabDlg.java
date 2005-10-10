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
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import org.jsampler.CC;
import org.jsampler.HF;

import org.jsampler.view.JSChannelsPane;
import org.jsampler.view.JSMainFrame;

import net.sf.juife.EnhancedDialog;
import net.sf.juife.JuifeUtils;

import static org.jsampler.view.classic.ClassicI18n.i18n;


/**
 *
 * @author Grigor Iliev
 */
public class NewChannelsTabDlg extends EnhancedDialog {
	private final NewTabPane newTabPane = new NewTabPane();
	private final JButton btnOk = new JButton(i18n.getButtonLabel("ok"));
	private final JButton btnCancel = new JButton(i18n.getButtonLabel("cancel"));
	
	/** Creates a new instance of NewChannelsTabDlg */
	public NewChannelsTabDlg(Frame frm) {
		super(frm, i18n.getLabel("NewChannelsTabDlg"), true);
		
		initNewChannelsTabDlg();
		handleEvents();
		setLocation(JuifeUtils.centerLocation(this, frm));
	}
	
	private void
	initNewChannelsTabDlg() {
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
		
		JPanel mainPane = new JPanel();
		mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
		
		newTabPane.setAlignmentX(RIGHT_ALIGNMENT);
		mainPane.add(newTabPane);
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
		String title = newTabPane.getTitle().trim();
		ChannelsPane pane = new ChannelsPane(title);
		
		if(title.length() == 0) {
			HF.showErrorMessage(i18n.getError("NewChannelsTabDlg.emptyTitle!"), this);
			return;
		}
		
		for(JSChannelsPane p : CC.getMainFrame().getChannelsPaneList()) {
			if(pane.getTitle().equals(p.getTitle())) {
				String s;
				s = i18n.getError("NewChannelsTabDlg.tabExist!", pane.getTitle());
				HF.showErrorMessage(s, this);
				return;
			}
		}
		JSMainFrame frm = CC.getMainFrame();
		if(newTabPane.rbBeginning.isSelected()) frm.insertChannelsPane(pane, 0);
		else if(newTabPane.rbAfter.isSelected()) {
			int i;
			i = frm.getChannelsPaneList().indexOf(newTabPane.cbTabs.getSelectedItem());
			if(i == -1) {
				CC.getLogger().warning("ChannelsPane not found in the list!");
				i = frm.getChannelsPaneCount() - 1;
			}
			frm.insertChannelsPane(pane, i + 1);
		} else frm.insertChannelsPane(pane, frm.getChannelsPaneCount());
		
		setVisible(false);
	}
	
	protected void
	onCancel() {
		
		setVisible(false);
	}
}

class NewTabPane extends JPanel {
	private final JLabel lTitle = new JLabel(i18n.getLabel("NewTabPane.lTitle"));
	private final JTextField tfTitle = new JTextField();
	
	protected final JRadioButton rbBeginning =
		new JRadioButton(i18n.getButtonLabel("NewTabPane.rbBeginning"));
	protected final JRadioButton rbEnd =
		new JRadioButton(i18n.getButtonLabel("NewTabPane.rbEnd"));
	protected final JRadioButton rbAfter =
		new JRadioButton(i18n.getButtonLabel("NewTabPane.rbAfter"));
	
	protected final JComboBox cbTabs = new JComboBox();
	
	private static int count = 2;
	
	NewTabPane() {
		tfTitle.setText("Untitled " + count++);
		tfTitle.selectAll();
		
		for(JSChannelsPane pane : CC.getMainFrame().getChannelsPaneList())
			cbTabs.addItem(pane);
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JPanel p1 = new JPanel();
		p1.setLayout(new BoxLayout(p1, BoxLayout.X_AXIS));
		p1.add(lTitle);
		p1.add(Box.createRigidArea(new Dimension(5, 0)));
		p1.add(tfTitle);
		
		ButtonGroup bg = new ButtonGroup();
		bg.add(rbBeginning);
		bg.add(rbEnd);
		bg.add(rbAfter);
		
		rbEnd.setSelected(true);
		
		JPanel p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		p2.add(rbAfter);
		p2.add(Box.createRigidArea(new Dimension(5, 0)));
		p2.add(cbTabs);
		
		p1.setAlignmentX(LEFT_ALIGNMENT);
		rbBeginning.setAlignmentX(LEFT_ALIGNMENT);
		rbEnd.setAlignmentX(LEFT_ALIGNMENT);
		p2.setAlignmentX(LEFT_ALIGNMENT);
		
		JSeparator sep = new JSeparator();
		
		add(p1);
		add(Box.createRigidArea(new Dimension(0, 6)));
		add(sep);
		add(rbBeginning);
		add(rbEnd);
		add(p2);
		
		cbTabs.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { rbAfter.setSelected(true); }
		});
	}
	
	public String
	getTitle() { return tfTitle.getText(); }
}
