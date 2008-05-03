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
import java.awt.Dimension;
import java.awt.Rectangle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.text.NumberFormat;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import net.sf.juife.InformationDialog;
import net.sf.juife.TitleBar;

import org.jdesktop.swingx.JXCollapsiblePane;

import org.jsampler.CC;
import org.jsampler.HF;
import org.jsampler.SamplerChannelModel;

import org.jsampler.event.SamplerChannelEvent;
import org.jsampler.event.SamplerChannelListEvent;
import org.jsampler.event.SamplerChannelListListener;
import org.jsampler.event.SamplerChannelListener;

import org.jsampler.view.std.JSChannelOutputRoutingDlg;
import org.jsampler.view.std.JSFxSendsPane;
import org.jsampler.view.std.JSInstrumentChooser;
import org.jsampler.view.std.JSVolumeEditorPopup;

import org.linuxsampler.lscp.SamplerChannel;

import static org.jsampler.view.fantasia.FantasiaI18n.i18n;
import static org.jsampler.view.fantasia.FantasiaPrefs.*;
import static org.jsampler.view.fantasia.FantasiaUtils.*;
import static org.jsampler.view.std.JSVolumeEditorPopup.VolumeType;


/**
 *
 * @author Grigor Iliev
 */
public class Channel extends org.jsampler.view.JSChannel {
	private final JXCollapsiblePane mainPane;
	private ChannelView channelView;
	private ChannelOptionsView channelOptionsView;
	private final ChannelOptionsPane optionsPane = new ChannelOptionsPane();
	
	private InformationDialog fxSendsDlg = null;
	
	private final ContextMenu contextMenu = new ContextMenu();
	
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
		
		int viewIdx = preferences().getIntProperty(DEFAULT_CHANNEL_VIEW);
		if(viewIdx == 0) {
			contextMenu.rbmiSmallView.doClick(0);
		} else if(viewIdx == 1) {
			contextMenu.rbmiNormalView.doClick(0);
		} else {
			contextMenu.rbmiNormalView.doClick(0);
		}
		
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
	
	public void
	loadInstrument() {
		JSInstrumentChooser dlg = FantasiaUtils.createInstrumentChooser(CC.getMainFrame());
		dlg.setVisible(true);
		
		if(!dlg.isCancelled()) {
			SamplerChannelModel m = getModel();
			m.loadBackendInstrument(dlg.getInstrumentFile(), dlg.getInstrumentIndex());
		}
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
	
	public void
	showFxSendsDialog() {
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
	
	class EditInstrumentAction extends AbstractAction implements SamplerChannelListener {
		EditInstrumentAction() {
			super(i18n.getMenuLabel("channels.editInstrument"));
			channelChanged(null);
			getModel().addSamplerChannelListener(this);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			CC.getSamplerModel().editBackendInstrument(getChannelId());
		}
		
		public void
		channelChanged(SamplerChannelEvent e) {
			boolean b = getChannelInfo().getInstrumentStatus() == 100;
			setEnabled(b);
		}
		
		public void
		streamCountChanged(SamplerChannelEvent e) { }
		
		public void
		voiceCountChanged(SamplerChannelEvent e) { }
	}
	
	class FxSendsAction extends AbstractAction {
		FxSendsAction() {
			super(i18n.getMenuLabel("channels.fxSends"));
		}
		
		public void
		actionPerformed(ActionEvent e) {
			showFxSendsDialog();
		}
	}
	
	class ChannelRoutingAction extends AbstractAction implements SamplerChannelListener {
		ChannelRoutingAction() {
			super(i18n.getMenuLabel("channels.channelRouting"));
			channelChanged(null);
			getModel().addSamplerChannelListener(this);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			SamplerChannel c = getChannelInfo();
			new JSChannelOutputRoutingDlg(CC.getMainFrame(), c).setVisible(true);
		}
		
		public void
		channelChanged(SamplerChannelEvent e) {
			boolean b = getChannelInfo().getAudioOutputDevice() != -1;
			setEnabled(b);
		}
		
		public void
		streamCountChanged(SamplerChannelEvent e) { }
		
		public void
		voiceCountChanged(SamplerChannelEvent e) { }
	}
	
	class SetSmallViewAction extends AbstractAction {
		SetSmallViewAction() {
			super(i18n.getMenuLabel("channels.smallView"));
		}
		
		public void
		actionPerformed(ActionEvent e) {
			if(channelView instanceof SmallChannelView) return;
			
			if(channelView != null) {
				mainPane.remove(channelView.getComponent());
				mainPane.remove(optionsPane);
				
				channelView.uninstallView();
				channelOptionsView.uninstallView();
			}
			
			channelView = new SmallChannelView(Channel.this);
			channelOptionsView = channelView.getChannelOptionsView();
			
			optionsPane.setContentPane(channelOptionsView.getComponent());
			
			updateChannelInfo();
		
			mainPane.add(channelView.getComponent());
			mainPane.add(optionsPane);
			mainPane.validate();
		}
	}
	
	class SetNormalViewAction extends AbstractAction {
		SetNormalViewAction() {
			super(i18n.getMenuLabel("channels.normalView"));
		}
		
		public void
		actionPerformed(ActionEvent e) {
			if(channelView instanceof NormalChannelView) return;
			
			if(channelView != null) {
				mainPane.remove(channelView.getComponent());
				mainPane.remove(optionsPane);
				
				channelView.uninstallView();
				channelOptionsView.uninstallView();
			}
			
			channelView = new NormalChannelView(Channel.this);
			channelOptionsView = channelView.getChannelOptionsView();
			
			optionsPane.setContentPane(channelOptionsView.getComponent());
			
			updateChannelInfo();
		
			mainPane.add(channelView.getComponent());
			mainPane.add(optionsPane);
			mainPane.validate();
		}
	}
	
	public ContextMenu
	getContextMenu() { return contextMenu; }
	
	class ContextMenu extends MouseAdapter {
		private final JPopupMenu menu = new JPopupMenu();
		
		protected final JRadioButtonMenuItem rbmiSmallView;
		protected final JRadioButtonMenuItem rbmiNormalView;
		
		ContextMenu() {
			menu.add(new JMenuItem(new EditInstrumentAction()));
			menu.addSeparator();
			
			rbmiSmallView = new JRadioButtonMenuItem(new SetSmallViewAction());
			rbmiNormalView = new JRadioButtonMenuItem(new SetNormalViewAction());
			
			ButtonGroup group = new ButtonGroup();
			group.add(rbmiSmallView);
			group.add(rbmiNormalView);
			
			menu.add(rbmiSmallView);
			menu.add(rbmiNormalView);
			
			menu.addSeparator();
			menu.add(new JMenuItem(new FxSendsAction()));
			menu.add(new JMenuItem(new ChannelRoutingAction()));
		}
		
		public void
		mousePressed(MouseEvent e) {
			if(e.isPopupTrigger()) show(e);
		}
	
		public void
		mouseReleased(MouseEvent e) {
			if(e.isPopupTrigger()) show(e);
		}
	
		void
		show(MouseEvent e) {
			menu.show(e.getComponent(), e.getX(), e.getY());
		}
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
	
	public static class StreamVoiceCountPane extends JPanel {
		private final Channel channel;
		
		private final JLabel lStreams = createScreenLabel(" --");
		private final JLabel lSlash = createScreenLabel("/");
		private final JLabel lVoices = createScreenLabel("-- ");
		
		public
		StreamVoiceCountPane(Channel channel) {
			this.channel = channel;
			
			setOpaque(false);
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			lStreams.setFont(Res.fontScreenMono);
			lStreams.setHorizontalAlignment(JLabel.RIGHT);
			lStreams.setToolTipText(i18n.getLabel("Channel.streamVoiceCount"));
			
			Dimension d = lStreams.getPreferredSize();
			lStreams.setMinimumSize(d);
			lStreams.setPreferredSize(d);
			lStreams.setMaximumSize(d);
			add(lStreams);
			
			lSlash.setFont(Res.fontScreenMono);
			lSlash.setToolTipText(i18n.getLabel("Channel.streamVoiceCount"));
			add(lSlash);
			
			lVoices.setFont(Res.fontScreenMono);
			lVoices.setToolTipText(i18n.getLabel("Channel.streamVoiceCount"));
			
			d = lStreams.getPreferredSize();
			lVoices.setMinimumSize(d);
			lVoices.setPreferredSize(d);
			lVoices.setMaximumSize(d);
			add(lVoices);
			
			lStreams.addMouseListener(channel.getContextMenu());
			lSlash.addMouseListener(channel.getContextMenu());
			lVoices.addMouseListener(channel.getContextMenu());
		}
		
		public void
		updateStreamCount(int count) {
			lStreams.setText(count == 0 ? " --" : String.valueOf(count));
		}
		
		public void
		updateVoiceCount(int count) {
			lVoices.setText(count == 0 ? "-- " : String.valueOf(count));
		}
	}
	
	public static class VolumePane extends JPanel {
		private final Channel channel;
		private final JButton btnVolume = createScreenButton("");
		private JSVolumeEditorPopup popupVolume;
		
		private static NumberFormat numberFormat = NumberFormat.getInstance();
		static { numberFormat.setMaximumFractionDigits(1); }
		
		public
		VolumePane(final Channel channel) {
			this.channel = channel;
			setOpaque(false);
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			
			btnVolume.setIcon(Res.iconVolume14);
			btnVolume.setIconTextGap(2);
			btnVolume.setAlignmentX(RIGHT_ALIGNMENT);
			btnVolume.setHorizontalAlignment(btnVolume.LEFT);
			updateVolumeInfo(100);
			Dimension d = btnVolume.getPreferredSize();
			d.width = 57;
			btnVolume.setPreferredSize(d);
			btnVolume.setMinimumSize(d);
			
			add(btnVolume);
			
			btnVolume.addActionListener(new ActionListener() {
				public void
				actionPerformed(ActionEvent e) {
					if(popupVolume.isVisible()) {
						popupVolume.commit();
						popupVolume.hide();
					} else {
						float vol = channel.getModel().getChannelInfo().getVolume();
						popupVolume.setCurrentVolume(vol);
						popupVolume.show();
					}
				}
			});
			
			popupVolume = new JSVolumeEditorPopup(btnVolume, VolumeType.CHANNEL);
			
			popupVolume.addActionListener(new ActionListener() {
				public void
				actionPerformed(ActionEvent e) {
					channel.getModel().setBackendVolume(popupVolume.getVolumeFactor());
				}
			});
			
			btnVolume.addMouseListener(channel.getContextMenu());
		}
		
		public void
		updateVolumeInfo(int volume) {
			if(CC.getViewConfig().isMeasurementUnitDecibel()) {
				String s = numberFormat.format(HF.percentsToDecibels(volume));
				btnVolume.setText(s + "dB");
			} else {
				btnVolume.setText(String.valueOf(volume) + "%");
			}
		}
	}
	
	public static class PowerButton extends PixmapToggleButton implements ActionListener {
		private final Channel channel;
		
		PowerButton(Channel channel) {
			this(channel, Res.gfxPowerOff, Res.gfxPowerOn);
		}
		
		PowerButton(Channel channel, ImageIcon defaultIcon, ImageIcon selectedIcon) {
			super(defaultIcon, selectedIcon);
			
			this.channel = channel;
		
			setSelected(true);
			addActionListener(this);
			setToolTipText(i18n.getButtonLabel("Channel.ttRemoveChannel"));
		}
		
		public void
		actionPerformed(ActionEvent e) {
			boolean b = preferences().getBoolProperty(CONFIRM_CHANNEL_REMOVAL);
			if(b) {
				String s = i18n.getMessage("Channel.remove?", channel.getChannelId());
				if(!HF.showYesNoDialog(channel, s)) {
					setSelected(true);
					return;
				}
			}
			channel.remove();
		}
		
		public boolean
		contains(int x, int y) { return (x - 11)*(x - 11) + (y - 11)*(y - 11) < 71; }
	}
	
	public static class OptionsButton extends PixmapToggleButton implements ActionListener {
		private final Channel channel;
		
		OptionsButton(Channel channel) {
			super(Res.gfxOptionsOff, Res.gfxOptionsOn);
			
			this.channel = channel;
			
			setRolloverIcon(Res.gfxOptionsOffRO);
			this.setRolloverSelectedIcon(Res.gfxOptionsOnRO);
			addActionListener(this);
			setToolTipText(i18n.getButtonLabel("Channel.ttShowOptions"));
		}
		
		public void
		actionPerformed(ActionEvent e) {
			channel.showOptionsPane(isSelected());
			
			String s;
			if(isSelected()) s = i18n.getButtonLabel("Channel.ttHideOptions");
			else s = i18n.getButtonLabel("Channel.ttShowOptions");
			
			setToolTipText(s);
		}
		
		public boolean
		contains(int x, int y) { return super.contains(x, y) & y < 13; }
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
