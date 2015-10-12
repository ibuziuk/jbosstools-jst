/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *       Red Hat, Inc. - initial API and implementation
 *******************************************************************************/
package org.jboss.tools.jst.js.npm;

/**
 * npm CLI commands
 *
 * @see <a href="https://docs.npmjs.com/">npm documentation</a>
 * @author "Ilya Buziuk (ibuziuk)"
 */
public enum NpmCommands {
	INIT("init"), //$NON-NLS-1$
	UPDATE("update"); //$NON-NLS-1$

	private final String value;

	private NpmCommands(final String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
