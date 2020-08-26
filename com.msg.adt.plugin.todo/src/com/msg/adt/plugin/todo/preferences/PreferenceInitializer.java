package com.msg.adt.plugin.todo.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.msg.adt.plugin.todo.Activator;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#
	 * initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.CHECK_TODO, true);
		store.setDefault(PreferenceConstants.CHECK_FIXME, true);
		store.setDefault(PreferenceConstants.CHECK_XXX, true);
		store.setDefault(PreferenceConstants.DEEP_SCAN, true);
		store.setDefault(PreferenceConstants.SCAN_CREATED_BY_ME, false);
		store.setDefault(PreferenceConstants.SCAN_EDITED_BY_ME, false);
	}

}
