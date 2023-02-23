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

package org.jsampler.view.classic;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.jsampler.JSPrefs;

import org.jsampler.view.InstrumentsDbTableView;
import org.jsampler.view.InstrumentsDbTreeView;
import org.jsampler.view.BasicIconSet;


/**
 *
 * @author Grigor Iliev
 */
public class ViewConfig extends org.jsampler.view.swing.ViewConfig {
	private InstrumentsDbTreeView instrumentsDbTreeView = new TreeView();
	private InstrumentsDbTableView instrumentsDbTableView = new TableView();
	private IconSet basicIconSet = new IconSet();
	
	/** Creates a new instance of <code>ViewConfig</code> */
	public
	ViewConfig() {
		
	}
	
	@Override
	public JSPrefs
	preferences() { return ClassicPrefs.preferences(); }
	
	@Override
	public InstrumentsDbTreeView
	getInstrumentsDbTreeView() { return instrumentsDbTreeView; }
	
	@Override
	public InstrumentsDbTableView
	getInstrumentsDbTableView() { return instrumentsDbTableView; }
	
	@Override
	public org.jsampler.view.SamplerBrowserView
	getSamplerBrowserView() { return null; }
	
	@Override
	public BasicIconSet
	getBasicIconSet() { return basicIconSet; }
	
	private class TreeView implements InstrumentsDbTreeView {
		@Override
		public Icon
		getRootIcon() { return Res.iconDb16; }
		
		@Override
		public Icon
		getClosedIcon() { return Res.iconFolder16; }
	
		@Override
		public Icon
		getOpenIcon() { return Res.iconFolderOpen16; }
	
		@Override
		public Icon
		getInstrumentIcon() { return Res.iconInstrument16; }
	
		@Override
		public Icon
		getGigInstrumentIcon() { return Res.iconInstrument16; }
	}
	
	private static class TableView implements InstrumentsDbTableView {
		@Override
		public Icon
		getFolderIcon() { return Res.iconFolder16; }
	
		@Override
		public Icon
		getInstrumentIcon() { return Res.iconInstrument16; }
	
		@Override
		public Icon
		getGigInstrumentIcon() { return Res.iconInstrument16; }
	}
	
	private class IconSet implements BasicIconSet {
		@Override
		public ImageIcon
		getApplicationIcon() { return Res.appIcon; }
		
		@Override
		public Icon
		getBack16Icon() { return Res.iconBack16; }
	
		@Override
		public Icon
		getUp16Icon() { return Res.iconUp16; }
	
		@Override
		public Icon
		getForward16Icon() { return Res.iconForward16; }
		
		@Override
		public Icon
		getReload16Icon() { return Res.iconReload16; }
		
		@Override
		public Icon
		getPreferences16Icon() { return Res.iconPreferences16; }
		
		@Override
		public Icon
		getWarning32Icon() { return Res.iconWarning32; }
		
		@Override
		public Icon
		getQuestion32Icon() { return Res.iconQuestion32; }
	}
	
	@Override
	public boolean
	getInstrumentsDbSupport() { return true; }
}
