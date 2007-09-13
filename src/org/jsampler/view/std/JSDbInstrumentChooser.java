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

package org.jsampler.view.std;

import java.awt.Dialog;
import java.awt.Frame;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sf.juife.OkCancelDialog;

import org.jsampler.CC;

import org.jsampler.view.DbDirectoryTreeNode;
import org.jsampler.view.InstrumentsDbTreeModel;

import org.linuxsampler.lscp.DbInstrumentInfo;
import org.linuxsampler.lscp.Parser;

import static org.jsampler.view.std.StdI18n.i18n;


/**
 *
 * @author Grigor Iliev
 */
public class JSDbInstrumentChooser extends OkCancelDialog implements ListSelectionListener {
	protected JSInstrumentsDbTree instrumentsDbTree;
	protected JSInstrumentsDbTable instrumentsDbTable;
	
	/**
	 * Creates a new instance of <code>JSDbInstrumentChooser</code>
	 */
	public JSDbInstrumentChooser(Frame owner) {
		super(owner, i18n.getLabel("JSDbInstrumentChooser.title"));
		initDbInstrumentChooser();
	}
	
	/**
	 * Creates a new instance of <code>JSDbInstrumentChooser</code>
	 */
	public JSDbInstrumentChooser(Dialog owner) {
		super(owner, i18n.getLabel("JSDbInstrumentChooser.title"));
		initDbInstrumentChooser();
	}
	
	private void
	initDbInstrumentChooser() {
		btnOk.setEnabled(false);
		instrumentsDbTree = new JSInstrumentsDbTree(CC.getInstrumentsDbTreeModel());
		JScrollPane sp = new JScrollPane(instrumentsDbTree);
		
		instrumentsDbTable = new JSInstrumentsDbTable(instrumentsDbTree);
		instrumentsDbTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		instrumentsDbTable.getSelectionModel().addListSelectionListener(this);
		instrumentsDbTable.loadColumnWidths();
		JScrollPane sp2 = new JScrollPane(instrumentsDbTable);
		
		JSplitPane splitPane = new JSplitPane (
			JSplitPane.HORIZONTAL_SPLIT,
			true,	// continuousLayout 
			sp,
			sp2
		);
		
		splitPane.setDividerSize(3);
		splitPane.setDividerLocation(200);
		
		setMainPane(splitPane);
		
		instrumentsDbTable.addMouseListener(new MouseAdapter() {
			public void
			mouseClicked(MouseEvent e) {
				if(e.getButton() != e.BUTTON1 || e.getClickCount() < 2) return;
				DbInstrumentInfo infos[];
				infos = instrumentsDbTable.getSelectedInstruments();
				if(infos.length == 0) return;
				onOk();
			}
		});
		
		installKeyboardListeners();
	}
	
	private void
	installKeyboardListeners() {
		getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put (
			KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0),
			"goUp"
		);
		
		getRootPane().getActionMap().put ("goUp", new GoUp());
		
		instrumentsDbTable.getInputMap().put (
			KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
			"Select"
		);
		
		instrumentsDbTable.getActionMap().put ("Select", new AbstractAction() {
			public void
			actionPerformed(ActionEvent e) {
				DbDirectoryTreeNode node;
				node = instrumentsDbTable.getSelectedDirectoryNode();
				if(node != null) {
					instrumentsDbTree.setSelectedDirectoryNode(node);
					return;
				}
				
				if(instrumentsDbTable.getSelectedInstruments().length > 0) {
					onOk();
				}
				
			}
		});
	}
	
	public String
	getSelectedInstrument() {
		DbInstrumentInfo[] infos = instrumentsDbTable.getSelectedInstruments();
		if(infos.length == 0) return null;
		return infos[0].getInstrumentPath();
	}
	
	public void
	setSelectedInstrument(String instr) {
		String parentDir = Parser.getParentDirectory(instr);
		if(parentDir == null) return;
		instrumentsDbTree.setSelectedDirectory(parentDir);
		if(instrumentsDbTable.getParentDirectoryNode() == null) return;
		
		instr = instr.substring(parentDir.length() + 1, instr.length());
		instrumentsDbTable.setSelectedInstrument(instr);
	}
	
	public void
	setSelectedDirectory(String dir) {
		instrumentsDbTree.setSelectedDirectory(dir);
	}
	
	protected void
	onOk() {
		if(!btnOk.isEnabled()) return;
		setCancelled(false);
		setVisible(false);
	}
	
	protected void
	onCancel() { setVisible(false); }
	
	public void
	valueChanged(ListSelectionEvent e) {
		boolean b = instrumentsDbTable.getSelectedInstruments().length > 0;
		btnOk.setEnabled(b);
	}
	
	private class GoUp extends AbstractAction {
		GoUp() { }
		
		public void
		actionPerformed(ActionEvent e) {
			DbDirectoryTreeNode node = instrumentsDbTree.getSelectedDirectoryNode();
			if(node == null) return;
			if(node.getParent() == null) return;
			instrumentsDbTree.setSelectedDirectoryNode(node.getParent());
		}
	}
}
