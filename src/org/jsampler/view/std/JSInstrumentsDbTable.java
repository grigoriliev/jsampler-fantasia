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

package org.jsampler.view.std;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.LinkedList;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
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

import net.sf.juife.swing.InformationDialog;
import net.sf.juife.swing.JuifeUtils;
import net.sf.juife.Task;

import net.sf.juife.event.TaskEvent;
import net.sf.juife.event.TaskListener;

import org.jsampler.CC;
import org.jsampler.OrchestraInstrument;
import org.jsampler.MidiInstrumentMap;
import org.jsampler.OrchestraModel;
import org.jsampler.SamplerChannelModel;

import org.jsampler.event.ListEvent;
import org.jsampler.event.ListListener;
import org.jsampler.event.SamplerChannelListEvent;
import org.jsampler.event.SamplerChannelListListener;

import org.jsampler.task.InstrumentsDb;
import org.jsampler.task.Midi;

import org.jsampler.view.swing.DbClipboard;
import org.jsampler.view.swing.DbDirectoryTreeNode;
import org.jsampler.view.swing.InstrumentsDbTableModel;
import org.jsampler.view.swing.SHF;

import org.linuxsampler.lscp.DbDirectoryInfo;
import org.linuxsampler.lscp.DbInstrumentInfo;
import org.linuxsampler.lscp.MidiInstrumentEntry;
import org.linuxsampler.lscp.MidiInstrumentInfo;

import static org.jsampler.view.swing.InstrumentsDbTableModel.ColumnType;
import static org.jsampler.view.std.StdI18n.i18n;

import static org.linuxsampler.lscp.Parser.*;

/**
 *
 * @author Grigor Iliev
 */
public class JSInstrumentsDbTable extends org.jsampler.view.swing.AbstractInstrumentsDbTable {
	private JSInstrumentsDbTree instrumentsDbTree;
	private InstrumentsDbCellRenderer cellRenderer = new InstrumentsDbCellRenderer();
	
	public final Action reloadAction = new ReloadAction();
	public final Action createDirectoryAction = new CreateDirectoryAction();
	public final Action deleteAction = new DeleteAction();
	public final AddInstrumentsFromFileAction addInstrumentsFromFileAction =
		new AddInstrumentsFromFileAction();
	public final AddInstrumentsFromDirAction addInstrumentsFromDirAction =
		new AddInstrumentsFromDirAction();
	public final Action propertiesAction = new PropertiesAction();
	public final Action renameAction = new RenameAction();
	public final Action changeDescriptionAction = new ChangeDescriptionAction();
	public final Action cutAction = new CutAction();
	public final Action copyAction = new CopyAction();
	public final Action pasteAction;
	
	private static final DbClipboard dbClipboard = new DbClipboard();
	
	/**
	 * Creates a new instance of <code>JSInstrumentsDbTable</code>
	 */
	public
	JSInstrumentsDbTable(JSInstrumentsDbTree tree) {
		this(tree, "");
	}
	
	/**
	 * Creates a new instance of <code>JSInstrumentsDbTable</code>
	 * @param columnPrefix Used to create unique property names for storing
	 * the column preferences for different tables e.g. for DbInstrumentChooser,
	 * InstrumentsDbFrame.
	 */
	public
	JSInstrumentsDbTable(JSInstrumentsDbTree tree, String columnPrefix) {
		super(columnPrefix);
		
		instrumentsDbTree = tree;
		
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
		
		CC.getMainFrame().addChannelsPaneSelectionListener(new org.jsampler.event.ListSelectionListener() {
			public void
			valueChanged(org.jsampler.event.ListSelectionEvent e) {
				updateLoadInstrumentMenus();
			}
		});
		
		ListListener<MidiInstrumentMap> l = new ListListener<MidiInstrumentMap>() {
			public void
			entryAdded(ListEvent<MidiInstrumentMap> e) { updateAddToMidiMapMenus(); }
			
			public void
			entryRemoved(ListEvent<MidiInstrumentMap> e) { updateAddToMidiMapMenus(); }
		};
		
		CC.getSamplerModel().addMidiInstrumentMapListListener(l);
		
		installKeyboardListeners();
	}
	
	public static DbClipboard
	getDbClipboard() { return dbClipboard; }
	
	@Override
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

		int modKey = CC.getViewConfig().getDefaultModKey();
		
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put (
			KeyStroke.getKeyStroke(KeyEvent.VK_X, modKey),
			"none"
		);
		
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put (
			KeyStroke.getKeyStroke(KeyEvent.VK_C, modKey),
			"none"
		);
		
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put (
			KeyStroke.getKeyStroke(KeyEvent.VK_V, modKey),
			"none"
		);
		
		getInputMap(JComponent.WHEN_FOCUSED).put (
			KeyStroke.getKeyStroke(KeyEvent.VK_X, modKey),
			"none"
		);
		
		getInputMap(JComponent.WHEN_FOCUSED).put (
			KeyStroke.getKeyStroke(KeyEvent.VK_C, modKey),
			"none"
		);
		
		getInputMap(JComponent.WHEN_FOCUSED).put (
			KeyStroke.getKeyStroke(KeyEvent.VK_V, modKey),
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
		StdA4n.updateLoadInstrumentMenu(menu, loadInstrActionFactory);
		updateLoadInstrumentMenuState(menu);
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
		for(JMenu menu : loadInstrumentMenus) {
			StdA4n.updateLoadInstrumentMenu(menu, loadInstrActionFactory);
			updateLoadInstrumentMenuState(menu);
		}
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
		if(w instanceof Dialog) return SHF.showYesNoDialog((Dialog)w, s);
		if(w instanceof Frame) return SHF.showYesNoDialog((Frame)w, s);
		return SHF.showYesNoDialog((Frame)null, s);
	}
	
	private class ReloadAction extends AbstractAction implements TreeSelectionListener {
		ReloadAction() {
			super(i18n.getMenuLabel("instrumentsdb.actions.reload"));
			
			String s = i18n.getMenuLabel("instrumentsdb.actions.reload.tt");
			putValue(SHORT_DESCRIPTION, s);
			setEnabled(false);
		}
		
		@Override
		public void
		actionPerformed(ActionEvent e) {
			DbDirectoryTreeNode n = instrumentsDbTree.getSelectedDirectoryNode();
			if(n == null) return;
			final String path = n.getInfo().getDirectoryPath();
			instrumentsDbTree.refreshDirectoryContent(path);
			CC.scheduleInTaskQueue(new Runnable() {
				public void
				run() { instrumentsDbTree.setSelectedDirectory(path); }
			});
		}
		
		@Override
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
		}
		
		@Override
		public void
		actionPerformed(ActionEvent e) {
			setDirectoryName(getUniqueDirectoryName());
			
			String path = instrumentsDbTree.getSelectedDirectoryPath();
			if(path.length() > 1) path += "/";
			path += toEscapedFileName(getDirectoryName());
			
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
		
		@Override
		public void
		actionPerformed(ActionEvent e) {
			final DbDirectoryInfo[] dirs = getSelectedDirectories();
			
			if(dirs.length > 0) {
				String s = i18n.getMessage("JSInstrumentsDbTable.confirmDeletion");
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
		
		@Override
		public void
		actionPerformed(ActionEvent e) {
			String s;
			DbDirectoryTreeNode node = getParentDirectoryNode();
			if(node == null || node.getInfo() == null) s = null;
			else s = node.getInfo().getDirectoryPath();
			
			JSAddDbInstrumentsFromFileDlg dlg;
			Icon ico = instrumentsDbTree.getView().getOpenIcon();
			Window w = JuifeUtils.getWindow(JSInstrumentsDbTable.this);
			if(w instanceof Dialog) {
				dlg = new JSAddDbInstrumentsFromFileDlg((Dialog)w, s, ico);
			} else if(w instanceof Frame) {
				dlg = new JSAddDbInstrumentsFromFileDlg((Frame)w, s, ico);
			} else {
				dlg = new JSAddDbInstrumentsFromFileDlg((Frame)null, s, ico);
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
		
		@Override
		public void
		actionPerformed(ActionEvent e) {
			String s;
			DbDirectoryTreeNode node = getParentDirectoryNode();
			if(node == null || node.getInfo() == null) s = null;
			else s = node.getInfo().getDirectoryPath();
			
			JSAddDbInstrumentsFromDirDlg dlg;
			Icon ico = instrumentsDbTree.getView().getOpenIcon();
			Window w = JuifeUtils.getWindow(JSInstrumentsDbTable.this);
			if(w instanceof Dialog) {
				dlg = new JSAddDbInstrumentsFromDirDlg((Dialog)w, s, ico);
			} else if(w instanceof Frame) {
				dlg = new JSAddDbInstrumentsFromDirDlg((Frame)w, s, ico);
			} else {
				dlg = new JSAddDbInstrumentsFromDirDlg((Frame)null, s, ico);
			}
			
			dlg.setVisible(true);
			if(w != null) w.toFront();
		}
	}
	
	class LoadInstrumentAction extends StdA4n.LoadInstrumentAction {
		LoadInstrumentAction(SamplerChannelModel model, boolean onPanel) {
			super(model, onPanel);
		}
		
		@Override
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
	
	private LoadInstrumentActionFactory loadInstrActionFactory = new LoadInstrumentActionFactory();
	
	class LoadInstrumentActionFactory implements StdA4n.LoadInstrumentActionFactory {
		public StdA4n.LoadInstrumentAction
		createLoadInstrumentAction(SamplerChannelModel model, boolean onPanel) {
			return new LoadInstrumentAction(model, onPanel);
		}
	}
	
	class AddToMidiMapAction extends AbstractAction {
		private final MidiInstrumentMap midiMap;
		
		AddToMidiMapAction(MidiInstrumentMap map) {
			super(map.getName());
			midiMap = map;
		}
		
		@Override
		public void
		actionPerformed(ActionEvent e) {
			DbInstrumentInfo[] instruments = getSelectedInstruments();
			int l = instruments.length;
			if(l == 0) return;
			
			if(l > 4) {
				String s = "JSInstrumentsDbTable.confirmAddToMidiMap";
				s = i18n.getMessage(s, l, midiMap.getName());
				if(!SHF.showYesNoDialog(JSInstrumentsDbTable.this, s)) return;
			}
			
			JSAddMidiInstrumentDlg dlg;
			Window w = JuifeUtils.getWindow(JSInstrumentsDbTable.this);

			boolean b = instruments.length > 1;
			boolean apply2all = false;
			float volume = 1.0f;
			MidiInstrumentInfo.LoadMode loadMode = MidiInstrumentInfo.LoadMode.DEFAULT;
			final LinkedList<MidiInstrumentInfo> instrs = new LinkedList<MidiInstrumentInfo>();

			for(DbInstrumentInfo i : instruments) {
				if(!apply2all) {
					if(w instanceof Dialog) {
						dlg = new JSAddMidiInstrumentDlg((Dialog)w, midiMap, i, b);
					} else if(w instanceof Frame) {
						dlg = new JSAddMidiInstrumentDlg((Frame)w, midiMap, i, b);
					} else {
						dlg = new JSAddMidiInstrumentDlg((Frame)null, midiMap, i, b);
					}

					dlg.setVisible(true);
					if(dlg.isApplyToAllSelected()) {
						if(dlg.isCancelled()) break;

						apply2all = true;
						volume = dlg.getVolume();
						loadMode = dlg.getLoadMode();
					}
				} else {
					final MidiInstrumentInfo instrInfo = new MidiInstrumentInfo();
					instrInfo.setName(i.getName());
					instrInfo.setFilePath(i.getFilePath());
					instrInfo.setInstrumentIndex(i.getInstrumentIndex());
					instrInfo.setEngine(i.getEngine());
					instrInfo.setVolume(volume);
					instrInfo.setLoadMode(loadMode);

					instrs.add(instrInfo);
				}
			}

			if(instrs.isEmpty()) return;

			addToMap(instrs);
		}

		private void
		addToMap(final LinkedList<MidiInstrumentInfo> instrs) {
			CC.scheduleInTaskQueue(new Runnable() {
				public void
				run() { addToMap0(instrs); }
			});
		}

		private void
		addToMap0(final LinkedList<MidiInstrumentInfo> instrs) {
			if(instrs.isEmpty()) return;

			MidiInstrumentEntry e = midiMap.getAvailableEntry();
			if(e == null) {
				CC.getLogger().info("No available MIDI entry");
				return;
			}

			int id = midiMap.getMapId();
			int b = e.getMidiBank();
			int p = e.getMidiProgram();
			final Midi.MapInstrument t = new Midi.MapInstrument(id, b, p, instrs.pop());

			t.addTaskListener(new TaskListener() {
				public void
				taskPerformed(TaskEvent e) {
					CC.scheduleTask(new Midi.UpdateInstruments(midiMap.getMapId()));
					addToMap(instrs);
				}
			});

			CC.getTaskQueue().add(t);
		}
	}
	
	class AddToOrchestraAction extends AbstractAction {
		private final OrchestraModel orchestraModel;
		
		AddToOrchestraAction(OrchestraModel model) {
			super(model.getName());
			orchestraModel = model;
		}
		
		@Override
		public void
		actionPerformed(ActionEvent e) {
			DbInstrumentInfo[] instruments = getSelectedInstruments();
			int l = instruments.length;
			if(l == 0) return;
			
			if(l > 1) {
				String s = "JSInstrumentsDbTable.confirmAddToOrchestra";
				s = i18n.getMessage(s, l, orchestraModel.getName());
				if(!SHF.showYesNoDialog(JSInstrumentsDbTable.this, s)) return;
			}
			
			for(DbInstrumentInfo i : instruments) {
				OrchestraInstrument instr = new OrchestraInstrument();
				instr.setFilePath(i.getFilePath());
				instr.setInstrumentIndex(i.getInstrumentIndex());
				instr.setName(i.getName());
				instr.setDescription(i.getDescription());
				instr.setEngine(i.getFormatFamily()); // TODO: this should be fixed
				orchestraModel.addInstrument(instr);
			}
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
		
		@Override
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
			JPanel p = new JSDbInstrumentPropsPane(instr);
			String s = i18n.getLabel("JSInstrumentsDbTable.instrProps");
			showDialog(s, p);
		}
		
		private void
		showDirectoryProperties(DbDirectoryInfo dir) {
			JPanel p = new JSDbDirectoryPropsPane(dir);
			String s = i18n.getLabel("JSInstrumentsDbTable.dirProps");
			showDialog(s, p);
		}
		
		private void
		showDialog(String title, JPanel mainPane) {
			InformationDialog dlg;
			Window w = JuifeUtils.getWindow(JSInstrumentsDbTable.this);
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
		
		@Override
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
		
		@Override
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
			JSDbDescriptionDlg dlg;
			Window w = JuifeUtils.getWindow(JSInstrumentsDbTable.this);
			if(w instanceof Dialog) {
				dlg = new JSDbDescriptionDlg((Dialog)w);
			} else if(w instanceof Frame) {
				dlg = new JSDbDescriptionDlg((Frame)w);
			} else {
				dlg = new JSDbDescriptionDlg((Frame)null);
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
		
		@Override
		public void
		actionPerformed(ActionEvent e) {
			getDbClipboard().setDirectories(getSelectedDirectories());
			getDbClipboard().setInstruments(getSelectedInstruments());
			getDbClipboard().setOperation(DbClipboard.Operation.CUT);
		}
	}
	
	class CopyAction extends AbstractAction {
		CopyAction() {
			super(i18n.getMenuLabel("instrumentsdb.edit.copy"));
			
			String s = i18n.getMenuLabel("instrumentsdb.edit.copy.tt");
			putValue(SHORT_DESCRIPTION, s);
			setEnabled(false);
		}
		
		@Override
		public void
		actionPerformed(ActionEvent e) {
			getDbClipboard().setDirectories(getSelectedDirectories());
			getDbClipboard().setInstruments(getSelectedInstruments());
			getDbClipboard().setOperation(DbClipboard.Operation.COPY);
		}
	}
	
	class PasteAction extends AbstractAction implements TreeSelectionListener, ChangeListener {
		PasteAction() {
			super(i18n.getMenuLabel("instrumentsdb.edit.paste"));
			
			String s = i18n.getMenuLabel("instrumentsdb.edit.paste.tt");
			putValue(SHORT_DESCRIPTION, s);
			setEnabled(false);
			getDbClipboard().addChangeListener(this);
		}
		
		@Override
		public void
		actionPerformed(ActionEvent e) {
			DbDirectoryInfo[] dirs = getDbClipboard().getDirectories();
			DbInstrumentInfo[] instrs = getDbClipboard().getInstruments();
			String dest = instrumentsDbTree.getSelectedDirectoryPath();
			
			Task t;
			if(getDbClipboard().getOperation() == DbClipboard.Operation.CUT) {
				t = new InstrumentsDb.Move(dirs, instrs, dest);
				getDbClipboard().setDirectories(new DbDirectoryInfo[0]);
				getDbClipboard().setInstruments(new DbInstrumentInfo[0]);
			} else if(getDbClipboard().getOperation() == DbClipboard.Operation.COPY) {
				t = new InstrumentsDb.Copy(dirs, instrs, dest);
			} else {
				return;
			}
			
			CC.getTaskQueue().add(t);
		}
		
		@Override
		public void
		valueChanged(TreeSelectionEvent e) { updateState(); }
		
		@Override
		public void
		stateChanged(ChangeEvent e) { updateState(); }
		
		private void
		updateState() {
			DbDirectoryTreeNode n = instrumentsDbTree.getSelectedDirectoryNode();
			if(n == null) {
				setEnabled(false);
				return;
			}
			
			int dirs = getDbClipboard().getDirectories().length;
			setEnabled(dirs > 0 || getDbClipboard().getInstruments().length > 0);
		}
	}
	
	class InstrumentsDbCellRenderer extends JLabel implements TableCellRenderer {
		
		InstrumentsDbCellRenderer() {
			setOpaque(true);
			setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
		}
		
		@Override
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
					setIcon(getView().getFolderIcon());
					s = ((DbDirectoryInfo)value).getDescription();
					setToolTipText(s.length() == 0 ? null : s);
				} else if(value instanceof String) {
					setIcon(getView().getFolderIcon());
					setToolTipText(null);
				} else if(value instanceof DbInstrumentInfo) {
					DbInstrumentInfo info = (DbInstrumentInfo)value;
					if("GIG".equals(info.getFormatFamily())) { // TODO: fix it!
						setIcon(getView().getGigInstrumentIcon());
					} else {
						setIcon(getView().getInstrumentIcon());
					}
					
					s = info.getDescription();
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
		
		@Override
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
		
		@Override
		public void
		valueChanged(TreeSelectionEvent e) {
			DbDirectoryTreeNode n = instrumentsDbTree.getSelectedDirectoryNode();
			setParentDirectoryNode(n);
			reloadAction.setEnabled(n != null);
			createDirectoryAction.setEnabled(n != null);
			propertiesAction.setEnabled(n != null || getLeadObject() != null);
		}
		
		@Override
		public void
		channelAdded(SamplerChannelListEvent e) {
			if(CC.getSamplerModel().getChannelListIsAdjusting()) return;
			updateLoadInstrumentMenus();
		}
		
		@Override
		public void
		channelRemoved(SamplerChannelListEvent e) {
			updateLoadInstrumentMenus();
		}
		
		@Override
		public void
		entryAdded(ListEvent<OrchestraModel> e) { updateAddToOrchestraMenus(); }
		
		@Override
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
		
		class MenuItem extends JMenuItem {
			MenuItem(Action a) { super(a); }
			
			public Icon
			getIcon() { return null; }
		}
		
		ContextMenu() {
			JMenuItem mi = new JMenuItem(pasteAction);
			mi.setIcon(null);
			menu.add(mi);
			
			menu.addSeparator();
			
			mi = new MenuItem(createDirectoryAction);
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
			
			mi = new MenuItem(reloadAction);
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
			loadInstrumentMenu = SHF.getViewConfig().createMultiColumnMenu(s);
			instrumentMenu.add(loadInstrumentMenu);
			registerLoadInstrumentMenus(loadInstrumentMenu);
			
			s = i18n.getMenuLabel("instrumentsdb.actions.addToMidiMap");
			addToMidiMapMenu = SHF.getViewConfig().createMultiColumnMenu(s);
			instrumentMenu.add(addToMidiMapMenu);
			registerAddToMidiMapMenu(addToMidiMapMenu);
			
			s = i18n.getMenuLabel("instrumentsdb.actions.addToOrchestra");
			addToOrchestraMenu = SHF.getViewConfig().createMultiColumnMenu(s);
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
		
		@Override
		public void
		mousePressed(MouseEvent e) {
			if(e.isPopupTrigger()) show(e);
		}
	
		@Override
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
