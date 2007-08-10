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

import java.awt.Dimension;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.logging.Level;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.jsampler.CC;
import org.jsampler.view.std.JSLscpScriptDlg;

import static org.jsampler.view.fantasia.FantasiaI18n.i18n;
import static org.jsampler.view.fantasia.FantasiaPrefs.preferences;

/**
 *
 * @author Grigor Iliev
 */
public class LSConsoleFrame extends JFrame {
	private final JMenuBar menuBar = new JMenuBar();
	private final LSConsolePane lsConsolePane = new LSConsolePane(this);
	
	/**
	 * Creates a new instance of <code>LSConsoleFrame</code>
	 */
	public
	LSConsoleFrame() {
		setTitle(i18n.getLabel("LSConsoleFrame.title"));
		if(Res.iconAppIcon != null) setIconImage(Res.iconLSConsole.getImage());
		
		add(lsConsolePane);
		addMenu();
		pack();
		setSavedSize();
		
		addWindowListener(new WindowAdapter() {
			public void
			windowClosing(WindowEvent we) { onWindowClose(); }
		});
	}
	
	private void
	addMenu() {
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
	}
	
	/** Invoked when this window is about to close. */
	private void
	onWindowClose() {
		boolean b = (getExtendedState() & MAXIMIZED_BOTH) == MAXIMIZED_BOTH;
		preferences().setBoolProperty("LSConsoleFrame.windowMaximized", b);
		if(b) return;
		
		java.awt.Point p = getLocation();
		Dimension d = getSize();
		StringBuffer sb = new StringBuffer();
		sb.append(p.x).append(',').append(p.y).append(',');
		sb.append(d.width).append(',').append(d.height);
		String s = "LSConsoleFrame.windowSizeAndLocation";
		preferences().setStringProperty(s, sb.toString());
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
		String sp = "LSConsoleFrame.windowSizeAndLocation";
		String s = preferences().getStringProperty(sp, null);
		if(s == null) {
			setDefaultSize();
			return;
		}
		
		try {
			int i = s.indexOf(',');
			int x = Integer.parseInt(s.substring(0, i));
			
			s = s.substring(i + 1);
			i = s.indexOf(',');
			int y = Integer.parseInt(s.substring(0, i));
			
			s = s.substring(i + 1);
			i = s.indexOf(',');
			int width = Integer.parseInt(s.substring(0, i));
			
			s = s.substring(i + 1);
			int height = Integer.parseInt(s);
			
			setBounds(x, y, width, height);
		} catch(Exception x) {
			String msg = "Parsing of window size and location string failed";
			CC.getLogger().log(Level.INFO, msg, x);
			setDefaultSize();
		}
		
		if(preferences().getBoolProperty("LSConsoleFrame.windowMaximized")) {
			setExtendedState(getExtendedState() | MAXIMIZED_BOTH);
		}
	}
	
	protected LSConsolePane
	getLSConsolePane() { return lsConsolePane; }
}
