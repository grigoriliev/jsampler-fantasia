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

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sf.juife.Wizard;

import net.sf.juife.wizard.ConfirmationPage;
import net.sf.juife.wizard.DefaultWizardModel;
import net.sf.juife.wizard.UserInputPage;
import net.sf.juife.wizard.WizardPage;

import org.jsampler.AudioDeviceModel;
import org.jsampler.CC;
import org.jsampler.MidiDeviceModel;

import org.jsampler.task.AddChannel;

import static org.jsampler.view.classic.ClassicI18n.i18n;


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
	private MidiDeviceWizardPage midiDevicePage = new MidiDeviceWizardPage();
	private MidiPortWizardPage midiPortPage = new MidiPortWizardPage();
	private AudioDeviceWizardPage audioDevicePage = new AudioDeviceWizardPage();
	private EngineWizardPage enginePage = new EngineWizardPage();
	private InstrumentWizardPage instrumentPage = new InstrumentWizardPage();
	private ConfirmationWizardPage confirmationPage = new ConfirmationWizardPage();
			
	NewChannelWizardModel() {
		addPage(midiDevicePage);
		addPage(midiPortPage);
		addStep(i18n.getLabel("NewChannelWizard.step1"), 2);
		
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
}

class MidiDeviceWizardPage extends UserInputPage {
	private final JLabel lDevice = new JLabel(i18n.getLabel("MidiDeviceWizardPage.lDevice"));
	private final JComboBox cbDevices = new JComboBox();
	
	MidiDeviceWizardPage() {
		super(i18n.getLabel("MidiDeviceWizardPage.subtitle"));
		
		setMainInstructions(i18n.getLabel("MidiDeviceWizardPage.mainInstructions"));
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		
		p.add(lDevice);
		
		p.add(Box.createRigidArea(new Dimension(6, 0)));
		
		for(MidiDeviceModel m : CC.getSamplerModel().getMidiDeviceModels()) {
			cbDevices.addItem(m.getDeviceInfo());
		}
		
		cbDevices.setMaximumSize(cbDevices.getPreferredSize());
		
		p.add(cbDevices);
		
		setMainPane(p);
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
}

class AudioDeviceWizardPage extends UserInputPage {
	private final JLabel lDevice = new JLabel(i18n.getLabel("AudioDeviceWizardPage.lDevice"));
	private final JComboBox cbDevices = new JComboBox();
	
	AudioDeviceWizardPage() {
		super(i18n.getLabel("AudioDeviceWizardPage.subtitle"));
		
		setMainInstructions(i18n.getLabel("AudioDeviceWizardPage.mainInstructions"));
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		
		p.add(lDevice);
		
		p.add(Box.createRigidArea(new Dimension(6, 0)));
		
		for(AudioDeviceModel m : CC.getSamplerModel().getAudioDeviceModels()) {
			cbDevices.addItem(m.getDeviceInfo());
		}
		
		cbDevices.setMaximumSize(cbDevices.getPreferredSize());
		
		p.add(cbDevices);
		
		setMainPane(p);
	}
}

class EngineWizardPage extends UserInputPage {
	private final JLabel lEngine = new JLabel(i18n.getLabel("EngineWizardPage.lEngine"));
	private final JComboBox cbEngines = new JComboBox();
	
	EngineWizardPage() {
		super(i18n.getLabel("EngineWizardPage.subtitle"));
		
		setOptionalButtons(OptionalButtons.LAST);
		
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
}

class InstrumentWizardPage extends UserInputPage {
	
	InstrumentWizardPage() {
		super(i18n.getLabel("InstrumentWizardPage.subtitle"));
		
		setMainInstructions(i18n.getLabel("InstrumentWizardPage.mainInstructions"));
	}
}

class ConfirmationWizardPage extends ConfirmationPage {
	
	ConfirmationWizardPage() {
		//super(i18n.getLabel("ConfirmationWizardPage.subtitle"));
		
		setPageType(Type.CONFIRMATION_PAGE);
	}
	
	/**
	 * Invoked when the user clicks the 'Finish' button
	 * while this page is the current page of the wizard.
	 * @return <code>true</code>
	 */
	public boolean
	mayFinish() {
		CC.getTaskQueue().add(new AddChannel());
		
		return true;
	}
}
