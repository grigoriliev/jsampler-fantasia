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

package org.jsampler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.NumberFormat;
import java.util.logging.Level;

import org.linuxsampler.lscp.LSException;
import org.linuxsampler.lscp.LscpException;

import static org.jsampler.JSI18n.i18n;


/**
 * This class contains some helper function.
 * @author Grigor Iliev
 */
public class HF {
	private static NumberFormat numberFormat = NumberFormat.getInstance();
	
	static {
		numberFormat.setMaximumFractionDigits(1);
	}
	
	/**
	 * Returns more meaningful, non-<code>null</code> message.
	 * @return More meaningful, non-<code>null</code> message.
	 */
	public static String
	getErrorMessage(Exception e) {
		String msg = e.getMessage();
		
		if(e instanceof LSException) {
			LSException x = (LSException)e;
		} else if(e instanceof LscpException) {
			
		} else { msg = (msg != null ? msg : i18n.getError("unknownError")); }
		
		return msg;
	}
	
	/**
	 * Deletes the specified file, if exists and
	 * is located in the JSampler's home directory.
	 * @param file The file to delete.
	 */
	public static void
	deleteFile(String file) {
		String s = CC.getJSamplerHome();
		if(s == null) return;
		
		try {
			File f = new File(s + File.separator + file);
			if(f.isFile()) f.delete();
		} catch(Exception x) {
			CC.getLogger().log(Level.INFO, getErrorMessage(x), x);
		}
	}
	
	/**
	 * Create a backup copy of the specified file, located in the JSampler's home directory.
	 * @param file The name of the file to backup.
	 * @param bkpFile The backup name of the file.
	 * @return <code>true</code> if the file is backuped successfully.
	 */
	public static boolean
	createBackup(String file, String bkpFile) {
		if(file == null || bkpFile == null) return false;
		if(file.length() == 0 || bkpFile.length() == 0) return false;
		
		String s = CC.getJSamplerHome();
		if(s == null) return false;
		
		File f = new File(s + File.separator + file);
		if(!f.isFile()) return false;
		
		try {
			FileInputStream fis = new FileInputStream(s + File.separator + file);
			
			FileOutputStream fos;
			fos = new FileOutputStream(s + File.separator + bkpFile, false);
			
			int i = fis.read();
			while(i != -1) {
				fos.write(i);
				i = fis.read();
			}
		} catch(Exception x) {
			CC.getLogger().log(Level.INFO, getErrorMessage(x), x);
			return false;
		}
		
		return true;
	}
	
	/**
	 * Converts the volume value specified in percents to decibels.
	 */
	public static double
	percentsToDecibels(int vol) {
		if(vol == 0) return Double.NEGATIVE_INFINITY;
		double i = vol;
		i /= 100;
		i = 20 * Math.log10(i);
		return i;
	}
	
	
	
	/**
	 * Converts the volume value specified in decibels to percents.
	 */
	public static int
	decibelsToPercents(double vol) {
		if(vol == Double.NEGATIVE_INFINITY) return 0;
		double i = Math.pow(10, vol/20);
		i *= 100;
		return (int)i;
	}
	
	/**
	 * Converts the volume value specified in decibels to volume factor.
	 */
	public static float
	decibelsToFactor(double vol) {
		if(vol == Double.NEGATIVE_INFINITY) return 0;
		double i = Math.pow(10, vol/20);
		return (float)i;
	}
	
	/**
	 * Converts the volume value specified in percents to volume factor.
	 */
	public static float
	percentsToFactor(int vol) {
		float f = vol;
		f /= 100;
		return f;
	}
	
	/** Tests whether the application can read/write the specified file. */
	public static boolean
	canReadWrite(String file) {
		File f = new File(file);
		if(f.isDirectory()) return false;
		if(f.canRead() && f.canWrite()) return true;
		return false;
	}
	
	/** Tests whether the application can read/write files in the specified directory. */
	public static boolean
	canReadWriteFiles(String dir) {
		File f = new File(dir);
		if(!f.isDirectory()) return false;
		if(f.canRead() && f.canWrite() && f.canExecute()) return true;
		return false;
	}
		
	public static String
	getVolumeString(int volume) {
		if(CC.getViewConfig().isMeasurementUnitDecibel()) {
			return numberFormat.format(HF.percentsToDecibels(volume)) + "dB";
		} else {
			return String.valueOf(volume) + "%";
		}
	}
}
