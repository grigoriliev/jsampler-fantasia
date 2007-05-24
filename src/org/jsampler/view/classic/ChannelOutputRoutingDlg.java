/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2006 Grigor Iliev <grigor@grigoriliev.com>
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

import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.JTableHeader;

import net.sf.juife.InformationDialog;
import net.sf.juife.JuifeUtils;

import org.jsampler.AudioDeviceModel;
import org.jsampler.CC;
import org.jsampler.task.Channel.SetAudioOutputChannel;

import org.linuxsampler.lscp.SamplerChannel;

import static org.jsampler.view.classic.ClassicI18n.i18n;


/**
 *
 * @author Grigor Iliev
 */
public class ChannelOutputRoutingDlg extends InformationDialog {
	private final ChannelRoutingTable channelRoutingTable;
	private SamplerChannel channel;
	
	
	/** Creates a new instance of ChannelOutputRoutingDlg */
	public
	ChannelOutputRoutingDlg(Frame owner, SamplerChannel channel) {
		super(owner, i18n.getLabel("ChannelOutputRoutingDlg.title"));
		this.channel = channel;
		
		channelRoutingTable = new ChannelRoutingTable();
		JScrollPane sp = new JScrollPane(channelRoutingTable);
		
		sp.setPreferredSize (
			JuifeUtils.getUnionSize(sp.getMinimumSize(), new Dimension(200, 150))
		);
		
		setMainPane(sp);
		
		
	}
	
	class ChannelRoutingTable extends JTable {
		private String[] columnToolTips = {
			i18n.getLabel("ChannelOutputRoutingDlg.ttAudioOut", channel.getChannelId()),
			i18n.getLabel("ChannelOutputRoutingDlg.ttAudioIn"),
		};
		
		ChannelRoutingTable() {
			super(new ChannelRoutingTableModel());
			
			JComboBox cb = new JComboBox();
			int devId = channel.getAudioOutputDevice();
			AudioDeviceModel adm = CC.getSamplerModel().getAudioDeviceById(devId);
			
			if(adm == null) {
				setEnabled(false);
			} else {
				int chns = adm.getDeviceInfo().getAudioChannelCount();
				for(Integer i = 0; i < chns; i++) cb.addItem(i);
			}
		
			TableColumn column = getColumnModel().getColumn(1);
			column.setCellEditor(new DefaultCellEditor(cb));
		}
		
		protected JTableHeader
		createDefaultTableHeader() {
			 return new JTableHeader(columnModel) {
				public String getToolTipText(java.awt.event.MouseEvent e) {
					java.awt.Point p = e.getPoint();
					int i = columnModel.getColumnIndexAtX(p.x);
					i = columnModel.getColumn(i).getModelIndex();
					return columnToolTips[i];
				}
			 };
		}
	}
	
	class ChannelRoutingTableModel extends AbstractTableModel {
		private String[] columnNames = {
			i18n.getLabel("ChannelOutputRoutingDlg.audioOut"),
			i18n.getLabel("ChannelOutputRoutingDlg.audioIn")
		};
		
		ChannelRoutingTableModel() {
			
		}
		
		public int
		getColumnCount() { return columnNames.length; }
		
		public String
		getColumnName(int column) { return columnNames[column]; }
		
		public int
		getRowCount() { return channel.getAudioOutputChannels(); }
		
		public Object
		getValueAt(int row, int column) {
			switch(column) {
			case 0:
				return row;
			case 1:
				return channel.getAudioOutputRouting()[row];
			default: return null;
			}
			
		}
		
		public boolean
		isCellEditable(int row, int column) {
			switch(column) {
			case 0:
				return false;
			case 1:
				return true;
			default: return false;
			}
		}
		
		public void
		setValueAt(Object value, int row, int column) {
			if(column == 0) return;
			int c = channel.getChannelId();
			int o = (Integer)getValueAt(row, 0);
			int i = (Integer)value;
			CC.getTaskQueue().add(new SetAudioOutputChannel(c, o, i));
			channel.getAudioOutputRouting()[row] = i;
			
			fireTableCellUpdated(row, column);
		}
	}
}
