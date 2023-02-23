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

package org.jsampler.view.classic;

import java.awt.Dialog;
import java.awt.Frame;

import javax.swing.Action;
import javax.swing.JButton;

import org.jsampler.view.swing.std.JSDbInstrumentChooser;
import org.jsampler.view.swing.std.JSInstrumentChooser;

/**
 *
 * @author Grigor Iliev
 */
public class ClassicUtils {
	
	/** Forbits the instantiation of the class */
	private
	ClassicUtils() { }
	
	public static JSInstrumentChooser
	createInstrumentChooser(Frame owner) {
		return new InstrumentChooser(owner);
	}
	
	private static class InstrumentChooser extends JSInstrumentChooser {
		InstrumentChooser(Frame owner) {
			super(owner);
		}
		
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
	}
}
