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
import org.jsoup.nodes.Element;

/**
 * Handles img tags.
 * @author Phil DeJarnett
 */
public class Image extends AbstractNodeHandler {

	/**
	 * Creates a link reference to an image, and renders the correct output.
	 *
	 * @param parent The previous node walker, in case we just want to remove an element.
	 * @param node	  Node to handle
	 * @param converter Parent converter for this object.
	 */
	public void handleNode(NodeHandler parent, Element node, DocumentConverter converter) {
		String url = converter.cleaner.cleanUrl(node.attr("src"));
		String alt = node.attr("alt");
		if(alt == null || alt.trim().length() == 0) {
			alt = node.attr("title");
			if(alt == null) {
				alt = "";
			}
		}
		alt = converter.cleaner.clean(alt.trim());
		if(converter.options.inlineLinks) {
			if(alt.length() == 0) {
				alt = "Image";
			}
			converter.output.printf("![%s](%s)", alt, url);
		} else {
			String linkId = converter.addLink(url, alt, true);
			// give a usable description based on filename whenever possible
			if(alt.length() == 0) {
				alt = linkId;
			}
			BlockWriter out = converter.output;
			if(alt.equals(linkId)) {
				out.printf("![%s][]", linkId);
			} else {
				out.printf("![%s][%s]", alt, linkId);
			}
		}
	}
}
