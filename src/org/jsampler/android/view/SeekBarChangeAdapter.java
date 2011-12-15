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

package org.jsampler.android.view;

import android.widget.SeekBar;

public class SeekBarChangeAdapter implements SeekBar.OnSeekBarChangeListener {
	@Override
	public void
	onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { }
	
	@Override
	public void
	onStartTrackingTouch(SeekBar seekBar) { }
	
	@Override
	public void
	onStopTrackingTouch(SeekBar seekBar) { }
}
