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
import java.awt.Dimension;
import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.jsampler.AudioDeviceModel;
import org.jsampler.CC;
import org.jsampler.MidiDeviceModel;
import org.jsampler.MidiInstrumentMap;
import org.jsampler.SamplerModel;

import org.jsampler.event.ListEvent;
import org.jsampler.event.ListListener;
import org.jsampler.event.MidiDeviceEvent;
import org.jsampler.event.MidiDeviceListEvent;
import org.jsampler.event.MidiDeviceListListener;
import org.jsampler.event.MidiDeviceListener;
import org.jsampler.event.SamplerAdapter;
import org.jsampler.event.SamplerChannelAdapter;
import org.jsampler.event.SamplerChannelEvent;
import org.jsampler.event.SamplerEvent;
import org.jsampler.event.SamplerListener;

import org.jsampler.view.std.JSChannelOutputRoutingDlg;

import org.linuxsampler.lscp.AudioOutputDevice;
import org.linuxsampler.lscp.MidiInputDevice;
import org.linuxsampler.lscp.MidiPort;
import org.linuxsampler.lscp.SamplerChannel;

import static org.jsampler.view.fantasia.FantasiaI18n.i18n;

/**
 *
 * @author Grigor Iliev
 */
public class NormalChannelOptionsView extends JPanel implements ChannelOptionsView {
	private final Channel channel;
	private MidiDeviceModel midiDevice = null;
	
	private final JComboBox cbMidiDevice = new FantasiaComboBox();
	private final JComboBox cbMidiPort = new FantasiaComboBox();
	private final JComboBox cbMidiChannel = new FantasiaComboBox();
	private final JComboBox cbInstrumentMap = new FantasiaComboBox();
	private final JComboBox cbAudioDevice = new FantasiaComboBox();
	
	private final PixmapButton btnChannelRouting;
	
	private boolean update = false;
	
	private final SamplerListener samplerListener;
	private final MapListListener mapListListener = new MapListListener();
	
	private class NoMap {
		public String
		toString() { return "[None]"; }
	}
	
	private NoMap noMap = new NoMap();
	
	private class DefaultMap {
		public String
		toString() { return "[Default]"; }
	}
	
	private DefaultMap defaultMap = new DefaultMap();
	
	/** Creates a new instance of <code>NormalChannelOptionsView</code> */
	public
	NormalChannelOptionsView(final Channel channel) {
		setLayout(new BorderLayout());
		PixmapPane bgp = new PixmapPane(Res.gfxChannelOptions);
		bgp.setPixmapInsets(new Insets(1, 1, 1, 1));
		
		this.channel = channel;
		
		bgp.setBorder(BorderFactory.createEmptyBorder(5, 4, 5, 4));
		bgp.setLayout(new BoxLayout(bgp, BoxLayout.X_AXIS));
		
		bgp.setPreferredSize(new Dimension(420, 44));
		bgp.setMinimumSize(getPreferredSize());
		bgp.setMaximumSize(getPreferredSize());
		
		JPanel p = new JPanel();
		p.setBorder(BorderFactory.createEmptyBorder(3, 4, 3, 4));
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		JLabel l = new JLabel(Res.gfxMidiInputTitle);
		l.setAlignmentX(LEFT_ALIGNMENT);
		p.add(l);
		
		JPanel p2 = new JPanel();
		p2.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		
		Object o = cbMidiDevice.getRenderer();
		if(o instanceof JLabel) ((JLabel )o).setHorizontalAlignment(SwingConstants.CENTER);
		
		cbMidiDevice.setPreferredSize(new Dimension(40, 18));
		cbMidiDevice.setMinimumSize(cbMidiDevice.getPreferredSize());
		cbMidiDevice.setMaximumSize(cbMidiDevice.getPreferredSize());
		p2.add(cbMidiDevice);
		
		p2.add(Box.createRigidArea(new Dimension(3, 0)));
		
		o = cbMidiPort.getRenderer();
		if(o instanceof JLabel) ((JLabel )o).setHorizontalAlignment(SwingConstants.CENTER);
		
		cbMidiPort.setPreferredSize(new Dimension(62, 18));
		cbMidiPort.setMinimumSize(cbMidiPort.getPreferredSize());
		cbMidiPort.setMaximumSize(cbMidiPort.getPreferredSize());
		p2.add(cbMidiPort);
		
		p2.add(Box.createRigidArea(new Dimension(3, 0)));
		
		o = cbMidiChannel.getRenderer();
		if(o instanceof JLabel) ((JLabel )o).setHorizontalAlignment(SwingConstants.CENTER);
		
		cbMidiChannel.addItem("All");
		for(int i = 1; i <= 16; i++) cbMidiChannel.addItem("Channel " + String.valueOf(i));
		cbMidiChannel.setPreferredSize(new Dimension(84, 18));
		cbMidiChannel.setMinimumSize(cbMidiChannel.getPreferredSize());
		cbMidiChannel.setMaximumSize(cbMidiChannel.getPreferredSize());
		
		p2.add(cbMidiChannel);
		p2.setAlignmentX(LEFT_ALIGNMENT);
		p2.setOpaque(false);
		p.add(p2);
		p.setBackground(new java.awt.Color(0x818181));
		
		bgp.add(p);
		
		bgp.add(Box.createRigidArea(new Dimension(4, 0)));
		
		p = new JPanel();
		p.setOpaque(true);
		p.setBorder(BorderFactory.createEmptyBorder(3, 4, 3, 4));
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		l = new JLabel(Res.gfxInstrumentMapTitle);
		l.setAlignmentX(LEFT_ALIGNMENT);
		l.setAlignmentX(LEFT_ALIGNMENT);
		p.add(l);
		
		p.add(Box.createRigidArea(new Dimension(0, 3)));
		
		//o = cbInstrumentMap.getRenderer();
		//if(o instanceof JLabel) ((JLabel )o).setHorizontalAlignment(SwingConstants.CENTER);
		
		cbInstrumentMap.setPreferredSize(new Dimension(122, 18));
		cbInstrumentMap.setMinimumSize(cbInstrumentMap.getPreferredSize());
		cbInstrumentMap.setMaximumSize(cbInstrumentMap.getPreferredSize());
		cbInstrumentMap.setAlignmentX(LEFT_ALIGNMENT);
		p.add(cbInstrumentMap);
		p.setBackground(new java.awt.Color(0x818181));
		bgp.add(p);
		
		bgp.add(Box.createRigidArea(new Dimension(4, 0)));
		
		p = new JPanel();
		p.setOpaque(true);
		p.setBorder(BorderFactory.createEmptyBorder(3, 4, 3, 4));
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		l = new JLabel(Res.gfxAudioOutputTitle);
		l.setAlignmentX(LEFT_ALIGNMENT);
		p.add(l);
		
		//p.add(Box.createRigidArea(new Dimension(0, 3)));
		
		p2 = new JPanel();
		p2.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		p2.setOpaque(false);
		p2.setAlignmentX(LEFT_ALIGNMENT);
		
		o = cbAudioDevice.getRenderer();
		if(o instanceof JLabel) ((JLabel )o).setHorizontalAlignment(SwingConstants.RIGHT);
		
		cbAudioDevice.setPreferredSize(new Dimension(40, 18));
		cbAudioDevice.setMinimumSize(cbAudioDevice.getPreferredSize());
		cbAudioDevice.setMaximumSize(cbAudioDevice.getPreferredSize());
		
		p2.add(cbAudioDevice);
		p2.add(Box.createRigidArea(new Dimension(3, 0)));
		btnChannelRouting = new PixmapButton(Res.gfxBtnCr, Res.gfxBtnCrRO);
		btnChannelRouting.setPressedIcon(Res.gfxBtnCrRO);
		btnChannelRouting.setEnabled(false);
		btnChannelRouting.setToolTipText(i18n.getLabel("ChannelOptions.routing"));
		
		btnChannelRouting.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				SamplerChannel c = channel.getChannelInfo();
				new JSChannelOutputRoutingDlg(CC.getMainFrame(), c).setVisible(true);
			
			}
		});
		
		p2.add(btnChannelRouting);
		
		p.add(p2);
		p.setBackground(new java.awt.Color(0x818181));
		p2 = new JPanel();
		p2.setLayout(new java.awt.BorderLayout());
		p.add(p2);
		bgp.add(p);
		
		add(bgp);
		
		cbMidiDevice.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { setMidiDevice(); }
		});
		
		cbMidiPort.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { setMidiPort(); }
		});
		
		cbMidiChannel.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { setMidiChannel(); }
		});
		
		samplerListener = new SamplerAdapter() {
			/** Invoked when the default MIDI instrument map is changed. */
			public void
			defaultMapChanged(SamplerEvent e) {
				updateCbInstrumentMapToolTipText();
				
			}
		};
		
		CC.getSamplerModel().addSamplerListener(samplerListener);
		
		cbInstrumentMap.addItem(noMap);
		cbInstrumentMap.addItem(defaultMap);
		for(MidiInstrumentMap map : CC.getSamplerModel().getMidiInstrumentMaps()) {
			cbInstrumentMap.addItem(map);
		}
		
		int map = channel.getModel().getChannelInfo().getMidiInstrumentMapId();
		cbInstrumentMap.setSelectedItem(CC.getSamplerModel().getMidiInstrumentMapById(map));
		if(cbInstrumentMap.getSelectedItem() == null) {
			if(map == -1) cbInstrumentMap.setSelectedItem(noMap);
			else if(map == -2) {
				cbInstrumentMap.setSelectedItem(defaultMap);
			}
		}
		
		updateCbInstrumentMapToolTipText();
		
		if(channel.getModel().getChannelInfo().getEngine() == null) {
			cbInstrumentMap.setEnabled(false);
		}
		
		cbInstrumentMap.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { updateInstrumentMap(); }
		});
		
		CC.getSamplerModel().addMidiInstrumentMapListListener(mapListListener);
		
		cbAudioDevice.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { setBackendAudioDevice(); }
		});
		
		channel.getModel().addSamplerChannelListener(new SamplerChannelAdapter() {
			public void
			channelChanged(SamplerChannelEvent e) { updateChannelProperties(); }
		});
		
		CC.getSamplerModel().addMidiDeviceListListener(getHandler());
		CC.getSamplerModel().addAudioDeviceListListener(getHandler());
		
		updateMidiDevices();
		updateAudioDevices();
		updateChannelProperties();
	}
	
	//////////////////////////////////////////////
	// Implementation of the ChannelOptionsView interface
	//////////////////////////////////////////////
	
	public JComponent
	getComponent() { return this; }
	
	public void
	installView() { }
	
	public void
	uninstallView() {
		onDestroy();
	}
	
	public void
	updateChannelInfo() {
		
	}
	
	//////////////////////////////////////////////
	
	/**
	 * Updates the channel settings. This method is invoked when changes to the
	 * channel were made.
	 */
	private void
	updateChannelProperties() {
		SamplerModel sm = CC.getSamplerModel();
		SamplerChannel sc = channel.getModel().getChannelInfo();
		
		MidiDeviceModel mm = sm.getMidiDeviceById(sc.getMidiInputDevice());
		AudioDeviceModel am = sm.getAudioDeviceById(sc.getAudioOutputDevice());
		
		if(isUpdate()) CC.getLogger().warning("Unexpected update state!");
		
		setUpdate(true);
		
		try {
			cbMidiDevice.setSelectedItem(mm == null ? null : mm.getDeviceInfo());
			
			cbAudioDevice.setSelectedItem(am == null ? null : am.getDeviceInfo());
			btnChannelRouting.setEnabled(am != null);
		} catch(Exception x) {
			CC.getLogger().log(Level.WARNING, "Unkown error", x);
		}
		
		if(sc.getEngine() != null) {
			cbInstrumentMap.setEnabled(true);
			int id = sc.getMidiInstrumentMapId();
			Object o;
			if(id == -2) o = defaultMap;
			else if(id == -1) o = noMap;
			else o = CC.getSamplerModel().getMidiInstrumentMapById(id);
			
			if(cbInstrumentMap.getSelectedItem() != o) {
				cbInstrumentMap.setSelectedItem(o);
			}
		} else {
			cbInstrumentMap.setSelectedItem(noMap);
			cbInstrumentMap.setEnabled(false);
		}
		
		setUpdate(false);
	}
	
	/**
	 * Updates the MIDI device list.
	 */
	private void
	updateMidiDevices() {
		SamplerModel sm = CC.getSamplerModel();
		SamplerChannel sc = channel.getModel().getChannelInfo();
		
		setUpdate(true);
		
		try {
			cbMidiDevice.removeAllItems();
		
			for(MidiDeviceModel m : sm.getMidiDevices())
				cbMidiDevice.addItem(m.getDeviceInfo());
		
			MidiDeviceModel mm = sm.getMidiDeviceById(sc.getMidiInputDevice());
			cbMidiDevice.setSelectedItem(mm == null ? null : mm.getDeviceInfo());
		} catch(Exception x) {
			CC.getLogger().log(Level.WARNING, "Unkown error", x);
		}
		 
		setUpdate(false);
	}
	
	
	private void
	updateInstrumentMap() {
		updateCbInstrumentMapToolTipText();
		
		int id = channel.getModel().getChannelInfo().getMidiInstrumentMapId();
		Object o = cbInstrumentMap.getSelectedItem();
		if(o == null && id == -1) return;
		
		int cbId;
		if(o == null || o == noMap) cbId = -1;
		else if(o == defaultMap) cbId = -2;
		else cbId = ((MidiInstrumentMap)o).getMapId();
		
		if(cbId == id) return;
		
		channel.getModel().setBackendMidiInstrumentMap(cbId);
	}
	
	private void
	updateCbInstrumentMapToolTipText() {
		if(cbInstrumentMap.getSelectedItem() != defaultMap) {
			cbInstrumentMap.setToolTipText(null);
			return;
		}
		
		MidiInstrumentMap m = CC.getSamplerModel().getDefaultMidiInstrumentMap();
		if(m != null) {
			String s = i18n.getLabel("Channel.ttDefault", m.getName());
			cbInstrumentMap.setToolTipText(s);
		} else {
			cbInstrumentMap.setToolTipText(null);
		}
	}
	
	/**
	 * Updates the audio device list.
	 */
	private void
	updateAudioDevices() {
		SamplerModel sm = CC.getSamplerModel();
		SamplerChannel sc = channel.getModel().getChannelInfo();
		
		setUpdate(true);
		
		try {
			cbAudioDevice.removeAllItems();
		
			for(AudioDeviceModel m : sm.getAudioDevices()) 
				cbAudioDevice.addItem(m.getDeviceInfo());
		
			AudioDeviceModel am = sm.getAudioDeviceById(sc.getAudioOutputDevice());
			cbAudioDevice.setSelectedItem(am == null ? null : am.getDeviceInfo());
		} catch(Exception x) {
			CC.getLogger().log(Level.WARNING, "Unkown error", x);
		}
		
		setUpdate(false);
	}
	
	private void
	setMidiDevice() {
		MidiInputDevice mid = (MidiInputDevice)cbMidiDevice.getSelectedItem();
		
		if(!isUpdate()) {
			if(mid != null) {
				channel.getModel().setBackendMidiInputDevice(mid.getDeviceId());
			}
			
			return;
		}
		
		if(midiDevice != null) midiDevice.removeMidiDeviceListener(getHandler());
		
		cbMidiPort.removeAllItems();
		
		if(mid == null) {
			midiDevice = null;
			cbMidiPort.setEnabled(false);
			
			cbMidiChannel.setSelectedItem(null);
			cbMidiChannel.setEnabled(false);
		} else {
			midiDevice = CC.getSamplerModel().getMidiDeviceById(mid.getDeviceId());
			if(midiDevice != null) midiDevice.addMidiDeviceListener(getHandler());
			
			cbMidiPort.setEnabled(true);
			
			MidiPort[] ports = mid.getMidiPorts();
			for(MidiPort port : ports) cbMidiPort.addItem(port);
			
			int p = channel.getModel().getChannelInfo().getMidiInputPort();
			cbMidiPort.setSelectedItem(p >= 0 && p < ports.length ? ports[p] : null);
			
			cbMidiChannel.setEnabled(true);
			int c = channel.getModel().getChannelInfo().getMidiInputChannel();
			cbMidiChannel.setSelectedItem(c == -1 ? "All" : "Channel " + (c + 1));
		}
	}
	
	private void
	setMidiPort() {
		if(isUpdate()) return;
		
		channel.getModel().setBackendMidiInputPort(cbMidiPort.getSelectedIndex());
	}
	
	private void
	setMidiChannel() {
		if(isUpdate()) return;
		
		Object o = cbMidiChannel.getSelectedItem();
		if(o == null) return;
		String s = o.toString();
		
		int c = s.equals("All") ? -1 : Integer.parseInt(s.substring(8)) - 1;
		
		channel.getModel().setBackendMidiInputChannel(c);
	}
	
	private void
	setBackendAudioDevice() {
		if(isUpdate()) return;
		AudioOutputDevice dev = (AudioOutputDevice)cbAudioDevice.getSelectedItem();
		if(dev != null) channel.getModel().setBackendAudioOutputDevice(dev.getDeviceId());
	}
	
	/**
	 * Determines whether the currently processed changes are due to update.
	 * @return <code>true</code> if the currently processed changes are due to update and
	 * <code>false</code> if the currently processed changes are due to user input.
	 */
	private boolean
	isUpdate() { return update; }
	
	/**
	 * Sets whether the currently processed changes are due to update.
	 * @param b Specify <code>true</code> to indicate that the currently 
	 * processed changes are due to update; <code>false</code> 
	 * indicates that the currently processed changes are due to user input.
	 */
	private void
	setUpdate(boolean b) { update = b; }
	
	protected void
	onDestroy() {
		SamplerModel sm = CC.getSamplerModel();
		
		sm.removeMidiDeviceListListener(getHandler());
		sm.removeAudioDeviceListListener(getHandler());
		sm.removeMidiInstrumentMapListListener(mapListListener);
		sm.removeSamplerListener(samplerListener);
		
		if(midiDevice != null) {
			midiDevice.removeMidiDeviceListener(getHandler());
		}
	}
	
	private final Handler handler = new Handler();
	
	private Handler
	getHandler() { return handler; }
	
	private class Handler implements MidiDeviceListListener, ListListener<AudioDeviceModel>,
					MidiDeviceListener {
		/**
		 * Invoked when a new MIDI device is created.
		 * @param e A <code>MidiDeviceListEvent</code>
		 * instance providing the event information.
		 */
		public void
		deviceAdded(MidiDeviceListEvent e) {
			cbMidiDevice.addItem(e.getMidiDeviceModel().getDeviceInfo());
		}
	
		/**
		 * Invoked when a MIDI device is removed.
		 * @param e A <code>MidiDeviceListEvent</code>
		 * instance providing the event information.
		 */
		public void
		deviceRemoved(MidiDeviceListEvent e) {
			cbMidiDevice.removeItem(e.getMidiDeviceModel().getDeviceInfo());
		}
		
		/**
		 * Invoked when a new audio device is created.
		 * @param e An <code>AudioDeviceListEvent</code>
		 * instance providing the event information.
		 */
		public void
		entryAdded(ListEvent<AudioDeviceModel> e) {
			cbAudioDevice.addItem(e.getEntry().getDeviceInfo());
		}
	
		/**
		 * Invoked when an audio device is removed.
		 * @param e An <code>AudioDeviceListEvent</code>
		 * instance providing the event information.
		 */
		public void
		entryRemoved(ListEvent<AudioDeviceModel> e) {
			cbAudioDevice.removeItem(e.getEntry().getDeviceInfo());
		}
		
		public void
		settingsChanged(MidiDeviceEvent e) {
			if(isUpdate()) {
				CC.getLogger().warning("Invalid update state");
				return;
			}
			
			setUpdate(true);
			int idx = cbMidiPort.getSelectedIndex();
			MidiInputDevice d = e.getMidiDeviceModel().getDeviceInfo();
			
			cbMidiPort.removeAllItems();
			for(MidiPort port : d.getMidiPorts()) cbMidiPort.addItem(port);
			
			if(idx >= cbMidiPort.getModel().getSize()) idx = 0;
			
			setUpdate(false);
			
			if(cbMidiPort.getModel().getSize() > 0) cbMidiPort.setSelectedIndex(idx);
		}
	}
	
	private class MapListListener implements ListListener<MidiInstrumentMap> {
		/** Invoked when a new MIDI instrument map is added to a list. */
		public void
		entryAdded(ListEvent<MidiInstrumentMap> e) {
			cbInstrumentMap.insertItemAt(e.getEntry(), cbInstrumentMap.getItemCount());
			boolean b = channel.getModel().getChannelInfo().getEngine() != null;
			if(b && !cbInstrumentMap.isEnabled()) cbInstrumentMap.setEnabled(true);
		}
	
		/** Invoked when a new MIDI instrument map is removed from a list. */
		public void
		entryRemoved(ListEvent<MidiInstrumentMap> e) {
			cbInstrumentMap.removeItem(e.getEntry());
			if(cbInstrumentMap.getItemCount() == 0) { // TODO: ?
				cbInstrumentMap.setSelectedItem(noMap);
				cbInstrumentMap.setEnabled(false);
			}
		}
	}
}
