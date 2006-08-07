/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005, 2006 Grigor Kirilov Iliev
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
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;


/**
 *
 * @author Grigor Iliev
 */
public class PixmapButton extends JButton {
	/** Creates a new instance of PixmapButton */
	PixmapButton(ImageIcon icon) {
		this(icon, null);
	}
	
	/** Creates a new instance of PixmapButton */
	PixmapButton(ImageIcon icon, ImageIcon rolloverIcon) {
		setBorder(BorderFactory.createEmptyBorder());
		setContentAreaFilled(false);
		setFocusPainted(false);
		setMargin(new Insets(0, 0, 0, 0));
	
		setIcon(icon);
		
		if(rolloverIcon != null) {
			setRolloverIcon(rolloverIcon);
			setRolloverEnabled(true);
		} else {
			setRolloverEnabled(false);
		}
		
		setPreferredSize(getMinimumSize());
		setMaximumSize(getMinimumSize());
		
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}
}
