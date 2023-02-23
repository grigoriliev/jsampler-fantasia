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

package org.jsampler.android.view.classic;

import org.jsampler.JSPrefs;
import org.jsampler.view.BasicIconSet;
import org.jsampler.view.InstrumentsDbTableView;
import org.jsampler.view.InstrumentsDbTreeView;
import org.jsampler.view.SamplerBrowserView;

import android.graphics.drawable.Drawable;
import android.util.Log;

public class ViewConfig extends org.jsampler.android.view.ViewConfig {
	@Override
	public JSPrefs
	preferences() { return ClassicPrefs.preferences(); }
	
	/**
	 * Provides UI information for instruments database trees.
	 */
	public InstrumentsDbTreeView<Drawable>
	getInstrumentsDbTreeView() { return null; }
	
	/**
	 * Provides UI information for instruments database tables.
	 */
	public InstrumentsDbTableView<Drawable>
	getInstrumentsDbTableView() { return null; }
	
	public SamplerBrowserView<Drawable>
	getSamplerBrowserView() { return null; }
	
	public BasicIconSet<Drawable>
	getBasicIconSet() { return null; }
	
	public void initInstrumentsDbTreeModel() { }
	
	public void resetInstrumentsDbTreeModel() { }
	
	/**
	 * Shows a dialog with the specified error message.
	 * @param msg The error message to be shown.
	 */
	public void showErrorMessage(String msg) { Log.w("showErrorMessage", msg); }
	
	/**
	 * Shows a dialog with error message.
	 * @param e The <code>Exception</code> from which the error message is obtained.
	 */
	public void showErrorMessage(Exception e) {
		Log.w("showErrorMessage", e.getMessage());
		e.printStackTrace();
	}
	
	/**
	 * Shows a dialog with error message.
	 * @param e The <code>Exception</code> from which the error message is obtained.
	 * @param prefix The prefix to be added to the error message.
	 */
	public void showErrorMessage(Exception e, String prefix) {
		Log.w("showErrorMessage", prefix + ": " + e.getMessage());
		e.printStackTrace();
	}
	
	/**
	 * Sets the default font to be used in the GUI.
	 * @param fontName The name of the font to be used as default.
	 */
	public void setUIDefaultFont(String fontName) {}
	
	public int getDefaultModKey() { return 0; }
}
