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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jsampler.CC;
import org.jsampler.HF;
import org.jsampler.OrchestraModel;

import org.jsampler.view.OrchestraTable;
import org.jsampler.view.OrchestraTableModel;

import static org.jsampler.view.std.StdI18n.i18n;

/**
 *
 * @author Grigor Iliev
 */
public class JSManageOrchestrasPane extends JPanel {
	protected final OrchestraTable orchestraTable;
	
	protected final Action actionAddOrchestra = new AddOrchestraAction();
	protected final Action actionEditOrchestra = new EditOrchestraAction();
	protected final Action actionDeleteOrchestra = new DeleteOrchestraAction();
	protected final Action actionOrchestraUp = new OrchestraUpAction();
	protected final Action actionOrchestraDown = new OrchestraDownAction();
	
	/** Creates a new instance of <code>JSManageOrchestrasPane</code> */
	public
	JSManageOrchestrasPane() {
		setLayout(new BorderLayout());
		orchestraTable = new OrchestraTable(new OrchestraTableModel(CC.getOrchestras()));
		JScrollPane sp = new JScrollPane(orchestraTable);
		add(sp);
		
		installListeneres();
	}
	
	private void
	installListeneres() {
		OrchestraSelectionHandler l = new OrchestraSelectionHandler();
		orchestraTable.getSelectionModel().addListSelectionListener(l);
		
		orchestraTable.addMouseListener(new MouseAdapter() {
			public void
			mouseClicked(MouseEvent e) {
				if(e.getClickCount() < 2) return;
				
				if(orchestraTable.getSelectedOrchestra() == null) return;
				editOrchestra(orchestraTable.getSelectedOrchestra());
			}
		});
	}
	
	/**
	 * Invoked when the user initiates the creation of new orchestra.
	 * @return The model of the orchestra to add
	 * or <code>null</code> if the user cancelled the task.
	 */
	public OrchestraModel
	createOrchestra() {
		JSAddOrEditOrchestraDlg dlg = new JSAddOrEditOrchestraDlg();
		dlg.setVisible(true);
		
		if(dlg.isCancelled()) return null;
		
		return dlg.getOrchestra();
	}
	
	public void
	editOrchestra(OrchestraModel model) {
		JSAddOrEditOrchestraDlg dlg = new JSAddOrEditOrchestraDlg(model);
		dlg.setVisible(true);
	}
	
	private class OrchestraSelectionHandler implements ListSelectionListener {
		public void
		valueChanged(ListSelectionEvent e) {
			if(e.getValueIsAdjusting()) return;
			
			if(orchestraTable.getSelectedOrchestra() == null) {
				actionEditOrchestra.setEnabled(false);
				actionDeleteOrchestra.setEnabled(false);
				actionOrchestraUp.setEnabled(false);
				actionOrchestraDown.setEnabled(false);
				return;
			}
			
			actionEditOrchestra.setEnabled(true);
			actionDeleteOrchestra.setEnabled(true);
			
			OrchestraModel orchestraModel = orchestraTable.getSelectedOrchestra();
			int idx = orchestraTable.getSelectedRow();
			actionOrchestraUp.setEnabled(idx != 0);
			actionOrchestraDown.setEnabled(idx != orchestraTable.getRowCount() - 1);
		}
	}
	
	private class AddOrchestraAction extends AbstractAction {
		AddOrchestraAction() {
			super("");
			
			String s = i18n.getLabel("JSManageOrchestrasPane.ttAddOrchestra");
			putValue(SHORT_DESCRIPTION, s);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			OrchestraModel newOrch = createOrchestra();
			if(newOrch == null) return;
			
			OrchestraModel om = orchestraTable.getSelectedOrchestra();
			int idx = CC.getOrchestras().getOrchestraIndex(om);
			if(idx < 0) CC.getOrchestras().addOrchestra(newOrch);
			else CC.getOrchestras().insertOrchestra(newOrch, idx);
			
			orchestraTable.setSelectedOrchestra(newOrch);
		}
	}
	
	private class EditOrchestraAction extends AbstractAction {
		EditOrchestraAction() {
			super("");
			
			String s = i18n.getLabel("JSManageOrchestrasPane.ttEditOrchestra");
			putValue(SHORT_DESCRIPTION, s);
			
			setEnabled(false);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			editOrchestra(orchestraTable.getSelectedOrchestra());
		}
	}
	
	private class DeleteOrchestraAction extends AbstractAction {
		DeleteOrchestraAction() {
			super("");
			
			String s = i18n.getLabel("JSManageOrchestrasPane.ttDeleteOrchestra");
			putValue(SHORT_DESCRIPTION, s);
			
			setEnabled(false);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			OrchestraModel om = orchestraTable.getSelectedOrchestra();
			if(om == null) return;
			if(om.getInstrumentCount() > 0) {
				String s;
				s = i18n.getMessage("JSManageOrchestrasPane.removeOrchestra?");
				if(!HF.showYesNoDialog(CC.getMainFrame(), s)) return;
			}
			
			CC.getOrchestras().removeOrchestra(om);
		}
	}
	
	private class OrchestraUpAction extends AbstractAction {
		OrchestraUpAction() {
			super("");
			
			String s = i18n.getLabel("JSManageOrchestrasPane.ttOrchestraUp");
			putValue(SHORT_DESCRIPTION, s);
			
			setEnabled(false);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			OrchestraModel om = orchestraTable.getSelectedOrchestra();
			CC.getOrchestras().moveOrchestraUp(om);
			orchestraTable.setSelectedOrchestra(om);
		}
	}
	
	private class OrchestraDownAction extends AbstractAction {
		OrchestraDownAction() {
			super("");
			
			String s = i18n.getLabel("JSManageOrchestrasPane.ttOrchestraDown");
			putValue(SHORT_DESCRIPTION, s);
			
			setEnabled(false);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			OrchestraModel om = orchestraTable.getSelectedOrchestra();
			CC.getOrchestras().moveOrchestraDown(om);
			orchestraTable.setSelectedOrchestra(om);
		}
	}
}
