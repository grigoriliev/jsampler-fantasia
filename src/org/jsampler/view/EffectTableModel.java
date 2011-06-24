/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2011 Grigor Iliev <grigor@grigoriliev.com>
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

import javax.swing.table.AbstractTableModel;
import org.jsampler.CC;
import org.jsampler.EffectList;
import org.linuxsampler.lscp.Effect;

import static org.jsampler.JSI18n.i18n;


/**
 *
 * @author Grigor Iliev
 */
public class EffectTableModel extends AbstractTableModel {
	public
	EffectTableModel() {
		
	}
	
	/**
	 * Gets the number of columns in the model.
	 * @return The number of columns in the model.
	 */
	@Override
	public int
	getColumnCount() { return 4; }
	
	/**
	 * Gets the number of rows in the model.
	 * @return The number of rows in the model.
	 */
	@Override
	public int
	getRowCount() { return CC.getSamplerModel().getEffects().getEffectCount(); }
	
	/**
	 * Gets the value for the cell at <code>columnIndex</code> and
	 * <code>rowIndex</code>.
	 * @param row The row whose value is to be queried.
	 * @param col The column whose value is to be queried.
	 * @return The value for the cell at <code>columnIndex</code> and
	 * <code>rowIndex</code>.
	 */
	@Override
	public Object
	getValueAt(int row, int col) {
		EffectList effects = CC.getSamplerModel().getEffects();
		switch(col) {
			case 0: return effects.getEffect(row).getDescription();
			case 1: return effects.getEffect(row).getSystem();
			case 2: return effects.getEffect(row).getModule();
			case 3: return effects.getEffect(row).getName();
			default: return effects.getEffect(row).getDescription();
		}
	}
	
	/**
	 * Gets the name of the column at <code>columnIndex</code>.
	 * @return The name of the column at <code>columnIndex</code>.
	 */
	@Override
	public String
	getColumnName(int col) {
		switch(col) {
			case 1:  return i18n.getLabel("EffectTableModel.type");
			case 2:  return i18n.getLabel("EffectTableModel.file");
			case 3:  return i18n.getLabel("EffectTableModel.id");
			default: return i18n.getLabel("EffectTableModel.effect");
		}
	}
	/**
	 * Determines whether the specified column is sortable.
	 * @return <code>true</code> if the specified column is
	 * sortable, <code>false</code> otherwise.
	 */
	public boolean
	isSortable(int col) { return true; }
	
	public Effect
	getEffect(int row) { return CC.getSamplerModel().getEffects().getEffect(row); }
}
