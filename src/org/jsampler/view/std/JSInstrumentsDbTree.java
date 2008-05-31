/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2008 Grigor Iliev <grigor@grigoriliev.com>
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

package org.jsampler.view.std;

import java.awt.Component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.KeyStroke;

import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;

import org.jsampler.view.DbDirectoryTreeNode;
import org.jsampler.view.InstrumentsDbTreeModel;

import static org.jsampler.view.std.StdI18n.i18n;

/**
 *
 * @author Grigor Iliev
 */
public class JSInstrumentsDbTree extends org.jsampler.view.AbstractInstrumentsDbTree {
	
	/**
	 * Creates a new instance of <code>JSInstrumentsDbTree</code>.
	 */
	public
	JSInstrumentsDbTree(InstrumentsDbTreeModel model) {
		super(model);
		CellRenderer renderer = new CellRenderer();
		setCellRenderer(renderer);
		
		setBorder(BorderFactory.createEmptyBorder(3, 3, 0, 0));
		
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		
		addMouseListener(new MouseAdapter() {
			public void
			mousePressed(MouseEvent e) {
				if(e.getButton() != e.BUTTON3) return;
				setSelectionPath(getClosestPathForLocation(e.getX(), e.getY()));
			}
		});
		
		ContextMenu contextMenu = new ContextMenu();
		//addMouseListener(contextMenu);
		installKeyboardListeners();
	}
	
	private void
	installKeyboardListeners() {
		AbstractAction a = new AbstractAction() {
			public void
			actionPerformed(ActionEvent e) { }
		};
		a.setEnabled(false);
		getActionMap().put("none", a);
		
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put (
			KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_MASK), "none"
		);
		
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put (
			KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_MASK), "none"
		);
		
		getInputMap(JComponent.WHEN_FOCUSED).put (
			KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_MASK), "none"
		);
		
		getInputMap(JComponent.WHEN_FOCUSED).put (
			KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_MASK), "none"
		);
	}
	
	private class CellRenderer extends DefaultTreeCellRenderer {
		CellRenderer() {
			setOpaque(false);
		}
		
		public Component
		getTreeCellRendererComponent (
			JTree tree,
			Object value,
			boolean sel,
			boolean expanded,
			boolean leaf,
			int row,
			boolean hasFocus
		) {
			super.getTreeCellRendererComponent (
				tree, value, sel,expanded, leaf, row,hasFocus
			);
			
			DbDirectoryTreeNode node = (DbDirectoryTreeNode)value;
			if(node.getInfo().getName() == "/") setIcon(getView().getRootIcon());
			else if(leaf) setIcon(getView().getInstrumentIcon());
			else if(expanded) setIcon(getView().getOpenIcon());
			else setIcon(getView().getClosedIcon());
			
			String s = node.getInfo().getDescription();
			if(s != null && s.length() > 0) setToolTipText(s);
			else setToolTipText(null);
			
			return this;
		}
	}
	
	class ContextMenu extends MouseAdapter {
		private final JPopupMenu cmenu = new JPopupMenu();
		JMenuItem miEdit = new JMenuItem(i18n.getMenuLabel("ContextMenu.edit"));
		
		ContextMenu() {
			cmenu.add(miEdit);
			miEdit.addActionListener(new ActionListener() {
				public void
				actionPerformed(ActionEvent e) {
					
				}
			});
			
			JMenuItem mi = new JMenuItem(i18n.getMenuLabel("ContextMenu.delete"));
			cmenu.add(mi);
			mi.addActionListener(new ActionListener() {
				public void
				actionPerformed(ActionEvent e) {
					removeSelectedDirectory();
				}
			});
			
		}
		
		public void
		mousePressed(MouseEvent e) {
			if(e.isPopupTrigger()) show(e);
		}
	
		public void
		mouseReleased(MouseEvent e) {
			if(e.isPopupTrigger()) show(e);
		}
	
		void
		show(MouseEvent e) {
			cmenu.show(e.getComponent(), e.getX(), e.getY());
		}
	}
}
