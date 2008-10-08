/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2006 Grigor Iliev <grigor@grigoriliev.com>
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

import net.sf.juife.AbstractTask;

import org.linuxsampler.lscp.LSException;


/**
 * This class extends <code>AbstractTask</code> to add new features.
 * @author Grigor Iliev
 */
public abstract class EnhancedTask<R> extends AbstractTask<R> {
	private boolean stopped = false;
	private boolean silent = false;
	
	/**
	 * Marks that the execution of this task was interrupted.
	 */
	public void
	stop() { stopped = true; }
	
	/**
	 * Determines whether the execution of this task was interrupted.
	 * @return <code>true</code> if the execution of this task was interrupted,
	 * <code>false</code> otherwise.
	 */
	public boolean
	isStopped() { return stopped; }
	
	/**
	 * Determines whether an error message should be shown
	 * if the execution of the task fail.
	 */
	public boolean
	isSilent() { return silent; }
	
	/**
	 * DSets whether an error message should be shown
	 * if the execution of the task fail.
	 */
	public void
	setSilent(boolean b) { silent = b; }
	
	/**
	 * Sets the error details provided by the given exception (if the given
	 * exception is <code>LSException</code> instance and contains error details).
	 */
	public void
	setErrorDetails(Exception e) {
		if(e == null) return;
		
		if(e instanceof LSException) {
			LSException x = (LSException)e;
			if(x.getDetails() != null && x.getDetails().length() > 0) {
				setErrorDetails(x.getDetails());
			}
		}
	}
}
