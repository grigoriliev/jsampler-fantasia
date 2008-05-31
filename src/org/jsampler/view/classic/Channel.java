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

package org.jsampler.view.classic;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.awt.datatransfer.Transferable;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.net.URL;

import java.text.NumberFormat;

import java.util.Vector;
import java.util.logging.Level;

import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.TransferHandler;

import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sf.juife.InformationDialog;
import net.sf.juife.JuifeUtils;

import org.jsampler.AudioDeviceModel;
import org.jsampler.CC;
import org.jsampler.HF;
import org.jsampler.OrchestraInstrument;
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

import org.linuxsampler.lscp.AudioOutputDevice;
import org.linuxsampler.lscp.MidiInputDevice;
import org.linuxsampler.lscp.MidiPort;
import org.linuxsampler.lscp.SamplerChannel;
import org.linuxsampler.lscp.SamplerEngine;

import static org.jsampler.view.classic.ClassicI18n.i18n;
import static org.jsampler.view.classic.ClassicPrefs.preferences;
import static org.jsampler.view.std.StdPrefs.*;


/**
 *
 * @author Grigor Iliev
 */
public class Channel extends org.jsampler.view.JSChannel {
	private final static ImageIcon iconEdit;
	
	private final static ImageIcon iconMuteOn;
	private final static ImageIcon iconMuteOff;
	private final static ImageIcon iconMutedBySolo;
	
	private final static ImageIcon iconSoloOn;
	private final static ImageIcon iconSoloOff;
	
	private final static ImageIcon iconShowProperties;
	private final static ImageIcon iconHideProperties;
	
	private static Border borderSelected;
	private static Border borderHighlighted;
	private static Border borderDeselected;
	
	private static Color chnColor;
	private static Color borderColor;
	private static Color borderHighlightedColor;
	private static Color chnSelectedColor;
	private static Color chnHighlightedColor;
	
	private final static Vector<PropertyChangeListener> propertyChangeListeners
		= new Vector<PropertyChangeListener>();
	
	
	private static NumberFormat numberFormat = NumberFormat.getInstance();
	
	static {
		numberFormat.setMaximumFractionDigits(1);
		
		iconEdit = new ImageIcon(Channel.class.getResource("res/icons/edit.png"));
		
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
		
		iconHideProperties = Res.iconDown16;
		
		if(ClassicPrefs.getCustomChannelBorderColor())
			setBorderColor(ClassicPrefs.getChannelBorderColor());
		else setBorderColor(ClassicPrefs.getDefaultChannelBorderColor());
		
		if(ClassicPrefs.getCustomChannelBorderHlColor())
			setBorderHighlightedColor(ClassicPrefs.getChannelBorderHlColor());
		else setBorderHighlightedColor(ClassicPrefs.getDefaultChannelBorderHlColor());
		
		borderSelected = new LineBorder(getBorderColor(), 2, true);
		borderHighlighted = new LineBorder(getBorderHighlightedColor(), 2, true);
		borderDeselected = BorderFactory.createEmptyBorder(2, 2, 2, 2);
		
		chnColor = new JPanel().getBackground();
		
		if(ClassicPrefs.getCustomSelectedChannelBgColor()) {
			chnSelectedColor = ClassicPrefs.getSelectedChannelBgColor();
		} else {
			int r = chnColor.getRed() - 14 < 0 ? 0 : chnColor.getRed() - 14;
			int g = chnColor.getGreen() - 8 < 0 ? 0 : chnColor.getGreen() - 8;
			int b = chnColor.getBlue() - 3 < 0 ? 0 : chnColor.getBlue() - 3;
		
			chnSelectedColor = new Color(r, g, b);
		}
		
		/*r = r + 5 > 255 ? 255 : r + 5;
		g = g + 4 > 255 ? 255 : g + 4;
		b = b + 1 > 255 ? 255 : b + 1;*/
		
		chnHighlightedColor = new Color(chnColor.getRGB());
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
	
	/**
	 * Gets the border color that is used when the mouse pointer is over a channel.
	 * @return The border color that is used when the mouse pointer is over a channel.
	 */
	public static Color
	getBorderHighlightedColor() { return borderHighlightedColor; }
	
	/**
	 * Sets the border color to be used when the mouse pointer is over a channel.
	 * @param c The border color to be used when the mouse pointer is over a channel.
	 */
	public static void
	setBorderHighlightedColor(Color c) {
		Color oldColor = borderHighlightedColor;
		if(oldColor != null && oldColor.getRGB() == c.getRGB()) return;
		
		borderHighlightedColor = c;
		borderHighlighted = new LineBorder(getBorderHighlightedColor(), 2, true);
		firePropertyChanged("borderHighlightedColor", oldColor, borderHighlightedColor);
	}
	
	/**
	 * Gets the background color that is used when a channel is selected.
	 * @return The background color that is used when a channel is selected.
	 */
	public static Color
	getSelectedChannelBgColor() { return chnSelectedColor; }
	
	/**
	 * Sets the background color that is used when a channel is selected.
	 * @param c The background color to be used when a channel is selected.
	 */
	public static void
	setSelectedChannelBgColor(Color c) {
		Color oldColor = chnSelectedColor;
		if(oldColor != null && oldColor.getRGB() == c.getRGB()) return;
		
		chnSelectedColor = c;
		firePropertyChanged("selectedChannelBgColor", oldColor, chnSelectedColor);
	}
	
	/**
	 * Gets the background color that is used when the mouse pointer is over a channel.
	 * @return The background color that is used when the mouse pointer is over a channel.
	 */
	public static Color
	getHighlightedChannelBgColor() { return chnHighlightedColor; }
	
	/**
	 * Sets the background color to be used when the mouse pointer is over a channel.
	 * @param c The background color to be used when the mouse pointer is over a channel.
	 */
	public static void
	setHighlightedChannelBgColor(Color c) {
		Color oldColor = chnHighlightedColor;
		if(oldColor != null && oldColor.getRGB() == c.getRGB()) return;
		
		chnHighlightedColor = c;
		firePropertyChanged("highlightedChannelBgColor", oldColor, chnHighlightedColor);
	}
	
	private static void
	firePropertyChanged(String propertyName, Object oldValue, Object newValue) {
		PropertyChangeEvent e =
			new PropertyChangeEvent(Channel.class, propertyName, oldValue, newValue);
		
		for(PropertyChangeListener l : propertyChangeListeners) l.propertyChange(e);
	}
	
	
	private final JPanel mainPane = new JPanel();
	private final ChannelProperties propertiesPane;
	private final JButton btnInstr = new InstrumentButton(i18n.getLabel("Channel.btnInstr"));
	private final Action actInstr;
	private final JButton btnEdit = new JButton(iconEdit);
	private final JButton btnMute = new JButton();
	private final JButton btnSolo = new JButton();
	private final JSlider slVolume = new JSlider(0, 100);
	private final JLabel lVolume = new JLabel();
	private final JLabel lVolImg = new JLabel(Res.iconVolume16);
	private final JLabel lStreams = new JLabel("--");
	private final JLabel lVoices = new JLabel("--");
	private final JToggleButton btnProperties = new JToggleButton();
	
	private static int count = 2;
	
	private boolean selected = false;
	private boolean mouseOver = false;
	
	
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
		addMouseListener(getHandler());
		addHierarchyListener(getHandler());
		
		JPanel p = new JPanel();
		p.setOpaque(false);
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		
		setToolTipText(i18n.getLabel("Channel.tt", getModel().getChannelId()));
		
		Dimension d = btnInstr.getPreferredSize();
		btnInstr.setMaximumSize(new Dimension(Short.MAX_VALUE, d.height));
		p.add(btnInstr);
		p.add(Box.createRigidArea(new Dimension(6, 0)));
		
		btnEdit.setToolTipText(i18n.getLabel("Channel.btnEdit.tt"));
		btnEdit.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		p.add(btnEdit);
		p.add(Box.createRigidArea(new Dimension(6, 0)));
		
		lStreams.setHorizontalAlignment(JLabel.CENTER);
		lVoices.setHorizontalAlignment(JLabel.CENTER);
		
		JPanel statPane = new JPanel();
		statPane.setOpaque(false);
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
		volumePane.setOpaque(false);
		volumePane.setBorder(BorderFactory.createLoweredBevelBorder());
		volumePane.setLayout(new BoxLayout(volumePane, BoxLayout.X_AXIS));
		volumePane.add(Box.createRigidArea(new Dimension(6, 0)));
		
		volumePane.add(lVolImg);
		volumePane.add(Box.createRigidArea(new Dimension(1, 0)));
		
		d = slVolume.getPreferredSize();
		slVolume.setMaximumSize(new Dimension(d.width > 300 ? d.width : 300, d.height));
		slVolume.setOpaque(false);
		volumePane.add(slVolume);
		
		lVolume.setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6));
		lVolume.setHorizontalAlignment(lVolume.RIGHT);
		
		// We use this to set the size of the lVolume
		// to prevent the frequent resizing of lVolume component
		if(CC.getViewConfig().isMeasurementUnitDecibel()) {
			lVolume.setText("-30.0dB");
		} else {
			lVolume.setText("100%");
		}
		lVolume.setPreferredSize(lVolume.getPreferredSize());
		
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
		
		int i = preferences().getIntProperty(MAXIMUM_CHANNEL_VOLUME);
		slVolume.setMaximum(i);
		String mcv = MAXIMUM_CHANNEL_VOLUME;
		preferences().addPropertyChangeListener(mcv, new PropertyChangeListener() {
			public void
			propertyChange(PropertyChangeEvent e) {
				int j = preferences().getIntProperty(MAXIMUM_CHANNEL_VOLUME);
				slVolume.setMaximum(j);
			}
		});
		
		String vmud = VOL_MEASUREMENT_UNIT_DECIBEL;
		preferences().addPropertyChangeListener(vmud, new PropertyChangeListener() {
			public void
			propertyChange(PropertyChangeEvent e) {
				boolean b;
				b = preferences().getBoolProperty(VOL_MEASUREMENT_UNIT_DECIBEL);
				// We use this to set the size of the lVolume
				// to prevent the frequent resizing of lVolume component
				lVolume.setPreferredSize(null);
				if(b) lVolume.setText("-30.0dB");
				else lVolume.setText("100%");
				lVolume.setPreferredSize(lVolume.getPreferredSize());
				///////
				updateVolume();
			}
		});
		
		getModel().addSamplerChannelListener(getHandler());
		
		actInstr = new AbstractAction() {
			public void
			actionPerformed(ActionEvent e) {
				if(actInstr.isEnabled()) loadInstrument();
			}
		};
		
		btnInstr.addActionListener(actInstr);
		
		btnEdit.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				CC.getSamplerModel().editBackendInstrument(getChannelId());
			}
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
	
	public class InstrumentButton extends JButton {
		private boolean dragging = false;
		
		InstrumentButton(String s) {
			super(s);
			
			setTransferHandler(new TransferHandler("instrument"));
			
			addMouseListener(new MouseAdapter() {
				public void
				mouseExited(MouseEvent e) {
					if(!dragging) return;
					
					int b1 = e.BUTTON1_DOWN_MASK;
					if((e.getModifiersEx() & b1) != b1) return;
					
					actInstr.setEnabled(false);
					doClick(0);
					actInstr.setEnabled(true);
					
					JComponent c = (JComponent)e.getSource();
					TransferHandler handler = c.getTransferHandler();
					handler.exportAsDrag(c, e, TransferHandler.COPY);
				}
				
				public void
				mouseReleased(MouseEvent e) { dragging = false; }
			});
			
			addMouseMotionListener(new MouseMotionAdapter() {
				public void
				mouseDragged(MouseEvent e) { dragging = true; }
			});
		}
		
		public String
		getInstrument() {
			SamplerChannel sc = Channel.this.getChannelInfo();
			
			if(sc.getInstrumentName() == null || sc.getInstrumentStatus() < 0)
				return null;
			
			OrchestraInstrument instr = new OrchestraInstrument();
			instr.setName(sc.getInstrumentName());
			instr.setInstrumentIndex(sc.getInstrumentIndex());
			instr.setFilePath(sc.getInstrumentFile());
			return instr.getDnDString();
		}
		
		public void setInstrument(String instr) {
			if(!OrchestraInstrument.isDnDString(instr)) return;
			
			String[] args = instr.split("\n");
			if(args.length < 6) return;
			
			try {
				int idx = Integer.parseInt(args[5]);
				Channel.this.getModel().loadBackendInstrument(args[4], idx);
			} catch(Exception x) {
				CC.getLogger().log(Level.INFO, HF.getErrorMessage(x), x);
			}
		}
	}
	
	private final EventHandler eventHandler = new EventHandler();
	
	private EventHandler
	getHandler() { return eventHandler; }
	
	private class EventHandler extends MouseAdapter implements SamplerChannelListener,
							PropertyChangeListener, HierarchyListener {
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
			if (
				e.getPropertyName() == "borderColor" ||
				e.getPropertyName() == "borderHighlightedColor" ||
				e.getPropertyName() == "selectedChannelBgColor" ||
				e.getPropertyName() == "highlightedChannelBgColor"
			) {
				updateColors(isSelected());
			}
		}
		
		public void
		mouseEntered(MouseEvent e) {
			mouseOver = true;
			updateColors(isSelected());
		}
		
		public void
		mouseExited(MouseEvent e) {
			if(getMousePosition(true) != null) return;
			
			mouseOver = false;
			updateColors(isSelected());
		}
		
		
	
		/** Called when the hierarchy has been changed. */
		public void
		hierarchyChanged(HierarchyEvent e) {
			if((e.getChangeFlags() & e.SHOWING_CHANGED) == e.SHOWING_CHANGED) {
				if(getMousePosition() == null) mouseExited(null);
				else mouseEntered(null);
			}
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
		updateColors(select);
		
		selected = select;
	}
	
	/**
	 * Updates the channel background and border colors.
	 * @param selected Specifies the selection state of this channel.
	 */
	private void
	updateColors(boolean selected) {
		if(selected) {
			mainPane.setBorder(borderSelected);
			mainPane.setBackground(chnSelectedColor);
		} else {
			if(mouseOver) {
				mainPane.setBorder(borderHighlighted);
				mainPane.setBackground(chnHighlightedColor);
			} else {
				mainPane.setBorder(borderDeselected);
				mainPane.setBackground(chnColor);
			}
		}
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
		} else if(status == -1) {
			btnInstr.setText(i18n.getLabel("Channel.btnInstr"));
		} else if(status < -1) {
			 btnInstr.setText(i18n.getLabel("Channel.errorLoadingInstrument"));
		} else {
			if(sc.getInstrumentName() != null) btnInstr.setText(sc.getInstrumentName());
			else btnInstr.setText(i18n.getLabel("Channel.btnInstr"));
		}
		
		boolean b = status == 100;
		if(btnEdit.isEnabled() != b) btnEdit.setEnabled(b);
		
		updateMuteIcon(sc);
		
		if(sc.isSoloChannel()) btnSolo.setIcon(iconSoloOn);
		else btnSolo.setIcon(iconSoloOff);
		
		slVolume.setValue((int)(sc.getVolume() * 100));
		
		b = sc.getEngine() != null;
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
		
		getModel().setBackendMute(b);
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
		
		getModel().setBackendSolo(b);
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
		getModel().setBackendVolume(volume);
	}
	
	private void
	updateVolume() {
		int volume = slVolume.getValue();
		
		if(CC.getViewConfig().isMeasurementUnitDecibel()) {
			String dB = numberFormat.format(HF.percentsToDecibels(volume));
			slVolume.setToolTipText(i18n.getLabel("Channel.volumeDecibels", dB));
			lVolImg.setToolTipText(i18n.getLabel("Channel.volumeDecibels", dB));
			lVolume.setText(dB + "dB");
		} else {
			slVolume.setToolTipText(i18n.getLabel("Channel.volume", volume));
			lVolImg.setToolTipText(i18n.getLabel("Channel.volume", volume));
			lVolume.setText(String.valueOf(volume) + '%');
		}
	}
	
	/**
	 * Updates the mute button with the proper icon regarding to information obtained
	 * from <code>channel</code>.
	 * @param channel A <code>SamplerChannel</code> instance containing the new settings
	 * for this channel.
	 */
	private void
	updateMuteIcon(SamplerChannel channel) {
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
		JSInstrumentChooser dlg = new JSInstrumentChooser(CC.getMainFrame());
		dlg.setVisible(true);
		
		if(dlg.isCancelled()) return;
		
		SamplerEngine engine = getChannelInfo().getEngine();
		if(dlg.getEngine() != null) {
			if(engine == null || !dlg.getEngine().equals(engine.getName()));
				getModel().setBackendEngineType(dlg.getEngine());
		}
		
		int idx = dlg.getInstrumentIndex();
		getModel().loadBackendInstrument(dlg.getInstrumentFile(), idx);
		
	}
}

class ChannelProperties extends JPanel {
	private final JLabel lMidiDevice =
		new JLabel(i18n.getLabel("ChannelProperties.lMidiDevice"));
	private final JLabel lMidiPort =
		new JLabel(i18n.getLabel("ChannelProperties.lMidiPort"));
	private final JLabel lMidiChannel =
		new JLabel(i18n.getLabel("ChannelProperties.lMidiChannel"));
	
	private final JLabel lInstrumentMap =
		new JLabel(i18n.getLabel("ChannelProperties.lInstrumentMap"));
	
	private final JLabel lAudioDevice =
		new JLabel(i18n.getLabel("ChannelProperties.lAudioDevice"));
	
	private final JComboBox cbEngines = new JComboBox();
	
	private final JComboBox cbInstrumentMap = new JComboBox();
	private final JComboBox cbMidiDevice = new JComboBox();
	private final JComboBox cbMidiPort = new JComboBox();
	private final JComboBox cbMidiChannel = new JComboBox();
	private final JComboBox cbAudioDevice = new JComboBox();
	
	private final JButton btnFxSends = new JButton(Res.iconFxSends22);
	private final JButton btnAudioProps = new JButton(Res.iconAudioProps16);
	private InformationDialog fxSendsDlg = null;
	
	private SamplerChannelModel channelModel = null;
	private MidiDeviceModel midiDevice = null;
	
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
	
	/**
	 * Creates a new instance of <code>ChannelProperties</code> using the specified non-null
	 * channel model.
	 * @param model The model to be used by this channel properties pane.
	 */
	ChannelProperties(SamplerChannelModel model) {
		channelModel = model;
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setOpaque(false);
		
		add(new JSeparator());
		
		JPanel enginesPane = createEnginePane();		
		
		JPanel devicesPane = new JPanel();
		devicesPane.setOpaque(false);
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
		
		int map = getModel().getChannelInfo().getMidiInstrumentMapId();
		cbInstrumentMap.setSelectedItem(CC.getSamplerModel().getMidiInstrumentMapById(map));
		if(cbInstrumentMap.getSelectedItem() == null) {
			if(map == -1) cbInstrumentMap.setSelectedItem(noMap);
			else if(map == -2) {
				cbInstrumentMap.setSelectedItem(defaultMap);
			}
		}
		
		updateCbInstrumentMapToolTipText();
		
		if(getModel().getChannelInfo().getEngine() == null) {
			cbInstrumentMap.setEnabled(false);
		}
		
		cbInstrumentMap.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { updateInstrumentMap(); }
		});
		
		CC.getSamplerModel().addMidiDeviceListListener(getHandler());
		CC.getSamplerModel().addAudioDeviceListListener(getHandler());
		CC.getSamplerModel().addSamplerChannelListListener(getHandler());
		CC.getSamplerModel().addMidiInstrumentMapListListener(mapListListener);
		
		btnAudioProps.setToolTipText(i18n.getLabel("ChannelProperties.routing"));
		btnAudioProps.setEnabled(false);
		btnAudioProps.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				SamplerChannel c = getModel().getChannelInfo();
				new JSChannelOutputRoutingDlg(CC.getMainFrame(), c).setVisible(true);
			
			}
		});
		
		btnFxSends.setToolTipText(i18n.getButtonLabel("ChannelProperties.btnFxSends"));
		btnFxSends.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				if(fxSendsDlg != null && fxSendsDlg.isVisible()) {
					fxSendsDlg.toFront();
					return;
				}
				
				FxSendsPane p = new FxSendsPane(getModel());
				int id = getModel().getChannelId();
				fxSendsDlg = new InformationDialog(CC.getMainFrame(), p);
				fxSendsDlg.setTitle(i18n.getLabel("FxSendsDlg.title", id));
				fxSendsDlg.setModal(false);
				fxSendsDlg.showCloseButton(false);
				fxSendsDlg.setVisible(true);
			}
		});
		
		updateMidiDevices();
		updateAudioDevices();
		updateChannelProperties();
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
	
	private JPanel
	createEnginePane() {
		for(SamplerEngine e : CC.getSamplerModel().getEngines()) cbEngines.addItem(e);
		
		cbEngines.setMaximumSize(cbEngines.getPreferredSize());
		
		JPanel p = new JPanel();
		p.setOpaque(false);
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.add(cbEngines);
		p.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		
		//enginesPane.add(Box.createGlue());
		JPanel enginesPane = new JPanel();
		enginesPane.setOpaque(false);
		enginesPane.setLayout(new BorderLayout());
		enginesPane.add(p, BorderLayout.SOUTH);
		//enginesPane.add(Box.createRigidArea(new Dimension(0, 3)));
		
		String s = i18n.getLabel("ChannelProperties.enginesPane");
		enginesPane.setBorder(BorderFactory.createTitledBorder(s));
		Dimension d = new Dimension(enginesPane.getPreferredSize().width, Short.MAX_VALUE);
		enginesPane.setMaximumSize(d);
		return enginesPane;
	}
	
	private JPanel
	createMidiPane() {
		JPanel midiPane = new JPanel();
		midiPane.setOpaque(false);
		
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		
		midiPane.setLayout(gridbag);
		
		c.gridx = 1;
		c.gridy = 0;
		c.insets = new Insets(0, 3, 3, 3);
		gridbag.setConstraints(lMidiDevice, c);
		midiPane.add(lMidiDevice);
		
		c.gridx = 2;
		c.gridy = 0;
		gridbag.setConstraints(lMidiPort, c);
		midiPane.add(lMidiPort);
		
		c.gridx = 3;
		c.gridy = 0;
		gridbag.setConstraints(lMidiChannel, c);
		midiPane.add(lMidiChannel);
		
		c.gridx = 4;
		c.gridy = 0;
		c.insets = new Insets(0, 10, 3, 3);
		gridbag.setConstraints(lInstrumentMap, c);
		midiPane.add(lInstrumentMap);
		
		btnFxSends.setMargin(new Insets(0, 0, 0, 0));
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 2;
		c.insets = new Insets(0, 5, 0, 8);
		gridbag.setConstraints(btnFxSends, c);
		midiPane.add(btnFxSends);
		
		c.gridx = 1;
		c.gridy = 1;
		c.gridheight = 1;
		c.insets = new Insets(0, 4, 4, 3);
		c.fill = GridBagConstraints.HORIZONTAL;
		gridbag.setConstraints(cbMidiDevice, c);
		midiPane.add(cbMidiDevice);
		
		c.gridx = 3;
		c.gridy = 1;
		gridbag.setConstraints(cbMidiChannel, c);
		midiPane.add(cbMidiChannel);
		
		c.gridx = 2;
		c.gridy = 1;
		gridbag.setConstraints(cbMidiPort, c);
		midiPane.add(cbMidiPort);
		
		c.gridx = 4;
		c.gridy = 1;
		c.weightx = 1.0;
		c.insets = new Insets(0, 10, 3, 3);
		gridbag.setConstraints(cbInstrumentMap, c);
		midiPane.add(cbInstrumentMap);
		
		String s = i18n.getLabel("ChannelProperties.midiPane");
		TitledBorder border = BorderFactory.createTitledBorder(s);
		//border.setTitlePosition(border.TOP);
		midiPane.setBorder(border);
		return midiPane;
	}
	
	private JPanel
	createAudioPane() {
		JPanel audioPane = new JPanel();
		audioPane.setOpaque(false);
		
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		
		audioPane.setLayout(gridbag);
		
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(0, 3, 3, 3);
		gridbag.setConstraints(lAudioDevice, c);
		audioPane.add(lAudioDevice);
		
		c.gridx = 0;
		c.gridy = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		gridbag.setConstraints(cbAudioDevice, c);
		audioPane.add(cbAudioDevice);
		
		btnAudioProps.setMargin(new Insets(0, 0, 0, 0));
		c.gridx = 1;
		c.gridy = 1;
		c.fill = GridBagConstraints.NONE;
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
	
	
	private void
	updateInstrumentMap() {
		updateCbInstrumentMapToolTipText();
		
		int id = getModel().getChannelInfo().getMidiInstrumentMapId();
		Object o = cbInstrumentMap.getSelectedItem();
		if(o == null && id == -1) return;
		
		int cbId;
		if(o == null || o == noMap) cbId = -1;
		else if(o == defaultMap) cbId = -2;
		else cbId = ((MidiInstrumentMap)o).getMapId();
		
		if(cbId == id) return;
		
		channelModel.setBackendMidiInstrumentMap(cbId);
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
	 * Updates the channel settings. This method is invoked when changes to the
	 * channel were made.
	 */
	private void
	updateChannelProperties() {
		SamplerModel sm = CC.getSamplerModel();
		SamplerChannel sc = getModel().getChannelInfo();
		
		MidiDeviceModel mm = sm.getMidiDeviceById(sc.getMidiInputDevice());
		AudioDeviceModel am = sm.getAudioDeviceById(sc.getAudioOutputDevice());
		
		if(isUpdate()) CC.getLogger().warning("Unexpected update state!");
		
		setUpdate(true);
		
		try {
			cbMidiDevice.setSelectedItem(mm == null ? null : mm.getDeviceInfo());
			
			cbEngines.setSelectedItem(sc.getEngine());
			
			cbAudioDevice.setSelectedItem(am == null ? null : am.getDeviceInfo());
			btnAudioProps.setEnabled(am != null);
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
		SamplerChannel sc = getModel().getChannelInfo();
		
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
			if(mid != null) getModel().setBackendMidiInputDevice(mid.getDeviceId());
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
		
		getModel().setBackendMidiInputPort(cbMidiPort.getSelectedIndex());
	}
	
	private void
	setMidiChannel() {
		if(isUpdate()) return;
		
		Object o = cbMidiChannel.getSelectedItem();
		if(o == null) return;
		
		int c = o.toString().equals("All") ? -1 : Integer.parseInt(o.toString()) - 1;
		
		getModel().setBackendMidiInputChannel(c);
	}
	
	/** Invoked when the user selects an engine. */
	private void
	setEngineType() {
		Object oldEngine = getModel().getChannelInfo().getEngine();
		SamplerEngine newEngine = (SamplerEngine)cbEngines.getSelectedItem();
		
		if(newEngine == null) cbEngines.setToolTipText(null);
		else cbEngines.setToolTipText(newEngine.getDescription());
		
		if(oldEngine != null) { if(oldEngine.equals(newEngine)) return; }
		else if(newEngine == null) return;
		
		getModel().setBackendEngineType(newEngine.getName());
		
	}
	
	private void
	setAudioDevice() {
		if(isUpdate()) return;
		AudioOutputDevice dev = (AudioOutputDevice)cbAudioDevice.getSelectedItem();
		if(dev != null) getModel().setBackendAudioOutputDevice(dev.getDeviceId());
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
		sm.removeSamplerChannelListListener(getHandler());
		
		if(midiDevice != null) {
			midiDevice.removeMidiDeviceListener(getHandler());
		}
		
		if(fxSendsDlg != null) fxSendsDlg.dispose();
	}
	
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
			if(e.getChannelModel().getChannelId() == channelModel.getChannelId()) {
				onDestroy();
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
			boolean b = getModel().getChannelInfo().getEngine() != null;
			if(b && !cbInstrumentMap.isEnabled()) cbInstrumentMap.setEnabled(true);
		}
	
		/** Invoked when a new MIDI instrument map is removed from a list. */
		public void
		entryRemoved(ListEvent<MidiInstrumentMap> e) {
			cbInstrumentMap.removeItem(e.getEntry());
			if(cbInstrumentMap.getItemCount() == 0) {
				cbInstrumentMap.setSelectedItem(noMap);
				cbInstrumentMap.setEnabled(false);
			}
		}
	}
}
