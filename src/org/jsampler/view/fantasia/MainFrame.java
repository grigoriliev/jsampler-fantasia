/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2006 Grigor Iliev <grigor@grigoriliev.com>
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
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.logging.Level;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.plaf.synth.SynthLookAndFeel;

import net.sf.juife.TitleBar;

import org.jsampler.CC;
import org.jsampler.HF;

import org.jsampler.view.JSChannel;
import org.jsampler.view.JSChannelsPane;
import org.jsampler.view.JSMainFrame;

import static org.jsampler.view.fantasia.FantasiaI18n.i18n;


/**
 *
 * @author Grigor Iliev
 */
public class MainFrame extends JSMainFrame {
	private final static int TITLE_BAR_WIDTH = 420;
	private final static int TITLE_BAR_HEIGHT = 29;
	
	private final ChannelsPane channelsPane = new ChannelsPane("");
	
	/** Creates a new instance of <code>MainFrame</code> */
	public
	MainFrame() {
		try {
			SynthLookAndFeel synth = new SynthLookAndFeel();
			synth.load(MainFrame.class.getResourceAsStream("gui.xml"), MainFrame.class);
			UIManager.setLookAndFeel(synth);
		} catch(Exception e) {
			CC.getLogger().log(Level.INFO, HF.getErrorMessage(e), e);
		}
		
		setTitle(i18n.getLabel("MainFrame.title"));
		addChannelsPane(channelsPane);
		add(channelsPane);
		setUndecorated(true);
		
		JToggleButton btn = new PixmapToggleButton(Res.iconPowerOff, Res.iconPowerOn) {
			public boolean
			contains(int x, int y) {
				return (x - 11)*(x - 11) + (y - 11)*(y - 11) < 71;
			}
		};
		
		btn.setSelected(true);
		btn.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		btn.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { onWindowClose(); }
		});
		
		
		FantasiaTitleBar tb = new FantasiaTitleBar();
		tb.setName("FantasiaTitleBar");
		tb.setLayout(new BoxLayout(tb, BoxLayout.X_AXIS));
		tb.setOpaque(true);
		tb.add(Box.createRigidArea(new Dimension(4, 0)));
		tb.add(btn);
		tb.add(Box.createRigidArea(new Dimension(3, 0)));
		
		
		
		tb.add(createVSeparator());
		
		tb.add(Box.createRigidArea(new Dimension(275, 0)));
		
		tb.add(createVSeparator());
		
		tb.add(Box.createRigidArea(new Dimension(29, 0)));
		
		tb.add(createVSeparator());
		
		tb.add(Box.createRigidArea(new Dimension(40, 0)));
		
		tb.add(createVSeparator());
		
		tb.setPreferredSize(new Dimension(TITLE_BAR_WIDTH, TITLE_BAR_HEIGHT));
		tb.setMinimumSize(tb.getPreferredSize());
		tb.setMaximumSize(tb.getPreferredSize());
		add(tb, BorderLayout.SOUTH);
		
		getContentPane().setBackground(new java.awt.Color(0x818181));
		getRootPane().setOpaque(false);
		getLayeredPane().setOpaque(false);
		//getContentPane().setVisible(false);
		
		setAlwaysOnTop(FantasiaPrefs.isAlwaysOnTop());
		pack();
		
		String s = FantasiaPrefs.getWindowLocation();
		
		try {
			if(s == null) {
				setDefaultLocation();
			} else {
				int i = s.indexOf(',');
				int x = Integer.parseInt(s.substring(0, i));
			
				s = s.substring(i + 1);
				int y = Integer.parseInt(s);
			
				setLocation(x, y);
			}
		} catch(Exception x) {
			String msg = "Parsing of window size and location string failed";
			CC.getLogger().log(Level.INFO, msg, x);
			setDefaultLocation();
		}
	}
	
	private void
	setDefaultLocation() {
		Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((d.width - TITLE_BAR_WIDTH) / 2, (d.height - TITLE_BAR_HEIGHT) / 2);
	}
	
	
	/** Invoked when this window is about to close. */
	protected void
	onWindowClose() {
		FantasiaPrefs.setAlwaysOnTop(isAlwaysOnTop());
		
		java.awt.Point p = getLocation();
		Dimension d = getSize();
		StringBuffer sb = new StringBuffer();
		sb.append(p.x).append(',').append(p.y + getSize().height - TITLE_BAR_HEIGHT);
		FantasiaPrefs.setWindowLocation(sb.toString());
		
		super.onWindowClose();
	}
	
	private JPanel
	createVSeparator() {
		JPanel p = new JPanel();
		p.setName("VSeparator");
		p.setOpaque(false);
		p.setPreferredSize(new Dimension(2, 29));
		p.setMinimumSize(p.getPreferredSize());
		p.setMaximumSize(p.getPreferredSize());
		return p;
	}
	
	/**
	 * This method does nothing, because <b>Fantasia</b> has exactly
	 * one pane containing sampler channels, which can not be changed.
	 */
	public void
	insertChannelsPane(JSChannelsPane pane, int idx) {
		getChannelsPaneList().removeAllElements();
		addChannelsPane(pane);
	}
	
	/**
	 * This method always returns the <code>JSChannelsPane</code> at index 0,
	 * because the <b>Fantasia</b> view has exactly one pane containing sampler channels.
	 * @return The <code>JSChannelsPane</code> at index 0.
	 */
	public JSChannelsPane
	getSelectedChannelsPane() { return getChannelsPane(0); }
	
	/**
	 * This method does nothing because the <b>Fantasia</b> view has
	 * exactly one pane containing sampler channels which is always shown. 
	 */
	public void
	setSelectedChannelsPane(JSChannelsPane pane) { }
	
	public static void
	repack(JSMainFrame frame) {
		int y = frame.getLocation().y;
		int height = frame.getSize().height;
		y += (height - frame.getPreferredSize().height);
		
		if((height - frame.getPreferredSize().height) > 0) {
			frame.pack();
			frame.setLocation(frame.getLocation().x, y);
		} else {
			frame.setLocation(frame.getLocation().x, y);
			frame.pack();
		}
	}
	
	public void
	installJSamplerHome() { }
	
	public void
	showDetailedErrorMessage(Frame owner, String err, String details) {
		// TODO: 
	}
	
	public void
	showDetailedErrorMessage(Dialog owner, String err, String details) {
		// TODO: 
	}
}

class FantasiaTitleBar extends TitleBar {
	FantasiaTitleBar() { this.addMouseListener(new ContextMenu()); }
	
	class ContextMenu extends MouseAdapter {
		private final JPopupMenu cmenu = new JPopupMenu();
		
		ContextMenu() {
			JMenuItem mi;
			
			final JCheckBoxMenuItem cmi = new JCheckBoxMenuItem (
				i18n.getMenuLabel("FantasiaTitleBar.AlwaysOnTop")
			);
			cmi.setIcon(null);
			cmi.setSelected(FantasiaPrefs.isAlwaysOnTop());
			
			cmenu.add(cmi);
			
			cmi.addActionListener(new ActionListener() {
				public void
				actionPerformed(ActionEvent e) {
					CC.getMainFrame().setAlwaysOnTop(cmi.isSelected());
				}
			});
			
			/*mi = new JMenuItem(A4n.moveChannelsUp);
			mi.setIcon(null);
			cmenu.add(mi);
			
			cmenu.addSeparator();
			
			mi = new JMenuItem(A4n.removeChannels);
			mi.setIcon(null);
			cmenu.add(mi);*/
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
			cmenu.show(e.getComponent(), e.getX(), e.getY());
		}
	}
}
