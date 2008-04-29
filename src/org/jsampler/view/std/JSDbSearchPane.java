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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.sf.juife.event.TaskEvent;
import net.sf.juife.event.TaskListener;

import org.jsampler.CC;
import org.jsampler.task.InstrumentsDb;

import org.linuxsampler.lscp.DbDirectoryInfo;
import org.linuxsampler.lscp.DbInstrumentInfo;
import org.linuxsampler.lscp.DbSearchQuery;

import static org.jsampler.view.std.StdI18n.i18n;


/**
 *
 * @author Grigor Iliev
 */
public abstract class JSDbSearchPane extends JPanel {
	private final JLabel lName = new JLabel(i18n.getLabel("JSDbSearchPane.lName"));
	private final JTextField tfName = new JTextField();
	
	private final JLabel lDescription =
		new JLabel(i18n.getLabel("JSDbSearchPane.lDescription"));
	private final JTextField tfDescription = new JTextField();
	
	private final JLabel lLookIn = new JLabel(i18n.getLabel("JSDbSearchPane.lLookIn"));
	private final JTextField tfLookIn = new JTextField();
	private JButton btnBrowse =
		new JButton(CC.getViewConfig().getInstrumentsDbTreeView().getOpenIcon());
	
	private TypeCriteriaPane typeCriteriaPane = new TypeCriteriaPane();
	private DateCriteriaPane dateCreatedPane = new DateCriteriaPane();
	private DateCriteriaPane dateModifiedPane = new DateCriteriaPane();
	private SizeCriteriaPane sizeCriteriaPane = new SizeCriteriaPane();
	//private FormatCriteriaPane formatCriteriaPane = new FormatCriteriaPane();
	private IsDrumCriteriaPane isDrumCriteriaPane = new IsDrumCriteriaPane();
	private MoreCriteriasPane moreCriteriasPane = new MoreCriteriasPane();
	private JButton btnFind = new JButton(i18n.getButtonLabel("JSDbSearchPane.btnFind"));
	
	private Frame owner;
	
	private final Vector<ChangeListener> listeners = new Vector<ChangeListener>();
	private DbDirectoryInfo[] directoryResults = null;
	private DbInstrumentInfo[] instrumentResults = null;
	
	private final JPanel mainPane;
		
	/**
	 * Creates a new instance of <code>JSDbSearchPane</code>.
	 */
	public
	JSDbSearchPane(final Frame owner) {
		this.owner = owner;
		
		setLayout(new BorderLayout());
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		
		JPanel p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));
		
		lName.setAlignmentX(LEFT_ALIGNMENT);
		p2.add(lName);
		p2.add(Box.createRigidArea(new Dimension(0, 3)));
		tfName.setAlignmentX(LEFT_ALIGNMENT);
		p2.add(tfName);
		
		p2.add(Box.createRigidArea(new Dimension(0, 6)));
		
		lDescription.setAlignmentX(LEFT_ALIGNMENT);
		p2.add(lDescription);
		p2.add(Box.createRigidArea(new Dimension(0, 3)));
		tfDescription.setAlignmentX(LEFT_ALIGNMENT);
		p2.add(tfDescription);
		
		p2.setBorder(BorderFactory.createEmptyBorder(3, 0, 6, 6));
		int h = p2.getPreferredSize().height;
		p2.setMaximumSize(new Dimension(Short.MAX_VALUE, h));
		p2.setOpaque(false);
		p2.setAlignmentX(LEFT_ALIGNMENT);
		p.add(p2);
		
		lLookIn.setAlignmentX(LEFT_ALIGNMENT);
		p.add(lLookIn);
		
		p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		tfLookIn.setText("/");
		p2.add(tfLookIn);
		p2.add(Box.createRigidArea(new Dimension(6, 0)));
		btnBrowse.setMargin(new Insets(0, 0, 0, 0));
		p2.add(btnBrowse);
		p2.setBorder(BorderFactory.createEmptyBorder(3, 0, 6, 6));
		h = p2.getPreferredSize().height;
		p2.setMaximumSize(new Dimension(Short.MAX_VALUE, h));
		p2.setOpaque(false);
		p2.setAlignmentX(LEFT_ALIGNMENT);
		p.add(p2);
		
		String s = i18n.getLabel("JSDbSearchPane.typeCriteria");
		p.add(createCriteriaPane(s, typeCriteriaPane));
		p.add(Box.createRigidArea(new Dimension(0, 6)));
		
		s = i18n.getLabel("JSDbSearchPane.modifiedCriteria");
		p.add(createCriteriaPane(s, dateModifiedPane));
		p.add(Box.createRigidArea(new Dimension(0, 6)));
		
		s = i18n.getLabel("JSDbSearchPane.createdCriteria");
		p.add(createCriteriaPane(s, dateCreatedPane));
		p.add(Box.createRigidArea(new Dimension(0, 6)));
		
		s = i18n.getLabel("JSDbSearchPane.sizeCriteria");
		p.add(createCriteriaPane(s, sizeCriteriaPane));
		p.add(Box.createRigidArea(new Dimension(0, 6)));
		
		/*s = i18n.getLabel("JSDbSearchPane.formatCriteria");
		p.add(new CriteriaPane(s, formatCriteriaPane));
		p.add(Box.createRigidArea(new Dimension(0, 6)));*/
		
		s = i18n.getLabel("JSDbSearchPane.isDrumCriteria");
		p.add(createCriteriaPane(s, isDrumCriteriaPane));
		p.add(Box.createRigidArea(new Dimension(0, 6)));
		
		s = i18n.getLabel("JSDbSearchPane.moreCriterias");
		p.add(createCriteriaPane(s, moreCriteriasPane));
		
		p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		p2.add(Box.createGlue());
		p2.add(btnFind);
		h = p2.getPreferredSize().height;
		p2.setMaximumSize(new Dimension(Short.MAX_VALUE, h));
		p2.setBorder(BorderFactory.createEmptyBorder(12, 0, 6, 6));
		p2.setOpaque(false);
		p2.setAlignmentX(LEFT_ALIGNMENT);
		p.add(p2);
		
		p2 = new JPanel();
		p2.setLayout(new BorderLayout());
		p2.setOpaque(false);
		p.add(p2);
		p.setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 3));
		
		mainPane = p;
		
		add(new JScrollPane(p));
		
		tfName.addActionListener(getHandler());
		tfName.getDocument().addDocumentListener(getHandler());
		
		tfDescription.addActionListener(getHandler());
		tfDescription.getDocument().addDocumentListener(getHandler());
		
		tfLookIn.getDocument().addDocumentListener(getHandler());
		
		btnFind.addActionListener(getHandler());
		btnFind.setEnabled(false);
		
		btnBrowse.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				JSDbDirectoryChooser dlg;
				dlg = new JSDbDirectoryChooser(owner);
				String s = tfLookIn.getText();
				if(s.length() > 0) dlg.setSelectedDirectory(s);
				dlg.setVisible(true);
				if(dlg.isCancelled()) return;
				tfLookIn.setText(dlg.getSelectedDirectory());
			}
		});
	}
	
	public void
	setBackgroundColor(java.awt.Color c) { mainPane.setBackground(c); }
	
	/**
	 * Registers the specified listener to be notified
	 * when the search results are changed.
	 */
	public void
	addChangeListener(ChangeListener l) { listeners.add(l); }
	
	/**  Removes the specified listener. */
	public void
	removeChangeListener(ChangeListener l) { listeners.remove(l); }
	
	private void
	fireSearchResultsChanged() {
		ChangeEvent e = new ChangeEvent(this);
		for(ChangeListener l : listeners) l.stateChanged(e);
	}
	
	public DbDirectoryInfo[]
	getDirectoryResults() { return directoryResults; }
	
	public DbInstrumentInfo[]
	getInstrumentResults() { return instrumentResults; }
	
	public void
	setSearchPath(String path) { tfLookIn.setText(path); }
	
	private void
	setSearchResults(DbDirectoryInfo[] dirs, DbInstrumentInfo[] instruments) {
		directoryResults = dirs;
		instrumentResults = instruments;
		fireSearchResultsChanged();
	}
	
	protected abstract JComponent createCriteriaPane(String title, JComponent mainPane);
	
	private void
	find() {
		if(!btnFind.isEnabled()) return;
		btnFind.setEnabled(false);
		DbSearchQuery query = new DbSearchQuery();
		query.name = tfName.getText();
		query.description = tfDescription.getText();
		query.createdAfter = dateCreatedPane.getDateAfter();
		query.createdBefore = dateCreatedPane.getDateBefore();
		query.modifiedAfter = dateModifiedPane.getDateAfter();
		query.modifiedBefore = dateModifiedPane.getDateBefore();
		query.minSize = sizeCriteriaPane.getMinSize();
		query.maxSize = sizeCriteriaPane.getMaxSize();
		query.product = moreCriteriasPane.getProduct();
		query.artists = moreCriteriasPane.getArtists();
		query.keywords = moreCriteriasPane.getKeywords();
		query.instrumentType = isDrumCriteriaPane.getInstrumentType();
		
		final InstrumentsDb.FindInstruments t;
		final InstrumentsDb.FindDirectories t2;
		
		if(typeCriteriaPane.getSearchInstruments()) {
			t = new InstrumentsDb.FindInstruments(tfLookIn.getText(), query);
		
			t.addTaskListener(new TaskListener() {
				public void
				taskPerformed(TaskEvent e) {
					if(!typeCriteriaPane.getSearchDirectories()) {
						updateState();
						if(t.doneWithErrors()) return;
						setSearchResults(null, t.getResult());
					}
				}
			});
		} else {
			t = null;
		}
		
		if(typeCriteriaPane.getSearchDirectories()) {
			t2 = new InstrumentsDb.FindDirectories(tfLookIn.getText(), query);
		
			t2.addTaskListener(new TaskListener() {
				public void
				taskPerformed(TaskEvent e) {
					updateState();
					if(t2.doneWithErrors()) return;
					
					if(t == null) {
						setSearchResults(t2.getResult(), null);
					} else {
						setSearchResults(t2.getResult(), t.getResult());
					}
				}
			});
		} else {
			t2 = null;
		}
		
		if(t != null) CC.getTaskQueue().add(t);
		if (t2 != null) CC.getTaskQueue().add(t2);
	}
	
	private void
	updateState() {
		boolean b = tfName.getText().length() != 0 || tfDescription.getText().length() != 0;
		b = b && tfLookIn.getText().length() != 0;
		btnFind.setEnabled(b);
	}
	
	private final Handler eventHandler = new Handler();
	
	private Handler
	getHandler() { return eventHandler; }
	
	private class Handler implements DocumentListener, ActionListener {
		// DocumentListener
		public void
		insertUpdate(DocumentEvent e) { updateState(); }
		
		public void
		removeUpdate(DocumentEvent e) { updateState(); }
		
		public void
		changedUpdate(DocumentEvent e) { updateState(); }
		
		public void
		actionPerformed(ActionEvent e) { find(); }
	}
	
	class TypeCriteriaPane extends JPanel implements ActionListener {
		private JRadioButton rbInstruments =
			new JRadioButton(i18n.getLabel("TypeCriteriaPane.rbInstruments"));
		private JRadioButton rbDirectories =
			new JRadioButton(i18n.getLabel("TypeCriteriaPane.rbDirectories"));
		private JRadioButton rbBoth =
			new JRadioButton(i18n.getLabel("TypeCriteriaPane.rbBoth"));
		
		TypeCriteriaPane() {
			setOpaque(false);
			
			ButtonGroup group = new ButtonGroup();
			group.add(rbInstruments);
			group.add(rbDirectories);
			group.add(rbBoth);
			rbInstruments.doClick(0);
			
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			rbInstruments.setAlignmentX(LEFT_ALIGNMENT);
			rbInstruments.setOpaque(false);
			add(rbInstruments);
			rbDirectories.setAlignmentX(LEFT_ALIGNMENT);
			rbDirectories.setOpaque(false);
			add(rbDirectories);
			rbBoth.setAlignmentX(LEFT_ALIGNMENT);
			rbBoth.setOpaque(false);
			add(rbBoth);
			
			setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 9));
			
			rbInstruments.addActionListener(this);
			rbDirectories.addActionListener(this);
			rbBoth.addActionListener(this);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			boolean b = !rbDirectories.isSelected();
			sizeCriteriaPane.setEnabled(b);
			isDrumCriteriaPane.setEnabled(b);
			moreCriteriasPane.setEnabled(b);
		}
		
		public boolean
		getSearchInstruments() {
			return rbInstruments.isSelected() || rbBoth.isSelected();
		}
		
		public boolean
		getSearchDirectories() {
			return rbDirectories.isSelected() || rbBoth.isSelected();
		}
	}
	
	class DateCriteriaPane extends JPanel implements ActionListener {
		private JRadioButton rbDontRemember =
			new JRadioButton(i18n.getLabel("DateCriteriaPane.rbDontRemember"));
		private JRadioButton rbSpecifyDates =
			new JRadioButton(i18n.getLabel("DateCriteriaPane.rbSpecifyDates"));
		private JSpinner spinnerBefore = new JSpinner(new SpinnerDateModel());
		private JSpinner spinnerAfter = new JSpinner(new SpinnerDateModel());
		
		DateCriteriaPane() {
			setOpaque(false);
			
			ButtonGroup group = new ButtonGroup();
			group.add(rbDontRemember);
			group.add(rbSpecifyDates);
			rbDontRemember.doClick(0);
			
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			rbDontRemember.setAlignmentX(LEFT_ALIGNMENT);
			rbDontRemember.setOpaque(false);
			add(rbDontRemember);
			rbSpecifyDates.setAlignmentX(LEFT_ALIGNMENT);
			rbSpecifyDates.setOpaque(false);
			add(rbSpecifyDates);
			
			JPanel p = new JPanel();
			GridBagLayout gridbag = new GridBagLayout();
			GridBagConstraints c = new GridBagConstraints();
			p.setLayout(gridbag);
			
			c.fill = GridBagConstraints.NONE;
			
			JLabel l = new JLabel(i18n.getLabel("DateCriteriaPane.from"));
			c.gridx = 0;
			c.gridy = 0;
			c.anchor = GridBagConstraints.WEST;
			c.insets = new Insets(3, 18, 3, 3);
			gridbag.setConstraints(l, c);
			p.add(l); 
			
			l = new JLabel(i18n.getLabel("DateCriteriaPane.to"));
			c.gridx = 0;
			c.gridy = 1;
			gridbag.setConstraints(l, c);
			p.add(l); 
			
			spinnerAfter.setEnabled(false);
			c.gridx = 1;
			c.gridy = 0;
			c.insets = new Insets(3, 3, 3, 3);
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1.0;
			gridbag.setConstraints(spinnerAfter, c);
			p.add(spinnerAfter); 
			
			spinnerBefore.setEnabled(false);
			c.gridx = 1;
			c.gridy = 1;
			gridbag.setConstraints(spinnerBefore, c);
			p.add(spinnerBefore); 
			
			p.setAlignmentX(LEFT_ALIGNMENT);
			p.setOpaque(false);
			add(p);
			
			setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 9));
			
			rbDontRemember.addActionListener(this);
			rbSpecifyDates.addActionListener(this);
			
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.MONTH, -1);
			spinnerAfter.setValue(calendar.getTime());
		}
		
		public void
		actionPerformed(ActionEvent e) {
			boolean b = rbSpecifyDates.isSelected();
			spinnerAfter.setEnabled(b);
			spinnerBefore.setEnabled(b);
		}
		
		public Date
		getDateAfter() {
			if(!rbSpecifyDates.isSelected()) return null;
			return (Date)spinnerAfter.getValue();
		}
		
		public Date
		getDateBefore() {
			if(!rbSpecifyDates.isSelected()) return null;
			return (Date)spinnerBefore.getValue();
		}
	}
	
	class SizeCriteriaPane extends JPanel implements ActionListener {
		private JRadioButton rbDontRemember =
			new JRadioButton(i18n.getLabel("SizeCriteriaPane.rbDontRemember"));
		private JRadioButton rbSpecifySize =
			new JRadioButton(i18n.getLabel("SizeCriteriaPane.rbSpecifySize"));
		private JSpinner spinnerFrom = new JSpinner(new SpinnerNumberModel());
		private JSpinner spinnerTo = new JSpinner(new SpinnerNumberModel());
		
		private final JComboBox cbEntity = new JComboBox();
		
		
		SizeCriteriaPane() {
			setOpaque(false);
			
			ButtonGroup group = new ButtonGroup();
			group.add(rbDontRemember);
			group.add(rbSpecifySize);
			rbDontRemember.doClick(0);
			
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			rbDontRemember.setAlignmentX(LEFT_ALIGNMENT);
			rbDontRemember.setOpaque(false);
			add(rbDontRemember);
			
			JPanel p = new JPanel();
			p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
			p.setAlignmentX(LEFT_ALIGNMENT);
			rbSpecifySize.setOpaque(false);
			p.add(rbSpecifySize);
			p.add(Box.createRigidArea(new Dimension(3, 0)));
			p.add(cbEntity);
			add(p);
			p.setOpaque(false);
			
			cbEntity.addItem("KB");
			cbEntity.addItem("MB");
			cbEntity.addItem("GB");
			cbEntity.setSelectedIndex(1);
			cbEntity.setMaximumSize(cbEntity.getPreferredSize());
			
			p = new JPanel();
			GridBagLayout gridbag = new GridBagLayout();
			GridBagConstraints c = new GridBagConstraints();
			p.setLayout(gridbag);
			
			c.fill = GridBagConstraints.NONE;
			
			JLabel l = new JLabel(i18n.getLabel("SizeCriteriaPane.from"));
			c.gridx = 0;
			c.gridy = 0;
			c.anchor = GridBagConstraints.WEST;
			c.insets = new Insets(3, 18, 3, 3);
			gridbag.setConstraints(l, c);
			p.add(l); 
			
			l = new JLabel(i18n.getLabel("SizeCriteriaPane.to"));
			c.gridx = 0;
			c.gridy = 1;
			gridbag.setConstraints(l, c);
			p.add(l); 
			
			spinnerFrom.setEnabled(false);
			c.gridx = 1;
			c.gridy = 0;
			c.insets = new Insets(3, 3, 3, 3);
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1.0;
			gridbag.setConstraints(spinnerFrom, c);
			p.add(spinnerFrom); 
			
			spinnerTo.setEnabled(false);
			c.gridx = 1;
			c.gridy = 1;
			gridbag.setConstraints(spinnerTo, c);
			p.add(spinnerTo); 
			
			p.setAlignmentX(LEFT_ALIGNMENT);
			p.setOpaque(false);
			add(p);
			
			setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 9));
			
			rbDontRemember.addActionListener(this);
			rbSpecifySize.addActionListener(this);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			boolean b = rbSpecifySize.isSelected();
			spinnerFrom.setEnabled(b);
			spinnerTo.setEnabled(b);
		}
		
		public long
		getMinSize() {
			if(!rbSpecifySize.isSelected()) return -1;
			return calcSize(Long.parseLong(spinnerFrom.getValue().toString()));
		}
		
		public long
		getMaxSize() {
			if(!rbSpecifySize.isSelected()) return -1;
			return calcSize(Long.parseLong(spinnerTo.getValue().toString()));
		}
		
		private long
		calcSize(long size) {
			switch(cbEntity.getSelectedIndex()) {
			case 0:
				size *= 1024;
				break;
			case 1:
				size *= (1024 * 1024);
				break;
			case 2:
				size *= (1024 * 1024 * 1024);
				break;
			}
			
			return size;
		}
		
		public void
		setEnabled(boolean b) {
			super.setEnabled(b);
			rbDontRemember.setEnabled(b);
			rbSpecifySize.setEnabled(b);
			spinnerFrom.setEnabled(b);
			spinnerTo.setEnabled(b);
			cbEntity.setEnabled(b);
			if(b) actionPerformed(null);
		}
	}
	
	class FormatCriteriaPane extends JPanel implements ItemListener {
		private JCheckBox checkAllFormats =
			new JCheckBox(i18n.getLabel("FormatCriteriaPane.checkAllFormats"));
		private JCheckBox checkGigFormat =
			new JCheckBox(i18n.getLabel("FormatCriteriaPane.checkGigFormat"));
		
		FormatCriteriaPane() {
			setOpaque(false);
			
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			checkAllFormats.setAlignmentX(LEFT_ALIGNMENT);
			checkAllFormats.setOpaque(false);
			add(checkAllFormats);
			
			JPanel p = new JPanel();
			p.setOpaque(false);
			p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
			p.setAlignmentX(LEFT_ALIGNMENT);
			p.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));
			
			p.add(checkGigFormat);
			add(p);
			
			setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 9));
			
			checkAllFormats.addItemListener(this);
			checkGigFormat.addItemListener(this);
		}
		
		public void
		itemStateChanged(ItemEvent e) {
			Object source = e.getItemSelectable();
			if(source == checkAllFormats) {
				
			} else if(source == checkGigFormat) {
				
			}
		}
	}
	
	class IsDrumCriteriaPane extends JPanel {
		private JRadioButton rbBoth =
			new JRadioButton(i18n.getLabel("IsDrumCriteriaPane.rbBoth"));
		private JRadioButton rbChromatic =
			new JRadioButton(i18n.getLabel("IsDrumCriteriaPane.rbChromatic"));
		private JRadioButton rbDrum =
			new JRadioButton(i18n.getLabel("IsDrumCriteriaPane.rbDrum"));
		
		
		IsDrumCriteriaPane() {
			setOpaque(false);
			
			ButtonGroup group = new ButtonGroup();
			group.add(rbBoth);
			group.add(rbChromatic);
			group.add(rbDrum);
			rbBoth.doClick(0);
			
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			
			rbBoth.setAlignmentX(LEFT_ALIGNMENT);
			rbBoth.setOpaque(false);
			add(rbBoth);
			
			rbChromatic.setAlignmentX(LEFT_ALIGNMENT);
			rbChromatic.setOpaque(false);
			add(rbChromatic);
			
			rbDrum.setAlignmentX(LEFT_ALIGNMENT);
			rbDrum.setOpaque(false);
			add(rbDrum);
			
			setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 9));
		}
		
		public DbSearchQuery.InstrumentType
		getInstrumentType() {
			if(rbChromatic.isSelected()) {
				return DbSearchQuery.InstrumentType.CHROMATIC;
			}
			
			if(rbDrum.isSelected()) {
				return DbSearchQuery.InstrumentType.DRUM;
			}
			
			return DbSearchQuery.InstrumentType.BOTH;
		}
		
		public void
		setEnabled(boolean b) {
			super.setEnabled(b);
			rbBoth.setEnabled(b);
			rbChromatic.setEnabled(b);
			rbDrum.setEnabled(b);
		}
	}
	
	class MoreCriteriasPane extends JPanel {
		private final JLabel lProduct =
			new JLabel(i18n.getLabel("MoreCriteriasPane.lProduct"));
		private final JLabel lArtists =
			new JLabel(i18n.getLabel("MoreCriteriasPane.lArtists"));
		private final JLabel lKeywords =
			new JLabel(i18n.getLabel("MoreCriteriasPane.lKeywords"));
		
		private final JTextField tfProduct = new JTextField();
		private final JTextField tfArtists = new JTextField();
		private final JTextField tfKeywords = new JTextField();
		
		MoreCriteriasPane() {
			setOpaque(false);
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			
			lProduct.setAlignmentX(LEFT_ALIGNMENT);
			add(lProduct);
			add(Box.createRigidArea(new Dimension(0, 3)));
			tfProduct.setAlignmentX(LEFT_ALIGNMENT);
			add(tfProduct);
			
			add(Box.createRigidArea(new Dimension(0, 3)));
			
			lArtists.setAlignmentX(LEFT_ALIGNMENT);
			add(lArtists);
			add(Box.createRigidArea(new Dimension(0, 3)));
			tfArtists.setAlignmentX(LEFT_ALIGNMENT);
			add(tfArtists);
			
			add(Box.createRigidArea(new Dimension(0, 3)));
			
			lKeywords.setAlignmentX(LEFT_ALIGNMENT);
			add(lKeywords);
			add(Box.createRigidArea(new Dimension(0, 3)));
			tfKeywords.setAlignmentX(LEFT_ALIGNMENT);
			add(tfKeywords);
			
			setBorder(BorderFactory.createEmptyBorder(0, 9, 0, 9));
		}
		
		public String
		getProduct() { return tfProduct.getText(); }
		
		public String
		getArtists() { return tfArtists.getText(); }
		
		public String
		getKeywords() { return tfKeywords.getText(); }
		
		public void
		setEnabled(boolean b) {
			super.setEnabled(b);
			tfProduct.setEnabled(b);
			tfArtists.setEnabled(b);
			tfKeywords.setEnabled(b);
		}
	}
}
