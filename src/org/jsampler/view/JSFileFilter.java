/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2009 Grigor Iliev <grigor@grigoriliev.com>
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
import java.io.FilenameFilter;

import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Grigor Iliev
 */
public abstract class JSFileFilter extends FileFilter implements FilenameFilter {
	private final String[] fileExts;

	public
	JSFileFilter(String fileExt) {
		if(fileExt == null) {
			throw new IllegalArgumentException("fileExt must be non-null");
		}

		fileExts = new String[1];
		fileExts[0] = fileExt;
	}

	public
	JSFileFilter(String[] fileExts) {
		if(fileExts == null) {
			throw new IllegalArgumentException("fileExts must be non-null");
		}

		if(fileExts.length < 1) {
			throw new IllegalArgumentException("fileExts length can't be zero");
		}

		this.fileExts = fileExts;
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
		return acceptFile(f.getName());

	}

	public boolean
	accept(File dir, String name) {
		return acceptFile(name);
	}

	/**
	 * Gets the first extension in the list.
	 */
	public String
	getExtension() { return fileExts[0]; }

	protected boolean
	acceptFile(String fileName) {
		boolean b = false;
		for(String ext : fileExts) {
			b = b || acceptFile(fileName, ext);
		}
		return b;
	}

	private boolean
	acceptFile(String fileName, String ext) {
		int i = fileName.lastIndexOf('.');
		if(i == -1) return false;
		fileName = fileName.substring(i);

		return fileName.equalsIgnoreCase(ext);
	}

	public static class Lscp extends JSFileFilter {
		public
		Lscp() { super(".lscp"); }

		/**
		 * The description of this filter.
		 * @return The description of this filter: <b>LSCP Script Files (*.lscp)</b>.
		 */
		public String
		getDescription() { return "LSCP Script Files (*.lscp)"; }
	}

	public static class Text extends JSFileFilter {
		public
		Text() { super(".txt"); }

		public String
		getDescription() { return "Text Files (*.txt)"; }
	}

	public static class Html extends JSFileFilter {
		public
		Html() { super(".html"); }

		public String
		getDescription() { return "Web Pages (*.html)"; }
	}


	public static class MidiMaps extends JSFileFilter {
		private static final String[] exts = { ".lscp", ".txt", ".html" };

		public
		MidiMaps() { super(exts); }

		public String
		getDescription() { return "Midi Instrument Maps"; }
	}
}
