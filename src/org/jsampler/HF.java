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

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.util.logging.Level;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import org.linuxsampler.lscp.LSException;
import org.linuxsampler.lscp.LscpException;

import static org.jsampler.JSI18n.i18n;


/**
 * This class contains some helper function.
 * @author Grigor Iliev
 */
public class HF {
// GUI HELPER FUNCIONS
	
	/**
	 * Returns more meaningful, non-<code>null</code> message.
	 * @return More meaningful, non-<code>null</code> message.
	 */
	public static String
	getErrorMessage(Exception e) {
		String msg = e.getMessage();
		
		if(e instanceof LSException) {
			LSException x = (LSException)e;
		} else if(e instanceof LscpException) {
			
		} else { msg = (msg != null ? msg : i18n.getError("unknownError")); }
		
		return msg;
	}
	
	/**
	 * Shows a dialog with the specified error message.
	 * @param msg The error message to be shown.
	 */
	public static void
	showErrorMessage(String msg) { showErrorMessage(msg, CC.getMainFrame()); }
	
	/**
	 * Shows a dialog with the specified error message.
	 * @param frame The parent <code>Frame</code> for the dialog.
	 * @param msg The error message to be shown.
	 */
	public static void
	showErrorMessage(String msg, Frame frame) {
		JOptionPane.showMessageDialog (
			frame, msg,
			i18n.getError("error"),
			JOptionPane.ERROR_MESSAGE
		);
	}
	
	/**
	 * Shows a dialog with the specified error message.
	 * @param dlg The parent <code>Dialog</code> from which the dialog is displayed.
	 * @param msg The error message to be shown.
	 */
	public static void
	showErrorMessage(String msg, Dialog dlg) {
		JOptionPane.showMessageDialog (
			dlg, msg,
			i18n.getError("error"),
			JOptionPane.ERROR_MESSAGE
		);
	}
	
	/**
	 * Shows a dialog with error message obtained by {@link #getErrorMessage} method.
	 * @param e The <code>Exception</code> from which the error message is obtained.
	 */
	public static void
	showErrorMessage(Exception e) { showErrorMessage(e, CC.getMainFrame()); }
	
	/**
	 * Shows a dialog with error message obtained by {@link #getErrorMessage} method.
	 * @param e The <code>Exception</code> from which the error message is obtained.
	 * @param prefix The prefix to be added to the error message.
	 */
	public static void
	showErrorMessage(Exception e, String prefix) {
		showErrorMessage(e, CC.getMainFrame(), prefix);
	}
	
	/**
	 * Shows a dialog with error message obtained by {@link #getErrorMessage} method.
	 * @param e The <code>Exception</code> from which the error message is obtained.
	 * @param frame The parent <code>Frame</code> for the dialog.
	 */
	public static void
	showErrorMessage(Exception e, Frame frame) {
		showErrorMessage(e, frame, "");
	}
	
	/**
	 * Shows a dialog with error message obtained by {@link #getErrorMessage} method.
	 * @param e The <code>Exception</code> from which the error message is obtained.
	 * @param frame The parent <code>Frame</code> for the dialog.
	 * @param prefix The prefix to be added to the error message.
	 */
	public static void
	showErrorMessage(Exception e, Frame frame, String prefix) {
		String msg = prefix + getErrorMessage(e);
		
		CC.getLogger().log(Level.INFO, msg, e);
			
		JOptionPane.showMessageDialog (
			frame, msg,
			i18n.getError("error"),
			JOptionPane.ERROR_MESSAGE
		);
	}
	
	/**
	 * Shows a dialog with error message obtained by {@link #getErrorMessage} method.
	 * @param e The <code>Exception</code> from which the error message is obtained.
	 * @param dlg The parent <code>Dialog</code> from which the dialog is displayed.
	 */
	public static void
	showErrorMessage(Exception e, Dialog dlg) {
		String msg = getErrorMessage(e);
		
		CC.getLogger().log(Level.INFO, msg, e);
		
		JOptionPane.showMessageDialog (
			dlg, msg,
			i18n.getError("error"),
			JOptionPane.ERROR_MESSAGE
		);
	}
	
	/**
	 * Brings up a question dialog with Yes, No options, empty title and the specified message.
	 * @param parent The parent <code>Component</code> for the dialog.
	 * @param message The message to display.
	 * @return <code>true</code> if the user chooses "yes", <code>false</code> otherwise.
	 */
	public static boolean
	showYesNoDialog(Component parent, String message) {
		return showYesNoDialog(parent, message, "");
	}
	
	/**
	 * Brings up a question dialog with Yes, No options, empty title and the specified message.
	 * @param parent The parent <code>Component</code> for the dialog.
	 * @param message The message to display.
	 * @param title The dialog's title.
	 * @return <code>true</code> if the user chooses "yes", <code>false</code> otherwise.
	 */
	public static boolean
	showYesNoDialog(Component parent, String message, String title) {
		Object[] options = { i18n.getButtonLabel("yes"), i18n.getButtonLabel("no") };
		int n = JOptionPane.showOptionDialog (
			parent,
			message, title,
			JOptionPane.YES_NO_OPTION,
			JOptionPane.QUESTION_MESSAGE,
			null,
			options, options[0]
		);
		
		return n == 0;
	}
	
	/**
	 * Sets the default font to be used in the GUI.
	 * @param fontName The name of the font to be used as default.
	 */
	public static void
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
///////
	
	/**
	 * Deletes the specified file, if exists and
	 * is located in the JSampler's home directory.
	 * @param file The file to delete.
	 */
	public static void
	deleteFile(String file) {
		String s = CC.getJSamplerHome();
		if(s == null) return;
		
		try {
			File f = new File(s + File.separator + file);
			if(f.isFile()) f.delete();
		} catch(Exception x) {
			CC.getLogger().log(Level.INFO, getErrorMessage(x), x);
		}
	}
	
	/**
	 * Create a backup copy of the specified file, located in the JSampler's home directory.
	 * @param file The name of the file to backup.
	 * @param bkpFile The backup name of the file.
	 * @return <code>true</code> if the file is backuped successfully.
	 */
	public static boolean
	createBackup(String file, String bkpFile) {
		if(file == null || bkpFile == null) return false;
		if(file.length() == 0 || bkpFile.length() == 0) return false;
		
		String s = CC.getJSamplerHome();
		if(s == null) return false;
		
		File f = new File(s + File.separator + file);
		if(!f.isFile()) return false;
		
		try {
			FileInputStream fis = new FileInputStream(s + File.separator + file);
			
			FileOutputStream fos;
			fos = new FileOutputStream(s + File.separator + bkpFile, false);
			
			int i = fis.read();
			while(i != -1) {
				fos.write(i);
				i = fis.read();
			}
		} catch(Exception x) {
			CC.getLogger().log(Level.INFO, getErrorMessage(x), x);
			return false;
		}
		
		return true;
	}
	
	
}
