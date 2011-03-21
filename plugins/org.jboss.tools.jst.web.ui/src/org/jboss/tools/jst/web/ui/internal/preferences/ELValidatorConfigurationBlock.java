/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 

package org.jboss.tools.jst.web.ui.internal.preferences;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.internal.ui.preferences.ScrolledPageContent;
import org.eclipse.jdt.internal.ui.wizards.IStatusChangeListener;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.jboss.tools.common.preferences.SeverityPreferences;
import org.jboss.tools.common.ui.preferences.SeverityConfigurationBlock;
import org.jboss.tools.jst.web.kb.WebKbPlugin;
import org.jboss.tools.jst.web.kb.preferences.ELSeverityPreferences;

/**
 * Find the instruction to Framework for Severity preferences in SeverityConfigurationBlock.java
 * 
 * @author Viacheslav Kabanovich
 */
public class ELValidatorConfigurationBlock extends SeverityConfigurationBlock {
	private static final String SETTINGS_SECTION_NAME = ELSeverityPreferencesMessages.JSF_VALIDATOR_CONFIGURATION_BLOCK_JSF_VALIDATOR_CONFIGURATION_BLOCK;

	private Button recognizeVarsCheckBox;
	private Button revalidateUnresolvedElCheckBox;
	private Combo elVariablesCombo;
	private Combo elPropertiesCombo;

	private static SectionDescription SECTION_EL = new SectionDescription(
			ELSeverityPreferencesMessages.JSFValidatorConfigurationBlock_section_el,
		new String[][] {
			{ELSeverityPreferences.EL_SYNTAX_ERROR, ELSeverityPreferencesMessages.JSFValidatorConfigurationBlock_pb_elSyntaxError_label},
			{ELSeverityPreferences.UNKNOWN_EL_VARIABLE_NAME, ELSeverityPreferencesMessages.JSFValidatorConfigurationBlock_pb_unknownElVariableName_label},
			{ELSeverityPreferences.UNKNOWN_EL_VARIABLE_PROPERTY_NAME, ELSeverityPreferencesMessages.JSFValidatorConfigurationBlock_pb_unknownElVariablePropertyName_label},
			{ELSeverityPreferences.UNPAIRED_GETTER_OR_SETTER, ELSeverityPreferencesMessages.JSFValidatorConfigurationBlock_pb_unpairedGetterOrSetter_label},
			{ELSeverityPreferences.NON_EXTERNALIZED_STRINGS, ELSeverityPreferencesMessages.JSFValidatorConfigurationBlock_pb_non_externalizedStrings_label}
		},
		WebKbPlugin.PLUGIN_ID
	);

	private static SectionDescription[] ALL_SECTIONS = new SectionDescription[]{
		SECTION_EL,
	};

	private static Key[] getKeys() {
		ArrayList<Key> keys = new ArrayList<Key>();
		for (int i = 0; i < ALL_SECTIONS.length; i++) {
			for (int j = 0; j < ALL_SECTIONS[i].options.length; j++) {
				keys.add(ALL_SECTIONS[i].options[j].key);
			}
		}
		keys.add(getKey(WebKbPlugin.PLUGIN_ID, ELSeverityPreferences.CHECK_VARS));
		keys.add(getKey(WebKbPlugin.PLUGIN_ID, ELSeverityPreferences.RE_VALIDATE_UNRESOLVED_EL));
		keys.add(MAX_NUMBER_OF_PROBLEMS_KEY);
		return keys.toArray(new Key[0]);
	}

	private static final Key MAX_NUMBER_OF_PROBLEMS_KEY = getKey(WebKbPlugin.PLUGIN_ID, SeverityPreferences.MAX_NUMBER_OF_MARKERS_PREFERENCE_NAME);

	@Override
	protected Key getMaxNumberOfProblemsKey() {
		return MAX_NUMBER_OF_PROBLEMS_KEY;
	}

	public ELValidatorConfigurationBlock(IStatusChangeListener context,
			IProject project,
			IWorkbenchPreferenceContainer container) {
		super(context, project, getKeys(), container);
	}

	@Override
	protected Composite createStyleTabContent(Composite folder) {
		int nColumns = 3;

		final ScrolledPageContent sc1 = new ScrolledPageContent(folder);

		Composite composite = sc1.getBody();

		addMaxNumberOfMarkersField(composite);

		GridLayout layout= new GridLayout(nColumns, false);
		layout.marginHeight= 0;
		layout.marginWidth= 0;
		composite.setLayout(layout);

		Label description= new Label(composite, SWT.LEFT | SWT.WRAP);
		description.setFont(description.getFont());
		description.setText(getCommonDescription()); 
		description.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false, nColumns - 1, 1));

		int defaultIndent = 0;

		for (int i = 0; i < ALL_SECTIONS.length; i++) {
			SectionDescription section = ALL_SECTIONS[i];
			String label = section.label; 
			ExpandableComposite excomposite = createStyleSection(composite, label, nColumns);

			Composite inner = new Composite(excomposite, SWT.NONE);
			inner.setFont(composite.getFont());
			inner.setLayout(new GridLayout(nColumns, false));
			excomposite.setClient(inner);

			for (int j = 0; j < section.options.length; j++) {
				OptionDescription option = section.options[j];
				label = option.label;
				Combo combo = addComboBox(inner, label, option.key, errorWarningIgnore, errorWarningIgnoreLabels, defaultIndent);
				if(option.label == ELSeverityPreferencesMessages.JSFValidatorConfigurationBlock_pb_unknownElVariableName_label) {
					elVariablesCombo = combo;
					combo.addSelectionListener(new SelectionListener(){
						public void widgetDefaultSelected(SelectionEvent e) {
							updateELCombox();
						}
						public void widgetSelected(SelectionEvent e) {
							updateELCombox();
						}
					});
				} else if(option.label == ELSeverityPreferencesMessages.JSFValidatorConfigurationBlock_pb_unknownElVariablePropertyName_label) {
					elPropertiesCombo = combo;
					combo.addSelectionListener(new SelectionListener(){
						public void widgetDefaultSelected(SelectionEvent e) {
							updateELCombox();
						}
						public void widgetSelected(SelectionEvent e) {
							updateELCombox();
						}
					});
				}
			}

			if(section==SECTION_EL) {
				label = ELSeverityPreferencesMessages.JSFValidatorConfigurationBlock_pb_checkVars_label; 
				recognizeVarsCheckBox = addCheckBox(inner, label, getKey(WebKbPlugin.PLUGIN_ID, ELSeverityPreferences.CHECK_VARS), enableDisableValues, defaultIndent);

				label = ELSeverityPreferencesMessages.JSFValidatorConfigurationBlock_pb_revalidateUnresolvedEl_label; 
				revalidateUnresolvedElCheckBox = addCheckBox(inner, label, getKey(WebKbPlugin.PLUGIN_ID, ELSeverityPreferences.RE_VALIDATE_UNRESOLVED_EL), enableDisableValues, defaultIndent);
			}
		}

		restoreSectionExpansionStates(getDialogSettings());

		updateELCombox();

		return sc1;
	}

	@Override
	public void performDefaults() {
		super.performDefaults();
		updateELCombox();
	}

	private void updateELCombox() {
		boolean enable = elPropertiesCombo.getSelectionIndex()!=2 || elVariablesCombo.getSelectionIndex()!=2;
		recognizeVarsCheckBox.setEnabled(enable);
		revalidateUnresolvedElCheckBox.setEnabled(enable);
	}

	@Override
	protected SectionDescription[] getAllSections() {
		return ALL_SECTIONS;
	}

	@Override
	protected String getCommonDescription() {
		return ELSeverityPreferencesMessages.JSFValidatorConfigurationBlock_common_description;
	}

	@Override
	protected IDialogSettings getDialogSettings() {
		return WebKbPlugin.getDefault().getDialogSettings().getSection(SETTINGS_SECTION_NAME);
	}
}