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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

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
		
		private ChannelViewDefaultsPane channelViewDefaultsPane = null;
		private final boolean showDefaultView;
		
		
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
				add(Box.createRigidArea(new Dimension(0, 6)));
				add(new javax.swing.JSeparator());
				add(Box.createRigidArea(new Dimension(0, 6)));
				
				channelViewDefaultsPane = new ChannelViewDefaultsPane();
				add(channelViewDefaultsPane);
			}
			
			setMaximumSize(new Dimension(Short.MAX_VALUE, getPreferredSize().height));
		}
		
		public void
		apply() {
			boolean b = checkChannelDefaults.isSelected();
			preferences().setBoolProperty(USE_CHANNEL_DEFAULTS, b);
			
			if(showDefaultView) {
				channelViewDefaultsPane.apply();
			}
		}
		
		protected void
		editChannelDefaults() {
			JDialog dlg = new JSChannelsDefaultSettingsPane().createDialog(owner);
			dlg.setVisible(true);
		}
	}
	
	public static class ChannelViewDefaultsPane extends JPanel {
		private final ComboBox cbDefaultView = new ComboBox();
		private final ComboBox cbMouseOverView = new ComboBox();
		
		private final JCheckBox checkMouseOverView =
			new JCheckBox(i18n.getLabel("JSDefaultsPropsPane.checkMouseOverView"));
		
		ChannelViewDefaultsPane() {
			GridBagLayout gridbag = new GridBagLayout();
			GridBagConstraints c = new GridBagConstraints();
			
			setLayout(gridbag);
			
			c.fill = GridBagConstraints.NONE;
			
			String s = i18n.getLabel("JSDefaultsPropsPane.lDefaultChannelView");
			JLabel l = new JLabel(s);
			
			c.gridx = 0;
			c.gridy = 0;
			c.anchor = GridBagConstraints.EAST;
			c.insets = new Insets(3, 3, 3, 3);
			gridbag.setConstraints(l, c);
			add(l);
			
			
			
			int i = preferences().getIntProperty(DEFAULT_CHANNEL_VIEW);
			cbDefaultView.setView(i);
			
			c.gridx = 1;
			c.gridy = 0;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.anchor = GridBagConstraints.WEST;
			gridbag.setConstraints(cbDefaultView, c);
			add(cbDefaultView);
			
			c.gridx = 0;
			c.gridy = 1;
			c.gridwidth = 2;
			c.insets = new Insets(9, 3, 3, 3);
			gridbag.setConstraints(checkMouseOverView, c);
			add(checkMouseOverView);
			
			i = preferences().getIntProperty(CHANNEL_VIEW_ON_MOUSE_OVER);
			cbMouseOverView.setView(i);
			
			c.gridx = 1;
			c.gridy = 2;
			c.gridwidth = 1;
			c.insets = new Insets(0, 3, 5, 3);
			gridbag.setConstraints(cbMouseOverView, c);
			add(cbMouseOverView);
			
			setAlignmentX(LEFT_ALIGNMENT);
			
			checkMouseOverView.addItemListener(new ItemListener() {
				public void
				itemStateChanged(ItemEvent e) {
					cbMouseOverView.setEnabled(checkMouseOverView.isSelected());
				}
			});
			
			cbMouseOverView.setEnabled(false);
			if(preferences().getBoolProperty(DIFFERENT_CHANNEL_VIEW_ON_MOUSE_OVER)) {
				checkMouseOverView.doClick(0);
			}
		}
		
		public void
		apply() {
			int i = cbDefaultView.getSelectedIndex();
			preferences().setIntProperty(DEFAULT_CHANNEL_VIEW, i);
			
			boolean b = checkMouseOverView.isSelected();
			preferences().setBoolProperty(DIFFERENT_CHANNEL_VIEW_ON_MOUSE_OVER, b);
			
			i = cbMouseOverView.getSelectedIndex();
			preferences().setIntProperty(CHANNEL_VIEW_ON_MOUSE_OVER, i);
		}
		
		class ComboBox extends JComboBox {
			ComboBox() {
				String s = i18n.getLabel("JSDefaultsPropsPane.lSmallView");
				addItem(s);
				
				s = i18n.getLabel("JSDefaultsPropsPane.lNormalView");
				addItem(s);
			}
			
			public void
			setView(int i) {
				if(i < 0 || i >= getItemCount()) i = 1;
			
				setSelectedIndex(i);
			}
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
