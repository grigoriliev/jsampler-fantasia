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
import java.awt.Dimension;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import javax.swing.plaf.ToolBarUI;

import net.sf.juife.InformationDialog;
import net.sf.juife.NavigationPage;
import net.sf.juife.NavigationPane;

import net.sf.juife.DefaultNavigationHistoryModel;
import net.sf.juife.Task;

import net.sf.juife.event.TaskEvent;
import net.sf.juife.event.TaskListener;

import org.jsampler.CC;
import org.jsampler.HF;
import org.jsampler.JSI18n;

import org.jsampler.task.InstrumentsDb;

import org.jsampler.view.DbDirectoryTreeNode;
import org.jsampler.view.InstrumentsDbTableModel;
import org.jsampler.view.InstrumentsDbTableView;
import org.jsampler.view.std.JSInstrumentsDbTable;
import org.jsampler.view.std.JSInstrumentsDbTree;

import org.linuxsampler.lscp.DbDirectoryInfo;
import org.linuxsampler.lscp.DbInstrumentInfo;
import org.linuxsampler.lscp.DbSearchQuery;

import static org.jsampler.view.classic.ClassicI18n.i18n;
import static org.jsampler.view.classic.ClassicPrefs.preferences;

/**
 *
 * @author Grigor Iliev
 */
public class InstrumentsDbFrame extends JFrame {
	private final JMenuBar menuBar = new JMenuBar();
	private final JSInstrumentsDbTree instrumentsDbTree;
	private final SidePane sidePane;
	private final JSplitPane splitPane;
	private final MainPane mainPane;
	
	private final GoUp goUp = new GoUp();
	private final GoBack goBack = new GoBack();
	private final GoForward goForward = new GoForward();
	
	private final NavigationHistoryModel navigationHistoryModel = new NavigationHistoryModel();
	
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
		
		getContentPane().add(new ToolbarBar(), BorderLayout.NORTH);
		
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
		
		instrumentsDbTree.addTreeSelectionListener(goUp);
		instrumentsDbTree.addTreeSelectionListener(navigationHistoryModel);
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
		
		mi = new JMenuItem(mainPane.getInstrumentsTable().renameAction);
		mi.setIcon(null);
		m.add(mi);
		
		mi = new JMenuItem(mainPane.getInstrumentsTable().changeDescriptionAction);
		mi.setIcon(null);
		m.add(mi);
		
		m = new JMenu(i18n.getMenuLabel("instrumentsdb.go"));
		menuBar.add(m);
		
		mi = new JMenuItem(goUp);
		mi.setIcon(null);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_UP, KeyEvent.ALT_MASK));
		m.add(mi);
		
		mi = new JMenuItem(goBack);
		mi.setIcon(null);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.ALT_MASK));
		m.add(mi);
		
		mi = new JMenuItem(goForward);
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
				if(!goUp.isEnabled()) return;
				goUp.actionPerformed(null);
			}
		});
	}
	
	/** Invoked when this window is about to close. */
	private void
	onWindowClose() {
		boolean b = (getExtendedState() & MAXIMIZED_BOTH) == MAXIMIZED_BOTH;
		ClassicPrefs.setWindowMaximized("InstrumentsDbFrame", b);
		if(b) return;
		
		java.awt.Point p = getLocation();
		Dimension d = getSize();
		StringBuffer sb = new StringBuffer();
		sb.append(p.x).append(',').append(p.y).append(',');
		sb.append(d.width).append(',').append(d.height);
		ClassicPrefs.setWindowSizeAndLocation("InstrumentsDbFrame", sb.toString());
		int i = splitPane.getDividerLocation();
		preferences().setIntProperty("InstrumentsDbFrame.dividerLocation", i);
		
		mainPane.getInstrumentsTable().saveColumnsVisibleState();
		mainPane.getInstrumentsTable().saveColumnWidths();
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
		String s = ClassicPrefs.getWindowSizeAndLocation("InstrumentsDbFrame");
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
			
			i = preferences().getIntProperty("InstrumentsDbFrame.dividerLocation");
			if(i != 0) splitPane.setDividerLocation(i);
			
		} catch(Exception x) {
			String msg = "Parsing of window size and location string failed";
			CC.getLogger().log(Level.INFO, msg, x);
			setDefaultSize();
		}
		
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
	
	private class GoUp extends AbstractAction implements TreeSelectionListener {
		GoUp() {
			super(i18n.getMenuLabel("instrumentsdb.go.up"));
			
			String s = i18n.getMenuLabel("instrumentsdb.go.up.tt");
			putValue(SHORT_DESCRIPTION, s);
			putValue(Action.SMALL_ICON, Res.iconGoUp22);
			setEnabled(false);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			DbDirectoryTreeNode node = instrumentsDbTree.getSelectedDirectoryNode();
			if(node == null) return;
			instrumentsDbTree.setSelectedDirectoryNode(node.getParent());
		}
		
		public void
		valueChanged(TreeSelectionEvent e) {
			DbDirectoryTreeNode n = instrumentsDbTree.getSelectedDirectoryNode();
			if(n == null) {
				setEnabled(false);
				return;
			}
			
			setEnabled(n.getParent() != null);
		}
	}
	
	private class GoBack extends AbstractAction {
		GoBack() {
			super(i18n.getMenuLabel("instrumentsdb.go.back"));
			
			String s = i18n.getMenuLabel("instrumentsdb.go.back.tt");
			putValue(SHORT_DESCRIPTION, s);
			putValue(Action.SMALL_ICON, Res.iconGoBack22);
			setEnabled(false);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			navigationHistoryModel.goBack();
		}
	}
	
	private class GoForward extends AbstractAction {
		GoForward() {
			super(i18n.getMenuLabel("instrumentsdb.go.forward"));
			
			String s = i18n.getMenuLabel("instrumentsdb.go.forward.tt");
			putValue(SHORT_DESCRIPTION, s);
			putValue(Action.SMALL_ICON, Res.iconGoForward22);
			setEnabled(false);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			navigationHistoryModel.goForward();
		}
	}
	
	class ToolbarBar extends JToolBar {
		private final ToggleButton btnFolders = new ToggleButton();
		private final ToggleButton btnFind = new ToggleButton();
		
		private final ToolbarButton btnGoUp = new ToolbarButton(goUp);
		private final ToolbarButton btnGoBack = new ToolbarButton(goBack);
		private final ToolbarButton btnGoForward = new ToolbarButton(goForward);
		private final ToolbarButton btnReload =
			new ToolbarButton(mainPane.getInstrumentsTable().reloadAction);
		
		private final ToolbarButton btnPreferences = new ToolbarButton();
		
		public ToolbarBar() {
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
	
	private class NavigationHistoryModel
		extends DefaultNavigationHistoryModel<DbDirectoryTreeNode>
		implements TreeSelectionListener, ActionListener {
		
		private boolean lock = false;
		
		NavigationHistoryModel() {
			addActionListener(this);
		}
		
		public DbDirectoryTreeNode
		goBack() {
			lock = true;
			DbDirectoryTreeNode node = selectDirectory(super.goBack());
			lock = false;
			return node;
		}
		
		public DbDirectoryTreeNode
		goForward() {
			lock = true;
			DbDirectoryTreeNode node = selectDirectory(super.goForward());
			lock = false;
			return node;
		}
		
		private DbDirectoryTreeNode
		selectDirectory(DbDirectoryTreeNode node) {
			if(node == null) return null;
			
			if(node == mainPane.getSearchResultsNode()) {
				mainPane.showSearchResultsNode();
				return node;
			}
			
			String path = node.getInfo().getDirectoryPath();
			if(CC.getInstrumentsDbTreeModel().getNodeByPath(path) != null) {
				getInstrumentsDbTree().setSelectedDirectory(path);
				return node;
			}
			
			removePage();
			fireActionPerformed();
			String s = i18n.getMessage("InstrumentsDbFrame.unknownDirectory!", path);
			HF.showErrorMessage(s, InstrumentsDbFrame.this);
			return node;
		}
		
		public void
		addPage(DbDirectoryTreeNode node) {
			if(lock) return;
			if(node == null) return;
			super.addPage(node);
		}
		
		public void
		valueChanged(TreeSelectionEvent e) {
			addPage(instrumentsDbTree.getSelectedDirectoryNode());
		}
		
		public void
		actionPerformed(ActionEvent e) {
			goBack.setEnabled(hasBack());
			goForward.setEnabled(hasForward());
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
			new JSInstrumentsDbTable(instrumentsDbTree);
		
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
			instrumentsTable.getRowSorter().toggleSortOrder(0);
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
			navigationHistoryModel.addPage(searchResultsNode);
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
	
	class PreferencesDlg extends InformationDialog implements ItemListener {
		private final JCheckBox checkShowSizeColumn =
			new JCheckBox(JSI18n.i18n.getLabel("InstrumentsDbTableModel.SIZE"));
		
		private final JCheckBox checkShowFormatFamilyColumn =
			new JCheckBox(JSI18n.i18n.getLabel("InstrumentsDbTableModel.FORMAT_FAMILY"));
		
		private final JCheckBox checkShowFormatVersionColumn =
			new JCheckBox(JSI18n.i18n.getLabel("InstrumentsDbTableModel.FORMAT_VERSION"));
		
		private final JCheckBox checkShowIsDrumColumn =
			new JCheckBox(JSI18n.i18n.getLabel("InstrumentsDbTableModel.IS_DRUM"));
		
		private final JCheckBox checkShowCreatedColumn =
			new JCheckBox(JSI18n.i18n.getLabel("InstrumentsDbTableModel.CREATED"));
		
		private final JCheckBox checkShowModifiedColumn =
			new JCheckBox(JSI18n.i18n.getLabel("InstrumentsDbTableModel.MODIFIED"));
		
		private final JCheckBox checkShowProductColumn =
			new JCheckBox(JSI18n.i18n.getLabel("InstrumentsDbTableModel.PRODUCT"));
		
		private final JCheckBox checkShowArtistsColumn =
			new JCheckBox(JSI18n.i18n.getLabel("InstrumentsDbTableModel.ARTISTS"));
		
		private final JCheckBox checkShowInstrumentFileColumn
			= new JCheckBox(JSI18n.i18n.getLabel("InstrumentsDbTableModel.INSTRUMENT_FILE"));
		
		private final JCheckBox checkShowInstrumentNrColumn =
			new JCheckBox(JSI18n.i18n.getLabel("InstrumentsDbTableModel.INSTRUMENT_NR"));
		
		private final JCheckBox checkShowKeywordsColumn =
			new JCheckBox(JSI18n.i18n.getLabel("InstrumentsDbTableModel.KEYWORDS"));
		
		PreferencesDlg() {
			super(InstrumentsDbFrame.this);
			InstrumentsDbTableModel m = mainPane.instrumentsTable.getModel();
			
			checkShowSizeColumn.setSelected(m.getShowSizeColumn());
			checkShowFormatFamilyColumn.setSelected(m.getShowFormatFamilyColumn());
			checkShowFormatVersionColumn.setSelected(m.getShowFormatVersionColumn());
			checkShowIsDrumColumn.setSelected(m.getShowIsDrumColumn());
			checkShowCreatedColumn.setSelected(m.getShowCreatedColumn());
			checkShowModifiedColumn.setSelected(m.getShowModifiedColumn());
			checkShowProductColumn.setSelected(m.getShowProductColumn());
			checkShowArtistsColumn.setSelected(m.getShowArtistsColumn());
			checkShowInstrumentFileColumn.setSelected(m.getShowInstrumentFileColumn());
			checkShowInstrumentNrColumn.setSelected(m.getShowInstrumentNrColumn());
			checkShowKeywordsColumn.setSelected(m.getShowKeywordsColumn());
			
			checkShowSizeColumn.addItemListener(this);
			checkShowFormatFamilyColumn.addItemListener(this);
			checkShowFormatVersionColumn.addItemListener(this);
			checkShowIsDrumColumn.addItemListener(this);
			checkShowCreatedColumn.addItemListener(this);
			checkShowModifiedColumn.addItemListener(this);
			checkShowProductColumn.addItemListener(this);
			checkShowArtistsColumn.addItemListener(this);
			checkShowInstrumentFileColumn.addItemListener(this);
			checkShowInstrumentNrColumn.addItemListener(this);
			checkShowKeywordsColumn.addItemListener(this);
			
			JPanel p = new JPanel();
			p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
			p.add(checkShowSizeColumn);
			p.add(checkShowFormatFamilyColumn);
			p.add(checkShowFormatVersionColumn);
			p.add(checkShowIsDrumColumn);
			p.add(checkShowCreatedColumn);
			p.add(checkShowModifiedColumn);
			p.add(checkShowProductColumn);
			p.add(checkShowArtistsColumn);
			p.add(checkShowInstrumentFileColumn);
			p.add(checkShowInstrumentNrColumn);
			p.add(checkShowKeywordsColumn);
			String s = i18n.getLabel("InstrumentsDbFrame.columns");
			p.setBorder(BorderFactory.createTitledBorder(s));
		
			setMainPane(p);
		}
		
		public void
		itemStateChanged(ItemEvent e) {
			mainPane.getInstrumentsTable().saveColumnWidths();
			
			InstrumentsDbTableModel m = mainPane.instrumentsTable.getModel();
			
			Object source = e.getItemSelectable();
			if(source == checkShowSizeColumn) {
				m.setShowSizeColumn(checkShowSizeColumn.isSelected());
			} else if(source == checkShowFormatFamilyColumn) {
				boolean b = checkShowFormatFamilyColumn.isSelected();
				m.setShowFormatFamilyColumn(b);
			} else if(source == checkShowFormatVersionColumn) {
				boolean b = checkShowFormatVersionColumn.isSelected();
				m.setShowFormatVersionColumn(b);
			} else if(source == checkShowIsDrumColumn) {
				m.setShowIsDrumColumn(checkShowIsDrumColumn.isSelected());
			} else if(source == checkShowCreatedColumn) {
				m.setShowCreatedColumn(checkShowCreatedColumn.isSelected());
			} else if(source == checkShowModifiedColumn) {
				m.setShowModifiedColumn(checkShowModifiedColumn.isSelected());
			} else if(source == checkShowProductColumn) {
				m.setShowProductColumn(checkShowProductColumn.isSelected());
			} else if(source == checkShowArtistsColumn) {
				m.setShowArtistsColumn(checkShowArtistsColumn.isSelected());
			} else if(source == checkShowInstrumentFileColumn) {
				boolean b = checkShowInstrumentFileColumn.isSelected();
				m.setShowInstrumentFileColumn(b);
			} else if(source == checkShowInstrumentNrColumn) {
				boolean b = checkShowInstrumentNrColumn.isSelected();
				m.setShowInstrumentNrColumn(b);
			} else if(source == checkShowKeywordsColumn) {
				m.setShowKeywordsColumn(checkShowKeywordsColumn.isSelected());
			}
			
			mainPane.getInstrumentsTable().loadColumnWidths();
			mainPane.getInstrumentsTable().getRowSorter().toggleSortOrder(0);
		}
	}
}
