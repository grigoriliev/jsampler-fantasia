/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2006 Grigor Iliev <grigor@grigoriliev.com>
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

import java.util.StringTokenizer;
import java.util.Vector;


/**
 * This class provides a collection of utility methods regarding LSCP protocol.
 * @author Grigor Iliev
 */
public class LscpUtils {
	
	/** Forbits the instantiation of this class. */
	private
	LscpUtils() { }
	
	/**
	 * Determines whether the specified command (or part of a command) is
	 * valid LSCP command.
	 * @param cmd The command to be checked.
	 * @return <code>true</code> if <code>cmd</code> is <code>null</code>,
	 * empty string, or valid command (or part of a command).
	 */
	public static boolean
	spellCheck(String cmd) {
		if(cmd == null || cmd.length() == 0) return true;
		if(cmd.charAt(0) == '#') return true;
		
		LscpNode node = LscpTree.getRoot();
		
		// Uses negative to prevent the discarding of the trailing empty strings.
		String[] kwS = cmd.split(" ", -20);
		
		for(int i = 0; i < kwS.length; i++) {
			if(i < kwS.length - 1) {
				node = checkCommand(kwS[i], node);
				if(node == null) return false;
				if(node.getChildren().length == 0) return node.hasParameters();
			} else {
				return checkPartialCommand(kwS[i], node);
			}
			
			if(node.isEndOfACommand() && node.hasParameters()) return true;
		}
		
		return true;
	}
	
	private static LscpNode
	checkCommand(String s, LscpNode node) {
		for(LscpNode n : node.getChildren()) {
			if(n.getName().equals(s)) return n;
		}
		
		return null;
	}
	
	private static boolean
	checkPartialCommand(String s, LscpNode node) {
		for(LscpNode n : node.getChildren()) {
			if(n.getName().startsWith(s)) return true;
		}
		
		return false;
	}
	
	/**
	 * Gets all completion possibilities of the specified incomplete LSCP command.
	 * @param cmd An incomplete command for which
	 * all completion possibilities should be returned.
	 * @return All completion possibilities of the specified incomplete LSCP command.
	 * @throws IllegalStateException If the specified part of a command is not valid.
	 */
	public static String[]
	getCompletionPossibilities(String cmd) {
		String prefix = "";
		LscpNode node = LscpTree.getRoot();
		
		// Uses negative to prevent the discarding of the trailing empty strings.
		String[] kwS = cmd.split(" ", -20);
		
		for(int i = 0; i < kwS.length; i++) {
			String s = kwS[i];
			if(i < kwS.length - 1) {
				if(prefix.length() > 0) prefix += " ";
				prefix += s;
				node = checkCommand(s, node);
				
				if(node == null)
					throw new IllegalStateException("Invalid command!");
				
				if(node.getChildren().length == 0) {
					if(node.isEndOfACommand() && !node.hasParameters()) {
						throw new IllegalStateException("Invalid command!");
					} else return new String[0];
				}
			} else {
				if(!checkPartialCommand(s, node))
					throw new IllegalStateException("Invalid command!");
				
				LscpNode n = checkCommand(s, node);
				if(n != null) {
					node = n;
					if(prefix.length() > 0) prefix += " ";
					prefix += s;
				} else {
					// The command ends with incomplete keyword.
					String[] cmdS = getKeywords(node, prefix, s);
					return cmdS;
				}
			}
		}
		
		/* If we are here this means that the command ends with
		 * complete keyword, or is empty string.
		 */
		
		return getKeywords(node, prefix, "");
	}
	
	/**
	 * Returns an array of commands which last keyword begins with <code>prefix</code>.
	 */
	private static String[]
	getKeywords(LscpNode node, String cmdPrefix, String prefix) {
		Vector<String> v = new Vector<String>();
		
		for(LscpNode n : node.getChildren()) {
			if(n.getName().startsWith(prefix)) {
				String suffix =
					n.isEndOfACommand() && !n.hasParameters() ? "" : " ";
				
				if(cmdPrefix.length() != 0) {
					v.add(cmdPrefix + " " + n.getName() + suffix);
				} else v.add(n.getName() + suffix);
			}
		}
		
		return v.toArray(new String[v.size()]);
	}
	
	/**
	 * Gets a list of all LSCP commands.
	 * @return An array containing all LSCP commands.
	 */
	public static String[]
	getCommandList() {
		Vector<String> v = new Vector<String>();
		addVariants(LscpTree.getRoot(), v);
		return v.toArray(new String[v.size()]);
	}
	
	private static void
	addVariants(LscpNode node, Vector<String> v) {
		addVariants(node, v, new String(""));
	}
	
	private static void
	addVariants(LscpNode node, Vector<String> v, String s) {
		if(node.getChildren().length == 0) {
			v.add(s + node.getName());
			return;
		}
		
		if(node.isEndOfACommand()) v.add(s + node.getName());
		
		if(node.getName().length() > 0) s = s + node.getName() + " ";
		for(LscpNode n : node.getChildren()) addVariants(n, v, s);
	}
}
