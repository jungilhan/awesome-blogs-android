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

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This is a customized subclass of BufferedWriter that handles working with Markdown block-level elements.
 * In the case of a non-block-level element occurring outside a block, it is automatically promoted.
 *
 * @author Phil DeJarnett
 */
public final class BlockWriter extends PrintWriter {

	private int blockDepth = 0;

	private int lastWrittenBlockDepth = -1;

	private boolean autoStartedBlock = false;

	private boolean empty = true;

	private StringWriter buffer = null;

	private String prependNewLineString = null;

	private boolean prependShouldAddBeforeNextWrite = true;

	private final ReentrantLock prependingLock = new ReentrantLock();

	/**
	 * Creates a new, empty BlockWriter with a StringWriter as the buffer.
	 * To get the contents of the StringWriter, call BlockWriter.toString()
	 *
	 * @see #toString()
	 * @return new BlockWriter
	 */
	public static BlockWriter create() {
		return BlockWriter.create(new StringWriter());
	}

	/**
	 * Creates a new, empty BlockWriter with a StringWriter as the buffer.
	 * To get the contents of the StringWriter, call BlockWriter.toString()
	 *
	 * @param initialSize  Initialize the output buffer to the specified size.
	 * @see #toString()
	 * @return new BlockWriter
	 */
	public static BlockWriter create(int initialSize) {
		return BlockWriter.create(new StringWriter(initialSize));
	}

	// handles the actual setting up of the buffer
	private static BlockWriter create(StringWriter buffer) {
		@SuppressWarnings({"IOResourceOpenedButNotSafelyClosed"})
		BlockWriter bw = new BlockWriter(buffer);
		bw.buffer = buffer;
		return bw;
	}

	public BlockWriter(Writer out) {
		super(out);
	}

	public BlockWriter(Writer out, boolean autoFlush) {
		super(out, autoFlush);
	}

	public BlockWriter(OutputStream out) {
		super(out);
	}

	public BlockWriter(OutputStream out, boolean autoFlush) {
		super(out, autoFlush);
	}

	@Override
	public void write(int c) {
		testNewBlock();
		super.write(c);
		if(c == '\n') {
			prependAfterNewline();
		}
    }

    @Override
	public void write(char cbuf[], int off, int len) {
		if(len == 0) {
			return;
		}
		testNewBlock();
		if(prependNewLineString != null && !prependingLock.isLocked()) {
			writePrepended(cbuf, off, len);
		} else {
			super.write(cbuf, off, len);
		}
    }

	// writes a character buffer while prepending lines
	private void writePrepended(char cbuf[], int off, int len) {
		prependingLock.lock();
		try {
			// keep track of what needs to be added
			int start = off;
			// loop over the cbuf and see if it contains any newlines.
			for(int i=off; i<off+len; i++) {
				if(cbuf[i] == '\n') {
					// if this is a newline, then write what we have, and handle the prepend
					write(cbuf, start, (i+1)-start);
					writePrepend();
					// start the next round at the newline
					start = i+1;
				}
			}
			// write tail
			if(start < off+len) {
				write(cbuf, start, (off+len)-start);
			}
		} finally {
			prependingLock.unlock();
		}
	}

    @Override
	public void write(String s, int off, int len) {
		if(len == 0) {
			return;
		}
		testNewBlock();
		if((prependNewLineString != null) && (s.indexOf('\n') != -1) && !prependingLock.isLocked()) {
			writePrepended(s, off, len);
		} else {
			super.write(s, off, len);
		}
    }

	// writes a string while prepending lines
	private void writePrepended(String s, int off, int len) {
		prependingLock.lock();
		try {
			// keep track of what needs to be added
			int start = off;
			// loop over the string and see if it contains any newlines.
			for(int i=off; i<off+len; i++) {
				if(s.charAt(i) == '\n') {
					// if this is a newline, then write what we have, and handle the prepend
					write(s, start, (i+1)-start);
					writePrepend();
					// start the next round at the newline
					start = i+1;
				}
			}
			// write tail
			if(start < off+len) {
				write(s, start, (off+len)-start);
			}
		} finally {
			prependingLock.unlock();
		}
	}

	@Override
	public void println() {
		testFirstPrepend();
		try {
			synchronized (lock) {
				if(out == null) {
					throw new IOException("Stream closed");
				}
				out.write('\n');
				out.flush();
			}
		}
		catch (InterruptedIOException x) {
			Thread.currentThread().interrupt();
		}
		catch (IOException x) {
			setError();
		}
		prependAfterNewline();
	}

	private void prependAfterNewline() {
		if(prependNewLineString != null && !prependingLock.isLocked()) {
			prependingLock.lock();
			try {
				writePrepend();
			} finally {
				prependingLock.unlock();
			}
		}
	}

	/**
	 * Tests to see if we need to forcibly start a new block.
	 * This is used in cases where an inline element is rendered outside a block element, such as:
	 *
	 * <blockquote>{@code <p>foo</p> <em>bar</em> <p>baz</p>}</blockquote>
	 *
	 * In this case, {@code bar} is promoted to it's own block element.
	 *
	 * Note: this only occurs at the top level.  Otherwise, once inside a block, it is rendered as
	 * part of whatever block is currently being rendered.
	 *
	 * (This method also handles adding the initial prepended string if necessary.)
	 *
	 */
	private void testNewBlock(){
		testFirstPrepend();
		if(blockDepth == 0) {
			startBlock();
			// keep track of automatically started blocks.  See startBlock below.
			autoStartedBlock = true;
		}
		if(lastWrittenBlockDepth != blockDepth) {
			// only print newlines if there is content
			if(empty) {
				// if this is the first block printed, then don't actually do anything.
				empty = false;
			} else {
				// otherwise, print two lines, so an empty line occurs between the blocks
				println();
				println();
			}
			lastWrittenBlockDepth = blockDepth;
		}
	}

	/**
	 * Test to see if we should add a prepend because this is the first line.
	 */
	private void testFirstPrepend() {
		if(prependShouldAddBeforeNextWrite && prependNewLineString != null && !prependingLock.isLocked()) {
			prependingLock.lock();
			try {
				writePrepend();
			} finally {
				prependingLock.unlock();
			}
			// set to true to prevent any more checking for prepending
			prependShouldAddBeforeNextWrite = false;
		}
	}

	/**
	 * Writes the prepend string to the output writer.
	 */
	private void writePrepend() {
		super.write(prependNewLineString, 0, prependNewLineString.length());
	}

	/**
	 * Starts a new block.  This is useful when streaming out content within a block.
	 * This method keeps track of the current block depth, so make sure that
	 * {@link #endBlock()} is called when the block is completed.
	 */
	public void startBlock() {
		if(autoStartedBlock) {
			// if following an auto-started block, close the previous block before continuing
			autoStartedBlock = false;
			endBlock();
		}
		// increment block depth so we can keep track of how far down we've traveled.
		blockDepth++;
	}

	/**
	 * Ends a block.  The depth counter is decreased, so we know when we are back at the root.
	 */
	public void endBlock() {
		if(blockDepth > 0) {
			blockDepth--;
		}
		lastWrittenBlockDepth = -1;
	}

	/**
	 * Writes an entire block in one go.
	 * This method automatically handles starting and ending the block.
	 *
	 * @param blockText The text of the block.
	 */
	public void writeBlock(Object blockText) {
		startBlock();
		print(blockText);
		endBlock();
	}

	/**
	 * Alias for {@link #writeBlock(Object)}.
	 * @param blockText The text of the block.
	 */
	public void printBlock(Object blockText) {
		writeBlock(blockText);
	}

	/**
	 * Returns how deep the number of blocks is.
	 *
	 * {@code 0} means that no blocks are currently active.
	 *
	 * @return block depth
	 */
	public int getBlockDepth() {
		return blockDepth;
	}

	/**
	 * Returns true if nothing has been written to the stream yet.
	 * @return true if nothing has been written yet.
	 */
	@SuppressWarnings({"UnusedDeclaration"})
	public boolean isEmpty() {
		return empty;
	}

	/**
	 * If this object has been created using {@link #create()}, returns the StringWriter output buffer.
	 *
	 * @return the buffer for this BlockWriter
	 */
	@SuppressWarnings({"UnusedDeclaration"})
	public StringWriter getBuffer() {
		return buffer;
	}

	/**
	 * Returns the string being prepended to new lines, if set.
	 * @return String that gets prepended before each new line, or null if not set.
	 */
	@SuppressWarnings({"UnusedDeclaration"})
	public String getPrependNewlineString() {
		return prependNewLineString;
	}

	/**
	 * Sets the string to prepend to new lines.  If set to null, this feature is disabled.
	 * By default, this starts adding the prepend immediately.
	 *
	 * @param prependNewLineString The string to prepend to each new line.
	 * @return This for chaining (especially after creation)
	 */
	public BlockWriter setPrependNewlineString(String prependNewLineString) {
		this.prependNewLineString = prependNewLineString;
		prependShouldAddBeforeNextWrite = true;
		return this;
	}

	/**
	 * Sets the string to prepend to new lines.  If set to null, this feature is disabled.
	 * The second parameter affects whether or not the next encountered new line gets the
	 * prepend string.  If it is true, the first line won't be affected.  Otherwise,
	 * this is the same as {@link #setPrependNewlineString(String)}.
	 *
	 * @param prependNewLineString The string to prepend to each new line.
	 * @param skipFirstLine If true, the first line won't be prepended.
	 * @return This for chaining (especially after creation)
	 */
	public BlockWriter setPrependNewlineString(String prependNewLineString, boolean skipFirstLine) {
		this.prependNewLineString = prependNewLineString;
		prependShouldAddBeforeNextWrite = !skipFirstLine;
		return this;
	}

	/**
	 * If this object has been created using {@link #create()}, this will return the contents
	 * of the StringWriter buffer.
	 *
	 * Otherwise, this returns the default Object.toString() method.
	 *
	 * @return The contents of the buffer, or a generic Object method.
	 */
	public String toString() {
		if(this.buffer != null) {
			this.flush();
			return buffer.toString();
		} else {
			return super.toString();
		}
	}
}
