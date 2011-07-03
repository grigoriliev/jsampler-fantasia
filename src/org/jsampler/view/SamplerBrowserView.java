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
package org.jsampler.view;

import javax.swing.Icon;

/**
 *
 * @author Grigor Iliev
 */
public interface SamplerBrowserView {
	public Icon getSamplerIcon();
	public Icon getOpenIcon();
	public Icon getCloseIcon();
	public Icon getChannelLaneOpenIcon();
	public Icon getChannelLaneCloseIcon();
	public Icon getSamplerChannelIcon();
	public Icon getFxSendsOpenIcon();
	public Icon getFxSendsCloseIcon();
	public Icon getFxSendIcon();
	public Icon getDestEffectDirIcon();
	public Icon getDestEffectIcon();
	public Icon getAudioDevicesOpenIcon();
	public Icon getAudioDevicesCloseIcon();
	public Icon getAudioDeviceIcon();
	public Icon getEffectsOpenIcon();
	public Icon getEffectsCloseIcon();
	public Icon getEffectIcon();
	public Icon getEffectInstanceIcon();
	public Icon getEffectChainIcon();
	public Icon getEffectChainsOpenIcon();
	public Icon getEffectChainsCloseIcon();
	
	/** Gets the appropriate icon for the specified value, or <code>null</code>. */
	public Icon getIcon(Object value, boolean b);
}
