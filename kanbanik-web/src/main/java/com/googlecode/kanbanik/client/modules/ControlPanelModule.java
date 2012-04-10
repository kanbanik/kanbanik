package com.googlecode.kanbanik.client.modules;


import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.ModuleActivatedMessage;
import com.googlecode.kanbanik.client.ModuleDeactivatedMessage;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.modules.KanbanikModule.ModuleInitializeCallback;
import com.googlecode.kanbanik.client.modules.editworkflow.ConfigureWorkflowModule;

public class ControlPanelModule extends TabPanel implements SelectionHandler<Integer> {
	
	private final BoardsModule boardsModule = new BoardsModule();
	
	private final ConfigureWorkflowModule configureWorkflowModule = new ConfigureWorkflowModule();

	private SimplePanel boardsContent = new SimplePanel();
	
	private SimplePanel configureWorkflowContent = new SimplePanel();
	
	public ControlPanelModule() {
		
		add(boardsContent, "Boards");
		add(configureWorkflowContent, "Configure Workflow");
		
		boardsModule.initialize(new BoardsModuleInitialized());
		addSelectionHandler(this);
		setWidth("100%");
	}

	class BoardsModuleInitialized implements ModuleInitializeCallback  {

		public void initialized(Widget widget) {
			boardsContent.add(widget);
			configureWorkflowModule.initialize(new ConfigureWorkflowModuleInitialized());
		}
	}
	
	class ConfigureWorkflowModuleInitialized implements ModuleInitializeCallback  {

		public void initialized(Widget widget) {
			configureWorkflowContent.add(widget);
			MessageBus.sendMessage(new ModuleActivatedMessage(BoardsModule.class, this));
			MessageBus.sendMessage(new ModuleActivatedMessage(ControlPanelModule.class, this));
			selectTab(0);
		}
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
	
	public void onSelection(SelectionEvent<Integer> event) {
		if (event.getSelectedItem() == 0) {
			boardsModule.initialize(new BoardsRefreshed());
		} else if (event.getSelectedItem() == 1) {
			configureWorkflowModule.initialize(new ConfigureWorkflowRefreshed());
		}
	}
	
}
