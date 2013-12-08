package com.googlecode.kanbanik.client.modules.editworkflow.classofservice;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.googlecode.kanbanik.client.api.DtoFactory;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.api.ServerCallCallback;
import com.googlecode.kanbanik.client.api.ServerCaller;
import com.googlecode.kanbanik.client.components.Closable;
import com.googlecode.kanbanik.client.components.Component;
import com.googlecode.kanbanik.client.components.PanelContainingDialog;
import com.googlecode.kanbanik.client.components.PanelContainingDialog.PanelContainingDialolgListener;
import com.googlecode.kanbanik.client.components.common.KanbanikRichTextArea;
import com.googlecode.kanbanik.dto.BoardDto;
import net.auroris.ColorPicker.client.ColorPicker;

public abstract class BaseClassOfServiceManipulatingComponent extends Composite
		implements PanelContainingDialolgListener, Closable,
		Component<Dtos.ClassOfServiceDto>, ClickHandler {
	private PanelContainingDialog dialog;

	private PanelContainingDialog colorPickerDialog;

	@UiField
	TextBox nameBox;

	@UiField
	KanbanikRichTextArea descriptionTextArea;

	@UiField
	PushButton colorButton;

	final ColorPicker colorPicker = new ColorPicker();

	private BoardDto currentBoard;
	
	interface MyUiBinder extends
			UiBinder<Widget, BaseClassOfServiceManipulatingComponent> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	public interface ButtonTemplate extends SafeHtmlTemplates {
		@Template("<div style=\"width: 20px; height: 15px; background-color:#{0}\"/>")
		SafeHtml buttonColour(String colour);
	}

	public interface Style extends CssResource {
		String colourPickerStyle();
	}

	private static final ButtonTemplate template = GWT
			.create(ButtonTemplate.class);

	private Dtos.ClassOfServiceDto classOfServiceDto;

	public BaseClassOfServiceManipulatingComponent() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	protected void setColour(String colour) {
		colorButton.getUpFace().setHTML(template.buttonColour(colour));
		try {
			colorPicker.setHex(colour);
		} catch (Exception e) {
			// well, so than leave the default color
		}
	}

	@Override
	public void setup(HasClickHandlers clickHandler, String title) {
		dialog = new PanelContainingDialog(title, this, nameBox);
		dialog.addListener(this);
		clickHandler.addClickHandler(this);

		initColorPicker();
	}

	private void initColorPicker() {
		Panel colorPickerPanel = new FlowPanel();
		colorPickerPanel.add(colorPicker);
		colorPickerDialog = new PanelContainingDialog("Select Colour",
				colorPickerPanel);

		colorButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				colorPickerDialog.center();

			}
		});

		colorPickerDialog.addListener(new PanelContainingDialolgListener() {

			@Override
			public void okClicked(PanelContainingDialog dialog) {
				setColour(colorPicker.getHexColor());
				colorPickerDialog.close();
			}

			@Override
			public void cancelClicked(PanelContainingDialog dialog) {
				// do nothing
			}
		});

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
                new ServerCallCallback<Dtos.ClassOfServiceDto>() {

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
	
	public BoardDto getCurrentBoard() {
		return currentBoard;
	}

	public void setCurrentBoard(BoardDto currentBoard) {
		this.currentBoard = currentBoard;
	}

	protected abstract void classOfServiceSuccessfullyManipulated(Dtos.ClassOfServiceDto classOfService);
	
	protected Dtos.ClassOfServiceDto createDto() {
        Dtos.ClassOfServiceDto res = DtoFactory.classOfServiceDto();
        res.setId(classOfServiceDto == null ? null : classOfServiceDto.getId());
        res.setName(nameBox.getText());
        res.setDescription(descriptionTextArea.getHtml());
        res.setColour(colorPicker.getHexColor());
        res.setVersion(classOfServiceDto == null ? 1 : classOfServiceDto.getVersion());
        return res;
	}
	
	public Dtos.ClassOfServiceDto getClassOfServiceDto() {
		return classOfServiceDto;
	}
	
}
