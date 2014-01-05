package com.googlecode.kanbanik.client.modules.editworkflow.classofservice;

import com.google.gwt.event.dom.client.ClickEvent;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.messages.classesofservice.ClassOfServiceAddedMessage;
import com.googlecode.kanbanik.dto.CommandNames;

public class ClassOfServiceCreatingComponent extends
		BaseClassOfServiceManipulatingComponent {

	
	public ClassOfServiceCreatingComponent() {
		clearDialog();
	}
	
	@Override
	protected void classOfServiceSuccessfullyManipulated(Dtos.ClassOfServiceDto classOfService) {
		MessageBus.sendMessage(new ClassOfServiceAddedMessage(classOfService, this));
	}
	
	@Override
	public void onClick(ClickEvent event) {
		// make sure there is no old DTO
		setDto(null);
		setColour("7183f4");
		super.onClick(event);
	}

    @Override
    protected Dtos.ClassOfServiceDto createDto() {
        Dtos.ClassOfServiceDto dto = super.createDto();
        dto.setCommandName(CommandNames.CREATE_CLASS_OF_SERVICE.name);
        return dto;
    }
}
