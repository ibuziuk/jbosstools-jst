/******************************************************************************* 
 * Copyright (c) 2013 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.jst.web.ui.palette.html.jquery.wizard;

import java.beans.PropertyChangeEvent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.common.ui.widget.editor.IFieldEditor;
import org.jboss.tools.jst.web.kb.internal.taglib.html.jq.JQueryMobileVersion;
import org.jboss.tools.jst.web.ui.internal.properties.advanced.LayoutUtil.TwoColumns;
import org.jboss.tools.jst.web.ui.palette.html.wizard.WizardMessages;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class NewTableWizardPage extends NewJQueryWidgetWizardPage {
	ColumnEditor columns = new ColumnEditor(this, 1, 6);

	public NewTableWizardPage() {
		super("newTable", WizardMessages.newTableTitle);
		setDescription(WizardMessages.newTableDescription);
	}

	@Override
	protected void createFieldPanel(Composite parent) {
		IFieldEditor modeEditor = JQueryFieldEditorFactory.createTableModeEditor();
		addEditor(modeEditor, parent);

		createIDEditor(parent, false);

		if(getVersion() != JQueryMobileVersion.JQM_1_3) {
			IFieldEditor searchFilter = JQueryFieldEditorFactory.createSearchFilterEditor();
			addEditor(searchFilter, parent);
		}

		columns.createControl(parent, WizardMessages.columnsLabel);

		TwoColumns columns = createTwoColumns(parent);

		IFieldEditor responsive = JQueryFieldEditorFactory.createResponsiveEditor();
		addEditor(responsive, columns.left());

		IFieldEditor stripes = JQueryFieldEditorFactory.createStripesEditor();
		addEditor(stripes, columns.right());

		IFieldEditor theme = JQueryFieldEditorFactory.createDataThemeEditor(getVersion());
		addEditor(theme, parent, true);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(columns.isSwitching) {
			return;
		}
		String name = evt.getPropertyName();
		String value = evt.getNewValue().toString();
		columns.onPropertyChange(name, value);

		super.propertyChange(evt);
	}

	@Override
	protected int getPreferredBrowser() {
		return isLinux ? super.getPreferredBrowser() : SWT.WEBKIT;
	}

}
