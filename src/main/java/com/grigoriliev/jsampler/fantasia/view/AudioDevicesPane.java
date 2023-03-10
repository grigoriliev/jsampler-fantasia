/*
 *   JSampler - a front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2023 Grigor Iliev <grigor@grigoriliev.com>
 *
 *   This file is part of JSampler.
 *
 *   JSampler is free software: you can redistribute it and/or modify it under
 *   the terms of the GNU General Public License as published by the Free
 *   Software Foundation, either version 3 of the License, or (at your option)
 *   any later version.
 *
 *   JSampler is distributed in the hope that it will be useful, but WITHOUT
 *   ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *   FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 *   more details.
 *
 *   You should have received a copy of the GNU General Public License along
 *   with JSampler. If not, see <https://www.gnu.org/licenses/>.
 */

package com.grigoriliev.jsampler.fantasia.view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import com.grigoriliev.jsampler.fantasia.view.basic.PixmapButton;
import com.grigoriliev.jsampler.fantasia.view.basic.PixmapPane;
import com.grigoriliev.jsampler.juife.swing.ComponentList;
import com.grigoriliev.jsampler.juife.swing.DefaultComponentListModel;

import com.grigoriliev.jsampler.juife.event.TaskEvent;
import com.grigoriliev.jsampler.juife.event.TaskListener;

import org.jdesktop.swingx.JXCollapsiblePane;

import com.grigoriliev.jsampler.CC;
import com.grigoriliev.jsampler.AudioDeviceModel;

import com.grigoriliev.jsampler.event.ListEvent;
import com.grigoriliev.jsampler.event.ListListener;

import com.grigoriliev.jsampler.task.Audio;

import com.grigoriliev.jsampler.view.JSViewConfig;
import com.grigoriliev.jsampler.view.SessionViewConfig.DeviceConfig;
import com.grigoriliev.jsampler.swing.view.std.JSNewAudioDeviceDlg;

import com.grigoriliev.jsampler.jlscp.AudioOutputDriver;

import static com.grigoriliev.jsampler.fantasia.view.FantasiaPrefs.*;

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
	
	public int
	getDevicePaneCount() { return listModel.size(); }
	
	public AudioDevicePane
	getDevicePaneAt(int index) { return listModel.get(index); }
	
	private void
	addDevice(AudioDeviceModel model) {
		for(int i = 0; i < listModel.getSize(); i++) {
			if(listModel.get(i).getDeviceId() == model.getDeviceId()) {
				CC.getLogger().warning("Audio device exist: " + model.getDeviceId());
				return;
			}
		}
		
		AudioDevicePane dev = new AudioDevicePane(model);
		DeviceConfig config = null;
		JSViewConfig viewConfig = CC.getViewConfig();
		if(viewConfig != null && viewConfig.getSessionViewConfig() != null) {
			config = viewConfig.getSessionViewConfig().pollAudioDeviceConfig();
		}
		
		if(config != null && config.expanded) dev.showOptionsPane(true);
		
		listModel.add(dev);
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
		
		@Override
		public Dimension
		getMaximumSize() {
			maxSize.width = Short.MAX_VALUE;
			maxSize.height = getPreferredSize().height;
			return maxSize;
		}
	}
	
	
	class NewDevicePane extends JPanel {
		private final PixmapButton btnNew = new PixmapButton(Res.gfxPowerOff18);
		private JXCollapsiblePane createDevicePane = null;
		private boolean createDevice = false;
		
		NewDevicePane() {
			setLayout(new BorderLayout());
			
			PixmapPane p = new PixmapPane(Res.gfxDeviceBg);
			p.setPixmapInsets(new Insets(1, 1, 1, 1));
			
			p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
			p.add(Box.createRigidArea(new Dimension(3, 0)));
			p.add(btnNew);
			p.add(Box.createRigidArea(new Dimension(3, 0)));
			
			p.add(createVSeparator());
			
			Dimension d = new Dimension(77, 24);
			p.setPreferredSize(d);
			p.setMinimumSize(d);
			p.setMaximumSize(new Dimension(Short.MAX_VALUE, 24));
			
			btnNew.setPressedIcon(Res.gfxPowerOn18);
			
			btnNew.addActionListener(new ActionListener() {
				public void
				actionPerformed(ActionEvent e) {
					showHidePopup();
				}
			});
			
			p.addMouseListener(new MouseAdapter() {
				public void
				mouseClicked(MouseEvent e) {
					if(e.getButton() != e.BUTTON1) {
						return;
					}
					
					showHidePopup();
				}
			});
			
			p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			
			String s = FantasiaI18n.i18n.getLabel("AudioDevicesPane.newDevice");
			btnNew.setToolTipText(s);
			p.setToolTipText(s);
			
			add(p, BorderLayout.NORTH);
			setAlignmentX(LEFT_ALIGNMENT);
		}
		
		private JXCollapsiblePane
		getCreateDevicePane() {
			if(createDevicePane != null) return createDevicePane;
			
			createDevicePane = new JXCollapsiblePane();
			final JSNewAudioDeviceDlg.Pane pane = new JSNewAudioDeviceDlg.Pane();
			
			PixmapPane p1 = new PixmapPane(Res.gfxChannelOptions);
			p1.setPixmapInsets(new Insets(1, 1, 1, 1));
			p1.setLayout(new BorderLayout());
			p1.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
			
			PixmapPane p2 = new PixmapPane(Res.gfxBorder);
			p2.setPixmapInsets(new Insets(1, 1, 1, 1));
			p2.setLayout(new BorderLayout());
			p2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			p2.add(pane);
			p1.add(p2);
			
			p1.setPreferredSize(new Dimension(getWidth(), 210));
			p1.setPreferredSize(new Dimension(100, 210));
			
			createDevicePane.setContentPane(p1);
			createDevicePane.setAnimated(false);
			createDevicePane.setCollapsed(true);
			createDevicePane.setAnimated(preferences().getBoolProperty(ANIMATED));
		
			preferences().addPropertyChangeListener(ANIMATED, new PropertyChangeListener() {
				public void
				propertyChange(PropertyChangeEvent e) {
					boolean b = preferences().getBoolProperty(ANIMATED);
					createDevicePane.setAnimated(b);
				}
			});
			
			String s = JXCollapsiblePane.ANIMATION_STATE_KEY;
			createDevicePane.addPropertyChangeListener(s, new PropertyChangeListener() {
				public void
				propertyChange(PropertyChangeEvent e) {
					Object o = e.getNewValue();
					if(o == "collapsed") {
						if(createDevice) {
							createAudioDevice0(pane);
							createDevice = false;
						}
					} else if(o == "expanded" || o == "expanding/collapsing") {
						ensureCreateDevicePaneIsVisible();
					}
				}
			});
			
			add(createDevicePane);
			
			pane.btnCancel.addActionListener(new ActionListener() {
				public void
				actionPerformed(ActionEvent e) {
					createDevicePane.setCollapsed(true);
				}
			});
			
			pane.btnCreate.addActionListener(new ActionListener() {
				public void
				actionPerformed(ActionEvent e) {
					createAudioDevice(pane);
				}
			});
			
			return createDevicePane;
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
		
		private void
		showHidePopup() {
			if(!CC.verifyConnection()) return;
			getCreateDevicePane().setCollapsed(!getCreateDevicePane().isCollapsed());
		}
		
		private void
		createAudioDevice(final JSNewAudioDeviceDlg.Pane pane) {
			if(!createDevicePane.isAnimated()) {
				createAudioDevice0(pane);
				return;
			}
			
			createDevice = true;
			createDevicePane.setCollapsed(true);
		}
		
		private void
		createAudioDevice0(final JSNewAudioDeviceDlg.Pane pane) {
			pane.btnCreate.setEnabled(false);
			final AudioOutputDriver driver = pane.getSelectedDriver();
			final Audio.CreateDevice cmd =
				new  Audio.CreateDevice(driver.getName(), pane.getParameters());
				
			cmd.addTaskListener(new TaskListener() {
				public void
				taskPerformed(TaskEvent e) {
					pane.btnCreate.setEnabled(true);
					getCreateDevicePane().setCollapsed(true);
				}
			});
			
			CC.getTaskQueue().add(cmd);
		}
		
		private void
		ensureCreateDevicePaneIsVisible() {
			Container p = createDevicePane.getParent();
			JScrollPane sp = null;
			int i = createDevicePane.getLocation().y + createDevicePane.getHeight();
			while(p != null) {
				if(p instanceof JScrollPane) {
					sp = (JScrollPane)p;
					break;
				}
				i += p.getLocation().y;
				p = p.getParent();
			}
			
			if(sp == null) return;
			sp.getViewport().scrollRectToVisible(new Rectangle(0, i, 5, 5));
		}
	}
	
	private final EventHandler eventHandler = new EventHandler();
	
	private EventHandler
	getHandler() { return eventHandler; }
	
	private class EventHandler implements ListListener<AudioDeviceModel> {
		@Override
		public void
		entryAdded(ListEvent<AudioDeviceModel> e) {
			addDevice(e.getEntry());
		}
		
		@Override
		public void
		entryRemoved(ListEvent<AudioDeviceModel> e) {
			removeDevice(e.getEntry());
		}
	}
}
