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
package org.jsampler.view.std;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.TableCellRenderer;
import org.jsampler.view.AbstractSamplerTable;
import org.jsampler.view.SamplerTreeModel.TreeNodeBase;

/**
 *
 * @author Grigor Iliev
 */
public class JSSamplerTable extends AbstractSamplerTable implements SamplerBrowser.ContextMenuOwner {
	private final JSSamplerTree samplerTree;
	private SamplerTableCellRenderer cellRenderer = new SamplerTableCellRenderer();
	
	public
	JSSamplerTable(final JSSamplerTree samplerTree) {
		this.samplerTree = samplerTree;
		setShowGrid(false);
		getColumnModel().setColumnMargin(0);
		
		addMouseListener(new MouseAdapter() {
			public void
			mouseClicked(MouseEvent e) {
				if(e.getButton() != e.BUTTON1) return;
				int r = rowAtPoint(e.getPoint());
				if(r == -1) {
					clearSelection();
					return;
				}
				
				if(e.getClickCount() < 2) return;
				
				openNode();
			}
		});
		
		addMouseListener(new MouseAdapter() {
			public void
			mousePressed(MouseEvent e) {
				int r = rowAtPoint(e.getPoint());
				
				if(e.getButton() != e.BUTTON1 && e.getButton() != e.BUTTON3) return;
				if(r == -1) {
					clearSelection();
					return;
				}
				
				if(e.getButton() != e.BUTTON3) return;
				if(getSelectionModel().isSelectedIndex(r)) {
					getSelectionModel().addSelectionInterval(r, r);
				} else {
					getSelectionModel().setSelectionInterval(r, r);
				}
			}
		});
		
		addMouseListener(new SamplerBrowser.ContextMenu(this));
		
		installKeyboardListeners();
	}
	
	private void
	openNode() {
		TreeNodeBase node = getSelectedNode();
		if(node == null) return;
		if(node.isLink()) {
			samplerTree.setSelectedNode(node.getLink());
		}else if(node.isLeaf()) {
			node.edit();
		} else {
			samplerTree.setSelectedNode(node);
		}
	}

	private void
	installKeyboardListeners() {
		getInputMap().put (
			KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
			"OpenNode"
		);
		
		getActionMap().put ("OpenNode", new AbstractAction() {
			public void
			actionPerformed(ActionEvent e) {
				openNode();
			}
		});
	}
	
	@Override
	public TableCellRenderer
	getCellRenderer(int row, int column) {
		return cellRenderer;
	}
	
	@Override
	public Object
	getSelectedItem() {
		int idx = getSelectedRow();
		if(idx == -1) return null;
		idx = convertRowIndexToModel(idx);
		return getModel().getValueAt(idx, 0);
	}
	
	@Override
	public Object
	getSelectedParent() { return getNode(); }
	
	
	class SamplerTableCellRenderer extends JLabel implements TableCellRenderer {
		
		SamplerTableCellRenderer() {
			setOpaque(true);
			setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
		}
		
		@Override
		public Component
		getTableCellRendererComponent (
			JTable table,
			Object value,
			boolean isSelected,
			boolean hasFocus,
			int row,
			int column
		) {
			if(column == 0 && value != null) {
				setIcon(getView().getIcon(value, false));
			} else {
				setIcon(null);
				setToolTipText(null);
			}
			
			if(value != null) setText(value.toString());
			else setText("");
			
			if (isSelected) {
				setBackground(table.getSelectionBackground());
				setForeground(table.getSelectionForeground());
			} else {
				setBackground(table.getBackground());
				setForeground(table.getForeground());
			}
			
			if(getNode() != null) {
				setHorizontalAlignment(getNode().getHorizontalAlignment(column));
			}
			
			return this;
		}
	}
}
