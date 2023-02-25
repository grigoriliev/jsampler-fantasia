/*
 *   JSampler - a front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2023 Grigor Iliev <grigor@grigoriliev.com>
 *
 *   This file is part of JSampler.
 *
 *   JSampler is free software: you can redistribute it and/or modify it under
 *   the terms of the GNU General Public License as published by the Free
 *   Software Foundation, either version 3 of the License, or (at your option)
 *   any later version.
 *
 *   JSampler is distributed in the hope that it will be useful, but WITHOUT
 *   ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *   FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 *   more details.
 *
 *   You should have received a copy of the GNU General Public License along
 *   with JSampler. If not, see <https://www.gnu.org/licenses/>.
 */

package com.grigoriliev.jsampler.fantasia.view.basic;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

import javax.swing.plaf.basic.BasicToggleButtonUI;


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
		setOpaque(false);
		setFocusPainted(false);
		setBorder(javax.swing.BorderFactory.createEmptyBorder());
		setMargin(new Insets(0, 0, 0, 0));

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

	@Override
	public void
	updateUI() { setUI(new BasicToggleButtonUI()); }
}
