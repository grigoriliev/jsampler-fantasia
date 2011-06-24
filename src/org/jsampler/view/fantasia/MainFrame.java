/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2011 Grigor Iliev <grigor@grigoriliev.com>
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
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Rectangle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jsampler.CC;
import org.jsampler.HF;
import org.jsampler.JSUtils;
import org.jsampler.LSConsoleModel;
import org.jsampler.Server;

import org.jsampler.view.JSChannelsPane;
import org.jsampler.view.SessionViewConfig;

import org.jsampler.view.fantasia.basic.FantasiaPainter;
import org.jsampler.view.fantasia.basic.FantasiaPanel;
import org.jsampler.view.fantasia.basic.FantasiaSubPanel;

import org.jsampler.view.std.JSBackendLogFrame;
import org.jsampler.view.std.JSConnectDlg;
import org.jsampler.view.std.JSDetailedErrorDlg;
import org.jsampler.view.std.JSQuitDlg;
import org.jsampler.view.std.JSamplerHomeChooser;
import org.jsampler.view.std.StdMainFrame;
import org.jsampler.view.std.StdUtils;

import static org.jsampler.view.fantasia.A4n.a4n;
import static org.jsampler.view.fantasia.FantasiaI18n.i18n;
import static org.jsampler.view.fantasia.FantasiaPrefs.preferences;
import static org.jsampler.view.std.StdPrefs.*;


/**
 *
 * @author Grigor Iliev
 */
public class MainFrame extends StdMainFrame {
	public final static int MAX_CHANNEL_LANE_NUMBER = 8;

	private final StandardBar standardBar = new StandardBar();
	private final FantasiaMenuBar menuBar = new FantasiaMenuBar();
	private final JPanel rootPane = new RootPane();
	private final BottomPane bottomPane;
	private final MainPane mainPane;
	private final PianoKeyboardPane pianoKeyboardPane;
	
	private final JMenu recentScriptsMenu =
		new JMenu(i18n.getMenuLabel("actions.recentScripts"));
	
	private final JSplitPane hSplitPane;
	
	private final LeftSidePane leftSidePane;
	private final RightSidePane rightSidePane;
	private final JPanel rightPane;
	
	//private final StatusBar statusBar = new StatusBar();
	
	private final LSConsoleFrame lsConsoleFrame = new LSConsoleFrame();
	private SamplerBrowserFrame samplerBrowserFrame = null;
	private final Vector<String> recentScripts = new Vector<String>();
	
	private final JSBackendLogFrame backendLogFrame = new JSBackendLogFrame();
		
	
	private final JCheckBoxMenuItem cbmiToolBarVisible =
			new JCheckBoxMenuItem(i18n.getMenuLabel("view.toolBar"));
	
	private final JCheckBoxMenuItem cbmiLeftSidePaneVisible =
			new JCheckBoxMenuItem(i18n.getMenuLabel("view.leftSidePane"));
	
	private final JCheckBoxMenuItem cbmiRightSidePaneVisible =
			new JCheckBoxMenuItem(i18n.getMenuLabel("view.rightSidePane"));
	
	private final JCheckBoxMenuItem cbmiMidiKeyboardVisible =
			new JCheckBoxMenuItem(i18n.getMenuLabel("view.midiKeyboard"));
	
	private final JCheckBoxMenuItem cbmiAlwaysOnTop =
			new JCheckBoxMenuItem(i18n.getMenuLabel("view.alwaysOnTop"));

	private final Timer guiTimer = new Timer(1000, null);
	
	/** Creates a new instance of <code>MainFrame</code> */
	public
	MainFrame() {
		setTitle(i18n.getLabel("MainFrame.title"));
		//setUndecorated(true);
		if(Res.iconAppIcon != null) setIconImage(Res.iconAppIcon.getImage());
		
		CC.setMainFrame(this); // TODO: 
		mainPane = new MainPane();
		leftSidePane = new LeftSidePane();
		rightSidePane = new RightSidePane();
		
		setSelectedChannelsPane(mainPane.getChannelsPane(0));
		
		getContentPane().add(standardBar, BorderLayout.NORTH);
		
		rightPane = createRightPane();
		
		hSplitPane = new JSplitPane (
			JSplitPane.HORIZONTAL_SPLIT,
			true,	// continuousLayout 
			leftSidePane, rightPane
		);
		hSplitPane.setResizeWeight(0.5);
		
		pianoKeyboardPane = new PianoKeyboardPane();
		
		for(int i = 0; i < mainPane.getChannelsPaneCount(); i++) {
			addChannelsPane(mainPane.getChannelsPane(i));
			getChannelsPane(i).addListSelectionListener(pianoKeyboardPane);
		}

		PropertyChangeListener l = new PropertyChangeListener() {
			public void
			propertyChange(PropertyChangeEvent e) {
				onChangeChannelLaneCount();
			}
		};

		preferences().addPropertyChangeListener("channelLanes.count", l);
		
		int h = preferences().getIntProperty("midiKeyboard.height");
		setMidiKeyboardHeight(h);
		
		l = new PropertyChangeListener() {
			public void
			propertyChange(PropertyChangeEvent e) {
				int h = preferences().getIntProperty("midiKeyboard.height");
				setMidiKeyboardHeight(h);
			}
		};
		
		CC.preferences().addPropertyChangeListener("midiKeyboard.height", l);
		
		bottomPane = new BottomPane();
		
		hSplitPane.setOpaque(false);
		rootPane.add(hSplitPane);
		rootPane.add(bottomPane, BorderLayout.SOUTH);
		add(rootPane);

		if(CC.isMacOS()) {
			try {
				String s = "org.jsampler.view.fantasia.MacOSApplicationHandler";
				Class.forName(s).newInstance(); }
			catch(Throwable e) { }
		}
		
		addMenu();
		
		if(CC.getViewConfig().isUsingScreenMenuBar()) {
			// fix for moving the menu bar on top of the screen
			// when running on Mac OS and third party plugin is used
			((ViewConfig)CC.getViewConfig()).restoreMenuProperties();
		}
		
		int i = preferences().getIntProperty("MainFrame.hSplitDividerLocation", 220);
		hSplitPane.setDividerLocation(i);
		
		setSavedSize();
		
		guiTimer.start();
	}
	
	private JPanel
	createRightPane() {
		JPanel p = new FantasiaPanel();
		p.setOpaque(false);
		
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
		c.insets = new Insets(0, 0, 0, 3);
		c.fill = GridBagConstraints.VERTICAL;
		gridbag.setConstraints(mainPane, c);
		p.add(mainPane);
		
		return p;
	}
	
	private void
	setSavedSize() {
		Rectangle r = StdUtils.getWindowBounds("MainFrame");
		if(r == null) {
			setDefaultSizeAndLocation();
			return;
		}
		
		setBounds(r);
	}
	
	private void
	setDefaultSizeAndLocation() {
		setPreferredSize(new Dimension(900, 600));
		pack();
		setLocationRelativeTo(null);
	}
	
	
	/** Invoked when this window is about to close. */
	@Override
	public void
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
		
		StdUtils.saveWindowBounds("MainFrame", getBounds());
		
		String[] list = recentScripts.toArray(new String[recentScripts.size()]);
		preferences().setStringListProperty(RECENT_LSCP_SCRIPTS, list);
		
		if(preferences().getBoolProperty(SAVE_LS_CONSOLE_HISTORY)) {
			if(lsConsoleFrame != null) getLSConsolePane().saveConsoleHistory();
		}
		
		if(getBackendLogFrame() != null) getBackendLogFrame().stopTimer();
		if(getLSConsolePane() != null) getLSConsolePane().disconnect();
		
		super.onWindowClose();
	}

	private void
	onChangeChannelLaneCount() {
		int newCount = preferences().getIntProperty("channelLanes.count");
		if(newCount < 1 || newCount > MAX_CHANNEL_LANE_NUMBER) return;
		if(newCount == getChannelsPaneCount()) return;
		int current = getChannelsPaneIndex(getSelectedChannelsPane());

		if(newCount > getChannelsPaneCount()) {
			int d = newCount - getChannelsPaneCount();
			for(int i = 0; i < d; i++) {
				JSChannelsPane p = mainPane.addChannelsPane();
				addChannelsPane(p);
				p.addListSelectionListener(pianoKeyboardPane);
			}
		} else {
			int d = getChannelsPaneCount() - newCount;
			for(int i = 0; i < d; i++) {
				int idx = getChannelsPaneCount() - 1 - i;
				if(getChannelsPane(idx).getChannelCount() > 0) {
					String s;
					s = i18n.getError("MainFrame.notEmptyChannelLane!", idx + 1);
					HF.showErrorMessage(s);
					return;
				}
			}

			for(int i = 0; i < d; i++) {
				int idx = getChannelsPaneCount() - 1;
				JSChannelsPane p = getChannelsPane(idx);
				removeChannelsPane(p);
				p.removeListSelectionListener(pianoKeyboardPane);
				mainPane.removeChannelsPane(idx);
			}
		}

		if(newCount == 1) {
			mainPane.getButtonsPanel().setVisible(false);
		} else if(!mainPane.getButtonsPanel().isVisible()) {
			mainPane.getButtonsPanel().setVisible(true);
		}
		mainPane.getButtonsPanel().setButtonNumber(newCount);
		if(current < 0 || current >= getChannelsPaneCount()) current = 0;
		setSelectedChannelsPane(getChannelsPane(current));
	}
	
	@Override
	public void
	setVisible(boolean b) {
		if(b == isVisible()) return;
		
		super.setVisible(b);
		
		if(b && preferences().getBoolProperty("MainFrame.windowMaximized")) {
			setExtendedState(getExtendedState() | MAXIMIZED_BOTH);
		}
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

		int modKey = CC.getViewConfig().getDefaultModKey();
		
		mi = new JMenuItem(a4n.exportSamplerConfig);
		mi.setIcon(null);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, modKey));
		exportMenu.add(mi);
		
		mi = new JMenuItem(a4n.exportMidiInstrumentMaps);
		mi.setIcon(null);
		exportMenu.add(mi);
		
		m.addSeparator();
		
		mi = new JMenuItem(a4n.loadScript);
		mi.setIcon(null);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, modKey));
		m.add(mi);
		
		String[] list = preferences().getStringListProperty(RECENT_LSCP_SCRIPTS);
		for(String s : list) recentScripts.add(s);
		
		updateRecentScriptsMenu();
		
		m.add(recentScriptsMenu);
		
		m.addSeparator();
		
		mi = new JMenuItem(a4n.changeBackend);
		mi.setIcon(null);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, modKey));
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
			KeyEvent.VK_P, modKey | KeyEvent.SHIFT_MASK
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
			KeyEvent.VK_L, modKey | KeyEvent.SHIFT_MASK
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
			KeyEvent.VK_R, modKey | KeyEvent.SHIFT_MASK
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
		
		m.add(cbmiMidiKeyboardVisible);
		
		cbmiMidiKeyboardVisible.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				setMidiKeyboardVisible(cbmiMidiKeyboardVisible.getState());
			}
		});
		
		b = preferences().getBoolProperty("midiKeyboard.visible");
		cbmiMidiKeyboardVisible.setSelected(b);
		setMidiKeyboardVisible(b);

		m.addSeparator();

		cbmiAlwaysOnTop.setAccelerator(KeyStroke.getKeyStroke (
			KeyEvent.VK_T, modKey | KeyEvent.SHIFT_MASK
		));
		m.add(cbmiAlwaysOnTop);

		cbmiAlwaysOnTop.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				setWindowAlwaysOnTop(cbmiAlwaysOnTop.getState());
			}
		});

		b = preferences().getBoolProperty("mainFrame.alwaysOnTop");
		cbmiAlwaysOnTop.setSelected(b);
		setWindowAlwaysOnTop(b);
		
		
		// Channels
		m = new FantasiaMenu(i18n.getMenuLabel("channels"));
		
		mi = new JMenuItem(i18n.getMenuLabel("channels.newChannel"));
		mi.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				CC.getSamplerModel().addBackendChannel();
			}
		});
		m.add(mi);
		
		m.addSeparator();
		
		MenuManager.ChannelViewGroup group = new MenuManager.ChannelViewGroup();
		MenuManager.getMenuManager().registerChannelViewGroup(group);
		
		for(JMenuItem menuItem : group.getMenuItems()) m.add(menuItem);
		
		m.addSeparator();
		
		m.add(new JMenuItem(a4n.moveChannelsOnTop));
		m.add(new JMenuItem(a4n.moveChannelsUp));
		m.add(new JMenuItem(a4n.moveChannelsDown));
		m.add(new JMenuItem(a4n.moveChannelsAtBottom));
		
		m.add(new ToPanelMenu());
		
		m.addSeparator();
		
		mi = new JMenuItem(a4n.selectAllChannels);
		mi.setIcon(null);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, modKey));
		m.add(mi);
		
		mi = new JMenuItem(a4n.deselectChannels);
		mi.setIcon(null);
		mi.setAccelerator(KeyStroke.getKeyStroke (
			KeyEvent.VK_A, modKey | KeyEvent.SHIFT_MASK
		));
		m.add(mi);
		
		menuBar.add(m);
		
		// Window
		m = new FantasiaMenu(i18n.getMenuLabel("window"));
		menuBar.add(m);
		
		mi = new JMenuItem(a4n.windowLSConsole);
		mi.setIcon(null);
		m.add(mi);
		
		mi = new JMenuItem(a4n.windowInstrumentsDb);
		mi.setIcon(null);
		m.add(mi);

		mi = new JMenuItem(a4n.windowSamplerBrowser);
		mi.setIcon(null);
		m.add(mi);
		
		m.addSeparator();
		
		final JMenuItem mi2 = new JMenuItem(i18n.getMenuLabel("window.backendLog"));
		m.add(mi2);
		mi2.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				if(getBackendLogFrame().isVisible()) {
					getBackendLogFrame().setVisible(false);
				}
				
				getBackendLogFrame().setVisible(true);
			}
		});
		
		mi2.setEnabled(CC.getBackendProcess() != null);
		
		CC.addBackendProcessListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				mi2.setEnabled(CC.getBackendProcess() != null);
			}
		});
		
		
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
	
	public static class ToPanelMenu extends FantasiaMenu implements ListSelectionListener {
		public
		ToPanelMenu() {
			super(i18n.getMenuLabel("channels.toPanel"));
			setEnabled(CC.getMainFrame().getSelectedChannelsPane().hasSelectedChannel());
			
			CC.getMainFrame().addChannelsPaneSelectionListener(this);
			
			for(int i = 0; i < CC.getMainFrame().getChannelsPaneCount(); i++) {
				JSChannelsPane p = CC.getMainFrame().getChannelsPane(i);
				add(new JMenuItem(new A4n.MoveChannelsToPanel(p)));
				p.addListSelectionListener(this);
			}
		}
		
		@Override
		public void
		valueChanged(ListSelectionEvent e) {
			setEnabled(CC.getMainFrame().getSelectedChannelsPane().hasSelectedChannel());
		}
	}
	
	public RightSidePane
	getRightSidePane() { return rightSidePane; }
	
	@Override
	public A4n
	getA4n() { return A4n.a4n; }
	
	/**
	 * This method does nothing, because <b>Fantasia</b> has constant
	 * number of panes containing sampler channels, which can not be changed.
	 */
	@Override
	public void
	insertChannelsPane(JSChannelsPane pane, int idx) {
		
	}
	
	@Override
	public JSChannelsPane
	getSelectedChannelsPane() { return mainPane.getSelectedChannelsPane(); }
	
	@Override
	public void
	setSelectedChannelsPane(JSChannelsPane pane) {
		mainPane.setSelectedChannelsPane(pane);
		fireChannelsPaneSelectionChanged();
	}
	
	@Override
	public void
	installJSamplerHome() {
		JSamplerHomeChooser chooser = new JSamplerHomeChooser(this);
		chooser.setVisible(true);
		if(chooser.isCancelled()) return;
		
		JSUtils.changeJSamplerHome(chooser.getJSamplerHome());
	}
	
	@Override
	public void
	showDetailedErrorMessage(Frame owner, String err, String details) {
		JSDetailedErrorDlg dlg = new JSDetailedErrorDlg (
			owner, Res.iconWarning32, i18n.getError("error"), err, details
		);
		dlg.setVisible(true);
	}
	
	@Override
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
	@Override
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
	@Override
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
	
	@Override
	public LSConsoleModel
	getLSConsoleModel() { return getLSConsolePane().getModel(); }
	
	protected LSConsolePane
	getLSConsolePane() {
		return getLSConsoleFrame().getLSConsolePane();
	}
	
	protected LSConsoleFrame
	getLSConsoleFrame() { return lsConsoleFrame; }

	protected SamplerBrowserFrame
	getSamplerBrowserFrame() {
		if(samplerBrowserFrame == null) samplerBrowserFrame = new SamplerBrowserFrame();
		return samplerBrowserFrame;
	}
	
	public JSBackendLogFrame
	getBackendLogFrame() { return backendLogFrame; }
	
	protected boolean
	runScript() {
		File f = StdUtils.showOpenLscpFileChooser();
		if(f == null) return false;
		runScript(f);
		
		return true;
	}
	
	@Override
	public void
	runScript(String script) { runScript(new File(script)); }
	
	private void
	runScript(File script) {
		FileReader fr;
		try { fr = new FileReader(script); }
		catch(FileNotFoundException e) {
			HF.showErrorMessage(i18n.getError("fileNotFound!", script.getAbsolutePath()));
			return;
		}
		
		String prefix = "#jsampler.fantasia: ";
		Vector<String> v = new Vector<String>();
		BufferedReader br = new BufferedReader(fr);
		
		try {
			String s = br.readLine();
			while(s != null) {
				getLSConsoleModel().setCommandLineText(s);
				getLSConsoleModel().execCommand();
				if(s.startsWith(prefix)) v.add(s.substring(prefix.length()));
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
		
		CC.getViewConfig().setSessionViewConfig(
			new SessionViewConfig(v.toArray(new String[v.size()]))
		);
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
	
	public void
	setMidiKeyboardVisible(boolean b) {
		preferences().setBoolProperty("midiKeyboard.visible", b);
		pianoKeyboardPane.setVisible(b);
		
		if(cbmiMidiKeyboardVisible.isSelected() != b) {
			cbmiMidiKeyboardVisible.setSelected(b);
		}
		
		if(standardBar.btnMidiKeyboard.isSelected() != b) {
			standardBar.btnMidiKeyboard.setSelected(b);
		}
		
		if(pianoKeyboardPane.btnPower.isSelected() != b) {
			pianoKeyboardPane.btnPower.setSelected(b);
		}
		
		rootPane.validate();
		rootPane.repaint();
	}

	private void
	setWindowAlwaysOnTop(boolean b) {
		preferences().setBoolProperty("mainFrame.alwaysOnTop", b);
		setAlwaysOnTop(b);

		if(cbmiAlwaysOnTop.isSelected() != b) {
			cbmiAlwaysOnTop.setSelected(b);
		}
	}
	
	public void
	setMidiKeyboardHeight(int height) {
		Dimension d = pianoKeyboardPane.getPreferredSize();
		d = new Dimension(d.width, height);
		pianoKeyboardPane.setPreferredSize(d);
		pianoKeyboardPane.setMinimumSize(d);
		pianoKeyboardPane.revalidate();
		pianoKeyboardPane.repaint();
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
		
		@Override
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
		FantasiaMenuBar() {
			setOpaque(false);
			setBorder(BorderFactory.createEmptyBorder(2, 6, 0, 0));
		}
		
		@Override
		protected void
		paintComponent(Graphics g) {
			//super.paintComponent(g);
			Graphics2D g2 = (Graphics2D)g;
			
			Paint oldPaint = g2.getPaint();
			Composite oldComposite = g2.getComposite();
			
			double h = getSize().getHeight();
			double w = getSize().getWidth();
			
			FantasiaPainter.paintGradient(g2, 0.0, 0.0, w - 1, h - 1, FantasiaPainter.color6, FantasiaPainter.color5);
			
			FantasiaPainter.Border b;
			
			
			if(standardBar.isVisible()) {
				b = new FantasiaPainter.Border(true, true, false, true);
				FantasiaPainter.paintBoldOuterBorder(g2, 0, 0, w - 1, h + 1, b);
			} else {
				b = new FantasiaPainter.Border(true, true, true, true);
				FantasiaPainter.paintBoldOuterBorder(g2, 0, 0, w - 1, h - 1, b);
			}
			
			g2.setPaint(oldPaint);
			g2.setComposite(oldComposite);
		}
	}
	
	class RootPane extends FantasiaSubPanel {
		private final Color color1 = new Color(0x454545);
		private final Color color2 = new Color(0x2e2e2e);
		
		RootPane() {
			setLayout(new BorderLayout());
			setBorder(BorderFactory.createEmptyBorder(9, 10, 6, 10));
			setOpaque(false);
		
		}
	
		@Override
		public void
		paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D)g;
			
			Paint oldPaint = g2.getPaint();
			Composite oldComposite = g2.getComposite();
			
			double h = getSize().getHeight();
			double w = getSize().getWidth();
			
			FantasiaPainter.paintBorder(g2, 0, -3, w - 1, h - 1, 6, false);
			paintComponent(g2, 5, 1, w - 10, h - 6, color1, color2);
			
			g2.setPaint(oldPaint);
			g2.setComposite(oldComposite);
		}
	}
	
	class BottomPane extends FantasiaPanel {
		BottomPane() {
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			setOpaque(false);
			add(pianoKeyboardPane);
			
		}
	}
}
