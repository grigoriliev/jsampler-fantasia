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

package org.jsampler.view.std;

import java.awt.Dimension;
import java.awt.Rectangle;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import org.jsampler.CC;

/**
 *
 * @author Grigor Iliev
 */
public class JSFrame extends JFrame {
	public
	JSFrame(String title, String name) {
		super(title);
		setName(name);
		
		setSavedSize();
		
		addWindowListener(new WindowAdapter() {
			public void
			windowClosing(WindowEvent we) { onWindowClose(); }
		});
	}
	
	/** Invoked when this window is about to close. */
	private void
	onWindowClose() {
		boolean b = (getExtendedState() & MAXIMIZED_BOTH) == MAXIMIZED_BOTH;
		CC.preferences().setBoolProperty(getName() + ".windowMaximized", b);
		if(b) return;
		
		StdUtils.saveWindowBounds(getName(), getBounds());setName("");
	}
	
	private void
	setDefaultSize() {
		Dimension dimension = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		double width = dimension.getWidth();
		double height = dimension.getHeight();
		setBounds(100, 100, (int) width - 200, (int) height - 200);
	}
	
	private void
	setSavedSize() {
		Rectangle r = StdUtils.getWindowBounds(getName());
		if(r == null) {
			setDefaultSize();
			return;
		}
		
		setBounds(r);
	}
	
	@Override
	public void
	setVisible(boolean b) {
		if(b == isVisible()) return;
		
		super.setVisible(b);
		
		if(b && CC.preferences().getBoolProperty(getName() + ".windowMaximized")) {
			setExtendedState(getExtendedState() | MAXIMIZED_BOTH);
		}
	}
}
