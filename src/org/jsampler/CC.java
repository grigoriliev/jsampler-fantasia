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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

import java.util.Vector;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import javax.swing.Timer;

import net.sf.juife.Task;
import net.sf.juife.TaskQueue;

import net.sf.juife.event.TaskEvent;
import net.sf.juife.event.TaskListener;
import net.sf.juife.event.TaskQueueEvent;
import net.sf.juife.event.TaskQueueListener;

import org.jsampler.event.OrchestraEvent;
import org.jsampler.event.OrchestraListEvent;
import org.jsampler.event.OrchestraListListener;
import org.jsampler.event.OrchestraListener;

import org.jsampler.task.*;

import org.jsampler.view.JSMainFrame;
import org.jsampler.view.JSProgress;

import org.linuxsampler.lscp.Client;
import org.linuxsampler.lscp.event.*;

import org.w3c.dom.Document;
import org.w3c.dom.Node;


/**
 * This class serves as a 'Control Center' of the application.
 * It also provides some fundamental routines and access to most used objects.
 * @author Grigor Iliev
 */
public class CC {
	private static Handler handler;
	private static FileOutputStream fos;
	
	private static JSMainFrame mainFrame = null;
	private static JSProgress progress = null;
	
	private final static Client lsClient = new Client();
	
	private final static TaskQueue taskQueue = new TaskQueue();
	private final static Timer timer = new Timer(2000, null);
	
	
	/**
	 * Returns the logger to be used for logging events.
	 * @return The logger to be used for logging events.
	 */
	public static Logger
	getLogger() {
		return Logger.getLogger (
			"org.jsampler",
			"org.jsampler.langprops.LogsBundle"
		);
	}
	
	/**
	 * Returns the task queue to be used for scheduling tasks
	 * for execution out of the event-dispatching thread.
	 * @return The task queue to be used for scheduling tasks
	 * for execution out of the event-dispatching thread.
	 */
	public static TaskQueue
	getTaskQueue() { return taskQueue; }
	
	/**
	 * Returns the main window of this application.
	 * @return The main window of this application.
	 */
	public static JSMainFrame
	getMainFrame() { return mainFrame; }
	
	/**
	 * Sets the main window of this application.
	 * @param mainFrame The main window of this application.
	 */
	public static void
	setMainFrame(JSMainFrame mainFrame) { CC.mainFrame = mainFrame; }
	
	/**
	 * Gets the progress indicator of this application.
	 * @return The progress indicator of this application.
	 */
	public static JSProgress
	getProgressIndicator() { return progress; }
	
	/**
	 * Sets the progress indicator to be used by this application.
	 * @param progress The progress indicator to be used by this application.
	 */
	public static void
	setProgressIndicator(JSProgress progress) { CC.progress = progress; }
	
	/**
	 * This method does the initial preparation of the application.
	 */
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
		
		taskQueue.addTaskQueueListener(getHandler());
		
		taskQueue.start();
		
		getClient().addChannelCountListener(getHandler());
		getClient().addChannelInfoListener(getHandler());
		getClient().addStreamCountListener(getHandler());
		getClient().addVoiceCountListener(getHandler());
		getClient().addTotalVoiceCountListener(getHandler());
		
		loadOrchestras();
		
		for(int i = 0; i < getOrchestras().getOrchestraCount(); i++) {
			getOrchestras().getOrchestra(i).addOrchestraListener(getHandler());
		}
		getOrchestras().addOrchestraListListener(getHandler());
	}
	
	private final static OrchestraListModel orchestras = new DefaultOrchestraListModel();
	
	/**
	 * Returns a list containing all available orchestras.
	 * @return A list containing all available orchestras.
	 */
	public static OrchestraListModel
	getOrchestras() { return orchestras; }
	
	private static void
	loadOrchestras() {
		String s = Prefs.getOrchestras();
		if(s == null) return;
		
		ByteArrayInputStream bais = new ByteArrayInputStream(s.getBytes());
		Document doc = DOMUtils.readObject(bais);
		
		try { getOrchestras().readObject(doc.getDocumentElement()); }
		catch(Exception x) { HF.showErrorMessage(x, "Loading orchestras: "); }
	}
	
	private static void
	saveOrchestras() {
		Document doc = DOMUtils.createEmptyDocument();
		
		Node node = doc.createElement("temp");
		doc.appendChild(node);
		
		getOrchestras().writeObject(doc, doc.getDocumentElement());
		
		doc.replaceChild(node.getFirstChild(), node);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DOMUtils.writeObject(doc, baos);
		Prefs.setOrchestras(baos.toString());
	}
	
	/**
	 * The exit point of the application which ensures clean exit with default exit status 0.
	 *  @see #cleanExit(int i)
	 */
	public static void
	cleanExit() { cleanExit(0); }
	
	/**
	 * The exit point of the application which ensures clean exit.
	 * @param i The exit status.
	 */
	public static void
	cleanExit(int i) {
		CC.getLogger().fine("CC.jsEnded");
		System.exit(i);
	}
	
	/**
	 * Gets the <code>Client</code> object that is used to communicate with the backend.
	 * @return The <code>Client</code> object that is used to communicate with the backend.
	 */
	public static Client
	getClient() { return lsClient; }
	
	private static final Vector<ActionListener> listeners = new Vector<ActionListener>();
	
	/**
	 * Registers the specified listener to be notified when reconnecting to LinuxSampler.
	 * @param l The <code>ActionListener</code> to register.
	 */
	public static void
	addReconnectListener(ActionListener l) { listeners.add(l); }
	
	/**
	 * Removes the specified listener.
	 * @param l The <code>ActionListener</code> to remove.
	 */
	public static void
	removeReconnectListener(ActionListener l) { listeners.remove(l); }
	
	private static void
	fireReconnectEvent() {
		ActionEvent e = new ActionEvent(CC.class, ActionEvent.ACTION_PERFORMED, null);
		for(ActionListener l : listeners) l.actionPerformed(e);
	}
	
	private static final SamplerModel samplerModel = new DefaultSamplerModel();
	
	/**
	 * Gets the sampler model.
	 * @return The sampler model.
	 */
	public static SamplerModel
	getSamplerModel() { return samplerModel; }
	
	/**
	 * Reconnects to LinuxSampler.
	 */
	public static void
	reconnect() {
		initSamplerModel();
		fireReconnectEvent();
	}
	
	/**
	 * This method updates the information about the backend state.
	 */
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
	
	private final static EventHandler eventHandler = new EventHandler();
	
	private static EventHandler
	getHandler() { return eventHandler; }
	
	private static class EventHandler implements ChannelCountListener, ChannelInfoListener,
		StreamCountListener, VoiceCountListener, TotalVoiceCountListener,
		TaskQueueListener, OrchestraListener, OrchestraListListener {
		
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
		
		/**
		 * Invoked to indicate that the state of a task queue is changed.
		 * This method is invoked only from the event-dispatching thread.
		 */
		public void
		stateChanged(TaskQueueEvent e) {
			switch(e.getEventID()) {
			case TASK_FETCHED:
				getProgressIndicator().setString (
					((Task)e.getSource()).getDescription()
				);
				break;
			case TASK_DONE:
				EnhancedTask t = (EnhancedTask)e.getSource();
				if(t.doneWithErrors() && !t.isStopped()) 
					HF.showErrorMessage(t.getErrorMessage());
				break;
			case NOT_IDLE:
				timer.start();
				break;
			case IDLE:
				timer.stop();
				getProgressIndicator().stop();
				break;
			}
		}
		
		/** Invoked when the name of orchestra is changed. */
		public void
		nameChanged(OrchestraEvent e) { saveOrchestras(); }
	
		/** Invoked when the description of orchestra is changed. */
		public void
		descriptionChanged(OrchestraEvent e) { saveOrchestras(); }
	
		/** Invoked when an instrument is added to the orchestra. */
		public void
		instrumentAdded(OrchestraEvent e) { saveOrchestras(); }
	
		/** Invoked when an instrument is removed from the orchestra. */
		public void
		instrumentRemoved(OrchestraEvent e) { saveOrchestras(); }
	
		/** Invoked when the settings of an instrument are changed. */
		public void
		instrumentChanged(OrchestraEvent e) { saveOrchestras(); }
		
		/** Invoked when an orchestra is added to the orchestra list. */
		public void
		orchestraAdded(OrchestraListEvent e) {
			e.getOrchestraModel().addOrchestraListener(getHandler());
			saveOrchestras();
		}
	
		/** Invoked when an orchestra is removed from the orchestra list. */
		public void
		orchestraRemoved(OrchestraListEvent e) {
			e.getOrchestraModel().removeOrchestraListener(getHandler());
			saveOrchestras();
		}
	}
}
