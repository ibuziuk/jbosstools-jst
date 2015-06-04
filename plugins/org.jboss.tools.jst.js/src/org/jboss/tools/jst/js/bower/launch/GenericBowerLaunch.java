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
package org.jboss.tools.jst.js.bower.launch;

import org.eclipse.core.externaltools.internal.IExternalToolConstants;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.jboss.tools.jst.js.bower.internal.ui.ExceptionNotifier;
import org.jboss.tools.jst.js.bower.internal.util.BowerUtil;
import org.jboss.tools.jst.js.internal.Activator;

/**
 * @author "Ilya Buziuk (ibuziuk)"
 */
@SuppressWarnings("restriction")
public abstract class GenericBowerLaunch implements ILaunchShortcut {
	
	@Override
	public void launch(ISelection selection, String mode) {
		if (selection instanceof IStructuredSelection) {
			 Object element = ((IStructuredSelection)selection).getFirstElement();
			 if (element != null && element instanceof IResource) {
				try {
					String nodeLocation = BowerUtil.getNodeExecutableLocation();
					String bowerLocation = BowerUtil.getBowerExecutableLocation();
					if (nodeLocation == null || nodeLocation.isEmpty()) {
						ExceptionNotifier.npmLocationNotDefined(); // TODO: FIX error message
					} else if (bowerLocation == null || bowerLocation.isEmpty()) {
						ExceptionNotifier.bowerNotInstalled(); // TODO: FIX error message
					} else {
						execute(getWorkingDirectory((IResource) element), nodeLocation, bowerLocation);						
					}
				} catch (CoreException e) {
					Activator.logError(e);
				}
			 }
		}
	}

	@Override
	public void launch(IEditorPart editor, String mode) {			
	}
	
	protected void execute(String workingDirectory, String nodeExecutableLocation, String bowerExecutableLocation) {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType programType = manager.getLaunchConfigurationType(IExternalToolConstants.ID_PROGRAM_LAUNCH_CONFIGURATION_TYPE);
		try {
			ILaunchConfiguration cfg = programType.newInstance(null, getLaunchName());
			ILaunchConfigurationWorkingCopy wc = cfg.getWorkingCopy();
			wc.setAttribute(IExternalToolConstants.ATTR_LOCATION, nodeExecutableLocation);
			wc.setAttribute(IExternalToolConstants.ATTR_WORKING_DIRECTORY, "${workspace_loc:" + workingDirectory + "}"); //$NON-NLS-1$ //$NON-NLS-2$
			// Add a good comment here
			wc.setAttribute(IExternalToolConstants.ATTR_TOOL_ARGUMENTS, bowerExecutableLocation + " " + getCommandArguments()); //$NON-NLS-1$
			cfg = wc.doSave();
			cfg.launch(ILaunchManager.RUN_MODE, null, false, true);
			cfg.delete();
		} catch (CoreException e) {
			Activator.logError(e);
			ExceptionNotifier.npmLocationNotDefined();
		}
	}
	
	
	protected abstract String getWorkingDirectory(IResource project) throws CoreException;

	protected abstract String getCommandArguments();

	protected abstract String getLaunchName();
}
