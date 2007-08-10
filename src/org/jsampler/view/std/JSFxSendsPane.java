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

package org.jsampler.view.std;

import java.awt.BorderLayout;
import java.awt.Dimension;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.JTableHeader;

import net.sf.juife.event.TaskEvent;
import net.sf.juife.event.TaskListener;

import org.jsampler.AudioDeviceModel;
import org.jsampler.CC;
import org.jsampler.SamplerChannelModel;

import org.jsampler.event.EffectSendsAdapter;
import org.jsampler.event.EffectSendsEvent;
import org.jsampler.event.EffectSendsListener;


import org.jsampler.task.Channel;

import org.jsampler.view.FxSendTable;

import org.linuxsampler.lscp.FxSend;

import static org.jsampler.view.std.StdI18n.i18n;


/**
 *
 * @author Grigor Iliev
 */
public class JSFxSendsPane extends JPanel implements ListSelectionListener {
	private SamplerChannelModel channelModel;
	private final FxSendTable fxSendsTable;
	
	protected final Action actionAddFxSend = new AddFxSendAction();
	protected final Action actionRemoveFxSend = new RemoveFxSendAction();
	
	private final JComboBox cbMidiCtrls = new JComboBox();
	private final JSlider slVolume = new JSlider(0, 100);
	
	private final JLabel lMidiCtrl = new JLabel(i18n.getLabel("JSFxSendsPane.lMidiCtrl"));
	private final JLabel lVolume = new JLabel(i18n.getLabel("JSFxSendsPane.lVolume"));
	
	private final ChannelRoutingTable channelRoutingTable;
	
	private FxSend fxSend = null;
	
	/**
	 * Creates a new instance of <code>JSFxSendsPane</code>.
	 * 
	 * @throws IllegalArgumentException If <code>model</code> is <code>null</code>.
	 */
	public
	JSFxSendsPane(SamplerChannelModel model) {
		if(model == null)
			throw new IllegalArgumentException("model should be non-null!");
		
		channelModel = model;
		fxSendsTable = new FxSendTable(channelModel);
		channelRoutingTable = new ChannelRoutingTable();
		
		setLayout(new BorderLayout());
		
		
		
		JPanel rightPane = createRightPane();
		
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(createLeftPane());
		splitPane.setRightComponent(rightPane);
		splitPane.setContinuousLayout(true);
		add(splitPane);
		
		channelModel.addEffectSendsListener(getHandler());
		
		fxSendsTable.getSelectionModel().addListSelectionListener(this);
		
		if(channelModel.getChannelInfo().getEngine() == null) {
			actionAddFxSend.setEnabled(false);
		}
		if(channelModel.getFxSendCount() == 0) {
			actionRemoveFxSend.setEnabled(false);
		} else {
			fxSendsTable.setSelectedFxSend(channelModel.getFxSend(0));
		}
		updateFxSend();
		
		Dimension d = getMinimumSize();
		int w = d.width > 500 ? d.width : 500;
		int h = d.height > 300 ? d.height : 300;
		setPreferredSize(new Dimension(w, h));
		splitPane.setDividerLocation(200);
	}
	
	protected JToolBar
	createToolBar() {
		JToolBar tb = new JToolBar();
		tb.setMaximumSize(new Dimension(Short.MAX_VALUE, tb.getPreferredSize().height));
		tb.setFloatable(false);
		tb.setAlignmentX(JPanel.RIGHT_ALIGNMENT);
		
		tb.add(new JButton(actionAddFxSend));
		tb.add(new JButton(actionRemoveFxSend));
		
		return tb;
	}
	
	protected JPanel
	createLeftPane() {
		JPanel leftPane = new JPanel();
		leftPane.setLayout(new BorderLayout());
		
		leftPane.add(createToolBar(), BorderLayout.NORTH);
		leftPane.add(new JScrollPane(fxSendsTable));
		
		return leftPane;
	}
	
	protected JPanel
	createRightPane() {
		for(int i = 0; i < 128; i++) cbMidiCtrls.addItem(new Integer(i));
		
		cbMidiCtrls.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				if(fxSend == null) return;
				
				int fxs = fxSend.getFxSendId();
				int ctrl = cbMidiCtrls.getSelectedIndex();
				
				if(ctrl == fxSend.getMidiController()) {
					return;
				}
				
				channelModel.setBackendFxSendMidiController(fxs, ctrl);
			}
		});
		
		slVolume.addChangeListener(new ChangeListener() {
			public void
			stateChanged(ChangeEvent e) {
				if(slVolume.getValueIsAdjusting() || fxSend == null) return;
				
				int i = (int)(fxSend.getLevel() * 100);
				if(slVolume.getValue() == i) return;
				
				float vol = slVolume.getValue();
				vol /= 100;
				channelModel.setBackendFxSendLevel(fxSend.getFxSendId(), vol);
			}
		});
		
		JPanel rightPane = new JPanel();
		rightPane.setLayout(new BorderLayout());
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		
		JPanel p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		
		p2.add(lVolume);
		p2.add(Box.createRigidArea(new Dimension(6, 0)));
		slVolume.setMinimumSize(slVolume.getPreferredSize());
		p2.add(slVolume);
		p2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		p.add(p2);
		
		p2 = new JPanel();
		
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		p2.add(lMidiCtrl);
		p2.add(Box.createRigidArea(new Dimension(6, 0)));
		p2.add(cbMidiCtrls);
		p2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		//p2.setAlignmentX(LEFT_ALIGNMENT);
		p.add(p2);
		
		rightPane.add(p, BorderLayout.NORTH);
		rightPane.add(new JScrollPane(channelRoutingTable));
		
		return rightPane;
	}
	
	public void
	valueChanged(ListSelectionEvent e) {
		if(e.getValueIsAdjusting()) return;
		
		fxSend = fxSendsTable.getSelectedFxSend();
		actionRemoveFxSend.setEnabled(fxSend != null);
		updateFxSend();
	}
	
	private void
	updateFxSend() {
		boolean b = (fxSend != null);
		cbMidiCtrls.setEnabled(b);
		slVolume.setEnabled(b);
		channelRoutingTable.setEnabled(b);
		if(!b) {
			slVolume.setValue(0);
			cbMidiCtrls.setSelectedIndex(0);
			return;
		}
		
		cbMidiCtrls.setSelectedIndex(fxSend.getMidiController());
		slVolume.setValue((int)(fxSend.getLevel() * 100));
	}
	
	class AddFxSendAction extends AbstractAction {
		private int fxSendId = -1;
		
		AddFxSendAction() {
			super(i18n.getLabel("JSFxSendsPane.AddFxSendAction"));
			
			String s = i18n.getLabel("JSFxSendsPane.AddFxSendAction.tt");
			putValue(SHORT_DESCRIPTION, s);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			int id = channelModel.getChannelId();
			final Channel.AddFxSend t = new Channel.AddFxSend(id, 0, "New effect send");
			
			t.addTaskListener(new TaskListener() {
				public void
				taskPerformed(TaskEvent e) {
					if(t.doneWithErrors()) {
						fxSendId = -1;
						return;
					}
					setFxSendId(t.getResult());
				}
			});
			CC.getTaskQueue().add(t);
		}
		
		public int
		getFxSendId() { return fxSendId; }
		
		public void
		setFxSendId(int id) { fxSendId = id; }
	}
	
	class RemoveFxSendAction extends AbstractAction {
		RemoveFxSendAction() {
			super(i18n.getLabel("JSFxSendsPane.RemoveFxSendAction"));
			
			String s = i18n.getLabel("JSFxSendsPane.RemoveFxSendAction.tt");
			putValue(SHORT_DESCRIPTION, s);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			FxSend fxs = fxSendsTable.getSelectedFxSend();
			if(fxs == null) return;
			channelModel.removeBackendFxSend(fxs.getFxSendId());
		}
	}
	
	class ChannelRoutingTable extends JTable {
		private String[] columnToolTips = {
			i18n.getLabel("JSFxSendsPane.ttAudioSrc"),
			i18n.getLabel("JSFxSendsPane.ttAudioDst"),
		};
		
		ChannelRoutingTable() {
			super(new ChannelRoutingTableModel());
			
			JComboBox cb = new JComboBox();
			int devId = channelModel.getChannelInfo().getAudioOutputDevice();
			AudioDeviceModel adm = CC.getSamplerModel().getAudioDeviceById(devId);
			
			int chns;
			if(adm == null) {
				chns = channelModel.getChannelInfo().getAudioOutputChannels();
			} else {
				chns = adm.getDeviceInfo().getAudioChannelCount();
			}
			
			for(Integer i = 0; i < chns; i++) cb.addItem(i);
			
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
	
	class ChannelRoutingTableModel extends AbstractTableModel implements ListSelectionListener {
		private String[] columnNames = {
			i18n.getLabel("JSFxSendsPane.audioSrc"),
			i18n.getLabel("JSFxSendsPane.audioDst")
		};
		
		ChannelRoutingTableModel() {
			channelModel.addEffectSendsListener(new EffectSendsAdapter() {
				/** Invoked when an effect send's setting are changed. */
				public void
				effectSendChanged(EffectSendsEvent e) {
					if(fxSend == null) {
						fireTableDataChanged();
						return;
					}
					
					if(fxSend.equals(e.getFxSend())) {
						int l;
						l = e.getFxSend().getAudioOutputRouting().length;
						fireTableRowsUpdated(0, l - 1);
					}
				}
			});
			
			fxSendsTable.getSelectionModel().addListSelectionListener(this);
		}
		
		public int
		getColumnCount() { return columnNames.length; }
		
		public String
		getColumnName(int column) { return columnNames[column]; }
		
		public int
		getRowCount() {
			if(fxSend == null) return 0;
			return fxSend.getAudioOutputRouting().length;
		}
		
		public Object
		getValueAt(int row, int column) {
			switch(column) {
			case 0:
				return row;
			case 1:
				return fxSend.getAudioOutputRouting()[row];
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
			int id = fxSend.getFxSendId();
			int src = (Integer)getValueAt(row, 0);
			int dst = (Integer)value;
			channelModel.setBackendFxSendAudioOutputChannel(id, src, dst);
			fxSend.getAudioOutputRouting()[row] = dst;
			
			fireTableCellUpdated(row, column);
		}
		
		public void
		valueChanged(ListSelectionEvent e) {
			if(e.getValueIsAdjusting()) return;
			
			fireTableDataChanged();
		}
	}
	
	private final Handler eventHandler = new Handler();
	
	private Handler
	getHandler() { return eventHandler; }
	
	private class Handler implements EffectSendsListener {
		/** Invoked when a new effect send is added to a sampler channel. */
		public void
		effectSendAdded(EffectSendsEvent e) {
			FxSend fxs = fxSendsTable.getSelectedFxSend();
			if(fxs == null) return;
			AddFxSendAction a = (AddFxSendAction)actionAddFxSend;
			if(fxs.getFxSendId() != a.getFxSendId()) return;
			
			fxSendsTable.requestFocus();
			fxSendsTable.editSelectedFxSend();
			a.setFxSendId(-1);
		}
		
		/** Invoked when an effect send is removed from a sampler channel. */
		public void 
		effectSendRemoved(EffectSendsEvent e) {
			if(channelModel.getFxSendCount() == 0) return;
			int id = e.getFxSend().getFxSendId();
			for(FxSend fxs : channelModel.getFxSends()) {
				if(fxs.getFxSendId() > id) {
					fxSendsTable.setSelectedFxSend(fxs);
					return;
				}
			}
			FxSend fxs = channelModel.getFxSend(channelModel.getFxSendCount() - 1);
			fxSendsTable.setSelectedFxSend(fxs);
		}
		
		/** Invoked when an effect send's setting are changed. */
		public void
		effectSendChanged(EffectSendsEvent e) {
			if(fxSend == null) return;
			if(fxSend.equals(e.getFxSend())) {
				fxSend = e.getFxSend();
				updateFxSend();
			}
		}
	}
}
