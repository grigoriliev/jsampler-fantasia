/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2007 Grigor Iliev <grigor@grigoriliev.com>
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.net.URI;

import java.text.NumberFormat;

import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JSlider;
import javax.swing.JToolTip;
import javax.swing.Popup;
import javax.swing.PopupFactory;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jsampler.CC;
import org.jsampler.HF;
import org.jsampler.JSPrefs;

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
	createPathComboBox() {
		final JComboBox cb = new JComboBox();
		cb.setEditable(true);
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
