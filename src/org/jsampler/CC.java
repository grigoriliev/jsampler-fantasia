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

package org.jsampler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.FileOutputStream;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import javax.swing.Timer;

import org.jsampler.task.*;

import org.jsampler.view.JSMainFrame;
import org.jsampler.view.JSProgress;

import org.linuxsampler.lscp.Client;
import org.linuxsampler.lscp.event.*;

import net.sf.juife.Task;
import net.sf.juife.TaskQueue;

import net.sf.juife.event.TaskEvent;
import net.sf.juife.event.TaskListener;
import net.sf.juife.event.TaskQueueEvent;
import net.sf.juife.event.TaskQueueListener;


/**
 *
 * @author Grigor Iliev
 */
public class CC {
	private static Handler handler;
	public static FileOutputStream fos;
	
	private static JSMainFrame mainFrame = null;
	private static JSProgress progress = null;
	
	private final static Client lsClient = new Client();
	
	private final static TaskQueue taskQueue = new TaskQueue();
	private final static Timer timer = new Timer(1000, null);
	
	private final static EventHandler eventHandler = new EventHandler();
	
	public static Logger
	getLogger() {
		return Logger.getLogger (
			"org.jsampler",
			"org.jsampler.langprops.LogsBundle"
		);
	}
	
	public static TaskQueue
	getTaskQueue() { return taskQueue; }
	
	public static JSMainFrame
	getMainFrame() { return mainFrame; }
	
	public static void
	setMainFrame(JSMainFrame mainFrame) { CC.mainFrame = mainFrame; }
	
	public static JSProgress
	getProgressIndicator() { return progress; }
	
	public static void
	setProgressIndicator(JSProgress progress) { CC.progress = progress; }
	
	protected static void
	initJSampler() {
		fos = null;
		
		try { fos = new FileOutputStream("JSampler.log"); }
		catch(Exception x) { x.printStackTrace(); }
		
		if(fos == null) handler = new StreamHandler(System.out, new SimpleFormatter());
		else handler = new StreamHandler(fos, new SimpleFormatter());
		
		handler.setLevel(Level.FINE);
		getLogger().addHandler(handler);
		getLogger().setLevel(Level.FINE);
		Logger.getLogger("org.linuxsampler.lscp").addHandler(handler);
		Logger.getLogger("org.linuxsampler.lscp").setLevel(Level.FINE);
		
		// Flushing logs on every second
		new java.util.Timer().schedule(new java.util.TimerTask() {
			public void
			run() { if(handler != null) handler.flush(); }
		}, 1000, 1000);
		
		CC.getLogger().fine("CC.jsStarted");
		
		HF.setUIDefaultFont(Prefs.getInterfaceFont());
		
		
		
		getClient().setServerAddress(Prefs.getLSAddress());
		getClient().setServerPort(Prefs.getLSPort());
		
		timer.setRepeats(false);
		
		timer.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { CC.getProgressIndicator().start(); }
		});
		
		taskQueue.addTaskQueueListener(new TaskQueueListener() {
			public void
			stateChanged(TaskQueueEvent e) {
				switch(e.getEventID()) {
				case TASK_FETCHED:
					CC.getProgressIndicator().setString (
						((Task)e.getSource()).getDescription()
					);
					break;
				case TASK_DONE:
					Task t = (Task)e.getSource();
					if(t.doneWithErrors()) 
						HF.showErrorMessage(t.getErrorMessage());
					break;
				case NOT_IDLE:
					timer.start();
					break;
				case IDLE:
					timer.stop();
					CC.getProgressIndicator().stop();
					break;
				}
			}
		});
		
		taskQueue.start();
		
		getClient().addChannelCountListener(eventHandler);
		getClient().addChannelInfoListener(eventHandler);
		getClient().addStreamCountListener(eventHandler);
		getClient().addVoiceCountListener(eventHandler);
		getClient().addTotalVoiceCountListener(eventHandler);
	}
	
	public static void
	cleanExit() { cleanExit(0); }
	
	public static void
	cleanExit(int i) {
		CC.getLogger().fine("CC.jsEnded");
		System.exit(i);
	}
	
	public static Client
	getClient() { return lsClient; }
	
	
	private static final SamplerModel samplerModel = new DefaultSamplerModel();
	
	/**
	 * Gets the sampler model.
	 * @return The sampler model.
	 */
	public static SamplerModel
	getSamplerModel() { return samplerModel; }
	
	public static void
	initSamplerModel() {
		final DefaultSamplerModel model = (DefaultSamplerModel)getSamplerModel();
		
		final GetServerInfo gsi = new GetServerInfo();
		gsi.addTaskListener(new TaskListener() {
			public void
			taskPerformed(TaskEvent e) {
				if(!gsi.doneWithErrors()) model.setServerInfo(gsi.getResult());
			}
		});
		
		final GetAODrivers gaod = new GetAODrivers();
		gaod.addTaskListener(new TaskListener() {
			public void
			taskPerformed(TaskEvent e) {
				if(!gaod.doneWithErrors())
					model.setAudioOutputDrivers(gaod.getResult());
			}
		});
		
		final GetEngines ge = new GetEngines();
		ge.addTaskListener(new TaskListener() {
			public void
			taskPerformed(TaskEvent e) {
				if(!ge.doneWithErrors()) model.setEngines(ge.getResult());
			}
		});
		
		final GetMIDrivers gmid = new GetMIDrivers();
		gmid.addTaskListener(new TaskListener() {
			public void
			taskPerformed(TaskEvent e) {
				if(!gmid.doneWithErrors())
					model.setMidiInputDrivers(gmid.getResult());
			}
		});
		
		final Connect cnt = new Connect();
		cnt.addTaskListener(new TaskListener() {
			public void
			taskPerformed(TaskEvent e) {
				if(cnt.doneWithErrors()) return;
				
				getTaskQueue().add(gsi);
				getTaskQueue().add(gaod);
				getTaskQueue().add(gmid);
				getTaskQueue().add(ge);
				getTaskQueue().add(new UpdateMidiDevices());
				getTaskQueue().add(new UpdateAudioDevices());
				getTaskQueue().add(new UpdateChannels());
			}
		});
		getTaskQueue().add(cnt);
	}
	
	private static class EventHandler implements ChannelCountListener, ChannelInfoListener,
				StreamCountListener, VoiceCountListener, TotalVoiceCountListener {
		
		/** Invoked when the number of channels has changed. */
		public void
		channelCountChanged( ChannelCountEvent e) {
			getTaskQueue().add(new UpdateChannels());
		}
		
		/** Invoked when changes to the sampler channel has occured. */
		public void
		channelInfoChanged(ChannelInfoEvent e) {
			/*
			 * Because of the rapid notification flow when instrument is loaded
			 * we need to do some optimization to decrease the traffic.
			 */
			boolean b = true;
			Task[] tS = getTaskQueue().getPendingTasks();
			
			for(int i = tS.length - 1; i >= 0; i--) {
				Task t = tS[i];
				
				if(t instanceof UpdateChannelInfo) {
					UpdateChannelInfo uci = (UpdateChannelInfo)t;
					if(uci.getChannelID() == e.getSamplerChannel()) return;
				} else {
					b = false;
					break;
				}
			}
			
			if(b) {
				Task t = getTaskQueue().getRunningTask();
				if(t instanceof UpdateChannelInfo) {
					UpdateChannelInfo uci = (UpdateChannelInfo)t;
					if(uci.getChannelID() == e.getSamplerChannel()) return;
				}
			}
			
			
			getTaskQueue().add(new UpdateChannelInfo(e.getSamplerChannel()));
		}
		
		/**
		 * Invoked when the number of active disk
		 * streams in a specific sampler channel has changed.
		 */
		public void
		streamCountChanged(StreamCountEvent e) {
			SamplerChannelModel scm = 
				getSamplerModel().getChannelModel(e.getSamplerChannel());
			
			if(scm == null) {
				CC.getLogger().log (
					Level.WARNING,
					"CC.unknownChannel!",
					e.getSamplerChannel()
				);
				
				return;
			}
			
			scm.setStreamCount(e.getStreamCount());
		}
		
		/**
		 * Invoked when the number of active voices
		 * in a specific sampler channel has changed.
		 */
		public void
		voiceCountChanged(VoiceCountEvent e) {
			SamplerChannelModel scm = 
				getSamplerModel().getChannelModel(e.getSamplerChannel());
			
			if(scm == null) {
				CC.getLogger().log (
					Level.WARNING,
					"CC.unknownChannel!",
					e.getSamplerChannel()
				);
				
				return;
			}
			
			scm.setVoiceCount(e.getVoiceCount());
		}
		
		/** Invoked when the total number of active voices has changed. */
		public void
		totalVoiceCountChanged(TotalVoiceCountEvent e) {
			getTaskQueue().add(new UpdateTotalVoiceCount());
		}
	}
}
