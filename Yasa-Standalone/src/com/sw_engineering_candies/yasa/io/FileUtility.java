/*
 * Copyright (C) 2009-2013, Markus Sprunck <sprunck.markus@gmail.com>
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * - The name of its contributor may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.sw_engineering_candies.yasa.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

final class FileUtility {

	/**
	 * Private constructor to prevent class instantiation.
	 */
	private FileUtility() {
	}

	/** standard logger (see log4j.properties file for details) */
	private static final Logger LOGGER = Logger.getLogger(FileUtility.class);

	static FileWriter createFileWriter(final String name) {
		final File file = new File(name);
		try {
			if (file.exists() && file.delete()) {
				LOGGER.debug(String.format("file '%s' deleted", name));
			}
			if (file.createNewFile()) {
				LOGGER.debug(String.format("file '%s' created", name));
			}
			return new FileWriter(file);
		} catch (final IOException e) {
			LOGGER.error(e.getMessage());
		}
		return null;
	};

	static ArrayList<String> tokenizeString(final String s, final String c) {
		final ArrayList<String> result = new ArrayList<String>(10);
		final StringTokenizer st;
		st = new StringTokenizer(s, c);
		while (st.hasMoreTokens()) {
			result.add(st.nextToken());
		}
		return result;
	}
}
