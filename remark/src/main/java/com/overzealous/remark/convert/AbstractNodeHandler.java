/*
 * Copyright 2011 OverZealous Creations, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.overzealous.remark.convert;

import com.overzealous.remark.util.BlockWriter;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;

import java.util.Map;

/**
 * Contains basic implementations for handling text nodes and ignored HTML elements.
 *
 * @author Phil DeJarnett
 */
public abstract class AbstractNodeHandler implements NodeHandler {

	/**
	 * Handle a child text node.
	 * The default method, implemented here, is to simply write the cleaned
	 * text directly.
	 *
	 * @param node	  Node to handle
	 * @param converter Parent converter for this object.
	 */
	public void handleTextNode(TextNode node, DocumentConverter converter) {
		converter.output.write(converter.cleaner.clean(node));
	}

	/**
	 * Handle an ignored HTMLElement.
	 * The default method here is to either write the HTMLElement as a block if it is a block element,
	 * or write it directly if it is not.
	 *
	 * @param node	  Node to handle
	 * @param converter Parent converter for this object.
	 */
	public void handleIgnoredHTMLElement(Element node, DocumentConverter converter) {
		if(node.isBlock()) {
			converter.output.writeBlock(node.toString());
		} else {
			// Note: because this is an inline element, we want to make sure it stays that way!
			// this means turning off prettyPrinting, so that JSoup doesn't add unecessary spacing around
			// the child nodes.
			Document doc = node.ownerDocument();
			boolean oldPrettyPrint = doc.outputSettings().prettyPrint();
			doc.outputSettings().prettyPrint(false);
			converter.output.write(node.toString());
			doc.outputSettings().prettyPrint(oldPrettyPrint);
		}
	}

	/**
	 * Recursively processes child nodes, and prepends the given string to the output.
	 * @param prepend String to prepend
	 * @param node Starting Node
	 * @param converter Parent document converter
	 * @param nodes Map of valid nodes
	 */
	@SuppressWarnings({"WeakerAccess", "SameParameterValue"})
	protected void prependAndRecurse(String prepend, Element node, DocumentConverter converter, Map<String,NodeHandler> nodes) {
		BlockWriter oldOutput = converter.output;
		converter.output = new BlockWriter(oldOutput).setPrependNewlineString(prepend);
		converter.walkNodes(this, node, nodes);
		converter.output = oldOutput;
	}
}