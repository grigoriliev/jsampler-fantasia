/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005 Grigor Kirilov Iliev
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

package org.jsampler;

import java.awt.Frame;

import org.jsampler.view.classic.ProgressDlg;

import static org.jsampler.JSI18n.i18n;


/**
 *
 * @author  Grigor Iliev
 */
public class JSampler {
	public final static String NAME = "JSampler";
	public final static String VERSION = "0.01a";
	
	
	public static void
	main(String[] args) {
		CC.initJSampler();
		
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void
			run() { initGUI(); }
		});
	}
	
	private static void
	initGUI() {
		String view = Prefs.getView();
		
		if(view.equals("classic")) {
			CC.setMainFrame(new org.jsampler.view.classic.MainFrame());
			CC.setProgressIndicator(new ProgressDlg(CC.getMainFrame()));
		} else {
			HF.showErrorMessage(i18n.getError("unknownError"), (Frame)null);
			CC.cleanExit(-1);
			return;
		}
		
		CC.getMainFrame().setVisible(true);
		
		CC.initSamplerModel();
	}
}
