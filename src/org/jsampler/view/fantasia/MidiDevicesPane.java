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

import net.sf.juife.ComponentList;
import net.sf.juife.DefaultComponentListModel;

import net.sf.juife.event.TaskEvent;
import net.sf.juife.event.TaskListener;

import org.jdesktop.swingx.JXCollapsiblePane;

import org.jsampler.CC;
import org.jsampler.MidiDeviceModel;

import org.jsampler.event.MidiDeviceListEvent;
import org.jsampler.event.MidiDeviceListListener;

import org.jsampler.task.Midi;

import org.jsampler.view.JSViewConfig;
import org.jsampler.view.SessionViewConfig.DeviceConfig;

import org.jsampler.view.fantasia.basic.FantasiaPanel;
import org.jsampler.view.fantasia.basic.PixmapButton;
import org.jsampler.view.fantasia.basic.PixmapPane;

import org.jsampler.view.std.JSNewMidiDeviceDlg;

import org.linuxsampler.lscp.MidiInputDriver;

import static org.jsampler.view.fantasia.FantasiaI18n.i18n;
import static org.jsampler.view.fantasia.FantasiaPrefs.*;

/**
 *
 * @author Grigor Iliev
 */
public class MidiDevicesPane extends JPanel {
	private final DeviceListPane devList = new DeviceListPane();
	private final DefaultComponentListModel<MidiDevicePane> listModel =
		new DefaultComponentListModel<MidiDevicePane>();
	
	/** Creates a new instance of <code>MidiDevicesPane</code> */
	public
	MidiDevicesPane() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		devList.setModel(listModel);
		devList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		devList.setAlignmentX(LEFT_ALIGNMENT);
		
		add(devList);
		add(new NewDevicePane());
		
		CC.getSamplerModel().addMidiDeviceListListener(getHandler());
		
		for(MidiDeviceModel m : CC.getSamplerModel().getMidiDevices()) {
			addDevice(m);
		}
	}
	
	public int
	getDevicePaneCount() { return listModel.size(); }
	
	public MidiDevicePane
	getDevicePaneAt(int index) { return listModel.get(index); }
	
	private void
	addDevice(MidiDeviceModel model) {
		for(int i = 0; i < listModel.getSize(); i++) {
			if(listModel.get(i).getDeviceId() == model.getDeviceId()) {
				CC.getLogger().warning("MIDI device exist: " + model.getDeviceId());
				return;
			}
		}
		
		MidiDevicePane dev = new MidiDevicePane(model);
		DeviceConfig config = null;
		JSViewConfig viewConfig = CC.getViewConfig();
		if(viewConfig != null && viewConfig.getSessionViewConfig() != null) {
			config = viewConfig.getSessionViewConfig().pollMidiDeviceConfig();
		}
		
		if(config != null && config.expanded) dev.showOptionsPane(true);
		
		listModel.add(dev);
	}
	
	private void
	removeDevice(MidiDeviceModel model) {
		for(int i = 0; i < listModel.getSize(); i++) {
			if(listModel.get(i).getDeviceId() == model.getDeviceId()) {
				listModel.remove(i);
				return;
			}
		}
		
		CC.getLogger().warning("MIDI device does not exist: " + model.getDeviceId());
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
	
	
	class NewDevicePane extends FantasiaPanel {
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
			
			String s = i18n.getLabel("MidiDevicesPane.newDevice");
			btnNew.setToolTipText(s);
			p.setToolTipText(s);
			
			add(p, BorderLayout.NORTH);
			setAlignmentX(LEFT_ALIGNMENT);
		}
		
		/*private Color c1 = new Color(0x888888);
		private Color c2 = new Color(0x707070);
		
		@Override
		protected void
		paintComponent(Graphics g) {
			super.paintComponent(g);
			
			double h = getSize().getHeight();
			double w = getSize().getWidth();
			
			FantasiaPainter.paintGradient((Graphics2D)g, 0, 0, w - 1, h - 1, c1, c2);
			
			FantasiaPainter.RoundCorners rc =
				new FantasiaPainter.RoundCorners(true);
			
			FantasiaPainter.paintOuterBorder((Graphics2D)g, 0, 0, w - 1, h - 1, rc);
		}*/
		
		private JPanel
		createVSeparator() {
			PixmapPane p = new PixmapPane(Res.gfxVLine);
			p.setOpaque(false);
			p.setPreferredSize(new Dimension(2, 24));
			p.setMinimumSize(p.getPreferredSize());
			p.setMaximumSize(p.getPreferredSize());
			return p;
		}
		
		private JXCollapsiblePane
		getCreateDevicePane() {
			if(createDevicePane != null) return createDevicePane;
			
			createDevicePane = new JXCollapsiblePane();
			final JSNewMidiDeviceDlg.Pane pane = new JSNewMidiDeviceDlg.Pane();
			
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
							createMidiDevice0(pane);
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
					createMidiDevice(pane);
				}
			});
			
			return createDevicePane;
		}
		
		private void
		showHidePopup() {
			if(!CC.verifyConnection()) return;
			getCreateDevicePane().setCollapsed(!getCreateDevicePane().isCollapsed());
		}
		
		private void
		createMidiDevice(final JSNewMidiDeviceDlg.Pane pane) {
			if(!createDevicePane.isAnimated()) {
				createMidiDevice0(pane);
				return;
			}
			
			createDevice = true;
			createDevicePane.setCollapsed(true);
		}
		
		private void
		createMidiDevice0(final JSNewMidiDeviceDlg.Pane pane) {
			pane.btnCreate.setEnabled(false);
			final MidiInputDriver driver = pane.getSelectedDriver();
			final Midi.CreateDevice cmd =
				new  Midi.CreateDevice(driver.getName(), pane.getParameters());
				
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
	
	private class EventHandler implements MidiDeviceListListener {
		@Override
		public void
		deviceAdded(MidiDeviceListEvent e) {
			addDevice(e.getMidiDeviceModel());
		}
		
		@Override
		public void
		deviceRemoved(MidiDeviceListEvent e) {
			removeDevice(e.getMidiDeviceModel());
		}
	}
}
