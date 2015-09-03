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

import org.jboss.tools.common.util.PlatformUtil;
import org.jboss.tools.jst.js.bower.internal.BowerConstants;
import org.jboss.tools.jst.js.bower.internal.preference.BowerPreferenceHolder;

/**
 * @author Ilya Buziuk (ibuziuk)
 */
public final class ExternalToolUtil {
	
	public static String getBowerExecutableLocation() {
		String bowerExecutableLocation = null;
		File bowerExecutable = new File(BowerPreferenceHolder.getBowerLocation(), BowerConstants.BOWER);
		if (bowerExecutable != null && bowerExecutable.exists()) {
			bowerExecutableLocation = bowerExecutable.getAbsolutePath();
		}
		return bowerExecutableLocation;
	}
	
	public static String getNodeExecutableLocation() {
		String nodeExecutableLocation = null;
		String nodeExecutableName = getNodeExecutableName();
		File nodeExecutable = new File(BowerPreferenceHolder.getNodeLocation(), nodeExecutableName);
		if (nodeExecutable.exists()) {
			nodeExecutableLocation = nodeExecutable.getAbsolutePath();
		} else if (PlatformUtil.isLinux()) {
			// JBIDE-20351 Bower tooling doesn't detect node when the binary is called 'nodejs'
			// If "nodejs" is not detected try to detect "node"
			nodeExecutableName = BowerConstants.NODE; 
			nodeExecutable = new File(BowerPreferenceHolder.getNodeLocation(), nodeExecutableName);
			if (nodeExecutable.exists()) {
				nodeExecutableLocation = nodeExecutable.getAbsolutePath();
			}				
		}
		return nodeExecutableLocation;
	}
	
	public static String getNodeExecutableName() {
		String name = null;
		switch(PlatformUtil.getOs()) {
			case WINDOWS:
				name = BowerConstants.NODE_EXE;	
				break;
				
			case MACOS:
				name = BowerConstants.NODE;
				break;
				
			case LINUX:
				name = BowerConstants.NODE_JS;
				break;
			
			case OTHER:
				name = BowerConstants.NODE;
				break;
		}
		return name;
	}
	
}
