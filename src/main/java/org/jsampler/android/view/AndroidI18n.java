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

public class AndroidI18n extends net.sf.juife.I18n {
	/** Provides the locale-specific data. */
	public static AndroidI18n i18n = new AndroidI18n();
	
	private
	AndroidI18n() {
		setButtonsBundle("org.jsampler.android.view.langprops.ButtonsLabelsBundle");
		setErrorsBundle("org.jsampler.android.view.langprops.ErrorsBundle");
		setLabelsBundle("org.jsampler.android.view.langprops.LabelsBundle");
		setMenusBundle("org.jsampler.android.view.langprops.MenuLabelsBundle");
		setMessagesBundle("org.jsampler.android.view.langprops.MessagesBundle");
	}
}
