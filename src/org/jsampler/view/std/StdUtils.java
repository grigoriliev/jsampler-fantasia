/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2009 Grigor Iliev <grigor@grigoriliev.com>
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
package org.jsampler.view.std;

import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Window;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.io.File;
import java.net.URI;

import java.text.NumberFormat;

import java.util.Vector;
import java.util.logging.Level;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JSlider;
import javax.swing.JToolTip;
import javax.swing.Popup;
import javax.swing.PopupFactory;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jsampler.CC;
import org.jsampler.HF;
import org.jsampler.JSPrefs;

import org.jsampler.view.JSFileFilter;
import static org.jsampler.view.std.StdI18n.i18n;
import static org.jsampler.view.std.StdPrefs.*;


/**
 *
 * @author Grigor Iliev
 */
public class StdUtils {
	
	/** Forbids the instantiation of this class */
	private
	StdUtils() { }
	
	private static JSPrefs
	preferences() { return CC.getViewConfig().preferences(); }

	public static JComboBox
	createEnhancedComboBox() {
		final JComboBox cb = new JComboBox();
		cb.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				if(cb.getSelectedItem() == null) {
					cb.setToolTipText(null);
					return;
				}
				String s = cb.getSelectedItem().toString();
				if(s.length() < 15) cb.setToolTipText(null);
				else cb.setToolTipText(s);
			}
		});

		return cb;
	}
	
	public static JComboBox
	createPathComboBox() {
		JComboBox cb = createEnhancedComboBox();
		cb.setEditable(true);
		return cb;
	}
	
	/**
	 * Updates the specified string list property by adding the specified
	 * element on the top. Also restricts the maximum number of elements to 12.
	 */
	public static void
	updateRecentElements(String property, String newElement) {
		String[] elements = preferences().getStringListProperty(property);
		Vector<String> v = new Vector<String>();
		v.add(newElement);
		for(String s : elements) {
			if(!newElement.equals(s)) v.add(s);
		}
		if(v.size() > 12) v.setSize(12);
		
		elements = v.toArray(new String[v.size()]);
		preferences().setStringListProperty(property, elements);
	}
	
	public static boolean
	checkDesktopSupported() {
		if(Desktop.isDesktopSupported()) return true;
		
		String s = i18n.getError("StdUtils.DesktopApiNotSupported");
		HF.showErrorMessage(s, CC.getMainFrame());
		
		return false;
	}
	
	public static void
	browse(String uri) {
		if(!checkDesktopSupported()) return;
		
		try { Desktop.getDesktop().browse(new URI(uri)); }
		catch(Exception x) { x.printStackTrace(); }
	}
	
	public static void
	mail(String uri) {
		if(!StdUtils.checkDesktopSupported()) return;
		
		Desktop desktop = Desktop.getDesktop();
		
		try { Desktop.getDesktop().mail(new URI(uri)); }
		catch(Exception x) { x.printStackTrace(); }
	}
	
	/**
	 * Gets the windows bounds from the preferences for the specified window.
	 * @return The windows bounds saved in the preferences for the specified window
	 * or <code>null</code>.
	 */
	public static Rectangle
	getWindowBounds(String windowName) {
		String s = windowName + ".windowSizeAndLocation";
		s = CC.preferences().getStringProperty(s, null);
		if(s == null) return null;
		
		try {
			int i = s.indexOf(',');
			int x = Integer.parseInt(s.substring(0, i));
			
			s = s.substring(i + 1);
			i = s.indexOf(',');
			int y = Integer.parseInt(s.substring(0, i));
			
			s = s.substring(i + 1);
			i = s.indexOf(',');
			int width = Integer.parseInt(s.substring(0, i));
			
			s = s.substring(i + 1);
			int height = Integer.parseInt(s);
			
			return new Rectangle(x, y, width, height);
		} catch(Exception x) {
			String msg = windowName;
			msg += ": Parsing of window size and location string failed";
			CC.getLogger().log(Level.INFO, msg, x);
			return null;
		}
	}
	
	/**
	 * Saves the windows bounds in the preferences for the specified window.
	 */
	public static void
	saveWindowBounds(String windowName, Rectangle r) {
		if(r.width < 50 || r.height < 50 || r.x < r.width * -1 || r.y < 0) {
			CC.getLogger().warning("Invalid window size or location");
			return;
		}

		StringBuffer sb = new StringBuffer();
		sb.append(r.x).append(',').append(r.y).append(',');
		sb.append(r.width).append(',').append(r.height);
		String s = windowName + ".windowSizeAndLocation";
		CC.preferences().setStringProperty(s, sb.toString());
	}

	public static File
	showOpenLscpFileChooser() {
		return showLscpFileChooser(true);
	}

	public static File
	showOpenLscpFileChooser(Window owner) {
		return showLscpFileChooser(true, owner);
	}

	public static File
	showSaveLscpFileChooser() {
		return showLscpFileChooser(false);
	}

	public static File
	showSaveLscpFileChooser(Window owner) {
		return showLscpFileChooser(false, owner);
	}

	private static File
	showLscpFileChooser(boolean openDialog) {
		return showLscpFileChooser(openDialog, CC.getMainFrame());
	}

	private static File
	showLscpFileChooser(boolean openDialog, Window owner) {
		return showFileChooser (
			openDialog, owner, false, new JSFileFilter.Lscp(), "lastScriptLocation"
		);
	}

	public static File
	showSaveMidiMapsChooser() {
		JSFileFilter filter = new JSFileFilter.MidiMaps();

		JSFileFilter[] filters = {
			new JSFileFilter.Lscp(), new JSFileFilter.Text(), new JSFileFilter.Html(),
			new JSFileFilter.Rgd()
		};

		return showFileChooser (
			false, CC.getMainFrame(), false, filter, filters, "lastScriptLocation"
		);
	}

	public static File
	showOpenInstrumentFileChooser(Window owner) {
		return showFileChooser(true, owner, false, null, "lastInstrumentLocation");
	}

	public static File
	showOpenDirectoryChooser(Window owner, String locationProperty) {
		return showFileChooser(true, owner, true, null, locationProperty);
	}

	private static File
	showFileChooser (
		boolean       openDialog,
		Window        owner,
		boolean       dirChooser,
		JSFileFilter  filter,
		String        locationProperty
	) {
		JSFileFilter[] filters = (filter == null) ? new JSFileFilter[0] : new JSFileFilter[1];
		if(filter != null) filters[0] = filter;
		
		return showFileChooser(openDialog, owner, dirChooser, filter, filters, locationProperty);
	}

	private static File
	showFileChooser (
		boolean         openDialog,
		Window          owner,
		boolean         dirChooser,
		JSFileFilter    filter,
		JSFileFilter[]  choosableFilters,
		String          locationProperty
	) {
		boolean nativeFileChooser = preferences().getBoolProperty("nativeFileChoosers");
		String oldPath = null;
		if(locationProperty != null) {
			oldPath = preferences().getStringProperty(locationProperty);
		}
		File f = null;
		if(nativeFileChooser && CC.isMacOS()) {
			if(dirChooser) {
				System.setProperty("apple.awt.fileDialogForDirectories", "true");
			}
			FileDialog dlg;
			if(owner instanceof Frame) dlg = new FileDialog((Frame)owner);
			else if(owner instanceof Dialog) dlg = new FileDialog((Dialog)owner);
			else dlg = new FileDialog(CC.getMainFrame());
			dlg.setDirectory(oldPath);
			dlg.setMode(openDialog ? FileDialog.LOAD : FileDialog.SAVE);
			if(filter != null) dlg.setFilenameFilter(filter);
			dlg.setVisible(true);
			if(dirChooser) {
				System.setProperty("apple.awt.fileDialogForDirectories", "false");
			}
			if(dlg.getFile() != null) {
				f = new File(new File(dlg.getDirectory()), dlg.getFile());
			}
		} else {
			JFileChooser fc = new JFileChooser(oldPath);
			for(JSFileFilter ff : choosableFilters) {
				fc.addChoosableFileFilter(ff);
			}
			if(choosableFilters.length > 0) fc.setFileFilter(choosableFilters[0]);
			
			if(dirChooser) fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int result;
			if(openDialog) result = fc.showOpenDialog(owner);
			else result = fc.showSaveDialog(owner);
			if(result == JFileChooser.APPROVE_OPTION) {
				f = fc.getSelectedFile();
			}

			if(result == JFileChooser.APPROVE_OPTION && !openDialog) {
				Object o = fc.getFileFilter();
				for(JSFileFilter ff : choosableFilters) {
					if(ff == o) {
						String fn = f.getName().toLowerCase();
						String ext = ff.getExtension().toLowerCase();
						if(fn.endsWith(ext)) break;

						fn = f.getAbsolutePath() + ff.getExtension();
						f = new File(fn);
						break;
					}
				}
			}
		}

		if(f == null) return null;
		String path = f.getParent();
		if(path != null && locationProperty != null) {
			preferences().setStringProperty(locationProperty, path);
		}
		return f;
	}
	
	public static JSlider
	createVolumeSlider() {
		return new VolumeSlider();
	}
	
	private static class VolumeSlider extends JSlider {
		private Popup popup = null;
		private final JToolTip tip = new JToolTip();
		private static NumberFormat numberFormat = NumberFormat.getInstance();
		
		VolumeSlider() {
			super(0, 100, 100);
			numberFormat.setMaximumFractionDigits(1);
			// Setting the tooltip size (workaround for preserving that size)
			boolean b = CC.getViewConfig().isMeasurementUnitDecibel();
			if(b) tip.setTipText(i18n.getLabel("StdUtils.volumeDecibels", "-30.0"));
			else tip.setTipText(i18n.getLabel("StdUtils.volume", "100"));
			tip.setPreferredSize(tip.getPreferredSize());
			tip.setMinimumSize(tip.getPreferredSize());
			///////
			tip.setComponent(this);
			tip.setTipText(i18n.getLabel("StdUtils.volume", 0));
			
			updateVolumeInfo();
			
			addMouseListener(new MouseAdapter() {
				public void
				mousePressed(MouseEvent e) {
					if(popup != null) {
						popup.hide();
						popup = null;
					}
					
					if(!VolumeSlider.this.isEnabled()) return;
					
					java.awt.Point p = VolumeSlider.this.getLocationOnScreen();
					PopupFactory pf = PopupFactory.getSharedInstance();
					popup = pf.getPopup(VolumeSlider.this, tip, p.x, p.y - 22);
					popup.show();
				}
				
				public void
				mouseReleased(MouseEvent e) {
					if(popup != null) {
						popup.hide();
						popup = null;
					}
				}
			});
			
			addChangeListener(new ChangeListener() {
				public void
				stateChanged(ChangeEvent e) { updateVolumeInfo(); }
			});
			
			String s = VOL_MEASUREMENT_UNIT_DECIBEL;
			preferences().addPropertyChangeListener(s, new PropertyChangeListener() {
				public void
				propertyChange(PropertyChangeEvent e) {
					// We use this to set the size of the lVolume
					// to prevent the frequent resizing of lVolume component
					boolean b = CC.getViewConfig().isMeasurementUnitDecibel();
					tip.setPreferredSize(null);
					String s;
					if(b) s = i18n.getLabel("StdUtils.volumeDecibels", "-30.0");
					else s = i18n.getLabel("StdUtils.volume", "100");
					tip.setTipText(s);
					tip.setPreferredSize(tip.getPreferredSize());
					tip.setMinimumSize(tip.getPreferredSize());
					///////
					updateVolumeInfo();
				}
			});
		}
		
		private void
		updateVolumeInfo() {
			String s;
			if(CC.getViewConfig().isMeasurementUnitDecibel()) {
				double d = HF.percentsToDecibels(getValue());
				s = i18n.getLabel("StdUtils.volumeDecibels", numberFormat.format(d));
			} else {
				s = i18n.getLabel("StdUtils.volume", getValue());
			}
			
			setToolTipText(s);
			tip.setTipText(s);
			tip.repaint();
		}
	}
}
