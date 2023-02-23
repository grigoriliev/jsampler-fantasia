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

package org.jsampler.view.swing;

import java.awt.Font;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.FontUIResource;

import org.jsampler.CC;
import org.jsampler.view.JSViewConfig;

public abstract class ViewConfig extends JSViewConfig<Icon> {
	public int
	getDefaultModKey() {
		return CC.isMacOS() ? KeyEvent.META_MASK : KeyEvent.CTRL_MASK;
	}

	/**  Constructs a new multicolumn menu with the supplied string as its text. */
	public javax.swing.JMenu
	createMultiColumnMenu(String s) { return new net.sf.juife.swing.MultiColumnMenu(s); }

	/**  Constructs a new multicolumn popup menu. */
	public JPopupMenu
	createMultiColumnPopupMenu()
	{ return new net.sf.juife.swing.MultiColumnMenu.PopupMenu(); }
	
	private static final Vector<ChangeListener> idtmListeners = new Vector<ChangeListener>();
	private static InstrumentsDbTreeModel instrumentsDbTreeModel = null;
	
	/**
	 * Gets the tree model of the instruments database.
	 * If the currently used view doesn't have instruments
	 * database support the tree model is initialized on first use.
	 * @return The tree model of the instruments database or
	 * <code>null</code> if the backend doesn't have instruments database support.
	 * @see org.jsampler.view.JSViewConfig#getInstrumentsDbSupport
	 */
	public static InstrumentsDbTreeModel
	getInstrumentsDbTreeModel() {
		if(CC.getSamplerModel().getServerInfo() == null) return null;
		if(!CC.getSamplerModel().getServerInfo().hasInstrumentsDbSupport()) return null;
		
		if(instrumentsDbTreeModel == null) {
			instrumentsDbTreeModel = new InstrumentsDbTreeModel();
			for(ChangeListener l : idtmListeners) l.stateChanged(null);
		}
		
		return instrumentsDbTreeModel;
	}
	
	public void initInstrumentsDbTreeModel() {
		getInstrumentsDbTreeModel();
	}
	
	public void resetInstrumentsDbTreeModel() {
		if(instrumentsDbTreeModel != null) {
			instrumentsDbTreeModel.reset();
			instrumentsDbTreeModel = null;
		}
	}
	
	public static void
	addInstrumentsDbChangeListener(ChangeListener l) {
		idtmListeners.add(l);
	}
	
	public static void
	removeInstrumentsDbChangeListener(ChangeListener l) {
		idtmListeners.remove(l);
	}
	
	/**
	 * Shows a dialog with the specified error message.
	 * @param msg The error message to be shown.
	 */
	public void
	showErrorMessage(String msg) {
		SHF.showErrorMessage(msg);
	}
	
	/**
	 * Shows a dialog with error message.
	 * @param e The <code>Exception</code> from which the error message is obtained.
	 */
	public void
	showErrorMessage(Exception e) {
		SHF.showErrorMessage(e);
	}
	
	/**
	 * Shows a dialog with error message.
	 * @param e The <code>Exception</code> from which the error message is obtained.
	 * @param prefix The prefix to be added to the error message.
	 */
	public void
	showErrorMessage(Exception e, String prefix) {
		SHF.showErrorMessage(e, prefix);
	}
	
	/**
	 * Sets the default font to be used in the GUI.
	 * @param fontName The name of the font to be used as default.
	 */
	public void
	setUIDefaultFont(String fontName) {
		if(fontName == null) return;
		
		java.util.Enumeration keys = UIManager.getDefaults().keys();
		while(keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if(value instanceof FontUIResource) {
				Font f = (FontUIResource)value;
				FontUIResource fr =
					new FontUIResource(fontName, f.getStyle(), f.getSize());
				UIManager.put(key, fr);
			}
		}
	}
}
