/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2011 Grigor Iliev <grigor@grigoriliev.com>
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

import java.awt.Component;
import javax.swing.JTree;
import org.jsampler.AudioDeviceModel;
import org.jsampler.view.std.JSDestEffectChooser;
import org.jsampler.view.swing.SHF;
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
