/*
 *   JSampler - a front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2023 Grigor Iliev <grigor@grigoriliev.com>
 *
 *   This file is part of JSampler.
 *
 *   JSampler is free software: you can redistribute it and/or modify it under
 *   the terms of the GNU General Public License as published by the Free
 *   Software Foundation, either version 3 of the License, or (at your option)
 *   any later version.
 *
 *   JSampler is distributed in the hope that it will be useful, but WITHOUT
 *   ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *   FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 *   more details.
 *
 *   You should have received a copy of the GNU General Public License along
 *   with JSampler. If not, see <https://www.gnu.org/licenses/>.
 */

package com.grigoriliev.jsampler.fantasia.view;

import java.awt.BorderLayout;
import java.awt.Dimension;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;

import com.grigoriliev.jsampler.swing.view.InstrumentsDbTreeModel;
import com.grigoriliev.jsampler.swing.view.SHF;
import com.grigoriliev.jsampler.swing.view.std.JSInstrumentsDbColumnPreferencesDlg;
import com.grigoriliev.jsampler.swing.view.std.JSInstrumentsDbTable;


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
		if(SHF.getInstrumentsDbTreeModel() != null) {
			instrumentsDbTree = new FantasiaInstrumentsDbTree(SHF.getInstrumentsDbTreeModel());
		} else {
			instrumentsDbTree = new FantasiaInstrumentsDbTree(new InstrumentsDbTreeModel(true));
		}
		
		instrumentsTable = new JSInstrumentsDbTable(instrumentsDbTree, "InstrumentsDbPane.");
		instrumentsTable.getModel().setShowDummyColumn(true);
		instrumentsTable.loadColumnsVisibleState();
		instrumentsTable.loadColumnWidths();
		instrumentsTable.loadSortOrder();
		
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
		
		splitPane.setResizeWeight(0.4);
		
		add(splitPane);
		add(new ToolBar(), BorderLayout.NORTH);
		
		int i = FantasiaPrefs.preferences().getIntProperty("InstrumentsDbPane.splitDividerLocation", 160);
		splitPane.setDividerLocation(i);
	}
	
	protected void
	savePreferences() {
		instrumentsTable.saveColumnsVisibleState();
		instrumentsTable.saveColumnWidths();
		
		int i = splitPane.getDividerLocation();
		FantasiaPrefs.preferences().setIntProperty("InstrumentsDbPane.splitDividerLocation", i);
	}
	
	class ToolBar extends JToolBar {
		protected final JButton btnGoUp = new ToolbarButton(instrumentsDbTree.actionGoUp);
		protected final JButton btnGoBack = new ToolbarButton(instrumentsDbTree.actionGoBack);
		protected final JButton btnGoForward = new ToolbarButton(instrumentsDbTree.actionGoForward);
		protected final JButton btnReload = new ToolbarButton(instrumentsTable.reloadAction);
		protected final JButton btnPreferences = new ToolbarButton(null);
		
		public ToolBar() {
			super("");
			setFloatable(false);
			
			add(btnGoBack);
			add(btnGoForward);
			add(btnGoUp);
			
			instrumentsTable.reloadAction.putValue(Action.SMALL_ICON, Res.iconReload16);
			add(btnReload);
			
			addSeparator();
			
			btnPreferences.setIcon(Res.iconPreferences16);
			add(btnPreferences);
			
			btnPreferences.addActionListener(new ActionListener() {
				public void
				actionPerformed(ActionEvent e) {
					new PreferencesDlg().setVisible(true);
				}
			});
		}
	}
	
	class PreferencesDlg extends JSInstrumentsDbColumnPreferencesDlg {
		PreferencesDlg() {
			super(SHF.getMainFrame(), instrumentsTable);
		}
	}
}
