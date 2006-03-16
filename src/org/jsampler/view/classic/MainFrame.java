/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005 Grigor Kirilov Iliev
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
import java.awt.Dimension;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import java.util.logging.Level;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
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
import org.jsampler.Prefs;

import org.jsampler.view.JSChannel;
import org.jsampler.view.JSChannelsPane;

import static org.jsampler.view.classic.ClassicI18n.i18n;
import static org.jsampler.view.classic.LeftPane.getLeftPane;


/**
 *
 * @author Grigor Iliev
 */
public class
MainFrame extends org.jsampler.view.JSMainFrame implements ChangeListener, ListSelectionListener {
	public static ImageIcon applicationIcon = null;
	
	static {
		String s = "org/jsampler/view/classic/res/icons/app-icon.png";
		java.net.URL url = ClassLoader.getSystemClassLoader().getResource(s);
		if(url != null) applicationIcon = new ImageIcon(url);
	}
	
	private final Toolbar toolbar = new Toolbar();
	private final Statusbar statusbar = new Statusbar();
	private final JMenuBar menuBar = new JMenuBar();
	
	private final JSplitPane splitPane;
	
	private final JPanel mainPane = new JPanel();
	private final JPanel channelsPane = new JPanel(new BorderLayout());
	
	private final JTabbedPane tabbedPane =
		new JTabbedPane(JTabbedPane.BOTTOM, JTabbedPane.SCROLL_TAB_LAYOUT);
	private final JMenu tabsMenu = new JMenu(i18n.getMenuLabel("channels.MoveToTab"));
	private final Vector<JMenuItem> miList = new Vector<JMenuItem>();
	
	/** Creates a new instance of JSMainFrame */
	public
	MainFrame() {
		setTitle(i18n.getLabel("MainFrame.title"));
		
		getContentPane().add(toolbar, BorderLayout.NORTH);
		getContentPane().add(mainPane);
		
		mainPane.setLayout(new BorderLayout());
		mainPane.add(statusbar, BorderLayout.SOUTH);
		
		ChannelsPane p = new ChannelsPane("Untitled");
		p.addListSelectionListener(this);
		getChannelsPaneList().add(p);
		miList.add(new JMenuItem(new A4n.MoveChannelsTo(p)));
		
		channelsPane.add(getChannelsPane(0));
		
		splitPane = new JSplitPane (
			JSplitPane.HORIZONTAL_SPLIT,
			true,	// continuousLayout 
			getLeftPane(),
			channelsPane
		);
		
		splitPane.setOneTouchExpandable(true);
		
		mainPane.add(splitPane);
		
		if(applicationIcon != null) setIconImage(applicationIcon.getImage());
		
		initMainFrame();
		pack();
		
		if(Prefs.getSaveWindowProperties()) setSavedSize();
		else setDefaultSize();
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
		String s = Prefs.getWindowSizeAndLocation();
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
		
		if(Prefs.getWindowMaximized())
			setExtendedState(getExtendedState() | MAXIMIZED_BOTH);
	}
	
	private void
	addMenu() {
		JMenu m;
		JMenu submenu;
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
		
		final JCheckBoxMenuItem cbmi =
			new JCheckBoxMenuItem(i18n.getMenuLabel("view.toolbar"));
		
		m.add(cbmi);
		cbmi.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { showToolbar(cbmi.getState()); }
		});
		
		boolean b = ClassicPrefs.shouldShowToolbar();
		cbmi.setSelected(b);
		showToolbar(b);
		
		final JCheckBoxMenuItem cbmi1 =
			new JCheckBoxMenuItem(i18n.getMenuLabel("view.leftPane"));
		
		m.add(cbmi1);
		cbmi1.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { showLeftPane(cbmi1.getState()); }
		});
		
		b = ClassicPrefs.shouldShowLeftPane();
		cbmi1.setSelected(b);
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
		
		
		// Help
		m = new JMenu(i18n.getMenuLabel("help"));
		menuBar.add(m);
		
		mi = new JMenuItem(A4n.helpAbout);
		mi.setIcon(null);
		m.add(mi);
	}
	
	private void
	handleEvents() {
		tabbedPane.addChangeListener(this);
	}
	
	private void
	showToolbar(boolean b) {
		if(b) getContentPane().add(toolbar, BorderLayout.NORTH);
		else getContentPane().remove(toolbar);
		
		ClassicPrefs.setShowToolbar(b);
		
		validate();
		repaint();
	}
	
	private void
	showStatusbar(boolean b) {
		ClassicPrefs.setShowStatusbar(b);
		statusbar.setVisible(b);
	}
	
	private void
	showLeftPane(boolean b) {
		ClassicPrefs.setShowLeftPane(b);
		
		mainPane.remove(splitPane);
		mainPane.remove(channelsPane);
		
		if(b) {
			splitPane.setRightComponent(channelsPane);
			mainPane.add(splitPane);
		} else {
			mainPane.add(channelsPane);
		}
		
		validate();
		repaint();
	}
	
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
	
	public JSChannelsPane
	getSelectedChannelsPane() {
		if(getChannelsPaneList().size() == 1) return getChannelsPane(0);
		return (JSChannelsPane)tabbedPane.getSelectedComponent();
	}
	
	public void
	setSelectedChannelsPane(JSChannelsPane pane) {
		if(getChannelsPaneList().size() == 1) return;
		tabbedPane.setSelectedComponent(pane);
	}
	
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
}
