package com.googlecode.kanbanik.client.modules.editworkflow.classofservice;

import java.util.List;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.googlecode.kanbanik.client.components.Component;
import com.googlecode.kanbanik.client.components.ListBoxWithAddEditDelete;
import com.googlecode.kanbanik.dto.ClassOfServiceDto;

public class ClassOfServicesListFactory {
	
	public ListBoxWithAddEditDelete<ClassOfServiceDto> create() {
		return new ListBoxWithAddEditDelete<ClassOfServiceDto>(
				"Classes Of Service", 
				new IdProvider(), 
				new LabelProvider(),
				new ClassOfServiceCreatingComponent(),
				new ClassOfServiceEditingComponent(),
				new ClassOfServiceDeletingComponent(), 
				new Refresher()
		);
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

	class ClassOfServiceEditingComponent implements
			Component<ClassOfServiceDto> {

		@Override
		public void setup(HasClickHandlers clickHandler, String title) {

		}

		@Override
		public void setDto(ClassOfServiceDto dto) {

		}

	}

	class ClassOfServiceDeletingComponent implements
			Component<ClassOfServiceDto> {

		@Override
		public void setup(HasClickHandlers clickHandler, String title) {

		}

		@Override
		public void setDto(ClassOfServiceDto dto) {

		}

	}

	class Refresher implements
			ListBoxWithAddEditDelete.Refresher<ClassOfServiceDto> {

		@Override
		public void refrehs(List<ClassOfServiceDto> items,
				ClassOfServiceDto newItem, int index) {

		}

	}
	
	
}