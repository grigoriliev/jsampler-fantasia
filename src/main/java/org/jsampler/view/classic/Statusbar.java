/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2006 Grigor Iliev <grigor@grigoriliev.com>
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.jsampler.CC;

import org.jsampler.event.SamplerAdapter;
import org.jsampler.event.SamplerChannelListEvent;
import org.jsampler.event.SamplerChannelListListener;
import org.jsampler.event.SamplerEvent;

import static org.jsampler.view.classic.ClassicI18n.i18n;


/**
 *
 * @author Grigor Iliev
 */
public class Statusbar extends JPanel {
	JLabel l1;
	JLabel l2;
	
	private final JProgressBar pbTotalVoices = new JProgressBar();
	
	/** Creates a new instance of StatusBar */
	public Statusbar() {
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		setLayout(gridbag);
		
		l1 = new JLabel();
		l1.setFont(l1.getFont().deriveFont(Font.PLAIN));
		l1.setBorder(BorderFactory.createLoweredBevelBorder());
		
		l2 = new JLabel(" ");
		l2.setBorder(BorderFactory.createLoweredBevelBorder());
		l2.setPreferredSize(l1.getPreferredSize());
		
		setTotalChannelCount(CC.getSamplerModel().getChannelCount());
		
		JPanel progressPane = new JPanel();
		progressPane.setLayout(new BorderLayout());
		progressPane.add(pbTotalVoices, BorderLayout.CENTER);
		progressPane.setBorder(BorderFactory.createLoweredBevelBorder());
		
		pbTotalVoices.setBorderPainted(false);
		pbTotalVoices.setStringPainted(true);
		float fsize = pbTotalVoices.getFont().getSize2D();
		fsize = fsize > 10 ? fsize - 3 : 8;
		pbTotalVoices.setFont(pbTotalVoices.getFont().deriveFont(fsize));
		
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(2, 0, 0, 2);
		//c.anchor = GridBagConstraints.WEST;
		c.weightx = 1;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		gridbag.setConstraints(l1, c);
		add(l1);
		
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 0.3;
		gridbag.setConstraints(progressPane, c);
		add(progressPane);
		
		c.gridx = 2;
		c.gridy = 0;
		c.weightx = 1;
		c.insets = new Insets(2, 0, 0, 0);
		gridbag.setConstraints(l2, c);
		add(l2);
		
		getHandler().totalVoiceCountChanged(null);
		
		CC.getSamplerModel().addSamplerListener(getHandler());
		CC.getSamplerModel().addSamplerChannelListListener(getHandler());
	}
	
	private void
	setTotalChannelCount(int count) {
		if(count == 1) l1.setText(" " + i18n.getLabel("Statusbar.totalChannel", count));
		else l1.setText(" " + i18n.getLabel("Statusbar.totalChannels", count));
		l2.setPreferredSize(l1.getPreferredSize());
	}
	
	private final Handler handler = new Handler();
	
	private Handler
	getHandler() { return handler; }
	
	private class Handler extends SamplerAdapter implements SamplerChannelListListener {
		/** Invoked when the total number of active voices is changed. */
		public void
		totalVoiceCountChanged(SamplerEvent e) {
			int voices = CC.getSamplerModel().getTotalVoiceCount();
			int voicesMax = CC.getSamplerModel().getTotalVoiceCountMax();
			
			// workaround for bug #223 in substance
			if(voicesMax == 0) voicesMax = 1;
			pbTotalVoices.setMaximum(voicesMax);
			pbTotalVoices.setValue(voices);
			pbTotalVoices.setString(i18n.getLabel("Statusbar.pbTotalVoices", voices));
		}
		
		/**
		 * Invoked when a new sampler channel is created.
		 * @param e A <code>SamplerChannelListEvent</code>
		 * instance providing the event information.
		 */
		public void
		channelAdded(SamplerChannelListEvent e) {
			setTotalChannelCount(CC.getSamplerModel().getChannelCount());
		}
		
		/**
		 * Invoked when a sampler channel is removed.
		 * @param e A <code>SamplerChannelListEvent</code>
		 * instance providing the event information.
		 */
		public void
		channelRemoved(SamplerChannelListEvent e) {
			setTotalChannelCount(CC.getSamplerModel().getChannelCount());
		}
	}
}
