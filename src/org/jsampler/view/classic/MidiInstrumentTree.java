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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import org.jsampler.CC;
import org.jsampler.HF;
import org.jsampler.MidiInstrument;
import org.jsampler.MidiInstrumentMap;

import org.jsampler.event.MidiInstrumentEvent;
import org.jsampler.event.MidiInstrumentListener;
import org.jsampler.event.MidiInstrumentMapEvent;
import org.jsampler.event.MidiInstrumentMapListener;

import org.linuxsampler.lscp.MidiInstrumentInfo;
import org.linuxsampler.lscp.MidiInstrumentMapInfo;

import static org.jsampler.view.classic.ClassicI18n.i18n;


/**
 *
 * @author Grigor Iliev
 */
public class MidiInstrumentTree extends JTree {
	private DefaultTreeModel model;
	private MidiInstrumentMap midiInstrumentMap;
	
	/** Creates a new instance of <code>MidiInstrumentTree</code> */
	public
	MidiInstrumentTree() {
		setRootVisible(false);
		setShowsRootHandles(true);
		setEditable(false);
		
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		renderer.setClosedIcon(Res.iconFolder16);
		renderer.setOpenIcon(Res.iconFolderOpen16);
		renderer.setLeafIcon(Res.iconInstrument16);
		
		setCellRenderer(renderer);
		
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
		ContextMenu contextMenu = new ContextMenu();
		addMouseListener(contextMenu);
		addTreeSelectionListener(contextMenu);
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
				findBankPosition(bank.getID())
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
			throw new IllegalArgumentException("Missing MIDI bank: " + bank.getID());
		
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
				s = i18n.getMessage("MidiInstrumentTree.removeInstruments?", c);
				if(!HF.showYesNoDialog(CC.getMainFrame(), s)) return;
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
			if(bank.getID() > bankID) return i;
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
		int id;
		
		MidiBank(int id) {
			this.id = id;
		}
		
		public int
		getID() { return id; }
		
		public boolean
		equals(Object obj) {
			if(obj == null) return false;
			if(!(obj instanceof MidiBank)) return false;
			if(getID() == ((MidiBank)obj).getID()) return true;
			return false;
		}
		
		public String
		toString() { return i18n.getLabel("MidiInstrumentTree.MidiBank.name", id); }
	}
	
	protected class BankTreeNode extends DefaultMutableTreeNode {
		BankTreeNode(Object obj) {
			super(obj);
		}
		
		public void
		setUserObject(Object userObject) {
			if(userObject instanceof MidiBank) {
				super.setUserObject(userObject);
				return;
			}
			
			// If we are here, this means that tree editing occurs.
			CC.getLogger().info("MidiInstrumentTree: editing not supported");
		}
		
		public boolean
		isLeaf() { return false; }
	}
	
	protected class InstrTreeNode extends DefaultMutableTreeNode {
		InstrTreeNode(Object obj) {
			super(obj);
		}
		
		public void
		setUserObject(Object userObject) {
			if(userObject instanceof MidiInstrument) {
				super.setUserObject(userObject);
				return;
			}
			
			// If we are here, this means that tree editing occurs.
			CC.getLogger().info("MidiInstrumentTree: editing not supported");
		}
		
		public boolean
		isLeaf() { return true; }
	}
		
	private void
	editSelectedInstrument() {
		MidiInstrument i = getSelectedInstrument();
		if(i == null) return;
		EditMidiInstrumentDlg dlg = new EditMidiInstrumentDlg(i.getInfo());
		dlg.setVisible(true);
		
		if(dlg.isCancelled()) return;
		
		MidiInstrumentInfo info = dlg.getInstrument();
		CC.getSamplerModel().mapBackendMidiInstrument (
			info.getMapId(), info.getMidiBank(), info.getMidiProgram(), info
		);
	}
	
	private final EventHandler eventHandler = new EventHandler();
	
	private EventHandler
	getHandler() { return eventHandler; }
	
	private class EventHandler implements MidiInstrumentListener, MidiInstrumentMapListener {
		
		/** Invoked when a MIDI instrument in a MIDI instrument map is changed. */
		public void
		instrumentInfoChanged(MidiInstrumentEvent e) {
			DefaultMutableTreeNode n = findInstrument(e.getSource());
			if(n != null) model.nodeChanged(n);
		}
		
		/** Invoked when the name of MIDI instrument map is changed. */
		public void nameChanged(MidiInstrumentMapEvent e) { }
		
		/** Invoked when an instrument is added to a MIDI instrument map. */
		public void
		instrumentAdded(MidiInstrumentMapEvent e) {
			e.getInstrument().addMidiInstrumentListener(getHandler());
			mapInstrument(e.getInstrument());
			
		}
	
		/** Invoked when an instrument is removed from a MIDI instrument map. */
		public void
		instrumentRemoved(MidiInstrumentMapEvent e) {
			unmapInstrument(e.getInstrument());
			e.getInstrument().removeMidiInstrumentListener(getHandler());
		}
	}
	
	class ContextMenu extends MouseAdapter implements TreeSelectionListener {
		private final JPopupMenu cmenu = new JPopupMenu();
		JMenuItem miEdit = new JMenuItem(i18n.getMenuLabel("ContextMenu.edit"));
		
		ContextMenu() {
			cmenu.add(miEdit);
			miEdit.addActionListener(new ActionListener() {
				public void
				actionPerformed(ActionEvent e) {
					editSelectedInstrument();
				}
			});
			
			JMenuItem mi = new JMenuItem(i18n.getMenuLabel("ContextMenu.delete"));
			cmenu.add(mi);
			mi.addActionListener(new ActionListener() {
				public void
				actionPerformed(ActionEvent e) {
					removeSelectedInstrumentOrBank();
				}
			});
			
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
			cmenu.show(e.getComponent(), e.getX(), e.getY());
		}
		
		public void
		valueChanged(TreeSelectionEvent e) {
			miEdit.setVisible(getSelectedInstrument() != null);
		}
	}
}
