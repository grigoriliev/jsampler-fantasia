/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005 Grigor Kirilov Iliev
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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.net.URL;

import java.util.Vector;

import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JToggleButton;

import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sf.juife.JuifeUtils;

import org.jsampler.CC;
import org.jsampler.AudioDeviceModel;
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

import org.linuxsampler.lscp.AudioOutputDevice;
import org.linuxsampler.lscp.MidiInputDevice;
import org.linuxsampler.lscp.MidiPort;
import org.linuxsampler.lscp.SamplerChannel;
import org.linuxsampler.lscp.SamplerEngine;

import static org.jsampler.view.classic.ClassicI18n.i18n;


/**
 *
 * @author Grigor Iliev
 */
public class Channel extends org.jsampler.view.JSChannel {
	private final static ImageIcon iconMuteOn;
	private final static ImageIcon iconMuteOff;
	private final static ImageIcon iconMutedBySolo;
	
	private final static ImageIcon iconSoloOn;
	private final static ImageIcon iconSoloOff;
	
	private final static ImageIcon iconShowProperties;
	private final static ImageIcon iconHideProperties;
	
	private static Border borderSelected;
	private static Border borderDeselected;
	
	private static Color borderColor;
	
	private final static Vector<PropertyChangeListener> propertyChangeListeners
		= new Vector<PropertyChangeListener>();
	
	static {
		String path = "org/jsampler/view/classic/res/icons/";
		URL url = ClassLoader.getSystemClassLoader().getResource(path + "mute_on.png");
		iconMuteOn = new ImageIcon(url);
		
		url = ClassLoader.getSystemClassLoader().getResource(path + "mute_off.png");
		iconMuteOff = new ImageIcon(url);
		
		url = ClassLoader.getSystemClassLoader().getResource(path + "muted_by_solo.png");
		iconMutedBySolo = new ImageIcon(url);
		
		url = ClassLoader.getSystemClassLoader().getResource(path + "solo_on.png");
		iconSoloOn = new ImageIcon(url);
		
		url = ClassLoader.getSystemClassLoader().getResource(path + "solo_off.png");
		iconSoloOff = new ImageIcon(url);
		
		url = ClassLoader.getSystemClassLoader().getResource(path + "Back16.gif");
		iconShowProperties = new ImageIcon(url);
		
		url = ClassLoader.getSystemClassLoader().getResource(path + "Down16.gif");
		iconHideProperties = new ImageIcon(url);
		
		if(ClassicPrefs.getCustomChannelBorderColor())
			setBorderColor(ClassicPrefs.getChannelBorderColor());
		else setBorderColor(ClassicPrefs.getDefaultChannelBorderColor());
		
		borderSelected = new LineBorder(getBorderColor(), 2, true);
		borderDeselected = BorderFactory.createEmptyBorder(2, 2, 2, 2);
	}
	
	/**
	 * Registers the specified listener for receiving property change events.
	 * @param l The <code>PropertyChangeListener</code> to register.
	 */
	public void
	addPropertyChangeListener(PropertyChangeListener l) {
		propertyChangeListeners.add(l);
	}
	
	/**
	 * Removes the specified listener.
	 * @param l The <code>PropertyChangeListener</code> to remove.
	 */
	public void
	removePropertyChangeListener(PropertyChangeListener l) {
		propertyChangeListeners.remove(l);
	}
	
	/**
	 * Gets the border color that is used when the channel is selected.
	 * @return The border color that is used when the channel is selected.
	 */
	public static Color
	getBorderColor() { return borderColor; }
	
	/**
	 * Sets the border color to be used when the channel is selected.
	 * @param c The border color to be used when the channel is selected.
	 */
	public static void
	setBorderColor(Color c) {
		if(borderColor != null && borderColor.getRGB() == c.getRGB()) return;
		
		Color oldColor = borderColor;
		borderColor = c;
		borderSelected = new LineBorder(getBorderColor(), 2, true);
		firePropertyChanged("borderColor", oldColor, borderColor);
	}
	
	private static void
	firePropertyChanged(String propertyName, Object oldValue, Object newValue) {
		PropertyChangeEvent e =
			new PropertyChangeEvent(Channel.class, propertyName, oldValue, newValue);
		
		for(PropertyChangeListener l : propertyChangeListeners) l.propertyChange(e);
	}
	
	
	private final JPanel mainPane = new JPanel();
	private final ChannelProperties propertiesPane;
	private final JButton btnInstr = new JButton(i18n.getLabel("Channel.btnInstr"));
	private final JButton btnMute = new JButton();
	private final JButton btnSolo = new JButton();
	private final JSlider slVolume = new JSlider(0, 100);
	private final JLabel lVolume = new JLabel();
	private final JLabel lStreams = new JLabel("--");
	private final JLabel lVoices = new JLabel("--");
	private final JToggleButton btnProperties = new JToggleButton();
	
	private final EventHandler eventHandler = new EventHandler();
	
	private static int count = 2;
	
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
		
		setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		
		mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		
		//setToolTipText(" Channel: " + String.valueOf(getChannelID()) + " ");
		
		Dimension d = btnInstr.getPreferredSize();
		btnInstr.setMaximumSize(new Dimension(Short.MAX_VALUE, d.height));
		p.add(btnInstr);
		p.add(Box.createRigidArea(new Dimension(6, 0)));
		
		lStreams.setHorizontalAlignment(JLabel.CENTER);
		lVoices.setHorizontalAlignment(JLabel.CENTER);
		
		JPanel statPane = new JPanel();
		statPane.setBorder(BorderFactory.createLoweredBevelBorder());
		statPane.setLayout(new BoxLayout(statPane, BoxLayout.X_AXIS));
		statPane.add(Box.createRigidArea(new Dimension(6, 0)));
		statPane.add(lStreams);
		statPane.add(new JLabel("/"));
		statPane.add(lVoices);
		statPane.add(Box.createRigidArea(new Dimension(6, 0)));
		
		p.add(statPane);
		
		p.add(Box.createRigidArea(new Dimension(6, 0)));
		
		btnMute.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		p.add(btnMute);
		p.add(Box.createRigidArea(new Dimension(6, 0)));
		
		btnSolo.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		p.add(btnSolo);
		p.add(Box.createRigidArea(new Dimension(6, 0)));
		
		JPanel volumePane = new JPanel();
		volumePane.setBorder(BorderFactory.createLoweredBevelBorder());
		volumePane.setLayout(new BoxLayout(volumePane, BoxLayout.X_AXIS));
		volumePane.add(Box.createRigidArea(new Dimension(6, 0)));
		
		d = slVolume.getPreferredSize();
		slVolume.setMaximumSize(new Dimension(d.width > 300 ? d.width : 300, d.height));
		volumePane.add(slVolume);
		
		lVolume.setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6));
		lVolume.setHorizontalAlignment(lVolume.RIGHT);
		
		// We use this to set the size of the lVolume that will be used in setVolume()
		// to prevent the frequent resizing of lVolume
		lVolume.setText("100%");
		
		volumePane.add(lVolume);
		
		p.add(volumePane);
		p.add(Box.createRigidArea(new Dimension(6, 0)));
		
		btnProperties.setContentAreaFilled(false);
		btnProperties.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		btnProperties.setIcon(iconShowProperties);
		btnProperties.setSelectedIcon(iconHideProperties);
		p.add(btnProperties);
		
		mainPane.add(p);
		
		propertiesPane = new ChannelProperties(model);
		propertiesPane.setBorder(BorderFactory.createEmptyBorder(0, 3, 3, 3));
		propertiesPane.setVisible(false);
		mainPane.add(propertiesPane);
		add(mainPane);
		
		d = getPreferredSize();
		setMaximumSize(new Dimension(getMaximumSize().width, d.height));
		
		getModel().addSamplerChannelListener(getHandler());
		
		btnInstr.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { loadInstrument(); }
		});
		
		btnMute.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { changeMute(); }
		});
		
		btnSolo.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { changeSolo(); }
		});
		
		slVolume.addChangeListener(new ChangeListener() {
			public void
			stateChanged(ChangeEvent e) { setVolume(); }
		});
		
		btnProperties.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				showProperties(btnProperties.isSelected());
				
				String s;
				if(btnProperties.isSelected()) {
					s = i18n.getButtonLabel("Channel.ttHideProps");
				} else {
					s = i18n.getButtonLabel("Channel.ttShowProps");
				}
				
				btnProperties.setToolTipText(s);
			}
		});
		
		btnProperties.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		String s;
		if(btnProperties.isSelected()) s = i18n.getButtonLabel("Channel.ttHideProps");
		else s = i18n.getButtonLabel("Channel.ttShowProps");
		
		btnProperties.setToolTipText(s);
		
		addPropertyChangeListener(getHandler());
		
		updateChannelInfo();
	}
	
	private EventHandler
	getHandler() { return eventHandler; }
	
	private class EventHandler implements SamplerChannelListener, PropertyChangeListener {
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
			updateStreamCount(getModel().getStreamCount());
		}
	
		/**
		 * Invoked when the number of active voices has changed.
		 * @param e A <code>SamplerChannelEvent</code> instance
		 * containing event information.
		 */
		public void
		voiceCountChanged(SamplerChannelEvent e) {
			updateVoiceCount(getModel().getVoiceCount());
		}
		
		public void
		propertyChange(PropertyChangeEvent e) {
			if(e.getPropertyName() == "borderColor") setSelected(isSelected());
		}
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
		if(select)  mainPane.setBorder(borderSelected);
		else mainPane.setBorder(borderDeselected);
		
		selected = select;
	}
	
	/** Hides the channel properties. */
	public void
	collapseChannel() { if(btnProperties.isSelected()) btnProperties.doClick(); }
	
	/** Shows the channel properties. */
	public void
	expandChannel() { if(!btnProperties.isSelected()) btnProperties.doClick(); }
	
	/**
	 * Updates the channel settings. This method is invoked when changes to the
	 * channel were made.
	 */
	private void
	updateChannelInfo() {
		SamplerChannel sc = getChannelInfo();
		
		int status = sc.getInstrumentStatus();
		if(status >= 0 && status < 100) {
			btnInstr.setText(i18n.getLabel("Channel.loadingInstrument", status));
		} else {
			if(sc.getInstrumentName() != null) btnInstr.setText(sc.getInstrumentName());
			else btnInstr.setText(i18n.getLabel("Channel.btnInstr"));
		}
		
		updateMute(sc);
		
		if(sc.isSoloChannel()) btnSolo.setIcon(iconSoloOn);
		else btnSolo.setIcon(iconSoloOff);
		
		slVolume.setValue((int)(sc.getVolume() * 100));
		
		boolean b = sc.getEngine() != null;
		slVolume.setEnabled(b);
		btnSolo.setEnabled(b);
		btnMute.setEnabled(b);
	}
	
	/** Invoked when the user clicks the mute button. */
	private void
	changeMute() {
		SamplerChannel sc = getChannelInfo();
		boolean b = true;
		
		/*
		 * Changing the mute button icon now instead of
		 * leaving the work to the notification mechanism of the LinuxSampler.
		 */
		if(sc.isMuted() && !sc.isMutedBySolo()) {
			b = false;
			boolean hasSolo = CC.getSamplerModel().hasSoloChannel();
			
			if(sc.isSoloChannel() || !hasSolo) btnMute.setIcon(iconMuteOff);
			else btnMute.setIcon(iconMutedBySolo);
		} else btnMute.setIcon(iconMuteOn);
		
		getModel().setMute(b);
	}
	
	/** Invoked when the user clicks the solo button. */
	private void
	changeSolo() {
		SamplerChannel sc = getChannelInfo();
		boolean b = !sc.isSoloChannel();
		
		/*
		 * Changing the solo button icon (and related) now instead of
		 * leaving the work to the notification mechanism of the LinuxSampler.
		 */
		if(b) {
			btnSolo.setIcon(iconSoloOn);
			if(sc.isMutedBySolo()) btnMute.setIcon(iconMuteOff);
		} else {
			btnSolo.setIcon(iconSoloOff);
			if(!sc.isMuted() && CC.getSamplerModel().getSoloChannelCount() > 1)
				btnMute.setIcon(iconMutedBySolo);
		}
		
		getModel().setSolo(b);
	}
	
	/** Invoked when the user changes the volume */
	private void
	setVolume() {
		updateVolume();
		
		if(slVolume.getValueIsAdjusting()) return;
		
		int vol = (int)(getChannelInfo().getVolume() * 100);
		
		if(vol == slVolume.getValue()) return;
		
		/*
		 * If the model's volume is not equal to the slider
		 * value we assume that the change is due to user input.
		 * So we must update the volume at the backend too.
		 */
		float volume = slVolume.getValue();
		volume /= 100;
		getModel().setVolume(volume);
	}
	
	private void
	updateVolume() {
		int volume = slVolume.getValue();
		slVolume.setToolTipText(i18n.getLabel("Channel.volume", volume));
		
		setVolumeLabel(volume);
		
		
	}
	
	private void
	setVolumeLabel(int volume) {
		Dimension d = lVolume.getPreferredSize();
		lVolume.setText(String.valueOf(volume) + '%');
		d = JuifeUtils.getUnionSize(d, lVolume.getPreferredSize());
		lVolume.setMinimumSize(d);
		lVolume.setPreferredSize(d);
		lVolume.setMaximumSize(d);
	}
	
	/**
	 * Updates the mute button with the proper icon regarding to information obtained
	 * from <code>channel</code>.
	 * @param channel A <code>SamplerChannel</code> instance containing the new settings
	 * for this channel.
	 */
	private void
	updateMute(SamplerChannel channel) {
		if(channel.isMutedBySolo()) btnMute.setIcon(iconMutedBySolo);
		else if(channel.isMuted()) btnMute.setIcon(iconMuteOn);
		else btnMute.setIcon(iconMuteOff);
	}
	
	/**
	 * Updates the number of active disk streams.
	 * @param count The new number of active disk streams.
	 */
	private void
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
	private void
	updateVoiceCount(int count) {
		Dimension d = lVoices.getPreferredSize();
		lVoices.setText(count == 0 ? "--" : String.valueOf(count));
		d = JuifeUtils.getUnionSize(d, lVoices.getPreferredSize());
		lVoices.setMinimumSize(d);
		lVoices.setPreferredSize(d);
		lVoices.setMaximumSize(d);
	}
	
	private void
	showProperties(boolean show) {propertiesPane.setVisible(show); }
	
	private void
	loadInstrument() {
		InstrumentChooser dlg = new InstrumentChooser(CC.getMainFrame());
		dlg.setVisible(true);
		
		if(!dlg.isCancelled()) {
			getModel().loadInstrument(dlg.getFileName(), dlg.getInstrumentIndex());
		}
	}
}

class ChannelProperties extends JPanel {
	private final static ImageIcon iconAudioProps;
	
	static {
		String path = "org/jsampler/view/classic/res/icons/";
		URL url = ClassLoader.getSystemClassLoader().getResource(path + "Import16.gif");
		iconAudioProps = new ImageIcon(url);
	}
	
	private final JLabel lMidiDevice =
		new JLabel(i18n.getLabel("ChannelProperties.lMidiDevice"));
	private final JLabel lMidiPort =
		new JLabel(i18n.getLabel("ChannelProperties.lMidiPort"));
	private final JLabel lMidiChannel =
		new JLabel(i18n.getLabel("ChannelProperties.lMidiChannel"));
	
	private final JLabel lAudioDevice =
		new JLabel(i18n.getLabel("ChannelProperties.lAudioDevice"));
	
	private final JComboBox cbEngines = new JComboBox();
	
	private final JComboBox cbMidiDevice = new JComboBox();
	private final JComboBox cbMidiPort = new JComboBox();
	private final JComboBox cbMidiChannel = new JComboBox();
	private final JComboBox cbAudioDevice = new JComboBox();
	
	private final JButton btnAudioProps = new JButton(iconAudioProps);
	
	private SamplerChannelModel channelModel = null;
	
	private boolean update = false;
	
	/**
	 * Creates a new instance of <code>ChannelProperties</code> using the specified non-null
	 * channel model.
	 * @param model The model to be used by this channel properties pane.
	 */
	ChannelProperties(SamplerChannelModel model) {
		channelModel = model;
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		add(new JSeparator());
		
		JPanel enginesPane = new JPanel();
		
		for(SamplerEngine e : CC.getSamplerModel().getEngines()) cbEngines.addItem(e);
		
		//cbEngines.setMaximumSize(cbEngines.getPreferredSize());
		
		enginesPane.add(cbEngines);
		String s = i18n.getLabel("ChannelProperties.enginesPane");
		enginesPane.setBorder(BorderFactory.createTitledBorder(s));
		
		JPanel devicesPane = new JPanel();
		devicesPane.setLayout(new BoxLayout(devicesPane, BoxLayout.X_AXIS));
		
		devicesPane.add(Box.createRigidArea(new Dimension(3, 0)));
		
		devicesPane.add(createMidiPane());
		
		devicesPane.add(Box.createRigidArea(new Dimension(3, 0)));
		
		devicesPane.add(enginesPane);
		
		devicesPane.add(Box.createRigidArea(new Dimension(3, 0)));
		
		JPanel audioPane = createAudioPane();
		Dimension d = audioPane.getPreferredSize();
		d.height = Short.MAX_VALUE;
		
		audioPane.setMaximumSize(d);
		devicesPane.add(audioPane);
		
		add(devicesPane);
		add(Box.createRigidArea(new Dimension(0, 6)));
		
		add(new JSeparator());
		
		cbMidiChannel.addItem("All");
		for(int i = 1; i <= 16; i++) cbMidiChannel.addItem(String.valueOf(i));
		
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
		
		cbEngines.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { setEngineType(); }
		});
		
		cbAudioDevice.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { setAudioDevice(); }
		});
		
		getModel().addSamplerChannelListener(new SamplerChannelAdapter() {
			public void
			channelChanged(SamplerChannelEvent e) { updateChannelProperties(); }
		});
		
		CC.getSamplerModel().addMidiDeviceListListener(getHandler());
		CC.getSamplerModel().addAudioDeviceListListener(getHandler());
		
		btnAudioProps.setToolTipText(i18n.getLabel("ChannelProperties.routing"));
		btnAudioProps.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				SamplerChannel c = getModel().getChannelInfo();
				new ChannelOutputRoutingDlg(CC.getMainFrame(), c).setVisible(true);
			
			}
		});
		
		
		
		updateMidiDevices();
		updateAudioDevices();
		updateChannelProperties();
	}

	private JPanel
	createMidiPane() {
		JPanel midiPane = new JPanel();
		
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		
		midiPane.setLayout(gridbag);
		
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(3, 3, 3, 3);
		gridbag.setConstraints(lMidiDevice, c);
		midiPane.add(lMidiDevice);
		
		c.gridx = 1;
		c.gridy = 0;
		gridbag.setConstraints(cbMidiDevice, c);
		midiPane.add(cbMidiDevice);
		
		c.gridx = 2;
		c.gridy = 0;
		gridbag.setConstraints(lMidiPort, c);
		midiPane.add(lMidiPort);
		
		c.gridx = 4;
		c.gridy = 0;
		gridbag.setConstraints(lMidiChannel, c);
		midiPane.add(lMidiChannel);
		
		c.gridx = 5;
		c.gridy = 0;
		gridbag.setConstraints(cbMidiChannel, c);
		midiPane.add(cbMidiChannel);
		
		c.gridx = 3;
		c.gridy = 0;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(3, 3, 3, 3);
		gridbag.setConstraints(cbMidiPort, c);
		midiPane.add(cbMidiPort);
		
		String s = i18n.getLabel("ChannelProperties.midiPane");
		midiPane.setBorder(BorderFactory.createTitledBorder(s));
		
		return midiPane;
	}
	
	private JPanel
	createAudioPane() {
		JPanel audioPane = new JPanel();
		
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		
		audioPane.setLayout(gridbag);
		
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(3, 3, 3, 3);
		gridbag.setConstraints(lAudioDevice, c);
		audioPane.add(lAudioDevice);
		
		c.gridx = 1;
		c.gridy = 0;
		gridbag.setConstraints(cbAudioDevice, c);
		audioPane.add(cbAudioDevice);
		
		btnAudioProps.setMargin(new Insets(0, 0, 0, 0));
		c.gridx = 2;
		c.gridy = 0;
		c.insets = new Insets(3, 9, 3, 3);
		gridbag.setConstraints(btnAudioProps, c);
		audioPane.add(btnAudioProps);
		
		String s = i18n.getLabel("ChannelProperties.audioPane");
		audioPane.setBorder(BorderFactory.createTitledBorder(s));
		
		return audioPane;
	}
	
	/**
	 * Gets the model that is currently used by this channel properties pane.
	 * @return model The <code>SamplerChannelModel</code> instance
	 * that provides information about the channel whose settings are
	 * represented by this channel properties pane.
	 */
	public SamplerChannelModel
	getModel() { return channelModel; }
	
	/**
	 * Updates the channel settings. This method is invoked when changes to the
	 * channel were made.
	 */
	private void
	updateChannelProperties() {
		SamplerModel sm = CC.getSamplerModel();
		SamplerChannel sc = getModel().getChannelInfo();
		
		MidiDeviceModel mm = sm.getMidiDeviceModel(sc.getMidiInputDevice());
		AudioDeviceModel am = sm.getAudioDeviceModel(sc.getAudioOutputDevice());
		
		if(isUpdate()) CC.getLogger().warning("Unexpected update state!");
		
		setUpdate(true);
		
		try {
			cbMidiDevice.setSelectedItem(mm == null ? null : mm.getDeviceInfo());
			
			cbEngines.setSelectedItem(sc.getEngine());
			
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
		SamplerChannel sc = getModel().getChannelInfo();
		
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
		SamplerChannel sc = getModel().getChannelInfo();
		
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
			if(mid != null) getModel().setMidiInputDevice(mid.getDeviceID());
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
			
			int p = getModel().getChannelInfo().getMidiInputPort();
			cbMidiPort.setSelectedItem(p >= 0 && p < ports.length ? ports[p] : null);
			
			cbMidiChannel.setEnabled(true);
			int c = getModel().getChannelInfo().getMidiInputChannel();
			cbMidiChannel.setSelectedItem(c == -1 ? "All" : String.valueOf(c + 1));
		}
		
		
	}
	
	private void
	setMidiPort() {
		if(isUpdate()) return;
		
		getModel().setMidiInputPort(cbMidiPort.getSelectedIndex());
	}
	
	private void
	setMidiChannel() {
		if(isUpdate()) return;
		
		Object o = cbMidiChannel.getSelectedItem();
		if(o == null) return;
		
		int c = o.toString().equals("All") ? -1 : Integer.parseInt(o.toString()) - 1;
		
		getModel().setMidiInputChannel(c);
	}
	
	/** Invoked when the user selects an engine. */
	private void
	setEngineType() {
		Object oldEngine = getModel().getChannelInfo().getEngine();
		SamplerEngine newEngine = (SamplerEngine)cbEngines.getSelectedItem();
		
		if(oldEngine != null) { if(oldEngine.equals(newEngine)) return; }
		else if(newEngine == null) return;
		
		getModel().setEngineType(newEngine.getName());
		
	}
	
	private void
	setAudioDevice() {
		if(isUpdate()) return;
		AudioOutputDevice dev = (AudioOutputDevice)cbAudioDevice.getSelectedItem();
		if(dev != null) getModel().setAudioOutputDevice(dev.getDeviceID());
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
