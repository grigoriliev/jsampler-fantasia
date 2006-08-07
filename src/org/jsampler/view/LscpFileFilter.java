/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005, 2006 Grigor Kirilov Iliev
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

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * A file filter for LSCP script files.
 * @author Grigor Iliev
 */
public class LscpFileFilter extends FileFilter {
	
	/** Creates a new instance of LscpFileFilter */
	public LscpFileFilter() {
	}
	
	/**
	 * Returns <code>true</code> if the specified file is a LSCP script.
	 * The file is recognized by its extension.
	 * @return <code>true</code> if the specified file is a LSCP script;
	 * <code>false</code> otherwise.
	 */
	public boolean
	accept(File f) {
		if(f.isDirectory()) return true;
		String s = f.getName();
		int i = s.lastIndexOf('.');
		if(i == -1) return false;
		s = s.substring(i);
		
		return s.equalsIgnoreCase(".lscp");
	}
	
	/**
	 * The description of this filter.
	 * @return The description of this filter: <b>LSCP Script Files (*.lscp)</b>.
	 */
	public String
	getDescription() { return "LSCP Script Files (*.lscp)"; }
}
