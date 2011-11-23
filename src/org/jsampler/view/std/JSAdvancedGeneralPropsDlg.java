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

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sf.juife.swing.OkCancelDialog;

import org.jsampler.CC;

import static org.jsampler.JSPrefs.*;
import static org.jsampler.view.std.StdI18n.i18n;


/**
 *
 * @author Grigor Iliev
 */
public class JSAdvancedGeneralPropsDlg extends OkCancelDialog {
	private final MainPane mainPane = new MainPane();
	
	/** Creates a new instance of <code>JSAdvancedGeneralPropsDlg</code> */
	public
	JSAdvancedGeneralPropsDlg(Dialog owner) {
		super(owner, i18n.getLabel("JSAdvancedGeneralPropsDlg.title"));
		btnOk.setText(i18n.getButtonLabel("apply"));
		
		
		
		setMainPane(mainPane);
		btnOk.requestFocus();
	}
	
	protected void
	onOk() {
		if(!btnOk.isEnabled()) return;
		
		mainPane.apply();
		
		setVisible(false);
		setCancelled(false);
	}
	
	protected void
	onCancel() { setVisible(false); }
	
	public static class MainPane extends JPanel {
		private final JSGeneralProps.MaxVolumePane maxVolumePane = new JSGeneralProps.MaxVolumePane();
		private final MidiBankProgramNumberingPane numberingPane = new MidiBankProgramNumberingPane();
		private final ExportSettingsPane exportSettingsPane = new ExportSettingsPane();
		
		public
		MainPane() {
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			add(maxVolumePane);
			add(Box.createRigidArea(new Dimension(0, 6)));
			add(numberingPane);
			add(Box.createRigidArea(new Dimension(0, 6)));
			add(exportSettingsPane);
		}
		
		public void
		apply() {
			maxVolumePane.apply();
			numberingPane.apply();
			exportSettingsPane.apply();
		}
	}
	
	public static class MidiBankProgramNumberingPane extends JPanel {
		private final JLabel lFirstBank =
			new JLabel(i18n.getLabel("JSAdvancedGeneralPropsDlg.lFirstBank"));
		
		private final JLabel lFirstProgram =
			new JLabel(i18n.getLabel("JSAdvancedGeneralPropsDlg.lFirstProgram"));
			
		private final JComboBox cbFirstBank = new JComboBox();
		private final JComboBox cbFirstProgram = new JComboBox();
		
		public
		MidiBankProgramNumberingPane() {
			GridBagLayout gridbag = new GridBagLayout();
			GridBagConstraints c = new GridBagConstraints();
			
			setLayout(gridbag);
			
			c.fill = GridBagConstraints.NONE;
			
			c.gridx = 0;
			c.gridy = 0;
			c.anchor = GridBagConstraints.EAST;
			c.insets = new Insets(3, 3, 3, 3);
			gridbag.setConstraints(lFirstBank, c);
			add(lFirstBank);
			
			c.gridx = 0;
			c.gridy = 1;
			gridbag.setConstraints(lFirstProgram, c);
			add(lFirstProgram);
			
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 1;
			c.gridy = 0;
			c.weightx = 1.0;
			c.insets = new Insets(3, 3, 3, 3);
			c.anchor = GridBagConstraints.WEST;
			gridbag.setConstraints(cbFirstBank, c);
			add(cbFirstBank);
			
			c.gridx = 1;
			c.gridy = 1;
			gridbag.setConstraints(cbFirstProgram, c);
			add(cbFirstProgram);
			
			String s = i18n.getLabel("MidiBankProgramNumberingPane.title");
			setBorder(BorderFactory.createTitledBorder(s));
			
			cbFirstBank.addItem("0");
			cbFirstBank.addItem("1");
			
			cbFirstProgram.addItem("0");
			cbFirstProgram.addItem("1");
			
			int i = CC.preferences().getIntProperty(FIRST_MIDI_BANK_NUMBER);
			if(i < 0 || i > 1) i  = 1;
			cbFirstBank.setSelectedIndex(i);
			
			i = CC.preferences().getIntProperty(FIRST_MIDI_PROGRAM_NUMBER);
			if(i < 0 || i > 1) i  = 1;
			cbFirstProgram.setSelectedIndex(i);
			
			setAlignmentX(LEFT_ALIGNMENT);
		}
		
		public void
		apply() {
			int i = cbFirstBank.getSelectedIndex();
			if(i < 0) i = 1;
			CC.preferences().setIntProperty(FIRST_MIDI_BANK_NUMBER, i);
			
			i = cbFirstProgram.getSelectedIndex();
			if(i < 0) i = 1;
			CC.preferences().setIntProperty(FIRST_MIDI_PROGRAM_NUMBER, i);
		}
	}
	
	public static class ExportSettingsPane extends JPanel {
		private final JCheckBox checkExportMidiMapsToSession =
			new JCheckBox(i18n.getLabel("ExportSettingsPane.checkExportMidiMapsToSession"));
		
		private final JCheckBox checkLoadInstrInBackground =
			new JCheckBox(i18n.getLabel("ExportSettingsPane.checkLoadInstrInBackground"));
		
		public
		ExportSettingsPane() {
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			
			boolean b;
			b = CC.preferences().getBoolProperty(EXPORT_MIDI_MAPS_TO_SESSION_SCRIPT);
			checkExportMidiMapsToSession.setSelected(b);
			checkExportMidiMapsToSession.setAlignmentX(LEFT_ALIGNMENT);
			add(checkExportMidiMapsToSession);
			
			b = CC.preferences().getBoolProperty(LOAD_MIDI_INSTRUMENTS_IN_BACKGROUND);
			checkLoadInstrInBackground.setSelected(b);
			checkLoadInstrInBackground.setAlignmentX(LEFT_ALIGNMENT);
			add(checkLoadInstrInBackground);
			
			String s = i18n.getLabel("ExportSettingsPane.title");
			setBorder(BorderFactory.createTitledBorder(s));
			setAlignmentX(LEFT_ALIGNMENT);
		}
	
		protected void
		apply() {
			boolean b = checkExportMidiMapsToSession.isSelected();
			CC.preferences().setBoolProperty(EXPORT_MIDI_MAPS_TO_SESSION_SCRIPT, b);
			
			b = checkLoadInstrInBackground.isSelected();
			CC.preferences().setBoolProperty(LOAD_MIDI_INSTRUMENTS_IN_BACKGROUND, b);
		}
	}
}
