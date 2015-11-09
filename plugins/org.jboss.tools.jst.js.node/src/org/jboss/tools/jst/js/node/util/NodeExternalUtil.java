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
package org.jboss.tools.jst.js.node.util;

import org.jboss.tools.jst.js.node.Constants;
import org.jboss.tools.jst.js.node.preference.NodePreferenceHolder;

/**
 * @author "Ilya Buziuk (ibuziuk)"
 */
public final class NodeExternalUtil {
	
	public static String getNodeExecutableLocation() {
		// TODO: validate here one more time ?
		return NodePreferenceHolder.getNodeLocation();
	}
	
	public static String getNodeExecutableName() {
		String name = null;
		switch(PlatformUtil.getOs()) {
			case WINDOWS:
				name = Constants.NODE_EXE;	
				break;
				
			case MACOS:
				name = Constants.NODE;
				break;
				
			case LINUX:
				name = Constants.NODE_JS;
				break;
			
			case OTHER:
				name = Constants.NODE;
				break;
		}
		return name;
	}
}
