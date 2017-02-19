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
 * Handles dl, dt, and dd elements
 * @author Phil DeJarnett
 */
public class Definitions extends AbstractNodeHandler {

	public void handleNode(NodeHandler parent, Element node, DocumentConverter converter) {
		// the first child node doesn't get a linebreak
		boolean first = true;
		boolean lastNodeWasDD = false;

		// we need to store this, because we're going to replace it for each dd below (for padding).
		BlockWriter parentWriter = converter.output;

		/* Note on block handling:
		 * We need a gap between each dd and the following dt, like so:
		 *     term
		 *     : definition
		 *
		 *     term
		 *     : definition
		 *
		 * To do this, we wrap the whole thing in a start/end block. Then,
		 * every time we come across a dt, we end a block and start a new one.
		 * The only exception to this rule is if the first node we come to is
		 * a dt - then we don't do anything.
		 */
		parentWriter.startBlock();
		for(final Element child : node.children()) {

			if(child.tagName().equals("dt")) {
				// print term
				if(first) {
					// the first node is a term, so we already started a block.
					first = false;
				} else {
					// add block separation between defs and next term.
					parentWriter.endBlock();
					parentWriter.startBlock();
				}
				converter.walkNodes(this, child, converter.inlineNodes);
				parentWriter.println();
				lastNodeWasDD = false;

			} else if(child.tagName().equals("dd")) {
				// print definition
				if(first) {
					// the first node is a def, so we'll need a new block next time.
					first = false;
				}
				if(lastNodeWasDD) {
					parentWriter.println();
				}
				parentWriter.print(":   ");
				// Is this necessary?  We only allow inline, and inline is always one line, so padding is redundant.
				// Of course, we may want to offer wrapped blocks later, when hardwraps are turned off.
				converter.output = new BlockWriter(parentWriter).setPrependNewlineString("    ", true);
				converter.walkNodes(this, child, converter.blockNodes);
				converter.output = parentWriter;
				
				lastNodeWasDD = true;

			} // else, ignore, bad node
		}
		// cleanup
		parentWriter.endBlock();
	}
}
