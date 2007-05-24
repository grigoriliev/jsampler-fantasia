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

import java.awt.Dialog;
import java.awt.Frame;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sf.juife.OkCancelDialog;

import org.jsampler.CC;

import org.linuxsampler.lscp.DbInstrumentInfo;

import org.jsampler.view.InstrumentsDbTreeModel;

import static org.jsampler.view.classic.ClassicI18n.i18n;


/**
 *
 * @author Grigor Iliev
 */
public class DbInstrumentChooser extends OkCancelDialog implements ListSelectionListener {
	private InstrumentsDbTree instrumentsDbTree;
	private InstrumentsDbTable instrumentsDbTable;
	
	/** Creates a new instance of <code>DbInstrumentChooser</code> */
	public DbInstrumentChooser(Frame owner) {
		super(owner, i18n.getLabel("DbInstrumentChooser.title"));
		initDbInstrumentChooser();
	}
	
	/** Creates a new instance of <code>DbInstrumentChooser</code> */
	public DbInstrumentChooser(Dialog owner) {
		super(owner, i18n.getLabel("DbInstrumentChooser.title"));
		initDbInstrumentChooser();
	}
	
	private void
	initDbInstrumentChooser() {
		btnOk.setEnabled(false);
		instrumentsDbTree = new InstrumentsDbTree(CC.getInstrumentsDbTreeModel());
		JScrollPane sp = new JScrollPane(instrumentsDbTree);
		
		instrumentsDbTable = new InstrumentsDbTable(instrumentsDbTree);
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
	}
	
	public String
	getSelectedInstrument() {
		DbInstrumentInfo[] infos = instrumentsDbTable.getSelectedInstruments();
		if(infos.length == 0) return null;
		return infos[0].getInstrumentPath();
	}
	
	public void
	setSelectedInstrument(String instr) {
		String parentDir = InstrumentsDbTreeModel.getParentDirectory(instr);
		if(parentDir == null) return;
		instrumentsDbTree.setSelectedDirectory(parentDir);
		if(instrumentsDbTable.getParentDirectoryNode() == null) return;
		
		instr = instr.substring(parentDir.length() + 1, instr.length());
		instrumentsDbTable.setSelectedInstrument(instr);
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
}
