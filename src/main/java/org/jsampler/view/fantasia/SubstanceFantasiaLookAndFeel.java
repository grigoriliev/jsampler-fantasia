/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2010 Grigor Iliev <grigor@grigoriliev.com>
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
import java.awt.Graphics;
import java.awt.Shape;

import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.painter.border.SubstanceBorderPainter;
import org.pushingpixels.substance.api.painter.fill.SubstanceFillPainter;
import org.pushingpixels.substance.api.skin.GraphiteSkin;

/**
 *
 * @author Grigor Iliev
 */
public class SubstanceFantasiaLookAndFeel extends SubstanceLookAndFeel {
	public
	SubstanceFantasiaLookAndFeel() {
		super(new SubstanceFantasiaSkin());
	}
}

class SubstanceFantasiaSkin extends GraphiteSkin {
	protected SubstanceBorderPainter baseBorderPainter;
	protected SubstanceFillPainter basefillPainter;

	public
	SubstanceFantasiaSkin() {
		baseBorderPainter = borderPainter;
		borderPainter = new FantasiaBorderPainter(baseBorderPainter);

		basefillPainter = fillPainter;
		fillPainter =  new FantasiaFillPainter(fillPainter);
	}
}

class FantasiaBorderPainter implements SubstanceBorderPainter {
	private SubstanceBorderPainter baseBorderPainter;

	public
	FantasiaBorderPainter(SubstanceBorderPainter baseBorderPainter)
	{ this.baseBorderPainter = baseBorderPainter; }

	@Override
	public boolean isPaintingInnerContour()
	{ return baseBorderPainter.isPaintingInnerContour(); }

	@Override
	public void paintBorder (
		Graphics g, Component c, int width, int height,
		Shape contour, Shape innerContour, SubstanceColorScheme borderScheme
	) {
		//if(c instanceof FantasiaComboBox) return;

		baseBorderPainter.paintBorder(g, c, width, height, contour, innerContour, borderScheme);
	}

	@Override
	public String
	getDisplayName()
	{ return baseBorderPainter.getDisplayName(); }
}

class FantasiaFillPainter implements SubstanceFillPainter {
	private SubstanceFillPainter basefillPainter;

	public
	FantasiaFillPainter(SubstanceFillPainter basefillPainter)
	{ this.basefillPainter = basefillPainter; }

	@Override
	public void
	paintContourBackground (
		Graphics g, Component comp, int width,
		int height, Shape contour, boolean isFocused,
		SubstanceColorScheme fillScheme, boolean hasShine
	) {
		//if(comp instanceof FantasiaComboBox) return;

		basefillPainter.paintContourBackground (
			g, comp, width, height, contour, isFocused, fillScheme, hasShine
		);
	}

	@Override
	public String
	getDisplayName()
	{ return basefillPainter.getDisplayName(); }
}
