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

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * A small collection of utilities for manipulating strings.
 *
 * @author Phil DeJarnett
 */
public class StringUtils {

	/** Represents left alignment. */
	public static final int ALIGN_LEFT = -1;
	/** Represents centered alignment. */
	public static final int ALIGN_CENTER = 0;
	/** Represents right alignment. */
	public static final int ALIGN_RIGHT = 1;

	/**
	 * Pads out a left-, right-, or center-aligned string using spaces up to the specified width.
	 * @param s String to pad
	 * @param width Minimum width of final string
	 * @param alignment How to align the string < 0 means left, 0 means center, and > 0 means right
	 * @return Padded string
	 */
	@SuppressWarnings({"SameParameterValue", "SameParameterValue"})
	public static String align(String s, int width, int alignment) {
		return align(s, width, ' ', alignment);
	}

	/**
	 * Pads out a left-, right-, or center-aligned string using the specified character up to the specified width.
	 * @param s String to pad
	 * @param width Minimum width of final string
	 * @param paddingChar Character to pad with
	 * @param alignment How to align the string < 0 means left, 0 means center, and > 0 means right
	 * @return Padded string
	 */
	public static String align(String s, int width, char paddingChar, int alignment) {
		if(s.length() < width) {
			int diff = width - s.length();
			String left = "";
			String right = "";
			if(alignment == 0) {
				int numLeftChars = diff/2;
				int numRightChars = numLeftChars + (diff % 2);
				left = multiply(paddingChar, numLeftChars);
				right = multiply(paddingChar, numRightChars);
			} else if(alignment < 0) {
				right = multiply(paddingChar, diff);
			} else {
				left = multiply(paddingChar, diff);
			}
			s = left + s + right;
		}
		return s;
	}

	/**
	 * Pads out a left-, right-, or center-aligned string using spaces up to the specified width.
	 * @param output Writer to output the centered string to
	 * @param s String to pad
	 * @param width Minimum width of final string
	 * @param alignment How to align the string < 0 means left, 0 means center, and > 0 means right
	 */
	public static void align(PrintWriter output, String s, int width, int alignment) {
		align(output, s, width, ' ', alignment);
	}

	/**
	 * Pads out a left-, right-, or center-aligned string using the specified character up to the specified width.
	 * @param output Writer to output the centered string to
	 * @param s String to pad
	 * @param width Minimum width of final string
	 * @param paddingChar Character to pad with
	 * @param alignment How to align the string < 0 means left, 0 means center, and > 0 means right
	 */
	public static void align(PrintWriter output, String s, int width, char paddingChar, int alignment) {
		if(s.length() < width) {
			int diff = width - s.length();
			if(alignment == 0) {
				int numLeftChars = diff/2;
				int numRightChars = numLeftChars + (diff % 2);
				multiply(output, paddingChar, numLeftChars);
				output.write(s);
				multiply(output, paddingChar, numRightChars);
			} else if(alignment < 0) {
				output.write(s);
				multiply(output, paddingChar, diff);
			} else {
				multiply(output, paddingChar, diff);
				output.write(s);
			}
		} else {
			output.write(s);
		}
	}

	/**
	 *  Duplicates the given character {@code count} times.
	 *  If {@code count} is less than or equal to 0, the empty string is returned.
	 * @param c Character to duplicate
	 * @param count Number of times to duplicate
	 * @return Duplicated string.
	 */
	public static String multiply(char c, int count) {
		return multiply(String.valueOf(c), count);
	}

	/**
	 *  Duplicates the given string {@code count} times.
	 *  If {@code count} is less than or equal to 0, the empty string is returned.
	 * @param s String to duplicate
	 * @param count Number of times to duplicate
	 * @return Duplicated string.
	 */
	public static String multiply(String s, int count) {
		if(count < 1) {
			return "";
		} else {
			StringBuilder sb = new StringBuilder();
			for(int i=0; i<count; i++) {
				sb.append(s);
			}
			return sb.toString();
		}
	}

	/**
	 *  Duplicates the given character {@code count} times to the Writer.
	 *  If {@code count} is less than or equal to 0, this is a no-op.
	 * @param output Writer to receive duplicated results
	 * @param c Character to duplicate
	 * @param count Number of times to duplicate
	 */
	public static void multiply(PrintWriter output, char c, int count) {
		for(int i=0; i<count; i++) {
			output.write(c);
		}
	}

	/**
	 *  Duplicates the given string {@code count} times to the Writer.
	 *  If {@code count} is less than or equal to 0, this is a no-op.
	 * @param output Writer to receive duplicated results
	 * @param s String to duplicate
	 * @param count Number of times to duplicate
	 */
	@SuppressWarnings({"SameParameterValue"})
	public static void multiply(PrintWriter output, String s, int count) {
		for(int i=0; i<count; i++) {
			output.write(s);
		}
	}


	/**
	 * Prepends a specific string to the front of each line of an input string.
	 *
	 * Lines are marked by a trailing {@code '\n'}, {@code '\r'}, or {@code '\f'} character.
	 *
	 * @param s The input string.
	 * @param prependWith The string to prepend to each line.
	 * @return The modified string.
	 */
	@SuppressWarnings({"SameParameterValue"})
	public static String prependEachLine(String s, String prependWith) {
		StringWriter sw = new StringWriter(s.length()+(prependWith.length()*20));
		//noinspection IOResourceOpenedButNotSafelyClosed
		prependEachLine(new PrintWriter(sw), s, prependWith);
		return sw.toString();
	}


	/**
	 * Prepends a specific string to the front of each line of an input string.
	 *
	 * Lines are marked by a trailing {@code '\n'}, {@code '\r'}, or {@code '\f'} character.
	 *
	 * @param output The writer to recieve the modified string.
	 * @param s The input string.
	 * @param prependWith The string to prepend to each line.
	 */
	public static void prependEachLine(PrintWriter output, String s, String prependWith) {
		if(s.length() == 0) {
			return;
		}
		output.write(prependWith);
		for(int i=0; i<s.length(); i++) {
			char c = s.charAt(i);
			output.write(c);
			if(c == '\n' || c == '\r' || c == '\f') {
				output.write(prependWith);
			}
		}
	}

}
