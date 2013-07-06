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

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sw_engineering_candies.yasa.io.CreateCSV;
import com.sw_engineering_candies.yasa.io.CreateSVG;
import com.sw_engineering_candies.yasa.io.ImportCSV;
import com.sw_engineering_candies.yasa.model.Model;

public class ModelTest {

	public static final String IMPORT_PATH = ".\\data\\small\\";

	static final String IMPORT_FILE_NOT_VALID = "import file not valid";
	
	static final String YASA_INPUT_CSV = "input-caller-callee.csv";
	static final String YASA_INPUT_NODE_CLUSTER_CSV = "input-node-cluster.csv";

	private static final String EXPORT_FILE_NOT_VALID = "export file not valid";
	private static final String YASA_INPUT_SVG = "yasa-input.svg";

	private static Model SA;

	@BeforeClass
	public static void importTestData() {
		SA = new Model();
		Assert.assertTrue(IMPORT_FILE_NOT_VALID, new ImportCSV().importModel(SA, ModelTest.IMPORT_PATH
				+ "input-caller-callee.csv", ModelTest.IMPORT_PATH + "input-node-cluster.csv", 10));
		Assert.assertEquals(IMPORT_FILE_NOT_VALID, 36, SA.getNodeCount());
		Assert.assertEquals(IMPORT_FILE_NOT_VALID, 43, SA.getLinkCount());
		Assert.assertEquals(IMPORT_FILE_NOT_VALID, 4, SA.getClusterCount());

		final CreateSVG createSVG = new CreateSVG();
		Assert.assertTrue(EXPORT_FILE_NOT_VALID, createSVG.exportData(SA, IMPORT_PATH + YASA_INPUT_SVG, false));
	}

	@Test
	public final void runOpimizationClusters() {
		SA.initParameters(false);
		SA.setIterations(10);
		SA.run();
		final CreateSVG createSVG = new CreateSVG();
		Assert.assertTrue(EXPORT_FILE_NOT_VALID, CreateCSV.exportData(SA, IMPORT_PATH + "yasa-output.csv"));
		Assert.assertTrue(EXPORT_FILE_NOT_VALID, createSVG.exportData(SA, IMPORT_PATH + "yasa-output.svg", false));
	}

	@Test
	public final void runOpimizationPosition() {
		SA.initParameters(true);
		SA.setIterations(10);
		SA.run();
		final CreateSVG createSVG = new CreateSVG();
		Assert.assertTrue(EXPORT_FILE_NOT_VALID, CreateCSV.exportData(SA, IMPORT_PATH + "yasa-output.csv"));
		Assert.assertTrue(EXPORT_FILE_NOT_VALID, createSVG.exportData(SA, IMPORT_PATH + "yasa-output.svg", false));
	}

	@Test
	public final void runGetterAndSetter() {
		SA.initParameters(true);
		Assert.assertEquals(Double.valueOf(5.08351893845611), Double.valueOf(SA.getDecay()));
		Assert.assertEquals(Long.valueOf(36000), Long.valueOf(SA.getIterations()));
		Assert.assertEquals(Long.valueOf(30), Long.valueOf(SA.getSteps()));
		Assert.assertEquals(Double.valueOf(0.1), Double.valueOf(SA.getTemperature()));

		SA.setDecay(0.5);
		SA.setIterations(1000);
		SA.setSteps(40);
		SA.setTemperature(1.5);

		Assert.assertEquals(Double.valueOf(0.5), Double.valueOf(SA.getDecay()));
		Assert.assertEquals(Long.valueOf(1000), Long.valueOf(SA.getIterations()));
		Assert.assertEquals(Long.valueOf(40), Long.valueOf(SA.getSteps()));
		Assert.assertEquals(Double.valueOf(1.5), Double.valueOf(SA.getTemperature()));
	}

}
