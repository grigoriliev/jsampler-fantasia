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

package org.jsampler.view.classic;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;


/**
 *
 * @author Grigor Iliev
 */
public class ToolbarButton extends JButton {
	/** Creates a button. */
	ToolbarButton() {
		initToolbarButton();
	}
	
	/** Creates a button where properties are taken from the Action supplied. */
	ToolbarButton(javax.swing.Action a) {
		super(a);
		
		initToolbarButton();
	}
	
	private void
	initToolbarButton() {
		setBorderPainted(false);
		setContentAreaFilled(false);
		setFocusPainted(false);
		
		addMouseListener(new MouseAdapter() {
			public void
			mouseEntered(MouseEvent e) {
				if(isEnabled()) setBorderPainted(true);
			}
		
			public void
			mouseExited(MouseEvent e) { setBorderPainted(false); }
		});
	}
	
	/** Sets the button's text. */
	public void
	setText(String text) { /* We don't want any text in toolbar buttons */ }
}
