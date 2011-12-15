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

package org.jsampler.android.view.classic;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.jsampler.AudioDeviceModel;
import org.jsampler.CC;
import org.jsampler.HF;
import org.jsampler.MidiDeviceModel;
import org.jsampler.MidiInstrumentMap;
import org.jsampler.SamplerChannelModel;
import org.jsampler.android.AHF;
import org.jsampler.android.R;
import org.jsampler.android.view.AndroidChannel;
import org.jsampler.android.view.AudioDeviceSpinnerAdapter;
import org.jsampler.android.view.EngineSpinnerAdapter;
import org.jsampler.android.view.MidiChannelSpinnerAdapter;
import org.jsampler.android.view.MidiDeviceSpinnerAdapter;
import org.jsampler.android.view.MidiMapSpinnerAdapter;
import org.jsampler.android.view.MidiPortSpinnerAdapter;
import org.jsampler.event.SamplerChannelEvent;
import org.jsampler.event.SamplerChannelListener;
import org.linuxsampler.lscp.MidiPort;
import org.linuxsampler.lscp.SamplerChannel;
import org.linuxsampler.lscp.SamplerEngine;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import static org.jsampler.JSPrefs.*;
import static org.jsampler.android.view.AndroidI18n.i18n;

public class Channel extends AndroidChannel {
	private View view = null;
	
	public Channel(SamplerChannelModel model) {
		super(model);
	}
	
	public View
	getView() { return view; }
	
	public void
	installView(View viewToReuse) {
		if(viewToReuse != null) view = viewToReuse;
		else view = createView();

		installListeners(); // don't change order
		updateView();
	}
	
	public void
	uninstallView() {
		uninstallListeners(); // don't change order
		view = null;
	}
	
	private void
	installListeners() {
		getModel().addSamplerChannelListener(getHandler());
		CC.preferences().addPropertyChangeListener(VOL_MEASUREMENT_UNIT_DECIBEL, getHandler());
				
		SeekBar slider = (SeekBar)view.findViewById(R.id.sampler_channel_slider_volume);
		int i = CC.preferences().getIntProperty(MAXIMUM_CHANNEL_VOLUME);
		slider.setMax(i);
		
		slider.setOnSeekBarChangeListener(getHandler());
		
		CC.preferences().addPropertyChangeListener(MAXIMUM_CHANNEL_VOLUME, getHandler());
		
		final ToggleButton tbMute = (ToggleButton)view.findViewById(R.id.sampler_channel_btn_mute);
		tbMute.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				getModel().setBackendMute(tbMute.isChecked());
			}
		});
		
		final ToggleButton tbSolo = (ToggleButton)view.findViewById(R.id.sampler_channel_btn_solo);
		tbSolo.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				getModel().setBackendSolo(tbSolo.isChecked());
			}
		});
		
		final Spinner spEngine = (Spinner)view.findViewById(R.id.sampler_channel_engine);
		SamplerEngine se = getChannelInfo().getEngine();
		
		int idx = ((EngineSpinnerAdapter)spEngine.getAdapter()).prepareForSelection(se);
		spEngine.setSelection(idx);
		
		spEngine.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void
			onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				SamplerEngine e = (SamplerEngine)spEngine.getAdapter().getItem(position);
				if(e == null) return;
				int pos = ((EngineSpinnerAdapter)spEngine.getAdapter()).prepareForSelection(e);
				if(pos != position) spEngine.setSelection(pos, false);
				getModel().setBackendEngineType(e.getName());
			}
			
			public void
			onNothingSelected(AdapterView<?> parent) { }
		});
		
		final Spinner spMidiDev = (Spinner)view.findViewById(R.id.sampler_channel_midi_device);
		spMidiDev.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void
			onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				MidiDeviceModel d = (MidiDeviceModel)spMidiDev.getAdapter().getItem(position);
				if(d == null) return;
				int pos = ((MidiDeviceSpinnerAdapter)spMidiDev.getAdapter()).prepareForSelection(d);
				if(pos != position) spMidiDev.setSelection(pos, false);
				
				if(d.getDeviceId() != getChannelInfo().getMidiInputDevice()) {
					getModel().setBackendMidiInputDevice(d.getDeviceId());
				}
			}
			
			public void
			onNothingSelected(AdapterView<?> parent) { }
		});
		
		spMidiDev.getAdapter().registerDataSetObserver(midiDevDataSetObserver);
		
		final Spinner spMidiPort = (Spinner)view.findViewById(R.id.sampler_channel_midi_port);
		spMidiPort.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void
			onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				MidiPort p = (MidiPort)spMidiPort.getAdapter().getItem(position);
				if(p == null) return;
				int pos = ((MidiPortSpinnerAdapter)spMidiPort.getAdapter()).prepareForSelection(p);
				if(pos != position) spMidiPort.setSelection(pos, false);
				
				if(pos != getChannelInfo().getMidiInputPort()) {
					getModel().setBackendMidiInputPort(pos);
				}
			}
			
			public void
			onNothingSelected(AdapterView<?> parent) { }
		});
		
		final Spinner spMidiChn = (Spinner)view.findViewById(R.id.sampler_channel_midi_channel);
		spMidiChn.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void
			onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String chn = (String)spMidiChn.getAdapter().getItem(position);
				if(chn == null) return;
				int pos = ((MidiChannelSpinnerAdapter)spMidiChn.getAdapter()).prepareForSelection(chn);
				if(pos != position) spMidiChn.setSelection(pos, false);
				
				if(pos == 0) pos = -1; // All channels
				else pos--; // convert to zero-based numbering
				if(pos != getChannelInfo().getMidiInputChannel()) {
					getModel().setBackendMidiInputChannel(pos);
				}
			}
			
			public void
			onNothingSelected(AdapterView<?> parent) { }
		});
		
		final Spinner spMidiMap = (Spinner)view.findViewById(R.id.sampler_channel_midi_map);
		spMidiMap.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void
			onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				Object map = spMidiMap.getAdapter().getItem(position);
				if(map == null) return;
				int pos = ((MidiMapSpinnerAdapter)spMidiMap.getAdapter()).prepareForSelection(map);
				if(pos != position) spMidiMap.setSelection(pos, false);
				
				if(map == MidiMapSpinnerAdapter.noMap) pos = -1;
				else if(map == MidiMapSpinnerAdapter.defaultMap) pos = -2;
				else pos = ((MidiInstrumentMap)map).getMapId();
				if(pos != getChannelInfo().getMidiInstrumentMapId()) {
					getModel().setBackendMidiInstrumentMap(pos);
				}
			}
			
			public void
			onNothingSelected(AdapterView<?> parent) { }
		});
		
		spMidiMap.getAdapter().registerDataSetObserver(midiMapDataSetObserver);
		
		final Spinner spAudioDev = (Spinner)view.findViewById(R.id.sampler_channel_audio_device);
		spAudioDev.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void
			onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				AudioDeviceModel d = (AudioDeviceModel)spAudioDev.getAdapter().getItem(position);
				if(d == null) return;
				int pos = ((AudioDeviceSpinnerAdapter)spAudioDev.getAdapter()).prepareForSelection(d);
				if(pos != position) spAudioDev.setSelection(pos, false);
				
				if(d.getDeviceId() != getChannelInfo().getAudioOutputDevice()) {
					getModel().setBackendAudioOutputDevice(d.getDeviceId());
				}
			}
			
			public void
			onNothingSelected(AdapterView<?> parent) { }
		});
		
		spAudioDev.getAdapter().registerDataSetObserver(audioDevDataSetObserver);
	}
	
	private void
	uninstallListeners() {
		getModel().removeSamplerChannelListener(getHandler());
		CC.preferences().removePropertyChangeListener(VOL_MEASUREMENT_UNIT_DECIBEL, getHandler());
		CC.preferences().removePropertyChangeListener(MAXIMUM_CHANNEL_VOLUME, getHandler());
		
		ToggleButton tb = (ToggleButton)view.findViewById(R.id.sampler_channel_btn_mute);
		tb.setOnClickListener(null);
		
		tb = (ToggleButton)view.findViewById(R.id.sampler_channel_btn_solo);
		tb.setOnClickListener(null);
		
		SeekBar slider = (SeekBar)view.findViewById(R.id.sampler_channel_slider_volume);
		slider.setOnSeekBarChangeListener(null);
		
		Spinner spinner = (Spinner)view.findViewById(R.id.sampler_channel_engine);
		spinner.setOnItemSelectedListener(null);
		
		spinner = (Spinner)view.findViewById(R.id.sampler_channel_midi_device);
		spinner.setOnItemSelectedListener(null);
		spinner.getAdapter().unregisterDataSetObserver(midiDevDataSetObserver);
		
		spinner = (Spinner)view.findViewById(R.id.sampler_channel_midi_port);
		spinner.setOnItemSelectedListener(null);
		
		spinner = (Spinner)view.findViewById(R.id.sampler_channel_midi_channel);
		spinner.setOnItemSelectedListener(null);
		
		spinner = (Spinner)view.findViewById(R.id.sampler_channel_midi_map);
		spinner.setOnItemSelectedListener(null);
		spinner.getAdapter().unregisterDataSetObserver(midiMapDataSetObserver);
		
		spinner = (Spinner)view.findViewById(R.id.sampler_channel_audio_device);
		spinner.setOnItemSelectedListener(null);
		spinner.getAdapter().unregisterDataSetObserver(audioDevDataSetObserver);
	}
	
	/* Used to update the spinner selection in case it was invalidated 
	 * due to removal of an item which was before the selected item. */
	private DataSetObserver midiDevDataSetObserver = new DataSetObserver() {
		public void
		onChanged() { updateMidiDevice(); }
	};
	
	/* Used to update the spinner selection in case it was invalidated 
	 * due to removal of an item which was before the selected item. */
	private DataSetObserver midiMapDataSetObserver = new DataSetObserver() {
		public void
		onChanged() { updateMidiMap(); }
	};
	
	/* Used to update the spinner selection in case it was invalidated 
	 * due to removal of an item which was before the selected item. */
	private DataSetObserver audioDevDataSetObserver = new DataSetObserver() {
		public void
		onChanged() { updateAudioDevice(); }
	};
	
	public View
	createView() {
		LayoutInflater inflater =
			(LayoutInflater)AHF.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.sampler_channel, null);
		
		Spinner spinner = (Spinner)v.findViewById(R.id.sampler_channel_engine);
		spinner.setAdapter(new EngineSpinnerAdapter());
		
		spinner = (Spinner)v.findViewById(R.id.sampler_channel_midi_device);
		spinner.setAdapter(new MidiDeviceSpinnerAdapter());
		
		spinner = (Spinner)v.findViewById(R.id.sampler_channel_midi_port);
		spinner.setAdapter(new MidiPortSpinnerAdapter());
		
		spinner = (Spinner)v.findViewById(R.id.sampler_channel_midi_channel);
		spinner.setAdapter(new MidiChannelSpinnerAdapter());
		
		spinner = (Spinner)v.findViewById(R.id.sampler_channel_midi_map);
		spinner.setAdapter(new MidiMapSpinnerAdapter());
		
		spinner = (Spinner)v.findViewById(R.id.sampler_channel_audio_device);
		spinner.setAdapter(new AudioDeviceSpinnerAdapter());
		
		return v;
	}
	
	public void
	updateView() {
		int num = CC.getMainFrame().getChannelNumber(getModel()) + 1;
		TextView text = (TextView)view.findViewById(R.id.sampler_channel_title);
		text.setText(i18n.getLabel("Channel.title", num));
		
		SamplerChannel sc = getChannelInfo();
		boolean b = sc.getEngine() != null;
		
		ToggleButton toggleBtn = (ToggleButton)view.findViewById(R.id.sampler_channel_btn_solo);
		toggleBtn.setChecked(sc.isSoloChannel());
		toggleBtn.setEnabled(b);
		
		toggleBtn = (ToggleButton)view.findViewById(R.id.sampler_channel_btn_mute);
		toggleBtn.setChecked(sc.isMuted() && !sc.isMutedBySolo());
		toggleBtn.setEnabled(b);
		
		SeekBar slider = (SeekBar)view.findViewById(R.id.sampler_channel_slider_volume);
		slider.setEnabled(b);
		updateVolume(slider);
		
		updateMidiDevice();
		updateEngine();
		updateMidiPort();
		updateMidiChannel();
		updateMidiMap();
		updateAudioDevice();
	}
	
	private void
	updateVolume(SeekBar slider) {
		slider.setProgress((int)(getChannelInfo().getVolume() * 100));
	}
	
	private void
	updateEngine() {
		Spinner spinner = (Spinner)view.findViewById(R.id.sampler_channel_engine);
		SamplerEngine se = getChannelInfo().getEngine();
		int pos = ((EngineSpinnerAdapter)spinner.getAdapter()).prepareForSelection(se);
		if(pos >= 0) {
			if(pos != spinner.getSelectedItemPosition()) {
				spinner.setSelection(pos);
			}
		} else {
			Log.w("view.Channel", "Engine not found in spinner. This is a bug!");
		}
	}
	
	private void
	updateMidiDevice() {
		Spinner spinner = (Spinner)view.findViewById(R.id.sampler_channel_midi_device);
		int id = getChannelInfo().getMidiInputDevice();
		MidiDeviceModel dev = null;
		if(id != -1) dev = CC.getSamplerModel().getMidiDeviceById(id);
		
		int pos = ((MidiDeviceSpinnerAdapter)spinner.getAdapter()).prepareForSelection(dev);
		if(pos >= 0) {
			if(pos != spinner.getSelectedItemPosition()) {
				spinner.setSelection(pos);
			}
		} else {
			Log.w("view.Channel", "MIDI device not found in spinner. This is a bug!");
		}
	}
	
	private void
	updateMidiPort() {
		Spinner spinner = (Spinner)view.findViewById(R.id.sampler_channel_midi_port);
		MidiPortSpinnerAdapter adapter = (MidiPortSpinnerAdapter)spinner.getAdapter();
		int id = getChannelInfo().getMidiInputDevice();
		adapter.setMidiDevice(id);
		
		if(id == -1) {
			spinner.setSelection(adapter.prepareForSelection(null));
		} else {
			MidiDeviceModel dev = CC.getSamplerModel().getMidiDeviceById(id);
			MidiPort p = dev.getDeviceInfo().getMidiPort(getChannelInfo().getMidiInputPort());
			
			int idx = adapter.prepareForSelection(p);
			if(idx != spinner.getSelectedItemPosition()) spinner.setSelection(idx);
		}
	}
	
	private void
	updateMidiChannel() {
		Spinner spinner = (Spinner)view.findViewById(R.id.sampler_channel_midi_channel);
		MidiChannelSpinnerAdapter adapter = (MidiChannelSpinnerAdapter)spinner.getAdapter();
		int id = getChannelInfo().getMidiInputDevice();
		adapter.setMidiDevice(id);
		
		if(id == -1) {
			spinner.setSelection(adapter.prepareForSelection(null));
		} else {
			String s;
			int chn = getChannelInfo().getMidiInputChannel();
			if(chn == -1) s = MidiChannelSpinnerAdapter.channels[0]; // all channels
			else s = MidiChannelSpinnerAdapter.channels[chn + 1];
			
			int idx = adapter.prepareForSelection(s);
			if(idx != spinner.getSelectedItemPosition()) spinner.setSelection(idx);
		}
	}
	
	private void
	updateMidiMap() {
		Spinner spinner = (Spinner)view.findViewById(R.id.sampler_channel_midi_map);
		MidiMapSpinnerAdapter adapter = (MidiMapSpinnerAdapter)spinner.getAdapter();
		if(getChannelInfo().getEngine() == null) {
			spinner.setSelection(adapter.prepareForSelection(null));
			spinner.setEnabled(false);
		} else {
			spinner.setEnabled(true);
			Object o;
			int mapId = getChannelInfo().getMidiInstrumentMapId();
			if(mapId == -2) o = MidiMapSpinnerAdapter.defaultMap;
			else if(mapId == -1) o = MidiMapSpinnerAdapter.noMap;
			else o = CC.getSamplerModel().getMidiInstrumentMapById(mapId);
			
			if(o == null) Log.w("view.classic.Channel", "Unknown map. This is a bug!");
			
			int idx = adapter.prepareForSelection(o);
			if(idx != spinner.getSelectedItemPosition()) spinner.setSelection(idx);
		}
	}
	
	private void
	updateAudioDevice() {
		Spinner spinner = (Spinner)view.findViewById(R.id.sampler_channel_audio_device);
		int id = getChannelInfo().getAudioOutputDevice();
		AudioDeviceModel dev = null;
		if(id != -1) dev = CC.getSamplerModel().getAudioDeviceById(id);
		
		int pos = ((AudioDeviceSpinnerAdapter)spinner.getAdapter()).prepareForSelection(dev);
		if(pos >= 0) {
			if(pos != spinner.getSelectedItemPosition()) {
				spinner.setSelection(pos);
			}
		} else {
			Log.w("view.Channel", "Audio device not found in spinner. This is a bug!");
		}
	}
	
	private void
	setVolume(boolean isAdjusting) {
		SeekBar sliderVolume = (SeekBar)view.findViewById(R.id.sampler_channel_slider_volume);
		TextView tvVolume = (TextView)view.findViewById(R.id.sampler_channel_text_volume);
		int volume = sliderVolume.getProgress();
		
		tvVolume.setText(HF.getVolumeString(volume));
		
		if(isAdjusting) return;
		
		int vol = (int)(getChannelInfo().getVolume() * 100);
		
		if(vol == sliderVolume.getProgress()) return;
		
		/*
		 * If the model's volume is not equal to the slider
		 * value we assume that the change is due to user input.
		 * So we must update the volume at the backend too.
		 */
		float v = sliderVolume.getProgress();
		v /= 100;
		getModel().setBackendVolume(v);
	}
	
	EventHandler handler = new EventHandler();
	
	private EventHandler
	getHandler() { return handler; }
	
	private class EventHandler implements SamplerChannelListener, PropertyChangeListener,
					SeekBar.OnSeekBarChangeListener {
		public void
		channelChanged(SamplerChannelEvent e) {
			updateView();
		}
		
		public void streamCountChanged(SamplerChannelEvent e) { }
		
		public void voiceCountChanged(SamplerChannelEvent e) { }
		
		public void
		propertyChange(PropertyChangeEvent e) {
			if(view == null) return;
			
			if (VOL_MEASUREMENT_UNIT_DECIBEL.equals(e.getPropertyName())) {
				setVolume(true);
			} else if (MAXIMUM_CHANNEL_VOLUME.equals(e.getPropertyName())) {
				int j = CC.preferences().getIntProperty(MAXIMUM_CHANNEL_VOLUME);
				SeekBar v = (SeekBar)view.findViewById(R.id.sampler_channel_slider_volume);
				v.setMax(j);
			}
		}
		
		@Override
		public void
		onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			setVolume(true);
		}
		
		@Override
		public void
		onStopTrackingTouch(SeekBar seekBar) { setVolume(false); }
		
		@Override
		public void
		onStartTrackingTouch(SeekBar seekBar) { }
	}
}
