package com.googlecode.kanbanik.client.modules.editworkflow.classofservice;

import java.util.List;

import com.googlecode.kanbanik.client.BaseAsyncCallback;
import com.googlecode.kanbanik.client.KanbanikServerCaller;
import com.googlecode.kanbanik.client.Modules;
import com.googlecode.kanbanik.client.ServerCommandInvokerManager;
import com.googlecode.kanbanik.client.components.ListBoxWithAddEditDelete;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.messaging.messages.classesofservice.ClassOfServiceAddedMessage;
import com.googlecode.kanbanik.client.messaging.messages.classesofservice.ClassOfServiceDeletedMessage;
import com.googlecode.kanbanik.client.messaging.messages.classesofservice.ClassOfServiceEditedMessage;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLifecycleListener;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLyfecycleListenerHandler;
import com.googlecode.kanbanik.dto.BoardDto;
import com.googlecode.kanbanik.dto.ClassOfServiceDto;
import com.googlecode.kanbanik.dto.ListDto;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
import com.googlecode.kanbanik.shared.ServerCommand;

public class ClassOfServicesListManager implements MessageListener<ClassOfServiceDto>, ModulesLifecycleListener {
	
	private ListBoxWithAddEditDelete<ClassOfServiceDto> listComponent;

	private ClassOfServiceCreatingComponent creatingComponent = new ClassOfServiceCreatingComponent();
	private ClassOfServiceEditingComponent editingComponent = new ClassOfServiceEditingComponent();
	
	public ClassOfServicesListManager() {
		MessageBus.registerListener(ClassOfServiceAddedMessage.class, this);
		MessageBus.registerListener(ClassOfServiceEditedMessage.class, this);
		MessageBus.registerListener(ClassOfServiceDeletedMessage.class, this);
		new ModulesLyfecycleListenerHandler(Modules.CONFIGURE, this);
	}
	
	public ListBoxWithAddEditDelete<ClassOfServiceDto> create() {

		listComponent=  new ListBoxWithAddEditDelete<ClassOfServiceDto>(
				"Classes Of Service", 
				new IdProvider(), 
				new LabelProvider(),
				creatingComponent,
				editingComponent,
				new ClassOfServiceDeletingComponent(), 
				new Refresher()
		);
		
		return listComponent;
	}
	
	public void selectedBoardChanged(final BoardDto board) {
		creatingComponent.setCurrentBoard(board);
		editingComponent.setCurrentBoard(board);

		new KanbanikServerCaller(
				new Runnable() {

					public void run() {
		ServerCommandInvokerManager.getInvoker().<SimpleParams<BoardDto>, SimpleParams<ListDto<ClassOfServiceDto>>> invokeCommand(
				ServerCommand.GET_ALL_CLASS_OF_SERVICES,
				new SimpleParams<BoardDto>(board),
				new BaseAsyncCallback<SimpleParams<ListDto<ClassOfServiceDto>>>() {
					@Override
					public void success(SimpleParams<ListDto<ClassOfServiceDto>> result) {
						listComponent.setContent(result.getPayload().getList());
					}
				});
		}

					});
	}
	
	class IdProvider implements
			ListBoxWithAddEditDelete.IdProvider<ClassOfServiceDto> {

		@Override
		public String getId(ClassOfServiceDto dto) {
			return dto.getId();
		}

	}

	class LabelProvider implements
			ListBoxWithAddEditDelete.LabelProvider<ClassOfServiceDto> {

		@Override
		public String getLabel(ClassOfServiceDto dto) {
			return dto.getName();
		}

	}

	class Refresher implements
			ListBoxWithAddEditDelete.Refresher<ClassOfServiceDto> {

		@Override
		public void refrehs(List<ClassOfServiceDto> items,
				ClassOfServiceDto newItem, int index) {
			items.set(index, newItem);
		}

	}

	@Override
	public void messageArrived(Message<ClassOfServiceDto> message) {
		if (message instanceof ClassOfServiceAddedMessage) {
			listComponent.addNewItem(message.getPayload());
		} else if (message instanceof ClassOfServiceEditedMessage) {
			listComponent.refresh(message.getPayload());
		} else if (message instanceof ClassOfServiceDeletedMessage) {
			listComponent.removeItem(message.getPayload());
		}
	}

	@Override
	public void activated() {
		if (!MessageBus.listens(ClassOfServiceAddedMessage.class, this)) {
			MessageBus.registerListener(ClassOfServiceAddedMessage.class, this);
		}
		
		if (!MessageBus.listens(ClassOfServiceEditedMessage.class, this)) {
			MessageBus.registerListener(ClassOfServiceEditedMessage.class, this);	
		}
		
		if (!MessageBus.listens(ClassOfServiceDeletedMessage.class, this)) {
			MessageBus.registerListener(ClassOfServiceDeletedMessage.class, this);	
		}
	}

	@Override
	public void deactivated() {
		MessageBus.unregisterListener(ClassOfServiceAddedMessage.class, this);
		MessageBus.unregisterListener(ClassOfServiceEditedMessage.class, this);
		MessageBus.unregisterListener(ClassOfServiceDeletedMessage.class, this);
		
		new ModulesLyfecycleListenerHandler(Modules.CONFIGURE, this);		
	}
	
	
}