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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;

import java.util.logging.Level;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sf.juife.NavigationPage;
import net.sf.juife.NavigationPane;

import org.jsampler.CC;
import org.jsampler.HF;
import org.jsampler.LSConsoleModel;
import org.jsampler.OrchestraModel;
import org.jsampler.Prefs;

import org.jsampler.view.JSChannel;
import org.jsampler.view.JSChannelsPane;
import org.jsampler.view.LscpFileFilter;

import static org.jsampler.view.classic.ClassicI18n.i18n;
import static org.jsampler.view.classic.LeftPane.getLeftPane;


/**
 *
 * @author Grigor Iliev
 */
public class
MainFrame extends org.jsampler.view.JSMainFrame implements ChangeListener, ListSelectionListener {
	public static ImageIcon applicationIcon = Res.appIcon;
	
	private final ChannelsBar channelsBar = new ChannelsBar();
	private final Statusbar statusbar = new Statusbar();
	private final JMenuBar menuBar = new JMenuBar();
	private final JMenu recentScriptsMenu =
		new JMenu(i18n.getMenuLabel("actions.recentScripts"));
	private final JMenu tabsMenu = new JMenu(i18n.getMenuLabel("channels.MoveToTab"));
	
	private final JSplitPane vSplitPane;
	private final JSplitPane hSplitPane;
	
	private final JPanel mainPane = new JPanel();
	private final StandardBar standardBar = new StandardBar();
	private final JPanel channelsPane = new JPanel(new BorderLayout());
	private final JPanel rightPane = new JPanel();
	private final JPanel bottomPane = new JPanel();
	private final LSConsolePane lsConsolePane = new LSConsolePane(this);
	private LSConsoleDlg lsConsoleDlg = null;
	
	private final JTabbedPane tabbedPane =
		new JTabbedPane(JTabbedPane.BOTTOM, JTabbedPane.SCROLL_TAB_LAYOUT);
	private final Vector<JMenuItem> miList = new Vector<JMenuItem>();
	
	private final JCheckBoxMenuItem cbmiLeftPaneVisible =
			new JCheckBoxMenuItem(i18n.getMenuLabel("view.leftPane"));
	
	private final JCheckBoxMenuItem cbmiStandardBarVisible =
			new JCheckBoxMenuItem(i18n.getMenuLabel("view.toolbars.standard"));
	
	private final JCheckBoxMenuItem cbmiLSConsoleShown =
			new JCheckBoxMenuItem(i18n.getMenuLabel("view.lsconsole"));
	
	private boolean lsConsolePopOut;
	
	private final Vector<String> recentScripts = new Vector<String>();
		
	
	/** Creates a new instance of <code>MainFrame</code>. */
	public
	MainFrame() {
		setTitle(i18n.getLabel("MainFrame.title"));
		
		getContentPane().add(standardBar, BorderLayout.NORTH);
		getContentPane().add(mainPane);
		
		mainPane.setLayout(new BorderLayout());
		mainPane.add(statusbar, BorderLayout.SOUTH);
		
		ChannelsPane p = new ChannelsPane("Untitled");
		p.addListSelectionListener(this);
		getChannelsPaneList().add(p);
		miList.add(new JMenuItem(new A4n.MoveChannelsTo(p)));
		
		channelsPane.add(getChannelsPane(0));
		
		bottomPane.setLayout(new BorderLayout());
		
		rightPane.setLayout(new BorderLayout());
		
		rightPane.add(channelsBar, BorderLayout.NORTH);
		rightPane.add(channelsPane);
		
		hSplitPane = new JSplitPane (
			JSplitPane.HORIZONTAL_SPLIT,
			true,	// continuousLayout 
			getLeftPane(),
			rightPane
		);
		
		hSplitPane.setOneTouchExpandable(true);
		if(ClassicPrefs.getSaveWindowProperties()) {
			hSplitPane.setDividerLocation(ClassicPrefs.getHSplitDividerLocation());
		}
		
		mainPane.add(hSplitPane);
		
		
		vSplitPane = new JSplitPane (
			JSplitPane.VERTICAL_SPLIT,
			true,	// continuousLayout 
			channelsPane,
			bottomPane
		);
		
		vSplitPane.setDividerSize(3);
		vSplitPane.setDividerLocation(ClassicPrefs.getVSplitDividerLocation());
		
		rightPane.add(vSplitPane);
		
		if(applicationIcon != null) setIconImage(applicationIcon.getImage());
		
		initMainFrame();
		
		pack();
		
		if(ClassicPrefs.getSaveWindowProperties()) setSavedSize();
		else setDefaultSize();
		
		if(ClassicPrefs.getSaveLeftPaneState()) {
			NavigationPage page =
				getLeftPane().getPages()[ClassicPrefs.getLeftPanePageIndex()];
			
			getLeftPane().getModel().addPage(page);
			getLeftPane().getModel().clearHistory();
			
			int idx = ClassicPrefs.getCurrentOrchestraIndex();
			if(idx >= 0 && idx < CC.getOrchestras().getOrchestraCount()) {
				OrchestraModel om = CC.getOrchestras().getOrchestra(idx);
				getLeftPane().getOrchestrasPage().setSelectedOrchestra(om);
			}
		}
		
		//CC.getInstrumentsDbTreeModel(); // used to initialize the db tree model
	}
	
	/** Invoked when this window is about to close. */
	protected void
	onWindowClose() {
		if(ClassicPrefs.getSaveWindowProperties()) {
			ClassicPrefs.setWindowMaximized (
				"Mainframe", (getExtendedState() & MAXIMIZED_BOTH) == MAXIMIZED_BOTH
			);
			
			setVisible(false);
			if(ClassicPrefs.getWindowMaximized("Mainframe")) {
				//setExtendedState(getExtendedState() & ~MAXIMIZED_BOTH);
				CC.cleanExit();
				return;
			}
			
			java.awt.Point p = getLocation();
			Dimension d = getSize();
			StringBuffer sb = new StringBuffer();
			sb.append(p.x).append(',').append(p.y).append(',');
			sb.append(d.width).append(',').append(d.height);
			ClassicPrefs.setWindowSizeAndLocation("Mainframe", sb.toString());
			
			ClassicPrefs.setHSplitDividerLocation(hSplitPane.getDividerLocation());
		}
		
		if(ClassicPrefs.getSaveLeftPaneState()) {
			int idx = 0;
			for(int i = 0; i < getLeftPane().getPages().length; i++) {
				if(getLeftPane().getPages()[i] == getLeftPane().getCurrentPage()) {
					idx = i;
					break;
				}
			}
			
			ClassicPrefs.setLeftPanePageIndex(idx);
			
			idx = getLeftPane().getOrchestrasPage().getCurrentOrchestraIndex();
			
			if(idx >= 0 && idx < CC.getOrchestras().getOrchestraCount())
				ClassicPrefs.setCurrentOrchestraIndex(idx);
		}
		
		StringBuffer sb = new StringBuffer();
		for(String s : recentScripts) sb.append(s).append("\n");
		ClassicPrefs.setRecentScripts(sb.toString());
		
		if(ClassicPrefs.getSaveConsoleHistory()) lsConsolePane.saveConsoleHistory();
		
		ClassicPrefs.setShowLSConsole(isLSConsoleShown());
		ClassicPrefs.setLSConsolePopOut(isLSConsolePopOut());
		
		ClassicPrefs.setVSplitDividerLocation(vSplitPane.getDividerLocation());
		super.onWindowClose();
	}
	
	private void
	initMainFrame() {
		addMenu();
		handleEvents();
	}
	
	private void
	setDefaultSize() {
		Dimension dimension = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		double width = dimension.getWidth();
		double height = dimension.getHeight();
		setBounds(50, 100, (int) width - 100, (int) height - 200);
	}
	
	private void
	setSavedSize() {
		String s = ClassicPrefs.getWindowSizeAndLocation("Mainframe");
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
		
		if(ClassicPrefs.getWindowMaximized("Mainframe"))
			setExtendedState(getExtendedState() | MAXIMIZED_BOTH);
	}
	
	private void
	addMenu() {
		JMenu m;
		JMenuItem mi;
		
		setJMenuBar(menuBar);
		
		// Actions
		m = new JMenu(i18n.getMenuLabel("actions"));
		menuBar.add(m);
		
		mi = new JMenuItem(A4n.connect);
		mi.setIcon(null);
		//mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_MASK));
		m.add(mi);
		
		mi = new JMenuItem(A4n.refresh);
		mi.setIcon(null);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
		m.add(mi);
		
		mi = new JMenuItem(A4n.resetSampler);
		mi.setIcon(null);
		m.add(mi);
		
		mi = new JMenuItem(A4n.samplerInfo);
		mi.setIcon(null);
		m.add(mi);
		
		JMenu midiInstrMenu = new JMenu(i18n.getMenuLabel("actions.midiInstruments"));
		m.add(midiInstrMenu);
		
		mi = new JMenuItem(A4n.addMidiInstrumentMap);
		mi.setIcon(null);
		midiInstrMenu.add(mi);
		
		mi = new JMenuItem(A4n.removeMidiInstrumentMap);
		mi.setIcon(null);
		midiInstrMenu.add(mi);
		
		mi = new JMenuItem(A4n.addMidiInstrumentWizard);
		mi.setIcon(null);
		midiInstrMenu.add(mi);
		
		m.addSeparator();
		
		JMenu exportMenu = new JMenu(i18n.getMenuLabel("actions.export"));
		m.add(exportMenu);
		
		mi = new JMenuItem(A4n.exportSamplerConfig);
		mi.setIcon(null);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK));
		exportMenu.add(mi);
		
		mi = new JMenuItem(A4n.exportMidiInstrumentMaps);
		mi.setIcon(null);
		exportMenu.add(mi);
		
		m.addSeparator();
		
		mi = new JMenuItem(A4n.loadScript);
		mi.setIcon(null);
		m.add(mi);
		
		String s = ClassicPrefs.getRecentScripts();
		BufferedReader br = new BufferedReader(new StringReader(s));
		
		try {
			s = br.readLine();
			while(s != null) {
				recentScripts.add(s);
				s = br.readLine();
			}
		} catch(Exception x) {
			CC.getLogger().log(Level.INFO, HF.getErrorMessage(x), x);
		}
		
		updateRecentScriptsMenu();
		
		m.add(recentScriptsMenu);
		
		m.addSeparator();
		
		mi = new JMenuItem(i18n.getMenuLabel("actions.exit"));
		m.add(mi);
		mi.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { CC.cleanExit(); }
		});
		
		// Edit
		m = new JMenu(i18n.getMenuLabel("edit"));
		menuBar.add(m);
		
		mi = new JMenuItem(i18n.getMenuLabel("edit.audioDevices"));
		m.add(mi);
		mi.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				if(!isLeftPaneVisible()) cbmiLeftPaneVisible.doClick(0);
				LeftPane.getLeftPane().showAudioDevicesPage();
			}
		});
		
		mi = new JMenuItem(i18n.getMenuLabel("edit.midiDevices"));
		m.add(mi);
		mi.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				if(!isLeftPaneVisible()) cbmiLeftPaneVisible.doClick(0);
				LeftPane.getLeftPane().showMidiDevicesPage();
			}
		});
		
		mi = new JMenuItem(i18n.getMenuLabel("edit.orchestras"));
		m.add(mi);
		mi.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				if(!isLeftPaneVisible()) cbmiLeftPaneVisible.doClick(0);
				LeftPane.getLeftPane().showManageOrchestrasPage();
			}
		});
		
		m.addSeparator();
		
		mi = new JMenuItem(A4n.preferences);
		mi.setIcon(null);
		mi.setAccelerator(KeyStroke.getKeyStroke (
			KeyEvent.VK_P, KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK
		));
		m.add(mi);
		
		// View
		m = new JMenu(i18n.getMenuLabel("view"));
		menuBar.add(m);
		
		JMenu toolbarsMenu = new JMenu(i18n.getMenuLabel("view.toolbars"));
		m.add(toolbarsMenu);
		
		toolbarsMenu.add(cbmiStandardBarVisible);
		cbmiStandardBarVisible.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				showStandardBar(cbmiStandardBarVisible.getState());
			}
		});
		
		boolean b = ClassicPrefs.shouldShowStandardBar();
		cbmiStandardBarVisible.setSelected(b);
		showStandardBar(b);
		
		final JCheckBoxMenuItem cbmi =
			new JCheckBoxMenuItem(i18n.getMenuLabel("view.toolbars.channels"));
		
		toolbarsMenu.add(cbmi);
		cbmi.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { showChannelsBar(cbmi.getState()); }
		});
		
		b = ClassicPrefs.shouldShowChannelsBar();
		cbmi.setSelected(b);
		showChannelsBar(b);
		
		m.add(cbmiLeftPaneVisible);
		cbmiLeftPaneVisible.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				showLeftPane(cbmiLeftPaneVisible.getState());
			}
		});
		
		b = ClassicPrefs.shouldShowLeftPane();
		cbmiLeftPaneVisible.setSelected(b);
		showLeftPane(b);
		
		final JCheckBoxMenuItem cbmi2 =
			new JCheckBoxMenuItem(i18n.getMenuLabel("view.statusbar"));
		
		m.add(cbmi2);
		cbmi2.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { showStatusbar(cbmi2.getState()); }
		});
		b = ClassicPrefs.shouldShowStatusbar();
		cbmi2.setSelected(b);
		showStatusbar(b);
		
		m.addSeparator();
		
		setLSConsolePopOut(ClassicPrefs.isLSConsolePopOut());
		cbmiLSConsoleShown.setSelected(ClassicPrefs.shouldShowLSConsole());
		showLSConsole(ClassicPrefs.shouldShowLSConsole());
		
		cbmiLSConsoleShown.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				showLSConsole(cbmiLSConsoleShown.isSelected());
			}
		});
		m.add(cbmiLSConsoleShown);
		
		lsConsolePane.updateLSConsoleViewMode();
		
		// Channels
		m = new JMenu(i18n.getMenuLabel("channels"));
		menuBar.add(m);
		
		mi = new JMenuItem(A4n.newChannel);
		mi.setIcon(null);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_MASK));
		m.add(mi);
		
		mi = new JMenuItem(A4n.newChannelWizard);
		mi.setIcon(null);
		mi.setAccelerator(KeyStroke.getKeyStroke (
			KeyEvent.VK_N, KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK)
		);
		m.add(mi);
		
		mi = new JMenuItem(A4n.duplicateChannels);
		mi.setIcon(null);
		m.add(mi);
		
		m.addSeparator();
		
		mi = new JMenuItem(A4n.moveChannelsOnTop);
		mi.setIcon(null);
		mi.setAccelerator(KeyStroke.getKeyStroke (
			KeyEvent.VK_UP, KeyEvent.ALT_MASK | KeyEvent.SHIFT_MASK
		));
		m.add(mi);
		
		mi = new JMenuItem(A4n.moveChannelsUp);
		mi.setIcon(null);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_UP, KeyEvent.ALT_MASK));
		m.add(mi);
		
		mi = new JMenuItem(A4n.moveChannelsDown);
		mi.setIcon(null);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, KeyEvent.ALT_MASK));
		m.add(mi);
		
		mi = new JMenuItem(A4n.moveChannelsAtBottom);
		mi.setIcon(null);
		mi.setAccelerator(KeyStroke.getKeyStroke (
			KeyEvent.VK_DOWN, KeyEvent.ALT_MASK | KeyEvent.SHIFT_MASK
		));
		m.add(mi);
		
		tabsMenu.setEnabled(false);
		m.add(tabsMenu);
		
		m.addSeparator();
		
		mi = new JMenuItem(A4n.selectAllChannels);
		mi.setIcon(null);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_MASK));
		m.add(mi);
		
		mi = new JMenuItem(A4n.deselectChannels);
		mi.setIcon(null);
		mi.setAccelerator(KeyStroke.getKeyStroke (
			KeyEvent.VK_A, KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK
		));
		m.add(mi);
		
		m.addSeparator();
		
		mi = new JMenuItem(A4n.removeChannels);
		mi.setIcon(null);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, KeyEvent.SHIFT_MASK));
		m.add(mi);
		
		
		// Tabs
		m = new JMenu(i18n.getMenuLabel("tabs"));
		menuBar.add(m);
		
		mi = new JMenuItem(A4n.newChannelsTab);
		mi.setIcon(null);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, KeyEvent.CTRL_MASK));
		m.add(mi);
		
		mi = new JMenuItem(A4n.editTabTitle);
		mi.setIcon(null);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
		m.add(mi);
		
		m.addSeparator();
		
		mi = new JMenuItem(A4n.moveTab2Beginning);
		mi.setIcon(null);
		mi.setAccelerator(KeyStroke.getKeyStroke (
			KeyEvent.VK_LEFT, KeyEvent.ALT_MASK | KeyEvent.SHIFT_MASK
		));
		m.add(mi);
		
		mi = new JMenuItem(A4n.moveTab2Left);
		mi.setIcon(null);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.ALT_MASK));
		m.add(mi);
		
		mi = new JMenuItem(A4n.moveTab2Right);
		mi.setIcon(null);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.ALT_MASK));
		m.add(mi);
		
		mi = new JMenuItem(A4n.moveTab2End);
		mi.setIcon(null);
		mi.setAccelerator(KeyStroke.getKeyStroke (
			KeyEvent.VK_RIGHT, KeyEvent.ALT_MASK | KeyEvent.SHIFT_MASK
		));
		m.add(mi);
		
		m.addSeparator();
		
		mi = new JMenuItem(A4n.closeChannelsTab);
		mi.setIcon(null);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.CTRL_MASK));
		m.add(mi);
		
		
		// Window
		m = new JMenu(i18n.getMenuLabel("window"));
		menuBar.add(m);
		
		mi = new JMenuItem(A4n.windowInstrumentsDb);
		mi.setIcon(null);
		m.add(mi);
		
		
		// Help
		m = new JMenu(i18n.getMenuLabel("help"));
		menuBar.add(m);
		
		mi = new JMenuItem(A4n.helpAbout);
		mi.setIcon(null);
		m.add(mi);
	}
	
	private class RecentScriptHandler implements ActionListener {
		private String script;
		
		RecentScriptHandler(String script) { this.script = script; }
		
		public void
		actionPerformed(ActionEvent e) {  runScript(script); }
	}
	
	private void
	handleEvents() {
		tabbedPane.addChangeListener(this);
	}
	
	private void
	showChannelsBar(boolean b) {
		channelsBar.setVisible(b);
		ClassicPrefs.setShowChannelsBar(b);
		
		validate();
		repaint();
	}
	
	private void
	showStatusbar(boolean b) {
		ClassicPrefs.setShowStatusbar(b);
		statusbar.setVisible(b);
	}
	
	protected boolean
	isLeftPaneVisible() { return cbmiLeftPaneVisible.isSelected(); }
	
	protected void
	setLeftPaneVisible(boolean b) {
		if(b != cbmiLeftPaneVisible.isSelected()) cbmiLeftPaneVisible.doClick(0);
	}
	
	protected boolean
	isLSConsoleVisible() { return cbmiLSConsoleShown.isSelected(); }
	
	protected void
	setLSConsoleVisible(boolean b) {
		if(b != cbmiLSConsoleShown.isSelected()) cbmiLSConsoleShown.doClick(0);
	}
	
	private void
	showLeftPane(boolean b) {
		ClassicPrefs.setShowLeftPane(b);
		
		mainPane.remove(hSplitPane);
		mainPane.remove(rightPane);
		
		if(b) {
			hSplitPane.setRightComponent(rightPane);
			mainPane.add(hSplitPane);
			if(ClassicPrefs.getSaveWindowProperties()) {
				int i = ClassicPrefs.getHSplitDividerLocation();
				hSplitPane.setDividerLocation(i);
			}
		} else {
			mainPane.add(rightPane);
		}
		
		validate();
		repaint();
	}
	
	private void
	showStandardBar(boolean b) {
		ClassicPrefs.setShowStandardBar(b);
		standardBar.setVisible(b);
		validate();
		repaint();
	}
	
	private void
	showBottomPane(boolean b) {
		if(!b) ClassicPrefs.setVSplitDividerLocation(vSplitPane.getDividerLocation());
		
		rightPane.remove(vSplitPane);
		rightPane.remove(channelsPane);
		
		if(b) {
			vSplitPane.setTopComponent(channelsPane);
			rightPane.add(vSplitPane);
			vSplitPane.setDividerLocation(ClassicPrefs.getVSplitDividerLocation());
		} else {
			rightPane.add(channelsPane);
		}
		
		validate();
		repaint();
	}
	
	protected void
	setLSConsolePopOut(boolean b) {
		if(b == lsConsolePopOut) return;
		
		lsConsolePopOut = b;
		
		if(isLSConsoleShown()) setLSConsolePopOut0(b);
	}
	
	/**
	 * Changes the pop-out state of the LS Console.
	 * Invoke this method only when LS Console is shown.
	 */
	private void
	setLSConsolePopOut0(boolean b) {
		if(b) {
			bottomPane.remove(lsConsolePane);
			showBottomPane(false);
			
			lsConsoleDlg = new LSConsoleDlg(this, lsConsolePane);
			lsConsoleDlg.setVisible(true);
		} else {
			if(lsConsoleDlg != null) lsConsoleDlg.setVisible(false);
			lsConsoleDlg = null;
			bottomPane.add(lsConsolePane);
			showBottomPane(true);
		}
	}
	
	protected boolean
	isLSConsolePopOut() { return lsConsolePopOut; }
	
	protected boolean
	isLSConsoleShown() { return cbmiLSConsoleShown.isSelected(); }
	
	protected void
	setLSConsoleShown(boolean b) { cbmiLSConsoleShown.setSelected(b); }
	
	protected LSConsoleModel
	getLSConsoleModel() { return lsConsolePane.getModel(); }
	
	/**
	 * Sets the text color of the LS Console.
	 * @param c The text color of the LS Console.
	 */
	protected void
	setLSConsoleTextColor(Color c) { lsConsolePane.setTextColor(c); }
	
	/**
	 * Sets the background color of the LS Console.
	 * @param c The background color of the LS Console.
	 */
	protected void
	setLSConsoleBackgroundColor(Color c) { lsConsolePane.setBackgroundColor(c); }
	
	/**
	 * Sets the notification messages' color of the LS Console.
	 * @param c The notification messages' color of the LS Console.
	 */
	protected void
	setLSConsoleNotifyColor(Color c) { lsConsolePane.setNotifyColor(c); }
	
	/**
	 * Sets the warning messages' color of the LS Console.
	 * @param c The warning messages' color of the LS Console.
	 */
	protected void
	setLSConsoleWarningColor(Color c) { lsConsolePane.setWarningColor(c); }
	
	/**
	 * Sets the error messages' color of the LS Console.
	 * @param c The error messages' color of the LS Console.
	 */
	protected void
	setLSConsoleErrorColor(Color c) { lsConsolePane.setErrorColor(c); }
	
	protected void
	showLSConsole(boolean b) {
		if(!b) {
			showBottomPane(false);
			if(lsConsoleDlg != null) lsConsoleDlg.setVisible(false);
			lsConsolePane.hideAutoCompleteWindow();
			return;
		}
		
		setLSConsolePopOut0(isLSConsolePopOut());
	}
	
	/**
	 * Adds the specified <code>JSChannelsPane</code> to the view.
	 * @param chnPane The <code>JSChannelsPane</code> to be added.
	 */
	public void
	addChannelsPane(JSChannelsPane chnPane) {
		insertChannelsPane(chnPane, getChannelsPaneCount());
	}
	
	public void
	insertChannelsPane(JSChannelsPane chnPane, int idx) {
		chnPane.addListSelectionListener(this);
		
		if(getChannelsPaneCount() == 1) {
			channelsPane.remove(getChannelsPane(0));
			channelsPane.add(tabbedPane);
			tabbedPane.addTab(getChannelsPane(0).getTitle(), getChannelsPane(0));
			A4n.closeChannelsTab.setEnabled(true);
			A4n.editTabTitle.setEnabled(true);
		}
		
		getChannelsPaneList().insertElementAt(chnPane, idx);
		tabbedPane.insertTab(chnPane.getTitle(), null, chnPane, null, idx);
		tabbedPane.setSelectedComponent(chnPane);
		miList.insertElementAt(new JMenuItem(new A4n.MoveChannelsTo(chnPane)), idx);
		
		updateTabsMenu();
	}
	
	/**
	 * Gets the <code>JSChannelsPane</code> that is currently shown.
	 * @return The currently shown <code>JSChannelsPane</code>.
	 */
	public JSChannelsPane
	getSelectedChannelsPane() {
		if(getChannelsPaneList().size() == 1) return getChannelsPane(0);
		return (JSChannelsPane)tabbedPane.getSelectedComponent();
	}
	
	/**
	 * Sets the <code>JSChannelsPane</code> to be selected.
	 * @param pane The <code>JSChannelsPane</code> to be shown.
	 */
	public void
	setSelectedChannelsPane(JSChannelsPane pane) {
		if(getChannelsPaneList().size() == 1) return;
		tabbedPane.setSelectedComponent(pane);
	}
	
	/**
	 * Removes the specified <code>JSChannelsPane</code> from the view.
	 * @param chnPane The <code>JSChannelsPane</code> to be removed.
	 * @return <code>true</code> if the specified code>JSChannelsPane</code>
	 * is actually removed from the view, <code>false</code> otherwise.
	 */
	public boolean
	removeChannelsPane(JSChannelsPane chnPane) {
		chnPane.removeListSelectionListener(this);
		
		tabbedPane.remove(chnPane);
		boolean b = super.removeChannelsPane(chnPane);
		for(int i = 0; i < miList.size(); i++) {
			A4n.MoveChannelsTo a = (A4n.MoveChannelsTo)miList.get(i).getAction();
			if(a.getChannelsPane().equals(chnPane)) {
				miList.remove(i);
				break;
			}
		}
		
		updateTabsMenu();
		
		if(getChannelsPaneCount() == 1) {
			A4n.closeChannelsTab.setEnabled(false);
			A4n.editTabTitle.setEnabled(false);
			tabbedPane.remove(getChannelsPane(0));
			channelsPane.remove(tabbedPane);
			channelsPane.add(getChannelsPane(0));
		}
		
		return b;
	}
	
	private void
	updateTabsMenu() {
		tabsMenu.removeAll();
		
		for(JMenuItem mi : miList) {
			A4n.MoveChannelsTo a = (A4n.MoveChannelsTo)mi.getAction();
			if(!a.getChannelsPane().equals(getSelectedChannelsPane())) tabsMenu.add(mi);
		}
			
	}
	
	public void
	updateTabTitle(JSChannelsPane chnPane) {
		tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(), chnPane.getTitle());
	}
	
	private void
	checkChannelSelection(JSChannelsPane pane) {
		if(!pane.hasSelectedChannel()) {
			A4n.duplicateChannels.putValue (
				Action.NAME, i18n.getMenuLabel("channels.duplicate")
			);
			A4n.duplicateChannels.setEnabled(false);
				
			A4n.removeChannels.putValue (
				Action.NAME, i18n.getMenuLabel("channels.RemoveChannel")
			);
			A4n.removeChannels.setEnabled(false);
			
			tabsMenu.setEnabled(false);
			
			A4n.moveChannelsOnTop.setEnabled(false);
			A4n.moveChannelsUp.setEnabled(false);
			A4n.moveChannelsDown.setEnabled(false);
			A4n.moveChannelsAtBottom.setEnabled(false);
			
			return;
		}
		
		A4n.duplicateChannels.setEnabled(true);
		A4n.removeChannels.setEnabled(true);
		
		if(getChannelsPaneCount() > 1) tabsMenu.setEnabled(true);
		
		if(pane.getSelectedChannelCount() > 1) {
			A4n.duplicateChannels.putValue (
				Action.NAME, i18n.getMenuLabel("channels.duplicateChannels")
			);
			A4n.removeChannels.putValue (
				Action.NAME, i18n.getMenuLabel("channels.RemoveChannels")
			);
		} else {
			A4n.duplicateChannels.putValue (
				Action.NAME, i18n.getMenuLabel("channels.duplicate")
			);
			A4n.removeChannels.putValue (
				Action.NAME, i18n.getMenuLabel("channels.RemoveChannel")
			);
		}
		
		A4n.moveChannelsOnTop.setEnabled(false);
		A4n.moveChannelsUp.setEnabled(true);
		A4n.moveChannelsDown.setEnabled(true);
		A4n.moveChannelsAtBottom.setEnabled(false);
		
		JSChannel[] chns = pane.getSelectedChannels();
		
		for(int i = 0; i < chns.length; i++) {
			if(pane.getChannel(i) != chns[i]) {
				A4n.moveChannelsOnTop.setEnabled(true);
				break;
			}
		}
		
		if(chns[0] == pane.getFirstChannel()) A4n.moveChannelsUp.setEnabled(false);
		
		if(chns[chns.length - 1] == pane.getLastChannel())
			A4n.moveChannelsDown.setEnabled(false);
		
		for(int i = chns.length - 1, j = pane.getChannelCount() - 1; i >= 0; i--, j--) {
			if(pane.getChannel(j) != chns[i]) {
				A4n.moveChannelsAtBottom.setEnabled(true);
				break;
			}
		}
	}
	
	private void
	checkTabSelection() {
		int si = tabbedPane.getSelectedIndex();
		
		if(si > 0) {
			A4n.moveTab2Beginning.setEnabled(true);
			A4n.moveTab2Left.setEnabled(true);
		} else {
			A4n.moveTab2Beginning.setEnabled(false);
			A4n.moveTab2Left.setEnabled(false);
		}
		
		if(si != -1 && si < tabbedPane.getTabCount() - 1) {
			A4n.moveTab2Right.setEnabled(true);
			A4n.moveTab2End.setEnabled(true);
		} else {
			A4n.moveTab2Right.setEnabled(false);
			A4n.moveTab2End.setEnabled(false);
		}
	}
	
	/*public JTabbedPane
	getTabbedPane() { return tabbedPane; }*/
	
	public JMenu
	getTabsMenu() { return tabsMenu; }
	
	public void
	stateChanged(ChangeEvent e) {
		updateTabsMenu();
		checkChannelSelection(getSelectedChannelsPane());
		checkTabSelection();
	}
	
	public void
	valueChanged(ListSelectionEvent e) {
		if(e.getValueIsAdjusting()) return;
		if(e.getSource() != getSelectedChannelsPane()) return;
		
		checkChannelSelection(getSelectedChannelsPane());
	}
	
	public void
	moveTab2Beginning() {
		int idx = tabbedPane.getSelectedIndex();
		if(idx < 1) {
			CC.getLogger().info("Can't move tab to beginning");
			return;
		}
		
		JSChannelsPane c = (JSChannelsPane)tabbedPane.getSelectedComponent();
		if(getChannelsPane(idx) != c)
			CC.getLogger().warning("Channels pane indices don't match");
		removeChannelsPane(c);
		insertChannelsPane(c, 0);
		tabbedPane.setSelectedComponent(c);
	}
	
	public void
	moveTab2Left() {
		int idx = tabbedPane.getSelectedIndex();
		if(idx < 1) {
			CC.getLogger().info("Can't move tab to left");
			return;
		}
		
		
		JSChannelsPane c = (JSChannelsPane)tabbedPane.getSelectedComponent();
		if(getChannelsPane(idx) != c)
			CC.getLogger().warning("Channels pane indices don't match");
		removeChannelsPane(c);
		insertChannelsPane(c, idx - 1);
		tabbedPane.setSelectedComponent(c);
	}
	
	public void
	moveTab2Right() {
		int idx = tabbedPane.getSelectedIndex();
		if(idx == -1 && idx >= tabbedPane.getTabCount()) {
			CC.getLogger().info("Can't move tab to right");
			return;
		}
		
		JSChannelsPane c = (JSChannelsPane)tabbedPane.getSelectedComponent();
		if(getChannelsPane(idx) != c)
			CC.getLogger().warning("Channels pane indices don't match");
		removeChannelsPane(c);
		insertChannelsPane(c, idx + 1);
		tabbedPane.setSelectedComponent(c);
	}
	
	public void
	moveTab2End() {
		int idx = tabbedPane.getSelectedIndex();
		if(idx == -1 && idx >= tabbedPane.getTabCount()) {
			CC.getLogger().info("Can't move tab to right");
			return;
		}
		
		JSChannelsPane c = (JSChannelsPane)tabbedPane.getSelectedComponent();
		if(getChannelsPane(idx) != c)
			CC.getLogger().warning("Channels pane indices don't match");
		removeChannelsPane(c);
		addChannelsPane(c);
		tabbedPane.setSelectedComponent(c);
	}
	
	protected void
	runScript() {
		JFileChooser fc = new JFileChooser(ClassicPrefs.getLastScriptLocation());
		fc.setFileFilter(new LscpFileFilter());
		int result = fc.showOpenDialog(this);
		if(result != JFileChooser.APPROVE_OPTION) return;
		
		String path = fc.getCurrentDirectory().getAbsolutePath();
		ClassicPrefs.setLastScriptLocation(path);
					
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
		
		if(!cbmiLSConsoleShown.isSelected()) cbmiLSConsoleShown.doClick(0);
		
		String s = script.getAbsolutePath();
		recentScripts.remove(s);
		recentScripts.insertElementAt(s, 0);
		
		while(recentScripts.size() > ClassicPrefs.getRecentScriptsSize()) {
			recentScripts.removeElementAt(recentScripts.size() - 1);
		}
		
		updateRecentScriptsMenu();
	}
	
	protected void
	clearRecentScripts() {
		recentScripts.removeAllElements();
		updateRecentScriptsMenu();
	}
	
	protected void
	updateRecentScriptsMenu() {
		while(recentScripts.size() > ClassicPrefs.getRecentScriptsSize()) {
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
	
	public void
	installJSamplerHome() {
		JSamplerHomeChooser chooser = new JSamplerHomeChooser(this);
		chooser.setVisible(true);
		if(chooser.isCancelled()) return;
		
		CC.changeJSamplerHome(chooser.getJSamplerHome());
	}
	
	public boolean
	getInstrumentsDbSupport() { return true; }
	
	public void
	showDetailedErrorMessage(Frame owner, String err, String details) {
		new DetailedErrorDlg(owner, i18n.getError("error"), err, details).setVisible(true);
	}
	
	public void
	showDetailedErrorMessage(Dialog owner, String err, String details) {
		new DetailedErrorDlg(owner, i18n.getError("error"), err, details).setVisible(true);
	}
}
