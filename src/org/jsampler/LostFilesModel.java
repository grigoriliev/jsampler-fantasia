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

import java.util.Vector;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sf.juife.event.TaskEvent;
import net.sf.juife.event.TaskListener;

import org.jsampler.task.InstrumentsDb.FindLostInstrumentFiles;

/**
 * A data model providing information about the lost instrument files in the instruments database.
 * @author Grigor Iliev
 */
public class LostFilesModel {
	private final Vector<String> lostFiles = new Vector<String>();
	private final Vector<ChangeListener> listeners = new Vector<ChangeListener>();
	
	/** Creates a new instance of <code>LostFilesModel</code> */
	public
	LostFilesModel() { }
	
	/**
	 * Registers the specified listener to be notified when the list
	 * of lost files is updated.
	 * @param l The <code>ChangeListener</code> to register.
	 */
	public void
	addChangeListener(ChangeListener l) { listeners.add(l); }
	
	/**
	 * Removes the specified listener.
	 * @param l The <code>ChangeListener</code> to remove.
	 */
	public void
	removeChangeListener(ChangeListener l) { listeners.remove(l); }
	
	/** Returns a list of all lost files. */
	public String[]
	getLostFiles() {
		return lostFiles.toArray(new String[lostFiles.size()]);
	}
	
	/**
	 * Gets the absolute pathname of the lost file at the specified position.
	 * @param index The position of the lost file to return.
	 * @return The absolute pathname of the lost file at the specified position.
	 */
	public String
	getLostFile(int index) { return lostFiles.get(index); }
	
	/**
	 * Gets the number of lost files in the instruments database.
	 * @return The number of lost files in the instruments database.
	 */
	public int
	getLostFileCount() { return lostFiles.size(); }
	
	/** Updates the list of lost instrument files. */
	public void
	update() {
		final FindLostInstrumentFiles t = new FindLostInstrumentFiles();
		t.addTaskListener(new TaskListener() {
			public void
			taskPerformed(TaskEvent e) {
				if(t.doneWithErrors()) return;
				lostFiles.removeAllElements();
				for(String s : t.getResult()) lostFiles.add(s);
				fireLostFileListUpdated();
			}
		});
		
		lostFiles.removeAllElements();
		fireLostFileListUpdated();
		
		CC.getTaskQueue().add(t);
	}
	
	/** 
	 * Notifies listeners that the list of lost files is updated.
	 */
	private void
	fireLostFileListUpdated() {
		ChangeEvent e = new ChangeEvent(this);
		for(ChangeListener l : listeners) l.stateChanged(e);
	}
}
