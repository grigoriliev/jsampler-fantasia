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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import net.sf.juife.JuifeUtils;
import net.sf.juife.Wizard;

import net.sf.juife.event.TaskEvent;
import net.sf.juife.event.TaskListener;

import net.sf.juife.wizard.DefaultWizardModel;
import net.sf.juife.wizard.UserInputPage;
import net.sf.juife.wizard.WizardPage;

import org.jsampler.AudioDeviceModel;
import org.jsampler.CC;
import org.jsampler.HF;
import org.jsampler.MidiDeviceModel;

import org.jsampler.event.ListEvent;
import org.jsampler.event.ListListener;
import org.jsampler.event.MidiDeviceListEvent;
import org.jsampler.event.MidiDeviceListListener;

import org.jsampler.task.Channel.LoadEngine;
import org.jsampler.task.Channel.LoadInstrument;
import org.jsampler.task.Channel.SetAudioOutputDevice;
import org.jsampler.task.Channel.SetMidiInputChannel;
import org.jsampler.task.Channel.SetMidiInputDevice;
import org.jsampler.task.Channel.SetMidiInputPort;

import org.jsampler.view.std.JSNewMidiDeviceDlg;
import org.jsampler.view.std.JSNewAudioDeviceDlg;

import org.linuxsampler.lscp.AudioOutputDevice;
import org.linuxsampler.lscp.MidiInputDevice;
import org.linuxsampler.lscp.MidiPort;
import org.linuxsampler.lscp.SamplerEngine;

import static org.jsampler.view.classic.ClassicI18n.i18n;
import static org.linuxsampler.lscp.Parser.*;


/**
 *
 * @author Grigor Iliev
 */
public class NewChannelWizard extends Wizard {
	
	/** Creates a new instance of <code>NewChannelWizard</code>. */
	public
	NewChannelWizard() {
		super(CC.getMainFrame(), i18n.getLabel("NewChannelWizard.title"));
		
		setModel(new NewChannelWizardModel());
	}
	
}

class NewChannelWizardModel extends DefaultWizardModel {
	private final MidiDeviceWizardPage midiDevicePage = new MidiDeviceWizardPage();
	private final MidiPortWizardPage midiPortPage = new MidiPortWizardPage();
	private final MidiChannelWizardPage midiChannelPage = new MidiChannelWizardPage();
	private final AudioDeviceWizardPage audioDevicePage = new AudioDeviceWizardPage();
	private final EngineWizardPage enginePage = new EngineWizardPage();
	private final InstrumentWizardPage instrumentPage = new InstrumentWizardPage();
	private final ConfirmationWizardPage confirmationPage = new ConfirmationWizardPage();
			
	NewChannelWizardModel() {
		addPage(midiDevicePage);
		addPage(midiPortPage);
		addPage(midiChannelPage);
		addStep(i18n.getLabel("NewChannelWizard.step1"), 3);
		
		addPage(audioDevicePage);
		addStep(i18n.getLabel("NewChannelWizard.step2"));
		
		addPage(enginePage);
		addStep(i18n.getLabel("NewChannelWizard.step3"));
		
		addPage(instrumentPage);
		addStep(i18n.getLabel("NewChannelWizard.step4"));
		
		addPage(confirmationPage);
		addStep(i18n.getLabel("NewChannelWizard.step5"));
		
		setLast(confirmationPage);
	}
	
	/**
	 * Moves to the next page in the wizard.
	 * @return The next page in the wizard.
	 */
	public WizardPage
	next() {
		if(getCurrentPage() == midiDevicePage && !midiDevicePage.getCustomSettings()) {
			super.next(); super.next();
		}
		
		return super.next();
	}
	
	/**
	 * Moves to the previous page in the wizard.
	 * @return The previous page in the wizard.
	 * @see #hasPrevious
	 */
	public WizardPage
	previous() {
		if(getCurrentPage() == audioDevicePage && !midiDevicePage.getCustomSettings()) {
			super.previous(); super.previous();
		}
		
		return super.previous();
	}
	
	/**
	 * Gets the selected MIDI device.
	 * @return The selected MIDI device or <code>null</code>
	 * if there is no MIDI device selected.
	 */
	public MidiInputDevice
	getSelectedMidiDevice() { return midiDevicePage.getSelectedDevice(); }
	
	/**
	 * Determines whether the user chooses to specify custom MIDI device settings.
	 * @return <code>true</code> if the user chooses to specify custom MIDI device settings,
	 * <code>false</code> otherwise.
	 */
	public boolean
	getCustomMidiSettings() { return midiDevicePage.getCustomSettings(); }
	
	/**
	 * Gets the selected MIDI input port.
	 * @return The selected MIDI input port.
	 */
	public MidiPort
	getSelectedMidiPort() { return midiPortPage.getSelectedPort(); }
	
	/**
	 * Gets the selected MIDI channel.
	 * @return The number of the selected MIDI channel or -1 which means all channels.
	 */
	public int
	getSelectedMidiChannel() { return midiChannelPage.getSelectedChannel(); }
	
	/**
	 * Gets the selected audio device.
	 * @return The selected audio device or <code>null</code> if there is no device selected.
	 */
	public AudioOutputDevice
	getSelectedAudioDevice() { return audioDevicePage.getSelectedDevice(); }
	
	/**
	 * Gets the selected sampler engine to be used.
	 * @return The selected sampler engine to be used.
	 */
	public SamplerEngine
	getSelectedEngine() { return enginePage.getSelectedEngine(); }
	
	/**
	 * Gets the name of the selected instrument file.
	 * @return The name of the selected instrument file.
	 */
	public String
	getSelectedFile() { return instrumentPage.getSelectedFile(); }
	
	/**
	 * Gets the index of the instrument in the instrument file.
	 * @return The index of the instrument in the instrument file.
	 */
	public int
	getInstrumentIndex() { return instrumentPage.getInstrumentIndex(); }
}

class MidiDeviceWizardPage extends UserInputPage {
	private final JLabel lDevice = new JLabel(i18n.getLabel("MidiDeviceWizardPage.lDevice"));
	private final JComboBox cbDevices = new JComboBox();
	private final JButton btnNewDevice =
		new JButton(i18n.getButtonLabel("MidiDeviceWizardPage.btnNewDevice"));
	private final JRadioButton rbDefaultSettings =
		new JRadioButton(i18n.getButtonLabel("MidiDeviceWizardPage.rbDefaultSettings"));
	private final JRadioButton rbCustomSettings =
		new JRadioButton(i18n.getButtonLabel("MidiDeviceWizardPage.rbCustomSettings"));
	
	MidiDeviceWizardPage() {
		super(i18n.getLabel("MidiDeviceWizardPage.subtitle"));
		
		setMainInstructions(i18n.getLabel("MidiDeviceWizardPage.mainInstructions"));
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		
		p.add(lDevice);
		
		p.add(Box.createRigidArea(new Dimension(6, 0)));
		
		for(MidiDeviceModel m : CC.getSamplerModel().getMidiDevices()) {
			cbDevices.addItem(m.getDeviceInfo());
		}
		
		cbDevices.setMaximumSize(cbDevices.getPreferredSize());
		if(cbDevices.getItemCount() == 0) enableDevComp(false);
		
		p.add(cbDevices);
		
		CC.getSamplerModel().addMidiDeviceListListener(getHandler());
		
		p.add(Box.createRigidArea(new Dimension(12, 0)));
		p.add(btnNewDevice);
		p.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		
		JPanel mainPane = new JPanel();
		mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
		mainPane.add(p);
		
		mainPane.add(Box.createRigidArea(new Dimension(0, 6)));
		
		rbDefaultSettings.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		mainPane.add(rbDefaultSettings);
		rbCustomSettings.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		mainPane.add(rbCustomSettings);
		
		ButtonGroup btnGroup = new ButtonGroup();
		btnGroup.add(rbDefaultSettings);
		btnGroup.add(rbCustomSettings);
		rbDefaultSettings.setSelected(true);
		
		setMainPane(mainPane);
		
		btnNewDevice.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				new JSNewMidiDeviceDlg(getWizardDialog()).setVisible(true);
			}
		});
	}
	
	/**
	 * Gets the selected device.
	 * @return The selected device or <code>null</code> if there is no device selected.
	 */
	public MidiInputDevice
	getSelectedDevice() { return (MidiInputDevice)cbDevices.getSelectedItem(); }
	
	private void
	enableDevComp(boolean b) {
		cbDevices.setEnabled(b);
		rbDefaultSettings.setEnabled(b);
		rbCustomSettings.setEnabled(b);
	}
	
	/**
	 * Determines whether the user chooses to specify custom device settings.
	 * @return <code>true</code> if the user chooses to specify custom device settings,
	 * <code>false</code> otherwise.
	 */
	public boolean
	getCustomSettings() { return rbCustomSettings.isSelected(); }
	
	private final Handler handler = new Handler();
	
	private Handler
	getHandler() { return handler; }
	
	private class Handler implements MidiDeviceListListener {
		public void
		deviceAdded(MidiDeviceListEvent e) {
			updateDeviceList(e.getMidiDeviceModel().getDeviceInfo());
		}
	
		public void
		deviceRemoved(MidiDeviceListEvent e) { updateDeviceList(null); }
			
		private void
		updateDeviceList(MidiInputDevice dev) {
			cbDevices.removeAllItems();
			
			for(MidiDeviceModel m : CC.getSamplerModel().getMidiDevices()) {
				cbDevices.addItem(m.getDeviceInfo());
			}
			
			if(cbDevices.getItemCount() == 0) enableDevComp(false);
			else { 
				enableDevComp(true);
				cbDevices.setSelectedItem(dev);
			}
			cbDevices.setMaximumSize(cbDevices.getPreferredSize());
		}
	}
}

class MidiPortWizardPage extends UserInputPage {
	private final JLabel lPort = new JLabel(i18n.getLabel("MidiPortWizardPage.lPort"));
	private final JComboBox cbPorts = new JComboBox();
	
	MidiPortWizardPage() {
		super(i18n.getLabel("MidiPortWizardPage.subtitle"));
		
		setMainInstructions(i18n.getLabel("MidiPortWizardPage.mainInstructions"));
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		
		p.add(lPort);
		
		p.add(Box.createRigidArea(new Dimension(6, 0)));
		
		cbPorts.setMaximumSize(cbPorts.getPreferredSize());
		
		p.add(cbPorts);
		
		setMainPane(p);
	}
	
	public void
	preinitPage() {
		updatePorts(((NewChannelWizardModel)getWizardModel()).getSelectedMidiDevice());
	}
	
	/**
	 * Gets the selected MIDI input port.
	 * @return The selected MIDI input port.
	 */
	public MidiPort
	getSelectedPort() { return (MidiPort)cbPorts.getSelectedItem(); }
	
	public void
	updatePorts(MidiInputDevice dev) {
		Object current = cbPorts.getSelectedItem();
		cbPorts.removeAllItems();
		
		if(dev != null) for(MidiPort p : dev.getMidiPorts()) cbPorts.addItem(p);
		if(current != null) cbPorts.setSelectedItem(current);
		cbPorts.setEnabled(cbPorts.getItemCount() > 0);
		cbPorts.setMaximumSize(cbPorts.getPreferredSize());
	}
}

class MidiChannelWizardPage extends UserInputPage {
	private final JLabel lChannel = new JLabel(i18n.getLabel("MidiChannelWizardPage.lChannel"));
	private final JComboBox cbChannels = new JComboBox();
	
	MidiChannelWizardPage() {
		super(i18n.getLabel("MidiChannelWizardPage.subtitle"));
		
		setMainInstructions(i18n.getLabel("MidiChannelWizardPage.mainInstructions"));
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		
		p.add(lChannel);
		
		p.add(Box.createRigidArea(new Dimension(6, 0)));
		
		cbChannels.addItem("All");
		for(int i = 1; i <= 16; i++) cbChannels.addItem(String.valueOf(i));
		cbChannels.setMaximumSize(cbChannels.getPreferredSize());
		
		p.add(cbChannels);
		
		setMainPane(p);
	}
	
	/**
	 * Gets the selected MIDI channel.
	 * @return The number of the selected MIDI channel or -1 which means all channels.
	 */
	public int
	getSelectedChannel() {
		Object o = cbChannels.getSelectedItem();
		if(o == null) return -1;
		
		return o.toString().equals("All") ? -1 : Integer.parseInt(o.toString());
	}
}

class AudioDeviceWizardPage extends UserInputPage {
	private final JLabel lDevice = new JLabel(i18n.getLabel("AudioDeviceWizardPage.lDevice"));
	private final JComboBox cbDevices = new JComboBox();
	private final JButton btnNewDevice =
		new JButton(i18n.getButtonLabel("AudioDeviceWizardPage.btnNewDevice"));
	
	AudioDeviceWizardPage() {
		super(i18n.getLabel("AudioDeviceWizardPage.subtitle"));
		
		setMainInstructions(i18n.getLabel("AudioDeviceWizardPage.mainInstructions"));
		setAdditionalInstructions (
			i18n.getLabel("AudioDeviceWizardPage.additionalInstructions")
		);
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		
		p.add(lDevice);
		
		p.add(Box.createRigidArea(new Dimension(6, 0)));
		
		for(AudioDeviceModel m : CC.getSamplerModel().getAudioDevices()) {
			cbDevices.addItem(m.getDeviceInfo());
		}
		
		cbDevices.setMaximumSize(cbDevices.getPreferredSize());
		if(cbDevices.getItemCount() == 0) cbDevices.setEnabled(false);
		
		p.add(cbDevices);
		
		CC.getSamplerModel().addAudioDeviceListListener(getHandler());
		
		p.add(Box.createRigidArea(new Dimension(12, 0)));
		p.add(btnNewDevice);
		
		setMainPane(p);
		
		btnNewDevice.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				new JSNewAudioDeviceDlg(getWizardDialog()).setVisible(true);
			}
		});
	}
	
	/**
	 * Gets the selected device.
	 * @return The selected device or <code>null</code> if there is no device selected.
	 */
	public AudioOutputDevice
	getSelectedDevice() { return (AudioOutputDevice)cbDevices.getSelectedItem(); }
	
	private final Handler handler = new Handler();
	
	private Handler
	getHandler() { return handler; }
	
	private class Handler implements ListListener<AudioDeviceModel> {
		public void
		entryAdded(ListEvent<AudioDeviceModel> e) {
			updateDeviceList(e.getEntry().getDeviceInfo());
		}
	
		public void
		entryRemoved(ListEvent<AudioDeviceModel> e) { updateDeviceList(null); }
			
		private void
		updateDeviceList(AudioOutputDevice dev) {
			cbDevices.removeAllItems();
			
			for(AudioDeviceModel m : CC.getSamplerModel().getAudioDevices()) {
				cbDevices.addItem(m.getDeviceInfo());
			}
			
			if(cbDevices.getItemCount() == 0) cbDevices.setEnabled(false);
			else { 
				cbDevices.setEnabled(true);
				cbDevices.setSelectedItem(dev);
			}
			cbDevices.setMaximumSize(cbDevices.getPreferredSize());
		}
	}
}

class EngineWizardPage extends UserInputPage {
	private final JLabel lEngine = new JLabel(i18n.getLabel("EngineWizardPage.lEngine"));
	private final JComboBox cbEngines = new JComboBox();
	
	EngineWizardPage() {
		super(i18n.getLabel("EngineWizardPage.subtitle"));
		
		setMainInstructions(i18n.getLabel("EngineWizardPage.mainInstructions"));
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		
		p.add(lEngine);
		
		p.add(Box.createRigidArea(new Dimension(6, 0)));
		
		for(Object o : CC.getSamplerModel().getEngines()) {
			cbEngines.addItem(o);
		}
		
		cbEngines.setMaximumSize(cbEngines.getPreferredSize());
		
		p.add(cbEngines);
		
		setMainPane(p);
	}
	
	/**
	 * Gets the selected sampler engine to be used.
	 * @return The selected sampler engine to be used.
	 */
	public SamplerEngine
	getSelectedEngine() { return (SamplerEngine)cbEngines.getSelectedItem(); }
}

class InstrumentWizardPage extends UserInputPage {
	private final JLabel lFilename = new JLabel(i18n.getLabel("InstrumentChooser.lFilename"));
	private final JLabel lIndex = new JLabel(i18n.getLabel("InstrumentChooser.lIndex"));
	
	private final JTextField tfFilename = new JTextField();
	private final JSpinner spinnerIndex = new JSpinner(new SpinnerNumberModel(0, 0, 500, 1));
	
	private final JButton btnBrowse =
		new JButton(i18n.getLabel("InstrumentChooser.btnBrowse"));
	
	
	InstrumentWizardPage() {
		super(i18n.getLabel("InstrumentWizardPage.subtitle"));
		
		setMainInstructions(i18n.getLabel("InstrumentWizardPage.mainInstructions"));
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		
		p.add(lFilename);
		
		p.add(Box.createRigidArea(new Dimension(6, 0)));
		
		tfFilename.setPreferredSize (
			new Dimension(200, tfFilename.getPreferredSize().height)
		);
		tfFilename.setMaximumSize(tfFilename.getPreferredSize());
		p.add(tfFilename);
		
		p.add(Box.createRigidArea(new Dimension(6, 0)));
		
		spinnerIndex.setMaximumSize(spinnerIndex.getPreferredSize());
		p.add(spinnerIndex);
		
		p.add(Box.createRigidArea(new Dimension(6, 0)));
		
		p.add(btnBrowse);
		
		p.setMaximumSize(p.getPreferredSize());
		setMainPane(p);
		
		btnBrowse.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { onBrowse(); }
		});
		
		btnBrowse.requestFocusInWindow();
	}
	
	public void
	preinitPage() {
		NewChannelWizardModel model = (NewChannelWizardModel)getWizardModel();
		if(model.getSelectedAudioDevice() == null) {
			String s = i18n.getLabel("InstrumentWizardPage.additionalInstructions");
			setAdditionalInstructions(s);
		} else { setAdditionalInstructions(""); }
	}
	
	public boolean
	mayGoToNext() {
		NewChannelWizardModel model = (NewChannelWizardModel)getWizardModel();
		if(model.getSelectedAudioDevice() == null && getSelectedFile().length() > 0) {
			String s = i18n.getError("InstrumentWizardPage.selectAODevice!");
			HF.showErrorMessage(s, getWizardDialog());
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Gets the name of the instrument file.
	 * @return The name of the instrument file.
	 */
	public String
	getSelectedFile() { return tfFilename.getText(); }
	
	/**
	 * Gets the index of the instrument in the instrument file.
	 * @return The index of the instrument in the instrument file.
	 */
	public int
	getInstrumentIndex() { return Integer.parseInt(spinnerIndex.getValue().toString()); }
	
	private void
	onBrowse() {
		JFileChooser fc = new JFileChooser();
		int result = fc.showOpenDialog(this);
		if(result == JFileChooser.APPROVE_OPTION) {
			String path = fc.getSelectedFile().getPath();
			if(java.io.File.separatorChar == '\\') {
				path.replace('\\', '/');
			}
			tfFilename.setText(toEscapedString(path));
		}
	}
}

class ConfirmationWizardPage extends WizardPage {
	private MidiInputDevice midiDev = null;
	private MidiPort midiPort = null;
	private int midiChannel = -1;
	private AudioOutputDevice audioDev = null;
	private SamplerEngine engine = null;
	private String instrFile = null;
	private int instrIndex;
	
	private final JLabel lInfo = new JLabel(i18n.getLabel("ConfirmationWizardPage.lInfo"));
	
	private final JLabel lMidiDevice =
		new JLabel(i18n.getLabel("ConfirmationWizardPage.lMidiDevice"));
	private final JLabel lMidiPort =
		new JLabel(i18n.getLabel("ConfirmationWizardPage.lMidiPort"));
	private final JLabel lMidiChannel =
		new JLabel(i18n.getLabel("ConfirmationWizardPage.lMidiChannel"));
	private final JLabel lEngine =
		new JLabel(i18n.getLabel("ConfirmationWizardPage.lEngine"));
	private final JLabel lInstrFile =
		new JLabel(i18n.getLabel("ConfirmationWizardPage.lInstrFile"));
	private final JLabel lInstrIndex =
		new JLabel(i18n.getLabel("ConfirmationWizardPage.lInstrIndex"));
	private final JLabel lAudioDevice =
		new JLabel(i18n.getLabel("ConfirmationWizardPage.lAudioDevice"));
	
	private final JTextField tfMidiDevice = new EnhancedTextField();
	private final JTextField tfMidiPort = new EnhancedTextField();
	private final JTextField tfMidiChannel = new EnhancedTextField();
	private final JTextField tfEngine = new EnhancedTextField();
	private final JTextField tfInstrFile = new EnhancedTextField();
	private final JTextField tfInstrIndex = new EnhancedTextField();
	private final JTextField tfAudioDevice = new EnhancedTextField();
	
	ConfirmationWizardPage() {
		super(i18n.getLabel("ConfirmationWizardPage.subtitle"), "", Type.CONFIRMATION_PAGE);
		
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		
		//JPanel mainPane = new JPanel();
		
		setLayout(gridbag);
		
		c.fill = GridBagConstraints.NONE;
		
		c.gridx = 0;
		c.gridy = 1;
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(3, 3, 3, 3);
		gridbag.setConstraints(lMidiDevice, c);
		add(lMidiDevice); 
		
		c.gridx = 0;
		c.gridy = 2;
		gridbag.setConstraints(lMidiPort, c);
		add(lMidiPort);
		
		c.gridx = 0;
		c.gridy = 3;
		gridbag.setConstraints(lMidiChannel, c);
		add(lMidiChannel);
		
		c.gridx = 0;
		c.gridy = 4;
		gridbag.setConstraints(lEngine, c);
		add(lEngine);
		
		c.gridx = 0;
		c.gridy = 5;
		gridbag.setConstraints(lInstrFile, c);
		add(lInstrFile);
		
		c.gridx = 0;
		c.gridy = 6;
		gridbag.setConstraints(lInstrIndex, c);
		add(lInstrIndex);
		
		c.gridx = 0;
		c.gridy = 7;
		gridbag.setConstraints(lAudioDevice, c);
		add(lAudioDevice);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(tfMidiDevice, c);
		add(tfMidiDevice);
			
		c.gridx = 1;
		c.gridy = 2;
		gridbag.setConstraints(tfMidiPort, c);
		add(tfMidiPort);
		
		c.gridx = 1;
		c.gridy = 3;
		gridbag.setConstraints(tfMidiChannel, c);
		add(tfMidiChannel);
		
		c.gridx = 1;
		c.gridy = 4;
		gridbag.setConstraints(tfEngine, c);
		add(tfEngine);
		
		c.gridx = 1;
		c.gridy = 5;
		gridbag.setConstraints(tfInstrFile, c);
		add(tfInstrFile);
		
		c.gridx = 1;
		c.gridy = 6;
		gridbag.setConstraints(tfInstrIndex, c);
		add(tfInstrIndex);
		
		c.gridx = 1;
		c.gridy = 7;
		gridbag.setConstraints(tfAudioDevice, c);
		add(tfAudioDevice);
		
		lInfo.setFont(lInfo.getFont().deriveFont(java.awt.Font.PLAIN));
		
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.insets = new Insets(12, 3, 17, 3);
		c.anchor = GridBagConstraints.NORTH;
		gridbag.setConstraints(lInfo, c);
		add(lInfo); 
		
		JPanel p = new JPanel();
		p.setOpaque(false);
		c.gridx = 0;
		c.gridy = 8;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.insets = new Insets(0, 0, 0, 0);
		gridbag.setConstraints(p, c);
		add(p); 
		
	}
	
	public void
	preinitPage() {
		NewChannelWizardModel model = (NewChannelWizardModel)getWizardModel();
		setMidiDevice(model.getSelectedMidiDevice());
		
		
		if(model.getCustomMidiSettings()) {
			setMidiPort(model.getSelectedMidiPort());
			setMidiChannel(model.getSelectedMidiChannel());
		} else {
			MidiInputDevice mdev = model.getSelectedMidiDevice();
			if(mdev == null) setMidiPort(null);
			else if(mdev.getMidiPorts().length < 1) setMidiPort(null);
			else setMidiPort(mdev.getMidiPort(0));
			
			setMidiChannel(-1);
		}
		
		setEngine(model.getSelectedEngine());
		setAudioDevice(model.getSelectedAudioDevice());
		setInstrumentFile(model.getSelectedFile());
		setInstrumentIndex(model.getInstrumentIndex());
	}
	
	/**
	 * Invoked when the user clicks the 'Finish' button
	 * while this page is the current page of the wizard.
	 * @return <code>true</code>
	 */
	public boolean
	mayFinish() {
		final org.jsampler.task.Channel.Add ac = new org.jsampler.task.Channel.Add();
		
		ac.addTaskListener(new TaskListener() {
			public void
			taskPerformed(TaskEvent e) {
				if(ac.doneWithErrors()) return;
				doIt(ac.getResult());
			}
		});
		
		CC.getTaskQueue().add(ac);
		
		return true;
	}
	
	private void
	doIt(int chn) {
		if(getEngine() != null)
			CC.getTaskQueue().add(new LoadEngine(getEngine().getName(), chn));
		
		/*if(getMidiDPort() != null) CC.getTaskQueue().add (
			new SetChannelMidiInputPort(chn, getMidiPort().)
		);*/
		
		MidiInputDevice d = getMidiDevice();
		if(d != null) {
			CC.getTaskQueue().add(new SetMidiInputChannel(chn, d.getDeviceId()));
			
			if(getMidiPort() != null) {
				int port = -1;
				for(int i = 0; i < d.getMidiPortCount(); i++) {
					if(d.getMidiPort(i) == getMidiPort()) {
						port = i;
						break;
					}
				}
				
				if(port != -1) CC.getTaskQueue().add (
					new SetMidiInputPort(chn, port)
				);
			}
			
		
			int mc = (getMidiChannel() == -1 ? -1 : getMidiChannel() - 1);
			CC.getTaskQueue().add(new SetMidiInputChannel(chn, mc));
		}
			
			
		
		if(getAudioDevice() != null) CC.getTaskQueue().add (
			new SetAudioOutputDevice(chn, getAudioDevice().getDeviceId())
		);
		
		if(getInstrumentFile().length() > 0) CC.getTaskQueue().add (
			new LoadInstrument(getInstrumentFile(), getInstrumentIndex(), chn)
		);
	}
	
	/**
	 * Gets the audio output device to be used by this sampler channel.
	 * @return The audio output device to be used by this sampler channel.
	 */
	public AudioOutputDevice
	getAudioDevice() { return audioDev; }
	
	/**
	 * Sets the audio output device to be used by this sampler channel.
	 * @param dev The audio output device to be used by this sampler channel.
	 */
	public void
	setAudioDevice(AudioOutputDevice dev) {
		audioDev = dev;
		if(dev == null)	{
			tfAudioDevice.setText(i18n.getLabel("ConfirmationWizardPage.notSpecified"));
		} else {
			tfAudioDevice.setText(dev.getDeviceId() + " (" + dev.getDriverName() + ")");
		}
	}
	
	/**
	 * Gets the sampler engine to be used.
	 * @return The sampler engine to be used.
	 */
	public SamplerEngine
	getEngine() { return engine; }
	
	/**
	 * Sets the sampler engine to be used.
	 * @param engine The sampler engine to be used.
	 */
	public void
	setEngine(SamplerEngine engine) {
		this.engine = engine;
		if(engine == null) {
			tfEngine.setText(i18n.getLabel("ConfirmationWizardPage.notSpecified"));
		} else {
			tfEngine.setText(engine.getName() + " (" + engine.getDescription() + ")");
		}
	}
	
	/**
	 * Gets the name of the instrument file.
	 * @return The name of the instrument file.
	 */
	public String
	getInstrumentFile() { return instrFile; }
	
	/**
	 * Sets the name of the instrument file.
	 * @param file The name of the instrument file.
	 */
	public void
	setInstrumentFile(String file) {
		instrFile = file;
		
		if(file.length() == 0)	{
			tfInstrFile.setText(i18n.getLabel("ConfirmationWizardPage.notSpecified"));
		} else {
			tfInstrFile.setText(file);
		}
	}
	
	/**
	 * Gets the index of the instrument in the instrument file.
	 * @return The index of the instrument in the instrument file.
	 */
	public int
	getInstrumentIndex() { return instrIndex; }
	
	/**
	 * Sets the index of the instrument in the instrument file.
	 * @param idx The index of the instrument in the instrument file.
	 */
	public void
	setInstrumentIndex(int idx) {
		instrIndex = idx;
		tfInstrIndex.setText(String.valueOf(idx));
	}
	
	/**
	 * Gets the MIDI channel to which this sampler channel will listen to.
	 * @return The MIDI channel to which this sampler channel will listen to
	 * (-1 means all channels).
	 * .
	 */
	public int
	getMidiChannel() { return midiChannel; }
	
	/**
	 * Sets the MIDI channel to which this sampler channel will listen to.
	 * @param chn The MIDI channel to which this sampler channel will listen to
	 * (-1 means all channels).
	 * .
	 */
	public void
	setMidiChannel(int chn) {
		midiChannel = chn;
		tfMidiChannel.setText(chn == -1 ? "All" : String.valueOf(chn));
	}
	
	/**
	 * Gets the MIDI input device to which this sampler channel will be connected to.
	 * @return The MIDI input device to which this sampler channel will be connected to.
	 */
	public MidiInputDevice
	getMidiDevice() { return midiDev; }
	
	/**
	 * Sets the MIDI input device to which this sampler channel will be connected to.
	 * @param dev The numerical id of the MIDI input device to which
	 * this sampler channel will be connected to.
	 */
	public void
	setMidiDevice(MidiInputDevice dev) {
		midiDev = dev;
		
		if(dev == null) {
			tfMidiDevice.setText(i18n.getLabel("ConfirmationWizardPage.notSpecified"));
		} else {
			tfMidiDevice.setText(dev.getDeviceId() + " (" + dev.getDriverName() + ")");
		}
	}
	
	/**
	 * Gets the MIDI input port.
	 * @return The MIDI input port.
	 */
	public MidiPort
	getMidiPort() { return midiPort; }
	
	/**
	 * Sets the MIDI input port.
	 * @param port The MIDI input port.
	 */
	public void
	setMidiPort(MidiPort port) {
		midiPort = port;
		
		if(port == null) {
			tfMidiPort.setText(i18n.getLabel("ConfirmationWizardPage.notSpecified"));
		} else {
			tfMidiPort.setText(port.getName());
		}
	}
	
	private class EnhancedTextField extends JTextField {
		EnhancedTextField() {
			setEditable(false);
			setBorder(BorderFactory.createEmptyBorder());
		}
	}
}
