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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.internal.ui.SWTFactory;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
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
	
	private Table authorsTable;
	private Button addAuthorButton;
	private Button removeAuthorButton;
	private Button editAuthorButton;
	
	private String defaultName;
	private String defaultVersion;
	private String defaultLicense;
	private String[] defaultAuthorsArray;
	private String[] defaultIgnoreArray;

	@Override
	public void createControl(Composite parent) {
		Composite mainComposite = SWTFactory.createComposite(parent, parent.getFont(), 1, 1, GridData.FILL_BOTH);
		((GridLayout) mainComposite.getLayout()).verticalSpacing = 4;
		
		createExecutionDirEditor(mainComposite);
		createUseDefaultsEditor(mainComposite);
		createBasePropertyEditor(mainComposite);
		createAuthorsEditor(mainComposite);
	
		setControl(mainComposite);
	}
	
	private void createExecutionDirEditor(Composite mainComposite) {
		Label label = new Label(mainComposite, SWT.NONE);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		label.setText("Base directoty:"); //$NON-NLS-1$

		this.dirText = new Text(mainComposite, SWT.BORDER);
		this.dirText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 5, 1));
		this.dirText.addModifyListener(new EntriesChangedListener());

		final Composite buttonComposite = new Composite(mainComposite, SWT.NONE);
		buttonComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 5, 1));
		final GridLayout buttonGridLayout = new GridLayout();
		buttonGridLayout.marginWidth = 0;
		buttonGridLayout.marginHeight = 0;
		buttonGridLayout.numColumns = 1;
		buttonComposite.setLayout(buttonGridLayout);

		final Button browseWorkspaceButton = new Button(buttonComposite, SWT.NONE);
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
						}
						entriesChanged();
					}
				}
			}
		});

	}
	
	private void createUseDefaultsEditor(Composite mainComposite) {
		Composite group = SWTFactory.createComposite(mainComposite, 2, 1, GridData.FILL_HORIZONTAL);
		useDefaultCheckBox = SWTFactory.createCheckButton(group, "Use default configuration", null, true, 2); //$NON-NLS-1$

		useDefaultCheckBox.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean useDefault = ((Button) e.widget).getSelection();
				nameText.setEnabled(!useDefault);
				versionText.setEnabled(!useDefault);
				licenseText.setEnabled(!useDefault);
				authorsTable.setEnabled(!useDefault);
				addAuthorButton.setEnabled(!useDefault);
				if (useDefault) {
					setDefaults();
				} else {
					nameText.setEnabled(true);
					versionText.setEnabled(true);
					licenseText.setEnabled(true);
					authorsTable.setEnabled(true);
					addAuthorButton.setEnabled(true);
				}
				entriesChanged();
			}
		});
	}
	
	private void createBasePropertyEditor(Composite mainComposite) {
		Group group = SWTFactory.createGroup(mainComposite, "Properties:", 2, 1, GridData.FILL_HORIZONTAL); //$NON-NLS-1$
		
		Label nameLabel = new Label(group, SWT.NONE);
		nameLabel.setText("Name:"); //$NON-NLS-1$
		nameText = new Text(group, SWT.BORDER);
		nameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		nameText.addModifyListener(new EntriesChangedListener());

		Label versionLabel = new Label(group, SWT.NONE);
		versionLabel.setText("Version:"); //$NON-NLS-1$
		versionText = new Text(group, SWT.BORDER);
		versionText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		versionText.addModifyListener(new EntriesChangedListener());

		Label licenseLabel = new Label(group, SWT.NONE);
		licenseLabel.setText("License:"); //$NON-NLS-1$
		licenseText = new Text(group, SWT.BORDER);
		licenseText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		licenseText.addModifyListener(new EntriesChangedListener());
	}
	
	private void createAuthorsEditor(Composite mainComposite) {
		Composite tableGroup = SWTFactory.createGroup(mainComposite, "Authors:", 2, 1, GridData.FILL_HORIZONTAL); //$NON-NLS-1$
		
		TableViewer tableViewer = new TableViewer(tableGroup, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
	    tableViewer.addDoubleClickListener(new IDoubleClickListener() {
	      public void doubleClick(DoubleClickEvent event) {
	        TableItem[] selection = authorsTable.getSelection();
	        if(selection.length == 1) {
	          editProperty(selection[0].getText(0), selection[0].getText(1));
	        }
	      }
	    });
	    tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

	      public void selectionChanged(SelectionChangedEvent event) {
	        TableItem[] items = authorsTable.getSelection();
	        if(items == null || items.length == 0) {
	          editAuthorButton.setEnabled(false);
	          removeAuthorButton.setEnabled(false);
	        } else if(items.length == 1) {
	          editAuthorButton.setEnabled(true);
	          removeAuthorButton.setEnabled(true);
	        } else {
	          editAuthorButton.setEnabled(false);
	          removeAuthorButton.setEnabled(true);
	        }
	      }

	    });

	    this.authorsTable = tableViewer.getTable();
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.heightHint = 50;
		this.authorsTable.setLayoutData(data);
	    this.authorsTable.setLinesVisible(true);
	    this.authorsTable.setHeaderVisible(true);

	    final TableColumn propColumn = new TableColumn(this.authorsTable, SWT.NONE, 0);
	    propColumn.setWidth(200);

		Composite buttonComposite = new Composite(tableGroup, SWT.NONE);
		FillLayout fillLayout = new FillLayout(SWT.VERTICAL);
		fillLayout.spacing = 2;
		buttonComposite.setLayout(fillLayout);
	    
	    addAuthorButton = new Button(buttonComposite, SWT.NONE);
	    addAuthorButton.setText("Add..."); //$NON-NLS-1$
	    addAuthorButton.addSelectionListener(new SelectionAdapter() {
	      public void widgetSelected(SelectionEvent e) {
	        addProperty();
	      }
	    });
	    editAuthorButton = new Button(buttonComposite, SWT.NONE);
	    editAuthorButton.setText("Edit..."); //$NON-NLS-1$
	    editAuthorButton.addSelectionListener(new SelectionAdapter() {
	      public void widgetSelected(SelectionEvent e) {
	        if(authorsTable.getSelectionCount() > 0) {
	          TableItem[] selection = authorsTable.getSelection();
	          if(selection.length == 1) {
	            editProperty(selection[0].getText(0), selection[0].getText(1));
	          }
	        }
	      }
	    });
	    editAuthorButton.setEnabled(false);
	    removeAuthorButton = new Button(buttonComposite, SWT.NONE);
	    removeAuthorButton.setText("Remove"); //$NON-NLS-1$
	    removeAuthorButton.addSelectionListener(new SelectionAdapter() {
	      public void widgetSelected(SelectionEvent e) {
	        if(authorsTable.getSelectionCount() > 0) {
	          authorsTable.remove(authorsTable.getSelectionIndices());
	          entriesChanged();
	        }
	      }
	    });
	    removeAuthorButton.setEnabled(false);
	}
	
	void addProperty() {
		PopUpPropertyDialog dialog = new PopUpPropertyDialog(getShell(), "Add Author", "Author:", "", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		if (dialog.open() == IDialogConstants.OK_ID) {
			TableItem item = new TableItem(authorsTable, SWT.NONE);
			item.setText(0, dialog.getName());
			entriesChanged();
		}
	}

	void editProperty(String name, String value) {
		PopUpPropertyDialog dialog = new PopUpPropertyDialog(getShell(), "Edit Author", "Author:", name, null); //$NON-NLS-1$ //$NON-NLS-2$
		if (dialog.open() == IDialogConstants.OK_ID) {
			TableItem[] item = authorsTable.getSelection();
			item[0].setText(0, dialog.getName());
			entriesChanged();
		}
	}
	
	@Override
	protected Shell getShell() {
		return super.getShell();
	}

	@Override
	public String getName() {
		return "Main"; //$NON-NLS-1$
	}
	
	@Override
	public boolean isValid(ILaunchConfiguration launchConfig) {
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
			
			// Authors
			TableItem item = new TableItem(authorsTable, SWT.NONE);
			item.setText(0, BowerConstants.DEFAULT_AUTHORS[0]);
			
			// Ignore
			boolean useDefault = useDefaultCheckBox.getSelection();
			
			this.nameText.setEnabled(!useDefault);
			this.versionText.setEnabled(!useDefault);
			this.licenseText.setEnabled(!useDefault);
			this.authorsTable.setEnabled(!useDefault);
			this.addAuthorButton.setEnabled(!useDefault);

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
		configuration.setAttribute(BowerLaunchConstants.ATTR_BOWER_AUTHORS, getAuthors());
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy workingCopy) {
	}
	
	private List<String> getAuthors() {
		List<String> authors = new ArrayList<>();
		TableItem[] items = authorsTable.getItems();
		if (items != null && items.length > 0) {
			for (TableItem item : items) {
				authors.add(item.getText());
			}
		}
		return authors;
	}
	

	private void setDefaults() {
		nameText.setText(defaultName);
		versionText.setText(defaultVersion);
		licenseText.setText(defaultLicense);
	}
	
	private class EntriesChangedListener implements ModifyListener, SelectionListener {
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
