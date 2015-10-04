package com.googlecode.kanbanik.client.modules;


import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.messages.modules.ModuleActivatedMessage;
import com.googlecode.kanbanik.client.messaging.messages.modules.ModuleDeactivatedMessage;
import com.googlecode.kanbanik.client.modules.KanbanikModule.ModuleInitializeCallback;
import com.googlecode.kanbanik.client.security.CurrentUser;

import java.util.ArrayList;
import java.util.List;

public class ControlPanelModule extends TabPanel implements SelectionHandler<Integer> {
	
	private static final BoardsModule boardsModule = new BoardsModule();
	
	private static final ConfigureWorkflowModule configureWorkflowModule = new ConfigureWorkflowModule();
	
	private static final SecurityModule securityModule = new SecurityModule();

	private SimplePanel boardsContent = new SimplePanel();
	
	private SimplePanel configureWorkflowContent = new SimplePanel();
	
	private SimplePanel securityContent = new SimplePanel();

    private Class<?> currentlyActiveModule;

	private int boardsModuleIndex = 0;

    private int configureModuleIndex = -1;

    private int securityModuleIndex = -1;

	public ControlPanelModule() {

        add(boardsContent, "Boards");

        if (CurrentUser.getInstance().canSeeConfigure()) {
            add(configureWorkflowContent, "Configure");
            configureModuleIndex = 1;
        }

		if (CurrentUser.getInstance().canSeeSecurity()) {
            add(securityContent, "Security");
            if (configureModuleIndex == 1) {
                securityModuleIndex = 2;
            } else {
                securityModuleIndex = 1;
            }
        }

		addSelectionHandler(this);
		selectTab(0);
		setWidth("100%");
		
		setStyleName("control-panel-style");
	}

	class BoardsRefreshed implements ModuleInitializeCallback {

		public void initialized(Widget widget) {
			boardsContent.clear();
			boardsContent.add(widget);

			MessageBus.sendMessage(new ModuleActivatedMessage(BoardsModule.class, this));
            currentlyActiveModule = BoardsModule.class;
		}
	}
	
	class ConfigureWorkflowRefreshed implements ModuleInitializeCallback {

		public void initialized(Widget widget) {
			configureWorkflowContent.clear();
			configureWorkflowContent.add(widget);

			MessageBus.sendMessage(new ModuleActivatedMessage(ConfigureWorkflowModule.class, this));
            currentlyActiveModule = ConfigureWorkflowModule.class;
		}
	}
	
	class SecurityModuleRefreshed implements ModuleInitializeCallback {

		@Override
		public void initialized(Widget widget) {
			securityContent.clear();
			securityContent.add(widget);

			MessageBus.sendMessage(new ModuleActivatedMessage(SecurityModule.class, this));
            currentlyActiveModule = SecurityModule.class;
		}
		
	}
	
	public void onSelection(SelectionEvent<Integer> event) {
        if (currentlyActiveModule != null) {
            MessageBus.sendMessage(new ModuleDeactivatedMessage(currentlyActiveModule, this));
        }

		if (event.getSelectedItem() == boardsModuleIndex) {
			boardsModule.initialize(new BoardsRefreshed());
		} else if (event.getSelectedItem() == configureModuleIndex) {
			configureWorkflowModule.initialize(new ConfigureWorkflowRefreshed());
		} else if (event.getSelectedItem() == securityModuleIndex) {
			securityModule.initialize(new SecurityModuleRefreshed());
		}
	}
	
}
