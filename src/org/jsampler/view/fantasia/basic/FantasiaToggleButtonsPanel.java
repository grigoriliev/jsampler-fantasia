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

package org.jsampler.view.fantasia.basic;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;

import java.awt.Paint;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JToggleButton;
import javax.swing.plaf.basic.BasicButtonUI;

import org.jsampler.view.fantasia.basic.FantasiaPainter.RoundCorners;


/**
 *
 * @author Grigor Iliev
 */
public class FantasiaToggleButtonsPanel extends FantasiaSubPanel {
	public final Vector<JToggleButton> buttons = new Vector<JToggleButton>();
	protected final ButtonGroup buttonGroup = new ButtonGroup();
	protected boolean dark;
	
	public
	FantasiaToggleButtonsPanel(int buttonNumber) {
		this(buttonNumber, true);
	}
	
	public
	FantasiaToggleButtonsPanel(int buttonNumber, boolean dark) {
		super(true, false, false);
		this.dark = dark;
		
		if(buttonNumber < 2) {
			throw new IllegalArgumentException("buttonNumber should be greater than 1");
		}
		
		buttons.add(new FirstButton());
		for(int i = 1; i < buttonNumber - 1; i++) {
			buttons.add(new MiddleButton());
		}
		buttons.add(new LastButton());
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		for(JToggleButton btn : buttons) {
			btn.setText("  3  ");
			buttonGroup.add(btn);
			add(btn);
		}
	}
	
	private class BasicButton extends JToggleButton {
		BasicButton() {
			setBorderPainted(false);
			setContentAreaFilled(false);
			setRolloverEnabled(true);
			if(!dark) setForeground(new Color(0xd4d4d4));
			setFont(getFont().deriveFont(11.0f));
			//setFont(getFont().deriveFont(Font.BOLD));
		}
		
		@Override
		public void
		updateUI() { setUI(new BasicButtonUI()); }
		
		protected void
		paintButton(Graphics g, FantasiaPainter.RoundCorners rc) {
			Graphics2D g2 = (Graphics2D)g;
			
			Paint oldPaint = g2.getPaint();
			Composite oldComposite = g2.getComposite();
			
			double w = getSize().getWidth();
			double h = getSize().getHeight();
			
			Color c1, c2;
			if(dark) {
				c1 = getModel().isRollover() ?
					FantasiaPainter.color4 : FantasiaPainter.color2;
				
				c2 = getModel().isRollover() ?
					FantasiaPainter.color2 : FantasiaPainter.color1;
				
				if(getModel().isSelected() || getModel().isPressed()) {
					c1 = FantasiaPainter.color2;
					c2 = FantasiaPainter.color4;
				}
			} else {
				c1 = getModel().isRollover() ?
					FantasiaPainter.color6 : FantasiaPainter.color5;
				
				c2 = getModel().isRollover() ?
					FantasiaPainter.color5 : FantasiaPainter.color4;
				
				if(getModel().isSelected() || getModel().isPressed()) {
					c1 = FantasiaPainter.color6;
					c2 = FantasiaPainter.color7;
				}
			}
			
			FantasiaPainter.paintGradient(g2, 0, 0, w - 1, h - 1, c1, c2);
			
			
			if(getModel().isPressed()) {
				FantasiaPainter.paintInnerBorder(g2, 0, 0, w - 1, h - 1, false, 0.5f, 1.0f);
			} else {
				FantasiaPainter.paintOuterBorder(g2, 0, 0, w - 1, h - 1, rc);
			}
			
			g2.setComposite(oldComposite);
			g2.setPaint(oldPaint);
			
			super.paintComponent(g);
		}
	}
	
	private class FirstButton extends BasicButton {
		@Override
		protected void
		paintComponent(Graphics g) {
			RoundCorners rc = new RoundCorners(true, true, false, false);
			paintButton(g, rc);
		}
	}
	
	private class MiddleButton extends BasicButton {
		@Override
		protected void
		paintComponent(Graphics g) {
			RoundCorners rc = new RoundCorners(false, false, false, false);
			paintButton(g, rc);
		}
	}
	
	private class LastButton extends BasicButton {
		@Override
		protected void
		paintComponent(Graphics g) {
			RoundCorners rc = new RoundCorners(false, false, true, true);
			paintButton(g, rc);
		}
	}
}
