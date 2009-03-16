/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2009 Grigor Iliev <grigor@grigoriliev.com>
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

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.jsampler.CC;

import static org.jsampler.view.std.StdI18n.i18n;

/**
 *
 * @author grishata
 */
public class JSConnectionFailurePane extends JOptionPane {
	public static Object[] buttons = {
		i18n.getButtonLabel("JSConnectionFailurePane.reconnect"),
		i18n.getButtonLabel("JSConnectionFailurePane.quit")
	};

	public
	JSConnectionFailurePane() {
		super (
			"Connection to backend failed",
			ERROR_MESSAGE,
			DEFAULT_OPTION,
			null,
			buttons,
			buttons[1]
		);
	}

	public void
	showDialog() {
		JDialog dlg = createDialog(CC.getMainFrame(), i18n.getError("error"));
		dlg.setModal(true);
		dlg.setVisible(true);
		Object val = getValue();
		if(val == null) {

		} else if(buttons[0].equals(val)) {
			CC.reconnect();
		} else if(buttons[1].equals(val)) {
			CC.getMainFrame().onWindowClose();
		}
	}
}
