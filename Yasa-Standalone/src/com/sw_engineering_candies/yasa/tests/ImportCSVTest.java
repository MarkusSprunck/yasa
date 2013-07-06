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

import java.io.File;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

import com.sw_engineering_candies.yasa.io.ImportCSV;
import com.sw_engineering_candies.yasa.model.Model;

public class ImportCSVTest {

	/** standard logger (see log4j.properties file for details) */
	private static final Logger LOGGER = Logger.getLogger(ImportCSVTest.class);

	@Test
	public final void testImportModelNoModel() {
		Assert.assertFalse(new ImportCSV().importModel(null,  ModelTest.YASA_INPUT_CSV, ModelTest.YASA_INPUT_NODE_CLUSTER_CSV, 10));
	}

	@Test
	public final void testImportModel() {
		final Model model = new Model();
		Assert.assertTrue(new ImportCSV().importModel(model, ModelTest.YASA_INPUT_CSV, ModelTest.YASA_INPUT_NODE_CLUSTER_CSV, 10));
	}

	@AfterClass
	public static void cleanUp() {
		File f = new File("yasa-input.svg");
		if (!f.delete()) {
			LOGGER.info(f.getPath() + " not deleted");
		}

		f = new File("yasa-output.csv");
		if (!f.delete()) {
			LOGGER.info(f.getPath() + " not deleted");
		}

		f = new File("yasa-output.svg");
		if (!f.delete()) {
			LOGGER.info(f.getPath() + " not deleted");
		}
	}

}
