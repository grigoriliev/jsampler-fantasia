/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2009 Grigor Iliev <grigor@grigoriliev.com>
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
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import org.jsampler.CC;
import org.jsampler.view.fantasia.basic.*;

import static org.jsampler.view.fantasia.A4n.a4n;

/**
 *
 * @author Grigor Iliev
 */
public class StandardBar extends JPanel {
	private final JToolBar toolbar = new JToolBar();
	private final JPanel mainPane;
	
	private final ToolbarButton btnSamplerInfo = new ToolbarButton(a4n.samplerInfo);
	private final ToolbarButton btnLoadSession = new ToolbarButton(a4n.loadScript);
	private final ToolbarButton btnExportSession = new ToolbarButton(a4n.exportSamplerConfig);
	private final ToolbarButton btnRefresh = new ToolbarButton(a4n.refresh);
	private final ToolbarButton btnResetSampler = new ToolbarButton(a4n.resetSampler);
	
	protected final ToggleButton btnMidiKeyboard = new ToggleButton();
	private final ToolbarButton btnLSConsole = new ToolbarButton(a4n.windowLSConsole);
	private final ToolbarButton btnInstrumentsDb = new ToolbarButton(a4n.windowInstrumentsDb);
	
	private final ToolbarButton btnPreferences = new ToolbarButton(a4n.editPreferences);
	
	private final JLabel lLogo = new JLabel(Res.gfxFantasiaLogo);

	private final Boolean screenMenuEnabled;
	
	/** Creates a new instance of <code>StandardBar</code> */
	public
	StandardBar() {
		screenMenuEnabled = CC.getViewConfig().isUsingScreenMenuBar();
		//super(Res.gfxToolBarBg);
		//setPixmapInsets(new Insets(0, 6, 6, 6));
		
		setLayout(new BorderLayout());
		setOpaque(false);

		int h = screenMenuEnabled ? 56 : 51;
		Dimension d = new Dimension(60, h);
		setMinimumSize(d);
		setPreferredSize(d);
		d = new Dimension(Short.MAX_VALUE, 51);
		setMaximumSize(d);
		int top = screenMenuEnabled ? 5 : 0;
		setBorder(BorderFactory.createEmptyBorder(top, 5, 2, 5));
		
		
		//mainPane = new PixmapPane(Res.gfxToolBar);
		//mainPane.setPixmapInsets(Res.insetsToolBar);
		mainPane = new MainPane();
		mainPane.setOpaque(false);
		mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.X_AXIS));
		
		toolbar.setOpaque(false);
		toolbar.setFloatable(false);
		
		toolbar.add(btnSamplerInfo);
		toolbar.addSeparator();
		toolbar.add(btnLoadSession);
		toolbar.add(btnExportSession);
		toolbar.add(btnRefresh);
		toolbar.add(btnResetSampler);
		toolbar.addSeparator();
		btnMidiKeyboard.setIcon(Res.iconMidiKeyboard32);
		btnMidiKeyboard.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				boolean b = btnMidiKeyboard.isSelected();
				MainFrame frm = (MainFrame)CC.getMainFrame();
				if(frm == null) return;
				frm.setMidiKeyboardVisible(b);
			}
		});
		toolbar.add(btnMidiKeyboard);
		toolbar.add(btnLSConsole);
		toolbar.add(btnInstrumentsDb);
		toolbar.addSeparator();
		toolbar.add(btnPreferences);
		
		mainPane.add(toolbar);
		mainPane.add(Box.createGlue());
		
		mainPane.add(lLogo);
		mainPane.add(Box.createRigidArea(new Dimension(17, 0)));
		FantasiaSubPanel fsp = new FantasiaSubPanel(true, false, false);
		fsp.add(mainPane);
		add(fsp);
	}
	
	public void
	showFantasiaLogo(boolean b) { lLogo.setVisible(b); }
	
	private Color midColor = new Color(0x797979);
	
	@Override
	public void
	paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		
		Paint oldPaint = g2.getPaint();
		Composite oldComposite = g2.getComposite();
		
		double h = getSize().getHeight();
		double w = getSize().getWidth();
		
		FantasiaPainter.paintGradient(g2, 0.0, 0.0, w - 1, h - 1, FantasiaPainter.color5, midColor);

		boolean paintTop = screenMenuEnabled;
		FantasiaPainter.Border b = new FantasiaPainter.Border(paintTop, true, false, true);
		FantasiaPainter.paintBoldOuterBorder(g2, 0, 0, w - 1, h + 2, b);
		
		g2.setPaint(oldPaint);
		g2.setComposite(oldComposite);
	}
	
	class ToggleButton extends JToggleButton {
		/** Creates a new instance of <code>ToolbarButton</code>. */
		ToggleButton() {
			setFocusable(false);
		}
		
		/** Creates a new instance of <code>ToolbarButton</code>. */
		public
		ToggleButton(Action a) {
			super(a);
			setFocusable(false);
		}
		
		/** This method does nothing. */
		@Override
		public void
		setText(String text) { /* We don't want any text in toolbar buttons */ }
	}
	
	class MainPane extends JPanel {
		private final Color color1 = new Color(0x505050);
		private final Color color2 = new Color(0x3e3e3e);
	
		@Override
		public void
		paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D)g;
			
			Paint oldPaint = g2.getPaint();
			Composite oldComposite = g2.getComposite();
			
			double h = getSize().getHeight();
			double w = getSize().getWidth();
			
			FantasiaPainter.paintGradient(g2, 0.0, 0.0, w - 1, h - 1, color1, color2);
			
			FantasiaPainter.paintOuterBorder(g2, 0, 0, w - 1, h - 1, true, 0.5f, 0.3f);
			
			g2.setPaint(oldPaint);
			g2.setComposite(oldComposite);
		}
	}
}
