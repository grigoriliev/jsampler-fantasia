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

package org.jsampler.view.std;

import java.awt.Color;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.Timer;

import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import org.jsampler.CC;
import org.jsampler.HF;

import static org.jsampler.view.std.StdI18n.i18n;

/**
 *
 * @author Grigor Iliev
 */
public class JSBackendLogFrame extends JSFrame {
	private final BackendLogPane backendLogPane = new BackendLogPane();
	private final Timer timer;
	
	public
	JSBackendLogFrame() {
		super(i18n.getLabel("JSBackendLogFrame.title"), "JSBackendLogFrame");
		ImageIcon i = CC.getViewConfig().getBasicIconSet().getApplicationIcon();
		if(i != null) setIconImage(i.getImage());
		
		add(new JScrollPane(backendLogPane));
		
		ActionListener l = new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				processInput();
			}
		};
		
		timer = new Timer(500, l);
		timer.start();
	}
	
	public void
	stopTimer() { timer.stop(); }
	
	private void
	processInput() {
		Process p = CC.getBackendProcess();
		if(p == null) return;
		
		try {
			StringBuffer sb = new StringBuffer();
			while(p.getInputStream().available() > 0) {
				sb.append((char) p.getInputStream().read());
			}
			String s = sb.toString();
			if(s.length() > 0) backendLogPane.appendText(s);
			
			int i = s.indexOf("Starting LSCP network server");
			if(i != -1 && s.indexOf("OK", i + 27) != -1) {
				synchronized(CC.getBackendMonitor()) {
					// Notify that the LSCP server is started
					CC.getBackendMonitor().notifyAll();
				}
			}
			
			sb = new StringBuffer();
			while(p.getErrorStream().available() > 0) {
				sb.append((char) p.getErrorStream().read());
			}
			s = sb.toString();
			if(s.length() > 0) backendLogPane.appendError(s);
		} catch(Exception x) {
			CC.getLogger().log(Level.INFO, HF.getErrorMessage(x), x);
		}
	}
	
	public static class BackendLogPane extends JTextPane {
		private final String STYLE_ROOT = "root";
		private final String STYLE_REGULAR = "regular";
		private final String STYLE_ERROR = "errorMessage";
		private final String STYLE_ERROR_0 = "errorMessage0";
		
		public
		BackendLogPane() {
			Style def;
			def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
			StyledDocument doc = getStyledDocument();
			Style root = doc.addStyle(STYLE_ROOT, def);
			Style regular = doc.addStyle(STYLE_REGULAR, root);
			Style style = doc.addStyle(STYLE_ERROR_0, regular);
			StyleConstants.setForeground(style, Color.RED);
			doc.addStyle(STYLE_ERROR, style);
			
			setEditable(false);
			setBorder(BorderFactory.createEmptyBorder());
		}
		
		public void
		appendText(String s) {
			StyledDocument doc = getStyledDocument();
			try {
				doc.insertString(doc.getLength(), s, doc.getStyle(STYLE_REGULAR));
			} catch(Exception x) {
				CC.getLogger().log(Level.INFO, HF.getErrorMessage(x), x);
			}
		}
		
		public void
		appendError(String s) {
			StyledDocument doc = getStyledDocument();
			try {
				doc.insertString(doc.getLength(), s, doc.getStyle(STYLE_ERROR));
			} catch(Exception x) {
				CC.getLogger().log(Level.INFO, HF.getErrorMessage(x), x);
			}
		}
	}
}
