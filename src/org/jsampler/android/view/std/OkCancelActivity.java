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

import org.jsampler.android.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public abstract class OkCancelActivity extends Activity {
	private final int layout;
	
	public OkCancelActivity(int layout) {
		this.layout = layout;
	}
	
	protected void
	onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(layout);
		installListeners();
	}
	
	/** This method is invoked when the user choose the OK button */
	protected abstract void onOk();
	
	/** This method is invoked when the user choose the Cancel button */
	protected abstract void onCancel();
	
	private void
	installListeners() {
		Button btnOk = (Button)findViewById(R.id.btn_ok);
		btnOk.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				onOk();
			}
		});
		
		Button btnCancel = (Button)findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) { onCancel(); }
		});
	}
}
