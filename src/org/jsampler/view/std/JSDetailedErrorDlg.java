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

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;

import net.sf.juife.swing.EnhancedDialog;
import net.sf.juife.swing.JuifeUtils;

import static org.jsampler.view.std.StdI18n.i18n;


/**
 *
 * @author Grigor Iliev
 */
public class JSDetailedErrorDlg extends EnhancedDialog {
	private final JLabel lError = new JLabel();
	private final JTextArea taDetails = new JTextArea();
	
	private final JButton btnDetails =
		new JButton(i18n.getButtonLabel("JSDetailedErrorDlg.showDetails"));
	
	private JComponent mainPane;
	private JComponent detailsPane;
	
	int hideHeight;
	int showHeight;
	boolean show = false;
	
	/**
	 * Creates a new instance of <code>JSDetailedErrorDlg</code>
	 */
	public JSDetailedErrorDlg(Frame owner, Icon iconWarning, String title, String err, String details) {
		super(owner, title);
		initDetailedErrorDlg(err, details, iconWarning);
	}
	
	/**
	 * Creates a new instance of <code>JSDetailedErrorDlg</code>
	 */
	public JSDetailedErrorDlg(Dialog owner, Icon iconWarning, String title, String err, String details) {
		super(owner, title);
		initDetailedErrorDlg(err, details, iconWarning);
	}
	
	private void
	initDetailedErrorDlg(String err, String details, Icon iconWarning) {
		lError.setText(err);
		lError.setIcon(iconWarning);
		lError.setAlignmentX(LEFT_ALIGNMENT);
		lError.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
		taDetails.setText(details);
		taDetails.setEditable(false);
		
		mainPane = new JPanel();
		mainPane.setLayout(new BorderLayout());
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.add(lError);
		
		JPanel btnPane = new JPanel();
		btnPane.setLayout(new BoxLayout(btnPane, BoxLayout.X_AXIS));
		btnPane.add(Box.createGlue());
		btnPane.add(btnDetails);
		btnPane.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		btnPane.setAlignmentX(LEFT_ALIGNMENT);
		p.add(btnPane);
		
		mainPane.add(p, BorderLayout.NORTH);
		
		detailsPane = new JPanel();
		detailsPane.setLayout(new BoxLayout(detailsPane, BoxLayout.Y_AXIS));
		detailsPane.add(Box.createRigidArea(new Dimension(0, 5)));
		detailsPane.add(new JSeparator());
		detailsPane.add(Box.createRigidArea(new Dimension(0, 12)));
		detailsPane.add(new JScrollPane(taDetails));
		detailsPane.setAlignmentX(LEFT_ALIGNMENT);
		
		mainPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		add(mainPane);
		
		Dimension d = mainPane.getPreferredSize();
		d = new Dimension(d.width > 300 ? d.width : 300, d.height);
		hideHeight = getPreferredSize().height;
		showHeight = hideHeight + 200;
		
		mainPane.setPreferredSize(d);
		mainPane.setMinimumSize(d);
		
		//d = new Dimension(300, 200);
		//detailsPane.setPreferredSize(d);
		//detailsPane.setMinimumSize(d);
		pack();
		setMinimumSize(getPreferredSize());
		
		setLocation(JuifeUtils.centerLocation(this, getOwner()));
		
		btnDetails.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { showDetails(show = !show); }
		});
	}
	
	public void
	showDetails(final boolean b) {
		if(b) btnDetails.setText(i18n.getButtonLabel("JSDetailedErrorDlg.hideDetails"));
		else btnDetails.setText(i18n.getButtonLabel("JSDetailedErrorDlg.showDetails"));
		
		if(b) {
			mainPane.add(detailsPane);
		} else {
			showHeight = getSize().height;
			mainPane.remove(detailsPane);
		}
		
		SwingUtilities.invokeLater(new Runnable() {
			public void
			run() {
				int w = getSize().width;
				if(b) {
					Rectangle r = getBounds();
					setBounds(r.x, r.y, w, showHeight);
				} else {
					Rectangle r = getBounds();
					setBounds(r.x, r.y, w, hideHeight);
				}
			}
		});
		
		setResizable(b);
	}
	
	protected void
	onOk() { }
	
	protected void
	onCancel() { setVisible(false); }
}
