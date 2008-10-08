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

package org.jsampler.view.classic;

import java.awt.BorderLayout;
import java.awt.Dimension;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sf.juife.NavigationPage;
import net.sf.juife.NavigationPane;

import org.jsampler.CC;
import org.jsampler.HF;

import org.jsampler.task.InstrumentsDb;

import org.jsampler.view.DbDirectoryTreeNode;

import org.jsampler.view.std.JSInstrumentsDbColumnPreferencesDlg;
import org.jsampler.view.std.JSInstrumentsDbTable;
import org.jsampler.view.std.JSInstrumentsDbTree;
import org.jsampler.view.std.JSLostFilesDlg;
import org.jsampler.view.std.StdUtils;

import org.linuxsampler.lscp.DbDirectoryInfo;
import org.linuxsampler.lscp.DbInstrumentInfo;

import static org.jsampler.view.classic.ClassicI18n.i18n;
import static org.jsampler.view.classic.ClassicPrefs.preferences;

/**
 *
 * @author Grigor Iliev
 */
public class InstrumentsDbFrame extends JFrame {
	private final ToolBar toolbar;
	private final JMenuBar menuBar = new JMenuBar();
	private final JSInstrumentsDbTree instrumentsDbTree;
	private final SidePane sidePane;
	private final JSplitPane splitPane;
	private final MainPane mainPane;
	
	private JMenu loadInstrumentMenu;
	private JMenu addToMidiMapMenu;
	private JMenu addToOrchestraMenu;
	
	/**
	 * Creates a new instance of <code>InstrumentsDbFrame</code>
	 */
	public
	InstrumentsDbFrame() {
		setTitle(i18n.getLabel("InstrumentsDbFrame.title"));
		if(Res.appIcon != null) setIconImage(Res.appIcon.getImage());
		
		instrumentsDbTree = new JSInstrumentsDbTree(CC.getInstrumentsDbTreeModel());
		
		sidePane = new SidePane();
		mainPane = new MainPane();
		
		instrumentsDbTree.setSelectedDirectory("/");
		
		toolbar = new ToolBar();
		getContentPane().add(toolbar, BorderLayout.NORTH);
		
		splitPane = new JSplitPane (
			JSplitPane.HORIZONTAL_SPLIT,
			true,	// continuousLayout 
			sidePane,
			mainPane
		);
		
		splitPane.setDividerSize(3);
		splitPane.setDividerLocation(200);
		
		addMenu();
		
		pack();
		setSavedSize();
		
		getContentPane().add(splitPane);
		mainPane.getInstrumentsTable().loadColumnWidths();
		
		addWindowListener(new WindowAdapter() {
			public void
			windowClosing(WindowEvent we) { onWindowClose(); }
		});
		
		installKeyboardListeners();
	}
	
	private void
	addMenu() {
		JMenu m;
		JMenuItem mi;
		
		setJMenuBar(menuBar);
		
		// Actions
		m = new JMenu(i18n.getMenuLabel("instrumentsdb.actions"));
		menuBar.add(m);
		
		mi = new JMenuItem(mainPane.getInstrumentsTable().createDirectoryAction);
		mi.setIcon(null);
		m.add(mi);
		
		mi = new JMenuItem(mainPane.getInstrumentsTable().deleteAction);
		mi.setIcon(null);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		m.add(mi);
		
		JMenu addInstrumentsMenu =
			new JMenu(i18n.getMenuLabel("instrumentsdb.actions.addInstruments"));
		m.add(addInstrumentsMenu);
		
		mi = new JMenuItem(mainPane.getInstrumentsTable().addInstrumentsFromFileAction);
		mi.setIcon(null);
		addInstrumentsMenu.add(mi);
		
		mi = new JMenuItem(mainPane.getInstrumentsTable().addInstrumentsFromDirAction);
		mi.setIcon(null);
		addInstrumentsMenu.add(mi);
		
		m.addSeparator();
		
		mi = new JMenuItem(i18n.getMenuLabel("instrumentsdb.actions.format"));
		m.add(mi);
		mi.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				String s = i18n.getMessage("InstrumentsDbFrame.formatDatabase?");
				if(!HF.showYesNoDialog(InstrumentsDbFrame.this, s)) return;
				CC.getTaskQueue().add(new InstrumentsDb.Format());
			}
		});
		
		mi = new JMenuItem(i18n.getMenuLabel("instrumentsdb.actions.checkForLostFiles"));
		m.add(mi);
		mi.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				new JSLostFilesDlg(InstrumentsDbFrame.this).setVisible(true);
			}
		});
		
		m.addSeparator();
		
		loadInstrumentMenu =
			new JMenu(i18n.getMenuLabel("instrumentsdb.actions.loadInstrument"));
		m.add(loadInstrumentMenu);
		mainPane.getInstrumentsTable().registerLoadInstrumentMenus(loadInstrumentMenu);
		
		addToMidiMapMenu =
			new JMenu(i18n.getMenuLabel("instrumentsdb.actions.addToMidiMap"));
		m.add(addToMidiMapMenu);
		mainPane.getInstrumentsTable().registerAddToMidiMapMenu(addToMidiMapMenu);
		
		addToOrchestraMenu =
			new JMenu(i18n.getMenuLabel("instrumentsdb.actions.addToOrchestra"));
		m.add(addToOrchestraMenu);
		mainPane.getInstrumentsTable().registerAddToOrchestraMenu(addToOrchestraMenu);
		
		m.addSeparator();
		
		mi = new JMenuItem(mainPane.getInstrumentsTable().reloadAction);
		mi.setIcon(null);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
		m.add(mi);
		
		m.addSeparator();
		
		mi = new JMenuItem(mainPane.getInstrumentsTable().propertiesAction);
		mi.setIcon(null);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.ALT_MASK));
		m.add(mi);
		
		m = new JMenu(i18n.getMenuLabel("instrumentsdb.edit"));
		menuBar.add(m);
		
		mi = new JMenuItem(mainPane.getInstrumentsTable().cutAction);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_MASK));
		mi.setIcon(null);
		m.add(mi);
		
		mi = new JMenuItem(mainPane.getInstrumentsTable().copyAction);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK));
		mi.setIcon(null);
		m.add(mi);
		
		mi = new JMenuItem(mainPane.getInstrumentsTable().pasteAction);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_MASK));
		mi.setIcon(null);
		m.add(mi);
		
		m.addSeparator();
		
		mi = new JMenuItem(i18n.getMenuLabel("instrumentsdb.edit.find"));
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_MASK));
		m.add(mi);
		mi.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				if(toolbar.btnFind.isSelected()) return;
				
				String path = instrumentsDbTree.getSelectedDirectoryPath();
				if(path != null) sidePane.searchPage.setSearchPath(path);
				toolbar.btnFind.doClick(0);
			}
		});
		
		m.addSeparator();
		
		mi = new JMenuItem(mainPane.getInstrumentsTable().renameAction);
		mi.setIcon(null);
		m.add(mi);
		
		mi = new JMenuItem(mainPane.getInstrumentsTable().changeDescriptionAction);
		mi.setIcon(null);
		m.add(mi);
		
		m = new JMenu(i18n.getMenuLabel("instrumentsdb.go"));
		menuBar.add(m);
		
		instrumentsDbTree.actionGoUp.putValue(Action.SMALL_ICON, Res.iconGoUp22);
		mi = new JMenuItem(instrumentsDbTree.actionGoUp);
		mi.setIcon(null);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_UP, KeyEvent.ALT_MASK));
		m.add(mi);
		
		instrumentsDbTree.actionGoBack.putValue(Action.SMALL_ICON, Res.iconGoBack22);
		mi = new JMenuItem(instrumentsDbTree.actionGoBack);
		mi.setIcon(null);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.ALT_MASK));
		m.add(mi);
		
		instrumentsDbTree.actionGoForward.putValue(Action.SMALL_ICON, Res.iconGoForward22);
		mi = new JMenuItem(instrumentsDbTree.actionGoForward);
		mi.setIcon(null);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.ALT_MASK));
		m.add(mi);
	}
	
	private void
	installKeyboardListeners() {
		getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put (
			KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0),
			"goUp"
		);
		
		getRootPane().getActionMap().put ("goUp", new AbstractAction() {
			public void
			actionPerformed(ActionEvent e) {
				if(!instrumentsDbTree.actionGoUp.isEnabled()) return;
				instrumentsDbTree.actionGoUp.actionPerformed(null);
			}
		});
	}
	
	/** Invoked when this window is about to close. */
	private void
	onWindowClose() {
		boolean b = (getExtendedState() & MAXIMIZED_BOTH) == MAXIMIZED_BOTH;
		ClassicPrefs.setWindowMaximized("InstrumentsDbFrame", b);
		if(b) return;
		
		StdUtils.saveWindowBounds("InstrumentsDbFrame", getBounds());
		int i = splitPane.getDividerLocation();
		preferences().setIntProperty("InstrumentsDbFrame.dividerLocation", i);
		
		mainPane.getInstrumentsTable().saveColumnsVisibleState();
		mainPane.getInstrumentsTable().saveColumnWidths();
	}
	
	@Override
	public void
	setVisible(boolean b) {
		if(b == isVisible()) return;
		
		super.setVisible(b);
		
		if(ClassicPrefs.getWindowMaximized("InstrumentsDbFrame")) {
			setExtendedState(getExtendedState() | MAXIMIZED_BOTH);
		}
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
		Rectangle r = StdUtils.getWindowBounds("InstrumentsDbFrame");
		if(r == null) {
			setDefaultSize();
			return;
		}
		
		setBounds(r);
		
		if(ClassicPrefs.getWindowMaximized("InstrumentsDbFrame"))
			setExtendedState(getExtendedState() | MAXIMIZED_BOTH);
	}
	
	private void
	addSidePane() {
		getContentPane().remove(mainPane);
		splitPane.setRightComponent(mainPane);
		getContentPane().add(splitPane);
		int i = preferences().getIntProperty("InstrumentsDbFrame.dividerLocation");
		if(i != 0) splitPane.setDividerLocation(i);
	}
	
	private void
	removeSidePane() {
		int i = splitPane.getDividerLocation();
		preferences().setIntProperty("InstrumentsDbFrame.dividerLocation", i);
		getContentPane().remove(splitPane);
		getContentPane().add(mainPane);
	}
	
	class ToolBar extends JToolBar {
		private final ToggleButton btnFolders = new ToggleButton();
		private final ToggleButton btnFind = new ToggleButton();
		
		private final ToolbarButton btnGoUp = new ToolbarButton(instrumentsDbTree.actionGoUp);
		private final ToolbarButton btnGoBack = new ToolbarButton(instrumentsDbTree.actionGoBack);
		private final ToolbarButton btnGoForward = new ToolbarButton(instrumentsDbTree.actionGoForward);
		private final ToolbarButton btnReload = new ToolbarButton(mainPane.getInstrumentsTable().reloadAction);
		
		private final ToolbarButton btnPreferences = new ToolbarButton();
		
		public ToolBar() {
			super(i18n.getLabel("InstrumentsDbFrame.ToolbarBar.name"));
			setFloatable(false);
			
			btnFolders.setIcon(Res.iconFolderOpen22);
			btnFolders.doClick(0);
			btnFind.setIcon(Res.iconFind22);
			btnPreferences.setIcon(Res.iconPreferences22);
			
			add(btnFolders);
			add(btnFind);
			addSeparator();
			add(btnGoBack);
			add(btnGoForward);
			add(btnGoUp);
			add(btnReload);
			addSeparator();
			add(btnPreferences);
			
			
			btnFolders.addActionListener(new ActionListener() {
				public void
				actionPerformed(ActionEvent e) {
					if(!btnFolders.isSelected() && !btnFind.isSelected()) {
						removeSidePane();
					}
					
					if(!btnFolders.isSelected()) return;
					
					if(btnFind.isSelected()) btnFind.doClick(0);
					else addSidePane();
					
					sidePane.showFoldersPage();
				}
			});
			
			btnFind.addActionListener(new ActionListener() {
				public void
				actionPerformed(ActionEvent e) {
					if(!btnFolders.isSelected() && !btnFind.isSelected()) {
						removeSidePane();
					}
					
					if(!btnFind.isSelected()) return;
					
					if(btnFolders.isSelected()) btnFolders.doClick(0);
					else addSidePane();
					
					sidePane.showSearchPage();
				}
			});
			
			btnPreferences.addActionListener(new ActionListener() {
				public void
				actionPerformed(ActionEvent e) {
					new PreferencesDlg().setVisible(true);
				}
			});
		}
	}
	
	class ToggleButton extends JToggleButton {
		ToggleButton() {
			setBorderPainted(false);
			setContentAreaFilled(false);
			setFocusPainted(false);
			
			addActionListener(new ActionListener() {
				public void
				actionPerformed(ActionEvent e) {
					setBorderPainted(isSelected());
				}
			});
		}
	}
	
	public JSInstrumentsDbTree
	getInstrumentsDbTree() { return instrumentsDbTree; }
	
	public void
	setSearchResults(DbDirectoryInfo[] directories) {
		setSearchResults(directories, null);
	}
	
	public void
	setSearchResults(DbInstrumentInfo[] instruments) {
		setSearchResults(null, instruments);
	}
	
	public void
	setSearchResults(DbDirectoryInfo[] directories, DbInstrumentInfo[] instruments) {
		DbDirectoryTreeNode node = mainPane.getSearchResultsNode();
		node.removeAllDirectories();
		node.removeAllInstruments();
		
		if(instruments != null) {
			for(DbInstrumentInfo i : instruments) i.setShowAbsolutePath(true);
			node.addInstruments(instruments);
		}
		
		if(directories != null) {
			DbDirectoryTreeNode[] nodeS = new DbDirectoryTreeNode[directories.length];
			for(int i = 0; i < directories.length; i++) {
				DbDirectoryInfo d = directories[i];
				d.setShowAbsolutePath(true);
				nodeS[i] = new DbDirectoryTreeNode(d);
			
			
			}
			node.addDirectories(nodeS);
		}
		
		mainPane.showSearchResultsNode();
	}
	
	class MainPane extends JPanel {
		private final JSInstrumentsDbTable instrumentsTable =
			new JSInstrumentsDbTable(instrumentsDbTree, "InstrumentsDbFrame.");
		
		private final DbDirectoryTreeNode searchResultsNode = new DbDirectoryTreeNode(null);
		
		MainPane() {
			setLayout(new BorderLayout());
			JScrollPane sp = new JScrollPane(instrumentsTable);
			add(sp);
			instrumentsTable.reloadAction.putValue(Action.SMALL_ICON, Res.iconReload22);
			
			instrumentsTable.createDirectoryAction.putValue (
				Action.SMALL_ICON, Res.iconNew16
			);
			
			instrumentsTable.getParent().setBackground(instrumentsTable.getBackground());
			
			searchResultsNode.setDetached(true);
		}
		
		public JSInstrumentsDbTable
		getInstrumentsTable() { return instrumentsTable; }
		
		public DbDirectoryTreeNode
		getSearchResultsNode() { return searchResultsNode; }
		
		public void
		showSearchResultsNode() {
			instrumentsDbTree.clearSelection();
			instrumentsTable.setParentDirectoryNode(searchResultsNode);
		}
	}
	
	
	class SidePane extends NavigationPane {
		private final NavigationPage foldersPage = new NavigationPage();
		private final DbSearchPage searchPage = new DbSearchPage(InstrumentsDbFrame.this);
		
		SidePane() {
			setTitlebarVisiblie(false);
			
			foldersPage.setTitle(i18n.getLabel("InstrumentsDbFrame.folders"));
			foldersPage.setLayout(new BorderLayout());
			foldersPage.add(new JScrollPane(instrumentsDbTree));
			
			NavigationPage[] pages = { foldersPage, searchPage };
			setPages(pages);
			showFoldersPage();
		}
		
		/** Shows the folders page in the left pane. */
		public void
		showFoldersPage() { getModel().addPage(foldersPage); }
		
		/** Shows the search page in the left pane. */
		public void
		showSearchPage() { getModel().addPage(searchPage); }
	}
	
	private final EventHandler eventHandler = new EventHandler();
	
	private EventHandler
	getHandler() { return eventHandler; }
	
	private class EventHandler implements ListSelectionListener {
		public void
		valueChanged(ListSelectionEvent e) {
			
		}
	}
	
	class PreferencesDlg extends JSInstrumentsDbColumnPreferencesDlg {
		PreferencesDlg() {
			super(InstrumentsDbFrame.this, mainPane.instrumentsTable);
		}
	}
}
