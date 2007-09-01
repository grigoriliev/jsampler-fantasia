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

package org.jsampler.view.fantasia;

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

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jsampler.CC;

import static org.jsampler.view.fantasia.FantasiaI18n.i18n;
import static org.jvnet.substance.SubstanceLookAndFeel.SCROLL_PANE_BUTTONS_POLICY;
import static org.jvnet.substance.utils.SubstanceConstants.ScrollPaneButtonPolicyKind;


/**
 *
 * @author Grigor Iliev
 */
public class MainPane extends JPanel {
	private final ChannelsBar channelsBar = new ChannelsBar();
	private final ChannelsPane channelsPane;
	
	final JScrollPane scrollPane;
	
	/** Creates a new instance of <code>MainPane</code> */
	public MainPane() {
		channelsPane = new ChannelsPane("", new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { scrollToBottom(); }
		});
		
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		
		setLayout(gridbag);
		
		JPanel p = new JPanel();
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
		
		p = createChannelsPane();
		p.setAlignmentX(LEFT_ALIGNMENT);
		
		scrollPane = new JScrollPane(p);
		JScrollPane sp = scrollPane;
		sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		//sp.putClientProperty(SCROLL_PANE_BUTTONS_POLICY, ScrollPaneButtonPolicyKind.NONE);
		sp.setBorder(BorderFactory.createEmptyBorder());
		sp.setOpaque(false);
		javax.swing.JViewport wp = sp.getViewport();
		wp.setMinimumSize(new Dimension(420, wp.getMinimumSize().height));
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
		
		//setMaximumSize(new Dimension(420, Short.MAX_VALUE));
	}
	
	private JPanel
	createChannelsPane() {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		channelsPane.setAlignmentX(LEFT_ALIGNMENT);
		p.add(channelsPane);
		JPanel p2 = new NewChannelPane();
		p2.setAlignmentX(LEFT_ALIGNMENT);
		p.add(p2);
		p.add(Box.createGlue());
		p.setOpaque(false);
		p.setBorder(BorderFactory.createEmptyBorder(7, 0, 0, 0));
		p.setMinimumSize(new Dimension(420, p.getMinimumSize().height));
		return p;
	}
	
	private void
	onScrollBarVisibilityChanged() {
		int h = scrollPane.getPreferredSize().height;
		int scrollbarWidth = scrollPane.getVerticalScrollBar().getPreferredSize().width;
		
		if(scrollPane.getVerticalScrollBar().isVisible()) {
			scrollPane.setPreferredSize(new Dimension(420 + scrollbarWidth, h));
		} else {
			scrollPane.setPreferredSize(new Dimension(420, h));
		}
		
		revalidate();
	}
	
	public void
	scrollToBottom() {
		int h = scrollPane.getViewport().getView().getHeight();
		scrollPane.getViewport().scrollRectToVisible(new Rectangle(0, h - 2, 1, 1));
	}
	
	public ChannelsPane
	getChannelsPane() { return channelsPane; }
	
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
	
	class NewChannelPane extends PixmapPane implements ActionListener {
		private final PixmapButton btnNew = new PixmapButton(Res.gfxPowerOff);
		
		NewChannelPane() {
			super(Res.gfxCreateChannel);
			setPixmapInsets(new Insets(3, 3, 3, 3));
			
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			add(Box.createRigidArea(new Dimension(4, 0)));
			add(btnNew);
			add(Box.createRigidArea(new Dimension(3, 0)));
			
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
			
			String s = i18n.getLabel("MainPane.NewChannelPane.newChannel");
			btnNew.setToolTipText(s);
			setToolTipText(s);
		}
		
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
