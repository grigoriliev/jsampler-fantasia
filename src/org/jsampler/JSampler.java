/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2008 Grigor Iliev <grigor@grigoriliev.com>
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;

import org.jsampler.view.JSViews;

/**
 * The main class of the application.
 * @author  Grigor Iliev
 */
public class JSampler {
	/** The application name. */
	public final static String NAME = "JSampler";
	
	/** The application version. */
	public final static String VERSION = "0.8a";
	
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
	
	private static void
	initGUI() {
		JSViews.parseManifest();
		JSViews.setView(JSViews.getDefaultView());
		
		SwingUtilities.invokeLater(new Runnable() {
			public void
			run() { initGUI0(); }
		});
	}
	
	private static void
	initGUI0() {
		CC.addConnectionEstablishedListener(new ConnectionEstablishedListener());
		
		CC.checkJSamplerHome();
		CC.loadOrchestras();
		CC.loadServerList();
		CC.connect();
	}
	
	private static class ConnectionEstablishedListener implements ActionListener, Runnable {
		@Override
		public void
		actionPerformed(ActionEvent e) {
			if(scripts == null) return;
			for(String s : scripts) CC.getMainFrame().runScript(s);
			SwingUtilities.invokeLater(this);
		}
		
		@Override
		public void
		run() { CC.removeConnectionEstablishedListener(this); }
	}
}
