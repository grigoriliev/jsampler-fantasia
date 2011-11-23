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


package org.jsampler.view;

import java.util.StringTokenizer;
import java.util.Vector;

import java.util.jar.Attributes;
import java.util.jar.Manifest;

import java.util.logging.Level;

import net.sf.juife.PDUtils;

import org.jsampler.CC;
import org.jsampler.HF;
import org.jsampler.Prefs;

/**
 * This class provides information about the available views in the current distribution.
 * @author Grigor Iliev
 */
public class JSViews {
	private static Vector<ViewEntry> viewEntries = new Vector<ViewEntry>();
	private static String currentView = null;
	private static String defaultView = null;
	
	/** Forbits instantiation of JSViews */
	private
	JSViews() { }
	
	/**
	 * Gets a list of all available views.
	 * @return A list of all available views.
	 */
	public static String[]
	getAvailableViews() {
		String[] views = new String[viewEntries.size()];
		for(int i = 0; i < viewEntries.size(); i++) views[i] = viewEntries.get(i).name;
		return views;
	}
	
	/**
	 * Gets the current view name of this distribution.
	 * @return The current view name of this distribution.
	 */
	public static String
	getCurrentView() { return currentView; }
	
	/**
	 * Gets the default view name of this distribution.
	 * @return The default view name of this distribution.
	 */
	public static String
	getDefaultView() { return defaultView; }
	
	/**
	 * Changes the current view.
	 * This method should be invoked only from the event-dispatching thread.
	 * @param viewName The new view to be used.
	 */
	public static void
	setView(String viewName) {
		if(viewName == null) {
			CC.getLogger().info("viewName is null!");
			return;
		}
		
		ViewEntry entry = null;
		
		for(ViewEntry ve : viewEntries) {
			if(viewName.equals(ve.name)) {
				entry = ve;
				break;
			}
		}
		
		if(entry == null) {
			CC.getLogger().info("Missing view: " + viewName);
			return;
		}
		if(CC.getMainFrame() != null) {
			PDUtils.runOnUiThread(new Runnable() {
				public void
				run() { CC.getMainFrame().setVisible(false); }
			});
		}
		
		try {
			CC.setViewConfig (
				(JSViewConfig)Class.forName(entry.viewConfig).newInstance()
			);
			CC.getViewConfig().setUIDefaultFont(Prefs.getInterfaceFont());
		} catch(Exception e) {
			CC.getLogger().info(HF.getErrorMessage(e));
			return;
		}
		
		setView0(entry);
	}
	
	private static void
	setView0(ViewEntry entry) {
		try {
			CC.setMainFrame((JSMainFrame)Class.forName(entry.mainFrame).newInstance());
			CC.setProgressIndicator (
				(JSProgress)Class.forName(entry.progressIndicator).newInstance()
			);
			CC.getMainFrame().setVisible(true);
			currentView = entry.name;
		} catch(Exception e) {
			CC.getLogger().log(Level.INFO, HF.getErrorMessage(e), e);
			return;
		}
	}
	
	/**
	 * Parses the manifest file <code>views.mf</code> to gather information
	 * about the available views in this JSampler distribution.
	 */
	public static void
	parseManifest() {
		viewEntries.removeAllElements();
		
		try {
			Manifest m = new Manifest(JSViews.class.getResourceAsStream("views.mf"));
			String s = m.getMainAttributes().getValue("JS-Views");
			if(s == null) {
				CC.getLogger().warning("Missing manifest attribute: JS-Views");
				return;
			}
			
			StringTokenizer st = new StringTokenizer(s);
			while(st.hasMoreTokens()) {
				s = st.nextToken();
				Attributes a = m.getAttributes(s);
				
				if(a == null) {
					CC.getLogger().warning("Missing manifest entry: " + s);
					continue;
				}
				
				ViewEntry ve = new ViewEntry();
				ve.name = a.getValue("View-Name");
				ve.viewConfig = a.getValue("View-Config");
				ve.mainFrame = a.getValue("Main-Frame");
				ve.progressIndicator = a.getValue("Progress-Indicator");
				
				viewEntries.addElement(ve);
			}
			
			s = m.getMainAttributes().getValue("JS-Default-View");
			Attributes a = m.getAttributes(s);
			if(a != null) defaultView = a.getValue("View-Name");
		} catch(Exception x) {
			CC.getLogger().log(Level.INFO, HF.getErrorMessage(x), x);
		}
	}
	
	private static class ViewEntry {
		String name;
		String viewConfig;
		String mainFrame;
		String progressIndicator;
	}
}
