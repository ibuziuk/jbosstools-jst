/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.jst.js.npm.util;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.jst.js.npm.PackageJson;
import org.jboss.tools.jst.js.npm.Bowerrc;
import org.jboss.tools.jst.js.npm.internal.NpmConstants;
import org.jboss.tools.jst.js.npm.internal.preference.NpmPreferenceHolder;
import org.jboss.tools.jst.js.node.util.WorkbenchResourceUtil;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author "Ilya Buziuk (ibuziuk)"
 */
public final class NpmUtil {

	private NpmUtil() {
	}

	public static boolean isPackageJsonExist(final IProject project) throws CoreException {
		IFile packageJson = null;
		if (project != null && project.exists()) {
			packageJson = WorkbenchResourceUtil.findFileRecursively(project, NpmConstants.PACKAGE_JSON);
		}
		return (packageJson != null && packageJson.exists());
	}

	public static boolean hasPackageJson(final IFolder folder) throws CoreException {
		IResource packageJson = folder.findMember(NpmConstants.PACKAGE_JSON);
		return (packageJson != null && packageJson.exists());
	}

	public static boolean isPackageJson(final IResource resource) {
		return (resource != null && NpmConstants.PACKAGE_JSON.equals(resource.getName()) && resource.exists());
	}

	public static String generateJson(PackageJson packageJson) {
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		return gson.toJson(packageJson);
	}
	
	/**
	 * .bowerrc is an optional file for bower configuration. The "directory"
	 * property defines the path in which installed components should be saved.
	 * If not specified the default "bower_components" folder will be used.
	 * 
	 * @throws CoreException
	 * @see <a href="http://bower.io/docs/config/#bowerrc-specification">.bowerrc specification</a>
	 */
	public static IFile getBowerrc(IProject project) throws CoreException {
		IFile bowerrc = null;
		if (project != null && project.exists()) {
			bowerrc = WorkbenchResourceUtil.findFileRecursively(project, NpmConstants.BOWERRC);
		}
		return bowerrc;
	}
	
	/**
	 * @return absolute path to directory in which native bower call must be performed. Basically, the method scans 
	 * project for bower.json file and returns it's parent, ignoring components directories i.e "bower_components" 
	 * or defined in .bowerrc file  
	 * @throws CoreException
	 */
	public static String getBowerWorkingDir(IProject project, final String... ignores) throws CoreException {
		String workingDir = null;
		final List<IFile> foundFiles = new ArrayList<>();
		if (project != null && project.exists()) {
			project.accept(new IResourceVisitor() {

				@Override
				public boolean visit(IResource resource) throws CoreException {
					if (!foundFiles.isEmpty()) {
						return false;
					} else if (resource.getType() == IResource.FOLDER && ignores != null) {
						for (String ignore : ignores) {
							if (resource.getName().equals(ignore)) {
								return false;
							}
						}
					} else if (resource.getType() == IResource.FILE
							&& NpmConstants.PACKAGE_JSON.equals(resource.getName())) {
						foundFiles.add((IFile) resource);
					}
					return true;
				}
			});
		}
		if (!foundFiles.isEmpty()) {
			workingDir = foundFiles.get(0).getParent().getFullPath().toOSString();
		}
		return workingDir;
	}
	
	/**
	 * @return name of the directory in which bower components would be unpacked according to the .bowerrc
	 * @throws CoreException 
	 * @throws UnsupportedEncodingException 
	 * @see <a href="http://bower.io/docs/config/#directory">Directory property</a> 
	 */
	public static String getDirectoryName(IFile bowerrc) throws UnsupportedEncodingException, CoreException {
		String directoryName = null;
		if (bowerrc != null && bowerrc.exists()) {
			Reader reader = new InputStreamReader(bowerrc.getContents(), NpmConstants.UTF_8);
			Bowerrc model = new Gson().fromJson(reader, Bowerrc.class);
			String directory = model.getDirectory();
			if (directory != null) {
				String[] split = directory.split("/"); //$NON-NLS-1$
				int length = split.length;
				if (length > 0) {
					directoryName = split[length - 1];
				}
			}
		}
		return directoryName;
	}
	
	public static String getNpmExecutableLocation() {
		String npmExecutableLocation = null;
		File npmExecutable = new File(NpmPreferenceHolder.getBowerLocation(), NpmConstants.NPM_CLI_JS);
		if (npmExecutable != null && npmExecutable.exists()) {
			npmExecutableLocation = npmExecutable.getAbsolutePath();
		}
		return npmExecutableLocation;
	}

}