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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToolTip;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jsampler.CC;
import org.jsampler.HF;

import org.jsampler.event.SamplerAdapter;
import org.jsampler.event.SamplerEvent;

import org.jsampler.view.std.JSVolumeEditorPopup;

import static org.jsampler.view.fantasia.FantasiaI18n.i18n;
import static org.jsampler.view.fantasia.FantasiaPrefs.preferences;
import static org.jsampler.view.fantasia.FantasiaUtils.*;

import static org.jsampler.view.std.JSVolumeEditorPopup.VolumeType;
import static org.jsampler.view.std.StdPrefs.*;

/**
 *
 * @author Grigor Iliev
 */
public class ChannelsBar extends PixmapPane {
	private final JSlider slVolume = new JSlider();
	JButton btnVolume = FantasiaUtils.createScreenButton("3 dB");
	
	private final JLabel lStreams = createScreenLabel(" --");
	private final JLabel lVoices = createScreenLabel("-- ");
	private JSVolumeEditorPopup popupVolume;
	
	private static NumberFormat numberFormat = NumberFormat.getInstance();
	
	/** Creates a new instance of <code>ChannelsBar</code> */
	public
	ChannelsBar() {
		super(Res.gfxCreateChannel);
		
		numberFormat.setMaximumFractionDigits(1);
		popupVolume = new JSVolumeEditorPopup(btnVolume, VolumeType.MASTER);
		
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
		add(Box.createRigidArea(new Dimension(5, 0)));
		
		PixmapPane p = new PixmapPane(Res.gfxTextField);
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.setPixmapInsets(new Insets(5, 5, 4, 5));
		p.setBorder(BorderFactory.createEmptyBorder(1, 8, 1, 5));
		
		lStreams.setFont(Res.fontScreenMono);
		lStreams.setHorizontalAlignment(JLabel.RIGHT);
		lStreams.setPreferredSize(lStreams.getPreferredSize());
		lStreams.setMaximumSize(lStreams.getPreferredSize());
		lStreams.setToolTipText(i18n.getLabel("ChannelsBar.streamVoiceCount"));
		p.add(lStreams);
		
		l = createScreenLabel("/");
		l.setFont(Res.fontScreenMono);
		l.setToolTipText(i18n.getLabel("ChannelsBar.streamVoiceCount"));
		p.add(l);
		
		lVoices.setFont(Res.fontScreenMono);
		lVoices.setPreferredSize(lVoices.getPreferredSize());
		lVoices.setMaximumSize(lVoices.getPreferredSize());
		lVoices.setToolTipText(i18n.getLabel("ChannelsBar.streamVoiceCount"));
		p.add(lVoices);
		
		add(Box.createRigidArea(new Dimension(5, 0)));
		
		btnVolume.setIcon(Res.iconVolume14);
		btnVolume.setIconTextGap(2);
		btnVolume.setHorizontalAlignment(btnVolume.LEFT);
		d = btnVolume.getPreferredSize();
		d.width = 55;
		btnVolume.setPreferredSize(d);
		btnVolume.setMaximumSize(d);
		p.add(btnVolume);
		
		p.setMaximumSize(p.getPreferredSize());
		
		add(p);
		add(Box.createGlue());
		
		d = new Dimension(420, 29);
		setMinimumSize(d);
		setPreferredSize(d);
		setMaximumSize(d);
		
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
		
		btnVolume.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				if(popupVolume.isVisible()) {
					popupVolume.commit();
					popupVolume.hide();
				} else {
					float vol = CC.getSamplerModel().getVolume();
					popupVolume.setCurrentVolume(vol);
					popupVolume.show();
				}
			}
		});
		
		popupVolume.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				CC.getSamplerModel().setBackendVolume(popupVolume.getVolumeFactor());
			}
		});
		
		s = VOL_MEASUREMENT_UNIT_DECIBEL;
		preferences().addPropertyChangeListener(s, new PropertyChangeListener() {
			public void
			propertyChange(PropertyChangeEvent e) {
				setVolume();
			}
		});
	}
	
	private void
	setVolume() {
		int volume = slVolume.getValue();
		
		if(CC.getViewConfig().isMeasurementUnitDecibel()) {
			double dB = HF.percentsToDecibels(volume);
			btnVolume.setText(numberFormat.format(dB) + "dB");
		} else {
			btnVolume.setText(volume + "%");
		}
		
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
