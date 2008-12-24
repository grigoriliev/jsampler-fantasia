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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.RenderingHints;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sf.juife.event.TaskEvent;
import net.sf.juife.event.TaskListener;

import org.jsampler.CC;
import org.jsampler.SamplerChannelModel;

import org.jsampler.event.SamplerChannelAdapter;
import org.jsampler.event.SamplerChannelEvent;
import org.jsampler.event.SamplerChannelListListener;
import org.jsampler.event.SamplerChannelListEvent;

import org.jsampler.view.JSChannel;
import org.jsampler.view.fantasia.basic.*;
import org.jsampler.view.std.JSPianoRoll;

import org.linuxsampler.lscp.Instrument;
import org.linuxsampler.lscp.SamplerChannel;

import org.linuxsampler.lscp.event.MidiDataEvent;
import org.linuxsampler.lscp.event.MidiDataListener;

import static javax.swing.Action.SMALL_ICON;
import static org.jsampler.task.Global.GetFileInstrument;
import static org.jsampler.view.fantasia.FantasiaI18n.i18n;

/**
 *
 * @author Grigor Iliev
 */
public class PianoKeyboardPane extends FantasiaPanel
			       implements ListSelectionListener, SamplerChannelListListener {
	
	protected final JToggleButton btnPower = new PowerButton();
	private final FantasiaLabel lDisplay = new FantasiaLabel(" ", true);
	//private final PitchWheel pitchWheel = new PitchWheel();
	//private final ModWheel modWheel = new ModWheel();
	private final JSPianoRoll pianoRoll = new JSPianoRoll();
	private SamplerChannelModel channel = null;
	
	private String file = null;
	private int index = -1;
	
	public
	PianoKeyboardPane() {
		pianoRoll.actionDecreaseKeyNumber.putValue(SMALL_ICON, Res.gfxBtnDecrease);
		pianoRoll.actionIncreaseKeyNumber.putValue(SMALL_ICON, Res.gfxBtnIncrease);
		pianoRoll.actionScrollLeft.putValue(SMALL_ICON, Res.gfxBtnScrollLeft);
		pianoRoll.actionScrollRight.putValue(SMALL_ICON, Res.gfxBtnScrollRight);
		
		setOpaque(false);
		
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		
		setLayout(gridbag);
		
		setBorder(BorderFactory.createEmptyBorder(0, 3, 5, 3));
		
		c.fill = GridBagConstraints.NONE;
		
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(3, 3, 3, 3);
		gridbag.setConstraints(btnPower, c);
		add(btnPower);
		
		JPanel p = createVSeparator();
		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 2;
		c.insets = new Insets(3, 0, 5, 3);
		c.fill = GridBagConstraints.VERTICAL;
		c.weighty = 1.0;
		gridbag.setConstraints(p, c);
		add(p);
		
		/*c.gridx = 2;
		c.gridy = 1;
		c.gridheight = 1;
		c.insets = new Insets(0, 3, 5, 3);
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(pitchWheel, c);
		add(pitchWheel);
		
		c.gridx = 3;
		c.gridy = 1;
		c.insets = new Insets(0, 0, 5, 6);
		gridbag.setConstraints(modWheel, c);
		add(modWheel);*/
		
		p = new KeyRangePropsPane();
		
		c.gridx = 2;
		c.gridy = 0;
		c.gridwidth = 3;
		c.gridheight = 1;
		c.insets = new Insets(0, 0, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.weighty = 0.0;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(p, c);
		add(p);
		
		p = new KeyRangePropsPane();
		
		c.gridx = 8;
		c.gridy = 0;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(0, 0, 0, 10);
		gridbag.setConstraints(p, c);
		add(p);
		
		lDisplay.setPreferredSize(new Dimension(300, lDisplay.getPreferredSize().height));
		
		c.gridx = 6;
		c.gridy = 0;
		c.insets = new Insets(3, 3, 3, 12);
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		gridbag.setConstraints(lDisplay, c);
		add(lDisplay);
		
		p = new JPanel();
		p.setOpaque(false);
		c.gridx = 5;
		c.gridy = 0;
		c.insets = new Insets(0, 0, 0, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		gridbag.setConstraints(p, c);
		add(p);
		
		p = new JPanel();
		p.setOpaque(false);
		c.gridx = 7;
		c.gridy = 0;
		gridbag.setConstraints(p, c);
		add(p);
		
		pianoRoll.setFocusable(false);
		pianoRoll.setBackground(new Color(0x2e2e2e));
		//pianoRoll.setOpaque(false);
		
		disablePianoRoll();
		
		c.gridx = 4;
		c.gridy = 1;
		c.gridwidth = 5;
		c.insets = new Insets(0, 3, 0, 12);
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;
		gridbag.setConstraints(pianoRoll, c);
		add(pianoRoll);
		
		CC.getSamplerModel().addSamplerChannelListListener(this);
		pianoRoll.addMidiDataListener(getHandler());
		
		updateKeyRange();
		
		PropertyChangeListener l = new PropertyChangeListener() {
			public void
			propertyChange(PropertyChangeEvent e) {
				updateKeyRange();
			}
		};
		
		CC.preferences().addPropertyChangeListener("midiKeyboard.firstKey", l);
		CC.preferences().addPropertyChangeListener("midiKeyboard.lastKey", l);
		
		addFocusListener(getHandler());
		
		MouseAdapter l2 = new MouseAdapter() {
			public void
			mouseClicked(MouseEvent e) { requestFocusInWindow(); }
		};
		
		addKeyListener(pianoRoll.getKeyListener());
		pianoRoll.registerKeys(this);
		
		addMouseListener(l2);
		pianoRoll.addMouseListener(l2);
		lDisplay.addMouseListener(l2);
	}
	
	public JSPianoRoll
	getPianoRoll() { return pianoRoll; }
	
	private void
	updateKeyRange() {
		int firstKey = CC.preferences().getIntProperty("midiKeyboard.firstKey");
		int lastKey = CC.preferences().getIntProperty("midiKeyboard.lastKey");
		pianoRoll.setKeyRange(firstKey, lastKey);
	}
	
	@Override public void
	valueChanged(ListSelectionEvent e) {
		if(e.getValueIsAdjusting()) return;
		
		JSChannel[] chnS = CC.getMainFrame().getSelectedChannelsPane().getSelectedChannels();
		if(chnS == null || chnS.length == 0) {
			disconnectChannel();
			return;
		}
		
		if(chnS[0].getModel() == channel) return;
		disconnectChannel();
		connectChannel(chnS[0].getModel());
	}
	
	private void
	updateInstrumentInfo() {
		final GetFileInstrument i = new GetFileInstrument(file, index);
		
		i.addTaskListener(new TaskListener() {
			public void
			taskPerformed(TaskEvent e) {
				if(i.doneWithErrors()) return;
				updatePianoKeyboard(i.getResult());
			}
		});
		
		CC.getTaskQueue().add(i);
	}
	
	private void
	disablePianoRoll() {
		pianoRoll.reset(true);
		pianoRoll.setPlayingEnabled(false);
	}
	
	private void
	updatePianoKeyboard(Instrument instr) {
		// The selected channel may have changed before this update
		// due to concurency, so also checking whether file is null
		if(instr == null || file == null) {
			disablePianoRoll();
			return;
		}
		
		pianoRoll.setPlayingEnabled(true);
		
		pianoRoll.setShouldRepaint(false);
		pianoRoll.reset(true);
		pianoRoll.setDisabled(instr.getKeyMapping(), false);
		pianoRoll.setKeyswitches(instr.getKeyswitchMapping(), true);
		pianoRoll.setShouldRepaint(true);
		pianoRoll.repaint();
	}
	
	private void
	connectChannel(SamplerChannelModel chn) {
		channel = chn;
		channel.addMidiDataListener(pianoRoll);
		channel.addSamplerChannelListener(getHandler());
		
		updateDisplay();
		
		file = channel.getChannelInfo().getInstrumentFile();
		if(file == null) {
			disablePianoRoll();
			return;
		}
		index = channel.getChannelInfo().getInstrumentIndex();
		updateInstrumentInfo();
	}
	
	private void
	disconnectChannel() {
		if(channel != null) {
			channel.removeMidiDataListener(pianoRoll);
			channel.removeSamplerChannelListener(getHandler());
			channel = null;
			file = null;
			index = -1;
			disablePianoRoll();
		}
		
		updateDisplay();
	}
	
	@Override public void
	channelAdded(SamplerChannelListEvent e) {
		updateDisplay();
	}
	
	@Override public void
	channelRemoved(SamplerChannelListEvent e) {
		if(e.getChannelModel() == channel) {
			disconnectChannel();
		}
		
		updateDisplay();
	}
	
	private JPanel
	createVSeparator() {
		PixmapPane p = new PixmapPane(Res.gfxVLine);
		p.setAlignmentY(JPanel.TOP_ALIGNMENT);
		p.setMinimumSize(new Dimension(2, 30));
		p.setPreferredSize(new Dimension(2, 60));
		p.setMaximumSize(new Dimension(2, Short.MAX_VALUE));
		return p;
	}
	
	private Color color1 = new Color(0x7a7a7a);
	private Color color2 = new Color(0x5e5e5e);
	private Color color3 = new Color(0x2e2e2e);
	
	@Override
	public void
	paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		
		Paint oldPaint = g2.getPaint();
		Composite oldComposite = g2.getComposite();
		
		Insets insets = this.getInsets();
		double x1 = insets.left;
		double y1 = insets.top;
		
		double w = getSize().getWidth();
		double x2 = w - insets.right - 1;
		double h = getSize().getHeight();
		double y2 = h - insets.bottom - 1;
		
		FantasiaPainter.paintGradient(g2, x1, y1, x2, y2 - 10, color1, color2);
		
		g2.setRenderingHint (
			RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF
		);
		
		double y3 = y2 - 10;
		if(y3 < 0) y3 = 0;
		
		Rectangle2D.Double rect = new Rectangle2D.Double(x1, y3, x2 - x1 + 1, 11);
		
		GradientPaint gr = new GradientPaint (
			0.0f, (float)y3, color2,
			0.0f, (float)h, color3
		);
		
		g2.setPaint(gr);
		g2.fill(rect);
		
		drawOutBorder(g2, x1, y1, x2, y2);
		
		double prX = pianoRoll.getLocation().getX();
		double prY = pianoRoll.getLocation().getY();
		drawInBorder (
			g2, prX - 2, prY - 2,
			prX + pianoRoll.getSize().getWidth() + 1, prY + pianoRoll.getSize().getHeight() - 3
		);
		
		g2.setPaint(oldPaint);
		g2.setComposite(oldComposite);
	}
	
	private void
	drawOutBorder(Graphics2D g2, double x1, double y1, double x2, double y2) {
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.40f);
		g2.setComposite(ac);
		
		g2.setPaint(Color.WHITE);
		Line2D.Double l = new Line2D.Double(x1, y1, x2, y1);
		g2.draw(l);
		
		g2.setComposite(ac.derive(0.20f));
		l = new Line2D.Double(x1, y1 + 1, x2, y1 + 1);
		g2.draw(l);
		
		g2.setComposite(ac.derive(0.255f));
		
		l = new Line2D.Double(x1, y1, x1, y2);
		g2.draw(l);
		
		g2.setComposite(ac.derive(0.40f));
		g2.setPaint(Color.BLACK);
		
		//l = new Line2D.Double(x1, y2, x2, y2);
		//g2.draw(l);
		
		g2.setComposite(ac.derive(0.20f));
		
		l = new Line2D.Double(x2, y1, x2, y2);
		g2.draw(l);
	}
	
	private void
	drawInBorder(Graphics2D g2, double x1, double y1, double x2, double y2) {
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.40f);
		g2.setComposite(ac);
		
		g2.setPaint(Color.WHITE);
		Line2D.Double l = new Line2D.Double(x1, y2, x2, y2);
		//g2.draw(l);
		
		g2.setComposite(ac.derive(0.255f));
		
		l = new Line2D.Double(x2 - 1, y1 + 1, x2 - 1, y2 + 1);
		g2.draw(l);
		
		g2.setComposite(ac.derive(0.13f));
		
		l = new Line2D.Double(x2, y1, x2, y2);
		g2.draw(l);
		
		g2.setComposite(ac.derive(0.20f));
		g2.setPaint(Color.BLACK);
		
		l = new Line2D.Double(x1, y1, x2, y1);
		g2.draw(l);
		
		g2.setComposite(ac.derive(0.40f));
		g2.setPaint(Color.BLACK);
		
		l = new Line2D.Double(x1 + 1, y1 + 1, x2 - 1, y1 + 1);
		g2.draw(l);
		
		g2.setComposite(ac.derive(0.20f));
		
		l = new Line2D.Double(x1, y1, x1, y2);
		g2.draw(l);
		
		g2.setComposite(ac.derive(0.40f));
		
		l = new Line2D.Double(x1 + 1, y1 + 1, x1 + 1, y2 + 1);
		g2.draw(l);
	}
	
	private void
	updateDisplay() {
		// TODO: called too often?
		if(channel == null) {
			lDisplay.setText(i18n.getLabel("PianoKeyboardPane.noChannel"));
			return;
		}
		
		SamplerChannel sc = channel.getChannelInfo();
		String s = CC.getMainFrame().getChannelPath(channel);
		
		StringBuffer sb = new StringBuffer();
		sb.append(i18n.getLabel("PianoKeyboardPane.channel", s)).append(" - ");
		
		int status = sc.getInstrumentStatus();
		if(status >= 0 && status < 100) {
			sb.append(i18n.getLabel("ChannelScreen.loadingInstrument", status));
		} else if(status == -1) {
			sb.append(i18n.getLabel("PianoKeyboardPane.noInstrument"));
		} else if(status < -1) {
			 sb.append(i18n.getLabel("ChannelScreen.errorLoadingInstrument"));
		} else {
			if(sc.getInstrumentName() != null) sb.append(sc.getInstrumentName());
		}
		
		lDisplay.setText(sb.toString());
	}
	
	
	class KeyRangePropsPane extends JPanel {
		private final JButton btnIncrease =
			new PixmapButton(pianoRoll.actionIncreaseKeyNumber, Res.gfxBtnIncrease);
		
		private final JButton btnDecrease =
			new PixmapButton(pianoRoll.actionDecreaseKeyNumber, Res.gfxBtnDecrease);
		
		private final JButton btnScrollLeft =
			new PixmapButton(pianoRoll.actionScrollLeft, Res.gfxBtnScrollLeft);
		
		private final JButton btnScrollRight =
			new PixmapButton(pianoRoll.actionScrollRight, Res.gfxBtnScrollRight);
		
		KeyRangePropsPane() {
			setOpaque(false);
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			
			btnIncrease.setFocusable(false);
			btnDecrease.setFocusable(false);
			btnScrollLeft.setFocusable(false);
			btnScrollRight.setFocusable(false);
			
			
			btnDecrease.setPressedIcon(Res.gfxBtnDecreaseRO);
			add(btnDecrease);
			
			btnIncrease.setPressedIcon(Res.gfxBtnIncreaseRO);
			add(btnIncrease);
			
			add(Box.createRigidArea(new Dimension(6, 0)));
			
			btnScrollLeft.setPressedIcon(Res.gfxBtnScrollLeftRO);
			add(btnScrollLeft);
			
			btnScrollRight.setPressedIcon(Res.gfxBtnScrollRightRO);
			add(btnScrollRight);
		}
	}
	
	
	public static class PowerButton extends PixmapToggleButton implements ActionListener {
		PowerButton() {
			this(Res.gfxPowerOff, Res.gfxPowerOn);
		}
		
		PowerButton(ImageIcon defaultIcon, ImageIcon selectedIcon) {
			super(defaultIcon, selectedIcon);
			
			setSelected(true);
			addActionListener(this);
		}
		
		@Override
		public void
		actionPerformed(ActionEvent e) {
			boolean b = isSelected();
			MainFrame frm = (MainFrame)CC.getMainFrame();
			if(frm == null) return;
			frm.setMidiKeyboardVisible(b);
		}
		
		@Override
		public boolean
		contains(int x, int y) { return (x - 11)*(x - 11) + (y - 11)*(y - 11) < 71; }
	}
	
	private final Handler handler = new Handler();
	
	private Handler
	getHandler() { return handler; }
	
	private class Handler extends SamplerChannelAdapter implements FocusListener, MidiDataListener {
		@Override
		public void
		midiDataArrived(MidiDataEvent e) {
			if(channel == null) return;
			channel.sendBackendMidiData(e);
		}
		
		@Override
		public void
		channelChanged(SamplerChannelEvent e) {
			updateDisplay();
			
			String newFile = channel.getChannelInfo().getInstrumentFile();
			int newIndex = channel.getChannelInfo().getInstrumentIndex();
			
			if(channel.getChannelInfo().getInstrumentStatus() != 100) {
				//don't use disablePianoRoll because of unnecessary repainting
				pianoRoll.setAllKeysPressed(false);
				pianoRoll.removeAllKeyswitches();
				pianoRoll.setAllKeysDisabled(true);
				pianoRoll.setPlayingEnabled(false);
				return;
			}
			
			if(newFile == null) {
				if(file != null) disablePianoRoll();
				file = null;
				index = -1;
				return;
			}
			
			if(newFile.equals(file) && newIndex == index) return;
			
			file = newFile;
			index = newIndex;
			updateInstrumentInfo();
		}
		
		@Override
		public void
		focusGained(FocusEvent e) {
			
		}
		
		@Override
		public void
		focusLost(FocusEvent e) {
			
		}
	}
}
