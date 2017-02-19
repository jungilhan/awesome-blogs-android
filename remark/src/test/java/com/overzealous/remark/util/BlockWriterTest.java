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

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Phil DeJarnett
 */
public class BlockWriterTest {

	@Test
	public void testWriteTwoBlocks() throws Exception {
		BlockWriter bw = BlockWriter.create();
		bw.writeBlock("block1");
		bw.writeBlock("block2");

		Assert.assertEquals("block1\n\nblock2", bw.toString());
	}

	@Test
	public void testWriteInlineThenBlock() throws Exception {
		BlockWriter bw = BlockWriter.create();
		bw.write("inline1");
		bw.writeBlock("block1");

		Assert.assertEquals("inline1\n\nblock1", bw.toString());
	}

	@Test
	public void testWriteBlockThenInlineThenBlock() throws Exception {
		BlockWriter bw = BlockWriter.create();
		bw.writeBlock("block1");
		bw.write("inline1");
		bw.writeBlock("block2");

		Assert.assertEquals("block1\n\ninline1\n\nblock2", bw.toString());
	}

	@Test
	public void testWriteManualBlock() throws Exception {
		BlockWriter bw = BlockWriter.create();
		bw.startBlock();
		bw.print("hello");
		bw.println();
		bw.print("world");
		bw.endBlock();
		bw.write("inline");
		bw.write("1");
		bw.startBlock();
		bw.write("block1");
		bw.endBlock();

		Assert.assertEquals("hello\nworld\n\ninline1\n\nblock1", bw.toString());
	}

	@Test
	public void testWriteNestedBlocks() throws Exception {
		BlockWriter bw = BlockWriter.create();
		bw.startBlock();
		bw.write("hello");
			bw.startBlock();
			bw.write("world");
			bw.endBlock();
		bw.endBlock();

		Assert.assertEquals(0, bw.getBlockDepth());

		Assert.assertEquals("hello\n\nworld", bw.toString());
	}

	@Test
	public void testPrintf() throws Exception {
		BlockWriter bw = BlockWriter.create();
		bw.printf("%s", "hello");
		bw.printf("%d", 42);
		bw.writeBlock("block1");

		Assert.assertEquals("hello42\n\nblock1", bw.toString());
	}

	@Test
	public void testPrependChar() throws Exception {
		BlockWriter bw = BlockWriter.create();
		bw.setPrependNewlineString("XXXX");
		bw.write('a');

		Assert.assertEquals("XXXXa", bw.toString());
	}

	@Test
	public void testPrependCharBuffer() throws Exception {
		BlockWriter bw = BlockWriter.create();
		bw.setPrependNewlineString("XXXX");
		bw.write("abc\ndef\nghi".toCharArray());

		Assert.assertEquals("XXXXabc\nXXXXdef\nXXXXghi", bw.toString());
	}

	@Test
	public void testPrependString() throws Exception {
		BlockWriter bw = BlockWriter.create();
		bw.setPrependNewlineString("XXXX");
		bw.write("abc\n\ndef\nghi");
		bw.write("\njkl");

		Assert.assertEquals("XXXXabc\nXXXX\nXXXXdef\nXXXXghi\nXXXXjkl", bw.toString());
	}

	@Test
	public void testPrependNotFirst() throws Exception {
		BlockWriter bw = BlockWriter.create();
		bw.setPrependNewlineString("XXXX", true);
		bw.write("- ");
		bw.write("abc\ndef\nghi");

		Assert.assertEquals("- abc\nXXXXdef\nXXXXghi", bw.toString());
	}

	@Test
	public void testPrependBlocks() throws Exception {
		BlockWriter bw = BlockWriter.create();
		bw.setPrependNewlineString("XXXX");
		bw.printBlock("abc\ndef");
		bw.printBlock("ghi\njkl");

		Assert.assertEquals("XXXXabc\nXXXXdef\nXXXX\nXXXXghi\nXXXXjkl", bw.toString());
	}

	@Test
	public void testEmptyBlocks() throws Exception {
		BlockWriter bw = BlockWriter.create();
		bw.writeBlock("block1");
		bw.startBlock();
			bw.startBlock();
				bw.writeBlock("block2");
			bw.endBlock();
		bw.endBlock();
		
		Assert.assertEquals("block1\n\nblock2", bw.toString());
	}

	@Test
	public void testEmptyAutoBlocks() throws Exception {
		BlockWriter bw = BlockWriter.create();
		bw.writeBlock("block1");
		bw.startBlock();
			bw.write("inline1");
			bw.startBlock();
				bw.writeBlock("block2");
			bw.endBlock();
			bw.write("inline2");
		bw.endBlock();
		
		Assert.assertEquals("block1\n\ninline1\n\nblock2\n\ninline2", bw.toString());
	}
}
