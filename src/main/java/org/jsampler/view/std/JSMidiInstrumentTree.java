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

import java.awt.Dimension;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.KeyStroke;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import org.jsampler.CC;
import org.jsampler.MidiInstrument;
import org.jsampler.MidiInstrumentMap;

import org.jsampler.event.MidiInstrumentEvent;
import org.jsampler.event.MidiInstrumentListener;
import org.jsampler.event.MidiInstrumentMapEvent;
import org.jsampler.event.MidiInstrumentMapListener;
import org.jsampler.view.swing.SHF;

import org.linuxsampler.lscp.MidiInstrumentInfo;

import net.sf.juife.swing.OkCancelDialog;

import static org.jsampler.JSPrefs.FIRST_MIDI_BANK_NUMBER;
import static org.jsampler.JSPrefs.FIRST_MIDI_PROGRAM_NUMBER;
import static org.jsampler.view.std.StdI18n.i18n;


/**
 *
 * @author Grigor Iliev
 */
public class JSMidiInstrumentTree extends JTree {
	private DefaultTreeModel model;
	private MidiInstrumentMap midiInstrumentMap;
	private final ContextMenu contextMenu;
	
	/**
	 * Creates a new instance of <code>JSMidiInstrumentTree</code>
	 */
	public
	JSMidiInstrumentTree() {
		setRootVisible(false);
		setShowsRootHandles(true);
		setEditable(false);
		
		addMouseListener(new MouseAdapter() {
			public void
			mousePressed(MouseEvent e) {
				if(e.getButton() != e.BUTTON3) return;
				setSelectionPath(getClosestPathForLocation(e.getX(), e.getY()));
			}
			
			public void
			mouseClicked(MouseEvent e) {
				if(e.getButton() != e.BUTTON1) return;
				if(e.getClickCount() > 1) editSelectedInstrument();
			}
		});
		
		setMidiInstrumentMap(null);
		
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		contextMenu = new ContextMenu();
		addMouseListener(contextMenu);
		
		addTreeSelectionListener(getHandler());
		
		Action a = new AbstractAction() {
			public void
			actionPerformed(ActionEvent e) {
				removeSelectedInstrumentOrBank();
			}
		};
		
		KeyStroke k = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
		getInputMap(JComponent.WHEN_FOCUSED).put(k, "removeSelectedInstrumentOrBank");
		getActionMap().put("removeSelectedInstrumentOrBank", a);
		
		String s = FIRST_MIDI_BANK_NUMBER;
		CC.preferences().addPropertyChangeListener(s, new PropertyChangeListener() {
			public void
			propertyChange(PropertyChangeEvent e) {
				model.reload();
			}
		});
		
		s = FIRST_MIDI_PROGRAM_NUMBER;
		CC.preferences().addPropertyChangeListener(s, new PropertyChangeListener() {
			public void
			propertyChange(PropertyChangeEvent e) {
				model.reload();
				contextMenu.updateChangeProgramMenu();
			}
		});
	}
	
	/**
	 * Gets the MIDI instrument map that is represented by this MIDI instrument tree.
	 * @return The MIDI instrument map that is represented by this MIDI instrument tree.
	 */
	public MidiInstrumentMap
	getMidiInstrumentMap() { return midiInstrumentMap; }
	
	/**
	 * Sets the MIDI instrument map to be represented by this MIDI instrument tree.
	 * @param map The MIDI instrument map to be represented by this MIDI instrument tree.
	 */
	public void
	setMidiInstrumentMap(MidiInstrumentMap map) {
		if(getMidiInstrumentMap() != null) {
			for(MidiInstrument instr : getMidiInstrumentMap().getAllMidiInstruments()) {
				instr.removeMidiInstrumentListener(getHandler());
			}
			
			getMidiInstrumentMap().removeMidiInstrumentMapListener(getHandler());
		}
		
		midiInstrumentMap = map;
		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode() {
			public boolean
			isLeaf() { return false; }
			
			public Object
			getUserObject() { return "/"; }
		};
		
		model = new DefaultTreeModel(root);
		
		if(map != null) {
			for(MidiInstrument instr : map.getAllMidiInstruments()) {
				mapInstrument(instr);
				instr.addMidiInstrumentListener(getHandler());
			}
		
			map.addMidiInstrumentMapListener(getHandler());
		}
		
		setEnabled(map != null);
		
		setModel(model);
	}
	
	/**
	 * Adds the specified MIDI instrument to this tree.
	 * @param instr The MIDI instrument to add.
	 */
	public void
	mapInstrument(MidiInstrument instr) {
		MidiBank bank = new MidiBank(instr.getInfo().getMidiBank());
		DefaultMutableTreeNode bankNode = findBank(bank);
		
		if(bankNode == null) {
			bankNode = new BankTreeNode(bank);
			
			model.insertNodeInto (
				bankNode,
				(DefaultMutableTreeNode)model.getRoot(),
				findBankPosition(bank.getId())
			);
		}
		
		model.insertNodeInto (
			new InstrTreeNode(instr),
			bankNode,
			removeProgram(bankNode, instr.getInfo().getMidiProgram())
		);
	}
	
	/**
	 * Removes the specified MIDI instrument from the tree.
	 * @param instr The MIDI instrument to remove.
	 * @throws IllegalArgumentException If the specified instrument is not found.
	 */
	public void
	unmapInstrument(MidiInstrument instr) {
		MidiBank bank = new MidiBank(instr.getInfo().getMidiBank());
		DefaultMutableTreeNode bankNode = findBank(bank);
		
		if(bankNode == null)
			throw new IllegalArgumentException("Missing MIDI bank: " + bank.getId());
		
		removeProgram(bankNode, instr.getInfo().getMidiProgram());
		if(bankNode.getChildCount() == 0) model.removeNodeFromParent(bankNode);
	}
	
	/**
	 * Gets the selected MIDI instrument.
	 * @return The selected MIDI instrument, or
	 * <code>null</code> if there is no MIDI instrument selected.
	 */
	public MidiInstrument
	getSelectedInstrument() {
		if(getSelectionCount() == 0) return null;
		Object obj = getSelectionPath().getLastPathComponent();
		if(!(obj instanceof InstrTreeNode)) return null;
		obj = ((InstrTreeNode)obj).getUserObject();
		return (MidiInstrument)obj;
	}
	
	/**
	 * Gets the selected MIDI bank.
	 * @return The selected MIDI bank, or
	 * <code>null</code> if there is no MIDI bank selected.
	 */
	public MidiBank
	getSelectedMidiBank() {
		if(getSelectionCount() == 0) return null;
		
		Object obj = getSelectionPath().getLastPathComponent();
		if(!(obj instanceof BankTreeNode)) return null;
		
		BankTreeNode n = (BankTreeNode)obj;
		return (MidiBank)n.getUserObject();
	}
	
	/**
	 * Removes (on the backend side) the selected MIDI instrument or MIDI bank.
	 */
	public void
	removeSelectedInstrumentOrBank() {
		if(getSelectionCount() == 0) return;
		
		Object obj = getSelectionPath().getLastPathComponent();
		if(obj instanceof InstrTreeNode) {
			obj = ((InstrTreeNode)obj).getUserObject();
			removeInstrument((MidiInstrument)obj);
		} else if(obj instanceof BankTreeNode) {
			BankTreeNode n = (BankTreeNode)obj;
			int c = n.getChildCount();
			if(c > 1) {
				String s;
				s = i18n.getMessage("JSMidiInstrumentTree.removeInstruments?", c);
				if(!SHF.showYesNoDialog(SHF.getMainFrame(), s)) return;
			}
			
			for(int i = c - 1; i >= 0; i--) {
				obj = ((InstrTreeNode)n.getChildAt(i)).getUserObject();
				removeInstrument((MidiInstrument)obj);
			}
		}
	}
	
	/**
	 * Removes (on the backend side) the specified MIDI instrument.
	 */
	private void
	removeInstrument(MidiInstrument instr) {
		MidiInstrumentInfo i = instr.getInfo();
		CC.getSamplerModel().unmapBackendMidiInstrument (
			i.getMapId(), i.getMidiBank(), i.getMidiProgram()
		);
	}
	
	/**
	 * Returns the position, where the specified bank should be inserted.
	 */
	private int
	findBankPosition(int bankID) {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
		
		for(int i = 0; i < root.getChildCount(); i++) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)root.getChildAt(i);
			MidiBank bank = (MidiBank)node.getUserObject();
			if(bank.getId() > bankID) return i;
		}
		
		return root.getChildCount();
	}
	
	/**
	 * If there is already an instrument with MIDI program <code>program</code>,
	 * those instrument is removed from the tree.
	 * @return The position, where the instrument of the specified program should be inserted.
	 */
	private int
	removeProgram(DefaultMutableTreeNode bankNode, int program) {
		
		for(int i = 0; i < bankNode.getChildCount(); i++) {
			DefaultMutableTreeNode n = (DefaultMutableTreeNode)bankNode.getChildAt(i);
			MidiInstrument instr = (MidiInstrument)n.getUserObject();
			
			if(instr.getInfo().getMidiProgram() == program) {
				model.removeNodeFromParent(n);
				return i;
			}
			
			if(instr.getInfo().getMidiProgram() > program) return i;
		}
		
		return bankNode.getChildCount();
	}
	
	private DefaultMutableTreeNode 
	findNode(DefaultMutableTreeNode parent, Object obj) {
		for(int i = 0; i < parent.getChildCount(); i++) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)parent.getChildAt(i);
			if(node.getUserObject().equals(obj)) return node;
		}
		
		return null;
	}
	
	private DefaultMutableTreeNode 
	findBank(Object obj) {
		return findNode((DefaultMutableTreeNode)model.getRoot(), obj);
	}
	
	private DefaultMutableTreeNode
	findInstrument(Object obj) {
		if(obj == null || !(obj instanceof MidiInstrument)) return null;
		MidiInstrument i = (MidiInstrument) obj;
		DefaultMutableTreeNode bank = findBank(new MidiBank(i.getInfo().getMidiBank()));
		if(bank == null) return null;
		return findNode(bank, obj);
	}
	
	
	private class MidiBank {
		private int id;
		
		MidiBank(int id) {
			this.id = id;
		}
		
		public int
		getId() { return id; }
		
		@Override
		public boolean
		equals(Object obj) {
			if(obj == null) return false;
			if(!(obj instanceof MidiBank)) return false;
			if(getId() == ((MidiBank)obj).getId()) return true;
			return false;
		}
		
		@Override
		public String
		toString() {
			int i = CC.getViewConfig().getFirstMidiBankNumber();
			return i18n.getLabel("JSMidiInstrumentTree.MidiBank.name", i + id);
		}
	}
	
	protected class BankTreeNode extends DefaultMutableTreeNode {
		BankTreeNode(Object obj) {
			super(obj);
		}
		
		@Override
		public void
		setUserObject(Object userObject) {
			if(userObject instanceof MidiBank) {
				super.setUserObject(userObject);
				return;
			}
			
			// If we are here, this means that tree editing occurs.
			CC.getLogger().info("MidiInstrumentTree: editing not supported");
		}
		
		@Override
		public boolean
		isLeaf() { return false; }
	}
	
	protected class InstrTreeNode extends DefaultMutableTreeNode {
		InstrTreeNode(Object obj) {
			super(obj);
		}
		
		@Override
		public void
		setUserObject(Object userObject) {
			if(userObject instanceof MidiInstrument) {
				super.setUserObject(userObject);
				return;
			}
			
			// If we are here, this means that tree editing occurs.
			CC.getLogger().info("MidiInstrumentTree: editing not supported");
		}
		
		@Override
		public boolean
		isLeaf() { return true; }
	}
		
	private void
	editSelectedInstrument() {
		MidiInstrument i = getSelectedInstrument();
		if(i == null) return;
		JSEditMidiInstrumentDlg dlg = new JSEditMidiInstrumentDlg(i.getInfo());
		dlg.setVisible(true);
		
		if(dlg.isCancelled()) return;
		
		MidiInstrumentInfo info = dlg.getInstrument();
		CC.getSamplerModel().mapBackendMidiInstrument (
			info.getMapId(), info.getMidiBank(), info.getMidiProgram(), info
		);
	}
	
	private void
	copyMidiBankTo() { copyOrMoveMidiBankTo(true); }
	
	private void
	moveMidiBankTo() { copyOrMoveMidiBankTo(false); }
	
	private void
	copyOrMoveMidiBankTo(boolean copy) {
		MidiBank bank = getSelectedMidiBank();
		if(bank == null) return;
		
		JSMidiBankChooser dlg = new JSMidiBankChooser();
		
		if(copy) dlg.setTitle(i18n.getLabel("JSMidiInstrumentTree.copyTo"));
		else dlg.setTitle(i18n.getLabel("JSMidiInstrumentTree.moveTo"));
		
		dlg.setSelectedMidiInstrumentMap(getMidiInstrumentMap());
		dlg.setVisible(true);
		if(dlg.isCancelled()) return;
		
		MidiInstrumentMap smap = dlg.getSelectedMidiInstrumentMap();
		
		if(smap == null) {
			SHF.showErrorMessage(i18n.getMessage("JSMidiInstrumentTree.noMap!"), this);
			return;
		}
		
		if(dlg.getMidiBank() == bank.getId() && smap.getMapId() == getMidiInstrumentMap().getMapId()) {
			String s = "JSMidiInstrumentTree.sameSourceAndDestination!";
			SHF.showErrorMessage(i18n.getMessage(s), this);
			return;
		}
		
		MidiInstrument[] instrs = getMidiInstrumentMap().getMidiInstruments(bank.getId());
		int mapId = smap.getMapId();
		int bnkId = dlg.getMidiBank();
		
		Vector<MidiInstrument> v = new Vector<MidiInstrument>();
		for(MidiInstrument i : instrs) {
			MidiInstrument instr;
			instr = smap.getMidiInstrument(bnkId, i.getInfo().getMidiProgram());
			if(instr != null) v.add(instr);
		}
		
		if(!v.isEmpty()) {
			String[] instrumentNames = new String[v.size()];
			for(int i = 0; i < v.size(); i++) {
				int base = CC.getViewConfig().getFirstMidiProgramNumber();
				int p = v.get(i).getInfo().getMidiProgram();
				instrumentNames[i] = (base + p) + ". " + v.get(i).getName();
			}
			JSOverrideInstrumentsConfirmDlg dlg2;
			dlg2 = new JSOverrideInstrumentsConfirmDlg(instrumentNames);
			dlg2.setVisible(true);
			if(dlg2.isCancelled()) return;
		}
		
		for(MidiInstrument i : instrs) {
			int p = i.getInfo().getMidiProgram();
			CC.getSamplerModel().mapBackendMidiInstrument(mapId, bnkId, p, i.getInfo());
		}
		
		if(copy) return;
		
		mapId = getMidiInstrumentMap().getMapId();
		bnkId = bank.getId();
		
		for(MidiInstrument i : instrs) {
			int p = i.getInfo().getMidiProgram();
			CC.getSamplerModel().unmapBackendMidiInstrument(mapId, bnkId, p);
		}
	}
	
	private void
	copyMidiInstrumentTo() { copyOrMoveMidiInstrumentTo(true); }
	
	private void
	moveMidiInstrumentTo() { copyOrMoveMidiInstrumentTo(false); }
	
	private void
	copyOrMoveMidiInstrumentTo(boolean copy) {
		MidiInstrument instr = getSelectedInstrument();
		if(instr == null) return;
		
		JSMidiBankChooser dlg = new JSMidiBankChooser();
		
		if(copy) dlg.setTitle(i18n.getLabel("JSMidiInstrumentTree.copyTo"));
		else dlg.setTitle(i18n.getLabel("JSMidiInstrumentTree.moveTo"));
		
		dlg.setSelectedMidiInstrumentMap(getMidiInstrumentMap());
		dlg.setVisible(true);
		if(dlg.isCancelled()) return;
		
		MidiInstrumentMap smap = dlg.getSelectedMidiInstrumentMap();
		
		if(smap == null) {
			SHF.showErrorMessage(i18n.getMessage("JSMidiInstrumentTree.noMap!"), this);
			return;
		}
		
		int bank = instr.getInfo().getMidiBank();
		if(dlg.getMidiBank() == bank && smap.getMapId() == getMidiInstrumentMap().getMapId()) {
			String s = "JSMidiInstrumentTree.sameSourceAndDestination!";
			SHF.showErrorMessage(i18n.getMessage(s), this);
			return;
		}
		
		int mapId = smap.getMapId();
		int bnkId = dlg.getMidiBank();
		int prgId = instr.getInfo().getMidiProgram();
		MidiInstrument oldInstr = smap.getMidiInstrument(bnkId, prgId);
		
		if(oldInstr != null) {
			String[] iS = new String [1];
			int base = CC.getViewConfig().getFirstMidiProgramNumber();
			iS[0] = (base + prgId) + ". " + oldInstr.getName();
			JSOverrideInstrumentsConfirmDlg dlg2;
			dlg2 = new JSOverrideInstrumentsConfirmDlg(iS);
			dlg2.setVisible(true);
			if(dlg2.isCancelled()) return;
		}
		
		CC.getSamplerModel().mapBackendMidiInstrument(mapId, bnkId, prgId, instr.getInfo());
		
		if(copy) return;
		
		mapId = getMidiInstrumentMap().getMapId();
		
		CC.getSamplerModel().unmapBackendMidiInstrument(mapId, bank, prgId);
	}
	
	private void
	moveSelectedInstrumentUp() {
		MidiInstrument instr = getSelectedInstrument();
		if(instr == null) return;
		moveSelectedInstrument(instr.getInfo().getMidiProgram() - 1);
	}
	
	private void
	moveSelectedInstrumentDown() {
		MidiInstrument instr = getSelectedInstrument();
		if(instr == null) return;
		moveSelectedInstrument(instr.getInfo().getMidiProgram() + 1);
	}
	
	private void
	moveSelectedInstrument(int newProgram) {
		if(newProgram < 0 || newProgram > 127) return;
		MidiInstrument instr = getSelectedInstrument();
		if(instr == null) return;
		
		int bnk = instr.getInfo().getMidiBank();
		int prg = instr.getInfo().getMidiProgram();
		
		MidiInstrument oldInstr = getMidiInstrumentMap().getMidiInstrument(bnk, newProgram);
		if(oldInstr != null) {
			String[] iS = new String [1];
			int base = CC.getViewConfig().getFirstMidiProgramNumber();
			iS[0] = (base + newProgram) + ". " + oldInstr.getName();
			JSOverrideInstrumentsConfirmDlg dlg;
			dlg = new JSOverrideInstrumentsConfirmDlg(iS);
			dlg.setVisible(true);
			if(dlg.isCancelled()) return;
		}
		
		int map = this.getMidiInstrumentMap().getMapId();
		CC.getSamplerModel().mapBackendMidiInstrument(map, bnk, newProgram, instr.getInfo());
		CC.getSamplerModel().unmapBackendMidiInstrument(map, bnk, prg);
	}
	
	private final EventHandler eventHandler = new EventHandler();
	
	private EventHandler
	getHandler() { return eventHandler; }
	
	private class EventHandler implements MidiInstrumentListener, MidiInstrumentMapListener,
					      TreeSelectionListener {
		
		/** Invoked when a MIDI instrument in a MIDI instrument map is changed. */
		@Override
		public void
		instrumentInfoChanged(MidiInstrumentEvent e) {
			DefaultMutableTreeNode n = findInstrument(e.getSource());
			if(n != null) model.nodeChanged(n);
		}
		
		/** Invoked when the name of MIDI instrument map is changed. */
		@Override
		public void nameChanged(MidiInstrumentMapEvent e) { }
		
		/** Invoked when an instrument is added to a MIDI instrument map. */
		@Override
		public void
		instrumentAdded(MidiInstrumentMapEvent e) {
			e.getInstrument().addMidiInstrumentListener(getHandler());
			mapInstrument(e.getInstrument());
			
		}
	
		/** Invoked when an instrument is removed from a MIDI instrument map. */
		@Override
		public void
		instrumentRemoved(MidiInstrumentMapEvent e) {
			unmapInstrument(e.getInstrument());
			e.getInstrument().removeMidiInstrumentListener(getHandler());
		}
		
		@Override
		public void
		valueChanged(TreeSelectionEvent e) {
			MidiInstrument instr = getSelectedInstrument();
			if(instr != null) {
				int p = instr.getInfo().getMidiProgram();
				contextMenu.miMoveInstrumentDown.setEnabled(p < 127);
				contextMenu.miMoveInstrumentUp.setEnabled(p > 0);
			}
		}
	}
	
	class ContextMenu extends MouseAdapter {
		private final JPopupMenu bankMenu = new JPopupMenu();
		
		private final JPopupMenu instrumentMenu = new JPopupMenu();
		
		private final JMenu changeProgramMenu =
			new JMenu(i18n.getMenuLabel("JSMidiInstrumentTree.ContextMenu.changeProgram"));
		
		private final JMenuItem miMoveInstrumentUp =
			new JMenuItem(i18n.getMenuLabel("JSMidiInstrumentTree.ContextMenu.moveUp"));
		
		private final JMenuItem miMoveInstrumentDown =
			new JMenuItem(i18n.getMenuLabel("JSMidiInstrumentTree.ContextMenu.moveDown"));
		
		private final JMenu programGroup1Menu = new JMenu();
		private final JMenu programGroup2Menu = new JMenu();
		private final JMenu programGroup3Menu = new JMenu();
		private final JMenu programGroup4Menu = new JMenu();
		private final JMenu programGroup5Menu = new JMenu();
		private final JMenu programGroup6Menu = new JMenu();
		private final JMenu programGroup7Menu = new JMenu();
		private final JMenu programGroup8Menu = new JMenu();
		
		
		ContextMenu() {
			JMenuItem mi = new JMenuItem(i18n.getMenuLabel("JSMidiInstrumentTree.ContextMenu.moveTo"));
			bankMenu.add(mi);
			mi.addActionListener(new ActionListener() {
				public void
				actionPerformed(ActionEvent e) {
					moveMidiBankTo();
				}
			});
			
			mi = new JMenuItem(i18n.getMenuLabel("JSMidiInstrumentTree.ContextMenu.copyTo"));
			bankMenu.add(mi);
			mi.addActionListener(new ActionListener() {
				public void
				actionPerformed(ActionEvent e) {
					copyMidiBankTo();
				}
			});
			
			bankMenu.addSeparator();
			
			mi = new JMenuItem(i18n.getMenuLabel("ContextMenu.delete"));
			bankMenu.add(mi);
			mi.addActionListener(new ActionListener() {
				public void
				actionPerformed(ActionEvent e) {
					removeSelectedInstrumentOrBank();
				}
			});
			
			// MIDI Instrument Menu
			
			mi = new JMenuItem(i18n.getMenuLabel("ContextMenu.edit"));
			instrumentMenu.add(mi);
			mi.addActionListener(new ActionListener() {
				public void
				actionPerformed(ActionEvent e) {
					editSelectedInstrument();
				}
			});
			
			instrumentMenu.addSeparator();
			
			mi = new JMenuItem(i18n.getMenuLabel("JSMidiInstrumentTree.ContextMenu.moveTo"));
			instrumentMenu.add(mi);
			mi.addActionListener(new ActionListener() {
				public void
				actionPerformed(ActionEvent e) {
					moveMidiInstrumentTo();
				}
			});
			
			mi = new JMenuItem(i18n.getMenuLabel("JSMidiInstrumentTree.ContextMenu.copyTo"));
			instrumentMenu.add(mi);
			mi.addActionListener(new ActionListener() {
				public void
				actionPerformed(ActionEvent e) {
					copyMidiInstrumentTo();
				}
			});
			
			instrumentMenu.add(changeProgramMenu);
			
			changeProgramMenu.add(miMoveInstrumentUp);
			miMoveInstrumentUp.addActionListener(new ActionListener() {
				public void
				actionPerformed(ActionEvent e) {
					moveSelectedInstrumentUp();
				}
			});
			
			changeProgramMenu.add(miMoveInstrumentDown);
			miMoveInstrumentDown.addActionListener(new ActionListener() {
				public void
				actionPerformed(ActionEvent e) {
					moveSelectedInstrumentDown();
				}
			});
			
			changeProgramMenu.addSeparator();
			
			changeProgramMenu.add(programGroup1Menu);
			addProgramMenuItems(programGroup1Menu, 0, 15);
			changeProgramMenu.add(programGroup2Menu);
			addProgramMenuItems(programGroup2Menu, 16, 31);
			changeProgramMenu.add(programGroup3Menu);
			addProgramMenuItems(programGroup3Menu, 32, 47);
			changeProgramMenu.add(programGroup4Menu);
			addProgramMenuItems(programGroup4Menu, 48, 63);
			changeProgramMenu.add(programGroup5Menu);
			addProgramMenuItems(programGroup5Menu, 64, 79);
			changeProgramMenu.add(programGroup6Menu);
			addProgramMenuItems(programGroup6Menu, 80, 95);
			changeProgramMenu.add(programGroup7Menu);
			addProgramMenuItems(programGroup7Menu, 96, 111);
			changeProgramMenu.add(programGroup8Menu);
			addProgramMenuItems(programGroup8Menu, 112, 127);
			
			instrumentMenu.addSeparator();
			updateChangeProgramMenu();
			
			mi = new JMenuItem(i18n.getMenuLabel("ContextMenu.delete"));
			instrumentMenu.add(mi);
			mi.addActionListener(new ActionListener() {
				public void
				actionPerformed(ActionEvent e) {
					removeSelectedInstrumentOrBank();
				}
			});
			
		}
		
		private void
		addProgramMenuItems(JMenu menu, int prgStart, int prgEnd) {
			for(int i = prgStart; i <= prgEnd; i++) {
				menu.add(new ProgramMenuItem(i));
			}
		}
		
		private void
		updateChangeProgramMenu() {
			updateProgramGroupMenu(programGroup1Menu, 0, 15);
			updateProgramGroupMenu(programGroup2Menu, 16, 31);
			updateProgramGroupMenu(programGroup3Menu, 32, 47);
			updateProgramGroupMenu(programGroup4Menu, 48, 63);
			updateProgramGroupMenu(programGroup5Menu, 64, 79);
			updateProgramGroupMenu(programGroup6Menu, 80, 95);
			updateProgramGroupMenu(programGroup7Menu, 96, 111);
			updateProgramGroupMenu(programGroup8Menu, 112, 127);
		}
		
		private void
		updateProgramGroupMenu(JMenu menu, int prgStart, int prgEnd) {
			int base = CC.getViewConfig().getFirstMidiProgramNumber();
			String s = "JSMidiInstrumentTree.ContextMenu.programGroup";
			String grp = "(" + (base + prgStart) + "-" + (base + prgEnd) + ")";
			menu.setText(i18n.getMenuLabel(s, grp));
			
			updateProgramGroupMenuItems(menu);
		}
		
		private void
		updateProgramGroupMenuItems(JMenu menu) {
			for(int i = 0; i < menu.getItemCount(); i++) {
				((ProgramMenuItem)menu.getItem(i)).updateProgramNumber();
			}
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
			if(getSelectionCount() == 0) return;
			
			if(getSelectedInstrument() != null) {
				instrumentMenu.show(e.getComponent(), e.getX(), e.getY());
			} else {
				bankMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
		
		private class ProgramMenuItem extends JMenuItem implements ActionListener {
			int program;
			
			ProgramMenuItem(int program) {
				this.program = program;
				updateProgramNumber();
				addActionListener(this);
			}
			
			public void
			updateProgramNumber() {
				int base = CC.getViewConfig().getFirstMidiProgramNumber();
				setText(String.valueOf(base + program));
			}
			
			@Override
			public void
			actionPerformed(ActionEvent e) {
				moveSelectedInstrument(program);
			}
		}
	}
}
	
class JSOverrideInstrumentsConfirmDlg extends OkCancelDialog {
	private final JLabel lMsg = new JLabel(i18n.getMessage("JSOverrideInstrumentsConfirmDlg.lMsg"));
	private final JTable table;
	
	JSOverrideInstrumentsConfirmDlg(String[] instrumentNames) {
		super(SHF.getMainFrame());
		
		JPanel mainPane = new JPanel();
		mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
		
		lMsg.setIcon(SHF.getViewConfig().getBasicIconSet().getWarning32Icon());
		lMsg.setAlignmentX(LEFT_ALIGNMENT);
		mainPane.add(lMsg);
		
		mainPane.add(Box.createRigidArea(new Dimension(0, 12)));
		
		String[][] instrs = new String[instrumentNames.length][1];
		for(int i = 0; i < instrumentNames.length; i++) {
			instrs[i][0] = instrumentNames[i];
		}
		
		String[] columns = new String[1];
		columns[0] = "";
		
		table = new JTable(instrs, columns);
		JScrollPane sp = new JScrollPane(table);
		Dimension d = new Dimension(200, 200);
		sp.setMinimumSize(d);
		sp.setPreferredSize(d);
		sp.setAlignmentX(LEFT_ALIGNMENT);
		mainPane.add(sp);
		
		setMainPane(mainPane);
		setMinimumSize(getPreferredSize());
		setResizable(true);
	}
	
	@Override
	protected void
	onOk() {
		if(!btnOk.isEnabled()) return;
		
		setVisible(false);
		setCancelled(false);
	}
	
	@Override
	protected void
	onCancel() { setVisible(false); }
}
