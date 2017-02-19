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

import com.overzealous.remark.Options;
import com.overzealous.remark.util.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is used to clean up plain text fields based on the selected set of options.
 * It optionally escapes certain special characters, as well as replacing various
 * HTML and Unicode entities with their plaintext equivalents.
 *
 * @author Phil DeJarnett
 */
public class TextCleaner {

	/**
	 * Internal class simply used to hold the various escape regexes.
	 */
	private class Escape {
		final Pattern pattern;
		final String replacement;
		public Escape(String pattern, String replacement) {
			this.pattern = Pattern.compile(pattern);
			this.replacement = replacement;
		}
	}

	/** Used to track the replacements based on matched groups. */
	private Map<String, String> replacements;
	/** Compiled entity replacement pattern. */
	private Pattern entityReplacementsPattern;
	/** Compiled unicode replacement pattern. */
	private Pattern unicodeReplacementsPattern = null;
	/** List of possible escapes */
	private List<Escape> escapes;
	private Pattern unescapeLeadingChars;

	private static final Pattern EMPTY_MATCHER = Pattern.compile("\\s+", Pattern.DOTALL);
	private static final Pattern LINEBREAK_REMOVER = Pattern.compile("(\\s*\\r?+\\n)+");
	
	private static final Pattern URL_CLEANER = Pattern.compile("([\\(\\) ])");

	/**
	 * Create a new TextCleaner based on the configured options.
	 * @param options Options that will affect what is cleaned.
	 */
	public TextCleaner(Options options) {
		setupReplacements(options);
		setupEscapes(options);
	}

	/**
	 * Configures the basic replacements based on the configured options.
	 * @param options Options that will affect what is replaced.
	 */
	@SuppressWarnings({"OverlyLongMethod"})
	private void setupReplacements(Options options) {
		this.replacements = new HashMap<String, String> ();

		// build replacement regex
		StringBuilder entities = new StringBuilder(replacements.size()*5);

		// this is a special case for double-encoded HTML entities.
		entities.append("&(?>amp;([#a-z0-9]++;)|(?>");
		addRepl(entities, "&amp;", "&");
		addRepl(entities, "&lt;", "<");
		addRepl(entities, "&gt;", ">");
		addRepl(entities, "&quot;", "\"");
		if(options.reverseHtmlSmartQuotes) {
			addRepl(entities, "&ldquo;", "\"");
			addRepl(entities, "&rdquo;", "\"");
			addRepl(entities, "&lsquo;", "\'");
			addRepl(entities, "&rsquo;", "\'");
			addRepl(entities, "&apos;", "\'");
			addRepl(entities, "&laquo;", "<<");
			addRepl(entities, "&raquo;", ">>");
		}
		if(options.reverseHtmlSmartPunctuation) {
			addRepl(entities, "&ndash;", "--");
			addRepl(entities, "&mdash;", "---");
			addRepl(entities, "&hellip;", "...");
		}
		entities.replace(entities.length()-1, entities.length(), ");)");

		entityReplacementsPattern = Pattern.compile(entities.toString(), Pattern.CASE_INSENSITIVE);

		if(options.reverseUnicodeSmartPunctuation || options.reverseUnicodeSmartQuotes) {
			StringBuilder unicode = new StringBuilder("[\\Q");
			if(options.reverseUnicodeSmartQuotes) {
				addRepl(unicode, "\u201c", "\""); // left double quote: “
				addRepl(unicode, "\u201d", "\""); // right double quote: ”
				addRepl(unicode, "\u2018", "\'"); // left single quote: ‘
				addRepl(unicode, "\u2019", "\'"); // right single quote: ’
				addRepl(unicode, "\u00ab", "<<"); // left angle quote: «
				addRepl(unicode, "\u00bb", ">>"); // right angle quote: »
			}
			if(options.reverseUnicodeSmartPunctuation) {
				addRepl(unicode, "\u2013", "--"); // en-dash: –
				addRepl(unicode, "\u2014", "---"); // em-dash: —
				addRepl(unicode, "\u2026", "..."); // ellipsis: …
			}
			unicode.append("\\E]");
			unicodeReplacementsPattern = Pattern.compile(unicode.toString());
		}
	}

	/**
	 * Utility method to make the code above easier to read.
	 * @param regex A character buffer to append the replacement to
	 * @param original Original character or string.
	 * @param replacement Replacement character or string.
	 */
	private void addRepl(StringBuilder regex, String original, String replacement) {
		replacements.put(original, replacement);
		if(original.charAt(0) == '&') {
			// add entity
			regex.append(original.substring(1, original.length() - 1));
			regex.append('|');
		} else {
			// add single character
			regex.append(original);
		}
	}

	/**
	 * Configures the basic escapes based on the configured options.
	 * @param options Options that will affect what is escaped.
	 */
	private void setupEscapes(Options options) {
		escapes = new ArrayList<Escape>();

		// confusingly, this replaces single backslashes with double backslashes.
		// Man, I miss Groovy's slashy strings in these moments...
		escapes.add(new Escape("\\\\", "\\\\\\\\"));

		// creates an set of characters that are universally escaped.
		// these characters are wrapped in \Q...\E to ensure they aren't treated as special characters.
		StringBuilder chars = new StringBuilder("([\\Q`*_{}[]#");
		if(options.tables.isConvertedToText() && !options.tables.isRenderedAsCode()) {
			chars.append('|');
		}
		chars.append("\\E])");
		escapes.add(new Escape(chars.toString(), "\\\\$1"));

		// finally, escape certain characters only if they are leading characters
		StringBuilder leadingChars = new StringBuilder("^( ?+)([\\Q-+");
		if(options.definitionLists) {
			leadingChars.append(':');
		}
		leadingChars.append("\\E])");
		escapes.add(new Escape(leadingChars.toString(), "$1\\\\$2"));

		// setup the leading character reverser
		// this is a bit of a hack to undo leading character escapes.
		unescapeLeadingChars = Pattern.compile(leadingChars.insert(6, "\\\\").toString());
	}

	/**
	 * Clean the given input text based on the original configuration Options.
	 * Newlines are also replaced with a single space.
	 *
	 * @param input The text to be cleaned. Can be any object. JSoup nodes are handled specially.
	 * @return The cleaned text.
	 */
	public String clean(Object input) {
		return clean(input, true);
	}

	/**
	 * Clean the given input text based on the original configuration Options.
	 * The text is treat as code, so it is not escaped, and newlines are preserved.
	 *
	 * @param input The text to be cleaned. Can be any object. JSoup nodes are handled specially.
	 * @return The cleaned text.
	 */
	public String cleanCode(Object input) {
		return clean(input, false);
	}

	/**
	 * Clean the given input text based on the original configuration Options.
	 * Optionally, don't escape special characters.
	 *
	 * @param oinput The text to be cleaned. Can be any object. JSoup nodes are handled specially.
	 * @param normalText If false, don't escape special characters.  This is usually only used for
	 * 					 inline code or code blocks, because they don't need to be escaped.
	 * @return The cleaned text.
	 */
	private String clean(Object oinput, boolean normalText) {
		String input;
		if(oinput instanceof TextNode) {
			input = getTextNodeText((TextNode)oinput, normalText);
		} else if(oinput instanceof Element) {
			if(normalText) {
				input = ((Element)oinput).text();
			} else {
				input = getPreformattedText((Element)oinput);
			}
		} else {
			input = oinput.toString();
		}
		String result;
		if(input.length() == 0) {
			// not seen, so just return an empty string.
			result = "";
		} else if(normalText) {
			// For non-code text, newlines are _never_ allowed.
			// Replace one or more set of whitespace chars followed by a newline with a single space.
			input = LINEBREAK_REMOVER.matcher(input).replaceAll(" ");

			// now escape special characters.
			for(final Escape rep : escapes) {
				input = rep.pattern.matcher(input).replaceAll(rep.replacement);
			}
			StringBuffer output = doReplacements(input, entityReplacementsPattern);
			if(unicodeReplacementsPattern != null) {
				output = doReplacements(output, unicodeReplacementsPattern);
			}
			result = output.toString();
		} else {
			// we have to revert ALL HTML entities for code, because they will end up
			// double-encoded by markdown
			// we also don't need to worry about escaping anything
			// note: we have to manually replace &apos; because it is ignored by StringEscapeUtils for some reason.
			result = StringEscapeUtils.unescapeHtml4(input.replace("&apos;", "'"));
		}
		return result;
	}

	/**
	 * Replaces all {@code <br/>} tags with a newline in a copy of the input node, and
	 * returns the resulting innter text.
	 * This is necessary to ensure that manual linebreaks are supported in preformatted code.
	 * 
	 * @param oinput Preformatted node to process
	 * @return inner text of the node.
	 */
	private String getPreformattedText(Element oinput) {
		Element el = oinput.clone();
		fixLineBreaks(el);
		return el.text();
	}
	
	// recursively processes the element to replace <br>'s with \n
	private void fixLineBreaks(Element el) {
		for(final Element e : el.children()) {
			if(e.tagName().equals("br")) {
				e.before("\n");
				e.remove();
			} else {
				fixLineBreaks(e);
			}
		}
	}

	/**
	 * Handles running the regex-based replacements in the input
	 * @param input String to process
	 * @param regex Pattern to use
	 * @return cleaned up input string
	 */
	private StringBuffer doReplacements(CharSequence input, Pattern regex) {
		StringBuffer output = new StringBuffer();

		Matcher m = regex.matcher(input);
		while (m.find()) {
			String repString;
			// if we have a hard match, do a simple replacement.
			String replacementKey = m.group().toLowerCase(Locale.ENGLISH);
			if(replacements.containsKey(replacementKey)) {
				repString = replacements.get(replacementKey);
			} else {
				// special case for escaped HTML entities.
				repString = "\\\\&$1";
			}
			m.appendReplacement(output, repString);
		}
		m.appendTail(output);

		return output;
	}

	/**
	 * Method to clean inline code, and, if necessary, add spaces to make sure that internal, leading, or
	 * trailing {@code '`'} characters don't break the inline code.
	 * Newlines are also replaced with spaces.
	 *
	 * This method also adds the leading and trailing {@code '`'} or {@code '```'} as necessary.
	 *
	 * @param input String to clean. Can be any object. JSoup nodes are handled specially.
	 * @return The cleaned text.
	 */
	public String cleanInlineCode(Object input) {
		String output = clean(input, false).replace('\n', ' ');
		if(output.indexOf('`') != -1) {
			String prepend = "";
			if(output.charAt(0) == '`') {
				prepend = " ";
			}
			String append = "";
			if(output.charAt(output.length()-1) == '`') {
				append = " ";
			}
			String delim = getDelimiter(output);
			output = String.format("%s%s%s%s%s", delim, prepend, output, append, delim);
		} else {
			output = String.format("`%s`", output);
		}
		return output;
	}

	/**
	 * Removes the escaping on leading characters, for example, when they are going to be rendered inside
	 * another node, such as a table.
	 * @param input String to process
	 * @return Cleaned string.
	 */
	public String unescapeLeadingCharacters(String input) {
		// removes any leading escapes...
		return unescapeLeadingChars.matcher(input).replaceAll("$1$2");
	}

	/**
	 * Handles escaping special characters in URLs to avoid issues when they are rendered out
	 * (ie: spaces, parentheses)
	 * @param input URL to process
	 * @return Cleaned URL
	 */
	public String cleanUrl(String input) {
		StringBuffer output = new StringBuffer();

		Matcher m = URL_CLEANER.matcher(input);
		while (m.find()) {
			char c = m.group().charAt(0);
			m.appendReplacement(output, String.format("%%%02x", (int)c));
		}
		m.appendTail(output);
		return output.toString();
	}

	String getDelimiter(String input) {
		int max = 0;
		int counter = 0;
		for(int i=0; i<input.length(); i++) {
			if(input.charAt(i) == '`') {
				counter++;
			} else {
				max = Math.max(max, counter);
				counter = 0;
			}
		}
		// check in case the last tick was at the end.
		max = Math.max(max, counter);
		return StringUtils.multiply('`', max + 1);
	}

	private String getTextNodeText(TextNode tn, boolean normalText) {
		String input = normalText ? tn.text() : tn.getWholeText();
		Node prev = tn.previousSibling();
		Node next = tn.nextSibling();
		boolean parentIsBlock = isBlock(tn.parent());
		if(isBlock(prev)) {
			input = ltrim(input);
		} else if(prev == null && parentIsBlock) {
			input = ltrim(input);
		} else if(normalText && prev instanceof TextNode) {
			TextNode tprev = (TextNode)prev;
			if(EMPTY_MATCHER.matcher(tprev.text()).matches()) {
				input = ltrim(input);
			}
		}
		if(input.length() > 0) {
			if(isBlock(next)) {
				input = rtrim(input);
			} else if(next == null && parentIsBlock) {
				input = rtrim(input);
			} else if(normalText && next instanceof TextNode) {
				TextNode tnext = (TextNode)next;
				if(EMPTY_MATCHER.matcher(tnext.text()).matches()) {
					input = rtrim(input);
				}
			}
		}
		return input;
	}

	private boolean isBlock(Node n) {
		boolean block = false;
		if(n != null && n instanceof Element) {
			Element el = (Element)n;
			block = el.isBlock() || el.tagName().equals("br");
		}
		return block;
	}

	private String ltrim(String s) {
		int start = 0;
		while((start+1 <= s.length()) &&
					  EMPTY_MATCHER.matcher(s.substring(start, start+1)).matches()) {
			start++;
		}
		String ret = "";
		if(start != s.length()) {
			ret = s.substring(start);
		}
		return ret;
	}

	private String rtrim(String s) {
		int end = s.length();
		while((end-1 >= 0) &&
					  EMPTY_MATCHER.matcher(s.substring(end-1, end)).matches()) {
			end--;
		}
		String ret = "";
		if(end != 0) {
			ret = s.substring(0, end);
		}
		return ret;
	}

}
