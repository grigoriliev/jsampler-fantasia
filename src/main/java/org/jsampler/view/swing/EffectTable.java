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
package org.jsampler.view.swing;

import javax.swing.JTable;
import javax.swing.table.TableColumnModel;
import org.jsampler.CC;
import org.jsampler.JSPrefs;
import org.linuxsampler.lscp.Effect;

/**
 *
 * @author Grigor Iliev
 */
public class EffectTable extends JSTable<EffectTableModel> {
	
	public
	EffectTable() { this("EffectTable"); }
	
	EffectTable(String tablePrefix) {
		super(new EffectTableModel(), tablePrefix);
		
		setAutoCreateRowSorter(true);
	}
	
	/**
	 * Gets the selected effects.
	 * @return The selected effects, or empty array if no effects are selected.
	 */
	public Effect[]
	getSelectedEffects() {
		int[] rows = getSelectedRows();
		Effect[] effects = new Effect[rows.length];
		
		for(int i = 0; i < rows.length; i++) {
			int idx = convertRowIndexToModel(rows[i]);
			effects[i] = getModel().getEffect(idx);
		}
		
		return effects;
	}
}
