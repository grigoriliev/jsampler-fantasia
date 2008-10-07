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

package org.jsampler.view.fantasia.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;

import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

import javax.swing.border.Border;

import javax.swing.plaf.ComponentUI;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.plaf.basic.BasicTaskPaneUI;

import org.jsampler.view.fantasia.Res;

/**
 *
 * @author Grigor Iliev
 */
public class FantasiaTaskPane extends JXTaskPane {
	public
	FantasiaTaskPane() {
		setUI(new FantasiaTaskPaneUI());
		
		JComponent c = (JComponent)getContentPane();
		while(c != null) {
			c.setOpaque(false);
			c = (JComponent)c.getParent();
		}
		setOpaque(false);
	}
}

class FantasiaTaskPaneUI extends BasicTaskPaneUI {
	private final Color titleColor = new Color(0xaaaaaa);
	
	public static ComponentUI
	createUI(JComponent c) { return new FantasiaTaskPaneUI(); }
	
	@Override
	protected Border
	createContentPaneBorder() {
		return BorderFactory.createEmptyBorder(0, 0, 0, 0);
	}
	
	@Override
	protected Border
	createPaneBorder() {
		//return super.createPaneBorder();
		return new FantasiaPaneBorder();
	}
	
	@Override
	public void
	paint(Graphics g, JComponent c) {
		double h = c.getSize().getHeight();
		double w = c.getSize().getWidth();
		FantasiaPainter.paintDarkGradient((Graphics2D)g, 1, 1, w - 3, h - 3);
		
		super.paint(g, c);
	}
	
	private class FantasiaPaneBorder extends PaneBorder {
		protected ImageIcon expandedIcon;
		protected ImageIcon collapsedIcon;
		
		FantasiaPaneBorder() {
			//super(5, 5, 5, 5);
			this.expandedIcon = Res.iconArrowUp;
			this.collapsedIcon = Res.iconArrowDown;
		}
		
		@Override
		public void
		paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
			Graphics2D g2 = (Graphics2D)g;
			
			Paint oldPaint = g2.getPaint();
			Composite oldComposite = g2.getComposite();
			
			int titleX = 3;
			int titleY = 1;
			int titleWidth = group.getWidth() - getTitleHeight(group) - 3;
			int titleHeight = getTitleHeight(group);
			paintTitle(group, g, titleColor, titleX, titleY, titleWidth, titleHeight);
			
			Rectangle2D.Double rect = new Rectangle2D.Double(x + 1, y + 1, width - 3, height - 3);
			g2.setPaint(new Color(0x2b2b2b));
			g2.draw(rect);
			
			int x2 = x + width - 1;
			int y2 = y + height - 1;
			FantasiaPainter.paintOuterBorder(g2, x + 2, y + 2, x2 - 2, y2 - 2, true, 0.5f, 1.0f);
			FantasiaPainter.paintInnerBorder(g2, x, y, x2, y2, true);
			
			int controlWidth = getTitleHeight(group) - 2 * getRoundHeight();
			int controlX = group.getWidth() - getTitleHeight(group);
			int controlY = getRoundHeight() - 1;
		
			g2.setPaint(oldPaint);
			g2.setComposite(oldComposite);
			
			paintExpandedControls(group, g, controlX, controlY, controlWidth, controlWidth);
		}

		@Override
		public Insets
		getBorderInsets(Component c) {
			return new Insets(getTitleHeight(c), 5, 3, 5);
		}
		
		@Override
		protected void
		paintExpandedControls(JXTaskPane group, Graphics g, int x, int y, int width, int height) {
			Icon arrowIcon = group.isCollapsed() ? collapsedIcon : expandedIcon;
			int iconHeight = arrowIcon.getIconHeight();
			arrowIcon.paintIcon(group, g, x + 3, y + (height - iconHeight) / 2 + 2);
		}
		
		@Override
		public Dimension
		getPreferredSize(JXTaskPane group) {
			// calculate the title width so it is fully visible
			// it starts with the title width
			configureLabel(group);
			Dimension dim = label.getPreferredSize();
			// add the title left offset
			dim.width += 3;
			// add the controls width
			dim.width += getTitleHeight(group);
			// and some space between label and controls
			dim.width += 3;

			dim.height = getTitleHeight(group) + 3;
			return dim;
		}
	}
}
