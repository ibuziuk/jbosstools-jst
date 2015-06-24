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
package org.jboss.tools.jst.js.bower.internal.launch.ui;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.internal.ui.SWTFactory;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.jboss.tools.jst.js.bower.internal.BowerConstants;
import org.jboss.tools.jst.js.bower.internal.launch.BowerLaunchConstants;
import org.jboss.tools.jst.js.internal.Activator;

/**
 * @author "Ilya Buziuk (ibuziuk)"
 */
@SuppressWarnings("restriction")
public class BowerLaunchConfigurationTab extends AbstractLaunchConfigurationTab {
	private Text dirText;
	private Text nameText;
	private Text versionText;
	private Text licenseText;
	private Button useDefaultCheckBox;
	
	private String defaultName;
	private String defaultVersion;
	private String defaultLicense;

	@Override
	public void createControl(Composite parent) {
		Composite mainComposite = new Composite(parent, SWT.NONE);
		setControl(mainComposite);
		GridLayout layout = new GridLayout();
		layout.numColumns = 5;
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		mainComposite.setLayout(layout);
		mainComposite.setLayoutData(gridData);
		mainComposite.setFont(parent.getFont());

		
		
		Listener modyfyingListener = new Listener();

		Label label = new Label(mainComposite, SWT.NONE);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		label.setText("Base directoty:"); //$NON-NLS-1$

		this.dirText = new Text(mainComposite, SWT.BORDER);
		this.dirText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 5, 1));
		this.dirText.addModifyListener(modyfyingListener);

		final Composite pomDirButtonsComposite = new Composite(mainComposite, SWT.NONE);
		pomDirButtonsComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 5, 1));
		final GridLayout pomDirButtonsGridLayout = new GridLayout();
		pomDirButtonsGridLayout.marginWidth = 0;
		pomDirButtonsGridLayout.marginHeight = 0;
		pomDirButtonsGridLayout.numColumns = 1;
		pomDirButtonsComposite.setLayout(pomDirButtonsGridLayout);

		final Button browseWorkspaceButton = new Button(pomDirButtonsComposite, SWT.NONE);
		browseWorkspaceButton.setText("Browse Workspace..."); //$NON-NLS-1$
		browseWorkspaceButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ContainerSelectionDialog dialog = new ContainerSelectionDialog(getShell(), //
						ResourcesPlugin.getWorkspace().getRoot(), false, "Folder Selection"); // $NON-NLS-1$ //$NON-NLS-1$
				dialog.showClosedProjects(false);

				int buttonId = dialog.open();
				if (buttonId == IDialogConstants.OK_ID) {
					Object[] resource = dialog.getResult();
					if (resource != null && resource.length > 0) {
						IPath path = ((IPath) resource[0]);
						IResource selectedResource = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
						if (selectedResource.exists()) {
							dirText.setText(selectedResource.getRawLocation().toOSString());
							dirText.setText(selectedResource.getRawLocation().toOSString());							
						}
						entriesChanged();
					}
				}
			}
		});
		
		Group group = SWTFactory.createGroup(mainComposite, "Properties:", 2, 1, GridData.FILL_HORIZONTAL); //$NON-NLS-1$
		useDefaultCheckBox = SWTFactory.createCheckButton(group, "Use default", null, true, 2); //$NON-NLS-1$
		
		Label nameLabel = new Label(group, SWT.NONE);
		GridData gd_namesLabel = new GridData();
		nameLabel.setLayoutData(gd_namesLabel);
		nameLabel.setText("Name:"); //$NON-NLS-1$
		
		nameText = new Text(group, SWT.BORDER);
		nameText.setData("name", "nameText"); //$NON-NLS-1$ //$NON-NLS-2$
		GridData gd_goalsText = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		nameText.setLayoutData(gd_goalsText);
		nameText.addModifyListener(modyfyingListener);
		
	    Label versionLabel = new Label(group, SWT.NONE);
	    versionLabel.setText("Version:"); //$NON-NLS-1$

	    versionText = new Text(group, SWT.BORDER);
	    versionText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
	    versionText.addModifyListener(modyfyingListener);
	    
	    Label licenseLabel = new Label(group, SWT.NONE);
	    licenseLabel.setText("License:"); //$NON-NLS-1$
	    licenseText = new Text(group, SWT.BORDER);
	    licenseText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
	    licenseText.addModifyListener(modyfyingListener);
	
	    useDefaultCheckBox.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean useDefault = ((Button) e.widget).getSelection();
				nameText.setEnabled(!useDefault);
				versionText.setEnabled(!useDefault);
				licenseText.setEnabled(!useDefault);
				if (useDefault) {
					setDefaults();
				} else {
					nameText.setEnabled(true);
					versionText.setEnabled(true);
					licenseText.setEnabled(true);
				}
				entriesChanged();
			}
		});
		
	}
	


	@Override
	public String getName() {
		return "Main"; //$NON-NLS-1$
	}
	
	@Override
	public boolean isValid(ILaunchConfiguration launchConfig) {
		// TODO Auto-generated method stub
		return super.isValid(launchConfig);
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			this.defaultName = configuration.getAttribute(BowerLaunchConstants.ATTR_BOWER_NAME, BowerConstants.DEFAULT_NAME);
			this.defaultVersion = configuration.getAttribute(BowerLaunchConstants.ATTR_BOWER_VERSION, BowerConstants.DEFAULT_VERSION); 
			this.defaultLicense = configuration.getAttribute(BowerLaunchConstants.ATTR_BOWER_LICENSE, BowerConstants.DEFAULT_LICENSE);
			
			this.dirText.setText(configuration.getAttribute(BowerLaunchConstants.ATTR_BOWER_DIR, "")); //$NON-NLS-1$
			this.nameText.setText(defaultName);
			this.versionText.setText(defaultVersion);
			this.licenseText.setText(defaultLicense);

			boolean useDefault = useDefaultCheckBox.isEnabled();

			this.nameText.setEnabled(!useDefault);
			this.versionText.setEnabled(!useDefault);
			this.licenseText.setEnabled(!useDefault);

			setDirty(false);

		} catch (CoreException e) {
			Activator.logError(e);
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(BowerLaunchConstants.ATTR_BOWER_DIR, dirText.getText());
		configuration.setAttribute(BowerLaunchConstants.ATTR_BOWER_NAME, nameText.getText());
		configuration.setAttribute(BowerLaunchConstants.ATTR_BOWER_VERSION, versionText.getText());
		configuration.setAttribute(BowerLaunchConstants.ATTR_BOWER_LICENSE, licenseText.getText());	
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy workingCopy) {
	}
	
	private void setDefaults() {
		nameText.setText(defaultName);
		versionText.setText(defaultVersion);
		licenseText.setText(defaultLicense);
	}
	
	private class Listener implements ModifyListener, SelectionListener {
		public void modifyText(ModifyEvent e) {
			entriesChanged();
		}

		public void widgetDefaultSelected(SelectionEvent e) {
			entriesChanged();
		}

		public void widgetSelected(SelectionEvent e) {
			entriesChanged();
		}
	}

	private void entriesChanged() {
		setDirty(true);
		updateLaunchConfigurationDialog();
	}
	
	
}
