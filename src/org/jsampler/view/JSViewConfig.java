/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2007 Grigor Iliev <grigor@grigoriliev.com>
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

package org.jsampler.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.jsampler.JSPrefs;

import static org.jsampler.JSPrefs.VOL_MEASUREMENT_UNIT_DECIBEL;

/**
 * Provides the view configuration.
 * @author Grigor Iliev
 */
public abstract class JSViewConfig {
	private boolean measurementUnitDecibel;
	
	/** Creates a new instance of <code>JSViewConfig</code> */
	public
	JSViewConfig() {
		measurementUnitDecibel = preferences().getBoolProperty(VOL_MEASUREMENT_UNIT_DECIBEL);
		
		String s = VOL_MEASUREMENT_UNIT_DECIBEL;
		preferences().addPropertyChangeListener(s, new PropertyChangeListener() {
			public void
			propertyChange(PropertyChangeEvent e) {
				boolean b;
				b = preferences().getBoolProperty(VOL_MEASUREMENT_UNIT_DECIBEL);
				measurementUnitDecibel = b;
			}
		});
	}
	
	/**
	 * Provides UI information for instruments database trees.
	 */
	public abstract InstrumentsDbTreeView getInstrumentsDbTreeView();
	
	/**
	 * Provides UI information for instruments database tables.
	 */
	public abstract InstrumentsDbTableView getInstrumentsDbTableView();
	
	public abstract JSPrefs preferences();
	
	/**
	 * Determines whether this view provides instruments database support.
	 * @return <code>false</code>
	 */
	public boolean
	getInstrumentsDbSupport() { return false; }
	
	/**
	 * Determines whether the volume values should be shown in decibels.
	 */
	public boolean
	isMeasurementUnitDecibel() { return measurementUnitDecibel; }
}
