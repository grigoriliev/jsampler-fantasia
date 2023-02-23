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
package org.jsampler.view.std;

import org.jsampler.AudioDeviceModel;
import org.jsampler.CC;

/**
 *
 * @author Grigor Iliev
 */
public abstract class StdViewConfig extends org.jsampler.view.swing.ViewConfig {
	public static StdViewConfig
	getViewConfig() { return (StdViewConfig)CC.getViewConfig(); }
	
	public JSDestEffectChooser
	createDestEffectChooser(AudioDeviceModel dev) { return new JSDestEffectChooser(dev); }
}
