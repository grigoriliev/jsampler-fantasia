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

package org.jsampler.view.fantasia;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sf.juife.NavigationPage;

import org.jsampler.view.std.JSDbSearchPane;

import static org.jsampler.view.fantasia.FantasiaI18n.i18n;
import static org.jsampler.view.fantasia.FantasiaPrefs.*;

/**
 *
 * @author Grigor Iliev
 */
public class DbSearchPage extends NavigationPage {
	private final DbSearchPane dbSearchPane;
	
	/** Creates a new instance of <code>DbSearchPage</code> */
	public
	DbSearchPage(final InstrumentsDbFrame frame) {
		setTitle(i18n.getLabel("DbSearchPage.title"));
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
	
	class DbSearchPane extends JSDbSearchPane {
		DbSearchPane(Frame owner) {
			super(owner);
		}
		
		protected JComponent
		createCriteriaPane(String title, JComponent mainPane) {
			final TaskPane tp = new TaskPane();
			tp.setTitle(title);
			tp.add(mainPane);
			tp.setAlignmentX(LEFT_ALIGNMENT);
			tp.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
			tp.setExpanded(false);
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
