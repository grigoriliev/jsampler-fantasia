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

package org.jsampler.view.fantasia;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.sf.juife.EnhancedDialog;
import net.sf.juife.JuifeUtils;

import org.jsampler.CC;
import org.jsampler.LSConsoleModel;
import org.jsampler.Prefs;

import org.jsampler.view.std.JSConnectionPropsPane;
import org.jsampler.view.std.JSDefaultsPropsPane;
import org.jsampler.view.std.JSGeneralProps;
import org.jsampler.view.std.JSLSConsolePropsPane;

import static org.jsampler.view.fantasia.FantasiaI18n.i18n;
import static org.jsampler.view.fantasia.FantasiaPrefs.*;


/**
 *
 * @author Grigor Iliev
 */
public class PrefsDlg extends EnhancedDialog {
	private final GeneralPane genPane = new GeneralPane();
	private final ConsolePane consolePane = new ConsolePane();
	private final JSConnectionPropsPane connectionPane = new JSConnectionPropsPane();
	private final JSDefaultsPropsPane defaultsPane;
	
	private final JButton btnApply = new JButton(i18n.getButtonLabel("apply"));
	private final JButton btnClose = new JButton(i18n.getButtonLabel("close"));
	
	
	/** Creates a new instance of <code>PrefsDlg</code> */
	public
	PrefsDlg(Frame owner) {
		super(owner, i18n.getLabel("PrefsDlg.title"), true);
		
		defaultsPane = new JSDefaultsPropsPane(this, Res.iconEdit16);
		
		JTabbedPane tp = new JTabbedPane();
		tp.addTab(i18n.getLabel("PrefsDlg.tabGeneral"), genPane);
		tp.addTab(i18n.getLabel("PrefsDlg.tabConsole"), consolePane);
		
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		p.add(connectionPane, BorderLayout.NORTH);
		tp.addTab(i18n.getLabel("PrefsDlg.tabConnection"), p);
		tp.addTab(i18n.getLabel("PrefsDlg.tabDefaults"), defaultsPane);
		
		tp.setAlignmentX(RIGHT_ALIGNMENT);
		
		// Set preferred size for Apply & Exit buttons
		Dimension d = JuifeUtils.getUnionSize(btnApply, btnClose);
		btnApply.setPreferredSize(d);
		btnClose.setPreferredSize(d);
		
		JPanel btnPane = new JPanel();
		btnPane.setLayout(new BoxLayout(btnPane, BoxLayout.X_AXIS));
		btnPane.add(btnApply);
		btnPane.add(Box.createRigidArea(new Dimension(5, 0)));
		btnPane.add(btnClose);
		btnPane.setAlignmentX(RIGHT_ALIGNMENT);
		
		JPanel mainPane = new JPanel();
		mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
		mainPane.add(tp);
		mainPane.add(Box.createRigidArea(new Dimension(0, 12)));
		mainPane.add(btnPane);
		mainPane.setBorder(BorderFactory.createEmptyBorder(11, 12, 12, 12));
		
		getContentPane().add(mainPane);
		
		pack();
		setResizable(false);
		setLocation(JuifeUtils.centerLocation(this, owner));
		
		installListeners();
		
		connectionPane.setLSAddress(Prefs.getLSAddress());
		connectionPane.setLSPort(String.valueOf(Prefs.getLSPort()));
	}
	
	private void
	installListeners() {
		btnApply.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { onApply(); }
		});
		
		btnClose.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { onExit(); }
		});
	}
	
	protected void
	onOk() {  onApply(); }
	
	protected void
	onCancel() { onExit(); }
	
	private void
	onApply() {
		genPane.apply();
		consolePane.apply();
		connectionPane.apply();
		defaultsPane.apply();
		
		setVisible(false);
	}
	
	private void
	onExit() { setVisible(false); }
}

class GeneralPane extends JPanel {
	private final JCheckBox checkTurnOffAnimationEffects =
		new JCheckBox(i18n.getLabel("GeneralPane.checkTurnOffAnimationEffects"));
	
	private final JCheckBox checkShowLSConsoleWhenRunScript =
		new JCheckBox(i18n.getLabel("GeneralPane.checkShowLSConsoleWhenRunScript"));
	
	private final JSGeneralProps.JSamplerHomePane jSamplerHomePane =
		new JSGeneralProps.JSamplerHomePane();
	
	private final RecentScriptsPane recentScriptsPane = new RecentScriptsPane();
	
	public
	GeneralPane() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		checkTurnOffAnimationEffects.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		
		boolean b = !preferences().getBoolProperty(ANIMATED);
		checkTurnOffAnimationEffects.setSelected(b);
		
		add(checkTurnOffAnimationEffects);
		
		add(Box.createRigidArea(new Dimension(0, 6)));
		
		checkShowLSConsoleWhenRunScript.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		
		b = preferences().getBoolProperty(SHOW_LS_CONSOLE_WHEN_RUN_SCRIPT);
		checkShowLSConsoleWhenRunScript.setSelected(b);
		
		add(checkShowLSConsoleWhenRunScript);
		
		add(Box.createRigidArea(new Dimension(0, 6)));
		
		add(jSamplerHomePane);
		
		add(Box.createRigidArea(new Dimension(0, 6)));
		
		add(recentScriptsPane);
		add(Box.createGlue());
		
		setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		
		
	}
	
	protected void
	apply() {
		boolean b = !checkTurnOffAnimationEffects.isSelected();
		preferences().setBoolProperty(ANIMATED, b);
		
		b = checkShowLSConsoleWhenRunScript.isSelected();
		preferences().setBoolProperty(SHOW_LS_CONSOLE_WHEN_RUN_SCRIPT, b);
		
		int size = recentScriptsPane.getRecentScriptsSize();
		preferences().setIntProperty(RECENT_LSCP_SCRIPTS_SIZE, size);
		((MainFrame)CC.getMainFrame()).updateRecentScriptsMenu();
		
		String s = jSamplerHomePane.getJSamplerHome();
		if(s.length() > 0 && !s.equals(CC.getJSamplerHome())) {
			CC.changeJSamplerHome(s);
		}
	}
	
	private class RecentScriptsPane extends JSGeneralProps.RecentScriptsPane {
		protected void
		clearRecentScripts() {
			((MainFrame)CC.getMainFrame()).clearRecentScripts();
		}
	}
}

class ConsolePane extends JSLSConsolePropsPane {
	protected void
	clearConsoleHistory() {
		MainFrame mainFrame = (MainFrame)CC.getMainFrame();
		mainFrame.getLSConsoleModel().clearCommandHistory();
	}
	
	protected void
	apply() {
		super.apply();
		
		MainFrame mainFrame = (MainFrame)CC.getMainFrame();
		
		LSConsoleModel model = mainFrame.getLSConsoleModel();
		model.setCommandHistorySize(preferences().getIntProperty(LS_CONSOLE_HISTSIZE));
		
		LSConsolePane console = mainFrame.getLSConsolePane();
		
		int c = preferences().getIntProperty(LS_CONSOLE_TEXT_COLOR);
		console.setTextColor(new Color(c));
		
		c = preferences().getIntProperty(LS_CONSOLE_BACKGROUND_COLOR);
		console.setBackgroundColor(new Color(c));
		
		c = preferences().getIntProperty(LS_CONSOLE_NOTIFY_COLOR);
		console.setNotifyColor(new Color(c));
		
		c = preferences().getIntProperty(LS_CONSOLE_WARNING_COLOR);
		console.setWarningColor(new Color(c));
		
		c = preferences().getIntProperty(LS_CONSOLE_ERROR_COLOR);
		console.setErrorColor(new Color(c));
	}
}
