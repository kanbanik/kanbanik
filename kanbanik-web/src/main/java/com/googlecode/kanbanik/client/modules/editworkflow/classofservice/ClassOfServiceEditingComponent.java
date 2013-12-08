package com.googlecode.kanbanik.client.modules.editworkflow.classofservice;

import com.google.gwt.event.dom.client.ClickEvent;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.messages.classesofservice.ClassOfServiceEditedMessage;

public class ClassOfServiceEditingComponent extends BaseClassOfServiceManipulatingComponent {

	@Override
	protected void classOfServiceSuccessfullyManipulated(Dtos.ClassOfServiceDto classOfService) {
		MessageBus.sendMessage(new ClassOfServiceEditedMessage(classOfService, this));
	}
	
	@Override
	public void onClick(ClickEvent event) {
		nameBox.setText(getClassOfServiceDto().getName());
		descriptionTextArea.setHtml(getClassOfServiceDto().getDescription());
		setColour(getClassOfServiceDto().getColour());
		
		super.onClick(event);
	}

}
