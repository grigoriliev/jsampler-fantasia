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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import static org.jsampler.view.fantasia.FantasiaI18n.i18n;
import static org.jsampler.view.fantasia.FantasiaPrefs.preferences;


/**
 *
 * @author Grigor Iliev
 */
public class RightSidePane extends PixmapPane {
	private JTabbedPane tabbedPane = new JTabbedPane();
	private InstrumentsDbPane instrumentsDbPane = null;
	private final DevicesPane devicesPane = new DevicesPane();
	private final JScrollPane spDevicesPane = new JScrollPane();
	
	
	/**
	 * Creates a new instance of <code>RightSidePane</code>
	 */
	public
	RightSidePane() {
		super(Res.gfxRoundBg14);
		setOpaque(false);
		setPixmapInsets(new java.awt.Insets(6, 6, 6, 6));
		setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		setLayout(new BorderLayout());
		
		spDevicesPane.setOpaque(false);
		spDevicesPane.getViewport().setOpaque(false);
		spDevicesPane.setViewportView(devicesPane);
		spDevicesPane.setBorder(BorderFactory.createEmptyBorder());
		int h = spDevicesPane.getMinimumSize().height;
		spDevicesPane.setMinimumSize(new Dimension(200, h));
		
		final String s = "rightSidePane.showInstrumentsDb";
		setTabbedView(preferences().getBoolProperty(s));
		
		preferences().addPropertyChangeListener(s, getHandler());
	}
	
	private void
	setTabbedView(boolean b) {
		remove(spDevicesPane);
		remove(tabbedPane);
		
		tabbedPane.removeChangeListener(getHandler());
		tabbedPane.removeAll();
		
		if(b) {
			if(instrumentsDbPane == null) instrumentsDbPane = new InstrumentsDbPane();
			tabbedPane.addTab(i18n.getLabel("RightSidePane.tabDevices"), spDevicesPane);
			tabbedPane.addTab(i18n.getLabel("RightSidePane.tabInstrumentsDb"), instrumentsDbPane);
			tabbedPane.addChangeListener(getHandler());
			add(tabbedPane);
			
			int i = preferences().getIntProperty("rightSidePane.tabIndex", 0);
			if(tabbedPane.getTabCount() > i) tabbedPane.setSelectedIndex(i);
		} else {
			add(spDevicesPane);
		}
		
		validate();
	}
	
	protected void
	savePreferences() {
		if(instrumentsDbPane != null) instrumentsDbPane.savePreferences();
	}
	
	private final EventHandler eventHandler = new EventHandler();
	
	private EventHandler
	getHandler() { return eventHandler; }
	
	private class EventHandler implements ChangeListener, PropertyChangeListener {
		public void
		stateChanged(ChangeEvent e) {
			int idx = tabbedPane.getSelectedIndex();
			if(idx == -1) return;
			preferences().setIntProperty("rightSidePane.tabIndex", idx);
		}
		
		public void
		propertyChange(PropertyChangeEvent e) {
			setTabbedView(preferences().getBoolProperty("rightSidePane.showInstrumentsDb"));
		}
	}
}
