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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.grigoriliev.jsampler.fantasia.view.basic.FantasiaPanel;
import com.grigoriliev.jsampler.fantasia.view.basic.FantasiaSubPanel;
import com.grigoriliev.jsampler.fantasia.view.basic.FantasiaTabbedPane;
import com.grigoriliev.jsampler.juife.swing.JuifeUtils;

import com.grigoriliev.jsampler.fantasia.view.basic.*;

import static com.grigoriliev.jsampler.fantasia.view.FantasiaPrefs.preferences;


/**
 *
 * @author Grigor Iliev
 */
public class RightSidePane extends FantasiaPanel {
	private FantasiaTabbedPane tabbedPane = new FantasiaTabbedPane();
	private InstrumentsDbPane instrumentsDbPane = null;
	private final DevicesPane devicesPane = new DevicesPane();
	private final JScrollPane spDevicesPane = new JScrollPane();
	
	private final JPanel mainPane = new FantasiaSubPanel(false, true);
	
	/**
	 * Creates a new instance of <code>RightSidePane</code>
	 */
	public
	RightSidePane() {
		setOpaque(false);
		setLayout(new BorderLayout());
		
		tabbedPane.getMainPane().setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		
		spDevicesPane.setOpaque(false);
		spDevicesPane.getViewport().setOpaque(false);
		spDevicesPane.setViewportView(devicesPane);
		spDevicesPane.setBorder(BorderFactory.createEmptyBorder());
		int h = spDevicesPane.getMinimumSize().height;
		spDevicesPane.setMinimumSize(new Dimension(200, h));
		
		final String s = "rightSidePane.showInstrumentsDb";
		setTabbedView(preferences().getBoolProperty(s));
		
		preferences().addPropertyChangeListener(s, getHandler());
		setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 3));
	}
	
	public DevicesPane
	getDevicesPane() { return devicesPane; }
	
	private void
	setTabbedView(boolean b) {
		remove(mainPane);
		remove(tabbedPane);
		
		tabbedPane.removeChangeListener(getHandler());
		tabbedPane.removeAll();
		
		if(b) {
			if(instrumentsDbPane == null) instrumentsDbPane = new InstrumentsDbPane();
			
			FantasiaTabbedPane tp = tabbedPane;
			tp.addTab(FantasiaI18n.i18n.getLabel("RightSidePane.tabDevices"), spDevicesPane);
			tp.addTab(FantasiaI18n.i18n.getLabel("RightSidePane.tabInstrumentsDb"), instrumentsDbPane);
			tp.addChangeListener(getHandler());
			
			Dimension d = JuifeUtils.getUnionSize(tp.getTabButton(0), tp.getTabButton(1));
			tp.getTabButton(0).setPreferredSize(d);
			tp.getTabButton(1).setPreferredSize(d);
			tp.getTabButton(0).setMinimumSize(d);
			tp.getTabButton(1).setMinimumSize(d);
		
			add(tabbedPane);
			
			int i = preferences().getIntProperty("rightSidePane.tabIndex", 0);
			if(tabbedPane.getTabCount() > i) tabbedPane.setSelectedIndex(i);
		} else {
			mainPane.add(spDevicesPane);
			add(mainPane);
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
		@Override
		public void
		stateChanged(ChangeEvent e) {
			int idx = tabbedPane.getSelectedIndex();
			if(idx == -1) return;
			preferences().setIntProperty("rightSidePane.tabIndex", idx);
		}
		
		@Override
		public void
		propertyChange(PropertyChangeEvent e) {
			setTabbedView(preferences().getBoolProperty("rightSidePane.showInstrumentsDb"));
		}
	}
}
