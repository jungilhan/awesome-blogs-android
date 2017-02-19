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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

/**
 * @author Phil DeJarnett
 */
public class MarkdownTableTest {

	@Test
	public void testTable() throws Exception {
		MarkdownTable mt = new MarkdownTable();
		List<MarkdownTableCell> header = mt.addHeaderRow();
		List<MarkdownTableCell> body = mt.addBodyRow();
		for(int i=1; i<4; i++) {
			header.add(new MarkdownTableCell("header"+i));
			body.add(new MarkdownTableCell("column "+i));
		}

		Assert.assertEquals(TestUtils.readResourceToString("/util/MarkdownTableTest.md"), getTableString(mt, false, false));
	}

	@Test
	public void testTableWithAlignment() throws Exception {
		MarkdownTable mt = new MarkdownTable();
		List<MarkdownTableCell> header = mt.addHeaderRow();
		List<MarkdownTableCell> body1 = mt.addBodyRow();
		List<MarkdownTableCell> body2 = mt.addBodyRow();
		for(int i=1; i<4; i++) {
			header.add(new MarkdownTableCell("h"+i, MarkdownTable.Alignment.find(i-2)));
			body1.add(new MarkdownTableCell("col"+i));
			body2.add(new MarkdownTableCell("column "+i));
		}

		Assert.assertEquals(TestUtils.readResourceToString("/util/MarkdownTableAlignmentTest.md"), getTableString(mt, false, false));
	}

	@Test
	public void testTableWithColspan() throws Exception {
		MarkdownTable mt = new MarkdownTable();
		List<MarkdownTableCell> header = mt.addHeaderRow();
		List<MarkdownTableCell> body1 = mt.addBodyRow();
		List<MarkdownTableCell> body2 = mt.addBodyRow();
		for(int i=1; i<4; i++) {
			header.add(new MarkdownTableCell("h"+i, MarkdownTable.Alignment.find(i-2)));
			body1.add(new MarkdownTableCell("col"+i));
		}
		body2.add(new MarkdownTableCell("column 1"));
		body2.add(new MarkdownTableCell("column 2", 2));

		Assert.assertEquals(TestUtils.readResourceToString("/util/MarkdownTableColspanTest.md"), getTableString(mt, true, false));
	}

	@Test
	public void testTableWithWideColspan() throws Exception {
		MarkdownTable mt = new MarkdownTable();
		List<MarkdownTableCell> header = mt.addHeaderRow();
		List<MarkdownTableCell> body1 = mt.addBodyRow();
		List<MarkdownTableCell> body2 = mt.addBodyRow();
		for(int i=1; i<5; i++) {
			header.add(new MarkdownTableCell("h"+i, MarkdownTable.Alignment.find(i-2)));
			body1.add(new MarkdownTableCell("col"+i));
		}
		body2.add(new MarkdownTableCell("column 1"));
		body2.add(new MarkdownTableCell("this is a really, really, really, really wide colspan", 3));

		Assert.assertEquals(TestUtils.readResourceToString("/util/MarkdownTableWideColspanTest.md"), getTableString(mt, true, false));
	}

	@Test
	public void testTableAsCode() throws Exception {
		MarkdownTable mt = new MarkdownTable();
		List<MarkdownTableCell> header = mt.addHeaderRow();
		List<MarkdownTableCell> body = mt.addBodyRow();
		for(int i=1; i<4; i++) {
			header.add(new MarkdownTableCell("header"+i));
			body.add(new MarkdownTableCell("column "+i));
		}

		Assert.assertEquals(TestUtils.readResourceToString("/util/MarkdownTableAsCodeTest.md"), getTableString(mt, false, true));
	}

	private String getTableString(MarkdownTable mt, boolean allowColSpan, boolean renderAsCode) {
		StringWriter sw = new StringWriter();
		//noinspection IOResourceOpenedButNotSafelyClosed
		mt.renderTable(new PrintWriter(sw), allowColSpan, renderAsCode);
		return '\n' + sw.toString();
	}

	@Test
	public void testGetNumberOfColumns() throws Exception {
		MarkdownTable mt = new MarkdownTable();
		for(int i=0; i<10; i++) {
			List<MarkdownTableCell> row = mt.addBodyRow();
			for(int j=0; j <= i%5; j++) {
				row.add(new MarkdownTableCell("cell "+j));
			}
		}

		Assert.assertEquals(5, mt.getNumberOfColumns());
	}

	@Test
	public void testGetNumberOfColumnsWithUnevenColumns() throws Exception {
		MarkdownTable mt = new MarkdownTable();
		for(int i=0; i<3; i++) {
			List<MarkdownTableCell> row = mt.addBodyRow();
			for(int j=0; j <= 2; j++) {
				if(i==1 && j==1) {
					// add a three-column cell
					row.add(new MarkdownTableCell("cell "+j, 3));
				} else {
					row.add(new MarkdownTableCell("cell "+j));
				}
			}
		}

		Assert.assertEquals(5, mt.getNumberOfColumns());

		List<MarkdownTableCell> row = mt.addHeaderRow();
		for(int i=1; i<=7; i++) {
			row.add(new MarkdownTableCell("head "+i));
		}

		Assert.assertEquals(7, mt.getNumberOfColumns());
	}
}
