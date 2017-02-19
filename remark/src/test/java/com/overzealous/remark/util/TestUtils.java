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

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.URL;

/**
 * @author Phil DeJarnett
 */
public class TestUtils {

	/**
	 * Reads a resource into a string.
	 * @param path Path to resource
	 * @return String contents of resource
	 */
	public static String readResourceToString(String path) {
		String result;
		try {
			URL u = StringUtils.class.getResource(path);
			if(u == null) {
				throw new Exception("Resource not found");
			}
			File f = FileUtils.toFile(u);
			if(!f.isFile()) {
				throw new Exception("Resource file does not exist or is not a file.");
			}
			result = FileUtils.readFileToString(f, "UTF-8");
			if(result == null) {
				throw new Exception("Error reading resource file.");
			}
		} catch(Exception e) {
			e.printStackTrace();
			result = "UNABLE TO LOAD RESOURCE "+path+": "+e.getMessage();
		}
		return result;
	}
}
