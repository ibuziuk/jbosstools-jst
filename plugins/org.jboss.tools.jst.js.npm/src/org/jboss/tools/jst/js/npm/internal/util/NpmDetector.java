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
package org.jboss.tools.jst.js.npm.internal.util;

import java.io.File;
import org.jboss.tools.common.util.PlatformUtil;
import static org.jboss.tools.jst.js.npm.internal.NpmConstants.*;

/**
 * @author Ilya Buziuk (ibuziuk)
 */
public final class NpmDetector {
	private static final String USR_LOCAL_BIN = "/usr/local/bin"; //$NON-NLS-1$
	private static final String USR_BIN = "/usr/bin"; //$NON-NLS-1$

	private NpmDetector() {
	}

	public static String detectNpm() {
		String bowerLocation = null;
		if (PlatformUtil.isWindows()) {
			String npmLocation = detectNpmFromPath();
			if (npmLocation != null) {
				String separator = File.separator;
				if (!npmLocation.endsWith(separator)) {
					npmLocation = npmLocation + separator;
				}

				File npmHome = new File(npmLocation, NODE_MODULES + separator + NPM + separator + BIN);
				if (npmHome != null && npmHome.exists()) {
					bowerLocation = npmHome.getAbsolutePath();
				}
			}
		} else {
			// Try to detect in "usr/local/bin" and "usr/bin" for Mac & Linux
			File usrLocalBin = new File(USR_LOCAL_BIN);
			if (isDetected(usrLocalBin, NPM_CLI_JS)) {
				bowerLocation = usrLocalBin.getAbsolutePath();
			} else {
				File usrBin = new File(USR_BIN);
				if (isDetected(usrBin, NPM_CLI_JS)) {
					bowerLocation = usrBin.getAbsolutePath();
				}
			}
		}
		return bowerLocation;
	}
	
	private static boolean isDetected(File parent, String name) {
		if (parent != null && parent.exists()) {
			File file = new File(parent, name);
			if (file != null && file.exists()) {
				return true;
			}
		}
		return false;
	}
	
	private static String detectNpmFromPath() {
		return detectFromPath(File.separator + NPM);
	}
		
	private static String detectFromPath(final String pattern) {
		String location = null;
		String path = getPath();
		String spliter = getPathSpliter();
		if (path != null) {
			String[] pathElements = path.split(spliter);
			for (String element : pathElements) {
				if (element.contains(pattern)) {
					location = element;
					break;
				}
			}
		}
		return location;
	}
	
	private static String getPath() {
		return System.getenv(PATH);
	}
	
	private static String getPathSpliter() {
		return (PlatformUtil.isWindows()) ? ";" : ":"; //$NON-NLS-1$ //$NON-NLS-2$
	}

}
