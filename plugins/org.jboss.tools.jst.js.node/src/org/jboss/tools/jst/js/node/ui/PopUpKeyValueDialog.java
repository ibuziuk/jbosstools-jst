package org.jboss.tools.jst.js.node.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class PopUpKeyValueDialog extends Dialog {
	private final String title;
	private final String initialKey;
	private final String initialValue;
	private final VerifyListener verifyListener;
	private String keyLabel;
	private Text keyText;
	private String valueLabel;
	private Text valueText;
	private String name;

	public PopUpKeyValueDialog(Shell shell, String title, String keyLabel, String initialKey, String valueLabel, String initialValue, VerifyListener verifyListener) {
		super(shell);
		this.title = title;
		this.keyLabel = keyLabel;
		this.initialKey = initialKey;
		this.valueLabel = valueLabel;
		this.initialValue = initialValue;
		this.verifyListener = verifyListener;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginTop = 7;
		gridLayout.marginWidth = 12;
		comp.setLayout(gridLayout);
		
		Label key = new Label(comp, SWT.NONE);
		key.setText(keyLabel);
		key.setFont(comp.getFont());
		
		keyText = new Text(comp, SWT.BORDER | SWT.SINGLE);
		GridData gdk = new GridData(GridData.FILL_HORIZONTAL);
		gdk.widthHint = 300;
		keyText.setLayoutData(gdk);
		keyText.setFont(comp.getFont());
		keyText.setText(initialKey == null ? "" : initialKey); //$NON-NLS-1$
		keyText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateButtons();
			}
		});

		Label value = new Label(comp, SWT.NONE);
		value.setText(valueLabel);
		value.setFont(comp.getFont());

		valueText = new Text(comp, SWT.BORDER | SWT.SINGLE);
		GridData gdv = new GridData(GridData.FILL_HORIZONTAL);
		gdv.widthHint = 300;
		valueText.setLayoutData(gdv);
		valueText.setFont(comp.getFont());
		valueText.setText(initialValue == null ? "" : initialValue); //$NON-NLS-1$
		valueText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateButtons();
			}
		});

		return comp;
	}

	public String getName() {
		return this.name;
	}

	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			name = valueText.getText();
		} else {
			name = null;
		}
		super.buttonPressed(buttonId);
	}

	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		if (title != null) {
			shell.setText(title);
		}
	}

	protected void updateButtons() {
		String name = valueText.getText().trim();
		Event e = new Event();
		e.widget = valueText;
		VerifyEvent ev = new VerifyEvent(e);
		ev.doit = true;
		if (verifyListener != null) {
			ev.text = name;
			verifyListener.verifyText(ev);
		}
		getButton(IDialogConstants.OK_ID).setEnabled((name.length() > 0));
	}

	public void create() {
		super.create();
		updateButtons();
	}
}
