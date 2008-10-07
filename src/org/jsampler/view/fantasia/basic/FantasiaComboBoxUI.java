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

import javax.swing.Icon;
import javax.swing.JButton;

import javax.swing.plaf.basic.ComboPopup;

import org.jsampler.view.fantasia.Res;

/**
 *
 * @author Grigor Iliev
 */
public class FantasiaComboBoxUI extends org.jvnet.substance.SubstanceComboBoxUI {
	
	/** Creates a new instance of <code>FantasiaComboBoxUI</code> */
	public
	FantasiaComboBoxUI() {
	}
	
	@Override
	public JButton
	createArrowButton() {
		JButton btn = new Button();
		return btn;
	}
	
	@Override
	protected ComboPopup
	createPopup() {
		ComboPopup popup = super.createPopup();
		popup.getList().setFont(popup.getList().getFont().deriveFont(10.0f));
		
		return popup;
	}
	
	private class Button extends PixmapButton {
		Button() {
			super(Res.gfxCbArrow);
			super.setIcon(Res.gfxCbArrow);
			super.setRolloverIcon(Res.gfxCbArrowRO);
			setDisabledIcon(Res.gfxCbArrowDisabled);
			setBackground(new java.awt.Color(0x818181));
		}
		
		@Override
		public void
		setIcon(Icon icon) { }
	}
}
