/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2011 Grigor Iliev <grigor@grigoriliev.com>
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

import net.sf.juife.PDUtils;
import net.sf.juife.event.GenericEvent;
import net.sf.juife.event.GenericListener;

import org.jsampler.view.JSViews;

/**
 * The main class of the application.
 * @author  Grigor Iliev
 */
public class JSampler {
	/** The application name. */
	public final static String NAME = "JSampler";
	
	/** The application version. */
	public final static String VERSION = "0.9cvs2";
	
	public static String[] scripts;
	
	
	/**
	 * The entry point of the application.
	 * @param args The command line arguments.
	 * @see CC#cleanExit
	 */
	public static void
	main(String[] args) {
		scripts = args;
		CC.initJSampler();
		initGUI();
	}

	/**
	 * Schedule the specified script to be run when connection is established.
	 * @param fileName The path of the script to run.
	 */
	public static void
	open(String fileName) {
		CC.getLogger().warning(fileName);
		if(fileName == null) return;
		if(CC.getClient().isConnected()) {
			CC.getMainFrame().runScript(fileName);
			return;
		}
		
		if(scripts == null) {
			scripts = new String[1];
			scripts[0] = fileName;
			CC.addConnectionEstablishedListener(new ConnectionEstablishedListener());
		} else {
			String[] files = new String[scripts.length + 1];
			for(int i = 0; i < scripts.length; i++) {
				files[i] = scripts[i];
			}
			files[files.length - 1] = fileName;
			scripts = files;
		}
	}
	
	private static void
	initGUI() {
		JSViews.parseManifest();

		PDUtils.runOnUiThread(new Runnable() {
			public void
			run() { initGUI0(); }
		});
	}
	
	private static void
	initGUI0() {
		JSViews.setView(JSViews.getDefaultView());
		if(scripts != null) {
			CC.addConnectionEstablishedListener(new ConnectionEstablishedListener());
		}
		
		JSUtils.checkJSamplerHome();
		CC.loadOrchestras();
		CC.loadServerList();
		CC.connect();
	}
	
	private static class ConnectionEstablishedListener implements GenericListener, Runnable {
		public void
		jobDone(GenericEvent e) {
			if(scripts == null) return;
			for(String s : scripts) CC.getMainFrame().runScript(s);
			scripts = null;
			PDUtils.runOnUiThread(this);
		}
		
		public void
		run() { CC.removeConnectionEstablishedListener(this); }
	}
}
