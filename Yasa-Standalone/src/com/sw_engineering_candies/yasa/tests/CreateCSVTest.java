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
package com.sw_engineering_candies.yasa.tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

import com.sw_engineering_candies.yasa.io.CreateCSV;
import com.sw_engineering_candies.yasa.io.ImportCSV;
import com.sw_engineering_candies.yasa.model.Model;

public final class CreateCSVTest {

	public static final String INPUT_CSV_CALLER_CALLEE = ModelTest.IMPORT_PATH + "input-caller-callee.csv";
	public static final String OUPUT_CSV_CALLER_CALLEE = ModelTest.IMPORT_PATH + "output-caller-callee.csv";

	public static final String INPUT_CSV_NODE_CLUSTER = ModelTest.IMPORT_PATH + "input-node-cluster.csv";
	public static final String OUPUT_CSV_NODE_CLUSTER = ModelTest.IMPORT_PATH + "output-node-cluster.csv";

	/** standard logger (see log4j.properties file for details) */
	private static final Logger LOGGER = Logger.getLogger(CreateCSVTest.class);

	@AfterClass
	public static void cleanUp() {
		final File f1 = new File(OUPUT_CSV_NODE_CLUSTER);
		Assert.assertTrue(f1.delete());

		final File f2 = new File(OUPUT_CSV_CALLER_CALLEE);
		Assert.assertTrue(f2.delete());
	}

	private static String getFileAsString(final String filePath) {
		final StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(filePath));
			char[] buf = new char[800 * 1024];
			int numRead = 0;
			while ((numRead = reader.read(buf)) != -1) {
				final String readData = String.valueOf(buf, 0, numRead);
				fileData.append(readData);
				buf = new char[1024];
			}
		} catch (final FileNotFoundException e) {
			e.getMessage();
		} catch (final IOException e) {
			e.getMessage();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (final IOException e) {
					LOGGER.error("unable to close reader");
				}
			}
		}
		return fileData.toString();
	}

	@Test
	public void basicTest() {
		final Model sa = new Model();
		Assert.assertTrue("import file not valid", new ImportCSV(sa, 10).importModel(INPUT_CSV_CALLER_CALLEE, INPUT_CSV_NODE_CLUSTER));

		final boolean exportCallerCalleeData = CreateCSV.exportCallerCalleeData(sa, OUPUT_CSV_CALLER_CALLEE);
		Assert.assertTrue("export file not valid", exportCallerCalleeData);

		final String inputCallerCalleeFileAsString = getFileAsString(INPUT_CSV_CALLER_CALLEE);
		final String outputCallerCalleeFileAsString = getFileAsString(OUPUT_CSV_CALLER_CALLEE);
		Assert.assertEquals(inputCallerCalleeFileAsString, outputCallerCalleeFileAsString);

		final boolean exportNodeClusterData = CreateCSV.exportNodeClusterData(sa, OUPUT_CSV_NODE_CLUSTER);
		Assert.assertTrue("export file not valid", exportNodeClusterData);

		final String inputNodeClusterFileAsString = getFileAsString(INPUT_CSV_NODE_CLUSTER);
		final String outputNodeClusterFileAsString = getFileAsString(OUPUT_CSV_NODE_CLUSTER);
		Assert.assertEquals(inputNodeClusterFileAsString, outputNodeClusterFileAsString);
	}

}
