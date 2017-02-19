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

/**
 * Handles abbr and acronym tags
 * @author Phil DeJarnett
 */
public class Abbr extends AbstractNodeHandler {

	/**
	 * Handles abbreviations.
	 *
	 * @param parent The previous node walker, in case we just want to remove an element.
	 * @param node	  Node to handle
	 * @param converter Parent converter for this object.
	 */
	public void handleNode(NodeHandler parent, Element node, DocumentConverter converter) {
		String abbr = converter.cleaner.clean(node);
		if(abbr.length() > 0) {
			converter.output.print(abbr);
			String desc = node.attr("title");
			if(desc.length() > 0) {
				converter.addAbbreviation(abbr, desc);
			}
		}
	}
}
