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

import java.util.ArrayList;

import android.database.DataSetObserver;
import android.widget.ListAdapter;

public abstract class AbstractListAdapter implements ListAdapter {
	private ArrayList<DataSetObserver> observers = new ArrayList<DataSetObserver>();
	
	public boolean
	areAllItemsEnabled() { return true; }
	
	public boolean
	isEnabled (int position) { return true; }
	
	public int
	getViewTypeCount() { return 1; }
	
	public int
	getItemViewType(int position) { return 42; }
	
	public boolean isEmpty() { return getCount() == 0; }
	
	public long
	getItemId (int position) { return position; }
	
	public boolean
	hasStableIds() { return false; }
	
	public void
	registerDataSetObserver(DataSetObserver observer) {
		observers.add(observer);
	}
	
	public void
	unregisterDataSetObserver (DataSetObserver observer) {
		observers.remove(observer);
	}
	
	public void
	notifyDataSetChanged() {
		for(DataSetObserver observer : observers) observer.onChanged();
	}
	
	public void
	notifyDataSetInvalidated() {
		for(DataSetObserver observer : observers) observer.onInvalidated();
	}
}
