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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JColorChooser;
import javax.swing.JPanel;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sf.juife.swing.JuifeUtils;
import net.sf.juife.swing.OkCancelDialog;

/**
 *
 * @author Grigor Iliev
 */
public class JSColorButton extends JPanel {
	private Color color;
	private final Vector<ActionListener> listeners = new Vector<ActionListener>();
	
	/** Creates a new instance of <code>JSColorButton</code> */
	public
	JSColorButton() { this(Color.WHITE); }
	
	/** Creates a new instance of <code>JSColorButton</code> */
	public
	JSColorButton(Color c) {
		color = c;
		
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		setPreferredSize(new Dimension(42, 16));
		setMaximumSize(new Dimension(42, 16));
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		addMouseListener(new MouseAdapter() {
			public void
			mouseClicked(MouseEvent e) {
				if(!isEnabled()) return;
				if(e.getButton() == e.BUTTON1) showColorChooser();
			}
		});
	}
	
	/**
	 * Registers the specified listener to be
	 * notified when the current color is changed.
	 * @param l The <code>ActionListener</code> to register.
	 */
	public void
	addActionListener(ActionListener l) { listeners.add(l); }

	/**
	 * Removes the specified listener.
	 * @param l The <code>ActionListener</code> to remove.
	 */
	public void
	removeActionListener(ActionListener l) { listeners.remove(l); }
	
	/** Notifies listeners that the current color is changed. */
	private void
	fireActionPerformed() {
		ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null);
		for(ActionListener l : listeners) l.actionPerformed(e);
	}

	public void
	setEnabled(boolean b) {
		setOpaque(b);
		if(b) setBorder(BorderFactory.createLineBorder(Color.BLACK));
		else setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		//setBorderPainted(!b);
		super.setEnabled(b);
	}
	
	private void
	showColorChooser() {
		ColorDlg dlg = new ColorDlg (
			(Dialog)JuifeUtils.getWindow(this), getColor()
		);
		
		dlg.setVisible(true);
		if(!dlg.isCancelled()) {
			setColor(dlg.getColor());
			fireActionPerformed();
		}
	}
	
	public Color
	getColor() { return color; }
	
	public void
	setColor(Color c) {
		color = c;
		setBackground(color);
	}
	
	
	protected static class ColorDlg extends OkCancelDialog {
		private final JColorChooser colorChooser = new JColorChooser();
		
		ColorDlg(Dialog owner) { this(owner, Color.WHITE); }
		
		ColorDlg(Dialog owner, Color c) {
			super(owner);
			
			colorChooser.setPreviewPanel(new JPanel());
			colorChooser.setColor(c);
			
			JPanel mainPane = new JPanel();
			mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
			mainPane.add(colorChooser);
			
			mainPane.add(Box.createRigidArea(new Dimension(0, 6)));
			
			final JPanel p = new JPanel();
			p.setBackground(c);
			p.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			mainPane.add(p);
			
			p.setPreferredSize(new Dimension(48, 8));
			p.setMaximumSize(new Dimension(Short.MAX_VALUE, 8));
			
			setMainPane(mainPane);
			
			colorChooser.getSelectionModel().addChangeListener(new ChangeListener() {
				public void
				stateChanged(ChangeEvent e) { p.setBackground(getColor()); }
			});
		}
		
		protected void
		onOk() { setVisible(false); }
		
		protected void
		onCancel() { setVisible(false); }
		
		public Color
		getColor() { return colorChooser.getColor(); }
	}
}
