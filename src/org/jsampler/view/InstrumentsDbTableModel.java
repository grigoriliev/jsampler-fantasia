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

import java.text.NumberFormat;

import java.util.Comparator;
import java.util.Date;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import net.sf.juife.Task;
import net.sf.juife.event.TaskEvent;
import net.sf.juife.event.TaskListener;

import org.jsampler.CC;
import org.jsampler.task.InstrumentsDb;

import org.linuxsampler.lscp.DbDirectoryInfo;
import org.linuxsampler.lscp.DbInstrumentInfo;

import org.linuxsampler.lscp.event.InstrumentsDbEvent;
import org.linuxsampler.lscp.event.InstrumentsDbListener;

import static org.jsampler.JSI18n.i18n;

/**
 *
 * @author Grigor Iliev
 */
public class InstrumentsDbTableModel extends AbstractTableModel {
	public static enum ColumnType {
		NAME (i18n.getLabel("InstrumentsDbTableModel.NAME")),
		SIZE (i18n.getLabel("InstrumentsDbTableModel.SIZE")),
		FORMAT_FAMILY (i18n.getLabel("InstrumentsDbTableModel.FORMAT_FAMILY")),
		FORMAT_VERSION (i18n.getLabel("InstrumentsDbTableModel.FORMAT_VERSION")),
		IS_DRUM (i18n.getLabel("InstrumentsDbTableModel.IS_DRUM")),
		CREATED (i18n.getLabel("InstrumentsDbTableModel.CREATED")),
		MODIFIED (i18n.getLabel("InstrumentsDbTableModel.MODIFIED")),
		PRODUCT (i18n.getLabel("InstrumentsDbTableModel.PRODUCT")),
		ARTISTS (i18n.getLabel("InstrumentsDbTableModel.ARTISTS")),
		INSTRUMENT_FILE (i18n.getLabel("InstrumentsDbTableModel.INSTRUMENT_FILE")),
		INSTRUMENT_NR (i18n.getLabel("InstrumentsDbTableModel.INSTRUMENT_NR")),
		KEYWORDS (i18n.getLabel("InstrumentsDbTableModel.KEYWORDS")),
		DUMMY ("");
		
		private final String name;
		
		ColumnType(String name) { this.name = name; }
		
		public String
		toString() { return name; }
	}
	
	private Vector<ColumnType> columns = new Vector<ColumnType>();
	
	private boolean showSizeColumn = true;
	private boolean showFormatFamilyColumn = true;
	private boolean showFormatVersionColumn = false;
	private boolean showIsDrumColumn = false;
	private boolean showCreatedColumn = false;
	private boolean showModifiedColumn = true;
	private boolean showProductColumn = false;
	private boolean showArtistsColumn = false;
	private boolean showInstrumentFileColumn = false;
	private boolean showInstrumentNrColumn = false;
	private boolean showKeywordsColumn = false;
	
	private DbDirectoryTreeNode directoryNode;
	
	
	/** Creates a new instance of <code>InstrumentsDbTableModel</code>. */
	public
	InstrumentsDbTableModel() {
		this(null);
	}
	
	/** Creates a new instance of <code>InstrumentsDbTableModel</code>. */
	public
	InstrumentsDbTableModel(DbDirectoryTreeNode node) {
		directoryNode = node;
		updateColumns();
	}
	
	/**
	 * Gets the type of the specified column.
	 * @param index The index of the column.
	 */
	public ColumnType
	getColumnType(int index) { return columns.get(index); }
	
	/**
	 * Gets the index of the dummy column.
	 */
	public int
	getDummyColumnIndex() {
		return columns.indexOf(ColumnType.DUMMY);
	}
	
	/** Gets whether the <b>Size</b> column is shown. */
	public boolean
	getShowSizeColumn() { return showSizeColumn; }
	
	/** Sets whether the <b>Size</b> column should be shown. */
	public void
	setShowSizeColumn(boolean b) {
		if(b == showSizeColumn) return;
		showSizeColumn = b;
		updateColumns();
	}
	
	/** Gets whether the <b>Format</b> column is shown. */
	public boolean
	getShowFormatFamilyColumn() { return showFormatFamilyColumn; }
	
	/** Sets whether the <b>Format</b> column should be shown. */
	public void
	setShowFormatFamilyColumn(boolean b) {
		if(b == showFormatFamilyColumn) return;
		showFormatFamilyColumn = b;
		updateColumns();
	}
	
	/** Gets whether the <b>Version</b> column is shown. */
	public boolean
	getShowFormatVersionColumn() { return showFormatVersionColumn; }
	
	/** Sets whether the <b>Version</b> column should be shown. */
	public void
	setShowFormatVersionColumn(boolean b) {
		if(b == showFormatVersionColumn) return;
		showFormatVersionColumn = b;
		updateColumns();
	}
	
	/** Gets whether the <b>Type</b> column is shown. */
	public boolean
	getShowIsDrumColumn() { return showIsDrumColumn; }
	
	/** Sets whether the <b>Type</b> column should be shown. */
	public void
	setShowIsDrumColumn(boolean b) {
		if(b == showIsDrumColumn) return;
		showIsDrumColumn = b;
		updateColumns();
	}
	
	/** Gets whether the <b>Date Created</b> column is shown. */
	public boolean
	getShowCreatedColumn() { return showCreatedColumn; }
	
	/** Sets whether the <b>Date Created</b> column should be shown. */
	public void
	setShowCreatedColumn(boolean b) {
		if(b == showCreatedColumn) return;
		showCreatedColumn = b;
		updateColumns();
	}
	
	/** Gets whether the <b>Date Modified</b> column is shown. */
	public boolean
	getShowModifiedColumn() { return showModifiedColumn; }
	
	/** Sets whether the <b>Date Modified</b> column should be shown. */
	public void
	setShowModifiedColumn(boolean b) {
		if(b == showModifiedColumn) return;
		showModifiedColumn = b;
		updateColumns();
	}
	
	/** Gets whether the <b>Product</b> column is shown. */
	public boolean
	getShowProductColumn() { return showProductColumn; }
	
	/** Sets whether the <b>Product</b> column should be shown. */
	public void
	setShowProductColumn(boolean b) {
		if(b == showProductColumn) return;
		showProductColumn = b;
		updateColumns();
	}
	
	/** Gets whether the <b>Artists</b> column is shown. */
	public boolean
	getShowArtistsColumn() { return showArtistsColumn; }
	
	/** Sets whether the <b>Artists</b> column should be shown. */
	public void
	setShowArtistsColumn(boolean b) {
		if(b == showArtistsColumn) return;
		showArtistsColumn = b;
		updateColumns();
	}
	
	/** Gets whether the <b>Instrument File</b> column is shown. */
	public boolean
	getShowInstrumentFileColumn() { return showInstrumentFileColumn; }
	
	/** Sets whether the <b>Instrument File</b> column should be shown. */
	public void
	setShowInstrumentFileColumn(boolean b) {
		if(b == showInstrumentFileColumn) return;
		showInstrumentFileColumn = b;
		updateColumns();
	}
	
	/** Gets whether the <b>Index</b> column is shown. */
	public boolean
	getShowInstrumentNrColumn() { return showInstrumentNrColumn; }
	
	/** Sets whether the <b>Index</b> column should be shown. */
	public void
	setShowInstrumentNrColumn(boolean b) {
		if(b == showInstrumentNrColumn) return;
		showInstrumentNrColumn = b;
		updateColumns();
	}
	
	/** Gets whether the <b>Keywords</b> column is shown. */
	public boolean
	getShowKeywordsColumn() { return showKeywordsColumn; }
	
	/** Sets whether the <b>Keywords</b> column should be shown. */
	public void
	setShowKeywordsColumn(boolean b) {
		if(b == showKeywordsColumn) return;
		showKeywordsColumn = b;
		updateColumns();
	}
	
	/**
	 * Returns a comparator for the specified column or <code>null</code>
	 * if there is no suitable comparator for the specified column.
	 */
	public Comparator
	getComparator(int col) {
		if(columns.get(col) == ColumnType.NAME) return nameComparator;
		if(columns.get(col) == ColumnType.CREATED) return dateComparator;
		if(columns.get(col) == ColumnType.MODIFIED) return dateComparator;
		if(columns.get(col) == ColumnType.SIZE) return sizeComparator;
		if(columns.get(col) == ColumnType.INSTRUMENT_NR) return numberComparator;
		return null;
	}
	
	/**
	 * Determines whether the specified column is sortable.
	 * @return <code>true</code> if the specified column is
	 * sortable, <code>false</code> otherwise.
	 */
	public boolean
	isSortable(int col) {
		if(columns.get(col) == ColumnType.DUMMY) return false;
		return true;
	}
	
	private NameComparator nameComparator = new NameComparator();
	
	private class NameComparator implements Comparator {
		public int
		compare(Object o1, Object o2) {
			if (o1 instanceof DbInstrumentInfo && o2 instanceof DbDirectoryInfo) {
				return 1;
			}
			if (o1 instanceof DbDirectoryInfo && o2 instanceof DbInstrumentInfo) {
				return -1;
			}
			
			return o1.toString().compareToIgnoreCase(o2.toString());
		}
	}
	
	private DateComparator dateComparator = new DateComparator();
	
	private class DateComparator implements Comparator {
		public int
		compare(Object o1, Object o2) {
			if (o1 instanceof Date && o2 instanceof Date) {
				return ((Date)o1).compareTo((Date)o2);
			}
			
			return o1.toString().compareToIgnoreCase(o2.toString());
		}
	}
	
	private SizeComparator sizeComparator = new SizeComparator();
	
	private class SizeComparator implements Comparator {
		public int
		compare(Object o1, Object o2) {
			if (o1 instanceof InstrumentSize && o2 instanceof InstrumentSize) {
				long l1 = ((InstrumentSize)o1).getSize();
				long l2 = ((InstrumentSize)o2).getSize();
				if(l1 < l2) return -1;
				if(l1 > l2) return 1;
				return 0;
			}
			
			return o1.toString().compareToIgnoreCase(o2.toString());
		}
	}
	
	private NumberComparator numberComparator = new NumberComparator();
	
	private class NumberComparator implements Comparator {
		public int
		compare(Object o1, Object o2) {
			if (o1 instanceof Integer && o2 instanceof Integer) {
				int i1 = (Integer)o1;
				int i2 = (Integer)o2;
				if(i1 < i2) return -1;
				if(i1 > i2) return 1;
				return 0;
			}
			
			return o1.toString().compareToIgnoreCase(o2.toString());
		}
	}
	
	private class InstrumentSize {
		private DbInstrumentInfo instrument;
		
		InstrumentSize(DbInstrumentInfo instr) {
			instrument = instr;
		}
		
		public long
		getSize() { return instrument.getSize(); }
		
		public String
		toString() { return instrument.getFormatedSize(); }
	}
	
	private void
	updateColumns() {
		columns.removeAllElements();
		columns.add(ColumnType.NAME);
		if(showSizeColumn) columns.add(ColumnType.SIZE);
		if(showFormatFamilyColumn) columns.add(ColumnType.FORMAT_FAMILY);
		if(showFormatVersionColumn) columns.add(ColumnType.FORMAT_VERSION);
		if(showIsDrumColumn) columns.add(ColumnType.IS_DRUM);
		if(showCreatedColumn) columns.add(ColumnType.CREATED);
		if(showModifiedColumn) columns.add(ColumnType.MODIFIED);
		if(showProductColumn) columns.add(ColumnType.PRODUCT);
		if(showArtistsColumn) columns.add(ColumnType.ARTISTS);
		if(showInstrumentFileColumn) columns.add(ColumnType.INSTRUMENT_FILE);
		if(showInstrumentNrColumn) columns.add(ColumnType.INSTRUMENT_NR);
		if(showKeywordsColumn) columns.add(ColumnType.KEYWORDS);
		columns.add(ColumnType.DUMMY);
		
		fireTableStructureChanged();
	}
	
	/**
	 * Gets the directory node, which
	 * content is represented by this table model.
	 */
	protected DbDirectoryTreeNode
	getParentDirectoryNode() { return directoryNode; }
	
	/**
	 * Sets the directory node, which
	 * content will be represented by this table model.
	 */
	protected void
	setParentDirectoryNode(DbDirectoryTreeNode node) {
		if(directoryNode != null) directoryNode.removeInstrumentsDbListener(getHandler());
		directoryNode = node;
		if(directoryNode != null) directoryNode.addInstrumentsDbListener(getHandler());
		fireTableDataChanged();
	}
	
	private String renamedInstrument = null;
	
	/**
	 * Gets the name of the last renamed instrument through this table model.
	 */
	public String
	getRenamedInstrument() { return renamedInstrument; }
	
	/**
	 * Sets the name of the last
	 * renamed instrument through this table model.
	 */
	public void
	setRenamedInstrument(String instr) { renamedInstrument = instr; }
	
	private String renamedDirectory = null;
	
	/**
	 * Gets the name of the last
	 * renamed directory through this table model.
	 */
	public String
	getRenamedDirectory() { return renamedDirectory; }
	
	/**
	 * Sets the name of the last
	 * renamed directory through this table model.
	 */
	public void
	setRenamedDirectory(String dir) { renamedDirectory = dir; }
	
	/**
	 * Gets the number of columns in the model.
	 * @return The number of columns in the model.
	 */
	public int
	getColumnCount() { return columns.size(); }
	
	/**
	 * Gets the number of rows in the model.
	 * @return The number of rows in the model.
	 */
	public int
	getRowCount() {
		if(directoryNode == null) return 0;
		return directoryNode.getChildCount() + directoryNode.getInstrumentCount();
	}
	
	/**
	 * Gets the name of the column at <code>columnIndex</code>.
	 * @return The name of the column at <code>columnIndex</code>.
	 */
	public String
	getColumnName(int col) {
		return columns.get(col).toString();
	}
	
	/**
	 * Gets the directory at the specified row index or
	 * <code>null</code> there is no directory at the specified index.
	 */
	public DbDirectoryTreeNode
	getDirectoryNode(int index) {
		if(directoryNode == null) return null;
		if(index >= directoryNode.getChildCount()) return null;
		return directoryNode.getChildAt(index);
	}
	
	/**
	 * Gets the instrument at the specified row index or
	 * <code>null</code> there is no instrument at the specified index.
	 */
	public DbInstrumentInfo
	getInstrument(int index) {
		index -= directoryNode.getChildCount();
		if(index < 0) return null;
		return directoryNode.getInstrumentAt(index);
	}
	
	/**
	 * Gets the row index of the specified directory.
	 * @param dir The name of the directory.
	 * @return The row index of the specified directory or
	 * <code>-1</code> if the specified directory is not found.
	 */
	public int
	getDirectoryRowIndex(String dir) {
		if(dir == null || dir.length() == 0) return -1;
		
		for(int i = 0; i < directoryNode.getChildCount(); i++) {
			if(dir.equals(directoryNode.getChildAt(i).getInfo().getName())) return i;
		}
		
		return -1;
	}
	
	/**
	 * Gets the row index of the specified instrument.
	 * @param instr The name of the instrument.
	 * @return The row index of the specified instrument or
	 * <code>-1</code> if the specified instrument is not found.
	 */
	public int
	getInstrumentRowIndex(String instr) {
		if(instr == null || instr.length() == 0) return -1;
		
		for(int i = 0; i < directoryNode.getInstrumentCount(); i++) {
			if(instr.equals(directoryNode.getInstrumentAt(i).getName())) {
				return i + directoryNode.getChildCount();
			}
		}
		
		return -1;
	}
	
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
		if(directoryNode.getChildCount() > row) {
			DbDirectoryInfo info = directoryNode.getChildAt(row).getInfo();
			
			if(columns.get(col) == ColumnType.NAME) return info;
			if(columns.get(col) == ColumnType.MODIFIED) {
				return info.getDateModified();
			}
			if(columns.get(col) == ColumnType.CREATED) {
				return info.getDateCreated();
			}
			
			return "";
		}
		
		DbInstrumentInfo instr;
		instr = directoryNode.getInstrumentAt(row - directoryNode.getChildCount());
		
		switch(columns.get(col)) {
		case NAME:
			return instr;
		
		case SIZE:
			return new InstrumentSize(instr);
		
		case FORMAT_FAMILY:
			return instr.getFormatFamily();
		
		case FORMAT_VERSION:
			return instr.getFormatVersion();
		
		case IS_DRUM:
			if(instr.isDrum()) return i18n.getLabel("InstrumentsDbTableModel.drumkit");
			return i18n.getLabel("InstrumentsDbTableModel.chromatic");
		
		case CREATED:
			return instr.getDateCreated();
		
		case MODIFIED:
			return instr.getDateModified();
		
		case PRODUCT:
			return instr.getProduct();
		
		case ARTISTS:
			return instr.getArtists();
		
		case INSTRUMENT_FILE:
			return instr.getFilePath();
		
		case INSTRUMENT_NR:
			return instr.getInstrumentIndex();
		
		case KEYWORDS:
			return instr.getKeywords();
		
		case DUMMY:
			return "";
		
		}
		
		return null;
	}
	
	/**
	 * Sets the value in the cell at <code>col</code>
	 * and <code>row</code> to <code>value</code>.
	 */
	public void
	setValueAt(Object value, final int row, final int col) {
		String s = value.toString();
		final String oldName;
		final Task t;
		if(directoryNode.getChildCount() > row) {
			final DbDirectoryInfo info = directoryNode.getChildAt(row).getInfo();
			oldName = info.getName();
			if(oldName.equals(s)) return;
			t = new InstrumentsDb.RenameDirectory(info.getDirectoryPath(), s);
			info.setName(s);
			setRenamedDirectory(info.getName());
			fireTableCellUpdated(row, col);
			t.addTaskListener(new TaskListener() {
				public void
				taskPerformed(TaskEvent e) {
					if(!t.doneWithErrors()) return;
					info.setName(oldName);
					fireTableCellUpdated(row, col);
				}
			});
			CC.getTaskQueue().add(t);
			return;
		}
		
		final DbInstrumentInfo instr;
		instr = directoryNode.getInstrumentAt(row - directoryNode.getChildCount());
		oldName = instr.getName();
		if(oldName.equals(s)) return;
		t = new InstrumentsDb.RenameInstrument(instr.getInstrumentPath(), s);
		instr.setName(s);
		setRenamedInstrument(instr.getName());
		fireTableCellUpdated(row, col);
		t.addTaskListener(new TaskListener() {
			public void
			taskPerformed(TaskEvent e) {
				if(!t.doneWithErrors()) return;
				instr.setName(oldName);
				fireTableCellUpdated(row, col);
			}
		});
		CC.getTaskQueue().add(t);
	}
	
	/**
	 * Returns <code>true</code> if the cell at
	 * <code>row</code> and <code>col</code> is editable.
	 */
	public boolean
	isCellEditable(int row, int col) {
		if(columns.get(col) == ColumnType.NAME) return true;
		return false;
	}
	
	private final EventHandler eventHandler = new EventHandler();
	
	private EventHandler
	getHandler() { return eventHandler; }
	
	private class EventHandler implements InstrumentsDbListener {
		/**
		 * Invoked when the number of instrument
		 * directories in a specific directory has changed.
		 */
		public void
		directoryCountChanged(final InstrumentsDbEvent e) {
			fireTableDataChanged();
		}
		
		/**
		 * Invoked when the settings of an instrument directory are changed.
		 */
		public void
		directoryInfoChanged(InstrumentsDbEvent e) {
			
		}
		
		/**
		 * Invoked when an instrument directory is renamed.
		 */
		public void
		directoryNameChanged(InstrumentsDbEvent e) {
			String d = e.getPathName();
			DbDirectoryInfo dir = getParentDirectoryNode().getInfo();
			if(dir == null || !d.startsWith(dir.getDirectoryPath())) return;
			d = d.substring(dir.getDirectoryPath().length(), d.length());
			if(d.length() == 0) return;
			if(d.charAt(0) == '/') d = d.substring(1, d.length());
			int row = getDirectoryRowIndex(d);
			if(row == -1) return;
			fireTableRowsUpdated(row, row);
		}
		
		/**
		 * Invoked when the number of instruments
		 * in a specific directory has changed.
		 */
		public void
		instrumentCountChanged(final InstrumentsDbEvent e) {
			fireTableDataChanged();
		}
		
		/**
		 * Invoked when the settings of an instrument are changed.
		 */
		public void
		instrumentInfoChanged(InstrumentsDbEvent e) {
			String instr = e.getPathName();
			DbDirectoryInfo dir = getParentDirectoryNode().getInfo();
			if(dir == null || !instr.startsWith(dir.getDirectoryPath())) return;
			instr = instr.substring(dir.getDirectoryPath().length(), instr.length());
			if(instr.length() == 0) return;
			if(instr.charAt(0) == '/') instr = instr.substring(1, instr.length());
			int row = getInstrumentRowIndex(instr);
			if(row == -1) return;
			fireTableRowsUpdated(row, row);
		}
		
		/**
		 * Invoked when an instrument is renamed.
		 */
		public void
		instrumentNameChanged(InstrumentsDbEvent e) {
			
		}
		
		/** Invoked when the status of particular job has changed. */
		public void
		jobStatusChanged(InstrumentsDbEvent e) { }
	}
}
