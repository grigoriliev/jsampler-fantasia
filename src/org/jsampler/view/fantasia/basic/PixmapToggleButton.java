/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2009 Grigor Iliev <grigor@grigoriliev.com>
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

package org.jsampler.view.fantasia.basic;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

import org.jvnet.lafwidget.animation.FadeConfigurationManager;
import org.jvnet.lafwidget.animation.FadeKind;

import org.jvnet.substance.SubstanceLookAndFeel;


/**
 *
 * @author Grigor Iliev
 */
public class PixmapToggleButton extends JToggleButton {
	private ImageIcon defaultIcon;
	private ImageIcon selectedIcon;
	
	/** Creates a new instance of PixmapToggleButton */
	public
	PixmapToggleButton(ImageIcon defaultIcon, ImageIcon selectedIcon) {
		this.defaultIcon = defaultIcon;
		this.selectedIcon = selectedIcon;
		
		setContentAreaFilled(false);
		setFocusPainted(false);
		setMargin(new Insets(0, 0, 0, 0));
		putClientProperty(SubstanceLookAndFeel.BUTTON_PAINT_NEVER_PROPERTY, Boolean.TRUE);
		putClientProperty(SubstanceLookAndFeel.BUTTON_NO_MIN_SIZE_PROPERTY, Boolean.TRUE);
		FadeConfigurationManager.getInstance().disallowFades(FadeKind.ROLLOVER, this);
		
		setIcon(defaultIcon);
		setSelectedIcon(selectedIcon);
		setRolloverEnabled(false);
		setPressedIcon(defaultIcon);
		Dimension d = new Dimension(defaultIcon.getIconWidth(), defaultIcon.getIconHeight());
		setPreferredSize(d);
		setMaximumSize(d);
		setMinimumSize(d);
		
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}
}
