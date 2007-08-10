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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

import net.sf.juife.ComponentList;
import net.sf.juife.ComponentListModel;
import net.sf.juife.DefaultComponentListModel;

import org.jsampler.CC;
import org.jsampler.AudioDeviceModel;

import org.jsampler.event.ListEvent;
import org.jsampler.event.ListListener;

import static org.jsampler.view.fantasia.A4n.a4n;
import static org.jsampler.view.fantasia.FantasiaI18n.i18n;

/**
 *
 * @author Grigor Iliev
 */
public class AudioDevicesPane extends JPanel {
	private final DeviceListPane devList = new DeviceListPane();
	private final DefaultComponentListModel<AudioDevicePane> listModel =
		new DefaultComponentListModel<AudioDevicePane>();
	
	/** Creates a new instance of <code>AudioDevicesPane</code> */
	public
	AudioDevicesPane() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		devList.setModel(listModel);
		devList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		devList.setAlignmentX(LEFT_ALIGNMENT);
		
		add(devList);
		add(new NewDevicePane());
		
		CC.getSamplerModel().addAudioDeviceListListener(getHandler());
		
		for(AudioDeviceModel m : CC.getSamplerModel().getAudioDevices()) {
			addDevice(m);
		}
	}
	
	private void
	addDevice(AudioDeviceModel model) {
		for(int i = 0; i < listModel.getSize(); i++) {
			if(listModel.get(i).getDeviceId() == model.getDeviceId()) {
				CC.getLogger().warning("Audio device exist: " + model.getDeviceId());
				return;
			}
		}
		
		listModel.add(new AudioDevicePane(model));
	}
	
	private void
	removeDevice(AudioDeviceModel model) {
		for(int i = 0; i < listModel.getSize(); i++) {
			if(listModel.get(i).getDeviceId() == model.getDeviceId()) {
				listModel.remove(i);
				return;
			}
		}
		
		CC.getLogger().warning("Audio device does not exist: " + model.getDeviceId());
	}
	
	class DeviceListPane extends ComponentList {
		private Dimension maxSize = new Dimension();
		
		public Dimension
		getMaximumSize() {
			maxSize.width = Short.MAX_VALUE;
			maxSize.height = getPreferredSize().height;
			return maxSize;
		}
	}
	
	
	class NewDevicePane extends PixmapPane {
		private final PixmapButton btnNew =
			new PixmapButton(a4n.createAudioDevice, Res.gfxPowerOff18);
		
		NewDevicePane() {
			super(Res.gfxDeviceBg);
			setPixmapInsets(new Insets(1, 1, 1, 1));
			
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			add(Box.createRigidArea(new Dimension(3, 0)));
			add(btnNew);
			add(Box.createRigidArea(new Dimension(3, 0)));
			
			add(createVSeparator());
			
			Dimension d = new Dimension(77, 24);
			setPreferredSize(d);
			setMinimumSize(d);
			setMaximumSize(new Dimension(Short.MAX_VALUE, 24));
			
			btnNew.setPressedIcon(Res.gfxPowerOn18);
			
			addMouseListener(new MouseAdapter() {
				public void
				mouseClicked(MouseEvent e) {
					if(e.getButton() != e.BUTTON1) {
						return;
					}
					
					a4n.createAudioDevice.actionPerformed(null);
				}
			});
			
			setAlignmentX(LEFT_ALIGNMENT);
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			
			String s = i18n.getLabel("AudioDevicesPane.newDevice");
			btnNew.setToolTipText(s);
			setToolTipText(s);
		}
		
		private JPanel
		createVSeparator() {
			PixmapPane p = new PixmapPane(Res.gfxVLine);
			p.setOpaque(false);
			p.setPreferredSize(new Dimension(2, 24));
			p.setMinimumSize(p.getPreferredSize());
			p.setMaximumSize(p.getPreferredSize());
			return p;
		}
	}
	
	private final EventHandler eventHandler = new EventHandler();
	
	private EventHandler
	getHandler() { return eventHandler; }
	
	private class EventHandler implements ListListener<AudioDeviceModel> {
		public void
		entryAdded(ListEvent<AudioDeviceModel> e) {
			addDevice(e.getEntry());
		}
		
		public void
		entryRemoved(ListEvent<AudioDeviceModel> e) {
			removeDevice(e.getEntry());
		}
	}
}
