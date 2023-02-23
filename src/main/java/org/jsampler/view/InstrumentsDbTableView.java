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

package org.jsampler.view;

/**
 * Provides UI information for instruments database tables.
 * @author Grigor Iliev
 */
public interface InstrumentsDbTableView<I> {
	/** Gets the folder icon. */
	public I getFolderIcon();
	
	/** Gets the default instrument icon. */
	public I getInstrumentIcon();
	
	/** Gets the icon used for representing GigaSamppler instruments. */
	public I getGigInstrumentIcon();
}
