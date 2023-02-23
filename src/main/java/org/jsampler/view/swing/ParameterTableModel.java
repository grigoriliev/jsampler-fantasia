/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2010 Grigor Iliev <grigor@grigoriliev.com>
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

package org.jsampler.view.swing;

import java.awt.Component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;

import javax.swing.border.Border;

import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import org.jsampler.CC;

import org.jsampler.event.ParameterEvent;
import org.jsampler.event.ParameterListener;

import org.linuxsampler.lscp.Parameter;
import org.linuxsampler.lscp.ParameterType;
import org.linuxsampler.lscp.StringListParameter;


/**
 * A tabular data model for representing LSCP parameters.
 * @see ParameterTable
 * @author Grigor Iliev
 */
public class ParameterTableModel extends AbstractTableModel {
	public final static int PARAMETER_NAME_COLUMN = 0;
	public final static int PARAMETER_VALUE_COLUMN = 1;
	
	private Parameter[] parameters;
	
	private final BooleanCellRenderer booleanRenderer = new BooleanCellRenderer();
	private final BooleanCellEditor booleanEditor = new BooleanCellEditor();
	
	private final IntegerCellRenderer integerRenderer = new IntegerCellRenderer();
	private final IntegerCellEditor integerEditor = new IntegerCellEditor();
	
	private final FloatCellRenderer floatRenderer = new FloatCellRenderer();
	private final FloatCellEditor floatEditor = new FloatCellEditor();
	
	private final StringListCellRenderer stringListRenderer = new StringListCellRenderer();
	private final StringListCellEditor stringListEditor = new StringListCellEditor();
	
	private boolean editFixedParameters;
	
	
	/**
	 * Creates a new instance of <code>ParameterTableModel</code>.
	 * @param parameters The parameters to be provided by this model.
	 */
	public
	ParameterTableModel(Parameter[] parameters) {
		this(parameters, false);
	}
	
	/**
	 * Creates a new instance of <code>ParameterTableModel</code>.
	 * @param parameters The parameters to be provided by this model.
	 * @param editFixedParameters Determines whether fixed parameters are editable.
	 * Specify <code>true</code> to enable the editing of fixed parameters.
	 */
	public
	ParameterTableModel(Parameter[] parameters, boolean editFixedParameters) {
		setParameters(parameters);
		setEditFixedParameters(editFixedParameters);
	}
	
	private final Vector<ParameterListener> parameterListeners =
		new Vector<ParameterListener>();
	
	/**
	 * Registers the specified listener for receiving event messages.
	 * @param l The <code>ParameterListener</code> to register.
	 */
	public void
	addParameterListener(ParameterListener l) { parameterListeners.add(l); }
	
	/**
	 * Removes the specified listener.
	 * @param l The <code>ParameterListener</code> to remove.
	 */
	public void
	removeParameterListener(ParameterListener l) { parameterListeners.remove(l); }
	
	/**
	 * Sets whether fixed parameters are editable.
	 * @param b Specify <code>true</code> to enable the editing of fixed parameters.
	 * @see org.linuxsampler.lscp.Parameter#isFixed
	 */
	public void
	setEditFixedParameters(boolean b) { editFixedParameters = b; }
	
	/**
	 * Determines whether fixed parameters are editable.
	 * @return <code>true</code> if the fixed parameters are
	 * editable, <code>false</code> otherwise.
	 * @see org.linuxsampler.lscp.Parameter#isFixed
	 */
	public boolean
	canEditFixedParameters() { return editFixedParameters; }
	
	/**
	 * Gets the parameter at the specified row.
	 * @param row The row number of the parameter to be provided.
	 * @return The parameter at the specified row.
	 */
	public Parameter
	getParameter(int row) { return parameters[row]; }
	
	/**
	 * Returns an appropriate renderer for the cell specified by
	 * <code>row</code> and <code>column</code>.
	 * @param row The row of the cell to render, where 0 is the first row.
	 * @param column The column of the cell to render, where 0 is the first column.
	 */
	public TableCellRenderer
	getCellRenderer(int row, int column) {
		if(column != PARAMETER_VALUE_COLUMN) return null;
		
		if(parameters[row].getType() == ParameterType.BOOL) return booleanRenderer;
		else if(parameters[row].getType() == ParameterType.INT) return integerRenderer;
		else if(parameters[row].getType() == ParameterType.FLOAT) return floatRenderer;
		else if(parameters[row].getType() == ParameterType.STRING_LIST) {
			return stringListRenderer;
		}
		
		return null;
	}
	
	/**
	 * Returns an appropriate editor for the cell specified by
	 * <code>row</code> and <code>column</code>.
	 * @param row The row of the cell to edit, where 0 is the first row.
	 * @param column The column of the cell to edit, where 0 is the first column.
	 */
	public TableCellEditor
	getCellEditor(int row, int column) {
		if(column != PARAMETER_VALUE_COLUMN) return null;
		
		Parameter p = parameters[row];
		if(p.getType() == ParameterType.BOOL) return booleanEditor;
		
		if(p.getType() == ParameterType.BOOL_LIST) {
			
		} if(p.getType() == ParameterType.FLOAT_LIST) {
			
		} if(p.getType() == ParameterType.INT_LIST) {
			
		} if(p.getType() == ParameterType.STRING_LIST) {
			// TODO: string list editor with no possibilities
			/*StringListParameter slp = (StringListParameter)p;
			if(slp.hasPossibilities()) {
				
			}*/
			return stringListEditor;
		} else if(p.hasPossibilities()) {
			JComboBox cb = new JComboBox(p.getPossibilities());
			cb.setSelectedItem(null);
			return new DefaultCellEditor(cb);
		} else if(p.getType() == ParameterType.INT) {
			Integer i = p.hasRangeMin() ? p.getRangeMin().intValue() : null;
			integerEditor.setMinimum(i);
			
			i = p.hasRangeMax() ? p.getRangeMax().intValue() : null;
			integerEditor.setMaximum(i);
			
			return integerEditor;
		} else if(p.getType() == ParameterType.FLOAT) {
			Float f = p.hasRangeMin() ? p.getRangeMin().floatValue() : null;
			floatEditor.setMinimum(f);
			
			f = p.hasRangeMax() ? p.getRangeMax().floatValue() : null;
			floatEditor.setMaximum(f);
			
			return floatEditor;
		}
		
		return null;
	}
	
	/** Gets the parameters that are shown in the table. */
	public Parameter[]
	getParameters() { return parameters; }
	
	/**
	 * Sets the parameters to be shown in the table.
	 * @param parameters The parameters to be shown in the table.
	 */
	public void
	setParameters(Parameter[] parameters) {
		this.parameters = parameters;
		fireTableDataChanged();
	}
	
	/**
	 * Gets the number of columns in the model.
	 * @return The number of columns in the model.
	 */
	public int
	getColumnCount() { return 2; }
	
	/**
	 * Gets the number of rows in the model.
	 * @return The number of rows in the model.
	 */
	public int
	getRowCount() { return parameters.length; }
	
	/**
	 * Gets the name of the column at <code>columnIndex</code>.
	 * @return The name of the column at <code>columnIndex</code>.
	 */
	public String
	getColumnName(int col) { return " "; }
	
	/**
	 * Gets the value for the cell at <code>columnIndex</code> and
	 * <code>rowIndex</code>.
	 * @param row The row whose value is to be queried.
	 * @param col The column whose value is to be queried.
	 * @return The value for the cell at <code>columnIndex</code> and
	 * <code>rowIndex</code>.
	 */
	public Object
	getValueAt(int row, int col) {
		switch(col) {
		case PARAMETER_NAME_COLUMN:
			return parameters[row].getName();
		case PARAMETER_VALUE_COLUMN:
			return parameters[row].getValue();
		}
		
		return null;
	}
	
	/**
	 * Sets the value in the cell at <code>col</code>
	 * and <code>row</code> to <code>value</code>.
	 */
	public void
	setValueAt(Object value, int row, int col) {
		if(col != PARAMETER_VALUE_COLUMN) return;
		
		parameters[row].setValue(value);
		fireTableCellUpdated(row,  col);
		fireParameterChanged(parameters[row]);
	}
	
	/**
	 * Returns <code>true</code> if the cell at
	 * <code>row</code> and <code>col</code> is editable.
	 */
	public boolean
	isCellEditable(int row, int col) {
		switch(col) {
		case PARAMETER_NAME_COLUMN:
			return false;
		case PARAMETER_VALUE_COLUMN:
			return canEditFixedParameters() || !parameters[row].isFixed();
		default: return false;
		}
	}
	
	private void
	fireParameterChanged(Parameter p) {
		ParameterEvent e = new ParameterEvent(this, p);
		for(ParameterListener l : parameterListeners) l.parameterChanged(e);
	}
	
	class BooleanCellRenderer extends JCheckBox implements TableCellRenderer {
		private final Border emptyBorder = BorderFactory.createEmptyBorder(1, 1, 1, 1);
		
		BooleanCellRenderer() {
			setHorizontalAlignment(CENTER);
			setBorderPainted(true);
		}
		
		public Component
		getTableCellRendererComponent (
			JTable table,
			Object value,
			boolean isSelected,
			boolean hasFocus,
			int row,
			int column
		) {
			if (isSelected) {
				setBackground(table.getSelectionBackground());
				setForeground(table.getSelectionForeground());
			} else {
				setBackground(table.getBackground());
				setForeground(table.getForeground());
			}
			
			Boolean b = (Boolean) value;
			setSelected(b != null && b);
			
			if(hasFocus) {
				setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
			} else { setBorder(emptyBorder); }
			
			return this;
		}
	}
	
	class BooleanCellEditor extends DefaultCellEditor {
		BooleanCellEditor() {
			super(new JCheckBox());
			JCheckBox cb = (JCheckBox)getComponent();
			cb.setHorizontalAlignment(JCheckBox.CENTER);
		}
	}
	
	class IntegerCellEditor extends NumberCellEditor {
		
	}
	
	class IntegerCellRenderer extends DefaultTableCellRenderer {
		IntegerCellRenderer() {
			setHorizontalAlignment(RIGHT);
		}
	}
	
	class FloatCellEditor extends NumberCellEditor {
		FloatCellEditor() {
			SpinnerNumberModel model = new SpinnerNumberModel(0.0, 0.0, 0.0, 1.0);
			setModel(model);
		}
	}
	
	class FloatCellRenderer extends DefaultTableCellRenderer {
		FloatCellRenderer() {
			setHorizontalAlignment(RIGHT);
		}
	}
	
	class StringListCellEditor extends AbstractCellEditor implements TableCellEditor {
		private final JButton editor = new JButton();
		private final JPopupMenu menu = ((ViewConfig)CC.getViewConfig()).createMultiColumnPopupMenu();
		private final Vector<JCheckBoxMenuItem> menuItems = new Vector<JCheckBoxMenuItem>();
		
		StringListCellEditor() {
			editor.setBorderPainted(false);
			editor.setContentAreaFilled(false);
			editor.setFocusPainted(false);
			editor.setMargin(new java.awt.Insets(0, 0, 0, 0));
			editor.setFont(editor.getFont().deriveFont(java.awt.Font.PLAIN));
			
			editor.addActionListener(new ActionListener() {
				public void
				actionPerformed(ActionEvent e) { menu.show(editor, 0, 0); } 
			});
			
			menu.addPopupMenuListener(new PopupMenuListener() {
				public void
				popupMenuCanceled(PopupMenuEvent e) {
					StringListCellEditor.this.cancelCellEditing();
				}
				
				public void
				popupMenuWillBecomeInvisible(PopupMenuEvent e) { }
				
				public void
				popupMenuWillBecomeVisible(PopupMenuEvent e) { }
			});
		}
		
		private final Vector<String> strings = new Vector<String>();
		
		public Object
		getCellEditorValue() {
			strings.removeAllElements();
			
			for(JCheckBoxMenuItem i : menuItems) {
				if(i.isSelected()) strings.add(i.getText());
			}
			
			return strings.toArray(new String[strings.size()]);
		}
		
		private final StringBuffer sb = new StringBuffer();
		
		public Component
		getTableCellEditorComponent (
			JTable table,
			Object value,
			boolean isSelected,
			int row,
			int column
		) {
			StringListParameter slp = (StringListParameter)parameters[row];
			if(slp.getPossibilities().length == 0) {
				editor.setText("");
				return editor;
			}
			String[] poss = slp.getPossibilities()[0];
			String[] vals = (String[])value;
			
			sb.setLength(0);
			
			if(vals != null) {
				if(vals.length > 0) sb.append(vals[0]);
				
				for(int i = 1; i < vals.length; i++) {
					sb.append(", ").append(vals[i]);
				}
			}
			
			editor.setText(sb.toString());
			
			menu.removeAll();
			menuItems.removeAllElements();
			
			for(String s : poss) {
				JCheckBoxMenuItem item = new JCheckBoxMenuItem(s);
				setListener(item);
				
				for(String s2 : vals) {
					if(s2.equals(s)) item.setSelected(true);
				}
				
				menu.add(item);
				menuItems.add(item);
			}
			
			return editor;
		}
		
		private void
		setListener(JCheckBoxMenuItem i) {
			i.addItemListener(new ItemListener() {
				public void
				itemStateChanged(ItemEvent e) { 
					StringListCellEditor.this.stopCellEditing();
				}
			});
		}
	}
	
	/*private static JComboBox comboBox = new JComboBox();
	class StringListCellEditor extends DefaultCellEditor {
		StringListCellEditor() {
			super(comboBox);
		}
		
		public Object
		getCellEditorValue() {
			Object o = comboBox.getSelectedItem();
			if(o == null) return null;
			String[] sS = new String[1];
			sS[0] = (String)o;
			return sS;
		}
		
		public Component
		getTableCellEditorComponent (
			JTable table,
			Object value,
			boolean isSelected,
			int row,
			int column
		) {
			StringListParameter slp = (StringListParameter)parameters[row];
			comboBox.removeAllItems();
			for(String s : slp.getPossibilities()[0]) comboBox.addItem(s);
			return comboBox;
		}
	}*/
	
	class StringListCellRenderer extends DefaultTableCellRenderer {
		private final StringBuffer sb = new StringBuffer();
		
		public Component
		getTableCellRendererComponent (
			JTable table,
			Object value,
			boolean isSelected,
			boolean hasFocus,
			int row,
			int column
		) {
			if(value instanceof String) return super.getTableCellRendererComponent (
				table, value, isSelected, hasFocus, row, column
			);
			
			String[] s = (String[])value;
			
			sb.setLength(0);
			
			if(s != null) {
				if(s.length > 0) sb.append(s[0]);
				for(int i = 1; i < s.length; i++) sb.append(", ").append(s[i]);
			}
			
			return super.getTableCellRendererComponent (
				table, sb.toString(), isSelected, hasFocus, row, column
			);
		}
	}
}
