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

/**
 *
 * @author Grigor Iliev
 */
public class LscpNode {
	private String name;
	private LscpNode[] children;
	private boolean endOfACommand;
	private boolean hasParameters;
	
	/**
	 * Creates a new instance of <code>LscpNode</code>.
	 * @param name The name of the node.
	 */
	public
	LscpNode(String name) { this(name, new LscpNode[0]); }
	
	/**
	 * Creates a new instance of <code>LscpNode</code>.
	 * @param name The name of the node.
	 * @param endOfACommand Determines whether this node can be an end of a command.
	 */
	public
	LscpNode(String name, boolean endOfACommand) {
		this(name, new LscpNode[0], endOfACommand);
	}
	
	/**
	 * Creates a new instance of <code>LscpNode</code>.
	 * @param name The name of the node.
	 * @param endOfACommand Determines whether this node can be an end of a command.
	 * @param hasParameters When the node is an end of a command,
	 * determines whether the command has one or more parameters.
	 */
	public
	LscpNode(String name, boolean endOfACommand, boolean hasParameters) {
		this(name, new LscpNode[0], endOfACommand, hasParameters);
	}
	
	/**
	 * Creates a new instance of <code>LscpNode</code> with
	 * the specified children which are not end of a command.
	 * @param name The name of the node.
	 * @param children The children nodes of this node.
	 */
	public
	LscpNode(String name, LscpNode[] children) {
		this(name, children, false);
	}
	
	/**
	 * Creates a new instance of <code>LscpNode</code>.
	 * @param name The name of the node.
	 * @param children The children nodes of this node.
	 * @param endOfACommand Determines whether this node can be an end of a command.
	 */
	public
	LscpNode(String name, LscpNode[] children, boolean endOfACommand) {
		this(name, children, endOfACommand, true);
	}
	
	/**
	 * Creates a new instance of <code>LscpNode</code>.
	 * @param name The name of the node.
	 * @param children The children nodes of this node.
	 * @param endOfACommand Determines whether this node can be an end of a command.
	 * @param hasParameters When the node is an end of a command,
	 * determines whether the command has one or more parameters.
	 * @see #isEndOfACommand
	 */
	public
	LscpNode(String name, LscpNode[] children, boolean endOfACommand, boolean hasParameters) {
		this.name = name;
		this.children = children;
		this.endOfACommand = endOfACommand;
		this.hasParameters = hasParameters;
	}
	
	/**
	 * Creates a new instance of <code>LscpNode</code> with the specified
	 * child wich is not an end of a command.
	 * @param name The name of the node.
	 * @param child The child node of this node.
	 */
	public
	LscpNode(String name, LscpNode child) {
		this(name, child, false);
	}
	
	/**
	 * Creates a new instance of <code>LscpNode</code>.
	 * @param name The name of the node.
	 * @param child The child node of this node.
	 * @param endOfACommand Determines whether this node can be an end of a command.
	 */
	public
	LscpNode(String name, LscpNode child, boolean endOfACommand) {
		this(name, child, endOfACommand, true);
	}
	
	/**
	 * Creates a new instance of <code>LscpNode</code>.
	 * @param name The name of the node.
	 * @param child The child node of this node.
	 * @param endOfACommand Determines whether this node can be an end of a command.
	 * @param hasParameters When the node is an end of a command,
	 * determines whether the command has one or more parameters.
	 */
	public
	LscpNode(String name, LscpNode child, boolean endOfACommand, boolean hasParameters) {
		this(name, new LscpNode[1], endOfACommand, hasParameters);
		children[0] = child;
	}
	
	/**
	 * Gets the name of this node.
	 * @return The name of this node.
	 */
	public String
	getName() { return name; }
	
	/**
	 * Gets the children nodes of this node.
	 * @return The children nodes of this node.
	 */
	public LscpNode[]
	getChildren() { return children; }
	
	/**
	 * Sets the children nodes of this node.
	 * @param children The new children nodes of this node.
	 */
	public void
	setChildren(LscpNode[] children) { this.children = children; }
	
	/**
	 * Determines whether this node is last keyword of a command.
	 * Note that this method doesn't determine whether this node is a leaf
	 * (example: <code>RESET</code> and <code>RESET CHANNEL</code> commands).
	 * @return <code>true</code> if this node can be an end of a command,
	 * <code>false</code> otherwise.
	 */
	public boolean
	isEndOfACommand() { return endOfACommand || getChildren().length == 0; }
	
	/**
	 * Determines whether the command represented by this path has one or more parameters.
	 * Do <b>not</b> trust this method when this node is not an end of a command.
	 * @see #isEndOfACommand
	 */
	public boolean
	hasParameters() { return hasParameters; }
}
