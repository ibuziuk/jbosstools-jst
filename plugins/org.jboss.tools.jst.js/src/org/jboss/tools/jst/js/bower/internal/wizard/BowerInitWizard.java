package org.jboss.tools.jst.js.bower.internal.wizard;

import org.eclipse.core.internal.resources.Container;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.jboss.tools.jst.js.bower.BowerJson;
import org.jboss.tools.jst.js.bower.internal.BowerConstants;
import org.jboss.tools.jst.js.bower.internal.util.BowerUtil;
import org.jboss.tools.jst.js.internal.Activator;
import org.jboss.tools.jst.js.util.WorkbenchResourceUtil;

@SuppressWarnings("restriction")
public class BowerInitWizard extends Wizard implements INewWizard {
	private IStructuredSelection selection;
	private BowerInitPage bowerInitPage;

	public BowerInitWizard() {
		super();
		setDefaultPageImageDescriptor(Activator.getImageDescriptor("/icons/bower_75.png")); //$NON-NLS-1$
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}
	
	@Override
	public void addPages() {
		super.addPages();
		bowerInitPage = new BowerInitPage(selection);
		addPage(bowerInitPage);
	}

	@Override
	public boolean performFinish() {
		BowerJson bowerJson = bowerInitPage.getModel();
		String dir = bowerInitPage.getExecutionDir();
		try {
			IContainer root = ResourcesPlugin.getWorkspace().getRoot().getContainerForLocation(new Path(dir));
			if (root != null && root.exists()) {
				IFile file = ((Container) root).getFile(BowerConstants.BOWER_JSON);
				if (!file.exists()) {
					String json = BowerUtil.generateJson(bowerJson);
					WorkbenchResourceUtil.createFile(file, json);
					WorkbenchResourceUtil.openInEditor(file, null);
				}
			}
		} catch (CoreException e) {
			Activator.logError(e);
			return false;
		}
		return true;
	}

}
