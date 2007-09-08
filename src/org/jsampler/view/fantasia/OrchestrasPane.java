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

package org.jsampler.view.fantasia;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import org.jsampler.CC;
import org.jsampler.DefaultOrchestraModel;
import org.jsampler.OrchestraModel;

import org.jsampler.event.OrchestraAdapter;
import org.jsampler.event.OrchestraEvent;
import org.jsampler.event.ListEvent;
import org.jsampler.event.ListListener;

import org.jsampler.view.InstrumentTable;
import org.jsampler.view.InstrumentTableModel;

import org.jsampler.view.std.JSManageOrchestrasPane;
import org.jsampler.view.std.JSOrchestraPane;

import static org.jsampler.view.fantasia.FantasiaI18n.i18n;
import static org.jsampler.view.fantasia.FantasiaPrefs.*;


/**
 *
 * @author Grigor Iliev
 */
public class OrchestrasPane extends JPanel {
	private final JPanel taskPaneContainer = new JPanel();
	private final TaskPane orchestrasTaskPane = new TaskPane();
	
	private ManageOrchestrasPane manageOrchestrasPane = new ManageOrchestrasPane();
	
	private final JComboBox cbOrchestras = new JComboBox();
	private final OrchestraPane orchestraPane = new OrchestraPane();
	
	/**
	 * Because the orchestras are added after the view is created,
	 * we need to remember the orchestra used in the previous session.
	 */
	private int orchIdx;
	
	/** Creates a new instance of <code>OrchestrasPane</code> */
	public
	OrchestrasPane() {
		setLayout(new BorderLayout());
		setOpaque(false);
		
		orchestrasTaskPane.setTitle(i18n.getLabel("OrchestrasPane.orchestrasTaskPane"));
		orchestrasTaskPane.add(manageOrchestrasPane);
		boolean b;
		orchestrasTaskPane.setAnimated(preferences().getBoolProperty(ANIMATED));
		b = preferences().getBoolProperty("OrchestrasPane.orchestrasTaskPane.expanded");
		orchestrasTaskPane.setExpanded(b);
		
		preferences().addPropertyChangeListener(ANIMATED, new PropertyChangeListener() {
			public void
			propertyChange(PropertyChangeEvent e) {
				boolean b = preferences().getBoolProperty(ANIMATED);
				orchestrasTaskPane.setAnimated(b);
			}
		});
		
		taskPaneContainer.setOpaque(false);
		taskPaneContainer.setLayout(new BorderLayout());
		taskPaneContainer.add(orchestrasTaskPane);
		taskPaneContainer.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		add(taskPaneContainer, BorderLayout.NORTH);
		add(new InstrumentsPane());
		
		orchIdx = preferences().getIntProperty("OrchestrasPane.OrchestraIndex", 0);
		
		cbOrchestras.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { orchestraChanged(); }
		});
		
		for(int i = 0; i < CC.getOrchestras().getOrchestraCount(); i++) {
			cbOrchestras.addItem(CC.getOrchestras().getOrchestra(i));
		}
		
		CC.getOrchestras().addOrchestraListListener(getHandler());
		cbOrchestras.setEnabled(cbOrchestras.getItemCount() != 0);
		
		
		if(CC.getOrchestras().getOrchestraCount() > orchIdx) {
			cbOrchestras.setSelectedIndex(orchIdx);
			orchIdx = -1;
		}
	}
	
	public void
	savePreferences() {
		boolean b = orchestrasTaskPane.isExpanded();
		preferences().setBoolProperty("OrchestrasPane.orchestrasTaskPane.expanded", b);
	}
	
	private void
	orchestraChanged() {
		OrchestraModel om = (OrchestraModel)cbOrchestras.getSelectedItem();
		orchestraPane.setOrchestra(om);
		
		if(om != null) {
			String s = om.getDescription();
			if(s != null && s.length() == 0) s = null;
			cbOrchestras.setToolTipText(s);
		}
		
		int i = cbOrchestras.getSelectedIndex();
		if(i >= 0) preferences().setIntProperty("OrchestrasPane.OrchestraIndex", i);
	}
	
	class InstrumentsPane extends JPanel {
		InstrumentsPane() {
			setOpaque(false);
			setLayout(new BorderLayout());
			setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			
			PixmapPane p = new PixmapPane(Res.gfxChannelOptions);
			p.setPixmapInsets(new Insets(1, 1, 1, 1));
			p.setLayout(new BorderLayout());
			p.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			
			PixmapPane p2 = new PixmapPane(Res.gfxRoundBg7);
			p2.setPixmapInsets(new Insets(3, 3, 3, 3));
			p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));
			p2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			p2.add(cbOrchestras);
			p2.add(Box.createRigidArea(new Dimension(0, 5)));
			p2.add(orchestraPane);
			
			add(p2);
		}
	}
	
	static class ToolBar extends JToolBar {
		private static Insets pixmapInsets = new Insets(1, 1, 1, 1);
		
		ToolBar() {
			setFloatable(false);
			setOpaque(false);
			setPreferredSize(new Dimension(77, 29));
			setMinimumSize(getPreferredSize());
		}
		
		protected void
		paintComponent(Graphics g) {
			super.paintComponent(g);
			
			PixmapPane.paintComponent(this, g, Res.gfxCreateChannel, pixmapInsets);
		}
	}
	
	class ManageOrchestrasPane extends JSManageOrchestrasPane {
		ManageOrchestrasPane() {
			actionAddOrchestra.putValue(Action.SMALL_ICON, Res.iconNew16);
			actionEditOrchestra.putValue(Action.SMALL_ICON, Res.iconEdit16);
			actionDeleteOrchestra.putValue(Action.SMALL_ICON, Res.iconDelete16);
			
			removeAll();
			
			ToolBar toolBar = new ToolBar();
			toolBar.add(new ToolbarButton(actionAddOrchestra));
			toolBar.add(new ToolbarButton(actionEditOrchestra));
			toolBar.add(new ToolbarButton(actionDeleteOrchestra));
			add(toolBar, BorderLayout.NORTH);
			
			JScrollPane sp = new JScrollPane(orchestraTable);
			sp.setPreferredSize(new Dimension(120, 130));
			
			PixmapPane p = new PixmapPane(Res.gfxChannelOptions);
			p.setPixmapInsets(new Insets(1, 1, 1, 1));
			p.setLayout(new BorderLayout());
			p.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
			
			PixmapPane p2 = new PixmapPane(Res.gfxBorder);
			p2.setPixmapInsets(new Insets(1, 1, 1, 1));
			p2.setLayout(new BorderLayout());
			p2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			p2.add(sp);
			p.add(p2);
			
			add(p);
		}
	}
	
	class OrchestraPane extends JSOrchestraPane {
		OrchestraPane() {
			actionAddInstrument.putValue(Action.SMALL_ICON, Res.iconNew16);
			actionEditInstrument.putValue(Action.SMALL_ICON, Res.iconEdit16);
			actionDeleteInstrument.putValue(Action.SMALL_ICON, Res.iconDelete16);
			//actionInstrumentUp.putValue(Action.SMALL_ICON, Res.iconUp16);
			//actionInstrumentDown.putValue(Action.SMALL_ICON, Res.iconDown16);
			
			removeAll();
			
			ToolBar toolBar = new ToolBar();
			toolBar.add(new ToolbarButton(actionAddInstrument));
			toolBar.add(new ToolbarButton(actionEditInstrument));
			toolBar.add(new ToolbarButton(actionDeleteInstrument));
			
			//toolBar.addSeparator();
			
			//toolBar.add(new ToolbarButton(actionInstrumentUp));
			//toolBar.add(new ToolbarButton(actionInstrumentDown));
		
			toolBar.setFloatable(false);
			add(toolBar, java.awt.BorderLayout.NORTH);
			
			JScrollPane sp = new JScrollPane(instrumentTable);
			Dimension d;
			d = new Dimension(sp.getMinimumSize().width, sp.getPreferredSize().height);
			sp.setPreferredSize(d);
			
			PixmapPane p = new PixmapPane(Res.gfxChannelOptions);
			p.setPixmapInsets(new Insets(1, 1, 1, 1));
			p.setLayout(new BorderLayout());
			p.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
			
			PixmapPane p2 = new PixmapPane(Res.gfxBorder);
			p2.setPixmapInsets(new Insets(1, 1, 1, 1));
			p2.setLayout(new BorderLayout());
			p2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			p2.add(sp);
			p.add(p2);
			
			add(p);
		}
	}
	
	private final Handler eventHandler = new Handler();
	
	private Handler
	getHandler() { return eventHandler; }
	
	private class Handler extends OrchestraAdapter implements ListListener<OrchestraModel> {
		/** Invoked when an orchestra is added to the orchestra list. */
		public void
		entryAdded(ListEvent<OrchestraModel> e) {
			if(cbOrchestras.getItemCount() == 0) cbOrchestras.setEnabled(true);
			cbOrchestras.addItem(e.getEntry());
			
			// we do this because the orchestras are added after creation of the view.
			if(orchIdx != -1 && cbOrchestras.getItemCount() > orchIdx) {
				cbOrchestras.setSelectedIndex(orchIdx);
				orchIdx = -1;
			}
		}
	
		/** Invoked when an orchestra is removed from the orchestra list. */
		public void
		entryRemoved(ListEvent<OrchestraModel> e) {
			cbOrchestras.removeItem(e.getEntry());
			if(cbOrchestras.getItemCount() == 0) cbOrchestras.setEnabled(false);
			
			if(orchIdx != -1) orchIdx = -1;
		}
		
		/** Invoked when the name of orchestra is changed. */
		public void
		nameChanged(OrchestraEvent e) {
			
		}
	
		/** Invoked when the description of orchestra is changed. */
		public void
		descriptionChanged(OrchestraEvent e) { }
	
	
	}
}
