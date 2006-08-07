/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005, 2006 Grigor Kirilov Iliev
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

import javax.swing.ImageIcon;


/**
 * This class contains all pixmap resources needed by <b>JS Classic</b> view.
 * @author Grigor Iliev
 */
public class Res {
	
	/** Forbits the instantiation of this class. */
	private Res() { }
	
	protected final static ImageIcon iconNew16
		= new ImageIcon(Res.class.getResource("res/icons/New16.gif"));
	
	protected final static ImageIcon iconEdit16
		= new ImageIcon(Res.class.getResource("res/icons/Edit16.gif"));
	
	protected final static ImageIcon iconCopy16
		= new ImageIcon(Res.class.getResource("res/icons/Copy16.gif"));
	
	protected final static ImageIcon iconDelete16
		= new ImageIcon(Res.class.getResource("res/icons/Delete16.gif"));
	
	protected final static ImageIcon iconProps16
		= new ImageIcon(Res.class.getResource("res/icons/Properties16.gif"));
	
	protected final static ImageIcon iconUp16
		= new ImageIcon(Res.class.getResource("res/icons/Up16.gif"));
	
	protected final static ImageIcon iconDown16
		= new ImageIcon(Res.class.getResource("res/icons/Down16.gif"));
	
	protected final static ImageIcon iconNew24
		= new ImageIcon(Res.class.getResource("res/icons/toolbar/New24.gif"));
	
	protected final static ImageIcon iconCopy24
		= new ImageIcon(Res.class.getResource("res/icons/toolbar/Copy24.gif"));
	
	protected final static ImageIcon iconPreferences24
		= new ImageIcon(Res.class.getResource("res/icons/toolbar/Preferences24.gif"));
	
}
