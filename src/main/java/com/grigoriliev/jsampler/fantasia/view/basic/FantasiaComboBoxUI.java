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

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.plaf.basic.BasicComboBoxUI;

import javax.swing.plaf.basic.ComboPopup;

import com.grigoriliev.jsampler.fantasia.view.Res;

import org.pushingpixels.substance.internal.utils.combo.SubstanceComboPopup;

/**
 *
 * @author Grigor Iliev
 */
public class FantasiaComboBoxUI extends BasicComboBoxUI {
	private JComboBox combo;
	
	/** Creates a new instance of <code>FantasiaComboBoxUI</code> */
	public
	FantasiaComboBoxUI(JComboBox combo) {
		super();
		this.combo = combo;
		currentValuePane.setBackground(combo.getBackground());
	}
	
	@Override
	public JButton
	createArrowButton() {
		JButton btn = new ArrowButton();
		return btn;
	}
	
	@Override
	protected ComboPopup
	createPopup() {
		ComboPopup cp = new SubstanceComboPopup(combo);
		cp.getList().setFont(cp.getList().getFont().deriveFont(10.0f));
		
		return cp;
	}
	
	public class ArrowButton extends PixmapButton {
		ArrowButton() {
			super(Res.gfxCbArrow);
			super.setIcon(Res.gfxCbArrow);
			super.setRolloverIcon(Res.gfxCbArrowRO);
			super.setDisabledIcon(Res.gfxCbArrowDisabled);
			setBackground(new java.awt.Color(0x818181));
		}
		
		@Override
		public void
		setIcon(Icon icon) { }

		@Override
		public void
		setRolloverIcon(Icon icon) { }

		@Override
		public void
		setDisabledIcon(Icon icon) { }
	}
}
