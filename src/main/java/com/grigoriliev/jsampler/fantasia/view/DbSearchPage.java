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

package com.grigoriliev.jsampler.fantasia.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.grigoriliev.jsampler.fantasia.view.basic.FantasiaTaskPane;
import com.grigoriliev.jsampler.juife.swing.NavigationPage;

import com.grigoriliev.jsampler.swing.view.std.JSDbSearchPane;

import static com.grigoriliev.jsampler.fantasia.view.FantasiaPrefs.*;

/**
 *
 * @author Grigor Iliev
 */
public class DbSearchPage extends NavigationPage {
	private final DbSearchPane dbSearchPane;
	
	/** Creates a new instance of <code>DbSearchPage</code> */
	public
	DbSearchPage(final InstrumentsDbFrame frame) {
		setTitle(FantasiaI18n.i18n.getLabel("DbSearchPage.title"));
		setLayout(new BorderLayout());
		
		dbSearchPane = new DbSearchPane(frame);
		dbSearchPane.setBackgroundColor(new java.awt.Color(0x626262));
		add(dbSearchPane);
		
		dbSearchPane.addChangeListener(new ChangeListener() {
			public void
			stateChanged(ChangeEvent e) {
				frame.setSearchResults (
					dbSearchPane.getDirectoryResults(),
					dbSearchPane.getInstrumentResults()
				);
			}
		});
	}
	
	public void
	setSearchPath(String path) { dbSearchPane.setSearchPath(path); }
	
	class DbSearchPane extends JSDbSearchPane {
		DbSearchPane(Frame owner) {
			super(owner);
		}
		
		@Override
		protected JComponent
		createCriteriaPane(String title, JComponent mainPane) {
			final FantasiaTaskPane tp = new FantasiaTaskPane();
			tp.setTitle(title);
			tp.add(mainPane);
			tp.setAlignmentX(LEFT_ALIGNMENT);
			tp.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
			tp.setCollapsed(true);
			tp.setAnimated(preferences().getBoolProperty(ANIMATED));
			
			preferences().addPropertyChangeListener(ANIMATED, new PropertyChangeListener() {
				public void
				propertyChange(PropertyChangeEvent e) {
					tp.setAnimated(preferences().getBoolProperty(ANIMATED));
				}
			});
			
			return tp;
		}
	}
}
