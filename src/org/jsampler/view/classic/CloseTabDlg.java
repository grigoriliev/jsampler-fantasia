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

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.jsampler.CC;
import org.jsampler.view.JSChannelsPane;

import net.sf.juife.JuifeUtils;
import net.sf.juife.OkCancelDialog;

import static org.jsampler.view.classic.ClassicI18n.i18n;


/**
 *
 * @author Grigor Iliev
 */
public class CloseTabDlg extends OkCancelDialog {
	private final JLabel l = new JLabel(i18n.getLabel("CloseTabDlg.?"));
	protected final JRadioButton rbRemove =
		new JRadioButton(i18n.getButtonLabel("CloseTabDlg.rbRemove"));
	protected final JRadioButton rbMove =
		new JRadioButton(i18n.getButtonLabel("CloseTabDlg.rbMove"));
	protected final JComboBox cbTabs = new JComboBox();
	
	
	/** Creates a new instance of CloseTabDlg */
	public
	CloseTabDlg(Frame frm) {
		super(frm);
		
		JPanel mainPane = new JPanel();
		mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
		
		l.setAlignmentX(LEFT_ALIGNMENT);
		mainPane.add(l);
		
		mainPane.add(Box.createRigidArea(new Dimension(0, 11)));
		
		ButtonGroup bg = new ButtonGroup();
		bg.add(rbRemove);
		bg.add(rbMove);
		rbRemove.setSelected(true);
		rbRemove.setForeground(new java.awt.Color(0xee3377));
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.add(Box.createRigidArea(new Dimension(12, 0)));
		p.add(rbRemove);
		p.setAlignmentX(LEFT_ALIGNMENT);
		mainPane.add(p);
		
		for(JSChannelsPane pane : CC.getMainFrame().getChannelsPaneList())
			if(pane != CC.getMainFrame().getSelectedChannelsPane())
				cbTabs.addItem(pane);
		
		cbTabs.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { rbMove.setSelected(true); }
		});
		
		p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.add(Box.createRigidArea(new Dimension(12, 0)));
		p.add(rbMove);
		p.add(Box.createRigidArea(new Dimension(5, 0)));
		p.add(cbTabs);
		p.setAlignmentX(LEFT_ALIGNMENT);
		mainPane.add(p);
		
		setMainPane(mainPane);
	}
	
	protected void
	onOk() {
		setCancelled(false);
		setVisible(false);
	}
	
	protected void
	onCancel() { setVisible(false); }
	
	public boolean
	remove() { return rbRemove.isSelected(); }
	
	public JSChannelsPane
	getSelectedChannelsPane() { return (JSChannelsPane)cbTabs.getSelectedItem(); }
}

