/*
 * Copyright (C) 2009-2013, Markus Sprunck <markus.sprunck@mnet-online.de>
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

package com.sw_engineering_candies.yasa;

import org.apache.log4j.Logger;

import com.sw_engineering_candies.yasa.io.CreateCSV;
import com.sw_engineering_candies.yasa.io.CreateHTML;
import com.sw_engineering_candies.yasa.io.CreateSVG;
import com.sw_engineering_candies.yasa.io.ImportCSV;
import com.sw_engineering_candies.yasa.model.Model;

public final class Starter {

	public static final String PARAMETER_P = "-p";

	public static final String PARAMETER_D = "-d";

	public static final String PARAMETER_T = "-t";

	public static final String PARAMETER_S = "-s";

	public static final String PARAMETER_I = "-i";

	private static long iterations = 0;

	private static long steps = 0;

	private static int prune = 1000;

	private static double decay = 0;

	private static double temperature = 0;

	private static String fileNameCallerCallee = "input-caller-callee.csv";

	private static String fileNameNodeCluster = "input-node-cluster.csv";

	/** standard logger (see log4j.properties file for details) */
	private static final Logger LOGGER = Logger.getLogger(Starter.class);

	public static boolean parseCommandLine(final String[] args) {
		for (int i = 0; i < args.length; i++) {
			if (PARAMETER_I.equalsIgnoreCase(args[i])) {
				if (args.length <= i + 1) {
					return false;
				}
				iterations = Long.valueOf(args[1 + i]);
			} else if (PARAMETER_S.equalsIgnoreCase(args[i])) {
				if (args.length <= i + 1) {
					return false;
				}
				steps = Long.valueOf(args[1 + i]);
			} else if (PARAMETER_T.equalsIgnoreCase(args[i])) {
				if (args.length <= i + 1) {
					return false;
				}
				temperature = Double.valueOf(args[1 + i]);
			} else if (PARAMETER_D.equalsIgnoreCase(args[i])) {
				if (args.length <= i + 1) {
					return false;
				}
				decay = Double.valueOf(args[1 + i]);
			} else if (PARAMETER_P.equalsIgnoreCase(args[i])) {
				if (args.length <= i + 1) {
					return false;
				}
				prune = Integer.parseInt(args[1 + i]);
			}

		}
		return true;
	}

	public static void main(final String[] args) {

		LOGGER.info("yasa v1.02 (c) 2013 by Markus Sprunck, Munich, Germany");
		LOGGER.info("");
		final Model sa = new Model();

		if (!Starter.parseCommandLine(args)) {
			LOGGER.error("ERROR wrong parameter list");
			return;
		}

		if (new ImportCSV().importModel(sa, fileNameCallerCallee, fileNameNodeCluster, prune)) {

			// 1. optimize the position
			final CreateSVG createSVG = new CreateSVG();
			sa.initParameters(true);
			setParameter(sa);
			sa.run();
			createSVG.exportData(sa, "yasa-input-sorted.svg", true);

			// 2. optimize the cluster assignment
			sa.initParameters(false);
			setParameter(sa);
			sa.run();

			// 3. reconnet the cluster nodes
			sa.reconnectClusterNodes();

			// 4. optimize the position
			sa.initParameters(true);
			setParameter(sa);
			sa.run();

			createSVG.exportData(sa, "yasa-output.svg", true);
			CreateCSV.exportData(sa, "yasa-output.csv");
			CreateHTML.createFile("yasa-result.html");
		}
		LOGGER.info("");
		LOGGER.info("end");

	}

	private static void setParameter(final Model sa) {
		if (0 < iterations) {
			sa.setIterations(iterations);
		}
		if (0 < temperature) {
			sa.setTemperature(temperature);
		}
		if (0 < steps) {
			sa.setSteps(steps);
		}
		if (0 < decay) {
			sa.setDecay(decay);
		}
	}

}
