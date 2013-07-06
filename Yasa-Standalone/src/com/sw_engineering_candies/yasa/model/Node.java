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
import java.util.Collection;
import java.util.List;

public final class Node {

	@Override
	public String toString() {
		return "Node [clusterNode=" + clusterNode + ", name=" + name + "]";
	}

	/** owning cluster of this node */
	private Cluster cluster = null;

	/** list of all links of the node */
	private final List<Link> links = new ArrayList<Link>(10);

	/** name of this node */
	private String name;

	/** the initial name of this node */
	private final String initialClusterName;

	/** column of this node */
	private long column = 0;

	/** row of this node */
	private long row = 0;

	private final boolean clusterNode;

	private final boolean notFrozen;

	public long getColumn() {
		return column;
	}

	public void setColumn(final long x) {
		this.column = x;
	}

	public long getRow() {
		return row;
	}

	public void setRow(final long y) {
		this.row = y;
	}

	public Cluster getCluster() {
		return cluster;
	}

	public void setCluster(final Cluster newCluster) {
		if (null != this.cluster) {
			this.cluster.removeNode(this);
		}
		newCluster.addNode(this);
		this.cluster = newCluster;
	}

	public Node(final String n, final String in, final Cluster c, final boolean clusterNode) {
		setCluster(c);
		this.name = n;
		this.initialClusterName = in;
		this.clusterNode = clusterNode;
		this.notFrozen = "-".equals(in);
	};

	public boolean isClusterNode() {
		return clusterNode;
	}

	public Collection<Link> getLinks() {
		return links;
	}

	public String getName() {
		return name;
	}

	public String getInitialClusterName() {
		return initialClusterName;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public boolean isFrozen() {
		return notFrozen;
	}
}
