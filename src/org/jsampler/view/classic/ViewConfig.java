/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2007 Grigor Iliev <grigor@grigoriliev.com>
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

package org.jsampler.view.classic;

import javax.swing.Icon;

import org.jsampler.JSPrefs;

import org.jsampler.view.InstrumentsDbTableView;
import org.jsampler.view.InstrumentsDbTreeView;
import org.jsampler.view.JSViewConfig;

/**
 *
 * @author Grigor Iliev
 */
public class ViewConfig extends JSViewConfig {
	private InstrumentsDbTreeView instrumentsDbTreeView = new TreeView();
	private InstrumentsDbTableView instrumentsDbTableView = new TableView();
	
	/** Creates a new instance of <code>ViewConfig</code> */
	public
	ViewConfig() {
		
	}
	
	public JSPrefs
	preferences() { return ClassicPrefs.preferences(); }
	
	public InstrumentsDbTreeView
	getInstrumentsDbTreeView() { return instrumentsDbTreeView; }
	
	public InstrumentsDbTableView
	getInstrumentsDbTableView() { return instrumentsDbTableView; }
	
	private class TreeView implements InstrumentsDbTreeView {
		public Icon
		getRootIcon() { return Res.iconDb16; }
		
		public Icon
		getClosedIcon() { return Res.iconFolder16; }
	
		public Icon
		getOpenIcon() { return Res.iconFolderOpen16; }
	
		public Icon
		getInstrumentIcon() { return Res.iconInstrument16; }
	
		public Icon
		getGigInstrumentIcon() { return Res.iconInstrument16; }
	}
	
	private static class TableView implements InstrumentsDbTableView {
		public Icon
		getFolderIcon() { return Res.iconFolder16; }
	
		public Icon
		getInstrumentIcon() { return Res.iconInstrument16; }
	
		public Icon
		getGigInstrumentIcon() { return Res.iconInstrument16; }
	}
	
	public boolean
	getInstrumentsDbSupport() { return true; }
}
