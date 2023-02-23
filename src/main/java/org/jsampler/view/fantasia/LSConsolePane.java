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

import java.awt.Window;

import javax.swing.Action;

import org.jsampler.view.swing.std.JSLSConsolePane;

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
