/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2008 Grigor Iliev <grigor@grigoriliev.com>
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
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sf.juife.JuifeUtils;

import org.jsampler.CC;
import org.jsampler.HF;
import org.jsampler.JSPrefs;

import org.jsampler.view.ServerTable;
import org.jsampler.view.ServerTableModel;

import static org.jsampler.view.std.StdI18n.i18n;
import static org.jsampler.view.std.StdPrefs.*;


/**
 *
 * @author Grigor Iliev
 */
public class JSConnectionPropsPane extends JPanel {
	private final JCheckBox checkManualSelect =
		new JCheckBox(i18n.getLabel("JSConnectionPropsPane.checkManualSelect"));
	
	private final JLabel lReadTimeout =
		new JLabel(i18n.getLabel("JSConnectionPropsPane.lReadTimeout"));
	
	private final JSpinner spinnerTimeout = new JSpinner(new SpinnerNumberModel(0, 0, 2000, 1));
	
	private final ServerListPane serverListPane;
	private final LocalSettingsPane localSettingsPane = new LocalSettingsPane();
	
	
	/** Creates a new instance of <code>JSConnectionPropsPane</code> */
	public
	JSConnectionPropsPane() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		boolean b = preferences().getBoolProperty(MANUAL_SERVER_SELECT_ON_STARTUP);
		checkManualSelect.setSelected(b);
		
		checkManualSelect.setAlignmentX(LEFT_ALIGNMENT);
		add(checkManualSelect);
		add(Box.createRigidArea(new Dimension(0, 6)));
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		
		p.add(lReadTimeout);
		p.add(Box.createRigidArea(new Dimension(6, 0)));
		
		int i = preferences().getIntProperty(SOCKET_READ_TIMEOUT);
		spinnerTimeout.setValue(i);
		p.add(spinnerTimeout);
		
		p.setAlignmentX(LEFT_ALIGNMENT);
		p.setMaximumSize(new Dimension(Short.MAX_VALUE, p.getPreferredSize().height));
		add(p);
		add(Box.createRigidArea(new Dimension(0, 6)));
		
		serverListPane = createServerListPane();
		add(serverListPane);
		add(Box.createRigidArea(new Dimension(0, 6)));
		
		add(localSettingsPane);
		
		setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
	}
	
	private ServerListPane
	createServerListPane() {
		ServerListPane p = new ServerListPane();
		p.setPreferredSize(new Dimension(200, 160));
		p.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
		
		String s = i18n.getLabel("JSConnectionPropsPane.title");
		p.setBorder(BorderFactory.createTitledBorder(s));
		p.setAlignmentX(LEFT_ALIGNMENT);
		
		return p;
	}
	
	private static JSPrefs
	preferences() { return CC.getViewConfig().preferences(); }
	
	/**
	 * Gets the read timeout in seconds.
	 */
	public int
	getReadTimeout() { return Integer.parseInt(spinnerTimeout.getValue().toString()); }
	
	public void
	apply() {
		boolean b = checkManualSelect.isSelected();
		preferences().setBoolProperty(MANUAL_SERVER_SELECT_ON_STARTUP, b);
		
		preferences().setIntProperty(SOCKET_READ_TIMEOUT, getReadTimeout());
		
		CC.setClientReadTimeout(getReadTimeout());
		
		int i = serverListPane.serverTable.getSelectedRow();
		if(i != -1) preferences().setIntProperty(SERVER_INDEX, i);
		
		localSettingsPane.apply();
	}
	
	public static class ServerListPane extends JPanel {
		protected final ServerTable serverTable;
		
		private final JButton btnAdd = new JButton(i18n.getButtonLabel("add"));
		private final JButton btnRemove = new JButton(i18n.getButtonLabel("remove"));
		
		private final JButton btnMoveUp =
			new JButton(i18n.getButtonLabel("JSConnectionPropsPane.btnMoveUp"));
		
		private final JButton btnMoveDown =
			new JButton(i18n.getButtonLabel("JSConnectionPropsPane.btnMoveDown"));
		
		public
		ServerListPane() {
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			
			ServerTableModel model = new ServerTableModel(CC.getServerList());
			serverTable = new ServerTable(model);
			
			add(new JScrollPane(serverTable));
			
			int h = btnAdd.getPreferredSize().height;
			btnAdd.setMaximumSize(new Dimension(Short.MAX_VALUE, h));
			
			h = btnRemove.getPreferredSize().height;
			btnRemove.setMaximumSize(new Dimension(Short.MAX_VALUE, h));
			
			h = btnMoveUp.getPreferredSize().height;
			btnMoveUp.setMaximumSize(new Dimension(Short.MAX_VALUE, h));
			
			h = btnMoveDown.getPreferredSize().height;
			btnMoveDown.setMaximumSize(new Dimension(Short.MAX_VALUE, h));
			
			JPanel p = new JPanel();
			p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
			p.add(btnAdd);
			p.add(Box.createRigidArea(new Dimension(0, 5)));
			p.add(btnRemove);
			p.add(Box.createRigidArea(new Dimension(0, 17)));
			p.add(btnMoveUp);
			p.add(Box.createRigidArea(new Dimension(0, 5)));
			p.add(btnMoveDown);
			p.add(Box.createGlue());
			
			p.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
			
			add(p);
			
			installListeners();
			
			int i = preferences().getIntProperty(JSPrefs.SERVER_INDEX);
			int size = CC.getServerList().getServerCount();
			if(size > 0) {
				if(i < 0 || i >= size) {
					serverTable.getSelectionModel().setSelectionInterval(0, 0);
				} else {
					serverTable.getSelectionModel().setSelectionInterval(i, i);
				}
			}
		}
		
		private void
		installListeners() {
			btnAdd.addActionListener(new ActionListener() {
				public void
				actionPerformed(ActionEvent e) { addServer(); }
			});
			
			btnRemove.addActionListener(new ActionListener() {
				public void
				actionPerformed(ActionEvent e) {
					removeServer();
				}
			});
			
			btnMoveUp.addActionListener(new ActionListener() {
				public void
				actionPerformed(ActionEvent e) {
					serverTable.moveSelectedServerUp();
				}
			});
			
			btnMoveDown.addActionListener(new ActionListener() {
				public void
				actionPerformed(ActionEvent e) {
					serverTable.moveSelectedServerDown();
				}
			});
			
			ServerSelectionHandler l = new ServerSelectionHandler();
			serverTable.getSelectionModel().addListSelectionListener(l);
		}
		
		private void
		addServer() {
			int i = serverTable.getSelectedRow();
			
			JSAddServerDlg dlg;
			Window w = JuifeUtils.getWindow(this);
			if(w instanceof Dialog) dlg = new JSAddServerDlg((Dialog)w);
			else if(w instanceof Frame) dlg = new JSAddServerDlg((Frame)w);
			else dlg = new JSAddServerDlg(CC.getMainFrame());
			
			dlg.setVisible(true);
			if(dlg.isCancelled()) return;
			
			serverTable.getSelectionModel().setSelectionInterval(i, i);
		}
		
		private void
		removeServer() {
			if(CC.getServerList().getServerCount() < 2) {
				Window w = JuifeUtils.getWindow(this);
				String s = i18n.getError("JSConnectionPropsPane.cantRemove");
				if(w instanceof Dialog) HF.showErrorMessage(s, (Dialog)w);
				else if(w instanceof Frame) HF.showErrorMessage(s, (Frame)w);
				return;
			}
			
			serverTable.removeSelectedServer();
		}
		
		private class ServerSelectionHandler implements ListSelectionListener {
			@Override
			public void
			valueChanged(ListSelectionEvent e) {
				if(e.getValueIsAdjusting()) return;
				
				if(serverTable.getSelectedServer() == null) {
					btnMoveUp.setEnabled(false);
					btnMoveDown.setEnabled(false);
					return;
				}
				
				int idx = serverTable.getSelectedRow();
				btnMoveUp.setEnabled(idx != 0);
				btnMoveDown.setEnabled(idx != serverTable.getRowCount() - 1);
			}
		}
	}
	
	public static class LocalSettingsPane extends JPanel {
		private final JCheckBox checkLaunchBackend =
			new JCheckBox(i18n.getLabel("JSConnectionPropsPane.checkLaunchBackend"));
		
		private final JLabel lCommand =
			new JLabel(i18n.getLabel("JSConnectionPropsPane.lCommand"));
		
		private final JLabel lDelay =
			new JLabel(i18n.getLabel("JSConnectionPropsPane.lDelay"));
		
		private final JLabel lSeconds =
			new JLabel(i18n.getLabel("JSConnectionPropsPane.lSeconds"));
		
		private final JTextField tfCommand = new JTextField();
		private final JSpinner spinnerDelay;
		
		public
		LocalSettingsPane() {
			GridBagLayout gridbag = new GridBagLayout();
			GridBagConstraints c = new GridBagConstraints();
			
			setLayout(gridbag);
			
			c.fill = GridBagConstraints.NONE;
			
			c.gridx = 0;
			c.gridy = 0;
			c.anchor = GridBagConstraints.WEST;
			c.insets = new Insets(0, 3, 3, 3);
			c.gridwidth = 3;
			gridbag.setConstraints(checkLaunchBackend, c);
			add(checkLaunchBackend);
			
			c.gridx = 0;
			c.gridy = 1;
			c.gridwidth = 1;
			c.insets = new Insets(3, 9, 3, 3);
			c.anchor = GridBagConstraints.EAST;
			gridbag.setConstraints(lCommand, c);
			add(lCommand);
			
			c.gridx = 0;
			c.gridy = 2;
			c.insets = new Insets(3, 3, 3, 3);
			gridbag.setConstraints(lDelay, c);
			add(lDelay);
			
			spinnerDelay = new JSpinner(new SpinnerNumberModel(3, 0, 100, 1));
			
			c.gridx = 1;
			c.gridy = 2;
			c.insets = new Insets(3, 3, 3, 3);
			c.anchor = GridBagConstraints.WEST;
			gridbag.setConstraints(spinnerDelay, c);
			add(spinnerDelay);
			
			c.gridx = 2;
			c.gridy = 2;
			gridbag.setConstraints(lSeconds, c);
			add(lSeconds);
			
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 1;
			c.gridy = 1;
			c.weightx = 1.0;
			c.gridwidth = 2;
			c.anchor = GridBagConstraints.WEST;
			gridbag.setConstraints(tfCommand, c);
			add(tfCommand);
			
			String s = i18n.getLabel("JSConnectionPropsPane.localSettings");
			setBorder(BorderFactory.createTitledBorder(s));
			
			setMaximumSize(new Dimension(Short.MAX_VALUE, getPreferredSize().height));
			
			setAlignmentX(LEFT_ALIGNMENT);
			
			checkLaunchBackend.addActionListener(new ActionListener() {
				public void
				actionPerformed(ActionEvent e) {
					updateLocalSettingsState();
				}
			});
			
			boolean b = preferences().getBoolProperty(LAUNCH_BACKEND_LOCALLY);
			checkLaunchBackend.setSelected(b);
			
			s = preferences().getStringProperty(BACKEND_LAUNCH_COMMAND);
			tfCommand.setText(s);
			
			int i = preferences().getIntProperty(BACKEND_LAUNCH_DELAY);
			spinnerDelay.setValue(i);
			
			updateLocalSettingsState();
		}
		
		private void
		updateLocalSettingsState() {
			boolean b = checkLaunchBackend.isSelected();
			tfCommand.setEnabled(b);
			spinnerDelay.setEnabled(b);
		}
		
		public void
		apply() {
			boolean b = checkLaunchBackend.isSelected();
			preferences().setBoolProperty(LAUNCH_BACKEND_LOCALLY, b);
			
			preferences().setStringProperty(BACKEND_LAUNCH_COMMAND, tfCommand.getText());
			
			int i = Integer.parseInt(spinnerDelay.getValue().toString());
			preferences().setIntProperty(BACKEND_LAUNCH_DELAY, i);
		}
	}
}
