/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2010 Grigor Iliev <grigor@grigoriliev.com>
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

import java.awt.Window;

import javax.swing.Action;

import org.jsampler.view.std.JSLSConsolePane;

/**
 *
 * @author Grigor Iliev
 */
public class LSConsolePane extends JSLSConsolePane {
	protected Action clearConsoleAction = new Actions(Actions.CLEAR_CONSOLE);
	protected Action clearSessionHistoryAction = new Actions(Actions.CLEAR_SESSION_HISTORY);
	
	/** Creates a new instance of <code>LSConsolePane</code> */
	public
	LSConsolePane(Window owner) {
		super(owner);
		getLSConsoleTextPane().putClientProperty("substancelaf.noExtraElements", Boolean.TRUE);
	}
	
	@Override
	protected void
	quitSession() {
		super.quitSession();
		getOwner().setVisible(false);
	}
	
}
