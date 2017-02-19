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

package com.overzealous.remark.util;

/**
 * This class contains the contents of a table cell.  It's used to help keep track of
 * information about the table so the final table can be built with clean formatting.
 *
 * @author Phil DeJarnett
 */
public class MarkdownTableCell {

	private MarkdownTable.Alignment alignment = MarkdownTable.Alignment.LEFT;

	private String contents = "";

	private int colspan = 1;

	/**
	 * Creates a new, empty MarkdownTableCell
	 */
	@SuppressWarnings({"UnusedDeclaration"})
	public MarkdownTableCell() {
		this("", MarkdownTable.Alignment.LEFT, 1);
	}

	/**
	 * Creates a new MarkdownTableCell with only contents
	 * @param contents The contents of this cell
	 */
	public MarkdownTableCell(String contents) {
		this(contents, MarkdownTable.Alignment.LEFT, 1);
	}

	/**
	 * Creates a new MarkdownTableCell with contents and alignment
	 * @param contents The contents of this cell
	 * @param alignment The alignment of this cell (if specified)
	 */
	public MarkdownTableCell(String contents, MarkdownTable.Alignment alignment) {
		this(contents, alignment, 1);
	}

	/**
	 * Creates a new MarkdownTableCell with contents and a colspan
	 * @param contents The contents of this cell
	 * @param colspan The number of columns this cell spans
	 */
	public MarkdownTableCell(String contents, int colspan) {
		this(contents, MarkdownTable.Alignment.LEFT, colspan);
	}

	/**
	 * Creates a new MarkdownTableCell with contents and a colspan
	 * @param contents The contents of this cell
	 * @param alignment The alignment of this cell (if specified)
	 * @param colspan The number of columns this cell spans
	 */
	public MarkdownTableCell(String contents, MarkdownTable.Alignment alignment, int colspan) {
		this.setContents(contents);
		this.setAlignment(alignment);
		this.setColspan(colspan);
	}

	/**
	 * Gets the text-alignment of this cell
	 * @return The alignment of this cell
	 */
	public MarkdownTable.Alignment getAlignment() {
		return alignment;
	}

	/**
	 * Sets the text-alignment.  Note: the alignment cannot be null.
	 * @param alignment The new alignment
	 */
	public void setAlignment(MarkdownTable.Alignment alignment) {
		if(alignment == null) {
			throw new IllegalArgumentException("Alignment cannot be null");
		}
		this.alignment = alignment;
	}

	public String getContents() {
		return contents;
	}

	/**
	 * Sets the contents of this cell.
	 * If the contents contain any linebreaks, they will be replaced with spaces.
	 *
	 * @param contents The new cell contents
	 */
	public void setContents(String contents) {
		if(contents == null) {
			contents = "";
		}
		// We don't allow linebreaks in a table cell
		this.contents = contents.replace("\n", " ");
	}

	public int getColspan() {
		return colspan;
	}

	/**
	 * Sets the number of columns this cell spans.  If the colspan is less than 1, it is set to 1.
	 * @param colspan The new colspan
	 */
	public void setColspan(int colspan) {
		if(colspan < 1) {
			colspan = 1;
		}
		this.colspan = colspan;
	}

	/**
	 * Returns the number of characters needed to show this column.
	 * It adds two to the content width, so there is padding around the content.
	 * @return The width of this column in characters, plus 2 chars for spacing.
	 */
	public int getWidth() {
		return this.contents.length() + 2;
	}

	@Override
	public String toString() {
		return "MarkdownTableCell{" +
					   "colspan=" + colspan +
					   ", alignment=" + alignment +
					   ", contents='" + contents + '\'' +
					   '}';
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) { return true; }
		if(o == null || getClass() != o.getClass()) { return false; }

		MarkdownTableCell that = (MarkdownTableCell) o;

		return !(colspan != that.colspan ||
						 alignment != that.alignment ||
						 !contents.equals(that.contents));
	}

	@Override
	public int hashCode() {
		int result = alignment.hashCode();
		result = 31 * result + contents.hashCode();
		result = 31 * result + colspan;
		return result;
	}
}
