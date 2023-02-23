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

package org.jsampler.view.fantasia;

import javax.swing.Action;

import javax.swing.JButton;

/**
 *
 * @author Grigor Iliev
 */
public class ToolbarButton extends JButton {
	/** Creates a new instance of <code>ToolbarButton</code>. */
	public
	ToolbarButton() {
		setFocusable(false);
	}
	
	/** Creates a new instance of <code>ToolbarButton</code>. */
	public
	ToolbarButton(Action a) {
		super(a);
		setFocusable(false);
	}
	
	/** This method does nothing. */
	public void
	setText(String text) { /* We don't want any text in toolbar buttons */ }
}
