/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2011 Grigor Iliev <grigor@grigoriliev.com>
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
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jsampler.CC;
import org.jsampler.view.EffectTable;

import org.linuxsampler.lscp.Effect;

import net.sf.juife.OkCancelDialog;

import static org.jsampler.view.std.StdI18n.i18n;

/**
 *
 * @author Grigor Iliev
 */
public class JSAddEffectInstancesDlg extends OkCancelDialog implements ListSelectionListener {
	public final EffectTable effectTable;
	public
	JSAddEffectInstancesDlg() {
		super(CC.getMainFrame(), i18n.getLabel("JSAddEffectInstancesDlg.title"));
		setName("JSAddEffectInstancesDlg");
		
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		
		effectTable = new EffectTable();
		p.add(new JScrollPane(effectTable));
		setMainPane(p);
		
		setSavedSize();
		
		setResizable(true);
		
		effectTable.getSelectionModel().addListSelectionListener(this);
		btnOk.setEnabled(effectTable.getSelectedRowCount() > 0);
	}
	
	@Override
	protected void
	onOk() {
		if(!btnOk.isEnabled()) return;
		
		effectTable.saveColumnWidths();
		StdUtils.saveWindowBounds(getName(), getBounds());
		
		setVisible(false);
		setCancelled(false);
	}
	
	@Override
	protected void
	onCancel() { setVisible(false); }
	
	private void
	setSavedSize() {
		Rectangle r = StdUtils.getWindowBounds(getName());
		if(r == null) return;
		
		setBounds(r);
	}
	
	public void
	valueChanged(ListSelectionEvent e) {
		btnOk.setEnabled(effectTable.getSelectedRowCount() > 0);
	}
	
	public Effect[]
	getSelectedEffects() { return effectTable.getSelectedEffects(); }
}
