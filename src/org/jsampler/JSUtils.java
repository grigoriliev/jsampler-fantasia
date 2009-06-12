/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2009 Grigor Iliev <grigor@grigoriliev.com>
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

import java.io.ByteArrayOutputStream;

import java.io.File;
import java.text.DateFormat;
import java.util.logging.Level;
import java.util.zip.GZIPOutputStream;

import org.jsampler.view.JSChannel;
import org.jsampler.view.JSChannelsPane;

import org.linuxsampler.lscp.AudioOutputChannel;
import org.linuxsampler.lscp.AudioOutputDevice;
import org.linuxsampler.lscp.Client;
import org.linuxsampler.lscp.FxSend;
import org.linuxsampler.lscp.MidiInputDevice;
import org.linuxsampler.lscp.MidiPort;
import org.linuxsampler.lscp.Parameter;
import org.linuxsampler.lscp.SamplerChannel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static org.jsampler.CC.preferences;
import static org.jsampler.JSI18n.i18n;

/**
 *
 * @author Grigor Iliev
 */
public class JSUtils {

	/** Forbids the instantiation of this class */
	private
	JSUtils() { }

	/**
	 * Checks whether the JSampler home directory is specified and exist.
	 * If the JSampler home directory is not specifed, or is specified
	 * but doesn't exist, a procedure of specifying a JSampler home
	 * directory is initiated.
	 * @see org.jsampler.view.JSMainFrame#installJSamplerHome
	 */
	public static void
	checkJSamplerHome() {
		if(CC.getJSamplerHome() != null) {
			File f = new File(CC.getJSamplerHome());
			if(f.exists() && f.isDirectory()) {
				return;
			}
		}

		CC.getMainFrame().installJSamplerHome();
	}

	/**
	 * Changes the JSampler's home directory and moves all files from
	 * the old JSampler's home directory to the new one. If all files are
	 * moved succesfully, the old directory is deleted.
	 * @param path The location of the new JSampler's home directory. If
	 * the last directory in the path doesn't exist, it is created.
	 */
	public static void
	changeJSamplerHome(String path) {
		File fNew = new File(path);
		if(fNew.exists() && fNew.isFile()) {
			HF.showErrorMessage(i18n.getError("CC.JSamplerHomeIsNotDir!"));
			return;
		}

		if(!fNew.exists()) {
			if(!fNew.mkdir()) {
				String s = fNew.getAbsolutePath();
				HF.showErrorMessage(i18n.getError("CC.mkdirFailed", s));
				return;
			}
		}

		if(CC.getJSamplerHome() == null || path.equals(CC.getJSamplerHome())) {
			CC.setJSamplerHome(fNew.getAbsolutePath());
			return;
		}

		File fOld = new File(CC.getJSamplerHome());
		if(!fOld.exists() || !fOld.isDirectory()) {
			CC.setJSamplerHome(fNew.getAbsolutePath());
			return;
		}

		File[] files = fOld.listFiles();
		boolean b = true;
		if(files != null) {
			String s = fNew.getAbsolutePath() + File.separator;
			for(File f : files) if(!f.renameTo(new File(s + f.getName()))) b = false;
		}

		if(b) fOld.delete();
		CC.setJSamplerHome(fNew.getAbsolutePath());
	}

	public static String
	exportInstrMapsToLscpScript() {
		StringBuffer sb = new StringBuffer("# Exported by: ");
		sb.append("JSampler - a java front-end for LinuxSampler\r\n# Version: ");
		sb.append(JSampler.VERSION).append("\r\n");
		sb.append("# Date: ").append(new java.util.Date().toString()).append("\r\n\r\n");

		Client lscpClient = new Client(true);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		lscpClient.setPrintOnlyModeOutputStream(out);

		exportInstrMapsToLscpScript(lscpClient);
		sb.append(out.toString());
		out.reset();

		return sb.toString();
	}

	private static void
	exportInstrMapsToLscpScript(Client lscpClient) {
		try {
			lscpClient.removeAllMidiInstrumentMaps();
			MidiInstrumentMap[] maps = CC.getSamplerModel().getMidiInstrumentMaps();
			for(int i = 0; i < maps.length; i++) {
				lscpClient.addMidiInstrumentMap(maps[i].getName());
				exportInstrumentsToLscpScript(i, maps[i], lscpClient);
			}
		} catch(Exception e) {
			CC.getLogger().log(Level.FINE, HF.getErrorMessage(e), e);
			HF.showErrorMessage(e);
		}
	}

	private static void
	exportInstrumentsToLscpScript(int mapId, MidiInstrumentMap map, Client lscpClient)
										throws Exception {

		boolean b = preferences().getBoolProperty(JSPrefs.LOAD_MIDI_INSTRUMENTS_IN_BACKGROUND);

		for(MidiInstrument i : map.getAllMidiInstruments()) {
			lscpClient.mapMidiInstrument(mapId, i.getInfo().getEntry(), i.getInfo(), b);
		}
	}

	public static String
	exportInstrMapsToText() {
		String nl = System.getProperty("line.separator");
		StringBuffer sb = new StringBuffer();


		MidiInstrumentMap[] maps = CC.getSamplerModel().getMidiInstrumentMaps();
		for(int i = 0; i < maps.length; i++) {
			sb.append("MIDI Instrument Map: ");
			sb.append(maps[i].getName()).append(nl);
			exportInstrumentsToText(maps[i], sb);
		}

		String date = DateFormat.getDateInstance().format(new java.util.Date());
		sb.append("Date: ").append(date).append(nl);
		sb.append("Exported by: JSampler - a java front-end for LinuxSampler, Version ");
		sb.append(JSampler.VERSION).append(nl);

		return sb.toString();
	}

	private static void
	exportInstrumentsToText(MidiInstrumentMap map, StringBuffer sb) {
		int bank = -1;
		String nl = System.getProperty("line.separator");
		int bnkOffset = preferences().getIntProperty(JSPrefs.FIRST_MIDI_BANK_NUMBER);
		int prgOffset = preferences().getIntProperty(JSPrefs.FIRST_MIDI_PROGRAM_NUMBER);

		for(MidiInstrument i : map.getAllMidiInstruments()) {
			int newBank = i.getInfo().getMidiBank();
			if(newBank != bank) {
				bank = newBank;
				sb.append(nl).append("\tMIDI Bank ");
				sb.append(bank + bnkOffset).append(nl);
			}
			sb.append("\t[").append(bank + bnkOffset).append("] ");
			sb.append(i.getInfo().getMidiProgram() + prgOffset);
			sb.append(" - ").append(i.getName()).append(nl);
		}

		sb.append(nl);
	}

	public static String
	exportInstrMapsToHtml() {
		String nl = System.getProperty("line.separator");
		StringBuffer sb = new StringBuffer("<html>").append(nl);

		sb.append("<head><title>MIDI Instrument Maps</title></head>");
		
		sb.append("<body>").append(nl);
		sb.append("<h1>MIDI Instrument Maps</h1>").append(nl);

		String date = DateFormat.getDateInstance().format(new java.util.Date());
		sb.append("Date: ").append(date).append("<br>").append(nl);
		sb.append("Exported by <a href=http://linuxsampler.org/jsampler/manual/html/jsampler.html>");
		sb.append("JSampler</a> version ");
		sb.append(JSampler.VERSION).append("<br>").append(nl);

		MidiInstrumentMap[] maps = CC.getSamplerModel().getMidiInstrumentMaps();

		sb.append("<ol>").append(nl);
		for(int i = 0; i < maps.length; i++) {
			String name = toHtmlEscapedText(maps[i].getName());
			sb.append("<li><a href=#map-").append(i + 1).append(">");
			sb.append(name).append("</a></li>").append(nl);
		}
		sb.append("</ol>").append(nl);

		for(int i = 0; i < maps.length; i++) {
			String s = toHtmlEscapedText(maps[i].getName());
			sb.append("<h2><a name=map-").append(i + 1).append(">");
			sb.append(s).append("</a></h2>").append(nl);
			exportInstrumentsToHtml(i, maps[i], sb);
		}

		sb.append(nl).append("</body>").append(nl).append("</html>");
		return sb.toString();
	}

	private static void
	exportInstrumentsToHtml(int mapId, MidiInstrumentMap map, StringBuffer sb) {
		int bank = -1;
		String nl = System.getProperty("line.separator");
		int bnkOffset = preferences().getIntProperty(JSPrefs.FIRST_MIDI_BANK_NUMBER);
		int prgOffset = preferences().getIntProperty(JSPrefs.FIRST_MIDI_PROGRAM_NUMBER);

		sb.append("<ol>").append(nl);
		for(MidiInstrument i : map.getAllMidiInstruments()) {
			int newBank = i.getInfo().getMidiBank();
			if(newBank != bank) {
				bank = newBank;
				String s = "map-" + (mapId + 1) + "-bank-" + (bank + bnkOffset);
				sb.append(nl).append("<li><a href=#").append(s);
				sb.append(">MIDI Bank ");
				sb.append(bank + bnkOffset).append("</a></li>").append(nl);
			}
		}
		sb.append("</ol>").append(nl);

		bank = -1;
		String bankName = "";

		sb.append("<table border=0>").append(nl);
		for(MidiInstrument i : map.getAllMidiInstruments()) {
			int newBank = i.getInfo().getMidiBank();
			if(newBank != bank) {
				bank = newBank;
				sb.append("</table>").append(nl);

				bankName = "map-" + (mapId + 1) + "-bank-" + (bank + bnkOffset);
				sb.append(nl).append("<h4><a name=").append(bankName);
				sb.append(">MIDI Bank ");
				sb.append(bank + bnkOffset).append("</a></h4>").append(nl);

				sb.append("<table border=0>").append(nl);
			}

			sb.append("<tr><td align='right'>");
			sb.append(i.getInfo().getMidiProgram() + prgOffset).append(" - </td>");
			String file = i.getInfo().getFilePath();
			String tooltip = "File: " + file + ", Index: " + i.getInfo().getInstrumentIndex();
			sb.append("<td><a title='").append(tooltip).append("'>");
			String s = toHtmlEscapedText(i.getName());
			sb.append(s).append("</a></td>");

			sb.append("<td>&nbsp;&nbsp;<a href=#").append(bankName).append(">");
			sb.append("[").append(bank + bnkOffset).append("]</a></td>").append(nl);
			sb.append("</tr>");
		}
		sb.append("</table>").append(nl);

		sb.append(nl);
	}

	private static String
	toHtmlEscapedText(String s) {
		s = s.replaceAll("&", "&amp;");
		s = s.replaceAll("<", "&lt;");
		s = s.replaceAll(">", "&gt;");

		return s;
	}

	public static byte[]
	exportInstrMapsToRGD() {
		Document doc = DOMUtils.createEmptyDocument();

		Element rgd = doc.createElement("rosegarden-data");
		rgd.setAttribute("version", "1.7.2");
		doc.appendChild(rgd);

		Element studio = doc.createElement("studio");
		studio.setAttribute("thrufilter", "0");
		studio.setAttribute("recordfilter", "0");
		rgd.appendChild(studio);

		MidiInstrumentMap[] maps = CC.getSamplerModel().getMidiInstrumentMaps();
		for(int i = 0; i < maps.length; i++) {
			Element dev = doc.createElement("device");
			dev.setAttribute("id", String.valueOf(i));
			dev.setAttribute("name", "LinuxSampler: " + maps[i].getName());
			dev.setAttribute("type", "midi");
			studio.appendChild(dev);

			Element el = doc.createElement("librarian");
			el.setAttribute("name", "Grigor Iliev");
			el.setAttribute("email", "grigor@grigoriliev.com");
			dev.appendChild(el);

			exportInstrumentsToRGD(maps[i], dev);
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DOMUtils.writeObject(doc, baos);

		// Hack to insert the file name in the archive
		byte[] data2 = null;
		try {
			ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
			GZIPOutputStream gzos = new GZIPOutputStream(baos2);
			gzos.write(baos.toByteArray());
			gzos.finish();
			byte[] data = baos2.toByteArray();
			data[3] = 8; // File name
			byte[] fn = "x-rosegarden-device".getBytes("US-ASCII");
			int fnsize = fn.length;
			data2 = new byte[data.length + fnsize + 1];
			
			for(int i = 0; i < 10; i++) data2[i] = data[i];
			for(int i = 0; i < fnsize; i++) data2[i + 10] = fn[i];
			data2[10 + fnsize] = 0;
			for(int i = 10; i < data.length; i++) data2[i + fnsize + 1] = data[i];
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		//////////////

		return data2;
	}

	private static void
	exportInstrumentsToRGD(MidiInstrumentMap map, Element el) {
		int bank = -1;
		int bnkOffset = preferences().getIntProperty(JSPrefs.FIRST_MIDI_BANK_NUMBER);
		Element elBank = null;

		for(MidiInstrument i : map.getAllMidiInstruments()) {
			int newBank = i.getInfo().getMidiBank();
			if(newBank != bank) {
				bank = newBank;
				elBank = el.getOwnerDocument().createElement("bank");
				elBank.setAttribute("name", "Bank " + (bank + bnkOffset));
				elBank.setAttribute("msb", String.valueOf((bank >> 7) & 0x7f));
				elBank.setAttribute("lsb", String.valueOf(bank & 0x7f));
				el.appendChild(elBank);
			}

			Element elProgram = el.getOwnerDocument().createElement("program");
			elProgram.setAttribute("id", String.valueOf(i.getInfo().getMidiProgram()));
			elProgram.setAttribute("name", i.getName());

			elBank.appendChild(elProgram);
		}
	}

	public static String
	exportSessionToLscpScript() {
		CC.getSamplerModel().setModified(false);

		StringBuffer sb = new StringBuffer("# Exported by: ");
		sb.append("JSampler - a java front-end for LinuxSampler\r\n# Version: ");
		sb.append(JSampler.VERSION).append("\r\n");
		sb.append("# Date: ").append(new java.util.Date().toString()).append("\r\n\r\n");

		Client lscpClient = new Client(true);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		lscpClient.setPrintOnlyModeOutputStream(out);

		try {
			lscpClient.resetSampler();
			sb.append(out.toString());
			out.reset();
			sb.append("\r\n");
			lscpClient.setVolume(CC.getSamplerModel().getVolume());
			sb.append(out.toString());
			out.reset();
			sb.append("\r\n");
		} catch(Exception e) { CC.getLogger().log(Level.FINE, HF.getErrorMessage(e), e); }

		MidiDeviceModel[] mDevs = CC.getSamplerModel().getMidiDevices();
		for(int i = 0; i < mDevs.length; i++) {
			exportMidiDeviceToLscpScript(mDevs[i].getDeviceInfo(), i, lscpClient);
			sb.append(out.toString());
			out.reset();
			sb.append("\r\n");
		}

		AudioDeviceModel[] aDevs = CC.getSamplerModel().getAudioDevices();
		for(int i = 0; i < aDevs.length; i++) {
			exportAudioDeviceToLscpScript(aDevs[i].getDeviceInfo(), i, lscpClient);
			sb.append(out.toString());
			out.reset();
			sb.append("\r\n");
		}

		boolean b = preferences().getBoolProperty(JSPrefs.EXPORT_MIDI_MAPS_TO_SESSION_SCRIPT);
		if(b) {
			exportInstrMapsToLscpScript(lscpClient);
			sb.append(out.toString());
			out.reset();
			sb.append("\r\n");
		}

		int chnId = 0;
		for(JSChannelsPane cp : CC.getMainFrame().getChannelsPaneList()) {
			for(JSChannel chn : cp.getChannels()) {
				SamplerChannelModel scm;
				scm = CC.getSamplerModel().getChannelById(chn.getChannelId());
				exportChannelToLscpScript(scm.getChannelInfo(), chnId, lscpClient);
				sb.append(out.toString());
				out.reset();

				sb.append("\r\n");

				exportFxSendsToLscpScript(scm, chnId, lscpClient);
				sb.append(out.toString());
				out.reset();

				sb.append("\r\n");

				chnId++;
			}
		}

		sb.append(CC.getViewConfig().exportSessionViewConfig());

		return sb.toString();
	}

	private static void
	exportMidiDeviceToLscpScript(MidiInputDevice mid, int devId, Client lscpCLient) {
		try {
			String s = mid.getDriverName();
			lscpCLient.createMidiInputDevice(s, mid.getAdditionalParameters());

			MidiPort[] mPorts = mid.getMidiPorts();
			int l = mPorts.length;
			if(l != 1) lscpCLient.setMidiInputPortCount(devId, l);

			for(int i = 0; i < l; i++) {
				Parameter[] prms = mPorts[i].getAllParameters();
				for(Parameter p : prms) {
					if(!p.isFixed() && p.getStringValue().length() > 0)
						lscpCLient.setMidiInputPortParameter(devId, i, p);
				}
			}
		} catch(Exception e) {
			CC.getLogger().log(Level.FINE, HF.getErrorMessage(e), e);
		}
	}

	private static void
	exportAudioDeviceToLscpScript(AudioOutputDevice aod, int devId, Client lscpCLient) {
		try {
			String s = aod.getDriverName();
			lscpCLient.createAudioOutputDevice(s, aod.getAllParameters());

			AudioOutputChannel[] chns = aod.getAudioChannels();

			for(int i = 0; i < chns.length; i++) {
				Parameter[] prms = chns[i].getAllParameters();
				for(Parameter p : prms) {
					if(p.isFixed() || p.getStringValue().length() == 0);
					else lscpCLient.setAudioOutputChannelParameter(devId, i, p);
				}
			}
		} catch(Exception e) {
			CC.getLogger().log(Level.FINE, HF.getErrorMessage(e), e);
		}
	}

	private static void
	exportChannelToLscpScript(SamplerChannel chn, int chnId, Client lscpCLient) {
		try {
			lscpCLient.addSamplerChannel();

			SamplerModel sm = CC.getSamplerModel();
			int id = chn.getMidiInputDevice();
			if(id != -1) {
				for(int i = 0; i < sm.getMidiDeviceCount(); i++) {
					if(sm.getMidiDevice(i).getDeviceId() == id) {
						lscpCLient.setChannelMidiInputDevice(chnId, i);
						break;
					}
				}
				lscpCLient.setChannelMidiInputPort(chnId, chn.getMidiInputPort());
				lscpCLient.setChannelMidiInputChannel(chnId, chn.getMidiInputChannel());
			}

			if(chn.getEngine() != null) {
				lscpCLient.loadSamplerEngine(chn.getEngine().getName(), chnId);
				lscpCLient.setChannelVolume(chnId, chn.getVolume());
				int mapId = chn.getMidiInstrumentMapId();
				lscpCLient.setChannelMidiInstrumentMap(chnId, mapId);
			}

			id = chn.getAudioOutputDevice();
			if(id != -1) {
				for(int i = 0; i < sm.getAudioDeviceCount(); i++) {
					if(sm.getAudioDevice(i).getDeviceId() == id) {
						lscpCLient.setChannelAudioOutputDevice(chnId, i);
						break;
					}
				}

				Integer[] routing = chn.getAudioOutputRouting();

				for(int j = 0; j < routing.length; j++) {
					int k = routing[j];
					if(k == j) continue;

					lscpCLient.setChannelAudioOutputChannel(chnId, j, k);
				}
			}

			String s = chn.getInstrumentFile();
			int i = chn.getInstrumentIndex();
			if(s != null) lscpCLient.loadInstrument(s, i, chnId, true);

			if(chn.isMuted() && !chn.isMutedBySolo()) lscpCLient.setChannelMute(chnId, true);
			if(chn.isSoloChannel()) lscpCLient.setChannelSolo(chnId, true);
		} catch(Exception e) {
			CC.getLogger().log(Level.FINE, HF.getErrorMessage(e), e);
		}
	}

	private static void
	exportFxSendsToLscpScript(SamplerChannelModel scm, int chnId, Client lscpClient) {
		try {
			FxSend[] fxSends = scm.getFxSends();

			for(int i = 0; i < fxSends.length; i++) {
				FxSend f = fxSends[i];
				lscpClient.createFxSend(chnId, f.getMidiController(), f.getName());
				lscpClient.setFxSendLevel(chnId, i, f.getLevel());

				Integer[] r = f.getAudioOutputRouting();
				for(int j = 0; j < r.length; j++) {
					lscpClient.setFxSendAudioOutputChannel(chnId, i, j, r[j]);
				}
			}
		} catch(Exception e) {
			CC.getLogger().log(Level.FINE, HF.getErrorMessage(e), e);
		}
	}
}
