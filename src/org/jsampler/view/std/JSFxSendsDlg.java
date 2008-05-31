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

package org.jsampler.view.std;

import org.jsampler.CC;
import org.jsampler.SamplerChannelModel;

import org.jsampler.event.SamplerChannelListEvent;
import org.jsampler.event.SamplerChannelListListener;

import net.sf.juife.InformationDialog;

import static org.jsampler.view.std.StdI18n.i18n;


/**
 *
 * @author Grigor Iliev
 */
public class JSFxSendsDlg extends InformationDialog {
	private JSFxSendsPane mainPane;
	private final SamplerChannelListListener channelListListener;
	
	private int channelIndex;
	
	/** Creates a new instance of <code>JSFxSendsDlg</code> */
	public
	JSFxSendsDlg(SamplerChannelModel model) {
		this(new JSFxSendsPane(model));
	}
	
	/** Creates a new instance of <code>JSFxSendsDlg</code> */
	public
	JSFxSendsDlg(JSFxSendsPane pane) {
		super(CC.getMainFrame(), pane);
		
		mainPane = pane;
		
		channelIndex = CC.getSamplerModel().getChannelIndex(pane.getChannelModel()) + 1;
		setTitle(i18n.getLabel("JSFxSendsDlg.title", channelIndex));
		setModal(false);
		showCloseButton(false);
		
		channelListListener = new SamplerChannelListListener() {
			public void
			channelAdded(SamplerChannelListEvent e) {
				if(CC.getSamplerModel().getChannelListIsAdjusting()) return;
				updateChannelIndex();
			}
			
			public void
			channelRemoved(SamplerChannelListEvent e) {
				//if(CC.getSamplerModel().getChannelListIsAdjusting()) return; //TODO: 
				
				updateChannelIndex();
			}
		};
		
		CC.getSamplerModel().addSamplerChannelListListener(channelListListener);
	}
	
	protected void
	updateChannelIndex() {
		int i = CC.getSamplerModel().getChannelIndex(mainPane.getChannelModel());
		if(channelIndex == i) return;
		
		channelIndex = i;
		setTitle(i18n.getLabel("JSFxSendsDlg.title", channelIndex + 1));
	}
}
