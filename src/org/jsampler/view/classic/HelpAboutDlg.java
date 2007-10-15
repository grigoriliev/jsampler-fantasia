/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2007 Grigor Iliev <grigor@grigoriliev.com>
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

import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.net.URI;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import net.sf.juife.InformationDialog;
import net.sf.juife.JuifeUtils;
import net.sf.juife.LinkButton;

import org.jsampler.HF;

import static org.jsampler.view.classic.ClassicI18n.i18n;


/**
 *
 * @author grish
 */
public class HelpAboutDlg extends InformationDialog {
	private JLabel lProductName =
		new JLabel("<html>\n<font size=+1>JS Classic (version 0.7a)</font>");
	
	private JLabel lAuthor = new JLabel(i18n.getLabel("HelpAboutDlg.lAuthor"));
	private JTextField tfAuthor = new JTextField(i18n.getLabel("HelpAboutDlg.tfAuthor"));
	
	private JLabel lLicense = new JLabel(i18n.getLabel("HelpAboutDlg.lLicense"));
	private LinkButton btnLicense = new LinkButton("GNU General Public License");
	
	private JLabel lLibraries = new JLabel("Using:");
	
	private LinkButton btnJlscp =
		new LinkButton("jlscp - A java LinuxSampler control protocol API");
	
	private LinkButton btnJuife
		= new LinkButton("juife - Java User Interface Framework Extensions");
	
	
	private JLabel lCopyright = new JLabel(i18n.getLabel("HelpAboutDlg.lCopyright"));
	
	private JPanel mainPane = new JPanel();
	
	/** Creates a new instance of HelpAboutDlg */
	public HelpAboutDlg(Frame owner) {
		super(owner, i18n.getLabel("HelpAboutDlg.title"));
		
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		
		JPanel p = new JPanel();
		p.setLayout(gridbag);
		
		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(0, 0, 0, 6);
		c.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints(lAuthor, c);
		p.add(lAuthor);
		
		c.gridx = 0;
		c.gridy = 2;
		gridbag.setConstraints(lLicense, c);
		p.add(lLicense);
		
		c.gridx = 0;
		c.gridy = 3;
		c.insets = new Insets(12, 0, 0, 6);
		c.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints(lLibraries, c);
		p.add(lLibraries);
		
		tfAuthor.setEditable(false);
		tfAuthor.setBorder(BorderFactory.createEmptyBorder());
		
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 1.0;
		c.insets = new Insets(0, 0, 0, 0);
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(tfAuthor, c);
		p.add(tfAuthor);
		
		c.gridx = 1;
		c.gridy = 2;
		gridbag.setConstraints(btnLicense, c);
		p.add(btnLicense);
		
		c.gridx = 1;
		c.gridy = 3;
		c.insets = new Insets(12, 0, 0, 0);
		gridbag.setConstraints(btnJlscp, c);
		p.add(btnJlscp);
		
		c.gridx = 1;
		c.gridy = 4;
		c.insets = new Insets(0, 0, 0, 0);
		gridbag.setConstraints(btnJuife, c);
		p.add(btnJuife);
		
		//
		/*c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		gridbag.setConstraints(lCopyright, c);
		p.add(lCopyright);*/
		
		lProductName.setHorizontalAlignment(JLabel.CENTER);
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.insets = new Insets(0, 0, 12, 0);
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(lProductName, c);
		p.add(lProductName);
		
		/*p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		
		p.add(lLicense);
		p.add(Box.createRigidArea(new Dimension(5, 0)));
		p.add(btnLicense);
		
		mainPane.add(p);*/
		
		mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
		p.setAlignmentX(LEFT_ALIGNMENT);
		mainPane.add(p);
		mainPane.add(Box.createRigidArea(new Dimension(0, 12)));
		
		/*lContactInfo.setAlignmentX(LEFT_ALIGNMENT);
		mainPane.add(lContactInfo);
		
		JSeparator sep = new JSeparator();
		sep.setAlignmentX(LEFT_ALIGNMENT);
		mainPane.add(sep);*/
		
		ContactInfoPane contactInfoPane = new ContactInfoPane();
		contactInfoPane.setAlignmentX(LEFT_ALIGNMENT);
		mainPane.add(contactInfoPane);
		
		mainPane.add(Box.createRigidArea(new Dimension(0, 12)));
		
		lCopyright.setFont(lCopyright.getFont().deriveFont(java.awt.Font.PLAIN));
		lCopyright.setHorizontalAlignment(JLabel.CENTER);
		lCopyright.setAlignmentX(LEFT_ALIGNMENT);
		mainPane.add(lCopyright);
		
		setMainPane(mainPane);
		
		pack();
		//setResizable(false);
		
		btnLicense.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { showLicense(License.GPL); }
		});
		
		btnJlscp.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { showJlscpInfo(); }
		});
		
		btnJuife.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { showJuifeInfo(); }
		});
	}
	
	private void
	showLicense(License license) {
		new LicenseDlg(this, license).setVisible(true);
	}
	
	private void
	showJlscpInfo() {
		String website = "http://sourceforge.net/projects/jlscp";
		String ver = Package.getPackage("org.linuxsampler.lscp").getImplementationVersion();
		new LibraryInfoDlg(this, "jlscp", ver, website, License.GPL).setVisible(true);
	}
	
	private void
	showJuifeInfo() {
		String website = "http://sourceforge.net/projects/juife";
		String ver = Package.getPackage("net.sf.juife").getImplementationVersion();
		new LibraryInfoDlg(this, "juife", ver, website, License.LGPL).setVisible(true);
	}
	
	class ContactInfoPane extends JPanel {
		private JLabel lAuthorEmail =
			new JLabel(i18n.getLabel("HelpAboutDlg.lAuthorEmail"));
		private JLabel lLSWebsite = new JLabel(i18n.getLabel("HelpAboutDlg.lLSWebsite"));
		private LinkButton btnAuthorEmail = new LinkButton("grigor@grigoriliev.com");
		private LinkButton btnLSWebsite = new LinkButton("www.linuxsampler.org");
		
		private JLabel lLSMailingList =
			new JLabel(i18n.getLabel("HelpAboutDlg.lLSMailingList"));
		
		private LinkButton btnMailingList = new LinkButton (
			"lists.sourceforge.net/lists/listinfo/linuxsampler-devel"
		);
	
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
			gridbag.setConstraints(lLSMailingList, c);
			add(lLSMailingList);
			
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
			c.gridwidth = 2;
			gridbag.setConstraints(btnMailingList, c);
			add(btnMailingList);
			
			setBorder(BorderFactory.createTitledBorder (
				i18n.getLabel("HelpAboutDlg.contactInfoPane")
			));
			
			installListeners();
		}
		
		private void
		installListeners() {
			btnAuthorEmail.addActionListener(new ActionListener() {
				public void
				actionPerformed(ActionEvent e) {
					browse("mailto:grigor@grigoriliev.com");
				}
			});
		
			btnLSWebsite.addActionListener(new ActionListener() {
				public void
				actionPerformed(ActionEvent e) {
					browse("http://www.linuxsampler.org");
				}
			});
		
			btnMailingList.addActionListener(new ActionListener() {
				public void
				actionPerformed(ActionEvent e) {
					browse("http://lists.sourceforge.net/lists/listinfo/linuxsampler-devel");
				}
			});
		}
	}
	
	private boolean
	checkDesktopSupported() {
		if(Desktop.isDesktopSupported()) return true;
		
		HF.showErrorMessage(i18n.getError("HelpAboutDlg.DesktopApiNotSupported"), this);
		
		return false;
	}
	
	private void
	browse(String uri) {
		if(!checkDesktopSupported()) return;
		
		try { Desktop.getDesktop().browse(new URI(uri)); }
		catch(Exception x) { x.printStackTrace(); }
	}
	
	private void
	mail(String uri) {
		if(!checkDesktopSupported()) return;
		
		Desktop desktop = Desktop.getDesktop();
		
		try { Desktop.getDesktop().mail(new URI(uri)); }
		catch(Exception x) { x.printStackTrace(); }
	}
}

class WebButton extends LinkButton {
	WebButton(String text) {
		super(text);
		
		setDisabledColor(getUnvisitedColor());
		setDisabledFontStyle(UNDERLINE);
		setUnvisitedFontStyle(UNDERLINE);
		setEnabled(false);
	}
}

enum License { GPL, LGPL }

class LicenseDlg extends InformationDialog {
	LicenseDlg(Dialog owner, License license) {
		super(owner);
		
		switch(license) {
			case GPL: setTitle("GNU General Public License"); break;
			case LGPL: setTitle("GNU Lesser General Public License"); break;
		}
		
		JScrollPane sp = new JScrollPane(new LicensePane(license));
		sp.setPreferredSize(new Dimension(800, 400));
		
		setMainPane(sp);
	}
	
	static class LicensePane extends JEditorPane {
		private static URL urlGPL;
		private static URL urlLGPL;
	
		static {
			String s = "licenses/gpl.html";
			urlGPL = ClassLoader.getSystemClassLoader().getResource(s);
				s = "licenses/lgpl.html";
			urlLGPL = ClassLoader.getSystemClassLoader().getResource(s);
		}
	
		LicensePane(License license) {
			try {
				switch(license) {
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

class LibraryInfoDlg extends InformationDialog {
	private JLabel lAuthor = new JLabel(i18n.getLabel("LibraryInfoDlg.lAuthor"));
	private JTextField tfAuthor = new JTextField(i18n.getLabel("LibraryInfoDlg.tfAuthor"));
	
	private JLabel lLicense = new JLabel(i18n.getLabel("LibraryInfoDlg.lLicense"));
	private LinkButton btnLicense = new LinkButton(" ");
	
	private JLabel lWebsite = new JLabel(i18n.getLabel("LibraryInfoDlg.lWebsite"));
	private WebButton btnWebsite = new WebButton("");
		
	LibraryInfoDlg (
		Dialog owner,
		String libName,
		String libVersion,
		String website,
		final License license
	) {
		super(owner, libName);
		
		switch(license) {
			case GPL: btnLicense.setText("GNU General Public License"); break;
			case LGPL: btnLicense.setText("GNU Lesser General Public License"); break;
		}
		
		btnWebsite.setText(website);
		
		setMainPane(new LibraryInfoPane(libName, libVersion, license));
		
		btnLicense.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { showLicense(license); }
		});
	}
	
	private void
	showLicense(License license) {
		new LicenseDlg(this, license).setVisible(true);
	}
	
	class LibraryInfoPane extends JPanel {
		private JLabel lProductName;
		
		
		LibraryInfoPane(String libName, String libVersion, final License license) {
			lProductName = new JLabel (
				"<html>\n<font size=+1>" + libName +
				" (version " + libVersion + ")</font>"
			);
			
			GridBagLayout gridbag = new GridBagLayout();
			GridBagConstraints c = new GridBagConstraints();
		
			setLayout(gridbag);
		
			c.gridx = 0;
			c.gridy = 1;
			c.insets = new Insets(0, 0, 0, 6);
			c.anchor = GridBagConstraints.EAST;
			gridbag.setConstraints(lAuthor, c);
			add(lAuthor);
			
			c.gridx = 0;
			c.gridy = 2;
			gridbag.setConstraints(lLicense, c);
			add(lLicense);
			
			c.gridx = 0;
			c.gridy = 3;
			c.insets = new Insets(12, 0, 0, 6);
			gridbag.setConstraints(lWebsite, c);
			add(lWebsite);
			
			tfAuthor.setEditable(false);
			tfAuthor.setBorder(BorderFactory.createEmptyBorder());
		
			c.gridx = 1;
			c.gridy = 1;
			c.insets = new Insets(0, 0, 0, 0);
			c.anchor = GridBagConstraints.WEST;
			gridbag.setConstraints(tfAuthor, c);
			add(tfAuthor);
			
			c.gridx = 1;
			c.gridy = 2;
			gridbag.setConstraints(btnLicense, c);
			add(btnLicense);
			
			c.gridx = 1;
			c.gridy = 3;
			c.insets = new Insets(12, 0, 0, 0);
			gridbag.setConstraints(btnWebsite, c);
			add(btnWebsite);
			
			lProductName.setHorizontalAlignment(JLabel.CENTER);
			c.gridx = 0;
			c.gridy = 0;
			c.gridwidth = 2;
			c.insets = new Insets(0, 0, 12, 0);
			c.anchor = GridBagConstraints.CENTER;
			gridbag.setConstraints(lProductName, c);
			add(lProductName);
		}
	}
}
