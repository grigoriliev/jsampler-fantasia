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

package org.jsampler.view.fantasia.basic;

import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import javax.swing.plaf.basic.BasicComboBoxEditor;

import org.jsampler.view.fantasia.Res;

import org.jvnet.substance.SubstanceLookAndFeel;

/**
 *
 * @author Grigor Iliev
 */
public class FantasiaComboBox extends JComboBox {
	public
	FantasiaComboBox() {
		setUI(new FantasiaComboBoxUI());
		setOpaque(true);
		setBackground(new java.awt.Color(0x818181));
		setBorder(BorderFactory.createEmptyBorder());
		setRenderer(new FantasiaListCellRenderer());
		putClientProperty(SubstanceLookAndFeel.COLORIZATION_FACTOR, 1.0);
		//putClientProperty(SubstanceLookAndFeel.FLAT_PROPERTY, Boolean.TRUE);
		//putClientProperty(SubstanceLookAndFeel.PAINT_ACTIVE_PROPERTY, Boolean.TRUE);
	}
	
	
}

class FantasiaComboBoxEditor extends BasicComboBoxEditor {
	
	/** Creates a new instance of <code>FantasiaComboBoxEditor</code> */
	public
	FantasiaComboBoxEditor() {
	}
	
	@Override
	protected JTextField
	createEditorComponent() { return new TextEditor(); }
	
	static class TextEditor extends JTextField {
		private static Insets pixmapInsets = new Insets(1, 1, 1, 1);
		
		TextEditor() {
			setOpaque(false);
			setBorder(BorderFactory.createEmptyBorder());
		}
		
		@Override
		public void
		setText(String s) {
			if(getText().equals(s)) return;
			setText(s);
		}
		
		@Override
		protected void
		paintComponent(Graphics g) {
			super.paintComponent(g);
			
			PixmapPane.paintComponent(this, g, Res.gfxTextField, pixmapInsets);
		}
	}
}
