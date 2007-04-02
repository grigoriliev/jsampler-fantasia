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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import net.sf.juife.OkCancelDialog;

import static org.jsampler.view.classic.ClassicI18n.i18n;


/**
 *
 * @author Grigor Iliev
 */
public class InstrumentChooser extends OkCancelDialog {
	private final JLabel lFilename = new JLabel(i18n.getLabel("InstrumentChooser.lFilename"));
	private final JLabel lIndex = new JLabel(i18n.getLabel("InstrumentChooser.lIndex"));
	
	private final JTextField tfFilename = new JTextField();
	private final JSpinner spinnerIndex = new JSpinner(new SpinnerNumberModel(0, 0, 500, 1));
	
	private final JButton btnBrowse =
		new JButton(Res.iconFolderOpen16);
	
	/** Creates a new instance of InstrumentChooser */
	public InstrumentChooser(Frame owner) {
		super(owner, i18n.getLabel("InstrumentChooser.title"));
		
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
		
		btnBrowse.setMargin(new Insets(0, 0, 0, 0));
		btnBrowse.setToolTipText(i18n.getLabel("InstrumentChooser.btnBrowse"));
		c.gridx = 2;
		c.gridy = 0;
		gridbag.setConstraints(btnBrowse, c);
		mainPane.add(btnBrowse);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(tfFilename, c);
		mainPane.add(tfFilename);
			
		c.gridx = 1;
		c.gridy = 1;
		gridbag.setConstraints(spinnerIndex, c);
		mainPane.add(spinnerIndex);
		
		tfFilename.setPreferredSize (
			new Dimension(200, tfFilename.getPreferredSize().height)
		);
		
		setMainPane(mainPane);
		
		btnBrowse.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { onBrowse(); }
		});
		
		btnBrowse.requestFocusInWindow();
	}
	
	protected void
	onOk() {
		if(tfFilename.getText().length() == 0) {
			JOptionPane.showMessageDialog (
				this, i18n.getLabel("InstrumentChooser.selectFile"),
				"",
				JOptionPane.INFORMATION_MESSAGE
			);
			setCancelled(true);
			return;
		}
		
		setVisible(false);
	}
	
	protected void
	onCancel() { setVisible(false); }
	
	private void
	onBrowse() {
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
	getFileName() { return tfFilename.getText(); }
	
	/**
	 * Gets the index of the instrument in the instrument file.
	 * @return The index of the instrument in the instrument file.
	 */
	public int
	getInstrumentIndex() { return Integer.parseInt(spinnerIndex.getValue().toString()); }
}
