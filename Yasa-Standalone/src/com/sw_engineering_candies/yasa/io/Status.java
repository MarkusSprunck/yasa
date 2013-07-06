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

public final class Status {

	private long noChange;

	private long better;

	private long worse;

	private long rejected;

	private double temperature;

	private long step;

	public long getBetter() {
		return better;
	}

	public long getNoChange() {
		return noChange;
	}

	public long getRejected() {
		return rejected;
	}

	public long getStep() {
		return step;
	}

	public double getTemperature() {
		return temperature;
	}

	public long getWorse() {
		return worse;
	}

	public void incrementBetter() {
		better++;
	}

	public void incrementConst() {
		noChange++;
	}

	public void incrementNumber() {
		step++;
	}

	public void incrementRejected() {
		rejected++;
	}

	public void incrementWorse() {
		worse++;
	}

	public void reset() {
		noChange = 0;
		better = 0;
		worse = 0;
		rejected = 0;
		temperature = 0.0;
	}

	public void setStep(final long stepNumber) {
		this.step = stepNumber;
	}

	public void setTemperature(final double stateTemperature) {
		this.temperature = stateTemperature;
	}
}
