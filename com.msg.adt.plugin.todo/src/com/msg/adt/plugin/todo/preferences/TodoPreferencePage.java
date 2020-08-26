package com.msg.adt.plugin.todo.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import com.msg.adt.plugin.todo.Activator;
import com.msg.adt.plugin.todo.custom.elements.SpacerFieldEditor;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */

public class TodoPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public TodoPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("General settings for TODO Plugin");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common GUI
	 * blocks needed to manipulate various types of preferences. Each field editor
	 * knows how to save and restore itself.
	 */
	public void createFieldEditors() {

		addField(new SpacerFieldEditor(getFieldEditorParent()));

		Composite parent = getFieldEditorParent();

		Group searchParameters = new Group(parent, SWT.CENTER);
		searchParameters.setText("Scan options");
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		searchParameters.setLayout(gridLayout);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		searchParameters.setLayoutData(gridData);

		BooleanFieldEditor checkTodo = new BooleanFieldEditor(PreferenceConstants.CHECK_TODO, "TODOs",
				searchParameters);
		addField(checkTodo);

		BooleanFieldEditor checkFixme = new BooleanFieldEditor(PreferenceConstants.CHECK_FIXME, "FIXMEs",
				searchParameters);
		addField(checkFixme);

		BooleanFieldEditor checkXxx = new BooleanFieldEditor(PreferenceConstants.CHECK_XXX, "XXXs", searchParameters);
		addField(checkXxx);

		StringFieldEditor customText = new StringFieldEditor(PreferenceConstants.CUSTOM_TEXT, "Custom Text:", 40,
				searchParameters);
		addField(customText);

		addField(new SpacerFieldEditor(getFieldEditorParent()));

		BooleanFieldEditor deepScan = new BooleanFieldEditor(PreferenceConstants.DEEP_SCAN, "Deep package scan",
				parent);
		addField(deepScan);

		BooleanFieldEditor createdByMe = new BooleanFieldEditor(PreferenceConstants.SCAN_CREATED_BY_ME,
				"Only objects created by me", parent);
		addField(createdByMe);

		/*
		 * BooleanFieldEditor editedByMe = new
		 * BooleanFieldEditor(PreferenceConstants.SCAN_EDITED_BY_ME,
		 * "Only objects edited by me", parent); addField(editedByMe);
		 */
		
		BooleanFieldEditor cleanBeforeScan = new BooleanFieldEditor(PreferenceConstants.CLEAN_EXISTING_MARKERS,
				"Clean previously created markers before scanning", parent);
		addField(cleanBeforeScan);
		
		BooleanFieldEditor scanSourceCode = new BooleanFieldEditor(PreferenceConstants.SCAN_SOURCE_CODE,
				"Scan the source code for custom text", parent);
		addField(scanSourceCode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

}