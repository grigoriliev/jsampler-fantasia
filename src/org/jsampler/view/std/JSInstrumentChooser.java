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

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.sf.juife.OkCancelDialog;

import net.sf.juife.event.TaskEvent;
import net.sf.juife.event.TaskListener;

import org.jsampler.CC;
import org.jsampler.Instrument;
import org.jsampler.OrchestraModel;

import org.jsampler.task.InstrumentsDb;

import static org.jsampler.view.std.StdI18n.i18n;

/**
 *
 * @author Grigor Iliev
 */
public class JSInstrumentChooser extends OkCancelDialog {
	private final JRadioButton rbSelectFromOrchestra =
		new JRadioButton(i18n.getLabel("JSInstrumentChooser.rbSelectFromOrchestra"));
	private final JRadioButton rbSelectFromDb =
		new JRadioButton(i18n.getLabel("JSInstrumentChooser.rbSelectFromDb"));
	private final JRadioButton rbSelectFromFile =
		new JRadioButton(i18n.getLabel("JSInstrumentChooser.rbSelectFromFile"));
	
	
	private final JLabel lOrchestra =
		new JLabel(i18n.getLabel("JSInstrumentChooser.lOrchestra"));
	
	private final JLabel lInstrument =
		new JLabel(i18n.getLabel("JSInstrumentChooser.lInstrument"));
	
	private final JComboBox cbOrchestras = new JComboBox();
	private final JComboBox cbInstruments = new JComboBox();
	
	
	private final JTextField tfDbInstrument = new JTextField();
	private final JButton btnBrowseDb;
	
	private final JLabel lFilename = new JLabel(i18n.getLabel("JSInstrumentChooser.lFilename"));
	private final JLabel lIndex = new JLabel(i18n.getLabel("JSInstrumentChooser.lIndex"));
	
	private final JTextField tfFilename = new JTextField();
	private final JSpinner spinnerIndex = new JSpinner(new SpinnerNumberModel(0, 0, 500, 1));
	
	private final JButton btnBrowse;
	
	private String instrumentFile = null;
	private int instrumentIndex = 0;
	private String engine = null;
	
	/**
	 * Creates a new instance of JSInstrumentChooser
	 */
	public JSInstrumentChooser(Frame owner) {
		super(owner, i18n.getLabel("JSInstrumentChooser.title"));
		
		btnOk.setEnabled(false);
		Icon iconBrowse = CC.getViewConfig().getInstrumentsDbTreeView().getOpenIcon();
		btnBrowseDb = new JButton(iconBrowse);
		btnBrowse = new JButton(iconBrowse);
		
		ButtonGroup group = new ButtonGroup();
		group.add(rbSelectFromOrchestra);
		group.add(rbSelectFromDb);
		group.add(rbSelectFromFile);
		
		rbSelectFromOrchestra.addActionListener(getHandler());
		rbSelectFromDb.addActionListener(getHandler());
		rbSelectFromFile.addActionListener(getHandler());
		rbSelectFromOrchestra.doClick(0);
		
		cbOrchestras.addFocusListener(getHandler());
		cbInstruments.addFocusListener(getHandler());
		
		tfDbInstrument.addFocusListener(getHandler());
		tfDbInstrument.getDocument().addDocumentListener(getHandler());
		
		tfFilename.addFocusListener(getHandler());
		tfFilename.getDocument().addDocumentListener(getHandler());
		
		spinnerIndex.addChangeListener(new ChangeListener() {
			public void
			stateChanged(ChangeEvent e) {
				if(!rbSelectFromFile.isSelected()) rbSelectFromFile.doClick(0);
			}
		});
		
		JPanel mainPane = new JPanel();
		mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
		
		rbSelectFromOrchestra.setAlignmentX(LEFT_ALIGNMENT);
		mainPane.add(rbSelectFromOrchestra);
		
		JPanel orchestraPane = createOrchestraPane();
		orchestraPane.setBorder(BorderFactory.createEmptyBorder(0, 32, 17, 0));
		mainPane.add(orchestraPane);
		
		rbSelectFromDb.setAlignmentX(LEFT_ALIGNMENT);
		mainPane.add(rbSelectFromDb);
		
		JPanel dbPane = createDbPane();
		dbPane.setBorder(BorderFactory.createEmptyBorder(0, 32, 17, 0));
		mainPane.add(dbPane);
		
		rbSelectFromFile.setAlignmentX(LEFT_ALIGNMENT);
		mainPane.add(rbSelectFromFile);
		
		JPanel filePane = createFilePane();
		filePane.setBorder(BorderFactory.createEmptyBorder(0, 32, 0, 0));
		mainPane.add(filePane);
		
		setMainPane(mainPane);
		
		if(!CC.getSamplerModel().getServerInfo().hasInstrumentsDbSupport()) {
			rbSelectFromDb.setEnabled(false);
			tfDbInstrument.setEnabled(false);
			btnBrowseDb.setEnabled(false);
		} else {
			btnBrowseDb.requestFocusInWindow();
		}
	}
	
	private JPanel
	createOrchestraPane() {
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		
		JPanel p = new JPanel();
		
		p.setLayout(gridbag);
		
		c.fill = GridBagConstraints.NONE;
		
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(0, 0, 6, 6);
		gridbag.setConstraints(lOrchestra, c);
		p.add(lOrchestra); 
		
		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(0, 0, 0, 6);
		gridbag.setConstraints(lInstrument, c);
		p.add(lInstrument);
		
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1.0;
		c.insets = new Insets(0, 0, 6, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTH;
		gridbag.setConstraints(cbOrchestras, c);
		p.add(cbOrchestras); 
		
		c.gridx = 1;
		c.gridy = 1;
		c.insets = new Insets(0, 0, 0, 0);
		gridbag.setConstraints(cbInstruments, c);
		p.add(cbInstruments);
		
		cbOrchestras.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { orchestraChanged(); }
		});
		
		cbInstruments.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { instrumentChanged(); }
		});
		
		for(int i = 0; i < CC.getOrchestras().getOrchestraCount(); i++) {
			cbOrchestras.addItem(CC.getOrchestras().getOrchestra(i));
		}
		
		p.setAlignmentX(LEFT_ALIGNMENT);
		return p;
	}
	
	private void
	orchestraChanged() {
		OrchestraModel om = (OrchestraModel)cbOrchestras.getSelectedItem();
		String s = om == null ? null : om.getDescription();
		if(s != null && s.length() == 0) s = null;
		cbOrchestras.setToolTipText(s);
		
		cbInstruments.removeAllItems();
		if(om == null || om.getInstrumentCount() == 0) {
			cbInstruments.setEnabled(false);
			return;
		}
		
		cbInstruments.setEnabled(true);
		
		for(int i = 0; i < om.getInstrumentCount(); i++) {
			cbInstruments.addItem(om.getInstrument(i));
		}
	}
	
	private void
	instrumentChanged() {
		Instrument instr = (Instrument)cbInstruments.getSelectedItem();
		String s = instr == null ? null : instr.getDescription();
		if(s != null && s.length() == 0) s = null;
		cbInstruments.setToolTipText(s);
		
		btnOk.setEnabled(instr != null);
	}
	
	
	
	private JPanel
	createDbPane() {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.add(tfDbInstrument);
		p.add(Box.createRigidArea(new Dimension(6, 0)));
		btnBrowseDb.setMargin(new Insets(0, 0, 0, 0));
		p.add(btnBrowseDb);
		//p.setMaximumSize(new Dimension(Short.MAX_VALUE, p.getPreferredSize().height));
		
		btnBrowseDb.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				if(!rbSelectFromDb.isSelected()) rbSelectFromDb.doClick(0);
				JSDbInstrumentChooser dlg;
				dlg = new JSDbInstrumentChooser(JSInstrumentChooser.this);
				String s = tfDbInstrument.getText();
				if(s.length() > 0) dlg.setSelectedInstrument(s);
				else dlg.setSelectedDirectory("/");
				dlg.setVisible(true);
				if(dlg.isCancelled()) return;
				tfDbInstrument.setText(dlg.getSelectedInstrument());
				tfDbInstrument.requestFocus();
			}
		});
		
		p.setAlignmentX(LEFT_ALIGNMENT);
		
		return p;
	}
	
	private JPanel
	createFilePane() {
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		
		JPanel filePane = new JPanel();
		
		filePane.setLayout(gridbag);
		
		c.fill = GridBagConstraints.NONE;
		
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(3, 3, 3, 3);
		gridbag.setConstraints(lFilename, c);
		filePane.add(lFilename); 
		
		c.gridx = 0;
		c.gridy = 1;
		gridbag.setConstraints(lIndex, c);
		filePane.add(lIndex);
		
		btnBrowse.setMargin(new Insets(0, 0, 0, 0));
		btnBrowse.setToolTipText(i18n.getLabel("JSInstrumentChooser.btnBrowse"));
		c.gridx = 2;
		c.gridy = 0;
		gridbag.setConstraints(btnBrowse, c);
		filePane.add(btnBrowse);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(tfFilename, c);
		filePane.add(tfFilename);
			
		c.gridx = 1;
		c.gridy = 1;
		gridbag.setConstraints(spinnerIndex, c);
		filePane.add(spinnerIndex);
		
		tfFilename.setPreferredSize (
			new Dimension(200, tfFilename.getPreferredSize().height)
		);
		
		btnBrowse.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { onBrowse(); }
		});
		
		filePane.setAlignmentX(LEFT_ALIGNMENT);
		return filePane;
	}
	
	protected void
	onOk() {
		if(!btnOk.isEnabled()) return;
		if(rbSelectFromOrchestra.isSelected()) {
			Instrument instr = (Instrument)cbInstruments.getSelectedItem();
			instrumentFile = instr.getPath();
			instrumentIndex = instr.getInstrumentIndex();
			engine = instr.getEngine();
			setVisible(false);
			return;
		}
		
		if(rbSelectFromFile.isSelected()) {
			instrumentFile = tfFilename.getText();
			instrumentIndex = Integer.parseInt(spinnerIndex.getValue().toString());
			setVisible(false);
			return;
		}
		
		if(!rbSelectFromDb.isSelected()) return;
		
		String instr = tfDbInstrument.getText();
		final InstrumentsDb.GetInstrument t = new InstrumentsDb.GetInstrument(instr);
		
		t.addTaskListener(new TaskListener() {
			public void
			taskPerformed(TaskEvent e) {
				updateState();
				if(t.doneWithErrors()) return;
				
				instrumentFile = t.getResult().getFilePath();
				instrumentIndex = t.getResult().getInstrumentIndex();
				engine = t.getResult().getFormatFamily(); // TODO: fix this
				setVisible(false);
			}
		});
		
		btnOk.setEnabled(false);
		CC.getTaskQueue().add(t);
	}
	
	protected void
	onCancel() { setVisible(false); }
	
	private void
	onBrowse() {
		if(!rbSelectFromFile.isSelected()) rbSelectFromFile.doClick(0);
		JFileChooser fc = new JFileChooser();
		int result = fc.showOpenDialog(this);
		if(result == JFileChooser.APPROVE_OPTION) {
			tfFilename.setText(fc.getSelectedFile().getPath());
			btnOk.requestFocusInWindow();
		}
	}
	
	/**
	 * Gets the name of the selected instrument file.
	 * @return The name of the selected instrument file.
	 */
	public String
	getInstrumentFile() { return instrumentFile; }
	
	/**
	 * Gets the index of the instrument in the instrument file.
	 * @return The index of the instrument in the instrument file.
	 */
	public int
	getInstrumentIndex() { return instrumentIndex; }
	
	public String
	getEngine() { return engine; }
	
	private void
	updateState() {
		boolean b = false;
		if(rbSelectFromOrchestra.isSelected()) {
			b = cbInstruments.getSelectedItem() != null;
		} else if(rbSelectFromDb.isSelected()) {
			b = tfDbInstrument.getText().length() > 0;
		} else if(rbSelectFromFile.isSelected()) {
			b = tfFilename.getText().length() > 0;
		}
		
		btnOk.setEnabled(b);
	}
	
	private final EventHandler eventHandler = new EventHandler();
	
	private EventHandler
	getHandler() { return eventHandler; }
	
	private class EventHandler extends FocusAdapter
				implements ActionListener, DocumentListener {
		
		public void
		actionPerformed(ActionEvent e) {
			updateState();
		}
		
		// DocumentListener
		public void
		insertUpdate(DocumentEvent e) { updateState(); }
		
		public void
		removeUpdate(DocumentEvent e) { updateState(); }
		
		public void
		changedUpdate(DocumentEvent e) { updateState(); }
		
		// FocusListener
		public void
		focusGained(FocusEvent e) {
			Object src = e.getSource();
			if(src == cbInstruments || src == cbOrchestras) {
				if(!rbSelectFromOrchestra.isSelected()) {
					rbSelectFromOrchestra.doClick(0);
				}
			} else if(src == tfDbInstrument) {
				if(!rbSelectFromDb.isSelected()) rbSelectFromDb.doClick(0);
			} else if(src == tfFilename) {
				if(!rbSelectFromFile.isSelected()) rbSelectFromFile.doClick(0);
			}
		}
	}
}
