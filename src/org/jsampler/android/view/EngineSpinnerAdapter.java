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

import org.jsampler.CC;
import org.linuxsampler.lscp.SamplerEngine;

import static org.jsampler.android.view.AndroidI18n.i18n;

public class EngineSpinnerAdapter extends AbstractSpinnerAdapter<SamplerEngine> {
	private SamplerEngine[] engines = CC.getSamplerModel().getEngines();
	
	@Override
	public int
	size() { return engines.length; }
	
	@Override
	public Object
	get(int position) { return engines[position]; }
	
	@Override
	public boolean
	compare(SamplerEngine se1, SamplerEngine se2) {
		if(se1 == null || se2 == null) return false;
		return se1.getName().equals(se2.getName());
	}
	
	public String
	getEmptyItemText() { return i18n.getLabel("EngineSpinnerAdapter.selectEngine"); }
}
