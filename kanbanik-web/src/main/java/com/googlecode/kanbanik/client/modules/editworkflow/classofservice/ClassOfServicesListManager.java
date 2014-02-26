package com.googlecode.kanbanik.client.modules.editworkflow.classofservice;

import com.googlecode.kanbanik.client.Modules;
import com.googlecode.kanbanik.client.api.DtoFactory;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.api.ServerCallCallback;
import com.googlecode.kanbanik.client.api.ServerCaller;
import com.googlecode.kanbanik.client.components.ListBoxWithAddEditDelete;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.messaging.messages.classesofservice.ClassOfServiceAddedMessage;
import com.googlecode.kanbanik.client.messaging.messages.classesofservice.ClassOfServiceDeletedMessage;
import com.googlecode.kanbanik.client.messaging.messages.classesofservice.ClassOfServiceEditedMessage;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLifecycleListener;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLyfecycleListenerHandler;
import com.googlecode.kanbanik.client.security.CurrentUser;
import com.googlecode.kanbanik.dto.CommandNames;

import java.util.List;

public class ClassOfServicesListManager implements MessageListener<Dtos.ClassOfServiceDto>, ModulesLifecycleListener {
	
	private ListBoxWithAddEditDelete<Dtos.ClassOfServiceDto> listComponent;

	private ClassOfServiceCreatingComponent creatingComponent = new ClassOfServiceCreatingComponent();
	private ClassOfServiceEditingComponent editingComponent = new ClassOfServiceEditingComponent();
	
	public ClassOfServicesListManager() {
		MessageBus.registerListener(ClassOfServiceAddedMessage.class, this);
		MessageBus.registerListener(ClassOfServiceEditedMessage.class, this);
		MessageBus.registerListener(ClassOfServiceDeletedMessage.class, this);
		new ModulesLyfecycleListenerHandler(Modules.CONFIGURE, this);
	}
	
	public ListBoxWithAddEditDelete<Dtos.ClassOfServiceDto> create() {

		listComponent=  new ListBoxWithAddEditDelete<Dtos.ClassOfServiceDto>(
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
	
	public void selectedBoardChanged(final Dtos.BoardDto board) {
		creatingComponent.setCurrentBoard(board);
		editingComponent.setCurrentBoard(board);

        Dtos.SessionDto dto = DtoFactory.sessionDto(CurrentUser.getInstance().getSessionId());
        dto.setCommandName(CommandNames.GET_ALL_CLASS_OF_SERVICE.name);

        ServerCaller.<Dtos.SessionDto, Dtos.ClassOfServicesDto>sendRequest(
                dto,
                Dtos.ClassOfServicesDto.class,
                new ServerCallCallback<Dtos.ClassOfServicesDto>() {

                    @Override
                    public void success(Dtos.ClassOfServicesDto response) {
                        listComponent.setContent(response.getValues());
                    }
                }
        );
	}
	
	class IdProvider implements
			ListBoxWithAddEditDelete.IdProvider<Dtos.ClassOfServiceDto> {

		@Override
		public String getId(Dtos.ClassOfServiceDto dto) {
			return dto.getId();
		}

	}

	class LabelProvider implements
			ListBoxWithAddEditDelete.LabelProvider<Dtos.ClassOfServiceDto> {

		@Override
		public String getLabel(Dtos.ClassOfServiceDto dto) {
			return dto.getName();
		}

	}

	class Refresher implements
			ListBoxWithAddEditDelete.Refresher<Dtos.ClassOfServiceDto> {

		@Override
		public void refrehs(List<Dtos.ClassOfServiceDto> items,
                            Dtos.ClassOfServiceDto newItem, int index) {
			items.set(index, newItem);
		}

	}

	@Override
	public void messageArrived(Message<Dtos.ClassOfServiceDto> message) {
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