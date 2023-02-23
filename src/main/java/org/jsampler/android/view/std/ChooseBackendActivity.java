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

package org.jsampler.android.view.std;

import org.jsampler.CC;
import org.jsampler.JSPrefs;
import org.jsampler.Server;
import org.jsampler.android.R;
import org.jsampler.android.view.ServerListAdapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import static org.jsampler.JSPrefs.SERVER_INDEX;

public class ChooseBackendActivity extends Activity {
	public static final int DLG_ADD_BACKEND = 1;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose_backend);
		ListView lv = (ListView)findViewById(R.id.choose_backend_list);
		lv.setItemsCanFocus(false);
		lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		ServerListAdapter sla = new ServerListAdapter();
		lv.setAdapter(sla);
		
		sla.registerDataSetObserver(new DataSetObserver() {
			public void onChanged() { updateButtonStates(); }
			public void onInvalidated() { updateButtonStates(); }
		});
		
		lv.setOnItemClickListener(new  ListView.OnItemClickListener() {
			@Override
			public void
			onItemClick(AdapterView<?> parent, View view, int position, long id) {
				updateButtonStates();
			}
		});
		
		/*lv.setOnItemSelectedListener(new  ListView.OnItemSelectedListener() {
			public void
			onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				
			}
			
			public void
			onNothingSelected (AdapterView<?> parent) {
				
			}
		});*/
		
		int i = preferences().getIntProperty(JSPrefs.SERVER_INDEX);
		if(i >= 0 && i < CC.getServerList().getServerCount()) {
			lv.setItemChecked(i, true);
		}
		
		updateButtonStates();
		
		installButtonListeners();
	}
	
	private void
	installButtonListeners() {
		Button btn = (Button)findViewById(R.id.choose_backend_btn_add);
		btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(DLG_ADD_BACKEND);
			}
		});
		
		btn = (Button)findViewById(R.id.choose_backend_btn_remove);
		btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				removeSelectedBackend();
			}
		});
		
		btn = (Button)findViewById(R.id.choose_backend_btn_up);
		btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				moveSelectedBackendUp();
			}
		});
		
		btn = (Button)findViewById(R.id.choose_backend_btn_down);
		btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				moveSelectedBackendDown();
			}
		});
		
		btn = (Button)findViewById(R.id.choose_backend_btn_connect);
		btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				connect();
			}
		});
	}
	
	private void
	removeSelectedBackend() {
		ListView lv = (ListView)findViewById(R.id.choose_backend_list);
		int pos = lv.getCheckedItemPosition();
		if (pos == ListView.INVALID_POSITION) return;
		lv.setItemChecked(pos, false);
		CC.getServerList().removeServer(pos);
		((ServerListAdapter)lv.getAdapter()).notifyDataSetChanged();
	}
	
	private void
	moveSelectedBackendUp() {
		ListView lv = (ListView)findViewById(R.id.choose_backend_list);
		int pos = lv.getCheckedItemPosition();
		if (pos == ListView.INVALID_POSITION || pos < 1) return;
		lv.setItemChecked(pos, false);
		CC.getServerList().moveServerUp(CC.getServerList().getServer(pos));
		lv.setItemChecked(pos - 1, true);
		((ServerListAdapter)lv.getAdapter()).notifyDataSetChanged();
	}
	
	private void
	moveSelectedBackendDown() {
		ListView lv = (ListView)findViewById(R.id.choose_backend_list);
		int pos = lv.getCheckedItemPosition();
		if (pos == ListView.INVALID_POSITION || pos >= lv.getAdapter().getCount() - 1) return;
		CC.getServerList().moveServerDown(CC.getServerList().getServer(pos));
		lv.setItemChecked(pos + 1, true);
		((ServerListAdapter)lv.getAdapter()).notifyDataSetChanged();
	}
	
	private void
	connect() {
		Server server = null;
		ListView lv = (ListView)findViewById(R.id.choose_backend_list);
		int pos = lv.getCheckedItemPosition();
		if (pos != ListView.INVALID_POSITION) {
			preferences().setIntProperty(SERVER_INDEX, pos);
			server = CC.getServerList().getServer(pos);
		}
		
		Intent i = getIntent();
		i.putExtra("org.jsampler.SomeSerializableObject", server);
		setResult(RESULT_OK, i);
		finish();
	}
	
	private void
	updateButtonStates() {
		ListView lv = (ListView)findViewById(R.id.choose_backend_list);
		boolean b = lv.getCheckedItemCount() > 0;
		((Button)findViewById(R.id.choose_backend_btn_connect)).setEnabled(b);
		
		boolean b2 = b && lv.getAdapter().getCount() > 1;
		((Button)findViewById(R.id.choose_backend_btn_remove)).setEnabled(b2);
		
		b2 = b && lv.getCheckedItemPosition() != 0;
		((Button)findViewById(R.id.choose_backend_btn_up)).setEnabled(b2);
		
		b2 = b && lv.getCheckedItemPosition() < lv.getAdapter().getCount() - 1;
		((Button)findViewById(R.id.choose_backend_btn_down)).setEnabled(b2);
	}
	
	private static JSPrefs
	preferences() { return CC.getViewConfig().preferences(); }
	
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		
		switch(id) {
		case DLG_ADD_BACKEND:
			dialog = new AddBackendDlg(this);
			break;
		default:
			dialog = null;
		}
		
		return dialog;
	}
	
	@Override
	protected void
	onDestroy() {
		final ListView lv = (ListView)findViewById(R.id.choose_backend_list);
		((ServerListAdapter)lv.getAdapter()).uninstall();
		super.onDestroy();
		
	}
}
