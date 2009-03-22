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

package org.jsampler.view.std;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sf.juife.InformationDialog;
import net.sf.juife.Task;

import org.jsampler.CC;
import org.jsampler.task.InstrumentsDb.SetInstrumentFilePath;
import org.jsampler.view.LostFilesTable;

import static org.jsampler.view.std.StdI18n.i18n;

/**
 *
 * @author Grigor Iliev
 */
public class JSLostFilesDlg extends InformationDialog {
	private final LostFilesTable lostFilesTable = new LostFilesTable();
	
	private final JButton btnReplace = new JButton(i18n.getButtonLabel("JSLostFilesDlg.btnReplace"));
	private final JButton btnRename = new JButton(i18n.getButtonLabel("rename"));
	private final JButton btnUpdate = new JButton(i18n.getButtonLabel("update"));
	
	/** Creates a new instance of <code>JSLostFilesDlg</code> */
	public
	JSLostFilesDlg(Frame owner) {
		super(owner, i18n.getLabel("JSLostFilesDlg.title"));
		
		JPanel mainPane = new JPanel();
		mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
		
		JScrollPane sp = new JScrollPane(lostFilesTable);
		sp.setPreferredSize(new Dimension(500, 250));
		mainPane.add(sp);
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		btnRename.setEnabled(false);
		p.add(btnRename);
		p.add(Box.createRigidArea(new Dimension(5, 0)));
		p.add(btnReplace);
		p.add(Box.createGlue());
		p.add(btnUpdate);
		
		mainPane.add(p);
		
		CC.getLostFilesModel().addChangeListener(getHandler());
		CC.getLostFilesModel().update();
		setMainPane(mainPane);
		
		lostFilesTable.getSelectionModel().addListSelectionListener(getHandler());
		
		btnRename.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { lostFilesTable.editSelectedFile(); }
		});
		
		btnReplace.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				new JSReplaceLostFilesDlg(JSLostFilesDlg.this).setVisible(true);
			}
		});
		
		btnUpdate.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { CC.getLostFilesModel().update(); }
		});
	}
	
	
	private final EventHandler eventHandler = new EventHandler();
	
	private EventHandler
	getHandler() { return eventHandler; }
	
	private class EventHandler implements ListSelectionListener, ChangeListener {
		@Override
		public void
		valueChanged(ListSelectionEvent e) {
			if(e.getValueIsAdjusting()) return;
			
			ListSelectionModel lsm = (ListSelectionModel)e.getSource();
			btnRename.setEnabled(!lsm.isSelectionEmpty());
		}
		
		@Override
		public void
		stateChanged(ChangeEvent e) {
			boolean b = CC.getLostFilesModel().getLostFileCount() != 0;
			btnReplace.setEnabled(b);
		}
	}
}

class JSReplaceLostFilesDlg extends InformationDialog {
	private final JLabel lFind = new JLabel(i18n.getLabel("JSReplaceLostFilesDlg.lFind"));
	private final JLabel lReplace = new JLabel(i18n.getLabel("JSReplaceLostFilesDlg.lReplace"));
	
	private final JTextField tfFind = new JTextField();
	private final JTextField tfReplace = new JTextField();
	
	private final JButton btnBrowse;
	private final JButton btnBrowse2;
	
	private final JButton btnPreview =
		new JButton(i18n.getButtonLabel("JSReplaceLostFilesDlg.btnPreview"));
	
	private final JButton btnReplace =
		new JButton(i18n.getButtonLabel("JSReplaceLostFilesDlg.btnReplace"));
	
	private final JButton btnCancel = new JButton(i18n.getButtonLabel("cancel"));
	
	JSReplaceLostFilesDlg(Dialog owner) {
		super(owner, i18n.getLabel("JSReplaceLostFilesDlg.title"));
		showCloseButton(false);
		
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		
		Icon iconBrowse = CC.getViewConfig().getInstrumentsDbTreeView().getOpenIcon();
		btnBrowse = new JButton(iconBrowse);
		btnBrowse2 = new JButton(iconBrowse);
		
		btnBrowse.setMargin(new Insets(0, 0, 0, 0));
		btnBrowse2.setMargin(new Insets(0, 0, 0, 0));
		
		if(!CC.getCurrentServer().isLocal()) {
			btnBrowse.setEnabled(false);
			btnBrowse2.setEnabled(false);
		}
		
		JPanel p = new JPanel();
		p.setLayout(gridbag);
		
		c.fill = GridBagConstraints.NONE;
		
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(3, 3, 3, 3);
		gridbag.setConstraints(lFind, c);
		p.add(lFind); 
		
		c.gridx = 0;
		c.gridy = 1;
		gridbag.setConstraints(lReplace, c);
		p.add(lReplace); 
		
		c.gridx = 2;
		c.gridy = 0;
		gridbag.setConstraints(btnBrowse, c);
		p.add(btnBrowse); 
		
		c.gridx = 2;
		c.gridy = 1;
		gridbag.setConstraints(btnBrowse2, c);
		p.add(btnBrowse2); 
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(tfFind, c);
		p.add(tfFind);
		
		c.gridx = 1;
		c.gridy = 1;
		gridbag.setConstraints(tfReplace, c);
		p.add(tfReplace);
		
		JPanel p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		p2.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		
		btnPreview.setEnabled(false);
		btnReplace.setEnabled(false);
		p2.add(btnPreview);
		p2.add(Box.createRigidArea(new Dimension(6, 0)));
		p2.add(btnReplace);
		p2.add(Box.createGlue());
		p2.add(Box.createRigidArea(new Dimension(17, 0)));
		p2.add(btnCancel);
		
		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 2;
		gridbag.setConstraints(p2, c);
		p.add(p2);
		
		p.setPreferredSize(new Dimension(360, p.getPreferredSize().height));
		
		setMainPane(p);
		
		btnBrowse.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				String s = browse();
				if(s != null) tfFind.setText(s);
			}
		});
		
		btnBrowse2.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				String s = browse();
				if(s != null) tfReplace.setText(s);
			}
		});
		
		btnPreview.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { onPreview(); }
		});
		
		btnReplace.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { onReplace(); }
		});
		
		btnCancel.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { onCancel(); }
		});
		
		tfFind.getDocument().addDocumentListener(getHandler());
		tfReplace.getDocument().addDocumentListener(getHandler());
	}
	
	private void
	onPreview() {
		String f = tfFind.getText();
		String r = tfReplace.getText();
		StringBuffer sb = new StringBuffer();
		
		for(int i = 0; i < CC.getLostFilesModel().getLostFileCount(); i++) {
			String s = CC.getLostFilesModel().getLostFile(i);
			if(!s.startsWith(f)) continue;
			
			sb.append(s).append(" -> ");
			sb.append(r).append(s.substring(f.length())).append("\n");
		}
		
		JSPreviewLostFilesDlg dlg = new JSPreviewLostFilesDlg(this, sb.toString());
		dlg.setVisible(true);
	}
	
	private void
	onReplace() {
		String f = tfFind.getText();
		String r = tfReplace.getText();
		
		for(int i = 0; i < CC.getLostFilesModel().getLostFileCount(); i++) {
			String s = CC.getLostFilesModel().getLostFile(i);
			if(!s.startsWith(f)) continue;
			
			String s2 = r + s.substring(f.length());
			Task t = new SetInstrumentFilePath(s, s2);
			CC.getTaskQueue().add(t);
		}
		
		CC.getLostFilesModel().update();
		
		onCancel();
	}
	
	private void
	updateState() {
		boolean b = tfFind.getText().length() != 0 && tfReplace.getText().length() != 0;
		btnPreview.setEnabled(b);
		btnReplace.setEnabled(b);
	}
	
	private String
	browse() {
		File f = StdUtils.showOpenDirectoryChooser(this, null);
		if(f == null) return null;
		
		String path = f.getAbsolutePath();
		if(path.length() < 2) return path;
		
		char sep = java.io.File.separatorChar;
		if(path.charAt(path.length() - 1) != sep) path += sep;
		
		return path;
	}
	
	private final EventHandler eventHandler = new EventHandler();
	
	private EventHandler
	getHandler() { return eventHandler; }
	
	private class EventHandler implements DocumentListener {
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

class JSPreviewLostFilesDlg extends InformationDialog {
	JSPreviewLostFilesDlg(Dialog owner, String text) {
		super(owner, i18n.getLabel("JSPreviewLostFilesDlg.title"));
		JTextArea ta = new JTextArea();
		ta.setText(text);
		ta.setEditable(false);
		JScrollPane sp = new JScrollPane(ta);
		sp.setPreferredSize(new Dimension(600, 200));
		setMainPane(sp);
	}
}
