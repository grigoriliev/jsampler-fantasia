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

import java.io.File;

import org.jsampler.CC;
import org.jsampler.JSUtils;
import org.jsampler.android.AHF;
import org.jsampler.android.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

public class JSamplerHomeChooser extends OkCancelDialog {
	private TextView tfHome;
	
	public JSamplerHomeChooser(Activity context) {
		super(context, R.string.choose_home_dir_title, R.layout.choose_home_dir);
	}
	
	protected void
	onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		tfHome = (TextView)findViewById(R.id.JSamplerHomeChooser_tfHome);
		if(CC.getJSamplerHome() != null) tfHome.setText(CC.getJSamplerHome());
		else {
			tfHome.setText(AHF.getActivity().getFilesDir().getAbsolutePath() + File.separator + ".jsampler");
		}
		
		
	}
	
	protected void onOk() {
		if(!((Button)findViewById(R.id.btn_ok)).isEnabled()) return;
		
		/*if(tv.getText().length() == 0) {
			JOptionPane.showMessageDialog (
				this, i18n.getLabel("JSamplerHomeChooser.selectFile"),
				"",
				JOptionPane.INFORMATION_MESSAGE
			);
			
			return;
		}
		
		setVisible(false);*/
		Log.w("changeJSamplerHome", tfHome.getText().toString());
		JSUtils.changeJSamplerHome(tfHome.getText().toString());
		dismiss();
	}
	
	protected void
	onCancel() { }
}
