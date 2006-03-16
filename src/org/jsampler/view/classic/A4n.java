/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005 Grigor Kirilov Iliev
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

import java.awt.MediaTracker;

import java.awt.event.ActionEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.net.URL;

import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.jsampler.CC;
import org.jsampler.HF;
import org.jsampler.JSampler;

import org.jsampler.view.JSChannel;
import org.jsampler.view.JSChannelsPane;
import org.jsampler.view.JSMainFrame;

import net.sf.juife.event.TaskEvent;
import net.sf.juife.event.TaskListener;

import static org.jsampler.view.classic.ClassicI18n.i18n;


/**
 * This class provides an <code>Action</code> instances performing all needed tasks.
 * @author Grigor Iliev
 */
public class A4n {
	private static boolean
	verifyConnection() {
		if(!CC.getClient().isConnected()) {
			HF.showErrorMessage(i18n.getError("notConnected"));
			return false;
		}
		
		return true;
	}
	
	public final static Action connect = new Connect();
		
	private static class Connect extends AbstractAction {
		Connect() {
			super(i18n.getMenuLabel("actions.connect"));
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("ttConnect"));
		}
		
		public void
		actionPerformed(ActionEvent e) {
			CC.initSamplerModel();
		}
	}
	
	public final static Action samplerInfo = new SamplerInfo();
	
	private static class SamplerInfo extends AbstractAction {
		SamplerInfo() {
			super(i18n.getMenuLabel("actions.samplerInfo"));
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("ttSamplerInfo"));
			
			try {
				URL url = ClassLoader.getSystemClassLoader().getResource (
					"org/jsampler/view/classic/res/icons/toolbar/About24.gif"
				);
				
				ImageIcon icon = new ImageIcon(url);
				if(icon.getImageLoadStatus() == MediaTracker.COMPLETE)
					putValue(Action.SMALL_ICON, icon);
			} catch(Exception x) {
				CC.getLogger().log(Level.INFO, HF.getErrorMessage(x), x);
			}
		}
		
		public void
		actionPerformed(ActionEvent e) {
			new SamplerInfoDlg(CC.getMainFrame()).setVisible(true);
		}
	}
	
	public final static Action refresh = new Refresh();
	
	private static class Refresh extends AbstractAction {
		Refresh() {
			super(i18n.getMenuLabel("actions.refresh"));
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("ttRefresh"));
			
			try {
				URL url = ClassLoader.getSystemClassLoader().getResource (
					"org/jsampler/view/classic/res/icons/toolbar/Refresh24.gif"
				);
				
				ImageIcon icon = new ImageIcon(url);
				if(icon.getImageLoadStatus() == MediaTracker.COMPLETE)
					putValue(Action.SMALL_ICON, icon);
			} catch(Exception x) {
				CC.getLogger().log(Level.INFO, HF.getErrorMessage(x), x);
			}
		}
		
		public void
		actionPerformed(ActionEvent e) { CC.initSamplerModel(); }
	}
	
	public final static Action resetSampler = new Reset();
		
	private static class Reset extends AbstractAction {
		Reset() {
			super(i18n.getMenuLabel("actions.resetSampler"));
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("ttResetSampler"));
		}
		
		public void
		actionPerformed(ActionEvent e) {
			CC.getTaskQueue().add(new org.jsampler.task.ResetSampler());
		}
	}
	
	public final static Action addMidiDevice = new AddMidiDevice();
	
	private static class AddMidiDevice extends AbstractAction {
		AddMidiDevice() {
			super("");
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("ttAddMidiDevice"));
			
			try {
				URL url = ClassLoader.getSystemClassLoader().getResource (
					"org/jsampler/view/classic/res/icons/New16.gif"
				);
				
				ImageIcon icon = new ImageIcon(url);
				if(icon.getImageLoadStatus() == MediaTracker.COMPLETE)
					putValue(Action.SMALL_ICON, icon);
			} catch(Exception x) {
				CC.getLogger().log(Level.INFO, HF.getErrorMessage(x), x);
			}
		}
		
		public void
		actionPerformed(ActionEvent e) {
			if(!verifyConnection()) return;
			new NewMidiDeviceDlg(CC.getMainFrame()).setVisible(true);
		}
	}
	
	public final static Action addAudioDevice = new AddAudioDevice();
	
	private static class AddAudioDevice extends AbstractAction {
		AddAudioDevice() {
			super("");
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("ttAddAudioDevice"));
			
			try {
				URL url = ClassLoader.getSystemClassLoader().getResource (
					"org/jsampler/view/classic/res/icons/New16.gif"
				);
				
				ImageIcon icon = new ImageIcon(url);
				if(icon.getImageLoadStatus() == MediaTracker.COMPLETE)
					putValue(Action.SMALL_ICON, icon);
			} catch(Exception x) {
				CC.getLogger().log(Level.INFO, HF.getErrorMessage(x), x);
			}
		}
		
		public void
		actionPerformed(ActionEvent e) {
			if(!verifyConnection()) return;
			new NewAudioDeviceDlg(CC.getMainFrame()).setVisible(true);
		}
	}

// EDIT
	public final static Action preferences = new Preferences();
	
	private static class Preferences extends AbstractAction {
		Preferences() {
			super(i18n.getMenuLabel("edit.preferences"));
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("ttPrefs"));
			
			try {
				URL url = ClassLoader.getSystemClassLoader().getResource (
					"org/jsampler/view/classic/res/icons/toolbar/Preferences24.gif"
				);
				
				ImageIcon icon = new ImageIcon(url);
				if(icon.getImageLoadStatus() == MediaTracker.COMPLETE)
					putValue(Action.SMALL_ICON, icon);
			} catch(Exception x) {
				CC.getLogger().log(Level.INFO, HF.getErrorMessage(x), x);
			}
		}
		
		public void
		actionPerformed(ActionEvent e) { new PrefsDlg(CC.getMainFrame()).setVisible(true); }
	}

// CHANNELS
	public final static Action newChannel = new NewChannel();
	public final static Action newChannelWizard = new NewChannelWizard();
	public final static Action duplicateChannels = new DuplicateChannels();
	
	public final static Action moveChannelsOnTop = new MoveChannelsOnTop();
	public final static Action moveChannelsUp = new MoveChannelsUp();
	public final static Action moveChannelsDown = new MoveChannelsDown();
	public final static Action moveChannelsAtBottom = new MoveChannelsAtBottom();
	
	public final static Action selectAllChannels = new SelectAllChannels();
	public final static Action deselectChannels = new DeselectChannels();
	
	public final static Action removeChannels = new RemoveChannels();
	
	
	private static class NewChannel extends AbstractAction {
		NewChannel() {
			super(i18n.getMenuLabel("channels.new"));
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("ttNewChannel"));
			
			try {
				URL url = ClassLoader.getSystemClassLoader().getResource (
					"org/jsampler/view/classic/res/icons/toolbar/New24.gif"
				);
				
				ImageIcon icon = new ImageIcon(url);
				if(icon.getImageLoadStatus() == MediaTracker.COMPLETE)
					putValue(Action.SMALL_ICON, icon);
			} catch(Exception x) {
				CC.getLogger().log(Level.INFO, HF.getErrorMessage(x), x);
			}
		}
		
		public void
		actionPerformed(ActionEvent e) {
			if(!verifyConnection()) return;
			CC.getSamplerModel().createChannel();
		}
	}
	
	private static class NewChannelWizard extends AbstractAction {
		NewChannelWizard() {
			super(i18n.getMenuLabel("channels.wizard"));
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("ttNewChannelWizard"));
		}
		
		public void
		actionPerformed(ActionEvent e) {
			new org.jsampler.view.classic.NewChannelWizard().showWizard();
		}
	}
	
	private static class DuplicateChannels extends AbstractAction {
		DuplicateChannels() {
			super(i18n.getMenuLabel("channels.duplicate"));
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("ttDuplicateChannels"));
			
			try {
				URL url = ClassLoader.getSystemClassLoader().getResource (
					"org/jsampler/view/classic/res/icons/toolbar/Copy24.gif"
				);
				
				ImageIcon icon = new ImageIcon(url);
				if(icon.getImageLoadStatus() == MediaTracker.COMPLETE)
					putValue(Action.SMALL_ICON, icon);
			} catch(Exception x) {
				CC.getLogger().log(Level.INFO, HF.getErrorMessage(x), x);
			}
			
			setEnabled(false);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			JSChannel[] channels =
				CC.getMainFrame().getSelectedChannelsPane().getSelectedChannels();
			
			if(channels.length > 2) {
				if(!HF.showYesNoDialog (
					CC.getMainFrame(),
					i18n.getMessage("A4n.duplicateChannels?")
				)) return;
			}
			
			CC.getTaskQueue().add (
				new org.jsampler.task.DuplicateChannels(channels)
			);
		}
	}
	
	private static class MoveChannelsOnTop extends AbstractAction {
		MoveChannelsOnTop() {
			super(i18n.getMenuLabel("channels.MoveOnTop"));
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("ttMoveChannelsOnTop"));
			setEnabled(false);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			ChannelsPane p = (ChannelsPane)CC.getMainFrame().getSelectedChannelsPane();
			p.moveSelectedChannelsOnTop();
		}
	}
	
	private static class MoveChannelsUp extends AbstractAction {
		MoveChannelsUp() {
			super(i18n.getMenuLabel("channels.MoveUp"));
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("ttMoveChannelsUp"));
			
			try {
				URL url = ClassLoader.getSystemClassLoader().getResource (
					"org/jsampler/view/classic/res/icons/toolbar/Up24.gif"
				);
				
				ImageIcon icon = new ImageIcon(url);
				if(icon.getImageLoadStatus() == MediaTracker.COMPLETE)
					putValue(Action.SMALL_ICON, icon);
			} catch(Exception x) {
				CC.getLogger().log(Level.INFO, HF.getErrorMessage(x), x);
			}
			
			setEnabled(false);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			ChannelsPane p = (ChannelsPane)CC.getMainFrame().getSelectedChannelsPane();
			p.moveSelectedChannelsUp();
		}
	}
	
	private static class MoveChannelsDown extends AbstractAction {
		MoveChannelsDown() {
			super(i18n.getMenuLabel("channels.MoveDown"));
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("ttMoveChannelsDown"));
			
			try {
				URL url = ClassLoader.getSystemClassLoader().getResource (
					"org/jsampler/view/classic/res/icons/toolbar/Down24.gif"
				);
				
				ImageIcon icon = new ImageIcon(url);
				if(icon.getImageLoadStatus() == MediaTracker.COMPLETE)
					putValue(Action.SMALL_ICON, icon);
			} catch(Exception x) {
				CC.getLogger().log(Level.INFO, HF.getErrorMessage(x), x);
			}
			
			setEnabled(false);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			ChannelsPane p = (ChannelsPane)CC.getMainFrame().getSelectedChannelsPane();
			p.moveSelectedChannelsDown();
		}
	}
	
	private static class MoveChannelsAtBottom extends AbstractAction {
		MoveChannelsAtBottom() {
			super(i18n.getMenuLabel("channels.MoveAtBottom"));
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("ttMoveChannelsAtBottom"));
			setEnabled(false);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			ChannelsPane p = (ChannelsPane)CC.getMainFrame().getSelectedChannelsPane();
			p.moveSelectedChannelsAtBottom();
		}
	}
	
	public static class
	MoveChannelsTo extends AbstractAction implements PropertyChangeListener {
		private final JSChannelsPane pane;
		
		MoveChannelsTo(JSChannelsPane pane) {
			super(pane.getTitle());
			
			this.pane = pane;
			
			String desc = i18n.getMenuLabel("ttMoveChannelsTo", pane.getTitle());
			putValue(SHORT_DESCRIPTION, desc);
			pane.addPropertyChangeListener(JSChannelsPane.TITLE, this);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			JSChannelsPane acp = CC.getMainFrame().getSelectedChannelsPane();
			JSChannel[] chns = acp.getSelectedChannels();
			
			for(JSChannel c : chns) acp.removeChannel(c);
			
			pane.addChannels(chns);
			
			CC.getMainFrame().setSelectedChannelsPane(pane);
			
		}
		
		public void
		propertyChange(PropertyChangeEvent e) {
			putValue(NAME, pane.getTitle());
			
			String desc = i18n.getMenuLabel("ttMoveChannelsTo", pane.getTitle());
			putValue(SHORT_DESCRIPTION, desc);
		}
	
		public JSChannelsPane
		getChannelsPane() { return pane; }
	}
	
	private static class SelectAllChannels extends AbstractAction {
		SelectAllChannels() {
			super(i18n.getMenuLabel("channels.selectAll"));
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("ttSelectAllChannels"));
		}
		
		public void
		actionPerformed(ActionEvent e) {
			ChannelsPane p = (ChannelsPane)CC.getMainFrame().getSelectedChannelsPane();
			p.selectAllChannels();
		}
	}
	
	private static class DeselectChannels extends AbstractAction {
		DeselectChannels() {
			super(i18n.getMenuLabel("channels.selectNone"));
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("ttDeselectChannels"));
		}
		
		public void
		actionPerformed(ActionEvent e) {
			ChannelsPane p = (ChannelsPane)CC.getMainFrame().getSelectedChannelsPane();
			p.deselectChannels();
		}
	}
	
	private static class RemoveChannels extends AbstractAction {
		RemoveChannels() {
			super(i18n.getMenuLabel("channels.RemoveChannel"));
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("ttRemoveChannels"));
			
			try {
				URL url = ClassLoader.getSystemClassLoader().getResource (
					"org/jsampler/view/classic/res/icons/toolbar/Delete24.gif"
				);
				
				ImageIcon icon = new ImageIcon(url);
				if(icon.getImageLoadStatus() == MediaTracker.COMPLETE)
					putValue(Action.SMALL_ICON, icon);
			} catch(Exception x) {
				CC.getLogger().log(Level.INFO, HF.getErrorMessage(x), x);
			}
			
			setEnabled(false);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			JSChannelsPane p = CC.getMainFrame().getSelectedChannelsPane();
			if(p.getSelectedChannelCount() > 1) 
				if(!HF.showYesNoDialog (
					CC.getMainFrame(), i18n.getMessage("A4n.removeChannels?")
				)) return;
			
			JSChannel[] chnS = p.getSelectedChannels();
			
			for(JSChannel c : chnS) removeChannel(c);
		}
		
		private void
		removeChannel(final JSChannel c) {
			final JSChannelsPane p = CC.getMainFrame().getSelectedChannelsPane();
			int id = c.getChannelInfo().getChannelID();
			final org.jsampler.task.RemoveChannel rc = 
				new org.jsampler.task.RemoveChannel(id);
			
			rc.addTaskListener(new TaskListener() {
				public void
				taskPerformed(TaskEvent e) {
					if(rc.doneWithErrors()) return;
				
					p.removeChannel(c);
				}
			});
			
			CC.getTaskQueue().add(rc);
		}
	}
	
// TABS
	public final static Action newChannelsTab = new NewChannelsTab();
	
	public final static Action editTabTitle = new EditTabTitle();
	
	public final static Action moveTab2Beginning = new MoveTab2Beginning();
	public final static Action moveTab2Left = new MoveTab2Left();
	public final static Action moveTab2Right = new MoveTab2Right();
	public final static Action moveTab2End = new MoveTab2End();
	
	public final static Action closeChannelsTab = new CloseChannelsTab();
	
	
	private static class NewChannelsTab extends AbstractAction {
		NewChannelsTab() {
			super(i18n.getMenuLabel("tabs.new"));
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("ttNewTab"));
			
			/*try {
				URL u = new URL(PATH + "general/Preferences24.gif");
				ImageIcon ii = new ImageIcon(u);
				if(ii.getImageLoadStatus() == MediaTracker.COMPLETE)
					putValue(Action.SMALL_ICON, ii);
			} catch(Exception x) {
				CC.getLogger().log(Level.INFO, HF.getErrorMessage(x), x);
			}*/
		}
		
		public void
		actionPerformed(ActionEvent e) {
			new NewChannelsTabDlg(CC.getMainFrame()).setVisible(true);
		}
	}
	
	private static class EditTabTitle extends AbstractAction {
		EditTabTitle() {
			super(i18n.getMenuLabel("tabs.changeTitle"));
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("ttEditTabTitle"));
			setEnabled(false);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			new ChangeTabTitleDlg(CC.getMainFrame()).setVisible(true);
		}
	}
	
	private static class MoveTab2Beginning extends AbstractAction {
		MoveTab2Beginning() {
			super(i18n.getMenuLabel("tabs.move2Beginning"));
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("ttMoveTab2Beginning"));
			setEnabled(false);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			((MainFrame)CC.getMainFrame()).moveTab2Beginning();
		}
	}
	
	private static class MoveTab2Left extends AbstractAction {
		MoveTab2Left() {
			super(i18n.getMenuLabel("tabs.move2Left"));
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("ttMoveTab2Left"));
			setEnabled(false);
		}
		
		public void
		actionPerformed(ActionEvent e) { ((MainFrame)CC.getMainFrame()).moveTab2Left(); }
	}
	
	private static class MoveTab2Right extends AbstractAction {
		MoveTab2Right() {
			super(i18n.getMenuLabel("tabs.move2Right"));
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("ttMoveTab2Right"));
			setEnabled(false);
		}
		
		public void
		actionPerformed(ActionEvent e) { ((MainFrame)CC.getMainFrame()).moveTab2Right(); }
	}
	
	private static class MoveTab2End extends AbstractAction {
		MoveTab2End() {
			super(i18n.getMenuLabel("tabs.move2End"));
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("ttMoveTab2End"));
			setEnabled(false);
		}
		
		public void
		actionPerformed(ActionEvent e) { ((MainFrame)CC.getMainFrame()).moveTab2End(); }
	}
	
	private static class CloseChannelsTab extends AbstractAction {
		CloseChannelsTab() {
			super(i18n.getMenuLabel("tabs.close"));
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("ttCloseTab"));
			
			setEnabled(false);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			JSMainFrame frm = CC.getMainFrame();
			JSChannelsPane chnPane = frm.getSelectedChannelsPane();
			if(chnPane.getChannelCount() > 0) {
				CloseTabDlg dlg = new CloseTabDlg(frm);
				dlg.setVisible(true);
				if(dlg.isCancelled()) return;
				
				JSChannel[] chns = chnPane.getChannels();
				
				if(dlg.remove()) {
					
					
				} else {
					JSChannelsPane p = dlg.getSelectedChannelsPane();
					for(JSChannel c : chns) {
						chnPane.removeChannel(c);
					}
					p.addChannels(chns);
					frm.setSelectedChannelsPane(p);
				}
			}
			
			frm.removeChannelsPane(chnPane);
		}
	}

// HELP
	public final static HelpAbout helpAbout = new HelpAbout();
	
	private static class HelpAbout extends AbstractAction {
		HelpAbout() {
			super(i18n.getMenuLabel("help.about", "JS Classic"));
		}
		
		public void
		actionPerformed(ActionEvent e) {
			new HelpAboutDlg(CC.getMainFrame()).setVisible(true);
		}
	}
}
