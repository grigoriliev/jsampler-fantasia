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

import java.awt.Frame;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import javax.swing.border.EtchedBorder;

import net.sf.juife.InformationDialog;

import org.jsampler.CC;

import org.linuxsampler.lscp.ServerInfo;

import static org.jsampler.view.classic.ClassicI18n.i18n;


/**
 *
 * @author Grigor Iliev
 */
public class SamplerInfoDlg extends InformationDialog {
	private final static ImageIcon iconLinuxSamplerLogo;
	
	static {
		java.net.URL url;
		url = ClassLoader.getSystemClassLoader().getResource (
			"org/jsampler/view/classic/res/LinuxSampler-logo.png"
		);
		iconLinuxSamplerLogo = new ImageIcon(url);
	}
	
	private final JLabel lLinuxSamplerLogo = new JLabel(iconLinuxSamplerLogo);
	private final JLabel lDescription = new JLabel();
	private final JLabel lVersion = new JLabel(i18n.getLabel("SamplerInfoDlg.lVersion"));
	private final JLabel lProtocolVersion =
		new JLabel(i18n.getLabel("SamplerInfoDlg.lProtocolVersion"));
	
	private final JTextField tfVersion = new JTextField();
	private final JTextField tfProtocolVersion = new JTextField();
	
	
	/** Creates a new instance of SamplerInfoDlg */
	public SamplerInfoDlg(Frame owner) {
		super(owner, i18n.getLabel("SamplerInfoDlg.title"));
		
		showCloseButton(false);
		setResizable(false);
		
		JPanel mainPane = new JPanel();
		ServerInfo si = CC.getSamplerModel().getServerInfo();
		
		if(si == null) {
			mainPane.add(new JLabel(i18n.getLabel("SamplerInfoDlg.unavailable")));
			setMainPane(mainPane);
			return;
		}
		
		lDescription.setText(si.getDescription());
		tfVersion.setText(si.getVersion());
		tfProtocolVersion.setText(si.getProtocolVersion());
		
		tfVersion.setEditable(false);
		tfVersion.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		
		tfProtocolVersion.setEditable(false);
		tfProtocolVersion.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		
		
		JPanel infoPane = new JPanel();
		
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		
		/*lText.setFont(lText.getFont().deriveFont(java.awt.Font.PLAIN));
		
		ServerInfo si = CC.getSamplerModel().getServerInfo();
		
		if(si == null) lText.setText( JSI18n.getLabel("SamplerInfoDlg.unavailable"));
		else lText.setText (
			"<html>\n" +
			"<h3>" + si.getDescription() + "</h3><br>" +
			"<table border=0  align=left>" +
			"<tr> <th align=right>Version:</th> <td align=left>" + si.getVersion() + "</td> </tr>" +
			"<tr> <th align=right>Protocol Version:</th> <td align=left>" + si.getProtocolVersion()+ "</td> </tr>" +
			"</table>"
		);
		
		mainPane.add(lText);
		*/
		infoPane.setLayout(gridbag);
		
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(3, 3, 3, 3);
		c.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints(lVersion, c);
		infoPane.add(lVersion);
		
		c.gridx = 0;
		c.gridy = 1;
		gridbag.setConstraints(lProtocolVersion, c);
		infoPane.add(lProtocolVersion);
		
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(tfVersion, c);
		infoPane.add(tfVersion);
		
		c.gridx = 1;
		c.gridy = 1;
		gridbag.setConstraints(tfProtocolVersion, c);
		infoPane.add(tfProtocolVersion);
		
		//infoPane.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		infoPane.setMaximumSize(infoPane.getPreferredSize());
		
		JPanel bodyPane = new JPanel();
		bodyPane.setLayout(new BoxLayout(bodyPane, BoxLayout.X_AXIS));
		bodyPane.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		
		bodyPane.add(lLinuxSamplerLogo);
		bodyPane.add(Box.createRigidArea(new Dimension(6, 0)));
		bodyPane.add(infoPane);
		
				
		mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
		
		mainPane.add(Box.createRigidArea(new Dimension(0, 6)));
		lDescription.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		mainPane.add(lDescription);
		mainPane.add(Box.createRigidArea(new Dimension(0, 12)));
		
		mainPane.add(bodyPane);
		
		setMainPane(mainPane);
	}
}
