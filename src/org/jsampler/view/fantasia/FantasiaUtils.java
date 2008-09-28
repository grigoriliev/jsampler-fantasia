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

import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicLabelUI;

import org.jsampler.view.InstrumentsDbTreeModel;

import org.jsampler.view.std.JSDbInstrumentChooser;
import org.jsampler.view.std.JSInstrumentChooser;
import org.jsampler.view.std.JSInstrumentsDbTree;


/**
 *
 * @author Grigor Iliev
 */
public class FantasiaUtils {
	
	/** Forbits the instantiation of the class */
	private
	FantasiaUtils() { }
	
	public static JComboBox
	createEnhancedComboBox() {
		return new JComboBox();
	}
	
	public static JSInstrumentChooser
	createInstrumentChooser(Frame owner) {
		return new InstrumentChooser(owner);
	}
	
	public static JLabel
	createScreenLabel(String s) { return new ScreenLabel(s); }
	
	public static JButton
	createScreenButton(String s) { return new ScreenButton(s); }
	
	public static JToolBar
	createSubToolBar() { return new ToolBar(); }
	
	public static JPanel
	createBottomSubPane() { return new BottomSubPane(); }
	
	
	private static class InstrumentChooser extends JSInstrumentChooser {
		InstrumentChooser(Frame owner) {
			super(owner);
		}
		
		protected JComboBox
		createComboBox() { return createEnhancedComboBox(); }
	
		@Override
		protected JSDbInstrumentChooser
		createDbInstrumentChooser(Dialog owner) {
			return new DbInstrumentChooser(owner);
		}
	}
	
	private static class DbInstrumentChooser extends JSDbInstrumentChooser {
		DbInstrumentChooser(Dialog owner) {
			super(owner);
		}
		
		@Override
		protected JButton
		createToolbarButton(Action a) { return new ToolbarButton(a); }
		
		@Override
		protected JSInstrumentsDbTree
		createInstrumentsDbTree(InstrumentsDbTreeModel m) {
			return new FantasiaInstrumentsDbTree(m);
		}
	}
	
	private static class ScreenLabel extends JLabel {
		ScreenLabel() { this(""); }
		
		ScreenLabel(String s) {
			super(s);
			setFont(Res.fontScreen);
			setForeground(new java.awt.Color(0xFFA300));
		}
		
		@Override
		protected void
		paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D)g;
			
			g2d.setRenderingHint (
				java.awt.RenderingHints.KEY_TEXT_ANTIALIASING,
				java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON
			);
			
			super.paintComponent(g2d);
		}
		
		@Override
		public void
		updateUI() { setUI(new BasicLabelUI()); }
	}
	
	private static class ScreenButton extends JButton {
		protected
		ScreenButton(String s) {
			super(s);
			setContentAreaFilled(false);
			setFocusPainted(false);
			setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
			setMargin(new Insets(0, 0, 0, 0));
			
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			setFont(Res.fontScreen);
			setForeground(new java.awt.Color(0xFFA300));
		}
		
		@Override
		protected void
		paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D)g;
			
			g2d.setRenderingHint (
				java.awt.RenderingHints.KEY_TEXT_ANTIALIASING,
				java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON
			);
			
			super.paintComponent(g2d);
		}
		
		@Override
		public void
		updateUI() { setUI(new BasicButtonUI()); }
	}
	
	
	
	private static class ToolBar extends JToolBar {
		ToolBar() {
			setFloatable(false);
			setOpaque(false);
			setPreferredSize(new Dimension(77, 29));
			setMinimumSize(getPreferredSize());
			//setBackground(Color.BLACK);
		}
		
		@Override
		protected void
		paintComponent(Graphics g) {
			super.paintComponent(g);
			
			double h = getSize().getHeight();
			double w = getSize().getWidth();
			
			FantasiaPainter.paintGradient((Graphics2D)g, 0, 0, w - 1, h - 1);
			
			FantasiaPainter.RoundCorners rc =
				new FantasiaPainter.RoundCorners(true, false, false, true);
			
			FantasiaPainter.paintOuterBorder((Graphics2D)g, 0, 0, w - 1, h - 1, rc);
		}
	}
	
	private static class BottomSubPane extends JPanel {
		BottomSubPane() {
			setLayout(new java.awt.BorderLayout());
			setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		}
		
		@Override
		protected void
		paintComponent(Graphics g) {
			super.paintComponent(g);
			
			Graphics2D g2 = (Graphics2D)g;
			double h = getSize().getHeight();
			double w = getSize().getWidth();
			
			FantasiaPainter.paintGradient(g2, 0, 0, w - 1, h - 1);
			
			FantasiaPainter.RoundCorners rc =
				new FantasiaPainter.RoundCorners(false, true, true, false);
			
			FantasiaPainter.paintOuterBorder(g2, 0, 0, w - 1, h - 1, rc);
			
			FantasiaPainter.paintInnerBorder(g2, 4, 4, w - 5, h - 5, true);
		}
	}
}
