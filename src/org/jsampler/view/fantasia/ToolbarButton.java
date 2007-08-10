/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2007 Grigor Iliev <grigor@grigoriliev.com>
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

import javax.swing.Action;

import javax.swing.JButton;

/**
 *
 * @author Grigor Iliev
 */
public class ToolbarButton extends JButton {
	/** Creates a new instance of <code>ToolbarButton</code>. */
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
