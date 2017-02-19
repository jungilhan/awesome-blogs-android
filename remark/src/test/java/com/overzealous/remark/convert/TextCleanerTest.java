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
import com.overzealous.remark.util.TestUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Phil DeJarnett
 */
public class TextCleanerTest {

	private static final TextCleaner BASIC = new TextCleaner(Options.markdown());

	private static final TextCleaner FULL = new TextCleaner(Options.pegdownAllExtensions());

	private String loadOut(String name) {
		return load(name, "out");
	}

	private String loadBasicIn() {
		return load("basic", "in");
	}

	private String loadFullIn() {
		return load("full", "in");
	}

	private String load(String name, String type) {
		return TestUtils.readResourceToString("/textcleaner/" + name + '.' + type + ".txt");
	}

	private void assertEqualsAndPrint(String expected, String result) {
		System.out.println(expected);
		System.out.println(result);
		System.out.println();
		Assert.assertEquals(expected, result);
	}

	@Test
	public void testCleanBasic() throws Exception {
		assertEqualsAndPrint(loadOut("cleanBasic"), BASIC.clean(loadBasicIn()));
	}

	@Test
	public void testCleanFull() throws Exception {
		assertEqualsAndPrint(loadOut("cleanFull"), FULL.clean(loadFullIn()));
	}

	@Test
	public void testCleanCodeBasic() throws Exception {
		assertEqualsAndPrint(loadOut("cleanCodeBasic"), BASIC.cleanCode(loadBasicIn()));
	}

	@Test
	public void testCleanCodeFull() throws Exception {
		assertEqualsAndPrint(loadOut("cleanCodeFull"), FULL.cleanCode(loadFullIn()));
	}

	@Test
	public void testCleanInlineCodeSimple() throws Exception {
		Assert.assertEquals("`hello &  > world`", BASIC.cleanInlineCode("hello &amp; \n&gt; world"));
	}

	@Test
	public void testCleanInlineCodeLeadingTick() throws Exception {
		Assert.assertEquals("`` `tick``", BASIC.cleanInlineCode("`tick"));
	}

	@Test
	public void testCleanInlineCodeTrailingTick() throws Exception {
		Assert.assertEquals("``tick` ``", BASIC.cleanInlineCode("tick`"));
	}

	@Test
	public void testCleanInlineCodeInlineTick() throws Exception {
		Assert.assertEquals("``ti`ck``", BASIC.cleanInlineCode("ti`ck"));
	}

	@Test
	public void testCleanInlineCodeLotsOfTicks() throws Exception {
		Assert.assertEquals("```` ``t```i`ck` ````", BASIC.cleanInlineCode("``t`&#96;`i`ck`"));
	}

	/** Disabled because this is too dependent on the computer
	@Test
	public void testReplacementPerformance() throws Exception {
		String basic = loadBasicIn();
		String full = loadFullIn();
		int numberOfTrials = 500;
		int numberOfTests = 4; // based on tests inside loop below
		long start = System.currentTimeMillis();
		for(int i=0; i<numberOfTrials; i++) {
			BASIC.clean(basic);
			FULL.clean(full);
			BASIC.cleanCode(basic);
			FULL.cleanCode(full);
		}
		long end = System.currentTimeMillis();

		// no more than an average of 1ms per test.
		int expectedTime = numberOfTrials * numberOfTests;

		// ensure that it doesn't take more than 1ms per test.
		Assert.assertTrue("ReplacementPerformance is too slow.", expectedTime > (end-start));
	}
	*/
}
