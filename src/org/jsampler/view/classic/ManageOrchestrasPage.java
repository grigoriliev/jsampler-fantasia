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

import java.awt.datatransfer.DataFlavor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sf.juife.JuifeUtils;
import net.sf.juife.LinkButton;
import net.sf.juife.NavigationPage;

import org.jsampler.CC;
import org.jsampler.DOMUtils;
import org.jsampler.DefaultOrchestraModel;
import org.jsampler.HF;
import org.jsampler.Instrument;
import org.jsampler.OrchestraListModel;
import org.jsampler.OrchestraModel;

import org.jsampler.view.InstrumentTable;
import org.jsampler.view.InstrumentTableModel;
import org.jsampler.view.OrchestraTable;
import org.jsampler.view.OrchestraTableModel;

import static org.jsampler.view.classic.ClassicI18n.i18n;


/**
 *
 * @author Grigor Iliev
 */
public class ManageOrchestrasPage extends NavigationPage {
	private final JComponent manageOrchestrasPane;
	
	// Manage Orchestra
	private final ToolbarButton btnAddOrchestra = new ToolbarButton(new AddOrchestra());
	private final ToolbarButton btnEditOrchestra = new ToolbarButton(new EditOrchestra());
	private final ToolbarButton btnDeleteOrchestra = new ToolbarButton(new DeleteOrchestra());
	private final ToolbarButton btnOrchestraUp = new ToolbarButton(new OrchestraUp());
	private final ToolbarButton btnOrchestraDown = new ToolbarButton(new OrchestraDown());
	
	private OrchestraTable orchestraTable;
	
	private final ToolbarButton btnAddInstrument = new ToolbarButton(new AddInstrument());
	private final ToolbarButton btnEditInstrument = new ToolbarButton(new EditInstrument());
	private final ToolbarButton btnDeleteInstrument = new ToolbarButton(new DeleteInstrument());
	private final ToolbarButton btnInstrumentUp = new ToolbarButton(new InstrumentUp());
	private final ToolbarButton btnInstrumentDown = new ToolbarButton(new InstrumentDown());
	
	private LinkButton lnkOrchestras =
		new LinkButton(i18n.getButtonLabel("ManageOrchestrasPage.lnkOrchestras"));
	
	private InstrumentTable instrumentTable;
	private OrchestraModel orchestraModel;
	
	
	/** Creates a new instance of <code>ManageOrchestrasPage</code>. */
	public ManageOrchestrasPage() {
		setTitle(i18n.getLabel("ManageOrchestrasPage.title"));
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		manageOrchestrasPane = createManageOrchestrasPane();
		add(manageOrchestrasPane);
		
		installListeneres();
	}
	
	private JComponent
	createManageOrchestrasPane() {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		//p.add(Box.createRigidArea(new Dimension(0, 5)));
		
		JToolBar tb = new JToolBar();
		tb.add(btnAddOrchestra);
		tb.add(btnEditOrchestra);
		tb.add(btnDeleteOrchestra);
		
		tb.addSeparator();
		
		tb.add(btnOrchestraUp);
		tb.add(btnOrchestraDown);
				
		tb.setMaximumSize(new Dimension(Short.MAX_VALUE, tb.getPreferredSize().height));
		tb.setFloatable(false);
		tb.setAlignmentX(LEFT_ALIGNMENT);
		
		p.add(tb);
		
		orchestraTable = new OrchestraTable(new OrchestraTableModel(CC.getOrchestras()));
		JScrollPane sp = new JScrollPane(orchestraTable);
		
		sp.getViewport().addMouseListener(new MouseAdapter() {
			public void
			mouseClicked(MouseEvent e) { orchestraTable.clearSelection(); }
		});
		
		Dimension d;
		d = new Dimension(sp.getMinimumSize().width, sp.getPreferredSize().height);
		sp.setPreferredSize(d);
			
		sp.setAlignmentX(LEFT_ALIGNMENT);
		p.add(sp);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setContinuousLayout(true);
		splitPane.setTopComponent(p);
		
		p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		//p.add(Box.createRigidArea(new Dimension(0, 5)));
		
		tb = new JToolBar();
		btnAddInstrument.setEnabled(false);
		tb.add(btnAddInstrument);
		tb.add(btnEditInstrument);
		tb.add(btnDeleteInstrument);
		
		tb.addSeparator();
		
		tb.add(btnInstrumentUp);
		tb.add(btnInstrumentDown);
		
		tb.setMaximumSize(new Dimension(Short.MAX_VALUE, tb.getPreferredSize().height));
		tb.setFloatable(false);
		tb.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		
		p.add(tb);
		
		orchestraModel = new DefaultOrchestraModel();
		instrumentTable = new InstrumentTable(new InstrumentTableModel(orchestraModel));
		sp = new DnDScrollPane(instrumentTable);
		sp.setAlignmentX(LEFT_ALIGNMENT);
		p.add(sp);
		
		//p.add(lnkOrchestras);
		//p.add(Box.createRigidArea(new Dimension(0, 5)));
		
		splitPane.setBottomComponent(p);
		splitPane.setDividerSize(3);
		splitPane.setDividerLocation(180);
		
		return splitPane;
	}
	
	private void
	installListeneres() {
		lnkOrchestras.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				LeftPane.getLeftPane().showOrchestrasPage();
			}
		});
		
		OrchestraSelectionHandler l = new OrchestraSelectionHandler();
		orchestraTable.getSelectionModel().addListSelectionListener(l);
		
		orchestraTable.addMouseListener(new MouseAdapter() {
			public void
			mouseClicked(MouseEvent e) {
				if(e.getClickCount() < 2) return;
				
				if(orchestraTable.getSelectedOrchestra() == null) return;
				btnEditOrchestra.doClick(0);
			}
		});
		
		InstrumentSelectionHandler l2 = new InstrumentSelectionHandler();
		instrumentTable.getSelectionModel().addListSelectionListener(l2);
		
		instrumentTable.addMouseListener(new MouseAdapter() {
			public void
			mouseClicked(MouseEvent e) {
				if(e.getClickCount() < 2) return;
				
				if(instrumentTable.getSelectedInstrument() == null) return;
				btnEditInstrument.doClick(0);
			}
		});
	}
	
	public class DnDScrollPane extends JScrollPane {
		private InstrumentTable instrumentTable;
		
		private
		DnDScrollPane(InstrumentTable table) {
			instrumentTable = table;
			setViewport(new DnDViewPort());
			setViewportView(instrumentTable);
			
			Dimension d;
			d = new Dimension(getMinimumSize().width, getPreferredSize().height);
			setPreferredSize(d);
		}
		
		public class DnDViewPort extends javax.swing.JViewport {
			private
			DnDViewPort() {
				setTransferHandler(new TransferHandler("instrument") {
					public boolean
					canImport(JComponent comp, DataFlavor[] flavors) {
						if(instrumentTable.isPerformingDnD()) return false;
						if(!btnAddInstrument.isEnabled()) return false;
				
						return super.canImport(comp, flavors);
					}	
				});
				
				addMouseListener(new MouseAdapter() {
					public void
					mouseClicked(MouseEvent e) {
						instrumentTable.clearSelection();
					}
				});
			}
			
			public String
			getInstrument() { return null; }
			
			public void
			setInstrument(String instr) {
				instrumentTable.setSelectedInstrument(null);
				instrumentTable.setInstrument(instr);
			}
		}
	}
	
	private class OrchestraSelectionHandler implements ListSelectionListener {
		public void
		valueChanged(ListSelectionEvent e) {
			if(e.getValueIsAdjusting()) return;
			
			if(orchestraTable.getSelectedOrchestra() == null) {
				btnEditOrchestra.setEnabled(false);
				btnDeleteOrchestra.setEnabled(false);
				btnAddInstrument.setEnabled(false);
				btnOrchestraUp.setEnabled(false);
				btnOrchestraDown.setEnabled(false);
				instrumentTable.setModel(new InstrumentTableModel());
				return;
			}
			
			btnEditOrchestra.setEnabled(true);
			btnDeleteOrchestra.setEnabled(true);
			btnAddInstrument.setEnabled(true);
			
			orchestraModel = orchestraTable.getSelectedOrchestra();
			int idx = orchestraTable.getSelectedRow();
			btnOrchestraUp.setEnabled(idx != 0);
			btnOrchestraDown.setEnabled(idx != orchestraTable.getRowCount() - 1);
			
			instrumentTable.getModel().setOrchestraModel(orchestraModel);
		}
	}
	
	private class InstrumentSelectionHandler implements ListSelectionListener {
		public void
		valueChanged(ListSelectionEvent e) {
			if(e.getValueIsAdjusting()) return;
			
			if(instrumentTable.getSelectedInstrument() == null) {
				btnEditInstrument.setEnabled(false);
				btnDeleteInstrument.setEnabled(false);
				btnInstrumentUp.setEnabled(false);
				btnInstrumentDown.setEnabled(false);
				return;
			}
			
			btnEditInstrument.setEnabled(true);
			btnDeleteInstrument.setEnabled(true);
			
			int idx = instrumentTable.getSelectedRow();
			btnInstrumentUp.setEnabled(idx != 0);
			btnInstrumentDown.setEnabled(idx != instrumentTable.getRowCount() - 1);
		}
	}
	
	private class AddOrchestra extends AbstractAction {
		AddOrchestra() {
			super("");
			
			String s = i18n.getLabel("ManageOrchestrasPage.ttAddOrchestra");
			putValue(SHORT_DESCRIPTION, s);
			putValue(Action.SMALL_ICON, Res.iconNew16);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			AddOrchestraDlg dlg = new AddOrchestraDlg();
			dlg.setVisible(true);
			
			if(dlg.isCancelled()) return;
			
			OrchestraModel om = orchestraTable.getSelectedOrchestra();
			int idx = CC.getOrchestras().getOrchestraIndex(om);
			if(idx < 0) CC.getOrchestras().addOrchestra(dlg.getOrchestra());
			else CC.getOrchestras().insertOrchestra(dlg.getOrchestra(), idx);
			
			orchestraTable.setSelectedOrchestra(dlg.getOrchestra());
		}
	}
	
	private class EditOrchestra extends AbstractAction {
		EditOrchestra() {
			super("");
			
			String s = i18n.getLabel("ManageOrchestrasPage.ttEditOrchestra");
			putValue(SHORT_DESCRIPTION, s);
			putValue(Action.SMALL_ICON, Res.iconEdit16);
			
			setEnabled(false);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			OrchestraModel m = orchestraTable.getSelectedOrchestra();
			AddOrchestraDlg dlg = new AddOrchestraDlg(m);
			dlg.setVisible(true);
		}
	}
	
	private class DeleteOrchestra extends AbstractAction {
		DeleteOrchestra() {
			super("");
			
			String s = i18n.getLabel("ManageOrchestrasPage.ttDeleteOrchestra");
			putValue(SHORT_DESCRIPTION, s);
			putValue(Action.SMALL_ICON, Res.iconDelete16);
			
			setEnabled(false);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			OrchestraModel om = orchestraTable.getSelectedOrchestra();
			if(om == null) return;
			if(om.getInstrumentCount() > 0) {
				String s = i18n.getMessage("ManageOrchestrasPage.removeOrchestra?");
				if(!HF.showYesNoDialog(CC.getMainFrame(), s)) return;
			}
			
			CC.getOrchestras().removeOrchestra(om);
		}
	}
	
	private class OrchestraUp extends AbstractAction {
		OrchestraUp() {
			super("");
			
			String s = i18n.getLabel("ManageOrchestrasPage.OrchestraUp");
			putValue(SHORT_DESCRIPTION, s);
			putValue(Action.SMALL_ICON, Res.iconUp16);
			
			setEnabled(false);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			OrchestraModel om = orchestraTable.getSelectedOrchestra();
			CC.getOrchestras().moveOrchestraUp(om);
			orchestraTable.setSelectedOrchestra(om);
		}
	}
	
	private class OrchestraDown extends AbstractAction {
		OrchestraDown() {
			super("");
			
			String s = i18n.getLabel("ManageOrchestrasPage.OrchestraDown");
			putValue(SHORT_DESCRIPTION, s);
			putValue(Action.SMALL_ICON, Res.iconDown16);
			
			setEnabled(false);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			OrchestraModel om = orchestraTable.getSelectedOrchestra();
			CC.getOrchestras().moveOrchestraDown(om);
			orchestraTable.setSelectedOrchestra(om);
		}
	}
	
	private class AddInstrument extends AbstractAction {
		AddInstrument() {
			super("");
			
			String s = i18n.getLabel("ManageOrchestrasPage.ttAddInstrument");
			putValue(SHORT_DESCRIPTION, s);
			
			try {
				putValue(Action.SMALL_ICON, Res.iconNew16);
			} catch(Exception x) {
				CC.getLogger().log(Level.INFO, HF.getErrorMessage(x), x);
			}
		}
		
		public void
		actionPerformed(ActionEvent e) {
			AddInstrumentDlg dlg = new AddInstrumentDlg();
			dlg.setVisible(true);
			
			if(dlg.isCancelled()) return;
			
			orchestraModel.addInstrument(dlg.getInstrument());
		}
	}
	
	private class EditInstrument extends AbstractAction {
		EditInstrument() {
			super("");
			
			String s = i18n.getLabel("ManageOrchestrasPage.ttEditInstrument");
			putValue(SHORT_DESCRIPTION, s);
			putValue(Action.SMALL_ICON, Res.iconEdit16);
			
			setEnabled(false);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			Instrument instr = instrumentTable.getSelectedInstrument();
			AddInstrumentDlg dlg = new AddInstrumentDlg(instr);
			dlg.setVisible(true);
		}
	}
	
	private class DeleteInstrument extends AbstractAction {
		DeleteInstrument() {
			super("");
			
			String s = i18n.getLabel("ManageOrchestrasPage.ttDeleteInstrument");
			putValue(SHORT_DESCRIPTION, s);
			putValue(Action.SMALL_ICON, Res.iconDelete16);
			
			setEnabled(false);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			Instrument instr = instrumentTable.getSelectedInstrument();
			if(instr == null) return;
			orchestraModel.removeInstrument(instr);
		}
	}
	
	private class InstrumentUp extends AbstractAction {
		InstrumentUp() {
			super("");
			
			String s = i18n.getLabel("ManageOrchestrasPage.InstrumentUp");
			putValue(SHORT_DESCRIPTION, s);
			putValue(Action.SMALL_ICON, Res.iconUp16);
			
			setEnabled(false);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			Instrument instr = instrumentTable.getSelectedInstrument();
			instrumentTable.getModel().getOrchestraModel().moveInstrumentUp(instr);
			instrumentTable.setSelectedInstrument(instr);
		}
	}
	
	private class InstrumentDown extends AbstractAction {
		InstrumentDown() {
			super("");
			
			String s = i18n.getLabel("ManageOrchestrasPage.InstrumentDown");
			putValue(SHORT_DESCRIPTION, s);
			putValue(Action.SMALL_ICON, Res.iconDown16);
			
			setEnabled(false);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			Instrument instr = instrumentTable.getSelectedInstrument();
			instrumentTable.getModel().getOrchestraModel().moveInstrumentDown(instr);
			instrumentTable.setSelectedInstrument(instr);
		}
	}
}
