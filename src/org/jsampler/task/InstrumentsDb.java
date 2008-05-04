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

package org.jsampler.task;

import java.util.logging.Level;

import org.jsampler.CC;
import org.jsampler.HF;

import org.linuxsampler.lscp.DbDirectoryInfo;
import org.linuxsampler.lscp.DbInstrumentInfo;
import org.linuxsampler.lscp.DbSearchQuery;
import org.linuxsampler.lscp.ScanJobInfo;

import static org.jsampler.JSI18n.i18n;
import static org.linuxsampler.lscp.Client.ScanMode;

/**
 * Provides the instruments database specific tasks.
 * @author Grigor Iliev
 */
public class InstrumentsDb {
	
	/** Forbits the instantiation of this class. */
	private InstrumentsDb() { }
	
	/**
	 * This task retrieves the number of directories in the specified directory.
	 */
	public static class GetDirectoryCount extends EnhancedTask<Integer> {
		private String dir;
		private boolean recursive;
		
		/**
		 * Creates a new instance of <code>GetDirectoryCount</code>.
		 * @param dir The absolute path name of the directory.
		 * @param recursive If <code>true</code>, the number of all directories
		 * in the specified subtree will be returned.
		 */
		public
		GetDirectoryCount(String dir, boolean recursive) {
			setTitle("InstrumentsDb.GetDirectoryCount_task");
			setDescription(i18n.getMessage("InstrumentsDb.GetDirectoryCount.desc"));
			this.dir = dir;
			this.recursive = recursive;
		}
	
		/** The entry point of the task. */
		public void
		run() {
			try { setResult(CC.getClient().getDbDirectoryCount(dir, recursive)); }
			catch(Exception x) {
				setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
				CC.getLogger().log(Level.FINE, getErrorMessage(), x);
			}
		}
	}
	
	/**
	 * This task retrieves the number of instruments in the specified directory.
	 */
	public static class GetInstrumentCount extends EnhancedTask<Integer> {
		private String dir;
		private boolean recursive;
		
		/**
		 * Creates a new instance of <code>GetInstrumentCount</code>.
		 * @param dir The absolute path name of the directory.
		 * @param recursive If <code>true</code>, the number of all instruments
		 * in the specified subtree will be returned.
		 */
		public
		GetInstrumentCount(String dir, boolean recursive) {
			setTitle("InstrumentsDb.GetInstrumentCount_task");
			setDescription(i18n.getMessage("InstrumentsDb.GetInstrumentCount.desc"));
			this.dir = dir;
			this.recursive = recursive;
		}
	
		/** The entry point of the task. */
		public void
		run() {
			try { setResult(CC.getClient().getDbInstrumentCount(dir, recursive)); }
			catch(Exception x) {
				setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
				CC.getLogger().log(Level.FINE, getErrorMessage(), x);
			}
		}
	}
	
	/**
	 * This task retrieves the list of directories in the specified directory.
	 */
	public static class GetDrectories extends EnhancedTask<DbDirectoryInfo[]> {
		private String dir;
		
		/**
		 * Creates a new instance of <code>GetDrectories</code>.
		 * @param dir The absolute path name of the directory.
		 */
		public
		GetDrectories(String dir) {
			setTitle("InstrumentsDb.GetDrectories_task");
			setDescription(i18n.getMessage("InstrumentsDb.GetDrectories.desc"));
			this.dir = dir;
		}
	
		/** The entry point of the task. */
		public void
		run() {
			try { setResult(CC.getClient().getDbDirectories(dir)); }
			catch(Exception x) {
				setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
				CC.getLogger().log(Level.FINE, getErrorMessage(), x);
			}
		}
		
		public String
		getDirectory() { return dir; }
		
		/**
		 * Used to decrease the traffic. All task in the queue
		 * equal to this are removed if added using {@link org.jsampler.CC#scheduleTask}.
		 * @see org.jsampler.CC#addTask
		 */
		public boolean
		equals(Object obj) {
			if(obj == null) return false;
			if(!(obj instanceof GetDrectories)) return false;
			String d = ((GetDrectories)obj).getDirectory();
			if(getDirectory() == null) {
				return d == null;
			}
			if(!getDirectory().equals(d)) return false;
			
			return true;
		}
	}
	
	
	/**
	 * This task retrieves information about a directory.
	 */
	public static class GetDrectory extends EnhancedTask<DbDirectoryInfo> {
		private String dir;
		
		/**
		 * Creates a new instance of <code>GetDrectory</code>.
		 * @param dir The absolute path name of the directory.
		 */
		public
		GetDrectory(String dir) {
			setTitle("InstrumentsDb.GetDrectory_task");
			setDescription(i18n.getMessage("InstrumentsDb.GetDrectory.desc"));
			this.dir = dir;
		}
	
		/** The entry point of the task. */
		public void
		run() {
			try { setResult(CC.getClient().getDbDirectoryInfo(dir)); }
			catch(Exception x) {
				setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
				CC.getLogger().log(Level.FINE, getErrorMessage(), x);
			}
		}
	}
	
	/**
	 * This task creates a new directory.
	 */
	public static class CreateDirectory extends EnhancedTask {
		private String dir;
		
		/**
		 * Creates a new instance of <code>CreateDirectory</code>.
		 * @param dir The absolute path name of the directory to add.
		 */
		public
		CreateDirectory(String dir) {
			setTitle("InstrumentsDb.CreateDirectory_task");
			setDescription(i18n.getMessage("InstrumentsDb.CreateDirectory.desc"));
			this.dir = dir;
		}
	
		/** The entry point of the task. */
		public void
		run() {
			try { CC.getClient().addDbDirectory(dir); }
			catch(Exception x) {
				setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
				CC.getLogger().log(Level.FINE, getErrorMessage(), x);
			}
		}
	}
	
	/**
	 * This task renames the specified directory.
	 */
	public static class RenameDirectory extends EnhancedTask {
		private String dir;
		private String newName;
		
		/**
		 * Creates a new instance of <code>RenameDirectory</code>.
		 * @param dir The absolute path name of the directory to rename.
		 * @param newName The new name for the specified directory.
		 */
		public
		RenameDirectory(String dir, String newName) {
			setTitle("InstrumentsDb.RenameDirectory_task");
			setDescription(i18n.getMessage("InstrumentsDb.RenameDirectory.desc"));
			this.dir = dir;
			this.newName = newName;
		}
	
		/** The entry point of the task. */
		public void
		run() {
			try { CC.getClient().renameDbDirectory(dir, newName); }
			catch(Exception x) {
				setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
				CC.getLogger().log(Level.FINE, getErrorMessage(), x);
			}
		}
	}
	
	/**
	 * This task changes the description of a directory.
	 */
	public static class SetDirectoryDescription extends EnhancedTask {
		private String dir;
		private String desc;
		
		/**
		 * Creates a new instance of <code>SetDirectoryDescription</code>.
		 * @param dir The absolute path name of the directory.
		 * @param desc The new description for the directory.
		 */
		public
		SetDirectoryDescription(String dir, String desc) {
			setTitle("InstrumentsDb.SetDirectoryDescription_task");
			String s = i18n.getMessage("InstrumentsDb.SetDirectoryDescription.desc");
			setDescription(s);
			this.dir = dir;
			this.desc = desc;
		}
	
		/** The entry point of the task. */
		public void
		run() {
			try { CC.getClient().setDbDirectoryDescription(dir, desc); }
			catch(Exception x) {
				setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
				CC.getLogger().log(Level.FINE, getErrorMessage(), x);
			}
		}
	}
	
	/**
	 * This task removes the specified directories.
	 */
	public static class RemoveDirectories extends EnhancedTask {
		private DbDirectoryInfo[] directories;
		
		/**
		 * Creates a new instance of <code>RemoveDirectories</code>.
		 * @param directories The directories to remove.
		 */
		public
		RemoveDirectories(DbDirectoryInfo[] directories) {
			setTitle("InstrumentsDb.RemoveDirectories_task");
			setDescription(i18n.getMessage("InstrumentsDb.RemoveDirectories.desc"));
			this.directories = directories;
		}
	
		/** The entry point of the task. */
		public void
		run() {
			try {
				removeDirectories();
			} catch(Exception x) {
				setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
				setErrorDetails(x);
				CC.getLogger().log(Level.FINE, getErrorMessage(), x);
			}
		}
		
		private void
		removeDirectories() throws Exception {
			if(directories == null || directories.length == 0) return;
			if(directories.length == 1) {
				String path = directories[0].getDirectoryPath();
				CC.getClient().removeDbDirectory(path, true);
			} else {
				String[] dirs = new String[directories.length];
				for(int i = 0; i < directories.length; i++) {
					dirs[i] = directories[i].getDirectoryPath();
				}
			
				CC.getClient().removeDbDirectories(dirs, true);
			}
		}
	}
	
	/**
	 * This task finds all directories in the specified directory
	 * that corresponds to the provided search criterias.
	 */
	public static class FindDirectories extends EnhancedTask<DbDirectoryInfo[]> {
		private String dir;
		private DbSearchQuery query;
		
		/**
		 * Creates a new instance of <code>FindDirectories</code>.
		 * @param dir The absolute path name of the directory.
		 * @param query Provides the search criterias.
		 */
		public
		FindDirectories(String dir, DbSearchQuery query) {
			setTitle("InstrumentsDb.FindDirectories_task");
			setDescription(i18n.getMessage("InstrumentsDb.FindDirectories.desc"));
			this.dir = dir;
			this.query = query;
		}
	
		/** The entry point of the task. */
		public void
		run() {
			try { setResult(CC.getClient().findDbDirectories(dir, query)); }
			catch(Exception x) {
				setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
				CC.getLogger().log(Level.FINE, getErrorMessage(), x);
			}
		}
	}
	
	/**
	 * This task retrieves the list of instruments in the specified directory.
	 */
	public static class GetInstruments extends EnhancedTask<DbInstrumentInfo[]> {
		private String dir;
		
		/**
		 * Creates a new instance of <code>GetInstruments</code>.
		 * @param dir The absolute path name of the directory.
		 */
		public
		GetInstruments(String dir) {
			setTitle("InstrumentsDb.GetInstruments_task");
			setDescription(i18n.getMessage("InstrumentsDb.GetInstruments.desc"));
			this.dir = dir;
		}
	
		/** The entry point of the task. */
		public void
		run() {
			try { setResult(CC.getClient().getDbInstruments(dir)); }
			catch(Exception x) {
				setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
				setErrorDetails(x);
				CC.getLogger().log(Level.FINE, getErrorMessage(), x);
			}
		}
		
		public String
		getDirectory() { return dir; }
		
		/**
		 * Used to decrease the traffic. All task in the queue
		 * equal to this are removed if added using {@link org.jsampler.CC#scheduleTask}.
		 * @see org.jsampler.CC#addTask
		 */
		public boolean
		equals(Object obj) {
			if(obj == null) return false;
			if(!(obj instanceof GetInstruments)) return false;
			String d = ((GetInstruments)obj).getDirectory();
			if(getDirectory() == null) {
				return d == null;
			}
			if(!getDirectory().equals(d)) return false;
			return true;
		}
	}
	
	/**
	 * This task retrieves information about an instrument.
	 */
	public static class GetInstrument extends EnhancedTask<DbInstrumentInfo> {
		private String instr;
		
		/**
		 * Creates a new instance of <code>GetInstrument</code>.
		 * @param instr The absolute path name of the instrument.
		 */
		public
		GetInstrument(String instr) {
			setTitle("InstrumentsDb.GetInstrument_task");
			setDescription(i18n.getMessage("InstrumentsDb.GetInstrument.desc"));
			this.instr = instr;
		}
	
		/** The entry point of the task. */
		public void
		run() {
			try { setResult(CC.getClient().getDbInstrumentInfo(instr)); }
			catch(Exception x) {
				setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
				CC.getLogger().log(Level.FINE, getErrorMessage(), x);
			}
		}
	}
	
	/**
	 * This task finds all instruments in the specified directory
	 * that corresponds to the provided search criterias.
	 */
	public static class FindInstruments extends EnhancedTask<DbInstrumentInfo[]> {
		private String dir;
		private DbSearchQuery query;
		
		/**
		 * Creates a new instance of <code>FindInstruments</code>.
		 * @param dir The absolute path name of the directory.
		 * @param query Provides the search criterias.
		 */
		public
		FindInstruments(String dir, DbSearchQuery query) {
			setTitle("InstrumentsDb.FindInstruments_task");
			setDescription(i18n.getMessage("InstrumentsDb.FindInstruments.desc"));
			this.dir = dir;
			this.query = query;
		}
	
		/** The entry point of the task. */
		public void
		run() {
			try { setResult(CC.getClient().findDbInstruments(dir, query)); }
			catch(Exception x) {
				setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
				CC.getLogger().log(Level.FINE, getErrorMessage(), x);
			}
		}
	}
	
	/**
	 * This task renames the specified instrument.
	 */
	public static class RenameInstrument extends EnhancedTask {
		private String instr;
		private String newName;
		
		/**
		 * Creates a new instance of <code>RenameInstrument</code>.
		 * @param instr The absolute path name of the instrument to rename.
		 * @param newName The new name for the specified instrument.
		 */
		public
		RenameInstrument(String instr, String newName) {
			setTitle("InstrumentsDb.RenameInstrument_task");
			setDescription(i18n.getMessage("InstrumentsDb.RenameInstrument.desc"));
			this.instr = instr;
			this.newName = newName;
		}
	
		/** The entry point of the task. */
		public void
		run() {
			try { CC.getClient().renameDbInstrument(instr, newName); }
			catch(Exception x) {
				setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
				CC.getLogger().log(Level.FINE, getErrorMessage(), x);
			}
		}
	}
	
	/**
	 * This task changes the description of an instrument.
	 */
	public static class SetInstrumentDescription extends EnhancedTask {
		private String instr;
		private String desc;
		
		/**
		 * Creates a new instance of <code>SetInstrumentDescription</code>.
		 * @param instr The absolute path name of the instrument.
		 * @param desc The new description for the instrument.
		 */
		public
		SetInstrumentDescription(String instr, String desc) {
			setTitle("InstrumentsDb.SetInstrumentDescription_task");
			String s = i18n.getMessage("InstrumentsDb.SetInstrumentDescription.desc");
			setDescription(s);
			this.instr = instr;
			this.desc = desc;
		}
	
		/** The entry point of the task. */
		public void
		run() {
			try { CC.getClient().setDbInstrumentDescription(instr, desc); }
			catch(Exception x) {
				setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
				CC.getLogger().log(Level.FINE, getErrorMessage(), x);
			}
		}
	}
	
	/**
	 * This task removes the specified instruments.
	 */
	public static class RemoveInstruments extends EnhancedTask {
		private DbInstrumentInfo[] instruments;
		
		/**
		 * Creates a new instance of <code>RemoveInstruments</code>.
		 * @param instruments The instruments to remove.
		 */
		public
		RemoveInstruments(DbInstrumentInfo[] instruments) {
			setTitle("InstrumentsDb.RemoveInstruments_task");
			setDescription(i18n.getMessage("InstrumentsDb.RemoveInstruments.desc"));
			this.instruments = instruments;
		}
	
		/** The entry point of the task. */
		public void
		run() {
			try {
				removeInstruments();
			} catch(Exception x) {
				setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
				setErrorDetails(x);
				CC.getLogger().log(Level.FINE, getErrorMessage(), x);
			}
		}
		
		private void
		removeInstruments() throws Exception {
			if(instruments == null || instruments.length == 0) return;
			if(instruments.length == 1) {
				String path = instruments[0].getInstrumentPath();
				CC.getClient().removeDbInstrument(path);
			} else {
				String[] instrs = new String[instruments.length];
				for(int i = 0; i < instruments.length; i++) {
					instrs[i] = instruments[i].getInstrumentPath();
				}
			
				CC.getClient().removeDbInstruments(instrs);
			}
		}
	}
	
	/**
	 * This task adds instruments from an instrument file to the instruments database.
	 */
	public static class AddInstrumentsFromFile extends EnhancedTask<Integer> {
		private String dbDir;
		private String filePath;
		private int instrIndex;
		
		/**
		 * Creates a new instance of <code>AddInstrumentsFromFile</code>.
		 * @param dbDir The absolute path name of the database directory
		 * in which all instruments from the specified instrument file will be added.
		 * @param filePath The absolute path name of the instrument file.
		 */
		public
		AddInstrumentsFromFile(String dbDir, String filePath) {
			this(dbDir, filePath, -1);
		}
		
		/**
		 * Creates a new instance of <code>AddInstrumentsFromFile</code>.
		 * @param dbDir The absolute path name of the database directory
		 * in which the specified instrument will be added.
		 * @param filePath The absolute path name of the instrument file.
		 * @param instrIndex The index of the instrument
		 * (in the given instrument file) to add. If -1 is specified, all
		 * instruments in the given instrument file will be added.
		 */
		public
		AddInstrumentsFromFile(String dbDir, String filePath, int instrIndex) {
			setTitle("InstrumentsDb.AddInstrumentsFromFile_task");
			String s = i18n.getMessage("InstrumentsDb.AddInstrumentsFromFile.desc");
			setDescription(s);
			this.dbDir = dbDir;
			this.filePath = filePath;
			this.instrIndex = instrIndex;
		}
	
		/** The entry point of the task. */
		public void
		run() {
			try {
				int i;
				if(instrIndex != -1) {
					i = CC.getClient().addDbInstrument (
						dbDir, filePath, instrIndex, true
					);
				} else {
					i = CC.getClient().addDbInstruments(dbDir, filePath, true);
				}
				
				setResult(i);
			} catch(Exception x) {
				setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
				CC.getLogger().log(Level.FINE, getErrorMessage(), x);
			}
		}
	}
	
	/**
	 * This task adds instruments from a directory to the instruments database.
	 */
	public static class AddInstruments extends EnhancedTask<Integer> {
		private String dbDir;
		private String fsDir;
		private boolean flat;
		
		/**
		 * Creates a new instance of <code>AddInstruments</code>.
		 * @param dbDir The absolute path name of the database directory
		 * in which all instruments from the specified directory will be added.
		 * @param fsDir The absolute path name of the file system directory.
		 */
		public
		AddInstruments(String dbDir, String fsDir) {
			this(dbDir, fsDir, false);
		}
		
		/**
		 * Creates a new instance of <code>AddInstruments</code>.
		 * @param dbDir The absolute path name of the database directory
		 * in which all instruments from the specified directory will be added.
		 * @param fsDir The absolute path name of the file system directory.
		 * @param flat If <code>true</code>, the respective subdirectory structure
		 * will not be re-created in the supplied database directory.
		 */
		public
		AddInstruments(String dbDir, String fsDir, boolean flat) {
			setTitle("InstrumentsDb.AddInstruments_task");
			String s = i18n.getMessage("InstrumentsDb.AddInstruments.desc");
			setDescription(s);
			this.dbDir = dbDir;
			this.fsDir = fsDir;
			this.flat = flat;
		}
	
		/** The entry point of the task. */
		public void
		run() {
			try {
				int i;
				if(flat) {
					i = CC.getClient().addDbInstruments (
						ScanMode.FLAT, dbDir, fsDir, true
					);
				} else {
					i = CC.getClient().addDbInstruments (
						ScanMode.RECURSIVE, dbDir, fsDir, true
					);
				}
				
				setResult(i);
			}
			catch(Exception x) {
				setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
				CC.getLogger().log(Level.FINE, getErrorMessage(), x);
			}
		}
	}
	
	/**
	 * This task adds instruments from a file system directory (excluding 
	 * the instruments in the subdirectories) to the instruments database.
	 */
	public static class AddInstrumentsNonrecursive extends EnhancedTask<Integer> {
		private String dbDir;
		private String fsDir;
		
		/**
		 * Creates a new instance of <code>AddInstrumentsNonrecursive</code>.
		 * @param dbDir The absolute path name of the database directory
		 * in which the instruments from the specified directory (excluding 
		 * the instruments in the subdirectories) will be added.
		 * @param fsDir The absolute path name of the file system directory.
		 */
		public
		AddInstrumentsNonrecursive(String dbDir, String fsDir) {
			setTitle("InstrumentsDb.AddInstrumentsNonrecursive_task");
			String s = i18n.getMessage("InstrumentsDb.AddInstrumentsNonrecursive.desc");
			setDescription(s);
			this.dbDir = dbDir;
			this.fsDir = fsDir;
		}
	
		/** The entry point of the task. */
		public void
		run() {
			try {
				int i = CC.getClient().addDbInstruments (
					ScanMode.NON_RECURSIVE, dbDir, fsDir, true
				);
				
				setResult(i);
			}
			catch(Exception x) {
				setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
				CC.getLogger().log(Level.FINE, getErrorMessage(), x);
			}
		}
	}
	
	/**
	 * This task moves the specified instruments
	 * and directories to the specified location.
	 */
	public static class Move extends EnhancedTask {
		private DbDirectoryInfo[] directories;
		private DbInstrumentInfo[] instruments;
		private String dest;
		
		/**
		 * Creates a new instance of <code>Move</code>.
		 * @param directories The directories to move.
		 * @param instruments The instruments to move.
		 * @param dest The absolute path name of the directory where
		 * the specified instruments and directories will be moved to.
		 */
		public
		Move(DbDirectoryInfo[] directories, DbInstrumentInfo[] instruments, String dest) {
			setTitle("InstrumentsDb.Move_task");
			setDescription(i18n.getMessage("InstrumentsDb.Move.desc"));
			this.directories = directories;
			this.instruments = instruments;
			this.dest = dest;
		}
	
		/** The entry point of the task. */
		public void
		run() {
			try {
				moveInstruments();
				moveDirectories();
			} catch(Exception x) {
				setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
				setErrorDetails(x);
				CC.getLogger().log(Level.FINE, getErrorMessage(), x);
			}
		}
		
		private void
		moveInstruments() throws Exception {
			if(instruments == null || instruments.length == 0) return;
			if(instruments.length == 1) {
				String path = instruments[0].getInstrumentPath();
				CC.getClient().moveDbInstrument(path, dest);
			} else {
				String[] instrs = new String[instruments.length];
				for(int i = 0; i < instruments.length; i++) {
					instrs[i] = instruments[i].getInstrumentPath();
				}
			
				CC.getClient().moveDbInstruments(instrs, dest);
			}
		}
		
		private void
		moveDirectories() throws Exception {
			if(directories == null || directories.length == 0) return;
			if(directories.length == 1) {
				String path = directories[0].getDirectoryPath();
				CC.getClient().moveDbDirectory(path, dest);
			} else {
				String[] dirs = new String[directories.length];
				for(int i = 0; i < directories.length; i++) {
					dirs[i] = directories[i].getDirectoryPath();
				}
			
				CC.getClient().moveDbDirectories(dirs, dest);
			}
		}
	}
	
	/**
	 * This task copies the specified instruments
	 * and directories to the specified location.
	 */
	public static class Copy extends EnhancedTask {
		private DbDirectoryInfo[] directories;
		private DbInstrumentInfo[] instruments;
		private String dest;
		
		/**
		 * Creates a new instance of <code>Copy</code>.
		 * @param directories The directories to copy.
		 * @param instruments The instruments to copy.
		 * @param dest The absolute path name of the directory where
		 * the specified instruments and directories will be copied to.
		 */
		public
		Copy(DbDirectoryInfo[] directories, DbInstrumentInfo[] instruments, String dest) {
			setTitle("InstrumentsDb.Copy_task");
			setDescription(i18n.getMessage("InstrumentsDb.Copy.desc"));
			this.directories = directories;
			this.instruments = instruments;
			this.dest = dest;
		}
	
		/** The entry point of the task. */
		public void
		run() {
			try {
				copyInstruments();
				copyDirectories();
			} catch(Exception x) {
				setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
				setErrorDetails(x);
				CC.getLogger().log(Level.FINE, getErrorMessage(), x);
			}
		}
		
		private void
		copyInstruments() throws Exception {
			if(instruments == null || instruments.length == 0) return;
			if(instruments.length == 1) {
				String path = instruments[0].getInstrumentPath();
				CC.getClient().copyDbInstrument(path, dest);
			} else {
				String[] instrs = new String[instruments.length];
				for(int i = 0; i < instruments.length; i++) {
					instrs[i] = instruments[i].getInstrumentPath();
				}
			
				CC.getClient().copyDbInstruments(instrs, dest);
			}
		}
		
		private void
		copyDirectories() throws Exception {
			if(directories == null || directories.length == 0) return;
			if(directories.length == 1) {
				String path = directories[0].getDirectoryPath();
				CC.getClient().copyDbDirectory(path, dest);
			} else {
				String[] dirs = new String[directories.length];
				for(int i = 0; i < directories.length; i++) {
					dirs[i] = directories[i].getDirectoryPath();
				}
			
				CC.getClient().copyDbDirectories(dirs, dest);
			}
		}
	}
	
	/**
	 * This task gets a list of all instrument files in the database
	 * that that doesn't exist in the filesystem.
	 */
	public static class FindLostInstrumentFiles extends EnhancedTask<String[]> {
		
		/**
		 * Creates a new instance of <code>FindLostInstrumentFiles</code>.
		 */
		public
		FindLostInstrumentFiles() {
			setTitle("InstrumentsDb.FindLostInstrumentFiles_task");
			setDescription(i18n.getMessage("InstrumentsDb.FindLostInstrumentFiles.desc"));
		}
	
		/** The entry point of the task. */
		public void
		run() {
			try { setResult(CC.getClient().findLostDbInstrumentFiles()); }
			catch(Exception x) {
				setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
				CC.getLogger().log(Level.FINE, getErrorMessage(), x);
			}
		}
	}
	
	/**
	 * This task substitutes all occurrences of the specified instrument file
	 * in the database, with the specified new path.
	 */
	public static class SetInstrumentFilePath extends EnhancedTask {
		private String oldPath;
		private String newPath;
		
		/**
		 * Creates a new instance of <code>SetInstrumentFilePath</code>.
		 * @param oldPath The absolute path name of the instrument file to substitute.
		 * @param newPath The new absolute path name.
		 */
		public
		SetInstrumentFilePath(String oldPath, String newPath) {
			setTitle("InstrumentsDb.SetInstrumentFilePath_task");
			setDescription(i18n.getMessage("InstrumentsDb.SetInstrumentFilePath.desc"));
			this.oldPath = oldPath;
			this.newPath = newPath;
		}
	
		/** The entry point of the task. */
		public void
		run() {
			try { CC.getClient().setDbInstrumentFilePath(oldPath, newPath); }
			catch(Exception x) {
				setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
				CC.getLogger().log(Level.FINE, getErrorMessage(), x);
			}
		}
	}
	
	/**
	 * This task retrieves information about a scan job.
	 */
	public static class GetScanJobInfo extends EnhancedTask<ScanJobInfo> {
		private int jobId;
		
		/**
		 * Creates a new instance of <code>GetScanJobInfo</code>.
		 * @param jobId The ID of the scan job.
		 */
		public
		GetScanJobInfo(int jobId) {
			setTitle("InstrumentsDb.GetScanJobInfo_task");
			setDescription(i18n.getMessage("InstrumentsDb.GetScanJobInfo.desc"));
			this.jobId = jobId;
		}
	
		/** The entry point of the task. */
		public void
		run() {
			try { setResult(CC.getClient().getDbInstrumentsJobInfo(jobId)); }
			catch(Exception x) {
				setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
				CC.getLogger().log(Level.FINE, getErrorMessage(), x);
			}
		}
		
		public int
		getJobId() { return jobId; }
		
		/**
		 * Used to decrease the traffic. All task in the queue
		 * equal to this are removed if added using {@link org.jsampler.CC#scheduleTask}.
		 * @see org.jsampler.CC#addTask
		 */
		public boolean
		equals(Object obj) {
			if(obj == null) return false;
			if(!(obj instanceof GetScanJobInfo)) return false;
			if(((GetScanJobInfo)obj).getJobId() != getJobId()) return false;
			
			return true;
		}
	}
	
	/**
	 * This task formats the instruments database.
	 */
	public static class Format extends EnhancedTask {
		/**
		 * Formats the instruments database..
		 */
		public
		Format() {
			setTitle("InstrumentsDb.Format_task");
			String s = i18n.getMessage("InstrumentsDb.Format.desc");
			setDescription(s);
		}
	
		/** The entry point of the task. */
		public void
		run() {
			try { CC.getClient().formatInstrumentsDb(); }
			catch(Exception x) {
				setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
				CC.getLogger().log(Level.FINE, getErrorMessage(), x);
			}
		}
	}
}
