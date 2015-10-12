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
package org.jboss.tools.jst.js.npm.internal.launch.shortcut;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ide.ResourceUtil;
import org.jboss.tools.jst.js.npm.NpmCommands;
import org.jboss.tools.jst.js.npm.NpmPlugin;
import org.jboss.tools.jst.js.npm.internal.NpmConstants;
import org.jboss.tools.jst.js.npm.internal.ui.NpmExceptionNotifier;
import org.jboss.tools.jst.js.npm.util.NpmUtil;
import org.jboss.tools.jst.js.node.exception.NodeExceptionNotifier;
import org.jboss.tools.jst.js.node.launch.shortcut.GenericNativeNodeLaunch;
import org.jboss.tools.jst.js.node.util.NodeExternalUtil;

/**
 * @author "Ilya Buziuk (ibuziuk)"
 */
public class NpmUpdate extends GenericNativeNodeLaunch {
	private static final String LAUNCH_NAME = "Bower Update"; //$NON-NLS-1$
	
	@Override
	public void launch(ISelection selection, String mode) {
		if (selection instanceof IStructuredSelection) {
			 Object element = ((IStructuredSelection)selection).getFirstElement();
			 if (element != null && element instanceof IResource) {
				try {
					IResource selectedResource = (IResource) element;
					launchBower(selectedResource);
				} catch (CoreException e) {
					NpmPlugin.logError(e);
				}
			 }
		}
	}
	
	@Override
	public void launch(IEditorPart editor, String mode) {
		IEditorInput editorInput = editor.getEditorInput();
		IFile file = ResourceUtil.getFile(editorInput);
		if (file != null && file.exists() && NpmConstants.PACKAGE_JSON.equals(file.getName())) {
			try {
				launchBower(file);
			} catch (CoreException e) {
				NpmPlugin.logError(e);
			}
		}
	}

	@Override
	protected String getCommandName() {
		return NpmCommands.UPDATE.getValue();
	}

	@Override
	protected String getLaunchName() {
		return LAUNCH_NAME;
	}

	@Override
	protected String getWorkingDirectory(IResource resource) throws CoreException {
		String workingDir = null;
		if (resource != null && resource.exists()) {
			if (resource.getType() == IResource.FILE && NpmConstants.PACKAGE_JSON.equals(resource.getName())) {
				workingDir = resource.getParent().getFullPath().toOSString();
			} else if (resource.getType() == IResource.FOLDER) {
				workingDir = resource.getFullPath().toOSString();
			} else if (resource.getType() == IResource.PROJECT) {
				IProject project = (IProject) resource;
				IFile file = project.getFile(NpmConstants.PACKAGE_JSON);
				if (file.exists()) {
					workingDir = resource.getFullPath().toOSString();
				} else {
					try {
						workingDir = getWorkingDirectory(project);
					} catch (IOException e) {
						NpmPlugin.logError(e);
					}
				}
			}
		}
		return workingDir;
	}
	
	private void launchBower(IResource resource) throws CoreException {
		String nodeLocation = NodeExternalUtil.getNodeExecutableLocation();
		String bowerLocation = NpmUtil.getBowerExecutableLocation();
		if (nodeLocation == null || nodeLocation.isEmpty()) {
			NodeExceptionNotifier.nodeLocationNotDefined();
		} else if (bowerLocation == null || bowerLocation.isEmpty()) {
			NpmExceptionNotifier.bowerLocationNotDefined();
		} else {
			this.setWorkingProject(resource.getProject());
			launchNodeTool(getWorkingDirectory(resource), nodeLocation, bowerLocation);
		}
	}

	/**
	 * Detects working directory for bower execution depending on .bowerrc file
	 * @throws CoreException 
	 * @throws UnsupportedEncodingException
	 * @see <a href="http://bower.io/docs/config/">Bower Configuration</a>
	 */
	private String getWorkingDirectory(IProject project) throws CoreException, UnsupportedEncodingException {
		String workingDir = null;
		IFile bowerrc = NpmUtil.getBowerrc(project);
		if (bowerrc != null) {
			IContainer parent = bowerrc.getParent();
			if (parent.exists() && parent.findMember(NpmConstants.PACKAGE_JSON) != null) {
				workingDir = parent.getFullPath().toOSString();
			} else {
				String directoryName = NpmUtil.getDirectoryName(bowerrc);
				directoryName = (directoryName != null) ? directoryName : NpmConstants.BOWER_COMPONENTS;
				workingDir = NpmUtil.getBowerWorkingDir(project, directoryName);
			}
		} else {
			// Trying to find bower.json file ignoring "bower_components"
			// (default components directory)
			workingDir = NpmUtil.getBowerWorkingDir(project, NpmConstants.BOWER_COMPONENTS);
		}
		return workingDir;
	}
	
}
