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

import java.awt.Dialog;
import java.awt.Dimension;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jsampler.CC;
import org.jsampler.JSPrefs;

import org.linuxsampler.lscp.AudioOutputDriver;
import org.linuxsampler.lscp.MidiInputDriver;

import static org.jsampler.view.std.StdI18n.i18n;
import static org.jsampler.view.std.StdPrefs.*;

/**
 *
 * @author Grigor Iliev
 */
public class JSDefaultsPropsPane extends JPanel {
	private final ChannelDefaultsPane channelDefaultsPane;
	private final DefaultMidiDriverPane defaultMidiDriverPane = new DefaultMidiDriverPane();
	private final DefaultAudioDriverPane defaultAudioDriverPane = new DefaultAudioDriverPane();
	
	
	/** Creates a new instance of <code>JSDefaultsPropsPane</code> */
	public
	JSDefaultsPropsPane(Dialog owner, Icon iconChangeDefaults) {
		this(owner, iconChangeDefaults, false);
	}
	
	/** Creates a new instance of <code>JSDefaultsPropsPane</code> */
	public
	JSDefaultsPropsPane(Dialog owner, Icon iconChangeDefaults, boolean showDefaultChannelView) {
		channelDefaultsPane = new ChannelDefaultsPane(owner, iconChangeDefaults, showDefaultChannelView);
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(channelDefaultsPane);
		add(Box.createRigidArea(new Dimension(0, 6)));
		add(defaultMidiDriverPane);
		add(Box.createRigidArea(new Dimension(0, 6)));
		add(defaultAudioDriverPane);
	}
	
	private static JSPrefs
	preferences() { return CC.getViewConfig().preferences(); }
	
	public void
	apply() {
		channelDefaultsPane.apply();
		defaultMidiDriverPane.apply();
		defaultAudioDriverPane.apply();
	}
	
	public static class ChannelDefaultsPane extends JPanel {
		private final Dialog owner;
		private final JCheckBox checkChannelDefaults =
			new JCheckBox(i18n.getLabel("JSDefaultsPropsPane.checkChannelDefaults"));
		
		private final JButton btnChannelDefaults;
		
		private final boolean showDefaultView;
		private final JComboBox cbDefaultView = new JComboBox();
		
		
		public
		ChannelDefaultsPane(Dialog owner, Icon iconChangeDefaults) {
			this(owner, iconChangeDefaults, false);
		}
		
		public
		ChannelDefaultsPane(Dialog owner, Icon iconChangeDefaults, boolean showDefaultView) {
			this.owner = owner;
			this.showDefaultView = showDefaultView;
			btnChannelDefaults = new JButton(iconChangeDefaults);
			btnChannelDefaults.setEnabled(false);
			btnChannelDefaults.setMargin(new java.awt.Insets(0, 0, 0, 0));
			
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			
			JPanel p = new JPanel();
			p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
			
			p.add(checkChannelDefaults);
			p.add(Box.createRigidArea(new Dimension(5, 0)));
			p.add(btnChannelDefaults);
			p.setAlignmentX(LEFT_ALIGNMENT);
			p.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
			add(p);
			
			setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
			
			String s = i18n.getLabel("JSDefaultsPropsPane.titleChannels");
			setBorder(BorderFactory.createTitledBorder(s));
			setAlignmentX(LEFT_ALIGNMENT);
			
			checkChannelDefaults.addItemListener(new ItemListener() {
				public void
				itemStateChanged(ItemEvent e) {
					btnChannelDefaults.setEnabled(checkChannelDefaults.isSelected());
				}
			});
			
			if(preferences().getBoolProperty(USE_CHANNEL_DEFAULTS)) {
				checkChannelDefaults.doClick(0);
			}
			
			btnChannelDefaults.addActionListener(new ActionListener() {
				public void
				actionPerformed(ActionEvent e) { editChannelDefaults(); }
			});
			
			if(showDefaultView) {
				p = new JPanel();
				p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
				
				s = i18n.getLabel("JSDefaultsPropsPane.lDefaultChannelView");
				p.add(new JLabel(s));
				
				p.add(Box.createRigidArea(new Dimension(5, 0)));
				
				s = i18n.getLabel("JSDefaultsPropsPane.lSmallView");
				cbDefaultView.addItem(s);
				
				s = i18n.getLabel("JSDefaultsPropsPane.lNormalView");
				cbDefaultView.addItem(s);
				
				int i = preferences().getIntProperty(DEFAULT_CHANNEL_VIEW);
				if(i < 0 || i >= cbDefaultView.getItemCount()) i = 1;
				
				cbDefaultView.setSelectedIndex(i);
				
				p.add(cbDefaultView);
				
				p.setAlignmentX(LEFT_ALIGNMENT);
				p.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
				add(p);
			}
			
			setMaximumSize(new Dimension(Short.MAX_VALUE, getPreferredSize().height));
		}
		
		public void
		apply() {
			boolean b = checkChannelDefaults.isSelected();
			preferences().setBoolProperty(USE_CHANNEL_DEFAULTS, b);
			
			if(showDefaultView) {
				int i = cbDefaultView.getSelectedIndex();
				preferences().setIntProperty(DEFAULT_CHANNEL_VIEW, i);
			}
		}
		
		protected void
		editChannelDefaults() {
			JDialog dlg = new JSChannelsDefaultSettingsPane().createDialog(owner);
			dlg.setVisible(true);
		}
	}
	
	public static class DefaultMidiDriverPane extends JPanel {
		private final JComboBox cbDriver = new JComboBox();
		
		public
		DefaultMidiDriverPane() {
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			
			JPanel p = new JPanel();
			p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
			
			cbDriver.setAlignmentX(LEFT_ALIGNMENT);
			int h = cbDriver.getPreferredSize().height;
			cbDriver.setMaximumSize(new Dimension(Short.MAX_VALUE, h));
			
			p.add(cbDriver);
			p.setAlignmentX(LEFT_ALIGNMENT);
			p.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
			
			add(p);
			
			String s = i18n.getLabel("JSDefaultsPropsPane.titleMidiDriver");
			setBorder(BorderFactory.createTitledBorder(s));
			setAlignmentX(LEFT_ALIGNMENT);
			
			// not connected?
			if(CC.getSamplerModel().getMidiInputDrivers() == null) return;
			
			for(Object o : CC.getSamplerModel().getMidiInputDrivers()) {
				cbDriver.addItem(o);
			}
			
			String drv = preferences().getStringProperty(DEFAULT_MIDI_DRIVER);
			for(MidiInputDriver d : CC.getSamplerModel().getMidiInputDrivers()) {
				if(d.getName().equals(drv)){
					cbDriver.setSelectedItem(d);
					break;
				}
			}
		}
		
		public void
		apply() {
			// not connected?
			if(CC.getSamplerModel().getMidiInputDrivers() == null) return;
			
			Object o = cbDriver.getSelectedItem();
			if(o == null) {
				preferences().setStringProperty(DEFAULT_MIDI_DRIVER, null);
				return;
			}
			
			String drv = ((MidiInputDriver) o).getName();
			preferences().setStringProperty(DEFAULT_MIDI_DRIVER, drv);
		}
	}
	
	public static class DefaultAudioDriverPane extends JPanel {
		private final JComboBox cbDriver = new JComboBox();
		
		public
		DefaultAudioDriverPane() {
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			
			JPanel p = new JPanel();
			p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
			
			cbDriver.setAlignmentX(LEFT_ALIGNMENT);
			int h = cbDriver.getPreferredSize().height;
			cbDriver.setMaximumSize(new Dimension(Short.MAX_VALUE, h));
			
			p.add(cbDriver);
			p.setAlignmentX(LEFT_ALIGNMENT);
			p.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
			
			add(p);
			
			String s = i18n.getLabel("JSDefaultsPropsPane.titleAudioDriver");
			setBorder(BorderFactory.createTitledBorder(s));
			setAlignmentX(LEFT_ALIGNMENT);
			
			// not connected?
			if(CC.getSamplerModel().getAudioOutputDrivers() == null) return;
			
			for(Object o : CC.getSamplerModel().getAudioOutputDrivers()) {
				cbDriver.addItem(o);
			}
			
			String drv = preferences().getStringProperty(DEFAULT_AUDIO_DRIVER);
			for(AudioOutputDriver d : CC.getSamplerModel().getAudioOutputDrivers()) {
				if(d.getName().equals(drv)){
					cbDriver.setSelectedItem(d);
					break;
				}
			}
		}
		
		public void
		apply() {
			// not connected?
			if(CC.getSamplerModel().getAudioOutputDrivers() == null) return;
			
			Object o = cbDriver.getSelectedItem();
			if(o == null) {
				preferences().setStringProperty(DEFAULT_AUDIO_DRIVER, null);
				return;
			}
			
			String drv = ((AudioOutputDriver) o).getName();
			preferences().setStringProperty(DEFAULT_AUDIO_DRIVER, drv);
		}
	}
}
