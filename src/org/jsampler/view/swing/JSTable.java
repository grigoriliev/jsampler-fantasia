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
import javax.swing.table.TableModel;
import org.jsampler.CC;
import org.jsampler.JSPrefs;

/**
 *
 * @author Grigor Iliev
 */
public class JSTable<T extends TableModel> extends JTable {
	private String tablePrefix;
	
	public
	JSTable(T model) { this(model, null); }
	
	public
	JSTable(T model, String prefix) {
		super(model);
		if(prefix == null) prefix = getClass().getName();
		
		setTablePrefix(prefix);
		
		loadColumnWidths();
	}
	
	@Override
	public T
	getModel() { return (T)super.getModel(); }
	
	public String
	getTablePrefix() { return tablePrefix; }
	
	/**
	 * Sets the table prefix, which is used for ID
	 * for saving the table settings for the next session.
	 */
	public void
	setTablePrefix(String prefix) { tablePrefix = prefix; }
		
	public void
	loadColumnWidths() { loadColumnWidths(getTablePrefix()); }
	
	public void
	loadColumnWidths(String prefix) {
		if(prefix == null) return;
		TableColumnModel tcm = getColumnModel();
		
		for(int i = 0; i < getModel().getColumnCount(); i++) {
			String s = prefix + ": column " + i;
			int w = preferences().getIntProperty(s);
			if(w > 0) tcm.getColumn(i).setPreferredWidth(w);
		}
	}
	
	public void
	saveColumnWidths() { saveColumnWidths(getTablePrefix()); }
	
	public void
	saveColumnWidths(String prefix) {
		if(prefix == null) return;
		TableColumnModel tcm = getColumnModel();
		
		for(int i = 0; i < getModel().getColumnCount(); i++) {
			String s = prefix + ": column " + i;
			preferences().setIntProperty(s, tcm.getColumn(i).getWidth());
		}
	}
	
	protected JSPrefs
	preferences() { return CC.getViewConfig().preferences(); }
}
