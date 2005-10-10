/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005 Grigor Kirilov Iliev
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

import java.util.logging.Level;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import org.linuxsampler.lscp.LSException;
import org.linuxsampler.lscp.LscpException;

import static org.jsampler.JSI18n.i18n;


/**
 *
 * @author Grigor Iliev
 */
public class HF {
// GUI HELPER FUNCIONS
	public static String
	getErrorMessage(Exception e) {
		String msg = e.getMessage();
		
		if(e instanceof LSException) {
			LSException x = (LSException)e;
		} else if(e instanceof LscpException) {
			
		} else { msg = (msg != null ? msg : i18n.getError("unknownError")); }
		
		return msg;
	}
	
	public static void
	showErrorMessage(String msg) { showErrorMessage(msg, CC.getMainFrame()); }
	
	public static void
	showErrorMessage(String msg, Frame frame) {
		JOptionPane.showMessageDialog (
			frame, msg,
			i18n.getError("error"),
			JOptionPane.ERROR_MESSAGE
		);
	}
	
	public static void
	showErrorMessage(String msg, Dialog dlg) {
		JOptionPane.showMessageDialog (
			dlg, msg,
			i18n.getError("error"),
			JOptionPane.ERROR_MESSAGE
		);
	}
	
	public static void
	showErrorMessage(Exception e) { showErrorMessage(e, CC.getMainFrame()); }
	
	public static void
	showErrorMessage(Exception e, Frame frame) {
		String msg = getErrorMessage(e);
		
		CC.getLogger().log(Level.INFO, msg, e);
			
		JOptionPane.showMessageDialog (
			frame, msg,
			i18n.getError("error"),
			JOptionPane.ERROR_MESSAGE
		);
	}
	
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
	 * 
	 */
	public static boolean
	showYesNoDialog(Component parent, String message) {
		return showYesNoDialog(parent, message, "");
	}
	
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
}
