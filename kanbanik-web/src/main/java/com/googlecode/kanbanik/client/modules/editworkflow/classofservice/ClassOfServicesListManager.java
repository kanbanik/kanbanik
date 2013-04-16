package com.googlecode.kanbanik.client.modules.editworkflow.classofservice;

import java.util.List;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.googlecode.kanbanik.client.BaseAsyncCallback;
import com.googlecode.kanbanik.client.KanbanikServerCaller;
import com.googlecode.kanbanik.client.ServerCommandInvokerManager;
import com.googlecode.kanbanik.client.components.Component;
import com.googlecode.kanbanik.client.components.ListBoxWithAddEditDelete;
import com.googlecode.kanbanik.dto.BoardDto;
import com.googlecode.kanbanik.dto.ClassOfServiceDto;
import com.googlecode.kanbanik.dto.ListDto;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
import com.googlecode.kanbanik.shared.ServerCommand;

public class ClassOfServicesListManager {
	
	private ListBoxWithAddEditDelete<ClassOfServiceDto> listComponent;
	
	public ListBoxWithAddEditDelete<ClassOfServiceDto> create() {
		listComponent=  new ListBoxWithAddEditDelete<ClassOfServiceDto>(
				"Classes Of Service", 
				new IdProvider(), 
				new LabelProvider(),
				new ClassOfServiceCreatingComponent(),
				new ClassOfServiceEditingComponent(),
				new ClassOfServiceDeletingComponent(), 
				new Refresher()
		);
		
		return listComponent;
	}
	
	public void selectedBoardChanged(final BoardDto board) {
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

	class ClassOfServiceEditingComponent implements
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