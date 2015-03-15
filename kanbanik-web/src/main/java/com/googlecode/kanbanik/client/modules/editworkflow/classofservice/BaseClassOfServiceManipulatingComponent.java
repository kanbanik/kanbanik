package com.googlecode.kanbanik.client.modules.editworkflow.classofservice;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.googlecode.kanbanik.client.api.*;
import com.googlecode.kanbanik.client.components.Closable;
import com.googlecode.kanbanik.client.components.common.ColorPickerComponent;
import com.googlecode.kanbanik.client.components.Component;
import com.googlecode.kanbanik.client.components.PanelContainingDialog;
import com.googlecode.kanbanik.client.components.PanelContainingDialog.PanelContainingDialolgListener;
import com.googlecode.kanbanik.client.components.common.KanbanikRichTextArea;

public abstract class BaseClassOfServiceManipulatingComponent extends Composite
		implements PanelContainingDialolgListener, Closable,
		Component<Dtos.ClassOfServiceDto>, ClickHandler {
	private PanelContainingDialog dialog;

	@UiField
	TextBox nameBox;

	@UiField
	KanbanikRichTextArea descriptionTextArea;

	@UiField
    ColorPickerComponent colorPickerComponent;

	private Dtos.BoardDto currentBoard;
	
	interface MyUiBinder extends
			UiBinder<Widget, BaseClassOfServiceManipulatingComponent> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);



	public interface Style extends CssResource {
		String colourPickerStyle();
	}

	private Dtos.ClassOfServiceDto classOfServiceDto;

	public BaseClassOfServiceManipulatingComponent() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	protected void setColour(String colour) {
        colorPickerComponent.setColor(colour);
	}

	@Override
	public void setup(HasClickHandlers clickHandler, String title) {
		dialog = new PanelContainingDialog(title, this, nameBox);
		dialog.addListener(this);
		clickHandler.addClickHandler(this);

		colorPickerComponent.init();
	}

	@Override
	public void setDto(Dtos.ClassOfServiceDto classOfServiceDto) {
		this.classOfServiceDto = classOfServiceDto;
	}

	@Override
	public void close() {
		dialog.close();
	}
	
	@Override
	public void okClicked(PanelContainingDialog dialog) {
        ServerCaller.<Dtos.ClassOfServiceDto, Dtos.ClassOfServiceDto>sendRequest(
                createDto(),
                Dtos.ClassOfServiceDto.class,
                new ResourceClosingCallback<Dtos.ClassOfServiceDto>(dialog) {

                    @Override
                    public void success(Dtos.ClassOfServiceDto response) {
                        classOfServiceSuccessfullyManipulated(response);
                        clearDialog();
                    }
                }
        );
	}
	
	@Override
	public void onClick(ClickEvent event) {
		dialog.center();
	}
	
	@Override
	public void cancelClicked(PanelContainingDialog dialog) {
		clearDialog();
	}
	
	protected void clearDialog() {
		nameBox.setText("");
		descriptionTextArea.setHtml("");
		// blue is the default
		setColour("003d89");
	}
	
	public Dtos.BoardDto getCurrentBoard() {
		return currentBoard;
	}

	public void setCurrentBoard(Dtos.BoardDto currentBoard) {
		this.currentBoard = currentBoard;
	}

	protected abstract void classOfServiceSuccessfullyManipulated(Dtos.ClassOfServiceDto classOfService);
	
	protected Dtos.ClassOfServiceDto createDto() {
        Dtos.ClassOfServiceDto res = DtoFactory.classOfServiceDto();
        res.setId(classOfServiceDto == null ? null : classOfServiceDto.getId());
        res.setName(nameBox.getText());
        res.setDescription(descriptionTextArea.getHtml());
        res.setColour(colorPickerComponent.getColor());
        res.setVersion(classOfServiceDto == null ? 1 : classOfServiceDto.getVersion());
        return res;
	}
	
	public Dtos.ClassOfServiceDto getClassOfServiceDto() {
		return classOfServiceDto;
	}
	
}
