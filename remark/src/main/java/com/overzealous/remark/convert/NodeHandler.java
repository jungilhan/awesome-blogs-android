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

import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;

/**
 * Interface for classes that handle processing HTML Elements.
 * @author Phil DeJarnett
 */
public interface NodeHandler {

	/**
	 * Handles an HTML Element node.  This is where most of the work is done.
	 *
	 * Which NodeHandler is used is based on the tagName of the element.
	 *
	 * @param parent The previous node walker, in case we just want to remove an element.
	 * @param node Node to handle
	 * @param converter Parent converter for this object.
	 */
	public void handleNode(NodeHandler parent, Element node, DocumentConverter converter);

	/**
	 * Handle a child text node.
	 *
	 * @param node Node to handle
	 * @param converter Parent converter for this object.
	 */
	public void handleTextNode(TextNode node, DocumentConverter converter);

	/**
	 * Handle an ignored HTMLElement.
	 * @param node Node to handle
	 * @param converter Parent converter for this object.
	 */
	public void handleIgnoredHTMLElement(Element node, DocumentConverter converter);

}
