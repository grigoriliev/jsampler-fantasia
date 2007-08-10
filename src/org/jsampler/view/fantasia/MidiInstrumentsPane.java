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
import java.awt.Color;
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

import net.sf.juife.Wizard;

import org.jsampler.CC;
import org.jsampler.MidiInstrumentMap;

import org.jsampler.event.ListEvent;
import org.jsampler.event.ListListener;

import org.jsampler.view.std.JSManageMidiMapsPane;
import org.jsampler.view.std.JSMidiInstrumentsPane;
import org.jsampler.view.std.JSNewMidiInstrumentWizard;

import static org.jsampler.view.fantasia.FantasiaI18n.i18n;
import static org.jsampler.view.fantasia.FantasiaPrefs.*;

/**
 *
 * @author Grigor Iliev
 */
public class MidiInstrumentsPane extends JPanel {
	private final JPanel taskPaneContainer = new JPanel();
	private final TaskPane mapsTaskPane = new TaskPane();
	
	private ManageMapsPane manageMapsPane = new ManageMapsPane();
	private final InstrumentsPane instrumentsPane = new InstrumentsPane();
	
	private final JComboBox cbMaps = new JComboBox();
	//private final InstrumentsPane instrumentsPane = new InstrumentsPane();
	
	/** Creates a new instance of <code>MidiInstrumentsPane</code> */
	public
	MidiInstrumentsPane() {
		setLayout(new BorderLayout());
		setOpaque(false);
		
		mapsTaskPane.setTitle(i18n.getLabel("MidiInstrumentsPane.mapsTaskPane"));
		mapsTaskPane.add(manageMapsPane);
		boolean b;
		mapsTaskPane.setAnimated(preferences().getBoolProperty(ANIMATED));
		b = preferences().getBoolProperty("MidiInstrumentsPane.mapsTaskPane.expanded");
		mapsTaskPane.setExpanded(b);
		
		preferences().addPropertyChangeListener(ANIMATED, new PropertyChangeListener() {
			public void
			propertyChange(PropertyChangeEvent e) {
				mapsTaskPane.setAnimated(preferences().getBoolProperty(ANIMATED));
			}
		});
		
		taskPaneContainer.setOpaque(false);
		taskPaneContainer.setLayout(new BorderLayout());
		taskPaneContainer.add(mapsTaskPane);
		taskPaneContainer.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		add(taskPaneContainer, BorderLayout.NORTH);
		add(new MapsPane());
		
		cbMaps.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { mapChanged(); }
		});
		
		for(Object o : CC.getSamplerModel().getMidiInstrumentMaps()) {
			cbMaps.addItem(o);
		}
		
		CC.getSamplerModel().addMidiInstrumentMapListListener(getHandler());
		cbMaps.setEnabled(cbMaps.getItemCount() != 0);
	}
	
	private void
	mapChanged() {
		MidiInstrumentMap map = (MidiInstrumentMap)cbMaps.getSelectedItem();
		instrumentsPane.setMidiInstrumentMap(map);
	}
	
	public void
	savePreferences() {
		boolean b = mapsTaskPane.isExpanded();
		preferences().setBoolProperty("MidiInstrumentsPane.mapsTaskPane.expanded", b);
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
	
	class ManageMapsPane extends JSManageMidiMapsPane {
		ManageMapsPane() {
			actionAddMap.putValue(Action.SMALL_ICON, Res.iconNew16);
			actionEditMap.putValue(Action.SMALL_ICON, Res.iconEdit16);
			actionRemoveMap.putValue(Action.SMALL_ICON, Res.iconDelete16);
			
			removeAll();
			
			ToolBar toolBar = new ToolBar();
			toolBar.add(new ToolbarButton(actionAddMap));
			toolBar.add(new ToolbarButton(actionEditMap));
			toolBar.add(new ToolbarButton(actionRemoveMap));
			add(toolBar, BorderLayout.NORTH);
			
			JScrollPane sp = new JScrollPane(midiMapTable);
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
	
	class MapsPane extends JPanel {
		MapsPane() {
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
			p2.add(cbMaps);
			p2.add(Box.createRigidArea(new Dimension(0, 5)));
			p2.add(instrumentsPane);
			
			add(p2);
		}
	}
	
	class InstrumentsPane extends JSMidiInstrumentsPane {
		InstrumentsPane() {
			actionAddInstrument.putValue(Action.SMALL_ICON, Res.iconNew16);
			actionEditInstrument.putValue(Action.SMALL_ICON, Res.iconEdit16);
			actionRemove.putValue(Action.SMALL_ICON, Res.iconDelete16);
			
			removeAll();
			
			ToolBar toolBar = new ToolBar();
			toolBar.add(new ToolbarButton(actionAddInstrument));
			toolBar.add(new ToolbarButton(actionEditInstrument));
			toolBar.add(new ToolbarButton(actionRemove));
			
			toolBar.setFloatable(false);
			add(toolBar, java.awt.BorderLayout.NORTH);
			
			JScrollPane sp = new JScrollPane(midiInstrumentTree);
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
		
		public void
		addInstrument() {
			JSNewMidiInstrumentWizard wizard =
				new JSNewMidiInstrumentWizard(preferences(), Res.iconBrowse16);
			wizard.getWizardDialog().setResizable(false);
			wizard.putClientProperty(Wizard.BACK_BUTTON_ICON, Res.iconBack16);
			wizard.putClientProperty(Wizard.NEXT_BUTTON_ICON, Res.iconNext16);
			Color c = new Color(0x626262);
			wizard.putClientProperty(Wizard.LEFT_PANE_BACKGROUND_COLOR, c);
			//c = new Color(0x4b4b4b);
			//wizard.putClientProperty(Wizard.LEFT_PANE_FOREGROUND_COLOR, c);
			
			if(preferences().getBoolProperty("NewMidiInstrumentWizard.skip1")) {
				if(wizard.getModel().getCurrentPage() == null) {
					wizard.getModel().next();
				}
				wizard.getModel().next();
			}
			
			wizard.showWizard();
		}
	}
	
	private final Handler eventHandler = new Handler();
	
	private Handler
	getHandler() { return eventHandler; }
	
	private class Handler implements ListListener<MidiInstrumentMap> {
		/** Invoked when an orchestra is added to the orchestra list. */
		public void
		entryAdded(ListEvent<MidiInstrumentMap> e) {
			if(cbMaps.getItemCount() == 0) cbMaps.setEnabled(true);
			cbMaps.addItem(e.getEntry());
		}
	
		/** Invoked when an orchestra is removed from the orchestra list. */
		public void
		entryRemoved(ListEvent<MidiInstrumentMap> e) {
			cbMaps.removeItem(e.getEntry());
			if(cbMaps.getItemCount() == 0) cbMaps.setEnabled(false);
		}
	}
}
