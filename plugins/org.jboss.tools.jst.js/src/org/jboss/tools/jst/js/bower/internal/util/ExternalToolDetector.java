/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jst.js.bower.internal.util;

import java.io.File;

import static org.jboss.tools.jst.js.bower.internal.BowerConstants.*;
import org.jboss.tools.jst.js.internal.util.PlatformUtil;

/**
 * @author Ilya Buziuk (ibuziuk)
 */
public final class ExternalToolDetector {

	private ExternalToolDetector() {
	}

	public static String detectNode() {
		return detectNodeFromPath();
	}
	
	public static String detectBower() {
		String bowerLocation = null;
		String npmLocation = detectNpmFromPath();
		if (npmLocation != null) {
			String separator = File.separator;
			if (!npmLocation.endsWith(separator)) {
				npmLocation = npmLocation + separator;
			}
			
			File bowerHome =  new File(npmLocation, NODE_MODULES + separator + BOWER + separator + BIN);
			if (bowerHome != null && bowerHome.exists()) {
				bowerLocation = bowerHome.getAbsolutePath();
			}
		}
		return bowerLocation;
	}
	
	private static String detectNpmFromPath() {
		return detectFromPath(File.separator + NPM);
	}
	
	private static String detectNodeFromPath() {
		return detectFromPath(File.separator + NODE_JS);
	}
	
	private static String detectFromPath(final String pattern) {
		String nodeLocation = null;
		String path = getPath();
		String spliter = getPathSpliter();
		if (path != null) {
			String[] pathElements = path.split(spliter);
			for (String element : pathElements) {
				if (element.contains(pattern)) {
					nodeLocation = element;
					break;
				}
			}
		}
		return nodeLocation;
	}
	
	private static String getPath() {
		return System.getenv(PATH);
	}
	
	private static String getPathSpliter() {
		return (PlatformUtil.isWindows()) ? ";" : ":"; //$NON-NLS-1$ //$NON-NLS-2$
	}

}
