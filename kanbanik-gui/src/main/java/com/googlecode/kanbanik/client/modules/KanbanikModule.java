package com.googlecode.kanbanik.client.modules;

import com.google.gwt.user.client.ui.Widget;

public interface KanbanikModule {
	void initialize(ModuleInitializeCallback initializedCallback);
	
	interface ModuleInitializeCallback {
		void initialized(Widget widget);
	}
}
