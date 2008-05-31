/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2008 Grigor Iliev <grigor@grigoriliev.com>
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

package org.jsampler.view.fantasia;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import javax.swing.plaf.basic.BasicButtonUI;

import org.jvnet.lafwidget.animation.FadeConfigurationManager;
import org.jvnet.lafwidget.animation.FadeKind;

import org.jvnet.substance.SubstanceLookAndFeel;


/**
 *
 * @author Grigor Iliev
 */
public class PixmapButton extends JButton {
	private Dimension size;
	
	/** Creates a new instance of PixmapButton */
	PixmapButton(ImageIcon icon) {
		this(icon, null);
	}
	
	/** Creates a new instance of PixmapButton */
	PixmapButton(ImageIcon icon, ImageIcon rolloverIcon) {
		initPixmapButton(icon, rolloverIcon);
	}
	
	/** Creates a new instance of PixmapButton */
	PixmapButton(Action a, ImageIcon icon) {
		super(a);
		initPixmapButton(icon, null);
	}
	
	private void
	initPixmapButton(ImageIcon icon, ImageIcon rolloverIcon) {
		setText("");
		setContentAreaFilled(false);
		setFocusPainted(false);
		setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		setMargin(new Insets(0, 0, 0, 0));
		
		setIcon(icon);
		
		if(rolloverIcon != null) {
			setRolloverIcon(rolloverIcon);
			setRolloverEnabled(true);
		} else {
			setRolloverEnabled(false);
		}
		
		size = new Dimension(icon.getIconWidth(), icon.getIconHeight());
		setPreferredSize(size);
		setMaximumSize(size);
		
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}
	
	public void
	updateUI() { setUI(new BasicButtonUI()); }
}
