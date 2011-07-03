/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jsampler.view.fantasia;

import java.awt.Component;
import javax.swing.JTree;
import org.jsampler.AudioDeviceModel;
import org.jsampler.CC;
import org.jsampler.view.std.JSDestEffectChooser;
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
			icon = CC.getViewConfig().getSamplerBrowserView().getIcon(value, expanded);
			if(icon != null) setIcon(icon);
			
			return this;
		}
	}
}
