/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2011 Grigor Iliev <grigor@grigoriliev.com>
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

import java.util.Vector;

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
import org.jsampler.JSPrefs;
import org.jsampler.SamplerChannelModel;

import org.jsampler.event.EffectSendsAdapter;
import org.jsampler.event.EffectSendsEvent;
import org.jsampler.event.EffectSendsListener;


import org.jsampler.task.Channel;

import org.jsampler.view.swing.FxSendTable;

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
	private final JSlider slVolume = StdUtils.createVolumeSlider();
	
	private final JLabel lMidiCtrl = new JLabel(i18n.getLabel("JSFxSendsPane.lMidiCtrl"));
	private final JLabel lVolume = new JLabel(i18n.getLabel("JSFxSendsPane.lVolume"));
	
	private final ChannelRoutingTable channelRoutingTable;
	
	private FxSend fxSend = null;

	private final int[] undefinedControllers = {
		3, 9, 14, 15, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 85, 86, 87, 88, 89, 90,
		102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119
	};

	private int lastUsedController = 0;
	
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
		int w = d.width > 600 ? d.width : 600;
		int h = d.height > 300 ? d.height : 300;
		setPreferredSize(new Dimension(w, h));
		splitPane.setDividerLocation(200);
	}
	
	protected JSPrefs
	preferences() { return CC.getViewConfig().preferences(); }
	
	public SamplerChannelModel
	getChannelModel() { return channelModel; }
	
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
		for(int i = 0; i < 128; i++) {
			String s = "[" + i + "] " + getMidiControllerName(i);
			cbMidiCtrls.addItem(s);
		}
		
		cbMidiCtrls.setMinimumSize(new Dimension(100, cbMidiCtrls.getMinimumSize().height));
		
		cbMidiCtrls.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				if(fxSend == null) return;
				
				int fxs = fxSend.getFxSendId();
				int ctrl = cbMidiCtrls.getSelectedIndex();
				
				if(ctrl == fxSend.getMidiController()) {
					return;
				}

				lastUsedController = ctrl;
				channelModel.setBackendFxSendMidiController(fxs, ctrl);
			}
		});
		
		slVolume.addChangeListener(new ChangeListener() {
			public void
			stateChanged(ChangeEvent e) { setVolume(); }
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
	
	private String
	getMidiControllerName(int i) {
		switch(i) {
			case 0: return "Bank Select";
			case 1: return "Modulation Wheel or Lever";
			case 2: return "Breath Controller";
			case 4: return "Foot Controller";
			case 5: return "Portamento Time";
			case 6: return "Data Entry MSB";
			case 7: return "Channel Volume";
			case 8: return "Balance";
			case 10: return "Pan";
			case 11: return "Expression Controller";
			case 12: return "Effect Control 1";
			case 13: return "Effect Control 2";
			case 16: return "General Purpose Controller 1";
			case 17: return "General Purpose Controller 2";
			case 18: return "General Purpose Controller 3";
			case 19: return "General Purpose Controller 4";
			case 32: return "LSB for Control 0 (Bank Select)";
			case 33: return "LSB for Control 1 (Modulation Wheel or Lever)";
			case 34: return "LSB for Control 2 (Breath Controller)";
			case 35: return "LSB for Control 3 (Undefined)";
			case 36: return "LSB for Control 4 (Foot Controller)";
			case 37: return "LSB for Control 5 (Portamento Time)";
			case 38: return "LSB for Control 6 (Data Entry)";
			case 39: return "LSB for Control 7 (Channel Volume)";
			case 40: return "LSB for Control 8 (Balance)";
			case 41: return "LSB for Control 9 (Undefined)";
			case 42: return "LSB for Control 10 (Pan)";
			case 43: return "LSB for Control 11 (Expression Controller)";
			case 44: return "LSB for Control 12 (Effect control 1)";
			case 45: return "LSB for Control 13 (Effect control 2)";
			case 46: return "LSB for Control 14 (Undefined)";
			case 47: return "LSB for Control 15 (Undefined)";
			case 48: return "LSB for Control 16 (General Purpose Controller 1)";
			case 49: return "LSB for Control 17 (General Purpose Controller 2)";
			case 50: return "LSB for Control 18 (General Purpose Controller 3)";
			case 51: return "LSB for Control 19 (General Purpose Controller 4)";
			case 52: return "LSB for Control 20 (Undefined)";
			case 53: return "LSB for Control 21 (Undefined)";
			case 54: return "LSB for Control 22 (Undefined)";
			case 55: return "LSB for Control 23 (Undefined)";
			case 56: return "LSB for Control 24 (Undefined)";
			case 57: return "LSB for Control 25 (Undefined)";
			case 58: return "LSB for Control 26 (Undefined)";
			case 59: return "LSB for Control 27 (Undefined)";
			case 60: return "LSB for Control 28 (Undefined)";
			case 61: return "LSB for Control 29 (Undefined)";
			case 62: return "LSB for Control 30 (Undefined)";
			case 63: return "LSB for Control 31 (Undefined)";
			case 64: return "Damper Pedal on/off (Sustain)";
			case 65: return "Portamento On/Off";
			case 66: return "Sostenuto On/Off";
			case 67: return "Soft Pedal On/Off";
			case 68: return "Legato Footswitch";
			case 69: return "Hold 2";
			case 70: return "Sound Controller 1 (default: Sound Variation)";
			case 71: return "Sound Controller 2 (default: Timbre/Harmonic Intens.)";
			case 72: return "Sound Controller 3 (default: Release Time)";
			case 73: return "Sound Controller 4 (default: Attack Time)";
			case 74: return "Sound Controller 5 (default: Brightness)";
			case 75: return "Sound Controller 6 (default: Decay Time";
			case 76: return "Sound Controller 7 (default: Vibrato Rate)";
			case 77: return "Sound Controller 8 (default: Vibrato Depth)";
			case 78: return "Sound Controller 9 (default: Vibrato Delay)";
			case 79: return "Sound Controller 10 (default: undefined)";
			case 80: return "General Purpose Controller 5";
			case 81: return "General Purpose Controller 6";
			case 82: return "General Purpose Controller 7";
			case 83: return "General Purpose Controller 8";
			case 84: return "Portamento Control";
			case 91: return "Effects 1 Depth (default: Reverb Send Level)";
			case 92: return "Effects 2 Depth (formerly Tremolo Depth)";
			case 93: return "Effects 3 Depth(default: Chorus Send Level)";
			case 94: return "Effects 4 Depth (formerly Celeste [Detune] Depth)";
			case 95: return "Effects 5 Depth (formerly Phaser Depth)";
			case 96: return "Data Increment (Data Entry +1)";
			case 97: return "Data Decrement (Data Entry -1)";
			case 98: return "Non-Registered Parameter Number (NRPN) - LSB";
			case 99: return "Non-Registered Parameter Number (NRPN) - MSB";
			case 100: return "Registered Parameter Number (RPN) - LSB";
			case 101: return "Registered Parameter Number (RPN) - MSB";
			case 120: return "All Sound Off (Channel Mode Message)";
			case 121: return "Reset All Controllers (Channel Mode Message)";
			case 122: return "Local Control On/Off (Channel Mode Message)";
			case 123: return "All Notes Off (Channel Mode Message)";
			case 124: return "Omni Mode Off (Channel Mode Message)";
			case 125: return "Omni Mode On (Channel Mode Message)";
			case 126: return "Mono Mode On (Channel Mode Message)";
			case 127: return "Poly Mode On (Channel Mode Message)";
			default: return "Undefined";
		}
	}

	public boolean
	isUndefinedController(int ctrl) {
		for(int i = 0; i < undefinedControllers.length; i++) {
			if(ctrl == undefinedControllers[i]) return true;
		}

		return false;
	}

	private int
	findUndefinedController(int ctrl, Vector<Integer> excludes) {
		for(int i = 0; i < undefinedControllers.length; i++) {
			int c = undefinedControllers[i];
			if(c > ctrl) { if(!excludes.contains(c)) return c; }
		}

		for(int i = 0; i < undefinedControllers.length; i++) {
			int c = undefinedControllers[i];
			if(c < ctrl) { if(!excludes.contains(c)) return c; }
			else break;
		}

		return -1;
	}

	public int
	getUndefinedController() {
		int c = lastUsedController;
		if(c > 119) c = 0;
		FxSend[] fxSends = getChannelModel().getFxSends();
		Vector<Integer> v = new Vector<Integer>();

		for(int i = fxSends.length - 1; i >= 0; i--) {
			int c2 = fxSends[i].getMidiController();
			if(isUndefinedController(c2)) v.add(c2);
		}

		int ctrl = findUndefinedController(c, v);

		return ctrl != -1 ? ctrl : 3;
	}
	
	private void
	setVolume() {
		if(fxSend == null || slVolume.getValueIsAdjusting()) return;
		
		int i = (int)(fxSend.getLevel() * 100);
		if(slVolume.getValue() == i) return;
		
		float vol = slVolume.getValue();
		vol /= 100;
		channelModel.setBackendFxSendLevel(fxSend.getFxSendId(), vol);
	}
	
	@Override
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
		
		@Override
		public void
		actionPerformed(ActionEvent e) {
			int id = channelModel.getChannelId();
			int c = getUndefinedController();
			lastUsedController = c;
			final Channel.AddFxSend t = new Channel.AddFxSend(id, c, "New effect send");
			
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
		
		@Override
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
		
		@Override
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
		
		@Override
		public int
		getColumnCount() { return columnNames.length; }
		
		@Override
		public String
		getColumnName(int column) { return columnNames[column]; }
		
		@Override
		public int
		getRowCount() {
			if(fxSend == null) return 0;
			return fxSend.getAudioOutputRouting().length;
		}
		
		@Override
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
		
		@Override
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
		
		@Override
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
		
		@Override
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
		@Override
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
		@Override
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
		@Override
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
