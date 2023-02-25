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

package org.jsampler.view.fantasia;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;

import com.grigoriliev.jsampler.juife.swing.JuifeUtils;

import org.jsampler.view.fantasia.basic.FantasiaTabbedPane;
import org.jsampler.view.fantasia.basic.FantasiaPanel;

import static org.jsampler.view.fantasia.FantasiaI18n.i18n;
import static org.jsampler.view.fantasia.FantasiaPrefs.preferences;

/**
 *
 * @author Grigor Iliev
 */
public class LeftSidePane extends FantasiaPanel {
	FantasiaTabbedPane tabbedPane = new FantasiaTabbedPane();
	private final OrchestrasPane orchestraPane = new OrchestrasPane();
	private final MidiInstrumentsPane midiInstrumentsPane = new MidiInstrumentsPane();
	
	/**
	 * Creates a new instance of <code>LeftSidePane</code>
	 */
	public
	LeftSidePane() {
		setOpaque(false);
		setLayout(new BorderLayout());
		FantasiaTabbedPane tp = tabbedPane;
		tp.getMainPane().setBorder(BorderFactory.createEmptyBorder(0, 1, 1, 1));
		tp.addTab(i18n.getLabel("LeftSidePane.tabOrchestras"), orchestraPane);
		tp.addTab(i18n.getLabel("LeftSidePane.tabMidiInstruments"), midiInstrumentsPane);
		
		Dimension d = JuifeUtils.getUnionSize(tp.getTabButton(0), tp.getTabButton(1));
		tp.getTabButton(0).setPreferredSize(d);
		tp.getTabButton(1).setPreferredSize(d);
		tp.getTabButton(0).setMinimumSize(d);
		tp.getTabButton(1).setMinimumSize(d);
		
		add(tp);
		
		int i = preferences().getIntProperty("leftSidePane.tabIndex", 0);
		if(tp.getTabCount() > i) tp.setSelectedIndex(i);
		
		setBorder(BorderFactory.createEmptyBorder(0, 3, 6, 0));
	}
	
	public void
	savePreferences() {
		orchestraPane.savePreferences();
		midiInstrumentsPane.savePreferences();
		int i = tabbedPane.getSelectedIndex();
		if(i != -1) preferences().setIntProperty("leftSidePane.tabIndex", i);
	}
}
