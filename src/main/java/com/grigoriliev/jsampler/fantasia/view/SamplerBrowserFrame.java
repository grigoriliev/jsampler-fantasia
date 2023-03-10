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

import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import com.grigoriliev.jsampler.CC;
import com.grigoriliev.jsampler.swing.view.SamplerTreeModel;
import com.grigoriliev.jsampler.swing.view.SamplerTreeModel.TreeNodeBase;
import com.grigoriliev.jsampler.swing.view.std.JSFrame;
import com.grigoriliev.jsampler.swing.view.std.JSSamplerTable;

/**
 *
 * @author Grigor Iliev
 */
public class SamplerBrowserFrame extends JSFrame {
	private final JMenuBar menuBar = new JMenuBar();
	
	private final SidePane sidePane;
	private final JSplitPane splitPane;
	private final MainPane mainPane;
	
	private final FantasiaSamplerTree samplerTree = new FantasiaSamplerTree(new SamplerTreeModel());
	private final JSSamplerTable samplerTable = new JSSamplerTable(samplerTree);
	
	/**
	 * Creates a new instance of <code>InstrumentsDbFrame</code>
	 */
	public
	SamplerBrowserFrame() {
		super(FantasiaI18n.i18n.getLabel("SamplerBrowserFrame.title"), "SamplerBrowserFrame");
		if(Res.iconAppIcon != null) setIconImage(Res.iconAppIcon.getImage());

		((ViewConfig)CC.getViewConfig()).restoreMenuProperties();
		
		sidePane = new SidePane();
		mainPane = new MainPane();
		
		splitPane = new JSplitPane (
			JSplitPane.HORIZONTAL_SPLIT,
			true,	// continuousLayout 
			sidePane,
			mainPane
		);
		
		splitPane.setDividerSize(3);
		splitPane.setDividerLocation(200);
		
		// fix for moving the menu bar on top of the screen
		// when running on Mac OS and third party plugin is used
		((ViewConfig)CC.getViewConfig()).setNativeMenuProperties();

		addMenu();
		
		getContentPane().add(splitPane);
	}
	
	
	
	private void
	addMenu() { }
	
	class MainPane extends JPanel implements TreeSelectionListener {
		MainPane() {
			setLayout(new BorderLayout());
			add(new JScrollPane(samplerTable));
			
			samplerTree.getSelectionModel().addTreeSelectionListener(this);
			TreePath path = samplerTree.getSelectionPath();
			if(path != null) samplerTable.setNode((TreeNodeBase)path.getLastPathComponent());
		}
		
		public void
		valueChanged(TreeSelectionEvent e) {
			if(e.getNewLeadSelectionPath() == null) {
				samplerTable.setNode(null);
				return;
			}
			
			samplerTable.setNode((TreeNodeBase)e.getNewLeadSelectionPath().getLastPathComponent());
		}
	}
	
	
	class SidePane extends JPanel {
		SidePane() {
			setLayout(new BorderLayout());
			add(new JScrollPane(samplerTree));
		}
	}
}
