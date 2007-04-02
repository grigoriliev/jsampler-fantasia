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

import net.sf.juife.NavigationPage;
import net.sf.juife.NavigationPane;


/**
 *
 * @author Grigor Iliev
 */
public class LeftPane extends NavigationPane {
	private final static LeftPane leftPane = new LeftPane();
	
	private TasksPage tasksPage = new TasksPage();
	private MidiDevicesPage midiDevicesPage = new MidiDevicesPage();
	private AudioDevicesPage audioDevicesPage = new AudioDevicesPage();
	private OrchestrasPage orchestrasPage = new OrchestrasPage();
	private ManageOrchestrasPage manageOrchestrasPage = new ManageOrchestrasPage();
	private MidiInstrumentMapsPage midiInstrumentMapsPage = new MidiInstrumentMapsPage();
	
	/** Creates a new instance of LeftPane */
	private
	LeftPane() {
		NavigationPage[] pages = {
			tasksPage,
			midiDevicesPage,
			midiInstrumentMapsPage,
			audioDevicesPage,
			orchestrasPage,
			manageOrchestrasPage
		};
		
		setPages(pages);
		showTasksPage();
	}
	
	public static LeftPane
	getLeftPane() { return leftPane; }
	
	public OrchestrasPage
	getOrchestrasPage() { return orchestrasPage; }
	
	/** Shows the <code>TasksPage</code> in the left pane. */
	public void
	showTasksPage() { getModel().addPage(tasksPage); }
	
	/** Shows the <code>MidiDevicesPage</code> in the left pane. */
	public void
	showMidiDevicesPage() { getModel().addPage(midiDevicesPage); }
	
	/** Shows the <code>AudioDevicesPage</code> in the left pane. */
	public void
	showAudioDevicesPage() { getModel().addPage(audioDevicesPage); }
	
	/** Shows the <code>OrchestrasPage</code> in the left pane. */
	public void
	showOrchestrasPage() { getModel().addPage(orchestrasPage); }
	
	/** Shows the <code>ManageOrchestrasPage</code> in the left pane. */
	public void
	showManageOrchestrasPage() { getModel().addPage(manageOrchestrasPage); }
	
	/** Shows the <code>MidiInstrumentMapsPage</code> in the left pane. */
	public void
	showMidiInstrumentMapsPage() { getModel().addPage(midiInstrumentMapsPage); }
}
