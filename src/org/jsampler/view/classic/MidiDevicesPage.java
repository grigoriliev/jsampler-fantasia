/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005 Grigor Kirilov Iliev
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
import java.awt.Dimension;
import java.awt.MediaTracker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.net.URL;

import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import net.sf.juife.InformationDialog;
import net.sf.juife.JuifeUtils;
import net.sf.juife.NavigationPage;

import org.jsampler.CC;
import org.jsampler.HF;
import org.jsampler.MidiDeviceModel;

import org.jsampler.event.MidiDeviceEvent;
import org.jsampler.event.MidiDeviceListEvent;
import org.jsampler.event.MidiDeviceListListener;
import org.jsampler.event.MidiDeviceListener;
import org.jsampler.event.ParameterEvent;
import org.jsampler.event.ParameterListener;

import org.jsampler.task.CreateMidiDevice;
import org.jsampler.task.DestroyMidiDevice;
import org.jsampler.task.EnableMidiDevice;
import org.jsampler.task.SetMidiInputPortCount;
import org.jsampler.task.SetMidiPortParameter;

import org.jsampler.view.NumberCellEditor;
import org.jsampler.view.ParameterTable;

import org.linuxsampler.lscp.MidiInputDevice;
import org.linuxsampler.lscp.MidiPort;
import org.linuxsampler.lscp.Parameter;

import static org.jsampler.view.classic.ClassicI18n.i18n;
import static org.jsampler.view.classic.MidiDevicesTableModel.*;


/**
 *
 * @author Grigor Iliev
 */
public class MidiDevicesPage extends NavigationPage {
	private final Action duplicateMidiDevice = new DuplicateMidiDevice();
	private final Action removeMidiDevice = new RemoveMidiDevice();
	private final Action midiDeviceProps = new MidiDeviceProps();
	
	private final ToolbarButton btnNewDevice = new ToolbarButton(A4n.addMidiDevice);
	private final ToolbarButton btnDuplicateDevice = new ToolbarButton(duplicateMidiDevice);
	private final ToolbarButton btnRemoveDevice = new ToolbarButton(removeMidiDevice);
	private final ToolbarButton btnDeviceProps = new ToolbarButton(midiDeviceProps);
	
	private final JTable devicesTable = new JTable(new MidiDevicesTableModel());
	
	private final JLabel lPorts = new JLabel(i18n.getLabel("MidiDevicesPage.lPorts"));
	private final JComboBox cbPorts = new JComboBox();
	
	ParameterTable portParamTable = new ParameterTable();
	
	
	/** Creates a new instance of <code>MidiDevicesPage</code> */
	public
	MidiDevicesPage() {
		setTitle(i18n.getLabel("MidiDevicesPage.title"));
		
		cbPorts.setEnabled(false);
		
		TableColumn tc = devicesTable.getColumnModel().getColumn(ACTIVE_COLUMN_INDEX);
		tc.setPreferredWidth(tc.getMinWidth());
		
		NumberCellEditor nce = new NumberCellEditor();
		nce.setMinimum(0);
		nce.setMaximum(255);
		tc = devicesTable.getColumnModel().getColumn(PORTS_COLUMN_INDEX);
		tc.setCellEditor(nce);
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JToolBar tb = new JToolBar();
		tb.setMaximumSize(new Dimension(Short.MAX_VALUE, tb.getPreferredSize().height));
		tb.setFloatable(false);
		tb.setAlignmentX(JPanel.RIGHT_ALIGNMENT);
		
		tb.add(btnNewDevice);
		tb.add(btnDuplicateDevice);
		tb.add(btnRemoveDevice);
		tb.addSeparator();
		tb.add(btnDeviceProps);
		
		add(tb);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		
		devicesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane sp = new JScrollPane(devicesTable);
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.add(sp);
		p.add(Box.createRigidArea(new Dimension(0, 8)));
		
		splitPane.setTopComponent(p);
		
		//add(Box.createRigidArea(new Dimension(0, 12)));
		
		JPanel portsPane = new JPanel();
		portsPane.setLayout(new BoxLayout(portsPane, BoxLayout.Y_AXIS));
		
		p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.add(lPorts);
		p.add(Box.createRigidArea(new Dimension(5, 0)));
		p.add(cbPorts);
		p.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		portsPane.add(p);
		
		p = new JPanel();
		p.setLayout(new BorderLayout());
		p.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		p.add(new JScrollPane(portParamTable));
		portsPane.add(p);
		
		portsPane.setBorder (
			BorderFactory.createTitledBorder(i18n.getLabel("MidiDevicesPage.ports"))
		);
		
		p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.add(Box.createRigidArea(new Dimension(0, 5)));
		p.add(portsPane);
		
		splitPane.setBottomComponent(p);
		splitPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		splitPane.setAlignmentX(JPanel.RIGHT_ALIGNMENT);
		splitPane.setDividerSize(3);
		add(splitPane);
		
		splitPane.setDividerLocation(150);
		
		cbPorts.addActionListener(getHandler());
		
		devicesTable.getSelectionModel().addListSelectionListener(getHandler());
		portParamTable.getModel().addParameterListener(getHandler());
	}
	
	private MidiDeviceModel
	getSelectedMidiDeviceModel() {
		ListSelectionModel lsm = devicesTable.getSelectionModel();
		if(lsm.isSelectionEmpty()) return null;
		
		return ((MidiDevicesTableModel)devicesTable.getModel()).getMidiDeviceModel (
			lsm.getMinSelectionIndex()
		);
	}
	
	private final Handler handler = new Handler();
	
	private Handler
	getHandler() { return handler; }
	
	private class Handler implements ActionListener, ListSelectionListener,
							MidiDeviceListener, ParameterListener {
		public void
		actionPerformed(ActionEvent e) {
			Object obj = cbPorts.getSelectedItem();
			if(obj == null) {
				portParamTable.getModel().setParameters(new Parameter[0]);
				return;
			}
			
			MidiPort port = (MidiPort)obj;
			
			portParamTable.getModel().setParameters(port.getAllParameters());
		}
		
		public void
		valueChanged(ListSelectionEvent e) {
			if(e.getValueIsAdjusting()) return;
			
			for(MidiDeviceModel m : CC.getSamplerModel().getMidiDeviceModels()) {
				m.removeMidiDeviceListener(this);
			}
			
			ListSelectionModel lsm = (ListSelectionModel)e.getSource();
			
			if(lsm.isSelectionEmpty()) {
				duplicateMidiDevice.setEnabled(false);
				removeMidiDevice.setEnabled(false);
				midiDeviceProps.setEnabled(false);
				
				cbPorts.removeAllItems();
				cbPorts.setEnabled(false);
				return;
			}
			
			duplicateMidiDevice.setEnabled(true);
			removeMidiDevice.setEnabled(true);
			midiDeviceProps.setEnabled(true);
				
			MidiDeviceModel m;
			m = ((MidiDevicesTableModel)devicesTable.getModel()).getMidiDeviceModel (
				lsm.getMinSelectionIndex()
			);
			
			cbPorts.removeAllItems();
			for(MidiPort port : m.getDeviceInfo().getMidiPorts()) cbPorts.addItem(port);
			cbPorts.setEnabled(true);
			
			m.addMidiDeviceListener(this);
		}
		
		/** Invoked when when the settings of a particular MIDI device have changed. */
		public void
		settingsChanged(MidiDeviceEvent e) {
			MidiInputDevice d = e.getMidiDeviceModel().getDeviceInfo();
			
			int idx = cbPorts.getSelectedIndex();
			cbPorts.removeAllItems();
			for(MidiPort port : d.getMidiPorts()) cbPorts.addItem(port);
			
			if(idx >= cbPorts.getModel().getSize()) idx = 0;
			
			if(cbPorts.getModel().getSize() > 0) cbPorts.setSelectedIndex(idx);
		}
		
		/** Invoked when when the value of a particular parameter is changed. */
		public void
		parameterChanged(ParameterEvent e) {
			MidiDeviceModel m = getSelectedMidiDeviceModel();
			if(m == null) {
				CC.getLogger().warning("Unexpected null MidiDeviceModel!");
				return;
			}
			
			int port = cbPorts.getSelectedIndex();
			if(port == -1) {
				CC.getLogger().warning("There is no MIDI port selected!");
				return;
			}
			
			CC.getTaskQueue().add (
				new SetMidiPortParameter(m.getDeviceID(), port, e.getParameter())
			);
		}
	}
	
	private class DuplicateMidiDevice extends AbstractAction {
		DuplicateMidiDevice() {
			super("");
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("ttDuplicateMidiDevice"));
			
			try {
				URL url = ClassLoader.getSystemClassLoader().getResource (
					"org/jsampler/view/classic/res/icons/Copy16.gif"
				);
				
				ImageIcon icon = new ImageIcon(url);
				if(icon.getImageLoadStatus() == MediaTracker.COMPLETE)
					putValue(Action.SMALL_ICON, icon);
			} catch(Exception x) {
				CC.getLogger().log(Level.INFO, HF.getErrorMessage(x), x);
			}
			
			setEnabled(false);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			int i = devicesTable.getSelectedRow();
			if(i < 0) {
				CC.getLogger().info("There's no selected MIDI device to duplicate");
				return;
			}
			MidiDeviceModel m;
			m = ((MidiDevicesTableModel)devicesTable.getModel()).getMidiDeviceModel(i);
			String d = m.getDeviceInfo().getDriverName();
			Parameter[] pS = m.getDeviceInfo().getAdditionalParameters();
			for(Parameter p : pS) System.out.println(p.getName());
			CC.getTaskQueue().add(new CreateMidiDevice(d, pS));
		}
	}
	
	private class RemoveMidiDevice extends AbstractAction {
		RemoveMidiDevice() {
			super("");
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("ttRemoveMidiDevice"));
			
			try {
				URL url = ClassLoader.getSystemClassLoader().getResource (
					"org/jsampler/view/classic/res/icons/Delete16.gif"
				);
				
				ImageIcon icon = new ImageIcon(url);
				if(icon.getImageLoadStatus() == MediaTracker.COMPLETE)
					putValue(Action.SMALL_ICON, icon);
			} catch(Exception x) {
				CC.getLogger().log(Level.INFO, HF.getErrorMessage(x), x);
			}
			
			setEnabled(false);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			MidiDeviceModel m = getSelectedMidiDeviceModel();
			if(m == null) {
				CC.getLogger().warning("No selected MIDI device to remove!");
				return;
			}
			
			CC.getTaskQueue().add(new DestroyMidiDevice(m.getDeviceID()));
		}
	}
	
	private class MidiDeviceProps extends AbstractAction {
		MidiDeviceProps() {
			super("");
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("ttMidiDeviceProps"));
			
			try {
				URL url = ClassLoader.getSystemClassLoader().getResource (
					"org/jsampler/view/classic/res/icons/Properties16.gif"
				);
				
				ImageIcon icon = new ImageIcon(url);
				if(icon.getImageLoadStatus() == MediaTracker.COMPLETE)
					putValue(Action.SMALL_ICON, icon);
			} catch(Exception x) {
				CC.getLogger().log(Level.INFO, HF.getErrorMessage(x), x);
			}
			
			setEnabled(false);
		}
		
		public void
		actionPerformed(ActionEvent e) { new DevicePropsDlg().setVisible(true); }
	}
	
	private class DevicePropsDlg extends InformationDialog {
		DevicePropsDlg() {
			super(CC.getMainFrame(), i18n.getLabel("MidiDevicesPage.DevicePropsDlg"));
			
			MidiDeviceModel m = getSelectedMidiDeviceModel();
			ParameterTable table = new ParameterTable();
			table.getModel().setParameters (
				m.getDeviceInfo().getAdditionalParameters()
			);
			
			JScrollPane sp = new JScrollPane(table);
			sp.setPreferredSize(JuifeUtils.getUnionSize (
				sp.getMinimumSize(), new Dimension(200, 200)
			));
			setMainPane(sp);
		}
	}
}

class MidiDevicesTableModel extends AbstractTableModel {
	protected final static int ACTIVE_COLUMN_INDEX = 0;
	protected final static int DEVICE_ID_COLUMN_INDEX = 1;
	protected final static int PORTS_COLUMN_INDEX = 2;
	
	private final String[] columnNames = {
		"",
		i18n.getLabel("MidiDevicesTableModel.deviceID"),
		i18n.getLabel("MidiDevicesTableModel.ports")
	};
	
	private MidiDeviceModel[] deviceList;
	
	MidiDevicesTableModel() {
		CC.getSamplerModel().addMidiDeviceListListener(getHandler());
		deviceList = CC.getSamplerModel().getMidiDeviceModels();
		for(MidiDeviceModel m : deviceList) m.addMidiDeviceListener(getHandler());
	}
	
	public MidiDeviceModel
	getMidiDeviceModel(int index) { return deviceList[index]; }
	
// The Table Model implementation
	
	/**
	 * Gets the number of columns in the model.
	 * @return The number of columns in the model.
	 */
	public int
	getColumnCount() { return columnNames.length; }
	
	/**
	 * Gets the number of rows in the model.
	 * @return The number of rows in the model.
	 */
	public int
	getRowCount() { return deviceList.length; }
	
	/**
	 * Gets the name of the column at <code>columnIndex</code>.
	 * @return The name of the column at <code>columnIndex</code>.
	 */
	public String
	getColumnName(int col) { return columnNames[col]; }
	
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
		case ACTIVE_COLUMN_INDEX:
			return deviceList[row].getDeviceInfo().isActive();
		case DEVICE_ID_COLUMN_INDEX:
			return deviceList[row].getDeviceID();
		case PORTS_COLUMN_INDEX:
			return deviceList[row].getDeviceInfo().getMidiPortCount();
		}
		
		return null;
	}
	
	/**
	 * Sets the value in the cell at <code>columnIndex</code>
	 * and <code>rowIndex</code> to <code>value</code>.
	 */
	public void
	setValueAt(Object value, int row, int col) {
		switch(col) {
		case ACTIVE_COLUMN_INDEX:
			boolean active = (Boolean)value;
			deviceList[row].getDeviceInfo().setActive(active);
			CC.getTaskQueue().add (
				new EnableMidiDevice(deviceList[row].getDeviceID(), active)
			);
			break;
		case PORTS_COLUMN_INDEX:
			int deviceID = getMidiDeviceModel(row).getDeviceID();
			int ports = (Integer)value;
			CC.getTaskQueue().add(new SetMidiInputPortCount(deviceID, ports));
			break;
		default: return;
		}
		
		fireTableCellUpdated(row,  col);
	}
	
	/**
	 * Returns <code>true</code> if the cell at
	 * <code>rowIndex</code> and <code>columnIndex</code> is editable.
	 */
	public boolean
	isCellEditable(int row, int col) {
		switch(col) {
		case ACTIVE_COLUMN_INDEX:
			return true;
		case DEVICE_ID_COLUMN_INDEX:
			return false;
		case PORTS_COLUMN_INDEX:
			return true;
		default: return false;
		}
	}
	
	/**
	 * Returns the most specific superclass for all the cell values
	 * in the column. This is used by the <code>JTable</code> to set up a
	 * default renderer and editor for the column.
	 * @param columnIndex The index of the column.
	 * @return The common ancestor class of the object values in the model.
	 */
	public Class
	getColumnClass(int columnIndex) {
		return getValueAt(0, columnIndex).getClass();
	}
///////
	
	private final Handler handler = new Handler();
	
	private Handler
	getHandler() { return handler; }
	
	private class Handler implements MidiDeviceListener, MidiDeviceListListener {
		/**
		 * Invoked when a new MIDI device is created.
		 * @param e A <code>MidiDeviceListEvent</code>
		 * instance providing the event information.
		 */
		public void
		deviceAdded(MidiDeviceListEvent e) {
			for(MidiDeviceModel m : deviceList) m.removeMidiDeviceListener(this);
			deviceList = CC.getSamplerModel().getMidiDeviceModels();
			for(MidiDeviceModel m : deviceList) m.addMidiDeviceListener(this);
			fireTableDataChanged();
		}
	
		/**
		 * Invoked when a MIDI device is removed.
		 * @param e A <code>MidiDeviceListEvent</code>
		 * instance providing the event information.
		 */
		public void
		deviceRemoved(MidiDeviceListEvent e) {
			for(MidiDeviceModel m : deviceList) m.removeMidiDeviceListener(this);
			deviceList = CC.getSamplerModel().getMidiDeviceModels();
			for(MidiDeviceModel m : deviceList) m.addMidiDeviceListener(this);
			fireTableDataChanged();
		}
		
		/** Invoked when when the settings of a particular MIDI device have changed. */
		public void
		settingsChanged(MidiDeviceEvent e) {
			MidiInputDevice d = e.getMidiDeviceModel().getDeviceInfo();
			
			for(int i = 0; i < deviceList.length; i++) {
				MidiInputDevice d2 = deviceList[i].getDeviceInfo();
				
				if(d.getDeviceID() == d2.getDeviceID()) {
					fireTableRowsUpdated(i,  i);
				}
			}
		}
	}
}
