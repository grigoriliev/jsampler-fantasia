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

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import java.util.Vector;
import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import net.sf.juife.TitleBar;

import org.jsampler.CC;
import org.jsampler.HF;
import org.jsampler.LSConsoleModel;

import org.jsampler.view.JSChannel;
import org.jsampler.view.JSChannelsPane;
import org.jsampler.view.JSMainFrame;
import org.jsampler.view.LscpFileFilter;

import org.jsampler.view.std.JSamplerHomeChooser;

import static org.jsampler.view.fantasia.A4n.a4n;
import static org.jsampler.view.fantasia.FantasiaI18n.i18n;
import static org.jsampler.view.fantasia.FantasiaPrefs.preferences;
import static org.jsampler.view.std.StdPrefs.*;


/**
 *
 * @author Grigor Iliev
 */
public class MainFrame extends JSMainFrame {
	private final FantasiaMenuBar menuBar = new FantasiaMenuBar();
	private final StandardBar standardBar = new StandardBar();
	private final MainPane mainPane = new MainPane();
	private final DevicesPane devicesPane = new DevicesPane();
	
	private final JMenu recentScriptsMenu =
		new JMenu(i18n.getMenuLabel("actions.recentScripts"));
	
	private final JSplitPane hSplitPane;
	
	private final SidePane sidePane = new SidePane();
	
	private LSConsoleFrame lsConsoleFrame = null;
	private final Vector<String> recentScripts = new Vector<String>();
		
	
	/** Creates a new instance of <code>MainFrame</code> */
	public
	MainFrame() {
		setTitle(i18n.getLabel("MainFrame.title"));
		
		if(Res.iconAppIcon != null) setIconImage(Res.iconAppIcon.getImage());
		
		getContentPane().add(standardBar, BorderLayout.NORTH);
		
		addMenu();
		
		addChannelsPane(mainPane.getChannelsPane());
		
		JScrollPane sp = new JScrollPane(createRightPane());
		sp.setBorder(BorderFactory.createEmptyBorder());
		
		hSplitPane = new JSplitPane (
			JSplitPane.HORIZONTAL_SPLIT,
			true,	// continuousLayout 
			sidePane, sp
		);
		
		getContentPane().add(hSplitPane);
		
		int i = preferences().getIntProperty("MainFrame.hSplitDividerLocation", 220);
		hSplitPane.setDividerLocation(i);
		
		setSavedSize();
	}
	
	private JPanel
	createRightPane() {
		JPanel p = new JPanel();
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		
		p.setLayout(gridbag);
		
		c.fill = GridBagConstraints.BOTH;
		
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.insets = new Insets(0, 3, 3, 0);
		gridbag.setConstraints(devicesPane, c);
		p.add(devicesPane);
		
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.0;
		c.weighty = 1.0;
		c.insets = new Insets(0, 3, 3, 3);
		c.fill = GridBagConstraints.VERTICAL;
		gridbag.setConstraints(mainPane, c);
		p.add(mainPane);
		
		return p;
	}
	
	private void
	setSavedSize() {
		String s = preferences().getStringProperty("MainFrame.sizeAndLocation");
		if(s == null) {
			setDefaultSizeAndLocation();
			return;
		}
		pack();
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
			setDefaultSizeAndLocation();
		}
		
		if(preferences().getBoolProperty("MainFrame.windowMaximized")) {
			setExtendedState(getExtendedState() | MAXIMIZED_BOTH);
		}
	}
	
	private void
	setDefaultSizeAndLocation() {
		setPreferredSize(new Dimension(900, 600));
		pack();
		setLocationRelativeTo(null);
	}
	
	
	/** Invoked when this window is about to close. */
	protected void
	onWindowClose() {
		sidePane.savePreferences();
		
		int i = hSplitPane.getDividerLocation();
		preferences().setIntProperty("MainFrame.hSplitDividerLocation", i);
		
		preferences().setBoolProperty (
			"MainFrame.windowMaximized",
			(getExtendedState() & MAXIMIZED_BOTH) == MAXIMIZED_BOTH
		);
		
		if(preferences().getBoolProperty("MainFrame.windowMaximized")) {
			super.onWindowClose();
			return;
		}
		
		java.awt.Point p = getLocation();
		Dimension d = getSize();
		StringBuffer sb = new StringBuffer();
		sb.append(p.x).append(',').append(p.y).append(',');
		sb.append(d.width).append(',').append(d.height);
		preferences().setStringProperty("MainFrame.sizeAndLocation", sb.toString());
		
		String[] list = recentScripts.toArray(new String[recentScripts.size()]);
		preferences().setStringListProperty(RECENT_LSCP_SCRIPTS, list);
		
		if(preferences().getBoolProperty(SAVE_LS_CONSOLE_HISTORY)) {
			if(lsConsoleFrame != null) getLSConsolePane().saveConsoleHistory();
		}
		
		super.onWindowClose();
	}
	
	private void
	addMenu() {
		JMenu m;
		JMenuItem mi;
		
		setJMenuBar(menuBar);
		
		// Actions
		m = new FantasiaMenu(i18n.getMenuLabel("actions"));
		
		mi = new JMenuItem(a4n.connect);
		mi.setIcon(null);
		//mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_MASK));
		m.add(mi);
		
		mi = new JMenuItem(a4n.samplerInfo);
		mi.setIcon(null);
		m.add(mi);
		
		m.addSeparator();
		
		JMenu exportMenu = new JMenu(i18n.getMenuLabel("actions.export"));
		m.add(exportMenu);
		
		mi = new JMenuItem(a4n.exportSamplerConfig);
		mi.setIcon(null);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK));
		exportMenu.add(mi);
		
		mi = new JMenuItem(a4n.exportMidiInstrumentMaps);
		mi.setIcon(null);
		exportMenu.add(mi);
		
		m.addSeparator();
		
		mi = new JMenuItem(a4n.loadScript);
		mi.setIcon(null);
		m.add(mi);
		
		String[] list = preferences().getStringListProperty(RECENT_LSCP_SCRIPTS);
		for(String s : list) recentScripts.add(s);
		
		updateRecentScriptsMenu();
		
		m.add(recentScriptsMenu);
		
		m.addSeparator();
		
		mi = new JMenuItem(i18n.getMenuLabel("actions.exit"));
		m.add(mi);
		mi.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { onWindowClose(); }
		});
		
		menuBar.add(m);
		
		
		// Edit
		m = new FantasiaMenu(i18n.getMenuLabel("edit"));
		menuBar.add(m);
		
		mi = new JMenuItem(a4n.editPreferences);
		mi.setIcon(null);
		mi.setAccelerator(KeyStroke.getKeyStroke (
			KeyEvent.VK_P, KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK
		));
		m.add(mi);
		
		
		// Window
		m = new FantasiaMenu(i18n.getMenuLabel("window"));
		menuBar.add(m);
		
		mi = new JMenuItem(a4n.windowLSConsole);
		mi.setIcon(null);
		m.add(mi);
		
		mi = new JMenuItem(a4n.windowInstrumentsDb);
		mi.setIcon(null);
		m.add(mi);
		
		
		// Help
		m = new FantasiaMenu(i18n.getMenuLabel("help"));
		
		mi = new JMenuItem(a4n.helpAbout);
		mi.setIcon(null);
		m.add(mi);
		
		menuBar.add(m);
	}
	
	/**
	 * This method does nothing, because <b>Fantasia</b> has exactly
	 * one pane containing sampler channels, which can not be changed.
	 */
	public void
	insertChannelsPane(JSChannelsPane pane, int idx) {
		
	}
	
	/**
	 * This method always returns the <code>JSChannelsPane</code> at index 0,
	 * because the <b>Fantasia</b> view has exactly one pane containing sampler channels.
	 * @return The <code>JSChannelsPane</code> at index 0.
	 */
	public JSChannelsPane
	getSelectedChannelsPane() { return getChannelsPane(0); }
	
	/**
	 * This method does nothing because the <b>Fantasia</b> view has
	 * exactly one pane containing sampler channels which is always shown. 
	 */
	public void
	setSelectedChannelsPane(JSChannelsPane pane) { }
	
	public void
	installJSamplerHome() {
		JSamplerHomeChooser chooser = new JSamplerHomeChooser(this);
		chooser.setVisible(true);
		if(chooser.isCancelled()) return;
		
		CC.changeJSamplerHome(chooser.getJSamplerHome());
	}
	
	public void
	showDetailedErrorMessage(Frame owner, String err, String details) {
		// TODO: 
	}
	
	public void
	showDetailedErrorMessage(Dialog owner, String err, String details) {
		// TODO: 
	}
	
	protected LSConsoleModel
	getLSConsoleModel() { return getLSConsolePane().getModel(); }
	
	protected LSConsolePane
	getLSConsolePane() {
		return getLSConsoleFrame().getLSConsolePane();
	}
	
	protected LSConsoleFrame
	getLSConsoleFrame() {
		if(lsConsoleFrame == null) lsConsoleFrame = new LSConsoleFrame();
		return lsConsoleFrame;
	}
	
	protected void
	runScript() {
		String s = preferences().getStringProperty("lastScriptLocation");
		JFileChooser fc = new JFileChooser(s);
		fc.setFileFilter(new LscpFileFilter());
		int result = fc.showOpenDialog(this);
		if(result != JFileChooser.APPROVE_OPTION) return;
		
		String path = fc.getCurrentDirectory().getAbsolutePath();
		preferences().setStringProperty("lastScriptLocation", path);
					
		runScript(fc.getSelectedFile());
	}
	
	private void
	runScript(String script) { runScript(new File(script)); }
	
	private void
	runScript(File script) {
		FileReader fr;
		try { fr = new FileReader(script); }
		catch(FileNotFoundException e) {
			HF.showErrorMessage(i18n.getMessage("FileNotFound!"));
			return;
		}
		
		BufferedReader br = new BufferedReader(fr);
		
		try {
			String s = br.readLine();
			while(s != null) {
				getLSConsoleModel().setCommandLineText(s);
				getLSConsoleModel().execCommand();
				s = br.readLine();
			}
		} catch(Exception e) {
			HF.showErrorMessage(e);
			return;
		}
		
		String s = script.getAbsolutePath();
		recentScripts.remove(s);
		recentScripts.insertElementAt(s, 0);
		
		updateRecentScriptsMenu();
	}
	
	protected void
	clearRecentScripts() {
		recentScripts.removeAllElements();
		updateRecentScriptsMenu();
	}
	
	protected void
	updateRecentScriptsMenu() {
		int size = preferences().getIntProperty(RECENT_LSCP_SCRIPTS_SIZE);
		while(recentScripts.size() > size) {
			recentScripts.removeElementAt(recentScripts.size() - 1);
		}
		
		recentScriptsMenu.removeAll();
		
		for(String script : recentScripts) {
			JMenuItem mi = new JMenuItem(script);
			recentScriptsMenu.add(mi);
			mi.addActionListener(new RecentScriptHandler(script));
		}
		
		recentScriptsMenu.setEnabled(recentScripts.size() != 0);
	}
	
	private class RecentScriptHandler implements ActionListener {
		private String script;
		
		RecentScriptHandler(String script) { this.script = script; }
		
		public void
		actionPerformed(ActionEvent e) {
			runScript(script);
			if(preferences().getBoolProperty(SHOW_LS_CONSOLE_WHEN_RUN_SCRIPT)) {
				a4n.windowLSConsole.actionPerformed(null);
			}
		}
	}
	
	private static class FantasiaMenu extends JMenu {
		FantasiaMenu(String s) {
			super(s);
			setFont(getFont().deriveFont(java.awt.Font.BOLD));
			setOpaque(false);
		}
	}

	private static class FantasiaMenuBar extends JMenuBar {
		private static Insets pixmapInsets = new Insets(6, 6, 0, 6);
		
		FantasiaMenuBar() {
			setOpaque(false);
		}
		
		protected void
		paintComponent(Graphics g) {
			super.paintComponent(g);
			
			PixmapPane.paintComponent(this, g, Res.gfxMenuBarBg, pixmapInsets);
		}
	}
}
