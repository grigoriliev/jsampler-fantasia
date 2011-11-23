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

package org.jsampler.view.std;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import net.sf.juife.swing.OkCancelDialog;

import org.jsampler.CC;
import org.jsampler.view.swing.SHF;

import static org.jsampler.view.std.StdI18n.i18n;

/**
 *
 * @author Grigor Iliev
 */
public class JSPianoRollPrefsDlg extends OkCancelDialog {
	private final MainPane mainPane = new MainPane();
	
	public
	JSPianoRollPrefsDlg() {
		super(SHF.getMainFrame(), i18n.getLabel("JSPianoRollPrefsDlg.title"));
		btnOk.setText(i18n.getButtonLabel("apply"));
		
		setMainPane(mainPane);
		btnOk.requestFocus();
	}
	
	protected void
	onOk() {
		if(!btnOk.isEnabled()) return;
		
		try { mainPane.apply(); }
		catch(Exception x) { SHF.showErrorMessage(x); return; }
		
		setVisible(false);
		setCancelled(false);
	}
	
	protected void
	onCancel() { setVisible(false); }
	
	public static class MainPane extends JPanel {
		private final JLabel lFromKey =
			new JLabel(i18n.getLabel("JSPianoRollPrefsDlg.lFromKey"));
		
		private final JLabel lToKey =
			new JLabel(i18n.getLabel("JSPianoRollPrefsDlg.lToKey"));
		
		private final JLabel lHeight =
			new JLabel(i18n.getLabel("JSPianoRollPrefsDlg.lHeight"));
		
		private final JSpinner spinnerFirstKey;
		private final JSpinner spinnerLastKey;
		
		private final JSpinner spinnerHeight;
		
		public
		MainPane() {
			spinnerFirstKey = new JSpinner(new SpinnerNumberModel(0, 0, 127, 1));
			spinnerLastKey = new JSpinner(new SpinnerNumberModel(0, 0, 127, 1));
			spinnerHeight = new JSpinner(new SpinnerNumberModel(80, 80, 300, 1));
			
			int i = CC.preferences().getIntProperty("midiKeyboard.firstKey");
			spinnerFirstKey.setValue(i);
			
			i = CC.preferences().getIntProperty("midiKeyboard.lastKey");
			spinnerLastKey.setValue(i);
			
			i = CC.preferences().getIntProperty("midiKeyboard.height");
			spinnerHeight.setValue(i);
			
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			
			JPanel p = new JPanel();
			p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
			p.add(lFromKey);
			p.add(Box.createRigidArea(new Dimension(6, 0)));
			p.add(spinnerFirstKey);
			p.add(Box.createRigidArea(new Dimension(6, 0)));
			p.add(lToKey);
			p.add(Box.createRigidArea(new Dimension(6, 0)));
			p.add(spinnerLastKey);
			p.setAlignmentX(LEFT_ALIGNMENT);
			add(p);
			
			String s = i18n.getLabel("JSPianoRollPrefsDlg.keyRange");
			p.setBorder(BorderFactory.createTitledBorder(s));
			p.setMaximumSize(new Dimension(Short.MAX_VALUE, p.getPreferredSize().height));
			
			add(Box.createRigidArea(new Dimension(0, 6)));
			
			p = new JPanel();
			p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
			p.add(lHeight);
			p.add(Box.createRigidArea(new Dimension(6, 0)));
			p.add(spinnerHeight);
			p.setAlignmentX(LEFT_ALIGNMENT);
			add(p);
		}
		
		public void
		apply() throws Exception {
			int i = Integer.parseInt(spinnerFirstKey.getValue().toString());
			int j = Integer.parseInt(spinnerLastKey.getValue().toString());
			
			if(i < 0 || i > 127 || j < 0 || j > 127 || i >= j) {
				String s = i18n.getError("JSPianoRollPrefsDlg.invalidKeyRange!");
				throw new Exception(s);
			}
			
			if(j - i < 31) {
				int k = j - i + 1;
				String s = i18n.getError("JSPianoRollPrefsDlg.tooSmallKeyRange!", k);
				throw new Exception(s);
			}
			
			CC.preferences().setIntProperty("midiKeyboard.firstKey", i);
			CC.preferences().setIntProperty("midiKeyboard.lastKey", j);
			
			i = Integer.parseInt(spinnerHeight.getValue().toString());
			CC.preferences().setIntProperty("midiKeyboard.height", i);
		}
	}
}
