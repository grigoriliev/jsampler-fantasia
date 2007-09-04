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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import static org.jsampler.view.fantasia.A4n.a4n;

/**
 *
 * @author Grigor Iliev
 */
public class StandardBar extends PixmapPane {
	private final JToolBar toolbar = new JToolBar();
	private final PixmapPane mainPane;
	
	private final ToolbarButton btnSamplerInfo = new ToolbarButton(a4n.samplerInfo);
	private final ToolbarButton btnLoadSession = new ToolbarButton(a4n.loadScript);
	private final ToolbarButton btnExportSession = new ToolbarButton(a4n.exportSamplerConfig);
	private final ToolbarButton btnRefresh = new ToolbarButton(a4n.refresh);
	private final ToolbarButton btnResetSampler = new ToolbarButton(a4n.resetSampler);
	
	private final ToolbarButton btnLSConsole = new ToolbarButton(a4n.windowLSConsole);
	private final ToolbarButton btnInstrumentsDb = new ToolbarButton(a4n.windowInstrumentsDb);
	
	private final ToolbarButton btnPreferences = new ToolbarButton(a4n.editPreferences);
	
	private final JLabel lLogo = new JLabel(Res.gfxFantasiaLogo);
	
	/** Creates a new instance of <code>StandardBar</code> */
	public
	StandardBar() {
		super(Res.gfxToolBarBg);
		setPixmapInsets(new Insets(0, 6, 6, 6));
		
		setLayout(new BorderLayout());
		setOpaque(false);
		
		Dimension d = new Dimension(60, 60);
		setMinimumSize(d);
		setPreferredSize(d);
		d = new Dimension(Short.MAX_VALUE, 60);
		setMaximumSize(d);
		setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
		
		
		mainPane = new PixmapPane(Res.gfxToolbar);
		mainPane.setPixmapInsets(new Insets(1, 1, 1, 1));
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
		toolbar.add(btnLSConsole);
		toolbar.add(btnInstrumentsDb);
		toolbar.addSeparator();
		toolbar.add(btnPreferences);
		
		mainPane.add(toolbar);
		mainPane.add(Box.createGlue());
		
		mainPane.add(lLogo);
		mainPane.add(Box.createRigidArea(new Dimension(17, 0)));
		add(mainPane);
	}
	
	public void
	showFantasiaLogo(boolean b) { lLogo.setVisible(b); }
	
	private static class FantasiaToolBar extends JToolBar {
		private static Insets pixmapInsets = new Insets(1, 1, 1, 1);
		
		FantasiaToolBar() {
			setOpaque(false);
		}
		
		protected void
		paintComponent(Graphics g) {
			super.paintComponent(g);
			PixmapPane.paintComponent(this, g, Res.gfxToolbar, pixmapInsets);
		}
	}
}
