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

package com.sw_engineering_candies.yasa.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sw_engineering_candies.yasa.io.Status;

public final class Model implements Parameter {

	public static final String DEFAULT_CLUSTER_NAME = "-";

	private static final String STRING_FORMAT_LONG = "%10d";

	private static final String STRING_FORMAT_FLOAT = "%f";

	/** standard logger (see log4j.properties file for details) */
	private static final Logger LOGGER = Logger.getLogger(Model.class);

	/** list of all nodes in the model; used for random access by number */
	public static final int DEFAULT_SIZE_NODE_NUMBER = 2500;
	private final List<Node> nodes = new ArrayList<Node>(DEFAULT_SIZE_NODE_NUMBER);

	/** list of all links in the model */
	private static final int DEFAULT_SIZE_LINK_NUMBER = 50000;
	private final List<Link> links = new ArrayList<Link>(DEFAULT_SIZE_LINK_NUMBER);

	/** list of all clusters in the model; used for random access by name */
	public static final int DEFAULT_SIZE_CLUSTER_NUMBER = 20;
	private final List<Cluster> clusters = new ArrayList<Cluster>(DEFAULT_SIZE_CLUSTER_NUMBER);

	/** stores the current status during the optimization */
	private final Status status = new Status();

	/** dependent from the size of the model and the type of optimization */
	private long iterations = 0L;

	/** steps of optimization, for each step the temperature will be changed */
	private long steps = 0L;

	/** factor for the evaluation of the temperature */
	private double decay = 0.0d;

	/** start temperature */
	private double temperature = 0.0d;

	/** factor to scale the cost functions to 100% */
	private double costFactor = 1.0d;

	/** maximal number of columns of the grid */
	private int columns = 0;

	/** store the old cluster during an iteration to undo the change */
	private Cluster firstCluster, oldCluster = null;

	/** store the first node candidate during an iteration to undo the change */
	private Node firstNode = null;

	/** store the second node candidate during an iteration to undo the change */
	private Node secondNode = null;

	/** defines the type of the cost function */
	private boolean optimizePosition = true;

	private double getGlobalCost(final double d) {
		long totalCosts = 0;
		if (optimizePosition) {
			for (final Node node : nodes) {
				totalCosts += getPositionDeltaCost(node);
			}
		} else {
			for (final Node node : nodes) {
				if (!node.isClusterNode()) {
					totalCosts += getClusterDeltaCost(node);
				}
			}
		}
		return totalCosts / d;
	}

	public long getClusterDeltaCost(final Node node) {
		long totalCosts = 0;
		for (final Link link : node.getLinks()) {
			totalCosts += getLinkCost(link, node);
		}
		return totalCosts;
	}

	public long getPositionDeltaCost(final Node node) {
		long cost = 0L;
		for (final Link link : node.getLinks()) {
			final Node target = link.getTarget();
			final long target1 = (node.getColumn() - target.getColumn()) * (node.getColumn() - target.getColumn())
					+ (node.getRow() - target.getRow()) * (node.getRow() - target.getRow());
			cost += target1 == 1 ? 0 : link.isClusterLink() ? target1 : 2 * target1;

			final Node source = link.getSource();
			final long source1 = (node.getColumn() - source.getColumn()) * (node.getColumn() - source.getColumn())
					+ (node.getRow() - source.getRow()) * (node.getRow() - source.getRow());
			cost += source1 == 1 ? 0 : link.isClusterLink() ? source1 : 2 * source1;
		}
		return cost;
	}

	private static long getLinkCost(final Link link, final Node node) {

		if (link.getSource() == node) {
			return node.getCluster() != link.getTarget().getCluster() ? COST_FACTOR_LINKS : 0;
		} else {
			return node.getCluster() != link.getSource().getCluster() ? COST_FACTOR_LINKS : 0;
		}
	}

	private double evaluateNodeClusterCandidate(final double currentCost) {
		do {
			final int index = (int) Math.round(Math.floor(Math.random() * nodes.size()));
			firstNode = nodes.get(index);
		} while (!firstNode.isFrozen());

		firstCluster = firstNode.getCluster();
		final double oldDeltaCostFunction = getClusterDeltaCost(firstNode);

		// create the new state
		firstNode.setCluster(evaluateRandomCluster(firstCluster));

		// return the cost function
		final double newDeltaCostFunction = getClusterDeltaCost(firstNode);
		return currentCost + (newDeltaCostFunction - oldDeltaCostFunction) / costFactor;
	}

	private double evaluateNodePositionCandidate(final double currentCost) {
	//	do {
			// remember the old state
			int index = (int) Math.round(Math.floor(Math.random() * nodes.size()));
			firstNode = nodes.get(index);

			index = (int) Math.round(Math.floor(Math.random() * nodes.size()));
			secondNode = nodes.get(index);
	//	} while (firstNode != secondNode);

		final double oldDeltaCostFunction;
		oldDeltaCostFunction = getPositionDeltaCost(firstNode) + getPositionDeltaCost(secondNode);

		swapNodes();

		// return the cost function
		final double newDeltaCostFunction;
		newDeltaCostFunction = getPositionDeltaCost(firstNode) + getPositionDeltaCost(secondNode);

		return currentCost + (newDeltaCostFunction - oldDeltaCostFunction) / costFactor;
	}

	private void evaluateNextStepPosition() {
		// current value of the cost function
		double currentCost = getGlobalCost(costFactor);
		double bestCost = currentCost;
		status.reset();
		status.setTemperature(getCurrentTemperature());
		status.incrementNumber();

		for (long index = 1; index <= iterations; index++) {

			final double currentCostOLD = currentCost;
			currentCost = evaluateNodePositionCandidate(currentCost);
			if (currentCost > bestCost) {
				final double temp = (bestCost - currentCost) / status.getTemperature();
				if (temp > -15.0 && Math.exp(temp) > Math.random()) {
					bestCost = currentCost;
					status.incrementWorse();
				} else {
					swapNodes();
					status.incrementRejected();
					currentCost = currentCostOLD;
				}
			} else if (currentCost < bestCost) {
				bestCost = currentCost;
				status.incrementBetter();
			} else {
				status.incrementConst();
			}
		}
	}

	private void swapNodes() {
		final long xTemp = firstNode.getColumn();
		final long yTemp = firstNode.getRow();
		firstNode.setColumn(secondNode.getColumn());
		firstNode.setRow(secondNode.getRow());
		secondNode.setColumn(xTemp);
		secondNode.setRow(yTemp);
	}

	private void evaluateNextStepCluster() {
		// current value of the cost function
		double currentCost = getGlobalCost(costFactor);
		double bestCost = currentCost;
		status.reset();
		status.setTemperature(getCurrentTemperature());
		status.incrementNumber();

		for (long index = 1; index <= iterations; index++) {
			final double currentCostOLD = currentCost;
			currentCost = evaluateNodeClusterCandidate(currentCost);
			if (currentCost > bestCost) {
				final double temp = (bestCost - currentCost) / status.getTemperature();
				if (temp > -15.0 && Math.exp(temp) > Math.random()) {
					bestCost = currentCost;
					status.incrementWorse();
				} else {
					firstNode.setCluster(firstCluster);
					status.incrementRejected();
					currentCost = currentCostOLD;
				}
			} else if (currentCost < bestCost) {
				bestCost = currentCost;
				status.incrementBetter();
			} else {
				status.incrementConst();
			}
		}
	}

	public double getTemperature() {
		return temperature;
	}

	private double getCurrentTemperature() {
		return temperature * Math.pow(1 - (double) status.getStep() / (double) steps, decay);
	}

	private Cluster evaluateRandomCluster(final Cluster cluster) {
		final int index = 1 + (int) Math.round(Math.floor(Math.random() * (clusters.size() - 1)));
		final Cluster newCluster = clusters.get(index);
		if (cluster != newCluster) {
			return newCluster;
		} else {
			return evaluateRandomCluster(cluster);
		}
	}

	public long getLinkCount() {
		return links.size();
	}

	public long getClusterCount() {
		return clusters.size();
	}

	public List<Node> getNodes() {
		return nodes;
	}

	public List<Link> getLinks() {
		return links;
	}

	public List<Cluster> getClusters() {
		return clusters;
	}

	public int getColumns() {
		return columns;
	}

	public void setColumns(final int c) {
		columns = c;
	}

	public long getNodeCount() {
		return nodes.size();
	}

	private void outputStatus() {
		final String s1 = String.format(" %3d/%3d   ", status.getStep(), steps);
		final String s2 = String.format("%.2e", status.getTemperature());
		final String s3 = String.format(STRING_FORMAT_LONG, status.getNoChange());
		final String s4 = String.format(STRING_FORMAT_LONG, status.getBetter());
		final String s5 = String.format(STRING_FORMAT_LONG, status.getWorse());
		final String s6 = String.format(STRING_FORMAT_LONG, status.getRejected());
		final double globalCost = getGlobalCost(costFactor);
		final String s7 = String.format(" %9.2f%s", globalCost * 100, "%");

		if (status.getStep() != 0) {
			LOGGER.debug(s1 + s2 + s3 + s4 + s5 + s6 + s7);
		} else {
			LOGGER.debug(s1 + "       -         -         -         -         -" + s7);

		}
	}

	private static void outputStatusHeader() {
		LOGGER.debug("PROGRESS TEMPERATUR     CONST    BETTER     WORSE  REJECTED       COST");
		LOGGER.debug("");
	}

	public void run() {
		costFactor = getGlobalCost(1.0);

		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("");
			LOGGER.info("-----------------------------------------------------------------------");
			LOGGER.info("optimize position            = " + optimizePosition);
			LOGGER.info("decay                        = " + String.format(STRING_FORMAT_FLOAT, decay));
			LOGGER.info("temperature                  = " + String.format(STRING_FORMAT_FLOAT, temperature));
			LOGGER.info("iterations                   = " + iterations);
			LOGGER.info("steps                        = " + steps);
			LOGGER.info("inital cost                  = " + costFactor);
			LOGGER.info("");
		}

		// start optimization
		status.setStep(0);
		outputStatusHeader();
		outputStatus();
		while (status.getStep() < steps) {
			if (optimizePosition) {
				evaluateNextStepPosition();
			} else {
				evaluateNextStepCluster();
			}
			outputStatus();
			if (status.getBetter() == 0) {
				break;
			}
		}
		LOGGER.info("-----------------------------------------------------------------------");
		LOGGER.info("");

	}

	public void initParameters(final boolean position) {
		this.optimizePosition = position;
		iterations = ITTERATIONS_PER_NODE * nodes.size();
		decay = RUN_DECAY + (nodes.size() > 0 ? Math.log(nodes.size()) : 0);
		temperature = TEMPERATURE;
		steps = STEPS;
		oldCluster = clusters.get(0);
		firstCluster = oldCluster;
	}

	
	public void initNodePostion() {
		setColumns((int) Math.sqrt(getNodeCount()) - 1);
		int x = 0;
		int y = 0;
		final List<Node> nodes = getNodes();
		for (final Node node : nodes) {
			node.setColumn(x);
			node.setRow(y);
			if (x == getColumns()) {
				y++;
				x = 0;
			} else {
				x++;
			}
		}
	}
	
	
	public long getIterations() {
		return iterations;
	}

	public void setIterations(final long i) {
		this.iterations = i;
	}

	public long getSteps() {
		return steps;
	}

	public void setSteps(final long s) {
		this.steps = s;
	}

	public double getDecay() {
		return decay;
	}

	public void setDecay(final double d) {
		this.decay = d;
	}

	public void setTemperature(final double t) {
		this.temperature = t;
	}

	public Status getStatus() {
		return status;
	}

	public void reconnectClusterNodes() {
		LOGGER.info(String.format("reconnect cluster-nodes"));

		// find all cluster nodes for later use
		final Map<String, Node> nodesMap = new HashMap<String, Node>(Model.DEFAULT_SIZE_NODE_NUMBER);
		for (final Node node : getNodes()) {
			if (node.isClusterNode()) {
				nodesMap.put(node.getName(), node);
			}
		}

		// Create new cluster links
		for (final Node node : getNodes()) {
			if ("-".equals(node.getInitialClusterName())) {
				final Cluster cluster = node.getCluster();
				if (!cluster.getName().equals(DEFAULT_CLUSTER_NAME)) {
					final Node clusterNode = nodesMap.get("C@" + cluster.getName());
					if (clusterNode != null && !node.isClusterNode()) {
						links.add(0, new Link(clusterNode, node, true));
					}
				}
			}
		}

	}
}
