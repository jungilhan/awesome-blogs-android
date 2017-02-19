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

package com.overzealous.remark;

import java.util.HashSet;
import java.util.Set;

/**
 * This class is used to configure the Remark engine.
 *
 * Standard profiles have been created for a variety of Markdown processors, including:
 * <ul>
 *     <li>{@link #markdown() Standard Markdown}</li>
 *     <li>{@link #markdownExtra() PHP Markdown Extra}</li>
 *     <li>{@link #multiMarkdown() MultiMarkdown}</li>
 *     <li>{@link #pegdownBase() pegdown basic}</li>
 *     <li>{@link #pegdownAllExtensions() pegdown with all extensions}</li>
 *     <li>{@link #github() Github Flavored Markdown}</li>
 * </ul>
 *
 * @author Phil DeJarnett
 */
public class Options implements Cloneable {

	/**
	 * Provides settings to control how Tables are converted.
	 */
	public enum Tables {
		/**
		 * Remove all tables and their contents
		 */
		REMOVE(true,false,false,false,false),
		/**
		 * Leave all tables and their contents as raw HTML (the default, as
		 * this is the recommended syntax from Markdown)
		 */
		LEAVE_AS_HTML(false,true,false,false,false),
		/**
		 * Convert tables to clean code block (compatible with the original Markdown)
		 */
		CONVERT_TO_CODE_BLOCK(false,false,true,true,true),
		/**
		 * Convert tables to the syntax used by PHP Markdown Extra.
		 * @see <a href="http://michelf.com/projects/php-markdown/extra/#table">PHP Markdown Extra Tables</a>
		 */
		MARKDOWN_EXTRA(false,false,true,false,false),
		/**
		 * Convert tables to the syntax used by MultiMarkdown.
		 * @see <a href="http://fletcher.github.com/peg-multimarkdown/#tables">MultiMarkdown Tables</a>
		 */
		MULTI_MARKDOWN(false,false,true,false,true);

		// Private fields
		private final boolean removed;
		private final boolean leftAsHtml;
		private final boolean convertedToText;
		private final boolean renderedAsCode;
		private final boolean colspanEnabled;

		private Tables(boolean removed, boolean leftAsHtml, boolean convertedToText, boolean renderedAsCode, boolean colspanEnabled) {
			this.removed = removed;
			this.leftAsHtml = leftAsHtml;
			this.convertedToText = convertedToText;
			this.renderedAsCode = renderedAsCode;
			this.colspanEnabled = colspanEnabled;
		}

		/**
		 * True if the table is to be fully removed.
		 * @return true or false
		 */
		public boolean isRemoved() {
			return removed;
		}

		/**
		 * True if the table is to be left as raw HTML.
		 * @return true or false
		 */
		public boolean isLeftAsHtml() {
			return leftAsHtml;
		}

		/**
		 * True if the table is to be converted to plain text.
		 * This is true if the result is a Markdown table or a code block.
		 * @return true or false
		 */
		public boolean isConvertedToText() {
			return convertedToText;
		}

		/**
		 * True if the table should be rendered as a code block
		 * @return true or false
		 */
		public boolean isRenderedAsCode() {
			return renderedAsCode;
		}

		/**
		 * True if the table is rendered as a MultiMarkdown table with column spanning.
		 * @return true or false
		 */
		public boolean isColspanEnabled() {
			return colspanEnabled;
		}
	}

	/**
	 * Provides settings to configure if fenced code blocks are used.
	 */
	public enum FencedCodeBlocks {
		/**
		 * Completely disables fenced code blocks.
		 */
		DISABLED(false, ' '),
		/**
		 * Enables fenced code blocks, using multiple {@code '~'} as the separator characters.
		 */
		ENABLED_TILDE(true, '~'),
		/**
		 * Enables fenced code blocks, using multiple {@code '`'} as the separator characters.
		 */
		ENABLED_BACKTICK(true, '`');

		// private fields
		private final boolean enabled;
		private final char separatorCharacter;

		private FencedCodeBlocks(boolean enabled, char separatorCharacter) {
			this.enabled = enabled;
			this.separatorCharacter = separatorCharacter;
		}

		/**
		 * True if fenced code blocks are enabled
		 * @return true or false
		 */
		public boolean isEnabled() {
			return enabled;
		}

		/**
		 * Returns the separator character to use.
		 * @return the separator character
		 */
		public char getSeparatorCharacter() {
			return separatorCharacter;
		}

	}

	/**
	 * Provides options for how to handle in-word emphasis.
	 */
	public enum InWordEmphasis {
		/**
		 * Uses the default mode, which allows in-word emphasis.  Because Remark only uses
		 * asterisks for spacing ({@code '*'}), this mode works with parsers that disable
		 * in-word underscores ({@code '_'}) but not in-word asterisks.
		 */
		NORMAL(true, false),
		/**
		 * Adds spaces around the in-word emphasis characters.  This will actually render different output.
		 *
		 * For example, {@code My<em>Example</em>Word} becomes {@code My *Example* Word}.  This will actually
		 * render as {@code My <em>Example</em> Word}.
		 */
		ADD_SPACES(true, true),
		/**
		 * Removes in-word emphasis altogether.  Designed for parsers that do not allow in-word asterisks
		 * or in-word underscores.
		 *
		 * This means that {@code My<em>Example</em>Word} becomes {@code MyExampleWord}.
		 *
		 */
		REMOVE_EMPHASIS(false, false);

		private final boolean emphasisPreserved;
		private final boolean additionalSpacingNeeded;

		InWordEmphasis(boolean emphasisPreserved, boolean additionalSpacingNeeded) {
			this.emphasisPreserved = emphasisPreserved;
			this.additionalSpacingNeeded = additionalSpacingNeeded;
		}

		/**
		 * Returns whether or not to preserve emphasis at all.
		 * @return true if emphasis should be preserved, false to remove it altogether.
		 */
		public boolean isEmphasisPreserved() {
			return emphasisPreserved;
		}

		/**
		 * Returns true when Remark should add spaces around the emphasis characters.
		 * @return true if spaces should be added.
		 */
		public boolean isAdditionalSpacingNeeded() {
			return additionalSpacingNeeded;
		}
	}

	/**
	 * Creates and returns a new Options set with the default options
	 * compatible with the original Markdown.
	 *
	 * @return Options for original Markdown compatibility
	 */
	public static Options markdown() {
		return new Options();
	}


	/**
	 * Creates and returns a new Options set with the default options
	 * compatible with PHP Markdown Extra features.
	 *
	 * <p>Enables:</p>
	 * <ul>
	 *     <li>headerIDs</li>
	 *     <li>Markdown Extra fencedCodeBlocks</li>
	 *     <li>Markdown Extra tables</li>
	 *     <li>definitionLists</li>
	 *     <li>abbreviations</li>
	 * </ul>
	 *
	 * @return Options for PHP Markdown Extra compatibility
	 */
	public static Options markdownExtra() {
		Options opts = new Options();
		opts.headerIds = true;
		opts.fencedCodeBlocks = FencedCodeBlocks.ENABLED_TILDE;
		opts.tables = Tables.MARKDOWN_EXTRA;
		opts.definitionLists = true;
		opts.abbreviations = true;
		return opts;
	}


	/**
	 * Creates and returns a new Options set with the default options
	 * compatible with MultiMarkdown features.
	 *
	 * <p>Enables:</p>
	 * <ul>
	 *     <li>MultiMarkdown tables</li>
	 *     <li>definitionLists</li>
	 * </ul>
	 *
	 * @return Options for MultiMarkdown compatibility
	 */
	public static Options multiMarkdown() {
		Options opts = new Options();
		opts.tables = Tables.MULTI_MARKDOWN;
		opts.definitionLists = true;
		return opts;
	}


	/**
	 * Creates and returns a new Options set with the default options
	 * compatible with the base pegdown configuration.
	 *
	 * <p>Please note: if you are using pegdown version 1.0.2 or older, you'll need to
	 * manually enable {@link #fixPegdownStrongEmphasisInLinks}.</p>
	 *
	 * <p>Enables:</p>
	 * <ul>
	 *     <li>hardwraps</li>
	 * </ul>
	 *
	 * @return Options for pegdown compatibility
	 */
	@SuppressWarnings({"WeakerAccess"})
	public static Options pegdownBase() {
		Options opts = new Options();
		opts.inWordEmphasis = InWordEmphasis.REMOVE_EMPHASIS;
		return opts;
	}


	/**
	 * Creates and returns a new Options set with the default options
	 * compatible with pegdown configured with all extensions.
	 *
	 * <p>Please note: if you are using pegdown version 1.0.2 or older, you'll need to
	 * manually enable {@link #fixPegdownStrongEmphasisInLinks}.</p>
	 *
	 * <p>Enables:</p>
	 * <ul>
	 *     <li>hardwraps</li>
	 *     <li>Markdown Extra fencedCodeBlocks</li>
	 *     <li>MultiMarkdown tables</li>
	 *     <li>definitionLists</li>
	 *     <li>abbreviations</li>
	 *     <li>autoLinks</li>
	 *     <li>reverses all Smart options</li>
	 * </ul>
	 *
	 * @return Options for pegdown compatibility
	 */
	public static Options pegdownAllExtensions() {
		Options opts = pegdownBase();
		opts.hardwraps = true;
		opts.fencedCodeBlocks = FencedCodeBlocks.ENABLED_TILDE;
		opts.reverseHtmlSmartPunctuation = true;
		opts.reverseHtmlSmartQuotes = true;
		opts.reverseUnicodeSmartPunctuation = true;
		opts.reverseUnicodeSmartQuotes = true;
		opts.autoLinks = true;
		opts.tables = Tables.MULTI_MARKDOWN;
		opts.definitionLists = true;
		opts.abbreviations = true;
		return opts;
	}


	/**
	 * Creates and returns a new Options set with the default options
	 * compatible with github-flavored Markdown.
	 *
	 * <p>Enables:</p>
	 * <ul>
	 *     <li>hardwraps</li>
	 *     <li>Github fencedCodeBlocks</li>
	 *     <li>tables converted to code block</li>
	 *     <li>autoLinks</li>
	 * </ul>
	 *
	 * @return Options for github compatibility
	 */
	public static Options github() {
		Options opts = new Options();
		opts.hardwraps = true;
		opts.fencedCodeBlocks = FencedCodeBlocks.ENABLED_BACKTICK;
		opts.autoLinks = true;
		opts.tables = Tables.CONVERT_TO_CODE_BLOCK;
		return opts;
	}
	
	/**
	 * If true, {@code <br/>}s are replaced with a simple linebreak.
	 * <p>If false, {@code <br/>}s are replaced with a two spaces followed by a linebreak (default).</p>
	 */
	public boolean hardwraps = false;

	/**
	 * Configures how in-word emphasis is handled.
	 */
	public InWordEmphasis inWordEmphasis = InWordEmphasis.NORMAL;

    /**
     * If true, relative links are preserved. <strong>You must still provide a baseURI!</strong>
     * <p>Otherwise, relative links are resolved against the provided baseURI (the default).</p>
     */
    public boolean preserveRelativeLinks = false;
    
	/**
	 * If true, place the URLs for links inline.
	 * <p>Otherwise, generate link IDs and place at the end (the default).</p>
	 */
	public boolean inlineLinks = false;

	/**
	 * If true, link IDs are simply incremented as they are found.
	 * <p>Otherwise, Remark attempts to generate unique link IDs based on the link description.</p>
	 */
	public boolean simpleLinkIds = false;

	/**
	 * Configures how tables are handled.
	 */
	public Tables tables = Tables.LEAVE_AS_HTML;

	/**
	 * If true, replace all smart quote HTML entities (e.g:
	 * {@code &ldquo;} with simplified characters (e.g: {@code "}).
	 */
	public boolean reverseHtmlSmartQuotes = false;

	/**
	 * If true, replace all smart quote unicode characters (e.g:
	 * &#0147) with simplified characters (e.g: {@code "}).
	 */
	public boolean reverseUnicodeSmartQuotes = false;

	/**
	 * If true, replace all smart punctuation HTML entities (e.g:
	 * {@code &emdash;}) with simplified characters (e.g: {@code ---}).
	 */
	public boolean reverseHtmlSmartPunctuation = false;

	/**
	 * If true, replace all smart punctuation unicode characters (e.g:
	 * &#0151;) with simplified characters (e.g: {@code ---}).
	 */
	public boolean reverseUnicodeSmartPunctuation = false;

	/**
	 * If true, enable remarking definitions lists.  When enabled, definition
	 * lists ({@code <dl>}, {@code <dt>}, and {@code <dd>})
	 * are converted into <a href="http://michelf.com/projects/php-markdown/extra/#def-list">PHP Markdown Extra style definition lists</a>.
	 */
	public boolean definitionLists = false;

	/**
	 * If true, enable remarking abbreviations.  When enabled, {@code <abbr>} tags are converted
	 * into <a href="http://michelf.com/projects/php-markdown/extra/#abbr">PHP Markdown Extra style abbreviations</a>.
	 */
	public boolean abbreviations = false;

	/**
	 * If true, enable autoLinks.  This only affects links whose {@code href} attribute is the same
	 * as the node's inner text content.  In this case, the URL is simply written directly to the
	 * output.
	 *
	 * Example:
	 *     <blockquote>{@code <a href="http://www.example.com">http://www.example.com</a>}</blockquote>
	 * becomes
	 *     <blockquote>{@code http://www.example.com}</blockquote>
	 */
	public boolean autoLinks = false;

	/**
	 * If true, enable remarking header IDs.  When enabled, the ID of header tags will be converted
	 * into <a href="http://michelf.com/projects/php-markdown/extra/#header-id">PHP Markdown Extra style header IDs</a>.
	 */
	public boolean headerIds = false;

	/**
	 * Configures how to handle code blocks.  By default, code blocks are only configured
	 * using the indented format supported by Markdown.  This allows fenced code blocks when necessary.
	 */
	@SuppressWarnings({"WeakerAccess"})
	public FencedCodeBlocks fencedCodeBlocks = FencedCodeBlocks.DISABLED;

	/**
	 * Number of times to repeat the fencedCodeBlock character. (Defaults to 10.)
	 */
	@SuppressWarnings({"CanBeFinal"})
	public int fencedCodeBlocksWidth = 10;

	/**
	 * Allows the addition of extra HTML tags that can be left in the output.
	 * Please note that this does not override default handling (for example, {@code <em>} tags).
	 */
	@SuppressWarnings({"WeakerAccess"})
	public Set<IgnoredHtmlElement> ignoredHtmlElements = new HashSet<IgnoredHtmlElement>();

	/**
	 * This is a very specific fix for a very specific bug.  As of version 1.0.2, pegdown has a serious bug that
	 * really slows down when processing many links with <strong>bold</strong> and <em>italics</em> in the label for
	 * an anchor.
	 *
	 * This option causes Remark to replace items like {@code [***my important link***][link-id]} with just
	 * bold text, like {@code [**my important link**][link-id]}.
	 *
	 * <strong>Note: this was fixed in release 1.1.0!</strong>
	 *
	 * @see <a href="https://github.com/sirthias/pegdown/issues/34">Pegdown Issue #34</a>
	 */
	@SuppressWarnings({"CanBeFinal"})
	public boolean fixPegdownStrongEmphasisInLinks = false;


	/**
	 * Configures a default set of options.
	 * The default set is configured to be most compatible with the original Markdown syntax.
	 */
	public Options() {

	}

	/**
	 * Always returns a non-null setting for FencedCodeBlocks
	 * @return The current FencedCodeBlocks or default if not set.
	 */
	public FencedCodeBlocks getFencedCodeBlocks() {
		if(fencedCodeBlocks == null) {
			fencedCodeBlocks = FencedCodeBlocks.DISABLED;
		}
		return fencedCodeBlocks;
	}

	/**
	 * Always returns a non-null setting for IgnoredHtmlElements
	 * @return The current set of IgnoredHtmlElements or an empty set if null.
	 */
	public Set<IgnoredHtmlElement> getIgnoredHtmlElements() {
		if(ignoredHtmlElements == null) {
			ignoredHtmlElements = new HashSet<IgnoredHtmlElement>();
		}
		return ignoredHtmlElements;
	}

	/**
	 * Always returns a non-null setting for Tables
	 * @return the current Tables or default if not set.
	 */
	public Tables getTables() {
		if(tables == null) {
			tables = Tables.LEAVE_AS_HTML;
		}
		return tables;
	}

	/**
	 * Always returns a non-null setting for InWordEmphasis
	 * @return the current InWordEmphasis or default if not set.
	 */
	public InWordEmphasis getInWordEmphasis() {
		if(inWordEmphasis == null) {
			inWordEmphasis = InWordEmphasis.NORMAL;
		}
		return inWordEmphasis;
	}

	/**
	 * Utility method to set reversing of both unicode and html
	 * smart quotes.
	 * 
	 * @param reverse true if they should be reversed
	 */
	public void setReverseSmartQuotes(boolean reverse) {
		this.reverseHtmlSmartQuotes = reverse;
		this.reverseUnicodeSmartQuotes = reverse;
	}
	

	/**
	 * Utility method to set reversing of both unicode and html
	 * smart punctuation.
	 * 
	 * @param reverse true if they should be reversed
	 */
	public void setReverseSmartPunctuation(boolean reverse) {
		this.reverseHtmlSmartPunctuation = reverse;
		this.reverseUnicodeSmartPunctuation = reverse;
	}
	

	/**
	 * Utility method to set reversing of both unicode and html
	 * smart quotes and punctuation.
	 * 
	 * @param reverse true if they should be reversed
	 */
	@SuppressWarnings({"UnusedDeclaration"})
	public void setReverseAllSmarts(boolean reverse) {
		setReverseSmartQuotes(reverse);
		setReverseSmartPunctuation(reverse);
	}
	
	public Options getCopy() {
		Options copy;
		try {
			copy = (Options)this.clone();
		} catch(CloneNotSupportedException e) {
			throw new RuntimeException("Should never happen");
		}
		return copy;
	}
}
