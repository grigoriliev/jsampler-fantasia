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

import java.awt.Color;
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
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.jsampler.CC;
import org.jsampler.JSPrefs;

import static org.jsampler.view.std.StdI18n.i18n;
import static org.jsampler.JSPrefs.*;


/**
 *
 * @author Grigor Iliev
 */
public class JSLSConsolePropsPane extends JPanel {
	private final JCheckBox checkSaveCmdHist =
		new JCheckBox(i18n.getLabel("JSLSConsolePropsPane.checkSaveCmdHist"));
	
	private final JLabel lCmdHistorySize =
		new JLabel(i18n.getLabel("JSLSConsolePropsPane.lCmdHistorySize"));
	private JSpinner spCmdHistorySize;
	private final JLabel lLines = new JLabel(i18n.getLabel("JSLSConsolePropsPane.lLines"));
	private final JButton btnClearCmdHistory =
		new JButton(i18n.getButtonLabel("JSLSConsolePropsPane.btnClearCmdHistory"));
	
	private final JLabel lTextColor =
		new JLabel(i18n.getLabel("JSLSConsolePropsPane.lTextColor"));
	
	private final JSColorButton btnTextColor = new JSColorButton();
	
	private final JLabel lBGColor = new JLabel(i18n.getLabel("JSLSConsolePropsPane.lBGColor"));
	private final JSColorButton btnBGColor = new JSColorButton();
	
	private final JLabel lNotifyColor =
		new JLabel(i18n.getLabel("JSLSConsolePropsPane.lNotifyColor"));
	
	private final JSColorButton btnNotifyColor = new JSColorButton();
	
	private final JLabel lWarningColor
		= new JLabel(i18n.getLabel("JSLSConsolePropsPane.lWarningColor"));
	
	private final JSColorButton btnWarningColor = new JSColorButton();
	
	private final JLabel lErrorColor =
		new JLabel(i18n.getLabel("JSLSConsolePropsPane.lErrorColor"));
	
	private final JSColorButton btnErrorColor = new JSColorButton();
	
	
	/** Creates a new instance of <code>JSLSConsolePropsPane</code> */
	public
	JSLSConsolePropsPane() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		add(checkSaveCmdHist);
		
		add(createCommandHistoryPane());
		add(Box.createRigidArea(new Dimension(0, 6)));
		add(createConsoleColorsPane());
		
		setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
	}
	
	private JPanel
	createCommandHistoryPane() {
		JPanel chp = new JPanel();
		chp.setAlignmentX(CENTER_ALIGNMENT);
		
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
	
		chp.setLayout(gridbag);
		
		int i = preferences().getIntProperty(LS_CONSOLE_HISTSIZE, 1000);
		spCmdHistorySize = new JSpinner(new SpinnerNumberModel(i, 0, 20000, 1));
		spCmdHistorySize.setMaximumSize(spCmdHistorySize.getPreferredSize());
		
		btnClearCmdHistory.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				JSLSConsolePane.clearConsoleHistory();
				clearConsoleHistory();
			}
		});
			
		btnClearCmdHistory.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		add(btnClearCmdHistory);
		
		c.fill = GridBagConstraints.NONE;
		
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(0, 6, 3, 0);
		gridbag.setConstraints(lCmdHistorySize, c);
		chp.add(lCmdHistorySize); 

		c.gridx = 1;
		c.gridy = 0;
		gridbag.setConstraints(spCmdHistorySize, c);
		chp.add(spCmdHistorySize); 

		c.gridx = 2;
		c.gridy = 0;
		gridbag.setConstraints(lLines, c);
		chp.add(lLines); 

		c.gridx = 3;
		c.gridy = 0;
		c.insets = new Insets(0, 12, 3, 6);
		gridbag.setConstraints(btnClearCmdHistory, c);
		chp.add(btnClearCmdHistory); 
		
		checkSaveCmdHist.setSelected(preferences().getBoolProperty(SAVE_LS_CONSOLE_HISTORY));
		checkSaveCmdHist.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 4;
		c.insets = new Insets(3, 6, 3, 6);
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(checkSaveCmdHist, c);
		chp.add(checkSaveCmdHist); 

		String s = i18n.getLabel("JSLSConsolePropsPane.commandHistory");
		chp.setBorder(BorderFactory.createTitledBorder(s));
		
		chp.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
		
		return chp;
	}
	
	private JPanel
	createConsoleColorsPane() {
		JPanel ccp = new JPanel();
		ccp.setAlignmentX(CENTER_ALIGNMENT);
		
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
	
		ccp.setLayout(gridbag);
		
		c.fill = GridBagConstraints.NONE;
	
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(3, 3, 3, 3);
		gridbag.setConstraints(lTextColor, c);
		ccp.add(lTextColor); 

		c.gridx = 0;
		c.gridy = 1;
		gridbag.setConstraints(lBGColor, c);
		ccp.add(lBGColor);
	
		c.gridx = 0;
		c.gridy = 2;
		gridbag.setConstraints(lNotifyColor, c);
		ccp.add(lNotifyColor);
	
		c.gridx = 0;
		c.gridy = 3;
		gridbag.setConstraints(lWarningColor, c);
		ccp.add(lWarningColor);
	
		c.gridx = 0;
		c.gridy = 4;
		gridbag.setConstraints(lErrorColor, c);
		ccp.add(lErrorColor);
	
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		//c.weightx = 1.0;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(btnTextColor, c);
		ccp.add(btnTextColor);
		
		c.gridx = 1;
		c.gridy = 1;
		gridbag.setConstraints(btnBGColor, c);
		ccp.add(btnBGColor);
		
		c.gridx = 1;
		c.gridy = 2;
		gridbag.setConstraints(btnNotifyColor, c);
		ccp.add(btnNotifyColor);
		
		c.gridx = 1;
		c.gridy = 3;
		gridbag.setConstraints(btnWarningColor, c);
		ccp.add(btnWarningColor);
		
		c.gridx = 1;
		c.gridy = 4;
		gridbag.setConstraints(btnErrorColor, c);
		ccp.add(btnErrorColor);
		
		int i = preferences().getIntProperty(LS_CONSOLE_TEXT_COLOR);
		btnTextColor.setColor(new Color(i));
		
		i = preferences().getIntProperty(LS_CONSOLE_BACKGROUND_COLOR);
		btnBGColor.setColor(new Color(i));
		
		i = preferences().getIntProperty(LS_CONSOLE_NOTIFY_COLOR);
		btnNotifyColor.setColor(new Color(i));
		
		i = preferences().getIntProperty(LS_CONSOLE_WARNING_COLOR);
		btnWarningColor.setColor(new Color(i));
		
		i = preferences().getIntProperty(LS_CONSOLE_ERROR_COLOR);
		btnErrorColor.setColor(new Color(i));
		
		String s = i18n.getButtonLabel("JSLSConsolePropsPane.btnDefaults");
		JButton btnDefaults = new JButton(s);
		
		btnDefaults.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				int i = preferences().getDefaultIntValue(LS_CONSOLE_TEXT_COLOR);
				preferences().setIntProperty(LS_CONSOLE_TEXT_COLOR, i);
				btnTextColor.setColor(new Color(i));
				
				i = preferences().getDefaultIntValue(LS_CONSOLE_BACKGROUND_COLOR);
				preferences().setIntProperty(LS_CONSOLE_BACKGROUND_COLOR, i);
				btnBGColor.setColor(new Color(i));
				
				i = preferences().getDefaultIntValue(LS_CONSOLE_NOTIFY_COLOR);
				preferences().setIntProperty(LS_CONSOLE_NOTIFY_COLOR, i);
				btnNotifyColor.setColor(new Color(i));
				
				i = preferences().getDefaultIntValue(LS_CONSOLE_WARNING_COLOR);
				preferences().setIntProperty(LS_CONSOLE_WARNING_COLOR, i);
				btnWarningColor.setColor(new Color(i));
				
				i = preferences().getDefaultIntValue(LS_CONSOLE_ERROR_COLOR);
				preferences().setIntProperty(LS_CONSOLE_ERROR_COLOR, i);
				btnErrorColor.setColor(new Color(i));
			}
		});
		
		JPanel p = new JPanel();
		p.setAlignmentX(LEFT_ALIGNMENT);
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 6));
		p.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
		
		p.add(Box.createGlue());
		p.add(btnDefaults);
		p.add(Box.createGlue());
		
		c.gridx = 0;
		c.gridy = 5;
		c.gridwidth = 2;
		gridbag.setConstraints(p, c);
		ccp.add(p);
		
		s = i18n.getLabel("JSLSConsolePropsPane.consoleColors");
		ccp.setBorder(BorderFactory.createTitledBorder(s));
		
		ccp.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
		
		return ccp;
	}
	
	private JSPrefs
	preferences() { return CC.getViewConfig().preferences(); }
	
	/**
	 * Override this method to clear the command history of the LS Console's model.
	 */
	protected void
	clearConsoleHistory() { }
	
	protected void
	apply() {
		boolean b = checkSaveCmdHist.isSelected();
		preferences().setBoolProperty(SAVE_LS_CONSOLE_HISTORY, b);
		
		int size = Integer.parseInt(spCmdHistorySize.getValue().toString());
		preferences().setIntProperty(LS_CONSOLE_HISTSIZE, size);
		
		///***///
		
		int c = btnTextColor.getColor().getRGB();
		preferences().setIntProperty(LS_CONSOLE_TEXT_COLOR, c);
		
		c = btnBGColor.getColor().getRGB();
		preferences().setIntProperty(LS_CONSOLE_BACKGROUND_COLOR, c);
		
		c = btnNotifyColor.getColor().getRGB();
		preferences().setIntProperty(LS_CONSOLE_NOTIFY_COLOR, c);
		
		c = btnWarningColor.getColor().getRGB();
		preferences().setIntProperty(LS_CONSOLE_WARNING_COLOR, c);
		
		c = btnErrorColor.getColor().getRGB();
		preferences().setIntProperty(LS_CONSOLE_ERROR_COLOR, c);
	}
}
