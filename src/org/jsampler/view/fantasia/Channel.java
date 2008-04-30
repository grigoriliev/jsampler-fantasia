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

import java.awt.Container;
import java.awt.Rectangle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;

import net.sf.juife.TitleBar;

import org.jdesktop.swingx.JXCollapsiblePane;

import org.jsampler.CC;
import org.jsampler.HF;
import org.jsampler.SamplerChannelModel;

import org.jsampler.event.SamplerChannelEvent;
import org.jsampler.event.SamplerChannelListEvent;
import org.jsampler.event.SamplerChannelListListener;
import org.jsampler.event.SamplerChannelListener;

import static org.jsampler.view.fantasia.FantasiaPrefs.*;


/**
 *
 * @author Grigor Iliev
 */
public class Channel extends org.jsampler.view.JSChannel {
	private final JXCollapsiblePane mainPane;
	private ChannelView channelView;
	private ChannelOptionsView channelOptionsView;
	private final ChannelOptionsPane optionsPane = new ChannelOptionsPane();
	
	private boolean selected = false;
	
	private AnimatedPorpetyListener animatedPorpetyListener = new AnimatedPorpetyListener();
	
	class AnimatedPorpetyListener implements PropertyChangeListener {
		public void
		propertyChange(PropertyChangeEvent e) {
			mainPane.setAnimated(preferences().getBoolProperty(ANIMATED));
		}
	}
	
	/**
	 * Creates a new instance of <code>Channel</code> using the specified
	 * non-<code>null</code> channel model.
	 * @param model The model to be used by this channel.
	 * @throws IllegalArgumentException If the model is <code>null</code>.
	 */
	public
	Channel(SamplerChannelModel model) {
		this(model, null);
	}
	
	/**
	 * Creates a new instance of <code>Channel</code> using the specified
	 * non-<code>null</code> channel model.
	 * @param model The model to be used by this channel.
	 * @param listener A listener which is notified when the newly created
	 * channel is fully expanded on the screen.
	 * @throws IllegalArgumentException If the model is <code>null</code>.
	 */
	public
	Channel(SamplerChannelModel model, final ActionListener listener) {
		super(model);
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		optionsPane.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		
		mainPane = new JXCollapsiblePane();
		mainPane.getContentPane().setLayout (
			new BoxLayout(mainPane.getContentPane(), BoxLayout.Y_AXIS)
		);
		
		channelView = new NormalChannelView(this);
		channelOptionsView = channelView.getChannelOptionsView();
		
		optionsPane.setContentPane(channelOptionsView.getComponent());
		
		mainPane.add(channelView.getComponent());
		mainPane.add(optionsPane);
		
		setOpaque(false);
		
		getModel().addSamplerChannelListener(getHandler());
		
		updateChannelInfo();
		
		add(mainPane);
		
		if(listener != null) {
			final String s = JXCollapsiblePane.ANIMATION_STATE_KEY;
			mainPane.addPropertyChangeListener(s, new PropertyChangeListener() {
				public void
				propertyChange(PropertyChangeEvent e) {
					if(e.getNewValue() == "expanded") {
						// TODO: this should be done regardles the listener != null?
						mainPane.removePropertyChangeListener(s, this);
						///////
						listener.actionPerformed(null);
						ensureChannelIsVisible();
					} else if(e.getNewValue() == "expanding/collapsing") {
						ensureChannelIsVisible();
					}
				}
			});
		}
		
		mainPane.setAnimated(false);
		mainPane.setCollapsed(true);
		mainPane.setAnimated(preferences().getBoolProperty(ANIMATED));
		mainPane.setCollapsed(false);
		
		preferences().addPropertyChangeListener(ANIMATED, animatedPorpetyListener);
		
		if(listener != null) {
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				public void
				run() { listener.actionPerformed(null); }
			});
		}
		
		CC.getSamplerModel().addSamplerChannelListListener(getHandler());
	}
	
	private void
	ensureChannelIsVisible() {
		Container p = getParent();
		JScrollPane sp = null;
		while(p != null) {
			if(p instanceof JScrollPane) {
				sp = (JScrollPane)p;
				break;
			}
			p = p.getParent();
		}
		if(sp == null) return;
		int h = sp.getViewport().getView().getHeight();
		sp.getViewport().scrollRectToVisible(new Rectangle(0, h - 2, 1, 1));
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
		boolean b = optionsPane.isAnimated();
		optionsPane.setAnimated(animated);
		channelView.expandChannel();
		optionsPane.setAnimated(b);
	}
	
	/**
	 * Updates the channel settings. This method is invoked when changes to the
	 * channel were made.
	 */
	private void
	updateChannelInfo() {
		channelView.updateChannelInfo();
		channelOptionsView.updateChannelInfo();
	}
	
	protected void
	onDestroy() {
		CC.getSamplerModel().removeSamplerChannelListListener(getHandler());
		preferences().removePropertyChangeListener(ANIMATED, animatedPorpetyListener);
		
		channelView.uninstallView();
		channelOptionsView.uninstallView();
	}
		
	public void
	remove() {
		if(!mainPane.isAnimated()) {
			CC.getSamplerModel().removeBackendChannel(getChannelId());
			return;
		}
		
		String s = JXCollapsiblePane.ANIMATION_STATE_KEY;
		mainPane.addPropertyChangeListener(s, getHandler());
		mainPane.setCollapsed(true);
	}
	
	public void
	showOptionsPane(boolean show) { optionsPane.showOptionsPane(show); }
	
	private final EventHandler eventHandler = new EventHandler();
	
	private EventHandler
	getHandler() { return eventHandler; }
	
	private class EventHandler implements SamplerChannelListener,
					SamplerChannelListListener, PropertyChangeListener {
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
			channelView.updateStreamCount(getModel().getStreamCount());
		}
	
		/**
		 * Invoked when the number of active voices has changed.
		 * @param e A <code>SamplerChannelEvent</code> instance
		 * containing event information.
		 */
		public void
		voiceCountChanged(SamplerChannelEvent e) {
			channelView.updateVoiceCount(getModel().getVoiceCount());
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
			if(e.getChannelModel().getChannelId() == getChannelId()) {
				onDestroy();
			}
		}
		
		public void
		propertyChange(PropertyChangeEvent e) {
			if(e.getNewValue() == "collapsed") {
				CC.getSamplerModel().removeBackendChannel(getChannelId());
			}
		}
	}
}

class ChannelOptionsPane extends JXCollapsiblePane {
	ChannelOptionsPane() {
		setAnimated(false);
		setCollapsed(true);
		setAnimated(preferences().getBoolProperty(ANIMATED));
		
		preferences().addPropertyChangeListener(ANIMATED, new PropertyChangeListener() {
			public void
			propertyChange(PropertyChangeEvent e) {
				setAnimated(preferences().getBoolProperty(ANIMATED));
			}
		});
	}
	
	public void
	showOptionsPane(boolean show) { setCollapsed(!show); }
}
