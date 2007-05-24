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

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.KeyStroke;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import net.sf.juife.InformationDialog;
import net.sf.juife.JuifeUtils;
import net.sf.juife.Task;

import net.sf.juife.event.TaskEvent;
import net.sf.juife.event.TaskListener;

import org.jsampler.CC;
import org.jsampler.HF;
import org.jsampler.Instrument;
import org.jsampler.MidiInstrumentMap;
import org.jsampler.OrchestraModel;
import org.jsampler.SamplerChannelModel;
import org.jsampler.SamplerModel;

import org.jsampler.event.ListEvent;
import org.jsampler.event.ListListener;
import org.jsampler.event.SamplerChannelListEvent;
import org.jsampler.event.SamplerChannelListListener;

import org.jsampler.task.InstrumentsDb;

import org.jsampler.view.DbClipboard;
import org.jsampler.view.DbDirectoryTreeNode;
import org.jsampler.view.InstrumentsDbTableModel;

import org.linuxsampler.lscp.DbDirectoryInfo;
import org.linuxsampler.lscp.DbInstrumentInfo;
import org.linuxsampler.lscp.MidiInstrumentInfo;

import static org.jsampler.view.InstrumentsDbTableModel.ColumnType;
import static org.jsampler.view.classic.ClassicI18n.i18n;

/**
 *
 * @author Grigor Iliev
 */
public class InstrumentsDbTable extends org.jsampler.view.AbstractInstrumentsDbTable {
	private InstrumentsDbTree instrumentsDbTree;
	private InstrumentsDbCellRenderer cellRenderer = new InstrumentsDbCellRenderer();
	
	protected final Action reloadAction = new ReloadAction();
	protected final Action createDirectoryAction = new CreateDirectoryAction();
	protected final Action deleteAction = new DeleteAction();
	protected final AddInstrumentsFromFileAction addInstrumentsFromFileAction =
		new AddInstrumentsFromFileAction();
	protected final AddInstrumentsFromDirAction addInstrumentsFromDirAction =
		new AddInstrumentsFromDirAction();
	protected final Action propertiesAction = new PropertiesAction();
	protected final Action renameAction = new RenameAction();
	protected final Action changeDescriptionAction = new ChangeDescriptionAction();
	protected final Action cutAction = new CutAction();
	protected final Action copyAction = new CopyAction();
	protected final Action pasteAction;
	
	/** Creates a new instance of <code>InstrumentsDbTable</code> */
	public InstrumentsDbTable(InstrumentsDbTree tree) {
		instrumentsDbTree = tree;
		String s;
		InstrumentsDbTableModel m = getModel();
		
		s = "DbInstrumentsTable.ShowSizeColumn";
		m.setShowSizeColumn(ClassicPrefs.getBoolProperty(s));
		s = "DbInstrumentsTable.ShowFormatFamilyColumn";
		m.setShowFormatFamilyColumn(ClassicPrefs.getBoolProperty(s));
		s = "DbInstrumentsTable.ShowFormatVersionColumn";
		m.setShowFormatVersionColumn(ClassicPrefs.getBoolProperty(s));
		s = "DbInstrumentsTable.ShowIsDrumColumn";
		m.setShowIsDrumColumn(ClassicPrefs.getBoolProperty(s));
		s = "DbInstrumentsTable.ShowCreatedColumn";
		m.setShowCreatedColumn(ClassicPrefs.getBoolProperty(s));
		s = "DbInstrumentsTable.ShowModifiedColumn";
		m.setShowModifiedColumn(ClassicPrefs.getBoolProperty(s));
		s = "DbInstrumentsTable.ShowProductColumn";
		m.setShowProductColumn(ClassicPrefs.getBoolProperty(s));
		s = "DbInstrumentsTable.ShowArtistsColumn";
		m.setShowArtistsColumn(ClassicPrefs.getBoolProperty(s));
		s = "DbInstrumentsTable.ShowInstrumentFileColumn";
		m.setShowInstrumentFileColumn(ClassicPrefs.getBoolProperty(s));
		s = "DbInstrumentsTable.ShowInstrumentNrColumn";
		m.setShowInstrumentNrColumn(ClassicPrefs.getBoolProperty(s));
		s = "DbInstrumentsTable.ShowKeywordsColumn";
		m.setShowKeywordsColumn(ClassicPrefs.getBoolProperty(s));
		
		/*for(int i = 0; i < getColumnModel().getColumnCount(); i++) {
			getColumnModel().getColumn(i).setMinWidth(50);
		}*/
		
		setShowGrid(false);
		getColumnModel().setColumnMargin(0);
		getTableHeader().setReorderingAllowed(false);
		
		setFillsViewportHeight(true);
		
		addMouseListener(new MouseAdapter() {
			public void
			mouseClicked(MouseEvent e) {
				if(e.getButton() != e.BUTTON1) return;
				int r = rowAtPoint(e.getPoint());
				if(r == -1) {
					clearSelection();
					return;
				}
				
				if(e.getClickCount() < 2) return;
				
				DbDirectoryTreeNode node = getSelectedDirectoryNode();
				if(node == null) return;
				if(!node.isDetached()) {
					instrumentsDbTree.setSelectedDirectoryNode(node);
				} else {
					String s = node.getInfo().getDirectoryPath();
					instrumentsDbTree.setSelectedDirectory(s);
				}
			}
		});
		
		addMouseListener(new MouseAdapter() {
			public void
			mousePressed(MouseEvent e) {
				int r = rowAtPoint(e.getPoint());
				
				if(e.getButton() != e.BUTTON1 && e.getButton() != e.BUTTON3) return;
				if(r == -1) {
					clearSelection();
					return;
				}
				
				if(e.getButton() != e.BUTTON3) return;
				if(getSelectionModel().isSelectedIndex(r)) {
					getSelectionModel().addSelectionInterval(r, r);
				} else {
					getSelectionModel().setSelectionInterval(r, r);
				}
			}
		});
		
		getSelectionModel().addListSelectionListener(getHandler());
		
		instrumentsDbTree.addTreeSelectionListener(getHandler());
		
		PasteAction pasteAction = new PasteAction();
		instrumentsDbTree.addTreeSelectionListener(pasteAction);
		this.pasteAction = pasteAction;
		
		ContextMenu contextMenu = new ContextMenu();
		addMouseListener(contextMenu);
		
		CC.getOrchestras().addOrchestraListListener(getHandler());
		CC.getSamplerModel().addSamplerChannelListListener(getHandler());
		
		ListListener<MidiInstrumentMap> l = new ListListener<MidiInstrumentMap>() {
			public void
			entryAdded(ListEvent<MidiInstrumentMap> e) { updateAddToMidiMapMenus(); }
			
			public void
			entryRemoved(ListEvent<MidiInstrumentMap> e) { updateAddToMidiMapMenus(); }
		};
		
		CC.getSamplerModel().addMidiInstrumentMapListListener(l);
		
		installKeyboardListeners();
	}
	
	public TableCellRenderer
	getCellRenderer(int row, int column) {
		return cellRenderer;
	}

	private void
	installKeyboardListeners() {
		AbstractAction a = new AbstractAction() {
			public void
			actionPerformed(ActionEvent e) { }
		};
		a.setEnabled(false);
		getActionMap().put("none", a);
		
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put (
			KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_MASK),
			"none"
		);
		
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put (
			KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK),
			"none"
		);
		
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put (
			KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_MASK),
			"none"
		);
		
		getInputMap(JComponent.WHEN_FOCUSED).put (
			KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_MASK),
			"none"
		);
		
		getInputMap(JComponent.WHEN_FOCUSED).put (
			KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK),
			"none"
		);
		
		getInputMap(JComponent.WHEN_FOCUSED).put (
			KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_MASK),
			"none"
		);
		
		getInputMap().put (
			KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
			"OpenDirectory"
		);
		
		getActionMap().put ("OpenDirectory", new AbstractAction() {
			public void
			actionPerformed(ActionEvent e) {
				DbDirectoryTreeNode node = getSelectedDirectoryNode();
				if(node == null) return;
				instrumentsDbTree.setSelectedDirectoryNode(node);
			}
		});
	}
		
	public void
	loadColumnWidths() {
		InstrumentsDbTableModel m = getModel();
		TableColumnModel tcm = getColumnModel();
		
		for(int i = 0; i < m.getColumnCount(); i++) {
			switch(m.getColumnType(i)) {
			case NAME:
				String s = "DbInstrumentsTable.nameColumnWidth";
				int w = ClassicPrefs.getIntProperty(s);
				if(w > 0) tcm.getColumn(i).setPreferredWidth(w);
				break;
			case SIZE:
				s = "DbInstrumentsTable.sizeColumnWidth";
				w = ClassicPrefs.getIntProperty(s);
				if(w > 0) tcm.getColumn(i).setPreferredWidth(w);
				break;
			case FORMAT_FAMILY:
				s = "DbInstrumentsTable.formatFamilyColumnWidth";
				w = ClassicPrefs.getIntProperty(s);
				if(w > 0) tcm.getColumn(i).setPreferredWidth(w);
				break;
			case FORMAT_VERSION:
				s = "DbInstrumentsTable.formatVersionColumnWidth";
				w = ClassicPrefs.getIntProperty(s);
				if(w > 0) tcm.getColumn(i).setPreferredWidth(w);
				break;
			case IS_DRUM:
				s = "DbInstrumentsTable.isDrumColumnWidth";
				w = ClassicPrefs.getIntProperty(s);
				if(w > 0) tcm.getColumn(i).setPreferredWidth(w);
				break;
			case CREATED:
				s = "DbInstrumentsTable.createdColumnWidth";
				w = ClassicPrefs.getIntProperty(s);
				if(w > 0) tcm.getColumn(i).setPreferredWidth(w);
				break;
			case MODIFIED:
				s = "DbInstrumentsTable.modifiedColumnWidth";
				w = ClassicPrefs.getIntProperty(s);
				if(w > 0) tcm.getColumn(i).setPreferredWidth(w);
				break;
			case PRODUCT:
				s = "DbInstrumentsTable.productColumnWidth";
				w = ClassicPrefs.getIntProperty(s);
				if(w > 0) tcm.getColumn(i).setPreferredWidth(w);
				break;
			case ARTISTS:
				s = "DbInstrumentsTable.artistsColumnWidth";
				w = ClassicPrefs.getIntProperty(s);
				if(w > 0) tcm.getColumn(i).setPreferredWidth(w);
				break;
			case INSTRUMENT_FILE:
				s = "DbInstrumentsTable.instrumentFileColumnWidth";
				w = ClassicPrefs.getIntProperty(s);
				if(w > 0) tcm.getColumn(i).setPreferredWidth(w);
				break;
			case INSTRUMENT_NR:
				s = "DbInstrumentsTable.instrumentNrColumnWidth";
				w = ClassicPrefs.getIntProperty(s);
				if(w > 0) tcm.getColumn(i).setPreferredWidth(w);
				break;
			case KEYWORDS:
				s = "DbInstrumentsTable.keywordsColumnWidth";
				w = ClassicPrefs.getIntProperty(s);
				if(w > 0) tcm.getColumn(i).setPreferredWidth(w);
				break;
			case DUMMY:
				tcm.getColumn(i).setPreferredWidth(10);
				break;
			}
		}
	}
	
	public void
	saveColumnWidths() {
		InstrumentsDbTableModel m = getModel();
		TableColumnModel tcm = getColumnModel();
		
		for(int i = 0; i < m.getColumnCount(); i++) {
			switch(m.getColumnType(i)) {
			case NAME:
				String s = "DbInstrumentsTable.nameColumnWidth";
				ClassicPrefs.setIntProperty(s, tcm.getColumn(i).getWidth());
				break;
			case SIZE:
				s = "DbInstrumentsTable.sizeColumnWidth";
				ClassicPrefs.setIntProperty(s, tcm.getColumn(i).getWidth());
				break;
			case FORMAT_FAMILY:
				s = "DbInstrumentsTable.formatFamilyColumnWidth";
				ClassicPrefs.setIntProperty(s, tcm.getColumn(i).getWidth());
				break;
			case FORMAT_VERSION:
				s = "DbInstrumentsTable.formatVersionColumnWidth";
				ClassicPrefs.setIntProperty(s, tcm.getColumn(i).getWidth());
				break;
			case IS_DRUM:
				s = "DbInstrumentsTable.isDrumColumnWidth";
				ClassicPrefs.setIntProperty(s, tcm.getColumn(i).getWidth());
				break;
			case CREATED:
				s = "DbInstrumentsTable.createdColumnWidth";
				ClassicPrefs.setIntProperty(s, tcm.getColumn(i).getWidth());
				break;
			case MODIFIED:
				s = "DbInstrumentsTable.modifiedColumnWidth";
				ClassicPrefs.setIntProperty(s, tcm.getColumn(i).getWidth());
				break;
			case PRODUCT:
				s = "DbInstrumentsTable.productColumnWidth";
				ClassicPrefs.setIntProperty(s, tcm.getColumn(i).getWidth());
				break;
			case ARTISTS:
				s = "DbInstrumentsTable.artistsColumnWidth";
				ClassicPrefs.setIntProperty(s, tcm.getColumn(i).getWidth());
				break;
			case INSTRUMENT_FILE:
				s = "DbInstrumentsTable.instrumentFileColumnWidth";
				ClassicPrefs.setIntProperty(s, tcm.getColumn(i).getWidth());
				break;
			case INSTRUMENT_NR:
				s = "DbInstrumentsTable.instrumentNrColumnWidth";
				ClassicPrefs.setIntProperty(s, tcm.getColumn(i).getWidth());
				break;
			case KEYWORDS:
				s = "DbInstrumentsTable.keywordsColumnWidth";
				ClassicPrefs.setIntProperty(s, tcm.getColumn(i).getWidth());
				break;
			
			}
		}
	}
	
	public void
	saveColumnsVisibleState() {
		InstrumentsDbTableModel m = getModel();
		
		String s = "DbInstrumentsTable.ShowSizeColumn";
		ClassicPrefs.setBoolProperty(s, m.getShowSizeColumn());
		s = "DbInstrumentsTable.ShowFormatFamilyColumn";
		ClassicPrefs.setBoolProperty(s, m.getShowFormatFamilyColumn());
		s = "DbInstrumentsTable.ShowFormatVersionColumn";
		ClassicPrefs.setBoolProperty(s, m.getShowFormatVersionColumn());
		s = "DbInstrumentsTable.ShowIsDrumColumn";
		ClassicPrefs.setBoolProperty(s, m.getShowIsDrumColumn());
		s = "DbInstrumentsTable.ShowCreatedColumn";
		ClassicPrefs.setBoolProperty(s, m.getShowCreatedColumn());
		s = "DbInstrumentsTable.ShowModifiedColumn";
		ClassicPrefs.setBoolProperty(s, m.getShowModifiedColumn());
		s = "DbInstrumentsTable.ShowProductColumn";
		ClassicPrefs.setBoolProperty(s, m.getShowProductColumn());
		s = "DbInstrumentsTable.ShowArtistsColumn";
		ClassicPrefs.setBoolProperty(s, m.getShowArtistsColumn());
		s = "DbInstrumentsTable.ShowInstrumentFileColumn";
		ClassicPrefs.setBoolProperty(s, m.getShowInstrumentFileColumn());
		s = "DbInstrumentsTable.ShowInstrumentNrColumn";
		ClassicPrefs.setBoolProperty(s, m.getShowInstrumentNrColumn());
		s = "DbInstrumentsTable.ShowKeywordsColumn";
		ClassicPrefs.setBoolProperty(s, m.getShowKeywordsColumn());
	}
		
	public String
	getUniqueDirectoryName() {
		DbDirectoryTreeNode node = getParentDirectoryNode();
		if(node == null || node.isDetached()) return null;
		if(node != instrumentsDbTree.getSelectedDirectoryNode()) return null;
		
		boolean b = false;
		int c = 2;
		String dir = "New Folder";
		
		while(true) {
			for(int i = 0; i < node.getChildCount(); i++) {
				
				if(dir.equals(node.getChildAt(i).getInfo().getName())) {
					b = true;
					break;
				}
			}
			
			if(!b) break;
			
			b = false;
			dir = "New Folder[" + c++ + "]";
		}
		
		return dir;
	}
	
	private final Vector<JMenu> loadInstrumentMenus = new Vector<JMenu>();
	private final Vector<JMenu> addToMidiMapMenus = new Vector<JMenu>();
	private final Vector<JMenu> addToOrchestraMenus = new Vector<JMenu>();
	
	public void
	registerLoadInstrumentMenus(JMenu menu) {
		loadInstrumentMenus.add(menu);
		updateLoadInstrumentMenu(menu);
	}
	
	public void
	registerAddToMidiMapMenu(JMenu menu) {
		addToMidiMapMenus.add(menu);
		updateAddToMidiMapMenu(menu);
	}
	
	public void
	registerAddToOrchestraMenu(JMenu menu) {
		addToOrchestraMenus.add(menu);
		updateAddToOrchestraMenu(menu);
	}
	
	private void
	updateLoadInstrumentMenus() {
		for(JMenu menu : loadInstrumentMenus) updateLoadInstrumentMenu(menu);
	}
	
	private void
	updateLoadInstrumentMenu(JMenu menu) {
		menu.removeAll();
		for(SamplerChannelModel m : CC.getSamplerModel().getChannels()) {
			menu.add(new JMenuItem(new LoadInstrumentAction(m)));
		}
		
		updateLoadInstrumentMenuState(menu);
	}
	
	private void
	updateLoadInstrumentMenuStates() {
		for(JMenu menu : loadInstrumentMenus) updateLoadInstrumentMenuState(menu);
	}
	
	private void
	updateLoadInstrumentMenuState(JMenu menu) {
		Object obj = getLeadObject();
		boolean b = obj == null || !(obj instanceof DbInstrumentInfo);
		b = b || CC.getSamplerModel().getChannelCount() == 0;
		menu.setEnabled(!b);
	}
	
	private void
	updateAddToMidiMapMenus() {
		for(JMenu menu : addToMidiMapMenus) updateAddToMidiMapMenu(menu);
	}
	
	private void
	updateAddToMidiMapMenu(JMenu menu) {
		menu.removeAll();
		for(int i = 0; i < CC.getSamplerModel().getMidiInstrumentMapCount(); i++) {
			MidiInstrumentMap m = CC.getSamplerModel().getMidiInstrumentMap(i);
			menu.add(new JMenuItem(new AddToMidiMapAction(m)));
		}
		
		updateAddToMidiMapMenuState(menu);
	}
	
	private void
	updateAddToMidiMapMenuStates() {
		for(JMenu menu : addToMidiMapMenus) updateAddToMidiMapMenuState(menu);
	}
	
	private void
	updateAddToMidiMapMenuState(JMenu menu) {
		Object obj = getLeadObject();
		boolean b = obj == null || !(obj instanceof DbInstrumentInfo);
		b = b || CC.getSamplerModel().getMidiInstrumentMapCount() == 0;
		menu.setEnabled(!b);
	}
	
	private void
	updateAddToOrchestraMenus() {
		for(JMenu menu : addToOrchestraMenus) updateAddToOrchestraMenu(menu);
	}

	private void
	updateAddToOrchestraMenu(JMenu menu) {
		menu.removeAll();
		for(int i = 0; i < CC.getOrchestras().getOrchestraCount(); i++) {
			OrchestraModel m = CC.getOrchestras().getOrchestra(i);
			Action a = new AddToOrchestraAction(m);
			menu.add(new JMenuItem(a));
		}
		
		updateAddToOrchestraMenuState(menu);
	}
	
	private void
	updateAddToOrchestraMenuStates() {
		for(JMenu menu : addToOrchestraMenus) updateAddToOrchestraMenuState(menu);
	}
	
	private void
	updateAddToOrchestraMenuState(JMenu menu) {
		Object obj = getLeadObject();
		boolean b = obj == null || !(obj instanceof DbInstrumentInfo);
		b = b || CC.getOrchestras().getOrchestraCount() == 0;
		menu.setEnabled(!b);
	}
	
	private boolean
	showYesNoDialog(String s) {
		Window w = JuifeUtils.getWindow(this);
		if(w instanceof Dialog) return HF.showYesNoDialog((Dialog)w, s);
		if(w instanceof Frame) return HF.showYesNoDialog((Frame)w, s);
		return HF.showYesNoDialog((Frame)null, s);
	}
	
	private class ReloadAction extends AbstractAction implements TreeSelectionListener {
		ReloadAction() {
			super(i18n.getMenuLabel("instrumentsdb.actions.reload"));
			
			String s = i18n.getMenuLabel("instrumentsdb.actions.reload.tt");
			putValue(SHORT_DESCRIPTION, s);
			putValue(Action.SMALL_ICON, Res.iconReload22);
			setEnabled(false);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			DbDirectoryTreeNode n = instrumentsDbTree.getSelectedDirectoryNode();
			if(n == null) return;
			instrumentsDbTree.refreshDirectoryContent(n.getInfo().getDirectoryPath());
		}
		
		public void
		valueChanged(TreeSelectionEvent e) {
			DbDirectoryTreeNode n = instrumentsDbTree.getSelectedDirectoryNode();
			setEnabled(n != null);
		}
	}
	
	class CreateDirectoryAction extends AbstractAction {
		private String directoryName = null;
		
		CreateDirectoryAction() {
			super(i18n.getMenuLabel("instrumentsdb.actions.createFolder"));
			
			String s = i18n.getMenuLabel("instrumentsdb.actions.createFolder.tt");
			putValue(SHORT_DESCRIPTION, s);
			putValue(Action.SMALL_ICON, Res.iconNew16);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			setDirectoryName(getUniqueDirectoryName());
			
			String path = instrumentsDbTree.getSelectedDirectoryPath();
			if(path.length() > 1) path += "/";
			path += getDirectoryName();
			
			final InstrumentsDb.CreateDirectory t =
				new InstrumentsDb.CreateDirectory(path);
			
			setCreatedDirectoryName(directoryName);
			
			t.addTaskListener(new TaskListener() {
				public void
				taskPerformed(TaskEvent e) {
					if(t.doneWithErrors()) {
						setCreatedDirectoryName(null);
						return;
					}
				}
			});
			CC.getTaskQueue().add(t);
		}
		
		/**
		 * Gets the name of the directory to be created.
		 * @return The name of the directory to be created.
		 */
		public String
		getDirectoryName() { return directoryName; }
		
		/**
		 * Sets the name of the directory to be created.
		 * @param name The name of the directory to be created.
		 */
		public void
		setDirectoryName(String name) { directoryName = name; }
	}
	
	class DeleteAction extends AbstractAction {
		DeleteAction() {
			super(i18n.getMenuLabel("instrumentsdb.actions.delete"));
			
			String s;
			s = i18n.getMenuLabel("instrumentsdb.actions.delete.tt");
			putValue(SHORT_DESCRIPTION, s);
			setEnabled(false);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			final DbDirectoryInfo[] dirs = getSelectedDirectories();
			
			if(dirs.length > 0) {
				String s = i18n.getMessage("InstrumentsDbTable.confirmDeletion");
				if(!showYesNoDialog(s)) return;
				
				final Task t = new InstrumentsDb.RemoveDirectories(dirs);
				t.addTaskListener(new TaskListener() {
					public void
					taskPerformed(TaskEvent e) {
						if(instrumentsDbTree.getSelectionCount() == 0) {
							// update search results
							// TODO: lazily implemented
							deleteDirectories(dirs);
						}
					}
				});
				CC.getTaskQueue().add(t);
				
				
			}
			
			final DbInstrumentInfo[] instrs = getSelectedInstruments();
			if(instrs.length > 0) {
				final Task t = new InstrumentsDb.RemoveInstruments(instrs);
				t.addTaskListener(new TaskListener() {
					public void
					taskPerformed(TaskEvent e) {
						if(instrumentsDbTree.getSelectionCount() == 0) {
							// update search results
							// TODO: lazily implemented
							deleteInstruments(instrs);
						}
					}
				});
				CC.getTaskQueue().add(t);
			}
		}
		
		/** Deletes the specified directories from the model */
		private void
		deleteDirectories(DbDirectoryInfo[] dirs) {
			for(DbDirectoryInfo info : dirs) {
				String path = info.getDirectoryPath();
				getParentDirectoryNode().removeDirectoryByPathName(path);
				getModel().fireTableDataChanged();
			}
		}
		
		/** Deletes the specified instruments from the model */
		private void
		deleteInstruments(DbInstrumentInfo[] instrs) {
			for(DbInstrumentInfo info : instrs) {
				String path = info.getInstrumentPath();
				getParentDirectoryNode().removeInstrumentByPathName(path);
				getModel().fireTableDataChanged();
			}
		}
	}
	
	class AddInstrumentsFromFileAction extends AbstractAction {
		AddInstrumentsFromFileAction() {
			super(i18n.getMenuLabel("instrumentsdb.actions.addInstruments.fromFile"));
			
			String s = "instrumentsdb.actions.addInstruments.fromFile.tt";
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel(s));
		}
		
		public void
		actionPerformed(ActionEvent e) {
			String s;
			DbDirectoryTreeNode node = getParentDirectoryNode();
			if(node == null || node.getInfo() == null) s = null;
			else s = node.getInfo().getDirectoryPath();
			
			AddDbInstrumentsFromFileDlg dlg;
			Window w = JuifeUtils.getWindow(InstrumentsDbTable.this);
			if(w instanceof Dialog) {
				dlg = new AddDbInstrumentsFromFileDlg((Dialog)w, s);
			} else if(w instanceof Frame) {
				dlg = new AddDbInstrumentsFromFileDlg((Frame)w, s);
			} else {
				dlg = new AddDbInstrumentsFromFileDlg((Frame)null, s);
			}
			
			dlg.setVisible(true);
			if(w != null) w.toFront();
		}
	}
	
	class AddInstrumentsFromDirAction extends AbstractAction {
		AddInstrumentsFromDirAction() {
			super(i18n.getMenuLabel("instrumentsdb.actions.addInstruments.fromDir"));
			
			String s = "instrumentsdb.actions.addInstruments.fromDir.tt";
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel(s));
		}
		
		public void
		actionPerformed(ActionEvent e) {
			String s;
			DbDirectoryTreeNode node = getParentDirectoryNode();
			if(node == null || node.getInfo() == null) s = null;
			else s = node.getInfo().getDirectoryPath();
			
			AddDbInstrumentsFromDirDlg dlg;
			Window w = JuifeUtils.getWindow(InstrumentsDbTable.this);
			if(w instanceof Dialog) {
				dlg = new AddDbInstrumentsFromDirDlg((Dialog)w, s);
			} else if(w instanceof Frame) {
				dlg = new AddDbInstrumentsFromDirDlg((Frame)w, s);
			} else {
				dlg = new AddDbInstrumentsFromDirDlg((Frame)null, s);
			}
			
			dlg.setVisible(true);
			if(w != null) w.toFront();
		}
	}
	
	class LoadInstrumentAction extends AbstractAction {
		private final SamplerChannelModel channelModel;
		
		LoadInstrumentAction(SamplerChannelModel model) {
			String s = "instrumentsdb.actions.loadInstrument.onChannel";
			putValue(Action.NAME, i18n.getMenuLabel(s, model.getChannelId()));
			channelModel = model;
		}
		
		public void
		actionPerformed(ActionEvent e) {
			Object obj = getLeadObject();
			if(obj == null || !(obj instanceof DbInstrumentInfo)) return;
			DbInstrumentInfo info = (DbInstrumentInfo)obj;
			int idx = info.getInstrumentIndex();
			channelModel.setBackendEngineType(info.getFormatFamily()); // TODO: fix this
			channelModel.loadBackendInstrument(info.getFilePath(), idx);
		}
	}
	
	class AddToMidiMapAction extends AbstractAction {
		private final MidiInstrumentMap midiMap;
		
		AddToMidiMapAction(MidiInstrumentMap map) {
			super(map.getName());
			midiMap = map;
		}
		
		public void
		actionPerformed(ActionEvent e) {
			Object obj = getLeadObject();
			if(obj == null || !(obj instanceof DbInstrumentInfo)) return;
			
			DbInstrumentInfo info = (DbInstrumentInfo)obj;
			
			AddMidiInstrumentDlg dlg;
			Window w = JuifeUtils.getWindow(InstrumentsDbTable.this);
			if(w instanceof Dialog) {
				dlg = new AddMidiInstrumentDlg((Dialog)w);
			} else if(w instanceof Frame) {
				dlg = new AddMidiInstrumentDlg((Frame)w);
			} else {
				dlg = new AddMidiInstrumentDlg((Frame)null);
			}
			
			dlg.setInstrumentName(info.getName());
			dlg.setVisible(true);
			if(dlg.isCancelled()) return;
			
			MidiInstrumentInfo instrInfo = new MidiInstrumentInfo();
			instrInfo.setName(dlg.getInstrumentName());
			instrInfo.setFilePath(info.getFilePath());
			instrInfo.setInstrumentIndex(info.getInstrumentIndex());
			instrInfo.setEngine(info.getFormatFamily()); // TODO: this should be fixed
			instrInfo.setVolume(dlg.getVolume());
			instrInfo.setLoadMode(dlg.getLoadMode());
			
			int id = midiMap.getMapId();
			int b = dlg.getMidiBank();
			int p = dlg.getMidiProgram();
			CC.getSamplerModel().mapBackendMidiInstrument(id, b, p, instrInfo);
		}
	}
	
	class AddToOrchestraAction extends AbstractAction {
		private final OrchestraModel orchestraModel;
		
		AddToOrchestraAction(OrchestraModel model) {
			super(model.getName());
			orchestraModel = model;
		}
		
		public void
		actionPerformed(ActionEvent e) {
			Object obj = getLeadObject();
			if(obj == null || !(obj instanceof DbInstrumentInfo)) return;
			DbInstrumentInfo info = (DbInstrumentInfo)obj;
			Instrument instr = new Instrument();
			instr.setPath(info.getFilePath());
			instr.setInstrumentIndex(info.getInstrumentIndex());
			instr.setName(info.getName());
			instr.setDescription(info.getDescription());
			instr.setEngine(info.getFormatFamily()); // TODO: this should be fixed
			orchestraModel.addInstrument(instr);
		}
	}
	
	class PropertiesAction extends AbstractAction {
		PropertiesAction() {
			super(i18n.getMenuLabel("instrumentsdb.actions.properties"));
			
			String s;
			s = i18n.getMenuLabel("instrumentsdb.actions.properties.tt");
			putValue(SHORT_DESCRIPTION, s);
			setEnabled(false);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			Object obj = getLeadObject();
			if(obj == null) {
				DbDirectoryTreeNode node = getParentDirectoryNode();
				if(node == null || node.getInfo() == null) return;
				showDirectoryProperties(node.getInfo());
				return;
			}
			
			if(obj instanceof DbDirectoryInfo) {
				showDirectoryProperties((DbDirectoryInfo)obj);
			} else if(obj instanceof DbInstrumentInfo) {
				showInstrumentProperties((DbInstrumentInfo)obj);
			}
		}
		
		private void
		showInstrumentProperties(DbInstrumentInfo instr) {
			JPanel p = new DbInstrumentPropsPane(instr);
			String s = i18n.getLabel("InstrumentsDbFrame.instrProps");
			showDialog(s, p);
		}
		
		private void
		showDirectoryProperties(DbDirectoryInfo dir) {
			JPanel p = new DbDirectoryPropsPane(dir);
			String s = i18n.getLabel("InstrumentsDbFrame.dirProps");
			showDialog(s, p);
		}
		
		private void
		showDialog(String title, JPanel mainPane) {
			InformationDialog dlg;
			Window w = JuifeUtils.getWindow(InstrumentsDbTable.this);
			if(w instanceof Dialog) {
				dlg = new InformationDialog((Dialog)w, title, mainPane);
			} else if(w instanceof Frame) {
				dlg = new InformationDialog((Frame)w, title, mainPane);
			} else {
				dlg = new InformationDialog((Frame)null, title, mainPane);
			}
			
			dlg.setMinimumSize(dlg.getPreferredSize());
			dlg.setVisible(true);
		}
	}
	
	class RenameAction extends AbstractAction {
		private String directoryPath = null;
		
		RenameAction() {
			super(i18n.getMenuLabel("instrumentsdb.edit.rename"));
			
			String s = i18n.getMenuLabel("instrumentsdb.edit.rename.tt");
			putValue(SHORT_DESCRIPTION, s);
			setEnabled(false);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			int i = getSelectionModel().getLeadSelectionIndex();
			if(i == -1) return;
			editCellAt(i, 0);
		}
	}
	
	class ChangeDescriptionAction extends AbstractAction {
		private String directoryPath = null;
		
		ChangeDescriptionAction() {
			super(i18n.getMenuLabel("instrumentsdb.edit.description"));
			
			String s = i18n.getMenuLabel("instrumentsdb.edit.description.tt");
			putValue(SHORT_DESCRIPTION, s);
			setEnabled(false);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			Object obj = getLeadObject();
			if(obj == null) return;
			
			if(obj instanceof DbDirectoryInfo) {
				DbDirectoryInfo info = (DbDirectoryInfo)obj;
				String s = editDescription(info.getDescription());
				if(s == null) return;
				String path = info.getDirectoryPath();
				Task t = new InstrumentsDb.SetDirectoryDescription(path, s);
				CC.getTaskQueue().add(t);
			} else if(obj instanceof DbInstrumentInfo) {
				DbInstrumentInfo info = (DbInstrumentInfo)obj;
				String s = editDescription(info.getDescription());
				if(s == null) return;
				String path = info.getInstrumentPath();
				Task t = new InstrumentsDb.SetInstrumentDescription(path, s);
				CC.getTaskQueue().add(t);
			}
		}
		
		private String
		editDescription(String s) {
			DbDescriptionDlg dlg;
			Window w = JuifeUtils.getWindow(InstrumentsDbTable.this);
			if(w instanceof Dialog) {
				dlg = new DbDescriptionDlg((Dialog)w);
			} else if(w instanceof Frame) {
				dlg = new DbDescriptionDlg((Frame)w);
			} else {
				dlg = new DbDescriptionDlg((Frame)null);
			}
			
			dlg.setDescription(s);
			dlg.setVisible(true);
			if(dlg.isCancelled()) return null;
			return dlg.getDescription();
		}
	}
	
	class CutAction extends AbstractAction {
		CutAction() {
			super(i18n.getMenuLabel("instrumentsdb.edit.cut"));
			
			String s = i18n.getMenuLabel("instrumentsdb.edit.cut.tt");
			putValue(SHORT_DESCRIPTION, s);
			setEnabled(false);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			DbClipboard dbClipboard = InstrumentsDbFrame.getDbClipboard();
			dbClipboard.setDirectories(getSelectedDirectories());
			dbClipboard.setInstruments(getSelectedInstruments());
			dbClipboard.setOperation(DbClipboard.Operation.CUT);
		}
	}
	
	class CopyAction extends AbstractAction {
		CopyAction() {
			super(i18n.getMenuLabel("instrumentsdb.edit.copy"));
			
			String s = i18n.getMenuLabel("instrumentsdb.edit.copy.tt");
			putValue(SHORT_DESCRIPTION, s);
			setEnabled(false);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			DbClipboard dbClipboard = InstrumentsDbFrame.getDbClipboard();
			dbClipboard.setDirectories(getSelectedDirectories());
			dbClipboard.setInstruments(getSelectedInstruments());
			dbClipboard.setOperation(DbClipboard.Operation.COPY);
		}
	}
	
	class PasteAction extends AbstractAction implements TreeSelectionListener, ChangeListener {
		PasteAction() {
			super(i18n.getMenuLabel("instrumentsdb.edit.paste"));
			
			String s = i18n.getMenuLabel("instrumentsdb.edit.paste.tt");
			putValue(SHORT_DESCRIPTION, s);
			setEnabled(false);
			InstrumentsDbFrame.getDbClipboard().addChangeListener(this);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			final DbClipboard dbClipboard = InstrumentsDbFrame.getDbClipboard();
			DbDirectoryInfo[] dirs = dbClipboard.getDirectories();
			DbInstrumentInfo[] instrs = dbClipboard.getInstruments();
			String dest = instrumentsDbTree.getSelectedDirectoryPath();
			
			Task t;
			if(dbClipboard.getOperation() == DbClipboard.Operation.CUT) {
				t = new InstrumentsDb.Move(dirs, instrs, dest);
				dbClipboard.setDirectories(new DbDirectoryInfo[0]);
				dbClipboard.setInstruments(new DbInstrumentInfo[0]);
			} else if(dbClipboard.getOperation() == DbClipboard.Operation.COPY) {
				t = new InstrumentsDb.Copy(dirs, instrs, dest);
			} else {
				return;
			}
			
			CC.getTaskQueue().add(t);
		}
		
		public void
		valueChanged(TreeSelectionEvent e) { updateState(); }
		
		public void
		stateChanged(ChangeEvent e) { updateState(); }
		
		private void
		updateState() {
			DbDirectoryTreeNode n = instrumentsDbTree.getSelectedDirectoryNode();
			if(n == null) {
				setEnabled(false);
				return;
			}
			
			DbClipboard dbClipboard = InstrumentsDbFrame.getDbClipboard();
			int dirs = dbClipboard.getDirectories().length;
			setEnabled(dirs > 0 || dbClipboard.getInstruments().length > 0);
		}
	}
	
	class InstrumentsDbCellRenderer extends JLabel implements TableCellRenderer {
		
		InstrumentsDbCellRenderer() {
			setOpaque(true);
			setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
		}
		
		public Component
		getTableCellRendererComponent (
			JTable table,
			Object value,
			boolean isSelected,
			boolean hasFocus,
			int row,
			int column
		) {
			if(column == 0 && value != null) {
				String s;
				if(value instanceof DbDirectoryInfo) {
					setIcon(Res.iconFolder16);
					s = ((DbDirectoryInfo)value).getDescription();
					setToolTipText(s.length() == 0 ? null : s);
				} else if(value instanceof String) {
					setIcon(Res.iconFolder16);
					setToolTipText(null);
				} else if(value instanceof DbInstrumentInfo) {
					setIcon(Res.iconInstrument16);
					s = ((DbInstrumentInfo)value).getDescription();
					setToolTipText(s.length() == 0 ? null : s);
				} else {
					setIcon(null);
					setToolTipText(null);
				}
			} else {
				setIcon(null);
				setToolTipText(null);
			}
			
			if(value != null) setText(value.toString());
			else setText("");
			
			if (isSelected) {
				setBackground(table.getSelectionBackground());
				setForeground(table.getSelectionForeground());
			} else {
				setBackground(table.getBackground());
				setForeground(table.getForeground());
			}
			
			ColumnType ct =
				((InstrumentsDbTableModel)table.getModel()).getColumnType(column);
			
			if(ct == ColumnType.IS_DRUM || ct == ColumnType.FORMAT_FAMILY) {
				setHorizontalAlignment(CENTER);
			} else  if (    ct == ColumnType.SIZE ||
					ct == ColumnType.INSTRUMENT_NR ||
					ct == ColumnType.FORMAT_VERSION
				) {
				setHorizontalAlignment(RIGHT);
			} else {
				setHorizontalAlignment(LEFT);
			}
			
			return this;
		}
	}
	
	private final EventHandler eventHandler = new EventHandler();
	
	private EventHandler
	getHandler() { return eventHandler; }
	
	private class EventHandler implements ListSelectionListener, TreeSelectionListener,
				SamplerChannelListListener, ListListener<OrchestraModel> {
		
		public void
		valueChanged(ListSelectionEvent e) {
			boolean b = !getSelectionModel().isSelectionEmpty();
			deleteAction.setEnabled(b);
			propertiesAction.setEnabled(b || instrumentsDbTree.getSelectionCount() > 0);
			renameAction.setEnabled(b);
			changeDescriptionAction.setEnabled(b);
			cutAction.setEnabled(b);
			copyAction.setEnabled(b);
			updateLoadInstrumentMenuStates();
			updateAddToMidiMapMenuStates();
			updateAddToOrchestraMenuStates();
		}
		
		public void
		valueChanged(TreeSelectionEvent e) {
			DbDirectoryTreeNode n = instrumentsDbTree.getSelectedDirectoryNode();
			setParentDirectoryNode(n);
			reloadAction.setEnabled(n != null);
			createDirectoryAction.setEnabled(n != null);
			propertiesAction.setEnabled(n != null || getLeadObject() != null);
		}
		
		public void
		channelAdded(SamplerChannelListEvent e) {
			updateLoadInstrumentMenus();
		}
		
		public void
		channelRemoved(SamplerChannelListEvent e) {
			updateLoadInstrumentMenus();
		}
		
		public void
		entryAdded(ListEvent<OrchestraModel> e) { updateAddToOrchestraMenus(); }
		
		public void
		entryRemoved(ListEvent<OrchestraModel> e) { updateAddToOrchestraMenus(); }
	}
	
	class ContextMenu extends MouseAdapter {
		private final JPopupMenu instrumentMenu = new JPopupMenu();
		private final JPopupMenu directoryMenu = new JPopupMenu();
		private final JPopupMenu menu = new JPopupMenu();
		
		private JMenu loadInstrumentMenu;
		private JMenu addToMidiMapMenu;
		private JMenu addToOrchestraMenu;
		
		ContextMenu() {
			JMenuItem mi = new JMenuItem(pasteAction);
			mi.setIcon(null);
			menu.add(mi);
			
			menu.addSeparator();
			
			mi = new JMenuItem(createDirectoryAction);
			mi.setIcon(null);
			menu.add(mi);
			
			String s = i18n.getMenuLabel("instrumentsdb.actions.addInstruments");
			JMenu addInstrumentsMenu = new JMenu(s);
			menu.add(addInstrumentsMenu);
			
			mi = new JMenuItem(addInstrumentsFromFileAction);
			mi.setIcon(null);
			addInstrumentsMenu.add(mi);
			
			mi = new JMenuItem(addInstrumentsFromDirAction);
			mi.setIcon(null);
			addInstrumentsMenu.add(mi);
			
			menu.addSeparator();
			
			mi = new JMenuItem(reloadAction);
			mi.setIcon(null);
			menu.add(mi);
			
			menu.addSeparator();
			
			mi = new JMenuItem(propertiesAction);
			mi.setIcon(null);
			menu.add(mi);
			
			// Instrument's context menu
			mi = new JMenuItem(cutAction);
			mi.setIcon(null);
			instrumentMenu.add(mi);
			
			mi = new JMenuItem(copyAction);
			mi.setIcon(null);
			instrumentMenu.add(mi);
			
			instrumentMenu.addSeparator();
			
			mi = new JMenuItem(deleteAction);
			mi.setIcon(null);
			instrumentMenu.add(mi);
			
			mi = new JMenuItem(renameAction);
			mi.setIcon(null);
			instrumentMenu.add(mi);
			
			mi = new JMenuItem(changeDescriptionAction);
			mi.setIcon(null);
			instrumentMenu.add(mi);
			
			instrumentMenu.addSeparator();
			
			s = i18n.getMenuLabel("instrumentsdb.actions.loadInstrument");
			loadInstrumentMenu = new JMenu(s);
			instrumentMenu.add(loadInstrumentMenu);
			registerLoadInstrumentMenus(loadInstrumentMenu);
			
			addToMidiMapMenu =
				new JMenu(i18n.getMenuLabel("instrumentsdb.actions.addToMidiMap"));
			instrumentMenu.add(addToMidiMapMenu);
			registerAddToMidiMapMenu(addToMidiMapMenu);
			
			s = i18n.getMenuLabel("instrumentsdb.actions.addToOrchestra");
			addToOrchestraMenu = new JMenu(s);
			instrumentMenu.add(addToOrchestraMenu);
			registerAddToOrchestraMenu(addToOrchestraMenu);
			
			instrumentMenu.addSeparator();
			
			mi = new JMenuItem(propertiesAction);
			mi.setIcon(null);
			instrumentMenu.add(mi);
			
			// Directory's context menu
			mi = new JMenuItem(cutAction);
			mi.setIcon(null);
			directoryMenu.add(mi);
			
			mi = new JMenuItem(copyAction);
			mi.setIcon(null);
			directoryMenu.add(mi);
			
			directoryMenu.addSeparator();
			
			mi = new JMenuItem(deleteAction);
			mi.setIcon(null);
			directoryMenu.add(mi);
			
			mi = new JMenuItem(renameAction);
			mi.setIcon(null);
			directoryMenu.add(mi);
			
			mi = new JMenuItem(changeDescriptionAction);
			mi.setIcon(null);
			directoryMenu.add(mi);
			
			directoryMenu.addSeparator();
			
			mi = new JMenuItem(propertiesAction);
			mi.setIcon(null);
			directoryMenu.add(mi);
		}
		
		public void
		mousePressed(MouseEvent e) {
			if(e.isPopupTrigger()) show(e);
		}
	
		public void
		mouseReleased(MouseEvent e) {
			if(e.isPopupTrigger()) show(e);
		}
	
		void
		show(MouseEvent e) {
			Object obj = getLeadObject();
			if(obj == null) {
				menu.show(e.getComponent(), e.getX(), e.getY());
				return;
			}
			
			if(obj instanceof DbInstrumentInfo) {
				instrumentMenu.show(e.getComponent(), e.getX(), e.getY());
				return;
			}
			
			if(obj instanceof DbDirectoryInfo) {
				directoryMenu.show(e.getComponent(), e.getX(), e.getY());
				return;
			}
		}
	}
}
