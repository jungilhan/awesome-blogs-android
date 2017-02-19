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
 * Handles ol and ul lists.
 * @author Phil DeJarnett
 */
public class List extends AbstractNodeHandler {

	public void handleNode(NodeHandler parent, Element node, DocumentConverter converter) {
		// the first node doesn't get a linebreak
		boolean first = true;
		// if this is an ol, it's numbered.
		boolean numericList = node.tagName().equals("ol");
		// keep track of where we are in the list.
		int listCounter = 1;

		// we need to store this, because we're going to replace it for each li below (for padding).
		BlockWriter parentWriter = converter.output;
		parentWriter.startBlock();
		for(final Element child : node.children()) {
			// handle linebreaks between li's
			if(first) {
				first = false;
			} else {
				parentWriter.println();
			}
			// handle starting character
			if(numericList) {
				parentWriter.print(listCounter);
				parentWriter.print(". ");
				if(listCounter < 10) {
					parentWriter.print(' ');
				}
			} else {
				parentWriter.print(" *  ");
			}

			// now, recurse downward, padding the beginning of each line so it looks nice.
			converter.output = new BlockWriter(parentWriter).setPrependNewlineString("    ", true);
			converter.walkNodes(this, child, converter.blockNodes);
			listCounter++;
		}
		// cleanup
		parentWriter.endBlock();
		converter.output = parentWriter;
	}
}
