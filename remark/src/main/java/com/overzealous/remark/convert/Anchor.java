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

import java.util.regex.Pattern;

/**
 * Handles anchor (a) tags, both links and named anchors.
 * @author Phil DeJarnett
 */
public class Anchor extends AbstractNodeHandler {

	private static final Pattern INLINE_LINK_ESCAPE = Pattern.compile("([\\(\\)])");
	private static final String INLINE_LINK_REPLACEMENT = "\\\\$1";

	/**
	 * Creates a link reference, and renders the correct output.
	 *
	 * If this happens to be a named anchor, then it is simply removed from output.
	 *
	 * @param parent The previous node walker, in case we just want to remove an element.
	 * @param node	  Node to handle
	 * @param converter Parent converter for this object.
	 */
	public void handleNode(NodeHandler parent, Element node, DocumentConverter converter) {
		if(node.hasAttr("href") && node.attr("href").trim().length() > 0) {
			// Must be a real link.
			String url = converter.cleaner.cleanUrl(node.attr("href"));
			String label = converter.getInlineContent(this, node);

			if(label.length() > 0) {
				if(converter.options.autoLinks && url.equals(label)) {
					// embed autolink
					converter.output.write(label);
				} else if(converter.options.inlineLinks) {
					// standard link
					if(converter.options.fixPegdownStrongEmphasisInLinks) {
						label = label.replace("***", "**");
					}
					converter.output.printf("[%s](%s)", label, url);
				} else {
					// standard link
					if(converter.options.fixPegdownStrongEmphasisInLinks) {
						label = label.replace("***", "**");
					}
					String linkId = converter.addLink(url, label, false);
					if(label.equals(linkId)) {
						converter.output.printf("[%s][]", label);
					} else {
						converter.output.printf("[%s][%s]", label, linkId);
					}
				}
			} // else, ignore links with no label
		} else {
			// named anchor, not a link
			// simply remove it from the flow.
			converter.walkNodes(parent, node);
		}
	}
}
