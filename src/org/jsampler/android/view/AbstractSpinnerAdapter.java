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

package org.jsampler.android.view;

import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public abstract class AbstractSpinnerAdapter<I> extends AbstractListAdapter implements SpinnerAdapter {
	protected boolean useEmptyItem = true;
	
	@Override
	public View
	getDropDownView(int position, View convertView, ViewGroup parent) {
		return getView(position, convertView, parent);
	}
	
	public abstract int size();
	
	public abstract Object get(int position);
	
	@Override
	public int
	getCount() { return size() + (useEmptyItem ? 1 : 0); }
	
	@Override
	public Object
	getItem(int position) {
		return useEmptyItem ? (position == 0 ? null : get(position - 1)) : get(position);
	}
	
	public void
	setUseEmptyItem(boolean b) {
		if(b == useEmptyItem) return;
		useEmptyItem = b;
		notifyDataSetChanged();
	}
	
	/**
	 * Prepares adapter for the new selection. If the item is <code>null</code>
	 * an extra (empty) element will be used to represent no selection, otherwise
	 * the extra element will be removed.
	 * @param item The item to be selected.
	 * @return
	 */
	public int
	prepareForSelection(I item) {
		if(item == null) {
			setUseEmptyItem(true);
			return 0;
		}
		
		setUseEmptyItem(false);
		return getItemPosition(item);
	}
	
	public int
	getItemPosition(I item) {
		int idx = -1;
		for(int i = 0; i < size(); i++) {
			if(compare((I)get(i), item)) {
				idx = i;
				break;
			}
		}
		
		return idx;
	}
	
	public boolean
	compare(I item1, I item2) { return item1 == item2; }
	
	/** Gets the text to be displayed when the empty item is selected. */
	public abstract String getEmptyItemText();
	
	public String
	getItemText(int position) { return getItem(position).toString(); }
	
	@Override
	public View
	getView(int position, View convertView, ViewGroup parent) {
		TextView text;
		if(convertView != null && convertView instanceof TextView) {
			text = (TextView) convertView;
			
		} else {
			text = new TextView(parent.getContext());
			text.setFocusable(false);
			text.setFocusableInTouchMode(false);
			text.setClickable(false);
		}
		
		if(useEmptyItem && position == 0) text.setText(getEmptyItemText());
		else text.setText(getItemText(position));
		
		return text;
	}
}
