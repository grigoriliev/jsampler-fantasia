/*
 *   JSampler - a front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2023 Grigor Iliev <grigor@grigoriliev.com>
 *
 *   This file is part of JSampler.
 *
 *   JSampler is free software: you can redistribute it and/or modify it under
 *   the terms of the GNU General Public License as published by the Free
 *   Software Foundation, either version 3 of the License, or (at your option)
 *   any later version.
 *
 *   JSampler is distributed in the hope that it will be useful, but WITHOUT
 *   ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *   FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 *   more details.
 *
 *   You should have received a copy of the GNU General Public License along
 *   with JSampler. If not, see <https://www.gnu.org/licenses/>.
 */

package com.grigoriliev.jsampler.fantasia.view;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import com.grigoriliev.jsampler.juife.swing.InformationDialog;
import com.grigoriliev.jsampler.juife.swing.LinkButton;

import com.grigoriliev.jsampler.swing.view.std.StdUtils;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;

/**
 *
 * @author Grigor Iliev
 */
public class HelpAboutDlg extends InformationDialog {
	private final JLabel lProductName =
		new JLabel("<html>\n<font size=+1>JSampler Fantasia (version 0.9.8-SNAPSHOT)</font>");
	
	private final JLabel lAuthor = new JLabel(FantasiaI18n.i18n.getLabel("HelpAboutDlg.lAuthor"));
	private final LinkButton btnAuthor = new Lnkbutton(FantasiaI18n.i18n.getLabel("HelpAboutDlg.btnAuthor"));
	
	private final JLabel lLicense = new JLabel(FantasiaI18n.i18n.getLabel("HelpAboutDlg.lLicense"));
	private final LinkButton btnLicense = new Lnkbutton("GNU Affero General Public License v.3");
	
	private final JLabel lDesign = new JLabel(FantasiaI18n.i18n.getLabel("HelpAboutDlg.lDesign"));
	
	private final JLabel lCopyright = new JLabel(FantasiaI18n.i18n.getLabel("HelpAboutDlg.lCopyright"));
	
	private final JPanel mainPane = new JPanel();
	
	/** Creates a new instance of <code>HelpAboutDlg</code> */
	public
	HelpAboutDlg(Frame owner) {
		super(owner, FantasiaI18n.i18n.getLabel("HelpAboutDlg.title"));
		
		mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
		
		lProductName.setHorizontalAlignment(JLabel.CENTER);
		lProductName.setAlignmentX(LEFT_ALIGNMENT);
		mainPane.add(lProductName);
		mainPane.add(Box.createRigidArea(new Dimension(0, 12)));
		
		JTabbedPane tp = new JTabbedPane();
		tp.addTab(FantasiaI18n.i18n.getLabel("HelpAboutDlg.about"), createAboutPane());
		tp.addTab(FantasiaI18n.i18n.getLabel("HelpAboutDlg.details"), createDetailsPane());
		
		tp.setAlignmentX(LEFT_ALIGNMENT);
		
		mainPane.add(tp);
		
		mainPane.add(Box.createRigidArea(new Dimension(0, 12)));
		
		lCopyright.setFont(lCopyright.getFont().deriveFont(java.awt.Font.PLAIN));
		lCopyright.setFont(lCopyright.getFont().deriveFont(10.0f));
		lCopyright.setHorizontalAlignment(JLabel.CENTER);
		lCopyright.setAlignmentX(LEFT_ALIGNMENT);
		mainPane.add(lCopyright);
		
		setMainPane(mainPane);
		
		pack();
		
		installListeners();
	}
	
	private void
	installListeners() {
		btnAuthor.addActionListener(e -> StdUtils.browse("http://www.grigoriliev.com"));
		
		btnLicense.addActionListener(e -> showLicense(License.AGPL));
	}
	
	private JPanel
	createAboutPane() {
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		
		JPanel p = new JPanel();
		p.setLayout(gridbag);
		
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(0, 0, 0, 6);
		c.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints(lAuthor, c);
		p.add(lAuthor);
		
		c.gridx = 0;
		c.gridy = 1;
		gridbag.setConstraints(lLicense, c);
		p.add(lLicense);
		
		c.gridx = 0;
		c.gridy = 2;
		gridbag.setConstraints(lDesign, c);
		p.add(lDesign);
		
		btnAuthor.setUnvisitedColor(new Color(0xFFA300));
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1.0;
		c.insets = new Insets(0, 0, 0, 0);
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(btnAuthor, c);
		p.add(btnAuthor);
		
		c.gridx = 1;
		c.gridy = 1;
		gridbag.setConstraints(btnLicense, c);
		p.add(btnLicense);
		
		JLabel l = new JLabel("Olivier Boyer, Grigor Iliev");
		l.setFont(l.getFont().deriveFont(java.awt.Font.BOLD));
		c.gridx = 1;
		c.gridy = 2;
		gridbag.setConstraints(l, c);
		p.add(l);
		
		p.setAlignmentX(LEFT_ALIGNMENT);
		
		JPanel aboutPane = new JPanel();
		aboutPane.setLayout(new BoxLayout(aboutPane, BoxLayout.Y_AXIS));
		
		aboutPane.add(p);
		
		aboutPane.add(Box.createRigidArea(new Dimension(0, 12)));
		
		aboutPane.add(createLibrariesPane());
		
		aboutPane.add(Box.createRigidArea(new Dimension(0, 12)));
		
		return aboutPane;
	}
	
	private JPanel
	createDetailsPane() {
		JPanel detailsPane = new JPanel();
		detailsPane.setLayout(new BoxLayout(detailsPane, BoxLayout.Y_AXIS));
		
		detailsPane.add(new ContactInfoPane());
		JPanel p = new JPanel();
		p.setLayout(new java.awt.BorderLayout());
		p.setOpaque(false);
		detailsPane.add(p);
		return detailsPane;
	}
	
	private JPanel
	createLibrariesPane() {
		JPanel p = new JPanel();
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		p.setLayout(gridbag);
		
		Button btn = new Button("swingx");
		btn.addActionListener(e -> StdUtils.browse("http://swingx.dev.java.net/"));
		
		c.gridx = 1;
		c.gridy = 0;
		c.insets = new Insets(3, 3, 3, 3);
		gridbag.setConstraints(btn, c);
		p.add(btn);
		
		btn = new Button("substance");
		btn.addActionListener(e -> StdUtils.browse("http://substance.dev.java.net/"));
		
		c.gridx = 1;
		c.gridy = 1;
		gridbag.setConstraints(btn, c);
		p.add(btn);
		
		btn = new Button("jlscp");
		btn.addActionListener(e -> StdUtils.browse("https://github.com/grigoriliev/jlscp"));
		
		c.gridx = 0;
		c.gridy = 2;
		gridbag.setConstraints(btn, c);
		p.add(btn);
		
		btn = new Button("substance-swingx");
		btn.addActionListener(e -> StdUtils.browse("http://substance-swingx.dev.java.net/"));
		
		c.gridx = 1;
		c.gridy = 2;
		gridbag.setConstraints(btn, c);
		p.add(btn);
		
		btn = new Button("juife");
		btn.addActionListener(e -> StdUtils.browse("https://github.com/grigoriliev/juife"));
		
		c.gridx = 2;
		c.gridy = 2;
		gridbag.setConstraints(btn, c);
		p.add(btn);
		
		p.setBorder(BorderFactory.createTitledBorder (
			FantasiaI18n.i18n.getLabel("HelpAboutDlg.using")
		));
		
		p.setAlignmentX(LEFT_ALIGNMENT);
		
		return p;
	}
	
	private void
	showLicense(License license) {
		new LicenseDlg(this, license).setVisible(true);
	}
	
	static class ContactInfoPane extends JPanel {
		private final JLabel lAuthorEmail =
			new JLabel(FantasiaI18n.i18n.getLabel("HelpAboutDlg.lAuthorEmail"));
		private final JLabel lLSWebsite = new JLabel(FantasiaI18n.i18n.getLabel("HelpAboutDlg.lLSWebsite"));
		private final JLabel lJSWebsite = new JLabel(FantasiaI18n.i18n.getLabel("HelpAboutDlg.lJSWebsite"));
		
		private final Lnkbutton btnAuthorEmail = new Lnkbutton("grigor@grigoriliev.com");
		private final Lnkbutton btnLSWebsite = new Lnkbutton("www.linuxsampler.org");
		private final Lnkbutton btnJSWebsite = new Lnkbutton("sf.net/projects/jsampler");
	
		private final Button btnDocumentation =
			new Button(FantasiaI18n.i18n.getButtonLabel("HelpAboutDlg.btnDocumentation"));
	
		private final Button btnLSDevelopers =
			new Button(FantasiaI18n.i18n.getButtonLabel("HelpAboutDlg.btnLSDevelopers"));
	
		private final Button btnLSMailingList =
			new Button(FantasiaI18n.i18n.getButtonLabel("HelpAboutDlg.btnLSMailingList"));
	
		ContactInfoPane() {
			GridBagLayout gridbag = new GridBagLayout();
			GridBagConstraints c = new GridBagConstraints();
		
			setLayout(gridbag);
		
			c.gridx = 0;
			c.gridy = 0;
			c.insets = new Insets(0, 0, 0, 6);
			c.anchor = GridBagConstraints.EAST;
			gridbag.setConstraints(lAuthorEmail, c);
			add(lAuthorEmail);
			
			c.gridx = 0;
			c.gridy = 1;
			gridbag.setConstraints(lLSWebsite, c);
			add(lLSWebsite);
			
			c.gridx = 0;
			c.gridy = 2;
			c.anchor = GridBagConstraints.EAST;
			gridbag.setConstraints(lJSWebsite, c);
			add(lJSWebsite);
			
			c.gridx = 1;
			c.gridy = 0;
			c.insets = new Insets(0, 0, 0, 0);
			c.anchor = GridBagConstraints.WEST;
			gridbag.setConstraints(btnAuthorEmail, c);
			add(btnAuthorEmail);
			
			c.gridx = 1;
			c.gridy = 1;
			gridbag.setConstraints(btnLSWebsite, c);
			add(btnLSWebsite);
			
			c.gridx = 1;
			c.gridy = 2;
			gridbag.setConstraints(btnJSWebsite, c);
			add(btnJSWebsite);
			
			c.gridx = 0;
			c.gridy = 3;
			c.gridwidth = 2;
			c.insets = new Insets(12, 0, 0, 0);
			c.anchor = GridBagConstraints.CENTER;
			gridbag.setConstraints(btnDocumentation, c);
			add(btnDocumentation);
			
			c.gridx = 0;
			c.gridy = 4;
			c.insets = new Insets(6, 0, 0, 0);
			gridbag.setConstraints(btnLSDevelopers, c);
			add(btnLSDevelopers);
			
			c.gridx = 0;
			c.gridy = 5;
			gridbag.setConstraints(btnLSMailingList, c);
			add(btnLSMailingList);
			
			setBorder(BorderFactory.createTitledBorder (
				FantasiaI18n.i18n.getLabel("HelpAboutDlg.contactInfoPane")
			));
		
			btnAuthorEmail.addActionListener(
				e -> StdUtils.browse("mailto:grigor@grigoriliev.com")
			);
		
			btnLSWebsite.addActionListener(
				e -> StdUtils.browse("http://www.linuxsampler.org")
			);
		
			btnJSWebsite.addActionListener(
				e -> StdUtils.browse("http://sf.net/projects/jsampler/")
			);
		
			btnDocumentation.addActionListener(
				e -> StdUtils.browse("http://www.linuxsampler.org/documentation.html")
			);
		
			btnLSDevelopers.addActionListener(
				e -> StdUtils.browse("http://www.linuxsampler.org/developers.html")
			);
		
			btnLSMailingList.addActionListener(
				e -> StdUtils.browse("http://lists.sourceforge.net/lists/listinfo/linuxsampler-devel")
			);
		}
	}
	
	private static class Lnkbutton extends LinkButton {
		Lnkbutton(String s) {
			super(s);
			Color c = new Color(0xFFA300);
			setUnvisitedColor(c);
			setUnvisitedFontStyle(BOLD);
			setVisitedColor(c);
			setVisitedFontStyle(BOLD);
			setHoverColor(c);
			setHoverFontStyle(BOLD | UNDERLINE);
		}
	}
	
	private static class Button extends JButton {
		Button(String s) {
			super(s);
			putClientProperty (
				SubstanceLookAndFeel.BUTTON_SHAPER_PROPERTY,
				"org.jvnet.substance.button.StandardButtonShaper"
			);
			
			setForeground(new Color(0xFFA300));
			setFont(getFont().deriveFont(java.awt.Font.BOLD));
		}
	}
}



enum License { AGPL, GPL, LGPL }

class LicenseDlg extends InformationDialog {
	LicenseDlg(Dialog owner, License license) {
		super(owner);
		
		switch(license) {
			case AGPL: setTitle("GNU Affero General Public License"); break;
			case GPL: setTitle("GNU General Public License"); break;
			case LGPL: setTitle("GNU Lesser General Public License"); break;
		}
		
		JScrollPane sp = new JScrollPane(new LicensePane(license));
		sp.setPreferredSize(new Dimension(800, 400));
		
		setMainPane(sp);
	}
	
	static class LicensePane extends JEditorPane {
		private static final URL urlAGPL;
		private static final URL urlGPL;
		private static final URL urlLGPL;
	
		static {
			urlAGPL = ClassLoader.getSystemClassLoader().getResource("agpl-3.0.html");
			urlGPL = ClassLoader.getSystemClassLoader().getResource("gpl.html");
			urlLGPL = ClassLoader.getSystemClassLoader().getResource("lgpl.html");
		}
	
		LicensePane(License license) {
			try {
				switch(license) {
					case AGPL: setPage(urlAGPL); break;
					case GPL: setPage(urlGPL); break;
					case LGPL: setPage(urlLGPL); break;
				}
			} catch(Exception x) {
				x.printStackTrace();
			}
			
			setEditable(false);
		}
	}
}
