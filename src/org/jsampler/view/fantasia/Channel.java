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
import java.awt.Graphics;
import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.logging.Level;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sf.juife.Dial;
import net.sf.juife.InformationDialog;
import net.sf.juife.JuifeUtils;
import net.sf.juife.TitleBar;

import org.jdesktop.swingx.JXCollapsiblePane;

import org.jsampler.AudioDeviceModel;
import org.jsampler.CC;
import org.jsampler.MidiDeviceModel;
import org.jsampler.MidiInstrumentMap;
import org.jsampler.SamplerChannelModel;
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
import org.jsampler.event.SamplerChannelListEvent;
import org.jsampler.event.SamplerChannelListListener;
import org.jsampler.event.SamplerChannelListener;
import org.jsampler.event.SamplerEvent;
import org.jsampler.event.SamplerListener;

import org.jsampler.view.std.JSChannelOutputRoutingDlg;
import org.jsampler.view.std.JSFxSendsPane;
import org.jsampler.view.std.JSInstrumentChooser;

import org.jvnet.lafwidget.animation.FadeConfigurationManager;
import org.jvnet.lafwidget.animation.FadeKind;

import org.jvnet.substance.SubstanceImageCreator;
import org.jvnet.substance.SubstanceLookAndFeel;

import org.linuxsampler.lscp.AudioOutputDevice;
import org.linuxsampler.lscp.MidiInputDevice;
import org.linuxsampler.lscp.MidiPort;
import org.linuxsampler.lscp.SamplerChannel;
import org.linuxsampler.lscp.SamplerEngine;

import static org.jsampler.view.fantasia.FantasiaI18n.i18n;
import static org.jsampler.view.fantasia.FantasiaPrefs.*;


/**
 *
 * @author Grigor Iliev
 */
public class Channel extends org.jsampler.view.JSChannel {
	private final JXCollapsiblePane mainPane;
	private final ChannelScreen screen = new ChannelScreen(this);
	private final ChannelOptions optionsPane = new ChannelOptions(this);
	
	private final PowerButton btnPower = new PowerButton();
	private final MuteButton btnMute = new MuteButton();
	private final SoloButton btnSolo = new SoloButton();
	private final OptionsButton btnOptions = new OptionsButton();
	
	private final EnhancedDial dialVolume = new EnhancedDial();
	
	private boolean selected = false;
	
	/**
	 * Creates a new instance of <code>Channel</code> using the specified
	 * non-<code>null</code> channel model.
	 * @param model The model to be used by this channel.
	 * @throws IllegalArgumentException If the model is <code>null</code>.
	 */
	public
	Channel(SamplerChannelModel model) {
		super(model);
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		ChannelPane p = new ChannelPane();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		
		//p.add(Box.createRigidArea(new Dimension(3, 0)));
		
		btnPower.setAlignmentY(JPanel.TOP_ALIGNMENT);
		
		JPanel tb = new JPanel();
		tb.setBorder(BorderFactory.createEmptyBorder(3, 3, 0, 4));
		tb.setLayout(new BoxLayout(tb, BoxLayout.X_AXIS));
		tb.setOpaque(false);
		tb.setAlignmentY(JPanel.TOP_ALIGNMENT);
		tb.add(btnPower);
		tb.setPreferredSize(new Dimension(tb.getPreferredSize().width, 58));
		tb.setMinimumSize(tb.getPreferredSize());
		tb.setMaximumSize(tb.getPreferredSize());
		p.add(tb);
		
		//p.add(Box.createRigidArea(new Dimension(4, 0)));
		
		p.add(createVSeparator());
		
		//p.add(Box.createRigidArea(new Dimension(3, 0)));
		
		JPanel p2 = new JPanel();
		p2.setOpaque(false);
		p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));
		p2.setAlignmentY(JPanel.TOP_ALIGNMENT);
		p2.setBorder(BorderFactory.createEmptyBorder(5, 3, 0, 2));
		p2.add(screen);
		p.add(p2);
		
		p.add(createVSeparator());
		
		p2 = new JPanel();
		p2.setOpaque(false);
		p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));
		p2.setAlignmentY(JPanel.TOP_ALIGNMENT);
		p2.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
		p2.add(new JLabel(Res.gfxMuteTitle));
		p2.add(btnMute);
		p2.add(new JLabel(Res.gfxSoloTitle));
		p2.add(btnSolo);
		
		p.add(p2);
		
		p.add(createVSeparator());
		
		p2 = new JPanel();
		p2.setOpaque(false);
		p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));
		p2.setAlignmentY(JPanel.TOP_ALIGNMENT);
		p2.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
		JLabel l = new JLabel(Res.gfxVolumeTitle);
		l.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		l.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
		p2.add(l);
		dialVolume.setDialPixmap(Res.gfxVolumeDial, 30, 330);
		dialVolume.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		p2.add(dialVolume);
		p.add(p2);
		
		p.add(createVSeparator());
		
		p2 = new JPanel();
		p2.setOpaque(false);
		p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));
		p2.setAlignmentY(JPanel.TOP_ALIGNMENT);
		p2.setBorder(BorderFactory.createEmptyBorder(27, 0, 0, 0));
		l = new JLabel(Res.gfxOptionsTitle);
		l.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		l.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
		p2.add(l);
		
		p2.add(Box.createRigidArea(new Dimension(0, 3)));
		
		btnOptions.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		p2.add(btnOptions);
		p.add(p2);
		
		
		p.setPreferredSize(new Dimension(420, 60));
		p.setMinimumSize(p.getPreferredSize());
		p.setMaximumSize(p.getPreferredSize());
		//p.setBorder(BorderFactory.createEmptyBorder(1, 0, 1, 0));

		p.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		optionsPane.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		
		mainPane = new JXCollapsiblePane();
		mainPane.getContentPane().setLayout (
			new BoxLayout(mainPane.getContentPane(), BoxLayout.Y_AXIS)
		);
		
		mainPane.add(p);
		mainPane.add(optionsPane);
		
		setOpaque(false);
		
		getModel().addSamplerChannelListener(getHandler());
		
		updateChannelInfo();
		
		add(mainPane);
		
		mainPane.setAnimated(false);
		mainPane.setCollapsed(true);
		mainPane.setAnimated(preferences().getBoolProperty(ANIMATED));
		mainPane.setCollapsed(false);
		
		preferences().addPropertyChangeListener(ANIMATED, new PropertyChangeListener() {
			public void
			propertyChange(PropertyChangeEvent e) {
				mainPane.setAnimated(preferences().getBoolProperty(ANIMATED));
			}
		});
	}
	
	private JPanel
	createVSeparator() {
		PixmapPane p = new PixmapPane(Res.gfxVLine);
		p.setAlignmentY(JPanel.TOP_ALIGNMENT);
		p.setPreferredSize(new Dimension(2, 60));
		p.setMinimumSize(p.getPreferredSize());
		p.setMaximumSize(p.getPreferredSize());
		return p;
	}
	
	/**
	 * Determines whether the channel is selected.
	 * @return <code>true</code> if the channel is selected, <code>false</code> otherwise.
	 */
	public boolean isSelected() { return selected; }
	
	/**
	 * Sets the selection state of this channel.
	 * This method is invoked when the selection state of the channel has changed.
	 * @param select Specifies the new selection state of this channel;
	 * <code>true</code> to select the channel, <code>false</code> otherwise.
	 */
	public void
	setSelected(boolean select) {
		
		selected = select;
	}
	
	/** Shows the channel properties. */
	public void
	expandChannel() { expandChannel(optionsPane.isAnimated()); }
	
	/** Shows the channel properties. */
	public void
	expandChannel(boolean animated) {
		if(btnOptions.isSelected()) return;
		
		boolean b = optionsPane.isAnimated();
		optionsPane.setAnimated(animated);
		btnOptions.doClick();
		optionsPane.setAnimated(b);
	}
	
	
	/** Invoked when the user changes the volume */
	private void
	setVolume() {
		screen.updateVolumeInfo(dialVolume.getValue());
		
		if(dialVolume.getValueIsAdjusting()) return;
		
		int vol = (int)(getChannelInfo().getVolume() * 100);
		
		if(vol == dialVolume.getValue()) return;
		
		
		/*
		 * If the model's volume is not equal to the dial knob
		 * value we assume that the change is due to user input.
		 * So we must update the volume at the backend too.
		 */
		float volume = dialVolume.getValue();
		volume /= 100;
		getModel().setBackendVolume(volume);
	}
	
	/**
	 * Updates the channel settings. This method is invoked when changes to the
	 * channel were made.
	 */
	private void
	updateChannelInfo() {
		SamplerChannel sc = getChannelInfo();
		
		screen.updateScreenInfo(sc);
		updateMuteIcon(sc);
		
		if(sc.isSoloChannel()) btnSolo.setIcon(Res.gfxSoloOn);
		else btnSolo.setIcon(Res.gfxSoloOff);
		
		dialVolume.setValue((int)(sc.getVolume() * 100));
		
		boolean b = sc.getEngine() != null;
		dialVolume.setEnabled(b);
		btnSolo.setEnabled(b);
		btnMute.setEnabled(b);
	}
	
	/**
	 * Updates the mute button with the proper icon regarding to information obtained
	 * from <code>channel</code>.
	 * @param channel A <code>SamplerChannel</code> instance containing the new settings
	 * for this channel.
	 */
	private void
	updateMuteIcon(SamplerChannel channel) {
		if(channel.isMutedBySolo()) btnMute.setIcon(Res.gfxMutedBySolo);
		else if(channel.isMuted()) btnMute.setIcon(Res.gfxMuteOn);
		else btnMute.setIcon(Res.gfxMuteOff);
	}
	
	private class EnhancedDial extends Dial {
		EnhancedDial() {
			super(0, 100);
			
			setMouseHandlerMode(MouseHandlerMode.LEFT_TO_RIGHT_AND_DOWN_TO_UP);
			
			addMouseListener(new MouseAdapter() {
				public void
				mouseClicked(MouseEvent e) {
					if(e.getButton() == e.BUTTON3) {
						setValue(getMaximum() / 2);
						return;
					}
					
					if(e.getButton() != e.BUTTON1) return;
					
					if(e.getClickCount() < 2) return;
					setValue(getValueByPoint(e.getPoint()));
				}
			});
			
			addChangeListener(new ChangeListener() {
				public void
				stateChanged(ChangeEvent e) { setVolume(); }
			});
		}
	}
	
	private final EventHandler eventHandler = new EventHandler();
	
	private EventHandler
	getHandler() { return eventHandler; }
	
	private class EventHandler implements SamplerChannelListener {
		/**
		 * Invoked when changes are made to a sampler channel.
		 * @param e A <code>SamplerChannelEvent</code> instance
		 * containing event information.
		 */
		public void
		channelChanged(SamplerChannelEvent e) { updateChannelInfo(); }
	
		/**
		 * Invoked when the number of active disk streams has changed.
		 * @param e A <code>SamplerChannelEvent</code> instance
		 * containing event information.
		 */
		public void
		streamCountChanged(SamplerChannelEvent e) {
			screen.updateStreamCount(getModel().getStreamCount());
		}
	
		/**
		 * Invoked when the number of active voices has changed.
		 * @param e A <code>SamplerChannelEvent</code> instance
		 * containing event information.
		 */
		public void
		voiceCountChanged(SamplerChannelEvent e) {
			screen.updateVoiceCount(getModel().getVoiceCount());
		}
	}
	
	
	private class PowerButton extends PixmapToggleButton
			implements ActionListener, PropertyChangeListener {
		
		PowerButton() {
			super(Res.gfxPowerOff, Res.gfxPowerOn);
		
			setSelected(true);
			addActionListener(this);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			if(!mainPane.isAnimated()) {
				CC.getSamplerModel().removeBackendChannel(getChannelId());
				return;
			}
			
			String s = JXCollapsiblePane.ANIMATION_STATE_KEY;
			mainPane.addPropertyChangeListener(s, this);
			mainPane.setCollapsed(true);
		}
		
		public void
		propertyChange(PropertyChangeEvent e) {
			if(e.getNewValue() == "collapsed") {
				CC.getSamplerModel().removeBackendChannel(getChannelId());
			}
		}
		
		public boolean
		contains(int x, int y) { return (x - 11)*(x - 11) + (y - 11)*(y - 11) < 71; }
	}
	
	private class MuteButton extends PixmapButton implements ActionListener {
		MuteButton() {
			super(Res.gfxMuteOff);
			//setDisabledIcon(Res.gfxMuteSoloDisabled);
			setDisabledIcon (
				SubstanceImageCreator.makeTransparent(this, Res.gfxMuteOff, 0.4)
			);
			addActionListener(this);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			SamplerChannel sc = getChannelInfo();
			boolean b = true;
		
			/*
			 * Changing the mute button icon now instead of
			 * leaving the work to the notification mechanism of the LinuxSampler.
			 */
			if(sc.isMuted() && !sc.isMutedBySolo()) {
				b = false;
				boolean hasSolo = CC.getSamplerModel().hasSoloChannel();
			
				if(sc.isSoloChannel() || !hasSolo) setIcon(Res.gfxMuteOff);
				else setIcon(Res.gfxMutedBySolo);
			} else setIcon(Res.gfxMuteOn);
			
			Channel.this.getModel().setBackendMute(b);
		}
		
		public boolean
		contains(int x, int y) { return (x > 5 && x < 23) && (y > 5 && y < 16); }
	}
	
	private class SoloButton extends PixmapButton implements ActionListener {
		SoloButton() {
			super(Res.gfxSoloOff);
			//setDisabledIcon(Res.gfxMuteSoloDisabled);
			setDisabledIcon (
				SubstanceImageCreator.makeTransparent(this, Res.gfxSoloOff, 0.4)
			);
			addActionListener(this);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			SamplerChannel sc = getChannelInfo();
			boolean b = !sc.isSoloChannel();
		
			/*
			 * Changing the solo button icon (and related) now instead of
			 * leaving the work to the notification mechanism of the LinuxSampler.
			 */
			if(b) {
				setIcon(Res.gfxSoloOn);
				if(sc.isMutedBySolo()) btnMute.setIcon(Res.gfxMuteOff);
			} else {
				setIcon(Res.gfxSoloOff);
				if(!sc.isMuted() && CC.getSamplerModel().getSoloChannelCount() > 1)
					btnMute.setIcon(Res.gfxMutedBySolo);
			}
		
			Channel.this.getModel().setBackendSolo(b);
		}
		
		public boolean
		contains(int x, int y) { return (x > 5 && x < 23) && (y > 5 && y < 16); }
	}
	
	private class OptionsButton extends PixmapToggleButton implements ActionListener {
		OptionsButton() {
			super(Res.gfxOptionsOff, Res.gfxOptionsOn);
			setRolloverIcon(Res.gfxOptionsOffRO);
			this.setRolloverSelectedIcon(Res.gfxOptionsOnRO);
			addActionListener(this);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			showOptionsPane(isSelected());
			
			String s;
			if(isSelected()) s = i18n.getButtonLabel("OptionsButton.ttHideOptions");
			else s = i18n.getButtonLabel("OptionsButton.ttShowOptions");
			
			setToolTipText(s);
		}
		
		private void
		showOptionsPane(boolean show) {
			optionsPane.setCollapsed(!show);
		}
		
		public boolean
		contains(int x, int y) { return super.contains(x, y) & y < 13; }
	}
}

class ChannelPane extends PixmapPane {
	ChannelPane() {
		super(Res.gfxChannel);
		setPixmapInsets(new Insets(3, 3, 3, 3));
	}
}

class ChannelScreen extends PixmapPane {
	private final Channel channel;
	private JButton btnInstr = new ScreenButton(i18n.getButtonLabel("ChannelScreen.btnInstr"));
	
	private final JButton btnFxSends =
		new ScreenButton(i18n.getButtonLabel("ChannelScreen.btnFxSends"));
	
	private final JButton btnEngine
		= new ScreenButton(i18n.getButtonLabel("ChannelScreen.btnEngine"));
	
	private final JPopupMenu menuEngines = new JPopupMenu();
	
	private final JLabel lVolume = new Label();
	private final JLabel lStreams = new Label("--");
	private final JLabel lVoices = new Label("--");
	
	private InformationDialog fxSendsDlg = null;
	
	private Dimension dimVolume;
	
	class Label extends JLabel {
		Label() { this(""); }
		
		Label(String s) {
			super(s);
			setFont(Res.fontScreen);
			setForeground(new java.awt.Color(0xFFA300));
		}
	}
	
	ChannelScreen(final Channel channel) {
		super(Res.gfxChannelScreen);
		setPixmapInsets(new Insets(6, 6, 6, 6));
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		this.channel = channel;
		
		setOpaque(false);
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		btnInstr.setAlignmentX(CENTER_ALIGNMENT);
		btnInstr.setRolloverEnabled(false);
		btnInstr.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
		
		add(btnInstr);
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.setAlignmentX(CENTER_ALIGNMENT);
		p.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		
		btnFxSends.setToolTipText(i18n.getButtonLabel("ChannelScreen.btnFxSends.tt"));
		btnFxSends.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				if(fxSendsDlg != null && fxSendsDlg.isVisible()) {
					fxSendsDlg.toFront();
					return;
				}
				FxSendsPane p = new FxSendsPane(channel.getModel());
				int id = channel.getModel().getChannelId();
				fxSendsDlg = new InformationDialog(CC.getMainFrame(), p);
				fxSendsDlg.setTitle(i18n.getLabel("FxSendsDlg.title", id));
				fxSendsDlg.setModal(false);
				fxSendsDlg.showCloseButton(false);
				fxSendsDlg.setVisible(true);
			}
		});
		
		p.add(btnFxSends);
		
		p.add(Box.createRigidArea(new Dimension(6, 0)));
		
		btnEngine.setIcon(Res.iconEngine12);
		p.add(btnEngine);
		//p.add(new Label("|"));
		
		//p.add(Box.createRigidArea(new Dimension(6, 0)));
		
		//p.add(btnReset);
		
		p.add(Box.createGlue());
		
		p.add(lStreams);
		p.add(new Label("/"));
		p.add(lVoices);
		
		p.add(Box.createRigidArea(new Dimension(12, 0)));
		
		lVolume.setIcon(Res.iconVolume14);
		lVolume.setAlignmentX(RIGHT_ALIGNMENT);
		updateVolumeInfo(100);
		dimVolume = lVolume.getPreferredSize();
		p.add(lVolume);
		p.setPreferredSize(new Dimension(250, p.getPreferredSize().height));
		p.setMinimumSize(p.getPreferredSize());
		p.setMaximumSize(p.getPreferredSize());
		
		//btnInstr.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		p.setOpaque(false);
		add(p);
		
		
		setPreferredSize(new Dimension(270, 48));
		setMinimumSize(getPreferredSize());
		setMaximumSize(getPreferredSize());
		
		createEngineMenu();
		installListeners();
	}
	
	private void
	createEngineMenu() {
		for(final SamplerEngine engine : CC.getSamplerModel().getEngines()) {
			JMenuItem mi = new JMenuItem(engine.getDescription());
			
			mi.addActionListener(new ActionListener() {
				public void
				actionPerformed(ActionEvent e) {
					channel.getModel().setBackendEngineType(engine.getName());
				}
			});
			
			menuEngines.add(mi);
		}
	}
	
	private void
	installListeners() {
		btnInstr.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { loadInstrument(); }
		});
		
		btnEngine.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				int y = btnEngine.getHeight() + 1;
				menuEngines.show(btnEngine, 0, y);
			}
		});
	}
	
	private void
	loadInstrument() {
		JSInstrumentChooser dlg = new JSInstrumentChooser(CC.getMainFrame());
		dlg.setVisible(true);
		
		if(!dlg.isCancelled()) {
			SamplerChannelModel m = channel.getModel();
			m.loadBackendInstrument(dlg.getInstrumentFile(), dlg.getInstrumentIndex());
		}
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
	
		if(sc.getEngine() != null) {
			String s = sc.getEngine().getDescription();
			if(!s.equals(btnEngine.getText())) btnEngine.setText(s);
		}
		
	}
	
	protected void
	updateVolumeInfo(int volume) {
		lVolume.setText(i18n.getLabel("ChannelScreen.volume", volume));
		lVolume.setMinimumSize(dimVolume);
		lVolume.setPreferredSize(dimVolume);
		
	}
	
	/**
	 * Updates the number of active disk streams.
	 * @param count The new number of active disk streams.
	 */
	protected void
	updateStreamCount(int count) {
		Dimension d = lStreams.getPreferredSize();
		lStreams.setText(count == 0 ? "--" : String.valueOf(count));
		d = JuifeUtils.getUnionSize(d, lStreams.getPreferredSize());
		lStreams.setMinimumSize(d);
		lStreams.setPreferredSize(d);
		lStreams.setMaximumSize(d);
	}
	
	/**
	 * Updates the number of active voices.
	 * @param count The new number of active voices.
	 */
	protected void
	updateVoiceCount(int count) {
		Dimension d = lVoices.getPreferredSize();
		lVoices.setText(count == 0 ? "--" : String.valueOf(count));
		d = JuifeUtils.getUnionSize(d, lVoices.getPreferredSize());
		lVoices.setMinimumSize(d);
		lVoices.setPreferredSize(d);
		lVoices.setMaximumSize(d);
	}
	
	class FxSendsPane extends JSFxSendsPane {
		FxSendsPane(SamplerChannelModel model) {
			super(model);
			
			actionAddFxSend.putValue(Action.SMALL_ICON, Res.iconNew16);
			actionRemoveFxSend.putValue(Action.SMALL_ICON, Res.iconDelete16);
		}
		
		protected JToolBar
		createToolBar() {
			JToolBar tb = new JToolBar();
			Dimension d = new Dimension(Short.MAX_VALUE, tb.getPreferredSize().height);
			tb.setMaximumSize(d);
			tb.setFloatable(false);
			tb.setAlignmentX(JPanel.RIGHT_ALIGNMENT);
			
			tb.add(new ToolbarButton(actionAddFxSend));
			tb.add(new ToolbarButton(actionRemoveFxSend));
		
			return tb;
		}
	}
	
	static class ScreenButton extends JButton {
		ScreenButton(String s) {
			super(s);
			setContentAreaFilled(false);
			setFocusPainted(false);
			setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
			setMargin(new Insets(0, 0, 0, 0));
			
			putClientProperty (
				SubstanceLookAndFeel.BUTTON_NO_MIN_SIZE_PROPERTY, Boolean.TRUE
			);
			
			putClientProperty (
				SubstanceLookAndFeel.BUTTON_PAINT_NEVER_PROPERTY, Boolean.TRUE
			);
			
			putClientProperty(SubstanceLookAndFeel.FLAT_PROPERTY, Boolean.TRUE);
			
			FadeConfigurationManager.getInstance().disallowFades(FadeKind.ROLLOVER, this);
			
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			setFont(Res.fontScreen);
			setForeground(new java.awt.Color(0xFFA300));
		}
	}
}

class ChannelOptions extends JXCollapsiblePane {
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
	
	ChannelOptions(final Channel channel) {
		setAnimated(false);
		setCollapsed(true);
		setAnimated(preferences().getBoolProperty(ANIMATED));
		
		preferences().addPropertyChangeListener(ANIMATED, new PropertyChangeListener() {
			public void
			propertyChange(PropertyChangeEvent e) {
				setAnimated(preferences().getBoolProperty(ANIMATED));
			}
		});
		
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
		cbMidiChannel.setPreferredSize(new Dimension(80, 18));
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
		
		cbInstrumentMap.setPreferredSize(new Dimension(126, 18));
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
		
		setContentPane(bgp);
		
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
		
		CC.getSamplerModel().addSamplerChannelListListener(getHandler());
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
	
	private final Handler handler = new Handler();
	
	private Handler
	getHandler() { return handler; }
	
	private class Handler implements MidiDeviceListListener, ListListener<AudioDeviceModel>,
					SamplerChannelListListener, MidiDeviceListener {
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
		
		/**
		 * Invoked when a new sampler channel is created.
		 * @param e A <code>SamplerChannelListEvent</code>
		 * instance providing the event information.
		 */
		public void
		channelAdded(SamplerChannelListEvent e) { }
	
		/**
		 * Invoked when a sampler channel is removed.
		 * @param e A <code>SamplerChannelListEvent</code>
		 * instance providing the event information.
		 */
		public void
		channelRemoved(SamplerChannelListEvent e) {
			// Some cleanup when the channel is removed.
			if(e.getChannelModel().getChannelId() == channel.getChannelId()) {
				SamplerModel sm = CC.getSamplerModel();
				
				sm.removeMidiDeviceListListener(getHandler());
				sm.removeAudioDeviceListListener(getHandler());
				sm.removeMidiInstrumentMapListListener(mapListListener);
				sm.removeSamplerListener(samplerListener);
				sm.removeSamplerChannelListListener(getHandler());
				
				if(midiDevice != null) {
					midiDevice.removeMidiDeviceListener(getHandler());
				}
			}
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
