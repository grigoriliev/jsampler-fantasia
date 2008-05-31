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

package org.jsampler.view.fantasia;

import java.awt.BorderLayout;
import java.awt.Dimension;

import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jsampler.CC;

import org.jsampler.view.InstrumentsDbTreeModel;
import org.jsampler.view.std.JSInstrumentsDbTable;

import org.jvnet.substance.SubstanceLookAndFeel;

import static org.jsampler.view.fantasia.FantasiaPrefs.preferences;


/**
 *
 * @author Grigor Iliev
 */
public class InstrumentsDbPane extends JPanel {
	private final FantasiaInstrumentsDbTree instrumentsDbTree;
	private final JSInstrumentsDbTable instrumentsTable;
	private final JSplitPane splitPane;
	
	/** Creates a new instance of <code>InstrumentsDbPane</code> */
	public
	InstrumentsDbPane() {
		setLayout(new BorderLayout());
		if(CC.getInstrumentsDbTreeModel() != null) {
			instrumentsDbTree = new FantasiaInstrumentsDbTree(CC.getInstrumentsDbTreeModel());
		} else {
			instrumentsDbTree = new FantasiaInstrumentsDbTree(new InstrumentsDbTreeModel(true));
		}
		
		instrumentsTable = new JSInstrumentsDbTable(instrumentsDbTree);
		instrumentsTable.getModel().showNameColumnOnly();
		instrumentsTable.getModel().setShowDummyColumn(true);
		instrumentsTable.loadColumnWidths("InstrumentsDbPane.");
		instrumentsTable.getRowSorter().toggleSortOrder(0);
		boolean b = preferences().getBoolProperty("InstrumentsDbPane.reverseSortOrder");
		if(b) instrumentsTable.getRowSorter().toggleSortOrder(0);
		
		CC.addInstrumentsDbChangeListener(new ChangeListener() {
			public void
			stateChanged(ChangeEvent e) {
				instrumentsDbTree.setModel(CC.getInstrumentsDbTreeModel());
				
				CC.scheduleInTaskQueue(new Runnable() {
					public void
					run() { instrumentsDbTree.setSelectedDirectory("/"); }
				});
			}
		});
		
		instrumentsDbTree.setSelectedDirectory("/");
		
		JScrollPane sp1 = new JScrollPane(instrumentsDbTree);
		sp1.setPreferredSize(new Dimension(200, 200));
		JScrollPane sp2 = new JScrollPane(instrumentsTable);
		sp2.setPreferredSize(new Dimension(200, 200));
		sp2.setOpaque(false);
		sp2.getViewport().setOpaque(false);
		
		splitPane = new JSplitPane (
			JSplitPane.VERTICAL_SPLIT,
			true,	// continuousLayout 
			sp1,
			sp2
		);
		
		add(splitPane);
	}
	
	protected void
	savePreferences() {
		instrumentsTable.saveColumnWidths("InstrumentsDbPane.");
		
		List<? extends SortKey> list = instrumentsTable.getRowSorter().getSortKeys();
		if(list.isEmpty()) return;
		SortKey k = list.get(0);
		if(k.getColumn() != 0) return;
		boolean b = k.getSortOrder() == SortOrder.DESCENDING;
		preferences().setBoolProperty("InstrumentsDbPane.reverseSortOrder", b);
	}
}
