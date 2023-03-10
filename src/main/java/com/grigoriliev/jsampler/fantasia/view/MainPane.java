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

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;

import com.grigoriliev.jsampler.fantasia.view.basic.FantasiaPanel;
import com.grigoriliev.jsampler.fantasia.view.basic.FantasiaToggleButtonsPanel;
import com.grigoriliev.jsampler.fantasia.view.basic.PixmapButton;
import com.grigoriliev.jsampler.fantasia.view.basic.PixmapPane;
import com.grigoriliev.jsampler.CC;
import com.grigoriliev.jsampler.view.JSChannelsPane;
import com.grigoriliev.jsampler.fantasia.view.basic.*;
import com.grigoriliev.jsampler.swing.view.SHF;


/**
 *
 * @author Grigor Iliev
 */
public class MainPane extends FantasiaPanel {
	private final ChannelsBar channelsBar;
	private final ButtonsPanel buttonsPanel;
	private final Vector<ChannelsPanel> channelsPanes = new Vector<ChannelsPanel>();
	
	final JScrollPane scrollPane = new JScrollPane();
	
	/** Creates a new instance of <code>MainPane</code> */
	public MainPane() {
		setOpaque(false);

		int count = CC.preferences().getIntProperty("channelLanes.count");
		for(int i = 0; i < count; i++) {
			String s = FantasiaI18n.i18n.getButtonLabel("MainPane.ButtonsPanel.tt", i + 1);
			channelsPanes.add(new ChannelsPanel(s));
		}
		
		buttonsPanel = new ButtonsPanel(count);
		if(count == 1) buttonsPanel.setVisible(false);
		channelsBar = new ChannelsBar(buttonsPanel);
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		
		setLayout(gridbag);
		
		JPanel p = new FantasiaPanel();
		p.setOpaque(false);
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.add(channelsBar);
		p.add(Box.createGlue());
		
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.insets = new Insets(0, 0, 0, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		gridbag.setConstraints(p, c);
		add(p);
		
		JScrollPane sp = scrollPane;
		sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		//sp.putClientProperty(SCROLL_PANE_BUTTONS_POLICY, ScrollPaneButtonPolicyKind.NONE);
		sp.setBorder(BorderFactory.createEmptyBorder());
		sp.setOpaque(false);
		javax.swing.JViewport wp = sp.getViewport();
		wp.setMinimumSize(new Dimension(400, wp.getMinimumSize().height));
		wp.setOpaque(false);
		sp.setMaximumSize(new Dimension(sp.getMaximumSize().width, Short.MAX_VALUE));
		sp.getVerticalScrollBar().setBorder(BorderFactory.createEmptyBorder(7, 4, 0, 1));
		//sp.getVerticalScrollBar().setUnitIncrement(12);
		sp.setPreferredSize(new Dimension(420, sp.getPreferredSize().height));
		
		sp.getVerticalScrollBar().addHierarchyListener(new HierarchyListener() {
			public void
			hierarchyChanged(HierarchyEvent e) {
				if((e.getChangeFlags() & e.SHOWING_CHANGED) != e.SHOWING_CHANGED) {
					return;
				}
				
				onScrollBarVisibilityChanged();
			}
		});
		
		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.insets = new Insets(0, 0, 0, 0);
		c.fill = GridBagConstraints.BOTH;
		gridbag.setConstraints(sp, c);
		add(sp);
		
		setMaximumSize(new Dimension(420, Short.MAX_VALUE));
	}

	public ChannelsPane
	addChannelsPane() {
		String s = FantasiaI18n.i18n.getButtonLabel("MainPane.ButtonsPanel.tt", channelsPanes.size() + 1);
		ChannelsPanel cp = new ChannelsPanel(s);
		channelsPanes.add(cp);
		return cp.getChannelsPane();
	}
	
	private void
	onScrollBarVisibilityChanged() {
		int w = 420;
		int h = scrollPane.getPreferredSize().height;
		int scrollbarWidth = scrollPane.getVerticalScrollBar().getPreferredSize().width;
		
		if(scrollPane.getVerticalScrollBar().isVisible()) w += scrollbarWidth;
		
		scrollPane.setMinimumSize(new Dimension(w, scrollPane.getPreferredSize().height));
		scrollPane.setPreferredSize(new Dimension(w, h));
		scrollPane.setMaximumSize(new Dimension(w, Short.MAX_VALUE));
		setMaximumSize(new Dimension(w, Short.MAX_VALUE));
		
		if(SHF.getMainFrame() != null && !SHF.getMainFrame().isResizable()) {
			// this means that there are no visible side panes
			
			w = SHF.getMainFrame().getPreferredSize().width;
			SHF.getMainFrame().setSize(w, SHF.getMainFrame().getHeight());
		}
		
		revalidate();
	}
	
	public void
	scrollToBottom() {
		int h = scrollPane.getViewport().getView().getHeight();
		scrollPane.getViewport().scrollRectToVisible(new Rectangle(0, h - 2, 1, 1));
	}
	
	public ChannelsPane
	getChannelsPane(int index) { return channelsPanes.get(index).getChannelsPane(); }
	
	public int
	getChannelsPaneCount() { return channelsPanes.size(); }

	public void
	removeChannelsPane(int index) { channelsPanes.remove(index); }
	
	public void
	setSelectedChannelsPane(JSChannelsPane pane) {
		ChannelsPanel chnPanel = null;
		
		for(int i = 0; i < getChannelsPaneCount(); i++) {
			if(channelsPanes.get(i).getChannelsPane() == pane) {
				chnPanel = channelsPanes.get(i);
				
				if(!buttonsPanel.buttons.get(i).isSelected()) {
					buttonsPanel.buttons.get(i).setSelected(true);
				}
				
				break;
			}
		}
		
		if(chnPanel == null) {
			CC.getLogger().warning("Unknown channels pane");
			return;
		}
		
		scrollPane.getViewport().setView(chnPanel);
	}
	
	public ChannelsPane
	getSelectedChannelsPane() {
		for(int i = 0; i < getChannelsPaneCount(); i++) {
			if(buttonsPanel.buttons.get(i).isSelected()) {
				return channelsPanes.get(i).getChannelsPane();
			}
		}
		
		return null;
	}

	public ButtonsPanel
	getButtonsPanel() { return buttonsPanel; }
	
	@Override
	protected void
	paintComponent(Graphics g) {
		super.paintComponent(g);
		int h = Res.gfxChannelsBg.getIconHeight();
		if(h < 1) return;
		int i = 0;
		while(i < getHeight()) {
			Res.gfxChannelsBg.paintIcon(this, g, 0, i);
			i += h;
		}
	}
	
	private final EventHandler eventHandler = new EventHandler();
	
	private EventHandler
	getHandler() { return eventHandler; }
	
	private class EventHandler extends MouseAdapter {
		@Override
		public void
		mousePressed(MouseEvent e) {
			// TAG: channel selection system
			CC.getMainFrame().getSelectedChannelsPane().setSelectedChannel(null);
			///////
		}
	}
	
	private class ChannelsPanel extends FantasiaPanel {
		private final ChannelsPane channelsPane;
		ChannelsPanel(String title) {
			ActionListener l = new ActionListener() {
				public void
				actionPerformed(ActionEvent e) { scrollToBottom(); }
			};
			
			channelsPane = new ChannelsPane(title, l);
			
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			channelsPane.setAlignmentX(LEFT_ALIGNMENT);
			add(channelsPane);
			JPanel p2 = new NewChannelPane();
			p2.setAlignmentX(LEFT_ALIGNMENT);
			add(p2);
			add(Box.createGlue());
			setOpaque(false);
			setBorder(BorderFactory.createEmptyBorder(7, 0, 0, 0));
			setMinimumSize(new Dimension(420, getMinimumSize().height));
			setAlignmentX(LEFT_ALIGNMENT);
			
			addMouseListener(getHandler());
		}
		
		public ChannelsPane
		getChannelsPane() { return channelsPane; }
	}
	
	public class ButtonsPanel extends FantasiaToggleButtonsPanel implements ActionListener {
		public
		ButtonsPanel(int count) {
			super(count, true);
			setButtonNumber(count);
		}
		
		@Override
		public void
		setButtonNumber(int number) {
			if(number > MainFrame.MAX_CHANNEL_LANE_NUMBER) return;
			int j = number == 7 ? 98 : 96;
			int k = j / number;

			for(int i = 0; i < buttons.size(); i++) {
				buttons.get(i).removeActionListener(this);
			}

			super.setButtonNumber(number);

			for(int i = 0; i < number; i++) {
				JToggleButton btn = buttons.get(i);
				btn.setText(String.valueOf(i + 1));
				btn.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
				btn.addActionListener(this);
				btn.setMinimumSize(new Dimension(k, btn.getPreferredSize().height));
				btn.setPreferredSize(btn.getMinimumSize());
				btn.setMaximumSize(btn.getMinimumSize());
				String s = "MainPane.ButtonsPanel.tt";
				btn.setToolTipText(FantasiaI18n.i18n.getButtonLabel(s, i + 1));
			}
			
			setMaximumSize(getPreferredSize());
		}
		
		@Override
		public void
		actionPerformed(ActionEvent e) {
			AbstractButton btn = (AbstractButton)e.getSource();
			if(!btn.isSelected()) return;
			int idx = buttons.indexOf(btn);
			if(idx == -1) return;
			
			ChannelsPanel chnPanel = channelsPanes.get(idx);
			CC.getMainFrame().setSelectedChannelsPane(chnPanel.getChannelsPane());
		}
	}
	
	class NewChannelPane extends PixmapPane implements ActionListener {
		private final PixmapButton btnNew = new PixmapButton(Res.gfxPowerOff);
		
		NewChannelPane() {
			super(Res.gfxCreateChannel);
			setPixmapInsets(new Insets(3, 3, 3, 3));
			
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			add(Box.createRigidArea(new Dimension(3, 0)));
			add(btnNew);
			add(Box.createRigidArea(new Dimension(4, 0)));
			
			add(createVSeparator());
			
			add(Box.createRigidArea(new Dimension(275, 0)));
			
			add(createVSeparator());
			
			add(Box.createRigidArea(new Dimension(29, 0)));
			
			add(createVSeparator());
			
			add(Box.createRigidArea(new Dimension(40, 0)));
			
			add(createVSeparator());
			
			setPreferredSize(new Dimension(420, 29));
			setMinimumSize(getPreferredSize());
			setMaximumSize(getPreferredSize());
			
			btnNew.setPressedIcon(Res.gfxPowerOn);
			btnNew.addActionListener(this);
			
			addMouseListener(new MouseAdapter() {
				public void
				mouseClicked(MouseEvent e) {
					if(e.getButton() != e.BUTTON1) {
						return;
					}
					
					CC.getSamplerModel().addBackendChannel();
				}
			});
			
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			
			String s = FantasiaI18n.i18n.getLabel("MainPane.NewChannelPane.newChannel");
			btnNew.setToolTipText(s);
			setToolTipText(s);
		}
		
		@Override
		public void
		actionPerformed(ActionEvent e) {
			CC.getSamplerModel().addBackendChannel();
		}
		
		private JPanel
		createVSeparator() {
			PixmapPane p = new PixmapPane(Res.gfxVLine);
			p.setOpaque(false);
			p.setPreferredSize(new Dimension(2, 29));
			p.setMinimumSize(p.getPreferredSize());
			p.setMaximumSize(p.getPreferredSize());
			return p;
		}
	}
}
