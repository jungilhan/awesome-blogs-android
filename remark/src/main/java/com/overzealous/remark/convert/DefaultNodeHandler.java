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
 * Default handler for unknown top-level nodes.
 * @author Phil DeJarnett
 */
public class DefaultNodeHandler extends AbstractNodeHandler {

	private static DefaultNodeHandler instance;

	public static DefaultNodeHandler getInstance() {
		if(instance == null) {
			instance = new DefaultNodeHandler();
		}
		return instance;
	}

	protected DefaultNodeHandler() {
		// exists for Singleton pattern
	}

	/**
	 * For the default state element, the nodes are simply ignored, recursing as necessary.
	 *
	 * @param parent The previous node walker, in case we just want to remove an element.
	 * @param node	  Node to handle
	 * @param converter Parent converter for this object.
	 */
	public void handleNode(NodeHandler parent, Element node, DocumentConverter converter) {
		converter.walkNodes(this, node, converter.blockNodes);
	}
}
