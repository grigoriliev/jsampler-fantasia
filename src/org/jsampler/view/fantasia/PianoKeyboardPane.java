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

import java.awt.BorderLayout;
import java.awt.Insets;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sf.juife.event.TaskEvent;
import net.sf.juife.event.TaskListener;

import org.jsampler.CC;
import org.jsampler.SamplerChannelModel;

import org.jsampler.event.SamplerChannelAdapter;
import org.jsampler.event.SamplerChannelEvent;
import org.jsampler.event.SamplerChannelListListener;
import org.jsampler.event.SamplerChannelListEvent;

import org.jsampler.view.JSChannel;
import org.jsampler.view.std.JSPianoRoll;

import org.linuxsampler.lscp.Instrument;

import org.linuxsampler.lscp.event.MidiDataEvent;
import org.linuxsampler.lscp.event.MidiDataListener;

import static org.jsampler.task.Global.GetFileInstrument;

/**
 *
 * @author Grigor Iliev
 */
public class PianoKeyboardPane extends PixmapPane
			       implements ListSelectionListener, SamplerChannelListListener {
	
	private final JSPianoRoll pianoRoll = new JSPianoRoll();
	private SamplerChannelModel channel = null;
	
	private String file = null;
	private int index = -1;
	
	public
	PianoKeyboardPane() {
		super(Res.gfxDeviceBg);
		setPixmapInsets(new java.awt.Insets(1, 1, 1, 1));
		setOpaque(false);
		
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		
		PixmapPane p2 = new PixmapPane(Res.gfxBorder);
		p2.setPixmapInsets(new Insets(1, 1, 1, 1));
		p2.setLayout(new BorderLayout());
		p2.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 1));
		p2.add(pianoRoll);
		add(p2);
		
		pianoRoll.setOpaque(false);
		disablePianoRoll();
		add(p2);
		
		CC.getSamplerModel().addSamplerChannelListListener(this);
		pianoRoll.addMidiDataListener(getHandler());
		
		updateKeyRange();
		
		PropertyChangeListener l = new PropertyChangeListener() {
			public void
			propertyChange(PropertyChangeEvent e) {
				updateKeyRange();
			}
		};
		
		CC.preferences().addPropertyChangeListener("midiKeyboard.firstKey", l);
		CC.preferences().addPropertyChangeListener("midiKeyboard.lastKey", l);
	}
	
	public JSPianoRoll
	getPianoRoll() { return pianoRoll; }
	
	private void
	updateKeyRange() {
		int firstKey = CC.preferences().getIntProperty("midiKeyboard.firstKey");
		int lastKey = CC.preferences().getIntProperty("midiKeyboard.lastKey");
		pianoRoll.setKeyRange(firstKey, lastKey);
		pianoRoll.setAllKeysDisabled(true);
		updateInstrumentInfo();
	}
	
	@Override public void
	valueChanged(ListSelectionEvent e) {
		if(e.getValueIsAdjusting()) return;
		
		JSChannel[] chnS = CC.getMainFrame().getChannelsPane(0).getSelectedChannels();
		if(chnS == null || chnS.length == 0) {
			disconnectChannel();
			return;
		}
		
		if(chnS[0].getModel() == channel) return;
		disconnectChannel();
		connectChannel(chnS[0].getModel());
	}
	
	private void
	updateInstrumentInfo() {
		final GetFileInstrument i = new GetFileInstrument(file, index);
		
		i.addTaskListener(new TaskListener() {
			public void
			taskPerformed(TaskEvent e) {
				if(i.doneWithErrors()) return;
				updatePianoKeyboard(i.getResult());
			}
		});
		
		CC.getTaskQueue().add(i);
	}
	
	private void
	disablePianoRoll() {
		pianoRoll.reset(true);
		pianoRoll.setPlayingEnabled(false);
	}
	
	private void
	updatePianoKeyboard(Instrument instr) {
		// The selected channel may have changed before this update
		// due to concurency, so also checking whether file is null
		if(instr == null || file == null) {
			disablePianoRoll();
			return;
		}
		
		pianoRoll.setPlayingEnabled(true);
		
		pianoRoll.setShouldRepaint(false);
		pianoRoll.reset(true);
		pianoRoll.setDisabled(instr.getKeyMapping(), false);
		pianoRoll.setKeyswitches(instr.getKeyswitchMapping(), true);
		pianoRoll.setShouldRepaint(true);
		pianoRoll.repaint();
	}
	
	private void
	connectChannel(SamplerChannelModel chn) {
		channel = chn;
		channel.addMidiDataListener(pianoRoll);
		channel.addSamplerChannelListener(getHandler());
		
		file = channel.getChannelInfo().getInstrumentFile();
		if(file == null) {
			disablePianoRoll();
			return;
		}
		index = channel.getChannelInfo().getInstrumentIndex();
		updateInstrumentInfo();
	}
	
	private void
	disconnectChannel() {
		if(channel != null) {
			channel.removeMidiDataListener(pianoRoll);
			channel.removeSamplerChannelListener(getHandler());
			channel = null;
			file = null;
			index = -1;
			disablePianoRoll();
		}
	}
	
	@Override public void
	channelAdded(SamplerChannelListEvent e) { }
	
	@Override public void
	channelRemoved(SamplerChannelListEvent e) {
		if(e.getChannelModel() == channel) {
			disconnectChannel();
		}
	}
	
	private final Handler handler = new Handler();
	
	private Handler
	getHandler() { return handler; }
	
	private class Handler extends SamplerChannelAdapter implements MidiDataListener {
		public void
		midiDataArrived(MidiDataEvent e) {
			if(channel == null) return;
			channel.sendBackendMidiData(e);
		}
		
		public void
		channelChanged(SamplerChannelEvent e) {
			String newFile = channel.getChannelInfo().getInstrumentFile();
			int newIndex = channel.getChannelInfo().getInstrumentIndex();
			
			if(channel.getChannelInfo().getInstrumentStatus() != 100) {
				//don't use disablePianoRoll because of unnecessary repainting
				pianoRoll.setAllKeysPressed(false);
				pianoRoll.removeAllKeyswitches();
				pianoRoll.setAllKeysDisabled(true);
				pianoRoll.setPlayingEnabled(false);
				return;
			}
			
			if(newFile == null) {
				if(file != null) disablePianoRoll();
				file = null;
				index = -1;
				return;
			}
			
			if(newFile.equals(file) && newIndex == index) return;
			
			file = newFile;
			index = newIndex;
			updateInstrumentInfo();
		}
	}
}
