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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import java.net.Socket;

import java.util.LinkedList;
import java.util.Vector;

import java.util.logging.Level;

import javax.swing.SwingUtilities;

import net.sf.juife.event.TaskEvent;
import net.sf.juife.event.TaskListener;

import org.jsampler.event.LSConsoleEvent;
import org.jsampler.event.LSConsoleListener;

import org.jsampler.task.LSConsoleConnect;

import static org.jsampler.JSI18n.i18n;


/**
 * This class provides default implementation of the <code>LSConsoleModel</code> interface.
 * @author Grigor Iliev
 */
public class DefaultLSConsoleModel implements LSConsoleModel {
	private Socket socket;
	private LscpOutputStream out;
	
	private final String[] cmdList = LscpUtils.getCommandList();
	
	private String cmdLine = "";
	
	/**
	 * Contains the global command history, excluding blank lines and comments.
	 */
	private final LinkedList<String> cmdHistory = new LinkedList<String>();
	
	private int cmdHistoryIdx = -1;
	
	private int commandHistorySize = 1000;
	
	private final LSConsoleThread lsConsoleThread = new LSConsoleThread();
	
	/**
	 * Contains the command history of the current
	 * session, including blank lines and comments.
	 */
	private final Vector<String> sessionHistory = new Vector<String>();
	
	/** Used to hold the current command when browsing through command history, etc. */
	private String currentCmd = "";
	
	private final Vector<LSConsoleListener> listeners = new Vector<LSConsoleListener>();
	
	
	/** Creates a new instance of <code>DefaultLSConsoleModel</code>. */
	public
	DefaultLSConsoleModel() {
		CC.addReconnectListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { reconnect(); }
		});
		
		lsConsoleThread.start();
	}
	
	private Socket
	getSocket() { return socket; }
	
	private void
	setSocket(Socket socket) { this.socket = socket; }
	
	private void
	reconnect() {
		final LSConsoleConnect cnt = new LSConsoleConnect(getSocket());
		
		cnt.addTaskListener(new TaskListener() {
			public void
			taskPerformed(TaskEvent e) { changeSocket(cnt.getResult()); }
		});
		
		CC.getTaskQueue().add(cnt);
	}
	
	private void
	changeSocket(Socket sock) {
		setSocket(sock);
		
		try {
			LscpInputStream in;
			in = sock == null ? null : new LscpInputStream(sock.getInputStream());
			out = sock == null ? null : new LscpOutputStream(sock.getOutputStream());
			
			lsConsoleThread.setInputStream(in);
		} catch(Exception x) { CC.getLogger().log(Level.INFO, HF.getErrorMessage(x), x); }
	}
	
	/**
	 * Registers the specified listener for receiving event messages.
	 * @param l The <code>LSConsoleListener</code> to register.
	 */
	public void
	addLSConsoleListener(LSConsoleListener l) { listeners.add(l); }
	
	/**
	 * Removes the specified listener.
	 * @param l The <code>LSConsoleListener</code> to remove.
	 */
	public void
	removeLSConsoleListener(LSConsoleListener l) { listeners.remove(l); }
	
	/** Executes the command specified in the command line. */
	public void
	execCommand() {
		String cmd = getCommandLineText();
		sessionHistory.add(cmd);
		if(cmd.trim().length() > 0 && !cmd.startsWith("#")) addToCommandHistory(cmd);
		while(cmdHistory.size() > getCommandHistorySize()) cmdHistory.removeFirst();
		
		setCommandLineText("");
		currentCmd = "";
		cmdHistoryIdx = -1;
		
		if( getSocket() == null || getSocket().isClosed() || !getSocket().isConnected() 
			|| getSocket().isOutputShutdown() || out == null ) {
			
			fireResponseReceived(i18n.getMessage("DefaultLSConsoleModel.notConnected"));
		} else {
			CC.getTaskQueue().add(new LSConsoleExecCommand(cmd));
		}
		
		fireCommandExecuted();
	}
	
	/**
	 * Gets the last executed command.
	 * @return The last command executed in the LS Console.
	 */
	public String
	getLastExecutedCommand() {
		int size = sessionHistory.size();
		return size == 0 ? "" : sessionHistory.get(size - 1);
	}
	
	/**
	 * Sets the text in the command line.
	 * @param cmdLine The new command line text.
	 */
	public void
	setCommandLineText(String cmdLine) {
		if(this.cmdLine.equals(cmdLine)) return;
		
		String oldCmdLine = this.cmdLine;
		this.cmdLine = cmdLine;
		fireCommandLineTextChanged(oldCmdLine);
	}
	
	/**
	 * Gets the text in the command line.
	 * @return The command line's text.
	 */
	public String
	getCommandLineText() { return cmdLine; }
	
	/**
	 * Gets the command history of the current session, including blank lines and comments.
	 * @return The command history of the current session, including blank lines and comments.
	 */
	public String[]
	getSessionHistory() {
		return sessionHistory.toArray(new String[sessionHistory.size()]);
	}
	
	/**
	 * Clears the session history.
	 * @see #getSessionHistory
	 */
	public void
	clearSessionHistory() { sessionHistory.removeAllElements(); }
	
	/**
	 * Adds the specified <code>command</code> to command history.
	 * @param command The command to be added to command history.
	 */
	public void
	addToCommandHistory(String command) { cmdHistory.add(command); }
	
	/**
	 * Gets the complete command history, excluding blank lines and comments.
	 * @return The complete command history, excluding blank lines and comments.
	 */
	public String[]
	getCommandHistory() {
		return cmdHistory.toArray(new String[cmdHistory.size()]);
	}
	
	/**
	 * Clears the complete/multisession command history.
	 * @see #getCommandHistory
	 */
	public void
	clearCommandHistory() {
		cmdHistory.clear();
		cmdHistoryIdx = -1;
	}
	
	/**
	 * Determines the maximum number of lines to be kept in the command history.
	 * @return The maximum number of lines to be kept in the command history.
	 */
	public int
	getCommandHistorySize() { return commandHistorySize; }
	
	/**
	 * Sets the maximum number of lines to be kept in the command history.
	 * @param size Determines the maximum number of lines to be kept in the command history.
	 */
	public void
	setCommandHistorySize(int size) { commandHistorySize = size; }
	
	/**
	 * Gets a list of all LSCP commands.
	 * @return A list of all LSCP commands.
	 */
	public String[]
	getCommandList() { return cmdList; }
	
	/** Browses the command history one line up. */
	public void
	browseCommandHistoryUp() {
		if(cmdHistory.size() == 0) return;
			
		if(cmdHistoryIdx == -1) {
			currentCmd = getCommandLineText();
			cmdHistoryIdx = cmdHistory.size() - 1;
			setCommandLineText(cmdHistory.get(cmdHistoryIdx));
			return;
		}
		
		if(cmdHistoryIdx == 0) return;
		
		setCommandLineText(cmdHistory.get(--cmdHistoryIdx));
	}
	
	/** Browses the command history one line down. */
	public void
	browseCommandHistoryDown() {
		if(cmdHistory.size() == 0 || cmdHistoryIdx == -1) return;
		if(cmdHistoryIdx == cmdHistory.size() - 1) {
			cmdHistoryIdx = -1;
			setCommandLineText(currentCmd);
			currentCmd = "";
			return;
		}
		
		setCommandLineText(cmdHistory.get(++cmdHistoryIdx));
	}
	
	/** Browses to the first line of the command history. */
	public void
	browseCommandHistoryFirst() {
		if(cmdHistory.size() == 0) return;
		cmdHistoryIdx = 0;
		setCommandLineText(cmdHistory.get(cmdHistoryIdx));
	}
	
	/** Browses to the last line of the command history. */
	public void
	browseCommandHistoryLast() {
		if(cmdHistory.size() == 0) return;
		cmdHistoryIdx = cmdHistory.size() - 1;
		setCommandLineText(cmdHistory.get(cmdHistoryIdx));
	}
	
	private Vector<String> tmpVector = new Vector<String>();
	
	/**
	 * Searches the command history for commands
	 * containing the string returned by {@link #getCommandLineText}.
	 * @return All commands that contains the string returned by {@link #getCommandLineText}.
	 * @see #getCommandHistory
	 */
	public String[]
	searchCommandHistory() { return searchCommandHistory(getCommandLineText()); }
	
	/**
	 * Searches the command history for commands containing <code>substring</code>.
	 * @param substring The substring to be used to perform the search.
	 * @return All commands that contains <code>substring</code>.
	 * @see #getCommandList
	 */
	public String[]
	searchCommandHistory(String substring) {
		tmpVector.removeAllElements();
		for(String s : cmdHistory) if(s.indexOf(substring) != -1) tmpVector.add(s);
		
		return tmpVector.toArray(new String[tmpVector.size()]);
	}
	
	/**
	 * Searches the LSCP command list for commands
	 * containing the string returned by {@link #getCommandLineText}.
	 * @return All commands that contains the string returned by {@link #getCommandLineText}.
	 * @see #getCommandList
	 */
	public String[]
	searchCommandList() { return searchCommandList(getCommandLineText()); }
	
	/**
	 * Searches the LSCP command list for commands containing <code>substring</code>.
	 * @param substring The substring to be used to perform the search.
	 * @return All commands that contains <code>substring</code>.
	 * @see #getCommandList
	 */
	public String[]
	searchCommandList(String substring) {
		tmpVector.removeAllElements();
		for(String s : cmdList) if(s.indexOf(substring) != -1) tmpVector.add(s);
		
		return tmpVector.toArray(new String[tmpVector.size()]);
	}
	
	/** Notifies listeners that the text in the command line has changed. */
	private void
	fireCommandLineTextChanged(String oldCmdLine) {
		LSConsoleEvent e = new LSConsoleEvent(this, null, oldCmdLine);
		for(LSConsoleListener l : listeners) l.commandLineTextChanged(e);
	}
	
	/** Notifies listeners that the command in the command line has been executed. */
	private void
	fireCommandExecuted() {
		LSConsoleEvent e = new LSConsoleEvent(this);
		for(LSConsoleListener l : listeners) l.commandExecuted(e);
	}
	
	/**
	 * Notifies listeners that response is received from LinuxSampler.
	 * @param response The response received from LinuxSampler.
	 */
	private void
	fireResponseReceived(final String response) {
		SwingUtilities.invokeLater(new Runnable() {
			public void
			run() {
				LSConsoleEvent e = new LSConsoleEvent(this, response);
				for(LSConsoleListener l : listeners) l.responseReceived(e);
			}
		});
	}
	
	/** Executes LS Console command. */
	private class LSConsoleExecCommand extends org.jsampler.task.EnhancedTask {
		private String cmd;
		
		/** Creates a new instance of <code>LSConsoleExecCommand</code>. */
		public
		LSConsoleExecCommand(String cmd) {
			setTitle("LSConsoleExecCommand_task");
			setDescription(i18n.getMessage("LSConsoleExecCommand.description"));
			this.cmd = cmd;
		}
	
		/** The entry point of the task. */
		public void
		run() {
			try { out.writeLine(cmd); }
			catch(Exception x) {
				setErrorMessage(getDescription() + ": " + HF.getErrorMessage(x));
				CC.getLogger().log(Level.FINE, getErrorMessage(), x);
			}
		}
	}

	class LSConsoleThread extends Thread {
		LscpInputStream in;
		private boolean terminate = false;
		
		LSConsoleThread() {super("LS-Console-Thread"); }
		
		public void
		run() {
			while(!mustTerminate()) {
				try { processInput(); }
				catch(Exception x) {
					CC.getLogger().log(Level.INFO, HF.getErrorMessage(x), x);
				}
				
				try { synchronized(this) { wait(100); } }
				catch(Exception x) {
					CC.getLogger().log(Level.INFO, HF.getErrorMessage(x), x);
				}
			}
		}
	
		private synchronized boolean
		mustTerminate() { return terminate; }
		
		public synchronized void
		terminate() {
			terminate = true;
			this.notifyAll();
		}
		
		/** Processes the input sent by LinuxSampler */
		private synchronized void
		processInput() throws IOException {
			while(in != null && in.available() > 0) {
				String response = in.readLine();
				fireResponseReceived(response); 
			}
		}
		
		public synchronized void
		setInputStream(LscpInputStream in) { this.in = in; }
	}
	
	class LscpInputStream {
		private InputStream in;
		private StringBuffer buf = new StringBuffer();
		
		/**
		 * Creates a new instance of LscpInputStream.
		 *
		 */
		public
		LscpInputStream(InputStream in) {
			this.in = in;
		}
	
		/**
		 * Reads a line. 
		 * This method is thread safe.
		 * 
		 * @return A string containing the next line readed from the stream or
		 * <code>null</code> if the end of the stream has been reached.
		 * @throws IOException If an I/O error occurs.
		 */
		public synchronized String
		readLine() throws IOException {
			int i;
			buf.setLength(0);
		
			while((i = in.read()) != -1) {
				if(i == '\r') {
					checkLF();
					break;
				}
				buf.append((char)i);
			}
		
			if(i == -1) {
				if(buf.length() > 0)
					throw new IOException("Unexpected end of line!");
				return null;
			}
			return buf.toString();
		}
	
		/**
		 * Returns the number of bytes that can
		 * be read from this input stream without blocking.
		 *
		 * @return The number of bytes that can
		 * be read from this input stream without blocking.
		 * @throws IOException If an I/O error occurs.
		 */
		public synchronized int
		available() throws IOException { return in.available(); }
	
		private void
		checkLF() throws IOException {
			int i = in.read();
			if(i == -1) throw new IOException("Unexpected end of file!");
			if(i != '\n') throw new IOException("Unexpected end of line!");
		}
	}
	
	class LscpOutputStream {
		private OutputStream out;
	
		/** Creates a new instance of LscpOutputStream */
		public
		LscpOutputStream(OutputStream out) { this.out = out; }
	
		/*
		 * Writes a line.
		 * @param line a string to be written.
		 */
		public void
		writeLine(String line) throws IOException {
			try {
				out.write(line.getBytes("US-ASCII"));
				out.write('\r');
				out.write('\n');
				out.flush();
			} catch(UnsupportedEncodingException x) { 
				CC.getLogger().log(Level.INFO, HF.getErrorMessage(x), x);
			}
		}
	}
}
