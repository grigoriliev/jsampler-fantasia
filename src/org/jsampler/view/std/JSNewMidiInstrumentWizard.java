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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.sf.juife.swing.Wizard;

import net.sf.juife.event.TaskEvent;
import net.sf.juife.event.TaskListener;

import net.sf.juife.wizard.DefaultWizardModel;
import net.sf.juife.wizard.UserInputPage;
import net.sf.juife.wizard.WizardPage;

import org.jsampler.CC;
import org.jsampler.OrchestraInstrument;
import org.jsampler.JSPrefs;
import org.jsampler.MidiInstrumentMap;
import org.jsampler.OrchestraModel;
import org.jsampler.task.Global;
import org.jsampler.view.swing.SHF;

import org.linuxsampler.lscp.Instrument;
import org.linuxsampler.lscp.MidiInstrumentEntry;
import org.linuxsampler.lscp.MidiInstrumentInfo;
import org.linuxsampler.lscp.SamplerEngine;

import static org.jsampler.view.std.StdI18n.i18n;
import static org.linuxsampler.lscp.Parser.*;


/**
 * A wizard for mapping new MIDI instrument.
 * @author Grigor Iliev
 */
public class JSNewMidiInstrumentWizard extends Wizard {
	/**
	 * Creates a new instance of <code>JSNewMidiInstrumentWizard</code>.
	 */
	public
	JSNewMidiInstrumentWizard(ImageIcon iconBrowse) {
		this(iconBrowse, null);
	}
	
	/**
	 * Creates a new instance of <code>JSNewMidiInstrumentWizard</code>.
	 */
	public
	JSNewMidiInstrumentWizard(ImageIcon iconBrowse, MidiInstrumentMap defaultMap) {
		super(SHF.getMainFrame(), i18n.getLabel("JSNewMidiInstrumentWizard.title"));
		setModel(new NewMidiInstrumentWizardModel(iconBrowse, defaultMap));
	}
	
}

class NewMidiInstrumentWizardModel extends DefaultWizardModel {
	private final InstrLocationMethodWizardPage instrLocationMethodWizardPage;
	private final OrchestraSelectWizardPage orchestraSelectWizardPage;
	private final ManualSelectWizardPage manualSelectWizardPage;
	private final InstrumentMappingWizardPage instrumentMappingWizardPage;
	
	private MidiInstrumentMap defaultMap;
	
	NewMidiInstrumentWizardModel(ImageIcon iconBrowse, MidiInstrumentMap defaultMap) {
		this.defaultMap = defaultMap;
		instrLocationMethodWizardPage = new InstrLocationMethodWizardPage();
		orchestraSelectWizardPage = new OrchestraSelectWizardPage();
		manualSelectWizardPage = new ManualSelectWizardPage(iconBrowse);
		instrumentMappingWizardPage = new InstrumentMappingWizardPage(this);
		
		addPage(instrLocationMethodWizardPage);
		addStep(i18n.getLabel("JSNewMidiInstrumentWizard.step1"));
		
		addPage(manualSelectWizardPage);
		addPage(orchestraSelectWizardPage);
		addStep(i18n.getLabel("JSNewMidiInstrumentWizard.step2"), 2);
		
		addPage(instrumentMappingWizardPage);
		addStep(i18n.getLabel("JSNewMidiInstrumentWizard.step3"));
	}
	
	public String
	getInstrumentName() {
		if(!instrLocationMethodWizardPage.isOrchestraMethodSelected()) {
			return manualSelectWizardPage.getInstrumentName();
		}
		OrchestraInstrument instr = orchestraSelectWizardPage.getInstrument();
		if(instr == null) return null;
		return instr.getName();
	}
	
	/**
	 * Moves to the next page in the wizard.
	 * @return The next page in the wizard.
	 */
	@Override
	public WizardPage
	next() {
		InstrLocationMethodWizardPage p1 = instrLocationMethodWizardPage;
		WizardPage p2 = manualSelectWizardPage;
		
		if(getCurrentPage() == p1 && p1.isOrchestraMethodSelected()) {
			super.next();
		} else if(getCurrentPage() == manualSelectWizardPage) {
			super.next();
		}
		
		return super.next();
	}
	
	/**
	 * Moves to the previous page in the wizard.
	 * @return The previous page in the wizard.
	 * @see #hasPrevious
	 */
	@Override
	public WizardPage
	previous() {
		InstrLocationMethodWizardPage p1 = instrLocationMethodWizardPage;
		WizardPage p2 = instrumentMappingWizardPage;
		
		if(getCurrentPage() == orchestraSelectWizardPage) {
			super.previous();
		} else if(getCurrentPage() == p2 && !p1.isOrchestraMethodSelected()) {
			super.previous();
		}
		
		return super.previous();
	}
	
	public void
	mapInstrument() {
		MidiInstrumentInfo instr = new MidiInstrumentInfo();
		if(instrLocationMethodWizardPage.isOrchestraMethodSelected()) {
			OrchestraInstrument i = orchestraSelectWizardPage.getInstrument();
			instr.setFilePath(i.getFilePath());
			instr.setInstrumentIndex(i.getInstrumentIndex());
			instr.setEngine(i.getEngine());
			instr.setLoadMode(orchestraSelectWizardPage.getLoadMode());
		} else {
			instr.setFilePath(manualSelectWizardPage.getSelectedFile());
			instr.setInstrumentIndex(manualSelectWizardPage.getInstrumentIndex());
			instr.setEngine(manualSelectWizardPage.getEngine());
			instr.setLoadMode(manualSelectWizardPage.getLoadMode());
		}
		
		int map = instrumentMappingWizardPage.getMapId();
		int bank = instrumentMappingWizardPage.getMidiBank();
		int prog = instrumentMappingWizardPage.getMidiProgram();
		
		instr.setName(instrumentMappingWizardPage.getInstrumentName());
		instr.setVolume(instrumentMappingWizardPage.getVolume());
		
		CC.getSamplerModel().mapBackendMidiInstrument(map, bank, prog, instr);
	}
	
	public MidiInstrumentMap
	getDefaultMap() { return defaultMap; }
	
	public void
	setDefaultMap(MidiInstrumentMap map) { defaultMap = map; }
}

class InstrLocationMethodWizardPage extends UserInputPage {
	private final static String INSTR_LOCATION_METHOD = "InstrLocationMethod";
	private final static String NEW_MIDI_INSTR_WIZARD_SKIP1 = "NewMidiInstrumentWizard.skip1";
	
	private final JRadioButton rbManual =
		new JRadioButton(i18n.getLabel("InstrLocationMethodWizardPage.rbManual"));
	
	private final JRadioButton rbOrchestra =
		new JRadioButton(i18n.getLabel("InstrLocationMethodWizardPage.rbOrchestra"));
	
	private final JCheckBox checkSkip =
		new JCheckBox(i18n.getLabel("InstrLocationMethodWizardPage.checkSkip"));
	
	InstrLocationMethodWizardPage() {
		super(i18n.getLabel("InstrLocationMethodWizardPage.subtitle"));
		
		String s = i18n.getLabel("InstrLocationMethodWizardPage.mainInstructions");
		setMainInstructions(s);
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		
		ButtonGroup group = new ButtonGroup();
		group.add(rbManual);
		group.add(rbOrchestra);
		rbManual.setSelected(true);
		
		p.add(rbManual);
		p.add(rbOrchestra);
		
		JPanel p2 = new JPanel();
		p2.setLayout(new BorderLayout());
		p2.add(p, BorderLayout.NORTH);
		p2.add(checkSkip, BorderLayout.SOUTH);
		setMainPane(p2);
		
		switch(preferences().getIntProperty(INSTR_LOCATION_METHOD)) {
		case 0:
			rbManual.setSelected(true);
			break;
		case 1:
			rbOrchestra.setSelected(true);
		}
		
		checkSkip.setSelected(preferences().getBoolProperty(NEW_MIDI_INSTR_WIZARD_SKIP1));
		
		rbManual.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				if(rbManual.isSelected()) {
					preferences().setIntProperty(INSTR_LOCATION_METHOD, 0);
				}
			}
		});
		
		rbOrchestra.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				if(rbOrchestra.isSelected()) {
					preferences().setIntProperty(INSTR_LOCATION_METHOD, 1);
				}
			}
		});
		
		checkSkip.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				boolean b = checkSkip.isSelected();
				preferences().setBoolProperty(NEW_MIDI_INSTR_WIZARD_SKIP1, b);
			}
		});
	}
	
	protected JSPrefs
	preferences() { return CC.getViewConfig().preferences(); }
	
	/**
	 * Determines whether the user selected an orchestra location method.
	 */
	public boolean
	isOrchestraMethodSelected() { return rbOrchestra.isSelected(); }
}

class OrchestraSelectWizardPage extends UserInputPage {
	private final JLabel lOrchestras =
		new JLabel(i18n.getLabel("OrchestraSelectWizardPage.lOrchestras"));
	
	private final JLabel lInstruments =
		new JLabel(i18n.getLabel("OrchestraSelectWizardPage.lInstruments"));
	
	private final JLabel lLoadMode =
		new JLabel(i18n.getLabel("OrchestraSelectWizardPage.lLoadMode"));
	
	private final JComboBox cbOrchestras = new JComboBox();
	private final JComboBox cbInstruments = new JComboBox();
	private final JComboBox cbLoadMode = new JComboBox();
	
	OrchestraSelectWizardPage() {
		super(i18n.getLabel("OrchestraSelectWizardPage.subtitle"));
		setMainInstructions(i18n.getLabel("OrchestraSelectWizardPage.mainInstructions"));
		
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		
		JPanel p = new JPanel();
		
		p.setLayout(gridbag);
		
		c.fill = GridBagConstraints.NONE;
		
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(0, 0, 6, 16);
		gridbag.setConstraints(lOrchestras, c);
		p.add(lOrchestras); 
		
		c.gridx = 0;
		c.gridy = 1;
		gridbag.setConstraints(lInstruments, c);
		p.add(lInstruments);
		
		c.gridx = 0;
		c.gridy = 2;
		c.insets = new Insets(12, 0, 6, 16);
		gridbag.setConstraints(lLoadMode, c);
		p.add(lLoadMode);
		
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1.0;
		c.insets = new Insets(0, 0, 6, 48);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTH;
		gridbag.setConstraints(cbOrchestras, c);
		p.add(cbOrchestras); 
		
		c.gridx = 1;
		c.gridy = 1;
		gridbag.setConstraints(cbInstruments, c);
		p.add(cbInstruments);
		
		c.gridx = 1;
		c.gridy = 2;
		c.insets = new Insets(12, 0, 6, 48);
		gridbag.setConstraints(cbLoadMode, c);
		p.add(cbLoadMode);
		
		JPanel p2 = new JPanel();
		p2.setOpaque(false);
		c.gridx = 0;
		c.gridy = 3;
		c.fill = GridBagConstraints.VERTICAL;
		c.weightx = 0.0;
		c.weighty = 1.0;
		gridbag.setConstraints(p2, c);
		p.add(p2);
		
		setMainPane(p);
		
		int orchIdx =
			preferences().getIntProperty("OrchestraSelectWizardPage.OrchestraIndex", 0);
		
		cbOrchestras.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { orchestraChanged(); }
		});
		
		for(int i = 0; i < CC.getOrchestras().getOrchestraCount(); i++) {
			cbOrchestras.addItem(CC.getOrchestras().getOrchestra(i));
		}
		
		if(CC.getOrchestras().getOrchestraCount() > orchIdx) {
			cbOrchestras.setSelectedIndex(orchIdx);
		}
		
		cbInstruments.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { instrumentChanged(); }
		});
		
		cbLoadMode.addItem(MidiInstrumentInfo.LoadMode.DEFAULT);
		cbLoadMode.addItem(MidiInstrumentInfo.LoadMode.ON_DEMAND);
		cbLoadMode.addItem(MidiInstrumentInfo.LoadMode.ON_DEMAND_HOLD);
		cbLoadMode.addItem(MidiInstrumentInfo.LoadMode.PERSISTENT);
		
		int i = preferences().getIntProperty("std.midiInstrument.loadMode", 0);
		if(cbLoadMode.getItemCount() > i) cbLoadMode.setSelectedIndex(i);
		
		cbLoadMode.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				int j = cbLoadMode.getSelectedIndex();
				if(j < 0) return;
				preferences().setIntProperty("std.midiInstrument.loadMode", j);
			}
		});
	}
	
	protected JSPrefs
	preferences() { return CC.getViewConfig().preferences(); }
	
	private void
	orchestraChanged() {
		OrchestraModel om = (OrchestraModel)cbOrchestras.getSelectedItem();
		String s = om == null ? null : om.getDescription();
		if(s != null && s.length() == 0) s = null;
		cbOrchestras.setToolTipText(s);
		
		s = "OrchestraSelectWizardPage.OrchestraIndex";
		int orchIdx = cbOrchestras.getSelectedIndex();
		if(orchIdx >= 0) preferences().setIntProperty(s, orchIdx);
		
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
		OrchestraInstrument instr = (OrchestraInstrument)cbInstruments.getSelectedItem();
		String s = instr == null ? null : instr.getDescription();
		if(s != null && s.length() == 0) s = null;
		cbInstruments.setToolTipText(s);
		
		getWizard().enableNextButton(instr != null);
	}
	
	@Override
	public void
	postinitPage() {
		getWizard().enableNextButton(cbInstruments.getSelectedItem() != null);
	}
	
	/**
	 * Gets the selected instrument.
	 */
	public OrchestraInstrument
	getInstrument() { return (OrchestraInstrument)cbInstruments.getSelectedItem(); }
	
	/**
	 * Gets the selected load mode.
	 */
	public MidiInstrumentInfo.LoadMode
	getLoadMode() { return (MidiInstrumentInfo.LoadMode) cbLoadMode.getSelectedItem(); }
}

class ManualSelectWizardPage extends UserInputPage {
	private final JLabel lFilename =
		new JLabel(i18n.getLabel("ManualSelectWizardPage.lFilename"));
	
	private final JLabel lIndex = new JLabel(i18n.getLabel("ManualSelectWizardPage.lIndex"));
	
	private final JLabel lEngine = new JLabel(i18n.getLabel("ManualSelectWizardPage.lEngine"));
	
	private final JLabel lLoadMode =
		new JLabel(i18n.getLabel("ManualSelectWizardPage.lLoadMode"));
	
	private final JComboBox cbFilename = new JComboBox();
	private final JComboBox cbIndex = new JComboBox();
	
	private final JButton btnBrowse;
	
	private final JComboBox cbEngine = new JComboBox();
	private final JComboBox cbLoadMode = new JComboBox();
	
	ManualSelectWizardPage(ImageIcon iconBrowse) {
		super(i18n.getLabel("ManualSelectWizardPage.subtitle"));
		setMainInstructions(i18n.getLabel("ManualSelectWizardPage.mainInstructions"));
		
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		
		JPanel mainPane = new JPanel();
		
		mainPane.setLayout(gridbag);
		
		c.fill = GridBagConstraints.NONE;
		
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(3, 3, 3, 3);
		gridbag.setConstraints(lFilename, c);
		mainPane.add(lFilename); 
		
		c.gridx = 0;
		c.gridy = 1;
		gridbag.setConstraints(lIndex, c);
		mainPane.add(lIndex);
		
		c.gridx = 0;
		c.gridy = 2;
		c.insets = new Insets(12, 3, 3, 3);
		gridbag.setConstraints(lEngine, c);
		mainPane.add(lEngine);
		
		c.gridx = 0;
		c.gridy = 3;
		c.insets = new Insets(3, 3, 3, 3);
		gridbag.setConstraints(lLoadMode, c);
		mainPane.add(lLoadMode);
		
		btnBrowse = new JButton(iconBrowse);
		btnBrowse.setMargin(new Insets(0, 0, 0, 0));
		btnBrowse.setToolTipText(i18n.getLabel("ManualSelectWizardPage.btnBrowse"));
		c.gridx = 2;
		c.gridy = 0;
		gridbag.setConstraints(btnBrowse, c);
		mainPane.add(btnBrowse);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(cbFilename, c);
		mainPane.add(cbFilename);
		
		for(int i = 0; i < 101; i++) cbIndex.addItem(i);
			
		c.gridx = 1;
		c.gridy = 1;
		gridbag.setConstraints(cbIndex, c);
		mainPane.add(cbIndex);
		
		c.gridx = 1;
		c.gridy = 2;
		c.insets = new Insets(12, 3, 3, 64);
		gridbag.setConstraints(cbEngine, c);
		mainPane.add(cbEngine);
		
		c.gridx = 1;
		c.gridy = 3;
		c.insets = new Insets(3, 3, 3, 64);
		gridbag.setConstraints(cbLoadMode, c);
		mainPane.add(cbLoadMode);
		
		JPanel p2 = new JPanel();
		p2.setOpaque(false);
		c.gridx = 0;
		c.gridy = 4;
		c.fill = GridBagConstraints.VERTICAL;
		c.weightx = 0.0;
		c.weighty = 1.0;
		gridbag.setConstraints(p2, c);
		mainPane.add(p2);
		
		setMainPane(mainPane);
		
		cbFilename.setEditable(true);
		String[] files = preferences().getStringListProperty("recentInstrumentFiles");
		for(String s : files) cbFilename.addItem(s);
		cbFilename.setSelectedItem(null);
		
		cbFilename.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				updateState();
				updateFileInstruments();
			}
		});
		
		btnBrowse.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { onBrowse(); }
		});
		
		for(SamplerEngine e : CC.getSamplerModel().getEngines()) cbEngine.addItem(e);
		
		cbLoadMode.addItem(MidiInstrumentInfo.LoadMode.DEFAULT);
		cbLoadMode.addItem(MidiInstrumentInfo.LoadMode.ON_DEMAND);
		cbLoadMode.addItem(MidiInstrumentInfo.LoadMode.ON_DEMAND_HOLD);
		cbLoadMode.addItem(MidiInstrumentInfo.LoadMode.PERSISTENT);
		
		int i = preferences().getIntProperty("std.midiInstrument.loadMode", 0);
		if(cbLoadMode.getItemCount() > i) cbLoadMode.setSelectedIndex(i);
		
		cbLoadMode.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				int j = cbLoadMode.getSelectedIndex();
				if(j < 0) return;
				preferences().setIntProperty("std.midiInstrument.loadMode", j);
			}
		});
	}
	
	protected JSPrefs
	preferences() { return CC.getViewConfig().preferences(); }
	
	private void
	onBrowse() {
		File f = StdUtils.showOpenInstrumentFileChooser(getWizardDialog());
		if(f == null) return;
		
		String path = f.getAbsolutePath();
		if(java.io.File.separatorChar == '\\') {
			path = path.replace('\\', '/');
		}
		cbFilename.setSelectedItem(toEscapedString(path));
	}
	
	private void
	updateState() {
		boolean b = false;
		Object o = cbFilename.getSelectedItem();
		if(o == null) b = false;
		else b = o.toString().length() > 0;
		
		o = cbIndex.getSelectedItem();
		if(o == null || o.toString().length() == 0) b = false;
		
		getWizard().enableNextButton(b);
	}
	
	private void
	updateFileInstruments() {
		Object o = cbFilename.getSelectedItem();
		if(o == null) return;
		String s = o.toString();
		final Global.GetFileInstruments t = new Global.GetFileInstruments(s);
		
		t.addTaskListener(new TaskListener() {
			public void
			taskPerformed(TaskEvent e) {
				Instrument[] instrs = t.getResult();
				if(instrs == null) {
					cbIndex.removeAllItems();
					for(int i = 0; i < 101; i++) cbIndex.addItem(i);
					return;
				} else {
				
					cbIndex.removeAllItems();
					for(int i = 0; i < instrs.length; i++) {
						cbIndex.addItem(i + " - " + instrs[i].getName());
					}
				}
			}
		});
		
		CC.getTaskQueue().add(t);
	}
	
	public String
	getInstrumentName() {
		if(cbIndex.getSelectedItem() == null) return null;
		String s = cbIndex.getSelectedItem().toString();
		int i = s.indexOf(" - ");
		if(i == -1) return null;
		return s.substring(i + 3);
	}
	
	@Override
	public void
	postinitPage() { updateState(); }
	
	/**
	 * Gets the name of the instrument file.
	 * @return The name of the instrument file.
	 */
	public String
	getSelectedFile() { return cbFilename.getSelectedItem().toString(); }
	
	/**
	 * Gets the index of the instrument in the instrument file.
	 * @return The index of the instrument in the instrument file.
	 */
	public int
	getInstrumentIndex() { return cbIndex.getSelectedIndex(); }
	
	/**
	 * Gets the selected engine.
	 */
	public String
	getEngine() { return ((SamplerEngine)cbEngine.getSelectedItem()).getName(); }
	
	/**
	 * Gets the selected load mode.
	 */
	public MidiInstrumentInfo.LoadMode
	getLoadMode() { return (MidiInstrumentInfo.LoadMode)cbLoadMode.getSelectedItem(); }
	
	private final Handler eventHandler = new Handler();
	
	private Handler
	getHandler() { return eventHandler; }
	
	private class Handler implements DocumentListener {
		// DocumentListener
		@Override
		public void
		insertUpdate(DocumentEvent e) { updateState(); }
		
		@Override
		public void
		removeUpdate(DocumentEvent e) { updateState(); }
		
		@Override
		public void
		changedUpdate(DocumentEvent e) { updateState(); }
	}
}

class InstrumentMappingWizardPage extends WizardPage  {
	private final JLabel lName = new JLabel(i18n.getLabel("InstrumentMappingWizardPage.lName"));
	private final JLabel lMap = new JLabel(i18n.getLabel("InstrumentMappingWizardPage.lMap"));
	private final JLabel lBank = new JLabel(i18n.getLabel("InstrumentMappingWizardPage.lBank"));
	private final JLabel lProgram =
		new JLabel(i18n.getLabel("InstrumentMappingWizardPage.lProgram"));
	
	private final JLabel lVolume =
		new JLabel(i18n.getLabel("InstrumentMappingWizardPage.lVolume"));
	
	private final JTextField tfName = new JTextField();
	private final JComboBox cbMap = new JComboBox();
	private final JSpinner spinnerBank;
	private final JComboBox cbProgram = new JComboBox();
	private final JSlider slVolume = StdUtils.createVolumeSlider();
	
	private final NewMidiInstrumentWizardModel wizardModel;
	
	private int mbBase;
	private int mpBase;
	
	InstrumentMappingWizardPage(NewMidiInstrumentWizardModel wizardModel) {
		super(i18n.getLabel("InstrumentMappingWizardPage.subtitle"));
		this.wizardModel = wizardModel;
		
		setPageType(Type.CONFIRMATION_PAGE);
		
		mbBase = CC.getViewConfig().getFirstMidiBankNumber();
		mpBase = CC.getViewConfig().getFirstMidiProgramNumber();
		
		spinnerBank = new JSpinner(new SpinnerNumberModel(mbBase, mbBase, 16383 + mbBase, 1));
		
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		
		setLayout(gridbag);
		
		c.fill = GridBagConstraints.NONE;
		
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(24, 3, 3, 0);
		gridbag.setConstraints(lName, c);
		add(lName); 
		
		c.gridx = 0;
		c.gridy = 2;
		c.insets = new Insets(3, 3, 3, 0);
		gridbag.setConstraints(lMap, c);
		add(lMap);
		
		c.gridx = 0;
		c.gridy = 3;
		gridbag.setConstraints(lBank, c);
		add(lBank);
		
		c.gridx = 0;
		c.gridy = 4;
		gridbag.setConstraints(lProgram, c);
		add(lProgram);
		
		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(3, 3, 24, 0);
		gridbag.setConstraints(lVolume, c);
		add(lVolume);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1.0;
		c.insets = new Insets(24, 12, 3, 36);
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(tfName, c);
		add(tfName);
			
		c.gridx = 1;
		c.gridy = 2;
		c.insets = new Insets(3, 12, 3, 36);
		gridbag.setConstraints(cbMap, c);
		add(cbMap);
		
		c.gridx = 1;
		c.gridy = 3;
		gridbag.setConstraints(spinnerBank, c);
		add(spinnerBank);
		
		c.gridx = 1;
		c.gridy = 4;
		gridbag.setConstraints(cbProgram, c);
		add(cbProgram);
		
		c.gridx = 1;
		c.gridy = 1;
		c.insets = new Insets(3, 12, 24, 36);
		gridbag.setConstraints(slVolume, c);
		add(slVolume);
		
		JPanel p2 = new JPanel();
		p2.setOpaque(false);
		c.gridx = 0;
		c.gridy = 5;
		c.insets = new Insets(0, 0, 0, 0);
		c.fill = GridBagConstraints.VERTICAL;
		c.weightx = 0.0;
		c.weighty = 1.0;
		gridbag.setConstraints(p2, c);
		add(p2);
		
		cbMap.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { updateState(); }
		});
		
		for(MidiInstrumentMap m : CC.getSamplerModel().getMidiInstrumentMaps()) {
			cbMap.addItem(m);
		}
		
		tfName.getDocument().addDocumentListener(getHandler());
		
		cbMap.setEnabled(cbMap.getItemCount() > 0);
		
		
		for(int i = 0; i < 128; i++) cbProgram.addItem(new Integer(i) + mpBase);
		
		cbMap.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { updateMapping(); }
		});
		
		updateMapping();
	}
	
	protected JSPrefs
	preferences() { return CC.getViewConfig().preferences(); }
	
	private void
	updateState() {
		cbMap.setEnabled(cbMap.getItemCount() > 0);
		boolean b = cbMap.getSelectedItem() != null;
		spinnerBank.setEnabled(b);
		cbProgram.setEnabled(b);
		
		b = tfName.getText().length() > 0 && cbMap.getSelectedItem() != null;
		b = b && cbProgram.getSelectedItem() != null;
		if(getWizard() != null) getWizard().enableFinishButton(b);
	}
	
	private void
	updateMapping() {
		if(cbMap.getSelectedItem() == null) return;
		MidiInstrumentMap map = (MidiInstrumentMap)cbMap.getSelectedItem();
		MidiInstrumentEntry entry = map.getAvailableEntry();
		if(entry == null) return;
		setMidiBank(entry.getMidiBank());
		cbProgram.setSelectedIndex(entry.getMidiProgram());
	}
	
	@Override
	public void
	postinitPage() {
		String s = wizardModel.getInstrumentName();
		if(s != null) tfName.setText(s);
		else tfName.setText("");
		if(wizardModel.getDefaultMap() != null) {
			cbMap.setSelectedItem(wizardModel.getDefaultMap());
		}
		updateState();
	}
	
	/**
	 * Invoked when the user clicks the 'Finish' button
	 * while this page is the current page of the wizard.
	 * @return <code>true</code>
	 */
	@Override
	public boolean
	mayFinish() {
		((NewMidiInstrumentWizardModel)getWizardModel()).mapInstrument();
		preferences().setIntProperty("lastUsedMidiBank", getMidiBank());
		preferences().setIntProperty("lastUsedMidiProgram", getMidiProgram());
		return true;
	}
	
	/**
	 * Gets the ID of the selected MIDI instrument map.
	 */
	public int
	getMapId() { return ((MidiInstrumentMap)cbMap.getSelectedItem()).getMapId(); }
	
	/**
	 * Gets the selected MIDI bank (always zero-based).
	 */
	public int
	getMidiBank() { return Integer.parseInt(spinnerBank.getValue().toString()) - mbBase; }
	
	/**
	 * Sets the selected MIDI bank (always zero-based).
	 */
	public void
	setMidiBank(int bank) { spinnerBank.setValue(mbBase + bank); }
	
	/**
	 * Gets the selected MIDI program (always zero-based).
	 */
	public int
	getMidiProgram() { return cbProgram.getSelectedIndex(); }
	
	/**
	 * Gets the chosen name for the new MIDI instrument.
	 * @return The chosen name for the new MIDI instrument.
	 */
	public String
	getInstrumentName() { return tfName.getText(); }
	
	/**
	 * Returns the volume level of the new MIDI instrument.
	 * @return The volume level of the new MIDI instrument.
	 */
	public float
	getVolume() {
		float f = slVolume.getValue();
		f /= 100;
		return f;
	}
	
	private final Handler eventHandler = new Handler();
	
	private Handler
	getHandler() { return eventHandler; }
	
	private class Handler implements DocumentListener {
		// DocumentListener
		@Override
		public void
		insertUpdate(DocumentEvent e) { updateState(); }
		
		@Override
		public void
		removeUpdate(DocumentEvent e) { updateState(); }
		
		@Override
		public void
		changedUpdate(DocumentEvent e) { updateState(); }
	}
}
