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

package org.jsampler.android;

import org.jsampler.CC;
import org.jsampler.android.view.AndroidMainFrame;

import android.view.MotionEvent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
 * This class contains some android helper function.
 * @author Grigor Iliev
 */
public class AHF {
	private static JSamplerActivity activity = null;
	
	public static JSamplerActivity
	getActivity() { return activity; }
	
	public static AndroidMainFrame
	getMainFrame() { return (AndroidMainFrame)CC.getMainFrame(); }
	
	public static void
	setActivity(JSamplerActivity activity) { AHF.activity = activity; }
	
	public static Animation
	inFromRightAnimation() {
		Animation inFromRight = new TranslateAnimation (
			Animation.RELATIVE_TO_PARENT, 1.0f,
			Animation.RELATIVE_TO_PARENT, 0.0f,
			Animation.RELATIVE_TO_PARENT, 0.0f,
			Animation.RELATIVE_TO_PARENT, 0.0f
		);
		inFromRight.setDuration(400);
		inFromRight.setInterpolator(new AccelerateInterpolator());
		return inFromRight;
	}
	
	public static Animation
	outToLeftAnimation() {
		Animation outToLeft = new TranslateAnimation (
			Animation.RELATIVE_TO_PARENT,  0.0f,
			Animation.RELATIVE_TO_PARENT, -1.0f,
			Animation.RELATIVE_TO_PARENT,  0.0f,
			Animation.RELATIVE_TO_PARENT,  0.0f
		);
		outToLeft.setDuration(400);
		outToLeft.setInterpolator(new AccelerateInterpolator());
		return outToLeft;
	}

	public static Animation
	inFromLeftAnimation() {
		Animation inFromLeft = new TranslateAnimation (
			Animation.RELATIVE_TO_PARENT, -1.0f,
			Animation.RELATIVE_TO_PARENT,  0.0f,
			Animation.RELATIVE_TO_PARENT,  0.0f,
			Animation.RELATIVE_TO_PARENT,  0.0f
		);
		inFromLeft.setDuration(400);
		inFromLeft.setInterpolator(new AccelerateInterpolator());
		return inFromLeft;
	}
	
	public static Animation
	outToRightAnimation() {
		Animation outToRight = new TranslateAnimation (
			Animation.RELATIVE_TO_PARENT, 0.0f,
			Animation.RELATIVE_TO_PARENT, 1.0f,
			Animation.RELATIVE_TO_PARENT, 0.0f,
			Animation.RELATIVE_TO_PARENT, 0.0f
		);
		outToRight.setDuration(400);
		outToRight.setInterpolator(new AccelerateInterpolator());
		return outToRight;
	}
	
	public static boolean
	isMotionHorizontal(MotionEvent e1, MotionEvent e2) {
		return Math.abs(e1.getX() - e2.getX()) > Math.abs(e1.getY() - e2.getY());
	}
	
	public static boolean
	isMotionVertical(MotionEvent e1, MotionEvent e2) {
		return Math.abs(e1.getX() - e2.getX()) < Math.abs(e1.getY() - e2.getY());
	}
	
	public static boolean
	isMotionLeftToRight(MotionEvent e1, MotionEvent e2, int min) {
		return isMotionHorizontal(e1, e2) && e2.getX() - e1.getX() > min;
	}
	
	public static boolean
	isMotionRightToLeft(MotionEvent e1, MotionEvent e2, int min) {
		return isMotionHorizontal(e1, e2) && e1.getX() - e2.getX() > min;
	}
	
	public static boolean
	isMotionUp(MotionEvent e1, MotionEvent e2, int min) {
		return isMotionVertical(e1, e2) && e1.getY() - e2.getY() > min;
	}
	
	public static boolean
	isMotionDown(MotionEvent e1, MotionEvent e2, int min) {
		return isMotionVertical(e1, e2) && e2.getY() - e1.getY() > min;
	}
}
