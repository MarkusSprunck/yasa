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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sw_engineering_candies.yasa.model.Cluster;
import com.sw_engineering_candies.yasa.model.Link;
import com.sw_engineering_candies.yasa.model.Model;
import com.sw_engineering_candies.yasa.model.Node;

public final class ImportCSV {

	/** standard logger (see log4j.properties file for details) */
	private static final Logger LOGGER = Logger.getLogger(ImportCSV.class);

	/** buffer for imported files */
	private List<String> linesCallerCallee, linesNodesCluster;

	/**
	 * This map is used to count how often each node is called from another
	 * node. If there are more callers than the pruneThreshold, this node will
	 * not be imported.
	 */
	private final Map<String, Integer> calledNodesCountMap = new HashMap<String, Integer>(Model.DEFAULT_SIZE_NODE_NUMBER);

	private final Map<String, Node> createdNodesMap = new HashMap<String, Node>(Model.DEFAULT_SIZE_NODE_NUMBER);
	private final Map<String, Boolean> prunedNodesMap = new HashMap<String, Boolean>(Model.DEFAULT_SIZE_NODE_NUMBER);

	private final Map<String, Boolean> createdLinksMap = new HashMap<String, Boolean>(Model.DEFAULT_SIZE_CLUSTER_NUMBER);
	private final Map<String, Boolean> prunedLinksMap = new HashMap<String, Boolean>(Model.DEFAULT_SIZE_CLUSTER_NUMBER);

	private final Map<String, Cluster> createdClusterMap = new HashMap<String, Cluster>(Model.DEFAULT_SIZE_CLUSTER_NUMBER);
	private final Map<String, Boolean> prunedClusterMap = new HashMap<String, Boolean>(Model.DEFAULT_SIZE_CLUSTER_NUMBER);

	private int pruneThreshold = 1000;

	private Model model = null;

	public ImportCSV(final Model model, final int pruneThreshold) {

		LOGGER.debug(String.format("prune threshold %d", pruneThreshold));
		this.pruneThreshold = pruneThreshold;

		this.model = model;
		if (null != this.model) {
			this.model.getStatus().reset();

			// create default cluster
			final Cluster value = new Cluster(Model.DEFAULT_CLUSTER_NAME);
			createdClusterMap.put(Model.DEFAULT_CLUSTER_NAME, value);
			model.getClusters().add(value);
		}
	}

	public boolean importModel(final String fileNameCallerCallee, final String fileNameNodeCluster) {

		if (null == this.model) {
			return false;
		}

		linesCallerCallee = inportLines(fileNameCallerCallee);
		linesNodesCluster = inportLines(fileNameNodeCluster);

		initNodesPruneMap();

		createCluster();
		createNodes();
		createLinks();

		model.initParameters(false);
		model.initNodePostion();

		LOGGER.debug(String.format("created nodes         %6d \t%d pruned  ", createdNodesMap.size(), prunedNodesMap.size()));
		LOGGER.debug(String.format("created links         %6d \t%d pruned  ", createdLinksMap.size(), prunedLinksMap.size()));
		LOGGER.debug(String.format("created clusters      %6d \t%d pruned  ", prunedClusterMap.size(), prunedClusterMap.size()));

		return true;
	}

	private void createCluster() {
		for (final String line : linesNodesCluster) {
			final List<String> tokenList = FileUtility.tokenizeString(line, ";");
			if (2 == tokenList.size()) {
				final String caller = tokenList.get(0).trim().replaceAll("\"", "");
				final String cluster = tokenList.get(1).trim().replaceAll("\"", "");

				if (!createdClusterMap.containsKey(cluster)) {

					if (isNodePruned(caller)) {
						prunedClusterMap.put(cluster, Boolean.TRUE);
					} else {
						// Create cluster node
						Cluster clusterNew = null;
						final String clusterNodeName = "C@" + cluster;
						Node clusterNode = createdNodesMap.get(clusterNodeName);
						if (null == clusterNode) {
							clusterNew = new Cluster(cluster);
							clusterNode = new Node(clusterNodeName, clusterNew.getName(), clusterNew, true);
							createdNodesMap.put(clusterNodeName, clusterNode);
							model.getNodes().add(clusterNode);
							createdClusterMap.put(clusterNodeName, clusterNew);
							model.getClusters().add(clusterNew);
						} else {
							clusterNew = createdClusterMap.get(clusterNodeName);
						}
						// Create node in cases it doesn't exists
						Node node = createdNodesMap.get(caller);
						if (null == node) {
							node = new Node(caller, clusterNew.getName(), clusterNew, false);
							createdNodesMap.put(caller, node);
							model.getNodes().add(node);
						}

						// Create cluster link
						model.getLinks().add(new Link(clusterNode, node, true));
					}
				}
			}
		}
	}

	private void initNodesPruneMap() {
		for (final String line : linesCallerCallee) {
			final List<String> tokenList = FileUtility.tokenizeString(line, ";");
			if (2 == tokenList.size()) {
				final String caller = tokenList.get(0).trim().replaceAll("\"", "");

				if (!calledNodesCountMap.containsKey(caller)) {
					calledNodesCountMap.put(caller, Integer.valueOf(0));
				}
				calledNodesCountMap.put(caller, calledNodesCountMap.get(caller) + 1);
			}
		}
	}

	private void createNodes() {
		for (final String line : linesCallerCallee) {
			final List<String> tokenList = FileUtility.tokenizeString(line, ";");
			if (2 == tokenList.size()) {
				final String caller = tokenList.get(0).trim().replaceAll("\"", "");
				final String callee = tokenList.get(1).trim().replaceAll("\"", "");

				if (isNodePruned(callee)) {
					prunedNodesMap.put(callee, Boolean.TRUE);
				} else {
					final Cluster sourceCluster = createdClusterMap.get(Model.DEFAULT_CLUSTER_NAME);
					if (!createdNodesMap.containsKey(callee)) {
						Node item = createdNodesMap.get(callee);
						if (null == item) {
							item = new Node(callee, sourceCluster.getName(), sourceCluster, false);
							createdNodesMap.put(callee, item);
							model.getNodes().add(item);
						} else {
							item.setCluster(sourceCluster);
						}
					} else {
						if (!sourceCluster.getName().equals(Model.DEFAULT_CLUSTER_NAME)) {
							createdNodesMap.get(callee).setCluster(sourceCluster);
						}
					}
				}

				if (isNodePruned(caller)) {
					prunedNodesMap.put(caller, Boolean.TRUE);
				} else {
					if (!createdNodesMap.containsKey(caller)) {
						final Cluster cluster = createdClusterMap.get(Model.DEFAULT_CLUSTER_NAME);
						Node item = createdNodesMap.get(caller);
						if (null == item) {
							item = new Node(caller, cluster.getName(), cluster, false);
							createdNodesMap.put(caller, item);
							model.getNodes().add(item);
						} else {
							item.setCluster(cluster);
						}
					}
				}

			}
		}
	}

	private void createLinks() {
		for (final String line : linesCallerCallee) {
			final List<String> tokenList = FileUtility.tokenizeString(line, ";");
			if (2 == tokenList.size()) {
				final String sourceName = tokenList.get(1).trim().replaceAll("\"", "");
				final String targetName = tokenList.get(0).trim().replaceAll("\"", "");

				if (isNodePruned(targetName)) {
					prunedLinksMap.put(sourceName + "->" + targetName, Boolean.TRUE);
				} else {
					if (createdNodesMap.containsKey(sourceName) && createdNodesMap.containsKey(targetName)) {
						model.getLinks().add(new Link(createdNodesMap.get(sourceName), createdNodesMap.get(targetName), false));
						createdLinksMap.put(sourceName + "->" + targetName, Boolean.TRUE);
					}
				}
			}
		}
	}

	private List<String> inportLines(final String fileName) {
		final List<String> result = new ArrayList<String>(Model.DEFAULT_SIZE_NODE_NUMBER);
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(fileName));
			String line = bufferedReader.readLine();
			while (null != line) {
				if (!line.isEmpty()) {
					result.add(line);
				}
				line = bufferedReader.readLine();
			}
			bufferedReader.close();
		} catch (final IOException e) {
			LOGGER.error(e.getMessage());
		} finally {
			if (null != bufferedReader) {
				try {
					bufferedReader.close();
				} catch (final IOException e) {
					LOGGER.error(e.getMessage());
				}
			}
		}
		LOGGER.info(String.format("file '%s' imported %d lines", fileName, result.size()));
		return result;
	}

	private boolean isNodePruned(final String node) {
		return null != calledNodesCountMap.get(node) && calledNodesCountMap.get(node) > pruneThreshold;
	}

}
