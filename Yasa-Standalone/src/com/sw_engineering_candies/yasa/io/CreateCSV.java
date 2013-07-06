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

import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.sw_engineering_candies.yasa.model.Link;
import com.sw_engineering_candies.yasa.model.Model;

public final class CreateCSV {

	/**
	 * Private constructor to prevent class instantiation.
	 */
	private CreateCSV() {
	}

	/** standard logger (see log4j.properties file for details) */
	private static final Logger LOGGER = Logger.getLogger(CreateCSV.class);

	public static boolean exportData(final Model model, final String exportFileName) {
		final FileWriter fw = FileUtility.createFileWriter(exportFileName);
		if (null != fw) {
			try {
				for (final Link link : model.getLinks()) {
					if (!link.isClusterLink()) {
						fw.append(link.getTarget().getName());
						fw.append(";");
						fw.append(link.getSource().getName());
						fw.append(System.getProperty("line.separator"));
					}
				}
				fw.close();
				return true;
			} catch (final IOException e) {
				LOGGER.error(e.getMessage());
			}
		} else {
			LOGGER.error("no valid file writer created");
		}
		return false;
	}
}
