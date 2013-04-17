package com.googlecode.kanbanik.client.modules.editworkflow.classofservice;

import com.google.gwt.event.dom.client.ClickEvent;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.messages.classesofservice.ClassOfServiceAddedMessage;
import com.googlecode.kanbanik.dto.ClassOfServiceDto;

public class ClassOfServiceCreatingComponent extends
		BaseClassOfServiceManipulatingComponent {

	
	public ClassOfServiceCreatingComponent() {
		clearDialog();
	}
	
	@Override
	protected void classOfServiceSuccessfullyManipulated(ClassOfServiceDto classOfService) {
		MessageBus.sendMessage(new ClassOfServiceAddedMessage(classOfService, this));
	}
	
	@Override
	public void onClick(ClickEvent event) {
		// make sure there is no old DTO
		setDto(null);
		super.onClick(event);
	}
	
}
