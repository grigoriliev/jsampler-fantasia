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

package org.jsampler.view.std;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;

import org.linuxsampler.lscp.DbInstrumentInfo;

import static org.jsampler.view.std.StdI18n.i18n;


/**
 *
 * @author Grigor Iliev
 */
public class JSDbInstrumentPropsPane extends JPanel {
	private final JLabel lName = createLabel(i18n.getLabel("JSDbInstrumentPropsPane.lName"));
	private final JLabel lLocation =
		createLabel(i18n.getLabel("JSDbInstrumentPropsPane.lLocation"));
	
	private final JLabel lSize = createLabel(i18n.getLabel("JSDbInstrumentPropsPane.lSize"));
	
	private final JLabel lFormat = createLabel(i18n.getLabel("JSDbInstrumentPropsPane.lFormat"));
	private final JLabel lType = createLabel(i18n.getLabel("JSDbInstrumentPropsPane.lType"));
	private final JLabel lDesc = createLabel(i18n.getLabel("JSDbInstrumentPropsPane.lDesc"));
	private final JLabel lCreated =
		createLabel(i18n.getLabel("JSDbInstrumentPropsPane.lCreated"));
	private final JLabel lModified =
		createLabel(i18n.getLabel("JSDbInstrumentPropsPane.lModified"));
	
	private final JLabel lFile = createLabel(i18n.getLabel("JSDbInstrumentPropsPane.lFile"));
	private final JLabel lIndex = createLabel(i18n.getLabel("JSDbInstrumentPropsPane.lIndex"));
	private final JLabel lProduct = createLabel(i18n.getLabel("JSDbInstrumentPropsPane.lProduct"));
	private final JLabel lArtists = createLabel(i18n.getLabel("JSDbInstrumentPropsPane.lArtists"));
	private final JLabel lKeywords =
		createLabel(i18n.getLabel("JSDbInstrumentPropsPane.lKeywords"));
	
	private final JTextArea taName = createTextArea();
	private final JTextArea taLocation = createTextArea();
	private final JTextArea taSize = createTextArea();
	private final JTextArea taFormat = createTextArea();
	private final JTextArea taType = createTextArea();
	private final JTextArea taDesc = createTextArea();
	private final JTextArea taCreated = createTextArea();
	private final JTextArea taModified = createTextArea();
	private final JTextArea taFile = createTextArea();
	private final JTextArea taIndex = createTextArea();
	private final JTextArea taProduct = createTextArea();
	private final JTextArea taArtists = createTextArea();
	private final JTextArea taKeywords = createTextArea();
	
	/**
	 * Creates a new instance of <code>JSDbInstrumentPropsPane</code>
	 */
	public JSDbInstrumentPropsPane(DbInstrumentInfo instrInfo) {
		setInstrumentInfo(instrInfo);
		
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		
		setLayout(gridbag);
		
		c.fill = GridBagConstraints.NONE;
		
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.NORTHEAST;
		c.insets = new Insets(3, 3, 3, 3);
		gridbag.setConstraints(lName, c);
		add(lName);
		
		c.gridx = 0;
		c.gridy = 2;
		gridbag.setConstraints(lLocation, c);
		add(lLocation);
		
		c.gridx = 0;
		c.gridy = 3;
		gridbag.setConstraints(lSize, c);
		add(lSize);
		
		c.gridx = 0;
		c.gridy = 4;
		gridbag.setConstraints(lFormat, c);
		add(lFormat);
		
		c.gridx = 0;
		c.gridy = 5;
		gridbag.setConstraints(lType, c);
		add(lType);
		
		c.gridx = 0;
		c.gridy = 6;
		gridbag.setConstraints(lDesc, c);
		add(lDesc);
		
		c.gridx = 0;
		c.gridy = 8;
		gridbag.setConstraints(lCreated, c);
		add(lCreated);
		
		c.gridx = 0;
		c.gridy = 9;
		gridbag.setConstraints(lModified, c);
		add(lModified);
		
		c.gridx = 0;
		c.gridy = 11;
		gridbag.setConstraints(lFile, c);
		add(lFile);
		
		c.gridx = 0;
		c.gridy = 12;
		gridbag.setConstraints(lIndex, c);
		add(lIndex);
		
		c.gridx = 0;
		c.gridy = 14;
		gridbag.setConstraints(lProduct, c);
		add(lProduct);
		
		c.gridx = 0;
		c.gridy = 15;
		gridbag.setConstraints(lArtists, c);
		add(lArtists);
		
		c.gridx = 0;
		c.gridy = 16;
		gridbag.setConstraints(lKeywords, c);
		add(lKeywords);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.NORTHWEST;
		gridbag.setConstraints(taName, c);
		add(taName);
			
		c.gridx = 1;
		c.gridy = 2;
		gridbag.setConstraints(taLocation, c);
		add(taLocation);
			
		c.gridx = 1;
		c.gridy = 3;
		gridbag.setConstraints(taSize, c);
		add(taSize);
			
		c.gridx = 1;
		c.gridy = 4;
		gridbag.setConstraints(taFormat, c);
		add(taFormat);
			
		c.gridx = 1;
		c.gridy = 5;
		gridbag.setConstraints(taType, c);
		add(taType);
			
		c.gridx = 1;
		c.gridy = 6;
		gridbag.setConstraints(taDesc, c);
		add(taDesc);
			
		c.gridx = 1;
		c.gridy = 8;
		gridbag.setConstraints(taCreated, c);
		add(taCreated);
			
		c.gridx = 1;
		c.gridy = 9;
		gridbag.setConstraints(taModified, c);
		add(taModified);
			
		c.gridx = 1;
		c.gridy = 11;
		gridbag.setConstraints(taFile, c);
		add(taFile);
			
		c.gridx = 1;
		c.gridy = 12;
		gridbag.setConstraints(taIndex, c);
		add(taIndex);
			
		c.gridx = 1;
		c.gridy = 14;
		gridbag.setConstraints(taProduct, c);
		add(taProduct);
			
		c.gridx = 1;
		c.gridy = 15;
		gridbag.setConstraints(taArtists, c);
		add(taArtists);
			
		c.gridx = 1;
		c.gridy = 16;
		gridbag.setConstraints(taKeywords, c);
		add(taKeywords);
		
		JSeparator sep = new JSeparator();
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		gridbag.setConstraints(sep, c);
		add(sep);
		
		sep = new JSeparator();
		c.gridx = 0;
		c.gridy = 7;
		gridbag.setConstraints(sep, c);
		add(sep);
		
		sep = new JSeparator();
		c.gridx = 0;
		c.gridy = 10;
		gridbag.setConstraints(sep, c);
		add(sep);
		
		sep = new JSeparator();
		c.gridx = 0;
		c.gridy = 13;
		gridbag.setConstraints(sep, c);
		add(sep);
		
		JPanel p = new JPanel();
		p.setOpaque(false);
		c.gridx = 0;
		c.gridy = 17;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		gridbag.setConstraints(p, c);
		add(p);
		
		Dimension d = getPreferredSize();
		int w = d.width > 300 ? d.width : 300;
		int h = d.height > 300 ? d.height : 300;
		setPreferredSize(new Dimension(w, h));
	}
	
	public void
	setInstrumentInfo(DbInstrumentInfo instrInfo) {
		taName.setText(instrInfo.getName());
		taLocation.setText(instrInfo.getDirectoryPath());
		
		String s = instrInfo.getFormatedSize();
		s += " (" + String.valueOf(instrInfo.getSize()) + " bytes)";
		taSize.setText(s);
		
		s = instrInfo.getFormatFamily();
		if(s.equals("GIG")) s = "GigaSampler";
		String ver = instrInfo.getFormatVersion();
		if(ver != null & ver.length() > 0) s += ", version " + ver;
		taFormat.setText(s);
		
		if(instrInfo.isDrum()) taType.setText(i18n.getLabel("JSDbInstrumentPropsPane.drum"));
		else taType.setText(i18n.getLabel("JSDbInstrumentPropsPane.chromatic"));
		
		taDesc.setText(instrInfo.getDescription());
		taCreated.setText(instrInfo.getDateCreated().toString());
		taModified.setText(instrInfo.getDateModified().toString());
		taFile.setText(instrInfo.getFilePath());
		taIndex.setText(String.valueOf(instrInfo.getInstrumentIndex()));
		taProduct.setText(instrInfo.getProduct());
		taArtists.setText(instrInfo.getArtists());
		taKeywords.setText(instrInfo.getKeywords());
	}
	
	protected JLabel
	createLabel(String text) {
		JLabel l = new JLabel(text);
		l.setFont(l.getFont().deriveFont(java.awt.Font.BOLD));
		return l;
	}
	
	protected JTextArea
	createTextArea() { return new TextArea(); }
	
	private class
	TextArea extends JTextArea {
		TextArea() {
			setLineWrap(true);
			setEditable(false);
			setOpaque(false);
			putClientProperty("substancelaf.noExtraElements", Boolean.TRUE);
			setBorder(BorderFactory.createEmptyBorder());
		}
	}
}
