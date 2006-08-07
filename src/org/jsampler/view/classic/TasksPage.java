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

import java.awt.Dimension;
import java.awt.MediaTracker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.net.URL;

import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import net.sf.juife.LinkButton;
import net.sf.juife.NavigationPage;

import org.jsampler.CC;

import static org.jsampler.view.classic.ClassicI18n.i18n;
import static org.jsampler.view.classic.LeftPane.getLeftPane;


/**
 *
 * @author Grigor Iliev
 */
public class TasksPage extends NavigationPage {
	private LinkButton lnkMidiDevices =
		new LinkButton(i18n.getButtonLabel("TasksPage.lnkMidiDevices"));
	private LinkButton lnkNewMidiDevice =
		new LinkButton(i18n.getButtonLabel("TasksPage.lnkNewMidiDevice"));
	private LinkButton lnkAudioDevices =
		new LinkButton(i18n.getButtonLabel("TasksPage.lnkAudioDevices"));
	private LinkButton lnkNewAudioDevice =
		new LinkButton(i18n.getButtonLabel("TasksPage.lnkNewAudioDevice"));
	private LinkButton lnkNewChannel =
		new LinkButton(i18n.getButtonLabel("TasksPage.lnkNewChannel"));
	private LinkButton lnkNewChannelWizard =
		new LinkButton(i18n.getButtonLabel("TasksPage.lnkNewChannelWizard"));
	private LinkButton lnkOrchestras =
		new LinkButton(i18n.getButtonLabel("TasksPage.lnkOrchestras"));
	private LinkButton lnkManageOrchestras =
		new LinkButton(i18n.getButtonLabel("TasksPage.lnkManageOrchestras"));
	
	
	private LinkButton lnkRefreshSampler =
		new LinkButton(i18n.getButtonLabel("TasksPage.lnkRefreshSampler"));
	
	/** Creates a new instance of <code>TasksPage</code> */
	public
	TasksPage() {
		setTitle(i18n.getLabel("TasksPage.title"));
		
		int h = lnkRefreshSampler.getPreferredSize().height;
		try {
			URL url = ClassLoader.getSystemClassLoader().getResource (
				"org/jsampler/view/classic/res/icons/Refresh16.gif"
			);
			
			ImageIcon icon = new ImageIcon(url);
			if(icon.getImageLoadStatus() == MediaTracker.COMPLETE)
				lnkRefreshSampler.setIcon(icon);
		} catch(Exception x) { CC.getLogger().log(Level.INFO, x.getMessage(), x); }
		
		lnkRefreshSampler.setText(i18n.getButtonLabel("TasksPage.lnkRefreshSampler"));
		lnkRefreshSampler.setMaximumSize(lnkRefreshSampler.getPreferredSize());
		setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JLabel lChannels = new JLabel(i18n.getLabel("TasksPage.lChannels"));
		add(lChannels);
		
		JSeparator sep = new JSeparator();
		sep.setMaximumSize(new Dimension(Short.MAX_VALUE, sep.getPreferredSize().height));
		add(sep);
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setBorder(BorderFactory.createEmptyBorder(6, 12, 17, 0));
		
		p.add(lnkNewChannel);
		p.add(lnkNewChannelWizard);
		p.add(Box.createGlue());
		p.setMaximumSize(p.getPreferredSize());
		
		add(p);
		
		JLabel lMidiDevices = new JLabel(i18n.getLabel("TasksPage.lMidiDevices"));
		add(lMidiDevices);
		sep = new JSeparator();
		sep.setMaximumSize(new Dimension(Short.MAX_VALUE, sep.getPreferredSize().height));
		add(sep);
		
		p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setBorder(BorderFactory.createEmptyBorder(6, 12, 17, 0));
		
		p.add(lnkNewMidiDevice);
		p.add(lnkMidiDevices);
		p.add(Box.createGlue());
		p.setMaximumSize(p.getPreferredSize());
		
		add(p);
		
		JLabel lAudioDevices = new JLabel(i18n.getLabel("TasksPage.lAudioDevices"));
		add(lAudioDevices);
		
		sep = new JSeparator();
		sep.setMaximumSize(new Dimension(Short.MAX_VALUE, sep.getPreferredSize().height));
		add(sep);
		
		p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setBorder(BorderFactory.createEmptyBorder(6, 12, 17, 0));
		
		p.add(lnkNewAudioDevice);
		p.add(lnkAudioDevices);
		p.add(Box.createGlue());
		p.setMaximumSize(p.getPreferredSize());
		
		add(p);
		
		JLabel lOrchestras = new JLabel(i18n.getLabel("TasksPage.lOrchestras"));
		add(lOrchestras);
		
		sep = new JSeparator();
		sep.setMaximumSize(new Dimension(Short.MAX_VALUE, sep.getPreferredSize().height));
		add(sep);
		
		p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setBorder(BorderFactory.createEmptyBorder(6, 12, 17, 0));
		
		p.add(lnkOrchestras);
		p.add(lnkManageOrchestras);
		p.add(Box.createGlue());
		p.setMaximumSize(p.getPreferredSize());
		
		add(p);
		
		add(Box.createGlue());
		
		sep = new JSeparator();
		sep.setMaximumSize(new Dimension(Short.MAX_VALUE, sep.getPreferredSize().height));
		add(sep);
		
		p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));
		
		p.add(lnkRefreshSampler);
		p.add(Box.createGlue());
		p.setMaximumSize(p.getPreferredSize());
		
		add(p);
		
		lnkNewChannel.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				A4n.newChannel.actionPerformed(null);
			}
		});
		
		lnkNewChannelWizard.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				A4n.newChannelWizard.actionPerformed(null);
			}
		});
		
		lnkNewMidiDevice.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				A4n.addMidiDevice.actionPerformed(null);
			}
		});
		
		lnkMidiDevices.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { getLeftPane().showMidiDevicesPage(); }
		});
		
		lnkNewAudioDevice.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				A4n.addAudioDevice.actionPerformed(null);
			}
		});
		
		lnkAudioDevices.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { getLeftPane().showAudioDevicesPage(); }
		});
		
		lnkOrchestras.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {  getLeftPane().showOrchestrasPage(); }
		});
		
		lnkManageOrchestras.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { getLeftPane().showManageOrchestrasPage(); }
		});
		
		lnkRefreshSampler.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { A4n.refresh.actionPerformed(null); }
		});
	}
	
}
