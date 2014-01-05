package com.googlecode.kanbanik.client.modules.editworkflow.classofservice;

import com.google.gwt.event.dom.client.ClickEvent;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.messages.classesofservice.ClassOfServiceEditedMessage;
import com.googlecode.kanbanik.dto.CommandNames;

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

    @Override
    protected Dtos.ClassOfServiceDto createDto() {
        Dtos.ClassOfServiceDto dto = super.createDto();
        dto.setCommandName(CommandNames.EDIT_CLASS_OF_SERVICE.name);
        return dto;
    }
}
