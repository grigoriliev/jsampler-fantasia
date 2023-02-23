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

package org.jsampler.view.fantasia;

import java.awt.Font;
import java.awt.Insets;

import java.util.Properties;
import java.util.logging.Level;

import javax.swing.ImageIcon;

import org.jsampler.CC;
import org.jsampler.HF;

import org.linuxsampler.lscp.Parser;


/**
 * This class contains all pixmap resources needed by <b>Fantasia</b> view.
 * @author Grigor Iliev
 */
public class Res {
	
	/** Forbits the instantiation of this class. */
	private Res() { }
	
	public static ImageIcon gfxFantasiaLogo;
	
	public final static ImageIcon gfxPowerOn
		= new ImageIcon(Res.class.getResource("res/gfx/power_on.png"));
	
	public final static ImageIcon gfxPowerOff
		= new ImageIcon(Res.class.getResource("res/gfx/power_off.png"));

	public final static ImageIcon gfxMuteOn
		= new ImageIcon(Res.class.getResource("res/gfx/btn_mute_on.png"));
	
	public final static ImageIcon gfxMuteOff
		= new ImageIcon(Res.class.getResource("res/gfx/btn_mute_off.png"));
	
	public final static ImageIcon gfxMuteSmallOn
		= new ImageIcon(Res.class.getResource("res/gfx/btn_mute_s_on.png"));
	
	public final static ImageIcon gfxMuteSmallOff
		= new ImageIcon(Res.class.getResource("res/gfx/btn_mute_s_off.png"));
	
	public final static ImageIcon gfxMuteSoloDisabled
		= new ImageIcon(Res.class.getResource("res/gfx/btn_mute_solo_disabled.png"));
	
	public final static ImageIcon gfxMutedBySolo
		= new ImageIcon(Res.class.getResource("res/gfx/btn_mute_off.png"));
	
	public final static ImageIcon gfxMutedBySoloSmall
		= new ImageIcon(Res.class.getResource("res/gfx/btn_mute_s_off.png"));
	
	public final static ImageIcon gfxSoloOn
		= new ImageIcon(Res.class.getResource("res/gfx/btn_solo_on.png"));
	
	public final static ImageIcon gfxSoloOff
		= new ImageIcon(Res.class.getResource("res/gfx/btn_mute_off.png"));
	
	public final static ImageIcon gfxSoloSmallOn
		= new ImageIcon(Res.class.getResource("res/gfx/btn_solo_s_on.png"));
	
	public final static ImageIcon gfxSoloSmallOff
		= new ImageIcon(Res.class.getResource("res/gfx/btn_solo_s_off.png"));
	
	public final static ImageIcon gfxMuteTitle
		= new ImageIcon(Res.class.getResource("res/gfx/title_mute.png"));
	
	public final static ImageIcon gfxSoloTitle
		= new ImageIcon(Res.class.getResource("res/gfx/title_solo.png"));
	
	public final static ImageIcon gfxVolumeTitle
		= new ImageIcon(Res.class.getResource("res/gfx/title_volume.png"));
	
	public final static ImageIcon gfxVolumeDial
		= new ImageIcon(Res.class.getResource("res/gfx/knob_volume.png"));
	
	public final static ImageIcon gfxOptionsTitle
		= new ImageIcon(Res.class.getResource("res/gfx/title_options.png"));
	
	public final static ImageIcon gfxOptionsOn
		= new ImageIcon(Res.class.getResource("res/gfx/btn_hide_channel_options.png"));
	
	public final static ImageIcon gfxOptionsOnRO
		= new ImageIcon(Res.class.getResource("res/gfx/btn_hide_channel_options_ro.png"));
	
	public final static ImageIcon gfxOptionsOff
		= new ImageIcon(Res.class.getResource("res/gfx/btn_show_channel_options.png"));
	
	public final static ImageIcon gfxOptionsOffRO
		= new ImageIcon(Res.class.getResource("res/gfx/btn_show_channel_options_ro.png"));
	
	public final static ImageIcon gfxFx
		= new ImageIcon(Res.class.getResource("res/gfx/btn_fx.png"));
	
	public final static ImageIcon gfxFxRO
		= new ImageIcon(Res.class.getResource("res/gfx/btn_fx_ro.png"));
	
	public final static ImageIcon gfxMidiInputTitle
		= new ImageIcon(Res.class.getResource("res/gfx/title_midi_input.png"));
	
	public final static ImageIcon gfxEngineTitle
		= new ImageIcon(Res.class.getResource("res/gfx/title_engine.png"));
	
	public final static ImageIcon gfxAudioOutputTitle
		= new ImageIcon(Res.class.getResource("res/gfx/title_audio_output.png"));
	
	public final static ImageIcon gfxInstrumentMapTitle
		= new ImageIcon(Res.class.getResource("res/gfx/title_midi_instrument_map.png"));
	
	public final static ImageIcon gfxChannel
		= new ImageIcon(Res.class.getResource("res/gfx/channel.png"));
	
	public final static ImageIcon gfxChannelScreen
		= new ImageIcon(Res.class.getResource("res/gfx/channel.screen.png"));
	
	public final static ImageIcon gfxHLine
		= new ImageIcon(Res.class.getResource("res/gfx/line_hor.png"));
	
	public final static ImageIcon gfxVLine
		= new ImageIcon(Res.class.getResource("res/gfx/line_vert.png"));
	
	public final static ImageIcon gfxChannelOptions
		= new ImageIcon(Res.class.getResource("res/gfx/channel.options.png"));
	
	public final static ImageIcon gfxCreateChannel
		= new ImageIcon(Res.class.getResource("res/gfx/create_channel.png"));
	
	public final static ImageIcon gfxTextField
		= new ImageIcon(Res.class.getResource("res/gfx/tf_bg.png"));
	
	public final static ImageIcon gfxCbLabelBg
		= new ImageIcon(Res.class.getResource("res/gfx/cb_label_bg.png"));
	
	public final static ImageIcon gfxCbArrow
		= new ImageIcon(Res.class.getResource("res/gfx/cb_arrow.png"));
	
	public final static ImageIcon gfxCbArrowDisabled
		= new ImageIcon(Res.class.getResource("res/gfx/cb_arrow_disabled.png"));
	
	public final static ImageIcon gfxCbArrowRO
		= new ImageIcon(Res.class.getResource("res/gfx/cb_arrow_ro.png"));
	
	public final static ImageIcon gfxPowerOn18
		= new ImageIcon(Res.class.getResource("res/gfx/power_on18.png"));
	
	public final static ImageIcon gfxPowerOff18
		= new ImageIcon(Res.class.getResource("res/gfx/power_off18.png"));
	
	public final static ImageIcon gfxDeviceBg
		= new ImageIcon(Res.class.getResource("res/gfx/device_bg.png"));
	
	public final static ImageIcon gfxRoundBg14
		= new ImageIcon(Res.class.getResource("res/gfx/round_bg14.png"));
	public final static ImageIcon gfxRoundBg7
		= new ImageIcon(Res.class.getResource("res/gfx/round_bg7.png"));
	public final static ImageIcon gfxMenuBarBg
		= new ImageIcon(Res.class.getResource("res/gfx/menubar_bg.png"));
	
	public final static ImageIcon gfxScreenBtnBg
		= new ImageIcon(Res.class.getResource("res/gfx/screen_btn_bg.png"));
	
	
	public final static ImageIcon gfxChannelsBg
		= new ImageIcon(Res.class.getResource("res/gfx/channels_bg.png"));
	
	public static ImageIcon gfxToolBar;
	public static Insets insetsToolBar;
	
	public final static ImageIcon gfxBorder
		= new ImageIcon(Res.class.getResource("res/gfx/border.png"));
	
	public final static ImageIcon gfxBtnCr
		= new ImageIcon(Res.class.getResource("res/gfx/btn_cr.png"));
	
	public final static ImageIcon gfxBtnCrRO
		= new ImageIcon(Res.class.getResource("res/gfx/btn_cr_ro.png"));
	
	public final static ImageIcon gfxBtnDecrease
		= new ImageIcon(Res.class.getResource("res/gfx/btn_decrease.png"));
	
	public final static ImageIcon gfxBtnDecreaseRO
		= new ImageIcon(Res.class.getResource("res/gfx/btn_decrease_ro.png"));
	
	public final static ImageIcon gfxBtnIncrease
		= new ImageIcon(Res.class.getResource("res/gfx/btn_increase.png"));
	
	public final static ImageIcon gfxBtnIncreaseRO
		= new ImageIcon(Res.class.getResource("res/gfx/btn_increase_ro.png"));
	
	public final static ImageIcon gfxBtnScrollLeft
		= new ImageIcon(Res.class.getResource("res/gfx/btn_scroll_left.png"));
	
	public final static ImageIcon gfxBtnScrollLeftRO
		= new ImageIcon(Res.class.getResource("res/gfx/btn_scroll_left_ro.png"));
	
	public final static ImageIcon gfxBtnScrollRight
		= new ImageIcon(Res.class.getResource("res/gfx/btn_scroll_right.png"));
	
	public final static ImageIcon gfxBtnScrollRightRO
		= new ImageIcon(Res.class.getResource("res/gfx/btn_scroll_right_ro.png"));
	
	
	public final static ImageIcon iconAppIcon
		= new ImageIcon(Res.class.getResource("res/icons/app_icon.png"));
	
	public final static ImageIcon iconArrowUp
		= new ImageIcon(Res.class.getResource("res/icons/arrow_up.png"));
	
	public final static ImageIcon iconArrowDown
		= new ImageIcon(Res.class.getResource("res/icons/arrow_down.png"));
	
	public final static ImageIcon iconEngine12
		= new ImageIcon(Res.class.getResource("res/icons/engine12.png"));
	
	public final static ImageIcon iconVolume14
		= new ImageIcon(Res.class.getResource("res/icons/volume14.png"));
	
	public final static ImageIcon iconNew16
		= new ImageIcon(Res.class.getResource("res/icons/new16.png"));
	
	public final static ImageIcon iconEdit16
		= new ImageIcon(Res.class.getResource("res/icons/edit16.png"));
	
	public final static ImageIcon iconDelete16
		= new ImageIcon(Res.class.getResource("res/icons/delete16.png"));
	
	public final static ImageIcon iconBack16
		= new ImageIcon(Res.class.getResource("res/icons/back16.png"));
	
	public final static ImageIcon iconNext16
		= new ImageIcon(Res.class.getResource("res/icons/next16.png"));
	
	public final static ImageIcon iconUp16
		= new ImageIcon(Res.class.getResource("res/icons/up16.png"));
	
	public final static ImageIcon iconBrowse16
		= new ImageIcon(Res.class.getResource("res/icons/folder_open16.png"));
	
	public final static ImageIcon iconDb16
		= new ImageIcon(Res.class.getResource("res/icons/collection16.png"));
	
	public final static ImageIcon iconFolder16
		= new ImageIcon(Res.class.getResource("res/icons/folder16.png"));
	
	public final static ImageIcon iconFolderOpen16
		= new ImageIcon(Res.class.getResource("res/icons/folder_open16.png"));
	
	public final static ImageIcon iconInstrument16
		= new ImageIcon(Res.class.getResource("res/icons/instr16.png"));
	
	public final static ImageIcon iconReload16
		= new ImageIcon(Res.class.getResource("res/icons/reload16.png"));
	
	public final static ImageIcon iconPreferences16
		= new ImageIcon(Res.class.getResource("res/icons/preferences16.png"));
	
	public final static ImageIcon iconSampler16
		= new ImageIcon(Res.class.getResource("res/icons/sampler16.png"));
	
	public final static ImageIcon iconSamplerChannel16
		= new ImageIcon(Res.class.getResource("res/icons/schannel16.png"));
	
	public final static ImageIcon iconFxSend16
		= new ImageIcon(Res.class.getResource("res/icons/fx-send16.png"));
	
	public final static ImageIcon iconDestEffect16
		= new ImageIcon(Res.class.getResource("res/icons/dest-effect16.png"));
	
	public final static ImageIcon iconAudioDevsOpen16
		= new ImageIcon(Res.class.getResource("res/icons/au-devs-folder_open16.png"));
	
	public final static ImageIcon iconAudioDevsClose16
		= new ImageIcon(Res.class.getResource("res/icons/au-devs-folder16.png"));
	
	public final static ImageIcon iconAudioDev16
		= new ImageIcon(Res.class.getResource("res/icons/au-dev16.png"));
	
	public final static ImageIcon iconEffectsOpen16
		= new ImageIcon(Res.class.getResource("res/icons/effects-folder_open16.png"));
	
	public final static ImageIcon iconEffectsClose16
		= new ImageIcon(Res.class.getResource("res/icons/effects-folder16.png"));
	
	public final static ImageIcon iconEffect16
		= new ImageIcon(Res.class.getResource("res/icons/effect16.png"));
	
	public final static ImageIcon iconEffectInstance16
		= new ImageIcon(Res.class.getResource("res/icons/effect-instance16.png"));
	
	public final static ImageIcon iconEffectInstanceLnk16
		= new ImageIcon(Res.class.getResource("res/icons/effect-instance-lnk16.png"));
	
	public final static ImageIcon iconEffectChain16
		= new ImageIcon(Res.class.getResource("res/icons/effect-chain16.png"));
	
	public final static ImageIcon iconEffectChainsOpen16
		= new ImageIcon(Res.class.getResource("res/icons/chains-folder_open16.png"));
	
	public final static ImageIcon iconEffectChainsClose16
		= new ImageIcon(Res.class.getResource("res/icons/chains-folder16.png"));
	
	public final static ImageIcon iconVolume22
		= new ImageIcon(Res.class.getResource("res/icons/volume22.png"));
	
	public final static ImageIcon iconFind22
		= new ImageIcon(Res.class.getResource("res/icons/Find22.png"));
	
	public final static ImageIcon iconGoUp22
		= new ImageIcon(Res.class.getResource("res/icons/GoUp22.png"));
	
	public final static ImageIcon iconGoBack22
		= new ImageIcon(Res.class.getResource("res/icons/GoBack22.png"));
	
	public final static ImageIcon iconGoForward22
		= new ImageIcon(Res.class.getResource("res/icons/GoForward22.png"));
	
	public final static ImageIcon iconFolderOpen22
		= new ImageIcon(Res.class.getResource("res/icons/folder_open22.png"));
	
	public final static ImageIcon iconPreferences22
		= new ImageIcon(Res.class.getResource("res/icons/Preferences22.png"));
	
	public final static ImageIcon iconReload22
		= new ImageIcon(Res.class.getResource("res/icons/reload22.png"));
	
	public final static ImageIcon iconSamplerInfo32
		= new ImageIcon(Res.class.getResource("res/icons/sampler_info32.png"));
	
	public final static ImageIcon iconOpen32
		= new ImageIcon(Res.class.getResource("res/icons/open32.png"));
	
	public final static ImageIcon iconSave32
		= new ImageIcon(Res.class.getResource("res/icons/save32.png"));
	
	public final static ImageIcon iconReload32
		= new ImageIcon(Res.class.getResource("res/icons/reload32.png"));
	
	public final static ImageIcon iconReset32
		= new ImageIcon(Res.class.getResource("res/icons/purge32.png"));
	
	public final static ImageIcon iconPreferences32
		= new ImageIcon(Res.class.getResource("res/icons/preferences32.png"));
	
	public final static ImageIcon iconMidiKeyboard32
		= new ImageIcon(Res.class.getResource("res/icons/midi_keyboard32.png"));
	
	public final static ImageIcon iconLSConsole32
		= new ImageIcon(Res.class.getResource("res/icons/ls_console32.png"));
	
	public final static ImageIcon iconLSConsole
		= new ImageIcon(Res.class.getResource("res/icons/ls_console.png"));
	
	public final static ImageIcon iconDb32
		= new ImageIcon(Res.class.getResource("res/icons/db32.png"));
	
	public final static ImageIcon iconWarning32
		= new ImageIcon(Res.class.getResource("res/icons/warning32.png"));
	
	public final static ImageIcon iconQuestion32
		= new ImageIcon(Res.class.getResource("res/icons/question32.png"));
	
	public final static ImageIcon iconLinuxSamplerLogo
		= new ImageIcon(Res.class.getResource("res/icons/LinuxSampler-logo.png"));
	
	public static Font fontScreen = null;
	public static Font fontScreenMono = null;
		
	
	static {
		try {
			fontScreen = Font.createFont (
				Font.TRUETYPE_FONT,
				Res.class.getResourceAsStream("res/fonts/DejaVuLGCCondensedSansBold.ttf")
			);
			fontScreen = fontScreen.deriveFont(10.0f);
			
			fontScreenMono = Font.createFont (
				Font.TRUETYPE_FONT,
				Res.class.getResourceAsStream("res/fonts/DejaVuLGCMonoSansBold.ttf")
			);
			fontScreenMono = fontScreenMono.deriveFont(10.0f);
		} catch(Exception e) {
			CC.getLogger().warning(HF.getErrorMessage(e));
		}
	}
	
	protected static void
	loadTheme(String themeName) {
		try {
			Properties p = new Properties();
			p.load(Res.class.getResourceAsStream("res/themes/themes.properties"));
			
			String path = p.getProperty(themeName);
			if(path == null) {
				String s = "Failed to load theme " + themeName;
				s += "! Falling back to the default theme...";
				CC.getLogger().warning(s);
				path = p.getProperty("Graphite");
				if(path == null) {
					CC.getLogger().warning("Failed to load the default theme!");
					CC.cleanExit();
					return;
				}
			}
			
			path = "res/" + path;
			if(path.charAt(path.length() - 1) != '/') path += "/";
			path += "theme.properties";
			p.load(Res.class.getResourceAsStream(path));
			
			String s = "res/" + p.getProperty("FantasiaLogo.gfx");
			gfxFantasiaLogo = new ImageIcon(Res.class.getResource(s));
			
			s = "res/" + p.getProperty("StandardBar.gfx");
			gfxToolBar = new ImageIcon(Res.class.getResource(s));
			
			insetsToolBar = parseInsets(p.getProperty("StandardBar.insets"));
		} catch(Exception e) {
			CC.getLogger().log(Level.INFO, "Failed to load theme " + themeName, e);
			CC.cleanExit();
		}
	}
	
	private static Insets
	parseInsets(String s) {
		Insets i = new Insets(0, 0, 0, 0);
		try {
			Integer[] list = Parser.parseIntList(s);
			if(list.length != 4) throw new Exception();
			i.set(list[0], list[1], list[2], list[3]);
		} catch(Exception x) {
			CC.getLogger().warning("Failed to parse insets: " + s);
		}
		
		return i;
	}
}
