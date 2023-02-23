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
package org.jsampler;

import java.util.ArrayList;

import org.linuxsampler.lscp.Effect;

/**
 * TODO: The set of available internal effects can change at runtime.
 * @author Grigor Iliev
 */
public class EffectList {
	private ArrayList<Effect> effects = new ArrayList<Effect>();
	
	public
	EffectList() {
		
	}
	
	public void
	setEffects(Effect[] fxS) {
		effects.clear();
		for(Effect e : fxS) effects.add(e);
	}
	
	public Effect
	getEffect(int idx) { return effects.get(idx); }
	
	public int
	getEffectCount() { return effects.size(); }
}
