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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import net.sf.juife.Wizard;

import org.jsampler.CC;
import org.jsampler.MidiInstrumentMap;

import org.jsampler.event.ListEvent;
import org.jsampler.event.ListListener;

import org.jsampler.view.fantasia.basic.*;

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
	private final FantasiaTaskPane mapsTaskPane = new FantasiaTaskPane();
	
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
		
		FantasiaSubPanel fsp = new FantasiaSubPanel(false, true, false);
		fsp.add(manageMapsPane);
		mapsTaskPane.add(fsp);
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
	
	class ManageMapsPane extends JSManageMidiMapsPane {
		ManageMapsPane() {
			actionAddMap.putValue(Action.SMALL_ICON, Res.iconNew16);
			actionEditMap.putValue(Action.SMALL_ICON, Res.iconEdit16);
			actionRemoveMap.putValue(Action.SMALL_ICON, Res.iconDelete16);
			
			removeAll();
			
			JToolBar toolBar = FantasiaUtils.createSubToolBar();
			toolBar.add(new ToolbarButton(actionAddMap));
			toolBar.add(new ToolbarButton(actionEditMap));
			toolBar.add(new ToolbarButton(actionRemoveMap));
			add(toolBar, BorderLayout.NORTH);
			
			JScrollPane sp = new JScrollPane(midiMapTable);
			sp.setPreferredSize(new Dimension(120, 130));
			
			JPanel p = FantasiaUtils.createBottomSubPane();
			p.add(sp);
			add(p);
		}
		
		@Override
		protected void
		paintComponent(Graphics g) {
			super.paintComponent(g);
			
			double h = getSize().getHeight();
			double w = getSize().getWidth();
			
			FantasiaPainter.paintGradient((Graphics2D)g, 0, 0, w - 1, h - 1);
			
			FantasiaPainter.RoundCorners rc =
				new FantasiaPainter.RoundCorners(true, false, false, true);
			
			FantasiaPainter.paintOuterBorder((Graphics2D)g, 0, 0, w - 1, h - 1, rc);
		}
	}
	
	class MapsPane extends JPanel {
		MapsPane() {
			setOpaque(false);
			setLayout(new BorderLayout());
			setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
			
			FantasiaSubPanel p2 = new FantasiaSubPanel(true, false);
			p2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));
			
			JPanel p = new FantasiaPanel();
			p.setOpaque(false);
			p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
			p.add(cbMaps);
			p.setBorder(BorderFactory.createEmptyBorder(3, 1, 5, 1));
			p2.add(p);
			
			FantasiaSubPanel fsp = new FantasiaSubPanel(false, true, false);
			fsp.add(instrumentsPane);
			
			p2.add(fsp);
			
			add(p2);
		}
	}
	
	class InstrumentsPane extends JSMidiInstrumentsPane {
		InstrumentsPane() {
			actionAddInstrument.putValue(Action.SMALL_ICON, Res.iconNew16);
			actionEditInstrument.putValue(Action.SMALL_ICON, Res.iconEdit16);
			actionRemove.putValue(Action.SMALL_ICON, Res.iconDelete16);
			
			removeAll();
			
			JToolBar toolBar = FantasiaUtils.createSubToolBar();
			toolBar.add(new ToolbarButton(actionAddInstrument));
			toolBar.add(new ToolbarButton(actionEditInstrument));
			toolBar.add(new ToolbarButton(actionRemove));
			
			add(toolBar, java.awt.BorderLayout.NORTH);
			
			JScrollPane sp = new JScrollPane(midiInstrumentTree);
			Dimension d;
			d = new Dimension(sp.getMinimumSize().width, sp.getPreferredSize().height);
			sp.setPreferredSize(d);
			
			JPanel p = FantasiaUtils.createBottomSubPane();
			p.add(sp);
			
			add(p);
		}
		
		@Override
		public void
		addInstrument() {
			MidiInstrumentMap map = (MidiInstrumentMap)cbMaps.getSelectedItem();
			
			JSNewMidiInstrumentWizard wizard =
				new JSNewMidiInstrumentWizard(Res.iconBrowse16, map);
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
		@Override
		public void
		entryAdded(ListEvent<MidiInstrumentMap> e) {
			if(cbMaps.getItemCount() == 0) cbMaps.setEnabled(true);
			cbMaps.addItem(e.getEntry());
		}
	
		/** Invoked when an orchestra is removed from the orchestra list. */
		@Override
		public void
		entryRemoved(ListEvent<MidiInstrumentMap> e) {
			cbMaps.removeItem(e.getEntry());
			if(cbMaps.getItemCount() == 0) cbMaps.setEnabled(false);
		}
	}
}
