/*
 *   JSampler - a front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2023 Grigor Iliev <grigor@grigoriliev.com>
 *
 *   This file is part of JSampler.
 *
 *   JSampler is free software: you can redistribute it and/or modify it under
 *   the terms of the GNU General Public License as published by the Free
 *   Software Foundation, either version 3 of the License, or (at your option)
 *   any later version.
 *
 *   JSampler is distributed in the hope that it will be useful, but WITHOUT
 *   ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *   FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 *   more details.
 *
 *   You should have received a copy of the GNU General Public License along
 *   with JSampler. If not, see <https://www.gnu.org/licenses/>.
 */

package com.grigoriliev.jsampler.fantasia.view.basic;

import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import javax.swing.plaf.basic.BasicComboBoxEditor;

import com.grigoriliev.jsampler.fantasia.view.Res;

import org.pushingpixels.substance.api.combo.ComboPopupPrototypeCallback;

/**
 *
 * @author Grigor Iliev
 */
public class FantasiaComboBox extends JComboBox {
	public
	FantasiaComboBox() {
		setOpaque(true);
		setBackground(new java.awt.Color(0x818181));
		setBorder(BorderFactory.createEmptyBorder());
		setRenderer(new FantasiaListCellRenderer());
		//putClientProperty(SubstanceLookAndFeel.COLORIZATION_FACTOR, 1.0);
		//putClientProperty(SubstanceLookAndFeel.FLAT_PROPERTY, Boolean.TRUE);
		//putClientProperty(SubstanceLookAndFeel.PAINT_ACTIVE_PROPERTY, Boolean.TRUE);
	}

	@Override
	public void
	updateUI() { setUI(new FantasiaComboBoxUI(this)); }
	
	public static class WidestComboPopupPrototype implements ComboPopupPrototypeCallback {
		@Override
		public Object
		getPopupPrototypeDisplayValue(JComboBox jc) {
			Object prototype = "";
			for(int i = 0; i < jc.getItemCount(); i++) {
				if(jc.getItemAt(i).toString().length() > prototype.toString().length()) {
					prototype = jc.getItemAt(i);
				}
			}
			return prototype;
		}
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
