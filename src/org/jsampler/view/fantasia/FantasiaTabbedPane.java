/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2008 Grigor Iliev <grigor@grigoriliev.com>
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

import java.awt.Component;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Paint;
import java.awt.RenderingHints;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Grigor Iliev
 */
public class FantasiaTabbedPane extends JPanel implements ItemListener {
	private final FantasiaTabPanel mainPane = new FantasiaTabPanel();
	private final Vector<FantasiaTabButton> buttons = new Vector<FantasiaTabButton>();
	private final Vector<Component> panes = new Vector<Component>();
	
	private final ButtonGroup buttonGroup = new ButtonGroup();
	
	private final Vector<ChangeListener> listeners = new Vector<ChangeListener>();
	
	public
	FantasiaTabbedPane() {
		setOpaque(false);
		
		mainPane.setLayout(new java.awt.BorderLayout());
		
		GridBagLayout gridbag = new GridBagLayout();
		setLayout(gridbag);
	}
	
	public void
	addChangeListener(ChangeListener l) { listeners.add(l); }
	
	public void
	removeChangeListener(ChangeListener l) { listeners.remove(l); }
	
	private void
	fireChangeEvent() {
		ChangeEvent e = new ChangeEvent(this);
		for(ChangeListener l : listeners) l.stateChanged(e);
	}
	
	public JPanel
	getMainPane() { return mainPane; }
	
	public void
	addTab(String title, Component component) {
		FantasiaTabButton btn = new FantasiaTabButton(this, title);
		int idx = buttons.size();
		
		GridBagConstraints c = new GridBagConstraints();
		GridBagLayout gridbag = (GridBagLayout)getLayout();
		
		c.gridx = idx;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		gridbag.setConstraints(btn, c);
		add(btn);
		
		remove(mainPane);
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = idx + 1;
		gridbag.setConstraints(mainPane, c);
		add(mainPane);
		
		btn.setIndex(idx);
		buttonGroup.add(btn);
		buttons.add(btn);
		btn.addItemListener(this);
		panes.add(component);
		
		if(idx == 0) btn.doClick(0);
	}
	
	public int
	getTabCount() { return buttons.size(); }
	
	public FantasiaTabButton
	getTabButton(int index) { return buttons.get(index); }
	
	public int
	getSelectedIndex() {
		for(int i = 0; i < buttons.size(); i++) {
			if(buttons.get(i).isSelected()) return i;
		}
		
		return -1;
	}
	
	public void
	setSelectedIndex(int index) {
		if(buttons.get(index).isSelected()) return;
		buttons.get(index).doClick(0);
	}
	
	@Override
	public void
	itemStateChanged(ItemEvent e) {
		int idx = buttons.indexOf(e.getItem());
		if(idx == -1) return;
		
		if (e.getStateChange() == ItemEvent.SELECTED) {
			mainPane.add(panes.get(idx));
			fireChangeEvent();
		} else {
			mainPane.remove(panes.get(idx));
		}
		
		mainPane.revalidate();
		mainPane.repaint();
	}
	
	@Override
	public void
	removeAll() {
		for(FantasiaTabButton btn : buttons) {
			buttonGroup.remove(btn);
			btn.removeItemListener(this);
		}
		
		buttons.removeAllElements();
		panes.removeAllElements();
		
		super.removeAll();
	}
}

class FantasiaTabPanel extends JPanel {
	public
	FantasiaTabPanel() { }
	
	@Override
	protected void
	paintComponent(Graphics g) {
		double h = getSize().getHeight();
		double w = getSize().getWidth();
		
		paintComponent((Graphics2D)g, 0, 0, w, h);
	}
	
	protected void
	paintComponent(Graphics2D g2, double x1, double y1, double width, double height) {
		Paint oldPaint = g2.getPaint();
		Composite oldComposite = g2.getComposite();
		
		g2.setRenderingHint (
			RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF
		);
		
		FantasiaPainter.paintGradient(g2, x1, y1, x1 + width - 1, y1 + height - 1);
		
		FantasiaPainter.Border b = new FantasiaPainter.Border(false, true, true, true);
		FantasiaPainter.paintBoldOuterBorder(g2, x1, y1, x1 + width - 1, y1 + height - 1, b);
		
		g2.setPaint(oldPaint);
		g2.setComposite(oldComposite);
	}
}

