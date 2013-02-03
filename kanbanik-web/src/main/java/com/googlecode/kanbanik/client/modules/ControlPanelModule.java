package com.googlecode.kanbanik.client.modules;


import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.messages.modules.ModuleActivatedMessage;
import com.googlecode.kanbanik.client.messaging.messages.modules.ModuleDeactivatedMessage;
import com.googlecode.kanbanik.client.modules.KanbanikModule.ModuleInitializeCallback;

public class ControlPanelModule extends TabPanel implements SelectionHandler<Integer> {
	
	private static final BoardsModule boardsModule = new BoardsModule();
	
	private static final ConfigureWorkflowModule configureWorkflowModule = new ConfigureWorkflowModule();
	
	private static final SecurityModule securityModule = new SecurityModule();

	private SimplePanel boardsContent = new SimplePanel();
	
	private SimplePanel configureWorkflowContent = new SimplePanel();
	
	private SimplePanel securityContent = new SimplePanel();
	
	public ControlPanelModule() {
		
		add(boardsContent, "Boards");
		add(configureWorkflowContent, "Configure Workflow");
		add(securityContent, "Security");
		
		addSelectionHandler(this);
		selectTab(0);
		setWidth("100%");
		
		setStyleName("control-panel-style");
	}

	class BoardsRefreshed implements ModuleInitializeCallback {

		public void initialized(Widget widget) {
			boardsContent.clear();
			boardsContent.add(widget);
			MessageBus.sendMessage(new ModuleDeactivatedMessage(ConfigureWorkflowModule.class, this));
			MessageBus.sendMessage(new ModuleActivatedMessage(BoardsModule.class, this));
		}
	}
	
	class ConfigureWorkflowRefreshed implements ModuleInitializeCallback {

		public void initialized(Widget widget) {
			configureWorkflowContent.clear();
			configureWorkflowContent.add(widget);
			MessageBus.sendMessage(new ModuleActivatedMessage(ConfigureWorkflowModule.class, this));
			MessageBus.sendMessage(new ModuleDeactivatedMessage(BoardsModule.class, this));
		}
	}
	
	class SecurityModuleRefreshed implements ModuleInitializeCallback {

		@Override
		public void initialized(Widget widget) {
			securityContent.clear();
			securityContent.add(widget);
			
			MessageBus.sendMessage(new ModuleDeactivatedMessage(BoardsModule.class, this));
			MessageBus.sendMessage(new ModuleActivatedMessage(BoardsModule.class, this));
			// no events in users so far
		}
		
	}
	
	public void onSelection(SelectionEvent<Integer> event) {
		if (event.getSelectedItem() == 0) {
			boardsModule.initialize(new BoardsRefreshed());
		} else if (event.getSelectedItem() == 1) {
			configureWorkflowModule.initialize(new ConfigureWorkflowRefreshed());
		} else if (event.getSelectedItem() == 2) {
			securityModule.initialize(new SecurityModuleRefreshed());
		}
	}
	
}
