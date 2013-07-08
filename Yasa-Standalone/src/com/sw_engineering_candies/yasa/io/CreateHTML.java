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

import org.apache.log4j.Logger;

public final class CreateHTML {

	/**
	 * Private constructor to prevent class instantiation.
	 */
	private CreateHTML() {
	}

	/** standard logger (see log4j.properties file for details) */
	private static final Logger LOGGER = Logger.getLogger(CreateHTML.class);

	public static boolean createFile(final String exportFileName) {
		final FileWriter fw = FileUtility.createFileWriter(exportFileName);
		if (null != fw) {
			try {
				fw.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				fw.append("<!DOCTYPE html PUBLIC \"-_W3C_DTD XHTML 1.0 Strict_EN\"");
				fw.append("	\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\" [");
				fw.append("   <!ELEMENT embed EMPTY>");
				fw.append("	<!ATTLIST embed");
				fw.append("	src CDATA #REQUIRED");
				fw.append("	width CDATA #IMPLIED");
				fw.append("	height CDATA #IMPLIED>");
				fw.append("	<!ENTITY % inline");
				fw.append("\"embed | a | %special; | %fontstyle; | %phrase; | %inline.forms;\">");
				fw.append("<html>");
				fw.append("	<head>");
				fw.append("		<title>yasa result</title>");
				fw.append("	</head>");
				fw.append("	<body>");
				fw.append("		<font size=\"2\" face=\"Courier New\">");
				fw.append("		<table border=\"1\">");
				fw.append("			<tr>");
				fw.append("    			<th>Step 1 - original input data</th>");
				fw.append("    			<th>Step 2 - optimize the position</th>");
				fw.append("  		</tr>");
				fw.append("  		<tr>");
				fw.append(" 	   		<td>");
				fw.append("   	     		<object type=\"image/svg+xml\" data=\"./yasa-step-1.svg\"");
				fw.append(" 	       			name=\"Output\"  width=\"500\" height=\"520\">");
				fw.append("					</object>");
				fw.append(" 	   		</td>");
				fw.append("		   		<td>");
				fw.append("		        	<object type=\"image/svg+xml\" data=\"./yasa-step-2.svg\"");
				fw.append(" 	       			name=\"Output\"  width=\"500\" height=\"520\">");
				fw.append("					</object>");
				fw.append(" 	   		</td>");
				fw.append("  		</tr>");
				fw.append("			<tr>");
				fw.append("    			<th>Step 3 - optimize the cluster assignment</th>");
				fw.append("    			<th>Step 4 - optimize the position</th>");
				fw.append("  		</tr>");
				fw.append("  		<tr>");
				fw.append(" 	   		<td>");
				fw.append("   	     		<object type=\"image/svg+xml\" data=\"./yasa-step-3.svg\"");
				fw.append(" 	       			name=\"Output\"  width=\"500\" height=\"520\">");
				fw.append("					</object>");
				fw.append(" 	   		</td>");
				fw.append("		   		<td>");
				fw.append("		        	<object type=\"image/svg+xml\" data=\"./yasa-step-4.svg\"");
				fw.append(" 	       			name=\"Output\"  width=\"500\" height=\"520\">");
				fw.append("					</object>");
				fw.append(" 	   		</td>");
				fw.append("  		</tr>");
				fw.append("		</table>");
				fw.append("		<p/>");
				fw.append(" 		<object data=\"yasa-output.txt\" type=\"text/plain\" width=\"1018\" height=\"1260\" border=\"0\">");
				fw.append("   			Your browser can't display the element!");
				fw.append("  		</object>");
				fw.append("    		</td>");
				fw.append("		</font>");
				fw.append("	</body>");
				fw.append("</html>");

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
}
