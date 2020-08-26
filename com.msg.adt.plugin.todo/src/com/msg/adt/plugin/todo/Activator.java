package com.msg.adt.plugin.todo;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractUIPlugin {

	// The plug-in ID
		public static final String PLUGIN_ID = "com.msg.adt.plugin.todo"; //$NON-NLS-1$

		// The shared instance
		private static Activator plugin;
		
		/**
		 * The constructor
		 */
		public Activator() {
		}

		@Override
		public void start(BundleContext context) throws Exception {
			super.start(context);
			plugin = this;
		}

		@Override
		public void stop(BundleContext context) throws Exception {
			plugin = null;
			super.stop(context);
		}

		/**
		 * Returns the shared instance
		 *
		 * @return the shared instance
		 */
		public static Activator getDefault() {
			return plugin;
		}

}
