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

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.Test;

import com.sw_engineering_candies.yasa.Starter;

public class StarterTest {

	@AfterClass
	public static void cleanUp() {
		final File f = new File("yasa-output.txt");
		f.deleteOnExit();
	}

	@Test
	public final void testMainSuccess() {

		final String[] args = new String[] {//
		Starter.PARAMETER_T, "1.11", Starter.PARAMETER_I, "20", Starter.PARAMETER_S, "12", Starter.PARAMETER_D, "0.3", Starter.PARAMETER_P, "12" };
		Assert.assertTrue(Starter.parseCommandLine(args));
	}

	@Test
	public final void testParseCommandLineErrors() {

		final String[] args3 = new String[] {//
		Starter.PARAMETER_T };
		Assert.assertFalse(Starter.parseCommandLine(args3));

		final String[] args4 = new String[] {//
		Starter.PARAMETER_T, "1.11", Starter.PARAMETER_I };
		Assert.assertFalse(Starter.parseCommandLine(args4));

		final String[] args5 = new String[] {//
		Starter.PARAMETER_T, "1.11", Starter.PARAMETER_I, "20", Starter.PARAMETER_S };
		Assert.assertFalse(Starter.parseCommandLine(args5));

		final String[] args6 = new String[] {//
		Starter.PARAMETER_T, "1.11", Starter.PARAMETER_I, "20", Starter.PARAMETER_S, "12", Starter.PARAMETER_D };
		Assert.assertFalse(Starter.parseCommandLine(args6));

		final String[] args7 = new String[] {//
		Starter.PARAMETER_T, "1.11", Starter.PARAMETER_I, "20", Starter.PARAMETER_S, "12", Starter.PARAMETER_D, "0.3", Starter.PARAMETER_P };
		Assert.assertFalse(Starter.parseCommandLine(args7));

	}

}
