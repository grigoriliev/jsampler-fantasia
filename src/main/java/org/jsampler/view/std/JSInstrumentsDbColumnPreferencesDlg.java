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

package org.jsampler.view.std;

import java.awt.Dialog;
import java.awt.Frame;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import net.sf.juife.swing.InformationDialog;

import org.jsampler.JSI18n;
import org.jsampler.view.swing.InstrumentsDbTableModel;

import static org.jsampler.view.std.StdI18n.i18n;


/**
 *
 * @author Grigor Iliev
 */
public class JSInstrumentsDbColumnPreferencesDlg extends InformationDialog implements ItemListener {
	private final JCheckBox checkShowSizeColumn =
		new JCheckBox(JSI18n.i18n.getLabel("InstrumentsDbTableModel.SIZE"));
	
	private final JCheckBox checkShowFormatFamilyColumn =
		new JCheckBox(JSI18n.i18n.getLabel("InstrumentsDbTableModel.FORMAT_FAMILY"));
	
	private final JCheckBox checkShowFormatVersionColumn =
		new JCheckBox(JSI18n.i18n.getLabel("InstrumentsDbTableModel.FORMAT_VERSION"));
	
	private final JCheckBox checkShowIsDrumColumn =
		new JCheckBox(JSI18n.i18n.getLabel("InstrumentsDbTableModel.IS_DRUM"));
	
	private final JCheckBox checkShowCreatedColumn =
		new JCheckBox(JSI18n.i18n.getLabel("InstrumentsDbTableModel.CREATED"));
	
	private final JCheckBox checkShowModifiedColumn =
		new JCheckBox(JSI18n.i18n.getLabel("InstrumentsDbTableModel.MODIFIED"));
	
	private final JCheckBox checkShowProductColumn =
		new JCheckBox(JSI18n.i18n.getLabel("InstrumentsDbTableModel.PRODUCT"));
	
	private final JCheckBox checkShowArtistsColumn =
		new JCheckBox(JSI18n.i18n.getLabel("InstrumentsDbTableModel.ARTISTS"));
	
	private final JCheckBox checkShowInstrumentFileColumn
		= new JCheckBox(JSI18n.i18n.getLabel("InstrumentsDbTableModel.INSTRUMENT_FILE"));
	
	private final JCheckBox checkShowInstrumentNrColumn =
		new JCheckBox(JSI18n.i18n.getLabel("InstrumentsDbTableModel.INSTRUMENT_NR"));
	
	private final JCheckBox checkShowKeywordsColumn =
		new JCheckBox(JSI18n.i18n.getLabel("InstrumentsDbTableModel.KEYWORDS"));
	
	private final JSInstrumentsDbTable table;
	
	public
	JSInstrumentsDbColumnPreferencesDlg(Frame owner, JSInstrumentsDbTable table) {
		super(owner);
		this.table = table;
		
		initInstrumentsDbColumnPreferencesDlg();
	}
	
	public
	JSInstrumentsDbColumnPreferencesDlg(Dialog owner, JSInstrumentsDbTable table) {
		super(owner);
		this.table = table;
		
		initInstrumentsDbColumnPreferencesDlg();
	}
	
	private void
	initInstrumentsDbColumnPreferencesDlg() {
		InstrumentsDbTableModel model = table.getModel();
		
		checkShowSizeColumn.setSelected(model.getShowSizeColumn());
		checkShowFormatFamilyColumn.setSelected(model.getShowFormatFamilyColumn());
		checkShowFormatVersionColumn.setSelected(model.getShowFormatVersionColumn());
		checkShowIsDrumColumn.setSelected(model.getShowIsDrumColumn());
		checkShowCreatedColumn.setSelected(model.getShowCreatedColumn());
		checkShowModifiedColumn.setSelected(model.getShowModifiedColumn());
		checkShowProductColumn.setSelected(model.getShowProductColumn());
		checkShowArtistsColumn.setSelected(model.getShowArtistsColumn());
		checkShowInstrumentFileColumn.setSelected(model.getShowInstrumentFileColumn());
		checkShowInstrumentNrColumn.setSelected(model.getShowInstrumentNrColumn());
		checkShowKeywordsColumn.setSelected(model.getShowKeywordsColumn());
		
		checkShowSizeColumn.addItemListener(this);
		checkShowFormatFamilyColumn.addItemListener(this);
		checkShowFormatVersionColumn.addItemListener(this);
		checkShowIsDrumColumn.addItemListener(this);
		checkShowCreatedColumn.addItemListener(this);
		checkShowModifiedColumn.addItemListener(this);
		checkShowProductColumn.addItemListener(this);
		checkShowArtistsColumn.addItemListener(this);
		checkShowInstrumentFileColumn.addItemListener(this);
		checkShowInstrumentNrColumn.addItemListener(this);
		checkShowKeywordsColumn.addItemListener(this);
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.add(checkShowSizeColumn);
		p.add(checkShowFormatFamilyColumn);
		p.add(checkShowFormatVersionColumn);
		p.add(checkShowIsDrumColumn);
		p.add(checkShowCreatedColumn);
		p.add(checkShowModifiedColumn);
		p.add(checkShowProductColumn);
		p.add(checkShowArtistsColumn);
		p.add(checkShowInstrumentFileColumn);
		p.add(checkShowInstrumentNrColumn);
		p.add(checkShowKeywordsColumn);
		String s = i18n.getLabel("JSInstrumentsDbColumnPreferencesDlg.columns");
		p.setBorder(BorderFactory.createTitledBorder(s));
	
		setMainPane(p);
	}
	
	public void
	itemStateChanged(ItemEvent e) {
		table.saveColumnWidths();
		
		InstrumentsDbTableModel m = table.getModel();
		
		Object source = e.getItemSelectable();
		if(source == checkShowSizeColumn) {
			m.setShowSizeColumn(checkShowSizeColumn.isSelected());
		} else if(source == checkShowFormatFamilyColumn) {
			boolean b = checkShowFormatFamilyColumn.isSelected();
			m.setShowFormatFamilyColumn(b);
		} else if(source == checkShowFormatVersionColumn) {
			boolean b = checkShowFormatVersionColumn.isSelected();
			m.setShowFormatVersionColumn(b);
		} else if(source == checkShowIsDrumColumn) {
			m.setShowIsDrumColumn(checkShowIsDrumColumn.isSelected());
		} else if(source == checkShowCreatedColumn) {
			m.setShowCreatedColumn(checkShowCreatedColumn.isSelected());
		} else if(source == checkShowModifiedColumn) {
			m.setShowModifiedColumn(checkShowModifiedColumn.isSelected());
		} else if(source == checkShowProductColumn) {
			m.setShowProductColumn(checkShowProductColumn.isSelected());
		} else if(source == checkShowArtistsColumn) {
			m.setShowArtistsColumn(checkShowArtistsColumn.isSelected());
		} else if(source == checkShowInstrumentFileColumn) {
			boolean b = checkShowInstrumentFileColumn.isSelected();
			m.setShowInstrumentFileColumn(b);
		} else if(source == checkShowInstrumentNrColumn) {
			boolean b = checkShowInstrumentNrColumn.isSelected();
			m.setShowInstrumentNrColumn(b);
		} else if(source == checkShowKeywordsColumn) {
			m.setShowKeywordsColumn(checkShowKeywordsColumn.isSelected());
		}
		
		table.loadColumnWidths();
		table.getRowSorter().toggleSortOrder(0);
	}
}
