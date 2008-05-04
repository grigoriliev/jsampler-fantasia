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

import java.awt.Dimension;
import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jsampler.CC;

import org.jvnet.substance.SubstanceImageCreator;

import org.linuxsampler.lscp.SamplerChannel;

import static org.jsampler.view.fantasia.FantasiaI18n.i18n;
import static org.jsampler.view.fantasia.FantasiaPrefs.*;
import static org.jsampler.view.fantasia.FantasiaUtils.*;


/**
 *
 * @author Grigor Iliev
 */
public class SmallChannelView extends PixmapPane implements ChannelView {
	private final Channel channel;
	private final ChannelOptionsView channelOptionsView;
	
	private final ChannelScreen screen;
	private final Channel.PowerButton btnPower;
	private final MuteButton btnMute = new MuteButton();
	private final SoloButton btnSolo = new SoloButton();
	private final Channel.OptionsButton btnOptions;
	
	private final Vector<JComponent> components = new Vector<JComponent>();
	
	/** Creates a new instance of <code>SmallChannelView</code> */
	public
	SmallChannelView(Channel channel) {
		this(channel, new NormalChannelOptionsView(channel));
	}
	
	/** Creates a new instance of <code>SmallChannelView</code> */
	public
	SmallChannelView(Channel channel, ChannelOptionsView channelOptionsView) {
		super(Res.gfxDeviceBg);
		
		setPixmapInsets(new Insets(1, 1, 1, 1));
		
		components.add(this);
		
		this.channel = channel;
		this.channelOptionsView = channelOptionsView;
		
		addMouseListener(channel.getContextMenu());
		
		screen = new ChannelScreen(channel);
		
		btnPower = new Channel.PowerButton(channel);
		components.add(btnPower);
		
		btnOptions = new Channel.OptionsButton(channel);
		components.add(btnOptions);
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		setBorder(BorderFactory.createEmptyBorder(1, 3, 0, 11));
		
		add(btnPower);
		add(Box.createRigidArea(new Dimension(4, 0)));
		
		add(createVSeparator());
		add(Box.createRigidArea(new Dimension(3, 0)));
		
		add(screen);
		
		add(Box.createRigidArea(new Dimension(2, 0)));
		add(createVSeparator());
		add(new FxSendsButton());
		
		add(createVSeparator());
		add(Box.createRigidArea(new Dimension(1, 0)));
		
		components.add(btnMute);
		components.add(btnSolo);
		
		add(btnMute);
		add(btnSolo);
		
		add(Box.createRigidArea(new Dimension(1, 0)));
		add(createVSeparator());
		add(Box.createRigidArea(new Dimension(8, 0)));
		
		add(btnOptions);
		
		setPreferredSize(new Dimension(420, 22));
		setMinimumSize(getPreferredSize());
		setMaximumSize(getPreferredSize());
		
		installView();
	}
	
	//////////////////////////////////////////////
	// Implementation of the ChannelView interface
	//////////////////////////////////////////////
	
	public Type
	getType() { return Type.SMALL; }
	
	public JComponent
	getComponent() { return this; }
	
	public void
	installView() {
		String vmud = VOL_MEASUREMENT_UNIT_DECIBEL;
		preferences().addPropertyChangeListener(vmud, new PropertyChangeListener() {
			public void
			propertyChange(PropertyChangeEvent e) {
				boolean b;
				b = preferences().getBoolProperty(VOL_MEASUREMENT_UNIT_DECIBEL);
				screen.updateVolumeInfo();
			}
		});
	}
	
	public void
	uninstallView() {
		
	}
	
	public ChannelOptionsView
	getChannelOptionsView() { return channelOptionsView; }
	
	public void
	updateChannelInfo() {
		SamplerChannel sc = channel.getChannelInfo();
		
		screen.updateScreenInfo(sc);
		screen.updateVolumeInfo();
		updateMuteIcon(sc);
		
		if(sc.isSoloChannel()) btnSolo.setIcon(Res.gfxSoloSmallOn);
		else btnSolo.setIcon(Res.gfxSoloSmallOff);
		
		boolean b = sc.getEngine() != null;
		btnSolo.setEnabled(b);
		btnMute.setEnabled(b);
	}
	
	public void
	updateStreamCount(int count) { screen.updateStreamCount(count); }
	
	public void
	updateVoiceCount(int count) { screen.updateVoiceCount(count); }
	
	public void
	expandChannel() {
		if(btnOptions.isSelected()) return;
		btnOptions.doClick();
	}
	
	public boolean
	isOptionsButtonSelected() { return btnOptions.isSelected(); }
	
	public void
	setOptionsButtonSelected(boolean b) {
		btnOptions.setSelected(b);
	}
	
	public void
	addEnhancedMouseListener(MouseListener l) {
		removeEnhancedMouseListener(l);
		
		for(JComponent c : components) c.addMouseListener(l);
		screen.addEnhancedMouseListener(l);
	}
	
	public void
	removeEnhancedMouseListener(MouseListener l) {
		for(JComponent c : components) c.removeMouseListener(l);
		screen.removeEnhancedMouseListener(l);
	}
	
	//////////////////////////////////////////////
	
	
	/**
	 * Updates the mute button with the proper icon regarding to information obtained
	 * from <code>channel</code>.
	 * @param channel A <code>SamplerChannel</code> instance containing the new settings
	 * for this channel.
	 */
	private void
	updateMuteIcon(SamplerChannel channel) {
		if(channel.isMutedBySolo()) btnMute.setIcon(Res.gfxMutedBySoloSmall);
		else if(channel.isMuted()) btnMute.setIcon(Res.gfxMuteSmallOn);
		else btnMute.setIcon(Res.gfxMuteSmallOff);
	}
	
	protected JPanel
	createVSeparator() {
		PixmapPane p = new PixmapPane(Res.gfxVLine);
		p.setOpaque(false);
		p.setPreferredSize(new Dimension(2, 22));
		p.setMinimumSize(p.getPreferredSize());
		p.setMaximumSize(p.getPreferredSize());
		return p;
	}
	
	
	private class MuteButton extends PixmapButton implements ActionListener {
		MuteButton() {
			super(Res.gfxMuteSmallOff);
			setDisabledIcon (
				SubstanceImageCreator.makeTransparent(this, Res.gfxMuteSmallOff, 0.4)
			);
			addActionListener(this);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			SamplerChannel sc = channel.getChannelInfo();
			boolean b = true;
		
			/*
			 * Changing the mute button icon now instead of
			 * leaving the work to the notification mechanism of the LinuxSampler.
			 */
			if(sc.isMuted() && !sc.isMutedBySolo()) {
				b = false;
				boolean hasSolo = CC.getSamplerModel().hasSoloChannel();
			
				if(sc.isSoloChannel() || !hasSolo) setIcon(Res.gfxMuteSmallOff);
				else setIcon(Res.gfxMutedBySoloSmall);
			} else setIcon(Res.gfxMuteSmallOn);
			
			channel.getModel().setBackendMute(b);
		}
		
		//public boolean
		//contains(int x, int y) { return (x > 5 && x < 23) && (y > 5 && y < 16); }
	}
	
	private class SoloButton extends PixmapButton implements ActionListener {
		SoloButton() {
			super(Res.gfxSoloSmallOff);
			//setDisabledIcon(Res.gfxMuteSoloDisabled);
			setDisabledIcon (
				SubstanceImageCreator.makeTransparent(this, Res.gfxSoloSmallOff, 0.4)
			);
			addActionListener(this);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			SamplerChannel sc = channel.getChannelInfo();
			boolean b = !sc.isSoloChannel();
		
			/*
			 * Changing the solo button icon (and related) now instead of
			 * leaving the work to the notification mechanism of the LinuxSampler.
			 */
			if(b) {
				setIcon(Res.gfxSoloSmallOn);
				if(sc.isMutedBySolo()) btnMute.setIcon(Res.gfxMuteSmallOff);
			} else {
				setIcon(Res.gfxSoloSmallOff);
				if(!sc.isMuted() && CC.getSamplerModel().getSoloChannelCount() > 1)
					btnMute.setIcon(Res.gfxMutedBySoloSmall);
			}
		
			channel.getModel().setBackendSolo(b);
		}
		
		//public boolean
		//contains(int x, int y) { return (x > 5 && x < 23) && (y > 5 && y < 16); }
	}
	
	static class ChannelScreen extends PixmapPane {
		private final Channel channel;
		
		//private final ChannelInfoPane channelInfoPane;
		
		private final Channel.StreamVoiceCountPane streamVoiceCountPane;
			
		
		private final Channel.VolumePane volumePane;
		
		private JButton btnInstr =
			createScreenButton(i18n.getButtonLabel("ChannelScreen.btnInstr"));
		
	
		private static Insets pixmapInsets = new Insets(5, 5, 4, 5);
		
		private final Vector<JComponent> components = new Vector<JComponent>();
		
		ChannelScreen(final Channel channel) {
			super(Res.gfxTextField);
			
			components.add(this);
			
			this.channel = channel;
			
			addMouseListener(channel.getContextMenu());
			
			streamVoiceCountPane = new Channel.StreamVoiceCountPane(channel);
			components.add(streamVoiceCountPane);
			
			//channelInfoPane = new ChannelInfoPane(channel);
			volumePane = new Channel.VolumePane(channel);
			components.add(volumePane);
			
			setPixmapInsets(pixmapInsets);
			setBorder(BorderFactory.createEmptyBorder(4, 3, 3, 4));
			
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			
			JPanel p = new JPanel();
			components.add(p);
			p.setOpaque(false);
			p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
			
			//p.add(channelInfoPane);
			
			btnInstr.setRolloverEnabled(false);
			btnInstr.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
			btnInstr.setHorizontalAlignment(btnInstr.LEFT);
			btnInstr.addMouseListener(channel.getContextMenu());
			
			int h = btnInstr.getPreferredSize().height;
			btnInstr.setPreferredSize(new Dimension(100, h));
			btnInstr.setMinimumSize(btnInstr.getPreferredSize());
			btnInstr.setMaximumSize(new Dimension(Short.MAX_VALUE, h));
			components.add(btnInstr);
			
			p.add(btnInstr);
			
			h = p.getPreferredSize().height;
			p.setPreferredSize(new Dimension(159, h));
			p.setMinimumSize(p.getPreferredSize());
			p.setMaximumSize(p.getPreferredSize());
			
			add(p);
			
			add(Box.createRigidArea(new Dimension(3, 0)));
			add(streamVoiceCountPane);
			add(volumePane);
			
			setPreferredSize(new Dimension(270, getPreferredSize().height));
			setMinimumSize(getPreferredSize());
			setMaximumSize(getPreferredSize());
			
			btnInstr.addActionListener(new ActionListener() {
				public void
				actionPerformed(ActionEvent e) { channel.loadInstrument(); }
			});
		}
		
		public void
		addEnhancedMouseListener(MouseListener l) {
			removeEnhancedMouseListener(l);
			for(JComponent c : components) c.addMouseListener(l);
		}
		
		public void
		removeEnhancedMouseListener(MouseListener l) {
			for(JComponent c : components) c.removeMouseListener(l);
		}
		
		protected void
		updateVolumeInfo() {
			float f = channel.getChannelInfo().getVolume() * 100.0f;
			volumePane.updateVolumeInfo((int)f);
		}
		
		/**
		* Updates the number of active disk streams.
		* @param count The new number of active disk streams.
		*/
		protected void
		updateStreamCount(int count) {
			streamVoiceCountPane.updateStreamCount(count);
		}
		
		/**
		 * Updates the number of active voices.
		 * @param count The new number of active voices.
		 */
		protected void
		updateVoiceCount(int count) {
			streamVoiceCountPane.updateVoiceCount(count);
		}
	
		protected void
		updateScreenInfo(SamplerChannel sc) {
			int status = sc.getInstrumentStatus();
			if(status >= 0 && status < 100) {
				btnInstr.setText(i18n.getLabel("ChannelScreen.loadingInstrument", status));
			} else if(status == -1) {
				btnInstr.setText(i18n.getButtonLabel("ChannelScreen.btnInstr"));
			} else if(status < -1) {
				 btnInstr.setText(i18n.getLabel("ChannelScreen.errorLoadingInstrument"));
			} else {
				if(sc.getInstrumentName() != null) btnInstr.setText(sc.getInstrumentName());
				else btnInstr.setText(i18n.getButtonLabel("ChannelScreen.btnInstr"));
			}
			
			//channelInfoPane.updateChannelInfo();
		}
	}
	
	private static class ChannelInfoPane extends JPanel {
		private final Channel channel;
		private final JLabel lInfo;
			
		ChannelInfoPane(Channel channel) {
			this.channel = channel;
			
			setOpaque(false);
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			
			lInfo = createScreenLabel("");
			lInfo.setFont(Res.fontScreenMono);
			lInfo.setText("1234");
			lInfo.setPreferredSize(lInfo.getPreferredSize()); // Don't remove this!
			lInfo.setMinimumSize(lInfo.getPreferredSize());
			lInfo.setMaximumSize(lInfo.getPreferredSize());
			
			updateChannelInfo();
			
			add(lInfo);
			
			setPreferredSize(getPreferredSize()); // Don't remove this!
			setMinimumSize(getPreferredSize());
			setMaximumSize(getPreferredSize());
		}
		
		protected void
		updateChannelInfo() {
			String s = channel.getChannelId() < 10 ? " " : "";
			s += String.valueOf(channel.getChannelId()) + ":";
			
			/*SamplerChannel sc = channel.getChannelInfo();
			s += sc.getMidiInputPort() + "/";
			
			if(sc.getMidiInputChannel() == -1) s += "All";
			else s += sc.getMidiInputChannel() + 1;*/
			lInfo.setText(s);
		}
	}
	
	private class FxSendsButton extends PixmapButton implements ActionListener {
		FxSendsButton() {
			super(Res.gfxFx);
			
			setRolloverIcon(Res.gfxFxRO);
			
			addActionListener(this);
		}
		
		public void
		actionPerformed(ActionEvent e) { channel.showFxSendsDialog(); }
		
		public boolean
		contains(int x, int y) { return (x > 5 && x < 23) && (y > 5 && y < 16); }
	}
}
