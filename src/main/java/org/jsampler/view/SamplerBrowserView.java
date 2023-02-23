/*
 *   JSampler - a front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2023 Grigor Iliev <grigor@grigoriliev.com>
 *
 *   This file is part of JSampler.
 *
 *   JSampler is free software: you can redistribute it and/or modify it under
 *   the terms of the GNU General Public License as published by the Free
 *   Software Foundation, either version 3 of the License, or (at your option)
 *   any later version.
 *
 *   JSampler is distributed in the hope that it will be useful, but WITHOUT
 *   ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *   FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 *   more details.
 *
 *   You should have received a copy of the GNU General Public License along
 *   with JSampler. If not, see <https://www.gnu.org/licenses/>.
 */
package org.jsampler.view;

/**
 *
 * @author Grigor Iliev
 */
public interface SamplerBrowserView<I> {
	public I getSamplerIcon();
	public I getOpenIcon();
	public I getCloseIcon();
	public I getChannelLaneOpenIcon();
	public I getChannelLaneCloseIcon();
	public I getSamplerChannelIcon();
	public I getFxSendsOpenIcon();
	public I getFxSendsCloseIcon();
	public I getFxSendIcon();
	public I getDestEffectDirIcon();
	public I getDestEffectIcon();
	public I getAudioDevicesOpenIcon();
	public I getAudioDevicesCloseIcon();
	public I getAudioDeviceIcon();
	public I getEffectsOpenIcon();
	public I getEffectsCloseIcon();
	public I getEffectIcon();
	public I getEffectInstanceIcon();
	public I getEffectChainIcon();
	public I getEffectChainsOpenIcon();
	public I getEffectChainsCloseIcon();
	
	/** Gets the appropriate icon for the specified value, or <code>null</code>. */
	public I getIcon(Object value, boolean b);
}
