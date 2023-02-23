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

package org.jsampler.view.swing;

import static org.jsampler.JSI18n.i18n;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import org.jsampler.CC;
import org.jsampler.HF;


/**
 * This class contains some Swing helper function.
 * @author Grigor Iliev
 */
public class SHF {
	public static SwingMainFrame
	getMainFrame() { return (SwingMainFrame)CC.getMainFrame(); }
	
	public static ViewConfig
	getViewConfig() { return ((ViewConfig)CC.getViewConfig()); }
	
	/**
	 * Gets the tree model of the instruments database.
	 * If the currently used view doesn't have instruments
	 * database support the tree model is initialized on first use.
	 * @return The tree model of the instruments database or
	 * <code>null</code> if the backend doesn't have instruments database support.
	 * @see org.jsampler.view.JSViewConfig#getInstrumentsDbSupport
	 */
	public static InstrumentsDbTreeModel
	getInstrumentsDbTreeModel() { return getViewConfig().getInstrumentsDbTreeModel(); }

	/**
	 * Shows a dialog with the specified error message.
	 * @param msg The error message to be shown.
	 */
	public static void
	showErrorMessage(String msg) { showErrorMessage(msg, getMainFrame()); }
	
	/**
	 * Shows a dialog with the specified error message.
	 * @param parentComponent determines the Frame in which the dialog is displayed
	 * @param msg The error message to be shown.
	 */
	public static void
	showErrorMessage(String msg, Component parentComponent) {
		JOptionPane.showMessageDialog (
			parentComponent, msg,
			i18n.getError("error"),
			JOptionPane.ERROR_MESSAGE
		);
	}
	
	/**
	 * Shows a dialog with error message obtained by {@link #getErrorMessage} method.
	 * @param e The <code>Exception</code> from which the error message is obtained.
	 */
	public static void
	showErrorMessage(Exception e) { showErrorMessage(e, getMainFrame()); }
	
	/**
	 * Shows a dialog with error message obtained by {@link #getErrorMessage} method.
	 * @param e The <code>Exception</code> from which the error message is obtained.
	 * @param prefix The prefix to be added to the error message.
	 */
	public static void
	showErrorMessage(Exception e, String prefix) {
		showErrorMessage(e, getMainFrame(), prefix);
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
		String msg = prefix + HF.getErrorMessage(e);
		
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
		String msg = HF.getErrorMessage(e);
		
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
}
