/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2009 Grigor Iliev <grigor@grigoriliev.com>
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
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;

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
import javax.swing.JTabbedPane;
import javax.swing.SpinnerNumberModel;

import net.sf.juife.EnhancedDialog;
import net.sf.juife.JuifeUtils;

import org.jsampler.CC;
import org.jsampler.JSUtils;
import org.jsampler.LSConsoleModel;

import org.jsampler.view.std.JSAdvancedGeneralPropsDlg;
import org.jsampler.view.std.JSConnectionPropsPane;
import org.jsampler.view.std.JSDefaultsPropsPane;
import org.jsampler.view.std.JSGeneralProps;
import org.jsampler.view.std.JSLSConsolePropsPane;
import org.jsampler.view.std.JSViewProps;

import static org.jsampler.view.fantasia.FantasiaI18n.i18n;
import static org.jsampler.view.fantasia.FantasiaPrefs.*;


/**
 *
 * @author Grigor Iliev
 */
public class PrefsDlg extends EnhancedDialog {
	private final JTabbedPane tabbedPane = new JTabbedPane();
	
	private final GeneralPane genPane = new GeneralPane(this);
	private final ViewPane viewPane = new ViewPane();
	private final ChannelsPropsPane channelsPane = new ChannelsPropsPane();
	private final ConsolePane consolePane = new ConsolePane();
	private final JSConnectionPropsPane connectionPane = new JSConnectionPropsPane();
	private final JSDefaultsPropsPane defaultsPane;
	
	private final JButton btnApply = new JButton(i18n.getButtonLabel("apply"));
	private final JButton btnClose = new JButton(i18n.getButtonLabel("close"));
	
	
	/** Creates a new instance of <code>PrefsDlg</code> */
	public
	PrefsDlg(Frame owner) { 
		super(owner, i18n.getLabel("PrefsDlg.title"), true);
		
		defaultsPane = new JSDefaultsPropsPane(this, Res.iconEdit16, true);
		
		JTabbedPane tp = tabbedPane;
		tp.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		
		tp.addTab(i18n.getLabel("PrefsDlg.tabGeneral"), genPane);
		tp.addTab(i18n.getLabel("PrefsDlg.tabView"), viewPane);
		tp.addTab(i18n.getLabel("PrefsDlg.tabChannels"), channelsPane);
		tp.addTab(i18n.getLabel("PrefsDlg.tabConsole"), consolePane);
		
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		p.add(connectionPane);
		tp.addTab(i18n.getLabel("PrefsDlg.tabBackend"), p);
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
		
		int i = preferences().getIntProperty("PrefsDlg.tabIndex");
		
		if(i >= 0 && i < tp.getTabCount()) tp.setSelectedIndex(i);
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
		viewPane.apply();
		channelsPane.apply();
		consolePane.apply();
		connectionPane.apply();
		defaultsPane.apply();
		
		preferences().setIntProperty("PrefsDlg.tabIndex", tabbedPane.getSelectedIndex());
		
		setVisible(false);
	}
	
	private void
	onExit() { setVisible(false); }
}

class GeneralPane extends JPanel {
	private final JCheckBox checkShowLSConsoleWhenRunScript =
		new JCheckBox(i18n.getLabel("GeneralPane.checkShowLSConsoleWhenRunScript"));
	
	private final JCheckBox checkShowVolumesInDecibels =
		new JCheckBox(i18n.getLabel("GeneralPane.checkShowVolumesInDecibels"));
	
	private final JSGeneralProps.PolyphonyPane polyphonyPane = new JSGeneralProps.PolyphonyPane();
	
	private final JSGeneralProps.JSamplerHomePane jSamplerHomePane =
		new JSGeneralProps.JSamplerHomePane();
	
	private final RecentScriptsPane recentScriptsPane = new RecentScriptsPane();
	
	private final JButton btnAdvanced = new JButton(i18n.getButtonLabel("GeneralPane.btnAdvanced"));
	
	private final Dialog owner;
	
	public
	GeneralPane(Dialog owner) {
		this.owner = owner;
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		checkShowLSConsoleWhenRunScript.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		
		boolean b = preferences().getBoolProperty(SHOW_LS_CONSOLE_WHEN_RUN_SCRIPT);
		checkShowLSConsoleWhenRunScript.setSelected(b);
		
		add(checkShowLSConsoleWhenRunScript);
		
		add(Box.createRigidArea(new Dimension(0, 6)));
		
		b = preferences().getBoolProperty(VOL_MEASUREMENT_UNIT_DECIBEL);
		checkShowVolumesInDecibels.setSelected(b);
		
		add(checkShowVolumesInDecibels);
		
		add(Box.createRigidArea(new Dimension(0, 6)));
		
		add(polyphonyPane);
		
		add(Box.createRigidArea(new Dimension(0, 6)));
		
		add(jSamplerHomePane);
		
		add(Box.createRigidArea(new Dimension(0, 6)));
		
		add(recentScriptsPane);
		
		add(Box.createRigidArea(new Dimension(0, 6)));
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		
		JPanel p2 = new JPanel();
		p2.setLayout(new BorderLayout());
		p.add(p2);
		p.add(btnAdvanced);
		p.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		add(p);
		
		add(Box.createGlue());
		
		setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		
		btnAdvanced.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { showAdvancedProperties(); }
		});
	}
	
	private void
	showAdvancedProperties() {
		new JSAdvancedGeneralPropsDlg(owner).setVisible(true);
	}
	
	protected void
	apply() {
		polyphonyPane.apply();
		
		boolean b = checkShowLSConsoleWhenRunScript.isSelected();
		preferences().setBoolProperty(SHOW_LS_CONSOLE_WHEN_RUN_SCRIPT, b);
		
		b = checkShowVolumesInDecibels.isSelected();
		preferences().setBoolProperty(VOL_MEASUREMENT_UNIT_DECIBEL, b);
		
		int size = recentScriptsPane.getRecentScriptsSize();
		preferences().setIntProperty(RECENT_LSCP_SCRIPTS_SIZE, size);
		((MainFrame)CC.getMainFrame()).updateRecentScriptsMenu();
		
		String s = jSamplerHomePane.getJSamplerHome();
		if(s.length() > 0 && !s.equals(CC.getJSamplerHome())) {
			JSUtils.changeJSamplerHome(s);
		}
	}
	
	private class RecentScriptsPane extends JSGeneralProps.RecentScriptsPane {
		protected void
		clearRecentScripts() {
			((MainFrame)CC.getMainFrame()).clearRecentScripts();
		}
	}
}

class ViewPane extends JPanel {
	private final JCheckBox checkTurnOffAnimationEffects =
		new JCheckBox(i18n.getLabel("GeneralPane.checkTurnOffAnimationEffects"));
	
	private final JCheckBox checkTurnOffCustomWindowDecoration =
		new JCheckBox(i18n.getLabel("ViewPane.checkTurnOffCustomWindowDecoration"));
	
	private final JCheckBox checkShowInstrumentsDb =
		new JCheckBox(i18n.getLabel("ViewPane.checkShowInstrumentsDb"));
	
	private final JCheckBox checkUseNativeFileChoosers =
		new JCheckBox(i18n.getLabel("ViewPane.checkUseNativeFileChoosers"));

	private final JSViewProps.MidiDevicesPane midiDevsPane = new JSViewProps.MidiDevicesPane();
	private final JSViewProps.AudioDevicesPane audioDevsPane = new JSViewProps.AudioDevicesPane();
	
	private final JSViewProps.ConfirmationMessagesPane confirmationMessagesPane =
		new JSViewProps.ConfirmationMessagesPane();
	
	ViewPane() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		checkTurnOffAnimationEffects.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		
		boolean b = !preferences().getBoolProperty(ANIMATED);
		checkTurnOffAnimationEffects.setSelected(b);
		
		add(checkTurnOffAnimationEffects);
		
		add(Box.createRigidArea(new Dimension(0, 6)));
		
		b = preferences().getBoolProperty("TurnOffCustomWindowDecoration");
		checkTurnOffCustomWindowDecoration.setSelected(b);
		checkTurnOffCustomWindowDecoration.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		add(checkTurnOffCustomWindowDecoration);
		add(Box.createRigidArea(new Dimension(0, 6)));
		
		b = preferences().getBoolProperty("rightSidePane.showInstrumentsDb");
		checkShowInstrumentsDb.setSelected(b);
		checkShowInstrumentsDb.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		add(checkShowInstrumentsDb);
		add(Box.createRigidArea(new Dimension(0, 6)));

		b = preferences().getBoolProperty("nativeFileChoosers");
		checkUseNativeFileChoosers.setSelected(b);
		checkUseNativeFileChoosers.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		add(checkUseNativeFileChoosers);
		add(Box.createRigidArea(new Dimension(0, 6)));
		
		add(midiDevsPane);
		add(audioDevsPane);
		add(confirmationMessagesPane);
	}
	
	protected void
	apply() {
		boolean b = !checkTurnOffAnimationEffects.isSelected();
		preferences().setBoolProperty(ANIMATED, b);
		
		String s = "TurnOffCustomWindowDecoration";
		preferences().setBoolProperty(s, checkTurnOffCustomWindowDecoration.isSelected());
		
		s = "rightSidePane.showInstrumentsDb";
		preferences().setBoolProperty(s, checkShowInstrumentsDb.isSelected());
		
		s = "nativeFileChoosers";
		preferences().setBoolProperty(s, checkUseNativeFileChoosers.isSelected());

		midiDevsPane.apply();
		audioDevsPane.apply();
		confirmationMessagesPane.apply();
	}
}

class ChannelsPropsPane extends JPanel {
	private final JCheckBox checkShowChannelNumbering =
		new JCheckBox(i18n.getLabel("ChannelsPropsPane.checkShowChannelNumbering"));
	
	private final JCheckBox checkShowMidiInfo =
		new JCheckBox(i18n.getLabel("ChannelsPropsPane.checkShowMidiInfo"));
	
	private final JCheckBox checkShowStreamVoiceCount =
		new JCheckBox(i18n.getLabel("ChannelsPropsPane.checkShowStreamVoiceCount"));

	private final JLabel lChannelLaneNumber =
		new JLabel(i18n.getLabel("ChannelsPropsPane.lChannelLaneNumber"));

	private JSpinner spChannelLaneNumber;
	
	ChannelsPropsPane() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		add(createChannelLanesPane());
		add(createSmallViewPane());

		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		add(p);
	}
	
	protected void
	apply() {
		boolean b = checkShowChannelNumbering.isSelected();
		preferences().setBoolProperty("channel.smallView.showChannelNumbering", b);
		
		b = checkShowMidiInfo.isSelected();
		preferences().setBoolProperty("channel.smallView.showMidiInfo", b);
		
		b = checkShowStreamVoiceCount.isSelected();
		preferences().setBoolProperty("channel.smallView.showStreamVoiceCount", b);

		int i = Integer.parseInt(spChannelLaneNumber.getValue().toString());
		preferences().setIntProperty("channelLanes.count", i);
	}
	
	private JPanel
	createSmallViewPane() {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		
		boolean b = preferences().getBoolProperty("channel.smallView.showChannelNumbering");
		checkShowChannelNumbering.setSelected(b);
		checkShowChannelNumbering.setAlignmentX(LEFT_ALIGNMENT);
		p.add(checkShowChannelNumbering);
		
		b = preferences().getBoolProperty("channel.smallView.showMidiInfo");
		checkShowMidiInfo.setSelected(b);
		checkShowMidiInfo.setAlignmentX(LEFT_ALIGNMENT);
		p.add(checkShowMidiInfo);
		
		b = preferences().getBoolProperty("channel.smallView.showStreamVoiceCount");
		checkShowStreamVoiceCount.setSelected(b);
		checkShowStreamVoiceCount.setAlignmentX(LEFT_ALIGNMENT);
		p.add(checkShowStreamVoiceCount);
		
		String s = i18n.getLabel("ChannelsPropsPane.smallView");
		p.setBorder(BorderFactory.createTitledBorder(s));
		p.setMaximumSize(new Dimension(Short.MAX_VALUE, p.getPreferredSize().height));
		
		return p;
	}

	private JPanel
	createChannelLanesPane() {
		int max = MainFrame.MAX_CHANNEL_LANE_NUMBER;

		int i = preferences().getIntProperty("channelLanes.count");
		if(i < 1 || i > max) i = max;
		spChannelLaneNumber = new JSpinner(new SpinnerNumberModel(i, 1, max, 1));

		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.add(lChannelLaneNumber);
		p.add(Box.createRigidArea(new Dimension(5, 0)));
		p.add(spChannelLaneNumber);
		p.setAlignmentX(LEFT_ALIGNMENT);
		p.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

		JPanel p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		String s = i18n.getLabel("ChannelsPropsPane.channelLanes");
		p2.setBorder(BorderFactory.createTitledBorder(s));
		p2.setMaximumSize(new Dimension(Short.MAX_VALUE, p.getPreferredSize().height));
		p2.add(p);
		p2.setAlignmentX(LEFT_ALIGNMENT);

		return p2;
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
