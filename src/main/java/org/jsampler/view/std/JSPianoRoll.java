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

package org.jsampler.view.std;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import static org.jsampler.view.std.StdI18n.i18n;


/**
 *
 * @author Grigor Iliev
 */
public class JSPianoRoll extends PianoRoll {
	private final ContextMenu contextMenu;
	
	public
	JSPianoRoll() {
		contextMenu = new ContextMenu();
		addMouseListener(contextMenu);
	}
	
	
	class ContextMenu extends MouseAdapter {
		private final JPopupMenu menu = new JPopupMenu();
		
		ContextMenu() {
			String s = i18n.getMenuLabel("JSPianoRoll.ContextMenu.preferences");
			JMenuItem mi = new JMenuItem(s);
			menu.add(mi);
			mi.addActionListener(new ActionListener() {
				public void
				actionPerformed(ActionEvent e) {
					new JSPianoRollPrefsDlg().setVisible(true);
				}
			});
		}
		
		public void
		mousePressed(MouseEvent e) {
			if(e.isPopupTrigger()) show(e);
		}
		
		public void
		mouseReleased(MouseEvent e) {
			if(e.isPopupTrigger()) show(e);
		}
		
		void
		show(MouseEvent e) {
			menu.show(e.getComponent(), e.getX(), e.getY());
		}
	}
}
