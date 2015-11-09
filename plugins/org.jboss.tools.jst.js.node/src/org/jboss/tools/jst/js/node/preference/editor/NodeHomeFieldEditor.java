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
package org.jboss.tools.jst.js.node.preference.editor;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.jst.js.node.Constants;
import org.jboss.tools.jst.js.node.Messages;
import org.jboss.tools.jst.js.node.util.NodeExternalUtil;
import org.jboss.tools.jst.js.node.util.PlatformUtil;

/**
 * @author "Ilya Buziuk (ibuziuk)"
 */
public class NodeHomeFieldEditor extends FileFieldEditor {

	public NodeHomeFieldEditor(String name, String label, Composite composite) {
		super(name, label, composite);
		setEmptyStringAllowed(true);
	}

	@Override
	protected boolean doCheckState() {
		String filename = getTextControl().getText();
		filename = filename.trim();
		if (filename.isEmpty()) {
			this.getPage().setMessage(Messages.NodePreferencePage_NotSpecifiedNodeWarning, IStatus.WARNING);
			return true;
		} else {
			// clear the warning message
			this.getPage().setMessage(null);
		}
		

		if (filename.endsWith(NodeExternalUtil.getNodeExecutableName())) {
			return true;
			
		// JBIDE-20351 Bower tooling doesn't detect node when the binary is called 'nodejs'
		// If "nodejs" is not detected try to detect "node"
		} else if (PlatformUtil.isLinux() && filename.endsWith(Constants.NODE)) {
			return true;
		
		//JBIDE-20988 Preference validation fails on windows if node executable called node64.exe
		} else if (PlatformUtil.isWindows() && filename.endsWith(Constants.NODE_64_EXE)) {
			return true;
			
		} else {
			setErrorMessage(Messages.NodePreferencePage_NotValidNodeError);
			return false;				
		}
	}

	@Override
	public void setValidateStrategy(int value) {
		super.setValidateStrategy(VALIDATE_ON_KEY_STROKE);
	}

}