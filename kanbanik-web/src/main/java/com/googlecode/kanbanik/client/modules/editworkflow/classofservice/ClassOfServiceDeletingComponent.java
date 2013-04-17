package com.googlecode.kanbanik.client.modules.editworkflow.classofservice;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.googlecode.kanbanik.client.KanbanikServerCaller;
import com.googlecode.kanbanik.client.ResourceClosingAsyncCallback;
import com.googlecode.kanbanik.client.ServerCommandInvokerManager;
import com.googlecode.kanbanik.client.components.Closable;
import com.googlecode.kanbanik.client.components.Component;
import com.googlecode.kanbanik.client.components.PanelContainingDialog;
import com.googlecode.kanbanik.client.components.PanelContainingDialog.PanelContainingDialolgListener;
import com.googlecode.kanbanik.client.components.WarningPanel;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.messages.classesofservice.ClassOfServiceDeletedMessage;
import com.googlecode.kanbanik.dto.ClassOfServiceDto;
import com.googlecode.kanbanik.dto.shell.FailableResult;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
import com.googlecode.kanbanik.dto.shell.VoidParams;
import com.googlecode.kanbanik.shared.ServerCommand;

public class ClassOfServiceDeletingComponent implements Component<ClassOfServiceDto>, ClickHandler, Closable {

	private ClassOfServiceDto dto;

	private PanelContainingDialog yesNoDialog;

	private WarningPanel warningPanel;
	
	@Override
	public void setup(HasClickHandlers clickHandler, String title) {
		clickHandler.addClickHandler(this);
	}

	@Override
	public void setDto(ClassOfServiceDto dto) {
		this.dto = dto;
		warningPanel = new WarningPanel(
				"Are you sure you want to delete class of service'" + dto.getName() + "'?");
	}

	@Override
	public void close() {
		yesNoDialog.close();
	}

	@Override
	public void onClick(ClickEvent event) {
		yesNoDialog = new PanelContainingDialog("Are you sure?", warningPanel);
		yesNoDialog.addListener(new YesNoDialogListener(dto));
		yesNoDialog.center();
	}
	
	class YesNoDialogListener implements PanelContainingDialolgListener {

		private final ClassOfServiceDto classOfServiceDto;

		public YesNoDialogListener(ClassOfServiceDto classOfServiceDto) {
			this.classOfServiceDto = classOfServiceDto;
		}

		public void okClicked(PanelContainingDialog dialog) {
			new KanbanikServerCaller(new Runnable() {

				public void run() {

					ServerCommandInvokerManager
							.getInvoker()
							.<SimpleParams<ClassOfServiceDto>, FailableResult<VoidParams>> invokeCommand(
									ServerCommand.DELETE_CLASS_OF_SERVICE,
									new SimpleParams<ClassOfServiceDto>(classOfServiceDto),
									new ResourceClosingAsyncCallback<FailableResult<VoidParams>>(
											ClassOfServiceDeletingComponent.this) {

										@Override
										public void success(
												FailableResult<VoidParams> result) {
											MessageBus.sendMessage(new ClassOfServiceDeletedMessage(classOfServiceDto, this));
										}
									});

				}
			});
		}

		public void cancelClicked(PanelContainingDialog dialog) {

		}
	}

}