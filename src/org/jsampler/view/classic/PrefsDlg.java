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

package org.jsampler.view.classic;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import net.sf.juife.EnhancedDialog;
import net.sf.juife.JuifeUtils;

import org.jsampler.HF;
import org.jsampler.JSI18n;
import org.jsampler.JSampler;
import org.jsampler.Prefs;

import static org.jsampler.view.classic.ClassicI18n.i18n;


/**
 *
 * @author Grigor Iliev
 */
public class PrefsDlg extends EnhancedDialog {
	private final ViewPane viewPane = new ViewPane();
	private final GeneralPane genPane = new GeneralPane();
	private final ConnectionPane conPane = new ConnectionPane();
	
	private final JButton btnApply = new JButton(i18n.getButtonLabel("apply"));
	private final JButton btnClose = new JButton(i18n.getButtonLabel("close"));
	
	
	public
	PrefsDlg(Frame frm) {
		super(frm, i18n.getLabel("PrefsDlg"), true);
		
		initPrefsDlg();
		handleEvents();
		initPrefs();
		
		setLocation(JuifeUtils.centerLocation(this, frm));
	}
	
	private void
	initPrefsDlg() {
		JTabbedPane tp = new JTabbedPane();
		tp.addTab(i18n.getLabel("PrefsDlg.tabGeneral"), genPane);
		tp.addTab(i18n.getLabel("PrefsDlg.tabView"), viewPane);
		tp.addTab(i18n.getLabel("PrefsDlg.tabConnection"), conPane);
		tp.setAlignmentX(RIGHT_ALIGNMENT);
		
		// Set preferred size for Apply & Exit buttons
		Dimension d = JuifeUtils.getUnionSize(btnApply, btnClose);
		btnApply.setPreferredSize(d);
		btnClose.setPreferredSize(d);

		JPanel btnPane = new JPanel();
		btnPane.setLayout(new BoxLayout(btnPane, BoxLayout.X_AXIS));
		btnPane.add(btnApply);
		btnPane.add(Box.createRigidArea(new Dimension(5, 0)));
		btnPane.add(btnClose);
		btnPane.setAlignmentX(RIGHT_ALIGNMENT);
		
		JPanel mainPane = new JPanel();
		mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
		mainPane.add(tp);
		mainPane.add(Box.createRigidArea(new Dimension(0, 12)));
		mainPane.add(btnPane);
		mainPane.setBorder(BorderFactory.createEmptyBorder(11, 12, 12, 12));
		
		getContentPane().add(mainPane);
		
		pack();
		setResizable(false);
	}
	
	private void
	handleEvents() {
		btnApply.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { onApply(); }
		});
		
		btnClose.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { onExit(); }
		});
	}
	
	protected void
	onOk() {  onApply(); }
	
	protected void
	onCancel() { onExit(); }
		
	private void
	initPrefs() {
		setLSAddress(Prefs.getLSAddress());
		setLSPort(Prefs.getLSPort());
	}
	
	private void
	onApply() {
		// GENERAL
		boolean b = Prefs.setInterfaceLanguage(viewPane.getInterfaceLanguage());
		boolean b2 = Prefs.setInterfaceCountry(viewPane.getInterfaceCountry());
		if (b || b2) JOptionPane.showMessageDialog (
			this, 
			i18n.getMessage("PrefsDlg.ifaceChangeInfo", JSampler.NAME),
			null,
			JOptionPane.INFORMATION_MESSAGE
		);
		
		String fontName = viewPane.getInterfaceFontName();
		if(fontName.equals("[Default]")) {
			if(Prefs.setInterfaceFont(null)) JOptionPane.showMessageDialog (
				this, 
				i18n.getMessage("PrefsDlg.ifaceFontChangeInfo", JSampler.NAME),
				null,
				JOptionPane.INFORMATION_MESSAGE
			);
		} else if(Prefs.setInterfaceFont(fontName)) {
			HF.setUIDefaultFont(fontName);
		}
		
		// CONNECTION
		Prefs.setLSAddress(getLSAddress());
		
		b = true;
		String s = getLSPort();
		try {
			if(s.length() > 0) {
				int port = Integer.parseInt(s);
				if(port > 0 && port < 0xffff)
					Prefs.setAuthSrvPort(port);
				else b = false;
			} else Prefs.setAuthSrvPort(-1);	// -1 resets to default value
		} catch(NumberFormatException x) {
			b = false;
		}
		
		if(!b) {
			JOptionPane.showMessageDialog (
				this, 
				i18n.getError("PrefsDlg.invalidPort", s),
				i18n.getError("error"),
				JOptionPane.ERROR_MESSAGE
			);
			
			return;
		}
		
		setVisible(false);
	}
	
	private void
	onExit() { setVisible(false); }
	
	private String
	getLSAddress() { return conPane.getLSAddress().trim(); }
	
	private void
	setLSAddress(String s) { conPane.setLSAddress(s); }
	
	private String
	getLSPort() { return conPane.getLSPort().trim(); }
	
	private void
	setLSPort(int port) { conPane.setLSPort(String.valueOf(port)); }
}

class GeneralPane extends JPanel {
	public
	GeneralPane() { initGeneralPane(); }
	
	private void
	initGeneralPane() {
		
	}
}

class ViewPane extends JPanel {
	private final JLabel lIfaceLanguage =
		new JLabel(i18n.getLabel("ViewPane.lIfaceLanguage"));
	private final JComboBox cbIfaceLanguage = new JComboBox();
	
	private final JLabel lIfaceFont =
		new JLabel(i18n.getLabel("ViewPane.lIfaceFont"));
	private final JComboBox cbIfaceFont = new JComboBox();
	
	public
	ViewPane() { initViewPane(); }
	
	private void
	initViewPane() {
		cbIfaceLanguage.setMaximumSize (
			new Dimension(Short.MAX_VALUE, cbIfaceLanguage.getPreferredSize().height)
		);
		
		for(Locale l : JSI18n.getAvailableLocales()) {
			LocaleBox box = new LocaleBox(l);
			cbIfaceLanguage.addItem(box);
			if (	l.getLanguage().equals(Prefs.getInterfaceLanguage()) &&
				l.getCountry().equals(Prefs.getInterfaceCountry())
			) cbIfaceLanguage.setSelectedItem(box);
		}
		
		cbIfaceFont.setMaximumSize (
			new Dimension(Short.MAX_VALUE, cbIfaceFont.getPreferredSize().height)
		);
		
		cbIfaceFont.addItem("[Default]");
		
		String[] fontS =
		GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		
		for(String f : fontS) cbIfaceFont.addItem(f);
		
		if(Prefs.getInterfaceFont() == null) cbIfaceFont.setSelectedItem("[Default]");
		else cbIfaceFont.setSelectedItem(Prefs.getInterfaceFont());
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JPanel ifacePane = new JPanel();
		ifacePane.setLayout(new BoxLayout(ifacePane, BoxLayout.X_AXIS));
		ifacePane.add(lIfaceLanguage);
		ifacePane.add(Box.createRigidArea(new Dimension(5, 0)));
		ifacePane.add(cbIfaceLanguage);
		
		add(ifacePane);
		
		add(Box.createRigidArea(new Dimension(0, 6)));
		
		JPanel fontPane = new JPanel();
		fontPane.setLayout(new BoxLayout(fontPane, BoxLayout.X_AXIS));
		fontPane.add(lIfaceFont);
		fontPane.add(Box.createRigidArea(new Dimension(5, 0)));
		fontPane.add(cbIfaceFont);
		
		add(fontPane);
		setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
	}
	
	protected String
	getInterfaceLanguage() {
		LocaleBox box = (LocaleBox)cbIfaceLanguage.getSelectedItem();
		if(box == null) return null;
		return box.getLocale().getLanguage();
	}
	
	protected String
	getInterfaceCountry() {
		LocaleBox box = (LocaleBox)cbIfaceLanguage.getSelectedItem();
		if(box == null) return null;
		return box.getLocale().getCountry();
	}
	
	protected String
	getInterfaceFontName() { return cbIfaceFont.getSelectedItem().toString(); }
	
	class LocaleBox {
		private Locale locale;
		
		LocaleBox(Locale locale) { this.locale = locale; }
		
		public Locale
		getLocale() { return locale; }
		
		public String
		toString() { return locale.getDisplayLanguage(JSI18n.i18n.getCurrentLocale()); }
	}
}

class ConnectionPane extends JPanel {
	final LSPrefsPane lsPrefsPane = new LSPrefsPane(); 
	
	public
	ConnectionPane() { initConnectionPane(); }
	
	private void
	initConnectionPane() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		add(lsPrefsPane);
		add(Box.createGlue());
		setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
	}
	
	public String
	getLSAddress() { return lsPrefsPane.getLSAddress(); }
	
	public void
	setLSAddress(String address) { lsPrefsPane.setLSAddress(address); }
	
	public String
	getLSPort() { return lsPrefsPane.getLSPort(); }
	
	public void
	setLSPort(String port) { lsPrefsPane.setLSPort(port); }
}

class LSPrefsPane extends JPanel {
	private final JLabel lAddress = new JLabel(i18n.getLabel("LSPrefsPane.Address"));
	private final JLabel lPort = new JLabel(i18n.getLabel("LSPrefsPane.Port"));
	private final JTextField tfAddress = new JTextField();
	private final JTextField tfPort = new JTextField();


	public
	LSPrefsPane() { initLSPrefsPane(); }

	private void
	initLSPrefsPane() {
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
	
		setLayout(gridbag);
		
		// Set preferred size for username & password fields
		int w1 = (int) tfAddress.getMinimumSize().getWidth();
		int h1 = (int) tfAddress.getMinimumSize().getHeight();
		Dimension d = new Dimension(w1 > 150 ? w1 : 150, h1);
		tfAddress.setMinimumSize(d);
		tfAddress.setPreferredSize(d);
	
		w1 = (int) tfPort.getMinimumSize().getWidth();
		h1 = (int) tfPort.getMinimumSize().getHeight();
		d = new Dimension(w1 > 150 ? w1 : 150, h1);
		tfPort.setMinimumSize(d);
		tfPort.setPreferredSize(d);
	
		c.fill = GridBagConstraints.NONE;
	
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(3, 3, 3, 3);
		gridbag.setConstraints(lAddress, c);
		add(lAddress); 

		c.gridx = 0;
		c.gridy = 1;
		gridbag.setConstraints(lPort, c);
		add(lPort);
	
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(tfAddress, c);
		add(tfAddress);
		
		c.gridx = 1;
		c.gridy = 1;
		gridbag.setConstraints(tfPort, c);
		add(tfPort);
		
		setBorder(BorderFactory.createTitledBorder(i18n.getLabel("LSPrefsPane")));
	}
	
	public String
	getLSAddress() { return tfAddress.getText(); }
	
	public void
	setLSAddress(String address) { tfAddress.setText(address); }
	
	public String
	getLSPort() { return tfPort.getText(); }
	
	public void
	setLSPort(String port) { tfPort.setText(port); }
}
