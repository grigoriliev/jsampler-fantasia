/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2008 Grigor Iliev <grigor@grigoriliev.com>
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

package org.jsampler.view.fantasia;

import java.awt.event.MouseListener;

import javax.swing.JComponent;

/**
 *
 * @author Grigor Iliev
 */
public interface ChannelView {
	enum Type { SMALL, NORMAL }
	
	/** Determines the type of the view. */
	public Type getType();
	
	public JComponent getComponent();
	
	/** Configures the view. */
	public void installView();
	
	/** Reverses configuration which was done during <code>installView</code>. */
	public void uninstallView();
	
	/**
	 * Creates and installs a channel options view for this channel view.
	 * Note that this method does nothing if there is installed view already.
	 */
	public void installChannelOptionsView();
	
	/**
	 * Reverses configuration which was done during <code>installChannelOptionsView</code>.
	 */
	public void uninstallChannelOptionsView();
	
	public ChannelOptionsView getChannelOptionsView();
	
	/**
	 * Updates the channel settings. This method is invoked when changes to the
	 * channel were made.
	 */
	public void updateChannelInfo();
	
	/**
	 * Invoked when the number of active disk streams has changed.
	 * @param count The new number of active disk streams.
	 */
	public void updateStreamCount(int count);
	
	
	/**
	 * Invoked when the number of active voices has changed.
	 * @param count The new number of active voices.
	 */
	public void updateVoiceCount(int count);
	
	public void expandChannel();
	
	/** Determines whether the <code>Options</code> button is selected. */
	public boolean isOptionsButtonSelected();
	
	/**
	 * Sets whether the <code>Options</code> button should be selected or not.
	 * Note that this method does not trigger an actionEvent.
	 */
	public void setOptionsButtonSelected(boolean b);
	
	/**
	 * Registers the specified listener to listen on the component
	 * and some of its children.
	 */
	public void addEnhancedMouseListener(MouseListener l);
	
	/**  Removes the specified listener. */
	public void removeEnhancedMouseListener(MouseListener l);
	
}
