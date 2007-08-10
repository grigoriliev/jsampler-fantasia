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

package org.jsampler.view.classic;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sf.juife.NavigationPage;

import org.jsampler.view.std.JSDbSearchPane;

import static org.jsampler.view.classic.ClassicI18n.i18n;

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
		dbSearchPane.setBackgroundColor(java.awt.Color.WHITE);
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
			return new CriteriaPane(title, mainPane);
		}
	}
	
	class CriteriaPane extends JPanel {
		
		CriteriaPane(String title, final JComponent mainPane) {
			setOpaque(false);
			
			final JToggleButton btn = new JToggleButton();
			btn.setBorderPainted(false);
			btn.setContentAreaFilled(false);
			btn.setFocusPainted(false);
			btn.setIcon(Res.iconBack16);
			btn.setSelectedIcon(Res.iconDown16);
			btn.setMargin(new Insets(0, 0, 0, 0));
			btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			btn.setOpaque(false);
			
			final JLabel l = new JLabel(title);
			l.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			int h = l.getMaximumSize().height;
			Dimension d = new Dimension(Short.MAX_VALUE, h);
			l.setMaximumSize(d);
			l.setOpaque(false);
			
			setLayout(new BorderLayout());
			JPanel p = new JPanel();
			p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
			p.add(l);
			p.add(btn);
			add(p, BorderLayout.NORTH);
			p.setOpaque(false);
			
			mainPane.setVisible(false);
			add(mainPane);
			
			h = getMaximumSize().height;
			d = new Dimension(Short.MAX_VALUE, h);
			setMaximumSize(d);
			
			btn.addActionListener(new ActionListener() {
				public void
				actionPerformed(ActionEvent e) {
					mainPane.setVisible(btn.isSelected());
				}
			});
			
			l.addMouseListener(new MouseAdapter() {
				public void
				mouseClicked(MouseEvent e) {
					if(e.getButton() != e.BUTTON1) return;
					if(e.getClickCount() != 1) return;
					
					btn.doClick();
				}
			});
			
			h = getPreferredSize().height;
			setMaximumSize(new Dimension(Short.MAX_VALUE, h));
			
			setAlignmentX(LEFT_ALIGNMENT);
		}
	}
}
