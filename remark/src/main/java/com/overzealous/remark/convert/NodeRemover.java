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
 * @author Phil DeJarnett
 */
public class NodeRemover implements NodeHandler {

	private static NodeRemover instance;

	private NodeRemover() {
		// singleton
	}

	public static NodeRemover getInstance() {
		if(instance == null) {
			instance = new NodeRemover();
		}
		return instance;
	}

	public void handleNode(NodeHandler parent, Element node, DocumentConverter converter) {
		// do nothing, node is removed.
	}

	public void handleTextNode(TextNode node, DocumentConverter converter) {
		// do nothing, node is removed.
	}

	public void handleIgnoredHTMLElement(Element node, DocumentConverter converter) {
		// do nothing, node is removed.
	}
}
