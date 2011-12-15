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
import org.jsampler.Server;
import org.jsampler.android.R;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

public class AddBackendDlg extends OkCancelDialog {
	public AddBackendDlg(Activity context) {
		super(context, R.string.add_backend_title, R.layout.add_backend);
	}
	
	protected void
	onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		installListeners();
		updateState();
	}
	
	private void
	installListeners() {
		EditText et = (EditText)findViewById(R.id.add_backend_et_name);
		et.addTextChangedListener(new TextAdapter() {
			public void
			afterTextChanged(Editable s) { updateState(); }
		});
		
		et = (EditText)findViewById(R.id.add_backend_et_addr);
		et.addTextChangedListener(new TextAdapter() {
			public void
			afterTextChanged(Editable s) { updateState(); }
		});
		
		et = (EditText)findViewById(R.id.add_backend_et_port);
		et.addTextChangedListener(new TextAdapter() {
			public void
			afterTextChanged(Editable s) { updateState(); }
		});
	}
	
	private void
	updateState() {
		boolean b = true;
		int name = ((EditText)findViewById(R.id.add_backend_et_name)).getText().length();
		int addr = ((EditText)findViewById(R.id.add_backend_et_addr)).getText().length();
		String p = ((EditText)findViewById(R.id.add_backend_et_port)).getText().toString();
		
		if(name == 0) b = false;
		if(addr == 0) b = false;
		
		if(p.length() == 0) b = false;
		else {
			int port = Integer.parseInt(p);
			if (port < 1 || port > 0xffff) b = false;
		}
		
		((Button)findViewById(R.id.btn_ok)).setEnabled(b);
	}
	
	protected void onOk() {
		if(!((Button)findViewById(R.id.btn_ok)).isEnabled()) return;

		String name = ((EditText)findViewById(R.id.add_backend_et_name)).getText().toString();
		String desc = ((EditText)findViewById(R.id.add_backend_et_desc)).getText().toString();
		String addr = ((EditText)findViewById(R.id.add_backend_et_addr)).getText().toString();
		String port = ((EditText)findViewById(R.id.add_backend_et_port)).getText().toString();
		
		Server server = new Server();
		server.setName(name);
		server.setDescription(desc);
		server.setAddress(addr);
		server.setPort(Integer.parseInt(port));
		
		CC.getServerList().addServer(server);
		
		dismiss();
	}
	
	protected void
	onCancel() { }
	
	private static abstract class TextAdapter implements TextWatcher {
		public void
		beforeTextChanged(CharSequence s, int start, int count, int after) { }
		
		public void
		onTextChanged(CharSequence s, int start, int before, int count) { }
	}
}
