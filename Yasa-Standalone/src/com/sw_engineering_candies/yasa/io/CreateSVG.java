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
import java.util.Locale;

import org.apache.log4j.Logger;

import com.sw_engineering_candies.yasa.model.Link;
import com.sw_engineering_candies.yasa.model.Model;
import com.sw_engineering_candies.yasa.model.Node;

public final class CreateSVG {

	private static final double OUTPUT_Y = 620.0;

	private static final double OUTPUT_X = 600.0;

	private final Point canvas = new Point(OUTPUT_X, OUTPUT_Y);

	private static final Locale LOCALE = new Locale("en", "EN");

	private FileWriter fw = null;

	private Model model;

	/** standard logger (see log4j.properties file for details) */
	private static final Logger LOGGER = Logger.getLogger(CreateSVG.class);

	public boolean exportData(final Model model, final String exportFileName, final boolean colorCluster) {
		this.model = model;
		fw = FileUtility.createFileWriter(exportFileName);
		if (null != fw) {
			try {
				final double rectLength = OUTPUT_Y / 2.1 / (2 + model.getColumns());
				final double radius = rectLength * 0.5;
				final double lineSize = rectLength * 0.05;
				final double fontSize = rectLength / 2.5;

				createStart(canvas);

				for (final Link link : model.getLinks()) {
					final Integer sourceClusterPos = model.getClusters().indexOf(link.getSource().getCluster());
					final String clusterColor = getClusterColor(sourceClusterPos);

					if (link.getSource().isClusterNode()) {
						final Point source = nodePosCenter(new Point(0, 0), link.getSource(), model.getColumns());
						final Point target = nodePosMoved(new Point(0, 0), link.getTarget(), model.getColumns());
						drawLine(source, target, lineSize, link.isClusterLink(), clusterColor, false);
					} else if (link.getTarget().isClusterNode()) {
						final Point source = nodePosMoved(new Point(0, 0), link.getSource(), model.getColumns());
						final Point target = nodePosCenter(new Point(0, 0), link.getTarget(), model.getColumns());
						drawLine(source, target, lineSize, link.isClusterLink(), clusterColor, false);
					} else {
						final Point source = nodePosMoved(new Point(0, 0), link.getSource(), model.getColumns());
						final Point target = nodePosMoved(new Point(0, 0), link.getTarget(), model.getColumns());
						drawLine(source, target, lineSize, link.isClusterLink(), clusterColor, link.getSource()
								.getCluster() != link.getTarget().getCluster());
					}
				}
				for (final Node node : model.getNodes()) {
					final Integer sourceClusterPos = model.getClusters().indexOf(node.getCluster());
					final String clusterColor = getClusterColor(sourceClusterPos);
					if (node.isClusterNode()) {
						final Point center = nodePosCenter(new Point(0, 0), node, model.getColumns());
						drawRect(center, radius, clusterColor);
						drawText(center, " ", node.getCluster().getName(), " ",  rectLength, fontSize);
					} else {
						final Point center = nodePosMoved(new Point(0, 0), node, model.getColumns());
						drawCircle(center, radius, clusterColor);
						drawText(center, " ", node.getName(), " ",  rectLength, fontSize);
					}
				}

				createEnd();
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

	private String getClusterColor(final Integer sourceClusterPos) {
		return getColorMap()[sourceClusterPos >= 0 ? sourceClusterPos % getColorMap().length : 0];
	}

	private Point nodePosCenter(final Point p, final Node n, final int i) {
		p.setLocation((1.0 + n.getColumn()) * canvas.x / (i + 2), (1.0 + n.getRow()) * canvas.y / (i + 4));
		return p;
	}

	private Point nodePosMoved(final Point p, final Node n, final int i) {
		p.setLocation(nodePosCenter(p, n, i).x , nodePosCenter(p, n, i).y );
		return p;
	}

	private static final String[] COLORS = new String[] {//
	"gainsboro", "gold", "yellowgreen", "deeppink", "darkviolet", "forestgreen", "cornflowerblue", "deepskyblue",
			"slategray", "turquoise", "wheat", "darkcyan", "darkolivegreen", "lawngreen", "paleturquoise", "yellow",
			"orangered", "brown", "dodgerblue" };

	private void createEnd() throws IOException {
		fw.append("</g>\n\n</svg>\n");
	}

	private void createStart(final Point point) throws IOException {
		fw.append("<?xml version='1.0'?>\n");
		fw.append(String.format(LOCALE, "<svg xmlns='http://www.w3.org/2000/svg' "
				+ "width=\"%.1f\" height=\"%.1f\" x=\"0\" y=\"0\">\n", point.getX(), point.getY()));
		fw.append("<g id=\"my_root\" style=\"stroke-width:1.0\" >\n");
		// define the arrow marker
		fw.append("<defs>");
		fw.append("<marker id=\"MidMarkerblue\" viewBox = \"0 0 10 10\" refX = \"1\" "
				+ "refY = \"5\" markerUnits=\"strokeWidth\" markerWidth = \"5\" markerHeight = \"5\" "
				+ "fill = \"none\" orient = \"auto\" >");
		fw.append("<polyline points=\"0,0 10,5 0,10 1,5\" fill=\"blue\" />");
		fw.append("</marker>");
		fw.append("</defs>");
		fw.append("<defs>");
		fw.append("<marker id=\"MidMarkerred\" viewBox = \"0 0 10 10\" refX = \"1\" "
				+ "refY = \"5\" markerUnits=\"strokeWidth\" markerWidth = \"5\" markerHeight = \"5\" "
				+ "fill = \"none\" orient = \"auto\" >");
		fw.append("<polyline points=\"0,0 10,5 0,10 1,5\" fill=\"red\" />");
		fw.append("</marker>");
		fw.append("</defs>");
	}

	private void drawText(final Point center, final String line1, final String line2, final String line3,
			final double rectLength, final double fontSize) throws IOException {
		final double x = center.getX();
		final double y = center.getY() - fontSize * 0.8;
		fw.append(String.format(LOCALE,
				"<text x=\"%.1f\" y=\"%.1f\" text-anchor=\"middle\"  style=\"stroke-width:0.0;fill:black;"
						+ "font-size:%.2f;font-family:Arial\" >" + "<tspan x=\"%.1f\" dy=\"1.2em\">%s</tspan> "
						+ "<tspan x=\"%.1f\" dy=\"1.2em\">%s</tspan> " + "<tspan x=\"%.1f\" dy=\"1.2em\">%s</tspan>"
						+ "</text>\n", x, y, fontSize, x, line1, x, line2, x, line3));

	}

	private void drawCircle(final Point center, final double radius, final String color) throws IOException {
		fw.append(String.format(LOCALE, "<circle cx=\"%.1f\" cy=\"%.1f\" r=\"%.3f\" fill=\"%s\" stroke=\"%s\" />\n",
				center.getX(), center.getY(), radius, color, color));
	}

	private void drawRect(final Point sourcePos, final double d, final String color) throws IOException {
		fw.append(String.format(LOCALE, "<rect x=\"%.1f\" y=\"%.1f\" width=\"%.1f\" height=\"%.1f\"" + " style=\"fill:"
				+ "white" + "\" " + "stroke=\"" + color + "\" " + " />\n", sourcePos.getX() - d, sourcePos.getY() - d,
				d * 2, d * 2));
	}

	private void drawLine(final Point from, final Point to, final double lineSize, final boolean b, final String color,
			final boolean crossCluster) throws IOException {

		final double middleX = (from.getX() + to.getX()) / 2;
		final double middleY = (from.getY() + to.getY()) / 2;

		if (b) {
			fw.append(String
					.format(LOCALE,
							"<path fill=\"none\" stroke-width=\"1\" stroke=\"%s\" stroke-dasharray=\"1,3\"  d=\"M %.1f %.1f %.1f %.1f \"   />",
							color, from.getX(), from.getY(), to.getX(), to.getY()));
		} else {
			final String lineColor = crossCluster ? "red" : "blue";
			fw.append(String.format(LOCALE, "<path fill=\"none\"  marker-mid=\"url(#MidMarker%s)\" "
					+ "stroke=\"%s\"  d=\"M %.1f %.1f T %.1f %.1f %.1f %.1f \"   />", lineColor, lineColor,
					from.getX(), from.getY(), middleX, middleY, to.getX(), to.getY()));
		}

	}

	private static String[] getColorMap() {
		return COLORS;
	}


}
