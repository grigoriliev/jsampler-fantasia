/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2011 Grigor Iliev <grigor@grigoriliev.com>
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

package org.jsampler.view.classic;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import net.sf.juife.swing.OkCancelDialog;

import org.jsampler.CC;
import org.jsampler.MidiInstrumentMap;
import org.jsampler.view.swing.SHF;

import static org.jsampler.view.classic.ClassicI18n.i18n;


/**
 *
 * @author Grigor Iliev
 */
public class RemoveMidiInstrumentMapDlg extends OkCancelDialog {
	private final JLabel lMap = new JLabel(i18n.getLabel("RemoveMidiInstrumentMapDlg.lMap"));
	private final JComboBox cbMaps = new JComboBox();
	
	/** Creates a new instance of <code>RemoveMidiInstrumentMapDlg</code> */
	public
	RemoveMidiInstrumentMapDlg() {
		this(null);
	}
	
	/**
	 * Creates a new instance of <code>RemoveMidiInstrumentMapDlg</code>.
	 * @param map The map to be selected for removal.
	 */
	public
	RemoveMidiInstrumentMapDlg(MidiInstrumentMap map) {
		super(SHF.getMainFrame(), i18n.getLabel("RemoveMidiInstrumentMapDlg.title"));
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.add(lMap);
		p.add(Box.createRigidArea(new Dimension(6, 0)));
		p.add(cbMaps);
		setMainPane(p);
		
		for(MidiInstrumentMap m : CC.getSamplerModel().getMidiInstrumentMaps()) {
			cbMaps.addItem(m);
		}
		if(map != null) cbMaps.setSelectedItem(map);
		
		pack();
	}
	
	/**
	 * Gets the selected map to be deleted.
	 * @return The selected map to be deleted.
	 */
	public MidiInstrumentMap
	getSelectedMap() { return (MidiInstrumentMap)cbMaps.getSelectedItem(); }
	
	protected void
	onOk() {
		if(!btnOk.isEnabled()) return;
		
		setVisible(false);
		setCancelled(false);
	}
	
	protected void
	onCancel() { setVisible(false); }
}
