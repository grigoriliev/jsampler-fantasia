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

package org.jsampler.view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import org.jsampler.CC;
import org.jsampler.MidiInstrumentMap;

/**
 * A table for representing MIDI instrument maps.
 * @author Grigor Iliev
 */
public class MidiMapTable extends JTable {
	
	/** Creates a new instance of <code>MidiMapTable</code> */
	public
	MidiMapTable() {
		super(new MidiMapTableModel());
		
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setFillsViewportHeight(true);
		
		addMouseListener(new MouseAdapter() {
			public void
			mouseClicked(MouseEvent e) {
				if(e.getButton() != e.BUTTON1) return;
				int r = rowAtPoint(e.getPoint());
				if(r == -1) {
					clearSelection();
					return;
				}
			}
		});
	}
	
	/**
	 * Gets the selected MIDI instrument map.
	 * @return The selected MIDI instrument map, or
	 * <code>null</code> if no MIDI instrument map is selected.
	 */
	public MidiInstrumentMap
	getSelectedMidiInstrumentMap() {
		int i = getSelectedRow();
		if(i == -1) return null;
		return CC.getSamplerModel().getMidiInstrumentMap(i);
	}
	
	/**
	 * Selects the specified MIDI instrument map. If <code>map</code> is
	 * <code>null</code> or is not in the table the current selection is cleared.
	 * @param map The MIDI instrument map to select.
	 */
	public void
	setSelectedOrchestra(MidiInstrumentMap map) {
		int i = CC.getSamplerModel().getMidiInstrumentMapIndex(map);
		if(i < 0) {
			clearSelection();
			return;
		}
		
		setRowSelectionInterval(i, i);
	}
}
