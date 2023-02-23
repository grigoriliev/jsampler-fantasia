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

package org.jsampler.view.classic;

import java.awt.Dimension;

import java.awt.datatransfer.DataFlavor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.TransferHandler;

import net.sf.juife.swing.LinkButton;
import net.sf.juife.swing.NavigationPage;

import org.jsampler.CC;
import org.jsampler.DefaultOrchestraModel;
import org.jsampler.OrchestraInstrument;
import org.jsampler.OrchestraModel;

import org.jsampler.event.OrchestraAdapter;
import org.jsampler.event.OrchestraEvent;
import org.jsampler.event.ListEvent;
import org.jsampler.event.ListListener;

import org.jsampler.view.JSChannel;
import org.jsampler.view.JSChannelsPane;
import org.jsampler.view.swing.InstrumentTable;
import org.jsampler.view.swing.SHF;

import static org.jsampler.view.classic.ClassicI18n.i18n;


/**
 *
 * @author Grigor Iliev
 */
public class OrchestrasPage extends NavigationPage {
	private final JComboBox cbOrchestras = new JComboBox();
	private final InstrumentTable instrumentTable = new InstrumentTable(new InstrTableModel());
	
	private final LinkButton lnkManageOrchestras =
		new LinkButton(i18n.getButtonLabel("OrchestrasPage.lnkManageOrchestras"));
	
	
	/** Creates a new instance of OrchestrasPage */
	public OrchestrasPage() {
		setTitle(i18n.getLabel("OrchestrasPage.title"));
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		lnkManageOrchestras.setAlignmentX(LEFT_ALIGNMENT);
		add(lnkManageOrchestras);
		
		JSeparator sep = new JSeparator();
		sep.setMaximumSize(new Dimension(Short.MAX_VALUE, sep.getPreferredSize().height));
		add(sep);
		
		add(Box.createRigidArea(new Dimension(0, 12)));
		
		cbOrchestras.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { orchestraChanged(); }
		});
		
		for(int i = 0; i < CC.getOrchestras().getOrchestraCount(); i++) {
			cbOrchestras.addItem(CC.getOrchestras().getOrchestra(i));
		}
		
		CC.getOrchestras().addOrchestraListListener(getHandler());
		cbOrchestras.setEnabled(cbOrchestras.getItemCount() != 0);
		
		Dimension d;
		d = new Dimension(Short.MAX_VALUE, cbOrchestras.getPreferredSize().height);
		cbOrchestras.setMaximumSize(d);
		cbOrchestras.setAlignmentX(LEFT_ALIGNMENT);
		add(cbOrchestras);
		
		add(Box.createRigidArea(new Dimension(0, 5)));
		
		JScrollPane sp = new DnDScrollPane(instrumentTable);
		sp.setAlignmentX(LEFT_ALIGNMENT);
		add(sp);
		
		setBorder(BorderFactory.createEmptyBorder(5, 3, 5, 3));
		
		installListeners();
	}
	
	private void
	installListeners() {
		lnkManageOrchestras.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				LeftPane.getLeftPane().showManageOrchestrasPage();
			}
		});
		
		instrumentTable.addMouseListener(new MouseAdapter() {
			public void
			mouseClicked(MouseEvent e) {
				if(e.getClickCount() < 2) return;
				
				OrchestraInstrument instr = instrumentTable.getSelectedInstrument();
				if(instr == null) return;
				loadInstrument(instr);
			}
		});
	}
	
	private void
	loadInstrument(OrchestraInstrument instr) {
		JSChannelsPane cp = CC.getMainFrame().getSelectedChannelsPane();
		JSChannel chn = null;
		
		if(cp.hasSelectedChannel()) chn = cp.getSelectedChannels()[0];
		if(chn == null) {
			for(JSChannel c : cp.getChannels()) {
				if( (c.getChannelInfo().getInstrumentName() == null ||
					c.getChannelInfo().getInstrumentStatus() < -1) &&
					c.getChannelInfo().getAudioOutputDevice() != -1 ) {
					chn = c;
					break;
				} 
			}
		}
		
		if(chn == null) {
			SHF.showErrorMessage("Select channel!");
			return;
		}
		
		chn.getModel().loadBackendInstrument(instr.getFilePath(), instr.getInstrumentIndex());
	}
	
	/**
	 * Gets the index of the orchestra whose instruments are shown.
	 * @return The position of the currently selected orchestra,
	 * and -1 if no orchestra is selected.
	 */
	public int
	getCurrentOrchestraIndex() {
		OrchestraModel om = (OrchestraModel)cbOrchestras.getSelectedItem();
		return CC.getOrchestras().getOrchestraIndex(om);
	}
	
	public void
	setSelectedOrchestra(OrchestraModel orchestra) {
		cbOrchestras.setSelectedItem(orchestra);
	}
	
	private void
	orchestraChanged() {
		OrchestraModel om = (OrchestraModel)cbOrchestras.getSelectedItem();
		if(om == null) om = new DefaultOrchestraModel();
		instrumentTable.getModel().setOrchestraModel(om);
		
		String s = om.getDescription();
		if(s != null && s.length() == 0) s = null;
		cbOrchestras.setToolTipText(s);
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
						if(cbOrchestras.getSelectedItem() == null) {
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
			
		/*class SPTransferHandler extends TransferHandler {
			public boolean
			canImport(JComponent comp, DataFlavor[] flavors) {
				if(instrumentTable.isPerformingDnD()) return false;
				if(cbOrchestras.getSelectedItem() == null) return false;
				
				for(int i = 0; i < flavors.length; i++) {
					if(DataFlavor.stringFlavor.equals(flavors[i])) {
						return true;
					}
				}
				
				return false;
			}
			
			public boolean
			importData(JComponent c, Transferable t) {
				if(!canImport(c, t.getTransferDataFlavors())) return false;
				String str;
				try {
					str = (String)t.getTransferData(DataFlavor.stringFlavor);
					instrumentTable.setInstrument(str);
					return true;
				} catch(Exception x) {
					CC.getLogger().log(Level.INFO, HF.getErrorMessage(x), x);
				}
				
				return false;
			}
		}*/
	}
		
	
	private final Handler eventHandler = new Handler();
	
	private Handler
	getHandler() { return eventHandler; }
	
	private class Handler extends OrchestraAdapter implements ListListener<OrchestraModel> {
		/** Invoked when an orchestra is added to the orchestra list. */
		public void
		entryAdded(ListEvent<OrchestraModel> e) {
			if(cbOrchestras.getItemCount() == 0) cbOrchestras.setEnabled(true);
			cbOrchestras.addItem(e.getEntry());
		}
	
		/** Invoked when an orchestra is removed from the orchestra list. */
		public void
		entryRemoved(ListEvent<OrchestraModel> e) {
			cbOrchestras.removeItem(e.getEntry());
			if(cbOrchestras.getItemCount() == 0) cbOrchestras.setEnabled(false);
		}
		
		/** Invoked when the name of orchestra is changed. */
		public void
		nameChanged(OrchestraEvent e) {
			
		}
	
		/** Invoked when the description of orchestra is changed. */
		public void
		descriptionChanged(OrchestraEvent e) { }
	
	
	}
	
	private class InstrTableModel extends org.jsampler.view.swing.InstrumentTableModel {
		/**
		 * Returns <code>true</code> if the cell at
		 * <code>row</code> and <code>col</code> is editable.
		 */
		public boolean
		isCellEditable(int row, int col) { return false; }
	
		/**
		 * Gets the name of the column at <code>columnIndex</code>.
		 * @return The name of the column at <code>columnIndex</code>.
		 */
		public String
		getColumnName(int col) {
			return i18n.getLabel("OrchestrasPage.instruments");
		}
	}
}
