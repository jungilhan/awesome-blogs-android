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

import com.overzealous.remark.Remark;
import com.overzealous.remark.util.TestUtils;
import org.junit.Assert;
import org.junit.Before;

/**
 * @author Phil DeJarnett
 */
public abstract class RemarkTester {

	private static final String BASE_PATH = "/conversions/";
	private static final String INPUT_PATH = BASE_PATH+"html/";
	private static final String MD_PATH = BASE_PATH+"markdown/";
	private static final String HTML_EXT = ".html";
	private static final String MD_EXT = ".md";

	@SuppressWarnings({"WeakerAccess"})
	Remark remark;
	@SuppressWarnings({"WeakerAccess", "CanBeFinal"})
	String baseURI = "http://www.example.com/";

	/**
	 * Override to change the remark method
	 * @return A Remark instance
	 */
	Remark setupRemark() {
		return new Remark();
	}

	@Before
	public void setUp() throws Exception {
		this.remark = setupRemark();
		Assert.assertNotNull(this.remark);
	}


	void test(String testName) throws Exception {
		test(testName, null);
	}

	@SuppressWarnings({"RedundantThrows"})
	void test(String testName, String option) throws Exception {
		String input = TestUtils.readResourceToString(INPUT_PATH + testName + HTML_EXT);
		if(option == null) {
			option = "";
		} else {
			option = '-' + option;
		}
		String expected = TestUtils.readResourceToString(MD_PATH + testName + option + MD_EXT);
		String converted = remark.convertFragment(input, baseURI);
		if(!converted.equals(expected)) {
			System.out.println("==============================");
			System.out.println(expected);
			System.out.println("------------------------------");
			System.out.println(converted);
			System.out.println("==============================");
		}
		Assert.assertEquals(expected, converted);
	}

}
