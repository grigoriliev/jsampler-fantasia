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

import net.sf.juife.event.GenericEvent;
import net.sf.juife.event.GenericListener;

import org.jsampler.CC;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.RadioButton;

public class ServerListAdapter extends AbstractListAdapter implements GenericListener {
	public ServerListAdapter() {
		CC.getServerList().addChangeListener(this);
	}
	
	public void
	uninstall() { CC.getServerList().removeChangeListener(this); }
	
	private static class ItemView extends RadioButton implements Checkable {

		public ItemView(Context context) {
			super(context);
		}
		
	}
	
	@Override
	public void
	jobDone(GenericEvent e) { notifyDataSetChanged(); }
	
	@Override
	public int
	getCount() { return CC.getServerList().getServerCount(); }
	
	@Override
	public Object
	getItem(int position) { return CC.getServerList().getServer(position); }
	
	@Override
	public View
	getView(int position, View convertView, ViewGroup parent) {
		ItemView rb;
		if(convertView != null && convertView instanceof ItemView) {
			rb = (ItemView) convertView;
			
		} else {
			rb = new ItemView(parent.getContext());
			rb.setFocusable(false);
			rb.setFocusableInTouchMode(false);
			rb.setClickable(false);
		}
		rb.setText(getItem(position).toString());
		
		return rb;
	}
}
