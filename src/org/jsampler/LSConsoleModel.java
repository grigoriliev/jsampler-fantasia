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

package org.jsampler;

import org.jsampler.event.LSConsoleListener;


/**
 * A data model representing LS Console.
 * @author Grigor Iliev
 */
public interface LSConsoleModel {
	/**
	 * Registers the specified listener for receiving event messages.
	 * @param l The <code>LSConsoleListener</code> to register.
	 */
	public void addLSConsoleListener(LSConsoleListener l);
	
	/**
	 * Removes the specified listener.
	 * @param l The <code>LSConsoleListener</code> to remove.
	 */
	public void removeLSConsoleListener(LSConsoleListener l);
	
	/** Executes the command specified in the command line. */
	public void execCommand();
	
	/**
	 * Gets the last executed command.
	 * @return The last command executed in the LS Console.
	 */
	public String getLastExecutedCommand();
	
	/**
	 * Sets the text in the command line.
	 * @param cmdLine The new command line text.
	 */
	public void setCommandLineText(String cmdLine);
	
	/**
	 * Gets the text in the command line.
	 * @return The command line's text.
	 */
	public String getCommandLineText();
	
	/**
	 * Gets the command history of the current session, including blank lines and comments.
	 * @return The command history of the current session, including blank lines and comments.
	 */
	public String[] getSessionHistory();
	
	/**
	 * Clears the session history.
	 * @see #getSessionHistory
	 */
	public void clearSessionHistory();
	
	/**
	 * Adds the specified <code>command</code> to command history.
	 * @param command The command to be added to command history.
	 */
	public void addToCommandHistory(String command);
	
	/**
	 * Gets the complete command history, excluding blank lines and comments.
	 * @return The complete command history, excluding blank lines and comments.
	 */
	public String[] getCommandHistory();
	
	/**
	 * Determines the maximum number of lines to be kept in the command history.
	 * @return The maximum number of lines to be kept in the command history.
	 */
	public int getCommandHistorySize();
	
	/**
	 * Sets the maximum number of lines to be kept in the command history.
	 * @param size Determines the maximum number of lines to be kept in the command history.
	 */
	public void setCommandHistorySize(int size);
	
	/**
	 * Clears the complete/multisession command history.
	 * @see #getCommandHistory
	 */
	public void clearCommandHistory();
	
	/**
	 * Gets a list of all LSCP commands.
	 * @return A list of all LSCP commands.
	 */
	public String[] getCommandList();
	
	/**
	 * Searches the LSCP command list for commands
	 * containing the string returned by {@link #getCommandLineText}.
	 * @return All commands that contains the string returned by {@link #getCommandLineText}.
	 * @see #getCommandList
	 */
	public String[] searchCommandList();
	
	/**
	 * Searches the LSCP command list for commands containing <code>substring</code>.
	 * @param substring The substring to be used to perform the search.
	 * @return All commands that contains <code>substring</code>.
	 * @see #getCommandList
	 */
	public String[] searchCommandList(String substring);
	
	/**
	 * Searches the command history for commands
	 * containing the string returned by {@link #getCommandLineText}.
	 * @return All commands that contains the string returned by {@link #getCommandLineText}.
	 * @see #getCommandHistory
	 */
	public String[] searchCommandHistory();
	
	/**
	 * Searches the command history for commands containing <code>substring</code>.
	 * @param substring The substring to be used to perform the search.
	 * @return All commands that contains <code>substring</code>.
	 * @see #getCommandList
	 */
	public String[] searchCommandHistory(String substring);
	
	/** Browses the command history one line up. */
	public void browseCommandHistoryUp();
	
	/** Browses the command history one line down. */
	public void browseCommandHistoryDown();
	
	/** Browses to the first line of the command history. */
	public void browseCommandHistoryFirst();
	
	/** Browses to the last line of the command history. */
	public void browseCommandHistoryLast();
}
