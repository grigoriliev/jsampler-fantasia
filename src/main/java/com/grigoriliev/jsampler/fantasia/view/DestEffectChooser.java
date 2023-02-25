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
package com.grigoriliev.jsampler.fantasia.view;

import java.awt.Component;

import javax.swing.JTree;

import com.grigoriliev.jsampler.AudioDeviceModel;
import com.grigoriliev.jsampler.swing.view.SHF;
import com.grigoriliev.jsampler.swing.view.std.JSDestEffectChooser;
import org.pushingpixels.substance.api.renderers.SubstanceDefaultTreeCellRenderer;

/**
 *
 * @author Grigor Iliev
 */
public class DestEffectChooser extends JSDestEffectChooser {
	public
	DestEffectChooser(AudioDeviceModel audioDev) {
		super(audioDev);
		
		CellRenderer renderer = new CellRenderer();
		tree.setCellRenderer(renderer);
	}
	
	private class CellRenderer extends SubstanceDefaultTreeCellRenderer {
		public Component
		getTreeCellRendererComponent (
			JTree tree,
			Object value,
			boolean sel,
			boolean expanded,
			boolean leaf,
			int row,
			boolean hasFocus
		) {
			super.getTreeCellRendererComponent (
				tree, value, sel,expanded, leaf, row,hasFocus
			);
			
			javax.swing.Icon icon;
			icon = SHF.getViewConfig().getSamplerBrowserView().getIcon(value, expanded);
			if(icon != null) setIcon(icon);
			
			return this;
		}
	}
}
