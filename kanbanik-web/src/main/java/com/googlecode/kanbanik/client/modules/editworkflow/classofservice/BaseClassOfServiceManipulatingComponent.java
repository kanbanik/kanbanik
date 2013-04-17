package com.googlecode.kanbanik.client.modules.editworkflow.classofservice;

import net.auroris.ColorPicker.client.ColorPicker;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.KanbanikServerCaller;
import com.googlecode.kanbanik.client.ResourceClosingAsyncCallback;
import com.googlecode.kanbanik.client.ServerCommandInvokerManager;
import com.googlecode.kanbanik.client.components.Closable;
import com.googlecode.kanbanik.client.components.Component;
import com.googlecode.kanbanik.client.components.PanelContainingDialog;
import com.googlecode.kanbanik.client.components.PanelContainingDialog.PanelContainingDialolgListener;
import com.googlecode.kanbanik.client.components.common.KanbanikRichTextArea;
import com.googlecode.kanbanik.dto.BoardDto;
import com.googlecode.kanbanik.dto.ClassOfServiceDto;
import com.googlecode.kanbanik.dto.shell.FailableResult;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
import com.googlecode.kanbanik.shared.ServerCommand;

public abstract class BaseClassOfServiceManipulatingComponent extends Composite
		implements PanelContainingDialolgListener, Closable,
		Component<ClassOfServiceDto>, ClickHandler {
	private PanelContainingDialog dialog;

	private PanelContainingDialog colorPickerDialog;

	@UiField
	TextBox nameBox;

	@UiField
	KanbanikRichTextArea descriptionTextArea;

	@UiField
	PushButton colorButton;

	@UiField
	CheckBox makePublic;

	final ColorPicker colorPicker = new ColorPicker();

	private String currentColour;

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

	private ClassOfServiceDto classOfServiceDto;

	public BaseClassOfServiceManipulatingComponent() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	protected void setColour(String colour) {
		colorButton.getUpFace().setHTML(template.buttonColour(colour));
		currentColour = colour;
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
				try {
					colorPicker.setHex(currentColour);
				} catch (Exception e) {
					// well, so than leave the default color
				}

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
	public void setDto(ClassOfServiceDto classOfServiceDto) {
		this.classOfServiceDto = classOfServiceDto;
	}

	@Override
	public void close() {
		dialog.close();
	}
	
	@Override
	public void okClicked(PanelContainingDialog dialog) {
		new KanbanikServerCaller(
				new Runnable() {

					public void run() {
		ServerCommandInvokerManager.getInvoker().<SimpleParams<ClassOfServiceDto>, FailableResult<SimpleParams<ClassOfServiceDto>>> invokeCommand(
				ServerCommand.SAVE_CLASS_OF_SERVICE,
				new SimpleParams<ClassOfServiceDto>(createDto()),
				new ResourceClosingAsyncCallback<FailableResult<SimpleParams<ClassOfServiceDto>>>(BaseClassOfServiceManipulatingComponent.this) {

					@Override
					public void success(FailableResult<SimpleParams<ClassOfServiceDto>> result) {
						classOfServiceSuccessfullyManipulated(result.getPayload().getPayload());
						clearDialog();
					}
				});
		}

					});
	}
	
	@Override
	public void onClick(ClickEvent event) {
		if (currentBoard == null) {
			makePublic.setValue(true);
			makePublic.setEnabled(false);
		}
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
		makePublic.setValue(false);
	}
	
	public BoardDto getCurrentBoard() {
		return currentBoard;
	}

	public void setCurrentBoard(BoardDto currentBoard) {
		this.currentBoard = currentBoard;
	}

	protected abstract void classOfServiceSuccessfullyManipulated(ClassOfServiceDto classOfService);
	
	protected ClassOfServiceDto createDto() {
		return new ClassOfServiceDto(
				classOfServiceDto == null ? null : classOfServiceDto.getId(),
				nameBox.getText(),
				descriptionTextArea.getHtml(),
				colorPicker.getHexColor(),
				makePublic.getValue(),
				classOfServiceDto == null ? 1 : classOfServiceDto.getVersion(),
				!makePublic.getValue() ? getCurrentBoard() : null);
	}
	
	public ClassOfServiceDto getClassOfServiceDto() {
		return classOfServiceDto;
	}
	
}
