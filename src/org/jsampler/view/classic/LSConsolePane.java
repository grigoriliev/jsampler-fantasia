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

package org.jsampler.view.classic;

import java.awt.Dimension;
import java.awt.Window;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import javax.swing.border.EtchedBorder;

import org.jsampler.CC;
import org.jsampler.view.std.JSLSConsolePane;

import static org.jsampler.view.classic.ClassicI18n.i18n;
import org.jsampler.view.std.JSLscpScriptDlg;

/**
 *
 * @author Grigor Iliev
 */
public class LSConsolePane extends JSLSConsolePane {
	private final JButton btnMenu = new ToolbarButton();
	private JPopupMenu menu = new JPopupMenu();
	
	private final LSConsoleViewMode lsConsoleViewMode;
	
	
	/**
	 * Creates a new instance of <code>LSConsolePane</code>
	 */
	public
	LSConsolePane(Window owner) {
		super(owner);
		
		lsConsoleViewMode = new LSConsoleViewMode();
		
		setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.add(Box.createGlue());
		
		btnMenu.setIcon(Res.iconDown16);
		btnMenu.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		btnMenu.setFocusPainted(false);
		p.add(btnMenu);
		p.setMaximumSize(new Dimension(Short.MAX_VALUE, p.getPreferredSize().height));
		
		add(p, java.awt.BorderLayout.NORTH);
		
		initMenu(owner);
	}
	
	private void
	initMenu(Window owner) {
		JMenuItem mi = new JMenuItem(lsConsoleViewMode);
		menu.add(mi);
		
		menu.addSeparator();
		
		JMenu clearMenu = new JMenu(i18n.getMenuLabel("LSConsolePane.clear"));
		
		mi = new JMenuItem(i18n.getMenuLabel("LSConsolePane.clearConsole"));
		clearMenu.add(mi);
		mi.addActionListener(new Actions(Actions.CLEAR_CONSOLE));
		
		mi = new JMenuItem(i18n.getMenuLabel("LSConsolePane.clearSessionHistory"));
		clearMenu.add(mi);
		mi.addActionListener(new Actions(Actions.CLEAR_SESSION_HISTORY));
		
		menu.add(clearMenu);
		
		JMenu exportMenu = new JMenu(i18n.getMenuLabel("LSConsolePane.export"));
		
		mi = new JMenuItem(i18n.getMenuLabel("LSConsolePane.exportSession"));
		exportMenu.add(mi);
		mi.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				JSLscpScriptDlg dlg = new JSLscpScriptDlg();
				dlg.setCommands(getModel().getSessionHistory());
				dlg.setVisible(true);
			}
		});
		
		mi = new JMenuItem(i18n.getMenuLabel("LSConsolePane.exportCommandHistory"));
		exportMenu.add(mi);
		mi.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				JSLscpScriptDlg dlg = new JSLscpScriptDlg();
				dlg.setCommands(getModel().getCommandHistory());
				dlg.setVisible(true);
			}
		});
		
		menu.add(exportMenu);
		
		mi = new JMenuItem(i18n.getMenuLabel("LSConsolePane.runScript"));
		menu.add(mi);
		mi.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				((MainFrame)CC.getMainFrame()).runScript();
			}
		});
		
		btnMenu.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				int x = (int)btnMenu.getMinimumSize().getWidth();
				x -= (int)menu.getPreferredSize().getWidth();
				int y = (int)btnMenu.getMinimumSize().getHeight() + 1;
				menu.show(btnMenu, x, y);
			}
		});
	}
	
	protected void
	quitSession() {
		super.quitSession();
		((MainFrame)CC.getMainFrame()).setLSConsoleVisible(false);
	}
	
	private class LSConsoleViewMode extends AbstractAction {
		LSConsoleViewMode() { }
		
		public void
		actionPerformed(ActionEvent e) {
			MainFrame mainFrame = (MainFrame)CC.getMainFrame();
			mainFrame.setLSConsolePopOut(!mainFrame.isLSConsolePopOut());
			
			setName(mainFrame.isLSConsolePopOut());
		}
		
		private void
		setName(boolean b) {
			if(b) {
				putValue(Action.NAME, i18n.getMenuLabel("LSConsolePane.popin"));
			} else {
				putValue(Action.NAME, i18n.getMenuLabel("LSConsolePane.popout"));
			}	
		}
	}
	
	/**
	 * Updates the text of the menu item responsible for changing the pop-out/pop-in mode.
	 */
	public void
	updateLSConsoleViewMode() {
		if(getOwner() instanceof LSConsoleDlg) lsConsoleViewMode.setName(true);
		else if(getOwner() instanceof MainFrame) {
			lsConsoleViewMode.setName(((MainFrame)getOwner()).isLSConsolePopOut());
		}
	}
}
