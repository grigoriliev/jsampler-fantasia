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

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import net.sf.juife.JuifeUtils;

import net.sf.juife.event.TaskEvent;
import net.sf.juife.event.TaskListener;

import org.jsampler.CC;
import org.jsampler.HF;
import org.jsampler.task.InstrumentsDb.GetScanJobInfo;

import org.linuxsampler.lscp.ScanJobInfo;

import org.linuxsampler.lscp.event.InstrumentsDbAdapter;
import org.linuxsampler.lscp.event.InstrumentsDbEvent;

import static org.jsampler.view.classic.ClassicI18n.i18n;

/**
 *
 * @author Grigor Iliev
 */
public class AddDbInstrumentsProgressDlg extends JDialog {
	private final JProgressBar progressJobStatus = new JProgressBar(0, 100);
	private final JProgressBar progressFileStatus = new JProgressBar(0, 100);
	private final JButton btnHide =
		new JButton(i18n.getButtonLabel("AddDbInstrumentsProgressDlg.btnHide"));
	
	private final int jobId;
	
	/** Creates a new instance of <code>AddDbInstrumentsProgressDlg</code> */
	public
	AddDbInstrumentsProgressDlg(Frame owner, int jobId) {
		super(owner, i18n.getLabel("AddDbInstrumentsProgressDlg.title"), true);
		this.jobId = jobId;
		
		initAddDbInstrumentsProgressDlg();
	}
	
	/** Creates a new instance of <code>AddDbInstrumentsProgressDlg</code> */
	public
	AddDbInstrumentsProgressDlg(Dialog owner, int jobId) {
		super(owner, i18n.getLabel("AddDbInstrumentsProgressDlg.title"), true);
		this.jobId = jobId;
		
		initAddDbInstrumentsProgressDlg();
	}
	
	private void
	initAddDbInstrumentsProgressDlg() {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		progressJobStatus.setAlignmentX(CENTER_ALIGNMENT);
		p.add(progressJobStatus);
		p.add(Box.createRigidArea(new Dimension(0, 6)));
		progressFileStatus.setAlignmentX(CENTER_ALIGNMENT);
		p.add(progressFileStatus);
		p.add(Box.createRigidArea(new Dimension(0, 6)));
		btnHide.setAlignmentX(CENTER_ALIGNMENT);
		p.add(btnHide);
		
		progressJobStatus.setStringPainted(true);
		progressFileStatus.setStringPainted(true);
		
		p.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		add(p);
		
		Dimension d = p.getPreferredSize();
		p.setPreferredSize(new Dimension(400, d.height));
		
		pack();
		setMinimumSize(getPreferredSize());
		setLocation(JuifeUtils.centerLocation(this, getOwner()));
		
		btnHide.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				// TODO: vvv this should be done out of the event-dispatching thread
				CC.getClient().removeInstrumentsDbListener(getHandler());
				//////
				setVisible(false);
			}
		});
		
		// TODO: vvv this should be done out of the event-dispatching thread
		CC.getClient().addInstrumentsDbListener(getHandler());
		//////
	}
	
	private void
	updateStatus() {
		final GetScanJobInfo t = new GetScanJobInfo(jobId);
		
		t.addTaskListener(new TaskListener() {
			public void
			taskPerformed(TaskEvent e) {
				if(t.doneWithErrors()) {
					failed();
					return;
				}
				
				updateStatus(t.getResult());
			}
		});
		
		CC.scheduleTask(t);
	}
		
	private void
	updateStatus(ScanJobInfo info) {
		if(info.isFinished()) {
			// TODO: vvv this should be done out of the event-dispatching thread
			CC.getClient().removeInstrumentsDbListener(getHandler());
			//////
			
			if(info.status < 0) {
				failed();
				return;
			}
			
			progressJobStatus.setValue(progressJobStatus.getMaximum());
			progressFileStatus.setValue(progressFileStatus.getMaximum());
			
			setVisible(false);
			getOwner().setVisible(false);
			return;
		}
		
		if(progressJobStatus.getMaximum() != info.filesTotal * 100) {
			progressJobStatus.setMaximum(info.filesTotal * 100);
		}
		
		String s = i18n.getMessage (
			"AddDbInstrumentsProgressDlg.jobStatus", info.filesScanned, info.filesTotal
		);
		progressJobStatus.setString(s);
		
		progressFileStatus.setValue(info.status);
		progressFileStatus.setString(info.scanning);
		
		progressJobStatus.setValue((info.filesScanned * 100) + info.status);
	}
	
	private void
	failed() {
		HF.showErrorMessage(i18n.getMessage("AddDbInstrumentsProgressDlg.failed"), this);
		setVisible(false);
	}
	
	private final EventHandler eventHandler = new EventHandler();
	
	private EventHandler
	getHandler() { return eventHandler; }
	
	private class EventHandler extends InstrumentsDbAdapter {
		/** Invoked when the status of particular job has changed. */
		public void
		jobStatusChanged(InstrumentsDbEvent e) {
			if(e.getJobId() != jobId) return;
			
			SwingUtilities.invokeLater(new Runnable() {
				public void
				run() { updateStatus(); }
			});
		}
	}
}
