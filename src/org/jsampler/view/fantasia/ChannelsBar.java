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

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JToolTip;
import javax.swing.Popup;
import javax.swing.PopupFactory;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jsampler.CC;

import org.jsampler.event.SamplerAdapter;
import org.jsampler.event.SamplerEvent;

import static org.jsampler.view.fantasia.FantasiaI18n.i18n;
import static org.jsampler.view.fantasia.FantasiaPrefs.preferences;
import static org.jsampler.view.std.StdPrefs.*;

/**
 *
 * @author Grigor Iliev
 */
public class ChannelsBar extends PixmapPane {
	private final JSlider slVolume = new JSlider();
	private Popup popup = null;
	private final JToolTip tip = new JToolTip();
	
	/** Creates a new instance of <code>ChannelsBar</code> */
	public
	ChannelsBar() {
		super(Res.gfxCreateChannel);
		setPixmapInsets(new Insets(1, 1, 1, 1));
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		add(Box.createRigidArea(new Dimension(5, 0)));
		JLabel l = new JLabel(Res.iconVolume22);
		add(l);
		
		slVolume.setOpaque(false);
		slVolume.setFocusable(false);
		Dimension d = new Dimension(150, 22);
		slVolume.setPreferredSize(d);
		slVolume.setMaximumSize(d);
		
		add(slVolume);
		
		add(Box.createGlue());
		
		d = new Dimension(420, 29);
		setMinimumSize(d);
		setPreferredSize(d);
		setMaximumSize(d);
		
		// Setting the tooltip size
		tip.setTipText(i18n.getLabel("ChannelsBar.volume", 1000));
		tip.setMinimumSize(tip.getPreferredSize());
		tip.setPreferredSize(tip.getPreferredSize()); // workaround for preserving that size
		tip.setComponent(slVolume);
		///////
		
		int i = preferences().getIntProperty(MAXIMUM_MASTER_VOLUME);
		slVolume.setMaximum(i);
		String s = MAXIMUM_MASTER_VOLUME;
		preferences().addPropertyChangeListener(s, new PropertyChangeListener() {
			public void
			propertyChange(PropertyChangeEvent e) {
				int j = preferences().getIntProperty(MAXIMUM_MASTER_VOLUME);
				slVolume.setMaximum(j);
			}
		});
		
		slVolume.addChangeListener(new ChangeListener() {
			public void
			stateChanged(ChangeEvent e) { setVolume(); }
		});
		
		CC.getSamplerModel().addSamplerListener(new SamplerAdapter() {
			public void
			volumeChanged(SamplerEvent e) { updateVolume(); }
		});
		
		updateVolume();
		
		slVolume.addMouseListener(new MouseAdapter() {
			public void
			mousePressed(MouseEvent e) {
				if(popup != null) {
					popup.hide();
					popup = null;
				}
				
				Point p = slVolume.getLocationOnScreen();
				PopupFactory pf = PopupFactory.getSharedInstance();
				popup = pf.getPopup(slVolume, tip, p.x, p.y - 22);
				popup.show();
			}
			
			public void
			mouseReleased(MouseEvent e) {
				if(popup != null) {
					popup.hide();
					popup = null;
				}
			}
		});
	}
	
	private void
	setVolume() {
		int volume = slVolume.getValue();
		String s = i18n.getLabel("ChannelsBar.volume", volume);
		slVolume.setToolTipText(s);
		tip.setTipText(s);
		tip.repaint();
		
		if(slVolume.getValueIsAdjusting()) return;
		
		int vol = (int)(CC.getSamplerModel().getVolume() * 100);
		
		if(vol == slVolume.getValue()) return;
		
		/*
		 * If the model's volume is not equal to the slider
		 * value we assume that the change is due to user input.
		 * So we must update the volume at the backend too.
		 */
		float v = slVolume.getValue();
		v /= 100;
		CC.getSamplerModel().setBackendVolume(v);
	}
	
	private void
	updateVolume() {
		slVolume.setValue((int)(CC.getSamplerModel().getVolume() * 100));
	}
	
}
