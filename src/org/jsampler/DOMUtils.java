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

package org.jsampler;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import org.xml.sax.SAXException;


/**
 * A collection of utility methods for DOM.
 * @author Grigor Iliev
 */
public class DOMUtils {
	
	/** Forbits instantiation of this class. */
	private DOMUtils() { }
	
	/**
	 * Creates an empty document.
	 * @throws RuntimeException if the creation failed.
	 */
	public static Document
	createEmptyDocument() {
		Document doc = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.newDocument();
		} catch(ParserConfigurationException x) {
			throw new RuntimeException("Failed to create new document!", x);
		}
		
		return doc;
	}
	/**
	 * Parses the input from the specified input stream and
	 * returns a new document providing the content read from the stream.
	 * @param in Provides the content to be parsed.
	 * @return A new document providing the content read from the stream.
	 * @throws RuntimeException if the parsing failed.
	 */
	public static Document
	readObject(InputStream in) {
		Document doc = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.parse(in);
		} catch(ParserConfigurationException x) {
			throw new RuntimeException("Parsing failed", x);
		} catch(SAXException x) {
			throw new RuntimeException("Parsing failed", x);
		} catch(IOException x) {
			throw new RuntimeException("Parsing failed", x);
		} catch(IllegalArgumentException x) {
			throw new RuntimeException("Parsing failed", x);
		}
		
		return doc;
	}
	
	/**
	 * Writes the content of document <code>doc</code> to the specified output stream.
	 * @param doc The document to be written.
	 * @param out The output stream where the document should be written.
	 * @throws RuntimeException if the operation failed.
	 */
	public static void
	writeObject(Document doc, OutputStream out) {
		try {
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(out);
			transformer.transform(source, result);
		} catch(TransformerConfigurationException x) {
			throw new RuntimeException("Failed to write the document!", x);
		} catch(TransformerException x) {
			throw new RuntimeException("Failed to write the document!", x);
		}	
	}
	
	/**
	 * Validates a text node.
	 * @throws IllegalArgumentException If the node is not a text node.
	 */
	public static void
	validateTextContent(Node node) throws IllegalArgumentException {
		if (
			node.getChildNodes().getLength() != 1 ||
			node.getFirstChild().getNodeType() != Node.TEXT_NODE
		) { throw new IllegalArgumentException(node.getNodeName() + ": Not a text node"); }
	}
}
