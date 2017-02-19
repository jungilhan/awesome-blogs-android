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
 * Provides a standard class to note which HTML elements should be left in the final output.
 * @author Phil DeJarnett
 */
public class IgnoredHtmlElement {

	private String tagName;

	private Set<String> attributes;

	/**
	 * Create a new IgnoredHtmlElement.  The tagname may also be referred to as the NodeName.
	 *
	 * @param tagName The tag name, such as {@code DIV}, case-insensitive.
	 */
	public IgnoredHtmlElement(String tagName) {
		this.tagName = tagName;
		this.attributes = new HashSet<String>();
	}

	/**
	 * Utility method to quickly create a new element.
	 *
	 * @param tagName The elements tag name.
	 * @param attributes Zero or more attributes that should be enabled on this tag.
	 * @return The newly created element.
	 */
	public static IgnoredHtmlElement create(String tagName, String... attributes) {
		IgnoredHtmlElement el = new IgnoredHtmlElement(tagName);
		for(final String attr : attributes) {
			el.addAttribute(attr);
		}
		return el;
	}

	/**
	 * Returns the tagname for this object.
	 * @return The name of this element.
	 */
	public String getTagName() {
		return tagName;
	}

	/**
	 * Gets all the attributes that should be left on this tag.
	 * @return A set of attributes that are left on the tag.
	 */
	public Set<String> getAttributes() {
		return attributes;
	}

	/**
	 * Adds a single to the list of allowed attributes.
	 * @param attributeName The name of the attribute to allow.
	 * @return true if the attribute was not already set.
	 */
	public boolean addAttribute(String attributeName) {
		return this.attributes.add(attributeName);
	}

	/**
	 * Adds one or more attributes to the list of allowed attributes.
	 * @param attributes The attribute names that are to be allowed.
	 */
	public void addAttributes(String... attributes) {
		for(final String attr : attributes) {
			this.addAttribute(attr);
		}
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) { return true; }
		if(o == null || getClass() != o.getClass()) { return false; }

		IgnoredHtmlElement that = (IgnoredHtmlElement) o;

		return tagName.equals(that.tagName);
	}

	@Override
	public int hashCode() {
		return tagName.hashCode();
	}
}
