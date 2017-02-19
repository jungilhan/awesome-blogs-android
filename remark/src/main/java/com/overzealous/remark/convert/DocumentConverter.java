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

import com.overzealous.remark.IgnoredHtmlElement;
import com.overzealous.remark.Options;
import com.overzealous.remark.util.BlockWriter;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.io.OutputStream;
import java.io.Writer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The class that does the heavy lifting for converting a JSoup Document into
 * valid Markdown
 *
 * @author Phil DeJarnett
 */
public class DocumentConverter {

	// These properties do not change for the life of this converter
	final Options options;
	final TextCleaner cleaner;
	private final Set<String> ignoredHtmlTags;
	final Map<String,NodeHandler> blockNodes;
	final Map<String,NodeHandler> inlineNodes;

	// These properties change for each conversion
	private Map<String,String> linkUrls; // for looking up links via URL
	private int genericLinkUrlCounter;
	private int genericImageUrlCounter;
	private Map<String,String> linkIds; // an inverse of linkUrls, for looking up links via ID
	private Map<String,String> abbreviations; // a cache of abbreviations mapped by abbreviated form
	BlockWriter output; // the output writer, which may change during recursion

	private Map<String,NodeHandler> lastNodeset;

	private static final Pattern COMMA = Pattern.compile(",");
	private static final Pattern LINK_MULTIPLE_SPACES = Pattern.compile(" {2,}", Pattern.DOTALL);
	private static final Pattern LINK_SAFE_CHARS = Pattern.compile("[^-\\w \\.]+", Pattern.DOTALL);
	private static final String LINK_REPLACEMENT = "_";
	private static final Pattern LINK_EDGE_REPLACE = Pattern.compile(String.format("(^%1$s++)|(%1$s++$)", LINK_REPLACEMENT));
	private static final Pattern LINK_MULTIPLE_REPLACE = Pattern.compile(String.format("%1$s{2,}", LINK_REPLACEMENT));
	private static final Pattern LINK_FILENAME = Pattern.compile("/([^/]++)$");


	/**
	 * Creates a DocumentConverted with the given options.
	 * @param options Options for this converter.
	 */
	public DocumentConverter(Options options) {
		// configure final properties
		this.options = options;
		cleaner = new TextCleaner(options);
		ignoredHtmlTags = new HashSet<String>();
		blockNodes = new HashMap<String, NodeHandler>();
		inlineNodes = new HashMap<String, NodeHandler>();

		// configure ignored tags
		for(final IgnoredHtmlElement ihe : options.getIgnoredHtmlElements()) {
			ignoredHtmlTags.add(ihe.getTagName());
		}
		
		configureNodes();
	}

	private void configureNodes() {
		addInlineNode(new InlineStyle(),	"i,em,b,strong,font,span,del,strike,s");
		addInlineNode(new InlineCode(),		"code,tt");
		addInlineNode(new Image(),			"img");
		addInlineNode(new Anchor(),			"a");
		addInlineNode(new Break(),			"br");
		addBlockNode (new Header(),			"h1,h2,h3,h4,h5,h6");
		addBlockNode (new Paragraph(),		"p");
		addBlockNode (new Codeblock(),		"pre");
		addBlockNode (new BlockQuote(),		"blockquote");
		addBlockNode (new HorizontalRule(),	"hr");
		addBlockNode (new List(),			"ol,ul");

		if(options.abbreviations) {
			addInlineNode(new Abbr(),		"abbr,acronym");
		}

		if(options.definitionLists) {
			addBlockNode(new Definitions(),	"dl");
		}


		// TABLES
		if(options.getTables().isConvertedToText()) {
			// if we are going to process it, add the handler
			addBlockNode(new Table(),		"table");

		} else if(options.getTables().isRemoved()) {
			addBlockNode(NodeRemover.getInstance(), "table");

		} // else, it's being added directly
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public Options getOptions() {
		return options;
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public TextCleaner getCleaner() {
		return cleaner;
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public Map<String, NodeHandler> getBlockNodes() {
		return Collections.unmodifiableMap(blockNodes);
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public Map<String, NodeHandler> getInlineNodes() {
		return Collections.unmodifiableMap(inlineNodes);
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public BlockWriter getOutput() {
		return output;
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public void setOutput(BlockWriter output) {
		this.output = output;
	}

	/**
	 * Customize the processing for a node.  This node is added to the
	 * inline list and the block list.  The inline list is used for nodes
	 * that do not contain linebreaks, such as {@code <em>} or {@code <strong>}.
	 *
	 * The tagnames is a comma-delimited list of tagnames for
	 * which this handler should be applied.
	 *
	 * @param handler The handler for the nodes
	 * @param tagnames One or more tagnames
	 */
	@SuppressWarnings({"WeakerAccess"})
	public void addInlineNode(NodeHandler handler, String tagnames) {
		for(final String key : COMMA.split(tagnames)) {
			if(key.length() > 0) {
				inlineNodes.put(key, handler);
				blockNodes.put(key, handler);
			}
		}
	}


	/**
	 * Customize the processing for a node.  This node is added to the
	 * block list only.  The node handler should properly use the
	 * {@link com.overzealous.remark.util.BlockWriter#startBlock()} and
	 * {@link com.overzealous.remark.util.BlockWriter#endBlock()} methods as
	 * appropriate.
	 *
	 * The tagnames is a comma-delimited list of tagnames for
	 * which this handler should be applied.
	 *
	 * @param handler The handler for the nodes
	 * @param tagnames One or more tagnames
	 */
	@SuppressWarnings({"WeakerAccess"})
	public void addBlockNode(NodeHandler handler, String tagnames) {
		for(final String key : COMMA.split(tagnames)) {
			if(key.length() > 0) {
				blockNodes.put(key, handler);
			}
		}
	}

	/**
	 * Convert a document to the given writer.
	 *
	 * <p><strong>Note: It is up to the calling class to handle closing the writer!</strong></p>
	 *
	 * @param doc Document to convert
	 * @param out Writer to receive the final output
	 */
	public void convert(Document doc, Writer out) {
		this.output = new BlockWriter(out, true);
		this.convertImpl(doc);
	}

	/**
	 * Convert a document to the given output stream.
	 *
	 * <p><strong>Note: It is up to the calling class to handle closing the stream!</strong></p>
	 *
	 * @param doc Document to convert
	 * @param out OutputStream to receive the final output
	 */
	public void convert(Document doc, OutputStream out) {
		this.output = new BlockWriter(out, true);
		this.convertImpl(doc);
	}

	/**
	 * Convert a document and return a string.
	 * When wanting a final string, this method should always be used.
	 * It will attempt to calculate the size of the buffer necessary to hold the entire output.
	 *
	 * @param doc Document to convert
	 * @return The Markdown-formatted string.
	 */
	public String convert(Document doc) {
		// estimate the size necessary to handle the final output
		BlockWriter bw = BlockWriter.create(DocumentConverter.calculateLength(doc, 0));
		this.output = bw;
		this.convertImpl(doc);
		return bw.toString();
	}

	// Utility method to quickly walk the DOM tree and estimate the size of the
	// buffer necessary to hold the result.
	private static int calculateLength(Element el, int depth) {
		int result = 0;
		for(final Node n : el.childNodes()) {
			if(n instanceof Element) {
				result += (4 * depth) + calculateLength((Element)n, depth+1);
			} else if(n instanceof TextNode) {
				result += ((TextNode)n).text().length();
			}
		}
		return result;
	}

	// implementation of the convert method.  Basically handles setting up the
	private void convertImpl(Document doc) {
		
		// linked, because we want the resulting list of links in order they were added
		linkIds = new LinkedHashMap<String, String>();
		// To keep track of already added URLs
		linkUrls = new HashMap<String, String>();
		genericImageUrlCounter = 0;
		genericLinkUrlCounter = 0;
		// linked, to keep abbreviations in the order they were added
		abbreviations = new LinkedHashMap<String, String>();

		lastNodeset = blockNodes;

		// walk the DOM
		walkNodes(DefaultNodeHandler.getInstance(), doc.body(), blockNodes);

		if(!linkIds.isEmpty()) {
			// Add links
			output.startBlock();
			for(final Map.Entry<String,String> link : linkIds.entrySet()) {
				output.printf("\n[%s]: %s", link.getKey(), link.getValue());
			}
			output.endBlock();
		}
		if(!abbreviations.isEmpty()) {
			// Add abbreviations
			output.startBlock();
			for(final Map.Entry<String,String> abbr : abbreviations.entrySet()) {
				output.printf("\n*[%s]: %s", abbr.getKey(), cleaner.clean(abbr.getValue()));
			}
			output.endBlock();
		}

		// free up unused properties
		linkIds = null;
		linkUrls = null;
		abbreviations = null;
		output = null;
	}

	/**
	 * Loops over the children of an HTML Element, handling TextNode and child Elements.
	 *
	 * @param currentNode The default node handler for TextNodes and IgnoredHTMLElements.
	 * @param el The parent HTML Element whose children are being looked at.
	 */
	public void walkNodes(NodeHandler currentNode, Element el) {
		walkNodes(currentNode, el, lastNodeset);
	}

	/**
	 * Loops over the children of an HTML Element, handling TextNode and child Elements.
	 *
	 * @param currentNodeHandler The default node handler for TextNodes and IgnoredHTMLElements.
	 * @param el The parent HTML Element whose children are being looked at.
	 * @param nodeList The list of valid nodes at this level.  Should be one of <b>blockNodes</b> or <b>inlineNodes</b>
	 */
	public void walkNodes(NodeHandler currentNodeHandler, Element el, Map<String, NodeHandler> nodeList) {
		Map<String, NodeHandler> backupLastNodeset = lastNodeset;
		lastNodeset = nodeList;
		for(final Node n : el.childNodes()) {
			if(n instanceof TextNode) {
				// It's just text!
				currentNodeHandler.handleTextNode((TextNode) n, this);

			} else if(n instanceof Element) {
				// figure out who can handle this
				Element node = (Element)n;
				String tagName = node.tagName();

				if(nodeList.containsKey(tagName)) {
					// OK, we know how to handle this node
					nodeList.get(tagName).handleNode(currentNodeHandler, node, this);

				} else if(ignoredHtmlTags.contains(tagName)) {
					// User wants to leave this tag in the output.  Naughty user.
					currentNodeHandler.handleIgnoredHTMLElement(node, this);

				} else {
					// No-can-do, just remove the node, and keep on walkin'
					// The only thing we'll do is add block status in if the unknown node
					// usually renders as a block.
					// Due to BlockWriter's intelligent tracking, we shouldn't get a whole bunch
					// of empty lines for empty nodes.
					if(node.isBlock()) {
						output.startBlock();
					}
					walkNodes(currentNodeHandler, node, nodeList);
					if(node.isBlock()) {
						output.endBlock();
					}					
				}
			} // else: not a node we care about (e.g.: comment nodes)
		}
		lastNodeset = backupLastNodeset;
	}

	/**
	 * Recursively processes child nodes and returns the potential output string.
	 * @param currentNode The default node handler for TextNodes and IgnoredHTMLElements.
	 * @param el The parent HTML Element whose children are being looked at.
	 * @return The potential output string.
	 */
	public String getInlineContent(NodeHandler currentNode, Element el) {
		return this.getInlineContent(currentNode, el, false);
	}

	/**
	 * Recursively processes child nodes and returns the potential output string.
	 * @param currentNode The default node handler for TextNodes and IgnoredHTMLElements.
	 * @param el The parent HTML Element whose children are being looked at.
	 * @param undoLeadingEscapes If true, leading escapes are removed
	 * @return The potential output string.
	 */
	public String getInlineContent(NodeHandler currentNode, Element el, boolean undoLeadingEscapes) {
		BlockWriter oldOutput = output;
		output = BlockWriter.create(1000);
		walkNodes(currentNode, el, inlineNodes);
		String ret = output.toString();
		output = oldOutput;
		if(undoLeadingEscapes) {
			ret = cleaner.unescapeLeadingCharacters(ret);
		}
		return ret;
	}

	/**
	 * Adds a link to the link set, and returns the actual ID for the link.
	 *
	 * @param url URL for link
	 * @param recommendedName A recommended name for non-simple link IDs. This might be modified.
	 * @param image If true, use "img-" instead of "link-" for simple link IDs.
	 * @return The actual link ID for this URL.
	 */
	public String addLink(String url, String recommendedName, boolean image) {
		String linkId;
		if(linkUrls.containsKey(url)) {
			linkId = linkUrls.get(url);
		} else {
			if(options.simpleLinkIds) {
				linkId = (image ? "image-" : "") + String.valueOf(linkUrls.size()+1);
			} else {
				recommendedName = cleanLinkId(url, recommendedName, image);
				if(linkIds.containsKey(recommendedName)) {
					int incr = 1;
					while(linkIds.containsKey(String.format("%s %d", recommendedName, incr))) {
						incr++;
					}
					recommendedName = String.format("%s %d", recommendedName, incr);
				}
				linkId = recommendedName;
			}
			linkUrls.put(url, linkId);
			linkIds.put(linkId, url);
		}
		return linkId;
	}

	/**
	 * Adds an abbreviation to the abbreviation set.
	 * @param abbr The abbreviation to be used
	 * @param definition The definition for the abbreviation, should NOT be pre-escaped.
	 */
	void addAbbreviation(String abbr, String definition) {
		if(!abbreviations.containsKey(abbr)) {
			abbreviations.put(abbr, definition);
		}
	}

	String cleanLinkId(String url, String linkId, boolean image) {
		// no newlines
		String ret = linkId.replace('\n', ' ');
		// multiple spaces should be a single space
		ret = LINK_MULTIPLE_SPACES.matcher(ret).replaceAll(" ");
		// remove all characters except letters, numbers, spaces, and some basic punctuation
		ret = LINK_SAFE_CHARS.matcher(ret).replaceAll(LINK_REPLACEMENT);
		// replace multiple underscores with a single underscore
		ret = LINK_MULTIPLE_REPLACE.matcher(ret).replaceAll(LINK_REPLACEMENT);
		// replace underscores on the left or right with nothing
		ret = LINK_EDGE_REPLACE.matcher(ret).replaceAll("");
		// trim any leading or trailing spaces
		ret = ret.trim();
		if(ret.length() == 0 || ret.equals(LINK_REPLACEMENT)) {
			// if we have nothing usable left, use a generic ID
			if(image) {
				if(url != null) {
					Matcher m = LINK_FILENAME.matcher(url);
					if(m.find()) {
						ret = cleanLinkId(null, m.group(1), true);
					} else {
						genericImageUrlCounter++;
						ret = "Image " + genericImageUrlCounter;
					}
				} else {
					genericImageUrlCounter++;
					ret = "Image " + genericImageUrlCounter;
				}
			} else {
				genericLinkUrlCounter++;
				ret = "Link " + genericLinkUrlCounter;
			}
		} // else, use the cleaned id
		return ret;
	}
}
