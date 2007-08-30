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
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.jsampler.CC;
import org.jsampler.JSPrefs;

import static org.jsampler.view.std.StdI18n.i18n;
import static org.jsampler.view.std.StdPrefs.*;

/**
 *
 * @author Grigor Iliev
 */
public class JSDefaultsPropsPane extends JPanel {
	private final ChannelDefaultsPane channelDefaultsPane;
	
	
	/** Creates a new instance of <code>JSDefaultsPropsPane</code> */
	public
	JSDefaultsPropsPane(Dialog owner, Icon iconChangeDefaults) {
		channelDefaultsPane = new ChannelDefaultsPane(owner, iconChangeDefaults);
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(channelDefaultsPane);
	}
	
	private static JSPrefs
	preferences() { return CC.getViewConfig().preferences(); }
	
	public void
	apply() {
		channelDefaultsPane.apply();
	}
	
	public static class ChannelDefaultsPane extends JPanel {
		private final Dialog owner;
		private final JCheckBox checkChannelDefaults =
			new JCheckBox(i18n.getLabel("JSDefaultsPropsPane.checkChannelDefaults"));
		
		private final JButton btnChannelDefaults;
		
		
		public
		ChannelDefaultsPane(Dialog owner, Icon iconChangeDefaults) {
			this.owner = owner;
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
			
			setMaximumSize(new Dimension(Short.MAX_VALUE, getMaximumSize().height));
			
			if(preferences().getBoolProperty(USE_CHANNEL_DEFAULTS)) {
				checkChannelDefaults.doClick(0);
			}
			
			btnChannelDefaults.addActionListener(new ActionListener() {
				public void
				actionPerformed(ActionEvent e) { editChannelDefaults(); }
			});
		}
		
		public void
		apply() {
			boolean b = checkChannelDefaults.isSelected();
			preferences().setBoolProperty(USE_CHANNEL_DEFAULTS, b);
		}
		
		protected void
		editChannelDefaults() {
			JDialog dlg = new JSChannelsDefaultSettingsPane().createDialog(owner);
			dlg.setVisible(true);
		}
	}
}
