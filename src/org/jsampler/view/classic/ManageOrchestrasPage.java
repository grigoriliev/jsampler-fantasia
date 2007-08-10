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

import org.jsampler.view.std.JSManageOrchestrasPane;
import org.jsampler.view.std.JSOrchestraPane;

import static org.jsampler.view.classic.ClassicI18n.i18n;
import org.jsampler.view.std.JSAddOrEditOrchestraDlg;


/**
 *
 * @author Grigor Iliev
 */
public class ManageOrchestrasPage extends NavigationPage {
	private final JComponent manageOrchestrasPane;
	OrchestraTable orchestraTable;
	
	private LinkButton lnkOrchestras =
		new LinkButton(i18n.getButtonLabel("ManageOrchestrasPage.lnkOrchestras"));
	
	private OrchestraPane orchestraPane = new OrchestraPane();
	
	
	/** Creates a new instance of <code>ManageOrchestrasPage</code>. */
	public ManageOrchestrasPage() {
		setTitle(i18n.getLabel("ManageOrchestrasPage.title"));
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		manageOrchestrasPane = createManageOrchestrasPane();
		add(manageOrchestrasPane);
		
		installListeneres();
	}
	
	class ManageOrchestrasPane extends JSManageOrchestrasPane {
		ManageOrchestrasPane() {
			actionAddOrchestra.putValue(Action.SMALL_ICON, Res.iconNew16);
			actionEditOrchestra.putValue(Action.SMALL_ICON, Res.iconEdit16);
			actionDeleteOrchestra.putValue(Action.SMALL_ICON, Res.iconDelete16);
			actionOrchestraUp.putValue(Action.SMALL_ICON, Res.iconUp16);
			actionOrchestraDown.putValue(Action.SMALL_ICON, Res.iconDown16);
			
			removeAll();
			
			JToolBar toolBar = new JToolBar();
			toolBar.add(new ToolbarButton(actionAddOrchestra));
			toolBar.add(new ToolbarButton(actionEditOrchestra));
			toolBar.add(new ToolbarButton(actionDeleteOrchestra));
			
			toolBar.addSeparator();
			
			toolBar.add(new ToolbarButton(actionOrchestraUp));
			toolBar.add(new ToolbarButton(actionOrchestraDown));
			
			toolBar.setFloatable(false);
			add(toolBar, java.awt.BorderLayout.NORTH);
			
			JScrollPane sp = new JScrollPane(orchestraTable);
			Dimension d;
			d = new Dimension(sp.getMinimumSize().width, sp.getPreferredSize().height);
			sp.setPreferredSize(d);
			
			add(sp);
			
			ManageOrchestrasPage.this.orchestraTable = this.orchestraTable;
		}
	}
	
	class OrchestraPane extends JSOrchestraPane {
		OrchestraPane() {
			actionAddInstrument.putValue(Action.SMALL_ICON, Res.iconNew16);
			actionEditInstrument.putValue(Action.SMALL_ICON, Res.iconEdit16);
			actionDeleteInstrument.putValue(Action.SMALL_ICON, Res.iconDelete16);
			actionInstrumentUp.putValue(Action.SMALL_ICON, Res.iconUp16);
			actionInstrumentDown.putValue(Action.SMALL_ICON, Res.iconDown16);
			
			removeAll();
			
			JToolBar toolBar = new JToolBar();
			toolBar.add(new ToolbarButton(actionAddInstrument));
			toolBar.add(new ToolbarButton(actionEditInstrument));
			toolBar.add(new ToolbarButton(actionDeleteInstrument));
			
			toolBar.addSeparator();
			
			toolBar.add(new ToolbarButton(actionInstrumentUp));
			toolBar.add(new ToolbarButton(actionInstrumentDown));
		
			toolBar.setFloatable(false);
			add(toolBar, java.awt.BorderLayout.NORTH);
			
			JScrollPane sp = new JScrollPane(instrumentTable);
			Dimension d;
			d = new Dimension(sp.getMinimumSize().width, sp.getPreferredSize().height);
			sp.setPreferredSize(d);
			
			add(sp);
		}
	}
	
	private JComponent
	createManageOrchestrasPane() {
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setContinuousLayout(true);
		splitPane.setTopComponent(new ManageOrchestrasPane());
		splitPane.setBottomComponent(orchestraPane);
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
						
						if(orchestraPane.getSelectedInstrument() == null) {
							return false;
						}
				
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
			
			orchestraPane.setOrchestra(orchestraTable.getSelectedOrchestra());
		}
	}
}
