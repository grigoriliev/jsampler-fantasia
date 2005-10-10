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
	
	/** Creates a new instance of LeftPane */
	private
	LeftPane() {
		NavigationPage[] pages = {
			tasksPage,
			midiDevicesPage,
			audioDevicesPage
		};
		
		setPages(pages);
		showTasksPage();
	}
	
	public static LeftPane
	getLeftPane() { return leftPane; }
	
	/** Shows the <code>TasksPage</code> in the left pane. */
	public void
	showTasksPage() { getModel().addPage(tasksPage); }
	
	/** Shows the <code>MidiDevicesPage</code> in the left pane. */
	public void
	showMidiDevicesPage() { getModel().addPage(midiDevicesPage); }
	
	/** Shows the <code>AudioDevicesPage</code> in the left pane. */
	public void
	showAudioDevicesPage() { getModel().addPage(audioDevicesPage); }
}
