/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005, 2006 Grigor Kirilov Iliev
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sf.juife.Dial;
import net.sf.juife.JuifeUtils;
import net.sf.juife.TitleBar;

import org.jsampler.AudioDeviceModel;
import org.jsampler.CC;
import org.jsampler.MidiDeviceModel;
import org.jsampler.SamplerChannelModel;
import org.jsampler.SamplerModel;

import org.jsampler.event.AudioDeviceListEvent;
import org.jsampler.event.AudioDeviceListListener;
import org.jsampler.event.MidiDeviceListEvent;
import org.jsampler.event.MidiDeviceListListener;
import org.jsampler.event.SamplerChannelAdapter;
import org.jsampler.event.SamplerChannelEvent;
import org.jsampler.event.SamplerChannelListener;

import org.jsampler.task.RemoveChannel;

import org.linuxsampler.lscp.AudioOutputDevice;
import org.linuxsampler.lscp.MidiInputDevice;
import org.linuxsampler.lscp.MidiPort;
import org.linuxsampler.lscp.SamplerChannel;
import org.linuxsampler.lscp.SamplerEngine;

import static org.jsampler.view.fantasia.FantasiaI18n.i18n;


/**
 *
 * @author Grigor Iliev
 */
public class Channel extends org.jsampler.view.JSChannel {
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
		JPanel p = new JPanel();
		p.setName("Channel");
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		
		//p.add(Box.createRigidArea(new Dimension(3, 0)));
		
		btnPower.setAlignmentY(JPanel.TOP_ALIGNMENT);
		
		TitleBar tb = new TitleBar();
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
		p2.add(new JLabel(Res.iconMuteTitle));
		p2.add(btnMute);
		p2.add(new JLabel(Res.iconSoloTitle));
		p2.add(btnSolo);
		
		p.add(p2);
		
		p.add(createVSeparator());
		
		p2 = new JPanel();
		p2.setOpaque(false);
		p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));
		p2.setAlignmentY(JPanel.TOP_ALIGNMENT);
		p2.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
		JLabel l = new JLabel(Res.iconVolumeTitle);
		l.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		l.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
		p2.add(l);
		dialVolume.setDialPixmap(Res.iconVolumeDial, 30, 330);
		dialVolume.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		p2.add(dialVolume);
		p.add(p2);
		
		p.add(createVSeparator());
		
		p2 = new JPanel();
		p2.setOpaque(false);
		p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));
		p2.setAlignmentY(JPanel.TOP_ALIGNMENT);
		p2.setBorder(BorderFactory.createEmptyBorder(27, 0, 0, 0));
		l = new JLabel(Res.iconOptionsTitle);
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
		add(p);
		add(optionsPane);
		
		setOpaque(true);
		
		getModel().addSamplerChannelListener(getHandler());
		
		updateChannelInfo();
	}
	
	private JPanel
	createVSeparator() {
		JPanel p = new JPanel();
		p.setName("VSeparator");
		p.setOpaque(false);
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
	expandChannel() { if(!btnOptions.isSelected()) btnOptions.doClick(); }
	
	
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
		getModel().setVolume(volume);
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
		
		if(sc.isSoloChannel()) btnSolo.setIcon(Res.iconSoloOn);
		else btnSolo.setIcon(Res.iconSoloOff);
		
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
		if(channel.isMutedBySolo()) btnMute.setIcon(Res.iconMutedBySolo);
		else if(channel.isMuted()) btnMute.setIcon(Res.iconMuteOn);
		else btnMute.setIcon(Res.iconMuteOff);
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
	
	
	private class PowerButton extends PixmapToggleButton implements ActionListener {
		PowerButton() {
			super(Res.iconPowerOff, Res.iconPowerOn);
		
			setSelected(true);
			addActionListener(this);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			CC.getTaskQueue().add(new RemoveChannel(getChannelID()));
		}
		
		public boolean
		contains(int x, int y) { return (x - 11)*(x - 11) + (y - 11)*(y - 11) < 71; }
	}
	
	private class MuteButton extends PixmapButton implements ActionListener {
		MuteButton() {
			super(Res.iconMuteOff);
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
			
				if(sc.isSoloChannel() || !hasSolo) setIcon(Res.iconMuteOff);
				else setIcon(Res.iconMutedBySolo);
			} else setIcon(Res.iconMuteOn);
			
			Channel.this.getModel().setMute(b);
		}
		
		public boolean
		contains(int x, int y) { return (x > 5 && x < 23) && (y > 5 && y < 16); }
	}
	
	private class SoloButton extends PixmapButton implements ActionListener {
		SoloButton() {
			super(Res.iconSoloOff);
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
				setIcon(Res.iconSoloOn);
				if(sc.isMutedBySolo()) btnMute.setIcon(Res.iconMuteOff);
			} else {
				setIcon(Res.iconSoloOff);
				if(!sc.isMuted() && CC.getSamplerModel().getSoloChannelCount() > 1)
					btnMute.setIcon(Res.iconMutedBySolo);
			}
		
			Channel.this.getModel().setSolo(b);
		}
		
		public boolean
		contains(int x, int y) { return (x > 5 && x < 23) && (y > 5 && y < 16); }
	}
	
	private class OptionsButton extends PixmapToggleButton implements ActionListener {
		OptionsButton() {
			super(Res.iconOptionsOff, Res.iconOptionsOn);
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
			optionsPane.setVisible(show);
			MainFrame.repack(CC.getMainFrame());
		}
		
		public boolean
		contains(int x, int y) { return y < 13; }
	}
}

class ChannelScreen extends JPanel {
	private final Channel channel;
	private JButton btnInstr = new ScreenButton(i18n.getButtonLabel("ChannelScreen.btnInstr"));
	private JButton btnReset = new ScreenButton(i18n.getButtonLabel("ChannelScreen.btnReset"));
	private JButton btnDuplicate =
		new ScreenButton(i18n.getButtonLabel("ChannelScreen.btnDuplicate"));
	
	private final JLabel lVolume = new JLabel();
	private final JLabel lStreams = new JLabel("--");
	private final JLabel lVoices = new JLabel("--");
	
	ChannelScreen(Channel channel) {
		this.channel = channel;
		
		setName("ChannelScreen");
		setOpaque(true);
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		btnInstr.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		btnInstr.setAlignmentX(CENTER_ALIGNMENT);
		
		add(btnInstr);
		
		JPanel p = new JPanel();
		p.setOpaque(false);
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.setAlignmentX(CENTER_ALIGNMENT);
		p.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		//lVolume.setFont(lVolume.getFont().deriveFont(java.awt.Font.PLAIN));
		
		p.add(btnDuplicate);
		
		p.add(Box.createRigidArea(new Dimension(6, 0)));
		
		p.add(new JLabel("|"));
		
		p.add(Box.createRigidArea(new Dimension(6, 0)));
		
		p.add(btnReset);
		
		p.add(Box.createGlue());
		
		p.add(lStreams);
		p.add(new JLabel("/"));
		p.add(lVoices);
		
		p.add(Box.createRigidArea(new Dimension(12, 0)));
		
		lVolume.setAlignmentX(RIGHT_ALIGNMENT);
		p.add(lVolume);
		p.setPreferredSize(new Dimension(250, p.getPreferredSize().height));
		p.setMinimumSize(p.getPreferredSize());
		p.setMaximumSize(p.getPreferredSize());
		
		add(p);
		
		
		setPreferredSize(new Dimension(270, 48));
		setMinimumSize(getPreferredSize());
		setMaximumSize(getPreferredSize());
		
		installListeners();
	}
	
	private void
	installListeners() {
		btnInstr.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { loadInstrument(); }
		});
	
		btnReset.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { channel.getModel().resetChannel(); }
		});
		
		btnDuplicate.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { channel.getModel().duplicateChannel(); }
		});
	}

	private void
	loadInstrument() {
		InstrumentChooser dlg = new InstrumentChooser(CC.getMainFrame());
		dlg.setVisible(true);
		
		if(!dlg.isCancelled()) {
			SamplerChannelModel m = channel.getModel();
			m.loadInstrument(dlg.getFileName(), dlg.getInstrumentIndex());
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
	
		
		
	}
	
	protected void
	updateVolumeInfo(int volume) {
		lVolume.setText(i18n.getLabel("ChannelScreen.volume", volume));
		
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
	
	static class ScreenButton extends JButton {
		ScreenButton(String s) {
			super(s);
			setContentAreaFilled(false);
			setFocusPainted(false);
			setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
			setMargin(new Insets(0, 0, 0, 0));
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}
	}
}

class ChannelOptions extends JPanel {
	private final Channel channel;
	
	private final JComboBox cbMidiDevice = new JComboBox();
	private final JComboBox cbMidiPort = new JComboBox();
	private final JComboBox cbMidiChannel = new JComboBox();
	private final JComboBox cbEngine = new JComboBox();
	private final JComboBox cbAudioDevice = new JComboBox();
	
	private boolean update = false;
	
	ChannelOptions(Channel channel) {
		this.channel = channel;
		
		setName("ChannelOptions");
		setVisible(false);
		setBorder(BorderFactory.createEmptyBorder(5, 4, 5, 4));
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		setPreferredSize(new Dimension(420, 44));
		setMinimumSize(getPreferredSize());
		setMaximumSize(getPreferredSize());
		
		JPanel p = new JPanel();
		p.setOpaque(true);
		p.setBorder(BorderFactory.createEmptyBorder(3, 4, 3, 4));
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		JLabel l = new JLabel(Res.iconMidiInputTitle);
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
		
		cbMidiPort.setPreferredSize(new Dimension(67, 18));
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
		p.add(p2);
		
		add(p);
		
		add(Box.createRigidArea(new Dimension(4, 0)));
		
		p = new JPanel();
		p.setOpaque(true);
		p.setBorder(BorderFactory.createEmptyBorder(3, 4, 3, 4));
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		l = new JLabel(Res.iconEngineTitle);
		l.setAlignmentX(LEFT_ALIGNMENT);
		l.setAlignmentX(LEFT_ALIGNMENT);
		p.add(l);
		
		p.add(Box.createRigidArea(new Dimension(0, 3)));
		
		o = cbEngine.getRenderer();
		if(o instanceof JLabel) ((JLabel )o).setHorizontalAlignment(SwingConstants.CENTER);
		
		for(SamplerEngine e : CC.getSamplerModel().getEngines()) cbEngine.addItem(e);
		cbEngine.setPreferredSize(new Dimension(125, 18));
		cbEngine.setMinimumSize(cbEngine.getPreferredSize());
		cbEngine.setMaximumSize(cbEngine.getPreferredSize());
		cbEngine.setAlignmentX(LEFT_ALIGNMENT);
		p.add(cbEngine);
		
		add(p);
		
		add(Box.createRigidArea(new Dimension(4, 0)));
		
		p = new JPanel();
		p.setOpaque(true);
		p.setBorder(BorderFactory.createEmptyBorder(3, 4, 3, 4));
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		l = new JLabel(Res.iconAudioOutputTitle);
		l.setAlignmentX(LEFT_ALIGNMENT);
		l.setAlignmentX(LEFT_ALIGNMENT);
		p.add(l);
		
		p.add(Box.createRigidArea(new Dimension(0, 3)));
		
		o = cbAudioDevice.getRenderer();
		if(o instanceof JLabel) ((JLabel )o).setHorizontalAlignment(SwingConstants.RIGHT);
		
		cbAudioDevice.setPreferredSize(new Dimension(61, 18));
		cbAudioDevice.setMinimumSize(cbAudioDevice.getPreferredSize());
		cbAudioDevice.setMaximumSize(cbAudioDevice.getPreferredSize());
		cbAudioDevice.setAlignmentX(LEFT_ALIGNMENT);
		p.add(cbAudioDevice);
		
		add(p);
		
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
		
		cbEngine.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { setEngineType(); }
		});
		
		cbAudioDevice.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { setAudioDevice(); }
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
		
		MidiDeviceModel mm = sm.getMidiDeviceModel(sc.getMidiInputDevice());
		AudioDeviceModel am = sm.getAudioDeviceModel(sc.getAudioOutputDevice());
		
		if(isUpdate()) CC.getLogger().warning("Unexpected update state!");
		
		setUpdate(true);
		
		try {
			cbMidiDevice.setSelectedItem(mm == null ? null : mm.getDeviceInfo());
			
			cbEngine.setSelectedItem(sc.getEngine());
			
			cbAudioDevice.setSelectedItem(am == null ? null : am.getDeviceInfo());
		} catch(Exception x) {
			CC.getLogger().log(Level.WARNING, "Unkown error", x);
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
		
			for(MidiDeviceModel m : sm.getMidiDeviceModels())
				cbMidiDevice.addItem(m.getDeviceInfo());
		
			MidiDeviceModel mm = sm.getMidiDeviceModel(sc.getMidiInputDevice());
			cbMidiDevice.setSelectedItem(mm == null ? null : mm.getDeviceInfo());
		} catch(Exception x) {
			CC.getLogger().log(Level.WARNING, "Unkown error", x);
		}
		 
		setUpdate(false);
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
		
			for(AudioDeviceModel m : sm.getAudioDeviceModels()) 
				cbAudioDevice.addItem(m.getDeviceInfo());
		
			AudioDeviceModel am = sm.getAudioDeviceModel(sc.getAudioOutputDevice());
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
			if(mid != null) channel.getModel().setMidiInputDevice(mid.getDeviceID());
			return;
		}
		
		cbMidiPort.removeAllItems();
		
		if(mid == null) {
			cbMidiPort.setEnabled(false);
			
			cbMidiChannel.setSelectedItem(null);
			cbMidiChannel.setEnabled(false);
		} else {
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
		
		channel.getModel().setMidiInputPort(cbMidiPort.getSelectedIndex());
	}
	
	private void
	setMidiChannel() {
		if(isUpdate()) return;
		
		Object o = cbMidiChannel.getSelectedItem();
		if(o == null) return;
		String s = o.toString();
		
		int c = s.equals("All") ? -1 : Integer.parseInt(s.substring(8)) - 1;
		
		channel.getModel().setMidiInputChannel(c);
	}
	
	/** Invoked when the user selects an engine. */
	private void
	setEngineType() {
		Object oldEngine = channel.getModel().getChannelInfo().getEngine();
		SamplerEngine newEngine = (SamplerEngine)cbEngine.getSelectedItem();
		
		if(oldEngine != null) { if(oldEngine.equals(newEngine)) return; }
		else if(newEngine == null) return;
		
		channel.getModel().setEngineType(newEngine.getName());
		
	}
	
	private void
	setAudioDevice() {
		if(isUpdate()) return;
		AudioOutputDevice dev = (AudioOutputDevice)cbAudioDevice.getSelectedItem();
		if(dev != null) channel.getModel().setAudioOutputDevice(dev.getDeviceID());
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
	
	private class Handler implements MidiDeviceListListener, AudioDeviceListListener {
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
		deviceAdded(AudioDeviceListEvent e) {
			cbAudioDevice.addItem(e.getAudioDeviceModel().getDeviceInfo());
		}
	
		/**
		 * Invoked when an audio device is removed.
		 * @param e An <code>AudioDeviceListEvent</code>
		 * instance providing the event information.
		 */
		public void
		deviceRemoved(AudioDeviceListEvent e) {
			cbAudioDevice.removeItem(e.getAudioDeviceModel().getDeviceInfo());
		}
	}
}
