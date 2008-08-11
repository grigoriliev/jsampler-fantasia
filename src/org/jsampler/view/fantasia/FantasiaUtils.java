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
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicLabelUI;
import javax.swing.plaf.basic.ComboPopup;

import org.jsampler.view.InstrumentsDbTreeModel;

import org.jsampler.view.std.JSDbInstrumentChooser;
import org.jsampler.view.std.JSInstrumentChooser;
import org.jsampler.view.std.JSInstrumentsDbTree;

import org.jvnet.lafwidget.animation.FadeConfigurationManager;
import org.jvnet.lafwidget.animation.FadeKind;

import org.jvnet.substance.SubstanceComboBoxUI;
import org.jvnet.substance.SubstanceLookAndFeel;

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
	
	
	private static class InstrumentChooser extends JSInstrumentChooser {
		InstrumentChooser(Frame owner) {
			super(owner);
		}
		
		protected JComboBox
		createComboBox() { return createEnhancedComboBox(); }
	
		protected JSDbInstrumentChooser
		createDbInstrumentChooser(Dialog owner) {
			return new DbInstrumentChooser(owner);
		}
	}
	
	private static class DbInstrumentChooser extends JSDbInstrumentChooser {
		DbInstrumentChooser(Dialog owner) {
			super(owner);
		}
		
		protected JButton
		createToolbarButton(Action a) { return new ToolbarButton(a); }
		
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
		
		protected void
		paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D)g;
			
			g2d.setRenderingHint (
				java.awt.RenderingHints.KEY_TEXT_ANTIALIASING,
				java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON
			);
			
			super.paintComponent(g2d);
		}
		
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
		
		protected void
		paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D)g;
			
			g2d.setRenderingHint (
				java.awt.RenderingHints.KEY_TEXT_ANTIALIASING,
				java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON
			);
			
			super.paintComponent(g2d);
		}
		
		public void
		updateUI() { setUI(new BasicButtonUI()); }
	}
}
