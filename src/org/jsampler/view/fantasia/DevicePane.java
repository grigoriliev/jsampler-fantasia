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
import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXCollapsiblePane;

import static org.jsampler.view.fantasia.FantasiaI18n.i18n;
import static org.jsampler.view.fantasia.FantasiaPrefs.*;

/**
 *
 * @author Grigor Iliev
 */
public class DevicePane extends JPanel {
	private final PowerButton btnDestroy;
	private final OptionsButton btnOptions = new OptionsButton();
	private final FantasiaLabel lDevName;
	
	private final JXCollapsiblePane mainPane = new JXCollapsiblePane();
	private final JXCollapsiblePane optionsPane = new JXCollapsiblePane();
	
	/** Creates a new instance of <code>DevicePane</code> */
	public
	DevicePane() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		mainPane.getContentPane().setLayout (
			new BoxLayout(mainPane.getContentPane(), BoxLayout.Y_AXIS)
		);
		
		PixmapPane p = new PixmapPane(Res.gfxDeviceBg);
		p.setPixmapInsets(new Insets(1, 1, 1, 1));
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.add(Box.createRigidArea(new Dimension(3, 0)));
		btnDestroy = new PowerButton();
		p.add(btnDestroy);
		p.add(Box.createRigidArea(new Dimension(3, 0)));
		
		p.add(createVSeparator());
		
		p.add(Box.createRigidArea(new Dimension(6, 0)));
		
		lDevName = new FantasiaLabel("", true);
		lDevName.setOpaque(false);
		lDevName.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		Dimension d = new Dimension(Short.MAX_VALUE, lDevName.getPreferredSize().height);
		lDevName.setMaximumSize(d);
		lDevName.setMinimumSize(new Dimension(70, lDevName.getPreferredSize().height));
		p.add(lDevName);
		
		p.add(Box.createRigidArea(new Dimension(5, 0)));
		
		p.add(btnOptions);
		
		p.add(Box.createRigidArea(new Dimension(5, 0)));
		
		d = new Dimension(77, 24);
		p.setPreferredSize(d);
		p.setMinimumSize(d);
		p.setMaximumSize(new Dimension(Short.MAX_VALUE, 24));
		p.setAlignmentX(LEFT_ALIGNMENT);
		mainPane.add(p);
		
		optionsPane.setAlignmentX(LEFT_ALIGNMENT);
		optionsPane.setAnimated(false);
		optionsPane.setCollapsed(true);
		optionsPane.setAnimated(preferences().getBoolProperty(ANIMATED));
		
		preferences().addPropertyChangeListener(ANIMATED, new PropertyChangeListener() {
			public void
			propertyChange(PropertyChangeEvent e) {
				optionsPane.setAnimated(preferences().getBoolProperty(ANIMATED));
			}
		});
		
		mainPane.add(optionsPane);
		
		add(mainPane);
		
		mainPane.setAnimated(false);
		mainPane.setCollapsed(true);
		mainPane.setAnimated(preferences().getBoolProperty(ANIMATED));
		mainPane.setCollapsed(false);
		
		preferences().addPropertyChangeListener(ANIMATED, new PropertyChangeListener() {
			public void
			propertyChange(PropertyChangeEvent e) {
				mainPane.setAnimated(preferences().getBoolProperty(ANIMATED));
			}
		});
	}
	
	protected void
	setDeviceName(String s) {
		lDevName.setText(s);
		Dimension d = new Dimension(Short.MAX_VALUE, lDevName.getPreferredSize().height);
		lDevName.setMaximumSize(d);
	}
	
	protected void
	destroyDevice() { }
	
	protected void
	restoreDevice() {
		btnDestroy.setSelected(true);
		mainPane.setCollapsed(false);
		
	}
	
	protected void
	setOptionsPane(javax.swing.JComponent c) {
		optionsPane.setContentPane(c);
	}
	
	protected JPanel
	createVSeparator() {
		PixmapPane p = new PixmapPane(Res.gfxVLine);
		p.setOpaque(false);
		p.setPreferredSize(new Dimension(2, 24));
		p.setMinimumSize(p.getPreferredSize());
		p.setMaximumSize(p.getPreferredSize());
		return p;
	}
	
	protected JPanel
	createHSeparator() {
		PixmapPane p = new PixmapPane(Res.gfxHLine);
		p.setOpaque(false);
		p.setPreferredSize(new Dimension(77, 2));
		p.setMinimumSize(p.getPreferredSize());
		p.setMaximumSize(new Dimension(Short.MAX_VALUE, 2));
		return p;
	}	
	
	private class OptionsButton extends PixmapToggleButton implements ActionListener {
		OptionsButton() {
			super(Res.gfxOptionsOff, Res.gfxOptionsOn);
			setRolloverIcon(Res.gfxOptionsOffRO);
			this.setRolloverSelectedIcon(Res.gfxOptionsOnRO);
			addActionListener(this);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			showOptionsPane(isSelected());
			
			String s;
			if(isSelected()) s = i18n.getButtonLabel("OptionsButton.ttHideOptions");
			else s = i18n.getButtonLabel("OptionsButton.ttShowOptions");
			
			setToolTipText(s);
		}
		
		private void
		showOptionsPane(boolean show) {
			optionsPane.setCollapsed(!show);
		}
		
		public boolean
		contains(int x, int y) { return super.contains(x, y) & y < 13; }
	}
	
	private class PowerButton extends PixmapToggleButton
			implements ActionListener, PropertyChangeListener {
		
		PowerButton() {
			super(Res.gfxPowerOff18, Res.gfxPowerOn18);
		
			setSelected(true);
			addActionListener(this);
			String s = JXCollapsiblePane.ANIMATION_STATE_KEY;
			mainPane.addPropertyChangeListener(s, this);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			if(!mainPane.isAnimated()) {
				destroyDevice();
				return;
			}
			
			mainPane.setCollapsed(true);
		}
		
		public void
		propertyChange(PropertyChangeEvent e) {
			if(e.getNewValue() == "collapsed") {
				destroyDevice();
			}
		}
	}
	
}
