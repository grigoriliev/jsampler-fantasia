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

package org.jsampler.view.std;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.sf.juife.OkCancelDialog;
import net.sf.juife.Task;

import net.sf.juife.event.TaskEvent;
import net.sf.juife.event.TaskListener;

import org.jsampler.CC;
import org.jsampler.JSPrefs;
import org.jsampler.task.InstrumentsDb;

import static org.jsampler.view.std.StdI18n.i18n;

/**
 *
 * @author Grigor Iliev
 */
public class JSAddDbInstrumentsFromFileDlg extends OkCancelDialog {
	private final JRadioButton rbAllInstruments =
		new JRadioButton(i18n.getLabel("JSAddDbInstrumentsFromFileDlg.rbAllInstruments"));
	private final JRadioButton rbIndex =
		new JRadioButton(i18n.getLabel("JSAddDbInstrumentsFromFileDlg.rbIndex"));
	
	private final JTextField tfSource = new JTextField();
	private final JSpinner spinnerIndex = new JSpinner(new SpinnerNumberModel(0, 0, 500, 1));
	private JButton btnBrowse;
	private final JTextField tfDest = new JTextField();
	private JButton btnBrowseDb;
	
	/**
	 * Creates a new instance of <code>JSAddDbInstrumentsFromFileDlg</code>
	 */
	public
	JSAddDbInstrumentsFromFileDlg(Frame owner, String dbDir, Icon iconBrowse) {
		super(owner, i18n.getLabel("JSAddDbInstrumentsFromFileDlg.title"));
		
		initAddDbInstrumentsFromFileDlg(dbDir, iconBrowse);
	}
	
	/**
	 * Creates a new instance of <code>JSAddDbInstrumentsFromFileDlg</code>
	 */
	public
	JSAddDbInstrumentsFromFileDlg(Dialog owner, String dbDir, Icon iconBrowse) {
		super(owner, i18n.getLabel("JSAddDbInstrumentsFromFileDlg.title"));
		
		initAddDbInstrumentsFromFileDlg(dbDir, iconBrowse);
	}
	
	private void
	initAddDbInstrumentsFromFileDlg(String dbDir, Icon iconBrowse) {
		btnBrowse = new JButton(iconBrowse);
		btnBrowseDb = new JButton(iconBrowse);
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		
		JPanel p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		p2.add(tfSource);
		p2.add(Box.createRigidArea(new Dimension(6, 0)));
		String s = i18n.getButtonLabel("browse");
		btnBrowse.setMargin(new Insets(0, 0, 0, 0));
		btnBrowse.setToolTipText(s);
		p2.add(btnBrowse);
		p2.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
		p2.setMaximumSize(new Dimension(Short.MAX_VALUE, p2.getPreferredSize().height));
		p2.setAlignmentX(LEFT_ALIGNMENT);
		
		p.add(p2);
		
		p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		p2.add(rbAllInstruments);
		p2.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
		p2.setAlignmentX(LEFT_ALIGNMENT);
		p.add(p2);
		
		p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		p2.add(rbIndex);
		p2.add(Box.createRigidArea(new Dimension(6, 0)));
		p2.add(spinnerIndex);
		p2.setBorder(BorderFactory.createEmptyBorder(0, 3, 3, 3));
		p2.setMaximumSize(new Dimension(Short.MAX_VALUE, p2.getPreferredSize().height));
		p2.setAlignmentX(LEFT_ALIGNMENT);
		
		p.add(p2);
		
		s = i18n.getLabel("JSAddDbInstrumentsFromFileDlg.source");
		p.setBorder(BorderFactory.createTitledBorder(s));
		
		JPanel mainPane = new JPanel();
		mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
		
		mainPane.add(p);
		mainPane.add(Box.createRigidArea(new Dimension(0, 6)));
		
		p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		
		p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		p2.add(tfDest);
		p2.add(Box.createRigidArea(new Dimension(6, 0)));
		s = i18n.getButtonLabel("browse");
		btnBrowseDb.setMargin(new Insets(0, 0, 0, 0));
		btnBrowseDb.setToolTipText(s);
		p2.add(btnBrowseDb);
		p2.setBorder(BorderFactory.createEmptyBorder(0, 3, 3, 3));
		p2.setMaximumSize(new Dimension(Short.MAX_VALUE, p2.getPreferredSize().height));
		p2.setAlignmentX(LEFT_ALIGNMENT);
		
		p.add(p2);
		
		s = i18n.getLabel("JSAddDbInstrumentsFromFileDlg.dest");
		p.setBorder(BorderFactory.createTitledBorder(s));
		
		mainPane.add(p);
		Dimension d = mainPane.getPreferredSize();
		mainPane.setPreferredSize(new Dimension(d.width > 300 ? d.width : 300, d.height));
				
		setMainPane(mainPane);
		
		setMinimumSize(this.getPreferredSize());
		setResizable(true);
		
		if(dbDir != null) tfDest.setText(dbDir);
		
		ButtonGroup group = new ButtonGroup();
		group.add(rbAllInstruments);
		group.add(rbIndex);
		rbAllInstruments.doClick(0);
		
		btnOk.setEnabled(false);
		spinnerIndex.setEnabled(false);
		
		tfSource.getDocument().addDocumentListener(getHandler());
		tfDest.getDocument().addDocumentListener(getHandler());
		
		btnBrowse.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { onBrowse(); }
		});
		
		rbAllInstruments.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				spinnerIndex.setEnabled(rbIndex.isSelected());
			}
		});
		
		rbIndex.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				spinnerIndex.setEnabled(rbIndex.isSelected());
			}
		});
		
		btnBrowseDb.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				JSDbDirectoryChooser dlg;
				dlg = new JSDbDirectoryChooser(JSAddDbInstrumentsFromFileDlg.this);
				String s = tfDest.getText();
				if(s.length() > 0) dlg.setSelectedDirectory(s);
				dlg.setVisible(true);
				if(dlg.isCancelled()) return;
				tfDest.setText(dlg.getSelectedDirectory());
			}
		});
	}
	
	protected JSPrefs
	preferences() { return CC.getViewConfig().preferences(); }
	
	private void
	onBrowse() {
		String path = preferences().getStringProperty("lastInstrumentLocation");
		JFileChooser fc = new JFileChooser(path);
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int result = fc.showOpenDialog(this);
		if(result != JFileChooser.APPROVE_OPTION) return;
		
		tfSource.setText(fc.getSelectedFile().getPath());
		path = fc.getCurrentDirectory().getAbsolutePath();
		preferences().setStringProperty("lastInstrumentLocation", path);
	}
	
	private void
	updateState() {
		boolean b = tfSource.getText().length() != 0 && tfDest.getText().length() != 0;
		btnOk.setEnabled(b);
	}
	
	protected void
	onOk() {
		if(!btnOk.isEnabled()) return;
		
		btnOk.setEnabled(false);
		String dbDir = tfDest.getText();
		String filePath = tfSource.getText();
		int idx = -1;
		if(rbIndex.isSelected()) idx = Integer.parseInt(spinnerIndex.getValue().toString());
		
		final Task<Integer> t = new InstrumentsDb.AddInstrumentsFromFile(dbDir, filePath, idx);
		t.addTaskListener(new TaskListener() {
			public void
			taskPerformed(TaskEvent e) {
				updateState();
				if(t.doneWithErrors()) return;
				showProgress(t.getResult());
			}
		});
		
		CC.getTaskQueue().add(t);
	}
	
	private void
	showProgress(int jobId) {
		final JSAddDbInstrumentsProgressDlg dlg;
		dlg = new JSAddDbInstrumentsProgressDlg(this, jobId);
		SwingUtilities.invokeLater(new Runnable() {
			public void
			run() { dlg.setVisible(true); }
		});
	}
	
	protected void
	onCancel() { setVisible(false); }
	
	private final Handler eventHandler = new Handler();
	
	private Handler
	getHandler() { return eventHandler; }
	
	private class Handler implements DocumentListener {
		// DocumentListener
		public void
		insertUpdate(DocumentEvent e) { updateState(); }
		
		public void
		removeUpdate(DocumentEvent e) { updateState(); }
		
		public void
		changedUpdate(DocumentEvent e) { updateState(); }
	}
}
