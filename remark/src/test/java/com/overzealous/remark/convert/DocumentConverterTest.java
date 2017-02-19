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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Phil DeJarnett
 */
public class DocumentConverterTest {

	private DocumentConverter dc;

	private static final String LINK_URL = "http://www.example.com/file.html";
	private static final String IMAGE_URL = "http://www.example.com/image.jpg";
	private static final String IMAGE_NOFILE_URL = "http://www.example.com/blah/";
	private static final String IMAGE_INVALID_URL = "http://www.example.com/_!!_";

	@Before
	public void setUp() throws Exception {
		dc = new DocumentConverter(Options.markdown());
	}

	@Test
	public void testSimpleLink() throws Exception {
		Assert.assertEquals("test", dc.cleanLinkId(LINK_URL, "test", false));
		Assert.assertEquals("test-test", dc.cleanLinkId(LINK_URL, "test-test", false));
		Assert.assertEquals("test test", dc.cleanLinkId(LINK_URL, "test\ntest", false));
	}

	@Test
	public void testReplacementLinks() throws Exception {
		Assert.assertEquals("test_test", dc.cleanLinkId(LINK_URL, "test*&^$#˜∂test", false));
		Assert.assertEquals("test", dc.cleanLinkId(LINK_URL, "&(^test", false));
		Assert.assertEquals("test", dc.cleanLinkId(LINK_URL, "test^%$", false));
	}

	@Test
	public void testGenericLinks() throws Exception {
		Assert.assertEquals("Link 1", dc.cleanLinkId(LINK_URL, "*(&(*&(", false));
		Assert.assertEquals("Link 2", dc.cleanLinkId(LINK_URL, "_!!\n!!_", false));
	}

	@Test
	public void testGenericImages() throws Exception {
		Assert.assertEquals("Image 1", dc.cleanLinkId(IMAGE_NOFILE_URL, "*(&(*&(", true));
		Assert.assertEquals("Image 2", dc.cleanLinkId(IMAGE_NOFILE_URL, "_!!\n!!_", true));
		Assert.assertEquals("image.jpg", dc.cleanLinkId(IMAGE_URL, "!", true));
		Assert.assertEquals("Image 3", dc.cleanLinkId(IMAGE_INVALID_URL, "!", true));
	}
}
