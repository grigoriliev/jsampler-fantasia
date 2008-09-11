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

package org.jsampler.view.fantasia;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import java.util.Vector;
import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.jsampler.CC;
import org.jsampler.HF;
import org.jsampler.LSConsoleModel;
import org.jsampler.Server;

import org.jsampler.view.JSChannelsPane;
import org.jsampler.view.JSMainFrame;
import org.jsampler.view.LscpFileFilter;

import org.jsampler.view.std.JSConnectDlg;
import org.jsampler.view.std.JSDetailedErrorDlg;
import org.jsampler.view.std.JSQuitDlg;
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
	private final StandardBar standardBar = new StandardBar();
	private final FantasiaMenuBar menuBar = new FantasiaMenuBar();
	private final JPanel rootPane = new JPanel();
	private final MainPane mainPane = new MainPane();
	private final PianoKeyboardPane pianoKeyboardPane;
	
	private final JMenu recentScriptsMenu =
		new JMenu(i18n.getMenuLabel("actions.recentScripts"));
	
	private final JSplitPane hSplitPane;
	private final JSplitPane pianoRollSplitPane;
	
	private final LeftSidePane leftSidePane = new LeftSidePane();
	private final RightSidePane rightSidePane = new RightSidePane();
	private final JPanel rightPane;
	
	//private final StatusBar statusBar = new StatusBar();
	
	private final LSConsoleFrame lsConsoleFrame = new LSConsoleFrame();
	private final Vector<String> recentScripts = new Vector<String>();
		
	
	private final JCheckBoxMenuItem cbmiToolBarVisible =
			new JCheckBoxMenuItem(i18n.getMenuLabel("view.toolBar"));
	
	private final JCheckBoxMenuItem cbmiLeftSidePaneVisible =
			new JCheckBoxMenuItem(i18n.getMenuLabel("view.leftSidePane"));
	
	private final JCheckBoxMenuItem cbmiRightSidePaneVisible =
			new JCheckBoxMenuItem(i18n.getMenuLabel("view.rightSidePane"));
	
	private final JCheckBoxMenuItem cbmiMidiKeyboardVisible =
			new JCheckBoxMenuItem(i18n.getMenuLabel("view.midiKeyboard"));
	
	private final Timer guiTimer = new Timer(1000, null);
	
	/** Creates a new instance of <code>MainFrame</code> */
	public
	MainFrame() {
		setTitle(i18n.getLabel("MainFrame.title"));
		
		if(Res.iconAppIcon != null) setIconImage(Res.iconAppIcon.getImage());
		
		getContentPane().add(standardBar, BorderLayout.NORTH);
		//getContentPane().add(statusBar, BorderLayout.SOUTH);
		
		rightPane = createRightPane();
		
		hSplitPane = new JSplitPane (
			JSplitPane.HORIZONTAL_SPLIT,
			true,	// continuousLayout 
			leftSidePane, rightPane
		);
		hSplitPane.setResizeWeight(0.5);
		
		addChannelsPane(mainPane.getChannelsPane());
		
		pianoKeyboardPane = new PianoKeyboardPane();
		getChannelsPane(0).addListSelectionListener(pianoKeyboardPane);
		
		pianoRollSplitPane = new JSplitPane (
			JSplitPane.VERTICAL_SPLIT,
			true,	// continuousLayout 
			rootPane, pianoKeyboardPane
		);
		
		pianoRollSplitPane.setResizeWeight(0.5);
		
		rootPane.setLayout(new BorderLayout());
		rootPane.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
		rootPane.setOpaque(false);
		rootPane.add(hSplitPane);
		
		addMenu();
		
		getContentPane().add(pianoRollSplitPane);
		
		int i = preferences().getIntProperty("MainFrame.hSplitDividerLocation", 220);
		hSplitPane.setDividerLocation(i);
		
		setSavedSize();
		
		guiTimer.start();
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
		c.insets = new Insets(0, 3, 0, 0);
		gridbag.setConstraints(rightSidePane, c);
		p.add(rightSidePane);
		
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.0;
		c.weighty = 1.0;
		c.insets = new Insets(0, 3, 0, 3);
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
		boolean b = preferences().getBoolProperty(CONFIRM_APP_QUIT);
		if(b && CC.getSamplerModel().isModified()) {
			JSQuitDlg dlg = new JSQuitDlg(Res.iconQuestion32);
			dlg.setVisible(true);
			if(dlg.isCancelled()) return;
		}
		
		leftSidePane.savePreferences();
		rightSidePane.savePreferences();
		
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
		
		mi = new JMenuItem(a4n.refresh);
		mi.setIcon(null);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
		m.add(mi);
		
		mi = new JMenuItem(a4n.samplerInfo);
		mi.setIcon(null);
		m.add(mi);
		
		mi = new JMenuItem(a4n.resetSampler);
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
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK));
		m.add(mi);
		
		String[] list = preferences().getStringListProperty(RECENT_LSCP_SCRIPTS);
		for(String s : list) recentScripts.add(s);
		
		updateRecentScriptsMenu();
		
		m.add(recentScriptsMenu);
		
		m.addSeparator();
		
		mi = new JMenuItem(a4n.changeBackend);
		mi.setIcon(null);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.CTRL_MASK));
		m.add(mi);
		
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
		
		mi = new JMenuItem(i18n.getMenuLabel("edit.addChannel"));
		m.add(mi);
		mi.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				CC.getSamplerModel().addBackendChannel();
			}
		});
		
		m.addSeparator();
		
		mi = new JMenuItem(a4n.createMidiDevice);
		mi.setIcon(null);
		m.add(mi);
		
		mi = new JMenuItem(a4n.createAudioDevice);
		mi.setIcon(null);
		m.add(mi);
		
		m.addSeparator();
		
		mi = new JMenuItem(a4n.editPreferences);
		mi.setIcon(null);
		mi.setAccelerator(KeyStroke.getKeyStroke (
			KeyEvent.VK_P, KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK
		));
		m.add(mi);
		
		// View
		m = new FantasiaMenu(i18n.getMenuLabel("view"));
		menuBar.add(m);
		
		m.add(cbmiToolBarVisible);
		
		cbmiToolBarVisible.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				showToolBar(cbmiToolBarVisible.getState());
			}
		});
		
		boolean b = preferences().getBoolProperty("toolBar.visible");
		cbmiToolBarVisible.setSelected(b);
		showToolBar(b);
		
		cbmiLeftSidePaneVisible.setAccelerator(KeyStroke.getKeyStroke (
			KeyEvent.VK_L, KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK
		));
		m.add(cbmiLeftSidePaneVisible);
		
		cbmiLeftSidePaneVisible.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				showSidePane(cbmiLeftSidePaneVisible.getState());
			}
		});
		
		b = preferences().getBoolProperty("leftSidePane.visible");
		cbmiLeftSidePaneVisible.setSelected(b);
		showSidePane(b);
		
		cbmiRightSidePaneVisible.setAccelerator(KeyStroke.getKeyStroke (
			KeyEvent.VK_R, KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK
		));
		m.add(cbmiRightSidePaneVisible);
		
		cbmiRightSidePaneVisible.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				showDevicesPane(cbmiRightSidePaneVisible.getState());
			}
		});
		
		b = preferences().getBoolProperty("rightSidePane.visible");
		cbmiRightSidePaneVisible.setSelected(b);
		showDevicesPane(b);
		
		m.addSeparator();
		
		m.add(cbmiMidiKeyboardVisible);
		
		cbmiMidiKeyboardVisible.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				showMidiKeyboard(cbmiMidiKeyboardVisible.getState());
			}
		});
		
		b = preferences().getBoolProperty("midiKeyboard.visible");
		cbmiMidiKeyboardVisible.setSelected(b);
		showMidiKeyboard(b);
		
		
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
		
		mi = new JMenuItem(a4n.browseOnlineTutorial);
		mi.setIcon(null);
		m.add(mi);
		
		m.addSeparator();
		
		mi = new JMenuItem(a4n.helpAbout);
		mi.setIcon(null);
		m.add(mi);
		
		menuBar.add(m);
	}
	
	public RightSidePane
	getRightSidePane() { return rightSidePane; }
	
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
		JSDetailedErrorDlg dlg = new JSDetailedErrorDlg (
			owner, Res.iconWarning32, i18n.getError("error"), err, details
		);
		dlg.setVisible(true);
	}
	
	public void
	showDetailedErrorMessage(Dialog owner, String err, String details) {
		JSDetailedErrorDlg dlg = new JSDetailedErrorDlg (
			owner, Res.iconWarning32, i18n.getError("error"), err, details
		);
		dlg.setVisible(true);
	}
	
	/**
	 * Gets the server address to which to connect. If the server should be
	 * manually selected, a dialog asking the user to choose a server is displayed.
	 */
	public Server
	getServer() {
		boolean b = preferences().getBoolProperty(MANUAL_SERVER_SELECT_ON_STARTUP);
		return getServer(b);
	}
	
	/**
	 * Gets the server address to which to connect. If the server should be
	 * manually selected, a dialog asking the user to choose a server is displayed.
	 * @param manualSelect Determines whether the server should be manually selected.
	 */
	public Server
	getServer(boolean manualSelect) {
		if(manualSelect) {
			JSConnectDlg dlg = new JSConnectDlg();
			dlg.setVisible(true);
			return dlg.getSelectedServer();
		}
		
		int i = preferences().getIntProperty(SERVER_INDEX);
		int size = CC.getServerList().getServerCount();
		if(size == 0) return null;
		if(i >= size) return CC.getServerList().getServer(0);
		
		return CC.getServerList().getServer(i);
	}
	
	public Timer
	getGuiTimer() { return guiTimer; }
	
	protected LSConsoleModel
	getLSConsoleModel() { return getLSConsolePane().getModel(); }
	
	protected LSConsolePane
	getLSConsolePane() {
		return getLSConsoleFrame().getLSConsolePane();
	}
	
	protected LSConsoleFrame
	getLSConsoleFrame() { return lsConsoleFrame; }
	
	protected boolean
	runScript() {
		String s = preferences().getStringProperty("lastScriptLocation");
		JFileChooser fc = new JFileChooser(s);
		fc.setFileFilter(new LscpFileFilter());
		int result = fc.showOpenDialog(this);
		if(result != JFileChooser.APPROVE_OPTION) return false;
		
		String path = fc.getCurrentDirectory().getAbsolutePath();
		preferences().setStringProperty("lastScriptLocation", path);
					
		runScript(fc.getSelectedFile());
		
		return true;
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
	
	private void
	showToolBar(boolean b) {
		preferences().setBoolProperty("toolBar.visible", b);
		standardBar.setVisible(b);
	}
	
	private void
	showSidePane(boolean b) {
		preferences().setBoolProperty("leftSidePane.visible", b);
		rootPane.remove(rightPane);
		rootPane.remove(hSplitPane);
		
		if(b) {
			hSplitPane.setRightComponent(rightPane);
			rootPane.add(hSplitPane);
			int i = preferences().getIntProperty("MainFrame.hSplitDividerLocation", 220);
			
			hSplitPane.setDividerLocation(i);
			hSplitPane.validate();
		} else {
			rootPane.add(rightPane);
			
		}
		
		int w = getPreferredSize().width;
		int h = getSize().height;
		setSize(new Dimension(w, h));
		
		rootPane.revalidate();
		rootPane.validate();
		rootPane.repaint();
		
		SwingUtilities.invokeLater(new Runnable() {
			public void
			run() { sidePanesVisibilityChanged(); }
		});
	}
	
	private void
	showDevicesPane(boolean b) {
		preferences().setBoolProperty("rightSidePane.visible", b);
		
		int width = leftSidePane.getWidth();
		int height = leftSidePane.getPreferredSize().height;
		if(width != 0) leftSidePane.setPreferredSize(new Dimension(width, height));
		
		if(b) {
			int w = preferences().getIntProperty("devicesPane.width", 200);
			
			int h = rightSidePane.getPreferredSize().height;
			rightSidePane.setPreferredSize(new Dimension(w, h));
		} else {
			int w = rightSidePane.getWidth();
			if(w > 0 && w < 200) w = 200;
			if(w != 0) preferences().setIntProperty("devicesPane.width", w);
		}
		
		hSplitPane.setResizeWeight(0.0);
		rightSidePane.setVisible(b);
		hSplitPane.resetToPreferredSizes();
		
		int w = getPreferredSize().width;
		int h = getSize().height;
		setSize(new Dimension(w, h));
		
		rootPane.validate();
		rootPane.repaint();
		//hSplitPane.validate();
		
		SwingUtilities.invokeLater(new Runnable() {
			public void
			run() { sidePanesVisibilityChanged(); }
		});
	}
	
	private void
	showMidiKeyboard(boolean b) {
		preferences().setBoolProperty("midiKeyboard.visible", b);
		if(b) {
			getContentPane().remove(rootPane);
			pianoRollSplitPane.setTopComponent(rootPane);
			getContentPane().add(pianoRollSplitPane);
		} else {
			getContentPane().remove(pianoRollSplitPane);
			pianoRollSplitPane.remove(rootPane);
			getContentPane().add(rootPane);
		}
		
		getContentPane().validate();
		getContentPane().repaint();
	}
	
	private void
	sidePanesVisibilityChanged() {
		boolean leftSidePaneVisible = cbmiLeftSidePaneVisible.isSelected();
		boolean rightSidePaneVisible = cbmiRightSidePaneVisible.isSelected();
		
		if(leftSidePaneVisible && rightSidePaneVisible) {
			hSplitPane.setResizeWeight(0.5);
		} else if(leftSidePaneVisible && !rightSidePaneVisible) {
			hSplitPane.setResizeWeight(1.0);
		}
		
		if(!leftSidePaneVisible && !rightSidePaneVisible) {
			standardBar.showFantasiaLogo(false);
			if(isResizable()) setResizable(false);
		} else {
			standardBar.showFantasiaLogo(true);
			if(!isResizable()) setResizable(true);
		}
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
			setContentAreaFilled(false);
		}
	}

	private class FantasiaMenuBar extends JMenuBar {
		private Insets pixmapInsets = new Insets(6, 6, 0, 6);
		private Insets pixmapInsets2 = new Insets(6, 6, 6, 6);
		
		FantasiaMenuBar() {
			setOpaque(false);
		}
		
		protected void
		paintComponent(Graphics g) {
			super.paintComponent(g);
			if(standardBar.isVisible()) {
				PixmapPane.paintComponent(this, g, Res.gfxMenuBarBg, pixmapInsets);
			} else {
				PixmapPane.paintComponent(this, g, Res.gfxRoundBg14, pixmapInsets2);
			}
		}
	}
}
