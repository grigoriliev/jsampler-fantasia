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

package org.jsampler.view.fantasia.basic;

import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import org.jsampler.view.fantasia.Res;

/**
 *
 * @author Grigor Iliev
 */
public class MultiColumnMenu extends net.sf.juife.swing.MultiColumnMenu {
	public
	MultiColumnMenu(String s) { super(s); }

	@Override
	protected JPopupMenu
	createPopupMenu() { return new FantasiaPopupMenu(); }

	public static class FantasiaPopupMenu extends PopupMenu {
		@Override
		protected JComponent
		createColumnSeparator() {
			PixmapPane p = new PixmapPane(Res.gfxVLine);
			p.setOpaque(false);
			p.setPreferredSize(new Dimension(2, 2));
			p.setMinimumSize(p.getPreferredSize());
			return p;
		}
	}
}
