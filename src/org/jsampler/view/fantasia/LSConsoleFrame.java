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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.jsampler.CC;
import org.jsampler.view.std.JSFrame;
import org.jsampler.view.std.JSLscpScriptDlg;

import static org.jsampler.view.fantasia.FantasiaI18n.i18n;

/**
 *
 * @author Grigor Iliev
 */
public class LSConsoleFrame extends JSFrame {
	private final JMenuBar menuBar = new JMenuBar();
	private final LSConsolePane lsConsolePane = new LSConsolePane(this);
	
	/**
	 * Creates a new instance of <code>LSConsoleFrame</code>
	 */
	public
	LSConsoleFrame() {
		super(i18n.getLabel("LSConsoleFrame.title"), "LSConsoleFrame");
		if(Res.iconAppIcon != null) setIconImage(Res.iconLSConsole.getImage());
		
		add(lsConsolePane);
		addMenu();
		pack();
	}
	
	private void
	addMenu() {
		if(CC.getViewConfig().isUsingScreenMenuBar()) {
			((ViewConfig)CC.getViewConfig()).setNativeMenuProperties();
		}
		
		JMenu m;
		JMenuItem mi;
		
		setJMenuBar(menuBar);
		
		// Actions
		m = new JMenu(i18n.getMenuLabel("lsconsole.actions"));
		m.setFont(m.getFont().deriveFont(java.awt.Font.BOLD));
		menuBar.add(m);
		
		JMenu clearMenu = new JMenu(i18n.getMenuLabel("lsconsole.clear"));
		
		mi = new JMenuItem(i18n.getMenuLabel("lsconsole.clearConsole"));
		clearMenu.add(mi);
		mi.addActionListener(lsConsolePane.clearConsoleAction);
		
		mi = new JMenuItem(i18n.getMenuLabel("lsconsole.clearSessionHistory"));
		clearMenu.add(mi);
		mi.addActionListener(lsConsolePane.clearSessionHistoryAction);
		
		m.add(clearMenu);
		
		JMenu exportMenu = new JMenu(i18n.getMenuLabel("lsconsole.export"));
		
		mi = new JMenuItem(i18n.getMenuLabel("lsconsole.export.session"));
		exportMenu.add(mi);
		mi.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				JSLscpScriptDlg dlg = new JSLscpScriptDlg();
				dlg.setCommands(lsConsolePane.getModel().getSessionHistory());
				dlg.setVisible(true);
			}
		});
		
		mi = new JMenuItem(i18n.getMenuLabel("lsconsole.export.commandHistory"));
		exportMenu.add(mi);
		mi.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				JSLscpScriptDlg dlg = new JSLscpScriptDlg();
				dlg.setCommands(lsConsolePane.getModel().getCommandHistory());
				dlg.setVisible(true);
			}
		});
		
		m.add(exportMenu);
		
		m.addSeparator();
		
		mi = new JMenuItem(i18n.getMenuLabel("lsconsole.runScript"));
		m.add(mi);
		mi.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				((MainFrame)CC.getMainFrame()).runScript();
			}
		});

		if(CC.getViewConfig().isUsingScreenMenuBar()) {
			((ViewConfig)CC.getViewConfig()).restoreMenuProperties();
		}
	}
	
	protected LSConsolePane
	getLSConsolePane() { return lsConsolePane; }
}
